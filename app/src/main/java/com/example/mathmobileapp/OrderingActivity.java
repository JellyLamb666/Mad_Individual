package com.example.mathmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderingActivity extends AppCompatActivity {

    private TextView[] numberSlots = new TextView[5]; // Target slots
    private TextView[] draggableNumbers = new TextView[5]; // Draggable numbers
    private int[] randomNumbers = new int[5]; // Stores randomly generated numbers
    private boolean ascendingOrder; // Will be randomly set
    private TextView instructionText;
    private TextView feedbackText;
    private Button checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        // UI Elements
        instructionText = findViewById(R.id.instructionText);
        feedbackText = findViewById(R.id.feedbackText);
        checkButton = findViewById(R.id.checkButton);

        // Assign number slots (empty boxes)
        numberSlots[0] = findViewById(R.id.target1);
        numberSlots[1] = findViewById(R.id.target2);
        numberSlots[2] = findViewById(R.id.target3);
        numberSlots[3] = findViewById(R.id.target4);
        numberSlots[4] = findViewById(R.id.target5);

        // Assign draggable numbers
        draggableNumbers[0] = findViewById(R.id.num1);
        draggableNumbers[1] = findViewById(R.id.num2);
        draggableNumbers[2] = findViewById(R.id.num3);
        draggableNumbers[3] = findViewById(R.id.num4);
        draggableNumbers[4] = findViewById(R.id.num5);

        // Randomly choose ascending or descending order
        ascendingOrder = new Random().nextBoolean();

        instructionText.setText(ascendingOrder ? "Arrange in ASCENDING Order" : "Arrange in DESCENDING Order");

        // Generate random numbers
        generateRandomNumbers();

        // Set touch listeners for dragging
        for (TextView numSlot : draggableNumbers) {
            numSlot.setOnTouchListener(new MyTouchListener());
        }

        // Set drag listeners for empty slots
        for (TextView slot : numberSlots) {
            slot.setOnDragListener(new MyDragListener());
        }

        // Check button logic
        checkButton.setOnClickListener(v -> checkOrder());
    }

    // Generate 5 random numbers and set them to draggable TextViews
    private void generateRandomNumbers() {
        Random random = new Random();
        List<Integer> numbers = new ArrayList<>();

        while (numbers.size() < 5) {
            int num = random.nextInt(50) + 1; // Generate numbers between 1-50
            if (!numbers.contains(num)) { // Ensure no duplicates
                numbers.add(num);
            }
        }

        Collections.shuffle(numbers); // Randomize the order

        for (int i = 0; i < 5; i++) {
            randomNumbers[i] = numbers.get(i);
            draggableNumbers[i].setText(String.valueOf(randomNumbers[i]));
        }
    }

    // Touch listener for dragging numbers
    private class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
            return false;
        }
    }

    // Drag listener for handling the drop action
    private class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    TextView draggedView = (TextView) event.getLocalState();
                    TextView targetView = (TextView) v;

                    // If the target box already has a number, swap it back to draggable numbers
                    if (!targetView.getText().toString().isEmpty()) {
                        TextView previousDraggable = (TextView) targetView.getTag();
                        if (previousDraggable != null) {
                            previousDraggable.setVisibility(View.VISIBLE); // Make it visible again
                        }
                    }

                    // Move number to the target box
                    targetView.setText(draggedView.getText().toString());
                    draggedView.setVisibility(View.INVISIBLE); // Hide the dragged number
                    targetView.setTag(draggedView); // Store reference of the original number

                    break;
            }
            return true;
        }
    }

    // Reset game properly
    private void resetGame() {
        ascendingOrder = new Random().nextBoolean();
        instructionText.setText(ascendingOrder ? "Arrange in Ascending Order" : "Arrange in Descending Order");

        generateRandomNumbers();

        for (TextView slot : numberSlots) {
            slot.setText("");
            slot.setTextColor(Color.BLACK);
            slot.setBackgroundColor(Color.TRANSPARENT); // Reset colors
            slot.setBackgroundResource(R.drawable.border_box);
        }
        for (TextView num : draggableNumbers) {
            num.setVisibility(View.VISIBLE);
        }

        feedbackText.setText(""); // Clear feedback message
    }

    private boolean isCorrectPlacement(int number, TextView slot) {
        int index = Arrays.asList(numberSlots).indexOf(slot);
        return ascendingOrder
                ? (index == 0 || numberSlots[index - 1].getText().toString().isEmpty() || Integer.parseInt(numberSlots[index - 1].getText().toString()) <= number)
                : (index == 0 || numberSlots[index - 1].getText().toString().isEmpty() || Integer.parseInt(numberSlots[index - 1].getText().toString()) >= number);
    }

    // Check if the numbers are in correct order
    private void checkOrder() {
        int[] currentOrder = new int[5];

        for (int i = 0; i < 5; i++) {
            String text = numberSlots[i].getText().toString();
            if (text.isEmpty()) {
                feedbackText.setText("Drag and complete the arrangement first !");
                feedbackText.setTextColor(Color.RED);
                return;
            }
            currentOrder[i] = Integer.parseInt(text);
        }

        boolean isCorrect = ascendingOrder ? isSortedAscending(currentOrder) : isSortedDescending(currentOrder);

        // Load fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        feedbackText.startAnimation(fadeIn);

        if (isCorrect) {
            for (TextView slot : numberSlots) {
                slot.setTextColor(Color.GREEN);  // ✅ Turn green if correct
            }
            feedbackText.setText("🎉 Correct ! \nWell done");
            feedbackText.setTextColor(Color.GREEN);

            // Reset after delay
            feedbackText.postDelayed(this::resetGame, 2000);
        } else {
            for (TextView slot : numberSlots) {
                slot.setTextColor(Color.RED);  // ❌ Turn red if wrong

                // Restore original numbers
                TextView originalView = (TextView) slot.getTag();
                if (originalView != null) {
                    originalView.setVisibility(View.VISIBLE); // Show again
                    slot.setText(""); // Clear wrong answer
                    slot.setTextColor(Color.BLACK);
                    slot.setTag(null); // Remove stored reference
                }
            }
            feedbackText.setText("❌ Try Again !");
            feedbackText.setTextColor(Color.RED);

            // Load and start shake animation when answer is incorrect
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            feedbackText.startAnimation(shake);
        }
    }

    // Check if numbers are in ascending order
    private boolean isSortedAscending(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    // Check if numbers are in descending order
    private boolean isSortedDescending(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] < arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
        finish();
    }
}