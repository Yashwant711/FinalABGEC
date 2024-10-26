package com.nikhil.finalabgec;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.finalabgec.Fragment.AddJob;
import com.nikhil.finalabgec.Fragment.AddPost;
import com.nikhil.finalabgec.Fragment.AlumniList;
import com.nikhil.finalabgec.Fragment.ImageFragment;
import com.nikhil.finalabgec.Fragment.JobSection;
import com.nikhil.finalabgec.Fragment.Post;
import com.nikhil.finalabgec.Fragment.Profile;

import java.util.Objects;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import www.sanju.motiontoast.MotionToast;

public class MainActivity extends AppCompatActivity {

    String uid;

    SmoothBottomBar bottomBar;
    Toolbar toolbar;
    NavigationView navView;
    Uri deep_link_uri;
    GoogleSignInClient mGoogleSignInClient;
    DrawerLayout drawer;
    Dialog dialog;
    FirebaseAuth auth;
    FirebaseUser user;
    Boolean closed = false;
    TextView yes,no,text1,text2;
    DatabaseReference user_ref;
    ImageView globe;
    FloatingActionButton add,job,post, admin;
    Animation rotateOpen,rotateClose,fromBottom,toBottom;

    String date = "";
    String name= "";
    String title= "";
    String description= "";
    String image_link= "";
    String like= "";
    String link= "";
    String pushkey= "";
    private int inAppUpdateType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.navView);
        drawer = findViewById(R.id.drawer);
        setStatusBarTransparent();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();

        rotateOpen =  AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);
        user_ref = FirebaseDatabase.getInstance().getReference().child("users");

        text1 = findViewById(R.id.textView19);
        text2 = findViewById(R.id.textView20);
        globe = findViewById(R.id.globe);
        add = findViewById(R.id.add_f);
        job = findViewById(R.id.job);
        post = findViewById(R.id.post);
        admin = findViewById(R.id.admin_panel);

        check_for_admin();
        check_for_student();

        setSupportActionBar(toolbar);

        deep_link_uri = getIntent().getData();

        // Show main fragment in container
       goToFragment(new Post());

        //  set default home fragment and its title
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        //  getSupportFragmentManager().beginTransaction().replace(R.id.container, new Post()).commit();
        navView.setCheckedItem(R.id.nav_home);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_ATOP);
        toggle.syncState();

        globe.setOnClickListener(v -> {
            String url = "https://abgec.in/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        add.setOnClickListener(v->{
            OnAddButtonClick();
        });

        //edit fab button on click
        job.setOnClickListener(v->{
            OnAddButtonClick();
            MainActivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.change_layout, new AddJob(), "list_announcement")
                    .commit();
        });

        //setting fab button on click
        post.setOnClickListener(v->{
            OnAddButtonClick();
            MainActivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.change_layout, new AddPost(), "list_announcement")
                    .commit();
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        boolean connected = networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));

        if (!connected){
            MotionToast.Companion.darkColorToast(MainActivity.this,
                    "No Internet",
                    "Connect with mobile network",
                    MotionToast.TOAST_NO_INTERNET,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(MainActivity.this, R.font.poppins));
        }

//        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//        List<Address> addresses = null;

        /*try {
            addresses = geocoder.getFromLocation(22.057280, 82.170952, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

       /* String address = addresses.get(0).getSubLocality();
        String cityName = addresses.get(0).getLocality();
        String stateName = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        Log.e("cityName",cityName);
        Log.e("stateName",stateName);
        Log.e("countryName",country); */

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    drawer.closeDrawer(GravityCompat.START);
                    navView.getMenu().getItem(0).setCheckable(true);
                } else if (itemId == R.id.list) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, ListOfAlumni.class);
                    navView.getMenu().getItem(2).setCheckable(false);
                    startActivity(intent);
                } else if (itemId == R.id.gallery) {
                    fragment = new ImageFragment();
                    drawer.closeDrawer(GravityCompat.START);
                    navView.getMenu().getItem(1).setCheckable(false);
                    callFragment(fragment);
                }
//                else if (itemId == R.id.privacy) {
//                    fragment = new Privacy();
//                    drawer.closeDrawer(GravityCompat.START);
//                    navView.getMenu().getItem(3).setCheckable(false);
//                    callFragment2(fragment);
//                }
                else if (itemId == R.id.nav_logout) {
                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_logout);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                    yes = dialog.findViewById(R.id.yes);
                    no = dialog.findViewById(R.id.no);

                    yes.setOnClickListener(v -> {
                        dialog.dismiss();
//                        navView.getMenu().getItem(8).setCheckable(false);
                        auth.signOut();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                    });

                    no.setOnClickListener(v -> {
                        dialog.dismiss();
//                        navView.getMenu().getItem(0).setCheckable(false);
                        drawer.closeDrawer(GravityCompat.START);
                    });
                }
                // Remove the commented-out part if it's not needed
                // else if (itemId == ...) {
                //     // Additional cases can be added here
                // }

                return true;
            }

        });

