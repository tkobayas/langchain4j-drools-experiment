# Simple MCP: Drools Loan Approval with Model Context Protocol

This project demonstrates exposing a Drools-based loan approval tool as an MCP (Model Context Protocol) server using Quarkus, and consuming it from a LangChain4j AI service client.

## Architecture

```
┌─────────────────────────────────┐
│   simple-mcp-client             │
│   (Quarkus LangChain4j)         │
│   - LoanAssistant AiService     │
│   - MCP Client Configuration    │
│   - OpenAI Integration          │
└────────────┬────────────────────┘
             │
             │ stdio transport
             │ (subprocess)
             ▼
┌─────────────────────────────────┐
│   simple-drools-mcp             │
│   (Quarkus MCP Server)          │
│   - SimpleDroolsTool            │
│   - Drools Rules Engine         │
│   - Loan Approval Rules         │
└─────────────────────────────────┘
```

## Components

### simple-drools-mcp (MCP Server)

An MCP server that exposes a Drools-based loan approval tool via stdio transport.

**Key Features:**
- Quarkus-based MCP server using `quarkus-mcp-server-stdio`
- Drools rules engine for business logic
- Tool method accepts primitive parameters (name, age, amount)
- Returns string "true" or "false" for approval status
- Logging to file (not stdout) to avoid stdio interference

**Business Rule:**
- Approves loans up to $5000 for applicants 18 years or older
- Rejects all other applications

### simple-mcp-client (MCP Client)

A Quarkus LangChain4j client that consumes the MCP server and provides an AI-powered loan assistant.

**Key Features:**
- Quarkus LangChain4j with MCP integration
- OpenAI GPT-4o-mini for natural language processing
- Automatic MCP tool discovery and invocation
- AI Service interface for loan evaluation

## Prerequisites

- Java 17+
- Maven 3.8+
- OpenAI API Key (for running the client)

## Building

### 1. Build MCP Server

```bash
cd simple-drools-mcp
mvn clean package
```

**Output**: `target/quarkus-app/quarkus-run.jar`

### 2. Build MCP Client

```bash
cd simple-mcp-client
mvn clean package -DskipTests
```

**Output**: `target/quarkus-app/quarkus-run.jar`

## Testing

### Server Tests Only

```bash
cd simple-drools-mcp
mvn test
```

All 5 tests should pass:
- Approve: Adult with small amount ($3000)
- Approve: Edge case (18 years old, $5000)
- Reject: Too young (16 years old)
- Reject: Amount too large ($8000)
- Reject: Both conditions fail

### Client Tests (Requires OpenAI API Key)

```bash
cd simple-mcp-client
export OPENAI_API_KEY=your-api-key-here
mvn test
```

**Test Scenarios:**
1. Approved: John, 45 years old, $3000
2. Approved: Alice, 18 years old, $5000
3. Rejected: Jane, 16 years old, $2000
4. Rejected: Bob, 30 years old, $8000
5. Rejected: Charlie, 15 years old, $10000

## Running

### Manual Testing

The client can be tested manually, but requires the server JAR to be built first.

**1. Set Environment Variable:**
```bash
export OPENAI_API_KEY=your-api-key-here
```

**2. Run Client Tests:**
```bash
cd simple-mcp-client
mvn test
```

The client will:
1. Automatically spawn the MCP server as a subprocess
2. Connect via stdio transport
3. Discover the `approve` tool
4. Use OpenAI to interpret user requests
5. Call the Drools tool with extracted parameters
6. Return AI-generated responses

## Configuration

### simple-drools-mcp (Server)

**Location**: `src/main/resources/application.properties`

```properties
quarkus.application.name=simple-drools-mcp

# Logging to file (not stdout)
quarkus.log.console.enable=false
quarkus.log.file.enable=true
quarkus.log.file.path=simple-drools-mcp.log
quarkus.log.level=INFO
```

### simple-mcp-client (Client)

**Location**: `src/main/resources/application.properties`

