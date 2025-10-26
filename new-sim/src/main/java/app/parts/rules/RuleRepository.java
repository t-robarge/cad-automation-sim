package app.parts.repo;

import app.parts.Db;
import app.parts.Feature;
import app.parts.Part;
import app.parts.dao.FeatureDao;
import app.parts.dao.PartDao;
import app.parts.rules.*;

import java.sql.*;
import java.util.List;

public class RuleRepository {
    private final PartDao partDao = new PartDao();
    private final FeatureDao featureDao = new FeatureDao();

    public record RunResult(long runId, List<RuleViolation> violations) {}

    public RunResult runRulesForPart(String partNumber, RuleEngine engine) throws SQLException {
        try (Connection c = Db.get()) {
            c.setAutoCommit(false);
            try {
                var opt = partDao.findByPartNumber(c, partNumber);
                if (opt.isEmpty()) throw new SQLException("Part not found: " + partNumber);
                Part p = opt.get();
                List<Feature> features = featureDao.listByPart(c, p.getId());

                long runId = insertRun(c, p.getId(), "RUNNING");
                List<RuleViolation> violations = engine.run(new RuleContext(p, features));

                String status = violations.isEmpty() ? "OK" : "VIOLATIONS";
                for (RuleViolation v : violations) {
                    insertViolation(c, runId, v);
                }
                updateRunStatus(c, runId, status);
                c.commit();
                return new RunResult(runId, violations);
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    private long insertRun(Connection c, long partId, String status) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO rule_runs(part_id,status) VALUES(?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, partId);
            ps.setString(2, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); return rs.getLong(1); }
        }
    }

    private void updateRunStatus(Connection c, long runId, String status) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE rule_runs SET status=?, finished_at=CURRENT_TIMESTAMP WHERE id=?")) {
            ps.setString(1, status);
            ps.setLong(2, runId);
            ps.executeUpdate();
        }
    }

    private void insertViolation(Connection c, long runId, RuleViolation v) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO rule_violations(rule_run_id, code, message, severity) VALUES(?,?,?,?)")) {
            ps.setLong(1, runId);
            ps.setString(2, v.code());
            ps.setString(3, v.message());
            ps.setString(4, v.severity());
            ps.executeUpdate();
        }
    }
}
