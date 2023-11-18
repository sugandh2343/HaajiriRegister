package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterTimeline1;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelUserIndustry;
import com.skillzoomer_Attendance.com.Model.ModelWorkPlace;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityTimelineOtherIndustry1Binding;

import java.util.ArrayList;

public class TimelineOtherIndustry1 extends AppCompatActivity {
    ActivityTimelineOtherIndustry1Binding binding;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelWorkPlace> siteArrayList;
    private ArrayList<ModelUserIndustry> userIndustryArrayList;
    private SharedPreferences.Editor editor;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTimelineOtherIndustry1Binding.inflate(getLayoutInflater());
        firebaseAuth=FirebaseAuth.getInstance();


        siteArrayList=new ArrayList<>();
        userIndustryArrayList=new ArrayList<>();
//        toolbar = findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.menu_navigationbar);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                onOptionsItemSelected(item);
//                return false;
//            }
//        });
//        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.menu_navigationbar);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                onOptionsItemSelected(item);
//                return false;
//            }
//        });
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        Log.e("HRGHVHGFD","HR:::"+sharedpreferences.getString("hrUid",""));
        Log.e("HRGHVHGFD","TL:::"+sharedpreferences.getString("tlUid",""));
        Log.e("HRGHVHGFD","Des:::"+sharedpreferences.getString("designationName",""));
        editor = sharedpreferences.edit();
        String str = sharedpreferences.getString("fullName","");
        Log.e("String",str);
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        if (getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Language", "hi").equals("en")) {
            binding.txtAdmin.setText(getString(R.string.hii) + " " + builder + " " + getString(R.string.hi_you_are_an_administrator1) + " " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("designationName", ""));
        } else {
            binding.txtAdmin.setText(getString(R.string.hii) + " " + builder + " " + getString(R.string.hi_you_are_an_administrator1) + " " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("designationHindi", "") + " है");

        }
        binding.txtComapnyName.setText(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", ""));
        getIndustryList();


        getSiteList();

        binding.llRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimelineOtherIndustry1.this,ShowLeaveRequestActivity.class));
            }
        });
        binding.llWorkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimelineOtherIndustry1.this,EmpMasterData.class));
            }
        });
        binding.btnAddIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimelineOtherIndustry1.this,ActivityProfile.class));
            }
        });
        setContentView(binding.getRoot());
        binding.btnAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimelineOtherIndustry1.this,AddWorkPlaceActivity.class));
            }
        });
//        binding.llProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(TimelineOtherIndustry1.this, TaskActivity.class));
//            }
//        });
        binding.spinnerSelectIndustry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    editor.putLong("industryPosition",userIndustryArrayList.get(i).getIndustryPosition());
                    editor.putString("industryName", userIndustryArrayList.get(i).getIndustryName());
                    editor.putString("companyName", userIndustryArrayList.get(i).getCompanyName());
                    editor.apply();
                    editor.commit();
                    Log.e("SiteTime","SS"+userIndustryArrayList.get(i).getIndustryPosition());
                    if(userIndustryArrayList.get(i).getIndustryName().equals("Construction")){
                        startActivity(new Intent(TimelineOtherIndustry1.this,timelineActivity.class));
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    private void getIndustryList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIndustryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUserIndustry modelCategory = ds.getValue(ModelUserIndustry.class);
                    if(!modelCategory.getIndustryName().equals(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))){
                        userIndustryArrayList.add(modelCategory);
                    }

                }

                userIndustryArrayList.add(0,new ModelUserIndustry("Switch Industry",0));

                if(userIndustryArrayList.size()>1){
                    SpinnerCategoryAdapter spinnerCategoryAdapter = new SpinnerCategoryAdapter();
                    binding.spinnerSelectIndustry.setAdapter(spinnerCategoryAdapter);
                    binding.llIndustry.setVisibility(View.VISIBLE);
                }else{
                    binding.llIndustry.setVisibility(View.GONE);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class SpinnerCategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userIndustryArrayList.size();
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
            View row = inf.inflate(R.layout.layout_of_country_row, null);
            TextView spinner_text;

            spinner_text = row.findViewById(R.id.spinner_text);
            spinner_text.setText(userIndustryArrayList.get(position).getIndustryName());


            return row;
        }
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

                }

                Log.e("SiteArrayList",""+siteArrayList.size());
                Log.e("SiteArrayList","ISP"+getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));

                AdapterTimeline1 adapterTimeline1=new AdapterTimeline1(TimelineOtherIndustry1.this,siteArrayList);
                binding.rvTimeline.setAdapter(adapterTimeline1);
                if(siteArrayList.size()>0){
                    binding.btnAddSite.setText("Add another workplace");
                    binding.llWithAssociate.setVisibility(View.VISIBLE);
                    binding.txtAddMember.setVisibility(View.VISIBLE);
                }else{
                    binding.btnAddSite.setText("Add your First workplace");
                    binding.llWithAssociate.setVisibility(View.GONE);
                    binding.txtAddMember.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile:

                startActivity(new Intent(TimelineOtherIndustry1.this, AboutActivity.class));
//                finish();
                return true;
            case R.id.language:
                startActivity(new Intent(TimelineOtherIndustry1.this, LanguageChange.class));
//                finish();
                return true;
//                case R.id.share:
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("text/plain");
//                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.project.skill"); // url for share the app
//                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
//                    return true;
            case R.id.youtube:
                // rate implementation here
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/playlist?list=PLasxv4S2lb-Lz_4dJ9E5GOIT3NqJk_BX-"))); // rate the app
                return true;

            case R.id.share:
                // rate implementation here
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Hi!I am using this app called हाज़िरी Register and would like to share it with you. Download now for free from Playstore. Click on this link \n" + "https://play.google.com/store/apps/details?id=com.skillzoomer_Attendance.com";
                String sub = "Invite";
                System.out.println("++++++++++++++++++++body" + body);
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                myIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
                return true;

//            case R.id.changeWorkOption:
//                startActivity(new Intent(TimelineOtherIndustry1.this,AdminLoginOptions.class));
//                return true;
            case R.id.transaction:
                startActivity(new Intent(TimelineOtherIndustry1.this, TransactionListActivity.class));
                return true;
            case R.id.swapAdmin:
                startActivity(new Intent(TimelineOtherIndustry1.this, ActivitySowap.class));
                return true;
            case R.id.logout:
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(TimelineOtherIndustry1.this);
                builder.setTitle(R.string.log_out).setMessage(R.string.are_you_sure_you).setCancelable(false).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    firebaseAuth.signOut();
                    editor.clear();
                    editor.commit();
                    dialogInterface.dismiss();
//                                    startActivity(new Intent(TimelineOtherIndustry1.this,SplashActivity.class));
                    this.finishAffinity();
                }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
