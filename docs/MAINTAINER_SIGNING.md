# Maintainer signing setup

This document intentionally contains **no private key material and no passwords**.

Public APK releases must always use the same Android signing key so users can install updates over previous project releases.

## Required Actions secrets

Configure these repository secrets:

```text
ANDROID_KEYSTORE_B64
ANDROID_KEYSTORE_PASSWORD
ANDROID_KEY_ALIAS
ANDROID_KEY_PASSWORD
```

`ANDROID_KEYSTORE_B64` must contain the base64 representation of the persistent keystore file.

## Expected public certificate

After signing, the APK certificate must match:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

Verify a built APK with:

```bash
apksigner verify --print-certs VK-Video-*-patched.apk
```

## Backup policy

Keep at least two offline backups of the keystore and its credentials.

If the private key is lost, a newly generated key cannot update existing user installations. Users would have to uninstall and reinstall the app.

If the private key is exposed, stop publishing with it and clearly announce a certificate migration. Do not commit the keystore to Git, even in a private branch.

## Fork behavior

Forks can build and test the patch bundle without the upstream repository's signing secrets. They should use their own signing key if they choose to publish APKs.
