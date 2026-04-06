package com.example.waiuscheduler.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waiuscheduler.database.tables.PaperEntity;
import com.example.waiuscheduler.databinding.FragmentTimerBinding;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment {

    // Private variables
    private TimerViewModel timerViewModel;
    private FragmentTimerBinding binding;

    /// Initialises view elements for the fragment
    /// @param inflater XML layout with corresponding view objects
    /// @param container Holds view families
    /// @param savedInstanceState Bundle saved from previous state
    /// @return binding root as a View
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        // Find each element of the UI for the timer
        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        binding = FragmentTimerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /// Handling of timer once view is created
    /// @param view User interface components
    /// @param savedInstanceState Bundle saved from previous state
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe papers from the database to fill the spinner
        timerViewModel.getAllPapers().observe(getViewLifecycleOwner(), papers -> {
            List<String> paperNames = new ArrayList<>();
            paperNames.add("Select a paper...");
            for (PaperEntity paper : papers) {
                paperNames.add(paper.getPaperId());   // Add selected papers
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, paperNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.paperSpinner.setAdapter(adapter);

        });

        // Spinner selection
        binding.paperSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timerViewModel.setPaperSelected(position > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Update Timer and Button States
        timerViewModel.getTimeDisplay().observe(getViewLifecycleOwner(), time -> binding.timeView.setText(time));

        // Click Listeners
        binding.startButton.setOnClickListener(v -> {
            if (binding.paperSpinner.getSelectedItemPosition() != 0) {
                timerViewModel.start();
            }
        });
        binding.resetButton.setOnClickListener(v -> timerViewModel.reset());
        binding.stopButton.setOnClickListener(v -> { timerViewModel.stop();
            // Get information from user
            String notes = binding.editTextSessionNotes.getText().toString();
            String paperId = binding.paperSpinner.getSelectedItem().toString();
            timerViewModel.storeStudySession(notes, paperId);
            binding.editTextSessionNotes.setText("");
        });

    }

    /// Destroys view when no longer needed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}