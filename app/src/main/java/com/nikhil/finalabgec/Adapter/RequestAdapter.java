package com.nikhil.finalabgec.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.finalabgec.Model.UnverifiedDataModel;
import com.nikhil.finalabgec.R;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    Context context;
    ArrayList<UnverifiedDataModel> list;
    OnApproveClickListener listener;

    public RequestAdapter(Context context,
                          OnApproveClickListener onApproveClickListener,
                          ArrayList<UnverifiedDataModel> list) {
        this.context = context;
        this.list = list;
        this.listener = onApproveClickListener;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {

        String uName = list.get(position).getName();
        String uUid = list.get(position).getUid();
        String uBranch = list.get(position).getBranch();
        String uBatch = list.get(position).getBatch();
        String uEmail = list.get(position).getEmail();
        boolean uIsAdmin = list.get(position).getisAdmin();


        holder.name.setText(list.get(position).getName());
        holder.branch.setText(list.get(position).getBranch());
        holder.batch.setText(list.get(position).getBatch());
        holder.email.setText(list.get(position).getEmail());

        holder.approve.setOnClickListener(
                v -> {
                    listener.onApproveClick(new UnverifiedDataModel(uUid, uName, uBranch, uBatch, uEmail, uIsAdmin), position);
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,branch,batch,email;
        Button approve;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView);
            branch = itemView.findViewById(R.id.branchTextView);
            batch = itemView.findViewById(R.id.batchTextView);
            email = itemView.findViewById(R.id.emailTextView);
            approve = itemView.findViewById(R.id.approveButton);
        }

    }

    public interface OnApproveClickListener {
        void onApproveClick(UnverifiedDataModel user, int position);
    }

}