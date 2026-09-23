package dev.solvo37.vkvideopatches.extension;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Runtime settings for VK Video Patched.
 *
 * Preference reads deliberately fail open to stock VK behaviour if the extension
 * has not been initialised yet or Android storage is unavailable.
 */
public final class RuntimeSettings {
    private static final String PREFS_NAME = "vk_video_patched";

    public static final String KEY_SHOW_CLIPS = "show_clips";
    public static final String KEY_CLIPS_AUTOPLAY = "clips_autoplay";
    public static final String KEY_PAUSE_CLIPS_ON_OPEN = "pause_clips_on_open";
    public static final String KEY_VIDEO_AUTOPLAY = "video_autoplay";
    public static final String KEY_NEXT_VIDEO_AUTOPLAY = "next_video_autoplay";
    public static final String KEY_HIDE_PROMO_UI = "hide_promo_ui";
    public static final String KEY_DEBUG_LOGGING = "debug_logging";

    private static volatile Context appContext;

    private RuntimeSettings() {}

    public static void initialize(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    private static SharedPreferences preferences() {
        Context context = appContext;
        return context == null
                ? null
                : context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(String key, boolean stockFallback) {
        try {
            SharedPreferences prefs = preferences();
            return prefs == null ? stockFallback : prefs.getBoolean(key, stockFallback);
        } catch (Throwable ignored) {
            return stockFallback;
        }
    }

    public static void putBoolean(String key, boolean value) {
        try {
            SharedPreferences prefs = preferences();
            if (prefs != null) {
                prefs.edit().putBoolean(key, value).apply();
            }
        } catch (Throwable ignored) {
            // Settings are optional. Never crash VK Video because preference storage failed.
        }
    }

    public static boolean showClips() {
        return getBoolean(KEY_SHOW_CLIPS, true);
    }

    public static boolean clipsAutoplay() {
        return getBoolean(KEY_CLIPS_AUTOPLAY, true);
    }

    public static boolean pauseClipsOnOpen() {
        return getBoolean(KEY_PAUSE_CLIPS_ON_OPEN, false);
    }

    public static boolean videoAutoplay() {
        return getBoolean(KEY_VIDEO_AUTOPLAY, true);
    }

    public static boolean nextVideoAutoplay() {
        return getBoolean(KEY_NEXT_VIDEO_AUTOPLAY, true);
    }

    public static boolean hidePromoUi() {
        return getBoolean(KEY_HIDE_PROMO_UI, true);
    }

    public static boolean debugLogging() {
        return getBoolean(KEY_DEBUG_LOGGING, false);
    }

    public static void reset() {
        try {
            SharedPreferences prefs = preferences();
            if (prefs != null) {
                prefs.edit().clear().apply();
            }
        } catch (Throwable ignored) {
        }
    }

    public static Map<String, Boolean> snapshot() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        result.put(KEY_SHOW_CLIPS, showClips());
        result.put(KEY_CLIPS_AUTOPLAY, clipsAutoplay());
        result.put(KEY_PAUSE_CLIPS_ON_OPEN, pauseClipsOnOpen());
        result.put(KEY_VIDEO_AUTOPLAY, videoAutoplay());
        result.put(KEY_NEXT_VIDEO_AUTOPLAY, nextVideoAutoplay());
        result.put(KEY_HIDE_PROMO_UI, hidePromoUi());
        result.put(KEY_DEBUG_LOGGING, debugLogging());
        return result;
    }
}
