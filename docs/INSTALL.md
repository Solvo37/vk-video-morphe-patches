# Установка

## Готовый APK

Готовые сборки находятся в [GitHub Releases](https://github.com/Solvo37/vk-video-morphe-patches/releases).

Нужный asset имеет имя:

```text
VK-Video-<version>-patched.apk
```

Для текущей проверенной версии:

```text
VK-Video-1.163-patched.apk
```

## Первая установка

Проект подписывает APK собственным постоянным Android-ключом. Поэтому проектный APK нельзя установить обновлением поверх официального VK Видео с подписью VK.

Порядок первой установки:

1. удалить только официальный **VK Видео** (`com.vk.vkvideo`);
2. обычный **VK** (`com.vkontakte.android`) можно оставить;
3. установить `VK-Video-1.163-patched.apk`;
4. разрешить установку APK из используемого браузера/файлового менеджера, если Android попросит.

После этого следующие релизы проекта, подписанные тем же ключом, смогут ставиться поверх предыдущего проектного APK без удаления данных приложения.

## Obtainium

Добавьте:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Рекомендуемые фильтры:

```text
Release title filter:
^VK Video

APK asset filter:
^VK-Video-.*-patched\.apk$
```

Фильтр release title нужен потому, что в этом же репозитории публикуются отдельные Morphe bundle-релизы вида `patches-v0.1.x`.

После добавления записи Obtainium должен видеть APK-релизы приложения, например `VK Video 1.163 patched`.

## Обновления

Не удаляйте проектный VK Видео перед обычным обновлением: новый APK должен ставиться поверх старого, если оба подписаны постоянным release key проекта.

Если Android пишет о несовместимой подписи, сначала проверьте сертификат APK. Не переустанавливайте приложение вслепую, если отпечаток отличается от опубликованного ниже.

## Проверка подписи

Ожидаемый сертификат проектных APK:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

Проверка через Android build-tools:

```bash
apksigner verify --verbose --print-certs VK-Video-*-patched.apk
```

Текущий pipeline использует `zipalign` и APK Signature Scheme **v3 only**.

## Самостоятельный патч через Morphe

Репозиторий можно использовать как custom source:

```text
https://github.com/Solvo37/vk-video-morphe-patches
```

Обязательные патчи для переподписанного VK Видео:

- **Bypass native signature check** — без него 1.163 завершается на старте;
- **Fix install conflict with stock VK** — нужен для нормальной совместной установки с обычным VK.

Дополнительные патчи:

- Disable in-app update
- Remove video ads
- Hide promoted banner content
- Disable ad pixel tracking

Native bypass текущей версии рассчитан на ARM64.
