package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterTransaction;
import com.skillzoomer_Attendance.com.Model.ModelTransaction;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityTransactionListBinding;

import java.util.ArrayList;

public class TransactionListActivity extends AppCompatActivity {
    ActivityTransactionListBinding binding;
    private ArrayList<ModelTransaction> transactionArrayList;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTransactionListBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        transactionArrayList=new ArrayList<>();
        getPaymentData();



    }

    private void getPaymentData() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Payments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Snapshot",""+snapshot.exists());
                if(snapshot.getChildrenCount()>0){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        ModelTransaction modelTransaction=ds.getValue(ModelTransaction.class);
//                        Log.e("SiteName",modelTransaction.getSiteName());
                        transactionArrayList.add(modelTransaction);
                    }
                    AdapterTransaction adapterTransaction=new AdapterTransaction(TransactionListActivity.this,transactionArrayList);
                    binding.rvTransactionList.setAdapter(adapterTransaction);
                    binding.txtNoTransactions.setVisibility(View.GONE);
                }else{
                    binding.txtNoTransactions.setVisibility(View.VISIBLE);
                    binding.rvTransactionList.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}