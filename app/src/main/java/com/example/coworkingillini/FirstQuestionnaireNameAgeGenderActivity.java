package com.example.coworkingillini;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstQuestionnaireNameAgeGenderActivity extends AppCompatActivity {
    private Bundle bundle;
    private Spinner genderSpinner;
    private Spinner yearSpinner;

    private SeekBar progress;
    private EditText nameEditText;
    private EditText ageEditText;
    FirebaseDatabase database;

    private Button nextArrow;
    private ImageView backArrow;


    /*      Bundle brought variables        */
    private String bundleUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*      Default things      */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_questionnaire_name_age_gender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.first_questionnaire_name_age_gender_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();
        initializeComponents();
        receiveBundle();
        genderSpinner.setOnTouchListener(SpinnerOnTouch());
        yearSpinner.setOnTouchListener(SpinnerOnTouch());
        nextArrow.setOnClickListener(nextArrownOnClick());
        backArrow.setOnClickListener(backArrowOnClick());
    }
    private View.OnClickListener backArrowOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
    }

    private View.OnTouchListener SpinnerOnTouch() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        };
    }
    private View.OnClickListener nextArrownOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check()) {
                    sendBundle();
                }
                else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Please fill in all the details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);
                }
            }
        };
    }

    private boolean check()
    {
        if(nameEditText.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Name not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(ageEditText.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Age not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(genderSpinner.getSelectedItem().toString().isEmpty() || genderSpinner.getSelectedItem().toString().equals("Select Gender"))
        {
            Toast.makeText(this, "Gender not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(yearSpinner.getSelectedItem().toString().isEmpty() || yearSpinner.getSelectedItem().toString().equals("Select Education Year"))
        {
            Toast.makeText(this, "Year not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initializeComponents() {
        nameEditText = findViewById(R.id.first_questionnaire_name_age_edit_name);
        ageEditText = findViewById(R.id.first_questionnaire_name_age_edit_age);
        genderSpinner = findViewById(R.id.first_questionnaire_name_age_gender_spinner_gender);
        populateGenderSpinner();
        yearSpinner = findViewById(R.id.first_questionnaire_name_age_gender_spinner_education_year);
        populateYearSpinner();
        nextArrow = findViewById(R.id.first_questionnaire_name_age_gender_continue);
        backArrow = findViewById(R.id.first_questionnaire_name_age_gender_back_arrow);
    }
    private void receiveBundle() {
        if (getIntent().getExtras() != null) {
            bundleUsername = getIntent().getExtras().getString("generatedUsername");
        }
    }
    private void populateGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.first_questionnaire_gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

    }
    private void populateYearSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.first_questionnaire_education_year_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

    }
    private void sendBundle() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        DatabaseReference myRef = database.getReference("users").child(userId);
        myRef.child("name").setValue(nameEditText.getText().toString());
        myRef.child("age").setValue(ageEditText.getText().toString());
        myRef.child("gender").setValue(genderSpinner.getSelectedItem().toString());

        Toast.makeText(getApplicationContext(),userId,Toast.LENGTH_SHORT).show();

        Intent i = new Intent(FirstQuestionnaireNameAgeGenderActivity.this, FirstQuestionnaireSliderQuestionsActivity.class);

        startActivity(i);
    }
}