package app.parts.rules;
import java.util.ArrayList;
import java.util.List;

public class RuleEngine {
    private final List<Rule> rules = new ArrayList<>();
    public RuleEngine register(Rule r) { rules.add(r); return this; }
    public List<RuleViolation> run(RuleContext ctx) {
        List<RuleViolation> out = new ArrayList<>();
        for (Rule r : rules) out.addAll(r.evaluate(ctx));
        return out;
    }
}
