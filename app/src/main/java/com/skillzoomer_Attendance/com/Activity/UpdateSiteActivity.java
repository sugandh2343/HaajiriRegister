package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityUpdateSiteBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class UpdateSiteActivity extends AppCompatActivity {
    ActivityUpdateSiteBinding binding;
    String country="",city="",sitename="",address="";
    private long siteid=100;
    private ProgressDialog progressDialog;
    String userType,userMobile;
    FirebaseAuth firebaseAuth;
    Button btn;
    private final Calendar myCalendar = Calendar.getInstance();
    String startTime="",endTime="";
    String currentDate;
    Boolean forceLogout=true;
    Boolean picActivity=true;
    int selected_option=0;
    private ArrayList<ModelUser> memberArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateSiteBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();

        userType = sharedpreferences.getString("userDesignation" , "");
        userMobile = sharedpreferences.getString("userMobile" , "");
        Log.e("userMobile" , userMobile);
        binding.etCountry.setText("");
        memberArrayList=new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        binding.etSiteCreatedDate.setText(currentDate);
        Intent intent=getIntent();
        siteid=intent.getLongExtra("siteId",0);
        getSiteDetails(siteid);
        sitename=intent.getStringExtra("siteName");
        city=intent.getStringExtra("siteCity");
        startTime=intent.getStringExtra("siteStart");
        endTime=intent.getStringExtra("siteEnd");
        address=intent.getStringExtra("siteAddress");
        binding.etCity.setText(city);
        binding.etCountry.setText(address);
        binding.etSiteName.setText(sitename);
        binding.etStartTime.setText(startTime);
        binding.etEndTime.setText(endTime);

        selected_option=sharedpreferences.getInt("workOption",0);







        binding.btnAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country = binding.etCountry.getText().toString();
                city = binding.etCity.getText().toString();
                sitename = binding.etSiteName.getText().toString();
                if (TextUtils.isEmpty(city)) {
                    Toast.makeText(UpdateSiteActivity.this , "Enter City" , Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sitename)) {
                    Toast.makeText(UpdateSiteActivity.this , "Enter site name" , Toast.LENGTH_SHORT).show();
                }else if (startTime.equals("")) {
                    Toast.makeText(UpdateSiteActivity.this , "Enter site start time" , Toast.LENGTH_SHORT).show();
                }else if (endTime.equals("")) {
                    Toast.makeText(UpdateSiteActivity.this , "Enter site end Time" , Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    addSite();

                }
            }
        });
        TimePickerDialog.OnTimeSetListener timeStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view , int hourOfDay , int minute) {
                myCalendar.set(Calendar.HOUR , hourOfDay);
                myCalendar.set(Calendar.MINUTE , minute);
                String myFormat = "hh:mm"; //In which you need put here
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat(myFormat , Locale.US);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startTime = sdf.format(myCalendar.getTime());
                }
                Log.e("Time" , "Start:" + startTime);
                updateLabel(binding.etStartTime);

            }
        };
        TimePickerDialog.OnTimeSetListener timeEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view , int hourOfDay , int minute) {
                myCalendar.set(Calendar.HOUR , hourOfDay);
                myCalendar.set(Calendar.MINUTE , minute);
                String myFormat = "hh:mm"; //In which you need put here
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat(myFormat , Locale.US);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startTime = sdf.format(myCalendar.getTime());
                }
                Log.e("Time" , "Start:" + startTime);
                updateLabel(binding.etEndTime);

            }
        };


        binding.etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(UpdateSiteActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etStartTime.setText(selectedHour + ":" + selectedMinute);
                        startTime=selectedHour + ":" + selectedMinute;
                        Log.e("StartTime",startTime);
                    }
                } , hour , minute , true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        binding.etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(UpdateSiteActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etEndTime.setText(selectedHour + ":" + selectedMinute);
                        endTime=selectedHour + ":" + selectedMinute;
                        Log.e("endTime",endTime);

                    }
                } , hour , minute , true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
//


    }

    private void getSiteDetails(long siteid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){


                    for(DataSnapshot ds:snapshot.getChildren()){
                        if(ds.child("siteId").getValue(long.class).equals(siteid)){
                            ModelUser modelMember=ds.getValue(ModelUser.class);
                            memberArrayList.add(modelMember);
                        }



                    }
                    Log.e("LabourListSize1",""+memberArrayList.size());
//                    AdapterMember adapterMember=new AdapterMember(UpdateSiteActivity.this,memberArrayList);
//                    binding.rvMembers.setAdapter(adapterMember);
//                    binding.llNoAssociateAdded.setVisibility(View.GONE);
//                    binding.llAssociateAdded.setVisibility(View.GONE);
                }
//                else{
//                    binding.llNoAssociateAdded.setVisibility(View.GONE);
//                    binding.llAssociateAdded.setVisibility(View.GONE);
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateLabel(EditText fromdateEt) {
        String myFormat = "hh:mm"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat, Locale.US);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fromdateEt.setText(sdf.format(myCalendar.getTime()));
        }
    }








    private void addSite() {
        long siteIdIntent=siteid;

        String timestamp=""+System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("siteName",sitename);
        hashMap.put("siteCity",city);
        hashMap.put("startTime",startTime);
        hashMap.put("endTime",endTime);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteid)).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        getSiteId(timestamp);
                        progressDialog.dismiss();
                        Toast.makeText(UpdateSiteActivity.this , "Site Updated Successfully" , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UpdateSiteActivity.this,SiteActivity.class);
                        Log.e("SiteRegister",""+siteIdIntent);
                        intent.putExtra("siteID", siteIdIntent);
                        intent.putExtra("siteName",sitename);
                        intent.putExtra("siteTimestamp",timestamp);
                        startActivity(intent);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateSiteActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();

                    }
                });


    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(UpdateSiteActivity.this,timelineActivity.class));
    }
}
