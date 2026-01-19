package org.example.langchain4j.drools;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for LoanAssistant using MCP to communicate with Drools server.
 * Requires the MCP server to be built first: mvn clean package in simple-drools-mcp.
 */
@QuarkusTest
class LoanAssistantMcpTest {

    @Inject
    LoanAssistant loanAssistant;

    @Test
    void testApproveLoan_approved() {
        String result = loanAssistant.approveLoan(
            "Evaluate a loan application for John who is a 45 year old person requesting a loan of $3000."
        );

        System.out.println("Result: " + result);

        assertThat(result)
            .containsIgnoringCase("approve");
    }

    @Test
    void testApproveLoan_approved_edgeCase() {
        String result = loanAssistant.approveLoan(
            "Evaluate a loan application for Alice who is 18 years old requesting a loan of $5000."
        );

        System.out.println("Result: " + result);

        assertThat(result)
            .containsIgnoringCase("approve");
    }

    @Test
    void testApproveLoan_rejected_tooYoung() {
        String result = loanAssistant.approveLoan(
            "Evaluate a loan application for Jane who is a 16 year old person requesting a loan of $2000."
        );

        System.out.println("Result: " + result);

        assertThat(result)
            .containsIgnoringCase("reject");
    }

    @Test
    void testApproveLoan_rejected_tooMuch() {
        String result = loanAssistant.approveLoan(
            "Evaluate a loan application for Bob who is a 30 year old person requesting a loan of $8000."
        );

        System.out.println("Result: " + result);

        assertThat(result)
            .containsIgnoringCase("reject");
    }

    @Test
    void testApproveLoan_rejected_bothConditionsFail() {
        String result = loanAssistant.approveLoan(
            "Evaluate a loan application for Charlie who is 15 years old requesting a loan of $10000."
        );

        System.out.println("Result: " + result);

        assertThat(result)
            .containsIgnoringCase("reject");
    }
}
