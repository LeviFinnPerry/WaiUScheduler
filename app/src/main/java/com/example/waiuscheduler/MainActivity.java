package com.example.waiuscheduler;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.waiuscheduler.databinding.ActivityMainBinding;

import java.io.IOException;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    // Classes for paper outlines
    private CourseOutlineScraper courseOutlineScraper;
    private DatabaseController databaseController;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise the app database
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        // Get the database controller
        databaseController = new DatabaseController(appDatabase);

        // Get the course outline scraper


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_calendar, R.id.navigation_dashboard, R.id.navigation_courses, R.id.navigation_timer)
                .build();
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    // Method for getting and adding the paper outline
    public void storePaperOutline() throws IOException {
        // Get the course code from the user eg. COMPX576
        String courseCode = "COMPX576";

        // Form the course code into the URL
        String urlFormat = "https://uow-func-net-currmngmt-offmngmt-aue-prod.azurewebsites.net/api/outline/view/" + courseCode +"26A%20%28HAM%29";
        HttpUrl url = HttpUrl.parse(urlFormat);

        // Handle paper outline
        courseOutlineScraper.getCourseOutline(url);
    }

}