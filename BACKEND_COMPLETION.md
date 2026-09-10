# Backend completion notes

This version completes the backend implementation without changing the JSP/frontend layer.

## Completed

- Unified login with the project's password encoder and JWT-backed database sessions.
- Fixed the session repository to use the actual Oracle `USER_SESSIONS` schema (`TOKEN`, `LOGIN_TIME`, `EXPIRY_TIME`, `IS_ACTIVE`).
- Added session revocation and logout support.
- Added registration and password-change endpoints.
- Added password-reset token workflow using the existing `PASSWORD_RESET_TOKEN` table.
- Added missing 2FA columns and persistent one-time backup codes.
- Fixed the TOTP Base32 implementation and added enable/disable/backup-code flows.
- Added generated-key handling for database inserts so response DTOs receive IDs.
- Completed CRUD HTTP operations for users, roles, and permissions.
- Completed read/update endpoints for access requests and read/create endpoints for approvals and audits.
- Approval decisions now update the related access request status.
- Report date filters are now applied to request/audit counts.
- Authentication is enforced for non-public API endpoints and RBAC permissions are mapped to API operations.
- Added default `ADMIN` and `EMPLOYEE` roles plus standard permissions through migration `V012`.
- First registered user is assigned `ADMIN`; subsequent registered users are assigned `EMPLOYEE` when the seeded roles exist.
- Moved JWT signing secret to configuration/environment support (`AMS_JWT_SECRET`).
- Made HMAC verification constant-time.
- Added a database-backed `SessionManager` implementation.

## Database migrations

- `V011__complete_security_schema`: 2FA fields + backup-code table.
- `V012__seed_roles_permissions`: default roles and permissions.

## Verification

The Java 17 backend source was compiled successfully against the bundled Oracle JDBC driver using Jakarta Servlet API stubs for offline syntax verification. A small runtime check also passed for password encoding, TOTP secret generation/QR generation, and JSON serialization.

The actual Oracle database and Tomcat runtime were not available in this environment, so a live DB integration test was not performed.

## Important deployment setting

Set `AMS_JWT_SECRET` to a strong random value of at least 32 bytes in the real deployment. The configured placeholder is only a development fallback.

The password-reset endpoint currently returns the reset token to make the workflow testable without an email provider. In production, that token should be delivered through a trusted email/SMS notification service and not returned in the API response.
