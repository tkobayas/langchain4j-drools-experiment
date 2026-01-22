package org.example.langchain4j.task.domain;

/**
 * Represents a single task in the task tracking system.
 */
public class Task {
    
    private final int id;
    private final String description;
    private TaskStatus status;
    private String result;
    
    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.result = null;
    }
    
    public int getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    /**
     * Get a formatted string representation of the task with status icon.
     */
    public String getFormattedStatus() {
        String icon = switch (status) {
            case PENDING -> "[ ]";
            case IN_PROGRESS -> "[→]";
            case COMPLETED -> "[✓]";
        };
        return String.format("%s Task #%d: %s", icon, id, description);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Task{id=%d, status=%s, description='%s'", id, status, description));
        if (result != null && !result.isEmpty()) {
            sb.append(String.format(", result='%s'", result));
        }
        sb.append("}");
        return sb.toString();
    }
}
