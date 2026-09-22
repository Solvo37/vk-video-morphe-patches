package dev.solvo37.vkvideopatches

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import dev.solvo37.vkvideopatches.Constants.VK_VIDEO

private const val VIDEO_FEATURES = "Lcom/vk/toggle/features/VideoFeatures;"
private const val CLIPS_FEATURES = "Lcom/vk/toggle/features/ClipsFeatures;"
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
    description = "Disables player ad feature gates and strips server-provided instream/mobile/sport/banner ad payloads.",
    default = true
) {
    compatibleWith(VK_VIDEO)

    execute {
        VideoFeaturesEnabledFingerprint.method.apply {
            // VK Video 1.163 has one local register (v0) plus p0.
            // Refuse to patch a future build if that invariant changes.
            check(implementation!!.registerCount >= 2) {
                "VideoFeatures.a() has no free local register; fingerprint needs updating"
            }

            addInstructionsWithLabels(
                0,
                """
                    sget-object v0, $VIDEO_FEATURES->VIDEO_INSTREAM_ADS_OFF:$VIDEO_FEATURES
                    if-eq p0, v0, :force_enabled

                    sget-object v0, $VIDEO_FEATURES->VIDEO_OVERLAY_AD:$VIDEO_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $VIDEO_FEATURES->VIDEO_MOTION_AD_ENABLED:$VIDEO_FEATURES
                    if-eq p0, v0, :force_disabled

                    goto :original

                    :force_enabled
                    const/4 v0, 0x1
                    return v0

                    :force_disabled
                    const/4 v0, 0x0
                    return v0
                """,
                ExternalLabel("original", getInstruction(0))
            )
        }

        // VK Video 1.163 also has a separate server-driven ad path. Null every
        // ad payload as it enters the generated API model so account/login
        // configuration cannot reactivate preroll/midroll or related payloads.
        VideoGetAdsResponseConstructorFingerprint.method.addInstructions(
            0,
            """
                const/4 p1, 0x0
                const/4 p2, 0x0
                const/4 p3, 0x0
                const/4 p4, 0x0
            """
        )

        // Defense in depth for direct instream construction: remove all
        // preroll, midroll and postroll section lists.
        VideoInstreamSectionsConstructorFingerprint.method.addInstructions(
            0,
            """
                const/4 p1, 0x0
                const/4 p2, 0x0
                const/4 p3, 0x0
            """
        )
    }
}

@Suppress("unused")
val removeClipAdsPatch = bytecodePatch(
    name = "Remove clip ads",
    description = "Disables VK Clips ad feature gates, ad configs, and SDK ad feature parameters.",
    default = true
) {
    compatibleWith(VK_VIDEO)

    execute {
        // Clips have a separate ad stack from the regular video player. Disable
        // direct ClipsFeatures callers first, then the concrete config provider
        // used by the feed mapper / Clips SDK in VK Video 1.163.
        ClipsFeaturesEnabledFingerprint.method.apply {
            check(implementation!!.registerCount >= 2) {
                "ClipsFeatures.a() has no free local register; fingerprint needs updating"
            }

            addInstructionsWithLabels(
                0,
                """
                    sget-object v0, $CLIPS_FEATURES->CLIPS_YANDEX_AD_PARAMS:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_MARKET_AD:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_MARKET_AD_CHOICES:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_AD_BANNER_COMPANION:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_AD_BANNER_COMPANION_FOR_SELLERS:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_ADS_SDK_VIDEO:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_ADS_SDK_STATIC_AD:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_ADS_SDK_CAROUSEL:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_ADS_SDK_PROMO:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_ADS_SDK_VIDEO_OWNER:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_ADS_SDK_LABEL:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->FEED_END_REWATCH_NEW_AD:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    sget-object v0, $CLIPS_FEATURES->CLIPS_MARKET_AD_HEADER_CLICKS:$CLIPS_FEATURES
                    if-eq p0, v0, :force_disabled

                    goto :original

                    :force_disabled
                    const/4 v0, 0x0
                    return v0
                """,
                ExternalLabel("original", getInstruction(0))
            )
        }

        val returnFalse = """
            const/4 v0, 0x0
            return v0
        """.trimIndent()

        listOf(
            ClipAdsPromoProviderFingerprint.method,
            ClipAdsStaticProviderFingerprint.method,
            ClipAdsLabelProviderFingerprint.method,
            ClipMarketAdProviderFingerprint.method,
            ClipMarketAdChoicesProviderFingerprint.method,
            ClipAdsVideoOwnerProviderFingerprint.method,
            ClipFeedEndRewatchAdProviderFingerprint.method,
            ClipAdsVideoProviderFingerprint.method,
            ClipAdsCarouselProviderFingerprint.method,
        ).forEach { method ->
            check(method.implementation!!.registerCount >= 2) {
                "Clips ad provider method ${method.name} has no free local register"
            }
            method.addInstructions(0, returnFalse)
        }

        ClipYandexAdParamsProviderFingerprint.method.addInstructions(
            0,
            """
                sget-object v0, Lwo0/k;->b:Lwo0/k;
                return-object v0
            """
        )

        ClipMarketAdHeaderClicksProviderFingerprint.method.addInstructions(
            0,
            """
                sget-object v0, Lcom/vk/clips/sdk/shared/viewer/experiments/models/ClipsMarketAdHeaderClickConfig;->c:Lcom/vk/clips/sdk/shared/viewer/experiments/models/ClipsMarketAdHeaderClickConfig;
                return-object v0
            """
        )

        val returnDisabledBannerCompanion = """
            sget-object v0, Lcom/vk/clips/sdk/shared/viewer/experiments/models/ClipsBannerCompanionConfig;->d:Lcom/vk/clips/sdk/shared/viewer/experiments/models/ClipsBannerCompanionConfig;
            return-object v0
        """.trimIndent()

        ClipSellerBannerCompanionProviderFingerprint.method.addInstructions(
            0,
            returnDisabledBannerCompanion
        )
        ClipBannerCompanionProviderFingerprint.method.addInstructions(
            0,
            returnDisabledBannerCompanion
        )

        // ClipVideoFileAdapter already treats null as "no server ad feature params".
        ClipVideoFileAdsFeaturesParamsFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return-object v0
            """
        )
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
