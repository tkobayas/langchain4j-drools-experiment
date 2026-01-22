package org.example.langchain4j.task.domain;

/**
 * Represents the status of a task in the task tracking system.
 */
public enum TaskStatus {
    /**
     * Task has been created but not yet started
     */
    PENDING,
    
    /**
     * Task is currently being worked on
     */
    IN_PROGRESS,
    
    /**
     * Task has been completed
     */
    COMPLETED
}
