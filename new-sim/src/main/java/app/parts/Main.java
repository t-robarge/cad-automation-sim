package app.parts;

import app.parts.dao.FeatureDao;
import app.parts.dao.PartDao;
import app.parts.repo.PartRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Db.initSchema();

            // Build an in-memory Part aggregate first
            Part p = new Part("P-2001", "Mounting Plate", "MM");
            p.addFeature(new Feature(FeatureType.HOLE, 25.0, 30.0, 6.0, 0.0));
            p.addFeature(new Feature("hole", 75.0, 30.0, 6.0, 0.0)); // string parse
            p.addFeature(new Feature(FeatureType.SLOT, 50.0, 60.0, 10.0, 4.0));

            // Save atomically
            var repo = new PartRepository();
            var agg = repo.getByPartNumber("P-2001");
            System.out.println("Aggregate: " + agg.part());                  // now features list is filled
            for (Feature f : agg.features()) System.out.println("  * " + f);

            // Fetch back to prove persistence
            PartDao partDao = new PartDao();
            FeatureDao featureDao = new FeatureDao();
            try (Connection c = Db.get()) {
                var summary = partDao.findByPartNumber(c, "P-2001").orElseThrow();
                System.out.println("Summary via DAO: " + summary);

                System.out.println("All summaries:");
                for (var s : partDao.listAll(c)) {
                    System.out.println("  • " + s);
                }
            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

