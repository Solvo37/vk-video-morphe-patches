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
