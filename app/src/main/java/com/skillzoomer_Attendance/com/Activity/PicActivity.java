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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityPicAdminBinding;
import com.skillzoomer_Attendance.com.databinding.ActivityPicBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PicActivity extends AppCompatActivity {
//    ActivityPicBinding binding;
//    String currentDate;
//    public int counter = 600;
//    private String lastLoginTime;
//    private Boolean attendance_activity,pic_activity;
//    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
//        @Override
//        public void onTick(long l) {
//            Log.e("counter",""+counter);
//            int minute = counter / 60;
//            int sec = counter % 60;
//            if (minute > 0) {
//                binding.txtForcedLogoutMsg.setText("" + minute + "minutes and " + sec + " seconds");
//            } else {
//                binding.txtForcedLogoutMsg.setText("" + sec + " seconds");
//            }
//            counter--;
//
//        }
//
//        @Override
//        public void onFinish() {
//            forceLogout();
//
//        }
//    };
//    private static final int Location_Request_code = 100;
//    private String[] locationPermissions;
//    private ProgressDialog progressDialog;
//    private String userName;
//    FirebaseAuth firebaseAuth;
//    private SharedPreferences.Editor editor;
//    private SharedPreferences.Editor editorLogin;
//    String userType,siteName;
//    long siteId;
//    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
//    private int counterValue=2400;
//    ArrayList<String> result;
//    private final int REQUEST_IMAGE = 400;
//    private Uri image_uri1;
//    private Bitmap image_uri;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    private LocationManager locationManager;
//    private double latitude, longitude;
//    private ArrayList<ModelSite> siteAdminList;
//
//    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding= ActivityPicBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        setContentView(binding.getRoot());
//        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
//        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
//        editor= sharedpreferences.edit();
//        editorLogin=spLogin.edit();
//        lastLoginTime=spLogin.getString("LastLoginTime","NA");
//        attendance_activity=spLogin.getBoolean("AttendanceActivity",false);
//        pic_activity=spLogin.getBoolean("PicActivity",false);
//        userType=sharedpreferences.getString("userDesignation","");
//        siteName=sharedpreferences.getString("siteName","");
//        siteId=sharedpreferences.getLong("siteId",0);
//        userName=sharedpreferences.getString("userName","");
//        progressDialog = new ProgressDialog(this);
//        firebaseAuth=FirebaseAuth.getInstance();
//        progressDialog.setTitle(getResources().getString(R.string.please_wait));
//        progressDialog.setCanceledOnTouchOutside(false);
//        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);
//        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        siteAdminList=new ArrayList<>();
//
//        if(userType.equals("Supervisor")){
//            uid=sharedpreferences.getString("hrUid","");
//        }else{
//            uid=sharedpreferences.getString("uid","");
//        }
//
//
//        SimpleDateFormat df1 = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            currentDate = df1.format(c);
//        }
//        binding.llForceLogout.setVisibility(View.GONE);
//        countDownTimer.cancel();
//        if(userType.equals("Supervisor")){
//            if((!attendance_activity)&&(!pic_activity)){
//                if(!lastLoginTime.equals("NA")){
//                    binding.llForceLogout.setVisibility(View.VISIBLE);
//                    String currentTime = null;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
//                    }
//                    DateFormat df = new java.text.SimpleDateFormat("HH:mm");
//                    Date date1 = null;
//                    Date date2 = null;
//                    try {
//                        date1 = df.parse(currentTime);
//                        date2 = df.parse(lastLoginTime);
//                    } catch (ParseException e) {
//                        Log.e("DateException",e.getMessage());
//                        e.printStackTrace();
//                    }
//
//                    long diff = date1.getTime() - date2.getTime();
//                    String currentTime1 = null;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                        currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(diff);
//                    }
//                    Log.e("CurrentTime1",currentTime1);
//                    Log.e("Date1",""+date1.getTime());
//                    Log.e("Date2",""+date2.getTime());
//                    int timeInSeconds = (int) (diff / 1000);
//                    Log.e("timeInSeconds",""+timeInSeconds);
////                    int hours, minutes, seconds;
////                    hours = timeInSeconds / 3600;
////                    timeInSeconds = timeInSeconds - (hours * 3600);
////                    minutes = timeInSeconds / 60;
////                    timeInSeconds = timeInSeconds - (minutes * 60);
////                    seconds = timeInSeconds;
//                    counter=counterValue-timeInSeconds;
//                    if(counter<=0){
//                        Log.e("ForceLogout","ForceLogout");
//                        forceLogout();
//                    }
//                    countDownTimer.start();
//
//                }
//
//            }else{
//                binding.llForceLogout.setVisibility(View.GONE);
//                countDownTimer.cancel();
//            }
//        }else{
//            binding.llForceLogout.setVisibility(View.GONE);
//            countDownTimer.cancel();
//
//        }
//        if(userType.equals("Supervisor")){
//            binding.llSelectSite.setVisibility(View.GONE);
//            binding.llPicActivity.setVisibility(View.VISIBLE);
//        }else{
//            binding.llSelectSite.setVisibility(View.VISIBLE);
//            binding.llPicActivity.setVisibility(View.GONE);
//            getSiteListAdministrator();
//        }
//        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i>0){
//
//                        binding.llPicActivity.setVisibility(View.VISIBLE);
//                        siteId = siteAdminList.get(i).getSiteId();
//                        siteName = siteAdminList.get(i).getSiteName();
//                        Log.e("SiteId",""+siteId);
//
//
//                    }
//                }
//
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }     });
//
//        binding.ivMic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent
//                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                        RecognizerIntent.EXTRA_LANGUAGE);
//                if(sharedpreferences.getString("Language","hi").equals("hi")) {
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi");
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi");
//                    intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "hi");
//                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
//                }else if(sharedpreferences.getString("Language","hi").equals("en")){
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
//                    intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en");
//                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
//                }
//
//
//                try {
//
//                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//                }catch (Exception e) {
//                    Toast
//                            .makeText(PicActivity.this, " " + e.getMessage(),
//                                    Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//        binding.btnClickPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                permissionCheck();
//
//            }
//        });
//        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if((image_uri==null)&&TextUtils.isEmpty(binding.tvSpeechToText.getText().toString())){
//                    Toast.makeText(PicActivity.this, getString(R.string.click_an_image_to_upload), Toast.LENGTH_SHORT).show();
//                }else if(image_uri==null){
//                    if (checkLocationPermission()) {
//                        detectLocation();
//                    } else {
//                        requestLocationPermission();
//                    }
//                    uploadWithoutImage();
//                } else{
//                    if (checkLocationPermission()) {
//                        detectLocation();
//                    } else {
//                        requestLocationPermission();
//                    }
//                    progressDialog.show();
//                    uploadWorkActivity();
//                }
//            }
//        });

    }

