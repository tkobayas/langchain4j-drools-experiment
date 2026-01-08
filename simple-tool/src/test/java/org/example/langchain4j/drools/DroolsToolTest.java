package org.example.langchain4j.drools;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.langchain4j.drools.Models.baseModel;

class DroolsToolTest {

    /**
     * Demonstrates AI Service with Drools Tool
     *
     * We don't need Extractor here because the tool invocation properly structures the argument `LoanApplication`.
     */
    @Test
    void testSimpleDrools() {

        SimpleDroolsTool droolsTool = SimpleDroolsTool.getInstance(); // Tool

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        LoanAssistant loanAssistant = AiServices.builder(LoanAssistant.class)
                .chatModel(baseModel())
                .chatMemory(chatMemory)
                .tools(droolsTool)
                .build();

        String result = loanAssistant
                .approveLoan("""
                                     Evaluate a loan application for John who is a 45 year old person requesting a loan of $8000.
                                     """);

        System.out.println("===================================================");

        // See that the result doesn't contain much detail about the reason to reject, because the Drools tool returns only boolean.
        System.out.println("result: " + result);

        assertThat(result).containsAnyOf("rejected");
    }
}
