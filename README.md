# Shodh-a-Code: Lightweight Real-time Coding Contest Platform

A full-stack, containerized coding contest platform with:
- **Backend**: Spring Boot (Java 17), JPA/Hibernate, PostgreSQL
- **Frontend**: Next.js + React + Tailwind CSS
- **Judge**: Dockerized runner executing user code (Python/Java/C++) with simple limits
- **DevOps**: Docker Compose to run DB, backend, frontend, and judge image

## Architecture Overview
- **Backend (Spring Boot)** exposes REST APIs, persists entities, and asynchronously offloads judging to a Dockerized runner via `ProcessBuilder` (`docker run shodh-judge`).
- **Judge (Python)** image compiles/runs user code with time/memory constraints (basic), diffing stdout against expected output.
- **Frontend (Next.js)** provides Join, Contest page (problems, editor, leaderboard), polling for submission status and leaderboard.
- **Database (PostgreSQL)** stores users, contests, problems, submissions. A sample contest with 2 problems is pre-seeded.

## Repository Structure
```
/backend              # Spring Boot app
/frontend             # Next.js app
/judge                # Judge runner image with Python/Java/C++ toolchain
/docker-compose.yml   # Orchestrates DB, backend, frontend, judge image
```

## Quickstart (Docker Compose)
Prerequisites:
- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Node/NPM not required unless you want to run frontend locally

Run
```bash
# from repo root
docker compose up --build
```
What this does
- Spins up Postgres, Backend, Frontend, and builds a Judge image used by the backend.
- Frontend is on http://localhost:3000
- Backend is only accessible inside the Docker network (frontend talks to it as http://backend:8080).

Seed data
- On first run, a sample contest is created with 2 problems and a few official tests.
- Try Contest ID: 1 and any username.

Stop/clean
```bash
# stop
Ctrl+C
# stop and remove containers/volumes if you want a fresh DB
docker compose down -v
```

Optional: run pieces locally
- Backend: JDK 17 + Maven → `cd backend && mvn spring-boot:run`
- Frontend: Node 20 → `cd frontend && npm install && npm run dev` (set `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080` if running backend locally)

---

## 2) API Design 
Base path (inside compose): `http://backend:8080`

- GET `/api/contests/{contestId}`
  - Returns contest basics and embedded problems
  - Response (trimmed):
    ```json
    {
      "id": 1,
      "name": "Sample Contest",
      "description": "Demo contest with 2 problems",
      "startTime": "2025-10-30T09:00:00Z",
      "endTime":   "2025-10-30T10:00:00Z",
      "problems": [{"id":1, "title":"Sum A+B", ...}]
    }
    ```

- POST `/api/submissions`
  - Body:
    ```json
    {"username":"alice","problemId":1,"contestId":1,"code":"print(sum(map(int,input().split())))","language":"python"}
    ```
  - Response:
    ```json
    {"id": 42}
    ```
  - Side‑effect: backend queues judging and updates the submission status asynchronously.

- GET `/api/submissions/{submissionId}`
  - Returns the submission including `status` and `result` text
  - Example:
    ```json
    {"id":42, "status":"Accepted", "result":"[Case 1]\nACCEPTED\n"}
    ```

- GET `/api/contests/{contestId}/leaderboard`
  - Returns an ordered list by solved count:
    ```json
    [{"username":"alice","solved":2},{"username":"bob","solved":1}]
    ```

- GET `/api/contests/{contestId}/solved?username={u}`
  - Returns an array of problem IDs solved by the user (for UI badges):
    ```json
    [1,2]
    ```

- GET `/api/problems/{problemId}/tests/count`
  - Returns the number of official (hidden) tests for that problem:
    ```json
    3
    ```

Notes
- Status lifecycle: `Pending → Running → Accepted | Wrong Answer | Time Limit Exceeded | Error`.
- Judging compares stdout against expected for each official test; stops on first failure.

---

## 3) Design Choices & Justification 

Backend
- Spring Boot + JPA for fast CRUD and clear layering (entities, repos, services, controllers).
- Asynchronous judge calls with `@Async` so the API returns quickly after enqueueing work.
- Judging is executed by shelling out to `docker run shodh-judge:latest`, which keeps the runner language‑agnostic and isolated by container limits (`--memory`, `--cpus`).
- Schema kept minimal: Contest, Problem, User, Submission, TestCase. We seed a single contest with problems and a few official tests.

Frontend
- Next.js + React + Tailwind for a quick UI with server rewrites so the browser hits `/api/*` and Next proxies to the backend service.
- State is kept simple with `useSWR` for data fetching + polling. No heavy state library was necessary for this prototype.
- Monaco editor is used for a familiar code editing experience. The UI exposes language selection and shows judge output.

Docker/Orchestration
- One docker‑compose file runs the entire stack. The backend is not published to the host to avoid port clashes; the frontend reaches it via the compose network name `backend`.
- The backend needs access to Docker to `docker run` the judge – we mount the Docker socket into the backend container. This is convenient for a prototype but not ideal for multi‑tenant security. A real system would run a dedicated, locked‑down executor service.

Trade‑offs
- Security: the judge is basic. For production you’d add a stronger sandbox (no network, seccomp/AppArmor profiles, user namespaces, cgroups, read‑only FS, per‑job scratch volumes).
- Test coverage: we run a single set of hidden tests per problem. A full system would support multiple testcase groups, scoring, and time/memory per test.
- Realtime: we use polling for simplicity. WebSockets or server‑sent events would be more responsive for high‑scale contests.

---

## 4) Screenshots
![WhatsApp Image 2025-10-30 at 16 32 43_bf55ce27](https://github.com/user-attachments/assets/664045d3-8277-4aaf-b0d1-96745887bb25)
![WhatsApp Image 2025-10-30 at 16 32 53_3208bc95](https://github.com/user-attachments/assets/46d78ed4-cf1e-413d-b8db-8413d49cfaf6)
![WhatsApp Image 2025-10-30 at 16 33 10_c313b41c](https://github.com/user-attachments/assets/b1dfc138-0237-4345-8f01-db5bc5ad37bf)
![WhatsApp Image 2025-10-30 at 16 33 34_15e442fb](https://github.com/user-attachments/assets/b8378aaa-a162-4a34-94b0-e5f00cc9062c)
![WhatsApp Image 2025-10-30 at 16 33 59_efbbc1e8](https://github.com/user-attachments/assets/76ea57a2-c48d-4c2e-b578-adb983502e95)







---
## 4) What’s included
- Sample contest (ID 1) with two problems and official tests
- Leaderboard, per‑problem “Solved” badges, judge output panel, and basic code templates for Python/Java/C++



