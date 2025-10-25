package app.parts.dao;

public record PartSummary(
        long id,
        String partNumber,
        String name,
        String unit,
        long featureCount
) {
    @Override
    public String toString() {
        return "PartSummary{" +
                "id=" + id +
                ", partNumber='" + partNumber + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", featureCount=" + featureCount +
                '}';
    }
}
