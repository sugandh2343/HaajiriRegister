package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityLeavePolicyHomeBinding;

public class LeavePolicyHome extends AppCompatActivity {
    ActivityLeavePolicyHomeBinding binding;
    long siteId;
    String siteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLeavePolicyHomeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");

        binding.btnAddNewLeavePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(LeavePolicyHome.this,AddNewPolicy.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });

        binding.btnLeavePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(LeavePolicyHome.this,ViewLeavePolicy.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });
    }
}