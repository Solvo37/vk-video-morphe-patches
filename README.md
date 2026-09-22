# VK Video Morphe Patches

[Русский](./README.md) · [English](./README_EN.md)

[![CI](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml)
[![VK Video auto build](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml)
[![Publish patches](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/release-patches.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/release-patches.yml)
[![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](./LICENSE)

Публичный набор патчей **Morphe** для Android-приложения **VK Видео** (`com.vk.vkvideo`) с готовыми подписанными APK-релизами для **Obtainium**.

## Текущий статус

- ✅ Проверенная версия приложения: **VK Видео 1.163 / versionCode 51920**
- ✅ Готовый APK: [Release 1.163](https://github.com/Solvo37/vk-video-morphe-patches/releases/tag/1.163)
- ✅ Текущий bundle патчей: **v0.2.0**
- ✅ Проверен реальный запуск переподписанного APK с установленным обычным VK
- ✅ Сборка: **Morphe STRIP_FAST → zipalign → APK Signature Scheme v3**
- ⚠️ Native bypass сейчас рассчитан на **ARM64 / arm64-v8a**

Готовый релиз `1.163` содержит уже проверенную рабочую комбинацию патчей. Старые экспериментальные сборки до native bypass могли закрываться сразу после запуска — текущий release этим не страдает.

## Что патчится

| Патч | Назначение | Тип |
|---|---|---|
| **Fix install conflict with stock VK** | позволяет переподписанному VK Видео устанавливаться рядом с обычным `com.vkontakte.android` | обязательный compatibility fix |
| **Bypass native signature check** | изменяет проверку в `libvkcore.so`, которая завершала переподписанное приложение через native exit | обязательный compatibility fix |
| **Disable in-app update** | отключает встроенную проверку и предложение обновить VK Видео | пользовательский |
| **Remove video ads** | отключает клиентские instream / overlay / motion ad-фичи плеера | пользовательский |
| **Hide promoted banner content** | выключает показ рекламного баннера в Discover | пользовательский |
| **Disable ad pixel tracking** | останавливает отдельный рекламный pixel tracker | пользовательский |

Патчи не обещают удалить рекламу или аналитику, полностью формируемые сервером. Они меняют только конкретные клиентские механизмы, найденные в APK.

## Установка

### Готовый APK

Откройте [Releases](https://github.com/Solvo37/vk-video-morphe-patches/releases) и скачайте:

```text
VK-Video-<version>-patched.apk
```

Первую установку нельзя делать поверх официального **VK Видео**, потому что проект использует собственный постоянный signing key. Официальный VK Видео нужно удалить один раз.

Обычный **VK** (`com.vkontakte.android`) удалять не нужно: compatibility fix сделан именно для совместной установки.

### Obtainium

Добавьте репозиторий:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Рекомендуемые фильтры:

```text
Release title:
^VK Video

APK asset:
^VK-Video-.*-patched\.apk$
```

Это отделяет APK-релизы приложения от отдельных релизов Morphe bundle вида `patches-v0.1.x`.

Подробности: [docs/INSTALL.md](./docs/INSTALL.md).

## Самостоятельный патч через Morphe

Репозиторий можно добавить как custom source:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Bundle публикуется отдельным release:

```text
patches-v0.2.0
vk-video-morphe-patches-0.2.0.mpp
```

Для переподписанной сборки **Bypass native signature check** должен оставаться включённым. Без него VK Видео 1.163 завершает процесс на старте после проверки подписи.

## Автоматические обновления

Workflow `VK Video auto build` периодически опрашивает **все доступные upstream-источники** — RuStore, Google Play через gplaydl и APKPure через apkeep — проверяет каждый скачанный base APK и выбирает кандидат с **максимальным подтверждённым `versionCode`**. Если `versionCode` одинаковый, приоритет используется только как tie-breaker: RuStore → Google Play → APKPure.

Перед патчингом проверяются:

- package name `com.vk.vkvideo`;
- `versionName` и `versionCode`;
- оригинальный SHA-256 сертификата VK;
- отсутствие downgrade ниже подтверждённой baseline;
- применение всех обязательных fingerprints и native-паттерна.

Если новая версия несовместима, APK не публикуется: workflow останавливается и создаёт compatibility issue.

Split APK сначала объединяются в universal upstream, затем применяются Morphe `STRIP_FAST` и native patch. После статических compatibility gates итоговый APK проходит `zipalign` и подписывается постоянным ключом проекта только APK Signature Scheme v3. В app release публикуются APK, SHA-256, `.mpp`, `upstream.json`, build metadata и Morphe report.

Подробнее: [docs/UPSTREAM.md](./docs/UPSTREAM.md).

## Проверка подписи

Оригинальный upstream принимается только с сертификатом VK:

```text
057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32
```

APK этого проекта подписываются постоянным сертификатом:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

Проверка:

```bash
apksigner verify --verbose --print-certs VK-Video-*-patched.apk
```

Подробнее о модели доверия: [docs/TRUST.md](./docs/TRUST.md).

## Совместимость

| Компонент | Статус |
|---|---|
| VK Видео 1.163 / 51920 | ✅ проверено |
| Android ARM64 | ✅ текущая цель |
| Обычный VK установлен рядом | ✅ проверено |
| Будущие версии VK Видео | 🧪 только после автоматического compatibility test |
| Другие ABI | ⚠️ native bypass пока не заявлен |

R8-имена и native-код могут меняться между версиями. Поэтому версия не считается поддерживаемой только потому, что скачалась: все fingerprints должны реально примениться.

## Сборка bundle

Нужна Java 21.

```bash
gradle :patches:buildAndroid
```

Результат:

```text
patches/build/libs/*.mpp
```

Для разработки см. [CONTRIBUTING.md](./CONTRIBUTING.md) и [docs/reverse-engineering-1.163.md](./docs/reverse-engineering-1.163.md).

План до стабильной 1.0: [ROADMAP.md](./ROADMAP.md).

## Безопасность

Приватный signing key в репозитории не хранится. В GitHub Actions он доступен только через repository secrets. Если этот ключ будет потерян, существующие установки нельзя будет обновить новым ключом без переустановки; если ключ утечёт, доверять дальнейшим обновлениям с этим сертификатом будет нельзя.

См. [SECURITY.md](./SECURITY.md) и [docs/MAINTAINER_SIGNING.md](./docs/MAINTAINER_SIGNING.md).

## Disclaimer

Проект не связан с VK, VK Видео, Morphe или Obtainium и не одобрен ими. Репозиторий содержит патчи и инфраструктуру сборки, а не исходный код VK Видео.

Использование модифицированного клиента может зависеть от правил сервиса и законодательства вашей юрисдикции.

## License

Код проекта распространяется по [GPL-3.0](./LICENSE).
