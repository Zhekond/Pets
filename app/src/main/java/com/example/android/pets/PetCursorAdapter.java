package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

/**
 * Created by Panda on 2/4/2018.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor){
        //todo bind
        TextView tvName = (TextView)view.findViewById(R.id.nameId);
        TextView tvBreed = (TextView)view.findViewById(R.id.breedId);
        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetsEntry.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetsEntry.COLUMN_PET_BREED));
        tvName.setText(name);
        if(TextUtils.isEmpty(breed)){
            tvBreed.setText(R.string.no_breed);
        }else {
            tvBreed.setText(breed);
        }
    }
}
