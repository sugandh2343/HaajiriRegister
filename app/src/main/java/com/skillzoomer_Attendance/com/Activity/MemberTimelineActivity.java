package com.skillzoomer_Attendance.com.Activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelMember;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MemberTimelineActivity extends AppCompatActivity implements LocationListener {
    private CardView btn_skilled_viewMasterData;
    private CardView work_activity;
    //    private Button btn_unskilled_viewMasterData;
    private CardView btn_skilled_takeAttendance;
    private CardView btn_skilled_payment;
    //    private Button btn_unskilled_takeAttendance;
    private CardView btn_viewAllAttendance;
    private CardView btn_add_new_worker;
    private CardView card_pending_labour;
    private EditText et_your_remark;
    private EditText et_your_remark1;

    //    private Button downloadAttendance;
//    private Button downloadAdvances;
//    private Button btn_fundrequest;
//    private Button btn_payment_activity;
//    private Button btn_view_advancesReprt;
//    private Button btn_compileList;
    private Button downloadCompile;
    private ProgressDialog progressDialog;
    private String[] designation = {"Supervisor"};
    private String selectedDesignation;
    String timestamp, siteName = "", companyName;
    long siteId = 0;
    Boolean result;
    private ArrayList<ModelLabour> labourList;
    private ArrayList<ModelLabour> skilledLabourList;
    private ArrayList<ModelLabour> unskilledLabourList;
    private ArrayList<ModelMember> memberArrayList;
    String userType;
    FirebaseAuth firebaseAuth;
    private SharedPreferences.Editor editor;
    private static final int PICK_CONTACT = 401;
    EditText et_name, et_mobile_number;
    private TextView txt_companyName, txt_siteName, txt_site_id, txt_forcedLogout_msg, hometxt, txt_associate_request, txt_admin_reply;
    private LinearLayout ll_after_locationVerify, before_locationVerify, ll_afterLocationDetected, ll_home, ll_manpower, ll_payment, ll6, ll_home1, ll_bottom_layout;
    private CardView ll5;
    private TextView txt_city, txt_pinCode, txt_address, txt_click_to_view, txt_skip, txt_force_logout_heading;
    private static final int Location_Request_code = 100;
    private LocationManager locationManager;
    private double latitude, longitude;
    private String[] locationPermissions;
    private Button btn_verify_location;
    //    private Button btn_skilled_payment,btn_unskilled_payment;
    private Button btn_submit;
    private androidx.appcompat.widget.Toolbar toolbar;
    private SharedPreferences.Editor editorLogin;
    private String lastLoginTime;
    private Boolean attendance_activity;
    private LinearLayout ll_forceLogout;
    private TextView txt_requestStatus;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final int REQUEST_CODE_SPEECH_INPUT1 = 11;

    public int counter = 3600;
    public int LoginAttempt = 0;
    private ImageView iv_associate_notify;
    long memberOnline = 0;
    public String LoginTime = "";
    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
        @Override
        public void onTick(long l) {
            Log.e("counter", "" + counter);
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
            forceLogout();

        }

    };
    int selected_option;
    private String activity;

    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_member_timeline_temp);

        btn_skilled_viewMasterData = findViewById(R.id.btn_skilled_viewMasterData);
        card_pending_labour = findViewById(R.id.card_pending_labour);
        txt_click_to_view = findViewById(R.id.txt_click_to_view);
        txt_admin_reply = findViewById(R.id.txt_admin_reply);
//        btn_unskilled_viewMasterData=findViewById(R.id.btn_unskilled_viewMasterData);
        btn_skilled_takeAttendance = findViewById(R.id.btn_skilled_takeAttendance);
//        btn_unskilled_takeAttendance=findViewById(R.id.btn_unskilled_takeAttendance);
        btn_viewAllAttendance = findViewById(R.id.btn_viewAllAttendance);
//        downloadAttendance=findViewById(R.id.downloadAttendance);
//        btn_fundrequest=findViewById(R.id.btn_fundrequest);
//        btn_payment_activity=findViewById(R.id.btn_payment_activity);
        txt_companyName = findViewById(R.id.txt_companyName);
        txt_siteName = findViewById(R.id.txt_siteName);
        txt_site_id = findViewById(R.id.txt_site_id);
//        btn_compileList=findViewById(R.id.btn_compileList);
        ll_after_locationVerify = findViewById(R.id.ll_after_locationVerify);
        before_locationVerify = findViewById(R.id.before_locationVerify);
        btn_verify_location = findViewById(R.id.btn_verify_location);
        ll_afterLocationDetected = findViewById(R.id.ll_afterLocationDetected);
        btn_skilled_payment = findViewById(R.id.btn_skilled_payment);
        btn_add_new_worker = findViewById(R.id.btn_add_new_worker);
        txt_force_logout_heading = findViewById(R.id.txt_force_logout_heading);
        txt_skip = findViewById(R.id.txt_skip);
        txt_associate_request = findViewById(R.id.txt_associate_request);
//        btn_unskilled_payment=findViewById(R.id.btn_unskilled_payment);
//        btn_view_advancesReprt=findViewById(R.id.btn_view_advancesReprt);
//        downloadCompile=findViewById(R.id.downloadCompile);
        txt_forcedLogout_msg = findViewById(R.id.txt_forcedLogout_msg);
        iv_associate_notify = findViewById(R.id.iv_associate_notify);
        ll6 = findViewById(R.id.ll6);
        ll5 = findViewById(R.id.ll5);
        firebaseAuth = FirebaseAuth.getInstance();

