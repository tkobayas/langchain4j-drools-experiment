package org.example.langchain4j.task;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import org.example.langchain4j.task.domain.Task;
import org.example.langchain4j.task.domain.TaskList;
import org.example.langchain4j.task.domain.TaskStatus;

import java.util.Optional;

/**
 * Tool for managing tasks in a task tracking system.
 * Provides methods to create, update, and query tasks.
 */
public class TaskManagementTool {
    
    private static final TaskManagementTool INSTANCE = new TaskManagementTool();
    
    private TaskList currentTaskList;
    
    private TaskManagementTool() {
        // Private constructor for singleton
    }
    
    public static TaskManagementTool getInstance() {
        return INSTANCE;
    }
    
    /**
     * Reset the tool state (useful for testing).
     */
    public void reset() {
        currentTaskList = null;
    }
    
    @Tool("Create a new task list for a given goal or project. This should be called first before adding any tasks.")
    public String createTaskList(@P("Description of the overall goal or project") String description) {
        currentTaskList = new TaskList(description);
        return String.format("Task list created: '%s'", description);
    }
    
    @Tool("Add a new task to the current task list. Returns the task ID.")
    public String addTask(@P("Description of the task to add") String taskDescription) {
        if (currentTaskList == null) {
            return "Error: No task list exists. Please create a task list first using createTaskList.";
        }
        
        Task task = currentTaskList.addTask(taskDescription);
        return String.format("Task #%d added: %s", task.getId(), taskDescription);
    }
    
    @Tool("Update the status of a specific task. Valid statuses are: PENDING, IN_PROGRESS, COMPLETED")
    public String updateTaskStatus(
            @P("ID of the task to update") int taskId,
            @P("New status: PENDING, IN_PROGRESS, or COMPLETED") String statusStr) {
        
        if (currentTaskList == null) {
            return "Error: No task list exists.";
        }
        
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return String.format("Error: Invalid status '%s'. Valid values are: PENDING, IN_PROGRESS, COMPLETED", statusStr);
        }
        
        Optional<Task> task = currentTaskList.getTask(taskId);
        if (task.isEmpty()) {
            return String.format("Error: Task #%d not found.", taskId);
        }
        
        currentTaskList.updateTaskStatus(taskId, status);
        return String.format("Task #%d status updated to %s: %s", taskId, status, task.get().getDescription());
    }
    
    @Tool("Mark a task as completed with an optional result or outcome")
    public String completeTask(
            @P("ID of the task to complete") int taskId,
            @P("Result or outcome of the task (optional)") String result) {
        
        if (currentTaskList == null) {
            return "Error: No task list exists.";
        }
        
        Optional<Task> task = currentTaskList.getTask(taskId);
        if (task.isEmpty()) {
            return String.format("Error: Task #%d not found.", taskId);
        }
        
        currentTaskList.completeTask(taskId, result);
        return String.format("Task #%d completed: %s\nResult: %s", taskId, task.get().getDescription(), result);
    }
    
    @Tool("Get the current task list with all tasks and their statuses")
    public String getTaskList() {
        if (currentTaskList == null) {
            return "No task list exists. Create one first using createTaskList.";
        }
        
        return currentTaskList.getFormattedList();
    }
    
    @Tool("Get a summary of the current progress including task counts by status")
    public String getProgress() {
        if (currentTaskList == null) {
            return "No task list exists.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Progress Summary\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append(currentTaskList.getProgress()).append("\n\n");
        
        if (!currentTaskList.getCompletedTasks().isEmpty()) {
            sb.append("Completed Tasks:\n");
            for (Task task : currentTaskList.getCompletedTasks()) {
                sb.append("  ✓ ").append(task.getDescription());
                if (task.getResult() != null && !task.getResult().isEmpty()) {
                    sb.append(" - ").append(task.getResult());
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        
        if (!currentTaskList.getInProgressTasks().isEmpty()) {
            sb.append("In Progress:\n");
            for (Task task : currentTaskList.getInProgressTasks()) {
                sb.append("  → ").append(task.getDescription()).append("\n");
            }
            sb.append("\n");
        }
        
        if (!currentTaskList.getPendingTasks().isEmpty()) {
            sb.append("Pending Tasks:\n");
            for (Task task : currentTaskList.getPendingTasks()) {
                sb.append("  [ ] ").append(task.getDescription()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    @Tool("Get the next pending task that needs to be worked on")
    public String getNextPendingTask() {
        if (currentTaskList == null) {
            return "No task list exists.";
        }
        
        Optional<Task> nextTask = currentTaskList.getNextPendingTask();
        if (nextTask.isEmpty()) {
            return "No pending tasks. All tasks are either in progress or completed!";
        }
        
        Task task = nextTask.get();
        return String.format("Next pending task: #%d - %s", task.getId(), task.getDescription());
    }
    
    /**
     * Get the current task list (for testing purposes).
     */
    public TaskList getCurrentTaskList() {
        return currentTaskList;
    }
}
