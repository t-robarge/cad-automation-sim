package app.parts.api.dto;

public record PartSummaryResponse(long id, String partNumber, String name, String unit, long featureCount) {}
