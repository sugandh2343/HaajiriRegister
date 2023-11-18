package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Adapter.SliderAdapter;
import com.skillzoomer_Attendance.com.Model.SliderData;
import com.skillzoomer_Attendance.com.databinding.ActivityAdminLoginOptionsBinding;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AdminLoginOptions extends AppCompatActivity {
    ActivityAdminLoginOptionsBinding binding;
    String userName,userDesignation,userMobile,companyName,password;
    SharedPreferences sharedpreferences = null;
    private SharedPreferences.Editor editorLogin;
    private SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    private int selectedOption=1;
    String message="";
    private ProgressDialog progressDialog;
    SharedPreferences spLogin;
    private ArrayList<SliderData> sliderDataArrayList;
    private int sharedSelected;
    private SharedPreferences.Editor editorPL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminLoginOptionsBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        editor=sharedpreferences.edit();
        spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editorLogin = spLogin.edit();
        sliderDataArrayList=new ArrayList<>();
        sharedSelected=sharedpreferences.getInt("workOption",0);
        SharedPreferences sharedpreferencesPL = getSharedPreferences("PermanentLogin", Context.MODE_PRIVATE);
        editorPL=sharedpreferencesPL.edit();
        MyApplication my = new MyApplication( );
        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));
        Log.e("IndustryCount",""+sharedpreferences.getLong("industryCount",0));

        Intent intent= getIntent();
        if(sharedSelected==0){
            userName=intent.getStringExtra("userName");
            userDesignation=intent.getStringExtra("userDesignation");
            userMobile=intent.getStringExtra("userMobile");
            companyName=intent.getStringExtra("companyName");
            password=intent.getStringExtra("password");
        }else if(sharedSelected>0){
            selectedOption=sharedSelected;
            if(selectedOption==1){
                binding.rbOpt1.setChecked(true);

                message=getString(R.string.message_option1);
            }else if(selectedOption==2){
                binding.rbOpt2.setChecked(true);
                message=getString(R.string.message_option2);

            }else if(selectedOption==3){
//                binding.rbOpt3.setChecked(true);
                message=getString(R.string.message_option3);
            }
        }

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);


        binding.rbOpt1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(binding.rbOpt1.isChecked()){
                    binding.rbOpt1.setChecked(true);
                    binding.rbOpt2.setChecked(false);
//                    binding.rbOpt3.setChecked(false);
                    selectedOption=1;
                    message=getString(R.string.message_option1);

                }
            }
        });
        binding.rbOpt2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(binding.rbOpt2.isChecked()){
                    binding.rbOpt2.setChecked(true);
                    binding.rbOpt1.setChecked(false);

                    selectedOption=2;
                    message=getString(R.string.message_option2);

                }
            }
        });
        showSlider(3);



        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(selectedOption==0){
//                    Toast.makeText(AdminLoginOptions.this, "You need to select one option", Toast.LENGTH_SHORT).show();
//                }else{
//                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AdminLoginOptions.this);
//                    builder.setTitle(R.string.sure);
//                    builder.setMessage(message);
//                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
//                        progressDialog.show();
//                        if(sharedSelected>0){
//                            updateToFirebase();
//                        }else{
//                            signInFirebase();
//                        }
//
//
//                    });
//                    builder.setNegativeButton(R.string.change, (dialog, which) ->{
//                        dialog.dismiss();
//
//                    } );
//                    builder.show();
//                }

                Log.e("IndustryPosition",""+sharedpreferences.getLong("industryPosition",0));

                if(sharedpreferences.getLong("industryPosition",0)>1){
                    startActivity(new Intent(AdminLoginOptions.this,TimelineOtherIndustry1.class));
                    finish();
                }else{
                    startActivity(new Intent(AdminLoginOptions.this, timelineActivity.class));
                    finish();
                }


            }
        });
        binding.txtKmOpt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSlider(1);

            }
        });
        binding.txtKmOpt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSlider(2);
            }
        });
        binding.txtKmOpt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSlider(3);
            }
        });

    }

    private void showSlider(int opt) {
//        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        View mView = LayoutInflater.from(this).inflate(R.layout.layout_know_more, null);
//        alert.setView(mView);
//        SliderView slider;
//        slider=mView.findViewById(R.id.slider);
//        if(opt==1){
//            getSliderData("Opt1",slider);
//        }else if(opt==2){
//            getSliderData("Opt2",slider);
//        }else if(opt==3){
//            getSliderData("Opt3",slider);
//        }
//        final AlertDialog alertDialog = alert.create();
//        alertDialog.setCanceledOnTouchOutside(true);
//        alertDialog.show();
        getSliderData("Opt3",binding.slider);

    }

    private void getSliderData(String opt, SliderView slider) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Banner");
        reference.child(opt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderDataArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    SliderData sliderData=ds.getValue(SliderData.class);
                    sliderDataArrayList.add(sliderData);
                }
                Log.e("sliderData",""+sliderDataArrayList.size());
                SliderAdapter adapter = new SliderAdapter(AdminLoginOptions.this, sliderDataArrayList);
                binding.slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                binding.slider.setSliderAdapter(adapter);
                binding.slider.setScrollTimeInSec(5);
                binding.slider.setAutoCycle(true);
                binding.slider.startAutoCycle();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void signInFirebase() {
        firebaseAuth.signInWithEmailAndPassword(userName+"@yopmail.com",password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        editor.putString("uid", firebaseAuth.getUid());
                        editor.putString("userName", userName);
                        editor.putString("userDesignation", userDesignation);
                        editor.putString("userMobile", userMobile);
                        editor.putString("companyName", companyName);
                        editor.putInt("workOption", selectedOption);


                        editor.apply();
                        editor.commit();
                        editorPL.putString("userId",userName);
                        editorPL.apply();
                        editorPL.commit();
                        updateToFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminLoginOptions.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateToFirebase() {
        HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("workOpt",selectedOption);
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SimpleDateFormat df = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                        }
                        Date c = Calendar.getInstance().getTime();
                        String currentDate = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            currentDate = df.format(c);
                        }
                        if(sharedSelected==0){
                            editorLogin.putString("LastLogin", currentDate);
                            editorLogin.putBoolean("LoginFirstTime",false);
                            editorLogin.commit();
                            startActivity(new Intent(AdminLoginOptions.this, timelineActivity.class));
                            finish();

                        }else if(sharedSelected>0){
                            editor.putInt("workOption", selectedOption);
                            editor.apply();
                            editor.commit();
                            startActivity(new Intent(AdminLoginOptions.this, timelineActivity.class));
                            finish();

                        }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminLoginOptions.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}