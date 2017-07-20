package com.example.ugur.todolistapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    DbHelper dbHelper;
    ArrayList<String> arrayList;
    ArrayAdapter<String>arrayAdapter;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setContentView(R.layout.big_layout);

        dbHelper = new DbHelper(this);
        listView = (ListView)findViewById(R.id.listView);
      //  loadTaskList();
    }
    //////////////////////////////////////
    private void loadTaskList(){
        ArrayList<String> taskList = dbHelper.getTaskList();


        //Adapter add and clear
    }
    //////////////////////////////////////

}



