package com.dooo.android.sharedpreferencesmanager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class UserManager {
    public static void saveUser(Context context, String user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", user);
        editor.apply();
    }

    public static JSONObject loadUser(Context context) throws JSONException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String user = sharedPreferences.getString("UserData", null);
        return new JSONObject(user);
    }
}
