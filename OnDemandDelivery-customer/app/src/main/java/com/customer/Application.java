package com.customer;

import android.content.SharedPreferences;
import androidx.multidex.MultiDexApplication;

import com.teliver.sdk.core.Teliver;

public class Application extends MultiDexApplication {


    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        Teliver.init(this,"teliver_key");
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void storeStringInPref(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void storeBooleanInPref(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getStringInPref(String key) {
        return sharedPreferences.getString(key, null);
    }

    public boolean getBooleanInPef(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void deletePreference() {
        editor.clear();
        editor.commit();
    }
}
