package com.skillzoomer_Attendance.com.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.skillzoomer_Attendance.com.Adapter.AdapterMember;
import com.skillzoomer_Attendance.com.Adapter.AdapterPowerDistribution;
import com.skillzoomer_Attendance.com.Adapter.AdapterTimeline;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Utilities.ItemClickListener;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivitySiteBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutAddMemberBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutForceLogoutKnowMoreBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class SiteActivity extends AppCompatActivity {
    ActivitySiteBinding binding;
    LayoutToolbarBinding toolbarBinding;
    private ProgressDialog progressDialog;
    private String[] designation = {"Associate"};
    private String selectedDesignation;
    String timestamp,siteName="";
    long siteId=0;
    Boolean result;
    private ArrayList<ModelLabour> labourList;
    private ArrayList<ModelSite> siteArrayList;
    private ArrayList<ModelLabour> skilledLabourList;
    private ArrayList<ModelLabour> unskilledLabourList;
    private ArrayList<ModelUser> memberArrayList;
    private ArrayList<ModelUser> memberSiteArrayList;
    String userType;
    FirebaseAuth firebaseAuth;
    private SharedPreferences.Editor editor;
    private static final int PICK_CONTACT=401;
    EditText et_name,et_mobile_number;
    String inviteLink;
    private String companyName;
    private long memberCount=0;
    int selected_option;
    Boolean forceLogout=false;
    Boolean picActivity=false;
    Boolean attendanceManagement=true,workActivity=true,cashManagement=true,financeManagement=true;
    ItemClickListener itemClickListener;
    private ArrayList<ModelUser> associateArrayList;
    private AdapterPowerDistribution adapterPowerDistribution;
    private String siteStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_site);
        binding = ActivitySiteBinding.inflate(getLayoutInflater());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        siteArrayList=new ArrayList<>();
//        Log.e("binding",""+(binding.btnAddMember.getVisibility()==View.VISIBLE));
//        toolbarBinding.heading.setText("Site");
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        selected_option=sharedpreferences.getInt("workOption",0);
        associateArrayList=new ArrayList<>();
        memberSiteArrayList=new ArrayList<>();

        editor=sharedpreferences.edit();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("AttendanceChange"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver1,
                new IntentFilter("CashChange"));
        LocalBroadcastManager.getInstance(this).registerReceiver(refresh,
                new IntentFilter("Refresh"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver2,
                new IntentFilter("ExpenseChange"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver3,
                new IntentFilter("ForceLogout"));


        getSiteList();
//        designation[0]= String.valueOf(new String[]{getResources().getString(R.string.associate)});

//        toolbarBinding.back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SiteActivity.this);
//                builder.setTitle(getString(R.string.exit))
//                        .setMessage(R.string.are_you_sure_you_exit)
//                        .setCancelable(true)
//                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
//                           finishAffinity();
//                        })
//                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                builder.show();
//
//
//            }
//        });
//        if(selected_option==1){
//            binding.txtSkip.setVisibility(View.GONE);
//        }else{
//            binding.txtSkip.setVisibility(View.VISIBLE);
//        }
        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SiteActivity.this, timelineActivity.class));
//                if(selected_option==2){
//                    Intent intent=new Intent(SiteActivity.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(SiteActivity.this,ReportHome.class));
//                }
//
            }
        });
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            new MaterialShowcaseView.Builder(SiteActivity.this)
//                    .setTarget(binding.btnAddMember)
//                    .setGravity(2)
//                    .withRectangleShape(true)
//                    .setTargetTouchable(true)
//                    .setContentText(getString(R.string.content_site1))// optional but starting animations immediately in onCreate can make them choppy
//                    .setContentTextColor(getColor(R.color.white))
//                    .setDismissTextColor(getColor(R.color.red))
//                    .setDismissStyle(Typeface.DEFAULT_BOLD)
//                    .singleUse("addSite")
//                    .show();
//        }
        binding.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SiteActivity.this, ActivityProfile.class));
            }
        });

        binding.llWorkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add Activity work here
                startActivity(new Intent(SiteActivity.this, ViewMasterDataSheet.class));
