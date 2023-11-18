package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelAssociate;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class splashgif extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    private ImageView txt_nirman_skills;
    String userType;
    long siteId;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor_login_first_time;
    private Boolean login_first_time;
    private ProgressDialog progressDialog;
    private ArrayList<ModelSite> siteArrayList;


    ImageView imageView;
    Context context;
    private long industryPosition;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splashgif);
    //    txt_nirman_skills=findViewById(R.id.txt_nirman_skills);
        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferences1 = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences sp_firstTime = getSharedPreferences("LoginFirstTime", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferencesL = getSharedPreferences("Language", Context.MODE_PRIVATE);
        editor_login_first_time=sp_firstTime.edit();
        MyApplication my = new MyApplication( );
        my.updateLanguage(splashgif.this, sharedpreferences.getString("Language","hi"));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        //  binding.txtAdmin.setText("Hii" + binding.etName.getText().toString());
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();
        siteArrayList=new ArrayList<>();

        String paymentDate=sharedpreferences1.getString("PaymentLastUploadedDate","");
        Log.e("Paymentdate",paymentDate);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate1 = dateFormat.format(c);
//        String currentDate1 = "02/11/2022";
        try {
            Date fDate = dateFormat.parse(paymentDate);
            Date cDate=dateFormat.parse(currentDate1);
            if(cDate.after(fDate)){
                Log.e("date11111","After");
                deleteCart();
            }else{
                Log.e("date11111","Before");
            }
        } catch (ParseException e) {
            Log.e("Exception",e.getMessage());
            e.printStackTrace();
        }

        editor=sharedpreferences.edit();


        userType=sharedpreferences.getString("userDesignation","");
        siteId=sharedpreferences.getLong("siteId",0);
        industryPosition=sharedpreferences.getLong("industryPosition",0);

        String currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        Log.e("currentDate",currentDate);
        Log.e("currentTime",currentTime);



        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Boolean resul = firebaseAuth.getCurrentUser() == null;

                    Log.d("CurrentUser", "" + resul);
//                    if (firebaseAuth.getCurrentUser() == null) {
//                        if(sharedpreferences1.getString("LastLogin","").equals("n")){
//                            login_first_time=false;
//                            Intent intent = new Intent(splashgif.this, LoginActivity.class);
//                            intent.putExtra("LoginFirstTime",false);
////                            mp.release();
//                            startActivity(intent);
//                            finish();
//
//
//                        }else{
//                            Intent intent = new Intent(splashgif.this, LoginActivity.class);
//                            intent.putExtra("LoginFirstTime",true);
////                            mp.release();
//                            startActivity(intent);
//                            finish();
//                        }
//
//                    }
//                    else {

                    Log.e("FACU",""+(firebaseAuth.getCurrentUser()==null));
                    Log.e("FACU",""+(industryPosition));
                    if(firebaseAuth.getCurrentUser()!=null){
                        Log.e("firebaseAuth",""+(firebaseAuth.getCurrentUser().getEmail()));
                    }

                    if(firebaseAuth.getCurrentUser()!=null) {
                        Log.e("FACU123",""+(industryPosition));
                        if(industryPosition<=1){
                            if (userType.equals("Supervisor")) {
                                getAttendanceInfo();


                            } else {

                                getSiteList();


                            }
                        }else{
                            Log.e("UserDesusyhuf",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation",""));
                            if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("HR Manager")){
                                startActivity(new Intent(splashgif.this,TimelineOtherIndustry1.class));
                                finish();
                            }else if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("Associate")){
                                Intent intent=new Intent(splashgif.this,WorkplaceActivity.class);
                                Log.e("SiteID123w54",""+getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("siteId",0));
                                intent.putExtra("siteId",getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("siteId",0));
                                intent.putExtra("siteName",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("siteName",""));
                                startActivity(intent);

                                finish();
                            } else{
                                startActivity(new Intent(splashgif.this,EmployeeDashboard.class));
                                finish();
                            }

                        }

                    }else if(sharedpreferences1.getBoolean("LoginFirstTime",true)){
                        startActivity(new Intent(splashgif.this, RegisterActivity.class));
                        finish();
                    }
                    else
                   {

                        startActivity(new Intent(splashgif.this,LoginActivity.class));
                        finish();
                    }
