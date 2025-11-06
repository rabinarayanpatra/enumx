# EnumX

EnumX is a compile-time code generation toolkit that turns annotated Java enums into fully fledged Spring MVC REST endpoints. Annotate your enums once and EnumX will provide:

- Type-safe controllers exposing `/api/{path}` list + validation endpoints
- Automatic filtering derived from `@Filterable` fields with type-aware comparisons
- Runtime metadata registry with Spring Boot auto-configuration and diagnostics endpoint
- Simple annotation model (`@EnumApi`, `@Expose`, `@Filterable`, `@Hide`) that keeps enum declarations tidy

## Quick Start

1. **Add the dependency and annotation processor**

   ```xml
   <dependency>
     <groupId>io.github.rabinarayanpatra</groupId>
     <artifactId>enumx</artifactId>
     <version>1.0.3</version>
   </dependency>
   ```

   Configure the EnumX annotation processor (e.g. via Maven Surefire or build plugin) so controllers are generated during compilation.

2. **Annotate an enum**

   ```java
   @EnumApi(path = "roles", includeAllFields = true)
   public enum Role {
       ADMIN("Administrator", true),
       USER("Regular User", false);

       @Expose("displayName")
       private final String label;

       @Filterable
       private final boolean active;

       Role(String label, boolean active) {
           this.label = label;
           this.active = active;
       }

       public String getLabel() { return label; }
       public boolean isActive() { return active; }
   }
   ```

3. **Use the generated controller**

   A `RoleController` class appears under `com.yourpackage.generated` exposing:

   - `GET /api/roles` &rarr; returns enum entries with exposed fields, supporting `?active=true`
   - `POST /api/roles/validate` &rarr; validates provided enum names

4. **Inspect metadata**

   When using Spring Boot, include EnumX on the classpath and the auto-configuration will:

   - Register every `@EnumApi` enum found in your application packages
   - Provide `GET /enumx/metadata` describing registered enums and exposed/filterable fields

## Filtering Rules

- Mark a field `@Filterable` to allow `GET /api/{path}?field=value`
- Filter names default to the API field name (`@Expose` value if present) and can be overridden via `@Filterable("custom-name")`
- Comparisons are type aware (boolean, numeric primitives, `BigDecimal`, `BigInteger`, and enum typed fields). Unsupported types fall back to string comparison.
- Unknown filters result in no matches, helping clients spot typos quickly.

## Validation Endpoint

Every generated controller additionally exposes `POST /api/{path}/validate` which accepts a JSON array of enum names and returns a `{ "valid": [...], "invalid": [...] }` payload.

## Spring Boot Integration

EnumX ships with auto-configuration registered via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. Simply depend on the library in a Spring Boot application and:

- An `EnumRegistry` bean is created automatically
- All `@EnumApi` enums within your application base packages are registered at startup
- A diagnostics controller surfaces metadata at `/enumx/metadata`

No manual registration is required, but you can still inject `EnumRegistry` to inspect metadata or register enums programmatically if needed.

## Development

- Build & test: `mvn clean verify`
- Verify annotation processor output via the compile-testing suite under `src/test/java`
- The `deploy.yml` GitHub Actions workflow (see `.github/workflows/deploy.yml`) runs tests, bumps the Maven version, and publishes to GitHub Packages once changes land on the protected main branch.

Contributions and issue reports are welcome!
