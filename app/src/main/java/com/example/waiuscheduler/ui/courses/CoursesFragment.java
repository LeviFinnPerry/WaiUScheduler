package com.example.waiuscheduler.ui.courses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.databinding.FragmentCoursesBinding;

import java.util.ArrayList;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;

    // View Model
    private CoursesViewModel coursesViewModel;

    // Paper adapter
    private PaperAdapter adapter;

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

        // Observe the pipeline status
        coursesViewModel.getStatus().observe(getViewLifecycleOwner(), statusMessage -> {
            if (statusMessage != null) {
                Toast.makeText(getContext(), statusMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Trigger the pipeline via the button
        binding.buttonSearchPaper.setOnClickListener(v -> {
            try {

                // Get the information for the course outline
                String paperCode = String.valueOf(binding.editTextPaperCode.getText()).trim();
                String occCode = String.valueOf(binding.editTextOccCode.getText()).trim();

                // Find the selected location
                int locationId = binding.radioGroupLocation.getCheckedRadioButtonId();
                String location = getLocation(locationId);

                coursesViewModel.processCourseOutline(paperCode, occCode, location);
            } catch (Exception e) {
                Log.e("User Add Paper", "Error in getting paper information");
            }


            // RecyclerView
            RecyclerView recyclerView = view.findViewById(R.id.paperRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapter = new PaperAdapter(paper -> {
                coursesViewModel.deletePaper(paper);
            });

            recyclerView.setAdapter(adapter);

            // Observe the data from the view model
            coursesViewModel.getAllPapers().observe(
                    getViewLifecycleOwner(), papers -> {
                        if (papers != null) {
                            adapter.submitPapers((ArrayList<PaperTable>) papers);
                        }
                    }
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /// Function to determine which radiobutton is selected
    private String getLocation(int locationId) {
        if (locationId == binding.radioLocationHam.getId()) {
            return "HAM";
        } else if (locationId == binding.radioLocationTga.getId()) {
            return "TGA";
        } else if (locationId == binding.radioLocationNet.getId()) {
            return "NET";
        }
        return null;
    }

}