package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAdminDashboardBinding;

public class AdminDashboard extends AppCompatActivity {
    ActivityAdminDashboardBinding binding;
    long siteCount=0;
    long labourCount=0;
    long totalAttendance=0;
    long totalPayment=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteCount=0;
                labourCount=0;
                totalAttendance=0;
              binding.txtUserCount.setText(""+snapshot.getChildrenCount());
              for (DataSnapshot ds:snapshot.getChildren()){
                  if(ds.child("Industry").child("Construction").child("Site").exists()){
                      siteCount=siteCount+ds.child("Industry").child("Construction").child("Site").getChildrenCount();
                      for(DataSnapshot ds1:ds.child("Industry").child("Construction").child("Site").getChildren()){
                          if(ds1.child("Labours").exists()){
                              labourCount=labourCount+ds1.child("Labours").getChildrenCount();
                          }
                          if(ds1.child("Attendance").child("Labours").exists()){
                              for(DataSnapshot ds2:ds1.child("Attendance").child("Labours").getChildren()){
                                  Log.e("TotalAttendance",""+ds2.getChildrenCount());
                                  Log.e("TotalAttendance",""+ds2.getKey());
                                  totalAttendance= totalAttendance+ds2.getChildrenCount();
                              }
                          }
                      }
                  }
              }
              binding.txtTotalSite.setText(String.valueOf(siteCount));
              binding.txtTotalLabour.setText(String.valueOf(labourCount));
              binding.txtTotalAttendance.setText(String.valueOf(totalAttendance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
        reference1.orderByChild("userType").equalTo("Supervisor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.txtTotalAssociates2.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Users");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalPayment=0;
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.child("Payments").exists()){
                        for(DataSnapshot ds1:ds.child("Payments").getChildren()){
                            if(ds1.child("status").equals("Success")){
                                Log.e("Ds1",ds1.getKey());
                                totalPayment=totalPayment+(long)Integer.parseInt(ds1.child("paidAmount").getValue(String.class));
                            }
                        }
                    }
                }

                binding.txtPaymentSum.setText(String.valueOf(totalPayment));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.cardUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboard.this,GetUserDetails.class));
            }
        });
    }

}