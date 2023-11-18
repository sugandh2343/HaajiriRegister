package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEventnAnnouncementHomeBinding;

public class EventnAnnouncementHome extends AppCompatActivity {
    ActivityEventnAnnouncementHomeBinding binding;
    long siteId;
    String siteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEventnAnnouncementHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");


        binding.btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(EventnAnnouncementHome.this,AddEventActivity.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                intent1.putExtra("Activity","Add");
                startActivity(intent1);

            }
        });
        binding.btnAddAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(EventnAnnouncementHome.this,AddAnnouncement.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                startActivity(intent1);

            }
        });
        binding.btnViewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(EventnAnnouncementHome.this,ViewEventsActivity.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                intent1.putExtra("Activity","View");
                startActivity(intent1);

            }
        });
        binding.btnUpdateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(EventnAnnouncementHome.this,ViewEventsActivity.class);
                intent1.putExtra("siteId",siteId);
                intent1.putExtra("siteName",siteName);
                intent1.putExtra("Activity","Update");
                startActivity(intent1);

            }
        });
    }
}