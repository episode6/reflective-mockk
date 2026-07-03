# ChangeLog

### v1.0.1-SNAPSHOT - Unreleased

- Modernize build infrastructure: Kotlin `v2.3.21`, Gradle `v9.5.1`, Dokka `v2.2.0`
- Bump JVM target to 17
- Upgrade mockK to `v1.14.11`, assertk to `v0.28.1`, kotlinx-coroutines to `v1.11.0`
- Publish artifacts to Sonatype Central (replaces legacy OSSRH)
- Consolidate publish workflows and update GitHub Actions
- Add agent skills (`.agents/`, registered under `.claude/skills/`) and docs-verification CI

### v1.0.0 - Released 8/16/2023

- Upgrade mockK to v1.13.7
- Removed the relective hack that was powering reflective-mock now that [our mockK PR](https://github.com/mockk/mockk/pull/1005) has merged.

### v0.1.0 - Released 1/16/2023

- First release

### EOF
