package com.example.ugur.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by ugur on 20.07.2017.
 */


public class DbHelper extends SQLiteOpenHelper {

    public static final String dbName = "DataBase";
    private static final int  dbVersion = 1;
    public static final String dbTable = "Task";
    public static final String dbColumn = "TaskName";

    public DbHelper(Context context){
        super(context,dbName,null,dbVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL",dbTable,dbColumn);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s",dbTable);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertTask(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(dbColumn,task);
        db.insertWithOnConflict(dbTable,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
    public void deleteTask(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(dbTable,dbColumn +" - ?",new String [] {task});
        db.close();
    }
    ///////////////////////////////////////////////////////////
    public ArrayList<String> getTaskList(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> taskList = new ArrayList<>();


        db.close();
        return taskList;
    }
    //////////////////////////////////////////////////////////////
}