//                    }

                }
            }
        };

        timer.start();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(splashgif.this, R.color.white));

        }

    }

    private void getSiteList() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                Log.e("DataChange", "Yes");
                siteArrayList.clear();
                progressDialog.dismiss();
                Log.e("DateM","Today12:::"+currentDate);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String date =ds.child("date").getValue(String.class);
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    Log.e("DateM","Today:::"+(date==null));
                    if (date != null && !date.equals(currentDate)) {
                        Log.e("DateM","ConTrue");
                        makeOffline(modelSite,currentDate);
                    }else{
                        Log.e("DateM","ConFalse");
                    }
                    siteArrayList.add(modelSite);


                }
                if(getSharedPreferences("UserDetails", MODE_PRIVATE).getLong("industryPosition",0)>1){


                    if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("HR Manager")||
                            getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("Associate")){
                        startActivity(new Intent(splashgif.this,TimelineOtherIndustry1.class));
                        finish();
                    }else{
                        startActivity(new Intent(splashgif.this,EmployeeDashboard.class));
                        finish();
                    }

                }else{
                    startActivity(new Intent(splashgif.this, timelineActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void makeOffline(ModelSite modelSite, String currentDate) {
        Log.e("DateM","MakeOffline");
        HashMap<String,Object> hashMap=new HashMap<>();
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
        hashMap.put("date", currentDate);

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelSite.getHrUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        getMemberList(modelSite);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failure",e.getMessage());
                    }
                });
    }
    private void getMemberList(ModelSite modelSite) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelSite.getHrUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(modelSite.getSiteId())).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelAssociate modelUser=ds.getValue(ModelAssociate.class);
                    makeOffLineUser(modelSite,modelUser.getMemberUid());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeOffLineUser(ModelSite modelSite, String memberUid) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("online",false);
        hashMap.put("skilled",0);
        hashMap.put("unskilled",0);
        hashMap.put("SkilledTime","");
        hashMap.put("UnskilledTime","");
        hashMap.put("picActivity",false);
        hashMap.put("picId","");
        hashMap.put("picTime","");
        hashMap.put("picDate","");
        hashMap.put("picLink","");
        hashMap.put("picRemark","");
        hashMap.put("picLatitude", 0);
        hashMap.put("picLongitude", 0);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        if(modelSite.getHrUid()!=null && memberUid!=null){
            reference.child(modelSite.getHrUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(modelSite.getSiteId())).child("Members").child(memberUid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.e("Done","Member Offline");
                }
            });
        }

    }

    private void getAttendanceInfo() {
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        Log.e("HRSp",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid",""));
        Log.e("HRSp",""+(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")==null));
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid",""))
                .child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Attendance").child(currentDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Intent intent = new Intent(splashgif.this, MemberTimelineActivity.class);
//                            mp.release();
                            startActivity(intent);
                            finish();
                        }else{
                            HashMap<String,Object> hashMap =new HashMap<>();
                            hashMap.put("uid",firebaseAuth.getUid());
                            hashMap.put("online",false);
                            hashMap.put("profile","");
                            hashMap.put("latitude","");
                            hashMap.put("longitude","");
                            hashMap.put("timeStamp","");
                            hashMap.put("time","");
                            hashMap.put("picActivity",false);
                            hashMap.put("picId","");
                            hashMap.put("picTime","");
                            hashMap.put("picDate","");
                            hashMap.put("picLink","");
                            hashMap.put("picRemark","");
                            hashMap.put("picLatitude", 0);
                            hashMap.put("picLongitude", 0);
                            hashMap.put("forceLogout",false);
                            hashMap.put("date","");
                            DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
                            reference1.child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid",""))
                                    .child("Industry").child("Construction").child("Site")
                                    .child(String.valueOf(siteId)).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
//                                            firebaseAuth.signOut();
//                                            editor.clear();
//                                            editor.commit();

                                            startActivity(new Intent(splashgif.this,LoginActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    private void deleteCart() {
        EasyDB easyDB=EasyDB.init(this,"ATTENDANCE_Db")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
        deletePaymentCart();
    }

    private void deletePaymentCart() {
        EasyDB easyDB=EasyDB.init(this,"Payment_Db")
                .setTableName("Payment")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourType", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .addColumn(new Column("Advance", new String[]{"text", "not null"}))
                .addColumn(new Column("Reason", new String[]{"text", "not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
    }
}