# VK Video Morphe Patches

[Русский](./README.md) · [English](./README_EN.md)

[![CI](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/ci.yml)
[![VK Video upstream](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/auto-update.yml)
[![Publish patches](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/release-patches.yml/badge.svg)](https://github.com/Solvo37/vk-video-morphe-patches/actions/workflows/release-patches.yml)
[![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](./LICENSE)

Публичные патчи **Morphe** для Android-приложения **VK Видео** (`com.vk.vkvideo`).\n\n> ⚠️ Текущий APK-релиз 1.163 временно считается тестовым: предыдущая FULL-сборка может падать при запуске. Идёт проверка STRIP_FAST-сборки.

### Статус готового APK

Публичный Morphe `.mpp` уже опубликован. **Готовый APK для Obtainium появится только после публикации первого release asset вида `VK-Video-<version>-patched.apk`.** До этого Obtainium закономерно показывает «не удалось найти подходящий выпуск».



Проект содержит четыре пользовательских патча и один обязательный compatibility-fix:

| Патч | Что делает |
|---|---|
| **Disable in-app update** | отключает встроенную проверку/предложение обновить VK Видео |
| **Remove video ads** | отключает клиентские instream / overlay / motion ad-фичи плеера |
| **Hide promoted banner content** | скрывает рекламный баннер в Discover |
| **Disable ad pixel tracking** | отключает отдельный рекламный pixel tracker, не выключая авторизацию и общую рекомендательную аналитику |

> **Текущая подтверждённая версия:** VK Видео **1.163 (51920)**.  
> Новые версии сначала проходят реальный автоматический patch-test. Если fingerprint сломался, релиз не публикуется.

## Быстрый старт

### Вариант 1 — готовый APK + Obtainium

Если в [Releases](https://github.com/Solvo37/vk-video-morphe-patches/releases) есть файл вида:

```text
VK-Video-1.xxx-patched.apk
```

его можно установить напрямую и дальше получать обновления через **Obtainium**.

Для Obtainium:

- URL: `https://github.com/Solvo37/vk-video-morphe-patches`
- фильтр названия релиза: `^VK Video`
- фильтр APK: `^VK-Video-.*-patched\.apk$`

Подробно: [docs/INSTALL.md](./docs/INSTALL.md).

> Первый patched APK нельзя поставить обновлением поверх официального VK APK: подпись другая. Официальный VK Видео нужно удалить один раз, установить patched build, а дальше обновления этого проекта ставятся поверх него — при условии, что релизы подписаны одним и тем же ключом.

### Вариант 2 — патчить самому через Morphe

Добавьте этот репозиторий как custom source:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

В Releases публикуется `.mpp` bundle. Можно взять официальный APK VK Видео и применить нужные патчи самостоятельно.

## Как работает автоматика

GitHub Actions периодически:

1. проверяет доступную официальную сборку VK Видео;
2. сверяет package name, versionCode и сертификат исходного APK;
3. **не допускает downgrade ниже подтверждённой baseline-версии**;
4. собирает наш Morphe `.mpp`;
5. реально применяет все обязательные патчи к APK;
6. при несовместимости останавливается и создаёт issue;
7. при успешной проверке и наличии signing secrets собирает подписанный APK;
8. публикует APK + `.mpp` + upstream metadata в GitHub Release.

Автоматика теперь использует **RuStore как основной источник VK Видео**, **Google Play** как первый fallback и **APKPure** как второй fallback. Любой скачанный APK проверяется по package name и оригинальному сертификату VK; версия ниже baseline 1.163 никогда не публикуется.

## Проверка доверия

Официальный upstream APK принимается только с ожидаемым сертификатом VK:

```text
SHA-256: 057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32
```

Публичные APK этого проекта должны быть подписаны постоянным release-ключом проекта:

```text
SHA-256: D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
         14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

**Приватный signing key намеренно не публикуется.** Это не «жадность»: если приватный ключ выложить, любой сможет подписать вредоносный APK тем же сертификатом, и Android будет считать его допустимым обновлением.

Подробнее: [docs/TRUST.md](./docs/TRUST.md).

## Совместимость

- ✅ VK Видео **1.163 / 51920** — версия, на которой разрабатывались fingerprints.
- 🧪 будущие версии — допускаются только после автоматического patch-test.
- ❌ если обязательный fingerprint не найден — APK-релиз не создаётся.

## Google Play в CI

Самый простой способ — без ПК и Termux:

1. Установить **gplaydl Authenticator** на Android и добавить отдельный Google-аккаунт.
2. В Authenticator открыть **Link gplaydl** и получить одноразовый код.
3. В GitHub открыть **Actions → Link Google Play → Run workflow**, вставить код и запустить.
4. Workflow сам получает API key и сохраняет в репозитории только его AES-зашифрованную форму. Пароль шифрования берётся из уже существующего `ANDROID_KEY_PASSWORD` secret.

После этого `VK Video auto build` сможет использовать Google Play как fallback. Основной источник VK Видео — RuStore.

Альтернативно можно вручную создать repository secret `GPLAYDL_API_KEY`; он имеет приоритет над зашифрованным ключом.

## Для разработчиков

См. [CONTRIBUTING.md](./CONTRIBUTING.md) и [docs/reverse-engineering-1.163.md](./docs/reverse-engineering-1.163.md).

Сборка:

```bash
gradle :patches:buildAndroid
```

Результат:

```text
patches/build/libs/*.mpp
```

## Сообщить о проблеме

Используйте [Issues](https://github.com/Solvo37/vk-video-morphe-patches/issues):

- **Bug report** — приложение запускается, но патч работает неправильно;
- **New VK Video version** — вышла новая версия и fingerprint больше не подходит;
- **Feature request** — предложение нового патча.

Не прикладывайте к issue чужие APK-файлы. Достаточно версии, versionCode и логов Morphe.

## Правовой статус

Проект не связан с VK, VK Видео, Morphe или Obtainium и не одобрен ими.  
Репозиторий содержит **патчи и инструменты автоматизации**, а не исходный код VK Видео. Пользователь самостоятельно отвечает за соблюдение правил сервисов и законодательства своей юрисдикции.

## License

Код патчей распространяется по лицензии [GPL-3.0](./LICENSE).
