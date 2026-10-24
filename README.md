# FixFlow AI

FixFlow AI is a beginner-understandable AI-powered support ticket management application built for portfolio use. It helps small support teams manage tickets, use AI to classify issues and draft evidence-grounded replies using an approved knowledge base, with human review and feedback.

## Tech Stack
- Frontend: React, Vite, JavaScript
- Backend: Java 17, Spring Boot 3.3, Maven
- Database: PostgreSQL (with H2 for tests)
- ORM: Spring Data JPA
- AI: Groq API (OpenAI-compatible chat completions)
- Security: Spring Security (HTTP Basic)
- Testing: JUnit 5, Mockito

## Features
- Ticket CRUD (create/view/search/filter/update)
- Categories: Billing, Technical Issue, Account Access, Other
- Status: Open, In Progress, Waiting for Customer, Resolved
- Assignment to staff
- Status change history with timestamps and responsible user
- Approved knowledge base with keyword retrieval
- Groq AI suggestions (category, priority, explanation, response) with KB excerpts
- Abstain when KB doesn't support answer
- Human review (accept/edit/reject) with feedback
- Dashboard with counts by status/priority/category and recent activity
- AuthN/Z enforced; customers can only access their own tickets

## Setup

### Prerequisites
- Java 17
- Maven
- Node.js 18+
- PostgreSQL (or use Docker)

### Database (optional Docker)
```bash
cd docker
docker-compose up -d
```

### Backend
```bash
cd backend
cp .env.example .env  # create if needed
mvn spring-boot:run
```
Configure via env vars or `application.yml`:
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `GROQ_API_KEY` (keep on backend, never commit)
- `GROQ_MODEL` (e.g. `openai/gpt-oss-20b`)

### Frontend
```bash
cd frontend
npm install
npm run dev
```
App runs on http://localhost:5173, API on http://localhost:8080

## Testing
```bash
cd backend
mvn test
```
Tests use H2 in-memory and mocked AI where appropriate. Groq calls go through `GroqService` and are mocked in unit tests.

## API Documentation (key endpoints)
- POST `/api/auth/register` - register (role CUSTOMER or STAFF)
- GET `/api/auth/me` - current user
- POST `/api/tickets` - create ticket (auth)
- GET `/api/tickets` - list (filters: q, status, category, priority)
- GET `/api/tickets/{id}` - get ticket
- PUT `/api/tickets/{id}` - update ticket (staff can change status/assign)
- GET `/api/tickets/{id}/history` - status history
- GET `/api/kb` - list approved articles
- GET `/api/kb/search?q=...` - keyword search (staff)
- POST `/api/ai/tickets/{id}/suggest` - generate AI suggestion (staff)
- GET `/api/ai/tickets/{id}/suggest` - get suggestion
- POST `/api/ai/tickets/{id}/review` - review suggestion (staff)
- GET `/api/dashboard` - stats (staff/admin)

## Architecture (high level)
- Controller → Service → Repository → Entity/DTO
- Security via Spring Security with role checks
- AI isolated in `GroqService` with validation and fallbacks
- Knowledge retrieval by simple keyword matching

## Known Limitations
- HTTP Basic auth (simple for portfolio; no JWT/session)
- Keyword-based KB search (no embeddings/vector DB as requested)
- AI suggestions stored per ticket (one suggestion record)
- No email notifications
- No file attachments

## Demo Script
1. Start Postgres (or let backend use defaults)
2. Start backend: `mvn spring-boot:run`
3. Start frontend: `npm run dev`
4. Register as CUSTOMER, create ticket
5. Register/login as STAFF, view tickets, generate AI suggestion (with/without KB), review (accept/edit/reject)
6. View dashboard and history
7. Run tests: `mvn test`
