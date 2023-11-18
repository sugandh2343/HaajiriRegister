package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAddNewSalaryPolicyBinding;

import java.util.HashMap;

public class AddNewSalaryPolicy extends AppCompatActivity {
    ActivityAddNewSalaryPolicyBinding binding;
    long siteId;
    String siteName;
    long policyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddNewSalaryPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");
        Log.e("SPHsiteId",""+siteId);
        binding.llFirst.setVisibility(View.VISIBLE);
        binding.llSecond.setVisibility(View.GONE);
        binding.llThird.setVisibility(View.GONE);
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etPolicyName.getText().toString())){
                    Toast.makeText(AddNewSalaryPolicy.this, "Enter Policy Name to continue", Toast.LENGTH_SHORT).show();
                }else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.VISIBLE);
                    binding.llThird.setVisibility(View.GONE);
                }
            }
        });
        binding.btnNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etDa.getText().toString())||TextUtils.isEmpty(binding.etHra.getText().toString())){
                    Toast.makeText(AddNewSalaryPolicy.this, "DA and HRA are compulsary to fill", Toast.LENGTH_SHORT).show();
                }else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.btnNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                        .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                        .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                        .child("Site").child(String.valueOf(siteId));
                reference.child("Policy")
                        .child("Salary").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    policyId=(snapshot.getChildrenCount()+1)*100;
                                }else{
                                    policyId=100;
                                }
                                HashMap<String,Object>hashMap=new HashMap<>();
                                hashMap.put("policyId",policyId);
                                hashMap.put("policyName",binding.etPolicyName.getText().toString());
                                hashMap.put("da",binding.etDa.getText().toString());
                                hashMap.put("hra",binding.etHra.getText().toString());
                                hashMap.put("cca",binding.etCca.getText().toString());
                                hashMap.put("transport",binding.etTransport.getText().toString());
                                hashMap.put("medical",binding.etMedical.getText().toString());
                                hashMap.put("otherAllowance",binding.etOther.getText().toString());
                                hashMap.put("generalInsurance",binding.etGi.getText().toString());
                                hashMap.put("epf",binding.etEpf.getText().toString());
                                hashMap.put("tds",binding.etTds.getText().toString());
                                hashMap.put("gratuity",binding.etGratuity.getText().toString());
                                reference.child("Policy")
                                        .child("Salary").child(String.valueOf(policyId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddNewSalaryPolicy.this, "Salary Policy Updated Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AddNewSalaryPolicy.this,WorkplaceActivity.class));
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
}