# Roadmap 0.3 — Stability, Runtime Settings and Diagnostics

> Development branch only: `dev/0.3-stability`.
>
> Nothing in this roadmap is allowed to publish or replace the stable APK automatically.
> Stable `main` and the current public release remain untouched until explicit promotion after real-device testing.

## P0 — Authorization / login compatibility

### Problem report

Users of modified VK Video builds report that login via e-mail can return:

```text
too many request
```

while login via phone number may succeed.

At this point this is an **unconfirmed compatibility issue**. Do not patch authentication blindly.

### Test matrix

Test the same account/device/network where possible:

| Build | Stock VK installed | Login by phone | Login by e-mail |
|---|---:|---:|---:|
| Official VK Video | yes | test | test |
| Official VK Video | no | test | test |
| Stable patched build | yes | test | test |
| Stable patched build | no | test | test |
| 0.3 dev candidate | yes | test | test |

Also repeat after clearing VK Video app data, without repeatedly requesting codes in a short interval.

### Diagnostics

Collect without exposing credentials, tokens or SMS/e-mail codes:

- Android `logcat` around the failed auth attempt;
- HTTP status/error family if visible in application logs;
- whether the failure occurs before or after a verification-code request;
- whether the same account succeeds immediately in official VK Video;
- whether changing only phone/e-mail changes the result;
- `dumpsys package com.vk.vkvideo` permission state.

### Compatibility areas to verify

The patched APK is re-signed with the project key and the coexistence patch removes duplicate declarations for:

```text
com.vkontakte.android.permission.ACCESS_DATA
com.vkontakte.android.permission.APP_REDIRECT
```

Investigate whether either permission, a VK deep-link callback, or a signature-aware auth component affects the e-mail login path.

Do **not** restore conflicting permission declarations globally: that would break coexistence with stock VK.

### Exit criteria

One of:

1. e-mail login works in the patched candidate under the same conditions as stock;
2. a reproducible patch-specific cause is identified and fixed;
3. the behaviour is demonstrated to be server/account rate-limiting independent of the patch and documented.

---

## P1 — Runtime settings foundation

Goal: one APK with user-selectable behaviour. Do not require repatching to change Clips/autoplay options.

### Storage

Create a small project runtime layer backed by private `SharedPreferences`.

Rules:

- preference read failure => fall back to stock behaviour;
- compatibility/signature fixes are never runtime switches;
- defaults should preserve current VK behaviour unless a feature is intentionally safe-by-default.

### Patched Settings screen

Initial sections:

- **Видео**
- **Клипы**
- **Интерфейс**
- **Конфиденциальность**
- **Диагностика**

Developer-only options can live behind a hidden **Для разработчика** section.

### Always-on internal fixes

Never expose toggles for:

- stock VK coexistence;
- native signature bypass;
- package/signing compatibility;
- release signing.

Turning these off can make the APK uninstallable or unable to launch.

---

## P2 — Debug / diagnostics menu

Before changing playback behaviour, build observability.

Show:

- VK Video `versionName`;
- VK Video `versionCode`;
- patch bundle version;
- build/revision identifier;
- current runtime settings;
- project certificate short fingerprint;
- last diagnostics timestamp.

Actions:

- reset all patch settings;
- copy diagnostic report;
- enable verbose patch logging;
- later: export sanitized log report.

Default: verbose debug logging **OFF**.

No credentials, auth tokens, cookies, e-mail addresses or phone numbers in exported diagnostics.

---

## P3 — Clips controls

All optional and runtime-switchable.

### Show Clips

```text
Показывать Клипы: ON
```

OFF should hide/disable the Clips entry point without damaging normal video playback.

### Clips autoplay

```text
Автоплей Клипов: ON
```

OFF should keep the current clip loaded but paused until explicit user action.

### Pause on opening Clips

```text
Открывать Клипы на паузе: OFF
```

Keep this separate from global autoplay because some users may want autoplay after the first clip.

### Future Clips options

Candidates after stable hooks are found:

