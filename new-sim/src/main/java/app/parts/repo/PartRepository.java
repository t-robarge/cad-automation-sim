package app.parts.repo;

import app.parts.Db;
import app.parts.Feature;
import app.parts.Part;
import app.parts.dao.FeatureDao;
import app.parts.dao.PartDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartRepository {
    private final PartDao partDao = new PartDao();
    private final FeatureDao featureDao = new FeatureDao();

    /** Inserts a Part and all its Features in a single transaction. */
    // PartRepository.java
    // PartRepository.java
    public Part createWithFeatures(Part p) throws Exception {
        try (var c = Db.get()) {
            c.setAutoCommit(false);
            try {
            var partDao = new PartDao();
            var featureDao = new FeatureDao();

            var existing = partDao.findByPartNumber(c, p.getPartNumber());
            Part target;
            if (existing.isPresent()) {
                // Part already exists; reuse it (don’t reinsert features)
                target = existing.get();
            } else {
                // Create part, then insert features one-by-one
                target = partDao.insert(c, p);
                for (var f : p.getFeatures()) {
                featureDao.insert(c, target.getId(), f);  // <— one-by-one
                }
            }

            c.commit();
            return target;
            } catch (Exception e) {
            c.rollback();
            throw e;
            } finally {
            c.setAutoCommit(true);
            }
        }
        }



    /** Loads a Part and its Features by partNumber, returning an aggregate. */
    public PartWithFeatures getByPartNumber(String partNumber) throws SQLException {
        try (Connection c = Db.get()) {
            var opt = partDao.findByPartNumber(c, partNumber); // <-- requires PartDao#findByPartNumber
            if (opt.isEmpty()) return null;
            Part p = opt.get();
            var features = featureDao.listByPart(c, p.getId());

            Part stitched = new Part(p.getId(), p.getPartNumber(), p.getName(), p.getUnit());
            for (Feature f : features) stitched.addFeature(f);

            return new PartWithFeatures(stitched, features);
        }
    }

    public static record SavedPart(Part part, List<Feature> features) {}
    public static record PartWithFeatures(Part part, List<Feature> features) {}
}
