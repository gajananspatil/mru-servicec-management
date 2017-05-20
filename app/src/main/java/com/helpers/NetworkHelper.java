package com.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Gajanan Patil on 3/14/2017.
 */

public class NetworkHelper {

    Context context;

    public NetworkHelper( Context context )
    {
        this.context = context;

    }
    public boolean isConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService( Context.CONNECTIVITY_SERVICE );

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return  ( networkInfo != null && networkInfo.isConnected() );

    }

}
