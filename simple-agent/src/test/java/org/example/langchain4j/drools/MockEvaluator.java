package org.example.langchain4j.drools;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.example.langchain4j.drools.domain.LoanApplication;

public interface MockEvaluator {

    @UserMessage("""
       evaluate a loan application '{{loanApplication}}'.
       Use your common sense.
       """)
    @Agent("loan evaluation")
    boolean approve(@V("loanApplication") LoanApplication loanApplication);
}
