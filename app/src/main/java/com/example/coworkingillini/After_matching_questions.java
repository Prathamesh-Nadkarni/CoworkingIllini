package com.example.coworkingillini;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class After_matching_questions extends AppCompatActivity {
    List<List<String>> questions = new ArrayList<>();
    List<Integer> userAnswers = new ArrayList<>();  // List to store the selected answer for each question
    // global variale to increase or decrease page number and change questions based on it
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fill the questions list with data
        List<String> list1 = new ArrayList<>();
        list1.add("For this study session, which best describes your approach?");
        list1.add("Exam Prepper: I’m here to focus and prepare efficiently");
        list1.add("Struggler: Looking for extra help with challenging material");
        list1.add("Answer Checker: I want to compare answers and check my understanding");
        questions.add(list1);

        List<String> list2 = new ArrayList<>();
        list2.add("What areas do you want to focus on in the study session");
        list2.add("Work through specific problem sets.");
        list2.add("Complete an assignment or group project.");
        list2.add("Go over class notes");

        questions.add(list2);

        List<String> list3 = new ArrayList<>();
        list3.add("How long are you looking to study?");
        list3.add("Quick review (30 mins)");
        list3.add("Focused session (1 hour)");
        list3.add("Deep dive (2+ hours)");
        questions.add(list3);

        // Initialize userAnswers with -1 for each question (indicating no answer selected)
        for (int j = 0; j < questions.size(); j++) {
            userAnswers.add(-1);
        }

        setContentView(R.layout.activity_after_matching_questions);

        TextView questionText = findViewById(R.id.question_text);
        RadioButton answer1 = findViewById(R.id.answer1);
        RadioButton answer2 = findViewById(R.id.answer2);
        RadioButton answer3 = findViewById(R.id.answer3);
        RadioGroup answerGroup = findViewById(R.id.persona_radio_group);
        Button next = findViewById(R.id.button_next);
        Button previous = findViewById(R.id.button_previous);

        // Set initial question and options
        updateQuestionAndOptions(questionText, answer1, answer2, answer3, answerGroup);

        // Next button click listener
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedAnswer(answerGroup); // Save the selected answer before moving
                if (i < questions.size() - 1) {
                    i++;
                    updateQuestionAndOptions(questionText, answer1, answer2, answer3, answerGroup);
                } else {
                    Toast.makeText(After_matching_questions.this, "You are on the last question", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Previous button click listener
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedAnswer(answerGroup); // Save the selected answer before moving
                if (i > 0) {
                    i--;
                    updateQuestionAndOptions(questionText, answer1, answer2, answer3, answerGroup);
                } else {
                    Toast.makeText(After_matching_questions.this, "You are on the first question", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to update question and options based on the current index i
    private void updateQuestionAndOptions(TextView questionText, RadioButton answer1, RadioButton answer2, RadioButton answer3, RadioGroup answerGroup) {
        List<String> currentQuestion = questions.get(i);
        questionText.setText(currentQuestion.get(0));
        answer1.setText(currentQuestion.get(1));
        answer2.setText(currentQuestion.get(2));
        answer3.setText(currentQuestion.get(3));

        // Clear previous selections in the radio group
        answerGroup.clearCheck();

        // Set the previously selected answer, if any
        int selectedAnswer = userAnswers.get(i);
        if (selectedAnswer != -1) {
            ((RadioButton) answerGroup.getChildAt(selectedAnswer)).setChecked(true);
        }
    }

    // Method to save the selected answer for the current question
    private void saveSelectedAnswer(RadioGroup answerGroup) {
        int selectedId = answerGroup.getCheckedRadioButtonId();
        int selectedAnswer = -1;
        if (selectedId == R.id.answer1) {
            selectedAnswer = 0;
        } else if (selectedId == R.id.answer2) {
            selectedAnswer = 1;
        } else if (selectedId == R.id.answer3) {
            selectedAnswer = 2;
        }
        // Save the selected answer for the current question index
        userAnswers.set(i, selectedAnswer);
    }
}
