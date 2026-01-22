# Task Tracking Supervisor Example

This example demonstrates task tracking using **SupervisorPlanner** to coordinate multiple specialized agents. It implements the same use case as the `task-tracking` example but uses a multi-agent architecture for comparison.

## Architecture: SupervisorPlanner + Sub-Agents

```
User Request → TaskTrackingSupervisor (Main Interface)
                      ↓
              SupervisorPlanner (Coordinator)
                      ↓
        ┌─────────────┴─────────────┬─────────────┐
        ↓                           ↓             ↓
TaskBreakdownAgent          TaskExecutionAgent  TaskProgressAgent
        ↓                           ↓             ↓
              TaskManagementTool (Shared)
                      ↓
              AgenticScope (Shared State)
```

## Key Components

### Sub-Agents

1. **TaskBreakdownAgent**: Analyzes requests and creates structured task lists
2. **TaskExecutionAgent**: Executes individual tasks and updates status
3. **TaskProgressAgent**: Tracks and reports progress summaries

### Supervisor

**TaskTrackingSupervisor**: Coordinates sub-agents using SupervisorPlanner
- Decides which agent to call based on context
- Manages shared state via AgenticScope
- Provides final summary after coordination

### Shared Components

- **TaskManagementTool**: Same tool used by all agents (from task-tracking)
- **Domain Models**: Task, TaskStatus, TaskList (from task-tracking)

## Comparison with task-tracking

| Aspect | task-tracking<br/>(AI Service + Tool) | task-tracking-supervisor<br/>(SupervisorPlanner) |
|--------|--------------------------------------|--------------------------------------------------|
| **Architecture** | Single AI Service | Supervisor + Multiple Agents |
| **Coordination** | Direct tool calls | Supervisor decides agent routing |
| **State Management** | Tool internal state | AgenticScope shared state |
| **LLM Calls** | ~3-5 per workflow | ~10-15 per workflow |
| **Latency** | Lower | Higher (coordination overhead) |
| **Complexity** | Simpler | More complex |
| **Extensibility** | Add more tools | Add more specialized agents |
| **Best For** | Single domain tasks | Multi-domain workflows |

## Example Usage

### Basic Task Breakdown

```java
// Build sub-agents
TaskBreakdownAgent breakdownAgent = AgenticServices.agentBuilder(TaskBreakdownAgent.class)
        .chatModel(chatModel)
        .build();

// Build supervisor
TaskTrackingSupervisor supervisor = AgenticServices.supervisorBuilder(TaskTrackingSupervisor.class)
        .chatModel(plannerModel)
        .responseStrategy(SupervisorResponseStrategy.SUMMARY)
        .subAgents(breakdownAgent)
        .supervisorContext("Focus on breaking down the task into clear, actionable sub-tasks.")
        .maxAgentsInvocations(5)
        .build();

ResultWithAgenticScope<String> result = supervisor.manageTask(
    "Create a simple calculator application"
);
```

### Complete Workflow with All Agents

```java
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
    "Create a REST API with three endpoints"
);
```

## Running the Tests

```bash
# Set your OpenAI API key
export OPENAI_API_KEY=your-api-key-here

# Run all tests
mvn test

# Run a specific test
mvn test -Dtest=TaskTrackingSupervisorTest#testCompleteWorkflowWithSupervisor
```

### Test Scenarios

1. **testTaskBreakdownWithSupervisor**: Basic task breakdown via supervisor
2. **testBreakdownAndExecuteWithSupervisor**: Breakdown + execution coordination
3. **testCompleteWorkflowWithSupervisor**: Full workflow with all agents
4. **testProgressTrackingWithSupervisor**: Progress reporting via supervisor
5. **testSupervisorOverheadComparison**: Performance measurement

## When to Use This Pattern

### ✅ Use SupervisorPlanner When:

- **Multiple specialized domains**: Different agents for different expertise areas
- **Complex decision trees**: Dynamic routing based on context
- **Agent reusability**: Same agents used in different workflows
- **Parallel execution**: Independent tasks that can run concurrently
- **Template for extension**: Starting point for multi-agent systems

