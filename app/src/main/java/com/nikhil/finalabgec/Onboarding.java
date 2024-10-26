package com.nikhil.finalabgec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nikhil.finalabgec.Adapter.IntroViewPagerAdapter;
import com.nikhil.finalabgec.Model.IntroScreenItem;

import java.util.ArrayList;
import java.util.List;

public class Onboarding extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    LinearLayout btnNext;
    int position = 0;
    FirebaseAuth auth;
    FirebaseUser user;
    LinearLayout btnGetStarted;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // when this activity is about to be launch we need to check if its opened before or not
        setContentView(R.layout.activity_onboarding);
        Window window = Onboarding.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Onboarding.this, R.color.white));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // ini views
        btnNext = findViewById(R.id.next);
        btnGetStarted = findViewById(R.id.getstarted);
        tabIndicator = findViewById(R.id.tab_indicator);
        //btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);
        // fill list screen
        final List<IntroScreenItem> mList = new ArrayList<>();
        mList.add(new IntroScreenItem("Get to know our college", "About our teachers, students, clubs and activities.", R.drawable.ic_onboardlist));
        mList.add(new IntroScreenItem("Find Like-Minded People", "Connect with people with similar interests and hobbies.", R.drawable.ic_onboardjob));
        mList.add(new IntroScreenItem("Get Inspired", "Take inspiration as well as share your own experiences", R.drawable.ic_onboardlist));
        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);
        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);
        // next button click Listner


        btnNext.setOnClickListener(v -> {
            position = screenPager.getCurrentItem();
            if (position < mList.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
            if (position == mList.size() - 1) { // when we rech to the last screen
                // TODO : show the GETSTARTED Button and hide the indicator and the next button
                loadLastScreen();
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle unselected state if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle reselection if needed
            }
        });



        // Get Started button click listener
        btnGetStarted.setOnClickListener(v -> {
            //open main activity
            Intent mainActivity = new Intent(getApplicationContext(), Login.class);
            startActivity(mainActivity);
            finish();
        });
        // skip button click listener
        tvSkip.setOnClickListener(v -> screenPager.setCurrentItem(mList.size()));

    }


    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        //tabIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        boolean is_verified = getSharedPreferences("Verification", MODE_PRIVATE)
                .getBoolean("isVerified", false);

        if (user != null) {
            if(is_verified){
                startActivity(new Intent(Onboarding.this, MainActivity.class));
                finish();
            }
        }
        else{
            boolean is_first_time = getSharedPreferences("Verification", MODE_PRIVATE)
                    .getBoolean("isFirstTime", true);
            if(!is_first_time){
                startActivity(new Intent(Onboarding.this, Login.class));
                finish();
            }
        }

    }
}