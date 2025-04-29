package com.example.coworkingillini;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat_results extends AppCompatActivity {
    List<List<String>> questions = new ArrayList<>();
    String key;
    String username;
    DatabaseReference chatRef;
    Bundle bundle;
    FirebaseDatabase database;
    String otherUsername;
    private TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_results);
        populateQuestions();

        // Find the Top back button by its ID
        ImageButton backToMatches = findViewById(R.id.chat_header_back_arrow);
        Intent intent1 = getIntent();
        bundle = intent1.getExtras();
        if (bundle != null) {
            key = bundle.getString("key");
            username = bundle.getString("Username");
            String[] usernames = key.split(":");
            otherUsername = usernames[0].equals(username) ? usernames[1] : usernames[0];

        }
        database = FirebaseDatabase.getInstance();
        chatRef = database.getReference("chats").child(key);
        txtView = findViewById(R.id.chat_header_profile_name);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    displaySurveyResults(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        txtView.setText(otherUsername);
        // Set an OnClickListener to navigate to the previous screen
        backToMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the matches screen
                Intent intent = new Intent(Chat_results.this, Survey.class);
                startActivity(intent);
            }
        });

        MaterialButton GoToChat = findViewById(R.id.chat_button);
        // Set an OnClickListener to navigate to the previous screen
        GoToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the matches screen
                Intent intent = new Intent(Chat_results.this, ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private void populateQuestions() {
        List<String> list1 = List.of("For this study session, which best describes your approach?",
                "Exam Prepper: I’m here to focus and prepare efficiently",
                "Struggler: Looking for extra help with challenging material",
                "Answer Checker: I want to compare answers and check my understanding");
        List<String> list2 = List.of("What areas do you want to focus on in the study session?",
                "Work through specific problem sets",
                "Complete an assignment or group project",
                "Go over class notes");
        List<String> list3 = List.of("How long are you looking to study?",
                "Quick review (30 mins)",
                "Focused session (1 hour)",
                "Deep dive (2+ hours)");

        questions.add(list1);
        questions.add(list2);
        questions.add(list3);
    }

    private void displaySurveyResults(DataSnapshot dataSnapshot) {
        String[] users = key.split(":");


        for (int i = 0; i < users.length; i++) {
            users[i] = users[i].trim();
        }
        String[] questions = {"For this study session, which best describes your approach?",
                "What areas do you want to focus on in the study session?",
                "How long are you looking to study?"};
        String[][] answers = {
                {"Exam Prepper: I'm here to focus and prepare efficiently",
                        "Struggler: Looking for extra help with challenging material",
                        "Answer Checker: I want to compare answers and check my understanding"},
                {"Work through specific problem sets",
                        "Complete an assignment or group project",
                        "Go over class notes"},
                {"Quick review (30 mins)",
                        "Focused session (1 hour)",
                        "Deep dive (2+ hours)"}
        };

        for (int i = 0; i < 3; i++) {
            TextView questionView = findViewById(getResources().getIdentifier("result_question_" + (i+1), "id", getPackageName()));
            TextView yourAnswerView = findViewById(getResources().getIdentifier("your_answer_" + (i+1), "id", getPackageName()));
            TextView theirAnswerView = findViewById(getResources().getIdentifier("their_answer_" + (i+1), "id", getPackageName()));

            questionView.setText(questions[i]);

            for (String user : users) {
                int answerIndex = dataSnapshot.child(user).child("question_" + (i+1)).getValue(Integer.class);
                String answerText = answers[i][answerIndex];

                if (user.equals(username)) {
                    yourAnswerView.setText("Your Answer: " + answerText);
                } else {
                    theirAnswerView.setText("Their Answer: " + answerText);
                }
            }
        }
    }
}

