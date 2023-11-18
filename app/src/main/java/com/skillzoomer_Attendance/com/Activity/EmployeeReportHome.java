package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAllEmployeeAttendanceReportBinding;
import com.skillzoomer_Attendance.com.databinding.ActivityEmployeeAttendanceReportBinding;
import com.skillzoomer_Attendance.com.databinding.ActivityEmployeeReportHomeBinding;

public class EmployeeReportHome extends AppCompatActivity {
    ActivityEmployeeReportHomeBinding binding;
    long siteId;
    String siteName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeReportHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        siteId = intent.getLongExtra("siteId", 0);
        siteName = intent.getStringExtra("siteName");
        progressDialog=new ProgressDialog(this);
        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("Employee")){
            binding.btnAllAttendanceReport.setVisibility(View.GONE);
        }
        binding.btnAllAttendanceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(EmployeeReportHome.this,AllEmployeeAttendanceReport.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);
            }
        });
        binding.btnAttendanceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CLICKEDJHJHVJHVJG","Clicked");
                startActivity(new Intent(EmployeeReportHome.this,EmployeeAttendanceReport.class));
            }
        });
    }
}