# Upstream version strategy

## Current source

The scheduled workflow queries RuStore for `com.vk.vkvideo`.

The returned metadata includes:

- versionName;
- versionCode;
- direct download URL;
- upstream signing certificate hash.

The downloaded APK is verified again locally before patching.

## Baseline protection

The project baseline is currently:

```text
VK Video 1.163
versionCode 51920
```

If an automated source offers an older version, the workflow reports it but refuses to publish a downgrade.

This matters because app stores and mirrors can expose different staged versions at the same time.

## Newer builds

A new version is not considered supported merely because it downloads successfully.

Before an APK release is published, Morphe must successfully apply all required patches:

- Disable in-app update
- Remove video ads
- Hide promoted banner content
- Disable ad pixel tracking

If any required fingerprint fails, the workflow stops and creates/updates a compatibility issue.

## Adding another source

A future source should be treated as a version candidate, not blindly trusted.

Required checks before it may replace the current candidate:

1. package is `com.vk.vkvideo`;
2. version/versionCode are internally consistent;
3. upstream VK signing certificate matches the expected fingerprint;
4. version is not lower than the current baseline;
5. full Morphe patch test succeeds.

Do not make third-party mirrors authoritative solely because they expose a numerically higher version.
