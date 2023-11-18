package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAddNewPolicyBinding;

import org.apache.poi.ss.formula.functions.T;

import java.util.HashMap;

public class AddNewPolicy extends AppCompatActivity {
    ActivityAddNewPolicyBinding binding;
    private String[] term = {"Select term", "Yearly", "Half Yearly","Quaterly","Monthly"};
    private String[] term1 = {"Select term", "Lifetime", "Yearly"};

    long siteId;
    String siteName;

    FirebaseAuth firebaseAuth;

    long policyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddNewPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");

        Log.e("IndustryNAME",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid",""));

        binding.llShortLeave.setVisibility(View.GONE);
        binding.llHalfDay.setVisibility(View.GONE);
        binding.llLate.setVisibility(View.GONE);
        binding.llCarryHalfDay.setVisibility(View.GONE);
        firebaseAuth=FirebaseAuth.getInstance();

        binding.llCarryCl.setVisibility(View.GONE);
        binding.llCl.setVisibility(View.GONE);
        binding.llCarryEl.setVisibility(View.GONE);
        binding.llEl.setVisibility(View.GONE);
        binding.llMl.setVisibility(View.GONE);
        binding.llPl.setVisibility(View.GONE);
        binding.llSl.setVisibility(View.GONE);
        binding.llCarrySl.setVisibility(View.GONE);

        

        binding.llFirst.setVisibility(View.VISIBLE);
        binding.llSecond.setVisibility(View.GONE);
        binding.llThird.setVisibility(View.GONE);
        binding.llFourth.setVisibility(View.GONE);
        binding.llFifth.setVisibility(View.GONE);
        binding.llSixth.setVisibility(View.GONE);
        binding.llSeventh.setVisibility(View.GONE);
        binding.llEighth.setVisibility(View.GONE);
        binding.llNinth.setVisibility(View.GONE);

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etPolicyName.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter Policy Name", Toast.LENGTH_SHORT).show();
                }else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.VISIBLE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                    binding.llFifth.setVisibility(View.GONE);
                    binding.llSixth.setVisibility(View.GONE);
                    binding.llSeventh.setVisibility(View.GONE);
                    binding.llEighth.setVisibility(View.GONE);
                    binding.llNinth.setVisibility(View.GONE);
                    binding.txtLeavePolicyName.setVisibility(View.VISIBLE);
                    binding.txtLeavePolicyName.setText(binding.etPolicyName.getText().toString());
                }
            }
        });
        binding.btnNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbShortLeave.isChecked() && TextUtils.isEmpty(binding.etCountShortLeave.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of short leaves allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbShortLeave.isChecked() &&binding.spinnerTermShortLeave.getSelectedItemPosition()==0){
                    Toast.makeText(AddNewPolicy.this, "Select The term for short leave", Toast.LENGTH_SHORT).show();
                }else if(binding.cbShortLeave.isChecked() && TextUtils.isEmpty(binding.etShortLeave.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter the time duration for short leave", Toast.LENGTH_SHORT).show();
                }else if(binding.cbShortLeave.isChecked() &&Integer.parseInt(binding.etShortLeave.getText().toString())>120){
                    Toast.makeText(AddNewPolicy.this, "Short leave duration cannot be greater than two hours", Toast.LENGTH_SHORT).show();
                }else if(binding.cbShortLeave.isChecked() && Integer.parseInt(binding.etShortLeave.getText().toString())<=0){
                    Toast.makeText(AddNewPolicy.this, "Short leave duration cannot be 0", Toast.LENGTH_SHORT).show();
                }else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.VISIBLE);
                    binding.llFourth.setVisibility(View.GONE);
                    binding.llFifth.setVisibility(View.GONE);
                    binding.llSixth.setVisibility(View.GONE);
                    binding.llSeventh.setVisibility(View.GONE);
                    binding.llEighth.setVisibility(View.GONE);
                    binding.llNinth.setVisibility(View.GONE);
                }
            }
        });
        binding.rbPenaltyLateCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.rbPenaltyLateSalary.isChecked()){
                    binding.rbPenaltyLateSalary.setChecked(false);
                    binding.rbPenaltyLateCl.setChecked(true);
                    binding.rbNoPenalty.setChecked(false);
                }else if(binding.rbPenaltyLateCl.isChecked()){
                    binding.rbPenaltyLateSalary.setChecked(false);
                    binding.rbPenaltyLateCl.setChecked(true);
                    binding.rbNoPenalty.setChecked(false);
                }else{
                    binding.rbPenaltyLateSalary.setChecked(false);
                    binding.rbPenaltyLateCl.setChecked(true);
                    binding.rbNoPenalty.setChecked(true);
                }
            }
        });
        binding.rbPenaltyLateSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.rbPenaltyLateCl.isChecked()){
                    binding.rbPenaltyLateSalary.setChecked(true);
                    binding.rbPenaltyLateCl.setChecked(false);
                    binding.rbNoPenalty.setChecked(false);
                }else if(binding.rbPenaltyLateSalary.isChecked()){
                    binding.rbPenaltyLateSalary.setChecked(true);
                    binding.rbPenaltyLateCl.setChecked(false);
                    binding.rbNoPenalty.setChecked(false);
                }
            }
        });
        binding.rbNoPenalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.rbNoPenalty.isChecked()){
                    binding.rbPenaltyLateSalary.setChecked(false);
                    binding.rbPenaltyLateCl.setChecked(false);
                    binding.rbNoPenalty.setChecked(false);
                }else if(binding.rbPenaltyLateSalary.isChecked()){
                    binding.rbPenaltyLateSalary.setChecked(false);
                    binding.rbPenaltyLateCl.setChecked(false);
                    binding.rbNoPenalty.setChecked(true);
                }else if(binding.rbPenaltyLateSalary.isChecked()){
                    binding.rbPenaltyLateSalary.setChecked(false);
                    binding.rbPenaltyLateCl.setChecked(false);
                    binding.rbNoPenalty.setChecked(true);
                }else{
                    binding.rbPenaltyLateSalary.setChecked(false);
                    binding.rbPenaltyLateCl.setChecked(false);
                    binding.rbNoPenalty.setChecked(true);
                }
            }
        });
        binding.btnNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbLate.isChecked() && TextUtils.isEmpty(binding.etCountLate.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of late arrivals allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbLate.isChecked() &&binding.spinnerTermLate.getSelectedItemPosition()==0){
                    Toast.makeText(AddNewPolicy.this, "Select The term for late arrival", Toast.LENGTH_SHORT).show();
                }else if(binding.cbLate.isChecked() && TextUtils.isEmpty(binding.etLate.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter the time duration for late arrival", Toast.LENGTH_SHORT).show();
                }else if(binding.cbLate.isChecked() &&Integer.parseInt(binding.etLate.getText().toString())>120){
                    Toast.makeText(AddNewPolicy.this, "Late Arrival duration cannot be greater than two hours", Toast.LENGTH_SHORT).show();
                }else if(binding.cbLate.isChecked() && Integer.parseInt(binding.etLate.getText().toString())<=0){
                    Toast.makeText(AddNewPolicy.this, "Late Arrival duration cannot be 0", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.etPenaltyLate.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter no of lates for penalty", Toast.LENGTH_SHORT).show();
                }else if(!binding.rbPenaltyLateCl.isChecked() && !binding.rbPenaltyLateSalary.isChecked()){
                    Toast.makeText(AddNewPolicy.this, "Select penalty for CL or Salary", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.VISIBLE);
                    binding.llFifth.setVisibility(View.GONE);
                    binding.llSixth.setVisibility(View.GONE);
                    binding.llSeventh.setVisibility(View.GONE);
                    binding.llEighth.setVisibility(View.GONE);
                    binding.llNinth.setVisibility(View.GONE);
                }

            }
        });
        SpinnerTermAdapter spinnerTermAdapter=new SpinnerTermAdapter();
        binding.spinnerTermShortLeave.setAdapter(spinnerTermAdapter);
        binding.spinnerTermLate.setAdapter(spinnerTermAdapter);
        binding.spinnerTermHalfDay.setAdapter(spinnerTermAdapter);
        binding.spinnerTermCl.setAdapter(spinnerTermAdapter);

        binding.spinnerTermSl.setAdapter(spinnerTermAdapter);
        SpinnerTermAdapter1 spinnerTermAdapter1=new SpinnerTermAdapter1();
        binding.spinnerTermMl.setAdapter(spinnerTermAdapter1);
        binding.spinnerTermPl.setAdapter(spinnerTermAdapter1);
        binding.cbShortLeave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llShortLeave.setVisibility(View.VISIBLE);
                }else{
                    binding.llShortLeave.setVisibility(View.GONE);
                }
            }
        });
        binding.cbLate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llLate.setVisibility(View.VISIBLE);
                }else{
                    binding.llLate.setVisibility(View.GONE);
                }
            }
        });
        binding.cbHalfDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llHalfDay.setVisibility(View.VISIBLE);
                    binding.llCarryHalfDay.setVisibility(View.VISIBLE);

                    binding.etCarryHalfDay.setVisibility(View.GONE);
                }else{
                    binding.llHalfDay.setVisibility(View.GONE);
                    binding.llCarryHalfDay.setVisibility(View.GONE);

                }
            }
        });


        binding.cbCarryHalfDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.etCarryHalfDay.setVisibility(View.VISIBLE);
                }else{
                    binding.etCarryHalfDay.setVisibility(View.GONE);
                }
            }
        });

        binding.btnNext3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbHalfDay.isChecked() && TextUtils.isEmpty(binding.etCountHalfDay.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of half days allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbLate.isChecked() &&binding.spinnerTermHalfDay.getSelectedItemPosition()==0){
                    Toast.makeText(AddNewPolicy.this, "Select The term for late arrival", Toast.LENGTH_SHORT).show();
                }else if(binding.cbCarryHalfDay.isChecked() && TextUtils.isEmpty(binding.etCarryHalfDay.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter max no of half days that can be taken at once", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                    binding.llFifth.setVisibility(View.VISIBLE);
                    binding.llSixth.setVisibility(View.GONE);
                    binding.llSeventh.setVisibility(View.GONE);
                    binding.llEighth.setVisibility(View.GONE);
                    binding.llNinth.setVisibility(View.GONE);
                }
            }
        });
        binding.btnNext4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbCl.isChecked() && TextUtils.isEmpty(binding.etCountCl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of CLs allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbCl.isChecked() &&binding.spinnerTermCl.getSelectedItemPosition()==0){
                    Toast.makeText(AddNewPolicy.this, "Select The term for CL", Toast.LENGTH_SHORT).show();
                }else if(binding.cbCarryCl.isChecked() && TextUtils.isEmpty(binding.etCarryCl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter max no of CL that can be taken at once", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                    binding.llFifth.setVisibility(View.GONE);
                    binding.llSixth.setVisibility(View.VISIBLE);
                    binding.llSeventh.setVisibility(View.GONE);
                    binding.llEighth.setVisibility(View.GONE);
                    binding.llNinth.setVisibility(View.GONE);
                }

            }
        });
        binding.cbCl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llCl.setVisibility(View.VISIBLE);
                    binding.llCarryCl.setVisibility(View.VISIBLE);
                }else{
                    binding.llCarryCl.setVisibility(View.GONE);
                    binding.llCl.setVisibility(View.GONE);
                }
            }
        });

        binding.cbCarryCl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.etCarryCl.setVisibility(View.VISIBLE);
                }else{
                    binding.etCarryCl.setVisibility(View.GONE);
                }
            }
        });

        binding.cbMl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llMl.setVisibility(View.VISIBLE);

                }else{

                    binding.llMl.setVisibility(View.GONE);
                }
            }
        });
        binding.cbPl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llPl.setVisibility(View.VISIBLE);

                }else{

                    binding.llPl.setVisibility(View.GONE);
                }
            }
        });

        //ghiugiyiyg

        binding.cbEl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llEl.setVisibility(View.VISIBLE);
                    binding.llCarryEl.setVisibility(View.VISIBLE);
                }else{
                    binding.llCarryEl.setVisibility(View.GONE);
                    binding.llEl.setVisibility(View.GONE);
                }
            }
        });
        binding.cbSl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llSl.setVisibility(View.VISIBLE);
                    binding.llCarrySl.setVisibility(View.VISIBLE);
                }else{
                    binding.llCarrySl.setVisibility(View.GONE);
                    binding.llSl.setVisibility(View.GONE);
                }
            }
        });

        binding.cbCarryEl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.etCarryEl.setVisibility(View.VISIBLE);
                }else{
                    binding.etCarryEl.setVisibility(View.GONE);
                }
            }
        });

        binding.btnNext5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbEl.isChecked() && TextUtils.isEmpty(binding.etCountEl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of ELs allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbEl.isChecked() && TextUtils.isEmpty(binding.spinnerTermEl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter the working days after which a person is eligible for EL", Toast.LENGTH_SHORT).show();
                }else if(binding.cbCarryEl.isChecked() && TextUtils.isEmpty(binding.etCarryEl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter max no of ELs that can be taken at once", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                    binding.llFifth.setVisibility(View.GONE);
                    binding.llSixth.setVisibility(View.GONE);
                    binding.llSeventh.setVisibility(View.VISIBLE);
                    binding.llEighth.setVisibility(View.GONE);
                    binding.llNinth.setVisibility(View.GONE);
                }

            }
        });

        binding.btnNext6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbMl.isChecked() && TextUtils.isEmpty(binding.etCountMl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of Maternity leaves allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbMl.isChecked() &&binding.spinnerTermMl.getSelectedItemPosition()==0){
                    Toast.makeText(AddNewPolicy.this, "Select The term for Maternity Leave", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                    binding.llFifth.setVisibility(View.GONE);
                    binding.llSixth.setVisibility(View.GONE);
                    binding.llSeventh.setVisibility(View.GONE);
                    binding.llEighth.setVisibility(View.VISIBLE);
                    binding.llNinth.setVisibility(View.GONE);
                }

            }
        });
        binding.btnNext7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbPl.isChecked() && TextUtils.isEmpty(binding.etCountPl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of Paternity leaves allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbPl.isChecked() &&binding.spinnerTermPl.getSelectedItemPosition()==0){
                    Toast.makeText(AddNewPolicy.this, "Select The term for Paternity Leave", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.llFirst.setVisibility(View.GONE);
                    binding.llSecond.setVisibility(View.GONE);
                    binding.llThird.setVisibility(View.GONE);
                    binding.llFourth.setVisibility(View.GONE);
                    binding.llFifth.setVisibility(View.GONE);
                    binding.llSixth.setVisibility(View.GONE);
                    binding.llSeventh.setVisibility(View.GONE);
                    binding.llEighth.setVisibility(View.GONE);
                    binding.llNinth.setVisibility(View.VISIBLE);
                }

            }
        });

        binding.btnNext8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbSl.isChecked() && TextUtils.isEmpty(binding.etCountSl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter number of sick leaves allowed", Toast.LENGTH_SHORT).show();
                }else if(binding.cbSl.isChecked() &&binding.spinnerTermSl.getSelectedItemPosition()==0){
                    Toast.makeText(AddNewPolicy.this, "Select The term for sick leave", Toast.LENGTH_SHORT).show();
                }else if(binding.cbCarrySl.isChecked() && TextUtils.isEmpty(binding.etCarrySl.getText().toString())){
                    Toast.makeText(AddNewPolicy.this, "Enter max no of sick leave that can be taken at once", Toast.LENGTH_SHORT).show();
                }
                else{


                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                            .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                                    .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                                            .child("Site").child(String.valueOf(siteId));
                    reference.child("Policy")
                            .child("Leave").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        policyId=(snapshot.getChildrenCount()+1)*100;
                                    }else{
                                        policyId=100;
                                    }
                                    HashMap<String,Object> hashMap=new HashMap<>();
                                    hashMap.put("policyId",policyId);
                                    hashMap.put("policyName",binding.etPolicyName.getText().toString());
                                    hashMap.put("shortLeave",binding.cbShortLeave.isChecked());
                                    hashMap.put("late",binding.cbLate.isChecked());
                                    hashMap.put("halfday",binding.cbHalfDay.isChecked());
                                    hashMap.put("cl",binding.cbCl.isChecked());
                                    hashMap.put("el",binding.cbEl.isChecked());
                                    hashMap.put("ml",binding.cbMl.isChecked());
                                    hashMap.put("pl",binding.cbPl.isChecked());
                                    hashMap.put("sl",binding.cbSl.isChecked());
                                    hashMap.put("latePenalty",binding.etPenaltyLate.getText().toString());
                                    hashMap.put("latePenaltyCl",binding.rbPenaltyLateCl.isChecked());
                                    hashMap.put("latePenaltySalary",binding.rbPenaltyLateSalary.isChecked());
                                    if(binding.cbShortLeave.isChecked()){
                                        hashMap.put("shortLeaveTerm",term[binding.spinnerTermShortLeave.getSelectedItemPosition()]);
                                        hashMap.put("shortLeaveCount",binding.etCountShortLeave.getText().toString());
                                        hashMap.put("shortLeaveDuration",binding.etShortLeave.getText().toString());
                                    }
                                    if(binding.cbLate.isChecked()){
                                        hashMap.put("lateTerm",term[binding.spinnerTermLate.getSelectedItemPosition()]);
                                        hashMap.put("lateCount",binding.etCountLate.getText().toString());
                                        hashMap.put("lateDuration",binding.etLate.getText().toString());
                                    }
                                    if(binding.cbHalfDay.isChecked()){
                                        hashMap.put("halfdayTerm",term[binding.spinnerTermHalfDay.getSelectedItemPosition()]);
                                        hashMap.put("halfdayCount",binding.etCountHalfDay.getText().toString());
                                        hashMap.put("halfdaycarry",binding.cbCarryHalfDay.isChecked());
                                        if(binding.cbCarryHalfDay.isChecked()){
                                            hashMap.put("maxhalfday",binding.etCarryHalfDay.getText().toString());

                                        }

                                    }
                                    if(binding.cbCl.isChecked()){
                                        hashMap.put("clTerm",term[binding.spinnerTermCl.getSelectedItemPosition()]);
                                        hashMap.put("clCount",binding.etCountCl.getText().toString());
                                        hashMap.put("clcarry",binding.cbCarryCl.isChecked());
                                        if(binding.cbCarryCl.isChecked()){
                                            hashMap.put("maxCl",binding.etCarryCl.getText().toString());

                                        }

                                    }
                                    if(binding.cbEl.isChecked()){
                                        hashMap.put("elTerm",binding.spinnerTermEl.getText().toString());
                                        hashMap.put("elCount",binding.etCountEl.getText().toString());
                                        hashMap.put("elcarry",binding.cbCarryEl.isChecked());
                                        if(binding.cbCarryEl.isChecked()){
                                            hashMap.put("maxEL",binding.etCarryEl.getText().toString());

                                        }

                                    }

                                    if(binding.cbMl.isChecked()){
                                        hashMap.put("mlTerm",term1[binding.spinnerTermMl.getSelectedItemPosition()]);
                                        hashMap.put("mlCount",binding.etCountMl.getText().toString());
                                    }
                                    if(binding.cbPl.isChecked()){
                                        hashMap.put("plTerm",term1[binding.spinnerTermPl.getSelectedItemPosition()]);
                                        hashMap.put("plCount",binding.etCountPl.getText().toString());
                                    }

                                    if(binding.cbSl.isChecked()){
                                        hashMap.put("slTerm",term[binding.spinnerTermSl.getSelectedItemPosition()]);
                                        hashMap.put("slCount",binding.etCountSl.getText().toString());
                                        hashMap.put("slcarry",binding.cbCarrySl.isChecked());
                                        if(binding.cbCarrySl.isChecked()){
                                            hashMap.put("maxSL",binding.etCarrySl.getText().toString());

                                        }

                                    }

                                    reference.child("Policy")
                                            .child("Leave").child(String.valueOf(policyId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                               reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                       long policycount=snapshot.child("policyCount").getValue(long.class)+1;
                                                       HashMap<String,Object> hashMap1=new HashMap<>();
                                                       hashMap1.put("policy",true);
                                                       hashMap1.put("policyCount",policycount);
                                                       reference.updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                           @Override
                                                           public void onSuccess(Void unused) {
                                                               Toast.makeText(AddNewPolicy.this, "Policy Added Sucessfully", Toast.LENGTH_SHORT).show();
//                                                               binding.llFirst.setVisibility(View.VISIBLE);
//                                                               binding.llSecond.setVisibility(View.GONE);
//                                                               binding.llThird.setVisibility(View.GONE);
//                                                               binding.llFourth.setVisibility(View.GONE);
//                                                               binding.llFifth.setVisibility(View.GONE);
//                                                               binding.llSixth.setVisibility(View.GONE);
//                                                               binding.llSeventh.setVisibility(View.GONE);
//                                                               binding.llEighth.setVisibility(View.GONE);
//                                                               binding.llNinth.setVisibility(View.GONE);
//                                                               binding.etPolicyName.setText("");
                                                               Intent intent1=new Intent(AddNewPolicy.this,WorkplaceActivity.class);
                                                               intent1.putExtra("siteId",siteId);
                                                               intent1.putExtra("siteName",siteName);
                                                               startActivity(intent1);

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

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            }
        });

    }
    class SpinnerTermAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return term.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inf = getLayoutInflater();
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(term[position]);

            return row;
        }
    }
    class SpinnerTermAdapter1 extends BaseAdapter {

        @Override
        public int getCount() {
            return term1.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inf = getLayoutInflater();
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(term1[position]);

            return row;
        }
    }

    @Override
    public void onBackPressed() {
        if(binding.llFirst.getVisibility()==View.VISIBLE){
           startActivity(new Intent(AddNewPolicy.this,PolicyHome.class));
        }else if(binding.llSecond.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.VISIBLE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
            binding.llFifth.setVisibility(View.GONE);
            binding.llSixth.setVisibility(View.GONE);
            binding.llSeventh.setVisibility(View.GONE);
            binding.llEighth.setVisibility(View.GONE);
            binding.llNinth.setVisibility(View.GONE);
        }else if(binding.llThird.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.VISIBLE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
            binding.llFifth.setVisibility(View.GONE);
            binding.llSixth.setVisibility(View.GONE);
            binding.llSeventh.setVisibility(View.GONE);
            binding.llEighth.setVisibility(View.GONE);
            binding.llNinth.setVisibility(View.GONE);
        }else if(binding.llFourth.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.VISIBLE);
            binding.llFourth.setVisibility(View.GONE);
            binding.llFifth.setVisibility(View.GONE);
            binding.llSixth.setVisibility(View.GONE);
            binding.llSeventh.setVisibility(View.GONE);
            binding.llEighth.setVisibility(View.GONE);
            binding.llNinth.setVisibility(View.GONE);
        }else if(binding.llFifth.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.VISIBLE);
            binding.llFifth.setVisibility(View.GONE);
            binding.llSixth.setVisibility(View.GONE);
            binding.llSeventh.setVisibility(View.GONE);
            binding.llEighth.setVisibility(View.GONE);
            binding.llNinth.setVisibility(View.GONE);
        }else if(binding.llSixth.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
            binding.llFifth.setVisibility(View.VISIBLE);
            binding.llSixth.setVisibility(View.GONE);
            binding.llSeventh.setVisibility(View.GONE);
            binding.llEighth.setVisibility(View.GONE);
            binding.llNinth.setVisibility(View.GONE);
        }else if(binding.llSeventh.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
            binding.llFifth.setVisibility(View.GONE);
            binding.llSixth.setVisibility(View.VISIBLE);
            binding.llSeventh.setVisibility(View.GONE);
            binding.llEighth.setVisibility(View.GONE);
            binding.llNinth.setVisibility(View.GONE);
        }else if(binding.llEighth.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
            binding.llFifth.setVisibility(View.GONE);
            binding.llSixth.setVisibility(View.GONE);
            binding.llSeventh.setVisibility(View.VISIBLE);
            binding.llEighth.setVisibility(View.GONE);
            binding.llNinth.setVisibility(View.GONE);
        }else if(binding.llNinth.getVisibility()==View.VISIBLE){
            binding.llFirst.setVisibility(View.GONE);
            binding.llSecond.setVisibility(View.GONE);
            binding.llThird.setVisibility(View.GONE);
            binding.llFourth.setVisibility(View.GONE);
            binding.llFifth.setVisibility(View.GONE);
            binding.llSixth.setVisibility(View.GONE);
            binding.llSeventh.setVisibility(View.GONE);
            binding.llEighth.setVisibility(View.VISIBLE);
            binding.llNinth.setVisibility(View.GONE);
        }
    }
}