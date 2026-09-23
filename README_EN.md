# VK Video Patched

[![CI](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml)
[![Auto build](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml)

Public **Morphe** patches and signed ARM64 builds for **VK Video**. The patched app can coexist with the regular VK app.

## Download

Current stable release: **1.163.6**.  
Android package version inside the APK: **1.163 / versionCode 51920**.

[Download the latest APK](https://github.com/Solvo37/vk-video-morphe-patches/releases/latest)

Only the APK is published as a Release asset. Build reports and diagnostics stay in GitHub Actions.

## Updates

The auto-build checks RuStore, Google Play and APKPure every 6 hours, verifies package/signing data, selects the highest verified versionCode, applies all required patches and publishes only if every compatibility gate passes.

Release revisions are immutable:

- current: `1.163.6`;
- next rebuild of Android 1.163: `1.163.7`;
- a new Android 1.164 line starts at `1.164.0`.

For Obtainium use this repository and the APK asset filter:

```text
^VK-Video-.*-patched\.apk$
```

## Install

Uninstall the official **VK Video** once before the first project-signed install. The regular **VK** app can stay installed. Future project builds use the same signing certificate and can update previous project builds.

## Development

```bash
gradle :patches:buildAndroid
```

See `patches/`, `ci/`, `.github/workflows/auto-update.yml` and `CHANGELOG.md`.

This project is not affiliated with or endorsed by VK, VK Video, Morphe or Obtainium. Licensed under [GPL-3.0](./LICENSE).
