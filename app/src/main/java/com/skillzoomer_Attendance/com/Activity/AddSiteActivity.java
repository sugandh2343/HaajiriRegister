package com.skillzoomer_Attendance.com.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAddSiteBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

public class AddSiteActivity extends AppCompatActivity {
    ActivityAddSiteBinding binding;
    LayoutToolbarBinding toolbarBinding;
    String country="",city="",sitename="";
    private long siteid=100;
    private ProgressDialog progressDialog;
    String userType,userMobile;
    FirebaseAuth firebaseAuth;
    Button btn;
    private final Calendar myCalendar = Calendar.getInstance();
    String startTime="09:0",endTime="18:30";
    String currentDate;
    Boolean forceLogout=true;
    Boolean picActivity=true;
    int selected_option;
    Boolean attendanceManagement=true,workActivity=true,cashManagement=true,financeManagement=true;

    EditText et_name,et_mobile_number;

    private static final int PICK_CONTACT=401;

    String inviteLink;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSiteBinding.inflate(getLayoutInflater());
        toolbarBinding = binding.toolbarLayout;
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);

        selected_option=sharedpreferences.getInt("workOption",0);
        firebaseAuth = FirebaseAuth.getInstance();
        MyApplication my = new MyApplication( );
        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));

        userType = sharedpreferences.getString("userDesignation" , "");
        userMobile = sharedpreferences.getString("userMobile" , "");
        Log.e("userMobile" , userMobile);
        toolbarBinding.heading.setText("Add Site");

        getSiteId();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        binding.etSiteCreatedDate.setText(currentDate);
        binding.cbForceYes.setChecked(false);
        binding.cbWorkYes.setChecked(false);
        binding.cbForceNo.setChecked(true);
        binding.cbWorkNo.setChecked(true);
        binding.cbWorkYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(binding.cbWorkYes.isChecked()){
                    binding.cbWorkYes.setChecked(true);
                    binding.cbWorkNo.setChecked(false);
                    picActivity=true;
                }
            }
        });
        binding.cbWorkNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(binding.cbWorkNo.isChecked()){
                    binding.cbWorkYes.setChecked(false);
                    binding.cbWorkNo.setChecked(true);
                    picActivity=false;
                }
            }
        });
        binding.cbForceYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(binding.cbForceYes.isChecked()){
                    binding.cbForceYes.setChecked(true);
                    binding.cbForceNo.setChecked(false);
                    forceLogout=true;
                }
            }
        });
        binding.cbForceNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(binding.cbForceNo.isChecked()){
                    binding.cbForceYes.setChecked(false);
                    binding.cbForceNo.setChecked(true);
                    forceLogout=false;
                }
            }
        });


        binding.btnAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country = binding.etAddress.getText().toString();
                city = binding.etCity.getText().toString();
                sitename = binding.etSiteName.getText().toString();
                if (TextUtils.isEmpty(city)) {
                    Toast.makeText(AddSiteActivity.this , "Enter City" , Toast.LENGTH_SHORT).show();
                } if (TextUtils.isEmpty(country)) {
                    Toast.makeText(AddSiteActivity.this , "Enter Site Address" , Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sitename)) {
                    Toast.makeText(AddSiteActivity.this , "Enter site name" , Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(binding.etCity.getText().toString())) {
                    Toast.makeText(AddSiteActivity.this , "Enter site short name" , Toast.LENGTH_SHORT).show();
                }else if (startTime.equals("")) {
                    Toast.makeText(AddSiteActivity.this , "Enter site start time" , Toast.LENGTH_SHORT).show();
                }else if (endTime.equals("")) {
                    Toast.makeText(AddSiteActivity.this , "Enter site end Time" , Toast.LENGTH_SHORT).show();
                } else {
                    showSiteUpdateDialog();


                }
            }
        });
        TimePickerDialog.OnTimeSetListener timeStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view , int hourOfDay , int minute) {
                myCalendar.set(Calendar.HOUR , hourOfDay);
                myCalendar.set(Calendar.MINUTE , minute);
                String myFormat = "hh:mm"; //In which you need put here
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat(myFormat , Locale.US);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startTime = sdf.format(myCalendar.getTime());
                }
                Log.e("Time" , "Start:" + startTime);
                updateLabel(binding.etStartTime);

            }
        };
        TimePickerDialog.OnTimeSetListener timeEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view , int hourOfDay , int minute) {
                myCalendar.set(Calendar.HOUR , hourOfDay);
                myCalendar.set(Calendar.MINUTE , minute);
                String myFormat = "hh:mm"; //In which you need put here
                SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat(myFormat , Locale.US);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    endTime = sdf.format(myCalendar.getTime());
                }
                Log.e("Time" , "Start:" + startTime);
                updateLabel(binding.etEndTime);

            }
        };


        binding.etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddSiteActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etStartTime.setText(selectedHour + ":" + selectedMinute);
                        endTime=selectedHour + ":" + selectedMinute;
                        Log.e("StartTime",endTime);
                        if(!TextUtils.isEmpty(startTime)){
                            calculateTime(binding.etStartTime.getText().toString(),binding.etEndTime.getText().toString());
                        }
                    }
                } , hour , minute , true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        binding.etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddSiteActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker , int selectedHour , int selectedMinute) {
                        binding.etEndTime.setText(selectedHour + ":" + selectedMinute);
                        startTime=selectedHour + ":" + selectedMinute;
                        Log.e("endTime",startTime);
                        if(!TextUtils.isEmpty(endTime)){
                            calculateTime(binding.etStartTime.getText().toString(),binding.etEndTime.getText().toString());
                        }

                    }
                } , hour , minute , true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