//                if((selected_option==2)||(selected_option==1)){
//                    startActivity(new Intent(SiteActivity.this,ViewMasterDataSheet.class));
//                }else{
//                    startActivity(new Intent(SiteActivity.this,AdminSiteActivity.class));
//                }

            }
        });
        binding.llRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SiteActivity.this, AdvancesHomeActivity.class);
                intent.putExtra("SiteSpinner", true);
                startActivity(intent);
//                if(selected_option==1){
//                    Intent intent=new Intent(SiteActivity.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(SiteActivity.this,RequestListActivity.class));
//                }

            }
        });
        binding.txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(memberCount==0){
                    androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(SiteActivity.this);
                    builder1.setTitle(getString(R.string.skip_title))
                            .setMessage(R.string.skip_to_admin)
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                startActivity(new Intent(SiteActivity.this, timelineActivity.class));
                                finish();


//                                    startActivity(new Intent(timelineActivity.this,SplashActivity.class));
                            })
                            .setNegativeButton(R.string.no, (dialogInterface, i) ->
                                    dialogInterface.dismiss());
                    builder1.show();
                }else {
                    startActivity(new Intent(SiteActivity.this, timelineActivity.class));
                }
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        userType=sharedpreferences.getString("userDesignation","");

        Intent intent=getIntent();
        firebaseAuth=FirebaseAuth.getInstance();



        companyName=sharedpreferences.getString("companyName","");


        Log.e("userType",""+(userType.equals("HR Manager")||userType.equals("Clerk")));

            binding.llHr.setVisibility(View.VISIBLE);
            siteName = intent.getStringExtra("siteName");
//        siteId = intent.getLongExtra("siteID", 0);
//        if(siteId>0)
//            getSiteStatus(siteId);

//

//                Log.e("siteIdIS", "" + siteId);
//                Log.e("siteIdIS", "" + siteName);


        Log.e("siteName11",""+siteName);
        Log.e("siteId11",""+siteId);
        Log.e("SiteActivity",""+siteId);
        labourList=new ArrayList<>();
        skilledLabourList=new ArrayList<>();
        unskilledLabourList=new ArrayList<>();
        memberArrayList=new ArrayList<>();

//        getLabourList();

        Log.e("SkilledLabour",""+skilledLabourList.size());


//        timestamp=intent.getStringExtra("siteTimestamp");
//        Log.e("TimeStampSiteActivity",timestamp);
//        binding.btnAddMember.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("memberdialog",""+siteId);
//
//                        if(memberCount==0){
//                            checkForInvitedMember(memberCount,siteId);
//                        }else{
//                            openAddMemberDialog(siteId,memberCount);
//                        }
//
//
//
//
//
//            }
//
//        });


