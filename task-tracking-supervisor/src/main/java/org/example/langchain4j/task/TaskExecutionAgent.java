package org.example.langchain4j.task;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.service.UserMessage;

/**
 * Agent responsible for executing tasks from the task list.
 * This agent identifies the next pending task, executes it, and updates its status.
 */
public interface TaskExecutionAgent {
    
    @UserMessage("""
            You are a task execution specialist. Execute the next pending task in the task list.
            
            Steps:
            1. Use getNextPendingTask to identify the next task to work on
            2. Update its status to IN_PROGRESS using updateTaskStatus
            3. Simulate completing the task (describe what would be done in a real scenario)
            4. Mark it as COMPLETED using completeTask with a brief result description
            5. Use getTaskList to show the updated status
            
            Be specific about what was accomplished in the task result.
            
            Return a summary of the task execution and the updated task list.
            """)
    @Agent(description = "Executes the next pending task and updates its status to completed",
           outputKey = "taskExecutionSummary")
    String executeNextTask();
    
    @ToolsSupplier
    static Object tools() {
        return TaskManagementTool.getInstance();
    }
}

// Made with Bob
