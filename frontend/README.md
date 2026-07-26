# Neon Ledger frontend

React interface for the Neon Ledger educational banking simulation.

## Local setup

```bash
cp .env.example .env.local
npm ci
npm start
```

The UI opens at `http://localhost:3000`. Start the Spring Boot backend first,
then sign in with the demo credentials configured on the backend.

## Checks

```bash
npm test -- --watchAll=false
npm run build
```

The frontend reads its API URL and demo sender account from environment
variables documented in `.env.example`.
