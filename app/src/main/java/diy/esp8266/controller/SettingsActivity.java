package diy.esp8266.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Globals g = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Use the chosen theme
        if(g.getDark()) {
            setTheme(R.style.AppTheme_Dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Switch darkModeToggle = findViewById(R.id.darkmodeSwitch);
        darkModeToggle.setSwitchTextAppearance(this, R.style.SwitchTextAppearance);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        darkModeToggle.setChecked(g.getDark());
        darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleTheme(isChecked);
            }
        });
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