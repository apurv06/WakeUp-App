package com.example.apurv.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Apurv on 7/5/2018.
 */


public class todoOpenHelper extends SQLiteOpenHelper {


    public todoOpenHelper(Context context) {
        super(context,Contract.Table_name, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql="create table "+Contract.Table_name+" ("
                +Contract.id+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +Contract.title+" TEXT,"
                +Contract.description+" TEXT,"
                +Contract.notification_set+" TEXT,"
                +Contract.repeating+" TEXT,"
                +Contract.date+" NUMERIC)";

        sqLiteDatabase.execSQL(sql);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
