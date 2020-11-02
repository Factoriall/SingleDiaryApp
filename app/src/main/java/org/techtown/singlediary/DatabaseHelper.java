package org.techtown.singlediary;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static String NAME = "diary.db";
    public static int VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists diary("
                + " _id integer PRIMARY KEY autoincrement, "
                + " content text, "
                + " imgPath text, "
                + " date text, "
                + " weather integer, "
                + " address text, "
                + " smileGauge integer)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i1 > 1){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS diary");
        }
    }
}
