# r-pixel-clone

A 100x100 collaborative pixel canvas (like Reddit's r/place): every user paints one pixel at a time, and all changes appear live for everyone.

## Stack

- **Backend**: Spring Boot 4.1 (Java 21), PostgreSQL, Redis, WebSocket, JWT auth
- **Frontend**: React + Vite + TypeScript
- **Infra**: docker-compose (postgres, redis, kafka, backend, frontend/nginx)

## Run

### Option A — everything in Docker (recommended)

```sh
docker compose up -d --build
```

Open http://localhost:8081 (nginx serves the React app and proxies `/api` + `/ws` to the backend).

### Option B — local backend + frontend dev servers

1. Start the infra only:
   ```sh
   docker compose up -d postgres redis
   ```
   (Postgres is mapped to host port **5433** to avoid clashing with a local postgres on 5432.)
2. Backend — either from your IDE or:
   ```sh
   cd backend
   mvnw spring-boot:run
   ```
   Defaults already point at `localhost:5433`; the docker-compose backend service overrides them via `POSTGRES_HOST`/`REDIS_HOST` env vars, so the same jar runs in both places.
3. Frontend:
   ```sh
   cd frontend
   npm install
   npm run dev
   ```
   Open http://localhost:5173 (Vite proxies `/api` and `/ws` to the backend on 8080).

Note: the `backend` container maps to host port **8082** (the frontend/nginx container still reaches it on `backend:8080` internally), so port 8080 stays free for a locally-run backend or IDE runs.

## API

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | – | `{username, password}` → `{token, username}` |
| POST | `/api/auth/login` | – | `{username, password}` → `{token, username}` |
| GET | `/api/canvas` | – | 10,000 colors, index `x*100+y`, `#FFFFFF` = empty |
| GET | `/api/pixels/{x}/{y}` | – | single pixel |
| POST | `/api/pixels` | Bearer token | `{x, y, color}` (hex `#RRGGBB`) |
| WS | `/ws/canvas` | – | live `{x, y, color, updatedBy, updatedAt}` broadcasts |

## Future: one-pixel cooldown

The rule is a plug-in point: `CooldownService` (currently a no-op). Set `app.pixel.cooldown-seconds` and implement a Redis-backed check when you're ready to restrict placement.
