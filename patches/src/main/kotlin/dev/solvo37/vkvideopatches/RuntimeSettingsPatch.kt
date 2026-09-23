package dev.solvo37.vkvideopatches

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import com.android.tools.smali.dexlib2.AccessFlags
import dev.solvo37.vkvideopatches.Constants.VK_VIDEO
import org.w3c.dom.Element

private const val RUNTIME_SETTINGS_CLASS =
    "Ldev/solvo37/vkvideopatches/extension/RuntimeSettings;"
private const val SETTINGS_ACTIVITY =
    "dev.solvo37.vkvideopatches.extension.PatchedSettingsActivity"

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
                activity.setAttribute("android:exported", "true")
                activity.setAttribute("android:label", "VK Video Patched Settings")

                // Temporary alpha-only launcher entry. This avoids touching VK's
                // own navigation while the runtime settings screen is being validated.
                val intentFilter = document.createElement("intent-filter")

                val action = document.createElement("action")
                action.setAttribute("android:name", "android.intent.action.MAIN")
                intentFilter.appendChild(action)

                val category = document.createElement("category")
                category.setAttribute("android:name", "android.intent.category.LAUNCHER")
                intentFilter.appendChild(category)

                activity.appendChild(intentFilter)
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

@Suppress("unused")
val runtimeSettingsFoundationPatch = bytecodePatch(
    name = "Runtime settings foundation",
    description = "Adds the development runtime settings storage and Patched Settings screen.",
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
    }
}
