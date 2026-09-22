# Roadmap to 1.0

The goal of 1.0 is not “more patches”. It is a release pipeline that can detect a new VK Video build, prove that the expected patch points still match, produce a signed APK, and refuse publication when confidence is insufficient.

## 0.2 — upstream and release hardening

- [x] verify package and original VK certificate;
- [x] query/download multiple upstream sources;
- [x] select the candidate with the highest verified `versionCode`;
- [x] keep a verified baseline independent from source rollout lag;
- [x] support same-`versionName` builds with different `versionCode`;
- [x] publish build metadata and checksums;
- [x] pin third-party GitHub Actions to immutable commits.

## 0.3 — native hardening

- [x] ARM64 `libvkcore.so` signature-check bypass;
- [x] fail closed unless the expected native pattern is unique;
- [x] static post-build verification that the old pattern is gone and the patched pattern occurs exactly once;
- [ ] add verified fingerprints for additional ABIs if future releases require them.

## 0.4 — launch testing

- [x] static startup prerequisites: Application class present, multidex retained, native library present;
- [ ] automated real-device/cloud-device launch test;
- [ ] gate stable publication on a successful runtime smoke test when a suitable ARM64 device backend is available.

GitHub-hosted Linux CI alone is not treated as a substitute for a real ARM64 Android runtime.

## 0.5 — patch validation

- [x] Morphe JSON result must report success;
- [x] every required patch name must appear in `appliedPatches`;
- [x] manifest coexistence validation;
- [x] native binary validation;
- [x] APK signature and zipalign validation;
- [ ] add deeper semantic post-build checks when Morphe exposes stable verification hooks for modified methods.

## 0.6 — release UX

- [x] app releases use `VK Video ... patched` titles for Obtainium;
- [x] app releases include APK, SHA-256, patch bundle and upstream/build metadata;
- [x] standalone patch-bundle releases are prereleases so they do not replace the stable APK release as “Latest”;
- [x] same `versionName` with a new `versionCode` gets a distinct tag.

## 0.9 — release candidate

Requirements before calling the project 1.0:

- at least one upstream version change handled by the automated pipeline without manual APK surgery;
- device smoke test on the produced candidate;
- update over the previous project-signed APK tested;
- video playback, seek, fullscreen/background transitions and the targeted ad surfaces checked.

## 1.0 — stable

1.0 means a new supported VK Video build can flow from upstream detection to a verified, signed Obtainium release without manual repair. If any required fingerprint changes, the pipeline must stop instead of guessing.
