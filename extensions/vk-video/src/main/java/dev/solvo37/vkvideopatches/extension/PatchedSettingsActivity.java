package dev.solvo37.vkvideopatches.extension;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Map;

/**
 * Development settings screen for the 0.3 branch.
 *
 * It is intentionally programmatic: no dependency on VK resources or AppCompat,
 * which keeps the initial runtime-settings foundation isolated from VK UI changes.
 */
@SuppressWarnings("deprecation")
public final class PatchedSettingsActivity extends Activity {
    private static final String PATCH_VERSION = "0.3.0-alpha1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RuntimeSettings.initialize(this);
        setTitle("VK Video Patched");

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20);
        root.setPadding(pad, pad, pad, pad);
        scroll.addView(root);

        TextView title = text("VK Video Patched Settings", 24f);
        root.addView(title);

        TextView version = text(buildVersionText(), 14f);
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

        Button diagnostics = new Button(this);
        diagnostics.setText("Показать диагностику");
        diagnostics.setOnClickListener(v -> showDiagnostics());
        root.addView(diagnostics);

        Button reset = new Button(this);
        reset.setText("Сбросить настройки патча");
        reset.setOnClickListener(v -> {
            RuntimeSettings.reset();
            recreate();
        });
        root.addView(reset);

        setContentView(scroll);
    }

    private void addSection(LinearLayout root, String label) {
        TextView view = text(label, 18f);
        view.setPadding(0, dp(18), 0, dp(6));
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
        toggle.setChecked(RuntimeSettings.getBoolean(key, stockDefault));
        toggle.setPadding(0, dp(6), 0, dp(6));
        toggle.setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isChecked) ->
                        RuntimeSettings.putBoolean(key, isChecked)
        );
        root.addView(toggle);
    }

    private TextView text(String value, float sizeSp) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sizeSp);
        return view;
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
