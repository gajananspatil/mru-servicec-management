package com.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.data.RequestDetail;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ViewRequestsActivity extends AppCompatActivity {
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<RequestDetail> m_requests = null;
    private RequestAdapter m_adapter;
    private Runnable viewRequests;
    ListView listview;
    public static final String TAG ="ViewRequests";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Requests");
        setContentView(R.layout.activity_view_requests);

        listview = (ListView) findViewById(R.id.requestsList);
        m_requests = new ArrayList<>();
        m_adapter = new RequestAdapter(this, R.layout.row,m_requests);
        listview.setAdapter(m_adapter);

        getRequests();
/*        viewRequests = new Runnable() {
            @Override
            public void run() {
                getRequests();
            }
        };

        Thread thread =  new Thread(null, viewRequests, "MagentoBackground");
        thread.start();*/
        m_ProgressDialog = ProgressDialog.show(ViewRequestsActivity.this,
                "Please wait...", "Retrieving data ...", true);


    }
    private void getRequests(){
        try{
            //m_requests = new ArrayList<RequestDetail>();


            String allRequests = "http://139.59.57.136:8080/backend-service-management/api/request/allRequestsForUser";

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            SharedPreferences preferences = getBaseContext().getSharedPreferences(getString(R.string.shared_preference),MODE_PRIVATE);
            String mobileNo = preferences.getString("mobileNo",null);
            params.put("userMobileNo",mobileNo);

            client.get(allRequests, params, new AsyncHttpResponseHandler () {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                    m_adapter.notifyDataSetChanged();
                    try {
                        String resp = new String(responseBody);
                        JSONArray allRequests = new JSONArray(resp);

                        for(int index=0;index<allRequests.length();++index)
                        {
                            JSONObject requestObj = allRequests.getJSONObject(index);

                            String subCategory = requestObj.getJSONObject("fullProductDetails").
                                    getJSONObject("productSubCategory").getString("subCategoryName");

                            String createDate =  requestObj.getJSONObject("complaint").getString("createDate");
                            String status = requestObj.getJSONObject("complaint").getString("complaintStatus");

                            int requestId = (int) requestObj.getInt("requestId");

                            RequestDetail requestDetail = new RequestDetail();
                            requestDetail.getComplaint().setComplaintId(requestId);
                            requestDetail.getComplaint().setCompliantStatus(status);
                            requestDetail.getComplaint().setCreateDate(sdf.parse(createDate));
                            requestDetail.setSubCategory(subCategory);

                            m_adapter.add(requestDetail);
                        }
                        m_ProgressDialog.dismiss();
                        m_adapter.notifyDataSetChanged();
                    }
                    catch (JSONException je)
                    {
                        Log.e( TAG, "JSON Exception during Fetch requests-"+je.getMessage());
                    }
                    catch (ParseException pe)
                    {
                        Log.e( TAG, "Failed to parse create/update date"+pe.getMessage());
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    m_ProgressDialog.dismiss();
                    String resp = new String(responseBody);

                    if(m_requests != null && m_requests.size() > 0){
                        m_adapter.notifyDataSetChanged();
                        for(int i=0;i<m_requests.size();i++)
                            m_adapter.add(m_requests.get(i));
                    }
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

                        try {
                            JSONObject errorMsg = new JSONObject(resp);
                            errorMsg.length();
                        }
                        catch (JSONException je)
                        {
                            Log.e(TAG,"Failed to parse error response of get requests: "+je.getMessage());
                        }

                        Toast.makeText(getApplicationContext(),
                                resp, Toast.LENGTH_LONG).show();
                        //TODO:Display actual error message received
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



           // Thread.sleep(2000);
            Log.i("ARRAY", ""+ m_requests.size());
        }
        catch (Exception e) {
            Log.e("BACKGROUND_PROC", e.getMessage());
        }
       // runOnUiThread(returnRes);
    }

    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_requests != null && m_requests.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_requests.size();i++)
                    m_adapter.add(m_requests    .get(i));
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
    };

    private class RequestAdapter extends ArrayAdapter<RequestDetail> {

        private ArrayList<RequestDetail> items;

        public RequestAdapter(Context context, int textViewResourceId, ArrayList<RequestDetail> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        private Runnable returnRes = new Runnable() {

            @Override
            public void run() {
                if(m_requests != null && m_requests.size() > 0){
                    m_adapter.notifyDataSetChanged();
                    for(int i=0;i<m_requests.size();i++)
                        m_adapter.add(m_requests.get(i));
                }
                m_ProgressDialog.dismiss();
                m_adapter.notifyDataSetChanged();
            }
        };


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            RequestDetail request = items.get(position);
            if (request != null) {
                TextView tt = (TextView) v.findViewById(R.id.topText);
                TextView line2 = (TextView) v.findViewById(R.id.textLine2);
                TextView line3 = (TextView) v.findViewById(R.id.textLine3);
                if (tt != null) {
                    tt.setText("Service for: "+ request.getSubCategory());
                }
                if(line2 != null){
                    line2.setText("Service raised on: "+ request.getComplaint().getCreateDate().toString());
                }

                if(line3 != null){
                    line3.setText("Service status: "+ request.getComplaint().getCompliantStatus());
                }
            }
            return v;
        }
    }
}
