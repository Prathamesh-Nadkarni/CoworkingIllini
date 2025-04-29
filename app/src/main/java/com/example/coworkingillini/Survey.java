package com.example.coworkingillini;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.coworkingillini.ui.dashboard.DashboardFragment;
import com.example.coworkingillini.ui.home.HomeFragment;
import com.example.coworkingillini.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.coworkingillini.databinding.ActivitySurveyBinding;

public class Survey extends AppCompatActivity {

    private ActivitySurveyBinding binding;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int EXIT_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySurveyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.INVISIBLE);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_people, R.id.nav_chat, R.id.nav_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_survey);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity(); // This will close all activities and exit the app
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, EXIT_DELAY);
    }


}