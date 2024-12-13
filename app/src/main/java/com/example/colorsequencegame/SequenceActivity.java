package com.example.colorsequencegame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SequenceActivity extends AppCompatActivity {

    private List<Integer> patternSequence;
    private FrameLayout buttonUp, buttonDown, buttonRight, buttonLeft;
    private TextView infoTextView;
    private Handler delayHandler;
    private int currentIndex;
    private int totalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        buttonUp = findViewById(R.id.buttonUp);
        buttonDown = findViewById(R.id.buttonDown);
        buttonRight = findViewById(R.id.buttonRight);
        buttonLeft = findViewById(R.id.buttonLeft);
        infoTextView = findViewById(R.id.infoTextView);

        patternSequence = new ArrayList<>();
        delayHandler = new Handler();

        totalScore = getIntent().getIntExtra("score", 0);

        createPattern(4);
        showPattern();
    }

    private void createPattern(int length) {
        Random randomizer = new Random();
        patternSequence.clear();
        for (int i = 0; i < length; i++) {
            patternSequence.add(randomizer.nextInt(4));
        }
    }

    private void showPattern() {
        currentIndex = 0;
        infoTextView.setText("Pay attention to the pattern!");

        for (int i = 0; i < patternSequence.size(); i++) {
            final int stepIndex = i;
            delayHandler.postDelayed(() -> highlightStep(patternSequence.get(stepIndex)), i * 1000L);
        }

        delayHandler.postDelayed(() -> {
            Intent playIntent = new Intent(SequenceActivity.this, PlayActivity.class);
            playIntent.putIntegerArrayListExtra("sequence", new ArrayList<>(patternSequence));
            playIntent.putExtra("score", totalScore);
            startActivity(playIntent);
            finish();
        }, patternSequence.size() * 1000L);
    }

    private void highlightStep(int step) {
        resetButtonColors();

        switch (step) {
            case 0:
                buttonUp.setBackgroundColor(getResources().getColor(R.color.lightred));
                break;
            case 1:
                buttonDown.setBackgroundColor(getResources().getColor(R.color.lightblue));
                break;
            case 2:
                buttonRight.setBackgroundColor(getResources().getColor(R.color.lightgreen));
                break;
            case 3:
                buttonLeft.setBackgroundColor(getResources().getColor(R.color.lightyellow));
                break;
        }

        delayHandler.postDelayed(this::resetButtonColors, 500L);
    }

    private void resetButtonColors() {
        buttonUp.setBackgroundColor(getResources().getColor(R.color.red));
        buttonDown.setBackgroundColor(getResources().getColor(R.color.blue));
        buttonRight.setBackgroundColor(getResources().getColor(R.color.green));
        buttonLeft.setBackgroundColor(getResources().getColor(R.color.yellow));
    }
}