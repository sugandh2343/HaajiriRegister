package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterAttendance;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelPresentLabour;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityCheckAttendanceBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CheckAttendanceActivity extends AppCompatActivity {
    ActivityCheckAttendanceBinding binding;
    private ProgressDialog progressDialog;
    private String workerType;
    private String userName;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelPresentLabour> presentLabourArrayList1;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorLogin;
    public int counter = 600;
    public int LoginAttempt = 0;
    public String LoginTime = "";
    private String lastLoginTime;
    private Boolean attendance_activity;
    private int counterValue=2400;
    long siteId;
    private ArrayList<ModelLabour>labourArrayList;
    private ArrayList<ModelLabour>labourArrayListConfirmed;
    private int count;
    private int position;
    String userType,siteName;
    private ArrayList<ModelPresentLabour> presentLabourArrayList;
    int adapter_position,adapter_position1;
    int localDatabaseSize=0;
    double latitude=0.0,longitude=0.0;

    String uid;



    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
        @Override
        public void onTick(long l) {
//            Log.e("counter", "" + counter);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCheckAttendanceBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        workerType=intent.getStringExtra("type");
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editorLogin= spLogin.edit();
        MyApplication my = new MyApplication( );
        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        lastLoginTime=spLogin.getString("LastLoginTime","NA");
        attendance_activity=spLogin.getBoolean("AttendanceActivity",false);
        Log.e("BooleanAtt1234",""+attendance_activity);
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
//        }
        binding.llForceLogout.setVisibility(View.GONE);

        userType=sharedpreferences.getString("userDesignation","");
        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        if(userType.equals("Supervisor")) {
            siteName = sharedpreferences.getString("siteName", "");
            siteId = sharedpreferences.getLong("siteId", 0);
        }else{
            siteName=intent.getStringExtra("siteName");
            siteId=intent.getLongExtra("siteId",0);
            Log.e("SiteIdC123",""+siteId);
        }

        Log.e("CheckAttendance",siteName);
        userName=sharedpreferences.getString("userName","");
        editor= sharedpreferences.edit();
        presentLabourArrayList=new ArrayList<>() ;
        presentLabourArrayList1=new ArrayList<>() ;
        lastLoginTime=spLogin.getString("LastLoginTime","NA");
        attendance_activity=spLogin.getBoolean("AttendanceActivity",false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver1,
                new IntentFilter("attendance_position"));
        constructPresentRecyclerView();

        binding.btnBackToAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CheckAttendanceActivity.this,MainActivity.class);
                intent.putExtra("type",workerType);
                intent.putExtra("siteId",siteId);
                intent.putExtra("siteName",siteName);
                startActivity(intent);
            }
        });



        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckAttendanceActivity.this);
                builder.setMessage(R.string.are_you_sure_tou_want_to_upload)
                        .setTitle(R.string.confirmation)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            editorLogin.putBoolean("AttendanceActivity",true);
                            editorLogin.apply();
                            editorLogin.commit();
                            Log.e("ATTAfter",""+spLogin.getBoolean("AttendanceActivity",false));

                            getAttendanceDetails(siteId);


                        }).setNegativeButton(R.string.no , (dialogInterface , i) -> {
                            dialogInterface.dismiss();

                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void getAttendanceDetails(long siteId) {
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate).orderByChild("labourType").equalTo(workerType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){


                    Toast.makeText(CheckAttendanceActivity.this, getString(R.string.upload_attendance_only_once), Toast.LENGTH_SHORT).show();
//                    if(userType.equals("Supervisor")){
//                        startActivity(new Intent(CheckAttendanceActivity.this, MemberTimelineActivity.class));
//                    }else{
//                        startActivity(new Intent(CheckAttendanceActivity.this, timelineActivity.class));
//                    }
                }  else{
                    progressDialog.setMessage("Uploading Data");
                    progressDialog.show();
                    uploadToFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void uploadToFirebase() {
        localDatabaseSize=0;


        EasyDB easyDB = EasyDB.init(this, "Attendance" + workerType)
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        Cursor res = easyDB.getAllData();
        int countDatabase=res.getCount();
        while (res.moveToNext()) {
            localDatabaseSize++;
            String labourId = res.getString(1);
            String labourName = res.getString(2);
            String date = res.getString(3);
            String time = res.getString(4);
            Log.e("Local","LabourId"+labourId);
            Log.e("Local","labourName"+labourName);
            Log.e("Local","date"+date);
            Log.e("Local","time"+time);


            ModelPresentLabour modelCartItem = new ModelPresentLabour(
                    labourId,labourName,date,time);
            startUpload(modelCartItem);


        }
        if(localDatabaseSize==countDatabase){
            Log.e("PresentLS",""+presentLabourArrayList.size());
            uploadToSite(presentLabourArrayList.size());

        }
        deleteCart();




//        startActivity(new Intent(MainActivity.this,MemberTimelineActivity.class));


    }
    private void startUpload(ModelPresentLabour modelCartItem) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("labourId",modelCartItem.getLabourId());
        hashMap.put("labourName",modelCartItem.getLabourName());
        hashMap.put("dateOfUpload",modelCartItem.getDate());
        hashMap.put("timeOfupload",modelCartItem.getTime());
        hashMap.put("labourType",workerType);
        hashMap.put("uploadedByName",userName);
        hashMap.put("uploadedBYUid",firebaseAuth.getUid());
        hashMap.put("uploadedByType",userType);
        hashMap.put("status","P");
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
                        Log.e("Exception",e.getMessage());
                    }
                });
    }
    private void uploadToLabourAttendance(ModelPresentLabour modelCartItem) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("date",modelCartItem.getDate());
        hashMap.put("time",modelCartItem.getTime());
        hashMap.put("status","P");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(modelCartItem.getLabourId())
                .child("Attendance").child(modelCartItem.getDate()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      getLabourWages(modelCartItem.getLabourId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception",e.getMessage());
                    }
                });
    }

    private void getLabourWages(String labourId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long wages = snapshot.child("wages").getValue(long.class);
                Log.e("Wages",String.valueOf(wages));
                long payableAmt = 0;
                if(snapshot.child("payableAmt").getValue(long.class)!=null) {
                    payableAmt = snapshot.child("payableAmt").getValue(long.class);
                }else{
                    payableAmt=0;
                }
                updatePayableAmt(wages, payableAmt,labourId);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updatePayableAmt(long wages, long payableAmt, String labourId) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("payableAmt",wages+payableAmt);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.e("Done","PayableAmt"+payableAmt);
                    }
                });
    }

    private void uploadToSite(int size) {
        SimpleDateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }

        Date c = Calendar.getInstance().getTime();
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = df.format(c);
        }
        HashMap<String,Object> hashMap=new HashMap<>();
        String time=workerType+"Time";
        String currentTime1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        Log.e("Size",""+size);
        Log.e("WorkerType1111",workerType);
        if(size>0) {
            if (workerType != null) {
                if (workerType.equals("Skilled")) {
                    hashMap.put("skilled", size);
                    hashMap.put("skilledSeen", false);
                    hashMap.put(time, currentTime1);
                    hashMap.put("date", currentDate);
                    hashMap.put("attendanceEditSkilled", false);
                } else {
                    hashMap.put("unskilled", size);
                    hashMap.put("unSkilledSeen", false);
                    hashMap.put(time, currentTime1);
                    hashMap.put("date", currentDate);
                    hashMap.put("attendanceEditUnSkilled", false);
                }
            }
        }else{
            hashMap.put(time, currentTime1);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(!userType.equals("Supervisor")){
                            progressDialog.dismiss();
                            AlertDialog.Builder builder=new AlertDialog.Builder(CheckAttendanceActivity.this);
                            builder.setTitle(getString(R.string.success))
                                    .setMessage(R.string.attendance_upload_suceessfully)
                                    .setCancelable(true)
                                    .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                                        progressDialog.dismiss();
                                        countDownTimer.cancel();
                                        editorLogin.putBoolean("AttendanceActivity",true);
                                        editorLogin.apply();
                                        editorLogin.commit();
                                        startActivity(new Intent(CheckAttendanceActivity.this,timelineActivity.class));




                                    });

                            builder.show();

                        }else{

                            uploadToMember(hashMap);
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }



    private void uploadToMember(HashMap<String, Object> hashMap) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                AlertDialog.Builder builder=new AlertDialog.Builder(CheckAttendanceActivity.this);
                builder.setTitle(getString(R.string.success))
                        .setMessage(R.string.attendance_upload_suceessfully)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                            progressDialog.dismiss();
                            countDownTimer.cancel();
                            editorLogin.putBoolean("AttendanceActivity",true);
                            editorLogin.apply();
                            editorLogin.commit();
                            getAdminUid();


                        });

                builder.show();
            }
        });
    }

    private void getAdminUid() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid=snapshot.child("hrUid").getValue(String.class);
                getFirebaseToken(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFirebaseToken(String uid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("token").getValue(String.class);
                sendNotification(token);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String token) {
        Log.d("token : ", token);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();

                jsonobject_notification.put("title", "Associate has uploaded Attendance");
                jsonobject_notification.put("body", userName +" "+ "has uploaded the attendance of"+ " "+workerType+ " "+" at"+" "+siteName);




            JSONObject jsonobject_data = new JSONObject();
            jsonobject_data.put("imgurl", "https://firebasestorage.googleapis.com/v0/b/haajiri-register.appspot.com/o/FCMImages%2Fopt2.png?alt=media&token=f4e37ac3-a1ff-417f-a0c7-3e5445515505");

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
                Intent intent=new Intent(CheckAttendanceActivity.this,MemberTimelineActivity.class);
                intent.putExtra("siteName",siteName);
                intent.putExtra("siteId",siteId);
                Log.e("CheckAttendance","ID:"+siteId+"name"+siteName);
                startActivity(intent);



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error :  ", String.valueOf(error));


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

    private void forceLogout() {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CheckAttendanceActivity.this);
        builder.setCancelable(false);
        String finalCurrentTime = currentTime;
        builder.setTitle("Forced Log Out")
                .setMessage("You have been forced logout due to not completed Attendance Activity in due time. Contact your administrator to login again.")
                .setCancelable(true)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    progressDialog.show();
                    firebaseAuth.signOut();
                    editor.clear();
                    editor.commit();

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("online",false);
                    hashMap.put("forceLogout",true);
                    hashMap.put("forceLogoutReason","Attendance Inactivity");
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
                                    Toast.makeText(CheckAttendanceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });
        builder.show();
    }

    private void deleteLabourFromCart(String labourId , int adapter_position) {
        EasyDB easyDB=EasyDB.init(CheckAttendanceActivity.this,"Attendance" + workerType)
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        easyDB.deleteRow(1,labourId);
        Toast.makeText(CheckAttendanceActivity.this,"Labour Removed From List",Toast.LENGTH_SHORT).show();
        //refresh list
        presentLabourArrayList.remove(adapter_position);
        if(presentLabourArrayList.size()>0){
            constructPresentRecyclerView();
        }else{
            Intent intent=new Intent(CheckAttendanceActivity.this,MainActivity.class);
            intent.putExtra("type",workerType);
            intent.putExtra("siteId",siteId);
            intent.putExtra("siteName",siteName);
            startActivity(intent);
        }



    }
    public BroadcastReceiver mMessageReceiver1=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context , Intent intent) {
            progressDialog.show();
            adapter_position1=intent.getIntExtra("position1",0);

            Log.e("ADP1",""+adapter_position1);
            if(adapter_position1<presentLabourArrayList.size()){

                    String labourId=presentLabourArrayList.get(adapter_position1).getLabourId();
                    Log.e("LabourID12334",labourId);
                    deleteLabourFromCart(labourId,adapter_position1);



            }





        }
    };
    private void constructPresentRecyclerView() {

        presentLabourArrayList.clear();
        EasyDB easyDB = EasyDB.init(this, "Attendance" + workerType)
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String labourId = res.getString(1);
            String labourName = res.getString(2);
            String date = res.getString(3);
            String time = res.getString(4);
            Log.e("Local","LabourId"+labourId);
            Log.e("Local","labourName"+labourName);
            Log.e("Local","date"+date);
            Log.e("Local","time"+time);


            ModelPresentLabour modelCartItem = new ModelPresentLabour(
                    labourId,labourName,date,time);
            presentLabourArrayList.add(modelCartItem);
        }
        presentLabourArrayList1=presentLabourArrayList;
        if(presentLabourArrayList.size()>0) {
            binding.txtTotalWorker.setText(""+presentLabourArrayList.size());
            AdapterAttendance adapterAttendance = new AdapterAttendance(CheckAttendanceActivity.this , presentLabourArrayList,workerType);
            binding.rvPresentLabour.setAdapter(adapterAttendance);


            Log.e("PresentLabourList",""+presentLabourArrayList.size());
        }
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    private void deleteCart() {
        EasyDB easyDB=EasyDB.init(this,"Attendance" + workerType)
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
    }


}