package com.example.coworkingillini.ui.invitations_sent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.coworkingillini.R;
import com.example.coworkingillini.databinding.InvitationsSentBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvitationsSentFragment extends Fragment {
    NavController navController;
    private InvitationsSentBinding binding;
    ImageView back;

    String userId;
    LinearLayout parentLayout;
    private FirebaseDatabase database;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         SentViewModel dashboardViewModel =
                new ViewModelProvider(this).get(SentViewModel.class);

        binding = InvitationsSentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        parentLayout = root.findViewById(R.id.sent_layout);
        TextView recieved = root.findViewById(R.id.recieved);
        TextView sent = root.findViewById(R.id.sent);

        sent.setBackgroundColor(Color.parseColor("#FEE8C4"));


        database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            DatabaseReference myRef = database.getReference("users").child(userId).child("Sent");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> receivedInvites = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        receivedInvites.add(snapshot.getKey());
                    }
                    parentLayout.removeAllViews();
                    createSentInvitesLayout(receivedInvites);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        recieved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_received);
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

    private void createSentInvitesLayout(List<String> receivedInvites) {
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

            for (int i = 0; i < receivedInvites.size(); i++) {
                View inviteView = inflater.inflate(R.layout.sentinvite, parentLayout, false);

                TextView profileName = inviteView.findViewById(R.id.profile_name_1);
                profileName.setText(receivedInvites.get(i));
                parentLayout.addView(inviteView);
            }
        }
    }


    private void handleReject(String profileName) {
        // Implement reject logic
    }

    private void handleAccept(String profileName) {
        // Implement accept logic
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