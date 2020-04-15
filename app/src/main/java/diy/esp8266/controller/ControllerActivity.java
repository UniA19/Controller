package diy.esp8266.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static diy.esp8266.controller.MainActivity.IS_DARK;
import static diy.esp8266.controller.MainActivity.IS_DEBUG;
import static diy.esp8266.controller.MainActivity.IS_GAMEPAD;
import static diy.esp8266.controller.MainActivity.PREFS_GLOBALS;

public class ControllerActivity extends AppCompatActivity {

    SharedPreferences globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    @Override
    protected void onResume() {
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

    private void restartActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}