package app.parts.api;

import app.parts.Db;
import app.parts.Feature;
import app.parts.FeatureType;
import app.parts.Part;

import app.parts.api.dto.ApiError;
import app.parts.api.dto.CreateFeatureRequest;
import app.parts.api.dto.CreatePartRequest;
import app.parts.api.dto.PartSummaryResponse;

import app.parts.dao.FeatureDao;
import app.parts.dao.PartDao;

import app.parts.repo.PartRepository;

import app.parts.rules.RuleEngine;

import io.javalin.Javalin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ApiServer {
    public static void main(String[] args) {
        try { Db.initSchema(); } catch (SQLException e) { throw new RuntimeException(e); }

        Javalin app = Javalin.create(cfg -> {
            cfg.http.defaultContentType = "application/json";
            // Javalin 5 CORS:
            cfg.plugins.enableCors(cors -> cors.add(rule -> {
                rule.anyHost();           // allow all origins for now
                // rule.allowCredentials = false; // defaults to false
            cfg.staticFiles.add("/public"); // public html
            }));
        });

        // ...rest of routes unchanged...
        app.get("/health", ctx -> ctx.result("ok"));

        // POST new part
        app.post("/parts", ctx -> {
            CreatePartRequest req = ctx.bodyAsClass(CreatePartRequest.class);
            if (isBlank(req.partNumber) || isBlank(req.name) || isBlank(req.unit)) {
                ctx.status(400).json(new ApiError("partNumber, name, unit are required"));
                return;
            }
            Part p = new Part(req.partNumber.trim(), req.name.trim(), req.unit.trim());
            if (req.features != null) {
                for (CreateFeatureRequest fr : req.features) {
                    try {
                        p.addFeature(new Feature(FeatureType.parse(fr.type), fr.x, fr.y, fr.d1, fr.d2));
                    } catch (IllegalArgumentException ex) {
                        ctx.status(400).json(new ApiError("Invalid feature: " + ex.getMessage()));
                        return;
                    }
                }
            }
            try {
                var saved = new PartRepository().createWithFeatures(p);
                ctx.status(201).json(saved);
            } catch (SQLException ex) {
                ctx.status(500).json(new ApiError("DB error: " + ex.getMessage()));
            }
        });
        // LIST all parts
        app.get("/parts", ctx -> {
            try (Connection c = Db.get()) {
                var partDao = new PartDao();
                var featureDao = new FeatureDao();
                var parts = partDao.listAll(c);
                var resp = new ArrayList<PartSummaryResponse>(parts.size());
                for (Part p : parts) {
                    long count = featureDao.listByPart(c, p.getId()).size();
                    resp.add(new PartSummaryResponse(p.getId(), p.getPartNumber(), p.getName(), p.getUnit(), count));
                }
                ctx.json(resp);
            } catch (SQLException ex) {
                ctx.status(500).json(new ApiError("DB error: " + ex.getMessage()));
            }
        });
        // LIST part by part number
        app.get("/parts/{partNumber}", ctx -> {
            String pn = ctx.pathParam("partNumber");
            try {
                var agg = new PartRepository().getByPartNumber(pn);
                if (agg == null) { ctx.status(404).json(new ApiError("Part not found: " + pn)); return; }
                ctx.json(agg.part());
            } catch (SQLException ex) {
                ctx.status(500).json(new ApiError("DB error: " + ex.getMessage()));
            }
        });
        // ADD FEATURE TO PART
        app.post("/parts/{partNumber}/features", ctx -> {
            String pn = ctx.pathParam("partNumber");
            var fr = ctx.bodyAsClass(CreateFeatureRequest.class);
            Feature feature;
            try { feature = new Feature(FeatureType.parse(fr.type), fr.x, fr.y, fr.d1, fr.d2); }
            catch (IllegalArgumentException ex) { ctx.status(400).json(new ApiError("Invalid feature: " + ex.getMessage())); return; }

            try (Connection c = Db.get()) {
                var partOpt = new PartDao().findByPartNumber(c, pn);
                if (partOpt.isEmpty()) { ctx.status(404).json(new ApiError("Part not found: " + pn)); return; }
                var saved = new FeatureDao().insert(c, partOpt.get().getId(), feature);
                ctx.status(201).json(saved);
            } catch (SQLException ex) {
                ctx.status(500).json(new ApiError("DB error: " + ex.getMessage()));
            }
        });
        // RUN Rules for Part
        app.post("/rules/run/{partNumber}", ctx -> {
            String pn = ctx.pathParam("partNumber");
            try {
                var rr = new app.parts.repo.RuleRepository()
                        .runRulesForPart(pn, defaultEngine());
                ctx.json(rr);
            } catch (SQLException ex) {
                ctx.status(500).json(new ApiError("DB error: " + ex.getMessage()));
            } catch (Exception ex) {
                ctx.status(400).json(new ApiError(ex.getMessage()));
            }
        });
        // GET violations by part
        app.get("/rules/violations/{partNumber}", ctx -> {
            String pn = ctx.pathParam("partNumber");
            try (var c = Db.get()) {
                var partOpt = new app.parts.dao.PartDao().findByPartNumber(c, pn);
                if (partOpt.isEmpty()) { ctx.status(404).json(new ApiError("Part not found: " + pn)); return; }
                long partId = partOpt.get().getId();
                var rs = c.prepareStatement("""
                SELECT rv.id, rr.id as run_id, rv.code, rv.message, rv.severity, rr.started_at, rr.finished_at
                FROM rule_violations rv
                JOIN rule_runs rr ON rr.id = rv.rule_run_id
                WHERE rr.part_id = ?
                ORDER BY rv.id DESC
                """);
                rs.setLong(1, partId);
                try (var r = rs.executeQuery()) {
                    var list = new java.util.ArrayList<java.util.Map<String,Object>>();
                    while (r.next()) {
                        var m = new java.util.HashMap<String,Object>();
                        m.put("id", r.getLong("id"));
                        m.put("runId", r.getLong("run_id"));
                        m.put("code", r.getString("code"));
                        m.put("message", r.getString("message"));
                        m.put("severity", r.getString("severity"));
                        m.put("startedAt", r.getString("started_at"));
                        m.put("finishedAt", r.getString("finished_at"));
                        list.add(m);
                    }
                    ctx.json(list);
                }
            }
        });
        // DELETE part (and all its features via cascade)
        app.delete("/parts/{partNumber}", ctx -> {
            String pn = ctx.pathParam("partNumber");
            try (var c = Db.get()) {
                c.setAutoCommit(false);
                try {
                    int n = new PartDao().deleteByPartNumber(c, pn);
                    c.commit();
                    if (n == 0) ctx.status(404).json(new ApiError("Part not found: " + pn));
                    else ctx.status(204);
                } catch (SQLException e) {
                    c.rollback();
                    ctx.status(500).json(new ApiError("DB error: " + e.getMessage()));
                } finally {
                    c.setAutoCommit(true);
                }
            }
        });

        // DELETE a single feature
        app.delete("/parts/{partNumber}/features/{featureId}", ctx -> {
            String pn = ctx.pathParam("partNumber");
            long featureId = Long.parseLong(ctx.pathParam("featureId"));
            try (var c = Db.get()) {
                c.setAutoCommit(false);
                try {
                    var partOpt = new PartDao().findByPartNumber(c, pn);
                    if (partOpt.isEmpty()) { ctx.status(404).json(new ApiError("Part not found: " + pn)); c.rollback(); return; }
                    long partId = partOpt.get().getId();
                    int n = new FeatureDao().deleteByIdAndPart(c, featureId, partId);
                    if (n == 0) { ctx.status(404).json(new ApiError("Feature not found for this part")); c.rollback(); return; }
                    c.commit();
                    ctx.status(204);
                } catch (SQLException e) {
                    c.rollback();
                    ctx.status(500).json(new ApiError("DB error: " + e.getMessage()));
                } finally {
                    c.setAutoCommit(true);
                }
            }
        });



        int port = getPort();
        app.start(port);
        System.out.println("API listening on http://localhost:" + port);
    }
    // Build a default engine
    private static RuleEngine defaultEngine() {
        return new RuleEngine()
            .register(new app.parts.rules.rules.HoleDiaLimitRule(10.0))
            .register(new app.parts.rules.rules.MinHoleCountRule(2));
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static int getPort() {
        try { return Integer.parseInt(System.getenv().getOrDefault("PORT", "7010")); }
        catch (Exception e) { return 7010; }
    }
}