//    private void getSiteListAdministrator() {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.child(uid).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                siteAdminList.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    ModelSite modelSite = ds.getValue(ModelSite.class);
//                    siteAdminList.add(modelSite);
//                }
//                siteAdminList.add(0,new ModelSite("Select Site",0));
//                Log.e("siteAdminList", "" + siteAdminList.size());
//                SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
//                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
////                siteId = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId();
////                siteName = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteCity();
//                binding.llSelectSite.setVisibility(View.VISIBLE);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//    private void uploadWithoutImage() {
//        String timestamp=""+System.currentTimeMillis();
//        String currentTime = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
//        }
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("picId", timestamp);
//        hashMap.put("picUid", firebaseAuth.getUid());
//        hashMap.put("picRemark", binding.tvSpeechToText.getText().toString());
//        hashMap.put("siteId", siteId);
//        hashMap.put("uploadedbyUid", firebaseAuth.getUid());
//        hashMap.put("uploadedBytype", getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation",""));
//        hashMap.put("picLink", "");
//        hashMap.put("dateOfUpload", currentDate);
//        hashMap.put("timeofUpload",currentTime);
//        hashMap.put("uploadedBytype", getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation",""));
//        hashMap.put("uploadedByName", getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
//        if (latitude > 0 && longitude > 0) {
//            hashMap.put("picLatitude", latitude);
//            hashMap.put("picLongitude", longitude);
//        }
//
//        String finalCurrentTime = currentTime;
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(timestamp).setValue(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.e("Task","storage");
//                        Log.e("siteIddihwgig",""+siteId);
//                        if(userType.equals("Supervisor")){
//                            updateToSite(siteId, finalCurrentTime,currentDate,null,timestamp);
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(PicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }
//
//    @SuppressLint("MissingPermission")
//    private void detectLocation() {
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestLocationPermission();
//            return;
//        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            {
//                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        Location location = task.getResult();
//                        if (location != null) {
//
//                            try {
//                                Geocoder geocoder = new Geocoder(PicActivity.this, Locale.ENGLISH);
//                                List<Address> addresses = geocoder.getFromLocation(
//                                        location.getLatitude(), location.getLongitude(), 1
//
//
//                                );
//                                latitude = addresses.get(0).getLatitude();
//                                longitude = addresses.get(0).getLongitude();
//                                Log.e("SourceLat", "Latitude" + latitude);
//                                Log.e("SourceLat", "Longitude" + latitude);
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//
//                            }
//
//                        } else {
//                            Toast.makeText(PicActivity.this, "Unable to detect Your Location", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//            }
//        } else {
//            Toast.makeText(this, "Your Location is Turned Off.", Toast.LENGTH_SHORT).show();
//            detectLocation();
//        }
//    }
//    private void requestLocationPermission() {
//        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);
//
//    }
//    private Boolean checkLocationPermission() {
//        boolean result = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
//        return result;
//
//    }
//
//    private void uploadWorkActivity() {
//        String timestamp=""+System.currentTimeMillis();
//        String currentTime = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
//        }
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        image_uri.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        byte[] data = bytes.toByteArray();
//        String filePathAndName="PicActivity/"+String.valueOf(siteId)+"/"+currentDate+"/"+timestamp;
//        StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
//        String finalCurrentTime = currentTime;
//        String finalCurrentTime1 = currentTime;
//        storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
//                while(!uriTask.isSuccessful());
//                Uri downloadImageUri=uriTask.getResult();
//                if(uriTask.isSuccessful()) {
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("picId", timestamp);
//                    hashMap.put("picRemark", binding.tvSpeechToText.getText().toString());
//                    hashMap.put("siteId", siteId);
//                    hashMap.put("uploadedbyUid", firebaseAuth.getUid());
//                    hashMap.put("picLink", "" + downloadImageUri.toString());
//                    hashMap.put("dateOfUpload", currentDate);
//                    hashMap.put("timeofUpload", finalCurrentTime1);
//                    hashMap.put("uploadedBytype", getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation",""));
//                    hashMap.put("uploadedByName", getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
//                    if (latitude > 0 && longitude > 0) {
//                        hashMap.put("picLatitude", latitude);
//                        hashMap.put("picLongitude", longitude);
//                    }
//                    Log.e("siteIddWA123",""+siteId);
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(timestamp).setValue(hashMap)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.e("Task","storage");
//                                    Log.e("siteIddWA",""+siteId);
//                                    if(userType.equals("Supervisor")){
//                                        updateToSite(siteId,finalCurrentTime,currentDate,downloadImageUri,timestamp);
//                                    }else{
//                                        progressDialog.dismiss();
//                                        startActivity(new Intent(PicActivity.this,timelineActivity.class));
//                                    }
//
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(PicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(PicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//    }
//
//    private void updateToSite(long siteId1, String currentTime, String currentDate, Uri downloadImageUri, String timestamp) {
//        Log.e("siteIddWA1",""+siteId1);
//        Log.e("siteSize",""+siteAdminList.size());
//        HashMap<String,Object> hashMap=new HashMap<>();
//        hashMap.put("picUid",firebaseAuth.getUid());
//        if(downloadImageUri!=null){
//            hashMap.put("picActivity",true);
//            hashMap.put("picId",timestamp);
//            hashMap.put("picTime",currentTime);
//            hashMap.put("picDate",currentDate);
//            hashMap.put("picLink",downloadImageUri.toString());
//            hashMap.put("picRemark",binding.tvSpeechToText.getText().toString());
//            if (latitude > 0 && longitude > 0) {
//                hashMap.put("picLatitude", latitude);
//                hashMap.put("picLongitude", longitude);
//            }
//
//
//        }else{
//            hashMap.put("picActivity",true);
//            hashMap.put("picId",timestamp);
//            hashMap.put("picTime",currentTime);
//            hashMap.put("picDate",currentDate);
//            hashMap.put("picLink","");
//            hashMap.put("picRemark",binding.tvSpeechToText.getText().toString());
//            if (latitude > 0 && longitude > 0) {
//                hashMap.put("picLatitude", latitude);
//                hashMap.put("picLongitude", longitude);
//            }
//
//        }
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId1)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                        image_uri=null;
//                        image_uri1=null;
//                        binding.tvSpeechToText.getText().clear();
//                        binding.btnClickPic.setText(getString(R.string.take_a_picture));
//                        binding.ivPic.setVisibility(View.GONE);
//                        editorLogin.putBoolean("PicActivity",true);
//                        editorLogin.apply();
//                        editorLogin.commit();
//                        if(userType.equals("Supervisor")){
//                            updateToMember(PicActivity.this.siteId,hashMap);
//                        }else{
//                            progressDialog.dismiss();
//                            Toast.makeText(PicActivity.this, getString(R.string.activity_updated), Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(PicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//    }
//
//    private void updateToMember(long siteId, HashMap<String, Object> hashMap) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                progressDialog.dismiss();
//
//                Toast.makeText(PicActivity.this, getString(R.string.activity_updated), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    private void permissionCheck() {
//        Dexter.withActivity(PicActivity.this)
//                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
////                        Log.e("Denied",""+report.getDeniedPermissionResponses().get(0).getPermissionName());
//                        if (report.areAllPermissionsGranted()) {
//                            launchCameraIntentForTax();
//                        }
//
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
//    }
//    private void launchCameraIntentForTax() {
//        ContentValues contentValues = new ContentValues();
////        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image title");
////        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_ Image Description");
////        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
//        startActivityForResult(intent, REQUEST_IMAGE);
//    }
//    private void showSettingsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(PicActivity.this);
//        builder.setTitle(R.string.grant_permission);
//        builder.setMessage(R.string.this_app_requires_permission);
//        builder.setPositiveButton(R.string.goto_settings, (dialog, which) -> {
//            dialog.cancel();
//            openSettings();
//        });
//        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
//        builder.show();
//    }
//
//    private void openSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", PicActivity.this.getPackageName(), null);
//        intent.setData(uri);
//        startActivityForResult(intent, 101);
//    }
//
//    private void checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_CODE_SPEECH_INPUT);
//        }
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    @Nullable Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
//            if (resultCode == RESULT_OK && data != null) {
//                result = data.getStringArrayListExtra(
//                        RecognizerIntent.EXTRA_RESULTS);
//                binding.tvSpeechToText.setText(
//                        Objects.requireNonNull(result).get(0));
//
//            }
//
//        }else if(requestCode==REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null){
//
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            image_uri = imageBitmap;
//            binding.ivPic.setImageBitmap(imageBitmap);
//            binding.ivPic.setVisibility(View.VISIBLE);
//            binding.btnClickPic.setText(getString(R.string.change_picture));
//
//        }
//    }
//    private void forceLogout() {
//        String currentTime = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
//        }
//        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PicActivity.this);
//        builder.setCancelable(false);
//        String finalCurrentTime = currentTime;
//        builder.setTitle(R.string.forcelogout)
//                .setMessage(R.string.forced_logout_attendance)
//                .setCancelable(true)
//                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
//                    progressDialog.show();
//                    firebaseAuth.signOut();
//                    editor.clear();
//                    editor.commit();
//
//                    HashMap<String,Object> hashMap=new HashMap<>();
//                    hashMap.put("online",false);
//                    hashMap.put("forceLogout",true);
//                    hashMap.put("forceLogoutReason","Work Inactivity");
//                    hashMap.put("time", finalCurrentTime);
//
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    startActivity(new Intent(PicActivity.this, LoginActivity.class));
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(PicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                });
//        builder.show();
//    }
//    class SiteSpinnerAdapter
//            extends BaseAdapter {
//        SiteSpinnerAdapter() {
//        }
//
//        public int getCount() {
//            return siteAdminList.size();
//        }
//
//        public Object getItem(int n) {
//            return null;
//        }
//
//        public long getItemId(int n) {
//            return 0L;
//        }
//
//        public View getView(int n, View view, ViewGroup viewGroup) {
//            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
//            TextView textView = (TextView) view2.findViewById(R.id.spinner_text);
//            textView.setText(siteAdminList.get(n).getSiteCity());
//            return view2;
//        }
//    }
}