package com.example.guest.habittracker.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
                Map<Integer, Integer> motivations = new HashMap<>();
                Integer motivation = 0;
                long frequency = dataSnapshot.getChildrenCount();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    motivation = snapshot.child("motivationLevel").getValue(Integer.class);
//                    if(motivations.containsKey(motivation)){
//                        motivations.put(motivation, motivations.get(motivation)+1);
//                    } else {
//                        motivations.put(motivation, 1);
//                    }
                }
                int dateColor;
                if(frequency < 3){
                    Log.d(TAG, "onChildAdded: low frequency");
                    dateColor = lightColors[motivation-1];
                } else if (frequency < 6){
                    Log.d(TAG, "onChildAdded: mid frequency");
                    dateColor = baseColors[motivation-1];
                } else {
                    Log.d(TAG, "onChildAdded: high frequency");
                    dateColor = darkColors[motivation-1];
                }
                ColorDrawable blueDrawable = new ColorDrawable(Color.parseColor("#0000FF"));
                ColorDrawable dateDrawable;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                   dateDrawable = new ColorDrawable(getResources().getColor(dateColor, null));
                } else {
                   dateDrawable =  new ColorDrawable(getResources().getColor(dateColor));
                }
                mCaldroidFragment.setBackgroundDrawableForDate(dateDrawable, date);
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
