# VK Video Morphe patches

[![CI](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml)
[![VK Video upstream](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml)

Morphe patches for **VK Video** (`com.vk.vkvideo`).

Developed against VK Video **1.163 (51920)**. New upstream versions are checked automatically in GitHub Actions before a patched APK can be released.

## Patches

- **Disable in-app update** — disables VK Video's own in-app update coordinator.
- **Remove video ads** — uses VK Video's own video feature toggles to disable instream/overlay/motion ads.
- **Hide promoted banner content** — forces the Discover ad-banner gate off.
- **Disable ad pixel tracking** — disables the dedicated ad pixel tracker without disabling general auth/recommendation analytics.

## Automatic update pipeline

The `VK Video upstream` workflow:

1. Queries **RuStore** for the latest `com.vk.vkvideo` version.
2. Downloads the official upstream APK using a pinned RuStore downloader.
3. Verifies the upstream APK is signed with the expected VK certificate.
4. Builds this Morphe `.mpp` bundle.
5. Applies the patches to the new APK with Morphe Desktop in `FULL` bytecode mode.
6. If patching fails, the workflow turns red and opens/updates a compatibility issue.
7. If patching succeeds and signing secrets are configured, it publishes the patched APK plus the `.mpp` bundle in a GitHub Release whose tag is the VK Video version.

The scheduled check runs every six hours. It exits early when that upstream version already has a release.

### Why RuStore first?

APK mirrors can lag behind the official Russian distribution channel. RuStore is used as the primary feed; APKMirror is useful as a secondary human cross-check, not as the automation download source.

## Morphe

Add this repository as a custom Morphe source:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

The exact 1.163 target is marked stable. Other app versions are exposed as experimental so the fingerprints can be compatibility-tested without pretending every future build is already verified.

## Obtainium

After the first signed APK release exists, add:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

as a GitHub source in Obtainium.

Recommended APK asset filter:

```regex
^VK-Video-.*-patched\.apk$
```

Release tags are the upstream VK Video version (for example `1.163`), so Obtainium can reconcile the release version with the installed app's `versionName`.

> The first patched install cannot update the stock VK-signed APK because the patched build is signed with your own key. Uninstall the stock VK Video once, install your signed patched build, and future releases can update it as long as the same signing key is retained.

## One-time signing setup

Do **not** commit a private keystore to this repository. Generate one locally and add it to GitHub Actions secrets.

Required repository secrets:

- `ANDROID_KEYSTORE_B64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Until these exist, Actions still performs the full upstream download + compatibility patch check and uploads a workflow artifact, but it deliberately does **not** publish an Obtainium release.

## Development target

- package: `com.vk.vkvideo`
- versionName: `1.163`
- versionCode: `51920`
- APK SHA-256 used for reverse engineering: `61d8c2b0837704d2d197a35b75f5c07872927e77d8861b61ce78ba0c5626c1c8`

See `docs/reverse-engineering-1.163.md` for the matching points.
