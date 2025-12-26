package org.example.langchain4j;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import org.example.langchain4j.drools.SimpleDroolsAgent;
import org.junit.jupiter.api.Test;

import static org.example.langchain4j.Models.plannerModel;

class DroolsAgentTest {

    @Test
    void testSimpleDrools() {

        SupervisorAgent supervisorAgent = AgenticServices.supervisorBuilder()
                .chatModel(plannerModel())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .subAgents(SimpleDroolsAgent.INSTANCE)
                .outputKey("summary")
                .build();

        ResultWithAgenticScope<String> result = supervisorAgent
                .invokeWithAgenticScope("""
                                                Evaluate a loan application for a 45 year old person requesting a loan of $5000.
                                                """);

        System.out.println("===================================================");
        System.out.println("summary: " + result.agenticScope().readState("summary"));
    }
}
