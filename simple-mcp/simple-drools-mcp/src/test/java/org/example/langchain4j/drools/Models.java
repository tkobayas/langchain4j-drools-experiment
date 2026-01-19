package org.example.langchain4j.drools;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public class Models {

    private static final ChatModel OPENAI_BASE_MODEL = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                        .temperature(0.0)
                        .logRequests(true)
                        .logResponses(true)
                        .build();

    private static final ChatModel OPENAI_PLANNER_MODEL = OPENAI_BASE_MODEL;

    public static ChatModel baseModel() {
        return OPENAI_BASE_MODEL;
    }

    public static ChatModel plannerModel() {
        return OPENAI_PLANNER_MODEL;
    }
}
