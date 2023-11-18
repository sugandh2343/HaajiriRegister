package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterAttendance;
import com.skillzoomer_Attendance.com.Adapter.AdapterData;
import com.skillzoomer_Attendance.com.Model.ModelData;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelPresentLabour;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAttendanceBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AttendanceActivity extends AppCompatActivity {
//    ActivityAttendanceBinding binding;
//    private ImageView iv_mic;
//    private TextView tv_Speech_to_text;
//    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
//    ArrayList<String> result;
//    ArrayList<ModelData> dataArrayList;
//    String currentDate;
//    private RecyclerView rv_dataList;
//    private Button btn_upload;
//    int adapter_position,adapter_position1;
//    Boolean localdatabase;
//    Boolean search;
//    Boolean search1;
//    AdapterData adapterData;
//    TextView txt_message_display;
//    String workerType;
//    long siteId;
//    private ArrayList<ModelLabour>labourArrayList;
//    private ArrayList<ModelLabour>labourArrayListConfirmed;
//    private int count;
//    private int position;
//    String userType,siteName;
//    LayoutToolbarBinding toolbarBinding;
//    String presentLabourId,presentLabourName;
//    private ArrayList<ModelPresentLabour> presentLabourArrayList;
//    String labourId;
//    private ProgressDialog progressDialog;
//    private String userName;
//    FirebaseAuth firebaseAuth;
//    private ArrayList<ModelPresentLabour> presentLabourArrayList1;
//    private SharedPreferences.Editor editor;
//    public int counter = 600;
//    public int LoginAttempt = 0;
//    public String LoginTime = "";
//    private String lastLoginTime;
//    private Boolean attendance_activity;
//    private int counterValue=2400;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding=ActivityAttendanceBinding.inflate(getLayoutInflater());
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        setContentView(binding.getRoot());
//        binding.llMainAttendance.setVisibility(View.GONE);
//        firebaseAuth=FirebaseAuth.getInstance();
//        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
//        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
//        editor= sharedpreferences.edit();
//        result=new ArrayList<>();
//        lastLoginTime=spLogin.getString("LastLoginTime","NA");
//        attendance_activity=spLogin.getBoolean("AttendanceActivity",false);
////        if(!attendance_activity){
////            if(!lastLoginTime.equals("NA")){
////                binding.llForceLogout.setVisibility(View.VISIBLE);
////                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
////                java.text.DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
////                Date date1 = null;
////                Date date2 = null;
////                try {
////                    date1 = df.parse(currentTime);
////                    date2 = df.parse(lastLoginTime);
////                } catch (ParseException e) {
////                    Log.e("DateException",e.getMessage());
////                    e.printStackTrace();
////                }
////
////                long diff = date1.getTime() - date2.getTime();
////                String currentTime1 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(diff);
////                Log.e("CurrentTime1",currentTime1);
////                Log.e("Date1",""+date1.getTime());
////                Log.e("Date2",""+date2.getTime());
////                int timeInSeconds = (int) (diff / 1000);
////                Log.e("timeInSeconds",""+timeInSeconds);
//////                    int hours, minutes, seconds;
//////                    hours = timeInSeconds / 3600;
//////                    timeInSeconds = timeInSeconds - (hours * 3600);
//////                    minutes = timeInSeconds / 60;
//////                    timeInSeconds = timeInSeconds - (minutes * 60);
//////                    seconds = timeInSeconds;
////                counter=counterValue-timeInSeconds;
////                if(counter<=0){
////                    Log.e("ForceLogout","ForceLogout");
////                    forceLogout();
////                }
////                countDownTimer.start();
////
////            }
////
////        }else{
////            binding.llForceLogout.setVisibility(View.GONE);
////        }
//        countDownTimer.cancel();
//        userType=sharedpreferences.getString("userDesignation","");
//        siteName=sharedpreferences.getString("siteName","");
//        siteId=sharedpreferences.getLong("siteId",0);
//        userName=sharedpreferences.getString("userName","");
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle(getResources().getString(R.string.please_wait));
//        progressDialog.setCanceledOnTouchOutside(false);
//        binding.llMarkedPresent.setVisibility(View.GONE);
//        workerType="";
//        dataArrayList=new ArrayList<>();
//        labourArrayList=new ArrayList<>();
//        binding.UserCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                workerType="Skilled";
////                binding.addLabourBtn.setText("Add new "+selectedType.toLowerCase(Locale.ROOT)+" worker");
//                binding.llMainAttendance.setVisibility(View.VISIBLE);
//                binding.llMarkedPresent.setVisibility(View.VISIBLE);
//                binding.llUnskilledTotal.setVisibility(View.GONE);
//                binding.llSkilledTotal.setVisibility(View.VISIBLE);
//                binding.UserCard.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));
//                binding.DriverCard.setCardBackgroundColor(getResources().getColor(R.color.white));
//
//                Log.e("LabourListAtee","S"+labourArrayList.size());
//
//            }
//        });
//        binding.DriverCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                workerType="Unskilled";
////                binding.addLabourBtn.setText("Add new "+selectedType.toLowerCase(Locale.ROOT)+" worker");
//                binding.llMainAttendance.setVisibility(View.VISIBLE);
//                binding.llMarkedPresent.setVisibility(View.VISIBLE);
//                binding.UserCard.setCardBackgroundColor(getResources().getColor(R.color.white));
//                binding.llUnskilledTotal.setVisibility(View.VISIBLE);
//                binding.llSkilledTotal.setVisibility(View.GONE);
//                binding.DriverCard.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));
//
//                Log.e("LabourListAtee","U"+labourArrayList.size());
//            }
//        });
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
//            checkPermission();
//        }
//
//        binding.ivMic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent
//                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//                        Locale.ENGLISH);
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
//
//
//                try {
//
//                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//                }catch (Exception e) {
//                    Toast
//                            .makeText(AttendanceActivity.this, " " + e.getMessage(),
//                                    Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//        binding.etSearchLabour.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
//                if(charSequence.toString().contains(" ")){
//                    result.add(charSequence.toString());
//                    recyclerViewData(result);
//                    result.clear();
//                    binding.etSearchLabour.getText().clear();
//
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                new IntentFilter("adapter_position"));
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver1,
//                new IntentFilter("attendance_position"));
    }
