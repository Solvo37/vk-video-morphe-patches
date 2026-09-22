# Upstream и автоматические обновления

## Приоритет источников

`VK Video auto build` проверяет источники в следующем порядке:

1. **RuStore** — основной источник;
2. **Google Play через gplaydl** — fallback;
3. **APKPure через apkeep** — последний fallback.

Источники могут отдавать разные rollout-версии. Поэтому сам факт успешного скачивания ещё не означает, что найден самый новый APK или что версия поддерживается.

## Проверка кандидата

Перед патчингом обязательны:

1. package = `com.vk.vkvideo`;
2. читаемые `versionName` и `versionCode`;
3. оригинальный SHA-256 сертификата VK:
   `057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32`;
4. отсутствие downgrade ниже baseline;
5. успешное совпадение всех bytecode fingerprints;
6. успешное совпадение native pattern для `libvkcore.so`.

Если любой gate не проходит, APK release не создаётся.

## Текущая baseline

```text
VK Видео 1.163
versionCode 51920
```

Эта версия реально проверена на устройстве в полной конфигурации патчей.

## Обязательные compatibility patches

Для проектной переподписанной сборки включаются:

- Fix install conflict with stock VK
- Bypass native signature check

Native patch сейчас предназначен для:

```text
lib/arm64-v8a/libvkcore.so
```

Он использует fail-closed подход: ожидаемый бинарный pattern должен встретиться ровно один раз. Для новой версии изменение native-кода требует повторной проверки.

## Пользовательские патчи

- Disable in-app update
- Remove video ads
- Hide promoted banner content
- Disable ad pixel tracking

Если fingerprint одного из обязательных патчей перестаёт совпадать, workflow открывает/обновляет compatibility issue и не публикует новый APK.

## Split APK

Google Play и некоторые другие источники могут возвращать base APK плюс configuration splits.

Pipeline:

1. находит и проверяет base APK;
2. патчит base через Morphe;
3. сохраняет нужные splits;
4. при необходимости объединяет их через APKEditor;
5. выполняет `zipalign`;
6. подписывает universal APK project key.

## Signing

Финальный APK подписывается постоянным project key:

- v1: disabled
- v2: disabled
- v3: enabled
- v4: disabled

После подписи запускаются `zipalign -c` и `apksigner verify`.

## Расписание

Проверка upstream запускается по расписанию и вручную. Если доступная версия уже имеет release, новый APK без причины не публикуется. Для диагностической пересборки предусмотрен `force_rebuild`.
