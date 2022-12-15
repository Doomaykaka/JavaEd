package com.example.lab5;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "spaceport.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "spaceman"; // название таблицы в бд

    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static String COLUMN_NAME = "name";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_RANK = "rank";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE spaceman (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
                + " TEXT, " + COLUMN_YEAR + " INTEGER," + COLUMN_AGE
                + " INTEGER, " + COLUMN_RANK + " TEXT) ;");

        // добавление начальных данных
        //db.execSQL("INSERT INTO " + TABLE + " (" + COLUMN_NAME
        //+ ", " + COLUMN_YEAR + ", " + COLUMN_AGE + ", " + COLUMN_RANK + ") VALUES ('Том', 1981, 20, 'A');");

        //УДАЛИТЬ ФАЙЛ БД!!!
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME,"Ivan");
        cv.put(COLUMN_YEAR, 2001);
        cv.put(COLUMN_AGE, 21);
        cv.put(COLUMN_RANK, "A");
        db.insert(TABLE, null, cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            //db.execSQL("ALTER TABLE " + TABLE + " RENAME COLUMN " + COLUMN_RANK + " TO РАНГ;");
            //onCreate(db);
        }

    }
}
