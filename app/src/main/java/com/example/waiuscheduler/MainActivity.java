package com.example.waiuscheduler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.waiuscheduler.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    /// On Create method to initialise app
    /// @param savedInstanceState Bundle of instance
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = getContent();
        AppBarConfiguration appBarConfiguration = getAppBar();
        NavHostFragment navHostFragment = getNavHost();
        if (navHostFragment != null) {
            setupWithNavController(navHostFragment, appBarConfiguration, binding);
        }
    }

    /// Sets the content view from the main activity
    /// @return binding of main activity
    private ActivityMainBinding getContent() {
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        return binding;
    }

    /// Gets the navigation bar in app ui
    /// @return bar configuration
    private AppBarConfiguration getAppBar() {
        return new AppBarConfiguration.Builder(
                R.id.navigation_calendar, R.id.navigation_dashboard, R.id.navigation_courses, R.id.navigation_timer)
                .build();
    }

    /// Gets the navigation host fragment
    /// @return nav host fragment
    private NavHostFragment getNavHost() {
        return (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
    }

    /// Sets up the navigation to the controller
    /// @param navHostFragment nav host fragment
    /// @param appBarConfiguration app bar configuration
    /// @param binding main app binding
    private void setupWithNavController(NavHostFragment navHostFragment, AppBarConfiguration appBarConfiguration, ActivityMainBinding binding) {
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}