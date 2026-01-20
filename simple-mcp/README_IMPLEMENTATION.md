# MCP Implementation - LangChain4j Drools Integration

## Overview

This project demonstrates the integration of Drools rule engine with LangChain4j using the Model Context Protocol (MCP). The implementation consists of two modules:

1. **simple-drools-mcp**: MCP server that exposes Drools-based loan approval tool
2. **simple-mcp-client**: MCP client that consumes the server via LangChain4j AI Service

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    simple-mcp-client                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         LoanAssistant (AI Service)                   │  │
│  │  @RegisterAiService                                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                          │                                  │
│                          │ LangChain4j                      │
│                          ▼                                  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │      Quarkus LangChain4j MCP Client                  │  │
│  │  (quarkus-langchain4j-mcp)                           │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                          │
                          │ stdio transport
                          │ (subprocess invocation)
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    simple-drools-mcp                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │      Quarkus MCP Server (stdio)                      │  │
│  │  (quarkus-mcp-server-stdio)                          │  │
│  └──────────────────────────────────────────────────────┘  │
│                          │                                  │
│                          ▼                                  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         SimpleDroolsTool                             │  │
│  │  @Tool @ApplicationScoped                            │  │
│  │  - approve(LoanApplication): String                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                          │                                  │
│                          ▼                                  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Drools Rule Engine                           │  │
│  │  loan-application.drl                                │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Key Technologies

- **Quarkus 3.27.0**: Application framework
- **Quarkus MCP Server 1.7.0**: MCP server implementation with stdio transport
- **Quarkus LangChain4j 0.23.0**: LangChain4j integration for Quarkus
- **Drools 9.44.0.Final**: Rule engine for business logic
- **LangChain4j 1.0.0-alpha1**: AI orchestration framework
- **OpenAI GPT-4o-mini**: Language model for natural language processing

## Module Details

### simple-drools-mcp (MCP Server)

**Purpose**: Exposes Drools-based loan approval functionality as an MCP tool.

**Key Components**:
- `SimpleDroolsTool.java`: MCP tool implementation with `@Tool` annotation
- `loan-application.drl`: Drools rules for loan approval logic
- `application.properties`: Quarkus configuration with file-based logging
- `DroolsUtils.java`: Utility for Drools session management

**Tool Signature**:
```java
@Tool(description = "Evaluates a loan application using business rules...")
public String approve(@ToolArg(description = "...") LoanApplication loanApplication)
```

**Return Format**: Descriptive strings like:
- `"APPROVED: Loan application for John (age 45) requesting $3000 has been approved."`
- `"REJECTED: Loan application for Jane (age 16) requesting $5000 has been rejected because applicant is under 18 years old."`

**Build Output**: `target/quarkus-app/quarkus-run.jar` (MCP server executable)

### simple-mcp-client (MCP Client)

**Purpose**: Consumes the MCP server via LangChain4j AI Service for natural language loan evaluation.

**Key Components**:
- `LoanAssistant.java`: AI Service interface with `@RegisterAiService`
- `application.properties`: OpenAI and MCP client configuration
- `DroolsToolTest.java`: Integration tests
- `Main.java`: Optional entry point for manual testing

**AI Service Interface**:
```java
@RegisterAiService
public interface LoanAssistant {
    @UserMessage("Evaluate a loan application... '{{request}}'.")
    String approveLoan(@V("request") String request);
}
```

**MCP Configuration** (application.properties):
```properties
quarkus.langchain4j.mcp.drools-mcp.transport-type=stdio
quarkus.langchain4j.mcp.drools-mcp.command=java,-jar,../simple-drools-mcp/target/quarkus-app/quarkus-run.jar
```

## Build Instructions

### Prerequisites
- Java 17 or later
- Maven 3.8+
- OpenAI API key (for running tests)

### Step 1: Install Parent POM
```bash
mvn clean install -N
```

### Step 2: Build MCP Server
```bash
cd simple-drools-mcp
mvn clean install
# or use the build script:
./build.sh
```

This creates:
- `target/quarkus-app/quarkus-run.jar` - MCP server executable
- Installs artifact to local Maven repository

### Step 3: Build MCP Client
```bash
cd simple-mcp-client
mvn clean package
```

## Running Tests

### MCP Server Tests (Unit Tests)
```bash
cd simple-drools-mcp
mvn test
```

Tests verify:
- Approved loan scenarios (age ≥ 18, amount ≤ $10,000)
- Rejected scenarios (age < 18, amount > $10,000)

### MCP Client Tests (Integration Tests)
```bash
cd simple-mcp-client
export OPENAI_API_KEY=your-api-key-here
mvn test
```

Tests verify:
- Natural language loan requests are processed correctly
- MCP server is invoked via stdio transport
- AI extracts applicant information and uses the tool
- Proper approval/rejection responses

## Manual Testing

### Option 1: Using Main.java
```bash
cd simple-mcp-client
export OPENAI_API_KEY=your-api-key-here
mvn quarkus:dev
```

Then interact with the application (if Main.java is configured for interactive mode).

### Option 2: Direct MCP Server Testing
```bash
cd simple-drools-mcp
java -jar target/quarkus-app/quarkus-run.jar
```

