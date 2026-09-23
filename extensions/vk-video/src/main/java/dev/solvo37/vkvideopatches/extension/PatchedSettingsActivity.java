package dev.solvo37.vkvideopatches.extension;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Map;

/**
 * Runtime settings screen for VK Video Patched.
 *
 * The screen is opened from VK Video's own "My" profile menu. It intentionally
 * uses only Android framework widgets so it does not depend on VK UI internals.
 */
@SuppressWarnings("deprecation")
public final class PatchedSettingsActivity extends Activity {
    private static final String PATCH_VERSION = "0.3.0-alpha2";

    private int backgroundColor;
    private int surfaceColor;
    private int primaryTextColor;
    private int secondaryTextColor;

    public static void open(Context context) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent(context, PatchedSettingsActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RuntimeSettings.initialize(this);
        configurePalette();

        setTitle("VK Video Patched");
        getWindow().setStatusBarColor(backgroundColor);
        getWindow().setNavigationBarColor(backgroundColor);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(backgroundColor);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(backgroundColor);
        int pad = dp(20);
        root.setPadding(pad, pad, pad, dp(32));
        scroll.addView(root);

        TextView title = text("VK Video Patched", 26f, primaryTextColor);
        root.addView(title);

        TextView version = text(buildVersionText(), 14f, secondaryTextColor);
        version.setPadding(0, dp(6), 0, dp(18));
        root.addView(version);

        addSection(root, "Клипы");
        addSwitch(root, "Показывать Клипы", RuntimeSettings.KEY_SHOW_CLIPS, true);
        addSwitch(root, "Автоплей Клипов", RuntimeSettings.KEY_CLIPS_AUTOPLAY, true);
        addSwitch(root, "Открывать Клипы на паузе", RuntimeSettings.KEY_PAUSE_CLIPS_ON_OPEN, false);

        addSection(root, "Видео");
        addSwitch(root, "Автоплей видео", RuntimeSettings.KEY_VIDEO_AUTOPLAY, true);
        addSwitch(root, "Автоплей следующего видео", RuntimeSettings.KEY_NEXT_VIDEO_AUTOPLAY, true);

        addSection(root, "Интерфейс");
        addSwitch(root, "Скрывать промо-интерфейс", RuntimeSettings.KEY_HIDE_PROMO_UI, true);

        addSection(root, "Для разработчика");
        addSwitch(root, "Debug logging", RuntimeSettings.KEY_DEBUG_LOGGING, false);

        Button diagnostics = button("Показать диагностику");
        diagnostics.setOnClickListener(v -> showDiagnostics());
        root.addView(diagnostics);

        Button reset = button("Сбросить настройки патча");
        reset.setOnClickListener(v -> {
            RuntimeSettings.reset();
            recreate();
        });
        LinearLayout.LayoutParams resetParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        resetParams.topMargin = dp(10);
        root.addView(reset, resetParams);

        setContentView(scroll);
    }

    private void configurePalette() {
        boolean dark = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (dark) {
            backgroundColor = Color.rgb(18, 18, 18);
            surfaceColor = Color.rgb(42, 42, 46);
            primaryTextColor = Color.rgb(245, 245, 247);
            secondaryTextColor = Color.rgb(180, 180, 186);
        } else {
            backgroundColor = Color.rgb(250, 250, 252);
            surfaceColor = Color.rgb(235, 235, 240);
            primaryTextColor = Color.rgb(25, 25, 28);
            secondaryTextColor = Color.rgb(95, 95, 102);
        }
    }

    private void addSection(LinearLayout root, String label) {
        TextView view = text(label, 19f, primaryTextColor);
        view.setPadding(0, dp(20), 0, dp(6));
        root.addView(view);
    }

    private void addSwitch(
            LinearLayout root,
            String label,
            String key,
            boolean stockDefault
    ) {
        Switch toggle = new Switch(this);
        toggle.setText(label);
        toggle.setTextColor(primaryTextColor);
        toggle.setTextSize(15f);
        toggle.setChecked(RuntimeSettings.getBoolean(key, stockDefault));
        toggle.setPadding(0, dp(8), 0, dp(8));
        toggle.setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isChecked) ->
                        RuntimeSettings.putBoolean(key, isChecked)
        );
        root.addView(toggle);
    }

    private TextView text(String value, float sizeSp, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        return view;
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(primaryTextColor);
        button.setTextSize(15f);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surfaceColor));
        }
        return button;
    }

    private String buildVersionText() {
        String versionName = "?";
        long versionCode = -1;
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = info.versionName == null ? "?" : info.versionName;
            versionCode = android.os.Build.VERSION.SDK_INT >= 28
                    ? info.getLongVersionCode()
                    : info.versionCode;
        } catch (Throwable ignored) {
        }
        return "VK Video " + versionName + " (" + versionCode + ")\n"
                + "Patch bundle " + PATCH_VERSION;
    }

    private void showDiagnostics() {
        StringBuilder body = new StringBuilder(buildVersionText());
        body.append("\n\nRuntime flags:");
        for (Map.Entry<String, Boolean> entry : RuntimeSettings.snapshot().entrySet()) {
            body.append("\n")
                    .append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue());
        }

        new AlertDialog.Builder(this)
                .setTitle("Диагностика")
                .setMessage(body.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
