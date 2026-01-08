package org.example.langchain4j.drools;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.example.langchain4j.drools.domain.LoanApplication;
import org.kie.api.KieBase;

/**
 * A simple Drools agent that contains loan approval rules. NonAI agent.
 */
public interface AdvancedDroolsAgent {

    // This example uses whole DRL rules to explain the decision
    // It may leak internal business secrets. If it's not desired, consider using rule names to provide such information.
    @UserMessage("""
            Evaluate a loan application in a user request.
            A response should indicate 'approved' or 'rejected' with a brief explanation including the reason of the decision.
            Use loan application rules text in DRL format to enrich the explanation.
            The user request is: '{{request}}'.
            """)
    @Agent("Evaluate a loan application in user request.")
    String evaluate(@V("request") String request);

    @ToolsSupplier
    static Object tools() {
        return DroolsExecutorTool.getInstance();
    }

}
