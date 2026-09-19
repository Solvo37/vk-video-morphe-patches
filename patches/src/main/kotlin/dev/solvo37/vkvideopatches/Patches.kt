package dev.solvo37.vkvideopatches

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import dev.solvo37.vkvideopatches.Constants.VK_VIDEO
import app.morphe.util.getFreeRegisterProvider

private const val VIDEO_FEATURES = "Lcom/vk/toggle/features/VideoFeatures;"
private const val EMPTY_DISPOSABLE = "Lio/reactivex/rxjava3/internal/disposables/EmptyDisposable;"

@Suppress("unused")
val disableInAppUpdatePatch = bytecodePatch(
    name = "Disable in-app update",
    description = "Disables the VK Video in-app update check and update prompt.",
    default = true
) {
    compatibleWith(VK_VIDEO)

    execute {
        InAppUpdateBootstrapFingerprint.method.addInstruction(0, "return-void")
    }
}

@Suppress("unused")
val removeVideoAdsPatch = bytecodePatch(
    name = "Remove video ads",
    description = "Disables instream video ads and player overlay/motion ad features.",
    default = true
) {
    compatibleWith(VK_VIDEO)

    execute {
        VideoFeaturesEnabledFingerprint.method.apply {
            // Do not hard-code v0 here. In a tiny instance method with no locals,
            // v0 aliases p0 (this), which corrupts the enum receiver and can make
            // every feature evaluate incorrectly during app startup.
            val tempRegister = getFreeRegisterProvider(0, 1).getFreeRegister()

            addInstructionsWithLabels(
                0,
                """
                    sget-object v$tempRegister, $VIDEO_FEATURES->VIDEO_INSTREAM_ADS_OFF:$VIDEO_FEATURES
                    if-eq p0, v$tempRegister, :force_enabled

                    sget-object v$tempRegister, $VIDEO_FEATURES->VIDEO_OVERLAY_AD:$VIDEO_FEATURES
                    if-eq p0, v$tempRegister, :force_disabled

                    sget-object v$tempRegister, $VIDEO_FEATURES->VIDEO_MOTION_AD_ENABLED:$VIDEO_FEATURES
                    if-eq p0, v$tempRegister, :force_disabled

                    goto :original

                    :force_enabled
                    const/4 v$tempRegister, 0x1
                    return v$tempRegister

                    :force_disabled
                    const/4 v$tempRegister, 0x0
                    return v$tempRegister
                """,
                ExternalLabel("original", getInstruction(0))
            )
        }
    }
}

@Suppress("unused")
val hidePromotedBannerPatch = bytecodePatch(
    name = "Hide promoted banner content",
    description = "Forces VideoDiscoverAdsDto.canShowAdBanner to false.",
    default = true
) {
    compatibleWith(VK_VIDEO)

    execute {
        DiscoverAdBannerFingerprint.method.addInstructions(
            0,
            """
                sget-object v0, Ljava/lang/Boolean;->FALSE:Ljava/lang/Boolean;
                return-object v0
            """
        )
    }
}

@Suppress("unused")
val disableAdPixelTrackingPatch = bytecodePatch(
    name = "Disable ad pixel tracking",
    description = "Stops PixelStatsTrackerImpl from sending individual and batch ad pixels.",
    default = true
) {
    compatibleWith(VK_VIDEO)

    execute {
        val returnEmptyDisposable = """
            sget-object v0, $EMPTY_DISPOSABLE->INSTANCE:$EMPTY_DISPOSABLE
            return-object v0
        """.trimIndent()

        PixelStatsSingleFingerprint.method.addInstructions(0, returnEmptyDisposable)
        PixelStatsBatchFingerprint.method.addInstructions(0, returnEmptyDisposable)
    }
}