//        binding.btnSkilledViewMasterData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(SiteActivity.this,ViewMasterDataSheet.class);
//                intent.putExtra("siteId",String.valueOf(siteId));
//                intent.putExtra("userType","Skilled");
//                intent.putExtra("siteName",siteName);
//                startActivity(intent);
//            }
//        });
        binding.llKnowMoreFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SiteActivity.this);
                View mView = LayoutInflater.from(SiteActivity.this).inflate(R.layout.layout_force_logout_know_more, null);
                alert.setView(mView);
                ImageView iv_close=mView.findViewById(R.id.iv_close);
                final android.app.AlertDialog alertDialog = alert.create();
                alertDialog.show();
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        binding.siteUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SiteActivity.this, UpdateSiteActivity.class);
                intent.putExtra("siteName",siteArrayList.get(binding.spinnerSite.getSelectedItemPosition()).getSiteName());
                intent.putExtra("siteId",siteArrayList.get(binding.spinnerSite.getSelectedItemPosition()).getSiteId());
                intent.putExtra("siteCity",siteArrayList.get(binding.spinnerSite.getSelectedItemPosition()).getSiteCity());
                intent.putExtra("siteStart",siteArrayList.get(binding.spinnerSite.getSelectedItemPosition()).getStartTime());
                intent.putExtra("siteEnd",siteArrayList.get(binding.spinnerSite.getSelectedItemPosition()).getEndTime());
                intent.putExtra("siteAddress",siteArrayList.get(binding.spinnerSite.getSelectedItemPosition()).getSiteAddress());
                startActivity(intent);
            }
        });
        binding.btnActiveInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                if(siteStatus.equals("Active")){
                    message=getString(R.string.are_you_sure_inactive);
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SiteActivity.this);
                    builder.setTitle(R.string.sure)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                getMemberSiteList();

                            })
                            .setNegativeButton(R.string.no, (dialogInterface, i) ->
                                    dialogInterface.dismiss());
                    builder.show();
                }else{
                    message=getString(R.string.are_you_sure_active);
                    getMemberSiteList();

                }


            }
        });
        binding.btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnPower.setVisibility(View.GONE);
                binding.llDistributePowe.setVisibility(View.VISIBLE);
                binding.llMemberList.setVisibility(View.GONE);
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnPower.setVisibility(View.VISIBLE);
                binding.llDistributePowe.setVisibility(View.GONE);
                binding.llMemberList.setVisibility(View.VISIBLE);
            }
        });


        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SiteActivity.this);
                builder.setTitle(R.string.log_out)
                        .setMessage(R.string.power_change)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialogInterface, j) -> {
                            if(associateArrayList.size()>0){
                                for(int i=0;i<associateArrayList.size();i++){

                                    ModelUser modelAssociate=associateArrayList.get(i);
                                    HashMap<String,Object> hashMap=new HashMap<>();
                                    hashMap.put("attendanceManagement",modelAssociate.getAttendanceManagement());
                                    hashMap.put("cashManagement",modelAssociate.getCashManagement());
                                    hashMap.put("financeManagement",modelAssociate.getFinanceManagement());
                                    hashMap.put("forceOpt",modelAssociate.getForceOpt());
                                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                                    reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(modelAssociate.getUid()).updateChildren(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    updateToUserDatabase(modelAssociate);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Exception",e.getMessage());
                                                }
                                            });

                                }
                                HashMap<String,Object> hashMap=new HashMap();
                                hashMap.put("tempLogout",true);
                                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });

                            }


                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();





            }
        });

        binding.spinnerSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    siteStatus=siteArrayList.get(i).getSiteStatus();
                    siteId=siteArrayList.get(i).getSiteId();
                    siteName=siteArrayList.get(i).getSiteName();
                    binding.llMain.setVisibility(View.VISIBLE);
                    if (siteStatus.equals("Active")) {
                        binding.btnActiveInactive.setBackgroundColor(getResources().getColor(R.color.red));
                        binding.btnActiveInactive.setText(getString(R.string.make_this_site_inactive));
                        binding.btnActiveInactive.setTextColor(getResources().getColor(R.color.white));
                        binding.llHr.setVisibility(View.VISIBLE);
                        getMemberList();

                    } else {
                        binding.btnActiveInactive.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                        binding.btnActiveInactive.setText(getString(R.string.make_this_site_active));
                        binding.btnActiveInactive.setTextColor(getResources().getColor(R.color.black));
                        binding.llHr.setVisibility(View.GONE);
                        binding.llDistributePowe.setVisibility(View.GONE);
                        binding.btnPower.setVisibility(View.GONE);

                    }

                }else{
                    binding.llMain.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    private void getSiteList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteArrayList.clear();
                String currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                Log.e("DataChange", "Yes");
                siteArrayList.clear();
                progressDialog.dismiss();
                Log.e("DateM", "Today12:::" + currentDate);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    siteArrayList.add(modelSite);



                }
                if (siteArrayList.size() > 0) {


                    siteArrayList.add(0,new ModelSite(getString(R.string.select_site), 0, false));

                    SiteSpinnerAdapter siteSpinnerAdapter=new SiteSpinnerAdapter();
                    binding.spinnerSite.setAdapter(siteSpinnerAdapter);





                } else if (siteArrayList.size() == 0) {

                    if (selected_option == 1) {
//                        binding.txtForgotPassword.setText("No Site Found");

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForInvitedMember(long memberCount, long siteId) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("InvitedMembers");
        reference.orderByChild("Hruid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    String mobile = null;
                    for(DataSnapshot ds:snapshot.getChildren()){
                        if(ds.child("siteId").getValue(long.class).equals(siteId)){
                            mobile=ds.child("MemberMobile").getValue(String.class);
                            Log.e("Mobile",mobile);
                        }

                    }
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SiteActivity.this);
                    String finalMobile = mobile;
                    builder.setTitle(R.string.sure)
                            .setMessage( getString(R.string.already_invited))
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                removeInVitedMember(siteId, finalMobile,memberCount);


                            })
                            .setNegativeButton(R.string.no, (dialogInterface, i) ->
                                    dialogInterface.dismiss());
                    builder.show();


                    Toast.makeText(SiteActivity.this, getString(R.string.already_invited), Toast.LENGTH_SHORT).show();
                }else{
                    openAddMemberDialog(SiteActivity.this.siteId,memberCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeInVitedMember(long siteId, String finalMobile, long memberCount) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("InvitedMembers");
        reference.child(finalMobile).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                openAddMemberDialog(siteId,memberCount);
            }
        });
    }

    private void getSiteStatus(long siteId) {
        Log.e("SiteId123",""+siteId);

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteStatus = snapshot.child("siteStatus").getValue(String.class);
                if (siteStatus.equals("Active")) {
                    binding.btnActiveInactive.setBackgroundColor(getResources().getColor(R.color.red));
                    binding.btnActiveInactive.setText(getString(R.string.make_this_site_inactive));
                    binding.btnActiveInactive.setTextColor(getResources().getColor(R.color.white));
                    binding.llHr.setVisibility(View.VISIBLE);
                    getMemberList();

                } else {
                    binding.btnActiveInactive.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                    binding.btnActiveInactive.setText(getString(R.string.make_this_site_active));
                    binding.btnActiveInactive.setTextColor(getResources().getColor(R.color.black));
                    binding.llHr.setVisibility(View.GONE);
                    binding.llDistributePowe.setVisibility(View.GONE);
                    binding.btnPower.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMemberSiteList() {
        Log.e("SiteId12345",String.valueOf(siteId));
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberCount=snapshot.getChildrenCount();
                if(memberCount>0){
                    memberSiteArrayList.clear();
                    Log.e("memberCount",""+memberCount);
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Log.e("Uid",""+ds.child("memberUid").getValue(String.class));
                        blockAssociate(ds.child("memberUid").getValue(String.class));

                    }
                    makeSiteInActive();
                }else{
                    makeSiteInActive();
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void blockAssociate(String uid) {
        HashMap<String,Object> hashMap=new HashMap<>();
        if(siteStatus.equals("Active")){
            hashMap.put("memberBlock","Blocked");
        }else{
            hashMap.put("memberBlock","Active");
        }
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("Uid",uid+"blocked");
            }
        });


    }

    private void makeSiteInActive() {
        HashMap<String,Object> hashMap=new HashMap<>();
        if(siteStatus.equals("Active")){
            hashMap.put("siteStatus","Inactive");
        }else{
            hashMap.put("siteStatus","Active");
        }
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SiteActivity.this, "Site Status changed successfully", Toast.LENGTH_SHORT).show();
                if(siteStatus.equals("Active")){
                    binding.btnActiveInactive.setBackgroundColor(getResources().getColor(R.color.red));
                    binding.btnActiveInactive.setText(getString(R.string.make_this_site_inactive));
                    binding.btnActiveInactive.setTextColor(getResources().getColor(R.color.white));
                }else{
                    binding.btnActiveInactive.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                    binding.btnActiveInactive.setText(getString(R.string.make_this_site_active));
                    binding.btnActiveInactive.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

    }

    private void updateToUserDatabase(ModelUser modelAssociate) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("attendanceManagement",modelAssociate.getAttendanceManagement());
        hashMap.put("cashManagement",modelAssociate.getCashManagement());
        hashMap.put("financeManagement",modelAssociate.getFinanceManagement());
        hashMap.put("forceOpt",modelAssociate.getForceOpt());
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelAssociate.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SiteActivity.this, getString(R.string.role_distributed_successfully), Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception",e.getMessage());
                    }
                });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position",-1);
            Boolean  value = intent.getBooleanExtra("value",false);
            if(position>=0 && associateArrayList.size()>0){
//                for (int i=0;i<associateArrayList.size();i++){
//                    associateArrayList.get(i).setAttendanceManagement(false);
//                }
                associateArrayList.get(position).setAttendanceManagement(value);
                adapterPowerDistribution.notifyDataSetChanged();
            }

        }
    };
    public BroadcastReceiver mMessageReceiver1 = new BroadcastReceiver() {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position",-1);
            Boolean  value = intent.getBooleanExtra("value",false);
            if(position>=0 && associateArrayList.size()>0){
//                for (int i=0;i<associateArrayList.size();i++){
//                    associateArrayList.get(i).setCashManagement(false);
//                }
                associateArrayList.get(position).setCashManagement(value);
                adapterPowerDistribution.notifyDataSetChanged();
            }

        }
    };
    public BroadcastReceiver refresh = new BroadcastReceiver() {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            getMemberList();

        }
    };
    public BroadcastReceiver mMessageReceiver2 = new BroadcastReceiver() {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position",-1);
            Boolean  value = intent.getBooleanExtra("value",false);
            if(position>=0 && associateArrayList.size()>0){
//                for (int i=0;i<associateArrayList.size();i++){
//                    associateArrayList.get(i).setFinanceManagement(false);
//                }
                associateArrayList.get(position).setFinanceManagement(value);
                adapterPowerDistribution.notifyDataSetChanged();
            }

        }
    };
    public BroadcastReceiver mMessageReceiver3 = new BroadcastReceiver() {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position",-1);
            Boolean force=intent.getBooleanExtra("forceLogout",false);
            Log.e("ForceLLL",""+force);
            if(position>=0 && associateArrayList.size()>0){

                associateArrayList.get(position).setForceOpt(force);
                Log.e("ForceLLL",""+associateArrayList.get(position).getForceOpt());
                adapterPowerDistribution.notifyDataSetChanged();
            }

        }
    };



    private void getMemberList() {
        Log.e("SiteId12345",String.valueOf(siteId));
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()) {
                    if (ds.child("siteId").getValue(long.class).equals(siteId)) {
                        memberCount++;
                    }
                }
