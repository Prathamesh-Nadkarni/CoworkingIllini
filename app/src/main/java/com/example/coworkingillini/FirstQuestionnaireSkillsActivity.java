package com.example.coworkingillini;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirstQuestionnaireSkillsActivity extends AppCompatActivity {
    private Bundle bundle;
    private ImageView backArrow;
    private Button nextArrow;
    private ArrayList<String> skills;
    private EditText skillInput;
    private FlexboxLayout skillsContainer;
    private ImageView help;

    private SeekBar progress;
    FirebaseDatabase database;

    /*      Bundle brought variables        */
    private String bundleUsername;
    private String bundleName;
    private int bundleAge;
    private String bundleGender;
    private int loudness;
    private int studyPace;

    private String instructions = "Please add skills relevant to your courses. These skills will showcase your talents and may allow people to swipe on you for what they seek.";
    private int teamPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*      Default things      */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_questionnaire_skills);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.first_questionnaire_skills_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        database = FirebaseDatabase.getInstance();
        nextArrow.setOnClickListener(nextArrowOnClick());
        backArrow.setOnClickListener(backArrowOnClick());
        skillInput.setOnEditorActionListener(skillInputOnDone());
        help.setOnClickListener(helpOnClick());

    }

    private View.OnClickListener helpOnClick() {
       return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FirstQuestionnaireSkillsActivity.this);
                    View customView = getLayoutInflater().inflate(R.layout.first_questionnaire_help_screen, null);
                    TextView messageView = customView.findViewById(R.id.help_message);
                    messageView.setText(instructions);

                    builder.setView(customView)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.first_questionnaire_dialog_background);
                    dialog.show();
                }
        };
    }

    private void initializeComponents() {
        backArrow = findViewById(R.id.first_questionnaire_skills_back_arrow);
        nextArrow = findViewById(R.id.first_questionnaire_skills_continue);
        skills = new ArrayList<String>();
        skillInput = findViewById(R.id.first_questionnaire_skills_txtview_5);
        skillsContainer = findViewById(R.id.first_questionnaire_skills_container);
        help = findViewById(R.id.first_questionnaire_skills_help);

        // Default skills
        setupSkillButton(R.id.first_questionnaire_skills_btn_3, "Motivation");
        setupSkillButton(R.id.first_questionnaire_skills_btn_5, "Communication");
        setupSkillButton(R.id.first_questionnaire_skills_btn_6, "Problem Solving");
    }

    private void sendBundle() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        DatabaseReference myRef = database.getReference("users").child(userId);
        myRef.child("skills").setValue(skills);
        for(int i = 0; i < skills.size(); i++)
        {
            myRef.child("skills").child(i+"").setValue(skills.get(i));
        }
    }
    private View.OnClickListener backArrowOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
    }
    private View.OnClickListener nextArrowOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skills.isEmpty())
                {
                    Toast.makeText(FirstQuestionnaireSkillsActivity.this, "Please add at least one skill", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendBundle();
                    Intent i = new Intent(FirstQuestionnaireSkillsActivity.this, FirstQuestionnaireCoursesActivity.class);
                    startActivity(i);
                }
            }
        };
    }
    private TextView.OnEditorActionListener skillInputOnDone() {
        return new TextView.OnEditorActionListener()  {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addCustomSkill(skillInput.getText().toString().trim());
                    skillInput.setText("");
                    return true;
                }
                return false;
            }
        };
    }

    private void addCustomSkill(String skill) {
        if (skill.isEmpty()) return;

        // Create new button
        Button newSkill = new Button(this);
        newSkill.setText(skill);

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) (55 * getResources().getDisplayMetrics().density)
        );
        params.setMargins(
                (int) (10 * getResources().getDisplayMetrics().density),
                (int) (10 * getResources().getDisplayMetrics().density),
                (int) (10 * getResources().getDisplayMetrics().density),
                0
        );

        newSkill.setLayoutParams(params);

        // Match existing button styling
        newSkill.setMinimumWidth(0);
        newSkill.setMinimumHeight(0);
        newSkill.setBackgroundResource(R.drawable.first_questionnaire_skills_button_filled_selected);
        newSkill.setTextColor(getResources().getColor(R.color.background, getTheme()));
        newSkill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        newSkill.setAllCaps(false); // Match case style
        newSkill.setPadding(
                (int) (20 * getResources().getDisplayMetrics().density),
                0,
                (int) (20 * getResources().getDisplayMetrics().density),
                0
        );

        // Add to container and list
        skillsContainer.addView(newSkill);
        skills.add(skill);

        // Setup toggle behavior
        newSkill.setOnClickListener(v -> toggleSkillSelection(newSkill, skill));
    }
    private void setupSkillButton(int buttonId, String skillName) {
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) (55 * getResources().getDisplayMetrics().density)
        );
        params.setMargins(
                (int) (10 * getResources().getDisplayMetrics().density),
                (int) (10 * getResources().getDisplayMetrics().density),
                (int) (10 * getResources().getDisplayMetrics().density),
                0
        );

        Button button = findViewById(buttonId);
        button.setLayoutParams(params);
        button.setMinimumWidth(0);
        button.setMinimumHeight(0);
        button.setBackgroundResource(R.drawable.first_questionnaire_skills_button_outline_deselected);
        button.setTextColor(getResources().getColor(R.color.colorOnSurface, getTheme()));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        button.setAllCaps(false); // Match case style
        button.setPadding(
                (int) (20 * getResources().getDisplayMetrics().density),
                0,
                (int) (20 * getResources().getDisplayMetrics().density),
                0
        );
        button.setOnClickListener(v -> toggleSkillSelection(button, skillName));
    }
    private void toggleSkillSelection(Button button, String skill) {
        if (skills.contains(skill)) {
            // Deselect
            skills.remove(skill);
            button.setBackgroundResource(R.drawable.first_questionnaire_skills_button_outline_deselected);
            button.setTextColor(getResources().getColor(R.color.colorOnSurface, getTheme()));
        } else {
            // Select
            skills.add(skill);
            button.setBackgroundResource(R.drawable.first_questionnaire_skills_button_filled_selected);
            button.setTextColor(getResources().getColor(R.color.background, getTheme()));
        }
    }
}
