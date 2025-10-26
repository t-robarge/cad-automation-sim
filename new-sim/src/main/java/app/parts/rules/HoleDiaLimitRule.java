package app.parts.rules.rules;
import app.parts.Feature;
import app.parts.FeatureType;
import app.parts.rules.*;

import java.util.ArrayList;
import java.util.List;

public class HoleDiaLimitRule implements Rule {
    private final double maxDia;
    public HoleDiaLimitRule(double maxDia) { this.maxDia = maxDia; }

    public String code() { return "HOLE_DIA_LIMIT"; }
    public String description() { return "Hole diameter must be <= " + maxDia; }

    public List<RuleViolation> evaluate(RuleContext ctx) {
        List<RuleViolation> out = new ArrayList<>();
        for (Feature f : ctx.features()) {
            if (f.getType() == FeatureType.HOLE && f.getD1() > maxDia) {
                out.add(new RuleViolation(code(),
                        "Hole Ø" + f.getD1() + " exceeds limit " + maxDia,
                        "ERROR"));
            }
        }
        return out;
    }
}
