package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterPicActivity;
import com.skillzoomer_Attendance.com.Model.ModelPicActivity;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelSiteSpinner;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityPicAdminBinding;

import java.util.ArrayList;

public class PicActivityAdmin extends AppCompatActivity {
//    ActivityPicAdminBinding binding;
//    String userType, siteName, userName;
//    long siteId=0;
//    private ArrayList<ModelSite> siteAdminList;
//    private ArrayList<ModelSiteSpinner> siteNameArrayList;
//    FirebaseAuth firebaseAuth;
//    private ArrayList<ModelPicActivity> picActivityArrayList;
//    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding=ActivityPicAdminBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        setContentView(binding.getRoot());
//        firebaseAuth=FirebaseAuth.getInstance();
//        picActivityArrayList=new ArrayList<>();
//        siteAdminList=new ArrayList<>();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle(getResources().getString(R.string.please_wait));
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//
//        binding.tabLayout.setSelected(false);
//
//        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                Log.e("TAB",""+tab.getPosition());
//                if(tab.getPosition()==0){
//                    getPicActivityList(siteId,false,"");
//                }else if(tab.getPosition()==1){
//                    getPicActivityList(siteId,true,"HR Manager");
//                }else if(tab.getPosition()==2){
//                    getPicActivityList(siteId,true,"Supervisor");
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//        binding.llManpower.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(PicActivityAdmin.this, ReportHome.class));
//            }
//        });
//        binding.llHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //TODO add Activity work here
//                startActivity(new Intent(PicActivityAdmin.this,timelineActivity.class));
//            }
//        });
//        getSiteListAdministrator();
//        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                siteId = siteAdminList.get(i).getSiteId();
//                siteName = siteAdminList.get(i).getSiteName();
//                Log.e("SiteId", "Spinner" + siteId);
//                if(binding.tabLayout.getSelectedTabPosition()==0){
//                    getPicActivityList(siteId,false,"");
//                }else  if(binding.tabLayout.getSelectedTabPosition()==2){
//                    getPicActivityList(siteId,true,"Supervisor");
//                }else if(binding.tabLayout.getSelectedTabPosition()==1){
//                    getPicActivityList(siteId,true,"HR Manager");
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
////        binding.btnShow.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                if(siteId==0){
////                    Toast.makeText(PicActivityAdmin.this, getString(R.string.select_site), Toast.LENGTH_SHORT).show();
////                }else{
////                    progressDialog.show();
////                    getPicActivityList(siteId, false, "");
////                }
////            }
////        });
    }

//    private void getPicActivityList(long siteId, boolean b, String s) {
//        picActivityArrayList.clear();
//        Log.e("SiteId","Pic"+siteId);
//        if(!b){
//            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Site");
//            reference.child(String.valueOf(siteId)).child("PicActivity").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    if (snapshot.exists()) {
//
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            Log.e("Snapshot",""+ds.hasChild("picId"));
//                            ModelPicActivity modelPicActivity = ds.getValue(ModelPicActivity.class);
//                            picActivityArrayList.add(modelPicActivity);
//                        }
//                        progressDialog.dismiss();
//                        binding.workActivityList.setVisibility(View.VISIBLE);
//                        AdapterPicActivity adapterPicActivity=new AdapterPicActivity(PicActivityAdmin.this,picActivityArrayList,userType,"Pending");
//                        binding.workActivityList.setAdapter(adapterPicActivity);
//                    }else{
//                        progressDialog.dismiss();
//                        Log.e("Snapshot","Not Exist");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }else{
//            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Site");
//            reference.child(String.valueOf(siteId)).child("PicActivity").orderByChild("uploadedBytype").equalTo(s).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            Log.e("Snapshot",""+ds.hasChild("picId"));
//                            ModelPicActivity modelPicActivity = ds.getValue(ModelPicActivity.class);
//                            picActivityArrayList.add(modelPicActivity);
//                        }
//                        progressDialog.dismiss();
//                        binding.workActivityList.setVisibility(View.VISIBLE);
//                        AdapterPicActivity adapterPicActivity=new AdapterPicActivity(PicActivityAdmin.this,picActivityArrayList,userType,"Pending");
//                        binding.workActivityList.setAdapter(adapterPicActivity);
//                    }else{
//                        progressDialog.dismiss();
//                        Log.e("Snapshot","Not Exist");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        }
//
//    }
//
//    private void getSiteListAdministrator() {
//        siteAdminList.clear();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    ModelSite modelSite = ds.getValue(ModelSite.class);
//                    siteAdminList.add(modelSite);
//                }
//                Log.e("siteAdminList", "" + siteAdminList.size());
//              SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
//                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
//                getPicActivityList(siteAdminList.get(0).getSiteId(),false,"");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//    class SiteSpinnerAdapter
//            extends BaseAdapter {
//        SiteSpinnerAdapter() {
//        }
//
//        public int getCount() {
//            return siteAdminList.size();
//        }
//
//        public Object getItem(int n) {
//            return null;
//        }
//
//        public long getItemId(int n) {
//            return 0L;
//        }
//
//        public View getView(int n, View view, ViewGroup viewGroup) {
//            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
//            TextView textView = (TextView) view2.findViewById(R.id.spinner_text);
//            textView.setText(siteAdminList.get(n).getSiteCity());
//            return view2;
//        }
//    }

}