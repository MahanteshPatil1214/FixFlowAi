# Setup Instructions

## Quick Start (local dev)

### 1) Database
Option A: Docker
```bash
cd docker
docker-compose up -d
```
Option B: Local PostgreSQL - create DB/user `fixflow/fixflow`.

### 2) Backend
```bash
cd backend
# set env vars (or edit application.yml)
export GROQ_API_KEY=your_key_here  # Windows: set GROQ_API_KEY=...
mvn clean install
mvn spring-boot:run
```
Backend runs on http://localhost:8080

### 3) Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on http://localhost:5173

## Environment Variables
- `SPRING_DATASOURCE_URL` (default jdbc:postgresql://localhost:5432/fixflow)
- `SPRING_DATASOURCE_USERNAME` (fixflow)
- `SPRING_DATASOURCE_PASSWORD` (fixflow)
- `GROQ_API_KEY` (required for real AI; leave empty for graceful fallback)
- `GROQ_MODEL` (default openai/gpt-oss-20b)
- `SERVER_PORT` (8080)

## Notes
- Keep Groq API key only on backend
- Tests run with H2, no external services needed
- If Groq unavailable, ticket CRUD still works; AI returns fallback
