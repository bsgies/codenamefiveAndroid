package com.itridtechnologies.codenamefive.Models.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.itridtechnologies.codenamefive.R;

public class PreferenceManager {
    private static final String TAG = "PreferenceManager";

    private Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    public void savePartnerEmail(String email) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.apply();
        Log.d(TAG, "savePartnerEmail: email saved in cache...");
    }//end fun

    public String retrievePartnerEmail() {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY),
                Context.MODE_PRIVATE
        );
        return preferences.getString("email", "abc@example.com");
    }//end fun

    public void savePartnerPhone(String phone) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY),
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone", phone);
        editor.apply();
        Log.d(TAG, "savePartnerPhone: email saved in cache...");
    }//end fun

    public String retrievePartnerPhone() {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.PREFERENCE_KEY),
                Context.MODE_PRIVATE
        );
        return preferences.getString("phone", "+00 123456789");
    }//end fun

}