//                memberCount=snapshot.getChildrenCount();
                Log.e("memberCount",""+memberCount);
                if(memberCount==1 &&siteStatus.equals("Active") ){
                    binding.llDistributePowe.setVisibility(View.GONE);
                    binding.llMemberList.setVisibility(View.VISIBLE);
                    binding.btnPower.setVisibility(View.VISIBLE);
                    getAssociateList();

                }else if(memberCount>1 &&siteStatus.equals("Active")){
                    binding.llDistributePowe.setVisibility(View.GONE);
                    binding.llMemberList.setVisibility(View.VISIBLE);
                    binding.btnPower.setVisibility(View.VISIBLE);
                    getAssociateList();
                }else if(memberCount==0&&siteStatus.equals("Active")){
                    binding.llMemberList.setVisibility(View.GONE);
                    binding.llDistributePowe.setVisibility(View.GONE);
                    binding.btnPower.setVisibility(View.GONE);

                }
                for(DataSnapshot ds:snapshot.getChildren()){

                    ModelUser modelMember=ds.getValue(ModelUser.class);
                    if(modelMember.getSiteId()==siteId){
                        memberArrayList.add(modelMember);
                    }


                }
                Log.e("LabourListSize1",""+memberArrayList.size());
                AdapterMember adapterMember=new AdapterMember(SiteActivity.this,memberArrayList);
                binding.rvMembers.setAdapter(adapterMember);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAssociateList() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                associateArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelAssociate=ds.getValue(ModelUser.class);
                    if(modelAssociate.getSiteId()==siteId){
                        if(modelAssociate.getMemberBlock().equals("Active")){
                            associateArrayList.add(modelAssociate);
                        }
                    }


                }
                Log.e("AssociateArray",""+associateArrayList.size());


                adapterPowerDistribution=new AdapterPowerDistribution(SiteActivity.this,associateArrayList,itemClickListener);
                binding.rvPower.setAdapter(adapterPowerDistribution);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLabourList() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
                    labourList.add(modelLabour);

                }
                Log.e("LabourListSize1",""+labourList.size());
                getLabourskilledList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLabourskilledList() {
        Log.e("LabourListSize",""+labourList.size());
        if(labourList.size()>0){
            for(int i=0;i<labourList.size();i++){
                Log.e("Check",""+labourList.get(i).getType());
                if(labourList.get(i).getType().equals("Skilled")){
                    ModelLabour modelLabour=labourList.get(i);
                    Log.e("wages",""+modelLabour.getWages());
                    skilledLabourList.add(modelLabour);
                }else{
                    ModelLabour modelLabour=labourList.get(i);
                    unskilledLabourList.add(modelLabour);
                }
                Log.e("skilledLabour1",""+skilledLabourList.size());
//                AdapterLabour adapterLabourSkilled=new AdapterLabour(this,skilledLabourList);
//                binding.rvSkilledLabourList.setAdapter(adapterLabourSkilled);
//                AdapterLabour adapterLabourunskilled=new AdapterLabour(this,unskilledLabourList);
//                binding.rvUnskilledLabourList.setAdapter(adapterLabourunskilled);

            }
        }


    }

    private void getLabourUnskilledList() {
    }

    private void openAddMemberDialog(long siteId, long memberCount) {
        Log.e("OpenMemberDialog","show");
        final AlertDialog.Builder alert = new AlertDialog.Builder(SiteActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_add_member, null);
        et_name = (EditText) mView.findViewById(R.id.et_name);
        ImageView iv_close=mView.findViewById(R.id.iv_close);



        et_mobile_number = (EditText) mView.findViewById(R.id.et_mobile_number);
        final ImageView iv_invite = (ImageView) mView.findViewById(R.id.iv_invite);
        final TextView fl_know_more=(TextView) mView.findViewById(R.id.fl_know_more);
        final TextView spinner_designation = (TextView) mView.findViewById(R.id.spinner_designation);
        final LinearLayout ll_knowMore_forced_logout = (LinearLayout) mView.findViewById(R.id.ll_knowMore_forced_logout);
        CheckBox cb_attendance_management,cb_finance_manangement,cb_cash_management,cb_work_activity;
        cb_attendance_management=mView.findViewById(R.id.cb_attendance_management);
        cb_finance_manangement=mView.findViewById(R.id.cb_finance_manangement);
        cb_cash_management=mView.findViewById(R.id.cb_cash_management);
        cb_work_activity=mView.findViewById(R.id.cb_work_activity);
        if(memberCount<1){
            cb_attendance_management.setEnabled(false);
            cb_cash_management.setEnabled(false);
            cb_finance_manangement.setEnabled(false);
            attendanceManagement=true;
            financeManagement=true;
            cashManagement=true;
        }else{
            cb_attendance_management.setEnabled(false);
            cb_attendance_management.setChecked(false);
            cb_cash_management.setEnabled(false);
            cb_cash_management.setChecked(false);
            cb_finance_manangement.setEnabled(false);
            cb_finance_manangement.setChecked(false);
            attendanceManagement=false;
            financeManagement=false;
            cashManagement=false;
        }
        fl_know_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_knowMore_forced_logout.setVisibility(View.VISIBLE);
            }
        });
        cb_work_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SiteActivity.this, "Work Activity is mandatory for all associates", Toast.LENGTH_SHORT).show();
            }
        });
        cb_attendance_management.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                attendanceManagement=b;
            }
        });
        cb_cash_management.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cashManagement=b;
            }
        });
        cb_finance_manangement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                financeManagement=b;
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_knowMore_forced_logout.setVisibility(View.GONE);
            }
        });


        selectedDesignation=getString(R.string.associate);
        spinner_designation.setText(selectedDesignation);
