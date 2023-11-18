package com.skillzoomer_Attendance.com.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import com.skillzoomer_Attendance.com.Adapter.AdapterAttendance;
import com.skillzoomer_Attendance.com.Adapter.AdapterData;
import com.skillzoomer_Attendance.com.Model.ModelData;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelPresentLabour;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityMainBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_mic;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    ArrayList<String> result;
    ArrayList<ModelData> dataArrayList;
    String currentDate;

    private Button btn_upload;
    int adapter_position, adapter_position1;
    Boolean localdatabase;
    Boolean search;
    Boolean search1;
    AdapterData adapterData;
    TextView txt_message_display;
    String workerType;
    long siteId;
    private ArrayList<ModelLabour> labourArrayList;
    private ArrayList<ModelLabour> labourArrayListConfirmed;
    private int count;
    private int position;
    String userType, siteName;
    ActivityMainBinding binding;
    LayoutToolbarBinding toolbarBinding;
    String presentLabourId, presentLabourName;
    private ArrayList<ModelPresentLabour> presentLabourArrayList;
    String labourId;
    private ProgressDialog progressDialog;
    private String userName;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelPresentLabour> presentLabourArrayList1;
    private SharedPreferences.Editor editor;
    public int counter = 600;
    public int LoginAttempt = 0;
    public String LoginTime = "";
    private String lastLoginTime;
    private Boolean attendance_activity;
    private double latitude=0.0, longitude=0.0;


    private int counterValue = 2400;
    private int count_database;
    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
        @Override
        public void onTick(long l) {
            Log.e("counter", "" + counter);
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
    private ArrayList<ModelLabour> searchLabourArrayList;

    String uid;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(binding.getRoot());

        result = new ArrayList<>();

        iv_mic = findViewById(R.id.iv_mic);
        txt_message_display = findViewById(R.id.txt_message_display);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_upload = findViewById(R.id.btn_upload);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        lastLoginTime = spLogin.getString("LastLoginTime", "NA");
        attendance_activity = spLogin.getBoolean("AttendanceActivity", false);
        searchLabourArrayList = new ArrayList<>();
        MyApplication my = new MyApplication();
        my.updateLanguage(this, sharedpreferences.getString("Language", "hi"));
        Log.e("Activity","OnCreateCalled");
//        if(!attendance_activity){
//            if(!lastLoginTime.equals("NA")){
//                binding.llForceLogout.setVisibility(View.VISIBLE);
//                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
//                java.text.DateFormat df = new java.text.SimpleDateFormat("HH:mm");
//                Date date1 = null;
//                Date date2 = null;
//                try {
//                    date1 = df.parse(currentTime);
//                    date2 = df.parse(lastLoginTime);
//                } catch (ParseException e) {
//                    Log.e("DateException",e.getMessage());
//                    e.printStackTrace();
//                }
//
//                long diff = date1.getTime() - date2.getTime();
//                String currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(diff);
//                Log.e("CurrentTime1",currentTime1);
//                Log.e("Date1",""+date1.getTime());
//                Log.e("Date2",""+date2.getTime());
//                int timeInSeconds = (int) (diff / 1000);
//                Log.e("timeInSeconds",""+timeInSeconds);
////                    int hours, minutes, seconds;
////                    hours = timeInSeconds / 3600;
////                    timeInSeconds = timeInSeconds - (hours * 3600);
////                    minutes = timeInSeconds / 60;
////                    timeInSeconds = timeInSeconds - (minutes * 60);
////                    seconds = timeInSeconds;
//                counter=counterValue-timeInSeconds;
//                if(counter<=0){
//                    Log.e("ForceLogout","ForceLogout");
//                    forceLogout();
//                }
//                countDownTimer.start();
//
//            }
//
//        }else{
//            binding.llForceLogout.setVisibility(View.GONE);
//            countDownTimer.cancel();
//        }
//        binding.llForceLogout.setVisibility(View.GONE);
//            countDownTimer.cancel();
        Intent intent = getIntent();
        userType = sharedpreferences.getString("userDesignation", "");
        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        if (userType.equals("Supervisor")) {
            siteName = sharedpreferences.getString("siteName", "");
            siteId = sharedpreferences.getLong("siteId", 0);
        } else {
            siteId = intent.getLongExtra("siteId", 0);
            siteName = intent.getStringExtra("siteName");
            Log.e("Back", "M:" + siteId + siteName);
        }

        userName = sharedpreferences.getString("userName", "");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);


        workerType = intent.getStringExtra("type");
        if (siteId == 0) {
            siteId = intent.getLongExtra("siteId", 0);
        }
        Log.e("WrokerType",workerType);
        if (workerType.equals("Skilled")) {
            txt_message_display.setText(getString(R.string.skilled) +" "+getString(R.string.attendance));
        } else {
            txt_message_display.setText(getString(R.string.unskilled) + getString(R.string.attendance));
        }

        labourArrayList = new ArrayList<>();
        gaetLabourList();
        presentLabourArrayList = new ArrayList<>();
        presentLabourArrayList1 = new ArrayList<>();
        binding.etSearchLabour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 2) {
                    result.add(charSequence.toString());
                    recyclerViewData(result);


                } else if (charSequence.toString().length() == 0) {
                    gaetLabourList();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


//        presentLabourArrayList=new ArrayList<>();
//        deleteCart();
        constructPresentRecyclerView();
        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemberTimelineActivity.class);
                intent.putExtra("type", "Skilled");
                intent.putExtra("siteName", siteName);
                intent.putExtra("siteId", siteId);
                startActivity(intent);
            }
        });
        binding.llPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdvancesHomeActivity.class);
                intent.putExtra("type", "Skilled");
                intent.putExtra("siteName", siteName);
                intent.putExtra("siteId", siteId);
                startActivity(intent);
            }
        });


        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterValue = 2400;
                Intent intent = new Intent(MainActivity.this, CheckAttendanceActivity.class);
                intent.putExtra("type", workerType);
                intent.putExtra("siteId", siteId);
                intent.putExtra("siteName", siteName);
                Log.e("MainSN", siteName);
                startActivity(intent);
                finish();


            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("adapter_position"));
        LocalBroadcastManager.getInstance(this).registerReceiver(databaseChange,
                new IntentFilter("DatabaseChange"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver1,
                new IntentFilter("attendance_position"));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        dataArrayList = new ArrayList<>();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);



        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.ENGLISH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_to_search));


                try {

                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(MainActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

//    private void uploadToFirebase() {
//
//        EasyDB easyDB = EasyDB.init(this, "ATTENDANCE_Db")
//                .setTableName("Attendance")
//                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
//                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
//                .addColumn(new Column("Date", new String[]{"text", "not null"}))
//                .addColumn(new Column("Time", new String[]{"text", "not null"}))
//                .doneTableColumn();
//        Cursor res = easyDB.getAllData();
//        while (res.moveToNext()) {
//            String labourId = res.getString(1);
//            String labourName = res.getString(2);
//            String date = res.getString(3);
//            String time = res.getString(4);
//            Log.e("Local", "LabourId" + labourId);
//            Log.e("Local", "labourName" + labourName);
//            Log.e("Local", "date" + date);
//            Log.e("Local", "time" + time);
//
//
//            ModelPresentLabour modelCartItem = new ModelPresentLabour(
//                    labourId, labourName, date, time);
//            startUpload(modelCartItem);
//        }
//        deleteCart();
//        uploadToSite(presentLabourArrayList1.size());
//
//
////        startActivity(new Intent(MainActivity.this,MemberTimelineActivity.class));
//
//
//    }

    private void uploadToSite(int size) {
        HashMap<String, Object> hashMap = new HashMap<>();
        Log.e("WorkerType1111", workerType);
        if (workerType != null) {
            if (workerType.equals("Skilled")) {
                hashMap.put("skilled", size);
                hashMap.put("attendanceEditSkilled", false);
            } else {
                hashMap.put("unskilled", size);
                hashMap.put("attendanceEditUnSkilled", false);

            }
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.success)
                                .setMessage(R.string.attendance_uploaded_successfully)
                                .setCancelable(true)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, MemberTimelineActivity.class));

                                });

                        builder.show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void startUpload(ModelPresentLabour modelCartItem) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("labourId", modelCartItem.getLabourId());
        hashMap.put("labourName", modelCartItem.getLabourName());
        hashMap.put("dateOfUpload", modelCartItem.getDate());
        hashMap.put("timeOfupload", modelCartItem.getTime());
        hashMap.put("labourType", workerType);
        hashMap.put("uploadedByName", userName);
        hashMap.put("uploadedBYUid", firebaseAuth.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours")
                .child(modelCartItem.getDate()).child(modelCartItem.getLabourId())
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        uploadToLabourAttendance(modelCartItem);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                });

    }

    private void uploadToLabourAttendance(ModelPresentLabour modelCartItem) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("date", modelCartItem.getDate());
        hashMap.put("time", modelCartItem.getTime());
        hashMap.put("status", "Present");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(modelCartItem.getLabourId())
                .child("Attendance").child(modelCartItem.getDate()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("Data", "Uploaded" + modelCartItem.getLabourName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                });
    }

    private void gaetLabourList() {

        presentLabourArrayList = new ArrayList<>();
        Log.e("Called", "1");
        Log.e("siteID786010", "" + siteId + "::::::::::" + siteName);
        Log.e("PresentLabour", "" + presentLabourArrayList.size());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").orderByChild("status").equalTo("Registered").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labourArrayList.clear();
                countDatabase();
                Log.e("SnapShot", "" + snapshot.getChildrenCount());

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelLabour modelLabour = ds.getValue(ModelLabour.class);
                    Log.e("siteId123", "" + siteId);
                    Log.e("siteId123", "Type" + workerType);
                    Log.e("siteId123", "LType" + modelLabour.getType());


                    if ((modelLabour.getType().equals(workerType)) && (modelLabour.getSiteCode() == siteId)) {
                        if (count_database > 0) {
                            Boolean status = false;
                            for (int i = 0; i < presentLabourArrayList.size(); i++) {


                                Log.e("ModelLabour", modelLabour.getLabourId());
                                Log.e("ModelLabour", "Size" + presentLabourArrayList.size());
                                Log.e("ModelLabour", "@i::" + i + "::" + presentLabourArrayList.get(i).getLabourId());
                                if (presentLabourArrayList.get(i).getLabourId().equals(modelLabour.getLabourId())) {
                                    modelLabour.setPresent(true);
                                    labourArrayList.add(modelLabour);
                                    status = true;
                                }
                                if (i == presentLabourArrayList.size() - 1) {
                                    if (!status) {
                                        modelLabour.setPresent(false);
                                        labourArrayList.add(modelLabour);
                                    }
                                }

                            }


                        } else {
                            modelLabour.setPresent(false);
                            labourArrayList.add(modelLabour);
                        }
                    }

                }
                Log.e("PresentLabour", "L:::" + labourArrayList.size());
                AdapterData adapterData = new AdapterData(MainActivity.this, labourArrayList, workerType);
                binding.rvPresentLabour.setAdapter(adapterData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public BroadcastReceiver databaseChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            countDatabase();
        }
    };

    private void countDatabase() {
        count_database = 0;
        presentLabourArrayList.clear();

        EasyDB easyDB = EasyDB.init(MainActivity.this, "Attendance" + workerType)
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();

        try {
            Cursor res = easyDB.getAllData();
            while (res.moveToNext()) {
                String labourId = res.getString(1);
                String labourName = res.getString(2);
                String date = res.getString(3);
                String time = res.getString(4);
                Log.e("Local", "LabourId" + labourId);
                Log.e("Local", "labourName" + labourName);
                Log.e("Local", "date" + date);
                Log.e("Local", "time" + time);


                ModelPresentLabour modelCartItem = new ModelPresentLabour(
                        labourId, labourName, date, time);
                presentLabourArrayList.add(modelCartItem);
            }
            presentLabourArrayList1 = presentLabourArrayList;
            if (presentLabourArrayList.size() > 0) {
                binding.txtTotalWorker.setText("" + presentLabourArrayList.size());

            } else {
                binding.txtTotalWorker.setText("" + presentLabourArrayList.size());
            }
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        count_database = easyDB.getAllData().getCount();
        Log.e("CountDatabase", "" + count_database);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter_position = intent.getIntExtra("position", 0);
            localdatabase = intent.getBooleanExtra("localDatabase", false);
            search = intent.getBooleanExtra("search", false);
            search1 = intent.getBooleanExtra("searchNameResult", false);
            String nameSearch = intent.getStringExtra("searchName");
            Log.e("adapter_position", "" + adapter_position);
            Log.e("localdatabase", "" + localdatabase);
            Log.e("search", "" + search);
            Log.e("search1", "" + search1);
            if (search && !localdatabase && search1) {
                Intent intent1 = new Intent(MainActivity.this, SearchForLabour.class);
                Log.e("WorkerType", workerType);
                intent1.putExtra("type", workerType);
                intent1.putExtra("name", nameSearch);
                intent1.putExtra("siteId", siteId);
                intent1.putExtra("siteName", siteName);
                startActivity(intent1);
            }
            if (adapter_position >= 0 && !localdatabase && !search && !search1) {
                dataArrayList.remove(adapter_position);
                adapterData.notifyItemRemoved(adapter_position);

            }
            if (search && !localdatabase && !search1) {
                Intent intent1 = new Intent(MainActivity.this, SearchForLabour.class);
                Log.e("WorkerType", workerType);
                intent1.putExtra("type", workerType);
                intent1.putExtra("siteId", siteId);
                intent1.putExtra("siteName", siteName);

                Log.e("searchValue", "" + siteId + "Name::::" + siteName);
                startActivity(intent1);
                finish();
            }


        }
    };
    public BroadcastReceiver mMessageReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter_position1 = intent.getIntExtra("position1", 0);
            String labourId = presentLabourArrayList.get(adapter_position1).getLabourId();
            Log.e("LabourID12334", labourId);
            deleteLabourFromCart(labourId, adapter_position1);


        }
    };

    private void deleteLabourFromCart(String labourId, int adapter_position) {
        EasyDB easyDB = EasyDB.init(MainActivity.this, "ATTENDANCE_Db")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        if(easyDB.getAllData().getCount()>1){
            easyDB.deleteRow(1, labourId);
            Toast.makeText(MainActivity.this, "Labour Removed From List", Toast.LENGTH_SHORT).show();
            //refresh list
            presentLabourArrayList.remove(adapter_position);
            constructPresentRecyclerView();
        }else{
            deleteCart();
        }




    }

    private void constructPresentRecyclerView() {
        presentLabourArrayList = new ArrayList<>();
        EasyDB easyDB = EasyDB.init(this, "ATTENDANCE_Db")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        try {
            Cursor res = easyDB.getAllData();
            while (res.moveToNext()) {
                String labourId = res.getString(1);
                String labourName = res.getString(2);
                String date = res.getString(3);
                String time = res.getString(4);
                Log.e("Local", "LabourId" + labourId);
                Log.e("Local", "labourName" + labourName);
                Log.e("Local", "date" + date);
                Log.e("Local", "time" + time);


                ModelPresentLabour modelCartItem = new ModelPresentLabour(
                        labourId, labourName, date, time);
                presentLabourArrayList.add(modelCartItem);
            }
            presentLabourArrayList1 = presentLabourArrayList;
            if (presentLabourArrayList.size() > 0) {
                binding.txtTotalWorker.setText("" + presentLabourArrayList.size());
                AdapterAttendance adapterAttendance = new AdapterAttendance(MainActivity.this, presentLabourArrayList, workerType);
                binding.rvPresentLabour.setAdapter(adapterAttendance);
            }
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_SPEECH_INPUT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                binding.etSearchLabour.setText(
                        Objects.requireNonNull(result).get(0));
                recyclerViewData(result);
            }
        }
    }

    private void recyclerViewData(ArrayList<String> result) {
        Log.e("result", result.get(0));
        String currentString = "Fruit: they taste good";
        String[] separated = result.get(0).split(" ");
        for (int i = 0; i < separated.length; i++) {
            Log.e("Split", separated[i]);
            checkInLabour(separated[i]);
//            dataArrayList.add(new ModelData(currentDate,separated[i]));
        }
//        recyclerViewConstruct();
//        separated[0]; // this will contain "Fruit"
//        separated[1]; // this will contain " they taste good"


    }

    private void checkInLabour(String name) {
        Log.e("Called1234", "" + labourArrayList.size());
        searchLabourArrayList.clear();

        ArrayList<Integer> multiplepositions = new ArrayList<>();
        int multiple = 0;
        count = 0;
        Log.e("Name", "" + labourArrayList.size());
        for (int i = 0; i < labourArrayList.size(); i++) {
            Log.e("Name", name.toLowerCase(Locale.ROOT));
            Log.e("Name", "L @" + i + " " + labourArrayList.get(i).getName().toLowerCase(Locale.ROOT));
            Log.e("Name", "Result:" + (name.toLowerCase(Locale.ROOT).equals(labourArrayList.get(i).getName().toLowerCase(Locale.ROOT))));
            if ((labourArrayList.get(i).getName().toLowerCase(Locale.ROOT)).contains((name.toLowerCase(Locale.ROOT))) ||
                    (labourArrayList.get(i).getLabourId().toLowerCase(Locale.ROOT)).contains(name.toLowerCase(Locale.ROOT))) {
                count++;
                position = i;
                multiplepositions.add(multiple, i);
                multiple += 1;
                Log.e("SearchResult", "i::::" + i + labourArrayList.get(i).getPresent());

            }
        }
        Log.e("Count11111", "" + count);
        if (count == 0) {
            Log.e("Cursor", "Count=0");
            binding.rvPresentLabour.setVisibility(View.GONE);
            binding.llSearchLabour.setVisibility(View.VISIBLE);
            binding.rvSearchLabour.setVisibility(View.GONE);
            binding.tvNoLabourPresent.setVisibility(View.VISIBLE);
            binding.btnOk.setVisibility(View.VISIBLE);
            binding.btnAddNewWorker.setVisibility(View.VISIBLE);
            binding.btnAddNewWorker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, LabourRegistration.class);
                    intent.putExtra("Activity", "Attendance");
                    intent.putExtra("siteId", String.valueOf(siteId));
                    intent.putExtra("siteName", siteName);
                    intent.putExtra("searchName", name);
                    intent.putExtra("type", workerType);
                    startActivity(intent);
                }
            });
            binding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.llSearchLabour.setVisibility(View.GONE);
                    binding.rvSearchLabour.setVisibility(View.GONE);
                    binding.rvPresentLabour.setVisibility(View.VISIBLE);
                    binding.etSearchLabour.getText().clear();
                    result.clear();
                    gaetLabourList();
                }
            });
