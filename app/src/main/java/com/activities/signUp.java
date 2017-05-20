package com.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.StringManuplation.Trim;

import com.helpers.HttpHandler;
import com.helpers.Toaster;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class signUp extends AppCompatActivity {

    private static final  String TAG = "SIGNUP";
    private Integer minPassLen ;

    TextView vName ;
    TextView vMobile ;
    TextView vPassword ;
    TextView vConfirmPassword ;
    TextView vEmailId ;
    TextView vAddress ;
    Animation animShake;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setTitle( getString(R.string.sign_up_title) );
        setContentView( R.layout.activity_sign_up );

        vName = (TextView) findViewById( R.id.SignUp_Name );
        vMobile = (TextView) findViewById( R.id.SignUp_MobileNo );
        vPassword = (TextView) findViewById( R.id.SignUp_password );
        vConfirmPassword = (TextView) findViewById( R.id.SignUp_confPassword );
        vEmailId = (TextView) findViewById( R.id.SignUp_Email );
        vAddress = (TextView) findViewById( R.id.SignUp_Address);

        animShake = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.shake );
    }

    public boolean validateSignUpFrom()
    {

        Log.d( TAG, "Validate Sign Up Form details");

        String name = Trim.text((EditText) vName);

        if( name.isEmpty() )
        {
            vName.setError( "Enter Valid Name" );
            vName.setAnimation(animShake);
            vName.startAnimation(animShake);

            vName.requestFocus();
            return  false;
        }

        String mobileNo = Trim.text((EditText) vMobile);
        if( mobileNo.isEmpty() || mobileNo.length() < getResources().getInteger( R.integer.mobileNoLength )  )
        {
            vMobile.setError("Enter valid mobile Number");
            vMobile.setAnimation(animShake);
            vMobile.startAnimation(animShake);

            vMobile.requestFocus();
            return false;
        }
        String email = Trim.text((EditText) vEmailId );
        if( email.isEmpty() ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() )
        {
            vEmailId.setError("Enter valid email id");
            vEmailId.setAnimation(animShake);
            vEmailId.startAnimation(animShake);

            vEmailId.requestFocus();
            return false;
        }


        minPassLen = getResources().getInteger( R.integer.minPasswordLength);
        String password = Trim.text((EditText) vPassword);

        if( password.isEmpty() || password.length() < minPassLen )
        {
            vPassword.setError("Password must be at least 6 characters long");
            vPassword.setAnimation(animShake);
            vPassword.startAnimation(animShake);

            vPassword.requestFocus();
            return false;
        }

        String confPassword = Trim.text((EditText) vConfirmPassword);

        if( confPassword.isEmpty() || confPassword.length() < minPassLen )
        {
            vConfirmPassword.setError("Password must be at least 6 characters long");
            vConfirmPassword.setAnimation(animShake);
            vConfirmPassword.startAnimation(animShake);

            vConfirmPassword.requestFocus();
            return false;
        }

        if( !password.equals( confPassword ))
        {
            vConfirmPassword.setError("Passwords do not match");
            vConfirmPassword.setAnimation(animShake);
            vConfirmPassword.startAnimation(animShake);

            vConfirmPassword.requestFocus();
            return false;
        }

        String address = Trim.text((EditText) vAddress);
        if( address.isEmpty() )
        {
            vAddress.setError( "Enter valid address" );
            vAddress.requestFocus();
            return  false;
        }

        return true;
    }
    /** Called when the user clicks the Submit button */
    public void signUpSubmit(View view) {

        boolean isValidInfo = validateSignUpFrom();


           if( isValidInfo )  //TODO Added for debug purpose remove this condition and code
           {
               SharedPreferences preferences = getBaseContext().getSharedPreferences( getString(R.string.share_pref), MODE_PRIVATE );
               SharedPreferences.Editor editor = preferences.edit();

               editor.putString( "username",Trim.text((EditText) vMobile) );
               editor.putString( "password",Trim.text((EditText) vPassword) );

               // Must Call this to write values to the Preferences
               editor.commit();


               Intent intent = new Intent( signUp.this, submitRequest.class );
               //EditText editText = (EditText) findViewById(R.id.imageButton);
               //String message = editText.getText().toString();
               //intent.putExtra(EXTRA_MESSAGE, message);
               //requestWindowFeature(Window.FEATURE_NO_TITLE);
               startActivity( intent );

           }
           else
           {
                return;
           }

           {

                   String registerUrl = getResources().getString( R.string.serviceURL) + "/userRegistration";

                   HashMap<String,String> userDetails = new HashMap<>();
                   //UserDetails userDetails = new UserDetails();
                   userDetails.put( "username", Trim.text((EditText) vMobile) );
                   userDetails.put("password",  Trim.text((EditText) vPassword) );
                   userDetails.put( "emailid", Trim.text((EditText) vEmailId) );
                   userDetails.put( "mobileno",  Trim.text((EditText) vMobile) );
                   userDetails.put("address",  Trim.text((EditText) vAddress) );

                   String response = "";
                   try
                   {
                       HttpHandler httpHandler = new HttpHandler();
                       response = httpHandler.performPostCall( registerUrl, userDetails);
                       JSONObject jsonObject= new JSONObject( response);

                       //TODO Handle JSON Response for registration

                       // Get a reference to the SharedPReferences, SO WE CAN BEGIN TO STORE THE VALUES ENTERED
                       SharedPreferences preferences = getBaseContext().getSharedPreferences( getString(R.string.share_pref), MODE_PRIVATE );
                       SharedPreferences.Editor editor = preferences.edit();

                       editor.putString( "username",Trim.text((EditText) vMobile) );
                       editor.putString( "password",Trim.text((EditText) vPassword) );

                       // Must Call this to write values to the Preferences
                       editor.commit();


                       Toaster.makeToast(this, "User registered successfully.", Toast.LENGTH_LONG);


                   }
                   catch ( JSONException je)
                   {
                       Log.e( TAG, "SignUp-"+je.getMessage());
                   }
                   catch ( Exception e)
                   {
                       Log.e( TAG, "SignUp-"+e.getMessage());
                   }

               }

       }

}
