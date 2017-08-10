package com.example.ugur.todolistapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String itemId;
    int position;
    ArrayList<HashMap<String, String>> contactList;
    private String TAG = MainActivity.class.getSimpleName(), editLabelText,date="";
    private ListView lv;
    private Button newButton, deleteButton,calendarButton;
    private EditText textLabel;
    DatePickerFragment newFragment;
    TimePickerFragment newFragmentTime;

    public static void main(String[] args) {


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, RegistrationService.class);
        startService(i);
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
        textLabel = (EditText) findViewById(R.id.etNewItem);
        newButton = (Button) findViewById(R.id.btnAddItem);
        deleteButton = (Button) findViewById(R.id.btnDeleteItem);

        calendarButton = (Button) findViewById(R.id.btnCalendar);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                 newFragmentTime = new TimePickerFragment();
                newFragmentTime.show(getSupportFragmentManager(), "timePicker");

            }
        });
        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(newFragmentTime.hour+":"+newFragmentTime.minute);
                if(newFragmentTime.hour>12){
                    date=newFragment.month+" "+newFragment.day+", "+newFragment.year+" "+(newFragmentTime.hour-12)
                            +":"+newFragmentTime.minute+" PM";}
                else{
                    date=newFragment.month+" "+newFragment.day+", "+newFragment.year+" "+newFragmentTime.hour
                            +":"+newFragmentTime.minute+" AM";
                }
                System.out.println(date);
                editLabelText = textLabel.getText().toString();
                new GetContacts().execute("add todo");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                itemId = "" + contactList.get(position).get("id");
                contactList.remove(position);
                new GetContacts().execute("delete todo");
            }
        });


        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1);
        lv.setAdapter(dataAdapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int itemPosition,
                                    long id) {

                position = itemPosition;
                parent.getChildAt(position).setBackgroundColor(Color.GRAY);


            }
        });

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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        int year ;
        String month ;
        int day;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }

        public void onDateSet(DatePicker view, int year1, int month1, int day1) {
            // Do something with the date chosen by the user
            year = view.getYear();
            month=new DateFormatSymbols(Locale.ENGLISH).getMonths()[view.getMonth()];
            day = view.getDayOfMonth();
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        int hour, minute;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
            // Do something with the time chosen by the user
            hour=hourOfDay;
            minute=minute1;
        }
    }

    private class GetContacts extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Connection is setup", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(String... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://10.4.43.130:3001/api/todos";
            if (arg0[0].equals("show all")) {
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
            } else if (arg0[0].equals("add todo")) {
                String id = sh.postNewItem(url, editLabelText,date);
                HashMap<String, String> contact = new HashMap<>();

                // adding each child node to HashMap key => value
                contact.put("id", id);
                contact.put("title", editLabelText);

                // adding contact to contact list
                contactList.add(contact);
            } else if (arg0[0].equals("delete todo")) {
                sh.deleteItem(url, itemId);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"id", "title"},
                    new int[]{R.id.id, R.id.title});
            lv.setAdapter(adapter);
        }

    }
}