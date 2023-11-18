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
import com.skillzoomer_Attendance.com.Adapter.AdapterEvent;
import com.skillzoomer_Attendance.com.Model.ModelEvent;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityViewEventsBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewEventsActivity extends AppCompatActivity {
    ActivityViewEventsBinding binding;
    private ArrayList<ModelEvent> eventArrayList;
    private ArrayList<ModelEvent> eventArrayListSorted;
    long siteId;
    String siteName;
    String todayDate;

    String Activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityViewEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventArrayList=new ArrayList<>();
        eventArrayListSorted=new ArrayList<>();
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");
        Activity=intent.getStringExtra("Activity");
        if(Activity.equals("Update")){
            binding.txtHeading.setText("Click on the event to update the details");
        }

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId));
        reference.child("Events").child("Event").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventArrayList.clear();
                eventArrayListSorted.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelEvent modelEvent=ds.getValue(ModelEvent.class);
                    eventArrayList.add(modelEvent);

                }
                if(eventArrayList.size()>0){
                    binding.txtNoEvent.setVisibility(View.GONE);
                    binding.rvEventList.setVisibility(View.VISIBLE);

                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    todayDate = df.format(c);

                    for(int i=0;i<eventArrayList.size();i++){
                        try {
                            Date fDate = df.parse(todayDate);
                            Date tDate = df.parse(eventArrayList.get(i).getEventDate());
                            if(todayDate.equals(eventArrayList.get(i).getEventDate())){
                                eventArrayListSorted.add(eventArrayList.get(i));
                                eventArrayListSorted.add(new ModelEvent(eventArrayList.get(i).getEventId(),
                                        eventArrayList.get(i).getEventName(),eventArrayList.get(i).getEventDate(),eventArrayList.get(i).getEventStart(),eventArrayList.get(i).getEventEnd()
                                ,eventArrayList.get(i).getEventCategory(),eventArrayList.get(i).getAddedBy(),"Today"));
                            }
                        }catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    for(int i=0;i<eventArrayList.size();i++){
                        try {
                            Date fDate = df.parse(todayDate);
                            Date tDate = df.parse(eventArrayList.get(i).getEventDate());
                            if(fDate.before(tDate)){
                                eventArrayListSorted.add(new ModelEvent(eventArrayList.get(i).getEventId(),
                                        eventArrayList.get(i).getEventName(),eventArrayList.get(i).getEventDate(),eventArrayList.get(i).getEventStart(),eventArrayList.get(i).getEventEnd()
                                        ,eventArrayList.get(i).getEventCategory(),eventArrayList.get(i).getAddedBy(),"Upcoming"));
                            }
                        }catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    for(int i=0;i<eventArrayList.size();i++){
                        try {
                            Date fDate = df.parse(todayDate);
                            Date tDate = df.parse(eventArrayList.get(i).getEventDate());
                            if(tDate.before(fDate)){
                                eventArrayListSorted.add(new ModelEvent(eventArrayList.get(i).getEventId(),
                                        eventArrayList.get(i).getEventName(),eventArrayList.get(i).getEventDate(),eventArrayList.get(i).getEventStart(),eventArrayList.get(i).getEventEnd()
                                        ,eventArrayList.get(i).getEventCategory(),eventArrayList.get(i).getAddedBy(),"Past"));
                            }
                        }catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    AdapterEvent adapterEvent=new AdapterEvent(ViewEventsActivity.this,eventArrayListSorted,Activity,siteId,siteName);
                    binding.rvEventList.setAdapter(adapterEvent);
                }else{
                    binding.txtNoEvent.setVisibility(View.VISIBLE);
                    binding.rvEventList.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1=new Intent(ViewEventsActivity.this,EventnAnnouncementHome.class);
        intent1.putExtra("siteId",siteId);
        intent1.putExtra("siteName",siteName);
        startActivity(intent1);
    }
}