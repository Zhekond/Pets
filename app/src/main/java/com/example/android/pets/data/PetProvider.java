package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


/**
 * Created by Panda on 1/19/2018.
 */

public class PetProvider extends ContentProvider {
    private PetDbHelper mDbHelper;

    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PET_ID);
    }
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return PetContract.PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri+" with match "+match);
        }
    }
    @Override
    public int update(Uri uri, ContentValues values,String selection,String[] selectionArgs){

        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return updatePet(uri,values,selection,selectionArgs);
            case PET_ID:
                //todo extract id from uri
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for: "+uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //todo sanity check gender, weight, name, but before check if a key pair exists since we dont have to update all values every time (containsKey())
        if (values.containsKey(PetContract.PetsEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
            if (name == null)
                throw new IllegalArgumentException("Pet requires a name");
        }
        if(values.containsKey(PetContract.PetsEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetContract.PetsEntry.isValidGender(gender))
                throw new IllegalArgumentException("Pet requires valid gender");
        }
        if(values.containsKey(PetContract.PetsEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);
            if (weight !=null && weight <0)
                throw new IllegalArgumentException("Pet requires valid weight");
        }
        if(values.size()==0)
            return 0;
//        return mDbHelper.getWritableDatabase().update(PetContract.PetsEntry.TABLE_NAME,values,selection,selectionArgs);
        int rowsUpdated = database.update(PetContract.PetsEntry.TABLE_NAME,values,selection,selectionArgs);
        if(rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
    @Override
    public Uri insert(Uri uri,ContentValues values){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case (PETS):
                //todo
                return insertPet(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for "+uri);

        }
    }
    private Uri insertPet (Uri uri, ContentValues values){
        String name = values.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Pet requires a name");
        }
        Integer gender = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);
        if(gender == null || !PetContract.PetsEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires valid gender");
        }
        Integer weight = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);
        if(weight != null && weight < 0){
            throw new IllegalArgumentException("Pet requiers valid weight");
        }
        //if all senity checks are passed insert new row into the database
        long id = mDbHelper.getWritableDatabase().insert(PetContract.PetsEntry.TABLE_NAME,null,values);

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public boolean onCreate(){
        //todo asd asfasfsdg
        mDbHelper = new PetDbHelper(getContext());

        // Create and/or open a database to read from it
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return true;
    }




    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                rowsDeleted = database.delete(PetContract.PetsEntry.TABLE_NAME,selection,selectionArgs);
                break;
//                return mDbHelper.getWritableDatabase().delete(PetContract.PetsEntry.TABLE_NAME,selection,selectionArgs);
            case PET_ID:
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                return mDbHelper.getWritableDatabase().delete(PetContract.PetsEntry.TABLE_NAME,selection,selectionArgs);
                rowsDeleted = database.delete(PetContract.PetsEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }




    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                cursor = db.query(PetContract.PetsEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case PET_ID:
                selection = PetContract.PetsEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetContract.PetsEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

}
