package com.nikhil.finalabgec;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhil.finalabgec.Adapter.RequestAdapter;
import com.nikhil.finalabgec.Model.UnverifiedDataModel;

import java.util.ArrayList;

public class AdminPanel extends AppCompatActivity {

    TextView batchYearTv;
    Button editButton;
    RecyclerView recyclerView;
    FrameLayout overlayLayout;
    boolean flag1 = false;
    boolean flag2 = false;
    DatabaseReference batchReference, unverifiedReference, userReference;
    ArrayList<UnverifiedDataModel> requestList;
    int size = 0;
    RequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        batchYearTv = findViewById(R.id.batchCutoffTextView);
        editButton = findViewById(R.id.editButton);
        recyclerView = findViewById(R.id.verificationRequestsRecyclerView);
        overlayLayout = findViewById(R.id.overlay_layout);

        overlayLayout.setVisibility(View.VISIBLE);
        requestList = new ArrayList<>();
        adapter = new RequestAdapter(this,
                this::showApproveDialog,
                requestList
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userReference = FirebaseDatabase.getInstance().getReference("users");
        unverifiedReference = FirebaseDatabase.getInstance().getReference("Unverified");
        unverifiedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(DataSnapshot uid: snapshot.getChildren()){
                    UnverifiedDataModel unverifiedDataModel = uid.getValue(UnverifiedDataModel.class);
                    if(unverifiedDataModel != null){
                        requestList.add(unverifiedDataModel);
                        adapter.notifyItemInserted(size);
                        size += 1;
                    }
                }
                setFlags();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setBatchYear();
        
        editButton.setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setHint("Enter new Batch Cutoff");

            new AlertDialog.Builder(this)
                    .setTitle("Update Batch Year")
                    .setView(input)
                    .setPositiveButton("Update", (dialog, which) ->{
                        String newCutoff = input.getText().toString().trim();
                        if (!newCutoff.isEmpty()) {
                            updateBatchYear(newCutoff);
                        } else {
                            Toast.makeText(this, "Batch year cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        
    }

    private void updateBatchYear(String year) {
        batchReference = FirebaseDatabase.getInstance().getReference("BatchCutoff");
        batchReference.setValue(year)
                .addOnSuccessListener(aVoid -> {
                    batchYearTv.setText(year);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update batch year: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setBatchYear(){
        batchReference = FirebaseDatabase.getInstance().getReference("BatchCutoff");
        batchReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String year = snapshot.getValue(String.class);
                if(year == null){
                    year = "9999";
                }
                setFlags(year);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFlags(String year){
        batchYearTv.setText(year);
        flag1 = true;
        if(flag2){
            overlayLayout.setVisibility(View.GONE);
        }
    }

    private void setFlags(){
        flag2 = true;
        if(flag1){
            overlayLayout.setVisibility(View.GONE);
        }
    }

    private void showApproveDialog(UnverifiedDataModel user, int position){
        new AlertDialog.Builder(this)
                .setTitle("Approve Request")
                .setMessage("Are you sure you want to approve this request?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    approve(user, position);
                    overlayLayout.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    removeUnverifiedUser(user, position);
                    overlayLayout.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                })
                .show();
    }

    private void approve(UnverifiedDataModel user, int position){
        try{
            userReference.child(user.getUid()).setValue(user)
                    .addOnSuccessListener(aVoid ->{
                        Toast.makeText(this, "User approved successfully", Toast.LENGTH_SHORT).show();
                        sendEmail(user.getEmail());
                        removeUnverifiedUser(user, position);
                    })
                    .addOnFailureListener(e ->{
                        Toast.makeText(this, "Failed to approve user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        overlayLayout.setVisibility(View.GONE);
                    });
        }
        catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            overlayLayout.setVisibility(View.GONE);
        }
    }

    private void removeUnverifiedUser(UnverifiedDataModel user, int position){
        unverifiedReference.child(user.getUid()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    requestList.remove(position);
                    adapter.notifyItemRemoved(position);
                    overlayLayout.setVisibility(View.GONE);
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(this, "Failed to remove user, Please contact developer before approving anyone else", Toast.LENGTH_SHORT).show();
                    overlayLayout.setVisibility(View.GONE);
                });
    }

    private void sendEmail(String emailId) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        intent.putExtra(Intent.EXTRA_SUBJECT,
                "ABGEC Verification successful");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear User,\n"
        + "We are glad to inform you that your account has been verified. You can now log into your account.\n"
        + "Thankyou");
        try {
            startActivity(Intent.createChooser(intent, "Choose an Email Client"));
        } catch (android.content.ActivityNotFoundException ex)
        {
            // Handle the case where no email clients are available
            Toast.makeText(this, "No email clients installed", Toast.LENGTH_SHORT).show();
        }
    }

}