//


    }

    private void calculateTime(String startTime, String endTime) {
        SimpleDateFormat simpleDateFormat = null;
        Date date1=null,date2=null;
        int days,hours,min;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            simpleDateFormat = new SimpleDateFormat("hh:mm");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                date1 = simpleDateFormat.parse(startTime);
                date2 = simpleDateFormat.parse(endTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }


        long difference = Math.abs((int)(date2.getTime() - date1.getTime())) ;
        if(difference<0){

        }
        days = (int) (difference / (1000*60*60*24));
        hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);
        binding.etHours.setText(String.valueOf(hours));
        binding.etMin.setText(String.valueOf(min));
        Log.i("======= Hours"," :: "+hours);
    }


    private void updateLabel(EditText fromdateEt) {
        String myFormat = "hh:mm"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat, Locale.US);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fromdateEt.setText(sdf.format(myCalendar.getTime()));
        }
    }


    private void getSiteId() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&& snapshot.getChildrenCount()>0) {
                    siteid = ((snapshot.getChildrenCount()+1) * 100);
                }else{
                    siteid=100;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void addSite() {
        long siteIdIntent=siteid;

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteid)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    siteid=siteid+100;
                    addSite();
                }else{
                    String timestamp=""+System.currentTimeMillis();
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("siteId",siteid);
                    hashMap.put("siteName",sitename);
                    hashMap.put("siteCity",city);
                    hashMap.put("siteAddress",country);
                    hashMap.put("timestamp",timestamp);
                    hashMap.put("hrUid",firebaseAuth.getUid());
                    hashMap.put("industryPosition",getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));
                    hashMap.put("online",false);
                    hashMap.put("skilled",0);
                    hashMap.put("unskilled",0);
                    hashMap.put("time","12:00");
                    hashMap.put("startTime",startTime);
                    hashMap.put("endTime",endTime);
                    hashMap.put("siteCreatedDate",currentDate);
                    hashMap.put("forceLogout",false);
                    hashMap.put("cashReq",false);
                    hashMap.put("reqPendency",false);
                    hashMap.put("cashInHand",0);
                    hashMap.put("paymentSum",0);
                    hashMap.put("upToDatePAyment",0);
                    hashMap.put("memberCount",0);
                    hashMap.put("siteStatus","Active");
                    hashMap.put("memberOnline",0);
