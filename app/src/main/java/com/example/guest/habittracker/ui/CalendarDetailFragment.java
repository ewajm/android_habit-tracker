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
import com.example.guest.habittracker.models.Activity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarDetailFragment extends Fragment {
    @Bind(R.id.dateHeadingView) TextView mDateHeadingView;
    @Bind(R.id.activityListView) ListView mActivityListView;
    String mDate;
    String mUserId;
    FirebaseListAdapter<Activity> mAdapter;


    public CalendarDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = getArguments().getString("date");
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_detail, container, false);
        ButterKnife.bind(this, view);
        mDateHeadingView.setText(mDate);
        makeActivityListView();
        mActivityListView.setEmptyView(view.findViewById(android.R.id.empty));
        mActivityListView.setAdapter(mAdapter);
        return view;
    }

    private void makeActivityListView() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mUserId).child("dates").child(mDate);
        mAdapter = new FirebaseListAdapter<Activity>(getActivity(), Activity.class, android.R.layout.two_line_list_item, ref) {
            @Override
            protected void populateView(View v, Activity model, int position) {
                String[] motivations = {"meh", "fine", "do things", "PARKOUR"};
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getName());
                ((TextView)v.findViewById(android.R.id.text2)).setText(motivations[model.getMotivationLevel()-1]);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.cleanup();
        }
    }

}
