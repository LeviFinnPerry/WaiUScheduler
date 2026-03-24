package com.example.waiuscheduler.ui.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waiuscheduler.databinding.FragmentCoursesBinding;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;

    // View Model
    private CoursesViewModel coursesViewModel;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        binding = FragmentCoursesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe text changes

        // Observe the pipeline status
        coursesViewModel.getStatus().observe(getViewLifecycleOwner(), statusMessage -> {
            if (statusMessage != null) {
                Toast.makeText(getContext(), statusMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Trigger the pipeline via the button
        binding.button.setOnClickListener(v -> {
            coursesViewModel.processCourseOutline("TESTING");
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}