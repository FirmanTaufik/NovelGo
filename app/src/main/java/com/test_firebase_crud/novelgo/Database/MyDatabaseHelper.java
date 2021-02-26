package com.test_firebase_crud.novelgo.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "novel_2.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_FAVORITE = "favorite";
    private static final String COLUMN_ID_FAVORITE = "id_favorite";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_GAMBAR = "gambar";
    private static final String COLUMN_LINK = "link";

    public static final String TABEL_NONTON= "nonton";
    public static final String KOLOM_EPISODE= "id_anime";
    public static final String KOLOM_LINK ="link";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String favorite = "CREATE TABLE " + TABLE_FAVORITE +
                " (" + COLUMN_ID_FAVORITE + " TEXT, " +
                COLUMN_TITLE + " TEXT, "+
                COLUMN_GAMBAR + " TEXT, "+
                COLUMN_LINK + " TEXT); ";

        db.execSQL(favorite);

        db.execSQL(" CREATE TABLE " + TABEL_NONTON + " (" +
                KOLOM_EPISODE + " TEXT NOT NULL, " +
                KOLOM_LINK + " TEXT NOT NULL);"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABEL_NONTON);
        onCreate(db);
    }


    public void tambahFavourite(String id, String title, String gambar,String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID_FAVORITE,id);
        cv.put(COLUMN_TITLE,title);
        cv.put(COLUMN_GAMBAR,gambar);
        cv.put(COLUMN_LINK,link);

        long result = db.insert(TABLE_FAVORITE,null, cv);

        if(result == -1){
            Toast.makeText(context, "Failed to Save", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Successfully to Save", Toast.LENGTH_SHORT).show();


        }
    }

    

    public Cursor readAllDataFavorite(){
        String query = "SELECT * FROM " + TABLE_FAVORITE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }


    public void deleteOneRowFavorite(String link){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_FAVORITE, COLUMN_LINK+" = ?", new String[]{link});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();


        }
    }

    public int getTontonCount(String episode, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABEL_NONTON + " WHERE  "+KOLOM_LINK+" = "+ "'" +link +"'" ;
        //   String query = "SELECT  * FROM " + TABEL_NONTON  ;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void simpanNonton(String episode, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KOLOM_EPISODE, episode);
        cv.put(KOLOM_LINK, link);
        long result = db.insert(TABEL_NONTON, null, cv);

        if (result == -1) {
            Log.i("dbhelpher", "Failed to Save");
            //Toast.makeText(context, "Failed to Save", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("dbhelpher", "Successfully to Save");
            //      Toast.makeText(context, "Successfully to Save", Toast.LENGTH_SHORT).show();


        }
    }




}