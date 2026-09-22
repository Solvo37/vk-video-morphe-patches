# Changelog

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
