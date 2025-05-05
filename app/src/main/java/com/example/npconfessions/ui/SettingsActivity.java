package com.example.npconfessions.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.npconfessions.R;

public class SettingsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.settings_activity);
        if (b == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }
}
