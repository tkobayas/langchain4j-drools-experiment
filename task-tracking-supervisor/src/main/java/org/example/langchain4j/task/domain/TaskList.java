package org.example.langchain4j.task.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages a collection of tasks for a specific goal or project.
 */
public class TaskList {
    
    private final String description;
    private final List<Task> tasks;
    private int nextId;
    
    public TaskList(String description) {
        this.description = description;
        this.tasks = new ArrayList<>();
        this.nextId = 1;
    }
    
    /**
     * Add a new task to the list.
     */
    public Task addTask(String taskDescription) {
        Task task = new Task(nextId++, taskDescription);
        tasks.add(task);
        return task;
    }
    
    /**
     * Get a task by its ID.
     */
    public Optional<Task> getTask(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }
    
    /**
     * Update the status of a task.
     */
    public boolean updateTaskStatus(int id, TaskStatus status) {
        Optional<Task> task = getTask(id);
        if (task.isPresent()) {
            task.get().setStatus(status);
            return true;
        }
        return false;
    }
    
    /**
     * Mark a task as completed with a result.
     */
    public boolean completeTask(int id, String result) {
        Optional<Task> task = getTask(id);
        if (task.isPresent()) {
            task.get().setStatus(TaskStatus.COMPLETED);
            task.get().setResult(result);
            return true;
        }
        return false;
    }
    
    /**
     * Get all tasks.
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
    /**
     * Get all pending tasks.
     */
    public List<Task> getPendingTasks() {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all in-progress tasks.
     */
    public List<Task> getInProgressTasks() {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all completed tasks.
     */
    public List<Task> getCompletedTasks() {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());
    }
    
    /**
     * Get the next pending task, if any.
     */
    public Optional<Task> getNextPendingTask() {
        return tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .findFirst();
    }
    
    /**
     * Get a progress summary string.
     */
    public String getProgress() {
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long pending = tasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count();
        int total = tasks.size();
        
        return String.format("Progress: %d/%d completed, %d in progress, %d pending", 
                completed, total, inProgress, pending);
    }
    
    /**
     * Get a formatted string representation of the task list.
     */
    public String getFormattedList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task List: ").append(description).append("\n");
        sb.append("=".repeat(50)).append("\n");
        
        if (tasks.isEmpty()) {
            sb.append("No tasks yet.\n");
        } else {
            for (Task task : tasks) {
                sb.append(task.getFormattedStatus());
                if (task.getResult() != null && !task.getResult().isEmpty()) {
                    sb.append("\n  Result: ").append(task.getResult());
                }
                sb.append("\n");
            }
            sb.append("=".repeat(50)).append("\n");
            sb.append(getProgress()).append("\n");
        }
        
        return sb.toString();
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getTaskCount() {
        return tasks.size();
    }
    
    @Override
    public String toString() {
        return String.format("TaskList{description='%s', tasks=%d, completed=%d}",
                description, tasks.size(), getCompletedTasks().size());
    }
}
