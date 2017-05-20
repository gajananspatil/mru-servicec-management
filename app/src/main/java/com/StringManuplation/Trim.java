package com.StringManuplation;

import android.widget.EditText;

/**
 * Created by Gajanan Patil on 3/14/2017.
 */

public class Trim {

    public static String text(EditText v){
        return v.getText().toString().trim();
    }
}
