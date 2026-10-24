# Demo Script

1. **Setup**
   - Start Postgres via docker-compose or local
   - Start backend: `cd backend && mvn spring-boot:run`
   - Start frontend: `cd frontend && npm run dev`

2. **Customer flow**
   - Go to http://localhost:5173/register, register as CUSTOMER
   - Login as customer
   - Create a ticket (e.g. "Cannot login to account")
   - View ticket details, check history (initial entry)

3. **Staff flow**
   - Register/login as STAFF (different account)
   - Go to Tickets, view all tickets
   - Open the customer's ticket
   - (Optional) Add knowledge base articles via DB if needed for demo
   - Click "Generate AI Suggestion" - see category/priority/explanation, KB excerpts
   - Review: Accept / Edit response / Reject with feedback
   - Update ticket status (e.g., IN_PROGRESS) and assign (enter assignee user ID)
   - View updated history

4. **Dashboard**
   - As staff, go to Dashboard - see counts by status/priority/category and recent

5. **Tests**
   - `cd backend && mvn test` - show passing tests for CRUD, authz, status transitions, KB retrieval, AI failures
