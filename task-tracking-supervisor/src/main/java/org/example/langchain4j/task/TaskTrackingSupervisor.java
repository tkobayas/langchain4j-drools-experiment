package org.example.langchain4j.task;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.V;

/**
 * Main supervisor interface for task tracking system.
 * Coordinates multiple specialized agents to break down, execute, and track tasks.
 * 
 * This demonstrates the SupervisorPlanner pattern where a supervisor coordinates
 * multiple sub-agents, each with specific responsibilities:
 * - TaskBreakdownAgent: Breaks down complex requests into sub-tasks
 * - TaskExecutionAgent: Executes individual tasks
 * - TaskProgressAgent: Tracks and reports progress
 * 
 * The supervisor uses AgenticScope to share state (TaskList) between agents.
 */
public interface TaskTrackingSupervisor {
    
    /**
     * Main entry point for task management.
     * The supervisor will coordinate sub-agents to:
     * 1. Break down the request into tasks
     * 2. Execute tasks iteratively
     * 3. Track and report progress
     * 
     * @param request The user's request describing what needs to be done
     * @return Result with AgenticScope containing the final summary and shared state
     */
    @Agent
    ResultWithAgenticScope<String> manageTask(@V("request") String request);
}

// Made with Bob
