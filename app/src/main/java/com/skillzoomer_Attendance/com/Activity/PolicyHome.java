package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityPolicyHomeBinding;

public class PolicyHome extends AppCompatActivity {
    ActivityPolicyHomeBinding binding;
    long siteId;
    String siteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPolicyHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");
        binding.btnLeavePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(PolicyHome.this,LeavePolicyHome.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });

        binding.btnAddSalaryPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(PolicyHome.this,SalaryPolicyHome.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });
        binding.btnHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(PolicyHome.this,AddHoliday.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });
    }
}