//        if(isStudent) {
//            add.setVisibility(View.GONE);
//        }
//        if(!isAdmin) {
//            admin.setVisibility(View.GONE);
//        }

//        if (getSupportFragmentManager().findFragmentById(R.id.container) != null) {
//            getSupportFragmentManager()
//                    .beginTransaction().
//                    remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.container))).commit();
//        }
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container, new Post())
//                .commit();

        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(0);

        bottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            Fragment selectedFragment;
            String tag;

            switch (i) {
                case 0:
                    selectedFragment = new Post();
                    tag = "post_fragment";
                    break;
                case 1:
                    selectedFragment = new JobSection();
                    tag = "job_section_fragment";
                    break;
                case 2:
                    selectedFragment = new AlumniList();
                    tag = "alumni_list_fragment";
                    break;
                case 3:
                    selectedFragment = new Profile();
                    tag = "profile_fragment";
                    break;
                default:
                    return false;
            }

            // Replace the existing fragment with the selected one
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, selectedFragment, tag)
                    .commit();

            // Set the selected item index (if needed)
            bottomBar.setItemActiveIndex(i);

            return true;
        });

        admin.setOnClickListener( v -> {
            Intent intent = new Intent(MainActivity.this, AdminPanel.class);
            startActivity(intent);
        });

        // Handle the back button press
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
////                FragmentManager fragmentManager = getSupportFragmentManager();
//                finish();
////                // If there are fragments in the back stack, pop the last one
////                if (fragmentManager.getBackStackEntryCount() > 0) {
////                    fragmentManager.popBackStack();
////                } else {
////                    // Otherwise, exit the app
////                    finish();  // or use super.onBackPressed(); if you want to keep default behavior
////                }
//            }
//        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        topic topic=new topic();
//        String val = "";
//        topic.noti("","" , val);
//        Log.e("notification_intent",val);
//        if (val.equals("fromjob")){
//            Toast.makeText(MainActivity.this, "Getting from Job section notification  ", Toast.LENGTH_SHORT).show();
//        }
//        check_for_student();
//    }

//    public static void deleteCache(Context context) {
//        try {
//            File dir = context.getCacheDir();
//            deleteDir(dir);
//        } catch (Exception e) { e.printStackTrace();}
//    }

    private void OnAddButtonClick() {
        setVisibility(closed);
        setAnimation(closed);
        closed = !closed;
    }
    private void setAnimation(boolean closed) {
        if(!closed){
            job.startAnimation(fromBottom);
            post.startAnimation(fromBottom);
            add.startAnimation(rotateOpen);
        }else{
            job.startAnimation(toBottom);
            post.startAnimation(toBottom);
            add.startAnimation(rotateClose);
        }
    }
    // used to set visibility to VISIBLE / INVISIBLE
    private void setVisibility(boolean closed) {
        if(!closed)
        {
            job.setVisibility(View.VISIBLE);
            post.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
            text1.setVisibility(View.VISIBLE);
        }else{
            job.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            text1.setVisibility(View.GONE);
        }
    }

    private void callFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.change_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragment,"mainFrag").commit();
    }

    private void setStatusBarTransparent () {
        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }


    private void check_for_student(){
        try{
            DatabaseReference cutoffRef = FirebaseDatabase.getInstance().getReference("BatchCutoff");
            cutoffRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String year = snapshot.getValue(String.class);
                    if(year == null){
                        year = "9999";
                    }
                    Log.d("BatchCutoff", "Year: " + year);
                    final String cutoffYear = year;
                    user_ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String batchYear = dataSnapshot.child("batch").getValue(String.class);
                            if(batchYear == null){
                                batchYear = "0";
                            }
                            Log.d("Batch", "Year: " + batchYear);
                            setIsStudent(batchYear, cutoffYear);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("FirebaseError", "Error reading data: " + databaseError.getMessage());
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch( Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void check_for_admin() {
        try{
            user_ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean isAdmin = dataSnapshot.child("isAdmin").getValue(Boolean.class);
                    if (isAdmin == null) {
                        isAdmin = false;
                    }
                    if(isAdmin){
                        setIsAdmin();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseError", "Error reading data: " + databaseError.getMessage());
                }
            });
        }
        catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", e.toString());
        }
    }

    private void setIsStudent(String batchYear, String cutoffYear) {
        int y1 = Integer.parseInt(batchYear);
        int y2 = Integer.parseInt(cutoffYear);
        if(y1 <= y2){
            add.setVisibility(View.VISIBLE);
        }
    }

    private void setIsAdmin() {
        admin.setVisibility(View.VISIBLE);
    }

}