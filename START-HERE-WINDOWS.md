# Start Here (Windows + VS Code)

This is a complete standalone full-stack project. Keep `backend` and `frontend` as separate folders inside this project root.

## Recommended: run with Docker
1. Install Docker Desktop and start it.
2. In VS Code, open this project root (the folder containing `backend`, `frontend`, and `docker-compose.yml`).
3. Open Terminal and run:

   ```powershell
   docker compose up --build
   ```

4. Open `http://localhost:5173`.
5. API documentation: `http://localhost:4040/swagger-ui.html`.

## Run without Docker
You need Java 17+, Maven, Node.js 20+, and MySQL 8+.

1. In MySQL, create the database and local user (or change credentials in `backend/src/main/resources/application.properties`):

   ```sql
   CREATE DATABASE job_portal;
   CREATE USER 'jobportal'@'localhost' IDENTIFIED BY 'jobportal';
   GRANT ALL PRIVILEGES ON job_portal.* TO 'jobportal'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. Start the backend in terminal 1:

   ```powershell
   cd backend
   mvn spring-boot:run
   ```

3. Start the frontend in terminal 2 (from the project root):

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

4. Visit `http://localhost:5173`.

## Demo accounts
- Candidate: `candidate@jobportal.local` / `Candidate@123`
- Recruiter: `recruiter@jobportal.local` / `Recruiter@123`

The backend seeds demo accounts and example jobs on an empty database. Sign in as a candidate, open **My Profile**, add your skills, education, location and experience, save, then open **Jobs** or **Recommended** to view matching jobs. A recruiter account can create job listings.

## If you already have an older project folder
Do not copy files randomly over your existing project. Extract this ZIP to a new folder first and run it independently. Your screenshot's `CandidateProfileService.java` may belong to a different version/package structure; merging only some files can cause package and endpoint errors. Once this version runs, you can migrate your own changes deliberately.

## Notes
- The job recommendations use a local similarity engine and do not require an AI API key.
- The AI assistant has a graceful fallback; configure `GEMINI_API_KEY` in `.env` to enable Gemini responses when using Docker.
- Change demo passwords and secrets before any public deployment. This is a capstone-ready starter, not a security-audited commercial service.
