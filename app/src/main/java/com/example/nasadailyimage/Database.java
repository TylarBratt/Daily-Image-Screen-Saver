package com.example.nasadailyimage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;


public class Database extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "MyDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "NASAIMAGES";
    public final static String COL_NAME = "NAME";
    public final static String COL_IMG = "IMG";
    public final static String COL_DATE = "DATE";
    private SQLiteDatabase db;
    private UUID id;
    public Database(Context cont) { super(cont, DATABASE_NAME, null, VERSION_NUM);}


    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL( "CREATE TABLE " + TABLE_NAME + "(DATE PRIMARY KEY , "
                +COL_IMG + ", COMPRESSEDIMAGE, HEIGHT , WIDTH)");
    db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES('100', '1', '1', '1', '1')");
    db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES('200', '1', '1', '1', '1')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void loadToDatabase(String date, String image, byte[] compressedImage, int height, int width){
        String queryString = "INSERT INTO " + TABLE_NAME + " VALUES('" + date + "', '" + image + "', '" + compressedImage + "', '" + height + "', '" + width + "')";
        SQLiteDatabase writable = this.getWritableDatabase();
        writable.execSQL(queryString);
        writable.close();
    }
    public String getImage(String date){
        String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE DATE = " + date;
        String image;

        SQLiteDatabase datab = this.getReadableDatabase();
        Cursor cursor = datab.rawQuery(queryString,null);


        if (cursor.moveToFirst()){
            image = cursor.getString(1);
        }else{image = "No Image";}


        cursor.close();
        datab.close();
        return image;
}
    public Bitmap getCompressedImage(String date){
        String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE DATE = '" + date + "'";
        byte[] compressedImage = null;
        int height = 0;
        int width = 0;
        SQLiteDatabase datab = this.getReadableDatabase();
        Cursor cursor = datab.rawQuery(queryString,null);


        if (cursor.moveToFirst()){
            compressedImage = cursor.getBlob(2);
            height = Integer.valueOf(cursor.getString(3));
            width = Integer.valueOf(cursor.getString(4));
        }

        cursor.close();
        datab.close();
        Bitmap bitmap = null;
        return bitmap;

    }
    public ArrayList<String> getSavedImages(){
        String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE DATE IS NOT NULL;";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);

        int total = cursor.getCount();
        ArrayList<String> savedDates = new ArrayList<>();
        cursor.moveToFirst();
        for(int i = 1; i <= total; i++){
            savedDates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
return savedDates;
 }
}