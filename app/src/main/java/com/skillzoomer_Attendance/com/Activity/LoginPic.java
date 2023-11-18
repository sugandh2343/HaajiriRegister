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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityLoginPicBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginPic extends AppCompatActivity {
    ActivityLoginPicBinding binding;
    public int counter = 600;
    public int LoginAttempt = 0;
    public String LoginTime = "";
    private static final int Location_Request_code = 100;
    private String[] locationPermissions;
    private Bitmap image_uri;
    private Uri image_uri1;
    private final int REQUEST_IMAGE = 400;
    FirebaseAuth firebaseAuth;

    private LocationManager locationManager;
    ModelUser modelUser;
    private String mobile;
    private SharedPreferences.Editor editorPL;
    private String companyName;
    private long industryPosition;
    private String industryName;
    private String selectedDesignation;
    private String siteName;
    private String hrUid;
    private double latitude, longitude;
    long siteId;
    private Boolean attendance_activity;
    private String userId, userPassword;
    private ProgressDialog progressDialog;
    private String name;
    SharedPreferences spLogin;
    int count = 0;
    String hruid;
    FusedLocationProviderClient fusedLocationProviderClient;
    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
        @Override
        public void onTick(long l) {
            int minute = counter / 60;
            int sec = counter % 60;
            if (minute > 0) {
                binding.txtForcedLogoutMsg.setText("" + minute + "minutes and " + sec + " seconds");
            } else {
                binding.txtForcedLogoutMsg.setText("" + sec + " seconds");
            }
            counter--;

        }

        @Override
        public void onFinish() {
            forceLogout();

        }
    };


    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorLogin;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("OnCreate", "LOginPic");
        binding = ActivityLoginPicBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        hruid=intent.getStringExtra("hrUid");
        userPassword = intent.getStringExtra("Password");
        siteId = intent.getLongExtra("SiteId", 0);
        progressDialog = new ProgressDialog(LoginPic.this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferencesPL = getSharedPreferences("PermanentLogin", Context.MODE_PRIVATE);
        editorPL = sharedpreferencesPL.edit();
        spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editorLogin = spLogin.edit();
        editor = sharedpreferences.edit();
        getForcedLogoutStatus(userId,siteId);

        if (firebaseAuth.getCurrentUser() != null) {
            finish();
        }


//        countDownTimer.start();

        binding.btnSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionCheck();
            }
        });
        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_uri == null) {
                    Toast.makeText(LoginPic.this, "Take a selfie in given time to upload", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkLocationPermission()) {
                        progressDialog.show();
                        detectLocation();
                    } else {
                        requestLocationPermission();
                    }

                }
            }
        });


    }

    private void getForcedLogoutStatus(String userId1,long siteId) {
        Log.e("getForcedLogoutStatus",userId1);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("userId").equalTo(userId1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("getForcedLogoutStatus",""+snapshot.getChildrenCount());
                for(DataSnapshot ds: snapshot.getChildren()){
                    Log.e("getForcedLogoutStatus",""+ds.child("forceOpt").getValue(Boolean.class));
                    Log.e("getForcedLogoutStatus",""+(ds.child("dateOfRegister").getValue(String.class) != null));
                    if (ds.child("dateOfRegister").getValue(String.class) != null && ds.child("forceOpt").getValue(Boolean.class)) {
                        String registerTimestamp = ds.child("timestamp").getValue(String.class);
                        Log.e("getForcedLogoutStatus",registerTimestamp);
                        String timestamp = "" + System.currentTimeMillis();
                        long difference = (long) ((Long.parseLong(timestamp)) - (Long.parseLong(registerTimestamp)));
                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;

                        long elapsedDays = difference / daysInMilli;
                        difference = difference % daysInMilli;

                        long elapsedHours = difference / hoursInMilli;
                        difference = difference % hoursInMilli;

                        long elapsedMinutes = difference / minutesInMilli;
                        difference = difference % minutesInMilli;

                        long elapsedSeconds = difference / secondsInMilli;
                        System.out.printf(
                                "%d days, %d hours, %d minutes, %d seconds%n",
                                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
                        Log.e("Elapsedhours", "" + (73 - elapsedHours));
                        if (elapsedHours < 73) {
                            binding.llTrialFl.setVisibility(View.VISIBLE);
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ForceLogout");
                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String message, knowMore;
                                    if (getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Language", "hi").equals("en")) {
                                        knowMore = snapshot.child("knowMoreLink").getValue(String.class);

                                    } else {
                                        knowMore = snapshot.child("knowMoreLinkHindi").getValue(String.class);

                                    }
                                    binding.flKnowMore.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(LoginPic.this);
                                            View mView = LayoutInflater.from(LoginPic.this).inflate(R.layout.layout_force_logout_know_more, null);
                                            alert.setView(mView);
                                            ImageView iv_close=mView.findViewById(R.id.iv_close);
                                            final android.app.AlertDialog alertDialog = alert.create();
                                            alertDialog.show();
                                            iv_close.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    alertDialog.dismiss();
                                                }
                                            });

                                        }
                                    });
                                    binding.txtTrialForceLogout.setText(getString(R.string.your_trial) +" "+""+(73 - elapsedHours) +" "+ getString(R.string.hours));
                                    editor.putBoolean("ForceLogout", false);
                                    editor.putBoolean("ForceLogoutTrial", true);








                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            binding.llForceLogout.setVisibility(View.VISIBLE);
                            countDownTimer.start();
                            binding.llTrialFl.setVisibility(View.GONE);
                            editor.putBoolean("ForceLogout", true);
                            editor.putBoolean("ForceLogoutTrial", false);

                        }

                    } else {
                        binding.llForceLogout.setVisibility(View.GONE);
                        countDownTimer.cancel();
                        binding.llTrialFl.setVisibility(View.GONE);
                        editor.putBoolean("ForceLogout", false);
                        editor.putBoolean("ForceLogoutTrial", false);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBlockStatus(long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("memberBlock").getValue(String.class) != null && snapshot.child("memberBlock").getValue(String.class).equals("Active")) {
                    uploadAttendance(siteId);
                } else {
                    progressDialog.dismiss();
                    firebaseAuth.signOut();
                    Toast.makeText(LoginPic.this, getString(R.string.blocked_not_login), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadAttendance(long siteId) {
        Log.e("latitudeUsing", "" + latitude);
        Log.e("longitudeUsing", "" + longitude);
        Log.e("siteIDUpload", "" + siteId);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image_uri.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] data = bytes.toByteArray();

//        String path = MediaStore.Images.Media.insertImage(
//                LoginPic.this.getContentResolver(), image_uri, "IMG_" + System.currentTimeMillis(), null
//        );
//        image_uri1 = Uri.parse(path);
        String timestamp = "" + System.currentTimeMillis();
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        String filePathAndName = "LoginDetails/" +hruid+"/" + String.valueOf(siteId) + "/" + currentDate + "/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        String finalCurrentDate = currentDate;
        String finalCurrentTime = currentTime;
        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;

//                         refreshGallery(LoginPic.this,timestamp,image_uri);
                        Uri downloadImageUri = uriTask.getResult();
//                        File file = new File(path);
//                        boolean deleted = file.delete();
//                        Log.e("Deleted",""+deleted);
                        if (uriTask.isSuccessful()) {
                            Log.e("UploadAttendance", "" + latitude);
                            Log.e("UploadAttendance", "" + longitude);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", firebaseAuth.getUid());
                            hashMap.put("online", true);
                            hashMap.put("status", "NormalLogin");
                            hashMap.put("profile", "" + downloadImageUri);
                            hashMap.put("timeStamp", "" + timestamp);
                            hashMap.put("time", "" + finalCurrentTime);


                            if (latitude > 0 && longitude > 0) {
                                hashMap.put("memberLatitude", latitude);
                                hashMap.put("memberLongitude", longitude);
                            }
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance")
                                    .child(finalCurrentDate).child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.e("AttendanceUploaded","AU");


                                            updateOnsiteMaster(String.valueOf(siteId), finalCurrentTime, timestamp);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("GetSiteName", "updateOnsiteMaster");
                                            Log.e("ProgressDialog", "4");
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginPic.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ProgressDialog", "3");
                        progressDialog.dismiss();
                        Log.e("Exception", e.getMessage());

                    }
                });

    }

    public void refreshGallery(Context context, String path, Bitmap image_uri) {
//        File file = (MediaStore.Images.Media.insertImage(
//                LoginPic.this.getContentResolver(), image_uri, "IMG_" + path, null));
//        Log.e("Check",""+(file.exists() && file.isFile()));
//        if (file.exists() && file.isFile()) {
//
//            file.delete();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Intent intent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            intent.setData(Uri.fromFile(file));
//            context.sendBroadcast(intent);
//        } else {
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(file.getAbsolutePath())));
//        }
        File file = new File(MediaStore.Images.Media.insertImage(
                LoginPic.this.getContentResolver(), image_uri, "IMG_" + path, null));
        Log.e("Path1", file.getAbsolutePath());
        try {
            Boolean delete = file.delete();
            Log.e("Delete1", "" + delete);

        } catch (Exception e) {
            Log.e("Exception111222", e.getMessage());
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(MediaStore.Images.Media.insertImage(
                LoginPic.this.getContentResolver(), image_uri, "IMG_" + path, null)))));
    }


    private void updateOnsiteMaster(String siteId, String currentTime, String timestamp) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hruid).child("Industry").child("Construction").child("Site").child(siteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long memberOnline = snapshot.child("memberOnline").getValue(long.class);
                Log.e("MemberONLINE", "" + memberOnline);

                siteName = snapshot.child("siteName").getValue(String.class);
                hrUid = snapshot.child("hrUid").getValue(String.class);
                updateToDatabase(siteName, hrUid, memberOnline, currentTime, timestamp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void updateToDatabase(String siteName, String hrUid, long memberOnline, String currentTime, String timestamp) {
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", firebaseAuth.getUid());
        hashMap.put("online", true);
        hashMap.put("time", "" + currentTime);
        hashMap.put("date", "" + currentDate);
        hashMap.put("onlineTimestamp", timestamp);
        hashMap.put("memberOnline", 1 + memberOnline);
        hashMap.put("tempLogout", false);
        if (latitude > 0 && longitude > 0) {
            hashMap.put("memberLatitude", latitude);
            hashMap.put("memberLongitude", longitude);
        }


        String finalCurrentDate = currentDate;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("GetSiteName", "updateOnsiteMaster");

                        updateToMember(currentTime, finalCurrentDate, longitude, latitude, timestamp);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GetSiteName", "updateOnsiteMaster1");
                        Log.e("ProgressDialog", "1");
//                        progressDialog.dismiss();
                        Toast.makeText(LoginPic.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateToMember(String currentTime, String finalCurrentDate, double longitude, double latitude, String timestamp) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("memberUid", firebaseAuth.getUid());
        hashMap.put("online", true);
        hashMap.put("time", "" + currentTime);
        hashMap.put("date", "" + finalCurrentDate);
        hashMap.put("onlineTimestamp", timestamp);
        if (latitude > 0 && longitude > 0) {
            hashMap.put("memberLatitude", latitude);
            hashMap.put("memberLongitude", longitude);
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                getUserInfo(timestamp);
            }
        });
    }

    private void getUserInfo(String timestamp) {
        Log.e("firebaseAuth", firebaseAuth.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("firebaseAuth", "" + snapshot.getChildrenCount());
                if (snapshot.getChildrenCount() > 0) {

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Log.e("GetSiteName", "GetUserInfo");
                        modelUser = child.getValue(ModelUser.class);
                        Log.e("ModelUser", modelUser.getMobile());
                        mobile = modelUser.getMobile();
                        String password = modelUser.getPassword();
                        companyName = modelUser.getCompanyName();
                        industryPosition=modelUser.getIndustryPosition();
                        industryName=modelUser.getIndustryName();
                        name = modelUser.getName();
                        selectedDesignation = modelUser.getUserType();
                        editor.putString("uid", firebaseAuth.getUid());
                        editor.putString("industryName", industryName);
                        editor.putLong("industryPosition",industryPosition);
                        editor.putString("userName", userId);
                        editor.putString("userDesignation", selectedDesignation);
                        editor.putString("userMobile", mobile);
                        editor.putString("companyName", companyName);
                        editor.putString("fullName", name);
                        editor.putString("hrDesignation", modelUser.getHrDesignation());
                        editor.putString("hrDesignationHindi", modelUser.getHrDesignationHindi());
                        editor.putString("fullName", name);
                        editor.putBoolean("attendanceManagement", modelUser.getAttendanceManagement());
                        editor.putBoolean("cashManagement", modelUser.getCashManagement());
                        editor.putBoolean("expenseManagement", modelUser.getFinanceManagement());

                        Log.e("SiteIdUpdate", "" + siteId);
                        editor.putLong("siteId", siteId);
//                        Log.e("SiteName",siteName);
//
                        editor.apply();
                        editor.commit();
                        editorLogin.putBoolean("LoginFirstTime", false);
                        editorLogin.apply();
                        editorLogin.commit();
                        editorPL.putString("userId", userId);
                        editorPL.apply();
                        editorPL.commit();
                        hrUid = child.child("hrUid").getValue(String.class);
                        editor.putString("siteName", siteName);
                        Log.e("Editor",hrUid);
                        editor.putString("hrUid", hrUid);
                        SimpleDateFormat df = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                        }
                        Date c = Calendar.getInstance().getTime();
                        String currentDate = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            currentDate = df.format(c);
                        }
                        String currentTime = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                        }
                        Log.e("CTLP", currentTime);

                        editorLogin.putString("LastLogin", currentDate);
                        editorLogin.putString("LastLoginTime", currentTime);
                        editorLogin.apply();
                        editorLogin.commit();


                        Log.e("SelectedDesignation", selectedDesignation);
                        editor.apply();
                        editor.commit();

                        Log.e("HrUid", modelUser.getHrUid());
                        updateToAttendance(timestamp,currentDate,name);

                    }


