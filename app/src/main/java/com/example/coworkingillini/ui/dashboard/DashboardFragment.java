package com.example.coworkingillini.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coworkingillini.ChatActivity;
import com.example.coworkingillini.Chat_results;
import com.example.coworkingillini.ProfileActivity;
import com.example.coworkingillini.Questions;
import com.example.coworkingillini.R;
import com.example.coworkingillini.Wait_results;
import com.example.coworkingillini.databinding.FragmentDashboardBinding;
import com.example.coworkingillini.survey_instructions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    ImageView back;
    Map<String, String> Users = new HashMap<>();
    String userId;
    String key;
    String profileUsername;
    String myUsername;
    private FirebaseDatabase database;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        database = FirebaseDatabase.getInstance();
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    Users.put(snap.getKey(), snap.child("Username").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            DatabaseReference myref = database.getReference("users").child(userId);
            myref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myUsername = snapshot.child("Username").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            fetchMatchesAndCourses(Users);
        }
        return root;
    }


    private void fetchMatchesAndCourses(Map<String, String> usersMap) {
        // Reference to current user's Matches node
        DatabaseReference matchRef = database.getReference("users").child(userId).child("Matches");
        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot matchSnapshot : dataSnapshot.getChildren()) {
                        String matchedUserId = matchSnapshot.getKey();
                        String username = usersMap.get(matchedUserId);
                        Log.d("DashboardFragment", "Matched User ID: " + matchedUserId);

                        for (Map.Entry<String, String> entry : Users.entrySet()) {
                            if (entry.getValue().equals(matchedUserId)) {
                                Log.d("DashboardFragment", "Matched User ID 2: " + matchedUserId);
                                username = entry.getKey();
                                break;
                            }
                        }


                        if (username != null) {
                            Log.d("DashboardFragment", "Matched User ID: " + matchedUserId);
                            fetchCoursesAndInflateView(matchedUserId, username);

                        } else {
                            // Handle case where no matches exist
                            showNoMatchesMessage();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(getContext(), "Failed to load matches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCoursesAndInflateView(String username, String userId) {
        // Reference to the matched user's Courses node
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("courses");

        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> courses = new ArrayList<>();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String course = courseSnapshot.getKey();
                    if (course != null) {
                        courses.add(course);
                    }
                }
                inflateMatchedProfile(username, courses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(getContext(), "Failed to load courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inflateMatchedProfile(String username, List<String> courses) {
        // Inflate matchedprofile.xml layout
        // Add the inflated view to the parent layout
        RelativeLayout parentLayout = getActivity().findViewById(R.id.parentLayout);
        parentLayout.removeAllViews();
        View matchedProfileView = LayoutInflater.from(getContext()).inflate(R.layout.matched_profile, null);
        matchedProfileView.setLayoutParams(new RelativeLayout.LayoutParams(
                ChipGroup.LayoutParams.MATCH_PARENT,
                ChipGroup.LayoutParams.WRAP_CONTENT));

        // Set username in TextView
        TextView usernameTextView = matchedProfileView.findViewById(R.id.profile_card_1_demographic)
                .findViewById(R.id.matchname);
        usernameTextView.setText(username);

        // Add courses as chips in ChipGroup
        ChipGroup chipGroup = (ChipGroup) matchedProfileView.findViewById(R.id.profile_card_1_course_1).getParent();
        chipGroup.removeAllViews(); // Clear existing chips

        for (String course : courses) {
            Chip chip = new Chip(getContext());
            chip.setText(course);
            chip.setChipStrokeWidth(1);
            chip.setTextAppearance(R.style.Body);
            chip.setTextSize(14);
            chipGroup.addView(chip);
        }
        matchedProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUsername = username;
                int comparison = username.compareTo(myUsername);
                if (comparison <= 0) {
                    key = username + ":" + myUsername;
                } else {
                    key = myUsername + ":" + username;
                }
                checkDone();

            }
        });
        parentLayout.addView(matchedProfileView);
    }

    private void showNoMatchesMessage() {
        RelativeLayout parentLayout = getActivity().findViewById(R.id.parentLayout);

        TextView noMatchesMessage = new TextView(getContext());
        noMatchesMessage.setText("No matches found");
        noMatchesMessage.setGravity(Gravity.CENTER);
        noMatchesMessage.setTextSize(18);
        parentLayout.removeAllViews();
        parentLayout.addView(noMatchesMessage);
    }
    int ret = 0;
    public void checkDone(){
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        bundle.putString("Username", myUsername);
        DatabaseReference dbRef = database.getReference("chats").child(key);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DashboardFragment", "Matched User ID: " + profileUsername);
                Log.d("DashboardFragment", "TF: " + snapshot.child(myUsername).exists());
               if(snapshot.child(myUsername).exists() && !snapshot.child(profileUsername).exists())
               {
                   Intent i = new Intent(getContext(), Wait_results.class);
                   i.putExtras(bundle);
                   startActivity(i);
               }
               else if(snapshot.child(myUsername).exists() && snapshot.child(profileUsername).exists())
               {
                   Intent i = new Intent(getContext(), Chat_results.class);
                   i.putExtras(bundle);
                   startActivity(i);
               }
               else
               {
                   Intent i = new Intent(getContext(), survey_instructions.class);
                   i.putExtras(bundle);
                   startActivity(i);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("DashboardFragment", "Matched User ID Return: " + ret);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}