package dev.solvo37.vkvideopatches

import app.morphe.patcher.patch.resourcePatch
import dev.solvo37.vkvideopatches.Constants.VK_VIDEO
import org.w3c.dom.Element

private val isolatedPermissionNames = mapOf(
    "com.vkontakte.android.permission.ACCESS_DATA" to
        "com.vk.vkvideo.morphe.permission.ACCESS_DATA",
    "com.vkontakte.android.permission.APP_REDIRECT" to
        "com.vk.vkvideo.morphe.permission.APP_REDIRECT",
)

/**
 * Diagnostic coexistence patch.
 *
 * Instead of deleting VK's duplicate signature-permission declarations, rename every
 * manifest reference to private names owned by the re-signed VK Video APK. This keeps
 * the app's own providers/receivers protected by a permission it can actually hold,
 * while avoiding INSTALL_FAILED_DUPLICATE_PERMISSION next to stock VK.
 */
@Suppress("unused")
val isolateVkSignaturePermissionsPatch = resourcePatch(
    name = "Experimental isolate VK signature permissions",
    description = "Diagnostic: renames VK signature permissions instead of deleting them.",
    default = false,
) {
    compatibleWith(VK_VIDEO)

    execute {
        document("AndroidManifest.xml").use { document ->
            val elements = document.getElementsByTagName("*")

            for (index in 0 until elements.length) {
                val element = elements.item(index) as? Element ?: continue
                val attributes = element.attributes

                for (attributeIndex in 0 until attributes.length) {
                    val attribute = attributes.item(attributeIndex)
                    val replacement = isolatedPermissionNames[attribute.nodeValue] ?: continue
                    attribute.nodeValue = replacement
                }
            }
        }
    }
}
