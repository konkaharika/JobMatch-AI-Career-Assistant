# 💼 AI-Powered Job Portal

**Windows/VS Code quick guide:** see [`START-HERE-WINDOWS.md`](START-HERE-WINDOWS.md).

A full-stack capstone project built with **React + Vite + Spring Boot + MySQL**. Candidates create profiles and receive ranked job recommendations based on their skills, education and experience. Recruiters can publish jobs. An AI assistant can answer questions using retrieved job context.

## Included syllabus coverage
- React, Vite, JSX, components, props/state, hooks/useEffect
- Forms + client/server validation
- Axios + REST APIs + HTTP status codes
- Spring Boot MVC, Controller → Service → Repository, dependency injection
- CRUD for jobs, profile management and application flow
- MySQL, JPA/Hibernate, entities and relationships
- Pagination and sorting
- Global exception handling
- JWT authentication, BCrypt password hashing, authorization and RBAC
- Protected frontend routes
- Embedding generation + cosine similarity vector search for job matching
- RAG-style AI assistant: retrieves current jobs, then sends retrieved context to the LLM
- Caching annotations, asynchronous embedding refresh, logging/Actuator metrics
- JUnit + Mockito tests
- Docker + Docker Compose + environment variables
- Swagger/OpenAPI
- Production-oriented Nginx frontend container

## Architecture
```
React/Vite
   │ Axios + JWT
   ▼
Spring Boot REST API
   ├── Controllers
   ├── Services
   │    ├── Authentication
   │    ├── Job matching / embeddings
   │    └── AI/RAG assistant
   ├── Repositories
   └── Spring Security
        │
        ▼
      MySQL
```

## 1. Fastest run: Docker

1. Copy `.env.example` to `.env`.
2. Change `JWT_SECRET` to a long random value.
3. Optional: add `GEMINI_API_KEY` for the real LLM assistant.
4. Run:

```bash
docker compose up --build
```

Open `http://localhost:5173`.

Swagger: `http://localhost:4040/swagger-ui.html`
Health: `http://localhost:4040/actuator/health`

## 2. Run locally without Docker

### MySQL
Create a database and user:

```sql
CREATE DATABASE job_portal;
CREATE USER 'jobportal'@'localhost' IDENTIFIED BY 'jobportal';
GRANT ALL PRIVILEGES ON job_portal.* TO 'jobportal'@'localhost';
FLUSH PRIVILEGES;
```

### Backend
Requirements: Java 17+, Maven 3.9+

```bash
cd backend
mvn spring-boot:run
```

Backend runs on port `4040` by default.

### Frontend
Requirements: Node.js 20+

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

## Demo accounts
The backend seeds these accounts on the first empty database:

- Candidate: `candidate@jobportal.local` / `Candidate@123`
- Recruiter: `recruiter@jobportal.local` / `Recruiter@123`

Do not use these credentials in a real deployment.

## How job matching works
1. Candidate updates skills, education and experience.
2. The backend creates a normalized 64-dimensional local embedding representation.
3. Each active job is represented from title, description and required skills.
4. Cosine similarity produces a match score from 0–100%.
5. Jobs are sorted by match score and returned through `/api/jobs/recommended`.

### Important production note
The included local embedding implementation is deterministic and dependency-free so the capstone runs without a paid AI account. For a real commercial deployment, replace `EmbeddingService` with a managed embedding model and persist vectors in a proper vector database/vector index (for example, a MySQL vector-capable setup, pgvector, or a dedicated vector store). The service boundary is already isolated for that upgrade.

## AI/RAG assistant
Set `GEMINI_API_KEY` in `.env`. The backend retrieves recent active jobs and builds a context prompt before calling Gemini. Without an API key, the assistant safely falls back to showing the retrieved job context.

This demonstrates:
- prompt engineering
- retrieval
- context injection (RAG)
- LLM integration
- graceful fallback

## API overview

### Public
- `POST /auth/register`
- `POST /auth/login`
- `GET /swagger-ui.html`

### Candidate / authenticated
- `GET /api/profile`
- `PUT /api/profile`
- `GET /api/jobs`
- `GET /api/jobs/recommended`
- `POST /api/jobs/{id}/apply`
- `GET /api/jobs/applications`
- `POST /api/ai/chat`

### Recruiter
- `POST /api/recruiter/jobs`
- `PUT /api/recruiter/jobs/{id}`
- `DELETE /api/recruiter/jobs/{id}`

## HTTP status design
- `200` successful read/update
- `201` can be introduced for create endpoints when expanding API contracts
- `400` validation/business input errors
- `401` missing/invalid authentication
- `403` authenticated user lacks role/permission
- `409` duplicate registration/application conflicts
- `500` unexpected server error

## Security notes
- Passwords use BCrypt with strength 12.
- JWT is stateless.
- Secrets are environment variables.
- Frontend never stores the password.
- Do not commit `.env` or production secrets.
- For production, put the API behind HTTPS and use a secure secret manager.
- Consider HttpOnly secure refresh tokens and short-lived access tokens for a higher-security deployment.

## Testing
```bash
cd backend
mvn test
```

The included tests cover the embedding/matching primitive and duplicate-email authentication behavior. Add controller integration tests with Testcontainers/MySQL before a real production release.

## Deployment path
A practical deployment is:

`Browser → HTTPS reverse proxy/CDN → Nginx React container + Spring Boot container → managed MySQL`

Use environment variables for DB credentials, JWT secret and AI credentials. Enable HTTPS, backups, database migrations (Flyway/Liquibase), centralized logs and monitoring before public release.

## Capstone explanation
The project demonstrates a complete recruitment lifecycle: registration → profile creation → AI-assisted matching → job discovery → application → recruiter job posting → AI career assistance. The frontend follows component-based React design and the backend follows MVC/service/repository separation.
