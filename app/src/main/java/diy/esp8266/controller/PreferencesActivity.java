package diy.esp8266.controller;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static diy.esp8266.controller.Globals.DEF_IP;
import static diy.esp8266.controller.Globals.DEF_PORT;
import static diy.esp8266.controller.Globals.IS_DARK;
import static diy.esp8266.controller.Globals.PREFS_GLOBALS;
import static diy.esp8266.controller.Globals.PREFS_IP;
import static diy.esp8266.controller.Globals.PREFS_PORT;

public class PreferencesActivity extends AppCompatActivity
{
    SharedPreferences globals;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        globals = getApplicationContext().getSharedPreferences(PREFS_GLOBALS, 0);
        editor = globals.edit();
        // Use the chosen theme
        if (globals.getBoolean(IS_DARK, false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        EditText ip = findViewById(R.id.ip);
        ip.setText(globals.getString(PREFS_IP, DEF_IP));
        ip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String text = String.valueOf(v.getText());

                if (text.matches("^\\d{1,3}.{1}\\d{1,3}.{1}\\d{1,3}.{1}\\d{1,3}")) {
                    editor.putString(PREFS_IP, String.valueOf(v.getText()));
                    editor.apply();
                    return false;
                } else {
                    v.setText(globals.getString(PREFS_IP, DEF_IP));
                    return true;
                }
            }
        });


        EditText port = findViewById(R.id.port);
        port.setText(String.valueOf(globals.getInt(PREFS_PORT, DEF_PORT)));
        port.setInputType(InputType.TYPE_CLASS_NUMBER);
        port.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String text = String.valueOf(v.getText());
                if (TextUtils.isDigitsOnly(text)) {
                    editor.putInt(PREFS_PORT, Integer.parseInt(text));
                    editor.apply();
                    return false;
                } else {
                    v.setText(String.valueOf(globals.getInt(PREFS_PORT, DEF_PORT)));
                    return true;
                }
            }
        });

        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

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
}
