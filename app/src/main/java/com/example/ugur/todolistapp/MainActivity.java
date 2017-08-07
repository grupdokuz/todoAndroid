package com.example.ugur.todolistapp;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import android.support.v7.app.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.*;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import java.io.*;
import android.app.Activity;
import android.app.ProgressDialog;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pusher.android.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.*;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.google.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.*;
import android.widget.AdapterView.OnItemClickListener;


public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String TAG = MainActivity.class.getSimpleName(),editLabelText;
    private ListView lv;
    private Button button;
    private EditText textLabel;
    ArrayList<HashMap<String, String>> contactList;
    public static void main(String[] args){


        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("764b61bac1147dd219b1", options);

        Channel channel = pusher.subscribe("my-channel");

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                System.out.println(data);
            }
        });

        pusher.connect();

    }


    private String[] ulkeler =
            {"Türkiye", "Almanya", "Avusturya", "Amerika","İngiltere",
                    "Macaristan", "Yunanistan", "Rusya", "Suriye", "İran", "Irak",
                    "Şili", "Brezilya", "Japonya", "Portekiz", "İspanya",
                    "Makedonya", "Ukrayna", "İsviçre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (playServicesAvailable()) {
            //PusherAndroidOptions options = new PusherAndroidOptions().setCluster(<pusher_app_cluster>);
            //PusherAndroid pusher = new PusherAndroid(<pusher_api_key>);
            //PushNotificationRegistration nativePusher = pusher.nativePusher();
            //String defaultSenderId = getString(R.string.gcm_defaultSenderId); // fetched from your google-services.json
            //nativePusher.registerFCM(this, defaultSenderId);
        } else {
            // ... log error, or handle gracefully
        }

        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.lvItems);

        new GetContacts().execute("show all");
        textLabel=(EditText)findViewById(R.id.etNewItem);
        button = (Button) findViewById(R.id.btnAddItem);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editLabelText=textLabel.getText().toString();
                new GetContacts().execute("add todo");
            }
        });



        final ArrayAdapter<String> veriAdaptoru=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, ulkeler);
        lv.setAdapter(veriAdaptoru);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                editLabelText=textLabel.getText().toString();
                new GetContacts().execute("delete todo");

                AlertDialog.Builder diyalogOlusturucu =
                        new AlertDialog.Builder(MainActivity.this);

                //diyalogOlusturucu.setMessage(ulkeler[position]);

                String itemId = ""+contactList.get(position);
                diyalogOlusturucu.setMessage(itemId);

                diyalogOlusturucu.create().show();

            }
        });



       /* lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.findViewById(R.id.btnDeleteItem).setVisibility(View.VISIBLE);
                editLabelText=textLabel.getText().toString();
                new GetContacts().execute("delete todo");
                lv.getItemAtPosition(position);

                return false;
            }
        });*/

    }
    private boolean playServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private class GetContacts extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                Toast.makeText(MainActivity.this,"Connection is setup",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(String... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://192.168.1.28:3001/api/todos";
            if(arg0[0].equals("show all")) {
                String jsonStr = sh.makeServiceCall(url);

                Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONArray deneme = new JSONArray(jsonStr);

                        // Getting JSON Array node
                        JSONArray contacts = deneme;

                        // looping through All Contacts
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            String id = c.getString("id");
                            String title = c.getString("title");

                            // Phone node is JSON Object


                            // tmp hash map for single contact
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value
                            contact.put("id", id);
                            contact.put("title", title);

                            // adding contact to contact list
                            contactList.add(contact);
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else if(arg0[0].equals("add todo")){
                String id=sh.postNewItem(url,editLabelText);
                HashMap<String, String> contact = new HashMap<>();

                // adding each child node to HashMap key => value
                contact.put("id", id);
                contact.put("title", editLabelText);

                // adding contact to contact list
                contactList.add(contact);
            }
            else if(arg0[0].equals("delete todo")){
                String id=sh.deleteItem(url,editLabelText);
                HashMap<String, String> contact = new HashMap<>();

                contact.remove(id);// removing contact from contact list
                contactList.remove(contact);
                  }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{ "id","title"},
                    new int[]{R.id.id, R.id.title});
            lv.setAdapter(adapter);
        }

    }
}