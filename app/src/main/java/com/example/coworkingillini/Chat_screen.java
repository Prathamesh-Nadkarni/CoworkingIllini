package com.example.coworkingillini;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class Chat_screen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);


        // Find the Top back button by its ID
        ImageButton backToMatches = findViewById(R.id.chat_header_back_arrow);

        // Set an OnClickListener to navigate to the previous screen
        backToMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the matches screen
                Intent intent = new Intent(Chat_screen.this, Survey.class);
                startActivity(intent);
            }
        });
    }
}