//            dataArrayList.add(new ModelData(name, "Not Found", 0, -1, null));

        } else if (count > 1) {
            Log.e("Cursor", "Count>1");
            searchLabourArrayList.clear();
            binding.rvPresentLabour.setVisibility(View.GONE);
            binding.llSearchLabour.setVisibility(View.VISIBLE);
            binding.rvSearchLabour.setVisibility(View.VISIBLE);
            binding.llSearchLabour.setWeightSum(1);
            binding.tvNoLabourPresent.setVisibility(View.GONE);
            binding.btnAddNewWorker.setVisibility(View.GONE);
            for (int i = 0; i < multiplepositions.size(); i++) {
                Log.e("Cursor", "i::::" + multiplepositions.get(i));
                searchLabourArrayList.add(labourArrayList.get(multiplepositions.get(i)));
            }
            Log.e("Cursor", "Size" + searchLabourArrayList.size());
            AdapterData adapterData = new AdapterData(MainActivity.this, searchLabourArrayList, workerType);
            binding.rvSearchLabour.setAdapter(adapterData);
            binding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.llSearchLabour.setVisibility(View.GONE);
                    binding.rvSearchLabour.setVisibility(View.GONE);
                    binding.rvPresentLabour.setVisibility(View.VISIBLE);
                    binding.etSearchLabour.getText().clear();
                    result.clear();
                    gaetLabourList();
                }
            });

        } else if (count == 1) {
            Log.e("Cursor", "Count=1");
            Log.e("Cursor", "Position" + position);
            searchLabourArrayList.clear();
            binding.rvPresentLabour.setVisibility(View.GONE);
            binding.llSearchLabour.setVisibility(View.VISIBLE);
            binding.rvSearchLabour.setVisibility(View.VISIBLE);
            binding.llSearchLabour.setWeightSum(1);
            binding.btnAddNewWorker.setVisibility(View.GONE);
            searchLabourArrayList.add(labourArrayList.get(position));
            binding.tvNoLabourPresent.setVisibility(View.GONE);

            AdapterData adapterData = new AdapterData(MainActivity.this, searchLabourArrayList, workerType);
            binding.rvSearchLabour.setAdapter(adapterData);
            binding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.llSearchLabour.setVisibility(View.GONE);
                    binding.rvSearchLabour.setVisibility(View.GONE);
                    binding.rvPresentLabour.setVisibility(View.VISIBLE);
                    gaetLabourList();
                }
            });
        }
