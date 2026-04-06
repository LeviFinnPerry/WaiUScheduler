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
    private final DataRepository repository;
    private final DatabaseController dbController;
    private final MutableLiveData<String> timeDisplay = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPaperSelected = new MutableLiveData<>();
    private final LiveData<List<PaperEntity>> allPapers;
    private long startTimeMillis;
    private String selectedPaperId;

    private final Handler handler = new Handler(Looper.getMainLooper());

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

    public TimerViewModel(@NonNull Application application) {
        // Start the timer
        handler.post(runnable);

        // Initialise repository
        this.repository = new DataRepository(application);
        this.dbController = repository.getDbController();
        allPapers = dbController.getAllPapers();
    }

    public LiveData<String> getTimeDisplay() {
        return timeDisplay;
    }

    public LiveData<Boolean> getPaperSelected() {
        return isPaperSelected;
    }

    public void setPaperSelected(boolean selected) {
        isPaperSelected.setValue(selected);
    }

    public LiveData<List<PaperEntity>> getAllPapers() { return allPapers; }

    public void start() {
        if (isPaperSelected.getValue() == Boolean.TRUE) {
            if (!running && seconds == 0) {
                startTimeMillis = System.currentTimeMillis();
            }
            running = true;
        }
    }
    public void stop() { running = false; }
    public void reset() {
        running = false;
        seconds = 0;
        updateTimeDisplay();
    }

    public void saveStudySession(String notes, String paperId) {
        if (seconds <= 0) return;

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

    private void updateTimeDisplay() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
        timeDisplay.setValue(time);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(runnable);
    }

}
