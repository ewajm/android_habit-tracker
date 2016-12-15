package com.example.guest.habittracker.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.guest.habittracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarDetailFragment extends Fragment {
    @Bind(R.id.dateHeadingView) TextView mDateHeadingView;
    @Bind(R.id.activityListView) ListView mActivityListView;
    String mDate;


    public CalendarDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = getArguments().getString("date");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_detail, container, false);
        ButterKnife.bind(this, view);
        mDateHeadingView.setText(mDate);
        return view;
    }

}
