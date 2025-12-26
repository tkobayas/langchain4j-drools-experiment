package org.example.langchain4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public class Models {

//    static {
//        System.out.println("OPENAI_API_KEY: " + System.getenv("OPENAI_API_KEY"));
//        System.out.println("OPENAI_ORGANIZATION_ID: " + System.getenv("OPENAI_ORGANIZATION_ID"));
//        System.out.println("OPENAI_BASE_URL: " + System.getenv("OPENAI_BASE_URL"));
//    }

    private static final ChatModel OPENAI_BASE_MODEL = OpenAiChatModel.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
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
