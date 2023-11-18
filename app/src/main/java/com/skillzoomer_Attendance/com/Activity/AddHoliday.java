package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterHoliday;
import com.skillzoomer_Attendance.com.Model.ModelHoliday;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAddHolidayBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddHoliday extends AppCompatActivity {
    ActivityAddHolidayBinding binding;
    long siteId;
    String siteName;
    long holidayId;
    ProgressDialog progressDialog;
    private ArrayList<ModelHoliday> holidayArrayList;
    FirebaseAuth firebaseAuth;

    private String toDate = "";

    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddHolidayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        holidayArrayList=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();

        String timestamp = "" + System.currentTimeMillis();
        String currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        holidayId=1;
        siteName=intent.getStringExtra("siteName");

        getHolidayList();
        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "dd-MMM-yyyy"; //In which you need put here
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);
            toDate = sdf.format(myCalendar.getTime());

            updateLabel1(binding.etHolidayDate);

        };
        binding.etHolidayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddHoliday.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy");
                datePickerDialog.show();
            }
        });

        binding.btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etHolidayDate.getText().toString())){
                    Toast.makeText(AddHoliday.this, "Enter the date of Holiday to continue", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.etHolidayTitle.getText().toString())){
                    Toast.makeText(AddHoliday.this, "Enter the title of holiday to continue", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String,Object>hashMap=new HashMap<>();
                    hashMap.put("id",""+holidayId);
                    hashMap.put("date",binding.etHolidayDate.getText().toString());
                    hashMap.put("title",binding.etHolidayTitle.getText().toString());
                    hashMap.put("addedBy",firebaseAuth.getUid());
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                            .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                            .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                            .child("Site").child(String.valueOf(siteId));
                    reference.child("Holiday").child(binding.etHolidayDate.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(AddHoliday.this, "holiday for this date already defined", Toast.LENGTH_SHORT).show();
                            }else{
                                reference.child("Holiday").child(binding.etHolidayDate.getText().toString()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                getHolidayList();
                                                binding.etHolidayDate.getText().clear();
                                                binding.etHolidayDate.getText().clear();
                                                Toast.makeText(AddHoliday.this, "Holiday Added Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
    private void updateLabel1(EditText fromdateEt) {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);
        fromdateEt.setText(sdf.format(myCalendar.getTime()));
    }

    private void getHolidayList() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId));
        reference.child("Holiday").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                holidayArrayList.clear();
                if(snapshot.exists()){
                    holidayId=snapshot.getChildrenCount()+1;
                }else{
                    holidayId=1;
                }
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelHoliday modelHoliday=ds.getValue(ModelHoliday.class);
                    holidayArrayList.add(modelHoliday);
                }
                if(holidayArrayList.size()>0){
                    binding.rvHoliday.setVisibility(View.VISIBLE);
                    binding.txtNoData.setVisibility(View.GONE);
                    AdapterHoliday adapterHoliday=new AdapterHoliday(AddHoliday.this,holidayArrayList);
                    binding.rvHoliday.setAdapter(adapterHoliday);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }else{
                    binding.rvHoliday.setVisibility(View.GONE);
                    binding.txtNoData.setVisibility(View.VISIBLE);
                    binding.txtNoData.setText("No Holiday Data To Show");
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}