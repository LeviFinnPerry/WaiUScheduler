package com.example.waiuscheduler;

import android.app.Application;

import com.example.waiuscheduler.parsing.DataRepository;

public class WaiUApplication extends Application {
    /// Initialises database repository regardless of which fragment opens first
    @Override
    public void onCreate() {
        super.onCreate();
        new DataRepository(this);
    }
}
