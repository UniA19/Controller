package diy.esp8266.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static diy.esp8266.controller.Globals.IS_DARK;
import static diy.esp8266.controller.Globals.IS_DEBUG;
import static diy.esp8266.controller.Globals.IS_GAMEPAD;
import static diy.esp8266.controller.Globals.PREFS_GLOBALS;

public class ControllerActivity extends AppCompatActivity
{

    SharedPreferences globals;
    static TextView debug;
    static ControllerActivity ca;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        globals = getApplicationContext().getSharedPreferences(PREFS_GLOBALS, 0);
        // Use the chosen theme
        if (globals.getBoolean(IS_DARK, false)) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCalibration();
            }
        });

        FloatingActionButton preferences = findViewById(R.id.preferences);
        preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPreferences();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        ca = this;

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

        debug = findViewById(R.id.text_debug);
        Connection.receiveData(globals);

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (globals.getBoolean(IS_GAMEPAD, false)) {
            // Check that the event came from a game controller
            if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                    InputDevice.SOURCE_JOYSTICK &&
                    event.getAction() == MotionEvent.ACTION_MOVE) {

                // Process all historical movement samples in the batch
                final int historySize = event.getHistorySize();

                // Process the movements starting from the
                // earliest historical position in the batch
                for (int i = 0; i < historySize; i++) {
                    // Process the event at historical position i
                    GamepadInputDaemon.processJoystickInput(event, i, globals.getBoolean(IS_DEBUG, false));
                }

                // Process the current movement sample in the batch (position -1)
                GamepadInputDaemon.processJoystickInput(event, -1, globals.getBoolean(IS_DEBUG, false));
                return true;
            }
        }
        return super.onGenericMotionEvent(event);
    }

    private void toCalibration()
    {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void toPreferences()
    {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    static void addToDebug(final String string)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                ca.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        debug.append(string + "\n");
                    }
                });
            }
        };
        thread.start();
    }

}