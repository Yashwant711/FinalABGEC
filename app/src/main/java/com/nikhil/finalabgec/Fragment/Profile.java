package com.nikhil.finalabgec.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.finalabgec.Edit;
import com.nikhil.finalabgec.MainActivity;
import com.nikhil.finalabgec.Model.UserDataModel;
import com.nikhil.finalabgec.R;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.ibrahimsn.lib.SmoothBottomBar;

public class Profile extends Fragment {

    View view;
    Context contextNullSafe;
//    TextView bio, organiztion, designation, year, name, state, country, city ,branch, passout_yr,dob,occupation;
//    TextView gend,textView70,textView80,textView90,textView91,about;
//    String gen, dateOfBirth, bioo, fcb, twt, lin, inst, occup, organ, desig, nam, br, py, countr, stat, cit,dp_link,phone;
    Dialog dialog;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String uid_of_user,addtostack;
    SmoothBottomBar smoothBottomBar;
    DatabaseReference reference, user_ref;
    FirebaseAuth auth;
    FirebaseUser user;
    String uid;

    String uName, uBatch, uBranch, uDob, uGender, uCity, uState, uCountry;
    String uBio, uDesignation, uOrganization, uEmail, uPhone;
    UserDataModel userDataModel;
    private TextView userName, batch, branch, dob, gender, location;
    LinearLayout editProfileLayout, jobsLayout, postsLayout, contactSection, profileLayout;
    private TextView bio, designation, organization, email, phone;
    private CircleImageView whatsappIcon;
    private CircleImageView linkedinIcon, facebookIcon, instagramIcon, twitterIcon;
    SimpleDraweeView profileImage;
    LottieAnimationView loadImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

//        whatsapp = view.findViewById(R.id.whatsapp);
//        //posts = view.findViewById(R.id.posts);
//        textView80 = view.findViewById(R.id.textView80);
//        textView90 = view.findViewById(R.id.textView90);
//        textView70 = view.findViewById(R.id.textView70);
//        textView91 = view.findViewById(R.id.textView91);
//        about = view.findViewById(R.id.about);
//        editProfile = view.findViewById(R.id.editProfileLayout);
//        postedJobs = view.findViewById(R.id.jobsLayout);
//        posts = view.findViewById(R.id.postsLayout);
//        constraintLayout1 = view.findViewById(R.id.constraintLayout1);

        smoothBottomBar=requireActivity().findViewById(R.id.bottomBar);

        if (contextNullSafe == null) getContextNullSafety();


        try {
//            assert getArguments() != null;
            addtostack = getArguments().getString("sending_user_from_sync");
            uid_of_user = getArguments().getString("uid_sending_profile");
        } catch (Exception e) {
            e.printStackTrace();
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();

      /*  if (check_for_admin()){
            editProfile.setVisibility(View.GONE);
        }*/
        // new Handler(Looper.myLooper()).postDelayed(this::valueGetting,500);

        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(addtostack!=null){
                    smoothBottomBar.setItemActiveIndex(2);
                    FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                    FragmentTransaction ft=fm.beginTransaction();
                    if(fm.getBackStackEntryCount()>0) {
                        fm.popBackStack();
                    }
                    ft.commit();
                }
                else {
                    if (((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer) != null) {
                        ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                                .beginTransaction().
                                remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer))).commit();
                    }
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new Post())
                            .commit();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);


        if(uid_of_user==null) {
            assert user != null;
            uid_of_user = user.getUid();//check for uid bundle if yes then don't do this and vice-versa.
        }


        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);

        reference = FirebaseDatabase.getInstance().getReference().child("users");

