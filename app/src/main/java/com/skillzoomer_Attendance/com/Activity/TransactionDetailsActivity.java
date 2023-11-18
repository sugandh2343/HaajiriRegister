package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelTransaction;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.databinding.ActivityTransactionDetailsBinding;

import java.util.ArrayList;

public class TransactionDetailsActivity extends AppCompatActivity {
    ActivityTransactionDetailsBinding binding;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelUser>userArrayList;
    private ArrayList<ModelTransaction> transactionArrayList;
    String timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTransactionDetailsBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        userArrayList=new ArrayList<>();
        transactionArrayList=new ArrayList<>();

        Intent intent=getIntent();
        timestamp=intent.getStringExtra("timestamp");
        getUserDetails();
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransactionDetailsActivity.this,TransactionListActivity.class));
                finish();
            }
        });


    }

    private void getUserDetails() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.txtUserName.setText(snapshot.child("name").getValue(String.class));
                binding.txtUserMobile.setText(snapshot.child("mobile").getValue(String.class));
                getTransactionDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTransactionDetails() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("promoApplied").getValue(Boolean.class)){
                    binding.llPromo.setVisibility(View.VISIBLE);
                    binding.txtPromo.setText(snapshot.child("promoTitle").getValue(String.class));
                }else{
                    binding.llPromo.setVisibility(View.GONE);
                }
                binding.txtOrderid.setText(snapshot.child("orderId").getValue(String.class));
                binding.txtFileName.setText(snapshot.child("downloadFile").getValue(String.class));

                binding.txtFrom.setText(snapshot.child("fromDate").getValue(String.class));
                binding.txtTo.setText(snapshot.child("toDate").getValue(String.class));
                binding.txtPaidAmount.setText(snapshot.child("paidAmount").getValue(String.class));
                binding.txtPaymentId.setText(snapshot.child("paymentId").getValue(String.class));
                binding.txtPaymentDate.setText(snapshot.child("dateOfPayment").getValue(String.class));
                binding.txtPaymentTime.setText(snapshot.child("timeOfPayment").getValue(String.class));
                binding.txtTotalAmount.setText(snapshot.child("totalAmount").getValue(String.class));
                binding.txtFileType.setText(snapshot.child("fileType").getValue(String.class));
                int savingsAmount=Integer.parseInt(String.valueOf(binding.txtTotalAmount.getText().toString()))-Integer.parseInt(String.valueOf(binding.txtPaidAmount.getText().toString()));
                binding.txtSavingsAmount.setText(String.valueOf(savingsAmount));
                if(!snapshot.child("status").getValue(String.class).equals("Success")){
                    binding.llPromo.setVisibility(View.GONE);
                    binding.llPaidAmount.setVisibility(View.GONE);
                    binding.llPaymentId.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}