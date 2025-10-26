package app.parts.rules;
import app.parts.Part;
import app.parts.Feature;
import java.util.List;
public record RuleContext(Part part, List<Feature> features) {}
