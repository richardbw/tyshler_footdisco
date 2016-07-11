package com.barneswebb.android.tyshlerfootdisco.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by richard.barnes-webb on 2016/06/28.
 * @Thankyou: http://stackoverflow.com/a/17961536 - detect connection
 */
public class DetectConnection
{
    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

         return (   con_manager.getActiveNetworkInfo() != null
            && con_manager.getActiveNetworkInfo().isAvailable()
            && con_manager.getActiveNetworkInfo().isConnected())?
         true : false;
    }
}
