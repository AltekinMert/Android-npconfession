package com.example.npconfessions.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** Small wrapper around SharedPreferences. */
public class PreferencesManager {

    private static final String FILE          = "np_prefs";
    private static final String KEY_DARK      = "dark_mode";
    private static final String KEY_PROVIDER  = "link_provider";       // "deezer"|"spotify"
    private static final String KEY_INTERVAL  = "sync_interval_h";
    private static final String DEF_PROVIDER  = "deezer";

    private final SharedPreferences prefs;

    public PreferencesManager(Context ctx) {
        prefs = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    /* ---------- Dark-mode toggle (future use) ---------- */
    public boolean isDarkMode() {
        return prefs.getBoolean("dark_mode", false);
    }
    public void setDarkMode(boolean v)    { prefs.edit().putBoolean(KEY_DARK, v).apply(); }

    /* ---------- Song-link provider ---------- */
    public String  getProvider()          { return prefs.getString(KEY_PROVIDER, DEF_PROVIDER); }
    public void    setProvider(String p)  { prefs.edit().putString(KEY_PROVIDER, p).apply(); }

    /* ---------- Worker interval (hrs) ---------- */
    public int  getSyncInterval()         { return prefs.getInt(KEY_INTERVAL, 6); }
    public int getSyncIntervalMinutes() {
        return Integer.parseInt(prefs.getString("sync_interval_h", "360"));
    }
}
