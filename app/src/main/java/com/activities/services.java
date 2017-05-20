package com.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class services extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle( getString(R.string.services_offered_title) );

        setContentView(R.layout.activity_services);
    }
}
