package com.example.quanlytrucnhatvamuontrathietbi;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "user_name";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUser(String id, String name) {
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public String getUserId() {
        return pref.getString(KEY_ID, "");
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
