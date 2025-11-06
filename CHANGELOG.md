# Changelog

All notable changes to this project will be documented in this file.

The format roughly follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). Dates use `YYYY-MM-DD`.

## [Unreleased]

### Added
- Spring Boot demo application under `examples/spring-boot-demo` showcasing generated controllers.
- Coverage enforcement (≥90% instruction/branch) for the runtime core package via JaCoCo.
- Optional metadata endpoint toggle with `enumx.metadata.enabled`.
- Improved filtering feedback (`400 Bad Request`) for unknown/blank query parameters.
- GitHub Actions CI (tests + coverage artifact) and release automation with GitHub Releases.

### Changed
- Documentation updates covering testing workflow, demo usage, and known limitations.

### Fixed
- Alignment between compile-time filtering logic and runtime registry metadata.

## [1.0.3] - 2025-11-07

Initial toolkit capabilities with annotation processing, generated controllers, metadata registry, and GitHub Packages publishing.
