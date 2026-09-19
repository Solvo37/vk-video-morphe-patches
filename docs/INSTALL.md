# Installation

## 1. Ready-to-install APK

Open the repository's **Releases** page and look for an asset named:

```text
VK-Video-<version>-patched.apk
```

Before installing the first patched build, uninstall the official VK Video app if it uses the same package name. Android does not allow an APK signed by a different certificate to update the stock installation.

After the first patched install, future project releases can update it as long as they are signed with the same project key.

## 2. Obtainium

Add:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Recommended settings:

```text
Release title filter:
^VK Video

APK asset filter:
^VK-Video-.*-patched\.apk$
```

The release-title filter prevents patch-bundle-only releases from being treated as application updates.

## 3. Morphe custom source

If you prefer to patch the official APK yourself, add the same repository URL as a Morphe source:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Select the VK Video APK, enable the patches you want and run Morphe.

Current patches are independent:

- Disable in-app update
- Remove video ads
- Hide promoted banner content
- Disable ad pixel tracking

## Signature verification

Expected project release certificate SHA-256:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

With Android build-tools:

```bash
apksigner verify --print-certs VK-Video-*-patched.apk
```

Never install a file claiming to be from this project if the certificate fingerprint does not match.
