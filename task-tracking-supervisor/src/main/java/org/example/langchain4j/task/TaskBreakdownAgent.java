package org.example.langchain4j.task;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Agent responsible for breaking down complex requests into manageable sub-tasks.
 * This agent analyzes the user's request and creates a structured task list.
 */
public interface TaskBreakdownAgent {
    
    @UserMessage("""
            You are a task breakdown specialist. Analyze the following request and break it down into smaller, manageable sub-tasks.
            
            Steps:
            1. First, create a task list using createTaskList with a brief description of the overall goal
            2. Then, add each sub-task using addTask with clear, actionable descriptions
            3. Finally, use getTaskList to show the complete task breakdown
            
            Make sure each task is:
            - Specific and actionable
            - Small enough to be completed independently
            - Ordered logically (if there are dependencies)
            
            User request: {{request}}
            
            Return a summary of the task list created.
            """)
    @Agent(description = "Breaks down complex requests into manageable sub-tasks and creates a task list", 
           outputKey = "taskBreakdownSummary")
    String breakdownTask(@V("request") String request);
    
    @ToolsSupplier
    static Object tools() {
        return TaskManagementTool.getInstance();
    }
}

// Made with Bob
