package com.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.AsyncListUtil;
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
import com.helpers.DialogBox;
import com.helpers.HttpHandler;
import com.data.Category;
import com.data.SubCategory;
import com.data.ProductType;
import com.helpers.Toaster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Set;

public class submitRequest extends AppCompatActivity  implements AdapterView.OnItemSelectedListener
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle( getString(R.string.raise_request_title) );
        setContentView(R.layout.activity_submit_request);

        categorySpinner = (Spinner) findViewById( R.id.spinnerCategory);
        subCategorySpinner = (Spinner) findViewById( R.id.spinnerSubCategory);
        productTypeSpinner = (Spinner) findViewById( R.id.spinnerSubCategory);
        productSubTypeSpinner = (Spinner) findViewById( R.id.spinnerSubType);
        complaintDetailsEditText = (EditText) findViewById( R.id.complaint_details);
        submitButton = (Button) findViewById( R.id.submitRequestButton) ;
        submitButton.setEnabled( false );

        subCategorySpinner.setOnItemSelectedListener( this );
        categorySpinner.setOnItemSelectedListener( this );
        productTypeSpinner.setOnItemSelectedListener( this );

        progressBar = (ProgressBar) findViewById( R.id.requestProgressBar);
        // On create of SubmitRequest category details need have populated
        new GetProducts().execute(R.id.spinnerCategory );

    }

    /*
        On create page of submit request all the categories should be available for selection.
     */

    private class GetProducts extends AsyncTask<Integer,Integer ,Integer>
    {


        @Override
        protected Integer doInBackground(Integer... params) {

            if(params[0] == R.id.spinnerCategory ) {
                populateCategoryDetails();
            }
            else if( params[0] == R.id.spinnerSubCategory )
            {
                PopulateSubCategoryDetails();
            }
            else if( params[0] == R.id.spinnerProductType )
            {
                PopulateProductType();
            }
            else
            {
                Log.e( TAG, "Invalid parameters received");
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer v)
        {
            progressBar.setVisibility(View.GONE);
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
           // allCategoriesUrl += "/categories";

            HttpHandler httpHandler = new HttpHandler();
            String response = httpHandler.makeServiceCall(allCategoriesUrl, "GET");

            if( response.isEmpty() )
            {
                Toast toast = Toast.makeText( this, "Check Internet connection or try again", Toast.LENGTH_LONG );
                toast.setGravity(Gravity.LEFT|Gravity.TOP,50,50);
                toast.show();
                finish();
            }


            //TODO verify json ojects returned by webservice
            JSONObject categoryJSONObject = new JSONObject( response);
            JSONObject jsonResp = categoryJSONObject.getJSONObject("RestResponse");
            JSONArray categoryJSONArr = jsonResp.getJSONArray(getString(R.string.json_categories));

            for( int i=0; i<categoryJSONArr.length(); i++ )
            {
                JSONObject cat = categoryJSONArr.getJSONObject( i );
                Integer Id  = cat.getInt("alpha2_code");
                String name = cat.getString("name");
                allCategories.put( name,  new Category( Id, name));
            }

            Set<String> keySet = allCategories.keySet();
            String[] catArr = keySet.toArray( new String[ keySet.size()]);

            //TODO Verify activity lifecycle - do we need to create new adaptor every time or
            // check if required for existing adapter

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, catArr );
            categorySpinner.setAdapter( adapter);

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
                subCategoriesURL += "/subCategory";
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
                JSONObject subCategoryJSONObject = new JSONObject(response);
                JSONArray subCategoryJSONArr = subCategoryJSONObject.getJSONArray("Categories");

                HashMap<String,SubCategory> subCatMap = new HashMap<>();
                for (int i = 0; i < subCategoryJSONArr.length(); i++) {
                    JSONObject cat = subCategoryJSONArr.getJSONObject(i);
                    Integer Id = cat.getInt("usbCategoryId");
                    String name = cat.getString("subCategoryName");
                    subCatMap.put( name,new SubCategory(Id, name) );
                }

                subCatArr = subCatMap.keySet().toArray(new String[subCatMap.size()]);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_dropdown_item, subCatArr );
            subCategorySpinner.setAdapter( adapter );

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

            SubCategory subCatObj = new SubCategory();
            subCatObj = subCatMap.get( this.subCategory);

            String[] productArr ;
            if( subCatObj.getProductTypes().size() > 0)
            {
                // We have already populated product types
                productArr = subCatObj.getProductTypes().keySet().toArray( new String[subCatObj.getProductTypes().size()]);

            }
            else  // Call web service to retrieve product types
            {
                String productTypesUrl = getResources().getString(R.string.serviceURL);
                productTypesUrl += "/products";
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
                JSONObject productJSONObject = new JSONObject(response);
                JSONArray productJSONArr = productJSONObject.getJSONArray("Products");


                HashMap<String,ProductType>productTypeList = new HashMap<>();
                for (int i = 0; i < productJSONArr.length(); i++) {
                    JSONObject cat = productJSONArr.getJSONObject(i);
                    Integer Id = cat.getInt("productId");
                    String name = cat.getString("productName");
                    productTypeList.put( name,new ProductType(Id, name)) ;
                }

                productArr = productTypeList.keySet().toArray(new String[productTypeList.size()]);

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_dropdown_item,productArr  );
            productTypeSpinner.setAdapter( adapter );
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

                PopulateSubCategoryDetails(  );

                break;


            case R.id.spinnerSubCategory:
                // Display Toast message on select of Subcategory
                toast = Toast.makeText(getApplicationContext(), "SubCategory Selected is: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT);
                toast.setMargin(250, 250);
                toast.show();

                subCategory = parent.getItemAtPosition(position).toString();
                PopulateProductType(  );
                break;
            case R.id.spinnerProductType:
                toast = Toast.makeText(getApplicationContext(), "Product Selected is: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT);
                toast.setMargin(250, 250);
                toast.show();

                productType = parent.getItemAtPosition(position).toString();
                submitButton.setEnabled( true );
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
        String complaintDetails = Trim.text((EditText) complaintDetailsEditText);

        Log.d( TAG,"Validate complaint details");
        if( complaintDetails.isEmpty() || complaintDetails.length() < 100 )
        {
            complaintDetailsEditText.setError( "Description must be minimum 100 characters long" );
            return;
        }

        //TODO Enable submit button only after popluating all the values.
        // If any of the dropdown is not selected do not allow to submit.

    }
}
