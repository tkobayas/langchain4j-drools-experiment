package org.example.langchain4j.drools;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;
import java.util.Map;
import org.example.langchain4j.drools.domain.LoanApplication;
import org.example.langchain4j.drools.domain.Person;
import org.kie.api.KieBase;
import org.kie.internal.utils.KieHelper;

/**
 * A simple Drools agent that contains loan approval rules.
 */
public class SimpleDroolsAgent {

    public static final SimpleDroolsAgent INSTANCE = new SimpleDroolsAgent();

    private KieBase kieBase;

    private SimpleDroolsAgent() {
//        kieBase = DroolsUtils.createKieBase("loan-application.drl");
    }

    @Agent("loan approval rule engine.")
    public boolean approve(@V("request") LoanApplication loanApplication) {

        // run drools
        System.out.println("Running Drools rules for loanApplication: " + loanApplication);
        return true;
    }
}