//        name = view.findViewById(R.id.name);
//        state =  view.findViewById(R.id.state_value);
//        city =  view.findViewById(R.id.city_p);
//        country =  view.findViewById(R.id.country_p);
//        //mobile_no = view.findViewById(R.id.mobile_no);
//        branch =  view.findViewById(R.id.branch_p);
//        organiztion =  view.findViewById(R.id.organ_p);
//        designation =  view.findViewById(R.id.design_p);
//        insta =  view.findViewById(R.id.instagram);
//        linkidin =  view.findViewById(R.id.linkedin);
//        fb =  view.findViewById(R.id.facebook);
//        twitter =  view.findViewById(R.id.twitter);
//        occupation = view.findViewById(R.id.occup_p);
//        bio =  view.findViewById(R.id.bio);
//        gend = view.findViewById(R.id.gender_p);

        userName = view.findViewById(R.id.userName);
        batch = view.findViewById(R.id.batch);
        branch = view.findViewById(R.id.branch);
        dob = view.findViewById(R.id.dob);
        gender = view.findViewById(R.id.gender);
        location = view.findViewById(R.id.location);
//        postsLayout = view.findViewById(R.id.postsLayout);
        jobsLayout = view.findViewById(R.id.jobsLayout);
        editProfileLayout = view.findViewById(R.id.editProfileLayout);
        profileLayout = view.findViewById(R.id.profile_layout);
        bio = view.findViewById(R.id.bio);
        designation = view.findViewById(R.id.designation);
        organization = view.findViewById(R.id.organization);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        whatsappIcon = view.findViewById(R.id.whatsappIcon);
        linkedinIcon = view.findViewById(R.id.linkedinIcon);
        facebookIcon = view.findViewById(R.id.facebookIcon);
        instagramIcon = view.findViewById(R.id.instagramIcon);
        twitterIcon = view.findViewById(R.id.twitterIcon);
        profileImage = view.findViewById(R.id.profileImage);
        contactSection = view.findViewById(R.id.contact_section);
        loadImg = view.findViewById(R.id.loadImage);

        //  Hide the keyboard
        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if(uid.equals(uid_of_user)){
            editProfileLayout.setVisibility(View.VISIBLE);
//            jobsLayout.setVisibility(View.VISIBLE);
//            postsLayout.setVisibility(View.VISIBLE);
        }

        valueGetting();

        mSwipeRefreshLayout.setOnRefreshListener(this::valueGetting);

        editProfileLayout.setOnClickListener(v->{

           /* if(!name.getText().toString().equals("")) {
                Intent intent = new Intent(getContextNullSafety(), Edit.class);
                intent.putExtra("gender", gen);
                intent.putExtra("dob", dateOfBirth);
                intent.putExtra("bio", bioo);
                intent.putExtra("fb", fcb);
                intent.putExtra("instagram", inst);
                intent.putExtra("linkedin", lin);
                intent.putExtra("twitter", twt);
                intent.putExtra("occupation", occup);
                intent.putExtra("organisation", organ);
                intent.putExtra("designation", desig);
                intent.putExtra("name", nam);
                intent.putExtra("branch", br);
                intent.putExtra("passout_yr", py);
                intent.putExtra("country", countr);
                intent.putExtra("state", stat);
                intent.putExtra("city", cit);
                intent.putExtra("dp_link", dp_link);
                startActivity(intent);
            }
            else {*/
//
//            dialog = new Dialog(getContextNullSafety());
//            dialog.setCancelable(true);
//            dialog.setContentView(R.layout.loading2);
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            dialog.show();
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    dialog.dismiss();
//                    Intent intent = new Intent(getContextNullSafety(), Edit.class);
//                    startActivity(intent);
//                }
//
//            },1000);

            Intent intent = new Intent(getContextNullSafety(), Edit.class);
            startActivity(intent);

        });

