package com.example.waiuscheduler.ui.courses;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.waiuscheduler.database.DatabaseController;
import com.example.waiuscheduler.database.tables.PaperEntity;
import com.example.waiuscheduler.database.tables.StaffEntity;
import com.example.waiuscheduler.parsing.DataRepository;

import java.util.List;

import okhttp3.HttpUrl;

public class CoursesViewModel extends AndroidViewModel {

    private final DataRepository repository;
    private final MutableLiveData<String> status;
    private final DatabaseController dbController;
    private final LiveData<List<PaperEntity>> papers;


    /// Constructor for the view model
    /// @param application Application for the view
    public CoursesViewModel(@NonNull Application application) {
        super(application);
        this.repository = new DataRepository(application);
        this.status = new MutableLiveData<>();
        this.dbController = DataRepository.getDbController();
        this.papers = dbController.getAllPapers();
    }

    /// Get status of the pipeline
    /// @return Status message
    public LiveData<String> getStatus() {
        return status;
    }

    /// Function to retrieve text from user and start course outline pipeline
    /// @param courseCode paper code
    /// @param occCode paper occurrence code
    /// @param loc paper occurrence location
    public void processCourseOutline(String courseCode, String occCode, String loc) {
        // Form the course code into the URL
        String urlFormat =
                "https://uow-func-net-currmngmt-offmngmt-aue-prod.azurewebsites.net/api/outline/view/" + courseCode + "-" + occCode + "%20%28" + loc + "%29";
        HttpUrl url = HttpUrl.parse(urlFormat);

        repository.startCourseOutlinePipeline(url, status::postValue);

    }

    /// Get all papers in view
    /// @return All displayed paper entities
    public LiveData<List<PaperEntity>> getAllPapers() {
        return papers;
    }

    /// Deletes paper
    /// @param paper Paper to delete
    public void deletePaper(PaperEntity paper) {
        dbController.deletePaper(paper);
    }

    /// Get all staff members
    /// @return All displayed staff entities
    public LiveData<List<StaffEntity>> getAllStaff() {
        return dbController.getAllStaff();
    }

}