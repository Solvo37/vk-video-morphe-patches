# Changelog

## 0.2.2 — 2026-09-22

Runtime ad removal hardening for VK Видео 1.163:

- added **Filter clip feed ads**: server-provided `StaticAd`, `MarketAd`, `FloatingAd` and MyTarget short-video feed DTOs are removed before `ClipsRecomResponseAdapter` can turn them into SDK feed items or an install/action CTA;
- added **Block midroll ads**: `AdSection.MIDROLL` is rejected in the `x13.e.b(...)` runtime gate before `VideoAutoPlay` pauses/switches the main player into `InstreamAdEngine`;
- both patches are separate from the earlier feature/config suppression so they can be safely applied as a delta over the 1.163.1 project-signed build;
- fixed the production Morphe JSON gate for the current report format (`patchingSteps[].success` instead of the obsolete top-level `.success`).

## 0.2.1 — 2026-09-22

Расширенное удаление рекламы для VK Видео 1.163:

- **Remove video ads** теперь не ограничивается тремя `VideoFeatures`: рекламные поля `instream`, `mobileInstream`, `sport` и `banners` обнуляются в `VideoGetAdsResponseDto`;
- списки `preroll`, `midroll` и `postroll` дополнительно обнуляются в `VideoVideoAdsInstreamSectionsDto`;
- добавлен отдельный **Remove clip ads** для рекламного стека VK Клипов: Clips feature gates, конкретный provider/config слой и `SdkClipsAdsFeaturesParams`;
- patch profile остаётся fail-closed: несовпавший fingerprint останавливает публикацию вместо молчаливого пропуска;
- изменение patch profile на `main` теперь принудительно создаёт новый immutable APK revision для того же upstream versionCode вместо пропуска как уже опубликованной версии.

## 0.2.0 — 2026-09-22

Release-pipeline hardening on the road to 1.0:

- all available upstream sources are evaluated and the highest verified `versionCode` wins;
- baseline can be resolved from the latest stable release metadata with repository fallback;
- split APKs are merged before Morphe so ARM64 native libraries are patchable;
- added machine-readable `upstream.json`, `build-metadata.json` and Morphe reports;
- added fail-closed manifest, multidex, native-pattern and v1-signature static gates;
- required patch names are verified against Morphe `appliedPatches`;
- final project certificate and zip alignment are verified before publishing;
- same-`versionName` / newer-`versionCode` builds get distinct release tags;
- patch-only releases are prereleases and the newest bundle is also attached to the latest stable app release;
- GitHub Actions dependencies are pinned to immutable commits;
- added `ROADMAP.md` with explicit 1.0 exit criteria.


## 0.1.2 — 2026-09-22

Рабочий production-профиль для VK Видео 1.163:

- добавлен обязательный **Bypass native signature check** для `lib/arm64-v8a/libvkcore.so`;
- подтверждено, что мгновенный exit переподписанного APK происходил в native signature/anti-tamper path;
- добавлен fail-closed binary pattern check для native patch;
- финальная сборка переведена на **Morphe STRIP_FAST**;
- добавлены `zipalign` и **APK Signature Scheme v3 only**;
- подтверждена совместная установка с обычным VK;
- подтверждён реальный запуск полной конфигурации патчей;
- опубликован исправленный APK release `1.163`;
- автоматический upstream: RuStore → Google Play/gplaydl → APKPure;
- bundle `patches-v0.1.2` опубликован отдельно от APK release.

## 0.1.1 — 2026-09-19

- добавлен **Fix install conflict with stock VK**;
- устранён `INSTALL_FAILED_DUPLICATE_PERMISSION / STATUS_FAILURE_CONFLICT` при установленном обычном VK;
- добавлен постоянный project signing key и Obtainium release flow;
- добавлены проверки upstream package/signature и защита от downgrade.

## 0.1.0 — 2026-09-19

Первый публичный patch set для VK Видео 1.163 / 51920:

- Disable in-app update
- Remove video ads
- Hide promoted banner content
- Disable ad pixel tracking
- CI build Morphe `.mpp`
- scheduled upstream compatibility checks
