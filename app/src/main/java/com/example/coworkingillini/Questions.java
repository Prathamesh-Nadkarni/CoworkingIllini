package com.example.coworkingillini;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
//Branch creation

public class Questions extends AppCompatActivity {
    List<List<String>> questions = new ArrayList<>();

    // Array to store the answers the users choose
    List<Integer> userAnswers = new ArrayList<>();

    // global variable to increase or decrease page number and change questions based on it
    int i = 0;

    // Global variable to see if both users have taken the quiz. 0 for none(at the beginning), 1 for 1 user, 2 for both users.
    public static int taken = 2;

    //Global variable to see if the user has completed the quiz and not show it to him again
    public static int done = 0;
    // BottomNavigationView nav_view;

    FirebaseDatabase database;
    String key;
    String username;
    DatabaseReference chatRef;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen_survey_question);
        TextView questionNumber = findViewById(R.id.survey_question);
        TextView questionText = findViewById(R.id.survey_question_1);
        Button answer1 = findViewById(R.id.answer_button_1);
        Button answer2 = findViewById(R.id.answer_button_2);
        Button answer3 = findViewById(R.id.answer_button_3);
        MaterialButton next = findViewById(R.id.next_button1);
        MaterialButton previous = findViewById(R.id.previous_button1);
        previous.setVisibility(View.INVISIBLE);
        LinearProgressIndicator progressBar = findViewById(R.id.chat_screen_progress_bar);
        Intent intent1 = getIntent();
        bundle = intent1.getExtras();
        if (bundle != null) {
            key = bundle.getString("key");
            username = bundle.getString("Username");
        }
        database = FirebaseDatabase.getInstance();
        chatRef = database.getReference("chats").child(key).child(username);;

        populateQuestions();
        initializeUserAnswers();

        updateQuestionAndOptions(questionNumber, questionText, answer1, answer2, answer3, progressBar);

        // Setting a listener for the 3 buttons of possible answers
        answer1.setOnClickListener(v -> saveSelectedAnswer(questionNumber, 0, questionText, answer1, answer2, answer3, progressBar));
        answer2.setOnClickListener(v -> saveSelectedAnswer(questionNumber, 1, questionText, answer1, answer2, answer3, progressBar));
        answer3.setOnClickListener(v -> saveSelectedAnswer(questionNumber, 2, questionText, answer1, answer2, answer3, progressBar));

        // Listener for the next button that updates the question showing the next one
        next.setOnClickListener(v -> {
            if (userAnswers.get(i) == -1) {
                // No answer selected, show error popup
                showErrorPopup();
            } else {
                // Save the current answer to Firebase
                saveAnswerToFirebase(i, userAnswers.get(i));

                if (i < questions.size() - 1) {
                    previous.setVisibility(View.VISIBLE);
                    i++;
                    updateQuestionAndOptions(questionNumber, questionText, answer1, answer2, answer3, progressBar);
                } else if (i == questions.size() - 1) {
                    done = 1;
                    // Handle the end of the quiz
                    if (taken == 1) {
                        Intent intent = new Intent(getApplicationContext(), Wait_results.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (taken == 2) {
                        Intent intent = new Intent(getApplicationContext(), View_results.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                if (i == 0) {
                    previous.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Listener for the previous button that updates the question showing the previous one
        previous.setOnClickListener(v -> {
            if (i > 0) {
                i--;
                updateQuestionAndOptions(questionNumber, questionText, answer1, answer2, answer3, progressBar);
            }  else {
                previous.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "You are on the first question", Toast.LENGTH_SHORT).show();
            }
            if (i == 0) {
                previous.setVisibility(View.INVISIBLE);
            }
        });

        // Find the Top back button by its ID
        ImageButton backToInstructions = findViewById(R.id.chat_header_back_arrow);

        // Set an OnClickListener to navigate to the previous screen
        backToInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the previous screen (instructions)
                Intent intent = new Intent(getApplicationContext(), survey_instructions.class);
                startActivity(intent);
            }
        });
    }

    private void saveAnswerToFirebase(int questionIndex, int answerIndex) {
        DatabaseReference questionRef = chatRef.child("question_" + (questionIndex + 1));
        questionRef.setValue(answerIndex);
    }

    private void showErrorPopup() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Please select an answer before proceeding.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Dismiss the dialog
        });
        builder.show();
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

    // Initialize userAnswers with -1 for each question (indicating no answer selected)
    private void initializeUserAnswers() {
        for (int j = 0; j < questions.size(); j++) {
            userAnswers.add(-1);
        }
    }

    // Helper method to update question and options based on the current index i and display them as well as update the progress bar
    private void updateQuestionAndOptions(TextView questionNumber, TextView questionText, Button answer1, Button answer2, Button answer3, LinearProgressIndicator progressBar) {
        List<String> currentQuestion = questions.get(i);
        questionNumber.setText("Question " + (i + 1));
        questionText.setText(currentQuestion.get(0));
        answer1.setText(currentQuestion.get(1));
        answer2.setText(currentQuestion.get(2));
        answer3.setText(currentQuestion.get(3));

        // Calculate progress as a percentage
        int progress = (int) (((float) (i + 1) / questions.size()) * 100);
        progressBar.setProgress(progress);
        //progressBar.setTrackColor(ContextCompat.getColor(this, R.color.yellow));
        //progressBar.setIndicatorColor(ContextCompat.getColor(this, R.color.orange));

        answer1.setBackgroundColor(Color.parseColor("#FEE8C4"));
        answer2.setBackgroundColor(Color.parseColor("#FEE8C4"));
        answer3.setBackgroundColor(Color.parseColor("#FEE8C4"));

        // Highlight selected answer, if any
        int selectedAnswer = userAnswers.get(i);
        if (selectedAnswer == 0)
            answer1.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
        else if (selectedAnswer == 1)
            answer2.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
        else if (selectedAnswer == 2)
            answer3.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
    }

    // Helper method to save the selected answer for the current question in the userAnswers list
    private void saveSelectedAnswer(TextView questionNumber, int answerIndex, TextView questionText, Button answer1, Button answer2, Button answer3, LinearProgressIndicator progressBar) {
        userAnswers.set(i, answerIndex);
        updateQuestionAndOptions(questionNumber, questionText, answer1, answer2, answer3, progressBar);
    }



}


