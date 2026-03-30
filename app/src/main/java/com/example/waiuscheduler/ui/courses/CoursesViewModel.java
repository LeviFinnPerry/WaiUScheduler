package com.example.waiuscheduler.ui.courses;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.waiuscheduler.parsing.DataRepository;

import okhttp3.HttpUrl;

public class CoursesViewModel extends AndroidViewModel {

    private final DataRepository repository;
    private final MutableLiveData<String> status;

    public CoursesViewModel(@NonNull Application application) {
        super(application);
        this.repository = new DataRepository(application);
        this.status = new MutableLiveData<>();
    }

    public LiveData<String> getStatus() {
        return status;
    }

    /// Function to retrieve text from user and start course outline pipeline
    public void processCourseOutline(String courseCode, String occCode, String loc) {
        // Form the course code into the URL
        String urlFormat =
                "https://uow-func-net-currmngmt-offmngmt-aue-prod.azurewebsites.net/api/outline/view/" + courseCode + "-" + occCode + "%20%28" + loc + "%29";
        HttpUrl url = HttpUrl.parse(urlFormat);

        repository.startCourseOutlinePipeline(url, status::postValue);

    }

}