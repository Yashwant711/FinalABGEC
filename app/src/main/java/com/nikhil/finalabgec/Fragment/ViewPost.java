package com.nikhil.finalabgec.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nikhil.finalabgec.R;

import java.util.Objects;

import www.sanju.motiontoast.MotionToast;

public class ViewPost extends Fragment {

    View view;
    Context contextNullSafe;
    ImageView back,share;
    TextView delete,link,des,nam,date,tit,lik;

    boolean isadmin=false;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    SimpleDraweeView imageNote;
    LinearLayout like_layout;
    String name,image_link,title,likes,uid,addtostack,description,dat,pushkey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_post, container, false);
        back = view.findViewById(R.id.imageView4);
        link = view.findViewById(R.id.link);
        des = view.findViewById(R.id.description);
        delete = view.findViewById(R.id.delete);
        tit = view.findViewById(R.id.title);
        nam = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        lik = view.findViewById(R.id.like_count);
        imageNote = view.findViewById(R.id.imageNote);
        like_layout = view.findViewById(R.id.like_layout);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        share = view.findViewById(R.id.share);
        reference = FirebaseDatabase.getInstance().getReference().child("posts");


        if (contextNullSafe == null) getContextNullSafety();

        try {
            assert getArguments() != null;
            addtostack=getArguments().getString("sending_user_from_home");
            uid = getArguments().getString("uid_sending_post");
            name = getArguments().getString("name");
            image_link = getArguments().getString("image");
            pushkey = getArguments().getString("pushkey");
            title = getArguments().getString("title");
            description = getArguments().getString("description");
            likes = getArguments().getString("like");
            dat = getArguments().getString("date");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Uri uri = Uri.parse(image_link);
            imageNote.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!name.equals(""))
            nam.setText(name);

        if (!description.equals(""))
            des.setText(description);

        if (!title.equals(""))
            tit.setText(title);

        if (!dat.equals(""))
            date.setText(dat);

        if (!likes.equals("0")) {
            like_layout.setVisibility(View.VISIBLE);
            lik.setText(likes);
        }




        //  Log.e("value_uid",uid_of_user);
        if (uid.equals(user.getUid()) || check_for_admin()){
            delete.setVisibility(View.VISIBLE);

            delete.setOnClickListener(v->{
                deletePost();
            });
        }
        else
            delete.setVisibility(View.GONE);

        back.setOnClickListener(v->{
            back();
        });

        imageNote.setOnClickListener(v->{
            ViewPhoto view_photo = new ViewPhoto();
            Bundle args = new Bundle();
            args.putString("image_sending_profile", image_link);
            view_photo.setArguments(args);
            ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.view_post_layout, view_photo)
                    .addToBackStack(null)
                    .commit();
        });

        share.setOnClickListener(v->{
            String str_title ="*"+title+"*"+"\n\n"+"Created by : "+name+
                    "\n"+"Date : "+date+
                    "\n"+"\n\n"+"*View* :"+"https://abgec.android/nikhil/"+uid+"/"+pushkey ; //Text to be shared
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, str_title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getContextNullSafety().getPackageName());
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });


        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);


        return view;
    }

    private void back() {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(ViewPost.this).commit();
    }

    private void deletePost(){
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel=dialog.findViewById(R.id.no);
        TextView yes=dialog.findViewById(R.id.yes);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        cancel.setOnClickListener(vi-> dialog.dismiss());
        yes.setOnClickListener(vi-> {
            String imagepath = "Post/" + title + pushkey + ".png";
            MotionToast.Companion.darkColorToast(requireActivity(),
                    "Deleted!!",
                    "Deleted successfully. Swipe down to refresh",
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireActivity(), R.font.poppins));

            if (image_link != null) {
                StorageReference storageReference =
                        FirebaseStorage.getInstance().getReference().child(imagepath);
                storageReference.delete();
            }

            reference.child(pushkey).removeValue();
            dialog.dismiss();
            back();
        });
    }

    private boolean check_for_admin(){
        SharedPreferences pref = getContextNullSafety().getSharedPreferences("our_user?", MODE_PRIVATE);
        return pref.getBoolean("admin", true);
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