- remember mute state;
- disable automatic transition to next clip;
- optionally hide Clips recommendations outside the Clips screen.

---

## P4 — Normal video playback controls

Runtime options:

```text
Автоплей видео: ON
Автоплей следующего видео: ON
```

Potential later options:

- autoplay only on Wi-Fi;
- start muted;
- remember player speed;
- remember last selected quality where VK already exposes that capability.

Do not implement options by forcing undocumented server-side premium/account entitlements.

---

## P5 — UI cleanup

Runtime switches where technically safe:

- hide/show promotional cards;
- hide/show selected navigation entries;
- simplify Home feed;
- choose startup section if a stable navigation hook exists;
- hide subscription/promotional UI through normal feature gates where possible.

Prefer native VK feature gates over early-return hacks in RecyclerView/menu construction.

---

## P6 — Privacy controls

Separate privacy from ad removal.

Candidates:

- disable dedicated ad pixel tracking;
- identify optional analytics/telemetry paths;
- expose only controls that can be disabled without breaking auth, playback or crash reporting needed for stability.

Each privacy hook needs a documented scope. Avoid broad network blocking.

---

## P7 — Safer fingerprints

Reduce dependency on R8 names such as:

```text
Lyo0/g;
Lee1/j;
Lr11/d;
Ln33/t;
Lxo/a;
```

Migration priority:

1. semantic method calls;
2. stable DTO / enum types;
3. distinctive strings;
4. parameter + return type combinations;
5. R8 class/method names only as a last resort.

Every migrated fingerprint must remain fail-closed if matching is ambiguous.

---

## P8 — Compatibility report for new VK versions

When VK Video 1.164+ appears, generate a machine-readable and human-readable report:

| Area | Result |
|---|---|
| Package/certificate | PASS/FAIL |
| Install coexistence | PASS/FAIL |
| Native signature bypass | PASS/FAIL |
| Video ads | PASS/FAIL |
| Clips ads | PASS/FAIL |
| Midroll | PASS/FAIL |
| Runtime settings hooks | PASS/FAIL |
| Auth-sensitive hooks | PASS/FAIL |

On failure, include the missing/ambiguous fingerprint name and stop publication.

---

## P9 — Candidate APK pipeline

Patch changes must not go directly to stable.

Flow:

```text
dev branch
  -> CI
  -> unsigned/static checks
  -> signed candidate APK
  -> GitHub Actions artifact only
  -> real-device test
  -> explicit promotion decision
  -> main/stable release
```

Candidate builds must never use the normal stable Release publishing step.

---

## P10 — ADB smoke tests

Create a repeatable device test script.

Minimum scenario:

1. install/update candidate;
2. cold launch;
3. Home screen;
4. Profile / My screen;
5. Clips screen;
6. play a normal video;
7. seek through video;
8. wait through likely midroll points;
9. return to Home;
10. inspect process survival and `logcat`.

Fail on obvious:

- `FATAL EXCEPTION`;
- `VerifyError`;
- repeated process death;
- Application startup failure.

Authorization testing remains a separate manual test because credentials must not enter CI logs.

---

## Suggested 0.3 milestones

### 0.3.0-alpha1

- auth test matrix + diagnostics notes;
- safe replacement for profile ad-free promo hook;
- runtime settings storage;
- basic Patched Settings screen;
- debug/version information;
- reset settings;
- no Clips/autoplay behaviour changes yet.

### 0.3.0-alpha2

- Show Clips toggle;
- Clips autoplay toggle;
- pause-on-open toggle;
- real-device tests.

### 0.3.0-alpha3

- normal video autoplay;
- autoplay next video;
- diagnostics export;
- first ADB smoke-test script.

### 0.3.0-beta

- semantic fingerprint migration for the most fragile hooks;
- compatibility report;
- no known auth regression;
- candidate APK tested on real ARM64 device.

### 0.3.0 stable candidate

Only after explicit approval and a clean real-device test should selected changes be considered for `main`.
