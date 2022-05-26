package com.example.registerlogin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper
{
    private  static  final  int DB_VERSION = 1;
    private  static  final String DB_NAME = "hallymplay.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS Enroll (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, price TEXT NOT NULL, nextDate TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }

    //SELECT 문
    public ArrayList<EnrollItem> getEnrollList() {
        ArrayList<EnrollItem> enrollItems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Enroll ORDER BY nextDate DESC", null);
        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {

                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                @SuppressLint("Range") String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                @SuppressLint("Range") String nextDate = cursor.getString(cursor.getColumnIndexOrThrow("nextDate"));

                EnrollItem enrollItem = new EnrollItem();
                enrollItem.setId(id);
                enrollItem.setTitle(title);
                enrollItem.setPrice(price);
                enrollItem.setNextDate(nextDate);
                enrollItems.add(enrollItem);
            }
        }
        cursor.close();

        return enrollItems;
    }

    //INSERT 문
    public void InsertEnroll(String _title, String _price, String _nextDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO Enroll (title, price, nextDate) VALUES('" + _title + "', '" + _price + "', '" + _nextDate + "');");
    }

    // UPDATE 문
    public void UpdateEnroll(String _title, String _price, String _nextDate, int _id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE Enroll SET title='" + _title + "', price='" + _price + "', nextDate='" + _nextDate + "' WHERE id='" + _id + "'");
    }

    // DELETE 문
    public void DeleteEnroll(int _id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM Enroll WHERE id = '" + _id + "'");
    }
}
