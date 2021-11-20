package com.ladopoulos.rart;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class SingleFavoriteActivity extends AppCompatActivity {
    DBHelper mydb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_favorite);
        mydb = new DBHelper(this);

        Bundle b = getIntent().getExtras();
        assert b != null;
        int id = b.getInt("id");
        cursor = mydb.getData(id);
        cursor.moveToFirst();

        String paintingName = cursor.getString(cursor.getColumnIndex(DBHelper.CONTACTS_COLUMN_PAINTING_NAME));
        String artistName = cursor.getString(cursor.getColumnIndex(DBHelper.CONTACTS_COLUMN_ARTIST_NAME));
        String year = cursor.getString(cursor.getColumnIndex(DBHelper.CONTACTS_COLUMN_YEAR));
        String culture = cursor.getString(cursor.getColumnIndex(DBHelper.CONTACTS_COLUMN_CULTURE));
        String link = cursor.getString(cursor.getColumnIndex(DBHelper.CONTACTS_COLUMN_LINK));

        if (!cursor.isClosed())  {
            cursor.close();
        }

        Log.e("Single Favorite", paintingName);

    }
}