//        downloadAdvances=findViewById(R.id.downloadAdvances);
        ll_home = findViewById(R.id.ll_home);
        ll_manpower = findViewById(R.id.ll_manpower);
        ll_payment = findViewById(R.id.ll_payment);
        ll_home1 = findViewById(R.id.ll_home1);
        ll_bottom_layout = findViewById(R.id.ll_bottom_layout);
        hometxt = findViewById(R.id.hometxt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ll_home.setBackgroundColor(getColor(R.color.white));
        }
        ll_forceLogout = findViewById(R.id.ll_forceLogout);
        txt_requestStatus = findViewById(R.id.txt_requestStatus);
        work_activity = findViewById(R.id.work_activity);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        selected_option = sharedpreferences.getInt("workOption", 0);

        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editorLogin = spLogin.edit();
        siteName = sharedpreferences.getString("siteName", "");
        companyName = sharedpreferences.getString("companyName", "");
        siteId = sharedpreferences.getLong("siteId", 0);
        Intent intent = getIntent();
        userType = sharedpreferences.getString("userDesignation", "");
        if (userType.equals("Supervisor")) {
            uid = sharedpreferences.getString("hrUid", "");
        } else {
            uid = sharedpreferences.getString("uid", "");
        }

        Log.e("UIDNew", uid);
        if (!userType.equals("Supervisor")) {
            siteId = intent.getLongExtra("siteId", 0);
            siteName = intent.getStringExtra("siteName");
            Log.e("SelectedOPt123", "" + selected_option);
            if (selected_option > 0) {
                if (selected_option == 2) {
                    btn_skilled_viewMasterData.setVisibility(View.GONE);
                    work_activity.setVisibility(View.GONE);
                    btn_skilled_payment.setVisibility(View.GONE);
                    ll5.setVisibility(View.GONE);
                    ll_home1.setVisibility(View.VISIBLE);
                    hometxt.setText(getString(R.string.site_activity));
                    ll_bottom_layout.setWeightSum(4);
                    ll_home1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MemberTimelineActivity.this, timelineActivity.class));
                            finish();
                        }
                    });


                } else if (selected_option == 3) {
                    btn_skilled_viewMasterData.setVisibility(View.GONE);
                    work_activity.setVisibility(View.VISIBLE);
                    btn_skilled_payment.setVisibility(View.GONE);
                    ll5.setVisibility(View.GONE);
                    ll_home1.setVisibility(View.VISIBLE);
                    hometxt.setText(getString(R.string.site_activity));
                    ll_bottom_layout.setWeightSum(4);
                    ll_home1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MemberTimelineActivity.this, timelineActivity.class));
                            finish();
                        }
                    });
                }
            }
        }
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            config.setContentTextColor(getColor(R.color.white));
            config.setMaskColor(getColor(R.color.black));

        }
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(MemberTimelineActivity.this, "Register");


        sequence.setConfig(config);
        sequence.singleUse("memberTimeline");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (userType.equals("Supervisor")) {
                sequence.addSequenceItem(new MaterialShowcaseView.Builder(MemberTimelineActivity.this)
                        .setTarget(btn_skilled_viewMasterData)
                        .setGravity(Gravity.BOTTOM)
                        .withOvalShape()
                        .setShapePadding(10)
                        .setTargetTouchable(false)
                        .setContentText(getString(R.string.content_member1))// optional but starting animations immediately in onCreate can make them choppy
                        .setContentTextColor(getColor(R.color.white))
                        .setDismissOnTouch(true)

                        .build());
            }

            sequence.addSequenceItem(new MaterialShowcaseView.Builder(MemberTimelineActivity.this)
                    .setTarget(btn_add_new_worker)
                    .setGravity(Gravity.BOTTOM)
                    .withOvalShape()
                    .setShapePadding(10)
                    .setTargetTouchable(false)
                    .setContentText(getString(R.string.content_member2))// optional but starting animations immediately in onCreate can make them choppy
                    .setContentTextColor(getColor(R.color.white))
                    .setDismissOnTouch(true)
                    .build());
            sequence.addSequenceItem(new MaterialShowcaseView.Builder(MemberTimelineActivity.this)
                    .setTarget(btn_skilled_takeAttendance)
                    .setGravity(Gravity.BOTTOM)
                    .withOvalShape()
                    .setShapePadding(10)
                    .setTargetTouchable(false)
                    .setContentText(getString(R.string.content_member3))// optional but starting animations immediately in onCreate can make them choppy
                    .setContentTextColor(getColor(R.color.white))
                    .setDismissOnTouch(true)
                    .build());
            if (userType.equals("Supervisor")) {
                sequence.addSequenceItem(new MaterialShowcaseView.Builder(MemberTimelineActivity.this)
                        .setTarget(btn_skilled_payment)
                        .setGravity(Gravity.BOTTOM)
                        .withOvalShape()
                        .setShapePadding(10)
                        .setTargetTouchable(false)
                        .setContentText(getString(R.string.content_member4))// optional but starting animations immediately in onCreate can make them choppy
                        .setContentTextColor(getColor(R.color.white))
                        .setDismissOnTouch(true)
                        .build());
            }
            sequence.addSequenceItem(new MaterialShowcaseView.Builder(MemberTimelineActivity.this)
                    .setTarget(btn_viewAllAttendance)
                    .setGravity(Gravity.BOTTOM)
                    .withOvalShape()
                    .setShapePadding(10)
                    .setTargetTouchable(false)
                    .setContentText(getString(R.string.content_member5))// optional but starting animations immediately in onCreate can make them choppy
                    .setContentTextColor(getColor(R.color.white))
                    .setDismissOnTouch(true)
                    .build());
            sequence.addSequenceItem(new MaterialShowcaseView.Builder(MemberTimelineActivity.this)
                    .setTarget(work_activity)
                    .setGravity(Gravity.BOTTOM)
                    .withOvalShape()
                    .setShapePadding(10)
                    .setTargetTouchable(false)
                    .setContentText(getString(R.string.content_member6))// optional but starting animations immediately in onCreate can make them choppy
                    .setContentTextColor(getColor(R.color.white))
                    .setDismissOnTouch(true)
                    .build());

            sequence.start();
        }
        checkForTempLogout();
        checkForReply();

        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MemberTimelineActivity.this);
                builder.setTitle(R.string.warning);
                builder.setMessage(R.string.you_can_skip_this);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> {

                    updateToFirebaseSkipLocation();
                    dialog.cancel();

                });

                builder.show();
            }
        });
        ll6.setVisibility(View.GONE);
//        ll6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MemberTimelineActivity.this,AdminLoginOptions.class));
//            }
//        });
        ll_manpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getBoolean("attendanceManagement", true)) {
                    Intent intent = new Intent(MemberTimelineActivity.this, ManpowerHomeActivity.class);
                    intent.putExtra("type", "Skilled");
                    intent.putExtra("siteName", siteName);
                    intent.putExtra("siteId", siteId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MemberTimelineActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_add_new_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getBoolean("attendanceManagement", true)) {
                    Intent intent = new Intent(MemberTimelineActivity.this, LabourRegistration.class);
                    intent.putExtra("Activity", "Main");
                    if (!userType.equals("Supervisor")) {
                        intent.putExtra("type", userType);
                        intent.putExtra("siteName", siteName);
                        intent.putExtra("siteId", String.valueOf(siteId));
                    }
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(MemberTimelineActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
                }


            }
        });
        ll_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("Supervisor")) {
                    if (sharedpreferences.getBoolean("cashManagement", true) || sharedpreferences.getBoolean("expenseManagement", true)) {
                        Intent intent = new Intent(MemberTimelineActivity.this, AdvancesHomeActivity.class);
                        intent.putExtra("type", "Skilled");
                        intent.putExtra("siteName", siteName);
                        intent.putExtra("siteId", siteId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MemberTimelineActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (selected_option == 3) {
                        Intent intent = new Intent(MemberTimelineActivity.this, AdvancesHomeActivity.class);
                        intent.putExtra("SiteSpinner", true);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });
        txt_city = findViewById(R.id.txt_city);
        txt_pinCode = findViewById(R.id.txt_pinCode);
        txt_address = findViewById(R.id.txt_address);
        btn_submit = findViewById(R.id.btn_submit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_navigationbar1);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return false;
            }
        });
