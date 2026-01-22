package org.example.langchain4j.task;

import dev.langchain4j.model.openai.OpenAiChatModel;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

/**
 * Utility class for creating chat models used in tests.
 */
public class Models {
    
    /**
     * Create a base chat model for agents.
     */
    public static OpenAiChatModel baseModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .temperature(0.0)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
    
    /**
     * Create a planner model for supervisor.
     */
    public static OpenAiChatModel plannerModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .temperature(0.0)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}

// Made with Bob
