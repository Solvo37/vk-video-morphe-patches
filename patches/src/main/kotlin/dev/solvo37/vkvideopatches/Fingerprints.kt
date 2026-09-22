package dev.solvo37.vkvideopatches

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

internal object VideoFeaturesEnabledFingerprint : Fingerprint(
    definingClass = "Lcom/vk/toggle/features/VideoFeatures;",
    name = "a",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipsFeaturesEnabledFingerprint : Fingerprint(
    definingClass = "Lcom/vk/toggle/features/ClipsFeatures;",
    name = "a",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)

internal object InAppUpdateBootstrapFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Lcom/vk/video/screens/main/MainActivity;"),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/vk/update/core/a;",
            name = "<init>"
        )
    )
)

internal object DiscoverAdBannerFingerprint : Fingerprint(
    definingClass = "Lcom/vk/api/generated/video/dto/VideoDiscoverAdsDto;",
    name = "b",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Boolean;",
    parameters = emptyList()
)

internal object PixelStatsSingleFingerprint : Fingerprint(
    definingClass = "Ltq/d;",
    name = "a",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Lio/reactivex/rxjava3/disposables/c;",
    parameters = listOf("Ljava/lang/String;"),
    filters = listOf(
        methodCall(
            definingClass = "Lio/reactivex/rxjava3/core/q;",
            name = "subscribe"
        )
    )
)

internal object PixelStatsBatchFingerprint : Fingerprint(
    definingClass = "Ltq/d;",
    name = "b",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Lio/reactivex/rxjava3/disposables/c;",
    parameters = listOf("Ljava/lang/Iterable;"),
    filters = listOf(
        methodCall(
            definingClass = "Lio/reactivex/rxjava3/core/q;",
            name = "subscribe"
        )
    )
)

// VK Video 1.163 Clips feature/config provider (R8 names).
internal object ClipAdsPromoProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "B",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipAdsStaticProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "C",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipAdsLabelProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "G",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipMarketAdProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "J",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipMarketAdChoicesProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "M",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipAdsVideoOwnerProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "O",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipYandexAdParamsProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "P",
    returnType = "Lwo0/k;",
    parameters = emptyList()
)

internal object ClipMarketAdHeaderClicksProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "Q",
    returnType = "Lcom/vk/clips/sdk/shared/viewer/experiments/models/ClipsMarketAdHeaderClickConfig;",
    parameters = emptyList()
)

internal object ClipFeedEndRewatchAdProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "R",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipSellerBannerCompanionProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "X",
    returnType = "Lcom/vk/clips/sdk/shared/viewer/experiments/models/ClipsBannerCompanionConfig;",
    parameters = emptyList()
)

internal object ClipAdsVideoProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "Y",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipAdsCarouselProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "k",
    returnType = "Z",
    parameters = emptyList()
)

internal object ClipBannerCompanionProviderFingerprint : Fingerprint(
    definingClass = "Lyo0/g;",
    name = "l",
    returnType = "Lcom/vk/clips/sdk/shared/viewer/experiments/models/ClipsBannerCompanionConfig;",
    parameters = emptyList()
)

internal object ClipVideoFileAdsFeaturesParamsFingerprint : Fingerprint(
    definingClass = "Lcom/vk/clips/viewer/impl/adapters/ClipVideoFileAdapter;",
    name = "A3",
    returnType = "Lcom/vk/clips/sdk/models/ads/SdkClipsAdsFeaturesParams;",
    parameters = emptyList()
)
