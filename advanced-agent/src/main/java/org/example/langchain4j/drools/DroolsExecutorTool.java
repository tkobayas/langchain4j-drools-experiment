package org.example.langchain4j.drools;

import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.example.langchain4j.drools.domain.LoanApplication;
import org.kie.api.KieBase;
import org.kie.api.event.rule.DefaultAgendaEventListener;

/**
 * A tool for executing loan approval rules. Provides any information specific to Drools execution.
 */
public class DroolsExecutorTool {

    private static final DroolsExecutorTool INSTANCE = new DroolsExecutorTool();
    public static final String LOAN_APPLICATION_DRL_FILE_NAME = "loan-application.drl";

    private KieBase kieBase;

    private DroolsExecutorTool() {
        kieBase = DroolsUtils.createKieBase(LOAN_APPLICATION_DRL_FILE_NAME);
    }

    public static DroolsExecutorTool getInstance() {
        return INSTANCE;
    }

    @Tool("evaluate if a loan application is approved or not.")
    public RuleResult executeRules(@P("loan application") LoanApplication loanApplication) {
        System.out.println("*** DroolsExecutorTool.evaluate: loanApplication = " + loanApplication);

        try (var kieSession = kieBase.newKieSession()) {

            // will be replaced by TrackingAgendaEventListener after drools 10.2.0
            List<String> firedRules = new ArrayList<>();
            kieSession.addEventListener(new DefaultAgendaEventListener() {
                @Override
                public void afterMatchFired(org.kie.api.event.rule.AfterMatchFiredEvent event) {
                    firedRules.add(event.getMatch().getRule().getName());
                }
            });

            kieSession.insert(loanApplication);
            kieSession.fireAllRules();
            return new RuleResult(loanApplication.isApproved(), firedRules);
        }
    }

    @Tool("provides loan application rules in DRL format.")
    public String getLoanApplicationDrlRules() {
        return DroolsUtils.getDrlRulesAsString(LOAN_APPLICATION_DRL_FILE_NAME);
    }
}
