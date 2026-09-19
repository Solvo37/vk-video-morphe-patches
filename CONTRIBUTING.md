# Contributing

Contributions are welcome.

## Useful contributions

- update fingerprints for a new VK Video version;
- make fingerprints less dependent on unstable R8 names;
- fix a patch that affects unrelated functionality;
- add narrowly scoped privacy/ad UI patches;
- improve CI validation or documentation.

## Compatibility work

When VK Video changes:

1. identify the exact `versionName` and `versionCode`;
2. verify the APK package and VK signing certificate;
3. inspect the changed call site / class;
4. prefer semantic fingerprints (strings, method calls, field types) over raw obfuscated class names;
5. keep each patch independent;
6. run a full Morphe patch test;
7. document the new matching point in `docs/`.

Do not upload or commit the proprietary VK Video APK to this repository.

## Build

Java 21 is required.

The Morphe Gradle plugin is fetched from GitHub Packages. For a local build, provide GitHub credentials with package-read access if Gradle requests them:

```bash
export GITHUB_ACTOR="<github-user>"
export GITHUB_TOKEN="<token-with-read-packages>"
gradle :patches:buildAndroid
```

## Pull requests

Please include:

- VK Video version/versionCode tested;
- which patch changed;
- why the old fingerprint stopped matching;
- what you used as the new semantic anchor;
- whether the resulting APK launches and the target behavior works.

Keep unrelated changes in separate pull requests.
