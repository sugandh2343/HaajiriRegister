package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterEmployee;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.Model.ModelWorkPlace;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEmpMasterDataBinding;

import java.util.ArrayList;

public class EmpMasterData extends AppCompatActivity {
    ActivityEmpMasterDataBinding binding;
    private ArrayList<ModelEmployee> employeeArrayList;
    long siteId;
    String siteName;
    private ArrayList<ModelWorkPlace> siteArrayList;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmpMasterDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        employeeArrayList=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        siteId=intent.getLongExtra("siteId",0);
        siteName=intent.getStringExtra("siteName");
        siteArrayList=new ArrayList<>();
        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("HR Manager")){
            binding.llMain.setVisibility(View.GONE);
            binding.spinnerSite.setVisibility(View.VISIBLE);
            getSiteList();
        }else{
            binding.llMain.setVisibility(View.VISIBLE);
            binding.spinnerSite.setVisibility(View.GONE);
            getEmpList();
        }
        binding.spinnerSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view , int position , long id) {
                if(position>0){
                    binding.llMain.setVisibility(View.VISIBLE);
                    siteId=siteArrayList.get(position).getSiteId();
                    getEmpList();

                }else{
                    binding.llMain.setVisibility(View.GONE);
                    binding.spinnerSite.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        binding.etSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void getEmpList() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId));
        reference.child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelEmployee modelEmployee=ds.getValue(ModelEmployee.class);
                    if(modelEmployee.getUid()!=null && !modelEmployee.getUid().equals(firebaseAuth.getUid())){
                        employeeArrayList.add(modelEmployee);
                    }
                }
                if(employeeArrayList.size()>0){
                    binding.txtNoLabours.setVisibility(View.GONE);
                    AdapterEmployee adapterEmployee=new AdapterEmployee(EmpMasterData.this,employeeArrayList);
                    binding.rvEmpList.setAdapter(adapterEmployee);
                }else{
                    binding.etSearchEmployee.setVisibility(View.GONE);
                    binding.rvEmpList.setVisibility(View.GONE);
                    binding.txtNoLabours.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteList() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid())
                .child("Industry").child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName","")).child("Site");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Log.e("SiteArrayList12","IP"+ds.child("industryPosition").getValue(long.class));
                    Log.e("SiteArrayList12",""+getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));

                    if(ds.child("industryPosition").getValue(long.class)!=null &&
                            ds.child("industryPosition").getValue(long.class)==getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0)){
                        ModelWorkPlace modelSite=ds.getValue(ModelWorkPlace.class);
                        siteArrayList.add(modelSite);
                    }
                    siteArrayList.add(0,new ModelWorkPlace("Select Workplace"));
                    SiteSpinnerAdapter siteSpinnerAdapter=new SiteSpinnerAdapter();
                    binding.spinnerSite.setAdapter(siteSpinnerAdapter);

                }

                Log.e("SiteArrayList",""+siteArrayList.size());
                Log.e("SiteArrayList","ISP"+getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class SiteSpinnerAdapter
            extends BaseAdapter {
        SiteSpinnerAdapter() {
        }

        public int getCount() {
            return siteArrayList.size();
        }

        public Object getItem(int n) {
            return null;
        }

        public long getItemId(int n) {
            return 0L;
        }

        public View getView(int n, View view, ViewGroup viewGroup) {
            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
            TextView textView = (TextView) view2.findViewById(R.id.spinner_text);
            textView.setText(siteArrayList.get(n).getSiteCity());
            return view2;
        }
    }
}