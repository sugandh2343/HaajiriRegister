package com.skillzoomer_Attendance.com.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaos.view.PinView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.skillzoomer_Attendance.com.Utilities.LocaleHelper;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityLoginBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    LayoutToolbarBinding toolbarBinding;
    public int counter = 600;
    public int LoginAttempt = 0;
    public String LoginTime = "";
    TextView txt_forcedLogout_msg;
    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
        @Override
        public void onTick(long l) {
            int minute = counter / 60;
            int sec = counter % 60;
            if (minute > 0) {
                txt_forcedLogout_msg.setText("" + minute + "minutes and " + sec + " seconds");
            } else {
                txt_forcedLogout_msg.setText("" + sec + " seconds");
            }
            counter--;

        }

        @Override
        public void onFinish() {
            Toast.makeText(context, "You have been forced logout. Contact Administrator for Re-login", Toast.LENGTH_SHORT).show();

        }
    };
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorLogin;
    private SharedPreferences.Editor editorPL;
    private SharedPreferences.Editor editorTutorial;
    private String[] designation = {"Select Designation", "HR Manager", "Clerk", "Supervisor"};
    private String selectedDesignation;
    private ProgressDialog progressDialog;
    private String verificationId;
    FirebaseAuth firebaseAuth;
    ModelUser modelUser;
    ImageView iv_profile;
    String mobile;
    private String loginValue;
    long siteId;
    String siteName, companyName, hrUid;
    private static final int Location_Request_code = 100;
    private static final int Camera_Request_code = 200;
    private static final int Storage_Request_code = 300;
    private static final int Image_pick_gallery_code = 400;
    private static final int Image_pick_camera_code = 500;
    //Permisssion Arrraya
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Bitmap image_uri;
    private Uri image_uri1;
    private final int REQUEST_IMAGE = 400;

    private LocationManager locationManager;
    private double latitude, longitude;


    FusedLocationProviderClient fusedLocationProviderClient;
    Context context;
    SharedPreferences spLogin;
    String name;
    private Boolean tutorial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
//        toolbarBinding = binding.toolbarLayout;
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        toolbarBinding.heading.setText("Login");

        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferencesPL = getSharedPreferences("PermanentLogin", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferencesTutorial = getSharedPreferences("Tutorial", Context.MODE_PRIVATE);
        tutorial = sharedPreferencesTutorial.getBoolean("timelineHr", true);


        editorPL = sharedpreferencesPL.edit();
        editorTutorial = sharedPreferencesTutorial.edit();
        spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editorLogin = spLogin.edit();

        if (sharedpreferencesPL.getString("userId", "") != null && !sharedpreferencesPL.getString("userId", "").equals("")) {
            binding.etUserId.setText(sharedpreferencesPL.getString("userId", ""));
            binding.etUserId.setEnabled(false);
            binding.etUserId.setFocusable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etUserId.setBackgroundColor(getColor(R.color.white));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etUserId.setTextColor(getColor(R.color.black));
            }


        } else {
            binding.etUserId.setText("");
            binding.etUserId.setEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.etUserId.setBackground(getDrawable(R.drawable.botton_half));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etUserId.setTextColor(getColor(R.color.white));
            }

        }

        Intent intent = getIntent();
        Log.e("Intent", "" + (intent == null));
        if (intent.getStringExtra("Activity") == null) {
            getDynamicLink();
        }


        Boolean firstTimeLogin = spLogin.getBoolean("LoginFirstTime", true);
        if (firstTimeLogin && intent.getStringExtra("Activity") != null) {
            binding.txtRegister.setVisibility(View.VISIBLE);
            binding.btnRegister.setVisibility(View.VISIBLE);
            binding.txtAnotherLogin.setVisibility(View.GONE);
        }
        MyApplication my = new MyApplication();


        binding.cbChangeLabguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.llLanguage.setVisibility(View.GONE);
                    if (sharedpreferences.getString("Language", "hi").equals("en")) {
                        editor.putString("Language", "hi");
                        editor.apply();
                        editor.commit();
                        context = LocaleHelper.setLocale(LoginActivity.this, "hi");
                        my.updateLanguage(LoginActivity.this, "hi");
                        finish();
                        startActivity(getIntent());
                    } else {
                        editor.putString("Language", "en");
                        editor.apply();
                        editor.commit();
                        context = LocaleHelper.setLocale(LoginActivity.this, "en");
                        my.updateLanguage(LoginActivity.this, "en");
                        finish();
                        startActivity(getIntent());
                    }

                }
            }
        });


//        Log.e("AfterDynamic","True");
//        if(firstTimeLogin && intent.getStringExtra("Activity")==null){
//            Log.e("AfterDynamic","Register");
//            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
//        }


        context = LocaleHelper.setLocale(LoginActivity.this, "hi");
