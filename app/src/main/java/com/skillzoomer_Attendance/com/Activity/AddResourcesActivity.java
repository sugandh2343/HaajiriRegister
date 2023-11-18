package com.skillzoomer_Attendance.com.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.skillzoomer_Attendance.com.Adapter.AdapterSite;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAddResourcesBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class AddResourcesActivity extends AppCompatActivity {
    ActivityAddResourcesBinding binding;
    LayoutToolbarBinding toolbarBinding;
    FirebaseAuth firebaseAuth;
    ModelUser modelUser;
    String uid,userName;
    private ProgressDialog progressDialog;
    private ArrayList<ModelSite> modelSiteArrayList;
    private long siteId;
    private String siteName,siteTimestamp;
    String userType;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddResourcesBinding.inflate(getLayoutInflater());
//        toolbarBinding=binding.toolbarLayout;
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        toolbarBinding.heading.setText("Add Resources");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();

        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType=sharedpreferences.getString("userDesignation","");
        username=sharedpreferences.getString("userDesignation","");
        MyApplication my = new MyApplication( );
        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));
        Log.e("userType",userType);
        modelUser=new ModelUser();
        modelSiteArrayList=new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new MaterialShowcaseView.Builder(AddResourcesActivity.this)
                    .setTarget(binding.btnAddSite)
                    .setGravity(2)
                    .withRectangleShape(true)
                    .setDismissOnTouch(true)
                    .setContentText(getString(R.string.content_add_new_site))// optional but starting animations immediately in onCreate can make them choppy
                    .setContentTextColor(getColor(R.color.white))
                    .singleUse("addNewSite")
                    .show();
        }
        getSiteList();
        uid=firebaseAuth.getCurrentUser().getUid();
        int siteID=0,siteName;
        Log.e("Designation",sharedpreferences.getString("designation",""));
        Log.e("Designation",sharedpreferences.getString("designationHindi",""));
        if (sharedpreferences.getString("Language","hi").equals("en")){
            binding.txtAdmin.setText(getString(R.string.hi_you_are_an_administrator)+" "+sharedpreferences.getString("designation",""));
        }else{
            binding.txtAdmin.setText(getString(R.string.hi_you_are_an_administrator)+" "+sharedpreferences.getString("designationHindi","")+" है");

        }
        if(uid!=null){
            progressDialog.show();
            getUserInfo();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_details"));
        binding.btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddResourcesActivity.this,MainActivity.class));
            }
        });
        binding.btnAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddResourcesActivity.this,AddSiteActivity.class));
            }
        });
        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddResourcesActivity.this, timelineActivity.class));
//                if(selected_option==2){
//                    Intent intent=new Intent(AddResourcesActivity.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(AddResourcesActivity.this,ReportHome.class));
//                }
//
            }
        });
        binding.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddResourcesActivity.this, ActivityProfile.class));
            }
        });

        binding.llWorkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add Activity work here
                startActivity(new Intent(AddResourcesActivity.this, ViewMasterDataSheet.class));
//                if((selected_option==2)||(selected_option==1)){
//                    startActivity(new Intent(AddResourcesActivity.this,ViewMasterDataSheet.class));
//                }else{
//                    startActivity(new Intent(AddResourcesActivity.this,AdminSiteActivity.class));
//                }

            }
        });
        binding.llRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddResourcesActivity.this, AdvancesHomeActivity.class);
                intent.putExtra("SiteSpinner", true);
                startActivity(intent);
//                if(selected_option==1){
//                    Intent intent=new Intent(AddResourcesActivity.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(AddResourcesActivity.this,RequestListActivity.class));
//                }

            }
        });



    }

    private void getSiteList() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelSiteArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    Log.e("ModelArrayList",""+modelSite.getSiteName());
                    modelSiteArrayList.add(modelSite);
                }
                if(modelSiteArrayList.size()>0) {
                    Log.e("ModelArrayList" , "" + modelSiteArrayList.size());
                    Log.e("ModelArrayList" , "" + modelSiteArrayList.get(0).getSiteName());
                    binding.rvSite.setVisibility(View.VISIBLE);
                    binding.txtAddMember.setVisibility(View.VISIBLE);
                    binding.btnAddSite.setText(R.string.add_another_work_site);
                    AdapterSite adapterSite = new AdapterSite(AddResourcesActivity.this , modelSiteArrayList);
                    binding.rvSite.setAdapter(adapterSite);
                }else{
                    binding.rvSite.setVisibility(View.GONE);
                    binding.txtAddMember.setVisibility(View.GONE);
                    binding.btnAddSite.setText(R.string.add_first_work_site);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            progressDialog.dismiss();
                            for(DataSnapshot child : snapshot.getChildren()){
                                modelUser = child.getValue(ModelUser.class);
                                Log.e("ModelUser",modelUser.getMobile());
                            }

                            userName= modelUser.getName();

                            Log.e("UserName",userName);







                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Log.e("DatabaseError",error.getMessage());

                    }
                });

    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            siteId=intent.getIntExtra("siteID",0);
            siteName=intent.getStringExtra("siteName");
            siteTimestamp=intent.getStringExtra("siteTimestamp");
        Intent intent1=new Intent(AddResourcesActivity.this, MemberTimelineActivity.class);
        Log.e("AddREsources",""+siteId);
        intent1.putExtra("siteID",siteId);
        intent1.putExtra("siteName",siteName);
        intent1.putExtra("timestamp",siteTimestamp);
        startActivity(intent1);




        }
    };
}