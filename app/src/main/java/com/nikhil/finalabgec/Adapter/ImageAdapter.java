package com.nikhil.finalabgec.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nikhil.finalabgec.Fragment.ViewPhoto;
import com.nikhil.finalabgec.Model.ImageModel;
import com.nikhil.finalabgec.R;

import java.util.ArrayList;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    ArrayList<ImageModel> list;
    Context context;

    public ImageAdapter(Context context,ArrayList<ImageModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < list.size()) {
            try {
                Uri uri = Uri.parse(list.get(position).getImagepath());
                holder.image.setImageURI(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (list.get(position).getText() != null) {
                holder.loadText.setText(list.get(position).getText());
            }
        }

        holder.cardView.setOnClickListener(v->{
            ViewPhoto fragobj = new ViewPhoto();
            Bundle bundle = new Bundle();

            bundle.putString("image_sending_profile", list.get(position).getImagepath());

            fragobj.setArguments(bundle);
            ((FragmentActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.change_layout, fragobj)
                    .addToBackStack(null)
                    .commit();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView image;
        CardView cardView;
        TextView loadText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.image);
            image  = itemView.findViewById(R.id.idIVImage);
            loadText = itemView.findViewById(R.id.loadText);
        }
    }
}