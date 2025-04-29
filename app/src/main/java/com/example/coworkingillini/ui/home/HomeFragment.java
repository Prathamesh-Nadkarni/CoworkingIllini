package com.example.coworkingillini.ui.home;


import static android.content.ContentValues.TAG;
import static java.lang.Math.abs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coworkingillini.ProfileData;
import com.example.coworkingillini.R;
import com.example.coworkingillini.SignupActivity;
import com.example.coworkingillini.databinding.FragmentHomeBinding;
import com.example.coworkingillini.ui.invitations_received.InvitationsRecievedFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    private List<ProfileData> profiles = new ArrayList<>();
    private int currentProfileIndex = 0;

    ImageView menu_icon;
    private TextView profileName, loud, pace, group, cram, compatibility_score;
    private CardView profileCard;
    private ChipGroup llcourses, llskills;
    private FirebaseDatabase database;
    private String currentusername = "";
    private String profileUsername = "";
    private String userId;
    private float initialX, initialY;
    private boolean isDragging = false;
    private FragmentHomeBinding binding;
    private List<Boolean> decisions = new ArrayList<>();

    private ImageView informationButton;
    long userloud, userpace, usergroup, usercram;

    private List<String> selectedCourses = new ArrayList<>();
    private List<String> selectedSkills = new ArrayList<>();
    private List<String> selectedGoals = new ArrayList<>();

    private String instructions = "The compatibility score is calculated on the basis of closeness of the user's score on the parameters of Loudness, Team Preference, Study Pace and Cram Nature with the user they are viewing ";
    ImageView invitations;
    NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        menu_icon = root.findViewById(R.id.menu_icon);

       // navController = Navigation.findNavController(requireActivity(), R.id.nav_people);
        database = FirebaseDatabase.getInstance();
        profileName = root.findViewById(R.id.profile_name);
        llcourses = root.findViewById(R.id.course);
        llskills = root.findViewById(R.id.skills);
        invitations = root.findViewById(R.id.invitations);
        loud = root.findViewById(R.id.loudness_text);
        pace = root.findViewById(R.id.pace_text);
        group = root.findViewById(R.id.team_preference_text);
        cram = root.findViewById(R.id.cram_text);
        compatibility_score = root.findViewById(R.id.compatibility_score);
        profileCard = root.findViewById(R.id.profile_card);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);


        if (userId != null) {
            DatabaseReference myRef = database.getReference("users").child(userId).child("Username");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        profileUsername = dataSnapshot.getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching username: " + databaseError.getMessage());
                }
            });
        }


        initializeProfiles(() -> {
            loadProfile();
            View swipeView = root.findViewById(R.id.profile_card);
            swipeView.setOnTouchListener(this::onTouch);
        });


        invitations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.navigate(R.id.nav_received);
            }
        });

        informationButton=root.findViewById(R.id.information_button_compatibility_score);
        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("What is this Compatibility Score?");
                builder.setMessage(instructions);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                ListView skillListView = dialogView.findViewById(R.id.skillListView);
                ListView goalListView = dialogView.findViewById(R.id.goalListView);
                Button applyButton = dialogView.findViewById(R.id.applyButton);
                Button cancelButton = dialogView.findViewById(R.id.cancelButton);

                // Fetch current user's courses
                DatabaseReference myRef = database.getReference("users").child(userId).child("courses");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Set<String> coursesSet = new HashSet<>();
                        for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                            String courseName = courseSnapshot.getKey();
                            if (courseName != null) {
                                coursesSet.add(courseName);
                            }
                        }
                        List<String> courses = new ArrayList<>(coursesSet);
                        Collections.sort(courses);

                        // Setup ListViews
                        setupListView(skillListView, getAllSkills(), selectedSkills);
                        setupListView(goalListView, Arrays.asList("Homework Help", "Routine Studying", "Exam Prep"), selectedGoals);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching courses: " + databaseError.getMessage());
                    }
                });

                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedSkills = getSelectedItems(skillListView);
                        selectedGoals = getSelectedItems(goalListView);
                        applyFilters(selectedCourses, selectedSkills, selectedGoals); // Ensure selectedCourses is populated appropriately
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCourses.clear();
                        selectedSkills.clear();
                        selectedGoals.clear();
                        applyFilters(selectedCourses, selectedSkills, selectedGoals);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });



        return root;
    }

    private List<String> getAllCourses() {
        Set<String> coursesSet = new HashSet<>();
        for (ProfileData profile : profiles) {
            for (List<String> course : profile.getCourses()) {
                coursesSet.add(course.get(0));
            }
        }
        List<String> courses = new ArrayList<>(coursesSet);
        Collections.sort(courses);
        return courses;
    }

    private List<String> getAllSkills() {
        Set<String> skillsSet = new HashSet<>();
        for (ProfileData profile : profiles) {
            skillsSet.addAll(profile.getSkills());
        }
        List<String> skills = new ArrayList<>(skillsSet);
        Collections.sort(skills);
        return skills;
    }



    private void setupListView(ListView listView, List<String> items, List<String> selectedItems) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_multiple_choice, items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for (int i = 0; i < items.size(); i++) {
            if (selectedItems.contains(items.get(i))) {
                listView.setItemChecked(i, true);
            }
        }
    }


    private List<String> getSelectedItems(ListView listView) {
        List<String> selectedItems = new ArrayList<>();
        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
        for (int i = 0; i < listView.getCount(); i++) {
            if (checkedItems.get(i)) {
                Log.d(TAG, "Selected item: " + listView.getItemAtPosition(i).toString());
                selectedItems.add(listView.getItemAtPosition(i).toString());
            }
        }
        return selectedItems;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        // Get SharedPreferences instance
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            // Show your first dialog
            AlertDialog.Builder builder1 = new AlertDialog.Builder(requireContext());
            builder1.setTitle("Instructions")
                    .setMessage("Swipe Right to send a request and Swipe Left to Reject")
                    .setPositiveButton("Next", (dialog, which) -> {
                        // Show second dialog
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(requireContext());
                        builder2.setTitle("Instructions")
                                .setMessage("Swipe Up and Down to browse throught the profiles without sending/rejecting a potential study buddy")
                                .setPositiveButton("Got it", (dialog2, which2) -> {
                                    // Mark first run as complete
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("isFirstRun", false);
                                    editor.apply();
                                })
                                .show();
                    })
                    .show();
        }
    }

    private void initializeProfiles(Runnable onComplete) {
        DatabaseReference myRef = database.getReference("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profiles.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId_d = userSnapshot.getKey();
                    if (userId_d != null && userId_d.equals(userId)) continue;

                    String username = userSnapshot.child("Username").getValue(String.class);
                    Integer cram = userSnapshot.child("Cram").getValue(Integer.class);
                    Integer loudness = userSnapshot.child("Loudness").getValue(Integer.class);
                    Integer studyPace = userSnapshot.child("StudyPace").getValue(Integer.class);
                    Integer teamPreference = userSnapshot.child("TeamPreference").getValue(Integer.class);

                    if (username == null || cram == null || loudness == null || studyPace == null || teamPreference == null) {
                        continue;
                    }

                    List<List<String>> courselist = new ArrayList<>();
                    DataSnapshot coursesSnapshot = userSnapshot.child("courses");
                    for (DataSnapshot courseSnapshot : coursesSnapshot.getChildren()) {
                        String courseName = courseSnapshot.getKey();
                        List<String> courseDetails = new ArrayList<>();
                        courseDetails.add(courseName);
                        for (DataSnapshot activitySnapshot : courseSnapshot.getChildren()) {
                            courseDetails.add(activitySnapshot.getValue(String.class));
                        }
                        courselist.add(courseDetails);
                    }

                    List<String> skills = new ArrayList<>();
                    DataSnapshot skillsSnapshot = userSnapshot.child("Skills");
                    for (DataSnapshot skillSnapshot : skillsSnapshot.getChildren()) {
                        skills.add(skillSnapshot.getValue(String.class));
                    }

                    int compatibilityScore = (int) (Math.random() * 100);
                    ProfileData profile = new ProfileData(username, cram, loudness, studyPace, teamPreference, courselist, skills, compatibilityScore);
                    profiles.add(profile);
                }
                onComplete.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading profiles: " + databaseError.getMessage());
                onComplete.run();
            }
        });
    }

    private void applyFilters(List<String> selectedCourses, List<String> selectedSkills, List<String> selectedGoals) {
        List<ProfileData> filteredProfiles = new ArrayList<>();

        for (ProfileData profile : profiles) {
            boolean hasCourses = selectedCourses.isEmpty() || profile.getCourses().stream()
                    .anyMatch(courseList -> selectedCourses.contains(courseList.get(0)));
            Log.d(TAG, "hasCourses: " + profile.getName());
            // Check if any course matches

            boolean hasSkills = selectedSkills.isEmpty() || profile.getSkills().stream()
                    .anyMatch(skill -> selectedSkills.contains(skill)); // Check if any skill matches



            boolean hasGoals = selectedGoals.isEmpty() || profile.getCourses().stream()
                    .flatMap(courseList -> courseList.stream().skip(1)) // Skip the first element (course name)
                    .anyMatch(goal -> selectedGoals.contains(goal));



            // Add profile if it matches all selected criteria
            if (hasSkills && hasGoals) {
                Log.d(TAG, "has all: " + profile.getName() + " " + profile.getCourses() +  " " + profile.getSkills());
                filteredProfiles.add(profile);
            }
        }

        // Update the profiles list with filtered results
        profiles.clear();
        profiles.addAll(filteredProfiles);
        loadProfile(); // Refresh the displayed profile
    }


    private void loadProfile() {
        if (profiles.isEmpty()) {
            Toast.makeText(getContext(), "No profiles match the selected filters.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentProfileIndex < profiles.size()) {
            addData();
            resetCardPosition();
        } else {
            Toast.makeText(getContext(), "No more profiles available.", Toast.LENGTH_SHORT).show();
        }
    }


    private void addData() {
        ProfileData currentProfile = profiles.get(currentProfileIndex);
        currentusername = currentProfile.getName();
        profileName.setText(currentusername);
        llcourses.removeAllViews();
        llskills.removeAllViews();
        compatibility_score.setText(String.format("Compatibility Score: %d",calculateScore()));


        for (List<String> course : currentProfile.getCourses().subList(0,2)) {
            createCourseTextViews(llcourses, course.get(0), course);
        }

        if(selectedSkills.isEmpty())
        {
            createSkillsTextViews(llskills, currentProfile.getSkills().subList(0,2));
        }
        else
        {
            createSkillsTextViews(llskills, selectedSkills.subList(0,2));
        }


        loud.setText(String.format("Loudness: %d", currentProfile.getLoud()));
        pace.setText(String.format("Study Pace: %d", currentProfile.getPace()));
        group.setText(String.format("Team: %d", currentProfile.getGroup()));
        cram.setText(String.format("Cram: %d", currentProfile.getCram()));
    }

    private boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                isDragging = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    float deltaX = event.getX() - initialX;
                    float deltaY = event.getY() - initialY;
                    profileCard.setTranslationX(deltaX);
                    profileCard.setTranslationY(deltaY);
                    float rotationDegree = deltaX / 20;
                    profileCard.setRotation(rotationDegree);
                    float alpha = 1 - Math.min(1f, (Math.abs(deltaX) + Math.abs(deltaY)) / (v.getWidth() / 2f));
                    profileCard.setAlpha(alpha);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    float finalX = event.getX() - initialX;
                    float finalY = event.getY() - initialY;
                    if (Math.abs(finalX) > v.getWidth() / 4) {
                        if (finalX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    } else if (Math.abs(finalY) > v.getHeight() / 4) {
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
                .withEndAction(() -> {
                    loadNextProfile();
                });
    }

    private void onSwipeRight() {
        recordDecision(true);
        database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        DatabaseReference myRef = database.getReference("users").child(userId).child("Sent");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    myRef.setValue(new HashMap<String, Object>());
                }
                myRef.child(currentusername).setValue(calculateScore());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("Firebase", "Error checking/creating Received node: " + databaseError.getMessage());
            }
        });

        setRequest();
        profileCard.animate()
                .translationX(1000)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    loadNextProfile();
                });
    }

    private void onSwipeUp() {
        animateVerticalSwipe(-1000);
    }

    private void onSwipeDown() {
        animateVerticalSwipe(1000);
    }

    private int calculateScore() {
        double score = 0.0;
        ProfileData currentProfile = profiles.get(currentProfileIndex);
        long profileloud = currentProfile.getLoud();
        long profilepace = currentProfile.getPace();
        long profilegroup = currentProfile.getGroup();
        long profilecram = currentProfile.getCram();





        DatabaseReference myRef = database.getReference("users").child(userId);
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        Log.d("firebase", "Data: " + dataSnapshot.getValue());
                        userloud = (long) dataSnapshot.child("Loudness").getValue();
                        userpace = (long) dataSnapshot.child("StudyPace").getValue();
                        usergroup = (long) dataSnapshot.child("TeamPreference").getValue();
                        usercram = (long) dataSnapshot.child("Cram").getValue();
                    } else {
                        Log.d("firebase", "No data available");
                    }
                }
            }
        });

        double maxDifference = 9.0;
        double totalWeight = 4.0;

        score += (maxDifference - Math.abs(userloud - profileloud)) / maxDifference;
        score += (maxDifference - Math.abs(usercram - profilecram)) / maxDifference;
        score += (maxDifference - Math.abs(userpace - profilepace)) / maxDifference;
        score += (maxDifference - Math.abs(usergroup - profilegroup)) / maxDifference;

        double percentage = (score / totalWeight) * 100;

        return (int) Math.round(percentage);
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
            Toast.makeText(getContext(), "No more profiles available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPreviousProfile() {
        currentProfileIndex--;

        if (currentProfileIndex >= 0) {
            loadProfile();
        } else {
            Toast.makeText(getContext(), "No previous profiles available.", Toast.LENGTH_SHORT).show();
            currentProfileIndex = 0;
        }
    }

    /*
 private void createCourseTextViews(ChipGroup parentLayout, String courseName, List<String> texts) {
        ChipGroup courseLayout = new ChipGroup(getContext());
        courseLayout.setLayoutParams(new ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.MATCH_PARENT,
                ChipGroup.LayoutParams.WRAP_CONTENT));
        courseLayout.setChipSpacing(2);
        // Disable minimum touch target size to remove extra padding

        Chip courseView = new Chip(getContext());
        courseView.setLayoutParams(new ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT));
        courseView.setText(courseName);
        courseView.setTypeface(null, Typeface.BOLD);
        courseView.setEnsureMinTouchTargetSize(false);

     */

    private void createCourseTextViews(ChipGroup parentLayout, String courseName, List<String> texts) {
        ChipGroup courseLayout = new ChipGroup(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, (int) (4 * getResources().getDisplayMetrics().density), 0, 0);
        courseLayout.setLayoutParams(layoutParams);
        courseLayout.setChipSpacing(2);

        Chip courseView = new Chip(getContext());
        courseView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        courseView.setText(courseName);
        courseView.setTypeface(null, Typeface.BOLD);
        courseView.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#725C39")));
        courseView.setChipStrokeWidth(0);
        courseView.setTextStartPadding(1);
        courseView.setTextEndPadding(1);
        courseView.setChipStartPadding(0);
        courseView.setChipEndPadding(0);
        courseView.setTextAppearance(R.style.Chip);
        courseLayout.addView(courseView);

        for (int i = 1; i < texts.size(); i++) {
            Chip textView = new Chip(getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(texts.get(i));
            textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.roundtext));
            textView.setPadding(
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0,
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0
            );
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            courseLayout.setChipSpacing((int) (8 * getResources().getDisplayMetrics().density));

            textView.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#725C39")));
            textView.setChipStrokeWidth(1);
            textView.setTextStartPadding(1);
            textView.setTextEndPadding(1);
            textView.setChipStartPadding(2);
            textView.setChipEndPadding(2);
            textView.setTextAppearance(R.style.Chip);

            courseLayout.addView(textView);
        }

        parentLayout.addView(courseLayout);
    }



    private void createSkillsTextViews(ChipGroup parentLayout, List<String> texts) {
        ChipGroup skillsLayout = new ChipGroup(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, (int) (4 * getResources().getDisplayMetrics().density), 0, 0);
        skillsLayout.setLayoutParams(layoutParams);
        skillsLayout.setChipSpacing(2);

        for (String skill : texts) {
            Chip textView = new Chip(getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(skill);
            textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.roundtext));
            textView.setPadding(
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0,
                    (int) (2 * getResources().getDisplayMetrics().density),
                    0
            );
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            skillsLayout.setChipSpacing((int) (8 * getResources().getDisplayMetrics().density));

            textView.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#725C39")));
            textView.setChipStrokeWidth(1);
            textView.setTextStartPadding(1);
            textView.setTextEndPadding(1);
            textView.setChipStartPadding(2);
            textView.setChipEndPadding(2);
            textView.setTextAppearance(R.style.Chip);
            skillsLayout.addView(textView);
        }

        parentLayout.addView(skillsLayout);
    }

    private void recordDecision(boolean accepted) {
        decisions.add(accepted);
        decisions.add(accepted);
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

    public void setRequest() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String username = childSnapshot.child("Username").getValue(String.class);
                    if (username != null && username.equals(currentusername)) {
                        DatabaseReference receivedRef = childSnapshot.getRef().child("Received");
                        receivedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    receivedRef.setValue(new HashMap<String, Object>());
                                }
                                receivedRef.child(profileUsername).setValue(calculateScore());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle possible errors
                                Log.e("Firebase", "Error checking/creating Received node: " + databaseError.getMessage());
                            }
                        });
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}