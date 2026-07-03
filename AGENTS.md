# Agent Guidelines for reflective-mockk

## Validating the Project

Run checks and docs generation:

```bash
./gradlew check dokkaGenerateHtml
```

This command:
- Compiles the project
- Runs all tests
- Generates Dokka HTML documentation

A passing build requires exit code 0 with no test failures.

## Project Structure

This is a single-module Kotlin Multiplatform project (targeting JVM only) rooted at the top of the repo:

- `src/commonMain/` — public API (`reflectiveMockk`, `reflectiveStubs`, etc.)
- `src/jvmMain/` — JVM `actual` implementations
- `src/commonTest/` — tests

## Build Configuration

- **Kotlin**: 2.3.21 with explicit API mode — all public declarations require `public` modifier
- **JVM target**: 17
- **Gradle**: 9.5.1
- **Docs**: Dokka 2.x

## Key Constraints

- **JVM only**: Do not add non-JVM targets
- **Explicit API**: Every public function, class, interface, and typealias must have an explicit `public` modifier
- **assertk**: Use `assertFailure { }` not `assertThat { }.isFailure()` (API changed in 0.28.x)
- **Gradle 9**: `useJUnitPlatform()` test modules must declare `testRuntimeOnly("org.junit.platform:junit-platform-launcher")` explicitly (handled in `buildSrc` ConfigMultiPlugin)

## Skills

See `.agents/` for available skills:
- `.agents/release-branch-skill/` — cut/create a new release branch (e.g. "cut a release branch"); also registered under `.claude/skills/` for auto-trigger
- `.agents/ship-release-skill/` — ship a release
