package app.parts.rules.rules;
import app.parts.Feature;
import app.parts.FeatureType;
import app.parts.rules.*;

import java.util.List;

public class MinHoleCountRule implements Rule {
    private final int min;
    public MinHoleCountRule(int min) { this.min = min; }

    public String code() { return "MIN_HOLE_COUNT"; }
    public String description() { return "Part must contain at least " + min + " holes"; }

    public List<RuleViolation> evaluate(RuleContext ctx) {
        long holes = ctx.features().stream().filter(f -> f.getType()==FeatureType.HOLE).count();
        return holes >= min ? List.of()
            : List.of(new RuleViolation(code(), "Found " + holes + " holes; need " + min, "WARN"));
    }
}
