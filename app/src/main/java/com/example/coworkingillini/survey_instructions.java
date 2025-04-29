package com.example.coworkingillini;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class survey_instructions extends AppCompatActivity {
    FirebaseDatabase database;
    String key;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen_survey_instructions);
        MaterialButton startSurveyButton = findViewById(R.id.start_survey_button);

       database = FirebaseDatabase.getInstance();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();



        // Set an OnClickListener to navigate to the next screen
        startSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(survey_instructions.this, Questions.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // Find the Back button by its ID
        ImageButton backToMatches = findViewById(R.id.chat_header_back_arrow);

        // Set an OnClickListener to navigate to the previous screen
        backToMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the previous screen (where the matches are)
                Intent intent = new Intent(survey_instructions.this, Survey.class);
                startActivity(intent);
            }
        });
    }
}