//        if(firstTimeLogin){
//            binding.llLogin.setVisibility(View.GONE);
//            binding.btnSwapAdmin.setVisibility(View.GONE);
//            binding.btnRegister.setVisibility(View.VISIBLE);
//        }else{
//            binding.llLogin.setVisibility(View.VISIBLE);
//            binding.btnSwapAdmin.setVisibility(View.VISIBLE);
//            binding.btnRegister.setVisibility(View.GONE);
//        }

        editorLogin = spLogin.edit();
        editor = sharedpreferences.edit();
        loginValue = spLogin.getString("LastLogin", "n");
        Log.e("LoginValue", loginValue);
        if (sharedpreferences.getString("LastLogin", "n").equals("Register")) {
            editor.putBoolean("LoginFirstTime", true);
        }
        binding.txtAnotherLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorPL.putString("userId", "");
                editorPL.apply();
                editorPL.commit();
                binding.etUserId.setText("");
                binding.etUserId.setEnabled(true);
                binding.etUserId.setFocusable(true);
                binding.etUserId.setFocusableInTouchMode(true);
                binding.etUserId.setClickable(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.etUserId.setBackground(getDrawable(R.drawable.botton_half));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.etUserId.setTextColor(getColor(R.color.white));
                }

            }
        });

//        if (loginValue.equals("n")){
//            binding.llLogin.setVisibility(View.GONE);
//            binding.btnSwapAdmin.setVisibility(View.GONE);
//            binding.btnRegister.setVisibility(View.VISIBLE);
//        }else{
//            binding.llLogin.setVisibility(View.VISIBLE);
//            binding.btnSwapAdmin.setVisibility(View.VISIBLE);
//            binding.btnRegister.setVisibility(View.GONE);
//        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setContentView(R.layout.custom_progress_bar);
        binding.llMainLogin.setVisibility(View.VISIBLE);
        binding.llVerifyOtp.setVisibility(View.GONE);
        modelUser = new ModelUser();
//        binding.txtRegister.setVisibility(View.VISIBLE);
//        binding.btnRegister.setVisibility(View.VISIBLE);
        binding.txtMsgDisplay.setText(getString(R.string.login));
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
//        if(ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != (PackageManager.PERMISSION_GRANTED)){
//            requestLocationPermission();
//        }


        progressDialog.setCancelable(false);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
        binding.spinnerDesignation.setAdapter(spinnerAdapter);

        binding.spinnerDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedDesignation = designation[position];
//                if(selectedDesignation.equals("HR Manager")){
//                    binding.btnRegister.setVisibility(View.GONE);
//                    binding.txtRegister.setVisibility(View.GONE);
//                }else if(selectedDesignation.equals("Clerk")||selectedDesignation.equals("Supervisor")
//                || selectedDesignation.equals("Select Designation")){
//                    binding.btnRegister.setVisibility(View.VISIBLE);
//                    binding.txtRegister.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));


            }
        });
        binding.btnSwapAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ActivitySowap.class));
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etUserId.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, getString(R.string.enter_user_id_toast), Toast.LENGTH_SHORT).show();
                } else if (binding.etPassword.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, getString(R.string.password_cannot_be_empty), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    checkUserId(binding.etUserId.getText().toString(), binding.etPassword.getText().toString());
                }

            }
        });
        binding.verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = binding.MobileOtpPinview.getText().toString().trim();
                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.otp_empty), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    verifyCode(binding.MobileOtpPinview.getText().toString());
                }
            }
        });


    }

    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);

        // after getting credential we are
        // calling sign in method.

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                        } else {
                            progressDialog.dismiss();
                            Log.e("Task", "" + task.getException().getMessage().toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.some_error_occurred), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

//    private void showSelfieDialog() {
//
//        final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
//        View mView = getLayoutInflater().inflate(R.layout.supervisor_selfie_attendance, null);
//        Button btn_selfie = (Button) mView.findViewById(R.id.btn_selfie);
//        Button btn_upload = (Button) mView.findViewById(R.id.btn_upload);
//        txt_forcedLogout_msg = mView.findViewById(R.id.txt_forcedLogout_msg);
//        iv_profile = (ImageView) mView.findViewById(R.id.iv_profile);
//        btn_selfie.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                permissionCheck();
//            }
//        });
//
//        alert.setView(mView);
//        final AlertDialog alertDialog = alert.create();
//        alertDialog.setCanceledOnTouchOutside(true);
//        btn_upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (siteId > 0) {
//                    progressDialog.show();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        uploadAttendance(siteId);
//                    }
//                }
//            }
//        });
//
//
//        alertDialog.show();
//
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void uploadAttendance(long siteId) {
//        Log.e("latitudeUsing", "" + latitude);
//        Log.e("longitudeUsing", "" + longitude);
//        Log.e("siteIDUpload", "" + siteId);
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        image_uri.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//
//        String path = MediaStore.Images.Media.insertImage(
//                LoginActivity.this.getContentResolver(), image_uri, "IMG_" + System.currentTimeMillis(), null
//        );
//        image_uri1 = Uri.parse(path);
//        String timestamp = "" + System.currentTimeMillis();
//        String currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
//        String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
//        String filePathAndName = "Attendance/" + String.valueOf(siteId) + "/" + currentDate + "/" + "Supervisor";
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
//        storageReference.putFile(image_uri1)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!uriTask.isSuccessful()) ;
//                        Uri downloadImageUri = uriTask.getResult();
//                        if (uriTask.isSuccessful()) {
//                            Log.e("UploadAttendance", "" + latitude);
//                            Log.e("UploadAttendance", "" + longitude);
//                            HashMap<String, Object> hashMap = new HashMap<>();
//                            hashMap.put("uid", firebaseAuth.getUid());
//                            hashMap.put("online", true);
//                            hashMap.put("profile", "" + downloadImageUri);
//                            hashMap.put("timeStamp", "" + timestamp);
//                            hashMap.put("time", "" + currentTime);
//                            if (latitude > 0 && longitude > 0) {
//                                hashMap.put("memberLatitude", latitude);
//                                hashMap.put("memberLongitude", longitude);
//                            }
//                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Site");
//                            ref.child(String.valueOf(siteId)).child("Attendance")
//                                    .child(currentDate).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//
//                                            updateOnsiteMaster(String.valueOf(siteId), currentTime, timestamp);
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//
//
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Log.e("Exception", e.getMessage());
//
//                    }
//                });
//
//
//    }

//    private void updateOnsiteMaster(String siteId, String currentTime, String timestamp) {
//        String currentDate = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
//        }
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("uid", firebaseAuth.getUid());
//        hashMap.put("online", true);
//        hashMap.put("time", "" + currentTime);
//        hashMap.put("date", "" + currentDate);
//        if (latitude > 0 && longitude > 0) {
//            hashMap.put("memberLatitude", latitude);
//            hashMap.put("memberLongitude", longitude);
//        }
//
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Site");
//        ref.child(siteId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        progressDialog.dismiss();
//                        startActivity(new Intent(LoginActivity.this, MemberTimelineActivity.class));
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void permissionCheck() {
        Dexter.withActivity(LoginActivity.this)
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);
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
        Uri uri = Uri.fromParts("package", LoginActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void launchCameraIntentForTax() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_ Image Description");
        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
//            image_uri1=data.getData();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_uri = imageBitmap;
//            iv_profile.setImageURI(null);
            iv_profile.setImageBitmap(image_uri);


            //callProfileUpload(base64);
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
                        Location location = task.getResult();
                        if (location != null) {

                            try {
                                Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.ENGLISH);
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1


                                );
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();
                                Log.e("SourceLat", "Latitude" + latitude);
                                Log.e("SourceLat", "Longitude" + latitude);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.unable_to_detect_location), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } else {
            Toast.makeText(this, R.string.location_turne_off, Toast.LENGTH_SHORT).show();
            detectLocation();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);

    }

    private void findAddress(double latitude1, double longitude1) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            addresses = geocoder.getFromLocation(latitude1, longitude1, 1);
            Log.e("SiteIDAdd", "" + siteId);
