package com.nikhil.finalabgec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.material.snackbar.Snackbar;
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
import com.nikhil.finalabgec.Fragment.Privacy;
import com.nikhil.finalabgec.Fragment.Profile;
import com.nikhil.finalabgec.Fragment.ViewPost;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import www.sanju.motiontoast.MotionToast;

public class MainActivity extends AppCompatActivity {

    SmoothBottomBar bottomBar;
    Toolbar toolbar;
    NavigationView navView;
    Uri deep_link_uri;
    OnBackPressedListener onBackpressedListener;
    GoogleSignInClient mGoogleSignInClient;
    DrawerLayout drawer;
    Dialog dialog;
    FirebaseAuth auth;
    FirebaseUser user;
    Boolean closed = false;
    TextView yes,no,text1,text2;
    DatabaseReference user_ref;
    ImageView globe;
    FloatingActionButton add,job,post;
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
       /* user =
       //SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);*/

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

        setSupportActionBar(toolbar);

        deep_link_uri = getIntent().getData();//deep link value

        // Show main fragment in container
       goToFragment(new Post());

        //set default home fragment and its title
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, new Post()).commit();
        navView.setCheckedItem(R.id.nav_home);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.main_blue));
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

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        if (!connected){
            MotionToast.Companion.darkColorToast(MainActivity.this,
                    "No Internet",
                    "Connect with mobile network",
                    MotionToast.TOAST_NO_INTERNET,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(MainActivity.this, R.font.poppins));
        }

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
     /*   try {
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
        Log.e("countryName",country);*/

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
                    callFragment2(fragment);
                } else if (itemId == R.id.privacy) {
                    fragment = new Privacy();
                    drawer.closeDrawer(GravityCompat.START);
                    navView.getMenu().getItem(3).setCheckable(false);
                    callFragment2(fragment);
                } else if (itemId == R.id.nav_logout) {
                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_logout);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                    yes = dialog.findViewById(R.id.yes);
                    no = dialog.findViewById(R.id.no);

                    yes.setOnClickListener(v -> {
                        dialog.dismiss();
                        navView.getMenu().getItem(8).setCheckable(false);
                        //deleteCache(MainActivity.this);
                        auth.signOut();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                    });

                    no.setOnClickListener(v -> {
                        dialog.dismiss();
                        navView.getMenu().getItem(8).setCheckable(false);
                        drawer.closeDrawer(GravityCompat.START);
                    });
                } // Remove the commented-out part if it's not needed
                // else if (itemId == ...) {
                //     // Additional cases can be added here
                // }

                return true;
            }

        });

        if (check_for_student()) {
            add.setVisibility(View.GONE);
        }

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


            if (i == 0) {
                bottomBar.setItemActiveIndex(0);
                if (MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                MainActivity.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new Post())
                        .commit();
            }
            else if (i == 1) {
                bottomBar.setItemActiveIndex(1);
                /*Intent intent = new Intent(Home.this , Home.class);
                startActivity(intent);*/

                if (getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new JobSection())
                        .commit();
            } else if (i == 2) {
                bottomBar.setItemActiveIndex(2);
                if (MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                MainActivity.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AlumniList(), "list_announcement")
                        .commit();
            } else if (i == 3) {
                if (check_for_student()) {
                    MotionToast.Companion.darkColorToast(MainActivity.this,
                            "Access Denied ☹️",
                            "You do not have authority to access your profile",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(MainActivity.this, R.font.poppins));
                }
                else {
                    bottomBar.setItemActiveIndex(3);
                    if (MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                        MainActivity.this.getSupportFragmentManager()
                                .beginTransaction().
                                remove(Objects.requireNonNull(MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                    }
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new Profile(), "list_announcement")
                            .commit();
                }
            }
            return false;
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
       /* topic topic=new topic();
        String val = "";
        topic.noti("","" , val);
        Log.e("notification_intent",val);
        if (val.equals("fromjob")){
            Toast.makeText(MainActivity.this, "Getting from Job section notification  ", Toast.LENGTH_SHORT).show();
        }*/
        check_for_student();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

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


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void callFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void callFragment2(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.change_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void goToFragment(Fragment fragment) {
        if(deep_link_uri!=null){
            Toast.makeText(this, "jhdsahjhsja", Toast.LENGTH_SHORT).show();
            if (deep_link_uri.toString().equals("https://abgec.android")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, fragment,"mainFrag").commit();
            }
            else if(deep_link_uri.toString().equals("http://abgec.android")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, fragment,"mainFrag").commit();
            }
            else if(deep_link_uri.toString().equals("abgec.android")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, fragment,"mainFrag").commit();
            }
            else{
                // if the uri is not null then we are getting the
                // path segments and storing it in list.
                List<String> parameters = deep_link_uri.getPathSegments();
                // after that we are extracting string from that parameters.
                if(parameters!=null) {
                    if(parameters.size()>1) {
                        String check_profile=parameters.get(parameters.size()-2);
                        if(check_profile.trim().equals("profile")){

                            String name=parameters.get(parameters.size()-1);
                            String uid=parameters.get(parameters.size()-3);
                            //sending values to home_content frag for opening profile...
                            Bundle args = new Bundle();
                            args.putString("deep_link_name", name);
                            args.putString("deep_link_uid_value_profile", uid);
                            fragment.setArguments(args);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.container, fragment, "mainFrag").commit();

                        }
                        else {
                            String param = parameters.get(parameters.size() - 1);
                            String uid = parameters.get(parameters.size() - 2);
                            // on below line we are setting
                            // that string to our text view
                            // which we got as params.
//                            Log.e("deep_link_value", param + "");
//                            Log.e("deep_link_value_uid", uid + "");
//                            Bundle args = new Bundle();
//                            args.putString("deep_link_value", param);
//                            args.putString("deep_link_uid_value", uid);
//                            fragment.setArguments(args);
                            openPost("-NpfIjR8PErVkhS9ljnc", "QexL0OUC3CeipMupyrN485JtByB3");
                            //openPost(param, uid);
                        }
                    }
                    else{
                        openPost("-NpfIjR8PErVkhS9ljnc", "QexL0OUC3CeipMupyrN485JtByB3");
//                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                        transaction.add(R.id.container, fragment,"mainFrag").commit();
                    }
                }
            }
        }
        else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragment,"mainFrag").commit();
        }
    }

    private void openPost(String push,String uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts").child(push);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               date = snapshot.child("date").getValue(String.class);
                 name = snapshot.child("name").getValue(String.class);
                title = snapshot.child("title").getValue(String.class);
                 description = snapshot.child("description").getValue(String.class);
                 image_link = snapshot.child("image_link").getValue(String.class);
                 like = snapshot.child("like").getValue(String.class);
                 link = snapshot.child("link").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ViewPost profile = new ViewPost();
        Bundle args = new Bundle();
        args.putString("sending_user_from_home","addstack");
        args.putString("uid_sending_post",uid);
        args.putString("name",name);
        args.putString("title",title);
        args.putString("description",description);
        args.putString("pushkey",push);
        args.putString("image",image_link);
        args.putString("date",date);
        args.putString("like", like);
        profile.setArguments(args);

        MainActivity.this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,  profile)
                .commit();
    }

    private void setStatusBarTransparent () {
        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    //on backpress
//    @Override
//    public void onBackPressed() {
//        if (onBackpressedListener != null) {
//            getSupportActionBar().setTitle("Home");
//            navView.setCheckedItem(R.id.nav_home);
//            onBackpressedListener.doBack();
//            drawer.closeDrawer(GravityCompat.START);
//        } else if (onBackpressedListener == null) {
//            finish();
//            super.onBackPressed();
//        }
//    }

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackpressedListener = onBackPressedListener;
    }

    @Override
    protected void onDestroy() {
        onBackpressedListener = null;
        super.onDestroy();
    }

    private boolean check_for_student(){
        SharedPreferences pref = getSharedPreferences("our_user?", MODE_PRIVATE);
        return pref.getBoolean("student", true);
    }

}