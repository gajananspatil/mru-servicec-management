package com.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.helpers.Toaster;


public class aboutUs extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="AboutUs";
    public static final int REQUEST_CALL_PHONE = 0x1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle( getString(R.string.about_us_title) );
        setContentView(R.layout.activity_about_us);

        FloatingActionButton fab_call_us = (FloatingActionButton) findViewById(R.id.fab_call_us);
        fab_call_us.setOnClickListener( this );

        FloatingActionButton fab_mail_us = (FloatingActionButton) findViewById(R.id.fab_mail_us);
        fab_mail_us.setOnClickListener(this);

    }

    private  void onFabCall()
    {
        try
        {
            Intent intent = new Intent( Intent.ACTION_CALL);
            intent.setData( Uri.parse("tel:" + getResources().getString(R.string.contact_us)) );
            int isPermitted = ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.CALL_PHONE);
            Log.d(TAG, "Call Phone permission" + isPermitted);
            if( isPermitted != PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions( aboutUs.this, new String[] {android.Manifest.permission.CALL_PHONE},REQUEST_CALL_PHONE);
            }
            startActivity(intent);
        }
        catch ( SecurityException se)
        {
            Log.e(TAG, se.getMessage());
            finish();
        }
    }

    private void onFabMail()
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+ "gajanansp@gmail.com"));
            startActivity(intent);

        }
        catch ( ActivityNotFoundException ae)
        {
            Log.e( TAG, ae.getMessage());
            finish();
        }

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();
        switch ( viewId)
        {
            case R.id.fab_call_us:
                onFabCall();
                break;
            case R.id.fab_mail_us:
                onFabMail();
                break;
            default:
                Log.e( TAG, "Unhandled button click event" + view.toString() );
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults )
    {

        switch ( requestCode )
        {
            case REQUEST_CALL_PHONE:

                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    Log.d( TAG, "User granted permission for CALL_PHONE");
                    onFabCall();

                }
                else
                {
                    Toaster.makeToast(this, "Can not proceed as user denied permissions" );
                    Toast toast = Toast.makeText( this, "Can not proceed as user denied permissions", Toast.LENGTH_LONG );
                    toast.setGravity(Gravity.LEFT|Gravity.TOP,50,50);
                    toast.show();
                    finish();
                }
                break;
        }
    }


}
