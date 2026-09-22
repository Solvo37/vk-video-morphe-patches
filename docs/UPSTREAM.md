# Upstream и автоматические обновления

## Источники

`VK Video auto build` работает с тремя upstream:

- **RuStore**
- **Google Play через gplaydl**
- **APKPure через apkeep**

Workflow не доверяет одному источнику как «самому свежему». Он скачивает все доступные кандидаты, проверяет base APK каждого источника по package и оригинальному сертификату VK и выбирает сборку с **максимальным `versionCode`**.

Если два источника отдают один и тот же `versionCode`, tie-breaker: RuStore → Google Play → APKPure.

Это важно из-за staged rollout: разные магазины могут одновременно показывать разные версии.

## Baseline

Минимальная подтверждённая baseline хранится в `ci/baseline.json`.

Текущее значение:

```text
VK Видео 1.163
versionCode 51920
```

Перед каждым запуском workflow дополнительно пытается взять baseline из metadata последнего стабильного app release. Если release metadata отсутствует, используется репозиторный baseline.

Downgrade ниже baseline никогда не публикуется.

## Проверка upstream

Кандидат допускается только если:

1. package = `com.vk.vkvideo`;
2. APK является base, а не configuration split;
3. читаются `versionName` и `versionCode`;
4. SHA-256 сертификата совпадает с оригинальным сертификатом VK:

```text
057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32
```

Все прошедшие кандидаты записываются в `upstream.json`, включая source, versionCode, SHA-256 APK и сертификат.

## Split APK

Если выбранный источник возвращает base + configuration splits, они **сначала** объединяются в universal upstream через APKEditor.

Это сделано до Morphe, потому что обязательный native patch находится в ARM64 split у некоторых Google Play сборок. После merge Morphe видит и bytecode, и `lib/arm64-v8a/libvkcore.so` в одном APK.

## Compatibility gates

В universal upstream применяются:

- Fix install conflict with stock VK
- Bypass native signature check
- Disable in-app update
- Remove video ads
- Hide promoted banner content
- Disable ad pixel tracking

Morphe запускается в режиме `STRIP_FAST`.

После патчинга workflow проверяет:

- `patch-result.json`: success, нет failed patches, присутствуют все шесть обязательных patch names;
- manifest: VK Видео больше не объявляет конфликтующие signature permissions обычного VK;
- `VkVideoApplication` присутствует в multidex;
- `lib/arm64-v8a/libvkcore.so` присутствует;
- старый native signature pattern отсутствует;
- новый patched pattern встречается ровно один раз;
- нет JAR/v1 signature entries.

Любой failure создаёт/обновляет compatibility issue и останавливает release.

## Signing gate

После `zipalign` APK подписывается постоянным project key:

```text
v1 = false
v2 = false
v3 = true
v4 = false
```

Затем workflow повторно проверяет:

- `zipalign -c`;
- `apksigner verify`;
- project certificate SHA-256;
- package и versionName;
- статический native/multidex gate уже на точных подписанных байтах.

## Release tags

Обычно app release tag равен `versionName`, например `1.164`.

Если upstream выпускает другой APK с тем же `versionName`, но большим `versionCode`, используется отдельный tag:

```text
1.164-52345
```

Это не даёт новой сборке затереть другую сборку с тем же отображаемым номером версии.

## Release assets

Стабильный app release содержит:

- `VK-Video-<version>-patched.apk`;
- SHA-256;
- текущий Morphe `.mpp`;
- `upstream.json`;
- `build-metadata.json`;
- `patch-result.json`.

Standalone `patches-v*` releases помечаются как **prerelease**, чтобы они не занимали GitHub Latest вместо installable APK.

## Runtime smoke test

Статические gates ловят уже найденные классы ошибок: сломанный multidex, отсутствие Application class, неправильный native bypass, permission conflict, неправильную подпись и несовместимые fingerprints.

Полноценный launch-test на реальном ARM64 Android runtime остаётся отдельным milestone перед 1.0. GitHub-hosted Linux runner не считается эквивалентом реального Android ARM64-устройства.
