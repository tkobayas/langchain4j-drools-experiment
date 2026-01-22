package org.example.langchain4j.task;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.example.langchain4j.task.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.langchain4j.task.Models.baseModel;

/**
 * Tests demonstrating the task tracking system.
 */
class TaskTrackingTest {
    
    private TaskManagementTool taskTool;
    private TaskTrackingAssistant assistant;
    
    @BeforeEach
    void setUp() {
        // Reset the tool state before each test
        taskTool = TaskManagementTool.getInstance();
        taskTool.reset();
        
        // Create the AI service with the task management tool
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        
        assistant = AiServices.builder(TaskTrackingAssistant.class)
                .chatModel(baseModel())
                .chatMemory(chatMemory)
                .tools(taskTool)
                .build();
    }
    
    /**
     * Test basic task breakdown functionality.
     * Demonstrates how the AI breaks down a complex request into sub-tasks.
     */
    @Test
    void testTaskBreakdown() {
        System.out.println("\n=== Test: Task Breakdown ===\n");
        
        String result = assistant.breakdownTask(
                "Create a simple calculator application with basic arithmetic operations"
        );
        
        System.out.println(result);
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify that tasks were created
        assertThat(taskTool.getCurrentTaskList()).isNotNull();
        assertThat(taskTool.getCurrentTaskList().getTaskCount()).isGreaterThan(0);
        assertThat(result).containsIgnoringCase("calculator");
    }
    
    /**
     * Test executing a single task.
     * Demonstrates status transitions: PENDING -> IN_PROGRESS -> COMPLETED
     */
    @Test
    void testExecuteSingleTask() {
        System.out.println("\n=== Test: Execute Single Task ===\n");
        
        // First, break down the task
        String breakdown = assistant.breakdownTask(
                "Build a simple to-do list application"
        );
        System.out.println("Initial breakdown:");
        System.out.println(breakdown);
        System.out.println("\n" + "-".repeat(80) + "\n");
        
        // Execute the first task
        String execution = assistant.executeNextTask();
        System.out.println("After executing first task:");
        System.out.println(execution);
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify that at least one task is completed
        assertThat(taskTool.getCurrentTaskList().getCompletedTasks()).isNotEmpty();
    }
    
    /**
     * Test progress tracking.
     * Demonstrates how to monitor task completion progress.
     */
    @Test
    void testProgressTracking() {
        System.out.println("\n=== Test: Progress Tracking ===\n");
        
        // Break down a task
        assistant.breakdownTask("Create a REST API with three endpoints");
        
        // Execute a couple of tasks
        assistant.executeNextTask();
        assistant.executeNextTask();
        
        // Get progress
        String progress = assistant.getProgress();
        System.out.println(progress);
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify progress information is present
        assertThat(progress).containsIgnoringCase("progress");
        assertThat(progress).containsIgnoringCase("completed");
    }
    
    /**
     * Test complete workflow: breakdown -> execute all -> verify completion.
     * This is the main demonstration of the task tracking system.
     */
    @Test
    void testCompleteWorkflow() {
        System.out.println("\n=== Test: Complete Workflow ===\n");
        
        // Step 1: Break down the task
        System.out.println("STEP 1: Breaking down the task...\n");
        String breakdown = assistant.breakdownTask(
                "Create a simple calculator with addition, subtraction, multiplication, and division"
        );
        System.out.println(breakdown);
        System.out.println("\n" + "-".repeat(80) + "\n");
        
        int totalTasks = taskTool.getCurrentTaskList().getTaskCount();
        System.out.println("Total tasks created: " + totalTasks);
        
        // Step 2: Execute all tasks
        System.out.println("\nSTEP 2: Executing all tasks...\n");
        String execution = assistant.executeAllTasks();
        System.out.println(execution);
        System.out.println("\n" + "-".repeat(80) + "\n");
        
        // Step 3: Verify completion
        System.out.println("STEP 3: Verifying completion...\n");
        String finalProgress = assistant.getProgress();
        System.out.println(finalProgress);
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Assertions
        assertThat(taskTool.getCurrentTaskList().getCompletedTasks().size())
                .isEqualTo(totalTasks);
        assertThat(taskTool.getCurrentTaskList().getPendingTasks()).isEmpty();
        assertThat(finalProgress).containsIgnoringCase("completed");
    }
    
    /**
     * Test with a different domain: building a web application.
     */
    @Test
    void testWebApplicationBreakdown() {
        System.out.println("\n=== Test: Web Application Breakdown ===\n");
        
        String result = assistant.breakdownTask(
                "Build a blog website with user authentication, post creation, and comments"
        );
        
        System.out.println(result);
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify reasonable number of tasks
        int taskCount = taskTool.getCurrentTaskList().getTaskCount();
        assertThat(taskCount).isGreaterThanOrEqualTo(5);
        assertThat(taskCount).isLessThanOrEqualTo(15);
        
        // Execute a few tasks to show progress
        System.out.println("Executing first 3 tasks...\n");
        assistant.executeNextTask();
        assistant.executeNextTask();
        assistant.executeNextTask();
        
        String progress = assistant.getProgress();
        System.out.println(progress);
        System.out.println("\n" + "=".repeat(80) + "\n");
    }
    
    /**
     * Test direct tool usage (without AI).
     * Demonstrates the tool API directly.
     */
    @Test
    void testDirectToolUsage() {
        System.out.println("\n=== Test: Direct Tool Usage ===\n");
        
        // Create task list
        String created = taskTool.createTaskList("Test Project");
        System.out.println(created);
        
        // Add tasks
        System.out.println(taskTool.addTask("Design database schema"));
        System.out.println(taskTool.addTask("Implement API endpoints"));
        System.out.println(taskTool.addTask("Write unit tests"));
        
        // Show initial list
        System.out.println("\nInitial task list:");
        System.out.println(taskTool.getTaskList());
        
        // Update task status
        System.out.println("\nUpdating task #1 to IN_PROGRESS:");
        System.out.println(taskTool.updateTaskStatus(1, "IN_PROGRESS"));
        
        // Complete a task
        System.out.println("\nCompleting task #1:");
        System.out.println(taskTool.completeTask(1, "Database schema designed with 5 tables"));
        
        // Show progress
        System.out.println("\nCurrent progress:");
        System.out.println(taskTool.getProgress());
        
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify
        assertThat(taskTool.getCurrentTaskList().getTaskCount()).isEqualTo(3);
        assertThat(taskTool.getCurrentTaskList().getTask(1).get().getStatus())
                .isEqualTo(TaskStatus.COMPLETED);
    }
}
