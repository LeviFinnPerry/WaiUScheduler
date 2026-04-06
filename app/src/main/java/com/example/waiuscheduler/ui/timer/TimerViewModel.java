package com.example.waiuscheduler.ui.timer;

import android.app.Application;
import android.os.Looper;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.tables.PaperEntity;
import com.example.waiuscheduler.database.tables.StudySessionEntity;
import com.example.waiuscheduler.parsing.DataRepository;

import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TimerViewModel extends ViewModel {

    // Private variables
    private int seconds = 0;
    private boolean running = false;
    private final DatabaseController dbController;
    private final MutableLiveData<String> timeDisplay = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPaperSelected = new MutableLiveData<>();
    private final LiveData<List<PaperEntity>> allPapers;
    private long startTimeMillis;

    // Handler of timer scheduling
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Interface to run the timer
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                seconds++;
                updateTimeDisplay();
            }
            handler.postDelayed(this, 1000);
        }
    };

    /// Timer View Model Constructor
    /// @param application Application context
    public TimerViewModel(@NonNull Application application) {
        // Start the timer
        handler.post(runnable);

        // Initialise repository
        DataRepository repository = new DataRepository(application);
        this.dbController = repository.getDbController();
        allPapers = dbController.getAllPapers();
    }

    /// Get the time display from the UI
    /// @return Live Data string of the time
    public LiveData<String> getTimeDisplay() {
        return timeDisplay;
    }

    /// Get whether the paper is selected by the user
    /// @return Live Data boolean
    public LiveData<Boolean> getPaperSelected() {
        return isPaperSelected;
    }

    /// Set whether the paper is selected by the user
    /// @param selected Boolean for paper selection
    public void setPaperSelected(boolean selected) {
        isPaperSelected.setValue(selected);
    }

    /// Retrieves all papers in current papers
    /// @return Live Data list of paper entities
    public LiveData<List<PaperEntity>> getAllPapers() { return allPapers; }

    /// Starts the timer by logging the start time
    public void start() {
        if (isPaperSelected.getValue() == Boolean.TRUE) {   // If there is a paper selected
            if (!running && seconds == 0) {                 // If timer is not already running
                startTimeMillis = System.currentTimeMillis();
            }
            running = true;     // Enable timer is running
        }
    }

    /// Stops the timer
    public void stop() { running = false; }

    /// Resets the timer
    public void reset() {
        running = false;
        seconds = 0;
        updateTimeDisplay();
    }

    /// Save the study session to the database
    /// @param notes Notes from user input
    /// @param paperId The foreign key for the paper id
    public void saveStudySession(String notes, String paperId) {
        if (seconds <= 0) return;   // If there is no time

        // Get the information from the study session
        long endTimeMillis = System.currentTimeMillis();
        double duration = (double) seconds / 3600.0;
        Date startTime = new Date(startTimeMillis);
        Date endTime = new Date(endTimeMillis);

        // Create a study session
        StudySessionEntity currSession = new StudySessionEntity(startTime, endTime, duration, notes, paperId);

        dbController.saveStudySession(currSession);
        reset();    // Reset UI after saving
    }

    /// Updates the UI time display
    private void updateTimeDisplay() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
        timeDisplay.setValue(time);
    }

    /// Clears the view model and callbacks
    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(runnable);
    }

}
