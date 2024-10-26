package com.nikhil.finalabgec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;

import www.sanju.motiontoast.MotionToast;

public class ForgotPassword extends AppCompatActivity {

    EditText emailId;
    TextView resetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setStatusBarTransparent();

        emailId = findViewById(R.id.email_id);
        resetPassword = findViewById(R.id.resetPassword);

        resetPassword.setOnClickListener(
                view -> {
                    String email = emailId.getText().toString().trim();
                    if (email.isEmpty()) {
                        emailId.setError("Email cannot be empty");
                        emailId.requestFocus();
                        return;
                    }
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    MotionToast.Companion.darkColorToast(this,
                                            "Reset Successful",
                                            "Sent a link to reset your password at your registered email ID",
                                            MotionToast.TOAST_SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.poppins));
                                }
                                else{
                                    MotionToast.Companion.darkColorToast(this,
                                            "Reset Failed",
                                            "Please try again later",
                                            MotionToast.TOAST_ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.poppins));
                                }
                            });
                    startActivity(new Intent(this, Login.class));
                    finish();
                }
        );
    }

    private void setStatusBarTransparent() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

}


