# FixFlow AI API Documentation

Base URL: `http://localhost:8080`

All endpoints under `/api` require HTTP Basic auth except `/api/auth/register` and `/api/auth/me` behavior as noted.

## Auth
### POST /api/auth/register
Register new user (role must be CUSTOMER or STAFF).
```json
{
  "email": "user@example.com",
  "password": "pass",
  "fullName": "User Name",
  "role": "CUSTOMER"
}
```

### GET /api/auth/me
Get current authenticated user.

## Tickets
### POST /api/tickets
Create ticket (any authenticated user).
```json
{
  "title": "Cannot login",
  "description": "Getting error...",
  "category": "ACCOUNT_ACCESS",
  "priority": "HIGH"
}
```

### GET /api/tickets
List tickets. Customers see only their own; staff see all. Query params: `q`, `status`, `category`, `priority`.

### GET /api/tickets/{id}
Get ticket details. Customers can only access own tickets.

### PUT /api/tickets/{id}
Update ticket. Staff can update all fields including status and assignee; customers cannot change status/assignee.
```json
{
  "status": "IN_PROGRESS",
  "assigneeId": 1
}
```

### GET /api/tickets/{id}/history
Get status change history.

## Knowledge Base
### GET /api/kb
List approved articles (all authenticated).

### GET /api/kb/search?q=...
Keyword search (STAFF/ADMIN only).

## AI
### POST /api/ai/tickets/{id}/suggest
Generate AI suggestion (STAFF/ADMIN). Retrieves KB excerpts, calls Groq, validates output.
Returns suggestion with `matchingArticleTitles` and `matchingArticleExcerpts`.

### GET /api/ai/tickets/{id}/suggest
Get suggestion for ticket (all authenticated if authorized to view ticket).

### POST /api/ai/tickets/{id}/review
Review suggestion (STAFF/ADMIN).
```json
{
  "action": "ACCEPT|REJECT|EDIT",
  "feedback": "Looks good",
  "editedResponse": "..."
}
```

## Dashboard
### GET /api/dashboard
Get stats (STAFF/ADMIN). Returns totals by status/priority/category and recent count.

## Error Responses
Consistent format:
```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "/api/..."
}
```
