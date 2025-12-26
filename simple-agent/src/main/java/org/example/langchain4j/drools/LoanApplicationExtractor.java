package org.example.langchain4j.drools;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.example.langchain4j.drools.domain.LoanApplication;

public interface LoanApplicationExtractor {

    @UserMessage("""
            Convert user request into a structured LoanApplication.
            Infer missing values when reasonable.
            The user request is: '{{request}}'.
            """)
    @Agent("Extract a LoanApplication from user request.")
    LoanApplication extract(@V("request") String request);
}
