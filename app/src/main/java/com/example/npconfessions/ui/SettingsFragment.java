package com.example.npconfessions.ui;

import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import com.example.npconfessions.R;
import com.example.npconfessions.utils.PreferencesManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override public void onCreatePreferences(Bundle b, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);

        PreferencesManager pm = new PreferencesManager(requireContext());
        ListPreference lp    = findPreference("link_provider");
        lp.setValue(pm.getProvider());
        lp.setOnPreferenceChangeListener((pref, newVal) -> {
            pm.setProvider((String) newVal);
            return true;
        });
    }
}