//        toolbar.setLogo(R.drawable.logo_toolbar);
        work_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userType.equals("Supervisor")) {
                    Intent intent = new Intent(MemberTimelineActivity.this, ChattingActivity.class);
                    intent.putExtra("siteId", siteId);
                    intent.putExtra("message", "Allow");
                    startActivity(intent);
                    finish();

                } else {
                    startActivity(new Intent(MemberTimelineActivity.this, ChattingActivity.class));
                    finish();
                }


            }
        });


        lastLoginTime = spLogin.getString("LastLoginTime", "NA");
        attendance_activity = spLogin.getBoolean("AttendanceActivity", false);
        Log.e("AttendanceActivity", "" + attendance_activity);
        Log.e("lastLoginTime", "" + lastLoginTime);
//
        ll_forceLogout.setVisibility(View.GONE);
        MyApplication my = new MyApplication();
        my.updateLanguage(this, sharedpreferences.getString("Language", "hi"));

        if (userType.equals("Supervisor")) {
            if (sharedpreferences.getBoolean("ForceLogout", false) || sharedpreferences.getBoolean("ForceLogoutTrial", false)) {
                if (sharedpreferences.getBoolean("ForceLogout", false)) {
                    checkForForceLogoutOpt();
                }

            }

            checkforPicActivity();
        }


        editor = sharedpreferences.edit();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);


        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        getMemberBlockStatus();


        getRequestStatus();
        getLabourPendingStatus();
        Log.e("siteIDMemberTimeline", "" + siteId);
        txt_companyName.setText(companyName);
        txt_site_id.setText(String.valueOf(siteId));
        txt_siteName.setText(String.valueOf(siteName));

        if (userType.equals("Supervisor")) {
            progressDialog.setMessage("Setting Up Timeline");
            progressDialog.show();
            checkIfSiteLocationIsVerified(siteId);
        } else {

            ll_after_locationVerify.setVisibility(View.VISIBLE);
            before_locationVerify.setVisibility(View.GONE);
            ll_afterLocationDetected.setVisibility(View.GONE);
        }

        btn_verify_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Detecting Your Location");
                progressDialog.show();
                if (checkLocationPermission()) {
                    activity = "siteLocation";
                    detectLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });


        Log.e("siteIdSP", "" + siteId);
        Log.e("userType", "" + (userType.equals("HR Manager") || userType.equals("Clerk")));
        if (siteName != null) {
            if (siteName.equals("") || siteId == 0) {
                siteName = intent.getStringExtra("siteName");
                siteId = intent.getLongExtra("siteID", 0);
                Log.e("siteIdIS", "" + siteId);
            }
        }

        Log.e("siteName11", "" + siteName);
        Log.e("siteId11", "" + siteId);
        Log.e("SiteActivity", "" + siteId);
        labourList = new ArrayList<>();
        skilledLabourList = new ArrayList<>();
        unskilledLabourList = new ArrayList<>();
        memberArrayList = new ArrayList<>();


        btn_skilled_viewMasterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getBoolean("attendanceManagement", true)) {
                    Intent intent = new Intent(MemberTimelineActivity.this, ViewMasterDataSheet.class);
                    intent.putExtra("siteId", String.valueOf(siteId));
                    intent.putExtra("siteName", siteName);
                    Log.e("SiteID786", "" + siteName + "::::" + siteId);
                    intent.putExtra("userType", "Skilled");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MemberTimelineActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_skilled_takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getBoolean("attendanceManagement", true)) {
                    Intent intent = new Intent(MemberTimelineActivity.this, ManpowerHomeActivity.class);
                    intent.putExtra("type", "Skilled");
                    intent.putExtra("siteName", siteName);
                    Log.e("MainSN", siteName);
                    intent.putExtra("siteId", siteId);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(MemberTimelineActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_viewAllAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MemberTimelineActivity.this, ReportHome.class));
                finish();
            }
        });


        btn_skilled_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getBoolean("cashManagement", true) || sharedpreferences.getBoolean("expenseManagement", true)) {
                    Intent intent = new Intent(MemberTimelineActivity.this, AdvancesHomeActivity.class);
                    intent.putExtra("type", "Skilled");
                    intent.putExtra("siteName", siteName);
                    intent.putExtra("siteId", siteId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MemberTimelineActivity.this, getString(R.string.you_do_not_have_authority), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void checkForReply() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Industry").child("Construction").child("Site");
        reference.child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("reply").getValue(Boolean.class) != null && (((snapshot.child("replyUid").getValue(String.class).equals(firebaseAuth.getUid())) && snapshot.child("reply").getValue(Boolean.class)))) {
                    ll5.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        new MaterialShowcaseView.Builder(MemberTimelineActivity.this)
                                .setTarget(txt_admin_reply)
                                .setGravity(Gravity.BOTTOM)
                                .withRectangleShape(true)
                                .setTargetTouchable(true)
                                .setContentText(getString(R.string.content_member7))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(getColor(R.color.white))
                                .setDismissTextColor(getColor(R.color.red))
                                .setDismissStyle(Typeface.DEFAULT_BOLD)
                                .singleUse("adminreply")
                                .show();
                    }
                    txt_admin_reply.setVisibility(View.VISIBLE);
                    if (getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Language", "hi").equals("en")) {
                        txt_admin_reply.setText(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrDesignation", "") + " " + getString(R.string.super_power_replied));

                    } else {
                        txt_admin_reply.setText(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrDesignationHindi", "") + " " + getString(R.string.super_power_replied));

                    }
                    iv_associate_notify.setImageResource(R.drawable.green);

                    txt_admin_reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("reply", false);
                            hashMap.put("replyUid", "");
                            reference.child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(MemberTimelineActivity.this, ChattingActivity.class));

                                }
                            });
                        }
                    });
                } else {
                    txt_admin_reply.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForTempLogout() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (userType.equals("Supervisor")) {
                    if (snapshot.child("tempLogout").getValue(Boolean.class) != null && snapshot.child("tempLogout").getValue(Boolean.class)) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MemberTimelineActivity.this);
                        builder.setTitle(R.string.log_out)
                                .setMessage(R.string.power_change_associate)
                                .setCancelable(false)
                                .setPositiveButton(R.string.yes, (dialogInterface, j) -> {

                                    logOutUser("");

                                });

                        builder.show();

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMemberBlockStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("memberBlock").getValue(String.class) != null && snapshot.child("memberBlock").getValue(String.class).equals("Blocked")) {
                    String message;
                    if (getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Language", "hi").equals("en")) {
                        message = getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrDesignation", "") + " " + "has blocked you.Unable to login";

                    } else {
                        message = "आपको" + " " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrDesignationHindi", "") + " " + "ने ब्लॉक कर दिया है|";

                    }
                    logOutUser(message);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void logOutUser(String message) {
        if (!message.equals("")) {
            String message1;
            if (getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Language", "hi").equals("en")) {
                message1 = getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrDesignation", "") + " " + "has blocked you.Unable to login";

            } else {
                message1 = "आपको" + " " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrDesignationHindi", "") + " " + "ने ब्लॉक कर दिया है|";

            }
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MemberTimelineActivity.this);
            builder.setTitle(R.string.log_out)
                    .setMessage(message1)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                        progressDialog.show();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                long memberOnline = snapshot.child("memberOnline").getValue(long.class);
                                if (memberOnline > 1) {
                                    hashMap.put("memberLatitude", 0.0);
                                    hashMap.put("memberLongitude", 0.0);
                                    hashMap.put("memberOnline", memberOnline - 1);
                                } else {
                                    hashMap.put("online", false);
                                    hashMap.put("memberLatitude", 0.0);
                                    hashMap.put("memberLongitude", 0.0);
                                    hashMap.put("memberOnline", memberOnline - 1);
                                }


                                reference.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                uploadToMember(hashMap, "ForceLogout");


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MemberTimelineActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    });

            builder.show();
        } else {
            progressDialog.show();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    long memberOnline = snapshot.child("memberOnline").getValue(long.class);
                    if (memberOnline > 1) {
                        hashMap.put("memberLatitude", 0.0);
                        hashMap.put("memberLongitude", 0.0);
                        hashMap.put("memberOnline", memberOnline - 1);
                    } else {
                        hashMap.put("online", false);
                        hashMap.put("memberLatitude", 0.0);
                        hashMap.put("memberLongitude", 0.0);
                        hashMap.put("memberOnline", memberOnline - 1);
                    }


                    reference.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    firebaseAuth.signOut();
                                    uploadToMember(hashMap, "ForceLogout");


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MemberTimelineActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }

    private void updateToFirebaseSkipLocation() {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("locationSkip", true);
        hashMap.put("skipTime", currentTime);
        hashMap.put("locationVerify", false);
        hashMap.put("locationVerifyConfirm", false);

        String finalCurrentTime = currentTime;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ll_after_locationVerify.setVisibility(View.VISIBLE);
                        before_locationVerify.setVisibility(View.GONE);
                        ll_afterLocationDetected.setVisibility(View.GONE);

                        editorLogin.putString("LocationSkipTime", finalCurrentTime);
                        editorLogin.apply();
                        editorLogin.commit();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                });
    }

    private void checkforPicActivity() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("workOpt")) {
                    if (snapshot.child("workOpt").getValue(Boolean.class)) {
                        work_activity.setVisibility(View.VISIBLE);
                        editorLogin.putBoolean("PicActivity", false);
                    } else {
                        ll_forceLogout.setVisibility(View.GONE);
                        editorLogin.putBoolean("PicActivity", true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForForceLogoutOpt() {
        Log.e("SiteId", String.valueOf(siteId));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("forceLogout")) {
                    if (snapshot.child("forceLogout").getValue(Boolean.class)) {
                        forceLogout();
                    } else {
                        ll_forceLogout.setVisibility(View.VISIBLE);
                        startTimer("Start", 0);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startTimer(String type, long days) {
        if (type.equals("Start")) {
            if (!attendance_activity) {

                if (!lastLoginTime.equals("NA")) {
                    ll_forceLogout.setVisibility(View.VISIBLE);
                    String currentTime = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                    }
//                    lastLoginTime=currentTime;
                    java.text.DateFormat df = new java.text.SimpleDateFormat("HH:mm");
                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1 = df.parse(currentTime);
                        date2 = df.parse(lastLoginTime);
                    } catch (ParseException e) {
                        Log.e("DateException", e.getMessage());
                        e.printStackTrace();
                    }

                    long diff = date1.getTime() - date2.getTime();
                    String currentTime1 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(diff);
                    }
                    Log.e("CurrentTime1", currentTime1);
                    Log.e("Date1", "" + date1.getTime());
                    Log.e("Date2", "" + date2.getTime());
                    int timeInSeconds = (int) (diff / 1000);
                    Log.e("timeInSeconds", "" + timeInSeconds);
//                    int hours, minutes, seconds;
//                    hours = timeInSeconds / 3600;
//                    timeInSeconds = timeInSeconds - (hours * 3600);
//                    minutes = timeInSeconds / 60;
//                    timeInSeconds = timeInSeconds - (minutes * 60);
//                    seconds = timeInSeconds;
                    counter = 1200 - timeInSeconds;
                    if (counter <= 0) {
                        Log.e("ForceLogout", "ForceLogout");
                        forceLogout();
                    }
                    countDownTimer.onTick(counter);
                    countDownTimer.start();

                }

            } else {
                ll_forceLogout.setVisibility(View.GONE);
                countDownTimer.cancel();
            }
        } else if (type.equals("Free")) {
            if (!attendance_activity) {

                if (!lastLoginTime.equals("NA")) {
                    ll_forceLogout.setVisibility(View.VISIBLE);

//                    counter=100*60*1000-(int)hours*60*1000;
                    txt_forcedLogout_msg.setText((String.valueOf(4 - (int) days)) + " " + "days");
                    if (counter <= 0) {
                        Log.e("ForceLogout", "ForceLogout");
                        Toast.makeText(this, getString(R.string.free_trial_force_logout_complete), Toast.LENGTH_SHORT).show();
                    }
//                    countDownTimer.onTick(counter);
//                    countDownTimer.start();

                }

            }


        }

    }

    private void getLabourPendingStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").orderByChild("status").equalTo("Pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0 && getSharedPreferences("UserDetails", MODE_PRIVATE).getBoolean("attendanceManagement", false)) {
                    card_pending_labour.setVisibility(View.VISIBLE);
                    txt_click_to_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO direct to activity
                            Intent intent = new Intent(MemberTimelineActivity.this, PendingLabourActivity.class);
                            intent.putExtra("siteId", siteId);
                            intent.putExtra("siteName", siteName);
                            startActivity(intent);
                            finish();

                        }
                    });
                } else {
                    card_pending_labour.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRequestStatus() {
        Log.e("SiteAss", "" + siteId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("reqStatus") && getSharedPreferences("UserDetails", MODE_PRIVATE).getBoolean("cashManagement", false)) {
                    iv_associate_notify.setImageResource(R.drawable.green);
                    String reqStatus = snapshot.child("reqStatus").getValue(String.class);
                    String reqAmt = snapshot.child("reqAmt").getValue(String.class);
                    if (reqStatus.equals("Approved") || reqStatus.equals("ApprovedPartial")) {
                        txt_requestStatus.setVisibility(View.VISIBLE);
                        txt_requestStatus.setText(getResources().getString(R.string.your_request_of) + " " + getString(R.string.rupee_symbol) + " " + reqAmt + " " +
                                getResources().getString(R.string.is_accepted));
                    } else if (reqStatus.equals("Denied")) {
                        txt_requestStatus.setText(getResources().getString(R.string.your_request_of) + " " + getString(R.string.rupee_symbol) + " " + reqAmt + " " +
                                getResources().getString(R.string.is_denied));
                    } else if (reqStatus.equals("Hold")) {
                        txt_requestStatus.setText(getResources().getString(R.string.your_request_of) + " " + getString(R.string.rupee_symbol) + " " + reqAmt + " " +
                                getResources().getString(R.string.is_on_hold));
                    }

                } else {
                    txt_requestStatus.setVisibility(View.GONE);
                }
                Log.e("SiteAss", "" + snapshot.hasChild("associateRequest"));
                if (snapshot.hasChild("associateRequest")) {
                    if (snapshot.child("associateRequestType").getValue(String.class) != null && snapshot.child("associateRequestType").getValue(String.class).equals("PayableAmount")
                            && getSharedPreferences("UserDetails", MODE_PRIVATE).getBoolean("expenseManagement", false)) {
                        iv_associate_notify.setImageResource(R.drawable.green);
                        Log.e("SiteAss", snapshot.child("associateRequestStatus").getValue(String.class));
                        if (snapshot.child("associateRequestStatus").getValue(String.class).equals("Allowed")) {
                            Log.e("Snapshot", "Allowed");
                            String toPayAmount = snapshot.child("toPayAmount").getValue(String.class);
                            long payableAmount = 0;
                            String labourIdPaying = "";
                            if (snapshot.child("payableAmount").getValue(long.class) != null) {
                                payableAmount = snapshot.child("payableAmount").getValue(long.class);
                                labourIdPaying = snapshot.child("labourIdPaying").getValue(String.class);
                            }

                            txt_associate_request.setVisibility(View.VISIBLE);

                            if (snapshot.child("associateRequestType").getValue(String.class).equals("PayableAmount")) {
                                txt_associate_request.setText(getString(R.string.your_request_to_pay) + " " + snapshot.child("toPayAmount").getValue(String.class) + getString(R.string.is_reconcile_accepted));
                            } else {
                                txt_associate_request.setText(getString(R.string.request_associate_reconcile) + " " + snapshot.child("toPayAmount").getValue(String.class) + " " + getString(R.string.is_reconcile_accepted));
                            }

                            long finalPayableAmount = payableAmount;
                            String finalLabourIdPaying = labourIdPaying;
                            txt_associate_request.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e("RequestType", "AAAA" + snapshot.child("associateRequestType").getValue(String.class));
                                    showRequestDialog("Allowed", toPayAmount, finalPayableAmount, finalLabourIdPaying, snapshot.child("associateRequestType").getValue(String.class));
                                }
                            });

                        } else if (snapshot.child("associateRequestStatus").getValue(String.class).equals("AskWhy")) {
                            String toPayAmount = snapshot.child("toPayAmount").getValue(String.class);
                            long payableAmount = 0;
                            String labourIdPaying = "";
                            if (snapshot.child("associateRequestType").getValue(String.class).equals("PayableAmount")) {
                                payableAmount = snapshot.child("payableAmount").getValue(long.class);
                                labourIdPaying = snapshot.child("labourIdPaying").getValue(String.class);
                            }

                            txt_associate_request.setVisibility(View.VISIBLE);
                            txt_associate_request.setText(R.string.admin_wants_to_know);
                            long finalPayableAmount = payableAmount;
                            String finalLabourIdPaying = labourIdPaying;
                            txt_associate_request.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showRequestDialog("AskWhy", toPayAmount, finalPayableAmount, finalLabourIdPaying, snapshot.child("associateRequestType").getValue(String.class));
                                }
                            });


                        }

                    } else if (snapshot.child("associateRequestType").getValue(String.class) != null && snapshot.child("associateRequestType").getValue(String.class).equals("Reconcile")
                            && getSharedPreferences("UserDetails", MODE_PRIVATE).getBoolean("cashManagement", false)) {
                        iv_associate_notify.setImageResource(R.drawable.green);
                        Log.e("SiteAss", snapshot.child("associateRequestStatus").getValue(String.class));
                        if (snapshot.child("associateRequestStatus").getValue(String.class).equals("Allowed")) {
                            Log.e("Snapshot", "Allowed");
                            String toPayAmount = snapshot.child("toPayAmount").getValue(String.class);
                            long payableAmount = 0;
                            String labourIdPaying = "";
                            if (snapshot.child("payableAmount").getValue(long.class) != null) {
                                payableAmount = snapshot.child("payableAmount").getValue(long.class);
                                labourIdPaying = snapshot.child("labourIdPaying").getValue(String.class);
                            }

                            txt_associate_request.setVisibility(View.VISIBLE);
                            if (snapshot.child("associateRequestType").getValue(String.class).equals("PayableAmount")) {
                                txt_associate_request.setText(getString(R.string.your_request_to_pay) + " " + snapshot.child("toPayAmount").getValue(String.class) + getString(R.string.is_reconcile_accepted));
                            } else {
                                txt_associate_request.setText(getString(R.string.request_associate_reconcile) + " " + snapshot.child("toPayAmount").getValue(String.class) + " " + getString(R.string.is_reconcile_accepted));
                            }

                            long finalPayableAmount = payableAmount;
                            String finalLabourIdPaying = labourIdPaying;
                            txt_associate_request.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e("RequestType", "AAAA" + snapshot.child("associateRequestType").getValue(String.class));
                                    showRequestDialog("Allowed", toPayAmount, finalPayableAmount, finalLabourIdPaying, snapshot.child("associateRequestType").getValue(String.class));
                                }
                            });

                        } else if (snapshot.child("associateRequestStatus").getValue(String.class).equals("AskWhy")) {
                            String toPayAmount = snapshot.child("toPayAmount").getValue(String.class);
                            long payableAmount = 0;
                            String labourIdPaying = "";
                            if (snapshot.child("associateRequestType").getValue(String.class).equals("PayableAmount")) {
                                payableAmount = snapshot.child("payableAmount").getValue(long.class);
                                labourIdPaying = snapshot.child("labourIdPaying").getValue(String.class);
                            }

                            txt_associate_request.setVisibility(View.VISIBLE);
                            txt_associate_request.setText(R.string.admin_wants_to_know);
                            long finalPayableAmount = payableAmount;
                            String finalLabourIdPaying = labourIdPaying;
                            txt_associate_request.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showRequestDialog("AskWhy", toPayAmount, finalPayableAmount, finalLabourIdPaying, snapshot.child("associateRequestType").getValue(String.class));
                                }
                            });


                        }

                    }

                } else {
                    txt_associate_request.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showRequestDialog(String askWhy, String toPayAmount, long payableAmount, String labourIdPaying, String requestType) {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MemberTimelineActivity.this);
        View mView = LayoutInflater.from(MemberTimelineActivity.this).inflate(R.layout.layout_payable_amount_dialog_associate, null);
        alert.setView(mView);
        TextView txt_labourId, txt_to_pay_amount, txt_payable_amount, txt_reconcile_amount;
        Button btn_allow, btn_cancel, btn_update, btn_reconcile_update;
        LinearLayout ll_allowed, ll_askWhy, ll_reconcile, ll_payable_amount, ll_askWhy1;
        ImageView iv_mic, iv_mic1;
        txt_labourId = mView.findViewById(R.id.txt_labourId);
        txt_to_pay_amount = mView.findViewById(R.id.txt_to_pay_amount);
        txt_payable_amount = mView.findViewById(R.id.txt_payable_amount);
        btn_allow = mView.findViewById(R.id.btn_allow);
        btn_cancel = mView.findViewById(R.id.btn_cancel);
        ll_reconcile = mView.findViewById(R.id.ll_reconcile);
        ll_payable_amount = mView.findViewById(R.id.ll_payable_amount);
        txt_reconcile_amount = mView.findViewById(R.id.txt_reconcile_amount);
        btn_reconcile_update = mView.findViewById(R.id.btn_reconcile_update);
        txt_labourId.setText(labourIdPaying);
        txt_to_pay_amount.setText(toPayAmount);
        txt_payable_amount.setText(String.valueOf(payableAmount));
        btn_update = mView.findViewById(R.id.btn_update);
        et_your_remark = mView.findViewById(R.id.et_your_remark);
        et_your_remark1 = mView.findViewById(R.id.et_your_remark1);
        iv_mic = mView.findViewById(R.id.iv_mic);
        iv_mic1 = mView.findViewById(R.id.iv_mic1);
        ll_allowed = mView.findViewById(R.id.ll_allowed);
        ll_askWhy = mView.findViewById(R.id.ll_askWhy);
        ll_askWhy1 = mView.findViewById(R.id.ll_askWhy1);
        Log.e("RequestType", requestType);
        if (requestType.equals("PayableAmount")) {
            ll_reconcile.setVisibility(View.GONE);
            ll_payable_amount.setVisibility(View.VISIBLE);
            if (askWhy.equals("AskWhy")) {
                ll_askWhy.setVisibility(View.VISIBLE);
                ll_allowed.setVisibility(View.GONE);
            } else if (askWhy.equals("Allowed")) {
                ll_askWhy.setVisibility(View.GONE);
                ll_allowed.setVisibility(View.VISIBLE);
            }
        } else {
            ll_reconcile.setVisibility(View.VISIBLE);
            ll_payable_amount.setVisibility(View.GONE);
            txt_reconcile_amount.setText(toPayAmount);
            if (askWhy.equals("AskWhy")) {
                ll_askWhy1.setVisibility(View.VISIBLE);
            } else if (askWhy.equals("Allowed")) {
                ll_askWhy1.setVisibility(View.GONE);
            }
        }
        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);


        btn_reconcile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (askWhy.equals("AskWhy")) {
                    if (TextUtils.isEmpty(et_your_remark1.getText().toString())) {
                        Toast.makeText(MemberTimelineActivity.this, getString(R.string.enter_reason), Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("associateRequestReason", et_your_remark1.getText().toString());
                        hashMap.put("associateRequest", true);
                        hashMap.put("associateRequestType", "Reconcile");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                alertDialog.dismiss();
                            }
                        });
                    }
                } else if (askWhy.equals("Allowed")) {
                    Intent intent = new Intent(MemberTimelineActivity.this, ReceiveCashActivity.class);
                    intent.putExtra("Reconcile", true);
                    intent.putExtra("Amount", toPayAmount);
                    startActivity(intent);
                    finish();
                }


            }
        });
        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.ENGLISH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");


                try {

                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(MemberTimelineActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        iv_mic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.ENGLISH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");


                try {

                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT1);
                } catch (Exception e) {
                    Toast
                            .makeText(MemberTimelineActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_your_remark.getText().toString())) {
                    Toast.makeText(MemberTimelineActivity.this, getString(R.string.enter_reason), Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("associateRequestReason", et_your_remark.getText().toString());
                    hashMap.put("associateRequest", true);
                    hashMap.put("associateRequestType", "PayableAmount");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });

        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                reference1.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.setMessage(getString(R.string.please_wait));
                        progressDialog.show();
                        long cashInHand = snapshot.child("cashInHand").getValue(long.class);
                        long toPayAmount = (long) Integer.parseInt(txt_to_pay_amount.getText().toString());
                        if (toPayAmount > cashInHand) {
                            String currentTime = "";
                            String currentDate = "";
                            String timestamp = "" + System.currentTimeMillis();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                            }
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("date", currentDate);
                            hashMap.put("time", currentTime);
                            hashMap.put("remark", "InsufficientFund");
                            hashMap.put("amount", txt_to_pay_amount.getText().toString());
                            hashMap.put("cashInHand", String.valueOf(cashInHand));
                            hashMap.put("name", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                            hashMap.put("uid", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("uid", ""));
                            hashMap.put("timestamp", timestamp);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Error").child(currentDate).child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            alertDialog.dismiss();
                                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MemberTimelineActivity.this);
                                            builder.setTitle(R.string.insufficient_fund_heading)
                                                    .setMessage(R.string.insufficient_fund)
                                                    .setCancelable(false)
                                                    .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {

                                                        dialogInterface.dismiss();

                                                    })
                                                    .setNegativeButton(getString(R.string.fund_request), (dialogInterface, i) -> {
                                                        startActivity(new Intent(MemberTimelineActivity.this, FundRequestActivity.class));

                                                    });
                                            builder.show();
                                            progressDialog.dismiss();
                                        }
                                    });
