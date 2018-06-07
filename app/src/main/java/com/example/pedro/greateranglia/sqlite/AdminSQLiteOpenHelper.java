package com.example.pedro.greateranglia.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pedro on 16/04/2018.
 */

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // CREATE TABLE ALERTS
        sqLiteDatabase.execSQL("CREATE TABLE alerts (id VARCHAR(100) PRIMARY KEY,alert VARCHAR(1000),lat VARCHAR(100),lon VARCHAR(100)) ");
        //CREATE TABLE STATIONS
        sqLiteDatabase.execSQL("CREATE TABLE stations (id VARCHAR(100) PRIMARY KEY,station VARCHAR( 100 ) NOT NULL, tpl VARCHAR( 10 ) NOT NULL , crs VARCHAR( 10 ) NOT NULL , lat REAL, lon REAL) ");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}