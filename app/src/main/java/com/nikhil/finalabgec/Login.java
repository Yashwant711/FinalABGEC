package com.nikhil.finalabgec;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import www.sanju.motiontoast.MotionToast;

public class Login extends AppCompatActivity {

    TextView loginLogin;
    TextView registerButton;
    TextView forgotPassword;
    EditText email_id, password;
    private FrameLayout overlay_layout;
    Animation animFadein, fade_out;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    String deviceToken;
    int downspeed;
    int upspeed;
    ImageView back;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSharedPreferences("Verification", MODE_PRIVATE).edit()
                .putBoolean("isFirstTime", false)
                .apply();

        mAuth = FirebaseAuth.getInstance();
        loginLogin = findViewById(R.id.loginLogin);
        registerButton = findViewById(R.id.register_button);
        forgotPassword = findViewById(R.id.forgot_password);
        email_id = findViewById(R.id.email_id);
        password = findViewById(R.id.password);
        back = findViewById(R.id.back_img);
        overlay_layout = findViewById(R.id.overlay_layout);
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        setStatusBarTransparent();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        boolean connected = networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));


        if (!connected){
            MotionToast.Companion.darkColorToast(Login.this,
                    "No Internet",
                    "Connect with mobile network",
                    MotionToast.TOAST_NO_INTERNET,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(Login.this, R.font.poppins));
        }

        getting_device_token();

        loginLogin.setOnClickListener(
                view -> {
                    String email = email_id.getText().toString().trim();
                    String pass = password.getText().toString();
                    if(TextUtils.isEmpty(email)){
                        email_id.setError("Please enter your email ID");
                    }
                    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        email_id.setError("Please enter a valid email address");
                    }
                    else if(TextUtils.isEmpty(pass)){
                        password.setError("Please enter your password");
                    }
                    else{
                        overlay_layout.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this, task -> {
                                    overlay_layout.setVisibility(View.GONE);

                                    if (task.isSuccessful()){
                                        user = mAuth.getCurrentUser();
                                        try{
                                            String uid = user.getUid();
                                            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        getSharedPreferences("Verification", MODE_PRIVATE).edit()
                                                                .putBoolean("isVerified", true)
                                                                .apply();
                                                        startActivity(new Intent(Login.this, MainActivity.class));
                                                        finish();
                                                    }
                                                    else{
                                                        MotionToast.Companion.darkColorToast(Login.this,
                                                                "Account Not Verified",
                                                                "Your account has not been verified yet, Please try again later.",
                                                                MotionToast.TOAST_ERROR,
                                                                MotionToast.GRAVITY_BOTTOM,
                                                                MotionToast.LONG_DURATION,
                                                                ResourcesCompat.getFont(Login.this, R.font.poppins));
                                                        mAuth.signOut();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                        catch(Exception e){
                                            Toast.makeText(Login.this, "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(Login.this, "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                    }
                }
        );

        registerButton.setOnClickListener(
                view -> {
                    startActivity(new Intent(Login.this, Registration.class));
                }
        );

        forgotPassword.setOnClickListener(
                view -> {
                    startActivity(new Intent(this, ForgotPassword.class));
                }
        );

    }
    // TODO: End of OnCreate, Method definition below

//    private void next_user() {
//        if ((edtPhone.getText().toString().length() == 10) && (val != 0)) {
//
//            offanimate(number);
//            offanimate(textView2);
//            offanimate(get_Otp);
//            offanimate(alumni);
//            offanimate(student);
//
//            new Handler(Looper.myLooper()).postDelayed(() -> {
//                otp_verify.setVisibility(View.VISIBLE);
//                back.setVisibility(View.VISIBLE);
//                otp_verify.startAnimation(animFadein);
//            }, 600);
//            textView3.setText("Successfully sent a verification code on " + edtPhone.getText().toString());
//            String phone = "+91" + edtPhone.getText().toString();
//            sendVerificationCode(phone);
//            Toast.makeText(Login.this, "Please wait while we process.", Toast.LENGTH_SHORT).show();
//
//        } else {
//            if (edtPhone.getText().toString().length() < 10)
//                Toast.makeText(Login.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
//            else if (val == 0) {
//                student.setVisibility(View.VISIBLE);
//                alumni.setVisibility(View.VISIBLE);
//                Toast.makeText(Login.this, "Please choose which option suits you", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void next() {
//        offanimate(number);
//        offanimate(textView2);
//        offanimate(get_Otp);
//        offanimate(alumni);
//        offanimate(student);
//
//        new Handler(Looper.myLooper()).postDelayed(() -> {
//            otp_verify.setVisibility(View.VISIBLE);
//            back.setVisibility(View.VISIBLE);
//            otp_verify.startAnimation(animFadein);
//
//        }, 600);
//        textView3.setText("Successfully sent a verification code on " + edtPhone.getText().toString());
//        String phone = "+91" + edtPhone.getText().toString();
//        sendVerificationCode(phone);
//        Toast.makeText(this, "Please wait while we process.", Toast.LENGTH_SHORT).show();
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth=FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//    }

    private void Home_gateway() {
        Intent mainIntent = new Intent(Login.this , MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToForm() {
        startActivity(new Intent(Login.this, Registration.class));
    }

    private void setStatusBarTransparent() {
        Window window = Login.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

//    void offanimate(View view){
//        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationX",-800f);
//        move.setDuration(1000);
//        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",0);
//        alpha2.setDuration(500);
//        AnimatorSet animset=new AnimatorSet();
//        animset.play(alpha2).with(move);
//        animset.start();
//    }
//    void onAnimate(View view){
//        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationX",0f);
//        move.setDuration(1000);
//        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",100);
//        alpha2.setDuration(500);
//        AnimatorSet animset =new AnimatorSet();
//        animset.play(alpha2).with(move);
//        animset.start();
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getting_device_token() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if(nc!=null) {
            downspeed = nc.getLinkDownstreamBandwidthKbps()/1000;
            upspeed = nc.getLinkUpstreamBandwidthKbps()/1000;
        }else{
            downspeed=0;
            upspeed=0;
        }
        if((upspeed!=0 && downspeed!=0) || getWifiLevel()!=0) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                if (!TextUtils.isEmpty(token)) {
                    // Successfully retrieved token
                } else {
                    // Handle case where token is null or empty
                }
            }).addOnFailureListener(e -> {
                //handle e
            }).addOnCanceledListener(() -> {
                //handle cancel
            }).addOnCompleteListener(task ->
            {
                try {
                    deviceToken = task.getResult();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public int getWifiLevel()
    {
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int linkSpeed = wifiManager.getConnectionInfo().getRssi();
            return WifiManager.calculateSignalLevel(linkSpeed, 5);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}