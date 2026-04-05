package com.example.waiuscheduler.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waiuscheduler.databinding.FragmentDashboardBinding;

public class TimerFragment extends Fragment {

    private TimerViewModel timerViewModel;
    private FragmentDashboardBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find each element of the UI for the timer
        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        timerViewModel.getTimeDisplay().observe(getViewLifecycleOwner(), timeString -> {
            //timeview.setText(timeString);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}