//    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            adapter_position=intent.getIntExtra("position",0);
//            localdatabase=intent.getBooleanExtra("localDatabase",false);
//            search=intent.getBooleanExtra("search",false);
//            search1=intent.getBooleanExtra("searchNameResult",false);
//            String nameSearch=intent.getStringExtra("searchName");
////            Log.e("NameSearch",nameSearch);
//            Log.e("adapter_position",""+adapter_position);
//            Log.e("localdatabase",""+localdatabase);
//            Log.e("search",""+search);
//            Log.e("search1",""+search1);
//
//
////            if(localdatabase && !search&&!search1){
//////                Log.e("LocalDatabase",localdatabase.toString());
//////                constructPresentRecyclerView();
////                String labourId=presentLabourArrayList.get(adapter_position).getLabourId();
////                Log.e("LabourID12334",labourId);
////                deleteLabourFromCart(labourId,adapter_position);
////
////            }
//            if(search && !localdatabase &&search1){
//                Intent intent1= new Intent(AttendanceActivity.this, SearchForLabour.class);
//                Log.e("WorkerType",workerType);
//                intent1.putExtra("type",workerType);
//                intent1.putExtra("name",nameSearch);
//                startActivity(intent1);
//            }
//            if(adapter_position>=0 &&!localdatabase&&!search&&!search1){
//                dataArrayList.remove(adapter_position);
//                adapterData.notifyItemRemoved(adapter_position);
//
//            }
//            if(search && !localdatabase &&!search1){
//                Intent intent1= new Intent(AttendanceActivity.this,SearchForLabour.class);
//                Log.e("WorkerType",workerType);
//                intent1.putExtra("type",workerType);
//                startActivity(intent1);
//            }
//
//
//
//
//        }
//    };
//    public BroadcastReceiver mMessageReceiver1=new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context , Intent intent) {
//            adapter_position1=intent.getIntExtra("position1",0);
//            String labourId=presentLabourArrayList.get(adapter_position1).getLabourId();
//            String workerDeletedType=intent.getStringExtra("workerDeletedType");
//            Log.e("LabourID12334",labourId);
//            deleteLabourFromCart(labourId,adapter_position1,workerDeletedType);
//
//
//
//        }
//    };

