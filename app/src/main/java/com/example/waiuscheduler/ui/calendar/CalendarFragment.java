package com.example.waiuscheduler.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waiuscheduler.databinding.FragmentCalendarBinding;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;


    /// Initialises fragment view
    /// @param inflater Layout inflater
    /// @param container View Group of the fragment
    /// @param savedInstanceState Bundle for the instance
    /// @return View for calendar
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /// Destroys the view
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}