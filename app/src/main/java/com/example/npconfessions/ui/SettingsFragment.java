package com.example.npconfessions.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.npconfessions.R;
import com.example.npconfessions.data.Repository;
import com.example.npconfessions.utils.PreferencesManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);

        PreferencesManager pm = new PreferencesManager(requireContext());

        // Link provider listener
        ListPreference lp = findPreference("link_provider");
        lp.setValue(pm.getProvider());
        lp.setOnPreferenceChangeListener((pref, newVal) -> {
            pm.setProvider((String) newVal);
            return true;
        });

        // Dark mode toggle (optional apply now or on restart)
        SwitchPreferenceCompat dark = findPreference("dark_mode");
        dark.setOnPreferenceChangeListener((pref, newVal) -> {
            boolean on = (Boolean) newVal;
            AppCompatDelegate.setDefaultNightMode(
                    on ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            return true;
        });
    }
}
