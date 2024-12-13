package com.example.colorsequencegame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "high_scores.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE high_scores ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "score INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS high_scores");
        onCreate(db);
    }

    public List<Integer> getHighScores(SQLiteDatabase database) {
        List<Integer> highScores = new ArrayList<>();
        Cursor cursor = database.query(
                "high_scores",
                new String[]{"score"},
                null,
                null,
                null,
                null,
                "score DESC",
                "5"
        );

        if (cursor.moveToFirst()) {
            do {
                int score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
                highScores.add(score);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return highScores;
    }
}
