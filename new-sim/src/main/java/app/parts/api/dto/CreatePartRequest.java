package app.parts.api.dto;

import java.util.List;

public class CreatePartRequest {
    public String partNumber;
    public String name;
    public String unit;
    public List<CreateFeatureRequest> features;
}