```properties
# OpenAI Configuration
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}
quarkus.langchain4j.openai.chat-model.model-name=gpt-4o-mini
quarkus.langchain4j.openai.chat-model.temperature=0.0

# MCP Client Configuration
quarkus.langchain4j.mcp.drools-mcp.transport-type=stdio
quarkus.langchain4j.mcp.drools-mcp.command=java,-jar,../simple-drools-mcp/target/quarkus-app/quarkus-run.jar
quarkus.langchain4j.mcp.drools-mcp.tool-execution-timeout=30s
```

## Key Implementation Details

### Tool Signature Considerations

The MCP server tool method accepts **primitive parameters** rather than complex objects:

```java
@Tool(description = "Evaluates a loan application...")
public String approve(
    @ToolArg(description = "Applicant's name") String applicantName,
    @ToolArg(description = "Applicant's age in years") int applicantAge,
    @ToolArg(description = "Loan amount requested in dollars") int amount)
```

**Reasons:**
1. Quarkus MCP server requires simple, JSON-serializable types
2. Complex objects (like `LoanApplication`) cause build errors
3. Return type must be `String` (boolean not supported)

### MCP Tool Discovery

The client **automatically discovers** MCP tools without explicit annotation:

```java
@RegisterAiService
public interface LoanAssistant {
    @SystemMessage("You are a loan approval assistant...")
    @UserMessage("Evaluate a loan application...")
    String approveLoan(@V("request") String request);
}
```

No `@McpToolBox` annotation is needed - the MCP configuration in `application.properties` makes tools available to all AI services.

### stdio Transport

The client spawns the server as a subprocess:
- Server stdout is used for MCP protocol messages
- Server logging goes to file to avoid interference
- Client manages server lifecycle automatically

## Troubleshooting

### Build Issues

**Problem**: `NoSuchMethodError: org.jboss.jandex.IndexView.getAllKnownImplementations`

**Solution**: Use Quarkus 3.20.1 or later (compatible with quarkus-mcp-server 1.8.0)

**Problem**: `Unsupported return type: boolean`

**Solution**: Use `String` return type instead of `boolean`

**Problem**: `Unsupported parameter type: LoanApplication`

**Solution**: Use primitive parameters (String, int) instead of complex objects

### Runtime Issues

**Problem**: `OPENAI_API_KEY` environment variable not set

**Solution**: Export the environment variable before running tests:
```bash
export OPENAI_API_KEY=your-key-here
```

**Problem**: Server JAR not found

**Solution**: Ensure server is built before running client:
```bash
cd simple-drools-mcp && mvn clean package
```

**Problem**: stdio communication errors

**Solution**: Check server log file (`simple-drools-mcp.log`) for errors. Ensure console logging is disabled on the server.

## Example Usage

**User Request:**
```
"Evaluate a loan application for John who is 45 years old requesting a loan of $3000."
```

**AI Processing:**
1. OpenAI parses the natural language request
2. Extracts: name="John", age=45, amount=3000
3. Calls MCP tool: `approve("John", 45, 3000)`
4. Receives: `"true"`
5. Generates natural language response

**AI Response:**
```
"The loan application for John has been approved. John is 45 years old and is
requesting $3000, which meets the approval criteria (applicant must be 18 or
older and the loan amount must not exceed $5000)."
```

## Technology Stack

- **Quarkus 3.20.1**: Modern Java framework
- **Quarkus MCP Server 1.8.0**: MCP server implementation
- **Quarkus LangChain4j 1.5.0**: LangChain4j integration with MCP support
- **Drools 10.1.0**: Business rules engine
- **LangChain4j 1.9.1**: AI orchestration framework
- **OpenAI GPT-4o-mini**: Language model

## References

- [Quarkus MCP Server Documentation](https://docs.quarkiverse.io/quarkus-mcp-server/dev/index.html)
- [Quarkus LangChain4j MCP Integration](https://docs.quarkiverse.io/quarkus-langchain4j/dev/mcp.html)
- [Using MCP with Quarkus+LangChain4j Blog](https://quarkus.io/blog/quarkus-langchain4j-mcp/)
- [Model Context Protocol Specification](https://modelcontextprotocol.io/)

## License

This is an example project for demonstration purposes.
