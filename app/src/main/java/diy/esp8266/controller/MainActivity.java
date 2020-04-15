package diy.esp8266.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_GLOBALS = "prefsGlobals";
    public static final String IS_GAMEPAD = "isGamepad";
    public static final String IS_DARK = "isDark";
    public static final String IS_DEBUG = "isDebug";

    SharedPreferences globals;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        globals = getApplicationContext().getSharedPreferences(PREFS_GLOBALS, 0);
        editor = globals.edit();

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggle, boolean isChecked) {
                switch (toggle.getId()) {
                    case R.id.darkmode_switch:
                        editor.putBoolean(IS_DARK, isChecked);
                        editor.apply();
                        restartActivity();
                        break;

                    case R.id.debugmode_switch:
                        editor.putBoolean(IS_DEBUG, isChecked);
                        editor.apply();
                        break;

                    case R.id.gamepad_switch:
                        editor.putBoolean(IS_GAMEPAD, isChecked);
                        editor.apply();
                        break;
                }
            }
        };

        // Use the chosen theme
        if (globals.getBoolean(IS_DARK, false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch darkModeToggle = findViewById(R.id.darkmode_switch);
        darkModeToggle.setChecked(globals.getBoolean(IS_DARK, false));
        darkModeToggle.setOnCheckedChangeListener(listener);

        Switch debugModeToggle = findViewById(R.id.debugmode_switch);
        debugModeToggle.setChecked(globals.getBoolean(IS_DEBUG, false));
        debugModeToggle.setOnCheckedChangeListener(listener);

        Switch gamepadToggle = findViewById(R.id.gamepad_switch);
        gamepadToggle.setChecked(globals.getBoolean(IS_GAMEPAD, false));
        gamepadToggle.setOnCheckedChangeListener(listener);

        Button calibrationButton = findViewById(R.id.calibration_button);
        calibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toController();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        Connection.start();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void toController() {
        Intent intent = new Intent(this, ControllerActivity.class);
        finish();
        startActivity(intent);
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();

        startActivity(intent);
    }

}
