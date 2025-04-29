package com.example.coworkingillini.ui.invitations_received;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.coworkingillini.R;
import com.example.coworkingillini.databinding.InvitationsRequestsBinding;
import com.example.coworkingillini.ui.invitations_received.ReceivedViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class InvitationsRecievedFragment extends Fragment {
    NavController navController;
    private InvitationsRequestsBinding binding;
    ImageView back;
    String userId;
    LinearLayout parentLayout;
    private FirebaseDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReceivedViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ReceivedViewModel.class);

        binding = InvitationsRequestsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        parentLayout = root.findViewById(R.id.received_layout);
        TextView recieved = root.findViewById(R.id.recieved);
        recieved.setBackgroundColor(Color.parseColor("#FEE8C4"));
        TextView sent = root.findViewById(R.id.sent);
        database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            DatabaseReference myRef = database.getReference("users").child(userId).child("Received");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Long> receivedInvites = new HashMap<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        receivedInvites.put(snapshot.getKey(), snapshot.getValue(Long.class));
                    }
                    parentLayout.removeAllViews();
                    createReceivedInvitesLayout(receivedInvites);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_sent);
            }
        });
        ImageView back = root.findViewById(R.id.chat_header_back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_people);
            }
        });
        return root;
    }



    private void createReceivedInvitesLayout(Map<String, Long> receivedInvites) {
        // Clear any existing views in the parent layout
        parentLayout.removeAllViews();

        if (receivedInvites == null || receivedInvites.isEmpty()) {
            // Create and add a TextView for "No requests pending"
            TextView noRequestsView = new TextView(getContext());
            noRequestsView.setText("No requests pending");
            noRequestsView.setTextSize(18); // Set text size (in sp)
            noRequestsView.setGravity(Gravity.CENTER);
            noRequestsView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            noRequestsView.setPadding(0, 50, 0, 0); // Add some top padding (in pixels)
            parentLayout.addView(noRequestsView);
        } else {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            for (Map.Entry<String, Long> entry : receivedInvites.entrySet()) {
                View inviteView = inflater.inflate(R.layout.receivedinvite, parentLayout, false);

                TextView profileName = inviteView.findViewById(R.id.profile_name_1);
                profileName.setText(entry.getKey());

                // Set unique IDs for the buttons
                MaterialButton rejectButton = inviteView.findViewById(R.id.reject_button_1);
                MaterialButton acceptButton = inviteView.findViewById(R.id.accept_button_1);

                // Set click listeners for buttons
                rejectButton.setOnClickListener(v -> handleReject(entry.getKey()));
                acceptButton.setOnClickListener(v -> handleAccept(entry.getKey()));

                parentLayout.addView(inviteView);
            }
        }
    }

    private void handleReject(String profileName) {
        DatabaseReference userRef = database.getReference("users").child(userId);
        userRef.child("Received").child(profileName).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Invitation rejected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to reject invitation", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleAccept(String profileName) {
        DatabaseReference userRef = database.getReference("users").child(userId);
        userRef.child("Received").child(profileName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.getValue(Long.class);
                if (value != null) {
                    // Add to Matches
                    userRef.child("Matches").child(profileName).setValue(value)
                            .addOnSuccessListener(aVoid -> {
                                // Remove from Received
                                userRef.child("Received").child(profileName).removeValue()
                                        .addOnSuccessListener(aVoid1 -> {
                                            Toast.makeText(getContext(), "Invitation accepted", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Failed to remove from Received", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to add to Matches", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

        @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}