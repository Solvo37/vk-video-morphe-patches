package dev.solvo37.vkvideopatches

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import dev.solvo37.vkvideopatches.Constants.VK_VIDEO
import org.w3c.dom.Element

private const val RUNTIME_SETTINGS_CLASS =
    "Ldev/solvo37/vkvideopatches/extension/RuntimeSettings;"
private const val SETTINGS_ACTIVITY =
    "dev.solvo37.vkvideopatches.extension.PatchedSettingsActivity"
private const val SETTINGS_ACTIVITY_DESCRIPTOR =
    "Ldev/solvo37/vkvideopatches/extension/PatchedSettingsActivity;"
private const val PROFILE_MENU_ITEM_DATA =
    "Lcom/vk/video/screens/profile/adapter/holder/i\$a;"
private const val PROFILE_MENU_ITEM_TYPE =
    "Lcom/vk/video/screens/profile/adapter/ProfileMenuItemType;"
private const val PROFILE_MENU_WRAPPER =
    "Lwx6/e\$a;"
private const val DEBUG_LAMBDA =
    "Ln10/g;"
private const val DEBUG_TABS_BUILDER =
    "Lcom/vk/video/screens/debug/VkVideoDebugTabsFragment\$b;"

private val runtimeSettingsManifestPatch = resourcePatch {
    compatibleWith(VK_VIDEO)

    execute {
        document("AndroidManifest.xml").use { document ->
            val applications = document.getElementsByTagName("application")
            check(applications.length == 1) {
                "Expected exactly one <application> node"
            }

            val application = applications.item(0) as Element
            val activities = document.getElementsByTagName("activity")
            val alreadyPresent = (0 until activities.length).any { index ->
                (activities.item(index) as? Element)
                    ?.getAttribute("android:name") == SETTINGS_ACTIVITY
            }

            if (!alreadyPresent) {
                val activity = document.createElement("activity")
                activity.setAttribute("android:name", SETTINGS_ACTIVITY)
                activity.setAttribute("android:exported", "false")
                activity.setAttribute("android:label", "VK Video Patched")
                application.appendChild(activity)
            }
        }
    }
}

private object VkVideoApplicationOnCreateFingerprint : Fingerprint(
    definingClass = "Lcom/vk/video/app/VkVideoApplication;",
    name = "onCreate",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = emptyList()
)

/**
 * VK already has a hidden developer/version row in the "My" profile menu.
 * It is a much safer integration point than inventing a new RecyclerView view type.
 *
 * In 1.163 the method:
 * - belongs to ProfileMenuDataProvider.kt;
 * - receives ArrayList;
 * - reads BuildInfo versionName + versionCode;
 * - creates ITEM_WITH_ICON.
 *
 * We replace the method body with our own ITEM_WITH_ICON row.
 */
private object ProfileDeveloperRowFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = listOf("Ljava/util/ArrayList;"),
    custom = { method, classDef ->
        classDef.sourceFile == "ProfileMenuDataProvider.kt" &&
            method.implementation?.instructions?.any { instruction ->
                (instruction as? ReferenceInstruction)?.reference?.toString() ==
                    "Lcom/vk/core/apps/BuildInfo;->e:Ljava/lang/String;"
            } == true &&
            method.implementation?.instructions?.any { instruction ->
                (instruction as? ReferenceInstruction)?.reference?.toString() ==
                    "Lcom/vk/core/apps/BuildInfo;->f:I"
            } == true
    }
)

/**
 * The hidden profile developer row already uses a synthetic click lambda whose
 * case opens VkVideoDebugTabsFragment. Hook that semantic branch rather than a
 * switch case number: the R8 class name may change, but the debug fragment type is stable.
 */
private object ProfileDeveloperClickFingerprint : Fingerprint(
    name = "invoke",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Ljava/lang/Object;"),
    custom = { method, _ ->
        method.implementation?.instructions?.any { instruction ->
            (instruction as? ReferenceInstruction)?.reference?.toString() == DEBUG_TABS_BUILDER
        } == true
    }
)

@Suppress("unused")
val runtimeSettingsFoundationPatch = bytecodePatch(
    name = "Runtime settings foundation",
    description = "Adds runtime settings storage and an internal VK Video Patched entry to the profile My screen.",
    default = false
) {
    compatibleWith(VK_VIDEO)
    dependsOn(runtimeSettingsManifestPatch)
    extendWith("extensions/vk-video.mpe")

    execute {
        VkVideoApplicationOnCreateFingerprint.method.addInstructions(
            0,
            """
                invoke-static/range { p0 .. p0 }, $RUNTIME_SETTINGS_CLASS->initialize(Landroid/content/Context;)V
            """
        )

        ProfileDeveloperRowFingerprint.method.apply {
            check(implementation!!.registerCount >= 6) {
                "Profile developer row has insufficient registers"
            }

            // Reuse VK's own ITEM_WITH_ICON holder and its already-present hidden
            // debug click lambda (case 15). The click branch is redirected below.
            addInstructions(
                0,
                """
                    new-instance v0, $PROFILE_MENU_ITEM_DATA

                    new-instance v1, $DEBUG_LAMBDA
                    const/16 v2, 0xf
                    invoke-direct { v1, v2 }, $DEBUG_LAMBDA-><init>(I)V

                    const/4 v2, 0x1
                    const v3, 0x7f081bd5
                    const-string v4, "VK Video Patched"

                    invoke-direct { v0, v3, v4, v1, v2 }, $PROFILE_MENU_ITEM_DATA-><init>(ILjava/lang/String;Lkotlin/jvm/functions/Function1;Z)V

                    sget-object v1, $PROFILE_MENU_ITEM_TYPE->ITEM_WITH_ICON:$PROFILE_MENU_ITEM_TYPE
                    invoke-virtual { v1 }, Ljava/lang/Enum;->ordinal()I
                    move-result v1

                    new-instance v2, $PROFILE_MENU_WRAPPER
                    invoke-direct { v2, v1, v0 }, $PROFILE_MENU_WRAPPER-><init>(ILjava/lang/Object;)V

                    invoke-virtual { p0, v2 }, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z
                    return-void
                """
            )
        }

        ProfileDeveloperClickFingerprint.method.apply {
            val redirectIndex = implementation!!.instructions.indexOfFirst { instruction ->
                (instruction as? ReferenceInstruction)?.reference?.toString() == DEBUG_TABS_BUILDER
            }
            check(redirectIndex >= 0) {
                "VK Video debug profile click branch not found"
            }

            addInstructions(
                redirectIndex,
                """
                    check-cast p1, Landroid/content/Context;
                    invoke-static { p1 }, $SETTINGS_ACTIVITY_DESCRIPTOR->open(Landroid/content/Context;)V
                    const/4 v0, 0x0
                    return-object v0
                """
            )
        }
    }
}
