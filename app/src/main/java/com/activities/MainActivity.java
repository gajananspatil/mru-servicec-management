package com.activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.helpers.HttpHandler;
import com.helpers.NetworkHelper;
import com.helpers.Toaster;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final  String TAG = "MainActivity";
    ProgressBar progressBar;
    String nextActivity ="";
    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById( R.id.requestProgressBar);

        if( !(new NetworkHelper(this)).isConnected() )
        {
            Toaster.makeToast( this, "Not connected to Internet. Some ServicesActivity may not work", Toast.LENGTH_LONG  );
            return;
        }
    }


    private void doUserLogin()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        if( userName != null && password != null) {
            params.add("userMobileNo", userName);
            params.add("password", password);

            String serverIp = getResources().getString(R.string.serviceURL);

            final ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
            Dialog.setMessage("Please wait..");
            Dialog.show();
            // params.p
            client.post("http://139.59.57.136:8080/backend-service-management/api/userLogin",
                    //serverIp + "userLogin",
                    params, new AsyncHttpResponseHandler() {
                        // When the response returned by REST has Http response code
                        // '200'
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Dialog.dismiss();
                            // Hide Progress Dialog
                            try {
                                // JSON Object
                                String resp = new String(responseBody);
                                JSONObject userObj = new JSONObject(resp);
                                SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                Iterator<String> iter = userObj.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    String value = userObj.getString(key);
                                    editor.putString(key, value);
                                }

                                editor.commit();
                                LoginActivity.setUserLogin(true);
                                Thread.sleep(3500);
                                if(nextActivity.equals("RaiseRequest")) {
                                    Intent raiseRequest = new Intent(
                                            getApplicationContext(),
                                            RaiseRequestActivity.class);
                                    raiseRequest.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(raiseRequest);
                                }
                                else if( nextActivity.equals("ViewRequests"))
                                {
                                    Intent viewRequests = new Intent(
                                            getApplicationContext(),
                                            ViewRequestsActivity.class);
                                    viewRequests.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(viewRequests);
                                }

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Error Occurred [Server's JSON response might be invalid]!",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        // When the response returned by REST has Http response code
                        // other than '200'
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {

                            Dialog.dismiss();

                            // When Http response code is '404'
                            if (statusCode == 404) {
                                Toast.makeText(getApplicationContext(),
                                        "Requested resource not found",
                                        Toast.LENGTH_LONG).show();
                            }
                            // When Http response code is '500'
                            else if (statusCode == 500) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong at server end",
                                        Toast.LENGTH_LONG).show();
                            }
                            else if (statusCode == 400) {
                                String resp = new String(responseBody);
                                Toast.makeText(getApplicationContext(),
                                        resp, Toast.LENGTH_LONG).show();
                            }
                            // When Http response code other than 404, 500
                            else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
                                        Toast.LENGTH_LONG).show();
                            }
                            try {
                                Thread.sleep(3500);

                            } catch (Exception e) {

                            }
                        }
                    });
        }
        else {
            Log.i(TAG, "New Login, Redirect to LoginActivity.");
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);

        }
    }
    private class LoginUserTask extends AsyncTask<Integer,Integer ,Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            Integer result = 1;
            SharedPreferences preferences = getBaseContext().getSharedPreferences(getString(R.string.shared_preference),MODE_PRIVATE);
            String userName = preferences.getString("username",null);
            String password = preferences.getString("password",null);

            if( userName != null && password != null)
            {
                // make web service call to validate username and password
                Log.d( TAG,"Validate Username and Password stored. username:password="+userName + ":"+password );
                try
                {
                    String validateUser = getResources().getString(R.string.serviceURL) + "/loginUser";
                    HttpHandler httphandler = new HttpHandler();
                    HashMap<String,String> userLogin = new HashMap<>();
                    userLogin.put("username",userName);
                    userLogin.put("password",password);
                    String response = httphandler.performPostCall(validateUser, userLogin);

                    if( response.isEmpty() )
                    {
                        Toaster.makeToast(MainActivity.this, "Check Internet connection or try again", Toast.LENGTH_LONG );
                        finish();
                    }

                    JSONObject userObj = new JSONObject(response);
                    SharedPreferences.Editor editor = preferences.edit();

                    Iterator<String> iter = userObj.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();

                        String value = userObj.getString(key);
                        editor.putString(key,value);
                    }

                    editor.commit();
                    LoginActivity.setUserLogin(true);

                    if ( true )
                    {
                        result =0;
                        Log.d(TAG,"Login Successful");
                        Intent intent = new Intent(MainActivity.this, RaiseRequestActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Log.i(TAG, "Redirect to LoginActivity activity ");
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);

                    }
                }

                catch ( Exception ex)
                {
                    ex.printStackTrace();
                    return 0;
                }
            }
            else {
                Log.i(TAG, "Redirect to LoginActivity activity ");
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result )
        {
            //progressBar.setVisibility(View.GONE);
            if( result == 0)
                Toaster.makeToast( MainActivity.this, "Login Successful");

        }

        @Override
        protected void onPreExecute()
        {
            Log.d( TAG, "Login User enabling progress Bar");
            //progressBar.setVisibility(View.VISIBLE);
        }
    }

    /** Called when the user clicks the Request Raise button */
    public void raiseRequest(View view) {
        nextActivity = "RaiseRequest";

        if( !LoginActivity.isUserLoggedIn() && firstTimeLogin() ) {

            ShowMessageDialog("You need to login to raise request.\n Would you like to Login?");
        }
        else{
            Intent intent = new Intent(MainActivity.this, RaiseRequestActivity.class);
            startActivity(intent);
        }
    }


    public void viewRequests(View view)
    {
        nextActivity = "ViewRequests";
        if( !LoginActivity.isUserLoggedIn() && firstTimeLogin() ) {

            ShowMessageDialog("You need to login to perform this action.\n Would you like to Login?");
        }
        else{
            Intent intent = new Intent(this, ViewRequestsActivity.class);
            startActivity(intent);
        }

    }

    private boolean firstTimeLogin()
    {
        SharedPreferences preferences = getBaseContext().getSharedPreferences(getString(R.string.shared_preference),MODE_PRIVATE);
        userName = preferences.getString("username",null);
        password = preferences.getString("password",null);

        if(userName == null)
            return true;

        return false;

    }

    /** Called when the user clicks the Services button */
    public void services(View view) {
        Intent intent = new Intent(this, ServicesActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the About US button */
    public void aboutUs(View view) {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }



    private void ShowMessageDialog( String message)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        doUserLogin();
                        //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        //startActivity(intent);
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
