package org.example.langchain4j.drools;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;
import java.util.Map;
import org.example.langchain4j.drools.domain.LoanApplication;
import org.example.langchain4j.drools.domain.Person;
import org.kie.api.KieBase;
import org.kie.internal.utils.KieHelper;

/**
 * A simple Drools agent that contains loan approval rules. NonAI agent.
 */
public class SimpleDroolsAgent {

    private static final SimpleDroolsAgent INSTANCE = new SimpleDroolsAgent();

    private KieBase kieBase;

    private SimpleDroolsAgent() {
        kieBase = DroolsUtils.createKieBase("loan-application.drl");
    }

    public static SimpleDroolsAgent getInstance() {
        return INSTANCE;
    }

    // Key Point: the return value is boolean. It's not very informative, so the supervisor doesn't provide a great summary.
    @Agent(description = "loan approval rule engine.", outputKey = "result")
    public boolean approve(@V("loanApplication") LoanApplication loanApplication) {
        System.out.println("*** SimpleDroolsAgent.approve: loanApplication = " + loanApplication);

        try (var kieSession = kieBase.newKieSession()) {
            kieSession.insert(loanApplication);
            kieSession.fireAllRules();
            return loanApplication.isApproved();
        }
    }
}
