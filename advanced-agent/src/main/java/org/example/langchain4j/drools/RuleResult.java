package org.example.langchain4j.drools;

import java.util.List;

public class RuleResult {

    private final boolean approved;
    private final List<String> firedRules;

    public RuleResult(boolean approved, List<String> firedRules) {
        this.approved = approved;
        this.firedRules = firedRules;
    }

    public boolean isApproved() {
        return approved;
    }

    public List<String> getFiredRules() {
        return firedRules;
    }
}
