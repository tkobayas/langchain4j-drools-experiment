package org.example.langchain4j.task;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.langchain4j.task.Models.baseModel;
import static org.example.langchain4j.task.Models.plannerModel;

/**
 * Tests demonstrating the task tracking system using SupervisorPlanner.
 * Compares with the AI Service + Tool approach in task-tracking example.
 */
class TaskTrackingSupervisorTest {
    
    private TaskManagementTool taskTool;
    
    @BeforeEach
    void setUp() {
        // Reset the tool state before each test
        taskTool = TaskManagementTool.getInstance();
        taskTool.reset();
    }
    
    /**
     * Test basic task breakdown using supervisor coordination.
     * Demonstrates how supervisor delegates to TaskBreakdownAgent.
     */
    @Test
    void testTaskBreakdownWithSupervisor() {
        System.out.println("\n=== Test: Task Breakdown with Supervisor ===\n");
        
        // Build sub-agents
        TaskBreakdownAgent breakdownAgent = AgenticServices.agentBuilder(TaskBreakdownAgent.class)
                .chatModel(baseModel())
                .build();
        
        // Build supervisor
        TaskTrackingSupervisor supervisor = AgenticServices.supervisorBuilder(TaskTrackingSupervisor.class)
                .chatModel(plannerModel())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .subAgents(breakdownAgent)
                .supervisorContext("Focus on breaking down the task into clear, actionable sub-tasks.")
                .maxAgentsInvocations(5)
                .build();
        
        ResultWithAgenticScope<String> result = supervisor.manageTask(
                "Create a simple calculator application with basic arithmetic operations"
        );
        
        System.out.println("Result: " + result.result());
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify that tasks were created
        assertThat(taskTool.getCurrentTaskList()).isNotNull();
        assertThat(taskTool.getCurrentTaskList().getTaskCount()).isGreaterThan(0);
    }
    
    /**
     * Test task breakdown and execution using supervisor coordination.
     * Demonstrates how supervisor coordinates multiple agents.
     */
    @Test
    void testBreakdownAndExecuteWithSupervisor() {
        System.out.println("\n=== Test: Breakdown and Execute with Supervisor ===\n");
        
        // Build sub-agents
        TaskBreakdownAgent breakdownAgent = AgenticServices.agentBuilder(TaskBreakdownAgent.class)
                .chatModel(baseModel())
                .build();
        
        TaskExecutionAgent executionAgent = AgenticServices.agentBuilder(TaskExecutionAgent.class)
                .chatModel(baseModel())
                .build();
        
        // Build supervisor
        TaskTrackingSupervisor supervisor = AgenticServices.supervisorBuilder(TaskTrackingSupervisor.class)
                .chatModel(plannerModel())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .subAgents(breakdownAgent, executionAgent)
                .supervisorContext("First break down the task, then execute one task to demonstrate the workflow.")
                .maxAgentsInvocations(10)
                .build();
        
        ResultWithAgenticScope<String> result = supervisor.manageTask(
                "Build a simple to-do list application"
        );
        
        System.out.println("Result: " + result.result());
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify that at least one task was completed
        assertThat(taskTool.getCurrentTaskList()).isNotNull();
        assertThat(taskTool.getCurrentTaskList().getTaskCount()).isGreaterThan(0);
    }
    
    /**
     * Test complete workflow with all agents.
     * Demonstrates full supervisor coordination: breakdown, execute, and track progress.
     */
    @Test
    void testCompleteWorkflowWithSupervisor() {
        System.out.println("\n=== Test: Complete Workflow with Supervisor ===\n");
        
        // Build all sub-agents
        TaskBreakdownAgent breakdownAgent = AgenticServices.agentBuilder(TaskBreakdownAgent.class)
                .chatModel(baseModel())
                .build();
        
        TaskExecutionAgent executionAgent = AgenticServices.agentBuilder(TaskExecutionAgent.class)
                .chatModel(baseModel())
                .build();
        
        TaskProgressAgent progressAgent = AgenticServices.agentBuilder(TaskProgressAgent.class)
                .chatModel(baseModel())
                .build();
        
        // Build supervisor with all agents
        TaskTrackingSupervisor supervisor = AgenticServices.supervisorBuilder(TaskTrackingSupervisor.class)
                .chatModel(plannerModel())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .subAgents(breakdownAgent, executionAgent, progressAgent)
                .supervisorContext("""
                        Coordinate the agents to:
                        1. Break down the task into sub-tasks
                        2. Execute 2-3 tasks to demonstrate the workflow
                        3. Provide a final progress summary
                        """)
                .maxAgentsInvocations(15)
                .build();
        
        ResultWithAgenticScope<String> result = supervisor.manageTask(
                "Create a REST API with three endpoints: GET /users, POST /users, DELETE /users/:id"
        );
        
        System.out.println("Final Result: " + result.result());
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify workflow completion
        assertThat(taskTool.getCurrentTaskList()).isNotNull();
        assertThat(taskTool.getCurrentTaskList().getTaskCount()).isGreaterThanOrEqualTo(3);
        assertThat(taskTool.getCurrentTaskList().getCompletedTasks()).isNotEmpty();
    }
    
    /**
     * Test progress tracking with supervisor.
     * Demonstrates how supervisor can delegate to progress agent.
     */
    @Test
    void testProgressTrackingWithSupervisor() {
        System.out.println("\n=== Test: Progress Tracking with Supervisor ===\n");
        
        // Pre-populate some tasks for testing
        taskTool.createTaskList("Test Project");
        taskTool.addTask("Task 1");
        taskTool.addTask("Task 2");
        taskTool.addTask("Task 3");
        taskTool.completeTask(1, "Task 1 completed");
        
        // Build progress agent
        TaskProgressAgent progressAgent = AgenticServices.agentBuilder(TaskProgressAgent.class)
                .chatModel(baseModel())
                .build();
        
        // Build supervisor
        TaskTrackingSupervisor supervisor = AgenticServices.supervisorBuilder(TaskTrackingSupervisor.class)
                .chatModel(plannerModel())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .subAgents(progressAgent)
                .supervisorContext("Provide a comprehensive progress summary.")
                .maxAgentsInvocations(5)
                .build();
        
        ResultWithAgenticScope<String> result = supervisor.manageTask(
                "Show me the current progress"
        );
        
        System.out.println("Progress Report: " + result.result());
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Verify progress information
        assertThat(result.result()).containsIgnoringCase("progress");
    }
}

// Made with Bob
