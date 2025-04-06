package com.example.mathmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Button compareButton = findViewById(R.id.compareButton);
        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, CompareActivity.class);
                startActivity(intent);
                finish(); // Close current activity
            }
        });

        Button orderButton = findViewById(R.id.orderButton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, OrderingActivity.class);
                startActivity(intent);
                finish(); // Close current activity
            }
        });

        Button composeButton = findViewById(R.id.composeButton);
        composeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, ComposingActivity.class);
                startActivity(intent);
                finish(); // Close current activity
            }
        });
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
