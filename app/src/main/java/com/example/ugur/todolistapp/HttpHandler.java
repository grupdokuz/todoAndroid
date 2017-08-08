package com.example.ugur.todolistapp;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    public String postNewItem(String reqUrl, String todoTitle) {

        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
        String ret = "";
        try {
            HttpPost request = new HttpPost(reqUrl);
            StringEntity params = new StringEntity("{ \"todo\": { \"title\": \"" + todoTitle + "\", \"created_by\": \"enes\" } }");
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            //handle response here...
            ret = response.toString();
            System.out.println(ret);
            ret = ret.substring(ret.indexOf(":3001/todos/") + 12, ret.indexOf(", Content-Type"));

        } catch (Exception ex) {
            //handle exception here

        } finally {
            //Deprecated
            //httpClient.getConnectionManager().shutdown();
        }
        return ret;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public String deleteItem(String reqUrl, String id) {
        String result = "";
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try {
            HttpDelete request = new HttpDelete(reqUrl + "/" + id);
            HttpResponse response = httpClient.execute(request);
            result = response.toString();
        } catch (Exception ex) {
        } finally {

        }
        return result;
    }
}
