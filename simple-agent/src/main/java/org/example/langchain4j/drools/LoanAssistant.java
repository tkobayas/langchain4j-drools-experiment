package org.example.langchain4j.drools;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.V;

/**
 * Loan assistant main interface.
 */
public interface LoanAssistant {

    // UserMessage is not used by SupervisorPlanner
    // To adjust a final response, use supervisorContext
    @Agent
    ResultWithAgenticScope<String> approveLoan(@V("request") String request);
}
