# Known Limitations

- **Auth**: HTTP Basic auth only (no JWT, no sessions). Acceptable for portfolio/demo.
- **Search**: Keyword-based KB retrieval, not semantic/vector search.
- **AI**: One suggestion per ticket; no automatic ticket resolution/assignment/permission changes.
- **Validation**: Basic JSON/schema validation on AI responses; still best-effort.
- **Attachments**: No file uploads.
- **Email**: No notifications.
- **Rate limits**: Handled with fallbacks but no retry/backoff queue.
- **Models**: Uses currently available Groq models; deprecations may require changing `groq.model`.
- **Tests**: Frontend has no automated tests (focused on backend per requirements emphasis).
- **CORS/Auth in dev**: Configured for localhost:5173 with HTTP Basic.
