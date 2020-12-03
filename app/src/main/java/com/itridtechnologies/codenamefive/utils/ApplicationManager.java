package com.itridtechnologies.codenamefive.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class ApplicationManager {

    public static void intent(final Class<? extends Activity> ActivityToOpen) {
        Context context = CNF.getAppContext();
        Intent intent = new Intent(context, ActivityToOpen);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void intent(final Class<? extends Activity> ActivityToOpen, String key, String putExtra) {
        Context context = CNF.getAppContext();
        Intent intent = new Intent(context, ActivityToOpen);
        intent.putExtra(key, putExtra);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void toast(String message) {
        Context context = CNF.getAppContext();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkOk() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) CNF.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

        } catch (Exception e) {
            Log.e("Connectivity Exception", Objects.requireNonNull(e.getMessage()));
        }
        return connected;
    }//end method

}//end MANAGER
