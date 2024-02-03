package com.nikhil.finalabgec.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nikhil.finalabgec.Adapter.PostAdapter;
import com.nikhil.finalabgec.Model.PostModel;
import com.nikhil.finalabgec.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import me.ibrahimsn.lib.SmoothBottomBar;


public class Post extends Fragment {


    View view;
    SmoothBottomBar smoothBottomBar;
    Context contextNullSafe;
    DatabaseReference reference;
    RecyclerView recyclerView;
    EditText search;
    ArrayList<PostModel> list;
    ArrayList<PostModel> mylist;
    LottieAnimationView loadimage;
    TextView loadText;

    SwipeRefreshLayout mSwipeRefreshLayout;
    PostAdapter userAdapter;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_post, container, false);
        smoothBottomBar = view.findViewById(R.id.bottomBar);

        if (contextNullSafe == null) getContextNullSafety();
//Hide the keyboard
        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        list=new ArrayList<>();
        mylist=new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("posts");
        loadimage = view.findViewById(R.id.loadImage);
        loadText = view.findViewById(R.id.loadText);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContextNullSafety());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setLayoutManager(layoutManager);

        search=view.findViewById(R.id.input);
        smoothBottomBar= requireActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setItemActiveIndex(0);

        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        getPost();
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/myTopic3")
                .addOnCompleteListener(task -> {
                    String msg = "Done";
                    if (!task.isSuccessful()) {
                        msg = "Failed";
                    }
                    Log.d("topic_log", msg);
                });

        mSwipeRefreshLayout.setOnRefreshListener(this::getPost);
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer) != null) {
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer))).commit();
                }
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,new Post())
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s+"");
            }
        });
        return view;
    }

    private void getPost() {

        mSwipeRefreshLayout.setRefreshing(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mylist.clear();
                    list.clear();
                    mSwipeRefreshLayout.setRefreshing(true);
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(PostModel.class));
                    }
                    Collections.reverse(list);
                    mSwipeRefreshLayout.setRefreshing(false);
                    loadimage.setVisibility(View.GONE);
                    loadText.setVisibility(View.GONE);
                    userAdapter = new PostAdapter(list, contextNullSafe);
                    userAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(userAdapter);
                }
                else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    loadimage.setVisibility(View.VISIBLE);
                    loadText.setVisibility(View.VISIBLE);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void search (String s) {
        mylist.clear();
        for (PostModel object : list) {
            try {
                if (object.getName().toLowerCase().contains(s.toLowerCase().trim())) {
                    mylist.add(object);
                } else if (object.getTitle().toLowerCase().contains(s.toLowerCase().trim())) {
                    mylist.add(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PostAdapter userAdapter = new PostAdapter(mylist,getContextNullSafety());
        userAdapter.notifyDataSetChanged();
        if (recyclerView != null)
            recyclerView.setAdapter(userAdapter);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
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