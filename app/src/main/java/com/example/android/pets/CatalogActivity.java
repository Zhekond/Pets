/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetContract.PetsEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    ListView listView;
    private static final int PET_LOADER = 0;

    PetCursorAdapter mCursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        listView = (ListView)findViewById(R.id.list_view_pet);
        listView.setEmptyView(findViewById(R.id.empty_view));

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mCursorAdapter = new PetCursorAdapter(this,null);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(PetsEntry.CONTENT_URI,id));
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PET_LOADER,null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet(){
                PetDbHelper mDbHelper = new PetDbHelper(this);
        ContentValues  values = new ContentValues();
                values.put(PetContract.PetsEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetsEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetsEntry.COLUMN_PET_GENDER, PetContract.PetsEntry.GENDER_MALE);
        values.put(PetContract.PetsEntry.COLUMN_PET_WEIGHT,7);
        long id = ContentUris.parseId(getContentResolver().insert(PetsEntry.CONTENT_URI, values));
        if (id == -1)
            Toast.makeText(this,getString(R.string.editor_insert_pet_failed),Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,getString(R.string.editor_insert_pet_successful),Toast.LENGTH_SHORT).show();
//        mDbHelper.getWritableDatabase().insert(PetContract.PetsEntry.TABLE_NAME,null,values);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                getContentResolver().delete(PetsEntry.CONTENT_URI,null,null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection={PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED};
        return new CursorLoader(this,PetsEntry.CONTENT_URI,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    mCursorAdapter.swapCursor(null);
    }
}
