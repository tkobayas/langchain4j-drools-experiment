# Task Tracking Example

This example demonstrates how to build an AI-powered task tracking system using langchain4j that can:
- Break down complex user requests into smaller, manageable sub-tasks
- Manage a dynamic task list with status tracking
- Execute tasks iteratively and report progress
- Track task status transitions (PENDING → IN_PROGRESS → COMPLETED)

## Architecture

This example uses the **AI Service + Tool** approach:
- **TaskTrackingAssistant**: AI Service interface that coordinates task management
- **TaskManagementTool**: Provides structured methods for creating, updating, and tracking tasks
- **Domain Models**: Task, TaskStatus, and TaskList for state management

```
User Request → TaskTrackingAssistant (AI Service)
                      ↓
              TaskManagementTool
                      ↓
                  TaskList
                      ↓
              [Task 1: PENDING]
              [Task 2: IN_PROGRESS]
              [Task 3: COMPLETED]
```

## Key Components

### Domain Models

- **TaskStatus**: Enum representing task states (PENDING, IN_PROGRESS, COMPLETED)
- **Task**: Individual task with ID, description, status, and optional result
- **TaskList**: Container managing multiple tasks with progress tracking

### TaskManagementTool

Provides @Tool annotated methods for the AI to manage tasks:
- `createTaskList(description)` - Initialize a new task list
- `addTask(description)` - Add a task to the list
- `updateTaskStatus(id, status)` - Update task status
- `completeTask(id, result)` - Mark task complete with result
- `getTaskList()` - Retrieve current task list
- `getProgress()` - Get progress summary
- `getNextPendingTask()` - Get next task to work on

### TaskTrackingAssistant

AI Service interface with methods:
- `breakdownTask(request)` - Break down complex request into sub-tasks
- `executeNextTask()` - Execute the next pending task
- `getProgress()` - Get current progress summary
- `executeAllTasks()` - Execute all remaining tasks

## Example Usage

### Basic Task Breakdown

```java
TaskManagementTool taskTool = TaskManagementTool.getInstance();

TaskTrackingAssistant assistant = AiServices.builder(TaskTrackingAssistant.class)
        .chatModel(chatModel)
        .chatMemory(chatMemory)
        .tools(taskTool)
        .build();

// Break down a complex request
String result = assistant.breakdownTask(
    "Create a simple calculator application with basic arithmetic operations"
);
```

**Output:**
```
Task List: Create a simple calculator application with basic arithmetic operations
==================================================
[ ] Task #1: Design the user interface
[ ] Task #2: Implement addition function
[ ] Task #3: Implement subtraction function
[ ] Task #4: Implement multiplication function
[ ] Task #5: Implement division function
[ ] Task #6: Write unit tests
[ ] Task #7: Create documentation
==================================================
Progress: 0/7 completed, 0 in progress, 7 pending
```

### Execute Tasks

```java
// Execute the first task
String execution = assistant.executeNextTask();
```

**Output:**
```
Task List: Create a simple calculator application with basic arithmetic operations
==================================================
[✓] Task #1: Design the user interface
  Result: UI mockup created with buttons for operations
[ ] Task #2: Implement addition function
[ ] Task #3: Implement subtraction function
...
==================================================
Progress: 1/7 completed, 0 in progress, 6 pending
```

### Track Progress

```java
String progress = assistant.getProgress();
```

**Output:**
```
Progress Summary
==================================================
Progress: 3/7 completed, 1 in progress, 3 pending

Completed Tasks:
  ✓ Design the user interface - UI mockup created
  ✓ Implement addition function - Addition working correctly
  ✓ Implement subtraction function - Subtraction implemented

In Progress:
  → Implement multiplication function

Pending Tasks:
  [ ] Implement division function
  [ ] Write unit tests
  [ ] Create documentation
```

## Running the Tests

The example includes comprehensive tests demonstrating various scenarios:

```bash
# Set your OpenAI API key
export OPENAI_API_KEY=your-api-key-here

# Run all tests
mvn test

# Run a specific test
mvn test -Dtest=TaskTrackingTest#testCompleteWorkflow
```

### Test Scenarios

1. **testTaskBreakdown**: Demonstrates basic task breakdown
2. **testExecuteSingleTask**: Shows status transitions for one task
3. **testProgressTracking**: Demonstrates progress monitoring
4. **testCompleteWorkflow**: Full workflow from breakdown to completion
5. **testWebApplicationBreakdown**: Different domain example
6. **testDirectToolUsage**: Direct tool API usage without AI

## Key Features

✅ **Dynamic Task Creation**: AI breaks down requests into appropriate sub-tasks  
✅ **Status Tracking**: Clear status transitions (PENDING → IN_PROGRESS → COMPLETED)  
✅ **Progress Monitoring**: Real-time progress summaries  
✅ **Iterative Execution**: Execute tasks one by one or all at once  
✅ **Flexible Architecture**: Easy to extend with new features  
✅ **Clear Separation**: AI handles reasoning, Tool handles state management

## Design Decisions

### Why AI Service + Tool?

- **Balance**: Simpler than Custom Planner, more flexible than Supervisor
- **Consistency**: Follows patterns from `simple-tool` example
- **Clarity**: Clear separation between AI reasoning and state management
- **Extensibility**: Easy to add new task management features

### Why Not Custom Planner?

- More complex to implement and maintain
- Overkill for this use case
- Requires deeper understanding of Planner interface

### Why Not Supervisor Planner?

- Less control over task status updates
- Task management would be implicit
- Harder to track individual task states

## Future Enhancements

Potential additions:
- **Task Dependencies**: Tasks that must complete before others
- **Task Priority**: High/Medium/Low priority levels
- **Task Assignment**: Assign tasks to different agents
- **Task Estimation**: Time estimates for each task
- **Parallel Execution**: Execute independent tasks simultaneously
- **Persistence**: Save/load task lists from storage
- **Task Validation**: Validate completion criteria

## Comparison with Other Examples

| Example | Approach | Use Case |
|---------|----------|----------|
| **simple-tool** | AI Service + Tool | Single domain-specific tool |
| **simple-agent** | Supervisor + Agents | Multi-agent coordination |
| **task-tracking** | AI Service + Task Tool | Dynamic task management |

## Learn More

- See [PLAN.md](PLAN.md) for detailed architecture and design decisions
- Check the test cases for more usage examples
- Explore the source code for implementation details

## Dependencies

- langchain4j: 1.9.1
- langchain4j-open-ai: 1.9.1
- JUnit Jupiter: 5.10.0
- AssertJ: 3.24.2