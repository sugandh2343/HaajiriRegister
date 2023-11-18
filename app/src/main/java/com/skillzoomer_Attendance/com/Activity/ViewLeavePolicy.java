package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterLeavePolicy;
import com.skillzoomer_Attendance.com.Model.ModelLeavePolicy;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityViewLeavePolicyBinding;

import java.util.ArrayList;

public class ViewLeavePolicy extends AppCompatActivity {
    ActivityViewLeavePolicyBinding binding;
    long siteId;
    String siteName;
    private ArrayList<ModelLeavePolicy> leavePolicyArrayList;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityViewLeavePolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");
        leavePolicyArrayList=new ArrayList<>();
        getPolicyCount();
    }

    private void getPolicyCount() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId));
        reference.child("Policy")
                .child("Leave").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            binding.llPolicyShow.setVisibility(View.VISIBLE);
                            binding.noDataToShow.setVisibility(View.GONE);
                            leavePolicyArrayList.clear();
                            for(DataSnapshot ds:snapshot.getChildren()){
                                ModelLeavePolicy modelLeavePolicy=ds.getValue(ModelLeavePolicy.class);
                                leavePolicyArrayList.add(modelLeavePolicy);

                            }
                            AdapterLeavePolicy adapterLeavePolicy=new AdapterLeavePolicy(ViewLeavePolicy.this,leavePolicyArrayList);
                            binding.rvLeavePolicy.setAdapter(adapterLeavePolicy);
                        }else{
                            binding.llPolicyShow.setVisibility(View.GONE);
                            binding.noDataToShow.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}