# API Contract — Schema Workflow Engine

Base URL (Dev A local test only): http://localhost:8080
Base URL (Dev B during development): https://CURRENT-NGROK-URL.ngrok-free.app  ← Dev A sends this each session
Base URL (production): https://your-render-app.onrender.com

NOTE FOR DEV B: Never use http://localhost:8080. That address only resolves on Dev A's own machine.
Use the ngrok URL Dev A sends you at the start of every work session. Set it in your .env.local as NEXT_PUBLIC_API_URL.

All requests with auth require:
  Header: Authorization: Bearer <JWT_TOKEN>

All error responses follow this shape:
{
  "timestamp": "2026-06-13T10:15:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Human-readable reason"
}

---

## Auth Endpoints

POST /api/auth/register
Request:  { "name": "string", "email": "string", "password": "string" }
Response: { "id": "string", "name": "string", "email": "string", "role": "SUBMITTER" }

POST /api/auth/login
Request:  { "email": "string", "password": "string" }
Response: { "token": "JWT_STRING", "user": { "id": "string", "name": "string", "email": "string", "role": "ADMIN|REVIEWER|SUBMITTER" } }

GET /api/auth/me  [requires auth]
Response: { "id": "string", "name": "string", "email": "string", "role": "string" }

---

## Schema Endpoints

POST /api/schemas  [requires ADMIN role]
Request:  { "name": "string", "description": "string?", "schema": FormSchemaJSON }
Response: { "id": "string", "name": "string", "version": 1, "isActive": true, "createdAt": "ISO date" }

GET /api/schemas
Response: { "schemas": [ { "id", "name", "description", "version", "isActive", "createdAt" } ] }

GET /api/schemas/:id
Response: { "id", "name", "description", "version", "isActive", "schema": FormSchemaJSON, "createdAt", "updatedAt" }

PUT /api/schemas/:id  [requires ADMIN role]
Request:  { "name": "string", "description": "string?", "schema": FormSchemaJSON }
Response: { "id": "(new version id)", "version": 2, "isActive": true }

---

## Submission Endpoints

POST /api/submissions  [requires auth]
Request:  { "formSchemaId": "string", "data": {...formAnswers}, "status": "DRAFT" }
Response: { "id": "string", "status": "DRAFT", "createdAt": "ISO date" }

GET /api/submissions  [requires auth]
Query params: ?status=SUBMITTED&mine=true
Response: { "submissions": [ { "id", "formSchemaId", "formSchemaName", "submitterName", "status", "createdAt", "updatedAt" } ] }

GET /api/submissions/:id  [requires auth]
Response: {
  "id", "formSchemaId", "formSchemaName", "submitterName",
  "data": {...formAnswers},
  "status": "string",
  "workflowLogs": [ { "id", "fromStatus", "toStatus", "actorId", "note", "createdAt" } ],
  "auditLogs": [ { "id", "actorId", "action", "metadata", "createdAt" } ],
  "createdAt", "updatedAt"
}

PATCH /api/submissions/:id/status  [requires auth]
Request:  { "toStatus": "SUBMITTED|UNDER_REVIEW|APPROVED|REJECTED", "note": "optional string" }
Response: { "id": "string", "status": "new status", "updatedAt": "ISO date" }
Error 403 if transition is invalid for current role or state.

---

## Dashboard Endpoints

GET /api/dashboard/stats  [requires auth]
Response: { "total": 0, "pendingReview": 0, "approvedThisMonth": 0, "rejectionRate": 0.12 }

---

## FormSchemaJSON Format (agreed format for form blueprints)

{
  "id": "string",
  "title": "string",
  "steps": [
    {
      "id": "string",
      "title": "string",
      "fields": [
        {
          "id": "string",
          "label": "string",
          "type": "text|email|number|textarea|select|radio|checkbox|date",
          "required": true,
          "placeholder": "string (optional)",
          "options": ["array", "of", "strings"],  // only for select/radio/checkbox
          "condition": {   // optional — field only shows if this is true
            "field": "fieldId",
            "operator": "equals|not_equals",
            "value": "someValue"
          }
        }
      ]
    }
  ]
}