//            updateLocationToSite();


        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void checkUserId(String userId, String password) {
        Intent intent = getIntent();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("userId").equalTo(binding.etUserId.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                modelUser = child.getValue(ModelUser.class);
                                Log.e("ModelUser", modelUser.getMobile());
                                Log.e("ModelUser", "UID:::::" + child.getKey());
                            }
                            SimpleDateFormat df = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                            }
                            Date c = Calendar.getInstance().getTime();
                            String currentDate = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                currentDate = df.format(c);
                            }

                            editorLogin.putString("LastLogin", currentDate);
                            editorLogin.commit();
                            mobile = modelUser.getMobile();
                            String password = modelUser.getPassword();
                            companyName = modelUser.getCompanyName();
                            name = modelUser.getName();
                            long industryPosition = modelUser.getIndustryPosition();
                            selectedDesignation = modelUser.getUserType();
                            Log.e("SelectedDesignation", selectedDesignation);
                            if (password.equals(binding.etPassword.getText().toString())) {
                                String phone = "+91" + mobile;
                                Log.e("mobile", mobile);
                                String email = userId + "@yopmail.com";
                                Log.e("Activity", "" + (intent.getStringExtra("Activity") == null));

                                if (industryPosition > 0 && industryPosition < 2) {
                                    if (selectedDesignation.equals("HR Manager") && (intent.getStringExtra("Activity") == null) && !spLogin.getString("LastLogin", "").equals("Register")) {
                                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        progressDialog.dismiss();
                                                        editor.putString("uid", firebaseAuth.getUid());
                                                        editor.putString("userName", binding.etUserId.getText().toString());
                                                        editor.putString("userDesignation", selectedDesignation);
                                                        editor.putString("userMobile", mobile);
                                                        editor.putString("companyName", companyName);
                                                        editor.putString("fullName", name);
                                                        Log.e("designation", modelUser.getDesignationName());
                                                        Log.e("designation", modelUser.getDesignationNameHindi());
                                                        if (modelUser.getDesignationPosition() > 0) {
                                                            editor.putString("designation", modelUser.getDesignationName());
                                                            editor.putString("designationHindi", modelUser.getDesignationNameHindi());
                                                            editor.putLong("designationPosition", modelUser.getDesignationPosition());
                                                        }
                                                        editor.putLong("industryPosition", modelUser.getIndustryPosition());
                                                        editor.putString("industryName", modelUser.getIndustryName());


                                                        editor.putLong("industryCount", modelUser.getIndustryCount());


                                                        if (modelUser.getWorkOpt() != 0) {
                                                            editor.putInt("workOption", 3);
                                                        }
                                                        Log.e("companyName", companyName);

//                                                    if (selectedDesignation.equals("Supervisor")) {
//                                                        Log.e("SiteIdUpdate", "" + siteId);
//                                                        editor.putLong("siteId", siteId);
//                                                        Log.e("SiteName",siteName);
//                                                        editor.putString("siteName", siteName);
//                                                        editor.putString("hrUid", hrUid);
//                                                        editor.apply();
//                                                        if (checkLocationPermission()) {
//                                                            progressDialog.setMessage("Please Wait");
//                                                            progressDialog.show();
//                                                           detectLocation();
//                                                        }
//                                                        else {
//                                                            requestLocationPermission();
//                                                        }
//                                                        countDownTimer.start();
//                                                        showSelfieDialog();
//
//                                                    } else {
                                                        editor.apply();
                                                        editorLogin.putBoolean("LoginFirstTime", false);
                                                        editorLogin.apply();
                                                        editorLogin.commit();
                                                        editorPL.putString("userId", binding.etUserId.getText().toString());
                                                        editorPL.apply();
                                                        editorPL.commit();
                                                        getFirebaseToken();

                                                        Log.e("Task", "successful");

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });

                                    } else if (selectedDesignation.equals("Supervisor")) {
                                        Log.e("UIDnew", modelUser.getHrUid());
                                        checkForForceLogout(modelUser.getSiteId(), modelUser.getUid(), modelUser.getHrUid());


                                    } else if (selectedDesignation.equals("HR Manager") && ((intent.getStringExtra("Activity") != null))) {
                                        Log.e("Snapshot123", "" + (modelUser.getWorkOpt()));
                                        if (modelUser.getWorkOpt() > 0) {
                                            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    Log.e("Snapshot", "here");
                                                    editor.putString("uid", firebaseAuth.getUid());
                                                    editor.putString("userName", binding.etUserId.getText().toString());
                                                    editor.putString("userDesignation", selectedDesignation);
                                                    editor.putString("userMobile", mobile);
                                                    editor.putString("companyName", companyName);
                                                    editor.putInt("workOption", 3);
                                                    editor.putLong("industryCount", modelUser.getIndustryCount());

                                                    if (modelUser.getDesignationPosition() > 0) {
                                                        editor.putString("designation", modelUser.getDesignationName());
                                                        editor.putString("designationHindi", modelUser.getDesignationNameHindi());
                                                        editor.putLong("designationPosition", modelUser.getDesignationPosition());
                                                    }

                                                    editor.putLong("industryPosition", modelUser.getIndustryPosition());
                                                    editor.putString("industryName", modelUser.getIndustryName());


                                                    editor.apply();
                                                    editor.commit();
                                                    editorLogin.putBoolean("LoginFirstTime", false);
                                                    editorLogin.apply();
                                                    editorLogin.commit();
                                                    editorPL.putString("userId", binding.etUserId.getText().toString());
                                                    editorPL.apply();
                                                    editorPL.commit();
                                                    getFirebaseToken();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Snapshot", e.getMessage());
                                                }
                                            });

                                        } else {
//                                        Intent intent=new Intent(LoginActivity.this,AdminLoginOptions.class);
//                                        intent.putExtra("userName", binding.etUserId.getText().toString());
//                                        intent.putExtra("userDesignation", selectedDesignation);
//                                        intent.putExtra("userMobile", mobile);
//                                        intent.putExtra("companyName", companyName);
//                                        intent.putExtra("password", binding.etPassword.getText().toString());
//                                        startActivity(intent);
                                            firebaseAuth.signInWithEmailAndPassword(binding.etUserId.getText().toString() + "@yopmail.com", password)
                                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                        @Override
                                                        public void onSuccess(AuthResult authResult) {
                                                            editor.putString("uid", firebaseAuth.getUid());
                                                            editor.putString("userName", binding.etUserId.getText().toString());
                                                            editor.putString("userDesignation", selectedDesignation);
                                                            editor.putString("userMobile", mobile);
                                                            editor.putString("companyName", companyName);
                                                            editor.putInt("workOption", 3);
                                                            editor.apply();
                                                            editor.commit();
                                                            editorPL.putString("userId", binding.etUserId.getText().toString());
                                                            editorPL.apply();
                                                            editorPL.commit();
                                                            if (modelUser.getDesignationPosition() > 0) {
                                                                editor.putString("designation", modelUser.getDesignationName());
                                                                editor.putString("designationHindi", modelUser.getDesignationNameHindi());
                                                                editor.putLong("designationPosition", modelUser.getDesignationPosition());
                                                            }

                                                            editor.putLong("industryPosition", modelUser.getIndustryPosition());
                                                            editor.putString("industryName", modelUser.getIndustryName());


                                                            getFirebaseToken();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }


                                    } else if (selectedDesignation.equals("Admin")) {
                                        startActivity(new Intent(LoginActivity.this, AdminDashboard.class));
                                    }
                                }
                                else {
                                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            editor.putString("uid", firebaseAuth.getUid());
                                            editor.putString("userName", binding.etUserId.getText().toString());
                                            editor.putString("userDesignation", selectedDesignation);
                                            editor.putString("userMobile", mobile);
                                            editor.putString("companyName", companyName);
                                            editor.putString("fullName", name);
                                            editor.putLong("industryPosition", modelUser.getIndustryPosition());
                                            editor.putString("industryName", modelUser.getIndustryName());
                                            editor.putString("designationName", modelUser.getDesignationName());
                                            if(selectedDesignation.equals("Employee")){
                                                editor.putString("hrUid",modelUser.getHrUid());
                                                editor.putString("tlUid",modelUser.getTlUid());
                                                editor.putLong("siteId",modelUser.getSiteId());

                                            }else if(selectedDesignation.equals("Associate")){
                                                editor.putString("hrUid",modelUser.getHrUid());
                                                editor.putString("tlUid",modelUser.getTlUid());

                                            }else{
                                                editor.putString("hrUid",firebaseAuth.getUid());
                                                editor.putString("tlUid",firebaseAuth.getUid());
                                            }

                                            editor.apply();
                                            editor.commit();
                                            editorLogin.putBoolean("LoginFirstTime", false);
                                            editorLogin.apply();
                                            editorLogin.commit();
                                            editorPL.putString("userId", binding.etUserId.getText().toString());
                                            editorPL.apply();
                                            editorPL.commit();
                                            FirebaseMessaging.getInstance().getToken()
                                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<String> task) {
                                                            if (!task.isSuccessful()) {
                                                                Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                                                                return;
                                                            }

                                                            // Get new FCM registration token
                                                            String token = task.getResult();

                                                            // Log and toast
                                                            //String msg = getString(R.string.msg_token_fmt, token);
                                                            Log.e("TAG", token);
                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                            hashMap.put("token", token);
                                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                                                            reference1.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            if (selectedDesignation.equals("HR Manager")) {


                                                                                startActivity(new Intent(LoginActivity.this, TimelineOtherIndustry1.class));
                                                                            } else if (selectedDesignation.equals("Associate")) {
                                                                                editor.putLong("siteId", modelUser.getSiteId());
                                                                                editor.putString("siteName", modelUser.getSiteName());
                                                                                editor.putString("hrUid", modelUser.getHrUid());
                                                                                editor.apply();
                                                                                editor.commit();
                                                                                Intent intent = new Intent(LoginActivity.this, WorkplaceActivity.class);

                                                                                intent.putExtra("siteId", modelUser.getSiteId());
                                                                                intent.putExtra("siteName", modelUser.getSiteName());
                                                                                startActivity(intent);

                                                                            } else {
                                                                                editor.putLong("siteId", modelUser.getSiteId());
                                                                                editor.putString("siteName", modelUser.getSiteName());
                                                                                editor.putString("hrUid", modelUser.getHrUid());
                                                                                startActivity(new Intent(LoginActivity.this, EmployeeDashboard.class));
                                                                            }
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                        }
                                                                    });


