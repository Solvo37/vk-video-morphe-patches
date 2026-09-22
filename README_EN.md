# VK Video Morphe Patches

[Русский](./README.md) · English

Public **Morphe** patches for the Android **VK Video** app (`com.vk.vkvideo`) with signed APK releases suitable for **Obtainium**.

## Current status

- ✅ Verified app target: **VK Video 1.163 / versionCode 51920**
- ✅ Prebuilt APK: [Release 1.163](https://github.com/Solvo37/vk-video-morphe-patches/releases/tag/1.163)
- ✅ Current patch bundle: **v0.1.2**
- ✅ Re-signed build verified to launch alongside the stock VK app
- ✅ Build pipeline: **Morphe STRIP_FAST → zipalign → APK Signature Scheme v3**
- ⚠️ The native bypass currently targets **ARM64 / arm64-v8a**

## Patches

| Patch | Purpose |
|---|---|
| **Fix install conflict with stock VK** | lets the project-signed VK Video coexist with stock `com.vkontakte.android` |
| **Bypass native signature check** | patches `libvkcore.so` so a re-signed build is not terminated during startup |
| **Disable in-app update** | disables VK Video's internal update prompt/check |
| **Remove video ads** | disables client-side instream / overlay / motion ad features |
| **Hide promoted banner content** | disables the Discover promoted-banner gate |
| **Disable ad pixel tracking** | disables the dedicated advertising pixel tracker |

These patches target specific client-side mechanisms. They do not claim to remove every server-controlled promotion, ad surface, or analytics event.

## Install

Download `VK-Video-<version>-patched.apk` from [Releases](https://github.com/Solvo37/vk-video-morphe-patches/releases).

The first project-signed build cannot update the official VK Video installation because the certificates differ. Uninstall the official **VK Video** app once, then install the project build.

The regular **VK** app may remain installed; the coexistence patch is specifically intended for that case.

### Obtainium

Repository URL:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Recommended filters:

```text
Release title: ^VK Video
APK asset:     ^VK-Video-.*-patched\.apk$
```

See [docs/INSTALL.md](./docs/INSTALL.md).

## Morphe custom source

Use the same repository URL as a Morphe custom source. Standalone patch bundles are published as `patches-v0.1.x` releases.

For a re-signed VK Video 1.163 build, **Bypass native signature check** is mandatory. Without it, the app terminates during startup after the native signature check.

## Automated updates

The release workflow checks upstream in this order:

1. RuStore
2. Google Play via gplaydl
3. APKPure via apkeep

Before patching, it validates package name, version metadata, the original VK certificate, downgrade protection, bytecode fingerprints, and the native signature-check pattern.

A new version is not published merely because it downloads. If a required fingerprint or native pattern no longer matches, the pipeline stops and opens a compatibility issue.

## Trust

Expected upstream VK certificate SHA-256:

```text
057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32
```

Expected project release certificate SHA-256:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

The private signing key is not stored in the repository.

See [docs/TRUST.md](./docs/TRUST.md), [docs/UPSTREAM.md](./docs/UPSTREAM.md), and [CONTRIBUTING.md](./CONTRIBUTING.md).

## Build the patch bundle

Java 21 is required.

```bash
gradle :patches:buildAndroid
```

Output:

```text
patches/build/libs/*.mpp
```

## Disclaimer

This project is not affiliated with or endorsed by VK, VK Video, Morphe, or Obtainium. It contains patches and build automation, not VK Video source code.

Licensed under [GPL-3.0](./LICENSE).
