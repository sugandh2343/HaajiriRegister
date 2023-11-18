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
import com.skillzoomer_Attendance.com.Adapter.AdapterAdminSite;
import com.skillzoomer_Attendance.com.Adapter.AdapterAdminSiteList;
import com.skillzoomer_Attendance.com.Model.ModelAdminSite;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityGetSingleUserDetailBinding;

import java.util.ArrayList;

public class GetSingleUserDetail extends AppCompatActivity {
    ActivityGetSingleUserDetailBinding binding;
    String uid;

    private ArrayList<ModelAdminSite> adminSiteArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGetSingleUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent= getIntent();
        binding.llSiteList.setVisibility(View.GONE);
        uid=intent.getStringExtra("uid");
        binding.txtView.setVisibility(View.GONE);
        adminSiteArrayList=new ArrayList<>();
        binding.txtUserId.setText(intent.getStringExtra("userId"));
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.child(uid).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.txtSiteCount.setText(""+snapshot.getChildrenCount());
                    if(snapshot.getChildrenCount()>0){
                        binding.txtView.setVisibility(View.VISIBLE);
                    }else{
                        binding.txtView.setVisibility(View.GONE);
                    }
                }else{
                    binding.txtView.setVisibility(View.GONE);
                    binding.txtSiteCount.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llSiteList.setVisibility(View.VISIBLE);
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                reference.child(uid).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adminSiteArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelAdminSite modelAdminSite=new ModelAdminSite();
                            modelAdminSite.setSiteId(ds.child("siteId").getValue(long.class));
                            modelAdminSite.setSiteName(ds.child("siteName").getValue(String.class));
                            if(ds.child("Members").exists()){
                                modelAdminSite.setTm(ds.child("Members").getChildrenCount());
                            }else{
                                modelAdminSite.setTm(0);
                            }
                            if(ds.child("Attendance").exists()){
                                modelAdminSite.setAttendanceCount(ds.child("Attendance").getChildrenCount());
                            }else{
                                modelAdminSite.setAttendanceCount(0);
                            }
                            if(ds.child("Labours").exists()){
                                modelAdminSite.setLabourCount(ds.child("Labours").getChildrenCount());
                            }else{
                                modelAdminSite.setLabourCount(0);
                            }
                            adminSiteArrayList.add(modelAdminSite);
                        }
                        AdapterAdminSiteList adapterAdminSiteList=new AdapterAdminSiteList(GetSingleUserDetail.this,adminSiteArrayList);
                        binding.rvSiteList.setAdapter(adapterAdminSiteList);
                        binding.rvSiteList.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}