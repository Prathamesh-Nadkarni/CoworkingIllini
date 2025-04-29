package com.example.coworkingillini;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coworkingillini.ui.home.HomeFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    public String username;
    int page = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signin, signup;
        signin = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", "pyn2");
        editor.apply();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("469398402018-dpnmda49bi58kbr4hlqclcl16rib2ses.apps.googleusercontent.com")
                .requestEmail()
                .setHostedDomain("illinois.edu")
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signIn();
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
                page = 0;
            }
        });



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signIn();
                Intent i = new Intent(MainActivity.this, Survey.class);
                startActivity(i);
                page = 1;
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                handleSignInResult(task);
            }
            catch (Exception e)
            {
                Toast.makeText(MainActivity.this, "\"Error message: \" + e.getMessage()", Toast.LENGTH_SHORT).show();
                Log.e("GoogleSignIn", "Error message: " + e.getMessage());
            }

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

            Log.d("GoogleSignIn", "signInResult:success");

        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Log.w("GoogleSignIn", "Error message: " + e.getMessage());
            switch (e.getStatusCode()) {
                case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                    Log.d("GoogleSignIn", "Sign-in cancelled");
                    break;
                case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                    Log.d("GoogleSignIn", "Sign-in failed");
                    break;
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String userId = email.split("@")[0];

                            DatabaseReference myRef = database.getReference("users").child(userId);

                            if(page == 1)
                            {
                                Intent i = new Intent(MainActivity.this, Survey.class);
                                startActivity(i);
                            }
                            else if(page == 0) {
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(MainActivity.this, Survey.class);
                                            startActivity(i);
                                        } else {
                                            myRef.setValue(userId);
                                            Intent i = new Intent(MainActivity.this, SignupActivity.class);
                                            startActivity(i);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("FirebaseCheck", "Error checking user ID: " + error.getMessage());
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(MainActivity.this, Survey.class);
            startActivity(i);
        }
    }
}