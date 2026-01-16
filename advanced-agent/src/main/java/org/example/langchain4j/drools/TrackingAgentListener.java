package org.example.langchain4j.drools;

import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.observability.AgentRequest;
import dev.langchain4j.agentic.observability.AgentResponse;
import dev.langchain4j.service.tool.BeforeToolExecution;
import dev.langchain4j.service.tool.ToolExecution;

/**
 * An AgentListener implementation that tracks and logs agent and tool invocations.
 * Generally, built-in AgentMonitor is good enough, but this class is to explore a better implementation.
 */
public class TrackingAgentListener implements AgentListener {

    public static final int LOG_LENGTH = 255;
    private List<String> logs = new ArrayList<>();
    private int indentationLevel = 0;

    public List<String> getLogs() {
        return logs;
    }

    public void printLogs() {
        logs.forEach(System.out::println);
    }

    public void beforeAgentInvocation(AgentRequest agentRequest) {
         logs.add(formatLog("agentRequest", agentRequest.agentName(), agentRequest.inputs().toString(), indentationLevel++));
    }

    private String formatLog(String phase, String name, String message, int i) {
        return "  ".repeat(i) + "[" + phase + "] (" + name + ") : " + truncate(message);
    }

    private String truncate(String message) {
        // remove new lines and truncate to 100 characters
        String singleLine = message.replaceAll("\\s+", " ");
        if (singleLine.length() > LOG_LENGTH) {
            return singleLine.substring(0, LOG_LENGTH) + "...";
        }
        return singleLine;
    }

    public void afterAgentInvocation(AgentResponse agentResponse) {
        logs.add(formatLog("agentResponse", agentResponse.agentName(), agentResponse.output().toString(), --indentationLevel));
    }

    public void beforeToolExecution(BeforeToolExecution beforeToolExecution) {
        logs.add(formatLog("toolRequest", beforeToolExecution.request().name(), beforeToolExecution.request().arguments(), indentationLevel++));
    }
    public void afterToolExecution(ToolExecution toolExecution) {
        logs.add(formatLog("toolResponse", toolExecution.request().name(), toolExecution.resultObject().toString(), --indentationLevel));
    }
}
