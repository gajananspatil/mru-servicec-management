package com.activities;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;



import com.helpers.HttpHandler;
import com.helpers.NetworkHelper;
import com.helpers.Toaster;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final  String TAG = "MainActivity";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById( R.id.requestProgressBar);


        if( !(new NetworkHelper(this)).isConnected() )
        {
            Toaster.makeToast( this, "Not connected to Internet. Some services may not work", Toast.LENGTH_LONG  );
            return;
        }
    }



    private class RegisterUser extends AsyncTask<Integer,Integer ,Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            Integer result = 0;
            SharedPreferences preferences = getBaseContext().getSharedPreferences(getString(R.string.share_pref),MODE_PRIVATE);
            String userName = preferences.getString("username",null);
            String password = preferences.getString("password",null);

            if( userName != null && password != null)
            {
                // make web service call to validate username and password
                Log.d( TAG,"Validate Username and Password stored. username:password="+userName + ":"+password );
                if( true) //TODO Remove this added for debug purpose
                {
                    Intent intent = new Intent(MainActivity.this, submitRequest.class);
                    //EditText editText = (EditText) findViewById(R.id.imageButton);
                    //String message = editText.getText().toString();
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    //requestWindowFeature(Window.FEATURE_NO_TITLE);
                    startActivity(intent);
                }
                else
                {
                    try
                    {
                        String validateUser = getResources().getString(R.string.serviceURL) + "/validateUser";
                        HttpHandler httphandler = new HttpHandler();
                        HashMap<String,String> userlogin = new HashMap<>();
                        userlogin.put("username",userName);
                        userlogin.put("password",password);
                        String response = httphandler.performPostCall(validateUser, userlogin);

                        if( response.isEmpty() )
                        {
                            Toaster.makeToast(MainActivity.this, "Check Internet connection or try again", Toast.LENGTH_LONG );
                            finish();
                        }

                        JSONObject jsonObj = new JSONObject(response);
                        String msg = jsonObj.getString("Message");
                        if ( msg.contains("Success") )
                        {

                            Intent intent = new Intent(MainActivity.this, submitRequest.class);
                            //EditText editText = (EditText) findViewById(R.id.imageButton);
                            //String message = editText.getText().toString();
                            //intent.putExtra(EXTRA_MESSAGE, message);
                            //requestWindowFeature(Window.FEATURE_NO_TITLE);
                            startActivity(intent);
                        }
                        else {
                            //TODO: Redirect to login activity.
                            Log.i(TAG, "Redirect to login activity ");
                        }
                    }
                    catch ( Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            else {

                ShowMessageDialog("You need to register to raise request.\n Would you like to register?");
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result )
        {
            //progressBar.setVisibility(View.GONE);
            if( result == 0)
                Toaster.makeToast( MainActivity.this, "Registration Successful");
            else
                Toaster.makeToast( MainActivity.this, "Registration Failed");
        }

        @Override
        protected void onPreExecute()
        {
            Log.d( TAG, "Register User enabling progress Bar");
            //progressBar.setVisibility(View.VISIBLE);
        }

    }

    /** Called when the user clicks the Request Raise button */
    public void raiseRequest(View view) {

        new RegisterUser().execute();
    }


    /** Called when the user clicks the Services button */
    public void services(View view) {
        Intent intent = new Intent(this, services.class);
        //EditText editText = (EditText) findViewById(R.id.imageButton);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /** Called when the user clicks the About US button */
    public void aboutUs(View view) {
        Intent intent = new Intent(this, aboutUs.class);
        //EditText editText = (EditText) findViewById(R.id.imageButton);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void ShowMessageDialog( String message)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), signUp.class);
                        //EditText editText = (EditText) findViewById(R.id.imageButton);
                        //String message = editText.getText().toString();
                        //intent.putExtra(EXTRA_MESSAGE, message);
                        //requestWindowFeature(Window.FEATURE_NO_TITLE);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
