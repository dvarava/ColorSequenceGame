package com.example.colorsequencegame;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HighScoreActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout highScoresContainer = findViewById(R.id.scoresLayout);
        Button returnButton = findViewById(R.id.playAgainButton);

        populateHighScores(highScoresContainer);

        returnButton.setOnClickListener(v -> {
            Intent mainMenuIntent = new Intent(HighScoreActivity.this, MainActivity.class);
            startActivity(mainMenuIntent);
            finish();
        });
    }

    private void populateHighScores(LinearLayout highScoresContainer) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor resultCursor = db.query(
                "high_scores",
                null,
                null,
                null,
                null,
                null,
                "score DESC",
                "5"
        );

        if (resultCursor.moveToFirst()) {
            do {
                String playerName = resultCursor.getString(resultCursor.getColumnIndexOrThrow("name"));
                int playerScore = resultCursor.getInt(resultCursor.getColumnIndexOrThrow("score"));

                TextView scoreEntryView = new TextView(this);
                scoreEntryView.setText(String.format("%s - %d", playerName, playerScore));
                scoreEntryView.setTextSize(18);
                scoreEntryView.setTextColor(getResources().getColor(android.R.color.white));

                highScoresContainer.addView(scoreEntryView);
            } while (resultCursor.moveToNext());
        }

        resultCursor.close();
    }
}