//        SpinnerAdapter spinnerAdapter=new SpinnerAdapter();
//        spinner_designation.setAdapter(spinnerAdapter);
//        spinner_designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView , View view , int i , long l) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        iv_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( ContextCompat.checkSelfPermission(SiteActivity.this,
                        Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED)){
                    Uri uri = Uri.parse("content://contacts");
                    Intent intent = new Intent(Intent.ACTION_PICK, uri);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_CONTACT);

                }else{
                    ActivityCompat.requestPermissions(SiteActivity.this,  new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
                }

            }
        });

        Button btn_add_member = (Button) mView.findViewById(R.id.addMemberBtn);
        Button okbtn = (Button) mView.findViewById(R.id.okBtn);
        alert.setView(mView);
        TextView txt_skip=(TextView) mView.findViewById(R.id.txt_skip);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        if(selected_option==1){
            txt_skip.setVisibility(View.GONE);
        }else{
            txt_skip.setVisibility(View.VISIBLE);
        }
        RadioButton cb_force_yes,cb_force_no;
        cb_force_yes=mView.findViewById(R.id.cb_force_yes);
        cb_force_no=mView.findViewById(R.id.cb_force_no);

        cb_force_no.setChecked(true);
        cb_force_yes.setChecked(false);

        cb_force_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(cb_force_no.isChecked()){
                    cb_force_no.setChecked(true);
                    cb_force_yes.setChecked(false);
                    forceLogout=false;
                }

            }
        });
        cb_force_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(cb_force_yes.isChecked()){
                    cb_force_no.setChecked(false);
                    cb_force_yes.setChecked(true);
                    forceLogout=true;
                }
            }
        });


        if(selected_option==3){
            txt_skip.setVisibility(View.VISIBLE);
        }else{
            txt_skip.setVisibility(View.GONE);
        }
        Log.e("MemberCount1",""+memberCount);
        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("MemberCount",""+memberCount);
                if(memberCount==0){
                    androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(SiteActivity.this);
                    builder1.setTitle(getString(R.string.skip_title))
                            .setMessage(getString(R.string.skip_to_admin))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                alertDialog.dismiss();

//                                    startActivity(new Intent(timelineActivity.this,SplashActivity.class));
                            })
                            .setNegativeButton(R.string.no, (dialogInterface, i) ->
                                    dialogInterface.dismiss());
                    builder1.show();

                }else{
                    alertDialog.dismiss();
                }


            }
        });

        btn_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_name.getText().toString().trim();
                String mobile=et_mobile_number.getText().toString().trim();
                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(mobile)){
                    Toast.makeText(SiteActivity.this , "Information entered not complete" , Toast.LENGTH_SHORT).show();
                }else if(selectedDesignation.equals("Select Designation")){
                    Toast.makeText(SiteActivity.this , "Select Designation" , Toast.LENGTH_SHORT).show();

                }else if(et_mobile_number.getText().length()>10){
                    Toast.makeText(SiteActivity.this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                }else{
//                    saveInfo(name ,mobile,selectedDesignation,alertDialog);
                    progressDialog.show();
                    checkForMobilePresent(mobile,name,alertDialog,memberCount);

                }


            }
        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                startActivity(new Intent(SiteActivity.this,timelineActivity.class));
            }
        });



        alertDialog.show();


    }

    private void checkForMobilePresent(String mobile, String name, AlertDialog alertDialog, long memberCount) {
        Log.e("CheckMobile",String.valueOf(siteId));
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean register=true;
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.child("siteId").getValue(long.class).equals(siteId)){
                        if(ds.child("mobile").getValue(String.class).equals(mobile)){
                            register=false;
                        }
                    }

                }
                if(register){
                    if(memberCount>0){
                        attendanceManagement=false;
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SiteActivity.this);
                        builder.setTitle(getString(R.string.warning))
                                .setMessage(R.string.adding_more_than_one_associate)
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                                    generateInvitelink(mobile,name,alertDialog);
                                    dialogInterface.dismiss();
                                });

                        builder.show();
                    }else {
                        generateInvitelink(mobile, name, alertDialog);
                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(SiteActivity.this, getString(R.string.team_member_already_registered), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void updateToSite(Boolean forceLogout, Boolean picActivity, String mobile, String name, AlertDialog alertDialog) {
//        HashMap<String,Object> hashMap=new HashMap<>();
//        hashMap.put("forceOpt",forceLogout);
//        hashMap.put("workOpt",picActivity);
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                generateInvitelink(mobile,name,alertDialog);
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SiteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    void generateInvitelink(String mobile, String name, AlertDialog alertDialog){
        Intent data=getIntent();

//        String url="https://indianfarmer.tk/?mobile="+mobile+"&site_id="+site_id+"&admin="+admin;
        Log.e("Invited","Siteid"+siteId);
//        String url="https://example.com?mobile="+mobile+"&site_id="+siteId+"&admin="+firebaseAuth.getUid()+"&company="+companyName+"&memberName="+name;
        Log.e("CompanyName",companyName);
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://haajiri1.page.link/?mobile="+mobile+"&site_id="+siteId+"&admin="+firebaseAuth.getUid()+"&company="+companyName+"&memberName="+name))
                .setDomainUriPrefix("https://haajiri1.page.link")
                // Set parameters
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.skillzoomer_Attendance.com")
                                .build())
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.skillzoomer_Attendance.com")
                                .setAppStoreId("123456789")
                                .setMinimumVersion("1.0.1")
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("orkut")
                                .setMedium("social")
                                .setCampaign("example-promo")
                                .build())
                .setItunesConnectAnalyticsParameters(
                        new DynamicLink.ItunesConnectAnalyticsParameters.Builder()
                                .setProviderToken("123456")
                                .setCampaignToken("example-promo")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Welcome to हाज़िरी Register")
                                .setDescription("This link is for you to start your Collaboration with हाज़िरी Register")
                                .build())
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            progressDialog.dismiss();
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            inviteLink="https://haajiri1.page.link"+shortLink.getPath().toString();
                            alertDialog.dismiss();
                            // Toast.makeText(getApplicationContext(), inviteLink, Toast.LENGTH_LONG).show();
//                            System.out.println("+++++++++++++++++++++++++link==============");
//                            System.out.println(shortLink.getPath());
                            // Toast.makeText(InviteActivity.this,"site-"+site_id,Toast.LENGTH_SHORT).show();
                            addMembersToInvitedList(siteId,name,mobile,"Associate",firebaseAuth.getUid(),companyName,siteName,inviteLink);


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to invite.", Toast.LENGTH_LONG).show();
                            Log.e("Error",task.getException().getMessage());
                            // Error
                            // ...
                        }
//                        Intent i=new Intent(InviteActivity.this,DashboardActivity.class);
//                        startActivity(i);
//                        finish();
                    }
                });

    }

    private void saveInfo(String name , String mobile , String designation , AlertDialog alertDialog) {


        String timestamp1=""+System.currentTimeMillis();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("MemberType",designation);
        hashMap.put("MemberName",name);
        hashMap.put("MemberMobile",mobile);
        hashMap.put("MemberStatus","invited");
        hashMap.put("timestamp",timestamp1);
        Log.e("siteIdMember",""+siteId);
        hashMap.put("siteId",siteId);
        hashMap.put("siteName",siteName);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(mobile).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                addMembersToInvitedList(name,mobile,designation,firebaseAuth.getUid(), companyName, siteName, inviteLink);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SiteActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                result=false;
            }
        });

    }

    private void addMembersToInvitedList(long siteId, String name, String mobile, String designation, String uid, String companyName, String siteName, String inviteLink) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("MemberType",designation);
        hashMap.put("MemberName",name);
        hashMap.put("MemberMobile",mobile);
        hashMap.put("MemberStatus","invited");
        hashMap.put("Hruid",uid);
        hashMap.put("siteId", siteId);
        hashMap.put("siteName", siteName);
        hashMap.put("companyName", companyName);
        hashMap.put("forceOpt", forceLogout);
        hashMap.put("attendanceManagement", attendanceManagement);
        hashMap.put("cashManagement", cashManagement);
        hashMap.put("financeManagement", financeManagement);
        hashMap.put("workActivity", workActivity);
        hashMap.put("industryPosition",getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));
        hashMap.put("industryName",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""));
        hashMap.put("hrDesignation",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("designation",""));
        hashMap.put("hrDesignationHindi",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("designationHindi",""));

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("InvitedMembers");
        reference.child(mobile).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Hi"+"\b"+name+" "+"!I invite you on हाज़िरी Register as Team Member of "+companyName+" for my site "+siteName+
                        "\nThis is your link "+inviteLink+" .Download the app. Use your Mobile No"+mobile+" for Registration in app. Start the work";
                String sub = "Invite";
                System.out.println("++++++++++++++++++++body"+body);
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SiteActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                        result=false;
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
    @SuppressLint("Range")
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK && data.getData()!=null) {
                Uri uri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                Log.e("TAG", "ZZZ number : " + number +" , name : "+name);
                Log.e("Tag1",""+number.length());
                et_name.setText(name);
                if(number.startsWith("+"))
                {
                    if(number.length()==13)
                    {
                        String str_getMOBILE=number.substring(3);
                        et_mobile_number.setText(str_getMOBILE);
                    }
                    else if(number.length()==14)
                    {
                        String str_getMOBILE=number.substring(4);
                        et_mobile_number.setText(str_getMOBILE);
                    }


                }
                else
                {
                    et_mobile_number.setText(number);
                }


            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SiteActivity.this,timelineActivity.class));

    }
}