//
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToAttendance(String timestamp, String currentDate, String name) {
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("name",name);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child(currentDate).child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getAdminToken(modelUser.getHrUid(), siteName);
            }
        });
    }

    private void getSiteName(long siteId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("GetSiteName", "GetSiteName");
                siteName = snapshot.child("siteName").getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAdminToken(String hrUid, String siteName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(hrUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token="";
                if(snapshot.child("token").getValue(String.class)!=null){
                    token = snapshot.child("token").getValue(String.class);
                }



                sendNotification(token, siteName, "Online");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String token, String siteName, String status) {
        Log.d("token : ", token);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();

            jsonobject_notification.put("title", "Associate is online");
            jsonobject_notification.put("body", name + " " + "is online at" + " " + siteName);


            JSONObject jsonobject_data = new JSONObject();
            jsonobject_data.put("imgurl", "https://firebasestorage.googleapis.com/v0/b/haajiri-register.appspot.com/o/Notifications%2Fic_app_logo1.png?alt=media&token=d77857f1-b0e0-4b15-9934-3ec8a88903e9");

            //JSONObject jsonobject = new JSONObject();

            js.put("to", token);
            js.put("notification", jsonobject_notification);
            js.put("data", jsonobject_data);

            //js.put("", jsonobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.e("response", String.valueOf(response));

                getFirebaseToken();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error :  ", String.valueOf(error));
                getFirebaseToken();

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer AAAAOXCkmcc:APA91bFUByxO9XAex4Tdz9fLVUDpRhbAL1XbQ_pKBA0JNFEX_wHhSoMcWzRwbsBDOYV0AzS60c8beYsnHlA9zXj829SlBGScHFH675E5TIkzYHKQZEvDxnRgP9Rv4EUx1_8Nq2HbtK3a ");//TODO add Server key here
                //headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        requestQueue.add(jsonObjReq);
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.e("TAG", token);
                        updateTokenToUserDatabase(token);
//                        Toast.makeText(RegistrationActivity.this, "" + token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTokenToUserDatabase(String token) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("siteName", siteName);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("ProgressDialog", "2");
                progressDialog.dismiss();
                Intent intent = new Intent(LoginPic.this, MemberTimelineActivity.class);
                startActivity(intent);
                finish();


            }
        });
    }

    private void permissionCheck() {
        Dexter.withActivity(LoginPic.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginPic.this);
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
        Uri uri = Uri.fromParts("package", LoginPic.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void launchCameraIntentForTax() {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_ Image Description");
//        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_uri = imageBitmap;
            binding.ivProfile.setImageBitmap(image_uri);


        }

    }

    private Boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;

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
                                    Geocoder geocoder = new Geocoder(LoginPic.this, Locale.ENGLISH);
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location.getLatitude(), location.getLongitude(), 1


                                    );
                                    latitude = addresses.get(0).getLatitude();
                                    longitude = addresses.get(0).getLongitude();
                                    Log.e("SourceLat", "Latitude" + latitude);
                                    Log.e("SourceLat", "Longitude" + latitude);
