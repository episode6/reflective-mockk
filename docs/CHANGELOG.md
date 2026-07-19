# ChangeLog

### v1.1.1-SNAPSHOT - Unreleased

- CI: snapshot publishes now use Maven's timestamped unique-snapshot protocol (new
  `scripts/upload-snapshots.py`, ported from tacita — uploads timestamped filenames and
  re-PUTs each module's `maven-metadata.xml` with an incremented buildNumber). The
  previous plain PUTs of non-unique snapshot filenames only registered on a version's
  first publish; sonatype central accepted but never served later republishes, so a
  republished `-SNAPSHOT` version kept serving its first build's bytes

### v1.1.0 - Released 7/3/2026

- Modernize build infrastructure: Kotlin `v2.3.21`, Gradle `v9.5.1`, Dokka `v2.2.0`
- Bump JVM target to 17
- Upgrade mockK to `v1.14.11`, assertk to `v0.28.1`, kotlinx-coroutines to `v1.11.0`
- Publish artifacts to Sonatype Central (replaces legacy OSSRH)
- Consolidate publish workflows and update GitHub Actions
- Add agent skills (`.agents/`, registered under `.claude/skills/`) and docs-verification CI
- Add release tooling (`scripts/ship-release.py`, `RELEASE_CHECKLIST.md`) that reads the version name from `self.versions.toml` (still the source of truth)
- Ignore generated `gradle/gradle-daemon-jvm.properties` and `.kotlin/`

### v1.0.0 - Released 8/16/2023

- Upgrade mockK to v1.13.7
- Removed the relective hack that was powering reflective-mock now that [our mockK PR](https://github.com/mockk/mockk/pull/1005) has merged.

### v0.1.0 - Released 1/16/2023

- First release

### EOF
