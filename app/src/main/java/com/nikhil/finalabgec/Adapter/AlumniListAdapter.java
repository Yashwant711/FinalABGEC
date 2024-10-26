package com.nikhil.finalabgec.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.finalabgec.Fragment.EditList;
import com.nikhil.finalabgec.Model.alumnilistModel;
import com.nikhil.finalabgec.R;

import java.util.ArrayList;

public class AlumniListAdapter extends RecyclerView.Adapter<AlumniListAdapter.ViewHolder> {

    Context context;
    ArrayList<alumnilistModel> list ;
    DatabaseReference user_ref,reference;
    FirebaseAuth auth;
    FirebaseUser user;

    private String lastVisibleKey = "";
    Boolean click = true;

    public AlumniListAdapter(Context context, ArrayList<alumnilistModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();

//        user_ref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
//        reference = FirebaseDatabase.getInstance().getReference().child("ALUMNI_DATA").child("data");
     /*   query = FirebaseDatabase.getInstance().getReference().child("ALUMNI_DATA").child("data")
                .orderByKey()
                .limitToFirst(10);*/

        if (list.get(position).getName().equals(""))
            holder.name.setVisibility(View.GONE);

        if (list.get(position).getBranch().equals(""))
            holder.branch.setVisibility(View.GONE);

        if (list.get(position).getState().equals(""))
            holder.state.setVisibility(View.GONE);

        if (list.get(position).getCountry().equals(""))
            holder.country.setVisibility(View.GONE);

        if (list.get(position).getCity().equals(""))
            holder.city.setVisibility(View.GONE);

        if (list.get(position).getDesignation().equals(""))
            holder.designation.setVisibility(View.GONE);

        if (list.get(position).getEmail().equals(""))
            holder.email.setVisibility(View.GONE);

        if (list.get(position).getMobileNumber().equals(""))
            holder.mobile.setVisibility(View.GONE);

        if (list.get(position).getPassoutYear().equals(""))
            holder.passout.setVisibility(View.GONE);

        if (list.get(position).getOrganisation().equals(""))
            holder.organisation.setVisibility(View.GONE);



      /*  if (position == getItemCount() - 1) {
            // This is the last item in the list, so we need to load more data
            Query nextQuery = query.startAfter(lastVisibleKey).limitToFirst(10);
            nextQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Add the new data to the existing list
                    // ...
                    if (snapshot.exists()){
                        if (snapshot.child("id").getValue().equals("Student")){
                            holder.mobile.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        }
*/
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds:snapshot.getChildren()) {
//                    reference.child(ds.getKey()).child("pushkey").setValue(ds.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
     /*   user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("id").getValue().equals("Student")){
                        holder.mobile.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            

            }
        });
*/


        holder.layout.setOnClickListener(v->{
            if (click){
                holder.hide_layout.setVisibility(View.VISIBLE);
                click = false;
            }
            else {
                holder.hide_layout.setVisibility(View.GONE);
                click = true;
            }
        });

        if (list.get(position).getCity().equals("") && list.get(position).getCountry().equals("") && list.get(position).getState().equals(""))
            holder.location.setVisibility(View.GONE);


//        if (("+91" + list.get(position).getMobileNumber()).equals(user.getPhoneNumber()))
//            holder.edit.setVisibility(View.VISIBLE);

        holder.edit.setOnClickListener(v->{
            EditList editList = new EditList();
            Bundle args = new Bundle();
            args.putString("name",list.get(position).getName());
            args.putString("branch", list.get(position).getBranch());
            args.putString("passout", list.get(position).getPassoutYear());
            args.putString("organisation", list.get(position).getOrganisation());
            args.putString("designation", list.get(position).getDesignation());
            args.putString("number", list.get(position).getMobileNumber());
            args.putString("city", list.get(position).getCity());
            args.putString("state", list.get(position).getState());
            args.putString("country", list.get(position).getCountry());
            args.putString("email", list.get(position).getEmail());
            args.putString("pushkey", list.get(position).getPushkey());
            editList.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.layout, editList)
                    .addToBackStack(null)
                    .commit();
        });

        holder.name.setText(list.get(position).getName());
        holder.branch.setText(list.get(position).getBranch());
        holder.city.setText(list.get(position).getCity().toLowerCase());
        holder.designation.setText(list.get(position).getDesignation());
        holder.country.setText(list.get(position).getCountry());
        holder.email.setText(list.get(position).getEmail());
        holder.mobile.setText(list.get(position).getMobileNumber());
        holder.organisation.setText(list.get(position).getOrganisation());
        holder.passout.setText(list.get(position).getPassoutYear());
        holder.state.setText(list.get(position).getState().toLowerCase());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,branch,city,designation,country,email,mobile,organisation,passout,state,edit;
        LinearLayout hide_layout,layout;
        ImageView location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            branch = itemView.findViewById(R.id.branch);
            passout = itemView.findViewById(R.id.passout);
            organisation = itemView.findViewById(R.id.organisation);
            designation = itemView.findViewById(R.id.designation);
            mobile = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            city = itemView.findViewById(R.id.city);
            country = itemView.findViewById(R.id.country);
            state = itemView.findViewById(R.id.state);
            hide_layout = itemView.findViewById(R.id.hide_layout);
            layout = itemView.findViewById(R.id.layout);
            location = itemView.findViewById(R.id.location);
            edit = itemView.findViewById(R.id.personal_btn);
        }
    }

}
