package app.parts;

public class Feature {
    private final Long id; // nullable until persisted
    private final Long partId; // nullable until we insert (or use repo to set it)
    private final FeatureType type;
    private final double x;
    private final double y;
    private final double d1;
    private final double d2;

    // New feature before insert, partId unknown
    public Feature(FeatureType type, double x, double y, double d1, double d2) {
        this(null, null, type, x, y, d1, d2);
    }

    // Convenience ctor for string types
    public Feature(String type, double x, double y, double d1, double d2) {
        this(FeatureType.parse(type), x, y, d1, d2);
    }

    // Hydration or fully-specified constructor
    public Feature(Long id, Long partId, FeatureType type, double x, double y, double d1, double d2) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        this.id = id;
        this.partId = partId;
        this.type = type;
        this.x = requireFinite(x, "x");
        this.y = requireFinite(y, "y");
        this.d1 = requireNonNegative(requireFinite(d1, "d1"), "d1");
        this.d2 = requireNonNegative(requireFinite(d2, "d2"), "d2");
    }

    public Long getId() { return id; }
    public Long getPartId() { return partId; }
    public FeatureType getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getD1() { return d1; }
    public double getD2() { return d2; }

    @Override
    public String toString() {
        return "Feature{" +
                "id=" + id +
                ", partId=" + partId +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", d1=" + d1 +
                ", d2=" + d2 +
                '}';
    }

    private static double requireFinite(double v, String field) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            throw new IllegalArgumentException(field + " must be a finite number");
        }
        return v;
    }
    private static double requireNonNegative(double v, String field) {
        if (v < 0) throw new IllegalArgumentException(field + " must be >= 0");
        return v;
    }
}