//                dataArrayList.add(new ModelData(labourArrayList.get(position).getName(),labourArrayList.get(position).getLabourId(),1,position,null));

//            if (labourArrayList.get(position).getStatus().equals("Pending")) {
//                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
//                builder.setCancelable(false);
//                builder.setTitle(getString(R.string.pending))
//                        .setMessage(R.string.first_confirm_the_registration_attendance)
//                        .setCancelable(true)
//                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
//                            dialogInterface.dismiss();
//
//                        });
//
//
//                builder.show();
//
//
//            } else {
//                uploadToLocalDatabase(labourArrayList.get(position).getLabourId(),
//                        labourArrayList.get(position).getName());
//            }


    }

    private void uploadToLocalDatabase(String labourId, String laborName) {

        SimpleDateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }
        SimpleDateFormat time = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            time = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        }
        Date c = Calendar.getInstance().getTime();
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = df.format(c);
        }
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = time.format(c);
        }
        EasyDB easyDB = EasyDB.init(MainActivity.this, "ATTENDANCE_Db")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        try {
            easyDB.addData("LabourId", labourId)
                    .addData("LabourName", laborName)
                    .addData("Date", currentDate)
                    .addData("Time", currentTime)
                    .doneDataAdding();
        } catch (SQLiteConstraintException e) {
            Log.e("Exception", e.getMessage());
        }
        constructPresentRecyclerView();
