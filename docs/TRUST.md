# Trust and release signing

## Upstream VK Video verification

The automation verifies the source APK before patching.

Expected upstream package:

```text
com.vk.vkvideo
```

Expected VK certificate SHA-256:

```text
057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32
```

A package/version/signature mismatch stops the pipeline.

## Project release key

Expected public certificate fingerprint for project APK releases:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

Certificate subject:

```text
CN=Solvo37 VK Video Patched, OU=Morphe, O=Solvo37
```

The private signing key must remain private. Publishing it would allow anyone to sign a modified APK with the same certificate, defeating Android's update-signature trust boundary.

## What the repository publishes

A normal app release may contain:

- patched APK;
- Morphe `.mpp` bundle;
- upstream metadata used for the build.

The repository does not intentionally commit the original VK Video APK.

## Reproducibility notes

Patch source code and CI configuration are public. APK output still depends on the exact upstream VK Video build and the release signing key. The CI records upstream metadata and refuses to publish when required fingerprints fail.