//                        Toast.makeText(RegistrationActivity.this, "" + token, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    });

                                }


//                                sendVerificationCode(phone);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                            }
//                                siteId = modelUser.getSiteId();
//                                siteName = modelUser.getSiteName();
//
//                                if (checkLocationPermission()) {
//                                    detectLocation();
//                                } else {
//                                    requestLocationPermission();
//                                }
//                               getSiteName(siteId);
//
//                                Log.e("siteId123", "" + siteId);
//                                Log.e("siteName123", "" + siteName);


                        } else {
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("InvitedMembers");
                            reference1.orderByChild("userId").equalTo(binding.etUserId.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getChildrenCount() > 0) {
                                        progressDialog.dismiss();
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            Log.e("snapshotLogin", "" + ds.child("industryPosition").getValue(long.class));
                                            if (ds.child("industryPosition").getValue(long.class) > 1) {
                                                if (ds.child("mobile").getValue(String.class).equals(binding.etPassword.getText().toString())) {
                                                    showChangePasswordDialog(ds);

                                                } else {
                                                    Toast.makeText(LoginActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                progressDialog.dismiss();

                                                Toast.makeText(LoginActivity.this, R.string.user_id_not_found, Toast.LENGTH_LONG).show();
                                                Log.e("snapshotChildren1", "" + snapshot.getChildrenCount());
                                            }
                                        }
                                    } else {
                                        progressDialog.dismiss();

                                        Toast.makeText(LoginActivity.this, R.string.user_id_not_found, Toast.LENGTH_LONG).show();
                                        Log.e("snapshotChildren1", "" + snapshot.getChildrenCount());
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void showChangePasswordDialog(DataSnapshot ds) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        View mView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_change_password, null);
        EditText et_company_name, et_user_id, et_mobile, et_password, et_confirm_password, et_aadhaar;
        PinView MobileOtp_Pinview;
        Button btn_send_otp, verify_otp, btn_login;
        LinearLayout ll_password, ll_verify_otp, ll_start;
        et_company_name = mView.findViewById(R.id.et_company_name);
        et_user_id = mView.findViewById(R.id.et_user_id);
        et_mobile = mView.findViewById(R.id.et_mobile);
        et_password = mView.findViewById(R.id.et_password);
        et_aadhaar = mView.findViewById(R.id.et_aadhaar);
        et_confirm_password = mView.findViewById(R.id.et_confirm_password);
        MobileOtp_Pinview = mView.findViewById(R.id.MobileOtp_Pinview);
        btn_send_otp = mView.findViewById(R.id.btn_send_otp);
        verify_otp = mView.findViewById(R.id.verify_otp);
        btn_login = mView.findViewById(R.id.btn_login);
        ll_password = mView.findViewById(R.id.ll_password);
        ll_verify_otp = mView.findViewById(R.id.ll_verify_otp);
        ll_start = mView.findViewById(R.id.ll_start);
        ll_start.setVisibility(View.VISIBLE);
        ll_password.setVisibility(View.GONE);
        ll_verify_otp.setVisibility(View.GONE);
        et_company_name.setEnabled(false);
        et_user_id.setEnabled(false);
        et_mobile.setEnabled(false);
        et_company_name.setText(ds.child("companyName").getValue(String.class));
        et_user_id.setText(ds.child("userId").getValue(String.class));
        et_mobile.setText(ds.child("mobile").getValue(String.class));
        String userId = ds.child("userId").getValue(String.class);
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String number = "+91" + et_mobile.getText().toString().trim();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        number,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        LoginActivity.this,               // Activity (for callback binding)
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                ll_start.setVisibility(View.GONE);
                                ll_password.setVisibility(View.GONE);
                                ll_verify_otp.setVisibility(View.VISIBLE);
                                verificationId = s;
                            }

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                                Log.e("Verification", "Completed");
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_start.setVisibility(View.GONE);
                ll_password.setVisibility(View.VISIBLE);
                ll_verify_otp.setVisibility(View.GONE);

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(et_password.getText().toString())) {
                    Toast.makeText(LoginActivity.this, getString(R.string.create_a_password), Toast.LENGTH_SHORT).show();
                } else if (!et_confirm_password.getText().toString().equals(et_password.getText().toString())) {
                    Toast.makeText(LoginActivity.this, getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    firebaseAuth.createUserWithEmailAndPassword(userId + "@yopmail.com", et_confirm_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            HashMap<String, Object> hashMap = new HashMap<>();
//                                    hashMap.put("userId", ds.child("userId").getValue(String.class));  //String
//                                    hashMap.put("name", ds.child("name").getValue(String.class));//String
//                                    hashMap.put("dept", ds.child("dept").getValue(String.class));//String
//                                    hashMap.put("designation", ds.child("designation").getValue(String.class));//Long
//                                    hashMap.put("userType", ds.child("userType").getValue(String.class));//String
//                                    hashMap.put("mobile", ds.child("mobile").getValue(String.class));//String
//                                    hashMap.put("doj", ds.child("doj").getValue(String.class));//String
//                                    hashMap.put("email", ds.child("email").getValue(String.class));//String
//                                    hashMap.put("workEmail", ds.child("workEmail").getValue(String.class));//String
//                                    hashMap.put("permanentEmployee", ds.child("permanentEmployee").getValue(Boolean.class));//String
//                                    hashMap.put("probationEmployee", ds.child("probationEmployee").getValue(Boolean.class));//String
//                                    hashMap.put("singleAttendance", ds.child("singleAttendance").getValue(Boolean.class));//Bool
//                                    hashMap.put("multiAttendance", ds.child("multiAttendance").getValue(Boolean.class));//Bool
//                                    hashMap.put("allAttendance", ds.child("allAttendance").getValue(Boolean.class));//Bool
//                                    hashMap.put("Sunday", ds.child("Sunday").getValue(Boolean.class));//Bool
//                                    hashMap.put("Monday", ds.child("Monday").getValue(Boolean.class));//Bool
//                                    hashMap.put("Tuesday", ds.child("Tuesday").getValue(Boolean.class));//Bool
//                                    hashMap.put("Wednesday", ds.child("Wednesday").getValue(Boolean.class));//Bool
//                                    hashMap.put("Thursday", ds.child("Thursday").getValue(Boolean.class));//Bool
//                                    hashMap.put("Friday", ds.child("Friday").getValue(Boolean.class));//Bool
//                                    hashMap.put("Saturday", ds.child("Saturday").getValue(Boolean.class));//Bool
//                                    hashMap.put("Salary", ds.child("Salary").getValue(String.class));//String
//                                    hashMap.put("registrationStatus", ds.child("registrationStatus").getValue(String.class));//String
//                                    hashMap.put("hrUid", ds.child("hrUid").getValue(String.class));//String
//                                    hashMap.put("siteId", ds.child("siteId").getValue(long.class));//Long
//                                    hashMap.put("grade", ds.child("grade").getValue(long.class));//Long
//                                    hashMap.put("siteName", ds.child("siteName").getValue(String.class));//String
//                                    hashMap.put("companyName", ds.child("companyName").getValue(String.class));//String
//                                    hashMap.put("industryPosition", ds.child("industryPosition").getValue(long.class));//Long
//                                    hashMap.put("industryName", ds.child("industryName").getValue(String.class));//String
//                                    hashMap.put("lastActivity", ds.child("lastActivity").getValue(String.class));//String
//                                    hashMap.put("leavePolicyId", ds.child("leavePolicyId").getValue(String.class));//String
//                                    hashMap.put("dob", ds.child("dob").getValue(String.class));//String
//                                    hashMap.put("leavePolicy",true);
//
//                                    hashMap.put("tlUid", ds.child("tlUid").getValue(String.class));//String
//                                    if (ds.hasChild("latitude")) {
//                                        hashMap.put("latitude", ds.child("latitude").getValue(long.class));//Long
//                                        hashMap.put("longitude", ds.child("longitude").getValue(long.class));//Long
//                                        hashMap.put("radius", ds.child("radius").getValue(long.class));//Long
//                                    }
//                            hashMap.put("addEmployee",ds.child("addEmployee").getValue(Boolean.class));
//                            hashMap.put("deleteEmployee",ds.child("deleteEmployee").getValue(Boolean.class));
//                            hashMap.put("leaveApproval",ds.child("leaveApproval").getValue(Boolean.class));
//                            hashMap.put("leavePolicy",ds.child("leavePolicy").getValue(Boolean.class));
//                            hashMap.put("salaryPolicy",ds.child("salaryPolicy").getValue(Boolean.class));
//                            hashMap.put("holidayList",ds.child("holidayList").getValue(Boolean.class));
//                            hashMap.put("eventAdd",ds.child("eventAdd").getValue(Boolean.class));
//                            hashMap.put("attendanceApproval",ds.child("attendanceApproval").getValue(Boolean.class));
                            hashMap.put("password", et_password.getText().toString());
                            hashMap.put("uid", firebaseAuth.getUid());
                            hashMap.put("aadhar", et_aadhaar.getText().toString());
                            String timestamp = "" + System.currentTimeMillis();
                            String currentDate = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                            }
                            hashMap.put("dateOfRegister", currentDate);//String
                            hashMap.put("timestamp", timestamp);//String

                            Log.e("FirebaseAuth", firebaseAuth.getUid());
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(firebaseAuth.getUid()).setValue(ds.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    reference.child(firebaseAuth.getUid()).updateChildren(hashMap);

                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("registrationStatus", "Registered");
                                    hashMap1.put("uid", firebaseAuth.getUid());
                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                                    reference1.child(ds.child("hrUid").getValue(String.class)).child("Industry")
                                            .child(ds.child("industryName").getValue(String.class))
                                            .child("Site").child(String.valueOf(ds.child("siteId").getValue(long.class))).child("Employee")
                                            .child(et_user_id.getText().toString()).updateChildren(hashMap1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    reference1.child(ds.child("hrUid").getValue(String.class)).child("Industry")
                                                            .child(ds.child("industryName").getValue(String.class))
                                                            .child("Site").child(String.valueOf(ds.child("siteId").getValue(long.class)))
                                                                    .child("Policy").child("Leave").child(ds.child("leavePolicyId").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                                    reference.child(firebaseAuth.getUid()).child("LeaveMaster").child(ds.child("leavePolicyId").getValue(String.class))
                                                                            .setValue(snapshot1.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    editor.putString("uid", firebaseAuth.getUid());
                                                                                    editor.putString("userName", et_user_id.getText().toString());
                                                                                    editor.putString("userDesignation", ds.child("userType").getValue(String.class));
                                                                                    editor.putString("userMobile", et_mobile.getText().toString());
                                                                                    editor.putString("companyName", ds.child("companyName").getValue(String.class));
                                                                                    editor.putLong("industryPosition", ds.child("industryPosition").getValue(long.class));
                                                                                    editor.putString("industryName", ds.child("industryName").getValue(String.class));
                                                                                    editor.putLong("siteId", ds.child("siteId").getValue(long.class));
                                                                                    Log.e("loginSiteId",""+ds.child("industryPosition").getValue(long.class));
                                                                                    editor.putString("siteName", ds.child("siteName").getValue(String.class));
                                                                                    editor.putInt("workOption", 3);
                                                                                    editor.putString("designationName", ds.child("designationName").getValue(String.class));
                                                                                    selectedDesignation=ds.child("userType").getValue(String.class);
                                                                                    if(selectedDesignation.equals("Employee")){
                                                                                        editor.putString("hrUid",ds.child("hrUid").getValue(String.class));
                                                                                        editor.putString("tlUid",ds.child("tlUid").getValue(String.class));

                                                                                    }else if(selectedDesignation.equals("Associate")){
                                                                                        editor.putString("hrUid",ds.child("hrUid").getValue(String.class));
                                                                                        editor.putString("tlUid",ds.child("tlUid").getValue(String.class));

                                                                                    }else{
                                                                                        editor.putString("hrUid",firebaseAuth.getUid());
                                                                                        editor.putString("tlUid",firebaseAuth.getUid());
                                                                                    }

                                                                                    editorLogin.putBoolean("LoginFirstTime", false);
                                                                                    editorLogin.apply();
                                                                                    editorLogin.commit();

                                                                                    editor.apply();
                                                                                    editor.commit();
                                                                                    editorPL.putString("userId", binding.etUserId.getText().toString());
                                                                                    editorPL.apply();
                                                                                    editorPL.commit();
                                                                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("InvitedMembers");
                                                                                    reference1.child(et_mobile.getText().toString()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            if (ds.child("userType").getValue(String.class).equals("Associate")) {
                                                                                                Intent intent = new Intent(LoginActivity.this, WorkplaceActivity.class);
                                                                                                intent.putExtra("siteId", ds.child("siteId").getValue(long.class));
                                                                                                intent.putExtra("siteName", ds.child("siteName").getValue(String.class));
                                                                                                startActivity(intent);
                                                                                            } else {
                                                                                                Intent intent = new Intent(LoginActivity.this, EmployeeDashboard.class);
                                                                                                intent.putExtra("siteId", ds.child("siteId").getValue(long.class));
                                                                                                intent.putExtra("siteName", ds.child("siteName").getValue(String.class));
                                                                                                startActivity(intent);

                                                                                            }

                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                }
                                            });

                                }
                            });



                        }
                    });
                }

                }



        });


        alertDialog.show();
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateToFirebase();
            }
        });
    }

    private void updateToFirebase() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("workOpt", 3);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                SimpleDateFormat df = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                }
                Date c = Calendar.getInstance().getTime();
                String currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentDate = df.format(c);
                }

                editorLogin.putString("LastLogin", currentDate);
                editorLogin.putBoolean("LoginFirstTime", false);
                editorLogin.commit();
                editor.putInt("workOption", 3);
                editor.apply();
                editor.commit();

                startActivity(new Intent(LoginActivity.this, AdminLoginOptions.class));
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForLoginOptions() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("userId").equalTo(binding.etUserId.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkForForceLogout(long siteId, String uid, String hrUid) {
        Log.e("uid", uid + siteId);
        Log.e("uidFT", hrUid);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(hrUid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean online = snapshot.child("online").getValue(Boolean.class);
                Log.e("Online", "" + online);
                Boolean forceLogout = false;
                if (snapshot.child("forceLogout").getValue(Boolean.class) != null) {
                    forceLogout = snapshot.child("forceLogout").getValue(Boolean.class);

                }
                String timestamp = "" + System.currentTimeMillis();
                String currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                String currentTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                }

                if (online != null && forceLogout != null && online || forceLogout) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.forced_logout_toast, Toast.LENGTH_SHORT).show();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("online", false);
                    hashMap.put("forceLogout", true);
                    hashMap.put("time", currentTime);
                    String finalCurrentTime = currentTime;
                    String finalCurrentDate = currentDate;
                    reference.child(hrUid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child(hrUid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("uid", uid);
                                    hashMap1.put("online", false);
                                    hashMap1.put("status", "Forced Logout");
                                    hashMap1.put("profile", "");
                                    hashMap1.put("timeStamp", "" + timestamp);
                                    hashMap1.put("time", "" + finalCurrentTime);
                                    hashMap1.put("name", "" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                                    hashMap1.put("memberLatitude", 0.0);
                                    hashMap1.put("memberLongitude", 0.0);
                                    reference.child(hrUid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child(finalCurrentDate).child(timestamp).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            finishAffinity();
                                        }
                                    });


                                }
                            });
                        }
                    });


                } else {
                    progressDialog.dismiss();
                    Log.e("LHR", hrUid);
                    Intent intent = new Intent(LoginActivity.this, LoginPic.class);
                    intent.putExtra("UserId", binding.etUserId.getText().toString());
                    intent.putExtra("Password", binding.etPassword.getText().toString());
                    intent.putExtra("SiteId", siteId);
                    intent.putExtra("hrUid", hrUid);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void getSiteName(long siteId) {
//        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("Site");
//        refrence.child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                siteName = snapshot.child("siteName").getValue(String.class);
//                hrUid = snapshot.child("hrUid").getValue(String.class);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressDialog.dismiss();
                        binding.txtMsgDisplay.setText("OTP sent to mobile:" + number);
                        binding.llMainLogin.setVisibility(View.GONE);
                        binding.llVerifyOtp.setVisibility(View.VISIBLE);
                        verificationId = s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                        Log.e("Verification", "Completed");
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return designation.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inf = getLayoutInflater();
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(designation[position]);

            return row;
        }
    }

    private void getDynamicLink() {
        Log.e("Dynamic", "Called");
        Log.e("Dynamic", "" + (FirebaseDynamicLinks.getInstance() == null) + "ABC:" + (FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).isCanceled()));
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        Toast.makeText(LoginActivity.this, "success get", Toast.LENGTH_SHORT).show();
                        // Get deep link from result (may be null if no link is found)
                        Log.e("Dynamic", "Success");
                        Uri deepLink = null;
                        Log.e("DeepLink", "" + (pendingDynamicLinkData == null));
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.e("DeepLink", "value" + deepLink.toString());
                        }
                        if (deepLink != null) {
                            int site_id = 0;
                            String admin = "", role = "";
//                            System.out.println("=============data=====");
//                            System.out.println(deepLink.toString());
                            String mob = deepLink.getQueryParameter("mobile");
                            site_id = Integer.parseInt(deepLink.getQueryParameter("site_id"));
                            admin = deepLink.getQueryParameter("admin");
                            String companyName = deepLink.getQueryParameter("company");
                            String userName = deepLink.getQueryParameter("memberName");
                            Log.e("companyName22", companyName);

                            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                            i.putExtra("mob", mob);
                            i.putExtra("admin", admin);
                            i.putExtra("site_id", site_id);
                            i.putExtra("role", "Supervisor");
                            i.putExtra("company", companyName);
                            i.putExtra("name", userName);

                            System.out.println("=================================data from link");
                            System.out.println("admin" + admin);
                            System.out.println("site" + site_id);
                            //Toast.makeText(getApplicationContext(), "site="+site_id, Toast.LENGTH_SHORT).show();
//                            System.out.println(mob);
                            startActivity(i);
                            finish();
                        } else {
                            SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

                            Boolean firstTimeLogin = spLogin.getBoolean("LoginFirstTime", true);
                            Intent intent = getIntent();

                            if (firstTimeLogin && intent.getStringExtra("Activity") == null) {
                                Log.e("AfterDynamic", "Register");
                                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                            }

                        }
                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DyException", e.getMessage());
                        // Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}