package org.example.langchain4j.drools;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Loan assistant main interface.
 */
public interface LoanAssistant {

    @UserMessage("""
            Evaluate a loan application in a user request.
            A response should indicate 'approved' or 'rejected' with a brief explanation.
            The user request is: '{{request}}'.
            """)
    String approveLoan(@V("request") String request);
}
