package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterSearchLabourPayment;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivitySearchForLabourPaymentBinding;

import java.util.ArrayList;
import java.util.Locale;

public class SearchForLabourPayment extends AppCompatActivity {
    ActivitySearchForLabourPaymentBinding binding;
    String workerType,searchValue="";
    private String[] designation={"Select type","Skilled","Unskilled"};
    private ArrayList<ModelLabour> labourArrayList;
    private ArrayList<ModelLabour> labourArrayListFilter;
    private long siteId;
    private String userType;
    private String siteName;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchForLabourPaymentBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        Intent intent=getIntent();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        userType=sharedpreferences.getString("userDesignation","");

        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        if(userType=="Supervisor"){
            binding.typeSpinner.setVisibility(View.GONE);
            siteId=sharedpreferences.getLong("siteId",0);
        }else{
            siteId=intent.getLongExtra("siteId",0);
            siteName=intent.getStringExtra("siteName");
            Log.e("siteId","SLabour:::"+siteId);
        }

        searchValue=intent.getStringExtra("name");
        workerType=intent.getStringExtra("WorkerType");
        binding.etSearch.setText(searchValue);
        binding.etSearch.setTextColor(getResources().getColor(R.color.white));
        labourArrayList=new ArrayList<>();
        labourArrayListFilter=new ArrayList<>();
        Log.e("siteIdSearch",""+siteId);
        if(siteId!=0){
            if(searchValue==null) {
                getLabourList();
            }else{
                getLabourListFilter(searchValue);
            }
        }
        binding.backToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SearchForLabourPayment.this,PaymentActivity.class);

                intent.putExtra("WorkerType",workerType);
                intent.putExtra("siteId",siteId);
                intent.putExtra("siteName",siteName);
                startActivity(intent);
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                if(charSequence.toString().contains(" ")){
                    getLabourListFilter(charSequence.toString().trim().toLowerCase(Locale.ROOT));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

    }
    private void getLabourListFilter(String searchValue) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours").orderByChild("type").equalTo(workerType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
                    if((modelLabour.getName().toLowerCase(Locale.ROOT).contains(searchValue)||
                            modelLabour.getLabourId().contains(searchValue)||
                            modelLabour.getUniqueId().contains(searchValue))&& modelLabour.getStatus().equals("Registered")) {
                        labourArrayListFilter.add(modelLabour);
                    }

                }
                Log.e("LabourListSize1",""+labourArrayList.size());
                if(labourArrayListFilter.size()==0){
                    binding.rvLabourList.setVisibility(View.GONE);
                    binding.txtNoLabours.setVisibility(View.VISIBLE);
                }else {
                    binding.rvLabourList.setVisibility(View.VISIBLE);
                    binding.txtNoLabours.setVisibility(View.GONE);
                    AdapterSearchLabourPayment adapterLabourSkilled = new AdapterSearchLabourPayment(SearchForLabourPayment.this, labourArrayListFilter);
                    binding.rvLabourList.setAdapter(adapterLabourSkilled);
                }
//                getLabourskilledList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getLabourList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours").orderByChild("type").equalTo(workerType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
                    if(modelLabour.getStatus().equals("Registered")){
                        labourArrayList.add(modelLabour);
                    }


                }
                Log.e("LabourListSize1",""+labourArrayList.size());
                if(labourArrayList.size()==0){
                    binding.rvLabourList.setVisibility(View.GONE);
                    binding.txtNoLabours.setVisibility(View.VISIBLE);
                }else {
                    binding.rvLabourList.setVisibility(View.VISIBLE);
                    binding.txtNoLabours.setVisibility(View.GONE);
                    AdapterSearchLabourPayment adapterLabourSkilled = new AdapterSearchLabourPayment(SearchForLabourPayment.this, labourArrayList);
                    binding.rvLabourList.setAdapter(adapterLabourSkilled);
                }
//                getLabourskilledList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}