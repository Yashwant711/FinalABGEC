package com.nikhil.finalabgec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import www.sanju.motiontoast.MotionToast;

public class Registration extends AppCompatActivity {

    TextView submit;
    EditText name, emailId, passwordCreate, passwordConfirm;
    Spinner branchSpinner, batchSpinner;
    ConstraintLayout lay;
    DatabaseReference reference;
    String uid;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Window window = Registration.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Registration.this, R.color.white));

        auth = FirebaseAuth.getInstance();

        submit = findViewById(R.id.submit_txt);
        name = findViewById(R.id.name);
        branchSpinner = findViewById(R.id.branch_spinner);
        batchSpinner = findViewById(R.id.batch_spinner);
        emailId = findViewById(R.id.email_id);
        passwordCreate = findViewById(R.id.et_password);
        passwordConfirm = findViewById(R.id.et_confirmPassword);

        ArrayAdapter<String> adapter = getYearArrayAdapter();
        adapter.setDropDownViewResource(R.layout.spinner_item);
        batchSpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = getStringArrayAdapter();
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        branchSpinner.setAdapter(adapter1);

        lay = findViewById(R.id.lay);
        reference = FirebaseDatabase.getInstance().getReference().child("Unverified");

        submit.setOnClickListener(v-> {
            if (
                    isFieldValid(name, "Please enter your name") &&
                            isFieldValid(emailId, "Please enter your email-id") &&
                            isFieldValid(passwordCreate, "Please create a password") &&
                            isFieldValid(passwordConfirm, "Please confirm your password") &&
                            isSpinnerValid(branchSpinner, "Please select a branch") &&
                            isSpinnerValid(batchSpinner, "Please select your batch") &&
                            isEmailValid()  &&
                            matchPasswords() &&
                            isNotShort()
            ) {
                createUnverifiedUser();
            }
        });
    }
    // End of OnCreate Method, member method declaration below

    private @NonNull ArrayAdapter<String> getYearArrayAdapter() {
        List<String> years = new ArrayList<>();
        years.add("Select your batch");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear+4; i >= currentYear - 95; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, years) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item (placeholder) from being selected.
                return position != 0;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Inflate the view for the selected item
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                // Set the placeholder text color to gray
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                // Set the hint (placeholder) color for the first item
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }

                return view;
            }
        };
        return adapter;
    }

    private @NonNull ArrayAdapter<String> getStringArrayAdapter() {
        String[] branches = {"Select your branch", "Computer Science", "Mechanical", "Electrical", "Civil", "Electronics & Telecommunication", "Mining", "Information Technology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, branches) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item (placeholder) from being selected.
                return position != 0;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Inflate the view for the selected item
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;

                // Set the placeholder text color to gray
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                // Set the hint (placeholder) color for the first item
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }

                return view;
            }
        };
        return adapter;
    }

    private boolean isFieldValid(EditText field, String errorMessage) {
        if (field.getText().toString().trim().isEmpty()) {
            field.setError("Empty");
            Snackbar.make(lay, errorMessage, Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
            return false;
        }
        return true;
    }

    private boolean isSpinnerValid(Spinner spinner, String errorMessage) {
        if (spinner.getSelectedItemPosition() == 0) {
            Snackbar.make(lay, errorMessage, Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(){
        String email = emailId.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailId.setError("Invalid Email");
            Snackbar.make(lay, "Please enter a valid email address", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
            return false;
        }
        return true;
    }

    private boolean matchPasswords(){
        String p1 = passwordConfirm.getText().toString();
        String p2 = passwordCreate.getText().toString();
        if (!p1.equals(p2)){
            Snackbar.make(lay, "Passwords not matching", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
            return false;
        }
        return true;
    }

    private boolean isNotShort(){
        String p = passwordCreate.getText().toString();
        if(p.length() < 8){
            Snackbar.make(lay, "Password too short", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
            return false;
        }
        return true;
    }

    private void createUnverifiedUser(){

        String email = emailId.getText().toString().trim();
        String password = passwordCreate.getText().toString();
        String name = this.name.getText().toString().trim();
        String batch = batchSpinner.getSelectedItem().toString();
        String branch = branchSpinner.getSelectedItem().toString();

        auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                user = auth.getCurrentUser();
                                uid = user.getUid();
                                reference.child(uid).child("uid").setValue(uid);
                                reference.child(uid).child("name").setValue(name);
                                reference.child(uid).child("email").setValue(email);
                                reference.child(uid).child("batch").setValue(batch);
                                reference.child(uid).child("branch").setValue(branch);
                                reference.child(uid).child("isAdmin").setValue(false);
                                MotionToast.Companion.darkColorToast(
                                        this,
                                        "Registration Successful!",
                                        "We will send you a mail after your account is activated.",
                                        MotionToast.TOAST_INFO,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(this, R.font.poppins));
                            }
                            else{
                                Exception exception = task.getException();
                                if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid email address
                                    MotionToast.Companion.darkColorToast(
                                            this,
                                            "Registration Failed!",
                                            "Invalid Email Address",
                                            MotionToast.TOAST_ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.poppins));
                                } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                    // Email address is already in use
                                    MotionToast.Companion.darkColorToast(
                                            this,
                                            "Registration Failed!",
                                            "Email already registered   ",
                                            MotionToast.TOAST_ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.poppins));
                                }
                                else{
                                    MotionToast.Companion.darkColorToast(
                                            this,
                                            "Registration Failed!",
                                            "Something went wrong. Please try again!",
                                            MotionToast.TOAST_ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.poppins));
                                }
                            }
                        });

        Intent myIntent = new Intent(Registration.this, Login.class);
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(Registration.this, R.anim.fade_in, R.anim.fade_out);
        Registration.this.startActivity(myIntent, options.toBundle());
        Registration.this.finish();
    }
}