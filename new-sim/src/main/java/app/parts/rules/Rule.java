package app.parts.rules;
import java.util.List;
public interface Rule {
    String code();
    String description();
    List<RuleViolation> evaluate(RuleContext ctx);
}
