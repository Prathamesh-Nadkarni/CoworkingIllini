package com.example.coworkingillini;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private String[] colors = {
            "Red", "White", "Blue", "Yellow", "Black", "Green", "Brown", "Pink", "Purple", "Orange",
            "Gray", "Violet", "Indigo", "Turquoise", "Aqua", "Beige", "Magenta", "Cyan", "Gold", "Silver",
            "Maroon", "Crimson", "Olive", "Lavender", "Teal", "Navy", "Lime", "Peach", "Mint", "Plum",
            "Emerald", "Charcoal", "Copper", "Ivory", "Rose", "Coral", "Aquamarine", "Amber", "Periwinkle",
            "Blush", "Topaz", "Onyx", "Ruby", "Sapphire", "Jade", "Tangerine", "Slate", "Scarlet"
    };
    private String[] animals = {
            "Panda", "Lion", "Tiger", "Cat", "Dog", "Elephant", "Giraffe", "Zebra", "Kangaroo", "Koala",
            "Bear", "Cheetah", "Leopard", "Horse", "Rabbit", "Wolf", "Fox", "Monkey", "Gorilla", "Panda",
            "Penguin", "Eagle", "Falcon", "Hawk", "Owl", "Peacock", "Parrot", "Sparrow", "Crow", "Dove",
            "Swan", "Vulture", "Crow", "Bat", "Mole", "Bison", "Crocodile", "Alligator", "Shark", "Whale",
            "Dolphin", "Seal", "Otter", "Lynx", "Raccoon", "Badger", "Squirrel", "Chipmunk", "Armadillo",
            "Sloth", "Anteater", "Porcupine", "Koala", "Chameleon", "Tiger Shark", "Komodo Dragon",
            "Peafowl", "Bald Eagle", "Lemur", "Coyote", "Ibex", "Bison", "Pelican", "Seal", "Wolverine",
            "Walrus", "Dragonfly"
    };

    private String[] instructions = {"This name is to allow you to preserve anonymity and allow you to surf the app while keeping you a verified user. Press Regenerate to change your chosen name"};
    private Button regenerateButton;
    ImageView first_questionnaire_display_name_help;
    private Button submitButton;
    private ImageView backArrow;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*      Default things      */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_questionnaire_display_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.first_questionnaire_display_name_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        DatabaseReference myRef = database.getReference("users").child(userId);
        first_questionnaire_display_name_help = findViewById(R.id.first_questionnaire_display_name_help);

        first_questionnaire_display_name_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setTitle("Display Name Help");
                builder.setMessage("Your display name is how other users will see you. Choose a unique name that represents you, but avoid using your real name for privacy reasons.");
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
        /*      generating username       */
        final String[] randomUsername = {generateUsername()};
        TextView usernameText = findViewById(R.id.first_questionnaire_display_name_help_display_name);
        usernameText.setText(randomUsername[0]);

        /*      regenerateButton onClick actions       */
        regenerateButton = findViewById(R.id.first_questionnaire_display_name_regenerate);
        regenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomUsername[0] = generateUsername();
                usernameText.setText(randomUsername[0]);
            }
        });

        backArrow = findViewById(R.id.first_questionnaire_display_name_back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SignupActivity.this)
                        .setTitle("Cancel Sign Up")
                        .setMessage("Are you sure you want to cancel the Sign Up process?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        submitButton = findViewById(R.id.first_questionnaire_display_name_continue);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.child("Username").setValue(randomUsername[0]);
                Intent i = new Intent(SignupActivity.this, FirstQuestionnaireNameAgeGenderActivity.class);
                startActivity(i);
            }
        });
    }
    public String generateUsername()
    {
        String username = colors[(int)(Math.random() * 45)] + " " + animals[(int)(Math.random() * 65)];
        return username;
    }
}