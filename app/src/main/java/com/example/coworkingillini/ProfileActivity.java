package com.example.coworkingillini;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private List<ProfileData> profiles;
    private int currentProfileIndex = 0;
    private TextView profileName, score, loud, pace, group, cram;
    private CardView profileCard;
    private LinearLayout llcourses;
    private LinearLayout llskills;
    private ImageView navigate;


    private float initialX;
    private float initialY;
    private boolean isDragging = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout swipeLayout = findViewById(R.id.profile_text);
        profileName = findViewById(R.id.profile_name);
        llcourses = findViewById(R.id.course);
        llskills = findViewById(R.id.skills);
        loud = findViewById(R.id.loud);
        pace = findViewById(R.id.pace);
        group =findViewById(R.id.group);
        cram = findViewById(R.id.cram);
        score = findViewById(R.id.score);
        profileCard = findViewById(R.id.profile_card);


        swipeLayout.setOnTouchListener(this::onTouch);

        initializeProfiles();

        loadProfile();


    }



    private void createCourseTextViews(LinearLayout parentLayout, String courseName, List<String> texts) {

        LinearLayout courseLayout = new LinearLayout(this);
        courseLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        courseLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView courseView = new TextView(this);
        courseView.setId(View.generateViewId());
        courseView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        courseView.setText(courseName);
        courseView.setTypeface(null, Typeface.BOLD);
        ((LinearLayout.LayoutParams) courseView.getLayoutParams()).setMargins(0, 0,
                (int) (5 * getResources().getDisplayMetrics().density), 0);
        courseLayout.addView(courseView);

        for (int i = 1; i < texts.size(); i++) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(texts.get(i));
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.roundtext));
            textView.setPadding(
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0,
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            params.setMargins((int) (5 * getResources().getDisplayMetrics().density), 0, 0, 0);

            courseLayout.addView(textView);
        }

        parentLayout.addView(courseLayout);
    }


    private void createSkillsTextViews(LinearLayout parentLayout, List<String> texts) {

        LinearLayout courseLayout = new LinearLayout(this);
        courseLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        courseLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView courseView = new TextView(this);
        courseView.setId(View.generateViewId());
        courseView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < texts.size(); i++) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(texts.get(i));
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.roundtext));
            textView.setPadding(
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0,
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            params.setMargins((int) (5 * getResources().getDisplayMetrics().density), 0, 0, 0);

            courseLayout.addView(textView);
        }

        parentLayout.addView(courseLayout);
    }


    private void initializeProfiles() {
        List<String> list1 = new ArrayList<>(Arrays.asList("CS 143", "Exam Prepper", "Struggler", "Homework Checker"));

        List<String> list2 = Arrays.asList("CS 257", "Exam Prepper", "Homework Checker");

        List<List<String>> courselist = new ArrayList<>(Arrays.asList(list1, list2));
        ProfileData profile1 = new ProfileData("Blue Whale", 4, 6,7,8, courselist,
                new ArrayList<>(Arrays.asList("Java", "Python")), 75);

        list1 = Arrays.asList("CS 257", "Exam Prepper", "Struggler");
        list2 = Arrays.asList("CS 358", "Exam Prepper", "Struggler", "Homework Checker");
        courselist = Arrays.asList(list1, list2);

        ProfileData profile2 = new ProfileData("Red Lion", 9, 2,1,3, courselist,
                new ArrayList<>(Arrays.asList("Java", "C++")), 95);


        list1 = Arrays.asList("CS 358", "Exam Prepper", "Struggler");
        list2 = Arrays.asList("CS 445", "Exam Prepper", "Homework Checker");
        courselist = Arrays.asList(list1, list2);

        ProfileData profile3 = new ProfileData("Green Elephant", 4, 6,7,8, courselist,
                new ArrayList<>(Arrays.asList("Python", "AI", "C++")), 75);


        list1 = Arrays.asList("CS 257", "Exam Prepper", "Struggler");
        list2 = Arrays.asList("CS 358", "Exam Prepper", "Struggler", "Homework Checker");
        courselist = Arrays.asList(list1, list2);
        ProfileData profile4 = new ProfileData("Yellow Seal", 9, 2,1,3, courselist,
                new ArrayList<>(Arrays.asList("Design", "Python")), 80);

        list1 = Arrays.asList("CS 257", "Exam Prepper", "Struggler");
        list2 = Arrays.asList("CS 447", "Exam Prepper", "Homework Checker");
        courselist = Arrays.asList(list1, list2);
        ProfileData profile5 = new ProfileData("Black Flamingo", 4, 6,7,8, courselist,
                new ArrayList<>(Arrays.asList("C++", "Python")), 20);

        list1 = Arrays.asList("CS 358", "Exam Prepper", "Struggler");
        list2 = Arrays.asList("CS 445", "Exam Prepper", "Homework Checker", "Struggler");
        courselist = Arrays.asList(list1, list2);
        ProfileData profile6 = new ProfileData("Pink Bear", 9, 2,1,3, courselist,
                new ArrayList<>(Arrays.asList("Java", "Python")), 60);
        profiles = new ArrayList<>();
        profiles.add(profile1);
        profiles.add(profile2);
        profiles.add(profile3);
        profiles.add(profile4);
        profiles.add(profile5);
        profiles.add(profile6);
        // Add more profiles as needed
    }

    private void loadProfile() {
        if (currentProfileIndex < profiles.size()) {

            addData();
            resetCardPosition();
        } else {
            Toast.makeText(this, "No more profiles available.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void addData()
    {
        ProfileData currentProfile = profiles.get(currentProfileIndex);
        profileName.setText(currentProfile.getName());
        llcourses.removeAllViews();
        llskills.removeAllViews();
        for(List<String> course : currentProfile.getCourses())
        {
            createCourseTextViews(llcourses, course.get(0), course);
        }
        createSkillsTextViews(llskills, currentProfile.getSkills());
        loud.setText("Loud - " + currentProfile.getLoud());
        pace.setText("Study Pace - " + currentProfile.getPace());
        group.setText("Group Nature - " + currentProfile.getGroup());
        cram.setText("Cram - " + currentProfile.getCram());
        score.setText(currentProfile.getCompatibilityScore()+"");
    }

    private void resetCardPosition() {
        profileCard.setTranslationX(0);
        profileCard.setTranslationY(0);
        profileCard.setRotation(0);
        profileCard.setScaleX(1f);
        profileCard.setScaleY(1f);
        profileCard.setAlpha(1f);
        isDragging = false;
    }

    private List<Boolean> decisions = new ArrayList<>();

    private void recordDecision(boolean accepted) {
        decisions.add(accepted);
        if (accepted) {
            Toast.makeText(this, "You accepted the profile.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You rejected the profile.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                isDragging = true;
                return true;

            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - initialX;
                float deltaY = event.getY() - initialY;

                if (isDragging) {
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        profileCard.setTranslationX(deltaX);
                        float rotationDegree = deltaX / 10;
                        profileCard.setRotation(rotationDegree);

                        float alpha = 1 - Math.abs(deltaX) / v.getWidth();
                        profileCard.setAlpha(alpha);
                    } else {
                        profileCard.setTranslationY(deltaY);

                        float alpha = 1 - Math.abs(deltaY) / v.getHeight();
                        profileCard.setAlpha(alpha);
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    float finalX = event.getX() - initialX;
                    float finalY = event.getY() - initialY;

                    if (Math.abs(finalX) > v.getWidth() / 2) {
                        if (finalX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    } else if (Math.abs(finalY) > v.getHeight() / 2) {
                        if (finalY > 0) {
                            onSwipeDown();
                        } else {
                            onSwipeUp();
                        }
                    } else {
                        resetCardPosition();
                    }
                    isDragging = false;
                }
                return true;

            default:
                return false;
        }
    }

    private void onSwipeLeft() {
        recordDecision(false);

        profileCard.animate()
                .translationX(-1000)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> loadNextProfile());
    }

    private void onSwipeRight() {
        recordDecision(true);

        profileCard.animate()
                .translationX(1000)
                .rotation(30f)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> loadNextProfile());
    }

    private void onSwipeUp() {
        animateVerticalSwipe(-1000);
    }

    private void onSwipeDown() {
        animateVerticalSwipe(1000);
    }

    private void animateVerticalSwipe(float translationY) {
        profileCard.animate()
                .translationY(translationY)
                .rotation(-15f)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    if (translationY < 0) {
                        loadNextProfile();
                    } else {
                        loadPreviousProfile();
                    }
                });
    }

    private void loadNextProfile() {
        currentProfileIndex++;

        if (currentProfileIndex < profiles.size()) {
            loadProfile();
        } else {
            Toast.makeText(this, "No more profiles available.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadPreviousProfile() {
        currentProfileIndex--;

        if (currentProfileIndex >= 0) {
            loadProfile();
        } else {
            Toast.makeText(this, "No previous profiles available.", Toast.LENGTH_SHORT).show();
            currentProfileIndex = 0;
        }
    }
}