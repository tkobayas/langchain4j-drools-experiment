package org.example.langchain4j.task;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.service.UserMessage;

/**
 * Agent responsible for tracking and reporting progress on the task list.
 * This agent provides comprehensive summaries of task completion status.
 */
public interface TaskProgressAgent {
    
    @UserMessage("""
            You are a progress tracking specialist. Provide a comprehensive progress summary of the current task list.
            
            Use getProgress to retrieve the current status and format it in a clear, easy-to-read manner showing:
            - Overall progress (completed/total)
            - Completed tasks with their results
            - Tasks currently in progress
            - Pending tasks
            
            Make the summary informative and encouraging. Highlight what has been accomplished.
            
            Return a well-formatted progress report.
            """)
    @Agent(description = "Provides progress summary showing completed, in-progress, and pending tasks",
           outputKey = "progressSummary")
    String getProgress();
    
    @ToolsSupplier
    static Object tools() {
        return TaskManagementTool.getInstance();
    }
}

// Made with Bob
