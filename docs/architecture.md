# FixFlow AI Architecture

## Layers
- **Controller**: REST endpoints, validation (`@Valid`), auth via Spring Security
- **Service**: Business rules, transactions, authorization checks
- **Repository**: Spring Data JPA interfaces
- **Model/Entity**: JPA entities with enums
- **DTO**: Request/response objects

## Key Components
- `TicketService`: CRUD, filtering, authorization, status history
- `AuthService`/`SecurityConfig`: Registration, HTTP Basic, role-based access
- `KnowledgeService`: Approved articles, simple keyword matching
- `GroqService`: Calls Groq API, validates JSON, handles timeouts/rate limits/errors, abstain logic
- `AiSuggestionService`: Orchestrates KB retrieval + Groq + storage + review
- `DashboardService`: Aggregates counts

## Security
- All `/api/**` except `/api/auth/**` and `/actuator/health` require auth
- Method security enabled with `@PreAuthorize`
- Customers restricted to their own tickets; staff/admin can access all
- API keys only on backend

## Data Model (core)
- User (id, email, passwordHash, fullName, role)
- Ticket (id, title, description, category, priority, status, creator, assignee)
- TicketStatusHistory (from/to, changedBy, changedAt)
- KnowledgeArticle (title, content, approved)
- AiSuggestion (ticket, suggestedCategory/Priority, explanation, response, KB matches, feedback, status, reviewedBy)

## AI Flow
1. Staff triggers suggestion for ticket
2. `KnowledgeService` finds relevant articles via keyword matching
3. `GroqService` builds prompt with ticket + KB excerpts, calls Groq
4. Response validated (JSON shape, allowed enums) - abstain if KB insufficient or invalid
5. `AiSuggestion` stored (separate from ticket) with KB titles/excerpts
6. Staff reviews (accept/edit/reject) with feedback recorded

## Trade-offs
- HTTP Basic simple vs JWT (easier to understand, acceptable for portfolio)
- Keyword matching vs embeddings (no vector DB as required, fast/simple)
- Storing full excerpts in AI suggestion for transparency
- One suggestion per ticket to keep simple
- Tests mock Groq to avoid credits
