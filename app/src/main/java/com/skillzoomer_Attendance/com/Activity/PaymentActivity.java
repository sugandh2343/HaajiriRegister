package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterPaymentData;
import com.skillzoomer_Attendance.com.Adapter.AdapterViewPayment;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelPaymentData;
import com.skillzoomer_Attendance.com.Model.ModelPresentLabour;
import com.skillzoomer_Attendance.com.Model.ModelViewPayment;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityPaymentBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    private ImageView iv_mic;
    private TextView tv_Speech_to_text;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    ArrayList<String> result;
    ArrayList<ModelPaymentData> dataArrayList;
    String currentDate,currentDate1;
    private RecyclerView rv_dataList;
    private Button btn_upload;
    int adapter_position,adapter_position1;
    Boolean localdatabase;
    Boolean search;
    Boolean search1;
    AdapterPaymentData adapterData;
    TextView txt_message_display;
    String workerType;
    long siteId;
    private ArrayList<ModelLabour>labourArrayList;
    private ArrayList<ModelLabour>labourArrayListConfirmed;
    private int count;
    private int position;
    String userType,siteName;
    LayoutToolbarBinding toolbarBinding;
    String presentLabourId,presentLabourName;
    private ArrayList<ModelPresentLabour> presentLabourArrayList;
    String labourId;
    private ProgressDialog progressDialog;
    private String userName;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelViewPayment> modelViewPayments;
    long count1=0;
    SharedPreferences.Editor editor;
    Integer payment_sum;
    long cashInHand;
    private long payment_sum_site=0;
    private long amount_sum=0;
    long expenseId=100;
    String currentTime;
    int selected_option;

    String uid;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Log.e("onCreate","called");
        binding.llMakePayment.setVisibility(View.VISIBLE);
        binding.llViewPayment.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);


        dataArrayList=new ArrayList<>();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferences1 = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editor=sharedpreferences1.edit();
        Intent intent=getIntent();
        firebaseAuth=FirebaseAuth.getInstance();
        userType=sharedpreferences.getString("userDesignation","");
        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        selected_option=sharedpreferences.getInt("workOption",0);
        if(userType.equals("Supervisor")){
            siteId=sharedpreferences.getLong("siteId",0);
            userType=sharedpreferences.getString("userDesignation","");
        }else{
            siteId=intent.getLongExtra("siteId",0);
            siteName=intent.getStringExtra("siteName");
            Log.e("SiteID7860",""+siteName+"::::"+siteId);
        }

        if(intent.getStringExtra("Activity")!=null && intent.getStringExtra("Activity").equals("Search")){
            binding.llMakePayment.setVisibility(View.GONE);
            binding.llViewPayment.setVisibility(View.VISIBLE);
            constructViewPaymentRecyclerView();

        }
        userName=sharedpreferences.getString("userName","");




        dataArrayList=new ArrayList<>();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        result=new ArrayList<>();

        workerType=intent.getStringExtra("WorkerType");
        Log.e("WorkerType",workerType);
        gaetLabourList(workerType);


        SimpleDateFormat df = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }
        SimpleDateFormat df1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = df.format(c);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate1=df1.format(c);
        }
        countDataBase();
        getCashInHand();
        getPaymentSum();
        getExpenseId();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("payment_done"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverDelete,
                new IntentFilter("adapter_position"));
        binding.etSearchLabour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                if(charSequence.toString().length()>2){
                    result.add(charSequence.toString());
                    recyclerViewData(result);
                    result.clear();
                    binding.etSearchLabour.getText().clear();

                }else if(charSequence.length()==0){
                    result.clear();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.ivMic.setOnClickListener(new View.OnClickListener() {
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
                            .makeText(PaymentActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count1==0){
                    Toast.makeText(PaymentActivity.this, "No payment data to show", Toast.LENGTH_SHORT).show();
                }else{
                    binding.llMakePayment.setVisibility(View.GONE);
                    binding.llViewPayment.setVisibility(View.VISIBLE);
                    constructViewPaymentRecyclerView();
                }
            }
        });
        binding.btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llMakePayment.setVisibility(View.VISIBLE);
                binding.llViewPayment.setVisibility(View.GONE);
                countDataBase();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver1,
                new IntentFilter("payment_updated"));
        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("SelectedOPtion",""+selected_option);

                Log.e("PaymentSum",""+payment_sum);
                Log.e("PaymentSum","C"+cashInHand);
                Log.e("PaymentSum","CU"+(userType.equals("Supervisor")));
                Log.e("PaymentSum","CU"+(userType));
                if(userType.equals("Supervisor")){
                    Log.e("PaymentSum","CU1"+userType);
                    if(cashInHand<payment_sum){
                        String currentTime="";
                        String currentDate="";
                        String timestamp=""+System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                        }
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("date",currentDate);
                        hashMap.put("time",currentTime);
                        hashMap.put("remark","Insufficient Fund");
                        hashMap.put("amount",String.valueOf(payment_sum));
                        hashMap.put("cashInHand",String.valueOf(cashInHand));
                        hashMap.put("name",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
                        hashMap.put("uid",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("uid",""));
                        hashMap.put("timestamp",timestamp);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Error").child(currentDate).child(timestamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PaymentActivity.this);
                                        builder.setTitle(R.string.log_out)
                                                .setMessage(R.string.insufficient_fund_heading)
                                                .setCancelable(false)
                                                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {

                                                    dialogInterface.dismiss();

                                                })
                                                .setNegativeButton(getString(R.string.fund_request), (dialogInterface, i) -> {
                                                    startActivity(new Intent(PaymentActivity.this,FundRequestActivity.class));

                                                });
                                        builder.show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }else{

                        getPaymentData();
                    }
                }else{
                    if(selected_option==2){
                        getPaymentData();
                    }else if(selected_option==1){
                        if(cashInHand<payment_sum){
                            Toast.makeText(PaymentActivity.this, R.string.insufficient_fund, Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.show();
                            cashInHand=cashInHand-payment_sum;
                            getPaymentData();
                        }
                    }else if(selected_option==3){
                        getSiteMemberStatus();
                    }

                }



            }
        });
    }

    private void getSiteMemberStatus() {
        Log.e("SiteId",""+siteId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Snapshot",""+snapshot.child("memberStatus").equals("Pending"));
                if(snapshot.child("memberStatus").getValue(String.class).equals("Pending")){
                    progressDialog.show();
                    cashInHand=cashInHand-payment_sum;
                    getPaymentData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getExpenseId() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Expense").child(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.getChildrenCount()>0){
                    expenseId= (snapshot.getChildrenCount()+1)*100;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPaymentSum() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("paymentSum")){
                    payment_sum_site=snapshot.child("paymentSum").getValue(long.class);
                    Log.e("PaymentSum","Site:"+payment_sum_site);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCashInHand() {
        Log.e("SiteId1294",""+siteId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("cashInHand")){
                    cashInHand=snapshot.child("cashInHand").getValue(long.class);

                }else{
                    cashInHand=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPaymentData() {
        modelViewPayments=new ArrayList<>();
        modelViewPayments.clear();
        Log.e("Model12345",""+modelViewPayments.size());
        int count=0;
        EasyDB db = EasyDB.init(PaymentActivity.this, "Payment_Db")
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
        Cursor res = db.getAllData();
        amount_sum=0;

        while (res.moveToNext()) {
            count++;
            String timestamp=res.getString(1);
            String labourId = res.getString(2);
            String labourName = res.getString(3);
            String labourType = res.getString(4);
            String date = res.getString(5);
            String time = res.getString(6);
            String advance = res.getString(7);
            String reason = res.getString(8);
            Log.e("Local", "LabourId" + labourId);
            Log.e("Local", "labourName" + labourName);
            Log.e("Local", "date" + date);
            Log.e("Local", "time" + time);
            Log.e("Local", "advance" + advance);
            Log.e("Local", "type" + labourType);
            amount_sum=amount_sum+Long.parseLong(advance);

            ModelViewPayment modelViewPayment=new ModelViewPayment(String.valueOf(count),labourId,labourName,labourType,date,time,advance,timestamp,reason);
           uploadToFirebase(count,modelViewPayment);
        }
        uploadToSiteDatabase(count);


    }

    private void uploadToSiteDatabase(int count) {
        HashMap<String,Object> hashMap=new HashMap<>();
        progressDialog.show();
        cashInHand=cashInHand-amount_sum;
        hashMap.put("cashInHand",cashInHand);
        hashMap.put("paymentSum",(payment_sum_site+amount_sum));
        Log.e("Payment",""+payment_sum_site+amount_sum);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PaymentActivity.this, "Upload of "+ count +" Payments completed successfully.", Toast.LENGTH_LONG).show();
                updateToExpense(amount_sum);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateToExpense(long amount_sum) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);
        String recId=""+siteId+expenseId;
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("expId",recId);
        hashMap.put("expType","Labour");
        hashMap.put("amount",String.valueOf(amount_sum));
        hashMap.put("expTime",currentTime);
        hashMap.put("expDate",currentDate);
        hashMap.put("expRemark","Payment To Labour");
        hashMap.put("entryByUid",sharedpreferences.getString("uid",""));
        hashMap.put("entryByName",sharedpreferences.getString("userName",""));
        hashMap.put("entryByType",userType);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Expense").child(currentDate).child(recId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        deleteDatabase();
                        Intent intent=new Intent(PaymentActivity.this, MemberTimelineActivity.class);
                        intent.putExtra("siteId",siteId);
                        intent.putExtra("siteName",siteName);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteDatabase() {
        EasyDB easyDB=EasyDB.init(this,"Payment_Db")
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
        easyDB.deleteAllDataFromTable();
    }

    private void uploadToFirebase(int count, ModelViewPayment modelViewPayment) {
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("date",modelViewPayment.getDate());
        hashMap.put("time",modelViewPayment.getTime());
        hashMap.put("amount",modelViewPayment.getAmt());
        hashMap.put("labourId",modelViewPayment.getLabourId());
        hashMap.put("labourName",modelViewPayment.getLabourName());
        hashMap.put("labourType",modelViewPayment.getLabourType());
        hashMap.put("uploadedByUid",firebaseAuth.getUid());
        hashMap.put("uploadedByType",userType);
        hashMap.put("uploadedByName",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
        hashMap.put("timestamp",timestamp);
        hashMap.put("reason",modelViewPayment.getReason());

        Log.e("date11111",currentDate1);
        Log.e("PaymentUpload",""+siteId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(currentDate).child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        editor.putString("PaymentLastUploadedDate",currentDate1);
                        editor.apply();
                        uploadToLabourDatabase(modelViewPayment,count,timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Failure",e.getMessage());
                        Toast.makeText(PaymentActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadToLabourDatabase(ModelViewPayment modelViewPayment, int count, String timestamp) {
        Log.e("Upload",String.valueOf(siteId));
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("date",modelViewPayment.getDate());
        hashMap.put("time",modelViewPayment.getTime());
        hashMap.put("amount",modelViewPayment.getAmt());
        hashMap.put("labourId",modelViewPayment.getLabourId());
        hashMap.put("labourName",modelViewPayment.getLabourName());
        hashMap.put("labourType",modelViewPayment.getLabourType());
        hashMap.put("timestamp",timestamp);
        hashMap.put("reason",modelViewPayment.getReason());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Industry").child("Construction").child("Site");
        reference.child(String.valueOf(siteId)).child("Labours").child(modelViewPayment.getLabourId()).child("Payments").child(modelViewPayment.getDate()).child(timestamp)
                .setValue(hashMap);

        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(modelViewPayment.getLabourId()).child("payableAmt");
        long Amount=(long) Integer.parseInt(modelViewPayment.getAmt());
        reference1.setValue(ServerValue.increment(-Amount));
//                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        getLabourWages(modelViewPayment);
//                        Log.e("Done","Finally");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });

    }

    private void updateToSitePayment(ModelViewPayment modelViewPayment, int count) {
        Log.e("Upload",String.valueOf(siteId));
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("date",modelViewPayment.getDate());
        hashMap.put("time",modelViewPayment.getTime());
        hashMap.put("amount",modelViewPayment.getAmt());
        hashMap.put("labourId",modelViewPayment.getLabourId());
        hashMap.put("labourName",modelViewPayment.getLabourName());
        hashMap.put("labourType",modelViewPayment.getLabourType());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(modelViewPayment.getDate()).child(modelViewPayment.getLabourId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String amount=snapshot.child("amount").getValue(String.class);
                            Log.e("UpdatedAmt","OriginalAmt"+amount);
                            int amount1=Integer.parseInt(modelViewPayment.getAmt())+Integer.parseInt(amount);
                            Log.e("UpdatedAmt",String.valueOf(modelViewPayment.getAmt()));
                            Log.e("UpdatedAmt","AddedAmt"+amount1);
                            hashMap.put("date",modelViewPayment.getDate());
                            hashMap.put("time",modelViewPayment.getTime());
                            hashMap.put("amount",String.valueOf(amount1));
                            hashMap.put("labourId",modelViewPayment.getLabourId());
                            hashMap.put("labourName",modelViewPayment.getLabourName());
                            hashMap.put("labourType",modelViewPayment.getLabourType());
                            hashMap.put("uploadedBy",firebaseAuth.getUid());
                            reference.child(String.valueOf(siteId)).child("Labours").child(modelViewPayment.getLabourId()).child("Payments").child(modelViewPayment.getDate())
                                    .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.e("OK","OOKKKK");

                                        }
                                    });
                        }else{
                            reference.child(String.valueOf(siteId)).child("Labours").child(modelViewPayment.getLabourId()).child("Payments").child(modelViewPayment.getDate())
                                    .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.e("UpdatedAmt","2");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void getLabourWages(ModelViewPayment modelViewPayment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(modelViewPayment.getLabourId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                updatePayableAmt(modelViewPayment.getAmt(), payableAmt,modelViewPayment);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updatePayableAmt(String wages, long payableAmt, ModelViewPayment modelViewPayment) {
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("payableAmt",payableAmt-Integer.parseInt(wages));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(modelViewPayment.getLabourId()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.e("Done","PayableAmt"+payableAmt);
                    }
                });
    }

    private void updateDataHashMap(String count, HashMap<String, Object> hashMap, String labourId, String date) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).child("Payments").child(date).child(count).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("Done","Finally");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception",e.getMessage());
                    }
                });
    }

    private void constructViewPaymentRecyclerView() {
        modelViewPayments=new ArrayList<>();
        int count=0;
        EasyDB db = EasyDB.init(PaymentActivity.this, "Payment_Db")
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
        Cursor res = db.getAllData();

        while (res.moveToNext()) {
            count++;
            String timestamp=res.getString(1);
            String labourId = res.getString(2);
            String labourName = res.getString(3);
            String labourType = res.getString(4);
            String date = res.getString(5);
            String time = res.getString(6);
            String advance = res.getString(7);
            Log.e("Local", "LabourId" + labourId);
            Log.e("Local", "labourName" + labourName);
            Log.e("Local", "date" + date);
            Log.e("Local", "time" + time);
            Log.e("Local", "advance" + advance);
            Log.e("Local", "type" + labourType);
            ModelViewPayment modelViewPayment=new ModelViewPayment(String.valueOf(count),labourId,labourName,labourType,date,time,advance,timestamp);
            modelViewPayments.add(modelViewPayment);
        }
        AdapterViewPayment adapterViewPayment=new AdapterViewPayment(this,modelViewPayments,siteId);
        binding.rvViewPayment.setAdapter(adapterViewPayment);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                binding.tvSpeechToText.setText(
                        Objects.requireNonNull(result).get(0));
                recyclerViewData(result);
            }
        }
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MessageReceive","Yes");
            countDataBase();



        }


    };
    public BroadcastReceiver mMessageReceiverDelete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter_position=intent.getIntExtra("position",0);
            dataArrayList.remove(adapter_position);
            adapterData.notifyItemRemoved(adapter_position);
            binding.tvSpeechToText.setText("");
            binding.etSearchLabour.getText().clear();



        }


    };
    public BroadcastReceiver mMessageReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MessageReceive","Yes");
           constructViewPaymentRecyclerView();



        }


    };

    private void countDataBase() {
        payment_sum=0;
        count=0;
        EasyDB db = EasyDB.init(PaymentActivity.this, "Payment_Db")
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
        Cursor res = db.getAllData();

        while (res.moveToNext()) {
            count++;
            String timestam=res.getString(1);
            String labourId = res.getString(2);
            String labourName = res.getString(3);
            String labourType = res.getString(4);
            String date = res.getString(5);
            String time = res.getString(6);
            String advance = res.getString(7);
            payment_sum=payment_sum+Integer.parseInt(advance);

            Log.e("Local", "LabourId" + labourId);
            Log.e("Local", "labourName" + labourName);
            Log.e("Local", "date" + date);
            Log.e("Local", "time" + time);
            Log.e("Local", "advance" + advance);
            Log.e("Local", "type" + labourType);


        }
        count1 = db.getAllData().getCount();
        binding.countPayment.setText(String.valueOf(count1));
        binding.countPaymentSum.setText(String.valueOf(payment_sum));
        Log.e("countDatabase", "" + count);
        db.close();
    }

    private void recyclerViewData(ArrayList<String> result) {
        Log.e("result",result.get(0));

        String[] separated = result.get(0).split(" ");
        for(int i=0;i<separated.length;i++){
            Log.e("Split",separated[i]);
            checkInLabour(separated[i]);
//            dataArrayList.add(new ModelData(currentDate,separated[i]));
        }
        recyclerViewConstruct();
//        separated[0]; // this will contain "Fruit"
//        separated[1]; // this will contain " they taste good"


    }
    private void checkInLabour(String name) {
        Log.e("Called",""+labourArrayList.size());
        ArrayList<Integer> multiplepositions = new ArrayList<>();
        int multiple=0;
        count=0;
        for(int i=0;i<labourArrayList.size();i++) {
            Log.e("Search1111",""+i+"Search: "+name.toLowerCase(Locale.ROOT)+"Check:"+labourArrayList.get(i).getName().toLowerCase(Locale.ROOT)+
                    "Result:"+name.toLowerCase(Locale.ROOT).equals(labourArrayList.get(i).getName().toLowerCase(Locale.ROOT)));
            if ((labourArrayList.get(i).getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))||
                   labourArrayList.get(i).getLabourId().toLowerCase(Locale.ROOT).contains( name.toLowerCase(Locale.ROOT)))) {

                count++;
                position = i;
                multiplepositions.add(multiple,i);
                multiple+=1;

            }
        }
        Log.e("Count11111",""+count);
        if(count==0){
            dataArrayList.add(new ModelPaymentData("Not Found",name,workerType,0,-1,0));

        }else if(count>1){
            dataArrayList.add(new ModelPaymentData("Multiple",name,workerType,count,-1,0));
        }else if(count==1){
            if(labourArrayList.get(position).getStatus().equals("Registered")){
                dataArrayList.add(new ModelPaymentData(labourArrayList.get(position).getLabourId(),labourArrayList.get(position).getName(),
                        labourArrayList.get(position).getType(),1,position,labourArrayList.get(position).getPayableAmt()));
            }




        }



    }




    private void recyclerViewConstruct() {
        Log.e("dataArrayList",""+dataArrayList.size());
        Log.e("SiteID1122",""+siteName+"::::::::"+siteId);
        adapterData=new AdapterPaymentData(PaymentActivity.this,dataArrayList,siteId,siteName);
        binding.rvDataList.setAdapter(adapterData);
    }
    private void gaetLabourList(String workerType) {
        labourArrayList=new ArrayList<>();
        Log.e("Called","1");
        Log.e("SiteId1111111",String.valueOf(siteId));
       Log.e("labourList",workerType);
        Log.e("labourList",""+labourArrayList.size());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours").orderByChild("type").equalTo(workerType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
                    labourArrayList.add(modelLabour);

                }
                Log.e("LabourListAtee",""+labourArrayList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {

        Log.e("onResume","called");
        countDataBase();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause","called");
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(PaymentActivity.this,MemberTimelineActivity.class);
        intent.putExtra("siteId",siteId);
        intent.putExtra("siteName",siteName);
        startActivity(intent);
    }
}