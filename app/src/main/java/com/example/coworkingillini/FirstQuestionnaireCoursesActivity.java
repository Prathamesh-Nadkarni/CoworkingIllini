package com.example.coworkingillini;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirstQuestionnaireCoursesActivity extends AppCompatActivity {
    private Bundle bundle;
    private ArrayList<Course> courses;
    private ImageView backArrow;
    private ImageView help;
    private Button nextArrow;
    private EditText search;

    /*      Bundle brought variables        */
    private String bundleUsername;
    private String bundleName;
    private int bundleAge;
    private String bundleGender;
    private int bundleLoudness;
    private int bundleStudyPace;
    private int bundleTeamPreference;
    private ArrayList<String> bundleSkills;

    private String[] instructions = {"Please choose your courses and your goals that you could match on the basis of. The app recognises that in some courses you would like to build long terms relationships in versus last minute exam prep.The options provided by us are: Homework Help, Routine Studying, Exam Prep"};
    SeekBar progress;
    Course newcourse;
    FirebaseDatabase database;

    boolean canGo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*      Default things      */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_questionnaire_courses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.first_questionnaire_courses_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();

        initializeComponents();
        backArrow.setOnClickListener(backArrowOnClick());
        nextArrow.setOnClickListener(nextArrowOnClick());
        search.setOnEditorActionListener(searchOnDone());
        help.setOnClickListener(helpOnClick());
    }
    private void addCourse(String string) {
        //Inflate View for new Course
        View courseView = getLayoutInflater().inflate(R.layout.first_questionnaire_course_item, null);

        //Temporary component
        TextView bottomText = courseView.findViewById(R.id.bottomText);

        //Initialize components

        ImageView deleteCourseImage = courseView.findViewById(R.id.first_questionnaire_course_delete_image);
        Button courseNameButton = courseView.findViewById(R.id.first_questionnaire_course_name_button);
        Button selectGoalButton = courseView.findViewById(R.id.first_questionnaire_course_goal_button);
        FlexboxLayout flexboxLayout;
        flexboxLayout = findViewById(R.id.first_questionnaire_courses_container);
        CharSequence[] allGoals = getResources().getStringArray(R.array.first_questionnaire_courses_goals);
        boolean[] checkedItems = new boolean[allGoals.length];
        newcourse = new Course();

        //Visually adding new Course
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,    // Width matches parent
                ViewGroup.LayoutParams.WRAP_CONTENT     // Height wraps content
        );
        params.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics())
        );
        courseView.setLayoutParams(params);
        flexboxLayout.addView(courseView);

        //Set Course Name
        courseNameButton.setText(string.toUpperCase());

        //Save Course Name
        newcourse.setName(courseNameButton.getText().toString());

        selectGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FirstQuestionnaireCoursesActivity.this);
                builder.setTitle("Select Course Goals")
                        .setMultiChoiceItems(allGoals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    newcourse.addGoal(allGoals[which].toString());
                                } else {
                                    newcourse.removeGoal(allGoals[which].toString());
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Update button text with selected items
                                if (!newcourse.getGoals().isEmpty()) {
                                    int selectedValuesCounter = 0;
                                    bottomText.setText("");
                                    for (boolean checkedItem : checkedItems) {
                                        if (checkedItem) {
                                            canGo = true;
                                            switch (selectedValuesCounter) {
                                                case 0: //Exam Prep
                                                    bottomText.setText(bottomText.getText() + "    Exam Prep    ");
                                                    break;
                                                case 1: //Homework Help
                                                    bottomText.setText(bottomText.getText() + "    HW Help    ");
                                                    break;
                                                case 2: //Routine Studying
                                                    bottomText.setText(bottomText.getText() + "    Routine Studying    ");
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        selectedValuesCounter++;
                                    }
                                } else {
                                    canGo = false;
                                    bottomText.setText("");
                                }

                                courses.remove(newcourse);
                                courses.add(newcourse);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        deleteCourseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setMessage("Are you sure you want to delete this course?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                flexboxLayout.removeView(courseView);
                                courses.remove(newcourse);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }
    private View.OnClickListener nextArrowOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canGo)
                {
                    sendBundle();
                    Intent i = new Intent(FirstQuestionnaireCoursesActivity.this, Survey.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Select at least one course and a goal to Go ahead", Toast.LENGTH_SHORT).show();
                }

            }
        };
    }
    private View.OnClickListener backArrowOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
    }
    private View.OnClickListener helpOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FirstQuestionnaireCoursesActivity.this);
                View customView = getLayoutInflater().inflate(R.layout.first_questionnaire_help_screen, null);
                TextView messageView = customView.findViewById(R.id.help_message);
                messageView.setText(instructions[0]);

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
    private TextView.OnEditorActionListener searchOnDone() {
        return new TextView.OnEditorActionListener()  {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addCourse(search.getText().toString());
                    search.setText("");
                    return true;
                }
                return false;
            }
        };
    }
    private void initializeComponents() {
        courses = new ArrayList<Course>();
        backArrow = findViewById(R.id.first_questionnaire_courses_back_arrow);
        nextArrow = findViewById(R.id.first_questionnaire_courses_finish);
        search = findViewById(R.id.first_questionnaire_courses_search);
        help = findViewById(R.id.first_questionnaire_courses_help);
    }
    private void sendBundle() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        DatabaseReference myRef = database.getReference("users").child(userId);
        myRef.child("courses").child(newcourse.getName()).setValue(newcourse.getGoals());
    }
}
