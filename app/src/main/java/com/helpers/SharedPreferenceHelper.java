package com.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.activities.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Gajanan on 15-06-2017.
 */

public final class SharedPreferenceHelper {

    public static void updateCredentials(Context context, String login, String password)
    {
        // Get a reference to the SharedPReferences, SO WE CAN BEGIN TO STORE THE VALUES ENTERED
        SharedPreferences preferences = context.getSharedPreferences( context.getString(R.string.shared_preference), MODE_PRIVATE );
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString( "username", login.trim() );
        editor.putString( "password", password.trim() );

        // Must Call this to write values to the Preferences
        editor.apply();
    }
}