//        hashMap.put("forceOpt",forceLogout);
//        hashMap.put("workOpt",picActivity);
                    hashMap.put("memberStatus","Pending");
                    hashMap.put("attendanceEdit","false");
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteid)).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
//                        getSiteId(timestamp);
                                    progressDialog.dismiss();
                                    if(selected_option==1 || selected_option==3){

                                        showAddMemberDialog();



                                    }else{
                                        Toast.makeText(AddSiteActivity.this , "Site Registered Successfully" , Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(AddSiteActivity.this, timelineActivity.class);
                                        Log.e("SiteRegister",""+siteIdIntent);
                                        intent.putExtra("siteID", siteIdIntent);
                                        intent.putExtra("siteName",sitename);
                                        intent.putExtra("siteTimestamp",timestamp);
                                        startActivity(intent);
                                    }



                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddSiteActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void showAddMemberDialog() {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(AddSiteActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_add_member, null);

        et_name = (EditText) mView.findViewById(R.id.et_name);
        ImageView iv_close=mView.findViewById(R.id.iv_close);



        et_mobile_number = (EditText) mView.findViewById(R.id.et_mobile_number);
        final ImageView iv_invite = (ImageView) mView.findViewById(R.id.iv_invite);
        final TextView fl_know_more=(TextView) mView.findViewById(R.id.fl_know_more);
        final TextView spinner_designation = (TextView) mView.findViewById(R.id.spinner_designation);
        final TextView txt_first = (TextView) mView.findViewById(R.id.txt_first);
        final TextView txt_second = (TextView) mView.findViewById(R.id.txt_second);
        final LinearLayout ll_knowMore_forced_logout = (LinearLayout) mView.findViewById(R.id.ll_knowMore_forced_logout);
        final Spinner spinner_site= (Spinner) mView.findViewById(R.id.spinner_site);
        final LinearLayout ll_member= (LinearLayout) mView.findViewById(R.id.ll_member);
        CheckBox cb_attendance_management,cb_finance_manangement,cb_cash_management,cb_work_activity;
        cb_attendance_management=mView.findViewById(R.id.cb_attendance_management);
        cb_finance_manangement=mView.findViewById(R.id.cb_finance_manangement);
        cb_cash_management=mView.findViewById(R.id.cb_cash_management);
        cb_work_activity=mView.findViewById(R.id.cb_work_activity);


            txt_second.setVisibility(View.GONE);
            txt_first.setVisibility(View.VISIBLE);
            cb_attendance_management.setEnabled(true);
            cb_cash_management.setEnabled(true);
            cb_finance_manangement.setEnabled(true);
            cb_attendance_management.setChecked(true);
            cb_cash_management.setChecked(true);
            cb_finance_manangement.setChecked(true);
            attendanceManagement=true;
            financeManagement=true;
            cashManagement=true;






        fl_know_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinner_site.getSelectedItemPosition()>0){
                    ll_knowMore_forced_logout.setVisibility(View.VISIBLE);
                }

            }
        });
        cb_work_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddSiteActivity.this, "Work Activity is mandatory for all associates", Toast.LENGTH_SHORT).show();
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


