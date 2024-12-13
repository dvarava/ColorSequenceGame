package com.example.colorsequencegame;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager motionManager;
    private Sensor motionSensor;
    private TextView statusTextView;
    private List<Integer> sequence;
    private int currentIndex;
    private boolean isGameActive;
    private int totalScore;
    private TextView logsTextView;

    private Handler delayHandler = new Handler();
    private boolean isWaitingForInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        statusTextView = findViewById(R.id.instructionTextView);
        logsTextView = findViewById(R.id.logsTextView);

        sequence = getIntent().getIntegerArrayListExtra("sequence");
        currentIndex = 0;
        isGameActive = true;
        totalScore = getIntent().getIntExtra("score", 0);

        motionManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (motionManager != null) {
            motionSensor = motionManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        statusTextView.setText("Move your phone to match the pattern!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (motionSensor != null) {
            motionManager.registerListener(this, motionSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        motionManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isGameActive || isWaitingForInput) return;

        float axisX = event.values[0];
        float axisY = event.values[1];

        isWaitingForInput = true;

        delayHandler.postDelayed(() -> {
            int tiltDirection = determineDirection(axisX, axisY);
            if (tiltDirection != -1) {
                String directionName = "";
                switch (tiltDirection) {
                    case 0: directionName = "Red"; break;
                    case 1: directionName = "Blue"; break;
                    case 2: directionName = "Green"; break;
                    case 3: directionName = "Yellow"; break;
                }

                String tiltMessage = directionName;
                logsTextView.setText(tiltMessage);

                if (tiltDirection == sequence.get(currentIndex)) {
                    currentIndex++;
                    totalScore += 4;

                    if (currentIndex == sequence.size()) {
                        advanceToNextRound(totalScore);
                    }
                } else {
                    endGame();
                }
            }

            isWaitingForInput = false;

        }, 1000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private int determineDirection(float x, float y) {
        if (x < -3.2) return 0; // Up
        if (x > 3.2) return 1;  // Down
        if (y > 3.2) return 2;  // Right
        if (y < -3.2) return 3;  // Left
        return -1;
    }

    private void advanceToNextRound(int updatedScore) {
        isGameActive = false;
        statusTextView.setText("Great! Proceeding to the next sequence...");
        Intent nextIntent = new Intent(PlayActivity.this, SequenceActivity.class);
        nextIntent.putIntegerArrayListExtra("sequence", new ArrayList<>(sequence));
        nextIntent.putExtra("score", updatedScore);
        startActivity(nextIntent);
        finish();
    }

    private void endGame() {
        isGameActive = false;
        statusTextView.setText("Game Over! Final Score: " + totalScore);
        Intent endIntent = new Intent(PlayActivity.this, GameOverActivity.class);
        endIntent.putExtra("score", totalScore);
        startActivity(endIntent);
        finish();
    }
}