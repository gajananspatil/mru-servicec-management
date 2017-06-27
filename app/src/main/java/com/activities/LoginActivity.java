package com.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.helpers.HttpHandler;
import com.helpers.Toaster;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends AppCompatActivity {

    private static boolean isLoggedIn = false;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the LoginActivity task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mLoginIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mRegisterView;
    private static final String TAG = "Login_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the LoginActivity form.
        mLoginIdView = (EditText) findViewById(R.id.loginId);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.loginId || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.btnLogin);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mRegisterView = findViewById(R.id.link_to_register);
        mRegisterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"User redirected to Register activity");

                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the LoginActivity form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual LoginActivity attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the LoginActivity attempt.
        String email = mLoginIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt LoginActivity and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user LoginActivity attempt.

            doUserLogin();

        }
    }

    private void doUserLogin()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("userMobileNo", mLoginIdView.getText().toString());
        params.add("password", mPasswordView.getText().toString());

        String serverIp = getResources().getString(R.string.serviceURL);

        final ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);
        Dialog.setMessage("Please wait..");
        Dialog.show();
        // params.p
        client.post( "http://139.59.57.136:8080/backend-service-management/api/userLogin",
                //serverIp + "userLogin",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http response code
                    // '200'
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        try {
                            // Hide Progress Dialog
                            Dialog.dismiss();
                            // JSON Object
                            String resp = new String( responseBody );
                            JSONObject userObj = new JSONObject(resp);
                            SharedPreferences preferences = getSharedPreferences( getString(R.string.shared_preference), MODE_PRIVATE );
                            SharedPreferences.Editor editor = preferences.edit();

                            Iterator<String> iter = userObj.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();

                                String value = userObj.getString(key);
                                editor.putString(key,value);
                            }

                            editor.commit();
                            isLoggedIn = true;
                            Toast.makeText(
                                    getApplicationContext(),
                                    " You are successfully logged in!",
                                    Toast.LENGTH_LONG).show();

                            Thread.sleep(3500);

                            Intent complaintsListIntent = new Intent(
                                    getApplicationContext(),
                                    MainActivity.class);
                            complaintsListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(complaintsListIntent);
                            finish();
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured [Server's JSON response might be invalid]!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }

                    }

                    // When the response returned by REST has Http response code
                    // other than '200'
                    @Override
                            public void onFailure( int statusCode, Header[] headers, byte[] responseBody, Throwable throwable )
                    {
                        String resp = new String(responseBody);
                        Dialog.dismiss();
                        // Hide Progress Dialog
                        // prgDialog.hide();
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
                        else if (statusCode == 400)
                        {
                            try {
                                JSONObject msgOjb = new JSONObject(resp);
                                String msg = msgOjb.getString("message");
                                Toast.makeText(getApplicationContext(),
                                        "Login Failed due to "+msg,
                                        Toast.LENGTH_LONG).show();
                            }
                            catch (JSONException je)
                            {
                                Log.e(TAG,"Failed to parse error json:"+je.getMessage());
                                Toast.makeText(getApplicationContext(),
                                        "Login Failed due", Toast.LENGTH_LONG).show();
                            }

                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() < R.integer.minPasswordLength;
    }

    /**
     * Shows the progress UI and hides the LoginActivity form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous LoginActivity/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLoginId;
        private final String mPassword;

        UserLoginTask(String loginId, String password) {
            mLoginId = loginId;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // attempt authentication against a network service.

            String response;
            try {
                // Simulate network access.
                //     Thread.sleep(2000);
                String loginUrl = getString(R.string.serviceURL) + "userLogin?";

                HashMap<String,String> credentials = new HashMap<>();
                credentials.put("userMobileNo",mLoginId);
                credentials.put("password",mPassword);

                Uri.Builder query = new Uri.Builder();
                query.appendQueryParameter("userMobileNo",mLoginId);
                query.appendQueryParameter("password",mPassword);

                HttpHandler httpHandler = new HttpHandler();
                response = httpHandler.performPostCall(loginUrl, query.build().getEncodedQuery() );
                //JSONObject jsonObject = new JSONObject(response);

                //TODO Handle JSON Response for LoginActivity
                Toaster.makeToast(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_LONG);

            }
            catch ( JSONException je)
            {
                Log.e( TAG, "JSON Exception during LoginActivity-"+je.getMessage());
                return false;
            }
            catch ( IOException ioe)
            {
                Log.e(TAG,"IO Exception during LoginActivity-"+ ioe.getMessage());
                return false;
            }
            catch (Exception ex)
            {
                Log.e(TAG,"General Exception:"+ex.getMessage());
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mLoginId)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // update password LoginActivity id in local database
               // updateLocalCredentials(mLoginId,mPassword);
                isLoggedIn = true;
                Intent mainIntent = new Intent( LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public static boolean isUserLoggedIn()
    {
        return isLoggedIn;
    }
    public static void setUserLogin(boolean status)
    {
        Log.d(TAG, "LoginActivity status changed to "+status);
        isLoggedIn = status;
    }
}
