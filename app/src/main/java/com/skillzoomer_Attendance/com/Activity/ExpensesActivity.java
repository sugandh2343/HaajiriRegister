package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterOtherExpenses;
import com.skillzoomer_Attendance.com.Model.MopdelOtherExpenses;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityExpensesBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ExpensesActivity extends AppCompatActivity {
    ActivityExpensesBinding binding;
    long siteId;
    String currentDate,currentTime;
    long expenseId=100;
    long cashInHand;
    private ProgressDialog progressDialog;
    private String userType;
    String timestamp,siteName,companyName;
    private ArrayList<MopdelOtherExpenses> otherExpensesArrayList;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityExpensesBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        binding.llOtherExpenses.setVisibility(View.GONE);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent=getIntent();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);
        userType=sharedpreferences.getString("userDesignation","");
        otherExpensesArrayList=new ArrayList<>();
        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
       
        if(userType.equals("Supervisor")){
            siteId=sharedpreferences.getLong("siteId",0);
            userType=sharedpreferences.getString("userDesignation","");
            binding.btnOtherEpenses.setVisibility(View.GONE);
            binding.llDetails.setVisibility(View.GONE);
        }else{
            siteId=intent.getLongExtra("siteId",0);
            siteName=intent.getStringExtra("siteName");
            binding.btnOtherEpenses.setVisibility(View.VISIBLE);
            checkForOtherExpenses();
            getSiteMemberStatus(siteId);
            Log.e("SiteID7860",""+siteName+"::::"+siteId);

        } 

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submitting Entry");
        progressDialog.setCanceledOnTouchOutside(false);
        getExpenseId();
//        getCashInHand();
        binding.btnSkilledPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ExpensesActivity.this, PaymentActivity.class);
                intent.putExtra("WorkerType","Skilled");
                startActivity(intent);
            }
        });
        binding.btnUnskilledPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ExpensesActivity.this,PaymentActivity.class);
                intent.putExtra("WorkerType","Unskilled");
                startActivity(intent);
            }
        });
        binding.btnOtherEpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llOtherExpenses.setVisibility(View.VISIBLE);
            }
        });
        binding.btnEpenseSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etOtherExpense.getText().toString())){
                    Toast.makeText(ExpensesActivity.this, "Enter other Expense Amount", Toast.LENGTH_SHORT).show();
                } else{
                    cashInHand=cashInHand-Integer.parseInt(binding.etOtherExpense.getText().toString());
                    Log.e("Amount",""+Integer.parseInt(binding.etOtherExpense.getText().toString()));
                    Log.e("Amount","C"+cashInHand);
                    updateToDatabase(cashInHand);
//                    Toast.makeText(ExpensesActivity.this, "Record Has been updated successfully", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void getSiteMemberStatus(long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("memberStatus").getValue(String.class).equals("Pending")) {

                    binding.llPaymentDo.setVisibility(View.VISIBLE);



                }else{

                    binding.llPaymentDo.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForOtherExpenses() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("OtherExpense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                otherExpensesArrayList.clear();
                if(snapshot.exists()){
                    binding.llDetails.setVisibility(View.VISIBLE);
                    for(DataSnapshot ds:snapshot.getChildren()){
                        MopdelOtherExpenses otherExpenses=ds.getValue(MopdelOtherExpenses.class);
                        otherExpensesArrayList.add(otherExpenses);
                    }
                    AdapterOtherExpenses adapterOtherExpenses=new AdapterOtherExpenses(ExpensesActivity.this,otherExpensesArrayList,siteId);
                    binding.rvOtherExpenses.setAdapter(adapterOtherExpenses);
                }else{
                    binding.llDetails.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadToSiteDatabase(long cashInHand1) {
        HashMap<String,Object> hashMap=new HashMap<>();
        Log.e("Amount","S"+ cashInHand1);
        hashMap.put("cashInHand", cashInHand1);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ExpensesActivity.this, "Expense of Amount:"+binding.etOtherExpense.getText().toString()+
                        " submitted successfully", Toast.LENGTH_SHORT).show();
                binding.llOtherExpenses.setVisibility(View.GONE);
                progressDialog.dismiss();
                startActivity(new Intent(ExpensesActivity.this,AdvancesHomeActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExpensesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCashInHand() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
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

    private void updateToDatabase(long cashInHand) {
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);
        String recId=""+siteId+expenseId;
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("expId",timestamp);
        hashMap.put("expType","Other");
        hashMap.put("amount",binding.etOtherExpense.getText().toString());
        hashMap.put("expTime",currentTime);
        hashMap.put("expDate",currentDate);
        hashMap.put("expRemark",binding.etOtherExpenseRemark.getText().toString());
        hashMap.put("entryByUid",sharedpreferences.getString("uid",""));
        hashMap.put("entryByName",sharedpreferences.getString("userName",""));
        hashMap.put("entryByType",userType);
        hashMap.put("show",true);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("OtherExpense").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ExpensesActivity.this, "Expense of Amount:"+binding.etOtherExpense.getText().toString()+
                                " submitted successfully", Toast.LENGTH_SHORT).show();
                        binding.llOtherExpenses.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        checkForOtherExpenses();
//                        startActivity(new Intent(ExpensesActivity.this,AdvancesHomeActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ExpensesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getExpenseId() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("OtherExpense").addValueEventListener(new ValueEventListener() {
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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ExpensesActivity.this, AdvancesHomeActivity.class);
        intent.putExtra("type","Skilled");
        intent.putExtra("siteName",siteName);
        intent.putExtra("siteId",siteId);
        startActivity(intent);
        finish();

    }
}