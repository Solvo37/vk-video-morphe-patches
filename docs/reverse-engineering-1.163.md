# VK Video 1.163 reverse-engineering notes

Target APK:

- package: `com.vk.vkvideo`
- versionName: `1.163`
- versionCode: `51920`
- SHA-256: `61d8c2b0837704d2d197a35b75f5c07872927e77d8861b61ce78ba0c5626c1c8`
- VK signing certificate SHA-256: `057d974412032066f1b5edb1fdb550f71854189815c806b27c4d486fb4f1ef32`

## In-app update

`InAppUpdateImpl.kt` is R8-renamed to `Lt76/b;` in this build. Its
`a(Lcom/vk/video/screens/main/MainActivity;)V` method constructs
`Lcom/vk/update/core/a;` and registers the update lifecycle observer.
Returning immediately from this bootstrap method prevents the in-app update flow from starting.

## Video ads

The app contains a dedicated video feature enum:
`Lcom/vk/toggle/features/VideoFeatures;`.

Relevant fields:

- `VIDEO_INSTREAM_ADS_OFF`
- `VIDEO_OVERLAY_AD`
- `VIDEO_MOTION_AD_ENABLED`
- `VIDEO_NEW_INSTREAM_LOGIC`

The patch modifies only the first three values at the common boolean evaluator.

## Promoted/banner content

`Lcom/vk/api/generated/video/dto/VideoDiscoverAdsDto;` contains
`canShowAdBanner: java.lang.Boolean`. Its getter is forced to `Boolean.FALSE`.

## Ad tracking

`PixelStatsTrackerImpl.kt` is `Ltq/d;` in this build. Only its dedicated
individual/batch advertising pixel methods are short-circuited.

## Future versions

R8 names may move between releases. The scheduled GitHub workflow therefore does a real Morphe
patch attempt against every newly detected upstream APK. A release is not published if any required
fingerprint fails.
