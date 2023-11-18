package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterAdminSite;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.databinding.ActivityAdminSiteBinding;

import java.util.ArrayList;

public class AdminSiteActivity extends AppCompatActivity {
//    ActivityAdminSiteBinding binding;
//    private ArrayList<ModelSite> siteArrayList;
//    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding= ActivityAdminSiteBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        siteArrayList=new ArrayList<>();
//        firebaseAuth=FirebaseAuth.getInstance();
//        getSiteList();

    }
//    private void getSiteList() {
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Site");
//        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                Log.e("DataChange","Yes");
//                siteArrayList.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    ModelSite modelSite = ds.getValue(ModelSite.class);
//                    siteArrayList.add(modelSite);
//
//
//
//
//                }
//                if(siteArrayList.size()>0) {
//                    binding.txtNoSite.setVisibility(View.GONE);
//                    binding.rvSiteList.setVisibility(View.VISIBLE);
//                    Log.e("ModelArrayList" , "" + siteArrayList.size());
//                    Log.e("ModelArrayList" , "" + siteArrayList.get(0).getSiteName());
//                    Log.e("ModelArrayList" , "" + siteArrayList.get(0).getSiteCreatedDate());
//                    AdapterAdminSite adapterTimeline = new AdapterAdminSite(AdminSiteActivity.this , siteArrayList);
//                    binding.rvSiteList.setAdapter(adapterTimeline);
//                }else if(siteArrayList.size()==0){
//                    binding.txtNoSite.setVisibility(View.VISIBLE);
//                    binding.rvSiteList.setVisibility(View.GONE);
//                    binding.cardHeading.setVisibility(View.VISIBLE);
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}