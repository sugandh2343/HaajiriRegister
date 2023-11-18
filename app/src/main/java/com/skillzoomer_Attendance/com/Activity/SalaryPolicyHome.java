package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivitySalaryPolicyHomeBinding;

public class SalaryPolicyHome extends AppCompatActivity {
    ActivitySalaryPolicyHomeBinding binding;
    long siteId;
    String siteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySalaryPolicyHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");

        binding.btnAddNewSalaryPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("SPHsiteId",""+siteId);
                Intent intent1=new Intent(SalaryPolicyHome.this,AddNewSalaryPolicy.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });
    }
}