//    private void deleteLabourFromCart(String labourId, int adapter_position, String workerDeletedType) {
//        EasyDB easyDB=EasyDB.init(AttendanceActivity.this,"ATTENDANCE_Db")
//                .setTableName(workerDeletedType)
//                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
//                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
//                .addColumn(new Column("Date", new String[]{"text", "not null"}))
//                .addColumn(new Column("Time", new String[]{"text", "not null"}))
//                .doneTableColumn();
//        easyDB.deleteRow(1,labourId);
//        Toast.makeText(AttendanceActivity.this,"Labour Removed From List",Toast.LENGTH_SHORT).show();
//        //refresh list
//        presentLabourArrayList.remove(adapter_position);
//        constructPresentRecyclerView();
//
//
//    }
//
//
//    private void getLabourList(String workerType, String s) {
//        labourArrayList.clear();
//        DatabaseReference reference= (DatabaseReference) FirebaseDatabase.getInstance().getReference("Site")
//                .child(String.valueOf(siteId));
//        reference.child("Labours").orderByChild("type").equalTo(workerType).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds:snapshot.getChildren()){
//                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
//                    labourArrayList.add(modelLabour);
//
//
//
//
//                }
//                checkInLabour(s,workerType,labourArrayList);
//                Log.e("LabourListAtee",""+labourArrayList.size());
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
//                recyclerViewData(result);
//            }
//        }
//    }
//    private void recyclerViewData(ArrayList<String> result) {
//        Log.e("result",result.get(0));
//        String currentString = "Fruit: they taste good";
//        String[] separated = result.get(0).split(" ");
//        for(int i=0;i<separated.length;i++){
//            Log.e("Split",separated[i]);
//            getLabourList(workerType,separated[i]);
//            Log.e("WorkerType","Search"+workerType);
////            dataArrayList.add(new ModelData(currentDate,separated[i]));
//        }
////        recyclerViewConstruct();
////        separated[0]; // this will contain "Fruit"
////        separated[1]; // this will contain " they taste good"
//
//
//    }
//
//
//
//    private void checkInLabour(String name, String workerType, ArrayList<ModelLabour> labourArrayList) {
//
//        Log.e("Called",""+ labourArrayList.size());
//        ArrayList<Integer> multiplepositions = new ArrayList<>();
//        int multiple=0;
//        count=0;
//        for(int i = 0; i< labourArrayList.size(); i++) {
//            if ((name.toLowerCase(Locale.ROOT).equals(labourArrayList.get(i).getName().toLowerCase(Locale.ROOT)))||name.toLowerCase(Locale.ROOT)
//                    .equals(labourArrayList.get(i).getLabourId().toLowerCase(Locale.ROOT))) {
//                count++;
//                position = i;
//                multiplepositions.add(multiple,i);
//                multiple+=1;
//
//            }
//        }
//        Log.e("Count11111",""+count);
//        if(count==0){
//            dataArrayList.add(new ModelData(name,"Not Found",0,-1,null));
//
//        }else if(count>1){
//            dataArrayList.add(new ModelData(name,"Multiple",count,-2,multiplepositions));
//        }else if(count==1){
////                dataArrayList.add(new ModelData(labourArrayList.get(position).getName(),labourArrayList.get(position).getLabourId(),1,position,null));
//
//
//            uploadToLocalDatabase(labourArrayList.get(position).getLabourId(),
//                    labourArrayList.get(position).getName());
//        }
//
//
//
//    }
//    private void uploadToLocalDatabase(String labourId , String laborName){
//
//        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//        SimpleDateFormat time = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
//        Date c = Calendar.getInstance().getTime();
//        String currentDate = df.format(c);
//        String currentTime=time.format(c) ;
//        EasyDB easyDB = EasyDB.init(AttendanceActivity.this, "ATTENDANCE_Db")
//                .setTableName(workerType)
//                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
//                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
//                .addColumn(new Column("Date", new String[]{"text", "not null"}))
//                .addColumn(new Column("Time", new String[]{"text", "not null"}))
//                .doneTableColumn();
//        try {
//            easyDB.addData("LabourId" , labourId)
//                    .addData("LabourName" , laborName)
//                    .addData("Date" , currentDate)
//                    .addData("Time" , currentTime)
//                    .doneDataAdding();
//        }catch (SQLiteConstraintException e){
//            Log.e("Exception",e.getMessage());
//        }
//        constructPresentRecyclerView();
////        holder.btn_delete.setVisibility(View.GONE);
//
//    }
//    private void constructPresentRecyclerView() {
//        presentLabourArrayList=new ArrayList<>() ;
//        EasyDB easyDB = EasyDB.init(this, "ATTENDANCE_Db")
//                .setTableName(workerType)
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
//            Log.e("Local","LabourId"+labourId);
//            Log.e("Local","labourName"+labourName);
//            Log.e("Local","date"+date);
//            Log.e("Local","time"+time);
//
//
//            ModelPresentLabour modelCartItem = new ModelPresentLabour(
//                    labourId,labourName,date,time);
//            presentLabourArrayList.add(modelCartItem);
//        }
//        presentLabourArrayList1=presentLabourArrayList;
//        if(presentLabourArrayList.size()>0) {
//            if(workerType.equals("Skilled")) {
//                binding.txtSkilledCount.setText("" + presentLabourArrayList1.size()+"/"+labourArrayList.size());
//            }else{
//                binding.txtUnskilledCount.setText("" + presentLabourArrayList1.size()+"/"+labourArrayList.size());
//            }
//                AdapterAttendance adapterAttendance = new AdapterAttendance(AttendanceActivity.this, presentLabourArrayList,workerType);
//                binding.rvPresentLabour.setAdapter(adapterAttendance);
//
//        }
//    }
//
//    private void forceLogout() {
//        String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
//        progressDialog.show();
//        firebaseAuth.signOut();
//        editor.clear();
//        editor.commit();
//
//        HashMap<String,Object> hashMap=new HashMap<>();
//        hashMap.put("online",false);
//        hashMap.put("forceLogout",true);
//        hashMap.put("forceLogoutReason","Attendance Inactivity");
//        hashMap.put("time",currentTime);
//
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
//                        builder.setCancelable(false);
//                        builder.setTitle(R.string.forcelogout)
//                                .setMessage(R.string.forced_logout_attendance)
//                                .setCancelable(false)
//                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
//                                    startActivity(new Intent(AttendanceActivity.this, SplashActivity.class));
//
//                                });
//                        builder.show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(AttendanceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }


}