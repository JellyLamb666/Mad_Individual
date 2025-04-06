package com.example.mathmobileapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.TypedValue;
import java.util.Random;

public class ComposingActivity extends AppCompatActivity {

    private TextView questionText, feedbackText;
    private Button firstSlot, secondSlot, submitButton;
    private ImageView operatorImage;
    private int correctAnswer;
    private char operator;
    private GridLayout numberGrid;

    // Declare validPairs to store possible answers
    private final java.util.List<int[]> validPairs = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composing);

        questionText = findViewById(R.id.questionText);
        firstSlot = findViewById(R.id.firstSlot);
        secondSlot = findViewById(R.id.secondSlot);
        operatorImage = findViewById(R.id.operatorImage);
        submitButton = findViewById(R.id.submitButton);
        feedbackText = findViewById(R.id.feedbackText);
        numberGrid = findViewById(R.id.numberGrid);

        generateNewQuestion();
        createNumberButtons();
        setDragListeners();

        submitButton.setOnClickListener(v -> checkAnswer());
    }

    private void generateNewQuestion() {
        Random random = new Random();
        int target = random.nextInt(9) + 2; // Ensure target is at least 2
        int numA = 0, numB = 0;

        int operation = random.nextInt(4); // Randomly pick an operation (0: +, 1: -, 2: ×, 3: ÷)

        switch (operation) {
            case 0: // Addition
                operator = '+';
                numA = random.nextInt(target - 1) + 1; // Ensure numA is at least 1
                numB = target - numA;
                operatorImage.setImageResource(R.drawable.ic_plus);
                break;

            case 1: // Subtraction
                operator = '-';
                numA = random.nextInt(9) + 2; // numA must be at least 2
                numB = random.nextInt(numA - 1) + 1; // numB must be < numA
                target = numA - numB;
                operatorImage.setImageResource(R.drawable.ic_minus);
                break;

            case 2: // Multiplication
                operator = '×';
                numB = random.nextInt(5) + 1; // numB is between 1-5 to keep numbers small
                numA = target / numB;
                while (numA * numB != target || numA == 0 || numA > 9) { // Ensure valid multiplication
                    numB = random.nextInt(5) + 1;
                    numA = target / numB;
                }
                operatorImage.setImageResource(R.drawable.ic_multiply);
                break;

            case 3: // Division
                operator = '÷';
                numB = random.nextInt(8) + 1; // numB is between 1-9
                numA = numB * (random.nextInt(9) + 1); // Ensure numA is a valid multiple within range

                // Ensure numA is within 1-9
                while (numA > 9) {
                    numB = random.nextInt(8) + 1;
                    numA = numB * (random.nextInt(9) + 1);
                }

                target = numA / numB;
                operatorImage.setImageResource(R.drawable.ic_divide);
                break;
        }

        correctAnswer = target;
        questionText.setText("Compose " + target + " using: ");
        firstSlot.setText("?");
        secondSlot.setText("?");
        feedbackText.setText("");
    }

    private void checkAnswer() {
        try {
            int numA = Integer.parseInt(firstSlot.getText().toString());
            int numB = Integer.parseInt(secondSlot.getText().toString());
            int userResult = 0;

            switch (operator) {
                case '+':
                    userResult = numA + numB;
                    break;
                case '-':
                    userResult = numA - numB;
                    break;
                case '×':
                    userResult = numA * numB;
                    break;
                case '÷':
                    if (numB != 0) { // Ensure no division by zero
                        userResult = numA / numB;
                    } else {
                        userResult = -1; // Invalid case
                    }
                    break;
            }

            // Load fade-in animation
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            feedbackText.startAnimation(fadeIn);

            if (userResult == correctAnswer) {
                String otherCombinations = generateOtherCombinations();
                if (otherCombinations.isEmpty()) {
                    feedbackText.setText("🎉 Correct !\n");
                    feedbackText.setTextColor(getResources().getColor(R.color.green));
                    submitButton.postDelayed(this::generateNewQuestion, 2000);
                } else {
                    feedbackText.setText("🎉 Correct !\n" + otherCombinations);
                    feedbackText.setTextColor(getResources().getColor(R.color.green));
                    submitButton.postDelayed(this::generateNewQuestion, 4000);
                }
            } else {
                feedbackText.setText("❌ Try Again !");
                feedbackText.setTextColor(getResources().getColor(R.color.red)); // Turn text red
                clearSlots(); // Clear slots so the user can try again

                // Load and start shake animation when answer is incorrect
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                feedbackText.startAnimation(shake);
            }
        } catch (NumberFormatException e) {
            feedbackText.setText("Please drag numbers into both slots !");
            feedbackText.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private String generateOtherCombinations() {
        validPairs.clear(); // Clear previous values

        // Get user's chosen numbers
        int userA, userB;
        try {
            userA = Integer.parseInt(firstSlot.getText().toString());
            userB = Integer.parseInt(secondSlot.getText().toString());
        } catch (NumberFormatException e) {
            userA = -1;
            userB = -1;
        }

        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                boolean isValid = false;

                if (operator == '+' && i + j == correctAnswer) {
                    isValid = true;
                } else if (operator == '-' && i - j == correctAnswer) {
                    isValid = true;
                } else if (operator == '×' && i * j == correctAnswer) {
                    isValid = true;
                } else if (operator == '÷' && j != 0 && i / j == correctAnswer && i % j == 0) {
                    isValid = true;
                }

                // Exclude the pair the user used
                if (isValid && !((i == userA && j == userB) || (i == userB && j == userA))) {
                    validPairs.add(new int[]{i, j});
                }
            }
        }

        return getValidPairsText();
    }

    private String getValidPairsText() {
        if (validPairs.isEmpty()) {
            return ""; // No other combinations, return an empty string
        }

        StringBuilder pairsText = new StringBuilder("\nOther combinations:\n");
        int count = 0;
        int columnCount;

        // Determine layout:
        if (validPairs.size() > 6) {
            columnCount = 3; // 3 columns for more than 6 pairs
        } else if (validPairs.size() > 3) {
            columnCount = 2; // 2 columns for 4 to 6 pairs
        } else {
            columnCount = 1; // 1 column for 3 or fewer pairs
        }

        for (int[] pair : validPairs) {
            pairsText.append(pair[0]).append(" ").append(operator).append(" ").append(pair[1]);

            // Format based on column count
            if ((count + 1) % columnCount == 0) {
                pairsText.append("\n"); // Move to the next row after filling the columns
            } else {
                pairsText.append("\t\t\t"); // Add space for column alignment
            }
            count++;
        }

        return pairsText.toString().trim(); // Trim to remove trailing spaces or newlines
    }


    private void clearSlots() {
        firstSlot.setText("?");
        secondSlot.setText("?");
    }

    private void createNumberButtons() {
        numberGrid.removeAllViews();

        for (int i = 1; i <= 9; i++) {
            Button numberButton = new Button(this);
            numberButton.setText(String.valueOf(i));
            numberButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(10, 10, 10, 10);
            numberButton.setLayoutParams(params);

            numberButton.setBackgroundResource(R.drawable.border_box);

            // Enable dragging
            numberButton.setOnTouchListener(touchListener);

            numberGrid.addView(numberButton);
        }
    }

    // Fix: Add missing touchListener
    private final View.OnTouchListener touchListener = (v, event) -> {
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            ClipData clipData = ClipData.newPlainText("number", ((Button) v).getText().toString());
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(clipData, shadowBuilder, v, 0);
            return true;
        }
        return false;
    };

    private void setDragListeners() {
        View.OnDragListener dragListener = (v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription() != null &&
                            event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DROP:
                    ClipData clipData = event.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        String droppedText = clipData.getItemAt(0).getText().toString();
                        if (v instanceof Button) {
                            ((Button) v).setText(droppedText);
                        }
                    }
                    return true;
            }
            return true;
        };

        firstSlot.setOnDragListener(dragListener);
        secondSlot.setOnDragListener(dragListener);
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
        finish();
    }
}