The server will start and listen on stdio for MCP protocol messages.

## Configuration

### MCP Server (simple-drools-mcp/src/main/resources/application.properties)
```properties
# Disable console logging (stdio reserved for MCP)
quarkus.log.console.enable=false

# Enable file logging
quarkus.log.file.enable=true
quarkus.log.file.path=simple-drools-mcp.log
quarkus.log.file.level=INFO
```

### MCP Client (simple-mcp-client/src/main/resources/application.properties)
```properties
# OpenAI Configuration
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}
quarkus.langchain4j.openai.chat-model.model-name=gpt-4o-mini
quarkus.langchain4j.openai.timeout=60s

# MCP Client Configuration
quarkus.langchain4j.mcp.drools-mcp.transport-type=stdio
quarkus.langchain4j.mcp.drools-mcp.command=java,-jar,../simple-drools-mcp/target/quarkus-app/quarkus-run.jar
```

## Business Rules

The Drools rules in `loan-application.drl` implement the following logic:

1. **Age Requirement**: Applicant must be 18 or older
2. **Amount Limit**: Loan amount must not exceed $10,000
3. **Approval**: If both conditions are met, loan is approved
4. **Rejection**: If either condition fails, loan is rejected with specific reason

## Key Implementation Details

### Logging Configuration
- MCP server uses file-based logging (stdio reserved for MCP protocol)
- Log file: `simple-drools-mcp.log`
- Client can use console logging normally

### Domain Objects
- Shared between server and client via Maven dependency
- Use Java Records for immutability
- Accessor methods: `.name()`, `.age()` (not `.getName()`, `.getAge()`)

### Dependency Management
- Parent POM must be installed first
- Server module must be installed before building client
- Quarkus version: 3.27.0 (compatible with MCP server 1.7.0)

## Troubleshooting

### Build Issues

**Problem**: `Could not find artifact simple-mcp-parent:pom`
**Solution**: Run `mvn clean install -N` in the root directory

**Problem**: `Could not find artifact simple-drools-mcp:jar`
**Solution**: Run `cd simple-drools-mcp && mvn clean install`

**Problem**: Jandex API incompatibility
**Solution**: Ensure Quarkus 3.27.0 and quarkus-mcp-server 1.7.0 versions

### Runtime Issues

**Problem**: MCP server not responding
**Solution**: Check `simple-drools-mcp.log` for errors, verify JAR path in client config

**Problem**: OpenAI API errors
**Solution**: Verify `OPENAI_API_KEY` environment variable is set correctly

**Problem**: Tool not found by AI
**Solution**: Verify MCP server is built and path in `application.properties` is correct

## Example Usage

### Natural Language Request
```
"I want to apply for a loan. My name is John, I'm 45 years old, and I need $3000."
```

### AI Processing Flow
1. AI extracts: name="John", age=45, amount=3000
2. AI calls MCP tool: `approve(LoanApplication(name="John", age=45, amount=3000))`
3. MCP server invokes Drools rules
4. Server returns: `"APPROVED: Loan application for John (age 45) requesting $3000 has been approved."`
5. AI formats response for user

### Expected Response
```
"Your loan application has been approved! John, your request for $3000 has been processed successfully."
```

## Project Structure

```
simple-mcp/
├── pom.xml                          # Parent POM
├── README_IMPLEMENTATION.md         # This file
├── IMPLEMENTATION_PLAN_CLAUDE.md    # Original implementation plan
├── simple-drools-mcp/               # MCP Server module
│   ├── pom.xml
│   ├── build.sh
│   └── src/
│       ├── main/
│       │   ├── java/.../
│       │   │   ├── SimpleDroolsTool.java
│       │   │   ├── DroolsUtils.java
│       │   │   ├── LoanAssistant.java (legacy)
│       │   │   └── domain/
│       │   │       ├── LoanApplication.java
│       │   │       └── Person.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── org/example/loan-application.drl
│       └── test/
│           └── java/.../DroolsToolTest.java
└── simple-mcp-client/               # MCP Client module
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/.../
        │   │   ├── LoanAssistant.java
        │   │   └── Main.java
        │   └── resources/
        │       └── application.properties
        └── test/
            └── java/.../DroolsToolTest.java
```

## Next Steps

1. **Run Integration Tests**: Set `OPENAI_API_KEY` and run `mvn test` in simple-mcp-client
2. **Extend Rules**: Add more complex business rules in `loan-application.drl`
3. **Add More Tools**: Create additional MCP tools in the server module
4. **Enhance AI Service**: Add more methods to `LoanAssistant` interface
5. **Production Deployment**: Configure for production use with proper error handling

## References

- [Quarkus MCP Server Documentation](https://docs.quarkiverse.io/quarkus-mcp-server/dev/index.html)
- [Quarkus LangChain4j Documentation](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)
- [Model Context Protocol Specification](https://modelcontextprotocol.io/)
- [Drools Documentation](https://docs.drools.org/)

## License

This project is provided as-is for demonstration purposes.

---
**Implementation Date**: January 2026  
**Quarkus Version**: 3.27.0  
**MCP Server Version**: 1.7.0  
**LangChain4j Version**: 0.23.0