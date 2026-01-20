package org.example.langchain4j.drools;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.langchain4j.drools.domain.LoanApplication;
import org.example.langchain4j.drools.domain.Person;
import org.kie.api.KieBase;
import org.jboss.logging.Logger;

/**
 * A simple Drools tool that contains loan approval rules.
 * Exposed as an MCP server tool via Quarkus MCP.
 */
@ApplicationScoped
public class SimpleDroolsTool {

    private static final Logger LOG = Logger.getLogger(SimpleDroolsTool.class);

    private final KieBase kieBase;

    public SimpleDroolsTool() {
        this.kieBase = DroolsUtils.createKieBase("loan-application.drl");
    }

    @Tool(description = "Evaluates a loan application based on business rules. " +
                       "Approves loans up to $5000 for applicants 18 years or older. " +
                       "Returns 'true' if approved, 'false' if rejected.")
    public Boolean approve(
            @ToolArg(description = "Applicant's name") String applicantName,
            @ToolArg(description = "Applicant's age in years") int applicantAge,
            @ToolArg(description = "Loan amount requested in dollars") int amount) {

        Person applicant = new Person(applicantName, applicantAge);
        LoanApplication loanApplication = new LoanApplication(applicant, amount);

        LOG.infof("Evaluating loan application: %s", loanApplication);

        try (var kieSession = kieBase.newKieSession()) {
            kieSession.insert(loanApplication);
            kieSession.fireAllRules();
            boolean result = loanApplication.isApproved();
            LOG.infof("Loan application result: %s", result ? "APPROVED" : "REJECTED");
            return result;
        }
    }
}
