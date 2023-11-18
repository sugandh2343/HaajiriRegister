package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterUserIndustry;
import com.skillzoomer_Attendance.com.Model.ModelCategory;
import com.skillzoomer_Attendance.com.Model.ModelDesignation;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.Model.ModelUserIndustry;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityProfile extends AppCompatActivity {
    ActivityProfileBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelUserIndustry> userIndustryArrayList;
    private ArrayList<ModelCategory> categoryArrayList;
    private ArrayList<ModelDesignation> designationArrayList;
    long countIndustry;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseAuth=FirebaseAuth.getInstance();
        userIndustryArrayList=new ArrayList<>();
        categoryArrayList=new ArrayList<>();
        designationArrayList=new ArrayList<>();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getUserDetails();
        binding.addAnotherIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(ActivityProfile.this);
                View mView = LayoutInflater.from(ActivityProfile.this).inflate(R.layout.layout_dialog_other_industry, null);
                Spinner spinner_select_industry,spinner_select_designation;
                EditText et_company_name,et_address_name,et_custom_designation,et_email_name;
                Button btn_submit;
                spinner_select_industry=mView.findViewById(R.id.spinner_select_industry);
                spinner_select_designation=mView.findViewById(R.id.spinner_select_designation);
                et_company_name=mView.findViewById(R.id.et_company_name);
                et_address_name=mView.findViewById(R.id.et_address_name);
                btn_submit=mView.findViewById(R.id.btn_submit);
                et_custom_designation=mView.findViewById(R.id.et_custom_designation);
                et_email_name=mView.findViewById(R.id.et_email_name);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(true);
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("CategoryMaster");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoryArrayList.clear();

                        for(DataSnapshot ds:snapshot.getChildren()){
                            int count=0;
                            ModelCategory modelCategory=ds.getValue(ModelCategory.class);
                            if(modelCategory.getId()>0){
                                for(int i=0;i<userIndustryArrayList.size();i++){
                                    if(userIndustryArrayList.get(i).getIndustryName().equals(modelCategory.getName())){
                                        count++;
                                    }

                                }
                                if(count==0){
                                    categoryArrayList.add(modelCategory);
                                }
                            }


                        }
                        SpinnerCategoryAdapter spinnerCategoryAdapter=new SpinnerCategoryAdapter();
                        spinner_select_industry.setAdapter(spinnerCategoryAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("DesignationMaster");
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        designationArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelDesignation modelCategory=ds.getValue(ModelDesignation.class);
                            designationArrayList.add(modelCategory);
                        }
                        SpinnerDesignationAdapter spinnerDesignationAdapter=new SpinnerDesignationAdapter();
                        spinner_select_designation.setAdapter(spinnerDesignationAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                spinner_select_designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i>0){
                            if(designationArrayList.get(i).getName().equals("Custom")){
                                et_custom_designation.setVisibility(View.VISIBLE);
                            }else{
                                et_custom_designation.setVisibility(View.GONE);
                            }
                        }else{
                            et_custom_designation.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(et_company_name.getText().toString())) {
                            Toast.makeText(ActivityProfile.this, getString(R.string.enter_company_name), Toast.LENGTH_SHORT).show();
                        }  else if (TextUtils.isEmpty(et_address_name.getText().toString())) {
                            Toast.makeText(ActivityProfile.this, getString(R.string.enter_company_address), Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(et_email_name.getText().toString())) {
                            Toast.makeText(ActivityProfile.this, getString(R.string.enter_company_email), Toast.LENGTH_SHORT).show();

                        }else{
                            progressDialog.show();

                            DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Users");
                            reference2.child(firebaseAuth.getUid()).child("Industry").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        countIndustry= snapshot.getChildrenCount()+1;
                                        HashMap<String,Object> hashMap1=new HashMap<>();
                                        hashMap1.put("industryPosition",categoryArrayList.get(spinner_select_industry.getSelectedItemPosition()).getId());
                                        hashMap1.put("industryName",categoryArrayList.get(spinner_select_industry.getSelectedItemPosition()).getName());
                                        hashMap1.put("industryNameHindi",categoryArrayList.get(spinner_select_industry.getSelectedItemPosition()).getHindiName());
                                        hashMap1.put("companyName",et_company_name.getText().toString());
                                        hashMap1.put("companyEmail",et_email_name.getText().toString());
                                        hashMap1.put("designationPosition",spinner_select_designation.getSelectedItemPosition());
                                        if(designationArrayList.get(spinner_select_designation.getSelectedItemPosition()).getName().equals("Custom")){
                                            hashMap1.put("designationName",et_custom_designation.getText().toString());
                                            hashMap1.put("designationNameHindi",et_custom_designation.getText().toString());
                                        }else{
                                            hashMap1.put("designationName",designationArrayList.get(spinner_select_designation.getSelectedItemPosition()).getName());
                                            hashMap1.put("designationNameHindi",designationArrayList.get(spinner_select_designation.getSelectedItemPosition()).getNameHindi());
                                        }

                                        DatabaseReference reference3=FirebaseDatabase.getInstance().getReference("Users");
                                        reference3.child(String.valueOf(firebaseAuth.getUid())).child("Industry")
                                                .child(categoryArrayList.get(spinner_select_industry.getSelectedItemPosition()).getName())

                                                .setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        reference3.child(firebaseAuth.getUid()).child("Industry").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                countIndustry=snapshot.getChildrenCount();
                                                                HashMap<String,Object> hashMap=new HashMap<>();
                                                                Log.e("InsustryCount",""+countIndustry);
                                                                hashMap.put("industryCount",countIndustry);

                                                                reference3.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(ActivityProfile.this, "Industry Added to your Profile", Toast.LENGTH_SHORT).show();
                                                                        alertDialog.dismiss();
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                });








                alertDialog.show();
            }
        });

    }
    class SpinnerCategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return categoryArrayList.size();
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
            LayoutInflater inf = getLayoutInflater();
            View row = inf.inflate(R.layout.layout_category_single_row, null);
            TextView txt_category_name;
            ImageView img_category;
            txt_category_name=row.findViewById(R.id.txt_category_name);
            img_category=row.findViewById(R.id.img_category);

            if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Language","hi").equals("en")) {
                txt_category_name.setText(categoryArrayList.get(position).getName());
            }else{
                txt_category_name.setText(categoryArrayList.get(position).getHindiName());
            }
            Picasso.get().load(categoryArrayList.get(position).getImage()).
                    resize(400,400).centerCrop()
                    .placeholder(R.drawable.ic_add).into(img_category);




            return row;
        }
    }
    class SpinnerDesignationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return designationArrayList.size();
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
            LayoutInflater inf = getLayoutInflater();
            View row = inf.inflate(R.layout.layout_designation_spinner, null);
            TextView txt_category_name;
            ImageView img_category;
            txt_category_name=row.findViewById(R.id.txt_category_name);
            img_category=row.findViewById(R.id.img_category);

            if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Language","hi").equals("en")){
                txt_category_name.setText(designationArrayList.get(position).getName());
            }else{
                txt_category_name.setText(designationArrayList.get(position).getNameHindi());
            }

            Picasso.get().load(designationArrayList.get(position).getImage()).
                    resize(400,400).centerCrop()
                    .placeholder(R.drawable.ic_add).into(img_category);




            return row;
        }
    }

    private void getUserDetails() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                Log.e("Snapshot",""+firebaseAuth.getUid());
//                    ModelUser modelUser=ds.getValue(ModelUser.class);
                Log.e("Name",snapshot.child("name").getValue(String.class));
                binding.etName.setText(snapshot.child("name").getValue(String.class));
                binding.etFullName.setText(snapshot.child("companyName").getValue(String.class));
                binding.etMobile.setText(snapshot.child("mobile").getValue(String.class));
                reference.child(firebaseAuth.getUid()).child("Industry").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userIndustryArrayList.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot ds: snapshot.getChildren()){
                                ModelUserIndustry modelUserIndustry=ds.getValue(ModelUserIndustry.class);
                                userIndustryArrayList.add(modelUserIndustry);
                            }
                            AdapterUserIndustry adapterUserIndustry=new AdapterUserIndustry(ActivityProfile.this,userIndustryArrayList);
                            binding.rvIndustry.setAdapter(adapterUserIndustry);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}