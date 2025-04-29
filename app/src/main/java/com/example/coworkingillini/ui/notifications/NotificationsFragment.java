package com.example.coworkingillini.ui.notifications;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coworkingillini.R;
import com.example.coworkingillini.databinding.FragmentNotificationsBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private FirebaseDatabase database;
    private String userId;

    private ImageView coursesInformationButton;
    private ImageView skillsInformationButton;
    private ImageView loudnessInformationButton;
    private ImageView paceInformationButton;
    private ImageView teamInformationButton;
    private ImageView cramInformationButton;

    private String courseinstructions = "Add your courses that you want to find study buddies for";
    private String skillinstructions = "Add your skills that you want to showcase to your study buddies ";
    private String loudnessinstructions = "0 means you like quiet working spaces, 10 means you're okay with noise";
    private String paceinstructions = "0 means you like slow pace, 10 means you're okay fast pace";
    private String teaminstructions = "0 means you like working induvidually, 10 means you like working collabratively";
    private String craminstructions = "0 means you like to start soon, 10 means you're okay with cramming";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            loadUserProfile();
        } else {
            Toast.makeText(getContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
        }

        setupEditListeners();



        return binding.getRoot();
    }




    private void loadUserProfile() {
        DatabaseReference userRef = database.getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateUIWithProfileData(snapshot);
                } else {
                    Toast.makeText(getContext(), "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSliderDialog(String title, String fieldKey, TextView targetView) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.slider_dialog, null);
        Slider slider = dialogView.findViewById(R.id.slider);
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);

        dialogTitle.setText(title);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("OK", (d, which) -> {
                    int value = (int) slider.getValue();
                    targetView.setText(title + ": " + value);
                    //saveValueToDatabase(fieldKey, String.valueOf(value));
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private void updateUIWithProfileData(DataSnapshot snapshot) {
        setText(binding.profileName, snapshot.child("Username").getValue(String.class));
        setText(binding.batchYear, snapshot.child("BatchYear").getValue(String.class));
        setText(binding.gender, snapshot.child("Gender").getValue(String.class));
        setText(binding.loudnessScore, "Loudness: " + getStringValue(snapshot, "Loudness"));
        setText(binding.studyPaceScore, "Study Pace: " + getStringValue(snapshot, "StudyPace"));
        setText(binding.teamPreferenceScore, "Team Preference: " + getStringValue(snapshot, "TeamPreference"));
        setText(binding.cramScore, "Cram: " + getStringValue(snapshot, "Cram"));




        updateChipGroup(binding.coursesChipGroup, snapshot.child("courses"), true);
        updateChipGroup(binding.skillsChipGroup, snapshot.child("skills"), false);
    }



    /**
     * Helper method to safely get a String value from a DataSnapshot.
     */
    private String getStringValue(DataSnapshot snapshot, String key) {
        return snapshot.child(key).getValue() != null ? snapshot.child(key).getValue().toString() : "N/A";
    }

    /**
     * Helper method to set text on a TextView.
     */
    private void setText(TextView textView, String text) {
        if (text != null) {
            textView.setText(text);
        }
    }

    /**
     * Update the ChipGroup with the provided data.
     */
    private void updateChipGroup(ChipGroup chipGroup, DataSnapshot dataSnapshot, boolean isCourses) {
        chipGroup.removeAllViews();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            String chipText = isCourses ? child.getKey() : child.getValue(String.class);
            if (chipText != null) {
                chipGroup.addView(createEditableChip(chipText));
            }
        }
    }

    /**
     * Create an editable chip with a close icon.
     */
    private Chip createEditableChip(String text) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.custom_diagram_layout, null);

                EditText editText = dialogView.findViewById(R.id.editText);
                CheckBox checkBox1 = dialogView.findViewById(R.id.checkbox1);
                CheckBox checkBox2 = dialogView.findViewById(R.id.checkbox2);
                CheckBox checkBox3 = dialogView.findViewById(R.id.checkbox3);

                builder.setView(dialogView)
                        .setTitle("Add Course")
                        .setPositiveButton("Modify", (dialog, which) -> {
                            String text = editText.getText().toString();
                            boolean isChecked1 = checkBox1.isChecked();
                            boolean isChecked2 = checkBox2.isChecked();
                            boolean isChecked3 = checkBox3.isChecked();
                            if (!text.isEmpty()) {
                                binding.coursesChipGroup.addView(createEditableChip(text));
                                binding.coursesChipGroup.removeView(chip);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        });
        chip.setOnCloseIconClickListener(v -> ((ViewGroup) chip.getParent()).removeView(chip));
        return chip;
    }



    /**
     * Set up listeners for editing actions like adding courses or skills.
     */
    private void setupEditListeners() {
        binding.SaveButton.setOnClickListener(v -> saveProfile());
        binding.addCourseButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.custom_diagram_layout, null);

            EditText editText = dialogView.findViewById(R.id.editText);
            CheckBox checkBox1 = dialogView.findViewById(R.id.checkbox1);
            CheckBox checkBox2 = dialogView.findViewById(R.id.checkbox2);
            CheckBox checkBox3 = dialogView.findViewById(R.id.checkbox3);

            builder.setView(dialogView)
                    .setTitle("Add Course")
                    .setPositiveButton("Add", (dialog, which) -> {
                        String text = editText.getText().toString();
                        boolean isChecked1 = checkBox1.isChecked();
                        boolean isChecked2 = checkBox2.isChecked();
                        boolean isChecked3 = checkBox3.isChecked();
                        if (!text.isEmpty()) {
                            binding.coursesChipGroup.addView(createEditableChip(text));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });
        binding.addSkillButton.setOnClickListener(v -> showAddDialog("Add Skill", binding.skillsChipGroup));
        binding.loudnessScore.setOnClickListener(v ->
                showSliderDialog("Loudness", "Loudness", binding.loudnessScore));

        binding.studyPaceScore.setOnClickListener(v ->
                showSliderDialog("Study Pace", "StudyPace", binding.studyPaceScore));

        binding.teamPreferenceScore.setOnClickListener(v ->
                showSliderDialog("Team Preference", "TeamPreference", binding.teamPreferenceScore));

        binding.cramScore.setOnClickListener(v ->
                showSliderDialog("Cram", "Cram", binding.cramScore));

        binding.batchYear.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenuInflater().inflate(R.menu.batch_year_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                binding.batchYear.setText(item.getTitle());
                //saveValueToDatabase("BatchYear", item.getTitle().toString());
                return true;
            });
            popup.show();
        });

        binding.gender.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenuInflater().inflate(R.menu.gender_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                binding.gender.setText(item.getTitle());
                //saveValueToDatabase("Gender", item.getTitle().toString());
                return true;
            });
            popup.show();
        });
        coursesInformationButton = binding.getRoot().findViewById(R.id.information_button_courses);
        coursesInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("What is this Courses Section?");
                builder.setMessage(courseinstructions);
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
        skillsInformationButton = binding.getRoot().findViewById(R.id.information_button_skills);
        skillsInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("What is this Skills Section?");
                builder.setMessage(skillinstructions);
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
        loudnessInformationButton = binding.getRoot().findViewById(R.id.information_button_loudness);
        loudnessInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("What is Loudness?");
                builder.setMessage(loudnessinstructions);
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
        paceInformationButton = binding.getRoot().findViewById(R.id.information_button_pace);
        paceInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("What is this Pace?");
                builder.setMessage(paceinstructions);
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
        teamInformationButton = binding.getRoot().findViewById(R.id.information_button_team);
        teamInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("What is this Team?");
                builder.setMessage(teaminstructions);
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
        cramInformationButton = binding.getRoot().findViewById(R.id.information_button_cram);
        cramInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("What is this Cram?");
                builder.setMessage(craminstructions);
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
    }

    /*private void saveValueToDatabase(String key, String value) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child(key)
                .setValue(value)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show());
    }*/

    /**
     * Show a dialog to add a new chip to a ChipGroup.
     */
    private void showAddDialog(String title, ChipGroup chipGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                chipGroup.addView(createEditableChip(text));
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Save the profile data to Firebase.
     */
    private void saveProfile() {
        DatabaseReference userRef = database.getReference("users").child(userId);
        Map<String, Object> updates = new HashMap<>();

        updates.put("Username", binding.profileName.getText().toString());
        updates.put("BatchYear", binding.batchYear.getText().toString());
        updates.put("Gender", binding.gender.getText().toString());
        updates.put("Loudness", parseInteger(binding.loudnessScore.getText().toString()));
        updates.put("StudyPace", parseInteger(binding.studyPaceScore.getText().toString()));
        updates.put("TeamPreference", parseInteger(binding.teamPreferenceScore.getText().toString()));
        updates.put("Cram", parseInteger(binding.cramScore.getText().toString()));

        updates.put("courses", extractChipGroupData(binding.coursesChipGroup));
        updates.put("Skills", extractChipGroupData(binding.skillsChipGroup));

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Extract data from a ChipGroup as a map.
     */
    private Map<String, Object> extractChipGroupData(ChipGroup chipGroup) {
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            data.put(chip.getText().toString(), true);
        }
        return data;
    }

    /**
     * Parse an integer safely from a String.
     */
    private int parseInteger(String text) {
        try {
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}