package com.monacoprime.primepets.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtils {

    public static String IP = "192.168.0.12";
    public static String PORTA = "8080";
    public static String URL_USUARIO = "http://"+IP+":"+PORTA+"/FormiguinhaWS/services/UsuarioWS?wsdl";

    public static boolean checkConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
            return true;
        }
        return true;
    }

}
