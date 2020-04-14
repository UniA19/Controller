package diy.esp8266.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    Globals g = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Connection.start();

        // Use the chosen theme
        if(g.getDark()) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch darkModeToggle = findViewById(R.id.darkmode_switch);
        darkModeToggle.setChecked(g.getDark());
        darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleTheme(isChecked);
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

    private void  toController()
    {
        Intent intent = new Intent(this, ControllerActivity.class);
        finish();
        startActivity(intent);
    }

    private void toGamepad() {
        Intent intent = new Intent(this, GamepadActivity.class);
        startActivity(intent);
    }

    private void toggleTheme(boolean darkTheme) {
        g.setDark(darkTheme);
        restartActivity();
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();

        startActivity(intent);
    }

}
