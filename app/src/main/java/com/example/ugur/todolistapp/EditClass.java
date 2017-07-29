package com.example.ugur.todolistapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_layout);
    }

    public void doneButtonClicked(View v){
        String taskText = ((EditText)findViewById(R.id.task)).getText().toString();

        if(taskText.equals("")){

        }else{
            Intent intent = new Intent();
            intent.putExtra(IntentClass.INTENT_TASK_FIELD,taskText);
            setResult(IntentClass.INTENT_RESULT_CODE,intent);
            finish();
        }
    }
}