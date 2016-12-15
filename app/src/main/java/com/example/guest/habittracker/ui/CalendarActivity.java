package com.example.guest.habittracker.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;

import com.example.guest.habittracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roomorama.caldroid.CaldroidFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.start;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = CalendarActivity.class.getSimpleName();
    int[] darkColors = {R.color.mehDark, R.color.fineDark, R.color.dothingsDark, R.color.parkourDark};
    int[] lightColors = {R.color.mehLight, R.color.fineLight, R.color.dothingsLight, R.color.parkourLight};
    int[] baseColors = {R.color.meh, R.color.fine, R.color.dothings, R.color.parkour};
    private CaldroidFragment mCaldroidFragment;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCaldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidCustom);
        mCaldroidFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.cal, mCaldroidFragment);
        t.commit();

        populateDates();
    }

    private void populateDates() {
        DatabaseReference userDates = FirebaseDatabase.getInstance().getReference("users").child(mUserId).child("dates");
        userDates.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String dateString = dataSnapshot.getKey();
                DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                Date date = null;
                try {
                    date = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SparseIntArray motivations = new SparseIntArray();
                Integer motivation = 0;
                long frequency = dataSnapshot.getChildrenCount();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    motivation = snapshot.child("motivationLevel").getValue(Integer.class);
                    int current = motivations.get(motivation-1);
                    motivations.put((motivation-1), current+1);

                }
                int startMotivation = motivations.keyAt(0);
                int endMotivation = motivations.keyAt(motivations.size()-1);
                int dateColor;
                int dateColor2;
                if(frequency < 3){
                    dateColor = lightColors[startMotivation];
                    dateColor2 = lightColors[endMotivation];
                } else if (frequency < 6){
                    dateColor = baseColors[startMotivation];
                    dateColor2= baseColors[endMotivation];
                } else {
                    dateColor = darkColors[startMotivation];
                    dateColor2 = darkColors[endMotivation];
                }
                ColorDrawable dateDrawable;
                GradientDrawable dateGradientDrawable;
                if(dateColor == dateColor2) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        dateDrawable = new ColorDrawable(getResources().getColor(dateColor, null));
                    } else {
                        dateDrawable = new ColorDrawable(getResources().getColor(dateColor));
                    }
                    mCaldroidFragment.setBackgroundDrawableForDate(dateDrawable, date);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        int[] colors = {getResources().getColor(dateColor, null), getResources().getColor(dateColor2, null)};
                        dateGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                    } else {
                        int[] colors = {getResources().getColor(dateColor), getResources().getColor(dateColor2)};
                        dateGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                    }
                    mCaldroidFragment.setBackgroundDrawableForDate(dateGradientDrawable, date);
                }
                mCaldroidFragment.refreshView();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
