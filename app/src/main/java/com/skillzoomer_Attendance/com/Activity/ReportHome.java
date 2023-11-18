package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.databinding.ActivityReportHomeBinding;

public class ReportHome extends AppCompatActivity {
    ActivityReportHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityReportHomeBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        MyApplication my = new MyApplication( );
        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));


        binding.btnViewAllAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReportHome.this, ShowAttendanceActivity.class);
                intent.putExtra("Activity","ShowAttendance");
                startActivity(intent);
            }
        });
        binding.downloadAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getString("userDesignation","").equals("HR Manager")){
                    Intent intent=new Intent(ReportHome.this,ShowAttendanceActivity.class);
                    intent.putExtra("Activity","DownloadAttendance");
                    startActivity(intent);

                }else{
                    Toast.makeText(ReportHome.this, getString(R.string.cannot_download_report), Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.btnCompileList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReportHome.this, ShowCompileListActivity.class);
                intent.putExtra("Activity","ShowAttendance");
                startActivity(intent);

            }
        });
        binding.downloadCompile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getString("userDesignation","").equals("HR Manager")){
                    Intent intent=new Intent(ReportHome.this,ShowCompileListActivity.class);
                    intent.putExtra("Activity","DownloadAttendance");
                    startActivity(intent);

                }else{
                    Toast.makeText(ReportHome.this, getString(R.string.cannot_download_report), Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.btnViewAdvancesReprt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReportHome.this, ShowPaymentActivity.class);
                intent.putExtra("Activity","ShowAttendance");
                startActivity(intent);

            }
        });
        binding.downloadAdvances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sharedpreferences.getString("userDesignation","").equals("HR Manager")){
                    Intent intent=new Intent(ReportHome.this,ShowPaymentActivity.class);
                    intent.putExtra("Activity","DownloadAttendance");
                    startActivity(intent);

                }else{
                    Toast.makeText(ReportHome.this,getString(R.string.cannot_download_report) , Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReportHome.this, timelineActivity.class));
//                if(selected_option==2){
//                    Intent intent=new Intent(ReportHome.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(ReportHome.this,ReportHome.class));
//                }
//
            }
        });
        binding.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReportHome.this, ActivityProfile.class));
            }
        });

        binding.llWorkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add Activity work here
                startActivity(new Intent(ReportHome.this, ViewMasterDataSheet.class));
//                if((selected_option==2)||(selected_option==1)){
//                    startActivity(new Intent(ReportHome.this,ViewMasterDataSheet.class));
//                }else{
//                    startActivity(new Intent(ReportHome.this,AdminSiteActivity.class));
//                }

            }
        });
        binding.llRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportHome.this, AdvancesHomeActivity.class);
                intent.putExtra("SiteSpinner", true);
                startActivity(intent);
//                if(selected_option==1){
//                    Intent intent=new Intent(ReportHome.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(ReportHome.this,RequestListActivity.class));
//                }

            }
        });
        if(sharedpreferences.getString("userDesignation","").equals("Supervisor")){
            binding.workActivityList.setVisibility(View.GONE);
            binding.llTeamMember.setVisibility(View.GONE);
            binding.bottomToolbar.setVisibility(View.GONE);
        }else{
            binding.workActivityList.setVisibility(View.VISIBLE);
            binding.llTeamMember.setVisibility(View.VISIBLE);
            binding.bottomToolbar.setVisibility(View.VISIBLE);

        }
        binding.workActivityList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReportHome.this, ChattingActivity.class));
            }
        });
        binding.btnDayBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReportHome.this, DayBookListActivity.class);
                intent.putExtra("Activity","ShowAttendance");
                startActivity(intent);
            }
        });

        binding.btnLabourReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReportHome.this, ShowLabourData.class);
                intent.putExtra("Activity","ShowAttendance");
                startActivity(intent);
            }
        });

        binding.downloadLabour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReportHome.this, ShowLabourData.class);
                intent.putExtra("Activity","DownloadAttendance");
                startActivity(intent);
            }
        });
        binding.downloadDayBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getString("userDesignation","").equals("HR Manager")){
                    Intent intent=new Intent(ReportHome.this,DayBookListActivity.class);
                    intent.putExtra("Activity","DownloadAttendance");
                    startActivity(intent);

                }else{
                    Toast.makeText(ReportHome.this,getString(R.string.cannot_download_report) , Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnAssociateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReportHome.this, AssociateDetailsActivity.class);
                intent.putExtra("Activity","ShowAttendance");
                startActivity(intent);
            }
        });
        binding.downloadAssociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getString("userDesignation","").equals("HR Manager")){
                    Intent intent=new Intent(ReportHome.this,AssociateDetailsActivity.class);
                    intent.putExtra("Activity","DownloadAttendance");
                    startActivity(intent);

                }else{
                    Toast.makeText(ReportHome.this,getString(R.string.cannot_download_report) , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            startActivity(new Intent(ReportHome.this,MemberTimelineActivity.class));
            finish();
        }else{
            startActivity(new Intent(ReportHome.this,timelineActivity.class));
            finish();
        }
    }
}