//        jobsLayout.setOnClickListener(v->{
//            FragmentManager manager = getFragmentManager();
//            assert manager != null;
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//            transaction.replace(R.id.change_layout, new PostedJobs());
//            transaction.addToBackStack(null);
//            transaction.commit();
//        });

        return view;
    }


    private void openTwitter(String twitterUsername) {
        try {
            // Get the Twitter app's URI
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterUsername));

            // Check if the Twitter app is installed
            PackageManager packageManager = getContextNullSafety().getPackageManager();
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            if (resolveInfoList.size() > 0) {
                // If the Twitter app is installed, open it
                startActivity(intent);
            } else {
                // If the Twitter app is not installed, open Twitter in a browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twitterUsername));
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid link", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFacebook(String facebookId) {
        // Replace with Facebook ID
        try {
            // Get the Facebook app's URI
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + facebookId));

            // Check if the Facebook app is installed
            PackageManager packageManager = getContextNullSafety().getPackageManager();
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            if (resolveInfoList.size() > 0) {
                // If the Facebook app is installed, open it
                startActivity(intent);
            } else {
                // If the Facebook app is not installed, open Facebook in a browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + facebookId));
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid link", Toast.LENGTH_SHORT).show();
        }
    }

    private void valueGetting() {

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid_of_user);
        mSwipeRefreshLayout.setRefreshing(true);
        profileLayout.setVisibility(View.GONE);
        loadImg.setVisibility(View.VISIBLE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    userDataModel = snapshot.getValue(UserDataModel.class);

//                    gen = snapshot.child("gender").getValue(String.class);
//                    dateOfBirth = snapshot.child("dob").getValue(String.class);
//                    bioo = snapshot.child("bio").getValue(String.class);
//                    fcb = snapshot.child("fb").getValue(String.class);
//                    inst = snapshot.child("insta").getValue(String.class);
//                    lin = snapshot.child("linkedin").getValue(String.class);
//                    twt = snapshot.child("twitter").getValue(String.class);
//                    occup = snapshot.child("occupation").getValue(String.class);
//                    organ = snapshot.child("organization").getValue(String.class);
//                    desig = snapshot.child("designation").getValue(String.class);
//                    nam = snapshot.child("name").getValue(String.class);
//                    br = snapshot.child("branch").getValue(String.class);
//                    py = snapshot.child("passout").getValue(String.class);
//                    countr = snapshot.child("country").getValue(String.class);
//                    stat = snapshot.child("state").getValue(String.class);
//                    cit = snapshot.child("city").getValue(String.class);
//                    dp_link =snapshot.child("dp_link").getValue(String.class);
//                    phone = snapshot.child("phone").getValue(String.class);

                    visibility_views();

                }

                mSwipeRefreshLayout.setRefreshing(false);
                profileLayout.setVisibility(View.VISIBLE);
                loadImg.setVisibility(View.GONE);

                whatsappIcon.setOnClickListener(v->{
                    try{
                        String phoneNumber = userDataModel.getPhone();
                        String message = "Hello,sir how are you?"; // Replace with message
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message));
                        startActivity(intent);
                    }
                    catch (Exception e){
                        Toast.makeText(getContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                    }
                });

                linkedinIcon.setOnClickListener(v -> {
                    try {
                        String url = userDataModel.getLinkedin();
                        Intent linkedInAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        linkedInAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        startActivity(linkedInAppIntent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Invalid link", Toast.LENGTH_SHORT).show();
                    }
                });

                twitterIcon.setOnClickListener(v -> {
                    openTwitter(userDataModel.getTwitter());
                });

                instagramIcon.setOnClickListener(v -> {
                    String inst = userDataModel.getInsta();
                    try{
                        Intent insta_in;
                        String scheme = "http://instagram.com/_u/" + inst;
                        String path = "https://instagram.com/" + inst;
                        String nomPackageInfo ="com.instagram.android";
                        try {
                            requireContext().getPackageManager().getPackageInfo(nomPackageInfo, 0);
                            insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
                        } catch (Exception e) {
                            insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                        }
                        startActivity(insta_in);
                    }
                    catch (Exception e){
                        Toast.makeText(getContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                    }
                });

                facebookIcon.setOnClickListener(v -> {
                    openFacebook(userDataModel.getFb());
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void visibility_views(){

        String dp_link = userDataModel.getDp_link() != null? userDataModel.getDp_link() : "";
        if(!dp_link.isEmpty()){
            try {
                profileImage.setImageURI(dp_link);
            } catch (Exception e) {
                Toast.makeText(getContextNullSafety(), "Invalid Image", Toast.LENGTH_SHORT).show();
            }
        }
        else profileImage.setImageURI(Uri.EMPTY);

        uName = userDataModel.getName() != null? userDataModel.getName() : "NA";
        uBatch = "Batch: " + (userDataModel.getBatch() != null? userDataModel.getBatch() : "NA");
        uBranch = "Branch: " + (userDataModel.getBranch() != null? userDataModel.getBranch() : "NA");
        uDob = "DOB: " + (userDataModel.getDob() != null? userDataModel.getDob() : "NA");
        uGender = "Gender: " + (userDataModel.getGender() != null? userDataModel.getGender() : "NA");
        userName.setText(uName);
        batch.setText(uBatch);
        branch.setText(uBranch);
        dob.setText(uDob);
        gender.setText(uGender);

        uCity = userDataModel.getCity() != null? userDataModel.getCity() : "NA";
        uState = userDataModel.getState() !=null? userDataModel.getState() : "NA";
        uCountry = userDataModel.getCountry() !=null? userDataModel.getCountry() : "NA";
        String loc = "Location: "+ uCity + ", " + uState + ", " + uCountry;
        location.setText(loc);


        uBio = userDataModel.getBio() != null? userDataModel.getBio() : "NA";
        uDesignation = userDataModel.getDesignation() != null? userDataModel.getDesignation() : "NA";
        uOrganization = userDataModel.getOrganization() != null? userDataModel.getOrganization() : "NA";
        bio.setText(uBio);
        designation.setText(uDesignation);
        organization.setText(uOrganization);

        if(userDataModel.isContact_visibility()){
            contactSection.setVisibility(View.VISIBLE);
            uEmail = "Email: " + (userDataModel.getEmail() != null? userDataModel.getEmail() : "NA");
            uPhone = "Phone Number: " + (userDataModel.getPhone() != null? userDataModel.getPhone() : "NA");
            email.setText(uEmail);
            phone.setText(uPhone);
        }

//        if (bioo.equals("") && occup.equals("") && desig.equals("") && organ.equals("")){
//            about.setVisibility(View.GONE);
//            constraintLayout1.setVisibility(View.GONE);
//        }
//        if (bioo.equals("")) {
//            bio.setVisibility(View.GONE);
//            textView70.setVisibility(View.GONE);
//        }
//        if (occup.equals("")) {
//            occupation.setVisibility(View.GONE);
//            textView80.setVisibility(View.GONE);
//        }
//        if (desig.equals("")) {
//            designation.setVisibility(View.GONE);
//            textView91.setVisibility(View.GONE);
//        }
//        if (organ.equals("")) {
//            organiztion.setVisibility(View.GONE);
//            textView90.setVisibility(View.GONE);
//        }
//        if(!fcb.equals("")){
//            fb.setVisibility(View.VISIBLE);
//        }
//        if(!inst.equals("")){
//            insta.setVisibility(View.VISIBLE);
//        }if(!twt.equals("")){
//            twitter.setVisibility(View.VISIBLE);
//        }if(!lin.equals("")){
//            linkidin.setVisibility(View.VISIBLE);
//        }
//        bio.setText(bioo);
//        organiztion.setText(organ);
//        designation.setText(desig);
//        occupation.setText(occup);
//        name.setText(nam);
//        branch.setText(br);
//        if (!py.equals("")) {
//            passout_yr.setVisibility(View.VISIBLE);
//            passout_yr.setText(py);
//        }
//        country.setText(countr);
//        state.setText(stat);
//        city.setText(cit);
//
//        if (gen.equals("Female")){
//            gend.setText("(She/her)");
//        }
//        else
//            gend.setText("(He/him)");

    }

    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();
        return null;
    }

}