### ❌ Don't Use SupervisorPlanner When:

- **Single domain**: All operations are in one area (like task management)
- **Sequential workflow**: Tasks must be executed in order
- **Performance critical**: Latency and cost are primary concerns
- **Simple use case**: Direct tool calls are sufficient

## Extending This Example

This example serves as a **template** that can be extended with additional agents:

### Example Extensions

1. **Add Domain-Specific Agents**:
   ```java
   // Agent for code-related tasks
   CodeGenerationAgent codeAgent = ...;
   
   // Agent for documentation tasks
   DocumentationAgent docAgent = ...;
   
   // Agent for testing tasks
   TestingAgent testAgent = ...;
   
   // Supervisor coordinates all agents
   supervisor = AgenticServices.supervisorBuilder(...)
           .subAgents(breakdownAgent, codeAgent, docAgent, testAgent)
           .build();
   ```

2. **Add Validation Agent**:
   ```java
   public interface TaskValidationAgent {
       @Agent(description = "Validates task completion and quality")
       String validateTask(@V("taskId") int taskId);
   }
   ```

3. **Add Prioritization Agent**:
   ```java
   public interface TaskPrioritizationAgent {
       @Agent(description = "Analyzes and prioritizes tasks based on dependencies")
       String prioritizeTasks();
   }
   ```

## Performance Considerations

### Overhead Analysis

The supervisor pattern introduces overhead:

1. **Additional LLM Calls**: Supervisor makes decisions about which agent to call
2. **Coordination Time**: Time spent in supervisor planning
3. **State Synchronization**: AgenticScope read/write operations

### Measured Impact

From `testSupervisorOverheadComparison`:
- **LLM Calls**: 2-3x more than direct tool approach
- **Latency**: ~50-100% higher due to coordination
- **Cost**: Proportional to additional LLM calls

### Optimization Tips

1. **Reduce maxAgentsInvocations**: Limit total agent calls
2. **Clear supervisorContext**: Help supervisor make better decisions
3. **Efficient agent descriptions**: Clear, concise agent capabilities
4. **Batch operations**: Group related tasks when possible

## Key Learnings

### Advantages

✅ **Modularity**: Each agent has clear, focused responsibility  
✅ **Extensibility**: Easy to add new specialized agents  
✅ **Reusability**: Agents can be reused in different supervisors  
✅ **Flexibility**: Supervisor adapts to different scenarios  
✅ **Template Value**: Good starting point for complex systems

### Disadvantages

⚠️ **Overhead**: More LLM calls = higher latency and cost  
⚠️ **Complexity**: More moving parts to understand and debug  
⚠️ **Overkill**: For simple use cases, direct approach is better  
⚠️ **Unpredictability**: Supervisor decisions may vary  
⚠️ **State Management**: Requires careful AgenticScope handling

## Comparison Summary

For **this specific use case** (task tracking), the `task-tracking` example with AI Service + Tool is more appropriate because:

1. Task management is a single domain
2. Workflow is inherently sequential
3. No benefit from multi-agent coordination
4. Performance matters for user experience
5. Simpler architecture is easier to maintain

However, this `task-tracking-supervisor` example is valuable as:

1. **Educational tool**: Demonstrates supervisor pattern
2. **Template**: Starting point for multi-domain systems
3. **Comparison**: Shows architectural trade-offs
4. **Reference**: When supervisor pattern IS appropriate

## Learn More

- See [PLAN.md](PLAN.md) for detailed architecture analysis
- Compare with [../task-tracking](../task-tracking) for direct approach
- Check test cases for usage examples
- Review [simple-agent](../simple-agent) for another supervisor example

## Dependencies

- langchain4j-agentic: 1.11.0-beta19-SNAPSHOT
- langchain4j-open-ai: 1.11.0-SNAPSHOT
- JUnit Jupiter: 5.10.0
- AssertJ: 3.24.2