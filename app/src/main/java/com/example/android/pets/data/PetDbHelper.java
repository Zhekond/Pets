package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Panda on 1/16/2018.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = PetDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;

    public PetDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
//        super.onCreate(sqLiteDatabase);
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetContract.PetsEntry.TABLE_NAME + " ( "+
                PetContract.PetsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PetContract.PetsEntry.COLUMN_PET_NAME+" TEXT NOT NULL, " +
                PetContract.PetsEntry.COLUMN_PET_BREED +" TEXT, "+
                PetContract.PetsEntry.COLUMN_PET_GENDER+ " INTEGER NOT NULL, "+
                PetContract.PetsEntry.COLUMN_PET_WEIGHT+ " INTEGER NOT NULL DEFAULT 0);";
        Log.i(LOG_TAG, SQL_CREATE_PETS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //
    }
}
