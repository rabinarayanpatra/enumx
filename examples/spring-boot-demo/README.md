# EnumX Spring Boot Demo

This example application demonstrates how [EnumX](../../README.md) generates REST controllers for annotated enums inside a standard Spring Boot project.

## Prerequisites

- Java 21
- Maven 3.9+
- A locally built version of EnumX (run `mvn -q install` in the repository root) or a released artifact available via Maven Central/GitHub Packages.

> ℹ️ By default the demo looks for `ENUMX_VERSION` environment variable. If you skip it, it falls back to `1.0.3-SNAPSHOT`.

## Running the demo

```bash
cd examples/spring-boot-demo
mvn -q spring-boot:run -Denumx.version=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version -f ../../pom.xml)
```

The application exposes the generated controllers at:

- `GET /api/priorities` – lists the `Priority` enum values with filtering by `level`, `category`, etc.
- `POST /api/priorities/validate` – validates enum names.
- `GET /api/regions` – lists the `Region` enum values (filterable by `continent`).

When the auto-configuration is active, diagnostic metadata is available at `GET /enumx/metadata` (can be disabled using `enumx.metadata.enabled=false`).

## Example requests

```bash
# List blocking priorities
curl 'http://localhost:8080/api/priorities?category=BLOCKING'

# Validate priority names
curl -X POST 'http://localhost:8080/api/priorities/validate' \
     -H 'Content-Type: application/json' \
     -d '["CRITICAL", "UNKNOWN"]'

# Inspect metadata (if enabled)
curl 'http://localhost:8080/enumx/metadata'
```

For more details on how EnumX works, check the root README and the `src/main/java` package in this demo.
