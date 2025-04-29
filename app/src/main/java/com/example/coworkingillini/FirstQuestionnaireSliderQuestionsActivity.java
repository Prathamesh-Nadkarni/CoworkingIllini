package com.example.coworkingillini;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstQuestionnaireSliderQuestionsActivity extends AppCompatActivity {
    private Bundle bundle;
    private ImageView backArrow;
    private Button nextArrow;
    private TextView questionHeading;
    private ImageView questionImage;
    private SeekBar seekBar;

    private SeekBar progress;
    private TextView questionTextView;
    private TextView leftExtremeTextView;
    private TextView rightExtremeTextView;

    /*      Bundle brought variables        */
    private String bundleUsername;
    private String bundleName;
    private int bundleAge;
    private String bundleGender;

    /*      Switchable Content variables        */
    private TypedArray images;
    private String[] questions;
    private String[] leftExtremes;
    private String[] rightExtremes;
    private int indexQuestion;
    private int loudness;
    private int studyPace;
    private int teamPreference;
    private int cram;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*      Default things      */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_questionnaire_slider_questions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.first_questionnaire_slider_questions_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();
        initializeComponents();
        receiveBundle();
        initializeSwitchableVariables();
        startQuestion();

        backArrow.setOnClickListener(backArrowOnClick());
        nextArrow.setOnClickListener(nextArrowOnClick());
    }
    private void initializeComponents() {
        backArrow = findViewById(R.id.first_questionnaire_slider_questions_back_arrow);
        questionHeading = findViewById(R.id.first_questionnaire_slider_questions_heading);
        questionImage = findViewById(R.id.first_questionnaire_slider_questions_imgview_1);
        seekBar = findViewById(R.id.first_questionnaire_slider_questions_seekbar);
        nextArrow = findViewById(R.id.first_questionnaire_slider_questions_continue);
        questionTextView = findViewById(R.id.first_questionnaire_slider_questions_txtview_2);
        leftExtremeTextView = findViewById(R.id.first_questionnaire_slider_questions_txtview_3);
        rightExtremeTextView = findViewById(R.id.first_questionnaire_slider_questions_txtview_4);
        //progress = findViewById(R.id.progress);
    }
    private void receiveBundle() {
        if (getIntent().getExtras() != null) {
            bundleUsername = getIntent().getExtras().getString("generatedUsername");
            bundleName = getIntent().getExtras().getString("name");
            bundleAge = getIntent().getExtras().getInt("age");
            bundleGender = getIntent().getExtras().getString("gender");
        }
    }
    private void initializeSwitchableVariables() {
        indexQuestion = 0;
        questionHeading.setText("Sign-Up (3/8)");
        images = getResources().obtainTypedArray(R.array.first_questionnaire_slider_questions_image);
        questions = getResources().getStringArray(R.array.first_questionnaire_slider_questions_array);
        leftExtremes = getResources().getStringArray(R.array.first_questionnaire_slider_questions_left_extreme_array);
        rightExtremes = getResources().getStringArray(R.array.first_questionnaire_slider_questions_right_extreme_array);
        loudness = 5;
        studyPace = 5;
        teamPreference = 5;
        cram = 5;
    }
    private void startQuestion() {
        indexQuestion = 0;
        changeQuestion();
    }
    private View.OnClickListener backArrowOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexQuestion == 0) {
                    finish();
                }
                else {
                    indexQuestion--;
                    changeQuestion();
                    switch (indexQuestion) {
                        case 0:
                            seekBar.setProgress(loudness);
                            break;
                        case 1:
                            seekBar.setProgress(studyPace);
                            break;
                        case 2:
                            seekBar.setProgress(teamPreference);
                            break;
                        case 3:
                            seekBar.setProgress(cram);
                            break;
                        default:
                            break;
                    }
                }
            }
        };
    }
    private View.OnClickListener nextArrowOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress.setProgress(indexQuestion+2);
                if (indexQuestion == 3) {
                    sendBundle();
                    Intent i = new Intent(FirstQuestionnaireSliderQuestionsActivity.this, FirstQuestionnaireSkillsActivity.class);
                    startActivity(i);
                } else {
                    saveProgress();
                    indexQuestion++;
                    changeQuestion();
                }
            }
        };
    }
    private void changeQuestion() {
        questionHeading.setText("Sign-Up (" + (indexQuestion + 3) + "/8)");
        questionImage.setImageResource(images.getResourceId(indexQuestion, -1));
        questionTextView.setText(questions[indexQuestion]);
        leftExtremeTextView.setText(leftExtremes[indexQuestion]);
        rightExtremeTextView.setText(rightExtremes[indexQuestion]);
        seekBar.setProgress(5);
        seekBar.setEnabled(true);

    }
    private void saveProgress() {
        switch (indexQuestion) {
            case 0:
                loudness = seekBar.getProgress();
                break;
            case 1:
                studyPace = seekBar.getProgress();
                break;
            case 2:
                teamPreference = seekBar.getProgress();
                break;
            case 3:
                cram = seekBar.getProgress();
                break;
            default:
                break;
        }
    }
    private void sendBundle() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        DatabaseReference myRef = database.getReference("users").child(userId);
        myRef.child("Loudness").setValue(loudness);
        myRef.child("StudyPace").setValue(studyPace);
        myRef.child("TeamPreference").setValue(teamPreference);
        myRef.child("Cram").setValue(cram);
    }
}


