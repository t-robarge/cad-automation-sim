package app.parts;

import app.parts.repo.PartRepository;

public class Seed {
    public static void main(String[] args) throws Exception {
        Db.initSchema();
        var p = new Part("DEMO-PLATE", "Demo Plate", "MM");
        p.addFeature(new Feature(FeatureType.HOLE, 25, 30, 12, 0)); // triggers hole > 10 rule
        p.addFeature(new Feature(FeatureType.SLOT, 50, 60, 10, 4));
        new PartRepository().createWithFeatures(p);
        System.out.println("Hi");
        System.out.println("Seeded DEMO-PLATE");
    }
}
