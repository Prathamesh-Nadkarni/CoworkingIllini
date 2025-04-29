package com.example.coworkingillini;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coworkingillini.ui.dashboard.DashboardFragment;
import com.example.coworkingillini.ui.home.HomeFragment;
import com.example.coworkingillini.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class post_quiz extends AppCompatActivity {
Button chat;

    BottomNavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_quiz);


        nav_view = findViewById(R.id.nav_view);

        nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_people) {
                    startActivity(new Intent(getApplicationContext(), HomeFragment.class));
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(getApplicationContext(), DashboardFragment.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(getApplicationContext(), NotificationsFragment.class));
                    return true;
                }
                return false;
            }
        });

        chat = findViewById(R.id.chat);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(i);
            }
        });

    }
}