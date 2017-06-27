package com.helpers;

import android.util.Log;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Gajanan Patil on 3/8/2017.
 */

public class HttpHandler {
    private static final String TAG = "HttpHandler";

    public HttpHandler(){

    }

    public String makeServiceCall(String reqUrl, String requestMethod ){
        String response = "";
        try
        {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);

            // Read response from web service

            InputStream input = new BufferedInputStream(conn.getInputStream());
            response = convertInputStreamToString(input);

        }
        catch(MalformedURLException ex)
        {
            Log.e(TAG,"MalformedURLException:" + ex.getMessage() );
        }
        catch (ProtocolException ex)
        {
            Log.e(TAG,"ProtocolException:" + ex.getMessage() );
        }
        catch (IOException ex)
        {
            Log.e(TAG,"IOException:" + ex.getMessage() );
        }
        return response;
    }

    public String performPostCall(String requestURL, HashMap<String,String> params ) throws  IOException,Exception
    {
        URL url = new URL(requestURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String response = "";
        try
        {
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            //urlConnection.setRequestProperty("Content-Type", "application/json");
            //urlConnection.setRequestProperty("Content-Length",""+requestBody.length());

            OutputStream out = new BufferedOutputStream( urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( out));
            String postData = getPostDataString( params );
            writer.write(postData);

            if( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                response = convertInputStreamToString( urlConnection.getInputStream());
            }
            else
            {
                response = convertInputStreamToString(urlConnection.getErrorStream());
            }
        }
        finally {
            urlConnection.disconnect();
        }

        return response;
    }

    public String performPostCall(String requestURL, String params ) throws  IOException,Exception
    {
        URL url = new URL(requestURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String response = "";
        try
        {
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            //urlConnection.setRequestProperty("Content-Type", "application/json");
            //urlConnection.setRequestProperty("Content-Length",""+requestBody.length());

            OutputStream out = new BufferedOutputStream( urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( out));
            //String postData = getPostDataString( params );
            writer.write( params );

            if( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                response = convertInputStreamToString( urlConnection.getInputStream());
            }
            else
            {
                response = convertInputStreamToString(urlConnection.getErrorStream());
            }
        }
        finally {
            urlConnection.disconnect();
        }

        return response;
    }

    private String getPostDataString( HashMap<String,String> params) throws  Exception {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for( Map.Entry<String,String> entry: params.entrySet())
        {
            if (first)
                first = false;
            else
                stringBuilder.append("&");

            stringBuilder.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
        }

        return stringBuilder.toString();
    }

    private String convertInputStreamToString(InputStream istream)
    {

        BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
        StringBuilder strb = new StringBuilder();
        String line ="";

        try
        {
            while((line = reader.readLine()) != null)
                strb.append(line).append('\n');
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }
        finally {
            try
            {
                istream.close();
            }
            catch (IOException ie) {
                ie.printStackTrace();
            }
        }
        return strb.toString();
    }
}
