package com.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.StringManuplation.Trim;
import com.data.Category;
import com.data.ProductType;
import com.data.SubCategory;
import com.helpers.HttpHandler;
import com.helpers.Toaster;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class RaiseRequestActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener
{

    public static final String TAG="SubmitRequest";
    //protected ArrayList<Category> allCategories = new ArrayList<Category>();
    private HashMap<String,Category> allCategories = new HashMap<>();

    Spinner categorySpinner;
    Spinner subCategorySpinner;
    Spinner productTypeSpinner;
    Spinner productSubTypeSpinner;
    TextView complaintDetailsEditText;
    Button submitButton;
    ProgressBar progressBar;

    String category;
    String subCategory;
    String productType;
    String subType;
    String mobileNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle( getString(R.string.raise_request_title) );
        setContentView(R.layout.activity_raise_request);

        categorySpinner = (Spinner) findViewById( R.id.spinnerCategory);
        subCategorySpinner = (Spinner) findViewById( R.id.spinnerSubCategory);
        productTypeSpinner = (Spinner) findViewById( R.id.spinnerProductType);
        productSubTypeSpinner = (Spinner) findViewById( R.id.spinnerSubType);
        complaintDetailsEditText = (EditText) findViewById( R.id.complaint_details);
        submitButton = (Button) findViewById( R.id.submitRequestButton) ;
        submitButton.setEnabled( false );


        subCategorySpinner.setOnItemSelectedListener( this );
        categorySpinner.setOnItemSelectedListener( this );
        productTypeSpinner.setOnItemSelectedListener( this );
        SharedPreferences preferences = getBaseContext().getSharedPreferences(getString(R.string.shared_preference),MODE_PRIVATE);
        mobileNo = preferences.getString("username",null);

        progressBar = (ProgressBar) findViewById( R.id.requestProgressBar);
        // On create of SubmitRequest category details need have populated
        new GetCategories().execute( );

    }

    /*
        On create page of submit request all the categories should be available for selection.
     */
    private class GetCategories extends AsyncTask<Void,Void ,Integer>
    {
        @Override
        protected Integer doInBackground(Void... params) {

            populateCategoryDetails();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer v)
        {
            progressBar.setVisibility(View.GONE);
            Set<String> keySet = allCategories.keySet();
            String[] catArr = keySet.toArray( new String[ keySet.size()]);

            //TODO Verify activity lifecycle - do we need to create new adaptor every time or check if we can reuse adapter

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>( RaiseRequestActivity.this, android.R.layout.simple_spinner_dropdown_item, catArr );
            categorySpinner.setAdapter( adapter);
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    /*
    On create page of submit request all the categories should be available for selection.
 */
    private class GetSubCategories extends AsyncTask<Void,Void ,Integer>
    {
        @Override
        protected Integer doInBackground(Void... params  ) {
            PopulateSubCategoryDetails();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer v)
        {
            progressBar.setVisibility(View.GONE);
            Category catObj = allCategories.get( RaiseRequestActivity.this.category );
            String[] subCatArr = catObj.getSubCategories().keySet().toArray(new String[catObj.getSubCategories().size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>( RaiseRequestActivity.this, android.R.layout.simple_spinner_dropdown_item, subCatArr );
            subCategorySpinner.setAdapter( adapter );
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    /*
    On create page of submit request all the categories should be available for selection.
 */
    private class GetProductTypes extends AsyncTask<Void, Void ,Integer>
    {
        @Override
        protected Integer doInBackground(Void... params) {

            PopulateProductType();
             return 1;
        }

        @Override
        protected void onPostExecute(Integer v)
        {
            progressBar.setVisibility(View.GONE);
            Category catObj = allCategories.get( RaiseRequestActivity.this.category );
            HashMap<String,SubCategory> subCatMap = catObj.getSubCategories();

            SubCategory subCatObj = subCatMap.get( RaiseRequestActivity.this.subCategory);
            String[] productArr = subCatObj.getProductTypes().keySet().toArray(
                    new String[subCatObj.getProductTypes().size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    RaiseRequestActivity.this, android.R.layout.simple_spinner_dropdown_item,productArr  );
            productTypeSpinner.setAdapter( adapter );
            submitButton.setEnabled( true );
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    private void populateCategoryDetails() {

        Log.d(TAG,"Populate All Categories");

        // On create we should have all categories fetched to select further details for product
        //TODO remove this code once web service is ready

        try
        {
            String allCategoriesUrl = getResources().getString( R.string.serviceURL);
            allCategoriesUrl += getString(R.string.allCategoriesAPI);

            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(allCategoriesUrl, "GET");

            if( response.isEmpty() )
            {
                Toast toast = Toast.makeText( this, "Check Internet connection or try again", Toast.LENGTH_LONG );
                toast.setGravity(Gravity.LEFT|Gravity.TOP,50,50);
                toast.show();
                finish();
            }

            // JSONObject categoryJSONObject = new JSONObject( response);
            //JSONObject jsonResp = categoryJSONObject.getJSONObject("RestResponse");
            JSONArray categoryJSONArr = new JSONArray( response) ;

            for( int i=0; i<categoryJSONArr.length(); i++ )
            {
                JSONObject cat = categoryJSONArr.getJSONObject( i );
                Integer Id  = cat.getInt("categoryId");
                String name = cat.getString("categoryName");
                allCategories.put( name,  new Category( Id, name));
            }

        }
        catch ( JSONException je)
        {
            Log.e( TAG, "PopulateCategories-"+je.getMessage() );
            //TODO at all exception what action should be taken ( mayb be redirect to home activity)
            Toaster.makeToast(this,"Unexpected Error occurred, try again");
        }

    }

    /*
        This method will be invoked when any of the category is selected to populate
        corresponding Sub Category list.
     */
    private void PopulateSubCategoryDetails(  ) {
        Log.d( TAG, "PopulateSubCategoryDetails");

        try
        {
            if(this.category == null) {
                Log.e( "PopulateSubCategory","Category is not populated");
                return;
            }
            Category catObj = allCategories.get( this.category );

            // TODO Is it better to have this array as class member which can be cleared when category
            // selection is changed and we don't have to create adapters each time we call populate functions
            String[] subCatArr; // = new String[0];

            if( catObj.getSubCategories().size() > 0)
            {
                // We have already populated sub categories
                HashMap<String,SubCategory> subCatList = catObj.getSubCategories();
                subCatArr = subCatList.keySet().toArray( new String[ subCatList.size()]);
            }
            else  // Call web service to retrieve sub categories
            {
                String subCategoriesURL = getResources().getString(R.string.serviceURL);
                subCategoriesURL += getString(R.string.allSubCategories);
                subCategoriesURL += "/";
                subCategoriesURL += catObj.getCategoryId();

                HttpHandler httpHandler = new HttpHandler();
                String response = httpHandler.makeServiceCall(subCategoriesURL, "GET");

                if( response.isEmpty() )
                {
                    Toast toast = Toast.makeText( this, "Check Internet connection or try again", Toast.LENGTH_LONG );
                    toast.setGravity(Gravity.LEFT|Gravity.TOP,50,50);
                    toast.show();
                    finish();
                }

                //TODO verify json objects returned by webservice
                JSONArray subCategoryJSONArr = new JSONArray( response );

                HashMap<String,SubCategory> subCatMap = new HashMap<>();
                for (int i = 0; i < subCategoryJSONArr.length(); i++) {
                    JSONObject cat = subCategoryJSONArr.getJSONObject(i);
                    Integer Id = cat.getInt("subCategoryId");
                    String name = cat.getString("subCategoryName");
                    subCatMap.put( name,new SubCategory(Id, name) );
                }
                catObj.setSubCategories( subCatMap );
            }
        }
        catch ( JSONException je)
        {
            Log.e( TAG, je.getMessage() );
            //TODO at all exception what action should be taken ( mayb be redirect to home activity)
            finish();
        }

    }

    /*
     *  Populate product types available for selected category and sub category
     */
    private void PopulateProductType()
    {
        try
        {
            int subCatId = -1;
            if(this.category == null || this.subCategory == null) {
                Log.e("PopulateProduct","Category or subCategory is not populated");
                return;
            }

            Category catObj = allCategories.get( this.category );
            HashMap<String,SubCategory> subCatMap = catObj.getSubCategories();
            if( subCatMap.size() <= 0)
            {
                //TODO is this check required
                Log.e("PopulateProduct", "SubCategory not populated");
                return;
            }

            SubCategory subCatObj =  subCatMap.get( this.subCategory);
            String[] productArr ;
            if( subCatObj.getProductTypes().size() > 0)
            {
                // We have already populated product types
                productArr = subCatObj.getProductTypes().keySet().toArray( new String[subCatObj.getProductTypes().size()]);

            }
            else  // Call web service to retrieve product types
            {
                String productTypesUrl = getResources().getString(R.string.serviceURL);
                productTypesUrl += getString(R.string.allProductTypes);
                productTypesUrl += "/";
                productTypesUrl += subCatObj.getSubCategoryId();

                HttpHandler httpHandler = new HttpHandler();
                String response = httpHandler.makeServiceCall( productTypesUrl, "GET" );

                if( response.isEmpty() )
                {
                    Toast toast = Toast.makeText( this, "Check Internet connection or try again", Toast.LENGTH_LONG );
                    toast.setGravity(Gravity.LEFT|Gravity.TOP,50,50);
                    toast.show();
                    finish();
                }

                //TODO verify json objects returned by webservice
                JSONArray productJSONArr = new JSONArray( response );

                HashMap<String,ProductType>productTypeList = new HashMap<>();
                for (int i = 0; i < productJSONArr.length(); i++) {
                    JSONObject cat = productJSONArr.getJSONObject(i);
                    Integer Id = cat.getInt("typeId");
                    String name = cat.getString("typeName");
                    productTypeList.put( name,new ProductType(Id, name)) ;
                }
                subCatObj.setProductTypes( productTypeList );
            }
        }
        catch ( JSONException je)
        {
            Log.e( TAG, je.getMessage() );
            //TODO at all exception what action should be taken ( mayb be redirect to home activity)
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
    int position, long id) {

        // Display Toast message on select of category
        int spinnerId = parent.getId();
        Toast toast;
        switch ( spinnerId )
        {
            case R.id.spinnerCategory:
                // Display Toast message on select of category
                toast = Toast.makeText( getApplicationContext(), "Category is Selected : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT );
                toast.setMargin(250, 250);
                toast.show();
                category = parent.getItemAtPosition(position).toString();
                subCategory = "";

                //Clear other spinners if they have some earlier populated data
                subCategorySpinner.setAdapter(null);
                productTypeSpinner.setAdapter(null);
                new GetSubCategories().execute();

                break;


            case R.id.spinnerSubCategory:
                // Display Toast message on select of Subcategory
                toast = Toast.makeText(getApplicationContext(), "SubCategory Selected is: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT);
                toast.setMargin(250, 250);
                toast.show();

                subCategory = parent.getItemAtPosition(position).toString();
                new GetProductTypes().execute();
                break;
            case R.id.spinnerProductType:
                toast = Toast.makeText(getApplicationContext(), "Product Selected is: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT);
                toast.setMargin(250, 250);
                toast.show();

                productType = parent.getItemAtPosition(position).toString();
                break;
            }
        }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        }


    // This method will be invoked on click of submit request
    public void onSubmitRequest(View view )
    {
        final String complaintDetails = Trim.text((EditText) complaintDetailsEditText);

        Log.d( TAG,"Validate complaint details");
        if( complaintDetails.isEmpty() || complaintDetails.length() < 10 )
        {
            complaintDetailsEditText.setError( "Description must be minimum 10 characters long" );
            return;
        }

        //TODO send complaint details to web server
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("userId", mobileNo);
        params.add("comments", complaintDetails);
        params.add("complaintStatus","Open");
        params.add("categoryName",category);
        params.add("subCategoryName",subCategory);
        params.add("typeName",productType);

        final ProgressDialog dialog = new ProgressDialog(RaiseRequestActivity.this);
        dialog.setMessage("Please wait..");
        dialog.show();
        client.post("http://139.59.57.136:8080/backend-service-management/api/request/createRequest", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    String response = new String(responseBody);

                    Toast.makeText(getApplicationContext(),
                            "Request is submitted successfully.",
                            Toast.LENGTH_LONG).show();
                    Thread.sleep(2000);
                    finish();
                }
                catch(Exception e)
                {
                    Log.e(TAG,"Request is raised successfully but failed to handle response"+e.getMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String resp = new String(responseBody);
                dialog.dismiss();
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
                        JSONObject errorMsg = new JSONObject(resp);
                        errorMsg.length();
                        //TODO:Display actual error message received
                    }
                    catch (JSONException je)
                    {
                        Log.e(TAG,"Failed to parse error response of raise requests: "+je.getMessage());
                        Toast.makeText(getApplicationContext(),
                                resp,Toast.LENGTH_LONG).show();
                    }

                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
                            Toast.LENGTH_LONG).show();
                }

                // Reset the comments in box
                complaintDetailsEditText.setText("");
            }
        });


    }
}
