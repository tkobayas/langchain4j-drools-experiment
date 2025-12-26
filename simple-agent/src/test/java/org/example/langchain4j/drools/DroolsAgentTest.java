package org.example.langchain4j.drools;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import org.junit.jupiter.api.Test;

import static org.example.langchain4j.drools.Models.baseModel;

class DroolsAgentTest {

    @Test
    void testSimpleDrools() {

        LoanApplicationExtractor extractor = AgenticServices.agentBuilder(LoanApplicationExtractor.class)
                .chatModel(baseModel())
                .outputKey("loanApplication")
                .build();

        SimpleDroolsAgent droolsAgent = SimpleDroolsAgent.getInstance(); // non-AI agent

        // supervisor fails because "loanApplication" in agentic scope is overwritten with String (SupervisorPlanner.nextSubagent)
//        LoanAssistant loanAssistant = AgenticServices.supervisorBuilder(LoanAssistant.class)
//                .chatModel(plannerModel())
//                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
//                .subAgents(extractor, droolsAgent)
//                .outputKey("summary")
//                .build();

//        MockEvaluator evaluator = AgenticServices.agentBuilder(MockEvaluator.class)
//                .chatModel(baseModel())
//                .outputKey("result")
//                .build();

        LoanAssistant loanAssistant = AgenticServices.sequenceBuilder(LoanAssistant.class)
                .subAgents(extractor, droolsAgent)
                .outputKey("summary")
                .build();

        ResultWithAgenticScope<String> result = loanAssistant
                .approveLoan("""
                                     Evaluate a loan application for a 45 year old person requesting a loan of $3000.
                                     """);

        System.out.println("===================================================");
        System.out.println("result: " + result.agenticScope().readState("result"));
    }
}
