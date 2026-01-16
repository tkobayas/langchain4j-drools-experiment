package org.example.langchain4j.drools;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import org.junit.jupiter.api.Test;
import util.log.CustomLogging;
import util.log.LogLevels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.langchain4j.drools.Models.baseModel;
import static org.example.langchain4j.drools.Models.plannerModel;

class DroolsAgentTest {

    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 200);  // control how much you see from the model calls
    }

    /**
     * Demonstrates Supervisor with a Loan Agent with Extractor Agent + Drools Tool + summary providing
     */
    @Test
    void testAdvancedDrools() {

        TrackingAgentListener agentListener = new TrackingAgentListener();

        AdvancedDroolsAgent droolsAgent = AgenticServices.agentBuilder(AdvancedDroolsAgent.class)
                .chatModel(baseModel())
                .listener(agentListener)
                .outputKey("evaluation")
                .build();

        LoanAssistant loanAssistant = AgenticServices.supervisorBuilder(LoanAssistant.class)
                .chatModel(plannerModel())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .subAgents(droolsAgent)
                .listener(agentListener)
                .outputKey("summary")
                .build();

        ResultWithAgenticScope<String> result = loanAssistant
                .approveLoan("""
                                     Evaluate a loan application for John who is a 45 year old person requesting a loan of $8000.
                                     """);

        String summary = (String) result.agenticScope().readState("summary");

        System.out.println("===================================================");
        System.out.println("summary: " + summary);

        assertThat(summary).containsAnyOf("rejected");

        System.out.println("===================================================");
        System.out.println("Agent Listener Logs:");
        agentListener.printLogs();
    }
}
