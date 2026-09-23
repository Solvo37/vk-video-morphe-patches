# VK Video Patched

[![CI](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml)
[![Auto build](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml)
[![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](./LICENSE)

Патчи **Morphe** и готовая подписанная ARM64-сборка **VK Видео** без найденных рекламных блоков. Модифицированный VK Видео устанавливается рядом с обычным VK.

## Скачать

**Текущий стабильный релиз: 1.163.6**  
Android внутри APK: **1.163 / versionCode 51920**.

➡️ [Скачать последний APK](https://github.com/Solvo37/vk-video-morphe-patches/releases/latest)

В Releases публикуется **только один APK**. Служебные отчёты, checksums и build metadata остаются в GitHub Actions и не засоряют список загрузок.

## Что изменено

- совместная установка с обычным `com.vkontakte.android`;
- bypass проверки подписи в `libvkcore.so`;
- отключение встроенного update prompt VK Видео;
- удаление найденных рекламных путей в обычном видео и VK Клипах;
- скрытие рекламных карточек/баннеров на Home, Discover и в профиле;
- блокировка ad pixel tracking;
- fail-closed проверки: если новая версия VK Видео несовместима с патчами, APK не публикуется.

Полный список активных патчей хранится в [patches-list.json](./patches-list.json).

## Установка

1. Если установлен официальный **VK Видео**, удалите его один раз — официальный APK и этот проект подписаны разными сертификатами.
2. Обычный **VK** удалять не нужно.
3. Установите APK из [Latest Release](https://github.com/Solvo37/vk-video-morphe-patches/releases/latest).

Все релизы проекта подписываются одним постоянным сертификатом, поэтому следующие сборки ставятся поверх предыдущих.

## Автообновление

Workflow **VK Video auto build** каждые 6 часов проверяет RuStore, Google Play и APKPure, валидирует package/certificate и выбирает самый новый подтверждённый `versionCode`.

Схема версий Releases отделена от Android `versionName`:

- текущий стабильный релиз: `1.163.6`;
- следующий rebuild этой же Android-версии: `1.163.7`;
- новая Android-версия 1.164 начнётся с `1.164.0`.

Каждый Release immutable: существующий APK не перезаписывается. Это важно для корректной работы клиентов обновлений и кэша GitHub asset IDs.

### Obtainium

Repository URL:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

APK asset filter:

```text
^VK-Video-.*-patched\.apk$
```

Release title filter при необходимости:

```text
^VK Video
```

## Для разработки

Сборка Morphe bundle:

```bash
gradle :patches:buildAndroid
```

Ключевые файлы:

- `patches/` — исходники патчей;
- `ci/` — проверки совместимости и выбор upstream;
- `.github/workflows/auto-update.yml` — автоматическая сборка и публикация;
- `CHANGELOG.md` — история изменений.

## Важно

Проект не связан с VK, VK Видео, Morphe или Obtainium и не одобрен ими. Репозиторий не содержит исходный код VK Видео.

Код проекта: [GPL-3.0](./LICENSE).
