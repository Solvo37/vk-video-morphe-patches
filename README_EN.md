# VK Video Morphe Patches

[Русский](./README.md) · English

Public **Morphe** patches for the Android **VK Video** app (`com.vk.vkvideo`).

## Included patches

- **Disable in-app update** — disables VK Video's in-app update coordinator.
- **Remove video ads** — disables client-side instream / overlay / motion ad features.
- **Hide promoted banner content** — disables the Discover ad banner gate.
- **Disable ad pixel tracking** — disables the dedicated advertising pixel tracker without disabling authentication or general recommendation analytics.

The currently confirmed target is **VK Video 1.163 (51920)**. Future versions must pass an automated real patch test before an APK release is published.

## Install

### Prebuilt APK / Obtainium

When a release contains `VK-Video-1.xxx-patched.apk`, add this repository to Obtainium:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Recommended filters:

```text
Release title: ^VK Video
APK asset:     ^VK-Video-.*-patched\.apk$
```

The first patched build cannot update the official VK-signed app because the signing certificate is different. Uninstall the stock app once, install the patched build, then future releases signed with the same project key can update it.

### Patch it yourself with Morphe

Add this repository as a custom Morphe source:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

The project publishes an `.mpp` bundle in GitHub Releases.

## Trust model

Official upstream APKs are accepted only when their VK signing certificate matches:

```text
057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32
```

Project APK releases use a persistent project signing key. Its public SHA-256 certificate fingerprint is:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

The private signing key is intentionally not public; publishing it would allow third parties to produce APKs Android accepts as legitimate updates.

See [docs/TRUST.md](./docs/TRUST.md), [docs/INSTALL.md](./docs/INSTALL.md), and [CONTRIBUTING.md](./CONTRIBUTING.md).

## Disclaimer

This project is not affiliated with or endorsed by VK, VK Video, Morphe, or Obtainium. It contains patches and automation, not VK Video source code.

Licensed under [GPL-3.0](./LICENSE).
