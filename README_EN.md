# VK Video Morphe Patches

[Русский](./README.md) · English

Public **Morphe** patches for the Android **VK Video** app (`com.vk.vkvideo`) with signed APK releases suitable for **Obtainium**.

## Current status

- ✅ Verified app target: **VK Video 1.163 / versionCode 51920**
- ✅ Prebuilt APK: [Latest stable release](https://github.com/Solvo37/vk-video-morphe-patches/releases/latest)
- ✅ Current patch bundle: **v0.2.3**
- ✅ Full patch profile verified on a real ARM64 device alongside the stock VK app; the exact release APK additionally passes automated 0.2 static gates
- ✅ Build pipeline: **Morphe STRIP_FAST → zipalign → APK Signature Scheme v3**
- ⚠️ The native bypass currently targets **ARM64 / arm64-v8a**

## Patches

| Patch | Purpose |
|---|---|
| **Fix install conflict with stock VK** | lets the project-signed VK Video coexist with stock `com.vkontakte.android` |
| **Bypass native signature check** | patches `libvkcore.so` so a re-signed build is not terminated during startup |
| **Disable in-app update** | disables VK Video's internal update prompt/check |
| **Remove video ads** | disables video ad feature gates and strips known server instream/mobile/sport/banner payloads |
| **Remove clip ads** | disables VK Clips ad feature/config/SDK paths |
| **Filter clip feed ads** | removes server-provided StaticAd / MarketAd / MyTarget / FloatingAd feed items before they become Clips SDK items or install CTAs |
| **Block midroll ads** | blocks the runtime MIDROLL branch before the main video player switches to instream ads |
| **Filter Clips SDK ads** | drops ad-marked SDK videos and StaticAds/MarketAds inside the Clips SDK, including client-injected install CTAs |
| **Block deep midroll ads** | disables the dedicated `request_midroll` runnable, midpoint setup, and direct `midroll` section starts |
| **Hide profile ad-free promo** | removes the “Disable ads / free for 14 days” card from the My screen before holder creation | user-selectable |
| **Hide promoted banner content** | disables the Discover promoted-banner gate |
| **Disable ad pixel tracking** | disables the dedicated advertising pixel tracker |

These patches target the ad paths reverse-engineered in VK Video 1.163, including server feed items and the runtime MIDROLL branch. A newly introduced server/client path in a future version still requires fresh reverse engineering.

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

Use the same repository URL as a Morphe custom source. Standalone patch bundles are published as `patches-v0.2.x` releases.

For a re-signed VK Video 1.163 build, **Bypass native signature check** is mandatory. Without it, the app terminates during startup after the native signature check.

## Automated updates

The release workflow queries every available upstream source — RuStore, Google Play via gplaydl, and APKPure via apkeep — verifies each downloaded base APK, and selects the candidate with the **highest verified `versionCode`**. Source priority is only used as a tie-breaker.

Before patching, it validates package name, version metadata, the original VK certificate, downgrade protection, bytecode fingerprints, the native signature-check pattern, the manifest coexistence fix, multidex startup prerequisites, zip alignment, and the final project signature.

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

Road to stable 1.0: [ROADMAP.md](./ROADMAP.md).

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
