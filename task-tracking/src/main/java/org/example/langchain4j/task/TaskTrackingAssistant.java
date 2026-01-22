package org.example.langchain4j.task;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI Service interface for task tracking and management.
 * Uses TaskManagementTool to break down complex requests into manageable sub-tasks
 * and track their execution.
 */
public interface TaskTrackingAssistant {
    
    /**
     * Break down a complex user request into smaller, manageable sub-tasks.
     * Creates a task list and adds each sub-task with appropriate descriptions.
     * 
     * @param request The user's request describing what needs to be done
     * @return A formatted list showing all created tasks with their IDs
     */
    @UserMessage("""
            You are a task management assistant. Break down the following request into smaller, manageable sub-tasks.
            
            Steps:
            1. First, create a task list using createTaskList with a brief description of the overall goal
            2. Then, add each sub-task using addTask with clear, actionable descriptions
            3. Finally, use getTaskList to show the complete task breakdown
            
            Make sure each task is:
            - Specific and actionable
            - Small enough to be completed independently
            - Ordered logically (if there are dependencies)
            
            User request: {{request}}
            
            Return the complete task list showing all tasks with their IDs and status.
            """)
    String breakdownTask(@V("request") String request);
    
    /**
     * Execute the next pending task in the task list.
     * Updates status to IN_PROGRESS, simulates work, then marks as COMPLETED.
     * 
     * @return Updated task list showing progress
     */
    @UserMessage("""
            Execute the next pending task in the task list.
            
            Steps:
            1. Use getNextPendingTask to identify the next task to work on
            2. Update its status to IN_PROGRESS using updateTaskStatus
            3. Simulate completing the task (you can describe what would be done)
            4. Mark it as COMPLETED using completeTask with a brief result description
            5. Use getTaskList to show the updated status
            
            Return the updated task list showing the progress.
            """)
    String executeNextTask();
    
    /**
     * Get a summary of the current progress.
     * 
     * @return Progress summary with task counts and status breakdown
     */
    @UserMessage("""
            Provide a comprehensive progress summary of the current task list.
            
            Use getProgress to retrieve the current status and format it in a clear,
            easy-to-read manner showing:
            - Overall progress (completed/total)
            - Completed tasks with their results
            - Tasks currently in progress
            - Pending tasks
            
            Make the summary informative and encouraging.
            """)
    String getProgress();
    
    /**
     * Execute all remaining tasks in sequence.
     * 
     * @return Final summary after all tasks are completed
     */
    @UserMessage("""
            Execute all remaining pending tasks in the task list, one by one.
            
            For each pending task:
            1. Get the next pending task
            2. Update status to IN_PROGRESS
            3. Simulate completing it with a realistic result
            4. Mark as COMPLETED
            
            Continue until all tasks are completed, then provide a final summary
            using getProgress showing all completed tasks.
            
            Return a comprehensive summary of all work completed.
            """)
    String executeAllTasks();
}
