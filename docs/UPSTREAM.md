# Upstream version strategy

## Source priority

The automatic release workflow uses this order:

1. **RuStore** — primary source for VK Video.
2. **Google Play via gplaydl** — first fallback.
3. **APKPure via apkeep** — final fallback.

Google Play access is optional in CI. Configure the linked gplaydl credential to enable the fallback. RuStore is always checked first.

Every candidate actually used for a build is verified locally before patching.

## Required verification

Before any release can be produced:

1. package must be `com.vk.vkvideo`;
2. `versionName` and `versionCode` must be readable from the base APK;
3. source APK certificate SHA-256 must equal the expected VK certificate;
4. version must not be below the project baseline;
5. every mandatory Morphe patch must apply successfully.

Expected upstream VK certificate:

```text
057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32
```

## Split APKs

Google Play may return a base APK plus configuration splits.

The workflow:

1. locates and verifies the base APK;
2. patches the base with Morphe;
3. copies all original configuration splits;
4. merges the patched base + splits into one universal APK with APKEditor;
5. signs the resulting universal APK with the project's persistent key;
6. verifies the signed APK before publishing it.

## Baseline protection

Current baseline:

```text
VK Video 1.163
versionCode 51920
```

A source that only exposes an older version is reported but never published as a downgrade.

## Compatibility gate

A newer APK is not considered supported merely because it downloads.

All mandatory patches must apply:

- Fix install conflict with stock VK
- Disable in-app update
- Remove video ads
- Hide promoted banner content
- Disable ad pixel tracking

A fingerprint failure stops the release and opens/updates a compatibility issue.
