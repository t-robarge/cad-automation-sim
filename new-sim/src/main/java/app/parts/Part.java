package app.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Part {
    private final Long id;
    private final String partNumber;
    private final String name;
    private final String unit;
    private final List<Feature> features = new ArrayList<>();
    private final Long featureCount; // nullable – set when fetched via DAO summary

    // For new Parts (no id, no pre-known count)
    public Part(String partNumber, String name, String unit) {
        this(null, partNumber, name, unit, null);
    }

    // Hydration from DB without count
    public Part(Long id, String partNumber, String name, String unit) {
        this(id, partNumber, name, unit, null);
    }

    // Hydration from DB with known count
    public Part(Long id, String partNumber, String name, String unit, Long featureCount) {
        this.id = id;
        this.partNumber = requireText(partNumber, "partNumber");
        this.name = requireText(name, "name");
        this.unit = requireText(unit, "unit");
        this.featureCount = featureCount;
    }

    public Long getId() { return id; }
    public String getPartNumber() { return partNumber; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
    public Long getFeatureCount() { return featureCount; }

    public void addFeature(Feature feature) {
        if (feature == null) throw new IllegalArgumentException("feature cannot be null");
        features.add(feature);
    }
    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    @Override
    public String toString() {
        long countToShow = !features.isEmpty()
                ? features.size()
                : (featureCount != null ? featureCount : 0L);
        return "Part{" +
                "id=" + id +
                ", partNumber='" + partNumber + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", features=" + countToShow +
                '}';
    }

    private static String requireText(String s, String field) {
        if (s == null) throw new IllegalArgumentException(field + " cannot be null");
        if (s.trim().isEmpty()) throw new IllegalArgumentException(field + " cannot be blank");
        return s;
    }
}
