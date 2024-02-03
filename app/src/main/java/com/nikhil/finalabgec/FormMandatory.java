package com.nikhil.finalabgec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FormMandatory extends AppCompatActivity {

    TextView submit,name,state,country,city;
    String nam,stat,count,cit,br,pass;
    AutoCompleteTextView branch,passout_yr;
    ConstraintLayout lay;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    String phone,token,uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_mandatotary);

        Window window = FormMandatory.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(FormMandatory.this, R.color.white));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        submit = findViewById(R.id.submit_txt);
        name = findViewById(R.id.name);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        //mobile_no = view.findViewById(R.id.mobile_no);
        branch = findViewById(R.id.branch);
        passout_yr = findViewById(R.id.passout_yr);

        String[] department={"CSE - Computer Science", "IT - Information Technology", "ME - Mining","ETNT - Electronics & Telecommunication",
                "MECH - Mechanical","EE - Electrical","Civil"};


        String[] year={"1970" ,"1971" ,"1972", "1973", "1974", "1975" ,"1976", "1977","1978","1979", "1980", "1981" ,"1982" ,"1983" ,"1984", "1985" ,"1986" ,"1987" ,"1988", "1989", "1990", "1991","1992", "1993" ,"1994", "1995" ,"1996" ,"1997", "1998", "1999", "2000","2001","2002" ,"2003" ,"2004" ,"2005" ,"2006" ,"2007" ,
                "2008", "2009", "2010" ,"2011" ,"2012", "2013", "2014", "2015" ,"2016" ,"2017" ,"2018" ,"2019" ,"2020" ,"2021" ,"2022", "2023"};

        ArrayAdapter<String> adapter= new ArrayAdapter<>(FormMandatory.this, android.R.layout.simple_dropdown_item_1line, department);
        branch.setThreshold(1);
        branch.setAdapter(adapter);


        ArrayAdapter<String>  adapter1 = new ArrayAdapter<>(FormMandatory.this, android.R.layout.simple_dropdown_item_1line, year);
        passout_yr.setThreshold(1);
        passout_yr.setAdapter(adapter1);

        lay = findViewById(R.id.lay);
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        valueGetting();



        submit.setOnClickListener(v-> {
            if(!name.getText().toString().trim().equals("")){
                if(!branch.getText().toString().trim().equals("")){
                    if(!passout_yr.getText().toString().trim().equals("")){
                        if(!country.getText().toString().trim().equals("")){
                            if(!state.getText().toString().trim().equals("")){
                                if(!city.getText().toString().trim().equals("")) {
                                    datasend();
                                }
                                else{
                                    city.setError("Empty");
                                    Snackbar.make(lay,"Please Add City",Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.parseColor("#171746"))
                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                            .setBackgroundTint(Color.parseColor("#171746"))
                                            .show();
                                }
                            }
                            else{
                                state.setError("Empty");
                                Snackbar.make(lay,"Please Add State",Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#171746"))
                                        .setTextColor(Color.parseColor("#FF7F5C"))
                                        .setBackgroundTint(Color.parseColor("#171746"))
                                        .show();
                            }
                        }
                        else{
                            country.setError("Empty");
                            Snackbar.make(lay,"Please Add Country",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                        }
                    }
                    else{
                        passout_yr.setError("Empty");
                        Snackbar.make(lay,"Please Add Passout Year",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#171746"))
                                .setTextColor(Color.parseColor("#FF7F5C"))
                                .setBackgroundTint(Color.parseColor("#171746"))
                                .show();
                    }
                }
                else{
                    branch.setError("Empty");
                    Snackbar.make(lay,"Please Add Branch.",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#171746"))
                            .setTextColor(Color.parseColor("#FF7F5C"))
                            .setBackgroundTint(Color.parseColor("#171746"))
                            .show();
                }
            }
            else{
                name.setError("Empty");
                Snackbar.make(lay,"Please Add Name.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        });
    }

    private void valueGetting() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    br = snapshot.child(user.getUid()).child("branch").getValue(String.class);
                    pass = snapshot.child(user.getUid()).child("passout").getValue(String.class);
                    count = snapshot.child(user.getUid()).child("country").getValue(String.class);
                    stat = snapshot.child(user.getUid()).child("state").getValue(String.class);
                    cit = snapshot.child(user.getUid()).child("city").getValue(String.class);
                    phone = snapshot.child(user.getUid()).child("phone").getValue(String.class);
                    nam = snapshot.child(user.getUid()).child("name").getValue(String.class);

                    name.setText(nam);
                    branch.setText(br);
                    passout_yr.setText(pass);
                    state.setText(stat);
                    country.setText(count);
                    city.setText(cit);
                    state.setText(stat);

                    getSharedPreferences("Authorized_for_Access",MODE_PRIVATE).edit()
                            .putBoolean("is_Authorized_to_access_the_app",true).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void datasend(){


        reference.child(user.getUid()).child("name").setValue(name.getText().toString());
        reference.child(user.getUid()).child("branch").setValue(branch.getText().toString());
        reference.child(user.getUid()).child("passout").setValue(passout_yr.getText().toString());
        reference.child(user.getUid()).child("country").setValue(country.getText().toString());
        reference.child(user.getUid()).child("state").setValue(state.getText().toString());
        reference.child(user.getUid()).child("city").setValue(city.getText().toString());
        reference.child(user.getUid()).child("id").setValue("Alumni");
        reference.child(user.getUid()).child("dp_link").setValue("");
        reference.child(user.getUid()).child("gender").setValue("");
        reference.child(user.getUid()).child("dob").setValue("");
        reference.child(user.getUid()).child("bio").setValue("");
        reference.child(user.getUid()).child("fb").setValue("");
        reference.child(user.getUid()).child("insta").setValue("");
        reference.child(user.getUid()).child("twitter").setValue("");
        reference.child(user.getUid()).child("linkedin").setValue("");
        reference.child(user.getUid()).child("occupation").setValue("");
        reference.child(user.getUid()).child("organization").setValue("");
        reference.child(user.getUid()).child("designation").setValue("");

        getSharedPreferences("Authorized_for_Access",MODE_PRIVATE).edit()
                .putBoolean("is_Authorized_to_access_the_app",true).apply();

        Intent myIntent = new Intent(FormMandatory.this, MainActivity.class);
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(FormMandatory.this, R.anim.fade_in, R.anim.fade_out);
        FormMandatory.this.startActivity(myIntent, options.toBundle());
        FormMandatory.this.finish();
    }
}