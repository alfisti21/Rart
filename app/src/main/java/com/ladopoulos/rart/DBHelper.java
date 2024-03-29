package com.ladopoulos.rart;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Favorites.db";
    private static final String CONTACTS_TABLE_NAME = "paintings";
    public static final String CONTACTS_COLUMN_ID = "id";
    static final String CONTACTS_COLUMN_PAINTING_NAME = "paintingName";
    static final String CONTACTS_COLUMN_ARTIST_NAME = "artistName";
    static final String CONTACTS_COLUMN_YEAR = "year";
    static final String CONTACTS_COLUMN_CULTURE = "culture";
    static final String CONTACTS_COLUMN_LINK = "link";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table paintings " +
                        "(id integer primary key, paintingName text, artistName text, year text, culture text, link text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS paintings");
        onCreate(db);
    }

    void insertPainting(String paintingName, String artistName, String year, String culture, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("paintingName", paintingName);
        contentValues.put("artistName", artistName);
        contentValues.put("year", year);
        contentValues.put("culture", culture);
        contentValues.put("link", link);
        db.insert("paintings", null, contentValues);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from paintings where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updatePainting (Integer id, String paintingName, String artistName, String year, String culture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("paintingName", paintingName);
        contentValues.put("artistName", artistName);
        contentValues.put("year", year);
        contentValues.put("culture", culture);
        db.update("paintings", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deletePainting (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("paintings",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    ArrayList<String> getAllPaintings() {
        ArrayList<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from paintings", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndexOrThrow(CONTACTS_COLUMN_PAINTING_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
