package com.nikhil.finalabgec.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.nikhil.finalabgec.Fragment.ViewPost;
import com.nikhil.finalabgec.Model.PostModel;
import com.nikhil.finalabgec.R;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<PostModel> list;
    Context context;
    View view;
    int count = 0;
    FirebaseUser user;
    FirebaseAuth auth;
    int total_likes;
    int click =0;
    DatabaseReference reference,reference2;
    public PostAdapter(List<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("posts");
        reference2 = FirebaseDatabase.getInstance().getReference().child("users");

        if (list.size()!=0) {

            reference.child(list.get(position).getPushkey()).child("likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.like.setText(String.valueOf(snapshot.getChildrenCount()));
                    total_likes = (int) snapshot.getChildrenCount();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (Objects.equals(ds.getKey(), user.getUid())) {
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = context.getResources().getDrawable(R.drawable.ic_heart_liked);
                            holder.heart_no.setImageDrawable(drawable);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        if (position < list.size()) {
            try {
                Uri uri = Uri.parse(list.get(position).getImage_link());
                holder.image.setImageURI(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!list.get(position).getDp_link().equals("")) {
                holder.profile_img.setImageURI(list.get(position).getDp_link());
            }else{
                holder.profile_img.setImageURI(String.valueOf(R.drawable.ic_abgec_loading));
            }

            if (!list.get(position).getTitle().equals("")) {
                holder.title.setText(list.get(position).getTitle());
            }
            if (!list.get(position).getName().equals("")) {
                holder.name.setText("By: " + list.get(position).getName());
            }
            if (!list.get(position).getDescription().equals("")) {
                holder.description.setText(list.get(position).getDescription());
            }

            holder.share.setVisibility(View.GONE);

            holder.heart_no.setOnClickListener(v->{
                if (click==0){
                    Drawable drawable = context.getResources().getDrawable(R.drawable.ic_heart_liked);
                    count += 1;
                    reference.child(list.get(position).getPushkey()).child("likes").child(user.getUid()).setValue("liked");
                    holder.heart_no.setImageDrawable(drawable);
                                /*holder.heart_no.setImageDrawable(R.id.heart_fill);
                                .setVisibility(View.GONE);
                                holder.heart.setVisibility(View.VISIBLE);*/
                    click++;
                }
                else {
                    Drawable drawable = context.getResources().getDrawable(R.drawable.ic_heart);
                    count = 1;
                    reference.child(list.get(position).getPushkey()).child("likes").child(user.getUid()).removeValue();
                    holder.heart_no.setImageDrawable(drawable);
                                /*holder.heart_no.setImageDrawable(R.id.heart_fill);
                                .setVisibility(View.GONE);
                                holder.heart.setVisibility(View.VISIBLE);*/
                    click = 0;
                }
            });

            holder.layout.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick() {

                    holder.like_btn.setVisibility(View.VISIBLE);
                    new  Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.like_btn.setVisibility(View.GONE);
                            if (click==0){
                                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_heart_liked);
                                count += 1;
                                reference.child(list.get(position).getPushkey()).child("likes").child(user.getUid()).setValue("liked");
                                holder.heart_no.setImageDrawable(drawable);
                                /*holder.heart_no.setImageDrawable(R.id.heart_fill);
                                .setVisibility(View.GONE);
                                holder.heart.setVisibility(View.VISIBLE);*/
                                click++;
                            }
                           /* else {
                                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_heart);
                                count -= 1;
                                reference.child("likes").child(user.getUid()).removeValue();
                                holder.heart_no.setImageDrawable(drawable);
                                *//*holder.heart_no.setImageDrawable(R.id.heart_fill);
                                .setVisibility(View.GONE);
                                holder.heart.setVisibility(View.VISIBLE);*//*
                                click--;
                            }
*/
                        }

                    },900);

                }

                @Override
                public void onSingleClick() {
                    ViewPost profile = new ViewPost();

                    Bundle args = new Bundle();
                    args.putString("sending_user_from_home","addstack");
                    args.putString("uid_sending_post", list.get(position).getUid());
                    args.putString("name",list.get(position).getName());
                    args.putString("title",list.get(position).getTitle());
                    args.putString("description",list.get(position).getDescription());
                    args.putString("pushkey",list.get(position).getPushkey());
                    args.putString("image",list.get(position).getImage_link());
                    args.putString("date",list.get(position).getDate());
                    args.putString("like", String.valueOf(total_likes));
                    profile.setArguments(args);
                    ((FragmentActivity)context).getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .add(R.id.change_layout, profile)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,link,title,description,branch,like;
        SimpleDraweeView image , profile_img;
        LinearLayout layout;
        ImageView heart_no, share;
        LottieAnimationView like_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.topic);
            name = itemView.findViewById(R.id.name);
            like = itemView.findViewById(R.id.like_count);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.post_image);
            layout = itemView.findViewById(R.id.layout);
            like_btn = itemView.findViewById(R.id.like_btn);
            heart_no = itemView.findViewById(R.id.heart);
            profile_img = itemView.findViewById(R.id.profile_img);
            share = itemView.findViewById(R.id.share);
        }
    }

    public abstract class DoubleClickListener implements View.OnClickListener {
        private static final long DEFAULT_QUALIFICATION_SPAN = 200;
        private boolean isSingleEvent;
        private long doubleClickQualificationSpanInMillis;
        private long timestampLastClick;
        private Handler handler;
        private Runnable runnable;

        public DoubleClickListener() {
            doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
            timestampLastClick = 0;
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (isSingleEvent) {
                        onSingleClick();
                    }
                }
            };
        }

        @Override
        public void onClick(View v) {
            if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
                isSingleEvent = false;
                handler.removeCallbacks(runnable);
                onDoubleClick();
                return;
            }

            isSingleEvent = true;
            handler.postDelayed(runnable, DEFAULT_QUALIFICATION_SPAN);
            timestampLastClick = SystemClock.elapsedRealtime();
        }

        public abstract void onDoubleClick();
        public abstract void onSingleClick();
    }
}
