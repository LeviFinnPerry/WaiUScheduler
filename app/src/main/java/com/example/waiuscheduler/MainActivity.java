package com.example.waiuscheduler;

import android.os.Bundle;
import android.util.Log;

import com.example.waiuscheduler.http.CourseOutlineScraper;
import com.example.waiuscheduler.http.OnDocumentReady;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.waiuscheduler.databinding.ActivityMainBinding;

import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    // Classes for paper outlines
    private CourseOutlineScraper courseOutlineScraper;
    private OnDocumentReady documentListener;
    private DatabaseController databaseController;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise the app database
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        // Get the database controller
        databaseController = new DatabaseController(appDatabase);

        // Get the course outline scraper and listener
        courseOutlineScraper = new CourseOutlineScraper();

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

    // To get the database the course outline scraper
    public CourseOutlineScraper getCourseOutlineScraper() {
        return courseOutlineScraper;
    }


}