//                            Toast.makeText(MemberTimelineActivity.this, getString(R.string.insufficient_fund), Toast.LENGTH_SHORT).show();

                        } else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(txt_labourId.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String labourName = snapshot.child("name").getValue(String.class);
                                    String type = snapshot.child("type").getValue(String.class);
                                    String timestamp = "" + System.currentTimeMillis();
                                    String currentTime = "";
                                    String currentDate = "";
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                                        currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                                    }
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("date", currentDate);
                                    hashMap.put("time", currentTime);
                                    hashMap.put("amount", txt_to_pay_amount.getText().toString());
                                    hashMap.put("labourId", txt_labourId.getText().toString());
                                    hashMap.put("labourName", labourName);
                                    hashMap.put("labourType", type);
                                    hashMap.put("uploadedByUid", firebaseAuth.getUid());
                                    hashMap.put("uploadedByType", userType);
                                    hashMap.put("uploadedByName", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                                    hashMap.put("timestamp", timestamp);

                                    String finalCurrentTime = currentTime;
                                    String finalCurrentTime1 = currentTime;
                                    String finalCurrentDate = currentDate;
                                    String finalCurrentDate1 = currentDate;
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(currentDate).child(timestamp).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("date", finalCurrentDate1);
                                                    hashMap.put("time", finalCurrentTime);
                                                    hashMap.put("amount", txt_to_pay_amount.getText().toString());
                                                    hashMap.put("labourId", txt_labourId.getText().toString());
                                                    hashMap.put("labourName", labourName);
                                                    hashMap.put("labourType", type);
                                                    hashMap.put("timestamp", timestamp);
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Industry").child("Construction").child("Site");
                                                    reference.child(String.valueOf(siteId)).child("Labours").child(txt_labourId.getText().toString()).child("Payments").child(finalCurrentDate1).child(timestamp)
                                                            .setValue(hashMap);

                                                    DatabaseReference reference1 = reference.child(String.valueOf(siteId)).child("Labours").child(txt_labourId.getText().toString()).child("payableAmt");
                                                    DatabaseReference reference2 = reference.child(String.valueOf(siteId)).child("cashInHand");
                                                    DatabaseReference reference3 = reference.child(String.valueOf(siteId)).child("paymentSum");
                                                    long Amount = (long) Integer.parseInt(txt_to_pay_amount.getText().toString());
                                                    reference1.setValue(ServerValue.increment(-Amount));
                                                    reference2.setValue(ServerValue.increment(-Amount));
                                                    reference3.setValue(ServerValue.increment(Amount));

                                                    reference.child(String.valueOf(siteId)).child("Cash").child("Expense").child(finalCurrentDate1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            long expenseId = 100;
                                                            if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                                                expenseId = (snapshot.getChildrenCount() + 1) * 100;
                                                            }
                                                            String recId = "" + siteId + expenseId;
                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                            hashMap.put("expId", recId);
                                                            hashMap.put("expType", "Labour");
                                                            hashMap.put("amount", txt_to_pay_amount.getText().toString());
                                                            hashMap.put("expTime", finalCurrentTime1);
                                                            hashMap.put("expDate", finalCurrentDate);
                                                            hashMap.put("expRemark", "Payment To Labour");
                                                            hashMap.put("entryByUid", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("uid", ""));
                                                            hashMap.put("entryByName", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userName", ""));
                                                            hashMap.put("entryByType", userType);
                                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                                            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Expense").child(finalCurrentDate).child(recId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                                                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequest").removeValue();
                                                                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequestType").removeValue();
                                                                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("toPayAmount").removeValue();
                                                                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("payableAmount").removeValue();
                                                                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("labourIdPaying").removeValue();
                                                                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequestStatus").removeValue();
                                                                    progressDialog.dismiss();
                                                                    alertDialog.dismiss();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Failure", e.getMessage());
                                                    Toast.makeText(MemberTimelineActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
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
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequest").removeValue();
                reference.child(String.valueOf(siteId)).child("associateRequestType").removeValue();
                reference.child(String.valueOf(siteId)).child("toPayAmount").removeValue();
                reference.child(String.valueOf(siteId)).child("payableAmount").removeValue();
                reference.child(String.valueOf(siteId)).child("labourIdPaying").removeValue();
                reference.child(String.valueOf(siteId)).child("associateRequestStatus").removeValue();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();


    }


    private void permissionCheck() {
        Dexter.withActivity(MemberTimelineActivity.this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        Log.e("Denied",""+report.getDeniedPermissionResponses().get(0).getPermissionName());
                        if (report.areAllPermissionsGranted()) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MemberTimelineActivity.this);
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
        Uri uri = Uri.fromParts("package", MemberTimelineActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void checkIfSiteLocationIsVerified(long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("LocationVerify", "" + snapshot.child("locationVerify").getValue(Boolean.class));
                siteName = snapshot.child("siteCity").getValue(String.class);
                txt_siteName.setText(siteName);
                if (snapshot.hasChild("locationVerify")) {
                    progressDialog.dismiss();
                    ll_after_locationVerify.setVisibility(View.VISIBLE);
                    before_locationVerify.setVisibility(View.GONE);
                    ll_afterLocationDetected.setVisibility(View.GONE);

                } else {
                    progressDialog.dismiss();
                    ll_after_locationVerify.setVisibility(View.GONE);
                    ll_afterLocationDetected.setVisibility(View.GONE);
                    before_locationVerify.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "Detecting Location", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Your Location is Turned Off.", Toast.LENGTH_SHORT).show();
            detectLocation();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);
    }

    private void findAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            if (latitude > 0) {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String pincode = addresses.get(0).getPostalCode();
                txt_city.setText(city);
                txt_address.setText(address);
                txt_pinCode.setText(pincode);
                if (activity.equals("siteLocation")) {
                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            progressDialog.setMessage("Submitting Details");
                            progressDialog.show();
                            submitToSite();
                        }

                    });
                } else if (activity.equals("normalLogout")) {
                    String currentTime = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    if (memberOnline > 1) {
                        hashMap.put("memberLatitude", latitude);
                        hashMap.put("memberLongitude", longitude);
                        hashMap.put("memberOnline", memberOnline - 1);
                        hashMap.put("uid", firebaseAuth.getUid());

                    } else {
                        hashMap.put("online", false);
                        hashMap.put("memberLatitude", latitude);
                        hashMap.put("memberLongitude", longitude);
                        hashMap.put("memberOnline", memberOnline - 1);
                        hashMap.put("time", currentTime);
                        hashMap.put("uid", firebaseAuth.getUid());
                    }


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    firebaseAuth.signOut();
                                    uploadToMember(hashMap, activity);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MemberTimelineActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }


            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void submitToSite() {
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        Log.e("UpdateTO Site:", "" + siteId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("locationVerify", true);
        hashMap.put("siteLatitude", latitude);
        hashMap.put("siteLongitude", longitude);
        hashMap.put("locationVerifyConfirm", false);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(sharedpreferences.getLong("siteId", 0))).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        ll_after_locationVerify.setVisibility(View.VISIBLE);
                        ll_afterLocationDetected.setVisibility(View.GONE);
                        before_locationVerify.setVisibility(View.GONE);
                        locationManager.removeUpdates(MemberTimelineActivity.this::onLocationChanged);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MemberTimelineActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.e("Latitude11", "" + latitude);
        Log.e("Longitude11", "" + longitude);
        progressDialog.dismiss();
        ll_after_locationVerify.setVisibility(View.GONE);
        ll_afterLocationDetected.setVisibility(View.VISIBLE);
        before_locationVerify.setVisibility(View.GONE);

        findAddress();

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //permission denied
        Toast.makeText(this, "Enable Location Service", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_navigationbar1, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Action menu
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile:
                Log.e("Clicked", "Profile");
                startActivity(new Intent(MemberTimelineActivity.this, AboutActivity.class));

                return true;
            case R.id.language:
                startActivity(new Intent(MemberTimelineActivity.this, LanguageChange.class));

//                finish();
                return true;
//                case R.id.share:
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("text/plain");
//                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.project.skill"); // url for share the app
//                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
//                    return true;
            case R.id.youtube:
                // rate implementation here
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/playlist?list=PLasxv4S2lb-Lz_4dJ9E5GOIT3NqJk_BX-")));
                // rate the app
                return true;
            case R.id.share:
                // rate implementation here
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Hi!I am using this app called हाज़िरी Register and would like to share it with you. Download now for free from Playstore. Click on this link \n" + "https://play.google.com/store/apps/details?id=com.skillzoomer_Attendance.com";
                String sub = "Invite";
                System.out.println("++++++++++++++++++++body" + body);
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                myIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
                // rate the app
                return true;
            case R.id.transaction:
                startActivity(new Intent(MemberTimelineActivity.this, TransactionListActivity.class));
                return true;
            case R.id.logout:
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MemberTimelineActivity.this);
                builder.setTitle(R.string.log_out)
                        .setMessage(R.string.are_you_sure_you)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            progressDialog.setMessage(getString(R.string.logging_out));
                            progressDialog.show();
                            if (userType.equals("Supervisor")) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                                        memberOnline = snapshot.child("memberOnline").getValue(long.class);

                                        if (checkLocationPermission()) {
                                            activity = "normalLogout";
                                            detectLocation();
                                        } else {
                                            requestLocationPermission();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                firebaseAuth.signOut();
                                editor.clear();
                                editor.commit();
                                dialogInterface.dismiss();
//                                    startActivity(new Intent(timelineActivity.this,SplashActivity.class));
                                this.finishAffinity();
                            }


                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();


//                myRef = myRef.child(session.getUserDetails( ).getAdminId( ));
//                myRef.child(session.getUserDetails( ).getId( )).child("attendance").setValue("false");
//                session.logoutUser( );

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void uploadToMember(HashMap<String, Object> hashMap, String status) {
        timestamp = "" + System.currentTimeMillis();
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        hashMap.put("online", false);
        hashMap.put("time", currentTime);
        Log.e("HashMAp", hashMap.toString());


        Log.e("uid", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("uid", ""));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("uid", "")).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                String currentDate = "";
                String currentTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                Log.e("Sign", "Out");
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("uid", firebaseAuth.getUid());
                hashMap1.put("online", false);
                hashMap1.put("status", status);
                hashMap1.put("profile", "");
                hashMap1.put("timeStamp", "" + timestamp);
                hashMap1.put("time", "" + currentTime);
                hashMap1.put("name", "" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));

                if (latitude > 0 && longitude > 0) {
                    hashMap1.put("memberLatitude", latitude);
                    hashMap1.put("memberLongitude", longitude);
                }

                reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child(currentDate).child(timestamp).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        editor.clear();
                        editor.commit();
                        editor.apply();
                        deleteCart();

                    }
                });


            }
        });
    }

    private void deleteCart() {
        EasyDB easyDB = EasyDB.init(this, "Attendance" + "Skilled")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        if (easyDB.getAllData().getCount() > 0) {
            easyDB.deleteAllDataFromTable();
            deletePaymentCart();
        } else {

//            startActivity(new Intent(MemberTimelineActivity.this,SplashActivity.class));
            deletePaymentCart();
        }
        EasyDB easyDB1 = EasyDB.init(this, "Attendance" + "Unskilled")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        if (easyDB1.getAllData().getCount() > 0) {
            easyDB1.deleteAllDataFromTable();
            deletePaymentCart();
        } else {

//            startActivity(new Intent(MemberTimelineActivity.this,SplashActivity.class));
            deletePaymentCart();
        }
    }

    private void deletePaymentCart() {
        EasyDB easyDB = EasyDB.init(this, "Payment_Db")
                .setTableName("Payment")
                .addColumn(new Column("Timestamp", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourType", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .addColumn(new Column("Advance", new String[]{"text", "not null"}))
                .addColumn(new Column("Reason", new String[]{"text", "not null"}))
                .doneTableColumn();
        if (easyDB.getAllData().getCount() > 0) {
            progressDialog.dismiss();
            easyDB.deleteAllDataFromTable();
            finish();
            finishActivity(Location_Request_code);
            finishAffinity();
            System.exit(0);


        } else {
            progressDialog.dismiss();
            finish();
            finishActivity(Location_Request_code);
            finishAffinity();
            System.exit(0);
        }


    }

    @Override
    public void onBackPressed() {
        if (userType.equals("Supervisor")) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MemberTimelineActivity.this);
            builder.setTitle(getString(R.string.exit))
                    .setMessage(R.string.are_you_sure_you_exit)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        this.finishAffinity();
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.show();

        } else {
            startActivity(new Intent(MemberTimelineActivity.this, timelineActivity.class));
            finish();
            this.finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                et_your_remark.setText(Objects.requireNonNull(result).get(0));


//
            }
        } else if (requestCode == REQUEST_CODE_SPEECH_INPUT1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                et_your_remark1.setText(Objects.requireNonNull(result).get(0));


//
            }
        }
    }

    private void forceLogout() {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MemberTimelineActivity.this);
        builder.setCancelable(false);
        String finalCurrentTime = currentTime;
        builder.setTitle(R.string.forcelogout)
                .setMessage(R.string.forced_logout_attendance)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    progressDialog.show();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("online", false);
                    hashMap.put("forceLogout", true);
                    hashMap.put("forceLogoutReason", "Inactivity");
                    hashMap.put("time", finalCurrentTime);
                    hashMap.put("uid", firebaseAuth.getUid());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    reference.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            String currentDate = "";
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                                            }
                                            String timestamp = "" + System.currentTimeMillis();
                                            HashMap<String, Object> hashMap1 = new HashMap<>();
                                            hashMap1.put("uid", firebaseAuth.getUid());
                                            hashMap1.put("online", false);
                                            hashMap1.put("status", "Forced Logout");
                                            hashMap1.put("profile", "");
                                            hashMap1.put("timeStamp", "" + timestamp);
                                            hashMap1.put("time", "" + finalCurrentTime);
                                            hashMap1.put("name", "" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                                            if (latitude > 0 && longitude > 0) {
                                                hashMap1.put("memberLatitude", 0.0);
                                                hashMap1.put("memberLongitude", 0.0);
                                            }
                                            reference.child(String.valueOf(siteId)).child("Attendance").child(currentDate).child(timestamp).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    HashMap<String, Object> hashMap2 = new HashMap<>();
                                                    hashMap2.put("forceLogout", true);
                                                    reference.child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap2)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    firebaseAuth.signOut();
                                                                    editor.clear();
                                                                    editor.commit();
                                                                    finish();
                                                                    finishActivity(Location_Request_code);
                                                                    finishAffinity();
                                                                }
                                                            });
                                                }
                                            });

                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MemberTimelineActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });
        builder.show();
    }

    @Override
    protected void onResume() {
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        MyApplication my = new MyApplication();
        my.updateLanguage(this, sharedpreferences.getString("Language", "hi"));
        super.onResume();
    }
}     ///MAinClass