//        holder.btn_delete.setVisibility(View.GONE);

    }


    private void recyclerViewConstruct() {
//        adapterData=new AdapterData(MainActivity.this,dataArrayList,workerType);
    }

    private void deleteCart() {
        EasyDB easyDB = EasyDB.init(this, "ATTENDANCE_Db")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
    }

    private void forceLogout() {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        String finalCurrentTime = currentTime;
        builder.setTitle(R.string.forcelogout)
                .setMessage(R.string.forced_logout_attendance)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    progressDialog.show();
                    firebaseAuth.signOut();
                    editor.clear();
                    editor.commit();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("online", false);
                    hashMap.put("forceLogout", true);
                    hashMap.put("forceLogoutReason", "Attendance Inactivity");
                    hashMap.put("time", finalCurrentTime);
                    hashMap.put("uid",firebaseAuth.getUid());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    reference.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            String currentDate="";
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                                            }
                                            String timestamp = "" + System.currentTimeMillis();
                                            HashMap<String,Object> hashMap1=new HashMap<>();
                                            hashMap1.put("uid", firebaseAuth.getUid());
                                            hashMap1.put("online", false);
                                            hashMap1.put("status", "Forced Logout");
                                            hashMap1.put("profile", "");
                                            hashMap1.put("timeStamp", "" + timestamp);
                                            hashMap1.put("time", "" + finalCurrentTime);
                                            hashMap1.put("name", "" + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
                                            if (latitude > 0 && longitude > 0) {
                                                hashMap1.put("memberLatitude", 0.0);
                                                hashMap1.put("memberLongitude", 0.0);
                                            }
                                            reference.child(String.valueOf(siteId)).child("Attendance").child(currentDate).child(timestamp).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    HashMap<String,Object> hashMap2=new HashMap<>();
                                                    hashMap2.put("forceLogout",true);
                                                    reference.child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap2)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    firebaseAuth.signOut();
                                                                    editor.clear();
                                                                    editor.commit();
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
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, MemberTimelineActivity.class);
        intent.putExtra("siteId", siteId);
        intent.putExtra("siteName", siteName);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.e("Activity","onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e("Activity","onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e("Activity","onResume");
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        Log.e("Activity","onPostResume");
        super.onPostResume();
    }
}
