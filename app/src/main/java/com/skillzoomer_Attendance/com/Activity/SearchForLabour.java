package com.skillzoomer_Attendance.com.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import com.skillzoomer_Attendance.com.Adapter.AdapterSearchLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivitySearchForLabourBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

public class SearchForLabour extends AppCompatActivity {
    ActivitySearchForLabourBinding binding;
    LayoutToolbarBinding toolbarBinding;
    String workerType,searchValue="";
    private String[] designation={"Select type","Skilled","Unskilled"};
    private ArrayList<ModelLabour> labourArrayList;
    private ArrayList<ModelLabour> labourArrayListFilter;
    private long siteId;
    private String userType;

    String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchForLabourBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Log.e("called","create");

        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        userType=sharedpreferences.getString("userDesignation","");
        Intent intent=getIntent();
        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        if(userType=="Supervisor"){
            binding.typeSpinner.setVisibility(View.GONE);
            siteId=sharedpreferences.getLong("siteId",0);
        }else{
            binding.typeSpinner.setVisibility(View.GONE);
            SpinnerAdapter spinnerAdapter=new SpinnerAdapter();
            binding.typeSpinner.setAdapter(spinnerAdapter);
            binding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    workerType=designation[position];

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            siteId=intent.getLongExtra("siteId",0);
            Log.e("siteId","SLabour:::"+siteId);

        }



        workerType=intent.getStringExtra("type");
        Log.e("workerReceived",workerType);

        searchValue=intent.getStringExtra("name");
        binding.etSearch.setText(searchValue);
        binding.etSearch.setTextColor(getResources().getColor(R.color.white));

        if(workerType!=null){
            if(workerType.equals("Skilled")){
                binding.typeSpinner.setSelection(1);
            }else{
                binding.typeSpinner.setSelection(2);
            }
        }

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
        labourArrayListFilter.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours").orderByChild("type").equalTo(workerType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
                    if((modelLabour.getName().toLowerCase(Locale.ROOT).contains(searchValue)||
                            modelLabour.getLabourId().contains(searchValue)||
                            modelLabour.getUniqueId().contains(searchValue))&&modelLabour.getStatus().equals("Registered")) {
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
                    AdapterSearchLabour adapterLabourSkilled = new AdapterSearchLabour(SearchForLabour.this, labourArrayListFilter);
                    binding.rvLabourList.setAdapter(adapterLabourSkilled);
                }
//                getLabourskilledList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return designation.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inf=getLayoutInflater();
            View row=inf.inflate(R.layout.spinner_child,null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(designation[position]);

            return row;
        }
    }
    private void getLabourList() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours").orderByChild("type").equalTo(workerType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labourArrayList.clear();
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
                    AdapterSearchLabour adapterLabourSkilled = new AdapterSearchLabour(SearchForLabour.this, labourArrayList);
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
        Intent intent=new Intent(SearchForLabour.this, MainActivity.class);
        intent.putExtra("type",workerType);
        intent.putExtra("siteId",siteId);
        Log.e("Back",""+siteId);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("called","resume");
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        siteId = sharedpreferences.getLong("siteId", 0);
        userType = sharedpreferences.getString("userDesignation", "");
        Intent intent = getIntent();
        if (userType == "Supervisor") {
            binding.typeSpinner.setVisibility(View.GONE);
        } else {
            binding.typeSpinner.setVisibility(View.GONE);
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
            binding.typeSpinner.setAdapter(spinnerAdapter);
            binding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    workerType = designation[position];

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            siteId = intent.getLongExtra("siteId", 0);
            Log.e("siteId", "SLabour:::" + siteId);
        }


        workerType = intent.getStringExtra("type");
        Log.e("workerReceived", workerType);

        searchValue = intent.getStringExtra("name");
        binding.etSearch.setText(searchValue);
        binding.etSearch.setTextColor(getResources().getColor(R.color.white));

        if (workerType != null) {
            if (workerType.equals("Skilled")) {
                binding.typeSpinner.setSelection(1);
            } else {
                binding.typeSpinner.setSelection(2);
            }
        }

//        labourArrayList = new ArrayList<>();
//        labourArrayListFilter = new ArrayList<>();
        Log.e("siteIdSearch", "" + siteId);
        if (siteId != 0) {
            if (searchValue == null) {
                getLabourList();
            } else {
                getLabourListFilter(searchValue);
            }
        }
    }
}