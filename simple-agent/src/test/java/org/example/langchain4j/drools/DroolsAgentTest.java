package org.example.langchain4j.drools;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.langchain4j.drools.Models.baseModel;
import static org.example.langchain4j.drools.Models.plannerModel;

class DroolsAgentTest {

    /**
     * Demonstrates sequence Extractor -> NonAI Drools Agent
     */
    @Test
    void testSimpleDrools() {

        LoanApplicationExtractor extractor = AgenticServices.agentBuilder(LoanApplicationExtractor.class)
                .chatModel(baseModel())
                .outputKey("loanApplication")
                .build();

        SimpleDroolsAgent droolsAgent = SimpleDroolsAgent.getInstance(); // non-AI agent

        // With version 1.9.1, supervisor fails because "loanApplication" in agentic scope is overwritten with String (SupervisorPlanner.nextSubagent)
        // See https://github.com/langchain4j/langchain4j/issues/4375
        // This works since 1.11.0-beta19-SNAPSHOT
        LoanAssistant loanAssistant = AgenticServices.supervisorBuilder(LoanAssistant.class)
                .chatModel(plannerModel())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .subAgents(extractor, droolsAgent)
                .supervisorContext("When writing a final response summary, use a word 'approved' or 'rejected' to indicate loan application status.")
                .outputKey("summary")
                .build();

        ResultWithAgenticScope<String> result = loanAssistant
                .approveLoan("""
                                     Evaluate a loan application for John who is a 45 year old person requesting a loan of $8000.
                                     """);

        String summary = (String) result.agenticScope().readState("summary");

        System.out.println("===================================================");
        System.out.println("summary: " + summary);

        // This is useful to confirm the Drools agent's decision
        assertThat((Boolean) result.agenticScope().readState("result")).isFalse();

        assertThat(summary).containsAnyOf("rejected");
    }
}
