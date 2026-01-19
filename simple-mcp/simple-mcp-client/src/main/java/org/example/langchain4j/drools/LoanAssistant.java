package org.example.langchain4j.drools;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

/**
 * Loan assistant AI Service interface.
 * Uses MCP to communicate with the Drools tool server.
 */
@RegisterAiService
public interface LoanAssistant {

    @SystemMessage("""
            You are a loan approval assistant. You have access to a tool that evaluates
            loan applications based on business rules. Use this tool to check if a loan
            should be approved or rejected. The tool requires the applicant's name, age,
            and the loan amount requested.
            """)
    @UserMessage("""
            Evaluate a loan application in a user request.
            A response should indicate 'approved' or 'rejected' with a brief explanation.
            The user request is: '{{request}}'.
            """)
    @McpToolBox
    String approveLoan(@V("request") String request);
}
