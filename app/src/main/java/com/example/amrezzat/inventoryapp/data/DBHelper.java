package com.example.amrezzat.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AmrEzzat on 12/3/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    //init DBHelper version and name of DB
    public DBHelper(Context context) {
        super(context, "productsApp.db", null, 3);
    }

    //create DB table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Contract.EntryData.NAME_OF_TABLE + " ("
                + Contract.EntryData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.EntryData.COLUMN_NAME + " TEXT NOT NULL, "
                + Contract.EntryData.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + Contract.EntryData.COLUMN_PRICE + " REAL NOT NULL, "
                + Contract.EntryData.COLUMN_DISCRIPTION + " TEXT NOT NULL, "
                + Contract.EntryData.COLUMN_PICTURE + " TEXT NOT NULL"
                + " );");
    }

    //on db version update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //wiping  data when Version up.
        db.execSQL("DROP TABLE IF EXISTS " + Contract.EntryData.NAME_OF_TABLE);
        //refactor DB table
        onCreate(db);
    }
}
