package org.example.langchain4j.drools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.example.langchain4j.drools.domain.LoanApplication;
import org.kie.api.KieBase;

/**
 * A simple Drools tool that contains loan approval rules.
 */
public class SimpleDroolsTool {

    private static final SimpleDroolsTool INSTANCE = new SimpleDroolsTool();

    private KieBase kieBase;

    private SimpleDroolsTool() {
        kieBase = DroolsUtils.createKieBase("loan-application.drl");
    }

    public static SimpleDroolsTool getInstance() {
        return INSTANCE;
    }

    @Tool("check if a loan application is approved or not.")
    public boolean approve(@P("loan application") LoanApplication loanApplication) {
        System.out.println("*** SimpleDroolsTool.approve: loanApplication = " + loanApplication);

        try (var kieSession = kieBase.newKieSession()) {
            kieSession.insert(loanApplication);
            kieSession.fireAllRules();
            return loanApplication.isApproved();
        }
    }
}
