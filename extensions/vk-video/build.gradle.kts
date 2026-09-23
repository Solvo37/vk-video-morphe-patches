import com.android.build.api.dsl.ApplicationExtension

extension {
    name = "extensions/vk-video.mpe"
}

configure<ApplicationExtension> {
    namespace = "dev.solvo37.vkvideopatches.extension"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