//                                if(latitude>0 && longitude>0){
//                                    fusedLocationProviderClient.removeLocationUpdates((LocationListener) locationManager);
//                                }
                                    if (siteId > 0) {

                                        String email = userId + "@yopmail.com";
                                        Log.e("email", "" + email);
                                        firebaseAuth.signInWithEmailAndPassword(email, userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Log.e("GetSiteName", "SignIn");

//                                countDownTimer.cancel();
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                                ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String currentDate = null;
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                                                        }
                                                        if(snapshot.child("date").getValue(String.class)!=null){
                                                            if(snapshot.child("date").getValue(String.class).equals(currentDate)){
                                                                getBlockStatus(siteId);
                                                            }else{
                                                                HashMap<String,Object> hashMap=new HashMap();
                                                                hashMap.put("online",false);
                                                                hashMap.put("skilled",0);
                                                                hashMap.put("unskilled",0);
                                                                hashMap.put("SkilledTime","");
                                                                hashMap.put("UnskilledTime","");
                                                                hashMap.put("memberOnline",0);
                                                                hashMap.put("picActivity",false);
                                                                hashMap.put("picId","");
                                                                hashMap.put("picTime","");
                                                                hashMap.put("picDate","");
                                                                hashMap.put("picLink","");
                                                                hashMap.put("picRemark","");
                                                                hashMap.put("picLatitude", 0);
                                                                hashMap.put("picLongitude", 0);
                                                                hashMap.put("memberOnline",0);
                                                                ref.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                getBlockStatus(siteId);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                            }
                                                                        });
                                                            }

                                                        }else{
                                                            getBlockStatus(siteId);
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
//                                            getBlockStatus(siteId);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("ProgressDialog", "5");
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginPic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();

                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginPic.this, "Unable to detect Your Location", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){


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

    private void forceLogout() {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        firebaseAuth.signOut();
        editor.clear();
        editor.commit();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", false);
        hashMap.put("forceLogout", true);
        hashMap.put("uid",firebaseAuth.getUid());

        hashMap.put("time", currentTime);
        hashMap.put("forceLogoutReason", "Login Selfie");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateForceLogoutinMemberDatabase();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginPic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginPic.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.forcelogout)
                .setMessage(R.string.foreced_logout_login)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {


                });
        builder.show();
    }

    private void updateForceLogoutinMemberDatabase() {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        firebaseAuth.signOut();
        editor.clear();
        editor.commit();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", false);
        hashMap.put("forceLogout", true);

        hashMap.put("time", currentTime);
        hashMap.put("forceLogoutReason", "Login Selfie");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hruid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(mobile).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        updateForceLogoutinMemberDatabase();
                        startActivity(new Intent(LoginPic.this, LoginActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginPic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginPic.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.forcelogout)
                .setMessage(R.string.foreced_logout_login)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {


                });
        builder.show();

    }

    @Override
    public void onBackPressed() {
      startActivity(new Intent(LoginPic.this,LoginActivity.class));
      finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        count++;
        Log.e("Count", "" + count);
        Log.e("OnCreate", "OnPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("OnCreate", "OnRestart");
    }

    @Override
    protected void onDestroy() {
        if(progressDialog!=null &&  progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}