//        selectedDesignation=getString(R.string.associate);
//        spinner_designation.setText(selectedDesignation);
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
                if( ContextCompat.checkSelfPermission(AddSiteActivity.this,
                        Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED)){
                    Uri uri = Uri.parse("content://contacts");
                    Intent intent = new Intent(Intent.ACTION_PICK, uri);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_CONTACT);

                }else{
                    ActivityCompat.requestPermissions(AddSiteActivity.this,  new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
                }

            }
        });

        Button btn_add_member = (Button) mView.findViewById(R.id.addMemberBtn);
        Button okbtn = (Button) mView.findViewById(R.id.okBtn);
        alert.setView(mView);
        TextView txt_skip=(TextView) mView.findViewById(R.id.txt_skip);
        final android.app.AlertDialog alertDialog = alert.create();
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
//        Log.e("MemberCount1",""+memberCount);
//        txt_skip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("MemberCount",""+memberCount);
//                if(memberCount==0){
//                    androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(AddSiteActivity.this);
//                    builder1.setTitle(getString(R.string.skip_title))
//                            .setMessage(getString(R.string.skip_to_admin))
//                            .setCancelable(false)
//                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
//                                dialogInterface.dismiss();
//                                alertDialog.dismiss();
//
////                                    startActivity(new Intent(AddSiteActivity.this,SplashActivity.class));
//                            })
//                            .setNegativeButton(R.string.no, (dialogInterface, i) ->
//                                    dialogInterface.dismiss());
//                    builder1.show();
//
//                }else{
//                    alertDialog.dismiss();
//                }
//
//
//            }
//        });

        btn_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_name.getText().toString().trim();
                String mobile=et_mobile_number.getText().toString().trim();
                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(mobile)){
                    Toast.makeText(AddSiteActivity.this , "Information entered not complete" , Toast.LENGTH_SHORT).show();
                }else if(et_mobile_number.getText().length()>10){
                    Toast.makeText(AddSiteActivity.this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                }else{
//                    saveInfo(name ,mobile,selectedDesignation,alertDialog);
                    progressDialog.setMessage(getString(R.string.please_wait));
                    progressDialog.show();
                    attendanceManagement=cb_attendance_management.isChecked();
                    cashManagement=cb_cash_management.isChecked();
                    financeManagement=cb_finance_manangement.isChecked();
                    checkForMobilePresent(mobile,name,alertDialog,0,siteid,
                            binding.etSiteName.getText().toString());

                }


            }
        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                startActivity(new Intent(AddSiteActivity.this,timelineActivity.class));
            }
        });



        alertDialog.show();

    }

    private void checkForMobilePresent(String mobile, String name, android.app.AlertDialog alertDialog, long memberCount, long siteId, String siteName) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean register=true;
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.child("siteId").getValue(long.class).equals(String.valueOf(siteId))){
                        if(ds.child("mobile").getValue(String.class).equals(mobile)){
                            register=false;
                        }
                    }

                }
                if(register){
                    generateInvitelink(mobile, name, alertDialog, siteId, siteName);


                }else{
                    progressDialog.dismiss();
                    Toast.makeText(AddSiteActivity.this, getString(R.string.team_member_already_registered), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateInvitelink(String mobile, String name, android.app.AlertDialog alertDialog, long siteId, String siteName) {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://haajiri1.page.link/?mobile="+mobile+"&site_id="
                        +siteId+"&admin="+firebaseAuth.getUid()+"&company="
                        +getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName","")+"&memberName="+name))
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
                            addMembersToInvitedList(siteId,name,mobile,"Associate",
                                    firebaseAuth.getUid(),
                                    getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName",""),
                                    siteName,inviteLink);


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
                        Toast.makeText(AddSiteActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void showSiteUpdateDialog() {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(AddSiteActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_site_verify, null);
        Button btn_ok=mView.findViewById(R.id.btn_ok);
        Button btn_update=mView.findViewById(R.id.btn_update);
        TextView txt_site_name,txt_short_name,txt_site_address,txt_start_time,txt_end_time,txt_working_hours;
        txt_site_name=mView.findViewById(R.id.txt_site_name);
        txt_short_name=mView.findViewById(R.id.txt_short_name);
        txt_site_address=mView.findViewById(R.id.txt_site_address);
        txt_start_time=mView.findViewById(R.id.txt_start_time);
        txt_end_time=mView.findViewById(R.id.txt_end_time);
        txt_working_hours=mView.findViewById(R.id.txt_working_hours);
        txt_site_name.setText(binding.etSiteName.getText().toString());
        txt_short_name.setText(binding.etCity.getText().toString());
        txt_site_address.setText(binding.etAddress.getText().toString());
        txt_start_time.setText(binding.etEndTime.getText().toString());
        txt_end_time.setText(binding.etStartTime.getText().toString());
        txt_working_hours.setText(binding.etHours.getText().toString()+" "+getString(R.string.hours_value)+ " "+binding.etMin.getText().toString()+" "+
                getString(R.string.min));
        alert.setView(mView);
        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                addSite();
                alertDialog.dismiss();
            }
        });


        alertDialog.show();
    }
}