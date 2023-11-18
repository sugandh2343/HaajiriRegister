package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterLeave;
import com.skillzoomer_Attendance.com.Adapter.AdapterTimeline1;
import com.skillzoomer_Attendance.com.Model.ModelLeave;
import com.skillzoomer_Attendance.com.Model.ModelWorkPlace;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityShowLeaveRequestBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ShowLeaveRequestActivity extends AppCompatActivity {
    ActivityShowLeaveRequestBinding binding;
    FirebaseAuth firebaseAuth;
    long siteId;
    String siteName;
    private ArrayList<ModelLeave> leaveArrayList;
    private ArrayList<ModelLeave> filterLeaveArrayList;
    private ArrayList<ModelWorkPlace> siteArrayList;
    String currentDate,selectedOption="Future";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityShowLeaveRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        leaveArrayList=new ArrayList<>();
        siteArrayList=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        filterLeaveArrayList=new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new android.icu.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("HR Manager")){
            binding.llMain.setVisibility(View.GONE);
            binding.spinnerSite.setVisibility(View.VISIBLE);
            getSiteList();
        }else{
            getLeaveData();
            binding.llMain.setVisibility(View.VISIBLE);
            binding.spinnerSite.setVisibility(View.GONE);
        }
        binding.rbApproved.setChecked(false);
        binding.rbFuture.setChecked(true);
        binding.rbPast.setChecked(false);

        binding.spinnerSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view , int position , long id) {
                if(position>0){
                    binding.llMain.setVisibility(View.VISIBLE);
                    siteId=siteArrayList.get(position).getSiteId();
                    getLeaveData();
                }else{
                    binding.llMain.setVisibility(View.GONE);
                    binding.spinnerSite.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        binding.rbFuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbApproved.setChecked(false);
                binding.rbFuture.setChecked(true);
                binding.rbPast.setChecked(false);
                selectedOption="Future";
                if(siteArrayList.size()>0){
                    getFilteredList();
                }else{
                    getLeaveData();
                }
            }
        });
        binding.rbPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbApproved.setChecked(false);
                binding.rbFuture.setChecked(false);
                binding.rbPast.setChecked(true);
                selectedOption="Past";
                if(siteArrayList.size()>0){
                    getFilteredList();
                }else{
                    getLeaveData();
                }
            }
        });
        binding.rbApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rbApproved.setChecked(true);
                binding.rbFuture.setChecked(false);
                binding.rbPast.setChecked(false);
                selectedOption="Approved";
                if(siteArrayList.size()>0){
                    getFilteredList();
                }else{
                    getLeaveData();
                }
            }
        });

    }
    private void getSiteList() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid())
                .child("Industry").child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName","")).child("Site");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Log.e("SiteArrayList12","IP"+ds.child("industryPosition").getValue(long.class));
                    Log.e("SiteArrayList12",""+getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));

                    if(ds.child("industryPosition").getValue(long.class)!=null &&
                            ds.child("industryPosition").getValue(long.class)==getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0)){
                        ModelWorkPlace modelSite=ds.getValue(ModelWorkPlace.class);
                        siteArrayList.add(modelSite);
                    }
                    siteArrayList.add(0,new ModelWorkPlace("Select Workplace"));
                    SiteSpinnerAdapter siteSpinnerAdapter=new SiteSpinnerAdapter();
                    binding.spinnerSite.setAdapter(siteSpinnerAdapter);

                }

                Log.e("SiteArrayList",""+siteArrayList.size());
                Log.e("SiteArrayList","ISP"+getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getLeaveData() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site")
                .child(String.valueOf(siteId)).child("LeaveRequest").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        leaveArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelLeave modelLeave=ds.getValue(ModelLeave.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            Boolean f = true;
                            leaveArrayList.add(modelLeave);



                        }
                        getFilteredList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFilteredList() {
        filterLeaveArrayList.clear();

        for (int i = 0; i < leaveArrayList.size(); i++) {
            if (selectedOption.equals("Future")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                Boolean f = true;
                try {
                    Date fDate = dateFormat.parse(currentDate);
                    Date tDate = dateFormat.parse(leaveArrayList.get(i).getLeaveStartDate());
//                    Log.e("JINDIN",leaveArrayList.get(i).getLeaveStatus());
                    Log.e("JINDIN",""+currentDate);
                    Log.e("JINDIN",""+(fDate==tDate));
                    if ((fDate.before(tDate)|| currentDate.equals(leaveArrayList.get(i).getLeaveStartDate())) && leaveArrayList.get(i).getLeaveStatus().equals("Requested")) {
                        Log.e("Flal",""+leaveArrayList.get(i).getLeaveappliedByName());

                        filterLeaveArrayList.add(leaveArrayList.get(i));
                    }

                } catch (ParseException e) {

                    throw new RuntimeException(e);

                }

            }else if(selectedOption.equals("Past")){
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                Boolean f = true;
                try {
                    Date fDate = dateFormat.parse(currentDate);
                    Date tDate = dateFormat.parse(leaveArrayList.get(i).getLeaveStartDate());
                    if (fDate.after(tDate) && leaveArrayList.get(i).getLeaveStatus().equals("Requested")) {
                        filterLeaveArrayList.add(new ModelLeave(leaveArrayList.get(i)));
                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }else{
                if(!leaveArrayList.get(i).getLeaveStatus().equals("Requested")){
                    filterLeaveArrayList.add(leaveArrayList.get(i));

                }
            }

        }

        AdapterLeave adapterLeave=new AdapterLeave(ShowLeaveRequestActivity.this,filterLeaveArrayList,siteId);
        binding.rvLeave.setAdapter(adapterLeave);


    }
    class SiteSpinnerAdapter
            extends BaseAdapter {
        SiteSpinnerAdapter() {
        }

        public int getCount() {
            return siteArrayList.size();
        }

        public Object getItem(int n) {
            return null;
        }

        public long getItemId(int n) {
            return 0L;
        }

        public View getView(int n, View view, ViewGroup viewGroup) {
            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
            TextView textView = (TextView) view2.findViewById(R.id.spinner_text);
            textView.setText(siteArrayList.get(n).getSiteCity());
            return view2;
        }
    }
}
