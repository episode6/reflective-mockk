---
name: release-branch-skill
description: >-
  Cut a new release branch and prepare the version-bump PRs, as defined in
  RELEASE_CHECKLIST.md. Use whenever the user asks to "cut a release branch"
  (or to cut/create/start a new release branch): verifies main is green,
  creates release/v<VERSION>, and opens the snapshot-on-main and
  release-on-branch version-bump PRs that update self.versions.toml and
  docs/CHANGELOG.md.
---

# Cut Release Branch Skill

This skill automates and describes the process of cutting a new release branch and preparing the version bumps, as defined in `RELEASE_CHECKLIST.md`.

## Steps to Execute

### 1. Pre-check
- Ensure the `main` branch is passing all CI checks (is "green").

### 2. Cut new Release Branch
- Checkout the `main` branch and pull the latest changes.
- Create a new branch: `git checkout -b release/v<VERSION>`
- Push the empty branch and set it to be tracked: `git push -u origin release/v<VERSION>`

### 3. Version Bump PRs
Create two separate Pull Requests to update versions.

#### PR 1: Snapshot Version on `main`
- **Target Branch:** `main`
- **PR Title:** `[VERSION] Snapshot v<NEXT_VERSION>-SNAPSHOT`
- **Changes:**
    - Update `version` in `self.versions.toml`. **(VITAL)** When computing `<NEXT_VERSION>`, increment **only the patch version** (e.g., `2.2.0` → `2.2.1`). Never automatically increment the major or minor version — those bumps require explicit human decision.
    - **(VITAL)** Update `docs/CHANGELOG.md` to include a new "Unreleased" section for the next version, AND update the version being released with its release date.

#### PR 2: Release Version on Release Branch
- **Target Branch:** `release/v<VERSION>`
- **PR Title:** `[VERSION] Release v<VERSION>`
- **Changes:**
    - Update `version` in `self.versions.toml` (remove `-SNAPSHOT` if present).
    - **(VITAL)** Update `docs/CHANGELOG.md` with the release date and the final version. Ensure all changes since the last release are documented.

### 4. Create Pull Requests
- Use `gh pr create` or the GitHub UI to create the Pull Requests for the version bump branches created in step 3.

## Verification
- After these steps, the project is ready for the "Harden Release Branch" phase, which requires manual verification and cherry-picking of bug fixes.
