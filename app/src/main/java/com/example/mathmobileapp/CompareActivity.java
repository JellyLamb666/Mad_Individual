package com.example.mathmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class CompareActivity extends AppCompatActivity {

    private TextView num1, num2, feedbackText;
    private Button btnGreater, btnLess;
    private int number1, number2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        feedbackText = findViewById(R.id.feedbackText);
        btnGreater = findViewById(R.id.btnGreater);
        btnLess = findViewById(R.id.btnLess);

        generateNewNumbers(); // Generate random numbers

        btnGreater.setOnClickListener(v -> checkAnswer(true));
        btnLess.setOnClickListener(v -> checkAnswer(false));
    }

    private void checkAnswer(boolean isGreater) {
        boolean isCorrect = (isGreater && number1 > number2) || (!isGreater && number1 < number2);

        // Load fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        feedbackText.startAnimation(fadeIn);

        if (isCorrect) {
            feedbackText.setText("🎉 Correct !\n" + number1 + (isGreater ? " is greater than " : " is less than ") + number2);
            feedbackText.setTextColor(ContextCompat.getColor(this, R.color.green));

            // Generate new numbers after a short delay
            feedbackText.postDelayed(this::generateNewNumbers, 2000);
        } else {
            feedbackText.setText("❌ Try Again !\n" + number1 + (isGreater ? " is NOT greater than " : " is NOT less than ") + number2);
            feedbackText.setTextColor(ContextCompat.getColor(this, R.color.red));

            // Load and start shake animation when answer is incorrect
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            feedbackText.startAnimation(shake);
        }
    }


    private void generateNewNumbers() {
        Random random = new Random();
        number1 = random.nextInt(20) + 1; // Random number between 1-20
        number2 = random.nextInt(20) + 1; // Random number between 1-20

        // Ensure both numbers are not the same
        while (number1 == number2) {
            number2 = random.nextInt(20) + 1;
        }

        num1.setText(String.valueOf(number1));
        num2.setText(String.valueOf(number2));
        feedbackText.setText(""); // Clear feedback for the next question
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
        finish();
    }
}