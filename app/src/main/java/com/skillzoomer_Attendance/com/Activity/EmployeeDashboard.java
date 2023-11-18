package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEmployeeDashboardBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EmployeeDashboard extends AppCompatActivity implements OnMapReadyCallback {
    ActivityEmployeeDashboardBinding binding;
    private ProgressDialog progressDialog;

    private static final int Location_Request_code = 100;
    private final int REQUEST_IMAGE = 400;
    private String[] locationPermissions;

    private LocationManager locationManager;

    FusedLocationProviderClient fusedLocationProviderClient;

    private ArrayList<ModelEmployee> employeeArrayList;


    FirebaseAuth firebaseAuth;
    private int hours,days,min;

    private double latitude=0.0, longitude=0.0;
    long siteId;

    GoogleMap googleMap;
    ModelEmployee modelEmployee;

    Marker marker;

    private Bitmap image_uri;

    private Circle circle;

    private String startTime,endTime;
    private long siteLatitude,siteLongitude,radius;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmployeeDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        employeeArrayList=new ArrayList<>();
        progressDialog = new ProgressDialog(EmployeeDashboard.this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        if (checkLocationPermission()) {
            progressDialog.show();
            detectLocation();
        } else {
            requestLocationPermission();
        }
        String timestamp = "" + System.currentTimeMillis();
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }

        SharedPreferences sharedPreferences=getSharedPreferences("LoginDetails",MODE_PRIVATE);
        if(sharedPreferences.getString("LastActivityDate","")==null ||sharedPreferences.getString("LastActivityDate","").equals("")||
                !sharedPreferences.getString("LastActivityDate","").equals(currentDate)){
            Log.e("Cursor","1");
            progressDialog.setMessage("Please Wait");
            getUserDetails();
        }else if(sharedPreferences.getString("LastActivityDate","")!=null &&(sharedPreferences.getString("LastActivityDate","").equals(currentDate)
                && sharedPreferences.getString("LastActivity","").equals("PunchIn"))){
            binding.btnPunchIn.setVisibility(View.GONE);
            binding.btnPunchOut.setVisibility(View.VISIBLE);
            binding.llPunchIn.setVisibility(View.VISIBLE);
            Log.e("Cursor","2");
        }else if(sharedPreferences.getString("LastActivityDate","")!=null &&(sharedPreferences.getString("LastActivityDate","").equals(currentDate)
                && sharedPreferences.getString("LastActivity","").equals("PunchOut"))){
            binding.llPunchIn.setVisibility(View.GONE);
            binding.btnPunchOut.setVisibility(View.GONE);
            binding.llTakeABreak.setVisibility(View.GONE);
            binding.workingHrs.setVisibility(View.VISIBLE);
            binding.workingHrs.setText("your attendance for today is marked");
            Log.e("Cursor","3");
        }

        binding.btnPunchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionCheck();
            }
        });
        binding.etBreakEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timestamp = "" + System.currentTimeMillis();
                String currentDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                String[] separataed=currentDate.split("-");
                String currentTime = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                }

                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                String finalCurrentTime = currentTime;
                String finalCurrentTime1 = currentTime;
                String finalCurrentDate = currentDate;
                reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("breakStartTime")){
                            long breakCount=0;
                            long breakHours=0;
                            String inStamp=snapshot.child("inTimeStamp").getValue(String.class);
                            if(snapshot.hasChild("breakCount")){
                                breakCount=snapshot.child("breakCount").getValue(long.class);
                            }
                            if(snapshot.hasChild("breakHours")){
                                breakHours=snapshot.child("breakHours").getValue(long.class);
                            }
                            SimpleDateFormat simpleDateFormat = null;
                            Date date1=null,date2=null;
                            int days,hours,min;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                simpleDateFormat = new SimpleDateFormat("hh:mm");
                            }


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                try {
                                    date1 = simpleDateFormat.parse(snapshot.child("breakStartTime").getValue(String.class));
                                    date2 = simpleDateFormat.parse(finalCurrentTime);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                            }


                            long difference = Math.abs((int)(date2.getTime() - date1.getTime())) ;
                            if(difference<0){

                            }
                            days = (int) (difference / (1000*60*60*24));
                            hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                            min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                            hours = (hours < 0 ? -hours : hours);
                            String workingHours=String.valueOf(hours)+"hrs"+"-"+String.valueOf(min)+"min";
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("breakCond",false);
                            hashMap.put("breakEndTime", finalCurrentTime1);
                            hashMap.put("breakEndTimestamp",timestamp);

                            hashMap.put("breakHours",breakHours+((hours*60)+min));//Long
                            hashMap.put("breakCount",breakCount+1);//Long
                            hashMap.put("totalBreakHours",workingHours);
                            long finalBreakCount = breakCount;
                            long finalBreakHours = breakHours;
                            int finalHours = hours;
                            reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    HashMap<String,Object> hashMap1=new HashMap<>();
                                    hashMap1.put("breakCount", finalBreakCount);
                                    hashMap1.put("breakHours", finalBreakHours +((finalHours *60)+min));//Long
                                    hashMap1.put("totalBreakHours",workingHours);
                                    if(modelEmployee!=null){
                                        reference.child(modelEmployee.getHrUid()).child("Industry").child(modelEmployee.getIndustryName()).child("Site").child(String.valueOf(modelEmployee.getSiteId()))
                                                .child("Attendance").child(separataed[2]).child(separataed[1]).child(finalCurrentDate).child(modelEmployee.getUserId()).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        reference.child(firebaseAuth.getUid()).child("Attendance").child(separataed[2]).child(separataed[1]).child(finalCurrentDate)
                                                                .updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        binding.llPunchIn.setVisibility(View.VISIBLE);
                                                                        binding.tilBreakStart.setVisibility(View.VISIBLE);
                                                                        binding.tilBreakEnd.setVisibility(View.GONE);

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        Log.e("DFFFsfWSF",getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrUid", ""));

        binding.etBreakStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EmployeeDashboard.this);
                builder.setTitle("Take a break")
                        .setMessage("Your working time would be stopped until you end this break.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialogInterface, j) -> {
                            String timestamp = "" + System.currentTimeMillis();
                            String currentDate = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                            }
                            String currentTime = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                            }
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("breakCond",true);
                            hashMap.put("breakStartTime",currentTime);
                            hashMap.put("breakStartTimestamp",timestamp);
                            String[] separataed=currentDate.split("-");

                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    binding.tilBreakEnd.setVisibility(View.VISIBLE);
                                    binding.tilBreakStart.setVisibility(View.GONE);
                                    binding.llPunchIn.setVisibility(View.GONE);
                                    binding.txtBreakHeading.setText("You are on Break....");
                                    Toast.makeText(EmployeeDashboard.this, "You are on break", Toast.LENGTH_SHORT).show();
                                }
                            });


                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });

                builder.show();
            }
        });



        binding.btnPunchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EmployeeDashboard.this);
                builder.setTitle("Punch Out")
                        .setMessage("Are sure you want to punch out? This will mark your attendance for today. You cannot punch in for today.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialogInterface, j) -> {
                            String timestamp = "" + System.currentTimeMillis();
                            String currentDate = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                            }
                            String[] separataed=currentDate.split("-");
                            String currentTime = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                            }
                            String filePathAndName = "LoginDetails/" +firebaseAuth.getUid()+"/" + String.valueOf(siteId) + "/" + currentDate + "/" + timestamp;
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                            String finalCurrentDate = currentDate;
                            String finalCurrentTime = currentTime;
                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String timestamp = "" + System.currentTimeMillis();
                                    String currentDate = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                                    }
                                    String currentTime = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                                    }
                                    if(snapshot.hasChild("inDate")){
                                        detectLocation();
                                        SimpleDateFormat simpleDateFormat = null;
                                        Date date1=null,date2=null;
                                        int days,hours,min;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            simpleDateFormat = new SimpleDateFormat("hh:mm");
                                        }


                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            try {
                                                date1 = simpleDateFormat.parse(snapshot.child("inTime").getValue(String.class));

                                                date2 = simpleDateFormat.parse(currentTime);
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }

                                        }


                                        long difference = Math.abs((int)(date2.getTime() - date1.getTime())) ;
                                        if(difference<0){

                                        }
                                        days = (int) (difference / (1000*60*60*24));
                                        hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                                        min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                                        hours = (hours < 0 ? -hours : hours);
                                        String workingHours=String.valueOf(hours)+"hrs"+"-"+String.valueOf(min)+"min";
                                        Log.i("======= Hours"," :: "+hours);
                                        HashMap<String,Object> hashMap=new HashMap<>();

                                        hashMap.put("uid", firebaseAuth.getUid());//String
                                        hashMap.put("punchIn", false);//Bool
                                        hashMap.put("punchOut", true);//Bool
                                        hashMap.put("lastActivity", "Out");//String
                                        hashMap.put("profile", "");//String
                                        hashMap.put("outTimeStamp", "" + timestamp);//String
                                        hashMap.put("outTime", currentTime);//String
                                        hashMap.put("outLatitude",latitude);//Long
                                        hashMap.put("outLongitude",longitude);//Long
                                        hashMap.put("workingHours",(hours*60)+min);//Long
                                        hashMap.put("totalWorkingHours",workingHours);//String
                                        hashMap.put("status","P");//String

                                        Log.e("firebaseAuth",firebaseAuth.getUid());



                                        String inStamp=snapshot.child("inTimeStamp").getValue(String.class);
                                        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if(modelEmployee!=null){
                                                    reference.child(modelEmployee.getHrUid()).child("Industry").child(modelEmployee.getIndustryName()).child("Site").child(String.valueOf(modelEmployee.getSiteId()))
                                                            .child("Attendance").child(separataed[2]).child(separataed[1]).child(finalCurrentDate).child(modelEmployee.getUserId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    reference.child(firebaseAuth.getUid()).child("Attendance").child(separataed[2]).child(separataed[1])
                                                                            .child(finalCurrentDate)
                                                                            .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    reference.child(modelEmployee.getHrUid()).child("Industry").child(modelEmployee.getIndustryName())
                                                                                            .child("Site").child(String.valueOf(modelEmployee.getSiteId())).updateChildren(hashMap)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    SharedPreferences sharedPreferences=getSharedPreferences("LoginDetails",MODE_PRIVATE);
                                                                                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                                                                                    editor.putString("PunchoutDate",finalCurrentDate);
                                                                                                    editor.putString("LastActivity","PunchOut");
                                                                                                    editor.putString("LastActivityDate",finalCurrentDate);
                                                                                                    editor.apply();
                                                                                                    editor.commit();
                                                                                                    binding.btnPunchIn.setVisibility(View.GONE);
                                                                                                    binding.btnPunchOut.setVisibility(View.GONE);
                                                                                                    binding.llPunchIn.setVisibility(View.GONE);
                                                                                                    binding.llTakeABreak.setVisibility(View.GONE);
                                                                                                    binding.workingHrs.setText("Your attendance for today is marked");
                                                                                                }
                                                                                            });


                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            }
                                        });





                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });

                builder.show();



            }
        });
        binding.llPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeDashboard.this,EmployeeReportHome.class));
            }
        });
        binding.llManpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeDashboard.this,EmployeeLeave.class));
            }
        });
        binding.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeDashboard.this,EmployeeProfileActivity.class));
            }
        });
    }

    private void getWorkplaceDetails() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelEmployee.getHrUid()).child("Industry").child(modelEmployee.getIndustryName()).child("Site").child(String.valueOf(modelEmployee.getSiteId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                startTime=snapshot.child("startTime").getValue(String.class);
                endTime=snapshot.child("endTime").getValue(String.class);
                radius=snapshot.child("circleRadius").getValue(long.class);
                latitude=snapshot.child("circleCenterLat").getValue(long.class);
                longitude=snapshot.child("circleCenterLon").getValue(long.class);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserDetails() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String timestamp = "" + System.currentTimeMillis();
                String currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                employeeArrayList.clear();
//                for(DataSnapshot ds:snapshot.getChildren()){
//                    ModelEmployee modelEmployee=new ModelEmployee();
//                   modelEmployee.setUserId(ds.child("userId").getValue(String.class));   //String
//                   modelEmployee.setName(ds.child("name").getValue(String.class));//String
//                   modelEmployee.setDept(ds.child("dept").getValue(String.class));//String
//                   modelEmployee.setDesignation(ds.child("designation").getValue(String.class)); //Long);//String
//                   modelEmployee.setUserType( ds.child("userType").getValue(String.class));//String
//                   modelEmployee.setDept(ds.child("dept").getValue(String.class));//String
//
//                    ds.child("userType").getValue(String.class);//String
//                    ds.child("mobile").getValue(String.class);//String
//                    ds.child("doj").getValue(String.class);//String
//                    ds.child("email").getValue(String.class);//String
//                    ds.child("workEmail").getValue(String.class);//String
//                    ds.child("permanentEmployee").getValue(Boolean.class);//String
//                    ds.child("probationEmployee").getValue(Boolean.class);//String
//                    ds.child("singleAttendance").getValue(Boolean.class);//Bool
//                    ds.child("multiAttendance").getValue(Boolean.class);//Bool
//                    ds.child("allAttendance").getValue(Boolean.class);//Bool
//                    ds.child("Sunday").getValue(Boolean.class);//Bool
//                    ds.child("Monday").getValue(Boolean.class);//Bool
//                    ds.child("Tuesday").getValue(Boolean.class);//Bool
//                    ds.child("Wednesday").getValue(Boolean.class);//Bool
//                    ds.child("Thursday").getValue(Boolean.class);//Bool
//                    ds.child("Friday").getValue(Boolean.class);//Bool
//                    ds.child("Saturday").getValue(Boolean.class);//Bool
//                    ds.child("Salary").getValue(String.class);//String
//                    ds.child("registrationStatus").getValue(String.class);//String
//                    ds.child("hrUid").getValue(String.class);//String
//                    ds.child("siteId").getValue(long.class);//Long
//                    ds.child("siteName").getValue(String.class);//String
//                    ds.child("companyName").getValue(String.class);//String
//                    ds.child("industryPosition").getValue(long.class);//Long
//                    ds.child("industryName").getValue(String.class);//String
//                    if(ds.hasChild("latitude")){
//                       ds.child("latitude").getValue(long.class);//Long
//                       ds.child("longitude").getValue(long.class);//Long
//                        ds.child("radius").getValue(long.class);//Long
//                    }
//                    ds.child("password").getValue(long.class);//Long
//                    ds.child("uid").getValue(long.class);//Long
//                    ds.child("dateOfRegister").getValue(long.class);//Long
//                    ds.child("timestamp").getValue(long.class);//Long
//
//
//                    try{
//
//                        employeeArrayList.add(modelEmployee);
//                    }catch (Exception e){
//
//                        Log.e("Exception",e.getMessage());
//                        Log.e("Exception",ds.getKey().toString());
//                    }
//
//                }
//                if(employeeArrayList.size()>0){
//                    binding.txtHeading.setText(getString(R.string.welcome)+" "+employeeArrayList.get(0).getName());
//
//                }else{
//                    binding.txtHeading.setVisibility(View.GONE);
//                }
                modelEmployee=snapshot.getValue(ModelEmployee.class);
                Log.e("ModelEmployee",modelEmployee.getName());
                binding.txtHeading.setText(getString(R.string.welcome)+" "+modelEmployee.getName());
                siteId=modelEmployee.getSiteId();
                getWorkplaceDetails();
                if(modelEmployee.getLastActivity()==null||(modelEmployee.getLastActivity().equals("Out"))||modelEmployee.getLastActivity().equals("")){


                    binding.btnPunchOut.setVisibility(View.GONE);
                    binding.workingHrs.setVisibility(View.VISIBLE);
                    binding.llPunchIn.setVisibility(View.VISIBLE);
                    if(modelEmployee.getInDate()!=null && modelEmployee.getInDate().equals(currentDate)){
                        binding.workingHrs.setText("You have marked your attendance for today. Working Hours: "+modelEmployee.getTotalWorkingHours());
                        binding.btnPunchIn.setVisibility(View.GONE);
                    }else{
                        binding.workingHrs.setVisibility(View.GONE);
                        binding.btnPunchIn.setVisibility(View.VISIBLE);
                    }

                    binding.llTakeABreak.setVisibility(View.GONE);
                }else if(modelEmployee.getLastActivity()==null||(modelEmployee.getLastActivity().equals("In")&&modelEmployee.getInDate().equals(currentDate))){
                    binding.btnPunchOut.setVisibility(View.VISIBLE);
                    binding.btnPunchIn.setVisibility(View.GONE);
                    binding.workingHrs.setVisibility(View.VISIBLE);
                    binding.llPunchIn.setVisibility(View.VISIBLE);
                    binding.llTakeABreak.setVisibility(View.VISIBLE);
                    if(modelEmployee.getBreakCond()==null || !modelEmployee.getBreakCond()){
                        binding.tilBreakStart.setVisibility(View.VISIBLE);
                        binding.tilBreakEnd.setVisibility(View.GONE);
                        binding.llPunchIn.setVisibility(View.VISIBLE);
                        binding.txtBreakHeading.setText("Wanna take a break?");
                    }else{
                        binding.tilBreakStart.setVisibility(View.GONE);
                        binding.tilBreakEnd.setVisibility(View.VISIBLE);
                        binding.llPunchIn.setVisibility(View.GONE);
                        binding.txtBreakHeading.setText("You are on Break....");
                    }
                    binding.workingHrs.setText("You have punched in at "+snapshot.child("inTime").getValue(String.class));
                }else if(modelEmployee.getLastActivity()==null||(modelEmployee.getLastActivity().equals("In")&&!modelEmployee.getInDate().equals(currentDate))){
                    binding.btnPunchIn.setVisibility(View.VISIBLE);
                    binding.btnPunchOut.setVisibility(View.GONE);
                    binding.workingHrs.setVisibility(View.GONE);
                    binding.llTakeABreak.setVisibility(View.GONE);
                }else{
                    binding.btnPunchIn.setVisibility(View.VISIBLE);
                    binding.btnPunchOut.setVisibility(View.GONE);
                    binding.workingHrs.setVisibility(View.GONE);
                    binding.llTakeABreak.setVisibility(View.GONE);
                }
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng latLng;
        latLng = new LatLng(latitude, longitude);

        marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                .title("Your Location")
                .draggable(true));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        if(modelEmployee!=null){
            if(modelEmployee.getLatitude()>0 && modelEmployee.getSingleAttendance() ){
                circle = googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(modelEmployee.getLatitude(), modelEmployee.getLongitude()))
                        .radius(modelEmployee.getRadius())
                        .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(1.0f));
            }

        }



        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));




    }

    private Boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    private void permissionCheck() {
        Dexter.withActivity(EmployeeDashboard.this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        Log.e("Denied",""+report.getDeniedPermissionResponses().get(0).getPermissionName());
                        if (report.areAllPermissionsGranted()) {
                            launchCameraIntentForTax();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeDashboard.this);
        builder.setTitle(R.string.grant_permission);
        builder.setMessage(R.string.this_app_requires_permission);
        builder.setPositiveButton(R.string.goto_settings, (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", EmployeeDashboard.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void launchCameraIntentForTax() {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_ Image Description");
//        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    @SuppressLint("MissingPermission")
    private void detectLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        try{
                            Location location = task.getResult();
                            if (location != null) {

                                try {
                                    Geocoder geocoder = new Geocoder(EmployeeDashboard.this, Locale.ENGLISH);
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location.getLatitude(), location.getLongitude(), 1


                                    );
                                    progressDialog.dismiss();
                                    latitude = addresses.get(0).getLatitude();
                                    longitude = addresses.get(0).getLongitude();
                                    if(addresses.get(0).getLocality()!=null){
                                        binding.txtAddress.setText("You are at : "+ addresses.get(0).getSubLocality());

                                    }else{
                                        binding.txtAddress.setVisibility(View.GONE);
                                    }

                                    getUserDetails();
                                    Log.e("SourceLat", "Latitude" + latitude);
                                    Log.e("SourceLat", "Longitude" + latitude);

                                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                    supportMapFragment.getMapAsync(EmployeeDashboard.this);
//                                if(latitude>0 && longitude>0){
//                                    fusedLocationProviderClient.removeLocationUpdates((LocationListener) locationManager);
//                                }
//                                    if (siteId > 0) {
//
//                                        String email = userId + "@yopmail.com";
//                                        Log.e("email", "" + email);
//                                        firebaseAuth.signInWithEmailAndPassword(email, userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                            @Override
//                                            public void onSuccess(AuthResult authResult) {
//                                                Log.e("GetSiteName", "SignIn");
//
////                                countDownTimer.cancel();
//                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//                                                ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                        String currentDate = null;
//                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                                                            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
//                                                        }
//                                                        if(snapshot.child("date").getValue(String.class)!=null){
//                                                            if(snapshot.child("date").getValue(String.class).equals(currentDate)){
//                                                                getBlockStatus(siteId);
//                                                            }else{
//                                                                HashMap<String,Object> hashMap=new HashMap();
//                                                                hashMap.put("online",false);
//                                                                hashMap.put("skilled",0);
//                                                                hashMap.put("unskilled",0);
//                                                                hashMap.put("SkilledTime","");
//                                                                hashMap.put("UnskilledTime","");
//                                                                hashMap.put("memberOnline",0);
//                                                                hashMap.put("picActivity",false);
//                                                                hashMap.put("picId","");
//                                                                hashMap.put("picTime","");
//                                                                hashMap.put("picDate","");
//                                                                hashMap.put("picLink","");
//                                                                hashMap.put("picRemark","");
//                                                                hashMap.put("picLatitude", 0);
//                                                                hashMap.put("picLongitude", 0);
//                                                                hashMap.put("memberOnline",0);
//                                                                ref.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                            @Override
//                                                                            public void onSuccess(Void unused) {
//                                                                                getBlockStatus(siteId);
//                                                                            }
//                                                                        })
//                                                                        .addOnFailureListener(new OnFailureListener() {
//                                                                            @Override
//                                                                            public void onFailure(@NonNull Exception e) {
//
//                                                                            }
//                                                                        });
//                                                            }
//
//                                                        }else{
//                                                            getBlockStatus(siteId);
//                                                        }
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
////                                            getBlockStatus(siteId);
//
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.e("ProgressDialog", "5");
//                                                progressDialog.dismiss();
//                                                Toast.makeText(LoginPic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();

                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(EmployeeDashboard.this, "Unable to detect Your Location", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            progressDialog.dismiss();


                        }



                    }
                });
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Your Location is Turned Off.", Toast.LENGTH_SHORT).show();
            detectLocation();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Location_Request_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    detectLocation();
                } else {
                    Toast.makeText(this, "" + grantResults[0], Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_uri = imageBitmap;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image_uri.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] data1 = bytes.toByteArray();
            String timestamp = "" + System.currentTimeMillis();
            String currentDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
            }

            String currentTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
            }
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm");
            Date cTime,stime;
            try {
                cTime = dateFormat.parse(currentTime);
                stime = dateFormat.parse(startTime);
                long difference = Math.abs((int)(cTime.getTime() - stime.getTime())) ;
                if(difference<0){

                }
                days = (int) (difference / (1000*60*60*24));
                hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours));
                hours = (hours < 0 ? -hours : hours);
                Log.i("======= Hours"," :: "+hours);
                if(cTime.after(stime)){
                    Toast.makeText(this , "You are late by "+min+" "  , Toast.LENGTH_SHORT).show();
                }
                float[] results = new float[1];
                Location.distanceBetween(siteLatitude, siteLongitude, latitude
                        , longitude, results);
                float distance = results[0];
                long dis_display=(long) distance;
                if(distance>radius){
                    progressDialog.dismiss();
                    Toast.makeText(this , "You are quite far from your workplace.Please Login within radius of workplace" , Toast.LENGTH_SHORT).show();
                }else{
                    String[] separataed=currentDate.split("-");
                    String filePathAndName = "LoginDetails/" +firebaseAuth.getUid()+"/" + String.valueOf(siteId) + "/" + currentDate + "/" + timestamp;
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                    String finalCurrentDate = currentDate;
                    String finalCurrentTime = currentTime;
                    storageReference.putBytes(data1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Task<Uri> uriTask = task.getResult().getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()) {
                                Log.e("UploadAttendance", "" + latitude);
                                Log.e("UploadAttendance", "" + longitude);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", firebaseAuth.getUid());//String
                                hashMap.put("punchIn", true);//Bool
                                hashMap.put("punchOut", false);//Bool
                                hashMap.put("lastActivity", "In");//String
                                hashMap.put("profile", "" + downloadImageUri);//String
                                hashMap.put("inTimeStamp", "" + timestamp);//String
                                hashMap.put("inTime", "" + finalCurrentTime);//String
                                hashMap.put("outTime", "");//String
                                hashMap.put("inLatitude",latitude);//Long
                                hashMap.put("inLongitude",longitude);//Lomg
                                hashMap.put("name",modelEmployee.getName());//String
                                hashMap.put("inDate",finalCurrentDate);//String
                                hashMap.put("outLatitude",0.0);//Long
                                hashMap.put("outLongitude",0.0);//Long
                                hashMap.put("workingHours",0);//Long
                                hashMap.put("status","Pending");

                                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        SharedPreferences sharedPreferences=getSharedPreferences("LoginDetails",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        editor.putString("PunchinDate",finalCurrentDate);
                                        editor.putString("LastActivity","PunchIn");
                                        editor.putString("LastActivityDate",finalCurrentDate);
                                        editor.apply();
                                        editor.commit();
                                        Log.e("CURRENTTEST",""+(modelEmployee==null));
                                        if(modelEmployee!=null){
                                            reference.child(modelEmployee.getHrUid()).child("Industry").child(modelEmployee.getIndustryName()).child("Site").child(String.valueOf(modelEmployee.getSiteId()))
                                                    .child("Attendance").child(separataed[2]).child(separataed[1]).child(finalCurrentDate).child(modelEmployee.getUserId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            reference.child(firebaseAuth.getUid()).child("Attendance").child(separataed[2])
                                                                    .child(separataed[1]).
                                                                    child(finalCurrentDate)
                                                                    .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            reference.child(modelEmployee.getHrUid()).child("Industry").child(modelEmployee.getIndustryName())
                                                                                    .child("Site").child(String.valueOf(modelEmployee.getSiteId()))
                                                                                    .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            binding.btnPunchIn.setVisibility(View.GONE);
                                                                                            binding.btnPunchOut.setVisibility(View.VISIBLE);
                                                                                            binding.llTakeABreak.setVisibility(View.VISIBLE);
                                                                                        }
                                                                                    });

                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    }
                                });






                            }
                        }
                    });

                }


            } catch (ParseException e) {
                e.printStackTrace();
            }





        }

    }


}