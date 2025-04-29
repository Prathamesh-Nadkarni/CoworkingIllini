package com.example.coworkingillini;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    GlobalVariables globalVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalVariables = new GlobalVariables();
        setContentView(R.layout.activity_signin);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Intent i = new Intent(getApplicationContext(), Survey.class);
               // startActivity(i);
                if(globalVariables.usernames.contains(username.getText().toString()))
                {
                    if(globalVariables.password.equals(password.getText().toString()))
                    {
                        Intent i = new Intent(getApplicationContext(), Survey.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Wrong Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}