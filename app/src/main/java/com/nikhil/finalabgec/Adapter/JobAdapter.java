package com.nikhil.finalabgec.Adapter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.finalabgec.Model.JobModel;
import com.nikhil.finalabgec.R;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import www.sanju.motiontoast.MotionToast;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    List<JobModel> list;
    Context context1;
    FirebaseAuth auth;
    FirebaseUser user;
    View view;
    Dialog dialog;
    TextView yes, no;
    Boolean click=true;
    DatabaseReference reference, adminReference;
    int previousExpandedPosition = -1;
    int mExpandedPosition=-1;
    String value;
    boolean isAdmin = false;


    public JobAdapter(ArrayList<JobModel> list, Context context) {
        this.list = list;
        this.context1 = context;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("jobs");
        adminReference = FirebaseDatabase.getInstance().getReference().child("admins").child(user.getUid());
        adminReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getValue(Boolean.class) != null && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                    setIsAdmin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_job,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position < list.size()) {
            if (list.get(position).getImageLink()!=null) {
                try {
                    Uri uri = Uri.parse(list.get(position).getImageLink());
                    holder.image.setImageURI(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.company.setText(list.get(position).getCompany());
            holder.jobTitle.setText(list.get(position).getJobTitle());
            holder.jobLocation.setText(list.get(position).getJoblocation());
            holder.jobType.setText(list.get(position).getJobType());
            holder.job_function.setText(list.get(position).getJobFunction());
            holder.job_mode.setText(list.get(position).getJobMode());
            holder.level.setText(list.get(position).getExperience());

//            if(list.get(position).getNumber() == null){
//                holder.number.setText("NA");
//            }
//            else{
//                holder.number.setText(list.get(position).getNumber());
//            }

            if(list.get(position).getSalary() == null){
                holder.salary.setText("NA");
            } else{
                try{
                    int amount = Integer.parseInt(list.get(position).getSalary());
                    DecimalFormat formatter = new DecimalFormat("#,###,##0");
                    String myNumber = formatter.format(amount);
                    holder.salary.setText("₹ " + myNumber + "/M.");
                }
                catch( Exception e){
                    holder.salary.setText("NA");
                }
            }

        }

        if (list.get(position).getUid().equals(user.getUid()) || isAdmin){
            holder.delete.setVisibility(View.VISIBLE);

            holder.delete.setOnClickListener(v->{
                dialog = new Dialog(context1);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                yes = dialog.findViewById(R.id.yes);
                no = dialog.findViewById(R.id.no);

                yes.setOnClickListener(v1->{
                    dialog.dismiss();
                    reference.child(list.get(position).getPushkey()).removeValue();
                    Toast.makeText(context1, "Please refresh this page once to check!", Toast.LENGTH_SHORT).show();
                });


                no.setOnClickListener(v2->{
                    dialog.dismiss();
                });
            });
        }


        // TODO: Check this part
        final boolean isExpanded = position==mExpandedPosition;
        holder.layout1.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        if (isExpanded)
            previousExpandedPosition = holder.getAdapterPosition();
        holder.layout.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1:position;
            notifyItemChanged(previousExpandedPosition);
            notifyItemChanged(position);
        });

        holder.apply.setOnClickListener(v->{

            String url = list.get(position).getUrl().trim().toString();
            String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
            //Matching the given phone number with regular expression
            boolean result = url.matches(regex);
            
            if (check_URL(url)) {
                try {
                    Log.e("URL", list.get(position).getUrl() + "");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context1.startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (result) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {url});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Job Requirement");
                context1.startActivity(Intent.createChooser(intent, "Email via..."));
            }
            else {
                MotionToast.Companion.darkColorToast((Activity) view.getContext(),
                        "Error",
                        "Not a valid Link or valid Email",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context1, R.font.poppins));
            }

        });

        holder.share.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Download App");
            String message = "*Company Name* - " + list.get(position).getCompany() +"\n" + "*Job Title* - " + list.get(position).getJobTitle() +"\n" +
                    "*Location* - " + list.get(position).getJoblocation()  + "*Mode* - " + list.get(position).getJobMode() +"\n" + "*Salary* - " + list.get(position).getSalary() +"\n" +
                    "*Function* - " + list.get(position).getJobFunction() + "*\nExperience Level* - " + list.get(position).getExperience() + "\n*Type* - " + list.get(position).getJobType() +

                    "\n\n*Apply at -* " + list.get(position).getUrl() +
                    "\n\n⭐ *ABGEC* ⭐";
            intent.putExtra(Intent.EXTRA_TEXT, message);
            context1.startActivity(Intent.createChooser(intent, "Share using"));
        });

    }

    private void setIsAdmin() {
        isAdmin = true;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView company,jobType,jobTitle,jobLocation,apply,job_function,job_mode,salary,level,number;
        SimpleDraweeView image;
        ImageView delete,share;
        LinearLayout layout, layout1;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            company = itemView.findViewById(R.id.company_name);
            jobType = itemView.findViewById(R.id.job_type);
            jobTitle = itemView.findViewById(R.id.job_title);
            jobLocation = itemView.findViewById(R.id.job_location);
            image = itemView.findViewById(R.id.job_img);
            layout = itemView.findViewById(R.id.job_layout);
            apply = itemView.findViewById(R.id.apply);
            job_function = itemView.findViewById(R.id.job_function);
            number = itemView.findViewById(R.id.number);
            job_mode = itemView.findViewById(R.id.job_mode);
            salary = itemView.findViewById(R.id.salary);
            level = itemView.findViewById(R.id.level);
            delete = itemView.findViewById(R.id.delete);
            share = itemView.findViewById(R.id.share);
            layout1 = itemView.findViewById(R.id.layout_click);
        }
    }

    public static boolean check_URL(String str) {
        try {
            new URL(str).toURI();
            return true;
        }catch (Exception e) {
            return false;
        }
    }

}
