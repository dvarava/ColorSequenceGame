package com.example.colorsequencegame;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class GameOverActivity extends AppCompatActivity {

    private int finalScore;
    private SQLiteDatabase dbConnection;
    private boolean isTopScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        finalScore = getIntent().getIntExtra("score", 0);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        dbConnection = databaseHelper.getWritableDatabase();

        TextView scoreDisplay = findViewById(R.id.scoreTextView);
        EditText playerNameInput = findViewById(R.id.nameEditText);
        Button submitScoreButton = findViewById(R.id.saveButton);
        Button viewHighScoresButton = findViewById(R.id.hiScoreButton);

        scoreDisplay.setText("Your Score: " + finalScore);

        isTopScore = isHighScore();

        if (isTopScore) {
            playerNameInput.setVisibility(View.VISIBLE);
            submitScoreButton.setVisibility(View.VISIBLE);
        } else {
            playerNameInput.setVisibility(View.GONE);
            submitScoreButton.setVisibility(View.GONE);
        }

        submitScoreButton.setOnClickListener(v -> {
            String playerName = playerNameInput.getText().toString().trim();
            if (!playerName.isEmpty()) {
                saveScoreToDatabase(playerName, finalScore);
                playerNameInput.setVisibility(View.GONE);
                submitScoreButton.setVisibility(View.GONE);
            }
        });

        viewHighScoresButton.setOnClickListener(v -> {
            Intent highScoresIntent = new Intent(GameOverActivity.this, HighScoreActivity.class);
            startActivity(highScoresIntent);
            finish();
        });
    }

    private boolean isHighScore() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Integer> topScores = databaseHelper.getHighScores(dbConnection);
        return topScores.size() < 5 || finalScore > topScores.get(topScores.size() - 1);
    }

    private void saveScoreToDatabase(String playerName, int score) {
        ContentValues scoreEntry = new ContentValues();
        scoreEntry.put("name", playerName);
        scoreEntry.put("score", score);
        dbConnection.insert("high_scores", null, scoreEntry);
    }
}