package com.example.ugur.todolistapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    //DbHelper dbHelper;
    String taskText;
    ArrayList<String> arrayList;
    ArrayAdapter<String>arrayAdapter;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setContentView(R.layout.big_layout);

        //dbHelper = new DbHelper(this);
        listView = (ListView)findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
      //  loadTaskList();
    }

    public void onClick(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,EditClass.class);
        startActivityForResult(intent, IntentClass.INTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==IntentClass.INTENT_RESULT_CODE){
            taskText=data.getStringExtra(IntentClass.INTENT_TASK_FIELD);
            arrayList.add(taskText);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    //////////////////////////////////////
    /*private void loadTaskList(){
        ArrayList<String> taskList = dbHelper.getTaskList();


        //Adapter add and clear
    }*/
    //////////////////////////////////////

}



