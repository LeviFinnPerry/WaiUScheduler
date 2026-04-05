package com.example.waiuscheduler.ui.timer;

import android.os.Looper;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;


public class TimerViewModel extends ViewModel {

    // Private variables
    private int seconds = 0;
    private boolean running = false;
    private final MutableLiveData<String> timeDisplay = new MutableLiveData<>();

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

    public TimerViewModel() {
        handler.post(runnable);
    }

    public LiveData<String> getTimeDisplay() {
        return timeDisplay;
    }

    public void start() { running = true; }
    public void stop() { running = false; }
    public void reset() {
        running = false;
        seconds = 0;
        updateTimeDisplay();
    }

    private void updateTimeDisplay() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
        timeDisplay.setValue(time);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(runnable);
    }
}
