package com.example.waiuscheduler.ui.courses;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waiuscheduler.http.CourseOutlineScraper;
import com.example.waiuscheduler.MainActivity;
import com.example.waiuscheduler.R;
import com.example.waiuscheduler.databinding.FragmentCoursesBinding;
import com.example.waiuscheduler.http.OnDocumentReady;

import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.HttpUrl;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;

    // outline scraper
    private CourseOutlineScraper courseOutlineScraper;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        CoursesViewModel coursesViewModel =
                new ViewModelProvider(this).get(CoursesViewModel.class);

        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCourses;
        coursesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialise button for getting paper outline

        Button parseButton = root.findViewById(R.id.button);
        parseButton.setOnClickListener(v -> {
            try {
                storePaperOutline();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            courseOutlineScraper = ((MainActivity) context).getCourseOutlineScraper();
        }
    }


    // Method for getting and adding the paper outline
    public void storePaperOutline() throws IOException {
        // Get the course code from the user eg. COMPX576
        String courseCode = "COMPX576";

        // Form the course code into the URL
        String urlFormat =
                "https://uow-func-net-currmngmt-offmngmt-aue-prod.azurewebsites.net/api/outline/view/" + courseCode + "-26A%20%28HAM%29";
        HttpUrl url = HttpUrl.parse(urlFormat);

        // Handle paper outline
        courseOutlineScraper.getCourseOutline(url);
    }

}