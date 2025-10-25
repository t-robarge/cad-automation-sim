package app.parts;
public enum FeatureType {
    HOLE, SLOT, POCKET, CUTOUT;

    public static FeatureType parse(String s) {
        if (s == null) throw new IllegalArgumentException("feature type cannot be null");
        try {
            return FeatureType.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Unknown feature type: '" + s + "'. Allowed: HOLE, SLOT, POCKET, CUTOUT"
            );
        }
    }
}
