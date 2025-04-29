package com.example.coworkingillini;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class View_results extends AppCompatActivity {
    String key;
    String username;
    DatabaseReference chatRef;
    Bundle bundle;
    FirebaseDatabase database;
    ImageView chat_header_results;

    String otherUsername;
    private TextView txtView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view_results);


        // Find the Top back button by its ID
        ImageButton backToMatches = findViewById(R.id.chat_header_back_arrow);
        Intent intent1 = getIntent();
        bundle = intent1.getExtras();
        if (bundle != null) {
            key = bundle.getString("key");
            username = bundle.getString("Username");
            String[] usernames = key.split(":");
            otherUsername = usernames[0].equals(username) ? usernames[1] : usernames[0];
        }
        txtView1 = findViewById(R.id.chat_header_profile_name);
        txtView1.setText(otherUsername);


        backToMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(View_results.this, Survey.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

       MaterialButton ViewResults = findViewById(R.id.View_Results);
       ViewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(View_results.this, Chat_results.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }




}