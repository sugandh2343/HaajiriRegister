package com.skillzoomer_Attendance.com.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.razorpay.RazorpayException;
//import com.skillzoomer_Attendance.com.Adapter.AdapterSelfTimeLine;
import com.skillzoomer_Attendance.com.Adapter.AdapterTimeline;
import com.skillzoomer_Attendance.com.Model.ModelAssociateDetails;
import com.skillzoomer_Attendance.com.Model.ModelAttendance;
import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourPayment;
import com.skillzoomer_Attendance.com.Model.ModelUserIndustry;
import com.skillzoomer_Attendance.com.Utilities.LocaleHelper;
import com.skillzoomer_Attendance.com.Model.ModelAssociate;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Adapter.SliderAdapterTimeline;
import com.skillzoomer_Attendance.com.Model.SliderDataTimeline;
import com.skillzoomer_Attendance.com.databinding.ActivityTimelineBinding;
import com.smarteist.autoimageslider.SliderView;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONException;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class timelineActivity extends AppCompatActivity {
    ActivityTimelineBinding binding;
    private ProgressDialog progressDialog;
    private ArrayList<ModelSite> siteArrayList;
    private ArrayList<ModelSite> siteSpinnerList;
    private ArrayList<ModelSite> siteArrayList_opt3;
    String companyName;
    FirebaseAuth firebaseAuth;
    ModelUser modelUser;
    String uid;
    Session session;
    int selected_option;
    private androidx.appcompat.widget.Toolbar toolbar;

    private SharedPreferences.Editor editor;
    private ArrayList<ModelSite> siteSortedArrayList;
    private ArrayList<ModelUser> userArrayList;
    Context context;
    MyApplication my = new MyApplication();
    private ArrayList<SliderDataTimeline> sliderDataTimelines;
    private ArrayList<ModelUserIndustry> userIndustryArrayList;
    private String currentDate;
    private ArrayList<ModelLabour> labourList;
    private ArrayList<ModelAttendance> modelAttendances;
    HSSFWorkbook workbook = null;
    FileOutputStream fos = null;
    File file = null;
    String currentDate1;
    String currentDate12;
    String weeklyfromDate, weeklyToDate;
    private ArrayList<ModelDate> modelDateArrayList;
    private ArrayList<ModelDate> shortDateList;
    private Double siteLatitude = 0.0, siteLongitude = 0.0;
    private ArrayList<ModelAssociateDetails> associateLoginDetails;
    private ArrayList<ModelAssociateDetails> associateLogOutdetails;

    private ArrayList<ModelCompileStatus> ModelCompileStatusArrayList;

    private String timestamp;
    ProgressDialog pd;

    Boolean attendanceManagement=true,workActivity=true,cashManagement=true,financeManagement=true;

    EditText et_name,et_mobile_number;

    private static final int PICK_CONTACT=401;

    String inviteLink;

    Boolean forceLogout=false;




    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        SharedPreferences sharedpreferencesL = getSharedPreferences("Language", Context.MODE_PRIVATE);
        userIndustryArrayList = new ArrayList<>();
        associateLoginDetails = new ArrayList<>();
        associateLogOutdetails = new ArrayList<>();
        labourList = new ArrayList<>();
        ModelCompileStatusArrayList = new ArrayList<>();
        modelDateArrayList = new ArrayList<>();
        siteSpinnerList=new ArrayList<>();
        shortDateList = new ArrayList<>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        Date c = Calendar.getInstance().getTime();





        timestamp = "" + System.currentTimeMillis();


        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate1 = df.format(c);


        selected_option = sharedpreferences.getInt("workOption", 0);
        Log.e("SelectedOption", "" + selected_option);
        Log.e("Language", Locale.ENGLISH.getLanguage());

        siteSortedArrayList = new ArrayList<>();
        userArrayList = new ArrayList<>();
        sliderDataTimelines = new ArrayList<>();
        getSliderData();
        binding.btnAddIndustry.setVisibility(View.VISIBLE);

        binding.btnAddIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(timelineActivity.this, ActivityProfile.class));
            }
        });


        if (!sharedpreferences.getString("Language", "hi").equals("en")) {
            context = LocaleHelper.setLocale(timelineActivity.this, "hi");
            my.updateLanguage(timelineActivity.this, sharedpreferences.getString("Language", "hi"));
        } else {
            context = LocaleHelper.setLocale(timelineActivity.this, "en");
            my.updateLanguage(timelineActivity.this, sharedpreferences.getString("Language", "en"));
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        //  binding.txtAdmin.setText("Hii" + binding.etName.getText().toString());
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        siteArrayList = new ArrayList<>();
        siteArrayList_opt3 = new ArrayList<>();
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_navigationbar);
        binding.spinnerSelectIndustry.setVisibility(View.VISIBLE);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return false;
            }
        });

        progressDialog.show();
        uid = firebaseAuth.getUid();
        modelUser = new ModelUser();
        getIndustryList();
        getUserInfo();
        getFreeReportStatus();
        getWeeklyReportStatus();
//        if(sharedpreferences.getLong("industryCount",0)>1 && sharedpreferences.getLong("industryPosition",0)==0 ){
//            Log.e("IP",""+sharedpreferences.getLong("industryPosition",0));
//            binding.rlMain.setVisibility(View.GONE);
//            binding.llBottomLayout.setVisibility(View.GONE);
//            binding.llIndustry.setVisibility(View.VISIBLE);
//            getIndustryList();
//        }else{
//            binding.llIndustry.setVisibility(View.GONE);
//            binding.rlMain.setVisibility(View.VISIBLE);
//            binding.llBottomLayout.setVisibility(View.VISIBLE);
//        }
        if (selected_option == 1) {
//            getSiteList();
            binding.llWithAssociate.setVisibility(View.VISIBLE);
//            binding.llWithoutAssociate.setVisibility(View.GONE);

            binding.rupeeicon.setVisibility(View.VISIBLE);
            binding.llRequest.setVisibility(View.VISIBLE);
            binding.llBottomLayout.setWeightSum(4);
            binding.workActivity.setText(getString(R.string.master_data));
//            binding.txtForgotPassword.setText(R.string.click_on_site_name);

        } else if (selected_option == 2) {
            binding.llWithAssociate.setVisibility(View.GONE);
//            binding.llWithoutAssociate.setVisibility(View.VISIBLE);

            binding.llWorkAssociate.setVisibility(View.GONE);
            binding.viewWa.setVisibility(View.GONE);
            binding.bookingicon.setImageResource(R.drawable.ic_rupee);
            binding.llManpower.setVisibility(View.GONE);
            binding.llBottomLayout.setWeightSum(3);
            binding.workActivity.setText(getString(R.string.master_data));
//            binding.txtForgotPassword.setText(R.string.click_on_site_name);
//            getSiteListPending();
        } else if (selected_option == 3) {
//            binding.llWithoutAssociate.setVisibility(View.GONE);

        }

        binding.btnAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent sendIntent = new Intent("android.intent.action.MAIN");
//                sendIntent.putExtra("jid", "+917007630285" + "@s.whatsapp.net");
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "DREPLY");
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.setPackage("com.whatsapp");
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
                startActivity(new Intent(timelineActivity.this, AddSiteActivity.class));
            }
        });
        binding.llManpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(timelineActivity.this, ReportHome.class));
//                if(selected_option==2){
//                    Intent intent=new Intent(timelineActivity.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(timelineActivity.this,ReportHome.class));
//                }
//
            }
        });
        binding.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(timelineActivity.this, ActivityProfile.class));
            }
        });

        binding.llWorkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add Activity work here
                startActivity(new Intent(timelineActivity.this, ViewMasterDataSheet.class));
//                if((selected_option==2)||(selected_option==1)){
//                    startActivity(new Intent(timelineActivity.this,ViewMasterDataSheet.class));
//                }else{
//                    startActivity(new Intent(timelineActivity.this,AdminSiteActivity.class));
//                }

            }
        });
        binding.spinnerSelectIndustry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    editor.putLong("industryPosition", userIndustryArrayList.get(i).getIndustryPosition());
                    editor.putString("industryName", userIndustryArrayList.get(i).getIndustryName());
                    editor.putString("companyName", userIndustryArrayList.get(i).getCompanyName());
                    editor.apply();
                    editor.commit();
                    if (!userIndustryArrayList.get(i).getIndustryName().equals("Construction") && !sharedpreferences.getString("industryName", "").equals("Construction")) {
                        startActivity(new Intent(timelineActivity.this, TimelineOtherIndustry1.class));
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.llRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(timelineActivity.this, AdvancesHomeActivity.class);
                intent.putExtra("SiteSpinner", true);
                intent.putExtra("Activity", "ShowAttendance");
                startActivity(intent);
//                if(selected_option==1){
//                    Intent intent=new Intent(timelineActivity.this,AdvancesHomeActivity.class);
//                    intent.putExtra("SiteSpinner",true);
//                    startActivity(intent);
//                }else{
//                    startActivity(new Intent(timelineActivity.this,RequestListActivity.class));
//                }

            }
        });

//        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(timelineActivity.this);
//                builder.setTitle("Log Out")
//                        .setMessage("Are you sure you want to log out????")
//                        .setCancelable(true)
//                        .setPositiveButton("Yes", (dialogInterface, i) -> {
//                            progressDialog.show();
//                            firebaseAuth.signOut();
//                            editor.clear();
//                            editor.commit();
//                            startActivity(new Intent(timelineActivity.this,LoginActivity.class));
//
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                builder.show();
//            }
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            new MaterialShowcaseView.Builder(timelineActivity.this)
//                    .setTarget(binding.btnAddSite)
//                    .setGravity(2)
//                    .withRectangleShape(true)
//                    .setTargetTouchable(true)
//                    .setContentText("Click to add your First Site")// optional but starting animations immediately in onCreate can make them choppy
//                    .setContentTextColor(getColor(R.color.white))
//                    .setDismissTextColor(getColor(R.color.red))
//                    .setDismissStyle(Typeface.DEFAULT_BOLD)
//                    .singleUse("addSite")
//                    .show();

            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(200);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                config.setContentTextColor(getColor(R.color.white));
                config.setMaskColor(getColor(R.color.black));

            }
            if (getSharedPreferences("Tutorial", MODE_PRIVATE).getBoolean("timelineHr", true)) {

                binding.llWithAssociate.setVisibility(View.GONE);
                final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(timelineActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_welcome_tutorial, null);
                Button show_tutorial=mView.findViewById(R.id.show_tutorial);
                Button cancel_tutorial=mView.findViewById(R.id.cancel_tutorial);
                alert.setView(mView);

                final android.app.AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(true);
                show_tutorial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(timelineActivity.this, "timeline");

//                new MaterialShowcaseView.Builder(timelineActivity.this)
//                        .setTarget(binding.btnAddSite)
//                        .setGravity(2)
//                        .withRectangleShape(true)
//                        .setTargetTouchable(true)
//                        .setContentText(getString(R.string.content_tl1))// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(getColor(R.color.white))
//                        .setDismissTextColor(getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .singleUse("addSite")
//                        .setDismissText(getString(R.string.content_dismiss))
//                        .show();


                        sequence.setConfig(config);
                        sequence.singleUse("timeline");



                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(timelineActivity.this)
                                .setTarget(binding.btnAddSite)
                                .setGravity(Gravity.BOTTOM)
                                .withCircleShape()
                                .setTargetTouchable(false)
                                .setContentText(getString(R.string.content_tl1))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(timelineActivity.this)
                                .setTarget(binding.llManpower)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setTargetTouchable(false)
                                .setContentText(getString(R.string.content_tl2))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .setShapePadding(20)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(timelineActivity.this)
                                .setTarget(binding.llWorkActivity)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(20)
                                .setTargetTouchable(false)
                                .setDismissOnTouch(true)
                                .setContentText(getString(R.string.content_tl3))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .setDismissStyle(Typeface.DEFAULT_BOLD)

                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder(timelineActivity.this)
                                .setTarget(binding.llRequest)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(20)
                                .setTargetTouchable(false)
                                .setDismissOnTouch(true)
                                .setContentText(getString(R.string.content_tl4))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(getColor(R.color.white))
                                .setListener(new IShowcaseListener() {
                                    @Override
                                    public void onShowcaseDisplayed(MaterialShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseDismissed(MaterialShowcaseView showcaseView) {
                                        binding.llWithAssociate.setVisibility(View.VISIBLE);
                                        getSiteList();
                                    }
                                })

                                .build());



                        sequence.start();
                        alertDialog.dismiss();
                    }
                });

                cancel_tutorial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editorTutorial=getSharedPreferences("Tutorial",MODE_PRIVATE).edit();
                        editorTutorial.putBoolean("timelineHr",false);
                        editorTutorial.apply();
                        editorTutorial.commit();
                        getSiteList();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();



            }else{
                binding.llWithAssociate.setVisibility(View.VISIBLE);

                getSiteList();
            }

            binding.btnAddAssociate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddMemberDialog();
//                    Intent intent=new Intent(timelineActivity.this,SiteActivity.class);
//                    intent.putExtra("Activity","Add Associate");
//                    startActivity(intent);
                }
            });
            binding.btnSite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(timelineActivity.this,SiteActivity.class);
                    intent.putExtra("Activity","Site");
                    startActivity(intent);
                }
            });


        }
        
    }

    private void showAddMemberDialog() {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(timelineActivity.this);
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

        SiteSpinnerAdapter spinnerAdapter=new SiteSpinnerAdapter();
        spinner_site.setAdapter(spinnerAdapter);

        spinner_site.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    Log.e("MemberCount",""+siteSpinnerList.get(i).getMemberCount());
                    ll_member.setVisibility(View.VISIBLE);

                    if(siteSpinnerList.get(i).getMemberCount()<1){
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
                    }
                    else{
                        txt_first.setVisibility(View.GONE);
                        txt_second.setVisibility(View.VISIBLE);
                        cb_attendance_management.setEnabled(true);
                        cb_attendance_management.setChecked(false);
                        cb_cash_management.setEnabled(true);
                        cb_cash_management.setChecked(false);
                        cb_finance_manangement.setEnabled(true);
                        cb_finance_manangement.setChecked(false);
                        attendanceManagement=false;
                        financeManagement=false;
                        cashManagement=false;
                    }

                }else{
                    ll_member.setVisibility(View.GONE);
                    txt_first.setVisibility(View.GONE);
                    txt_second.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
                Toast.makeText(timelineActivity.this, "Work Activity is mandatory for all associates", Toast.LENGTH_SHORT).show();
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
                if( ContextCompat.checkSelfPermission(timelineActivity.this,
                        Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED)){
                    Uri uri = Uri.parse("content://contacts");
                    Intent intent = new Intent(Intent.ACTION_PICK, uri);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_CONTACT);

                }else{
                    ActivityCompat.requestPermissions(timelineActivity.this,  new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
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
//                    androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(timelineActivity.this);
//                    builder1.setTitle(getString(R.string.skip_title))
//                            .setMessage(getString(R.string.skip_to_admin))
//                            .setCancelable(false)
//                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
//                                dialogInterface.dismiss();
//                                alertDialog.dismiss();
//
////                                    startActivity(new Intent(timelineActivity.this,SplashActivity.class));
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
                    Toast.makeText(timelineActivity.this , "Information entered not complete" , Toast.LENGTH_SHORT).show();
                }else if(et_mobile_number.getText().length()>10){
                    Toast.makeText(timelineActivity.this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                }else{
//                    saveInfo(name ,mobile,selectedDesignation,alertDialog);
                    progressDialog.setMessage(getString(R.string.please_wait));
                    progressDialog.show();
                    attendanceManagement=cb_attendance_management.isChecked();
                    cashManagement=cb_cash_management.isChecked();
                    financeManagement=cb_finance_manangement.isChecked();
                    checkForMobilePresent(mobile,name,alertDialog,siteSpinnerList.get(spinner_site.getSelectedItemPosition()).getMemberCount(),siteSpinnerList.get(spinner_site.getSelectedItemPosition()).getSiteId(),
                            siteSpinnerList.get(spinner_site.getSelectedItemPosition()).getSiteName());

                }


            }
        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                startActivity(new Intent(timelineActivity.this,timelineActivity.class));
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
                    Toast.makeText(timelineActivity.this, getString(R.string.team_member_already_registered), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateInvitelink(String mobile, String name, android.app.AlertDialog alertDialog, long siteId, String siteName) {
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
                        Toast.makeText(timelineActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void getWeeklyReportStatus() {
        String timestamp = "" + System.currentTimeMillis();
        Log.e("Timestamp", timestamp);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
        reference.child("WeeklyReport").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("download").getValue(Boolean.class)) {
                    Log.e("WeeklyR", "True");

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, -7);
                    Date e = c.getTime();
                    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    currentDate12 = df.format(e);
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                    reference1.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String registerTimestamp = "";
                            String dateOfRegister = "";
                            if (snapshot.child("weeklyDate").getValue(String.class) == null) {
                                registerTimestamp = snapshot.child("timestamp").getValue(String.class);
                                dateOfRegister = snapshot.child("dateOfRegister").getValue(String.class);
                            } else {
                                registerTimestamp = snapshot.child("weeklyTimestamp").getValue(String.class);
                                dateOfRegister = snapshot.child("weeklyDate").getValue(String.class);
                            }
                            Log.e("getForcedLogoutStatus", registerTimestamp);
                            String timestamp = "" + System.currentTimeMillis();
                            Log.e("Timestamp", timestamp);
                            long difference = (long) ((Long.parseLong(timestamp)) - (Long.parseLong(registerTimestamp)));
                            long secondsInMilli = 1000;
                            long minutesInMilli = secondsInMilli * 60;
                            long hoursInMilli = minutesInMilli * 60;
                            long daysInMilli = hoursInMilli * 24;

                            long elapsedDays = difference / daysInMilli;

                            difference = difference % daysInMilli;

                            long elapsedHours = difference / hoursInMilli;
                            difference = difference % hoursInMilli;

                            long elapsedMinutes = difference / minutesInMilli;
                            difference = difference % minutesInMilli;
                            Log.e("WeeklyR", "E:::" + elapsedDays);
                            if (elapsedDays > 7) {
                                final AlertDialog.Builder alert = new AlertDialog.Builder(timelineActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.layout_free_report, null);
                                alert.setView(mView);

                                TextView text_view;
                                Button btn_download;
                                btn_download = mView.findViewById(R.id.btn_download);
                                text_view = mView.findViewById(R.id.text_view);
                                text_view.setText("Your Weekly associate details report  is Ready.Download it now?");
                                final AlertDialog alertDialog = alert.create();
                                alertDialog.setCanceledOnTouchOutside(true);
                                String finalDateOfRegister = dateOfRegister;
                                btn_download.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        progressDialog.show();

                                        if (snapshot.child("weeklyDate").getValue(String.class) == null) {
                                            weeklyfromDate = finalDateOfRegister;
                                            weeklyToDate = currentDate;
                                        } else {
                                            weeklyfromDate = snapshot.child("weeklyDate").getValue(String.class);
                                            weeklyToDate = currentDate;
                                        }

                                        modelDateArrayList = new ArrayList<>();
                                        shortDateList = new ArrayList<>();
                                        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
                                        Boolean f = true;
                                        Date fDate = null, tDate = null;
                                        Log.e("callFrom", weeklyfromDate);
                                        Log.e("callFrom", weeklyToDate);
                                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy");
                                        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("dd/MM/yyyy");
                                        Date siteCreated = null;
                                        java.text.SimpleDateFormat df1 = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                        Date c1 = Calendar.getInstance().getTime();

                                        Calendar c = Calendar.getInstance();
                                        Calendar c12 = Calendar.getInstance();
                                        Date date = null, date1 = null;
                                        String str = null, str1 = null;
                                        try {
                                            date = df1.parse(weeklyfromDate);// it's format should be same as inputPattern
                                            str = sdf1.format(date);
                                            date1 = df1.parse(weeklyToDate);// it's format should be same as inputPattern
                                            str1 = sdf1.format(date1);
                                            Log.e("Log ", "str " + str + "str1" + str1);
//                        c12.setTime(sdf.parse(siteCreatedDate));
                                        } catch (ParseException e) {
                                            Log.e("Exc", e.getMessage());
                                            e.printStackTrace();
                                        }
                                        try {
                                            c12.setTime(sdf.parse(weeklyToDate));
                                        } catch (ParseException ex) {
                                            ex.printStackTrace();
                                        }

                                        java.text.SimpleDateFormat df2 = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                        String currentDate11 = df2.format(c1);
                                        try {
                                            c.setTime(sdf.parse(weeklyfromDate));
                                        } catch (ParseException ex) {
                                            ex.printStackTrace();
                                            Log.e("Exception123", ex.getMessage());
                                        }
                                        try {
                                            fDate = dateFormat.parse(str);
                                            tDate = dateFormat.parse(str1);
                                            Log.e("Date1111", "Before" + tDate.toString());

                                            c12.add(Calendar.DATE, 1);


                                            tDate = c12.getTime();
                                            Log.e("Date1111", "" + tDate.toString());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            Log.e("Exception124", e.getMessage());
                                        }
                                        if (fDate == null || tDate == null) {
                                            Log.e("Exception123", "Error");
                                        } else {
                                            Date temp = fDate;
                                            Log.e("callTemp", "" + temp);

                                            int count = 0;
                                            while (temp.before(tDate)) {


                                                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                                java.text.SimpleDateFormat df12 = new java.text.SimpleDateFormat("dd/MM", Locale.US);
                                                String date12 = df.format(temp);
                                                String date123 = df1.format(temp);
                                                Log.e("ShreyaMamKaDate", date123);
                                                modelDateArrayList.add(new ModelDate(date12));
                                                shortDateList.add(new ModelDate(date123));
                                                Log.e("dateeeee1", modelDateArrayList.get(count).getDate());
                                                count++;
                                                Log.e("dateeee", date12);
                                                c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                                temp = c.getTime();
                                                Log.e("Temp", "" + count + ":" + temp);


                                            }
                                            DownloadExcelWeekly(modelDateArrayList);
                                            alertDialog.dismiss();


//                                            alertDialog.dismiss();
                                        }
                                    }
                                });

                                alertDialog.show();

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DownloadExcelWeekly(ArrayList<ModelDate> modelDateArrayList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    long siteId1 = modelSite.getSiteId();
                    siteLatitude = snapshot.child(String.valueOf(siteId1)).child("siteLatitude").getValue(double.class);

                    siteLongitude = snapshot.child(String.valueOf(siteId1)).child("siteLongitude").getValue(double.class);
                    String siteName1 = modelSite.getSiteName();
                    associateLoginDetails.clear();
                    associateLogOutdetails.clear();

                    for (int m = 0; m < modelDateArrayList.size(); m++) {
                        if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0) {
                            for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Attendance").child(modelDateArrayList.get(m).getDate()).getChildren()) {

                                ModelAssociateDetails modelAssociateDetails = ds1.getValue(ModelAssociateDetails.class);
                                Log.e("Model123", "" + (modelAssociateDetails.getStatus() == null));
                                Log.e("Model123", "" + (modelAssociateDetails.getStatus()));
                                modelAssociateDetails.setDate(modelDateArrayList.get(m).getDate());

                                if (siteLatitude > 0 && siteLongitude > 0) {
                                    modelAssociateDetails.setSiteLatitude(siteLatitude);
                                    modelAssociateDetails.setSiteLongitude(siteLongitude);
                                }
                                if (modelAssociateDetails.getStatus().equals("NormalLogin")) {
                                    associateLoginDetails.add(modelAssociateDetails);
                                } else {
                                    associateLogOutdetails.add(modelAssociateDetails);
                                }


                            }
                        }

                    }

                    showChats asyncTask = new showChats(siteName1, siteId1, associateLoginDetails, associateLogOutdetails);
                    asyncTask.execute();


                }
                String timestamp = "" + System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("weeklyTimestamp", timestamp);
                hashMap.put("weeklyDate", currentDate);
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                reference1.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void DownloadExcel1(ArrayList<ModelAssociateDetails> associateLoginDetails, ArrayList<ModelAssociateDetails> associateLogOutdetails, long siteId1, String siteName1) {
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
//        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        createHeaderRow1(sheet, associateLoginDetails, siteId1, siteName1);
        createDayBookData1(sheet, associateLoginDetails, associateLogOutdetails, siteId1, siteName1);
        createFooter1(sheet, associateLoginDetails, siteId1, siteName1);


        if (associateLoginDetails.size() > 0) {
            Log.e("Associate", "Login" + associateLoginDetails.size() + "Logout:" + associateLogOutdetails.size());
//            AdapterTeamMemberCard adapterTeamMemberCard = new AdapterTeamMemberCard(timelineActivity.this, associateLoginDetails, associateLogOutdetails, siteName1, siteId1);
//            binding.rvTmCard.setAdapter(adapterTeamMemberCard);
//            Log.e("Associate","Item"+binding.rvTmCard.getAdapter().getItemCount());
//            DownloadPdf(associateLoginDetails, associateLogOutdetails, siteId1, siteName1, binding.rvTmCard);
//            binding.rvTmCard.setLayoutManager(new LinearLayoutManager(timelineActivity.this, LinearLayoutManager.VERTICAL, false) {
//                @Override
//                public void onLayoutCompleted(RecyclerView.State state) {
//                    super.onLayoutCompleted(state);
//                    // TODO
//                    Log.e("DownloadingPdf", "PDF");
//                    Log.e("DownloadingPdf", ""+binding.rvTmCard.getAdapter().getItemCount());
//
//                }
//            });
        }


//        try {
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
////            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
////            Log.e("Directory",directory.getAbsolutePath());
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            String timestamp = "" + System.currentTimeMillis();
//            Log.e("StrPath", str_path);
//
////            fos = new FileOutputStream(file);
//            Log.e("FilePath", file.getAbsolutePath().toString());
//            str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            Log.e("StrPath", str_path);
//            file = new File(str_path, siteName1 + "" + "TMRep" + timestamp + ".xls");
//            fos = new FileOutputStream(file);
//            workbook.write(fos);
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        Log.e("DayBook","SiteID:"+siteId1);
//        Log.e("DayBook","Date\tReceived\tRFrom\tExpense\tExpenseRemark");
//
//        for(int i=0;i<modelDayBookClassArrayList.size();i++){
//           Log.e("DayBook",modelDayBookClassArrayList.get(i).getDate()+"\t"+modelDayBookClassArrayList.get(i).getRecAmt()+"\t"
//           +modelDayBookClassArrayList.get(i).getRec_from()+"\t"+modelDayBookClassArrayList.get(i).getExpAmt()+"\t"+modelDayBookClassArrayList.get(i).getExpRemark());
//        }


    }

    public class showChats extends AsyncTask<String, String, String> {

        private String siteName1;
        private long siteId1;
        private ArrayList<ModelAssociateDetails> associateLoginDetails;
        private ArrayList<ModelAssociateDetails> associateLogoutDetails;

        public showChats(String siteName1, long siteId1, ArrayList<ModelAssociateDetails> associateLoginDetails, ArrayList<ModelAssociateDetails> associateLogoutDetails) {
            this.siteName1 = siteName1;
            this.siteId1 = siteId1;
            this.associateLoginDetails = associateLoginDetails;
            this.associateLogoutDetails = associateLogoutDetails;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pd = new ProgressDialog(timelineActivity.this);
            pd.setTitle(getString(R.string.please_wait));
//             progressDialog.setMessage("Laoding Chats.....");
//             progressDialog.show();
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.setMessage("Downloading Report");
            pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            FileInputStream input_document = null;
            String timestamp = "" + System.currentTimeMillis();
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "Tm_report" + "" + siteName1 + timestamp + ".pdf";
            File pdfFile = new File(str_path);

            com.itextpdf.text.Font fontHeading = new com.itextpdf.text.Font();
            fontHeading.setStyle(com.itextpdf.text.Font.NORMAL);
            fontHeading.setSize(25);
            fontHeading.setColor(BaseColor.BLACK);
            fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));

            com.itextpdf.text.Font fontHeading1 = new com.itextpdf.text.Font();
            fontHeading1.setStyle(com.itextpdf.text.Font.BOLD);
            fontHeading1.setSize(16);
            fontHeading1.setColor(BaseColor.BLACK);
            fontHeading1.setFamily(String.valueOf(Paint.Align.CENTER));

            com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font();
            fontBold.setStyle(com.itextpdf.text.Font.BOLD);
            fontBold.setSize(18);
            fontBold.setColor(BaseColor.BLACK);
            fontBold.setFamily(String.valueOf(Paint.Align.CENTER));
//
            // Read workbook into HSSFWorkbook
//            HSSFWorkbook my_xls_workbook = null;
//            my_xls_workbook = workbook;
//            // Read worksheet into HSSFSheet
//            HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
//            // To iterate over the rows
//            my_worksheet.getRow(6);
//            Iterator<Row> rowIterator = my_worksheet.iterator();
            //We will create output PDF document objects at this point
            com.itextpdf.text.Document iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 0f);

//            iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
//            iText_xls_2_pdf.setPageCount(associateLoginDetails.size()+1);
            try {
                PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            } catch (DocumentException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();

            } catch (FileNotFoundException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();
            }
            iText_xls_2_pdf.open();

            PdfPTable my_table1 = new PdfPTable(8);
            my_table1.setTotalWidth(iText_xls_2_pdf.getPageSize().getWidth());

            PdfPCell table_cellhh1 = new PdfPCell();
            table_cellhh1.setColspan(2);
            table_cellhh1.setPhrase(new Phrase("Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""), fontHeading1));
            table_cellhh1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh1);

            PdfPCell table_cellhh2 = new PdfPCell();
            table_cellhh2.setColspan(2);
            table_cellhh2.setPhrase(new Phrase("Generated On: " + currentDate, fontHeading1));
            table_cellhh2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh2);

            PdfPCell table_cellhh3 = new PdfPCell();
            table_cellhh3.setColspan(2);
            table_cellhh3.setPhrase(new Phrase("Site Id: " + siteId1, fontHeading1));
            table_cellhh3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh3);

            PdfPCell table_cellhh4 = new PdfPCell();
            table_cellhh4.setColspan(2);
            table_cellhh4.setPhrase(new Phrase("Site Name: " + siteName1, fontHeading1));
            table_cellhh4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh4);

            PdfPCell table_cellhh5 = new PdfPCell();
            table_cellhh5.setColspan(2);
            table_cellhh5.setPhrase(new Phrase("Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", ""), fontHeading1));
            table_cellhh5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh5);

            PdfPCell table_cellhh6 = new PdfPCell();
            table_cellhh6.setColspan(6);
            table_cellhh6.setPhrase(new Phrase("Team Member Attendance Report", fontHeading1));
            table_cellhh6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh6);

//
//            String date_val = "Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName","")+
//                    "\n"+"Generated On: " +currentDate+
//                    "\n"+"Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName","")+"\n"+
//                    "Site Id: " + siteId1 +
//                    "\n Site Name: " + siteName1;;
            Paragraph p1 = new Paragraph("\n\n");
            p1.setAlignment(Paragraph.ALIGN_CENTER);
//
//            Paragraph p2 = new Paragraph("Workers Master Data Sheet\n");
//            p2.setAlignment(Paragraph.ALIGN_CENTER);
//
//            Paragraph p3 = new Paragraph("\n");
//            p3.setAlignment(Paragraph.ALIGN_CENTER);


            try {
                iText_xls_2_pdf.add(my_table1);
                iText_xls_2_pdf.add(p1);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            //we have two columns in the Excel sheet, so we create a PDF table with two columns
            //Note: There are ways to make this dynamic in nature, if you want to.
            PdfPTable my_table = new PdfPTable(9 + 4);
            my_table.setTotalWidth(iText_xls_2_pdf.getPageSize().getWidth());
            //We will use the object below to dynamically add new data to the table
            PdfPCell table_cell;
            PdfPCell table_cellh1 = new PdfPCell();
            table_cellh1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh1.setPhrase(new Phrase("Sr No", fontBold));
            table_cellh1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh1);

            PdfPCell table_cellh2 = new PdfPCell();
            table_cellh2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh2.setPhrase(new Phrase("Date", fontBold));
            table_cellh2.setColspan(3);
            table_cellh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh2);

            PdfPCell table_cellh3 = new PdfPCell();
            table_cellh3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh3.setPhrase(new Phrase("Name", fontBold));
            table_cellh3.setColspan(3);
            table_cellh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh3);

            PdfPCell table_cellh4 = new PdfPCell();
            table_cellh4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh4.setPhrase(new Phrase("Login Time", fontBold));
            table_cellh4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh4);

            PdfPCell table_cellh5 = new PdfPCell();
            table_cellh5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh5.setPhrase(new Phrase("Distance", fontBold));
            table_cellh5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh5);

            PdfPCell table_cellh6 = new PdfPCell();
            table_cellh6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh6.setPhrase(new Phrase("Log Out Time", fontBold));
            table_cellh6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh6);

            PdfPCell table_cellh7 = new PdfPCell();
            table_cellh7.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh7.setPhrase(new Phrase("Distance", fontBold));
            table_cellh7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh7);

            PdfPCell table_cellh8 = new PdfPCell();
            table_cellh8.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh8.setPhrase(new Phrase("Status", fontBold));
            table_cellh8.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh8);

            PdfPCell table_cellh9 = new PdfPCell();
            table_cellh9.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh9.setPhrase(new Phrase("Selfie With Site", fontBold));
            table_cellh9.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh9);


            for (int i = 0; i < associateLoginDetails.size(); i++) {
                PdfPCell table_cell1 = new PdfPCell();
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setPhrase(new Phrase(String.valueOf(i + 1), fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell1);

                PdfPCell table_cell2 = new PdfPCell();
                table_cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell2.setPhrase(new Phrase(associateLoginDetails.get(i).getDate(), fontHeading));

                table_cell2.setColspan(3);
                table_cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell2);

                PdfPCell table_cell3 = new PdfPCell();
                table_cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (associateLoginDetails.get(i).getName() != null && !associateLoginDetails.get(i).getName().equals("")) {
                    table_cell3.setPhrase(new Phrase(associateLoginDetails.get(i).getName(), fontHeading));
                    table_cell3.setColspan(3);
                    my_table.addCell(table_cell3);

                } else {
                    table_cell3.setPhrase(new Phrase("-", fontHeading));
                    table_cell3.setColspan(3);
                    my_table.addCell(table_cell3);
                }


                table_cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell4 = new PdfPCell();
                table_cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

                if (associateLoginDetails.get(i).getStatus().equals("NormalLogin")) {
                    table_cell4.setPhrase(new Phrase(associateLoginDetails.get(i).getTime(), fontHeading));
                    my_table.addCell(table_cell4);
                }

                table_cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell5 = new PdfPCell();
                table_cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (associateLoginDetails.get(i).getSiteLatitude() > 0 && associateLoginDetails.get(i).getSiteLongitude() > 0 &&
                        associateLoginDetails.get(i).getMemberLatitude() > 0 &&
                        associateLoginDetails.get(i).getMemberLatitude() > 0) {
                    float[] results = new float[1];
                    Location.distanceBetween(associateLoginDetails.get(i).getSiteLatitude(), associateLoginDetails.get(i).getSiteLongitude(), associateLoginDetails.get(i).getMemberLatitude()
                            , associateLoginDetails.get(i).getMemberLongitude(), results);
                    float distance = results[0];
                    int dis_display = (int) distance;
                    table_cell5.setPhrase(new Phrase(String.valueOf(dis_display) + ("m"), fontHeading));
                    my_table.addCell(table_cell5);


                } else {
                    table_cell5.setPhrase(new Phrase("NA", fontHeading));
                    my_table.addCell(table_cell5);
                }


                table_cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell6 = new PdfPCell();
                table_cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell7 = new PdfPCell();
                table_cell7.setHorizontalAlignment(Element.ALIGN_CENTER);

                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell8 = new PdfPCell();
                table_cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell8.setPhrase(new Phrase(associateLoginDetails.get(i).getDate(), fontHeading));
                table_cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);

                if (i < associateLogOutdetails.size()) {
                    if (associateLogOutdetails.get(i).getStatus() != null && !associateLogOutdetails.get(i).getStatus().equals("normalLogout")) {
                        table_cell8.setPhrase(new Phrase("FL", fontHeading));

                    } else {
                        table_cell8.setPhrase(new Phrase("", fontHeading));
                    }
                    if (i + 1 < associateLoginDetails.size()) {
//                        Log.e("Tieerror",associateLogOutdetails.get(i).getTimeStamp());
//                        Log.e("Tieerror",associateLoginDetails.get(i + 1).getTimeStamp());
                        if (associateLogOutdetails.get(i).getTimeStamp() != null && associateLoginDetails.get(i + 1).getTimeStamp() != null &&
                                Long.parseLong(associateLogOutdetails.get(i).getTimeStamp()) > Long.parseLong(associateLoginDetails.get(i + 1).getTimeStamp())) {
                            associateLogOutdetails.add(i, new ModelAssociateDetails());
                            table_cell6.setPhrase(new Phrase("-", fontHeading));
                            table_cell7.setPhrase(new Phrase("-", fontHeading));


                        } else {
                            table_cell6.setPhrase(new Phrase(associateLogOutdetails.get(i).getTime(), fontHeading));


                            if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 &&
                                    associateLogOutdetails.get(i).getMemberLatitude() > 0 &&
                                    associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                                float[] results = new float[1];
                                Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude()
                                        , associateLogOutdetails.get(i).getMemberLongitude(), results);
                                float distance = results[0];
                                int dis_display = (int) distance;
                                table_cell7.setPhrase(new Phrase(String.valueOf(dis_display) + ("m"), fontHeading));


                            } else {
                                table_cell7.setPhrase(new Phrase("NA", fontHeading));

                            }
                        }


                    } else {

                        table_cell6.setPhrase(new Phrase(associateLogOutdetails.get(i).getTime(), fontHeading));


                        if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 &&
                                associateLogOutdetails.get(i).getMemberLatitude() > 0 &&
                                associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                            float[] results = new float[1];
                            Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude()
                                    , associateLogOutdetails.get(i).getMemberLongitude(), results);
                            float distance = results[0];
                            int dis_display = (int) distance;
                            table_cell7.setPhrase(new Phrase(String.valueOf(dis_display) + ("m"), fontHeading));


                        } else {
                            table_cell7.setPhrase(new Phrase("NA", fontHeading));

                        }

                    }
                } else {
                    table_cell6.setPhrase(new Phrase("-", fontHeading));
                    table_cell7.setPhrase(new Phrase("-", fontHeading));
                    table_cell8.setPhrase(new Phrase("-", fontHeading));

                }


                my_table.addCell(table_cell6);
                my_table.addCell(table_cell7);
                my_table.addCell(table_cell8);


                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here

                    String imageUrl = associateLoginDetails.get(i).getProfile();
                    Image imageFromWeb = null;
                    try {
                        imageFromWeb = Image.getInstance(new URL(imageUrl));
                    } catch (BadElementException e) {
                        Log.e("IEcxc", "1" + e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("IEcxc", "1" + e.getMessage());
                        e.printStackTrace();
                    }
                    PdfPCell cellImageInTable = new PdfPCell(imageFromWeb, true);
                    my_table.addCell(cellImageInTable);


                }

            }


            try {
                iText_xls_2_pdf.add(my_table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            try {
                Drawable d = getResources().getDrawable(R.drawable.logo23);
                BitmapDrawable bitDw = ((BitmapDrawable) d);
                Bitmap bmp = bitDw.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.setAlignment(Element.ALIGN_CENTER);
                image.setWidthPercentage(50);
                image.setScaleToFitHeight(true);
                iText_xls_2_pdf.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String date_val3 = "To know us more visit https://skillzoomers.com/hajiri-register/ ";

//          Paragraph p4 = new Paragraph(date_val1);
//          p4.setAlignment(Paragraph.ALIGN_CENTER);


            Paragraph p7 = new Paragraph("\n");
            p7.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph p8 = new Paragraph(date_val3);
            p8.setAlignment(Paragraph.ALIGN_CENTER);

            try {

                iText_xls_2_pdf.add(p7);
                iText_xls_2_pdf.add(p8);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            iText_xls_2_pdf.close();
            //we created our pdf file..
//          try {
//
//              input_document.close(); //close xls
//          } catch (IOException e) {
//              e.printStackTrace();
//          }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(timelineActivity.this, "Downloaded", Toast.LENGTH_LONG);
            pd.dismiss();


        }
    }

//    private void DownloadPdf(ArrayList<ModelAssociateDetails> associateLoginDetails, ArrayList<ModelAssociateDetails> associateLogOutdetails, long siteId1, String siteName1, RecyclerView rvTmCard) {
//        Log.e("Binduing",""+rvTmCard.getAdapter().getItemCount());
////        Bitmap recycler_view_bm = getScreenshotFromRecyclerView(rvTmCard);
//        try {
//
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "TM_Rep_Free_" +
//                    siteName1 + timestamp + ".pdf";
//            File pdfFile = new File(str_path);
//
////            pdfFile.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(pdfFile);
//
//            PdfDocument document = new PdfDocument();
//            com.itextpdf.text.Document iText_xls_2_pdf = new Document();
//            iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
//            try {
//                PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
//            } catch (DocumentException e) {
//                Log.e("exception123", e.getMessage());
//                e.printStackTrace();
//
//            } catch (FileNotFoundException e) {
//                Log.e("exception123", e.getMessage());
//                e.printStackTrace();
//            }
//            iText_xls_2_pdf.open();
//            String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") + "\n" + "Generated On: " + currentDate + "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "") + "\n" + "Site Id: " + siteId1 + "\n Site Name: " + siteName1;
//            ;
//            Paragraph p1 = new Paragraph(date_val);
//            p1.setAlignment(Paragraph.ALIGN_CENTER);
//
//            Paragraph p2 = new Paragraph("Team Member Report");
//            p2.setAlignment(Paragraph.ALIGN_CENTER);
//
//            Paragraph p3 = new Paragraph("\n");
//            p3.setAlignment(Paragraph.ALIGN_CENTER);
//
//            try {
//                iText_xls_2_pdf.add(p1);
//                iText_xls_2_pdf.add(p2);
//                iText_xls_2_pdf.add(p3);
//            } catch (DocumentException e) {
//                e.printStackTrace();
//            }
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK);
//            paint.setStyle(Paint.Style.FILL);
//
//
//            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(recycler_view_bm.getWidth(), recycler_view_bm.getHeight(), 2).create();
//            PdfDocument.Page page = document.startPage(pageInfo);
//            recycler_view_bm.prepareToDraw();
//            Canvas c;
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(20);
//
//            c = page.getCanvas();
//            c.drawPaint(paint);
//
////            c.drawText(date_val, 10, 25, paint);
////            c.drawText(date_val,0,0,null);
//            c.drawBitmap(recycler_view_bm, 0, 0, null);
//            document.finishPage(page);
//            document.writeTo(fOut);
//            document.close();
////            Toast.makeText(this, "Report Generated Successfully", Toast.LENGTH_SHORT).show();
//
////            Snackbar snackbar = Snackbar
////                    .make(equipmentsRecordActivityLayout, "PDF generated successfully.", Snackbar.LENGTH_LONG)
////                    .setAction("Open", new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////                            openPDFRecord(pdfFile);
////                        }
////                    });
////
////            snackbar.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
//        RecyclerView.Adapter adapter = view.getAdapter();
//        Bitmap bigBitmap = null;
//        if (adapter != null) {
//            Log.e("Size12323234213424", "" + adapter.getItemCount());
//            int size = adapter.getItemCount();
//            int height = 0;
//            Paint paint = new Paint();
//            int iHeight = 0;
//            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//
//            // Use 1/8th of the available memory for this memory cache.
//            final int cacheSize = maxMemory / 8;
//            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
//            for (int i = 0; i < size; i++) {
//
//                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
//                adapter.onBindViewHolder(holder, i);
////                Picasso.get().load(labourList.get(i).getProfile()).
////                        resize(400,400).centerCrop()
////                        .placeholder(R.drawable.profile).into((ImageView) holder.itemView.findViewById(R.id.img_profile));
//                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
//                holder.itemView.setDrawingCacheEnabled(true);
//                holder.itemView.buildDrawingCache();
//                Bitmap drawingCache = holder.itemView.getDrawingCache();
//
//                if (drawingCache != null) {
//
//                    bitmaCache.put(String.valueOf(i), drawingCache);
//                }
//
//
//
//                height += holder.itemView.getMeasuredHeight();
//                Log.e("ASsociate1","H"+holder.itemView.getMeasuredHeight());
//                if (i % 2 == 0 && i + 2 < size) {
//                    binding.rvTmCard.scrollToPosition(i + 2);
//                }
//
//            }
//            Log.e("Associate","W"+view.getMeasuredWidth());
//            Log.e("Associate","H"+height);
//
//            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
//            Canvas bigCanvas = new Canvas(bigBitmap);
//            bigCanvas.drawColor(Color.WHITE);
//
//
//            for (int i = 0; i < size; i++) {
//
//                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
//                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
//                iHeight += bitmap.getHeight();
//                bitmap.recycle();
//            }
//
//        }
//        return bigBitmap;
//    }

    private void createDayBookData1(Sheet sheet, ArrayList<ModelAssociateDetails> associateLoginDetails, ArrayList<ModelAssociateDetails> associateLogOutdetails, long siteId1, String siteName1) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            font.setColor((short) getColor(R.color.black));
        }
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        Log.e("AssLogDet", "" + associateLoginDetails.size());
        Log.e("AssLogDet", "" + associateLogOutdetails.size());
        for (int i = 0; i < associateLoginDetails.size(); i++) {

            Log.e("AssLogDate", associateLoginDetails.get(i).getDate());
            int j = i;
            Row row = sheet.createRow(i + 7);
            row.setHeightInPoints(25);
            Cell cellSrNo = row.createCell(0);
            cellSrNo.setCellStyle(cellStyle);
            cellSrNo.setCellValue(String.valueOf(i + 1));
            Cell cellDate = row.createCell(1);
            cellDate.setCellStyle(cellStyle);
            cellDate.setCellValue(associateLoginDetails.get(i).getDate());


            Cell cellSrNo1 = row.createCell(2);
            cellSrNo1.setCellStyle(cellStyle);
            if (associateLoginDetails.get(i).getName() != null && !associateLoginDetails.get(i).getName().equals("")) {
                cellSrNo1.setCellValue(associateLoginDetails.get(i).getName());
            } else {
                cellSrNo1.setCellValue("-");
            }

            Cell cellSrNo2 = row.createCell(3);
            cellSrNo2.setCellStyle(cellStyle);
//            cellSrNo2.setCellValue(modelDayBookClassArrayList.get(i).getRec_from());
            if (associateLoginDetails.get(i).getStatus().equals("NormalLogin")) {
                cellSrNo2.setCellValue(associateLoginDetails.get(i).getTime());
//                float[] results = new float[1];
//                Location.distanceBetween(associateDetailsArrayList.get(i).getSiteLatitude(), associateDetailsArrayList.get(i).getSiteLongitude(), associateDetailsArrayList.get(i).getMemberLatitude()
//                        , associateDetailsArrayList.get(i).getMemberLongitude(), results);
//                float distance = results[0];
//                int dis_display = (int) distance;
//
//                if (associateDetailsArrayList.get(holder.getAdapterPosition()).getSiteLongitude() > 0 && associateDetailsArrayList.get(holder.getAdapterPosition()).getMemberLongitude() > 0) {
//                    holder.txt_distance.setVisibility(View.VISIBLE);
//                    holder.txt_distance.setText(dis_display + ("m"));
//                } else {
//                    holder.txt_distance.setText("NA");
//                }

            }
            Cell cellSrNo3 = row.createCell(4);
            cellSrNo3.setCellStyle(cellStyle);
            if (associateLoginDetails.get(i).getSiteLatitude() > 0 && associateLoginDetails.get(i).getSiteLongitude() > 0 && associateLoginDetails.get(i).getMemberLatitude() > 0 && associateLoginDetails.get(i).getMemberLatitude() > 0) {
                float[] results = new float[1];
                Location.distanceBetween(associateLoginDetails.get(i).getSiteLatitude(), associateLoginDetails.get(i).getSiteLongitude(), associateLoginDetails.get(i).getMemberLatitude(), associateLoginDetails.get(i).getMemberLongitude(), results);
                float distance = results[0];
                int dis_display = (int) distance;
                cellSrNo3.setCellValue(String.valueOf(dis_display) + ("m"));


            } else {
                cellSrNo3.setCellValue("NA");
            }
            Cell cellSrNo4 = row.createCell(5);
            cellSrNo4.setCellStyle(cellStyle);
//            cellSrNo4.setCellValue(modelDayBookClassArrayList.get(i).getExpRemark());
//            if(associateLoginDetails.get(i).getSiteLatitude()>0 &&associateLoginDetails.get(i).getSiteLongitude()>0 &&
//                    associateLoginDetails.get(i).getMemberLatitude()>0 &&
//                    associateLoginDetails.get(i).getMemberLatitude()>0 ){
//                float[] results = new float[1];
//                Location.distanceBetween(associateLoginDetails.get(i).getSiteLatitude(), associateLoginDetails.get(i).getSiteLongitude(), associateDetailsArrayList.get(i).getMemberLatitude()
//                        , associateLoginDetails.get(i).getMemberLongitude(), results);
//                float distance = results[0];
//                int dis_display = (int) distance;
//                cellSrNo4.setCellValue(String.valueOf(dis_display) + ("m"));
//
//
//
//
//            }else{
//                cellSrNo4.setCellValue("NA");
//            }
            Cell cellSrNo5 = row.createCell(6);
            cellSrNo5.setCellStyle(cellStyle);
            if (i < associateLogOutdetails.size()) {
                if (i + 1 < associateLoginDetails.size()) {
                    if (Long.parseLong(associateLogOutdetails.get(i).getTimeStamp()) > Long.parseLong(associateLoginDetails.get(i + 1).getTimeStamp())) {
                        associateLogOutdetails.add(i, new ModelAssociateDetails());
                        cellSrNo4.setCellValue("-");
                        cellSrNo5.setCellValue("-");
                    } else {
                        cellSrNo4.setCellValue(associateLogOutdetails.get(i).getTime());

                        if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 && associateLogOutdetails.get(i).getMemberLatitude() > 0 && associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                            float[] results = new float[1];
                            Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude(), associateLogOutdetails.get(i).getMemberLongitude(), results);
                            float distance = results[0];
                            int dis_display = (int) distance;
                            cellSrNo5.setCellValue(String.valueOf(dis_display) + ("m"));


                        } else {
                            cellSrNo5.setCellValue("NA");
                        }
                    }


                } else {
                    cellSrNo4.setCellValue(associateLogOutdetails.get(i).getTime());

                    if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 && associateLogOutdetails.get(i).getMemberLatitude() > 0 && associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                        float[] results = new float[1];
                        Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude(), associateLogOutdetails.get(i).getMemberLongitude(), results);
                        float distance = results[0];
                        int dis_display = (int) distance;
                        cellSrNo5.setCellValue(String.valueOf(dis_display) + ("m"));


                    } else {
                        cellSrNo5.setCellValue("NA");
                    }

                }
            } else {
                cellSrNo4.setCellValue("-");
                cellSrNo5.setCellValue("-");
            }


        }

    }

    private void createFooter1(Sheet sheet, ArrayList<ModelAssociateDetails> associateDetailsArrayList, long siteId1, String siteName1) {
        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
        Font fontPresent = sheet.getWorkbook().createFont();
        fontPresent.setColor(HSSFColor.BLACK.index);
        fontPresent.setFontHeightInPoints((short) 12);
        cellStylePresent.setFont(fontPresent);
        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
        cellStylePresent.setBorderBottom((short) 1);
        cellStylePresent.setBorderTop((short) 1);
        cellStylePresent.setBorderLeft((short) 1);
        cellStylePresent.setBorderRight((short) 1);
        cellStylePresent.setFont(fontPresent);

        CellStyle cellStyle2 = sheet.getWorkbook().createCellStyle();
        Font font2 = sheet.getWorkbook().createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setBorderBottom((short) 0);
        cellStyle2.setBorderTop((short) 0);
        cellStyle2.setBorderLeft((short) 0);
        cellStyle2.setBorderRight((short) 0);
        cellStyle2.setWrapText(true);
        Date c = Calendar.getInstance().getTime();

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        sheet.createRow(associateDetailsArrayList.size() + 8);


        Row row = sheet.createRow(associateDetailsArrayList.size() + 10);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle2);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Generated by: " + " " + "Hajiri Register";
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(associateDetailsArrayList.size() + 10, associateDetailsArrayList.size() + 12, 0, 6);
        sheet.addMergedRegion(cellMerge);

        String date_val1 = "Powered by: " + " " + "Skill Zoomers";

        Row row1 = sheet.createRow(associateDetailsArrayList.size() + 14);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue(date_val1);

        CellRangeAddress cellMerge1 = new CellRangeAddress(associateDetailsArrayList.size() + 14, associateDetailsArrayList.size() + 16, 0, 6);
        sheet.addMergedRegion(cellMerge1);

        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ " + " " + "Skill Zoomers";

        Row row2 = sheet.createRow(associateDetailsArrayList.size() + 18);
        Cell cellHeading1 = row2.createCell(0);

        cellHeading1.setCellStyle(cellStyle2);
        cellHeading1.setCellValue(date_val2);

        CellRangeAddress cellMerge2 = new CellRangeAddress(associateDetailsArrayList.size() + 18, associateDetailsArrayList.size() + 20, 0, 6);
        sheet.addMergedRegion(cellMerge2);
    }

    private void createHeaderRow1(Sheet sheet, ArrayList<ModelAssociateDetails> associateLoginDetails, long siteId1, String siteName1) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);

        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        Font font1 = sheet.getWorkbook().createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font1.setFontHeightInPoints((short) 12);
        cellStyle1.setFont(font);
        cellStyle1.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle1.setBorderBottom((short) 0);
        cellStyle1.setBorderTop((short) 0);
        cellStyle1.setBorderLeft((short) 0);
        cellStyle1.setBorderRight((short) 0);
        cellStyle1.setWrapText(true);

        CellStyle cellStyle2 = sheet.getWorkbook().createCellStyle();
        Font font2 = sheet.getWorkbook().createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setBorderBottom((short) 0);
        cellStyle2.setBorderTop((short) 0);
        cellStyle2.setBorderLeft((short) 0);
        cellStyle2.setBorderRight((short) 0);
        cellStyle2.setWrapText(true);
        Date c = Calendar.getInstance().getTime();

        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);


        Row row = sheet.createRow(0);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle1);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") + "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "");

        cellFullName.setCellValue(date_val);
        int size = 0;
        if (modelDateArrayList.size() + 5 < 8) {
            size = 8;
        } else {
            size = modelDateArrayList.size() + 5;
        }

        CellRangeAddress cellMerge = new CellRangeAddress(0, 3, 0, 6);
        sheet.addMergedRegion(cellMerge);

        Cell cellFullName1 = row.createCell(7);
        cellFullName1.setCellStyle(cellStyle1);
        String date_val1 = "Generated On: " + currentDate + "\n" + "Site Id: " + siteId1 + "\t\t\t\t\t Site Name: " + siteName1;
        cellFullName1.setCellValue(date_val1);
        int size1 = 0;
        if (modelDateArrayList.size() + 4 < 10) {
            size1 = 12;
        } else {
            size1 = modelDateArrayList.size() + 4;
        }

        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 3, 7, size1);
        sheet.addMergedRegion(cellMerge1);


        Row row1 = sheet.createRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("Team Member Report");

        CellRangeAddress cellMerge2 = new CellRangeAddress(4, 5, 0, 7);
        sheet.addMergedRegion(cellMerge2);

        Row rowValues = sheet.createRow(6);
        rowValues.setHeightInPoints(30);
        Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        Cell cellDate = rowValues.createCell(1);
        cellDate.setCellStyle(cellStyle);
        cellDate.setCellValue("Date");
        Cell cellSrNo1 = rowValues.createCell(2);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Name");
        Cell cellSrNo2 = rowValues.createCell(3);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Login");
        Cell cellSrNo3 = rowValues.createCell(4);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Distance");
        Cell cellSrNo4 = rowValues.createCell(5);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Logout");
        Cell cellSrNo5 = rowValues.createCell(6);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("Distance");
    }

    private void getFreeReportStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
        reference.child("FreeReport").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("download").getValue(Boolean.class)) {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, -1);
                    Date e = c.getTime();
                    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    currentDate = df.format(e);


                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                    reference1.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Log.e("DailyDate",snapshot.child("dailyDate").getValue(String.class));
//                            Log.e("DailyDate",currentDate1);
//                            Log.e("DailyDate",""+(snapshot.child("dailyDate").getValue(String.class)==null));
//                            Log.e("DailyDate",""+(!snapshot.child("dailyDate").equals(currentDate1)));
//                            Log.e("DailyDate",""+snapshot.child("dailyDate").getValue(String.class).equals(currentDate1));
                            if (snapshot.child("dailyDate").getValue(String.class) == null || !snapshot.child("dailyDate").getValue(String.class).equals(currentDate1)) {
//                                if (1==1) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            labourList.clear();
                                            ModelCompileStatusArrayList.clear();
                                            ModelSite modelSite = ds.getValue(ModelSite.class);
                                            long siteId1 = modelSite.getSiteId();
                                            String siteName1 = modelSite.getSiteName();
                                            long countChild = snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).getChildrenCount();
                                            if (countChild > 0) {
                                                final AlertDialog.Builder alert = new AlertDialog.Builder(timelineActivity.this);
                                                View mView = getLayoutInflater().inflate(R.layout.layout_free_report, null);
                                                alert.setView(mView);


                                                Button btn_download;
                                                btn_download = mView.findViewById(R.id.btn_download);
                                                final AlertDialog alertDialog = alert.create();
                                                alertDialog.setCanceledOnTouchOutside(true);
                                                btn_download.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        progressDialog.setMessage("Downloading");
                                                        progressDialog.show();
                                                        Log.e("Progress", "" + progressDialog.isShowing());
                                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()) {
                                                            ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
                                                            labourList.add(modelLabour);
                                                            if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).child(modelLabour.getLabourId()).getChildrenCount() > 0) {
                                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount() > 0) {
                                                                    int countLabour = 0;
                                                                    int AmtSum = 0;
                                                                    String amount;
                                                                    for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildren()) {
                                                                        ModelLabourPayment labourPayment = ds2.getValue(ModelLabourPayment.class);
                                                                        if (labourPayment.getLabourId().equals(modelLabour.getLabourId())) {
                                                                            amount = snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
                                                                            AmtSum = AmtSum + Integer.parseInt(amount);
                                                                        }

                                                                    }
                                                                    ModelCompileStatus modelCompileStatus = new ModelCompileStatus(currentDate, modelLabour.getLabourId(), "P", modelLabour.getType(), String.valueOf(AmtSum));
                                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                                } else {
                                                                    ModelCompileStatus modelCompileStatus = new ModelCompileStatus(currentDate, modelLabour.getLabourId(), "P", modelLabour.getType(), "0");
                                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                                }

                                                            } else {
                                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount() > 0) {
                                                                    int countLabour = 0;
                                                                    int AmtSum = 0;
                                                                    String amount;
                                                                    for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildren()) {
                                                                        ModelLabourPayment labourPayment = ds2.getValue(ModelLabourPayment.class);
                                                                        if (labourPayment.getLabourId().equals(modelLabour.getLabourId())) {
                                                                            amount = snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
                                                                            AmtSum = AmtSum + Integer.parseInt(amount);
                                                                        }

                                                                    }
                                                                    ModelCompileStatus modelCompileStatus = new ModelCompileStatus(currentDate, modelLabour.getLabourId(), "A", modelLabour.getType(), String.valueOf(AmtSum));
                                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                                } else {
                                                                    ModelCompileStatus modelCompileStatus = new ModelCompileStatus(currentDate, modelLabour.getLabourId(), "A", modelLabour.getType(), "0");
                                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                                }
                                                            }

                                                        }
                                                        DownloadExcel(labourList, null, ModelCompileStatusArrayList, labourList, null, siteId1, siteName1, alertDialog);
                                                        alertDialog.dismiss();

                                                    }
                                                });
                                                alertDialog.show();


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DownloadExcel(ArrayList<ModelLabour> labourList, Object o, ArrayList<ModelCompileStatus> modelCompileStatusArrayList, ArrayList<ModelLabour> labourList1, Object o1, long siteId1, String siteName1, AlertDialog alertDialog) {
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
        createHeaderRow(sheet, null, null, siteId1, siteName1);
        createLAbourData(sheet, labourList);
        createAttendanceData(sheet, labourList, null, ModelCompileStatusArrayList);
        createPayment(sheet, labourList, null, ModelCompileStatusArrayList);
        createFooter(sheet, labourList, null, ModelCompileStatusArrayList);


        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String timestamp = "" + System.currentTimeMillis();
            Log.e("StrPath", str_path);
            file = new File(str_path, "HajiriRegister_" + "" + "FreeCom_rep" + timestamp + ".xls");

//            fos = new FileOutputStream(file);
            Log.e("FilePath", file.getAbsolutePath().toString());


//                Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//                createOrderId.start();
            str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            Log.e("StrPath", str_path);
            file = new File(str_path, "HajiriRegister_" + "" + "CompiledReoprt_" + timestamp + ".xls");

//                try {
//                    fos = new FileOutputStream(file);
//                    workbook.write(fos);
//                    fos.flush();
//                    fos.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Log.e("FilePath", file.getAbsolutePath().toString());
            Log.e("Exced", "" + labourList.size());
            Log.e("Exced", "" + modelCompileStatusArrayList.size());


            DownloadPdf(file, fos, workbook, siteId1, siteName1, labourList, null, modelCompileStatusArrayList, labourList, null, siteId1, siteName1, alertDialog);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exced", e.getMessage());
        } finally {

        }
    }

    private void ExcelToPdf(File file, FileOutputStream fos, HSSFWorkbook workbook, long siteId1, String siteName1, AlertDialog alertDialog) {
        FileInputStream input_document = null;
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "HajiriRegister_" + "" + "CompiledReoprt_" + timestamp + ".pdf";
        File pdfFile = new File(str_path);
//      try {
//          input_document = new FileInputStream(file);
//      } catch (FileNotFoundException e) {
//          e.printStackTrace();
//      }
        // Read workbook into HSSFWorkbook
        HSSFWorkbook my_xls_workbook = null;
        my_xls_workbook = workbook;
        // Read worksheet into HSSFSheet
        HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
        // To iterate over the rows
        my_worksheet.getRow(6);
        Iterator<Row> rowIterator = my_worksheet.iterator();
        //We will create output PDF document objects at this point
        com.itextpdf.text.Document iText_xls_2_pdf = new Document();

        iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
        try {
            PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
        } catch (DocumentException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();
        }
        iText_xls_2_pdf.open();

        String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") + "\n" + "Generated On: " + currentDate + "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "") + "\n" + "Site Id: " + siteId1 + "\n Site Name: " + siteName1;
        ;
        Paragraph p1 = new Paragraph(date_val);
        p1.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p2 = new Paragraph("Workers Compile Sheet\n");
        p2.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p3 = new Paragraph("\n");
        p3.setAlignment(Paragraph.ALIGN_CENTER);

        try {
            iText_xls_2_pdf.add(p1);
            iText_xls_2_pdf.add(p2);
            iText_xls_2_pdf.add(p3);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //we have two columns in the Excel sheet, so we create a PDF table with two columns
        //Note: There are ways to make this dynamic in nature, if you want to.
        PdfPTable my_table = new PdfPTable(7);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;

        //Loop through rows.


        Row row = rowIterator.next();
        Log.e("ValueI", "" + labourList.size());
        for (int i = 0; i < labourList.size() * 2 + 1; i++) {
            Log.e("ValueI123", "" + i);
            Row row1 = my_worksheet.getRow(i + 6);

            Cell cell1 = row1.getCell(2);

            Iterator<Cell> cellIterator = row1.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); //Fetch CELL

                Log.e("CellType", "" + cell.getCellType());

                switch (cell.getCellType()) { //Identify CELL type
                    //you need to add more code here based on
                    //your requirement / transformations


                    case Cell.CELL_TYPE_STRING:
                        //Push the data from Excel to PDF Cell
                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                        //feel free to move the code below to suit to your needs
                        my_table.addCell(table_cell);
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        table_cell = new PdfPCell(new Phrase(String.valueOf(cell.getNumericCellValue())));
                        my_table.addCell(table_cell);
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        Log.e("Blank", "Found");
                }
                //next line
            }
        }
//        Row row2=my_worksheet.getRow(labourList.size()*2 + 8);
//        Iterator<Cell> cellIterator = row2.cellIterator();
//        while (cellIterator.hasNext()) {
//            Cell cell = cellIterator.next(); //Fetch CELL
//
//            switch (cell.getCellType()) { //Identify CELL type
//                //you need to add more code here based on
//                //your requirement / transformations
//                case Cell.CELL_TYPE_STRING:
//                    //Push the data from Excel to PDF Cell
//                    table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
//                    //feel free to move the code below to suit to your needs
//                    my_table.addCell(table_cell);
//                    break;
//                case Cell.CELL_TYPE_BLANK:
//                    table_cell=new PdfPCell(new Phrase(""));
//                    my_table.addCell(table_cell);
//            }
//            //next line
//        }


        //Finally add the table to PDF document
        try {
            iText_xls_2_pdf.add(my_table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

//          String date_val1 =  "Generated by: "+ " " +getString(R.string.app_name)+" "+ "-आपके अपने मोबाइल पर";
        try {
            Drawable d = getResources().getDrawable(R.drawable.logo23);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_CENTER);
            image.setWidthPercentage(50);
            image.setScaleToFitHeight(true);
            iText_xls_2_pdf.add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String date_val3 = "To know us more visit https://skillzoomers.com/hajiri-register/ ";

//          Paragraph p4 = new Paragraph(date_val1);
//          p4.setAlignment(Paragraph.ALIGN_CENTER);


        Paragraph p7 = new Paragraph("\n");
        p7.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p8 = new Paragraph(date_val3);
        p8.setAlignment(Paragraph.ALIGN_CENTER);

        try {

            iText_xls_2_pdf.add(p7);
            iText_xls_2_pdf.add(p8);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        iText_xls_2_pdf.close();
        //we created our pdf file..
//          try {
//
//              input_document.close(); //close xls
//          } catch (IOException e) {
//              e.printStackTrace();
//          }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("dailyDate", currentDate);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(timelineActivity.this, "File Downloaded and saved in Downloads Folder", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Log.e("Progress", "" + progressDialog.isShowing());
                alertDialog.dismiss();
            }
        });


    }

    private void DownloadPdf(File file, FileOutputStream fos, HSSFWorkbook workbook,
                             long siteId1, String siteName1, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList,
                             ArrayList<ModelCompileStatus> modelCompileStatusArrayList, ArrayList<ModelLabour> list, ArrayList<ModelDate> shortDateList, long id1, String name1, AlertDialog alertDialog) throws DocumentException {
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "HajiriRegister_" + "" + "CompiledReoprt_" + timestamp + ".pdf";
        File pdfFile = new File(str_path);
        com.itextpdf.text.Document iText_xls_2_pdf = new Document();
        iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
        try {
            PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
        } catch (DocumentException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();
        }
        iText_xls_2_pdf.open();

        com.itextpdf.text.Font fontHeaderNormal = new com.itextpdf.text.Font();
        fontHeaderNormal.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderNormal.setSize(12);
        fontHeaderNormal.setColor(BaseColor.BLACK);
        fontHeaderNormal.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontHeaderBold = new com.itextpdf.text.Font();
        fontHeaderBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderBold.setSize(14);
        fontHeaderBold.setColor(BaseColor.BLACK);
        fontHeaderBold.setFamily(String.valueOf(Paint.Align.CENTER));
        PdfPTable my_table_header = new PdfPTable(20);
        my_table_header.setWidthPercentage(95);


        PdfPCell table_cell_industry_name;
        PdfPCell table_cell_company_name;
        PdfPCell table_cell_siteId;
        PdfPCell table_cell_siteName;
        PdfPCell table_cell_generated_on;
        PdfPCell table_cell_workers_advances_report;
        PdfPCell table_cell_to;
        PdfPCell table_cell_from;
        PdfPCell table_cell_total_no_of_days;
        table_cell_industry_name = new PdfPCell(new Phrase("Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""), fontHeaderBold));
        table_cell_company_name = new PdfPCell(new Phrase("Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", ""), fontHeaderBold));
        table_cell_siteId = new PdfPCell(new Phrase("Site Id: " + siteId1, fontHeaderNormal));
        table_cell_siteName = new PdfPCell(new Phrase("Site Name: " + siteName1, fontHeaderNormal));
        table_cell_generated_on = new PdfPCell(new Phrase("Generated On: " + currentDate, fontHeaderNormal));
        table_cell_workers_advances_report = new PdfPCell(new Phrase("Workers Compiled Report", fontHeaderBold));
        table_cell_from = new PdfPCell(new Phrase("From:" + currentDate1, fontHeaderNormal));
        table_cell_to = new PdfPCell(new Phrase("To:" + currentDate, fontHeaderNormal));
        table_cell_total_no_of_days = new PdfPCell(new Phrase("Total No of days:1", fontHeaderNormal));

        table_cell_industry_name.setColspan(7);
        table_cell_industry_name.setBorder(Rectangle.NO_BORDER);
        table_cell_industry_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_generated_on.setColspan(7);
        table_cell_generated_on.setBorder(Rectangle.NO_BORDER);
        table_cell_generated_on.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_company_name.setColspan(6);
        table_cell_company_name.setBorder(Rectangle.NO_BORDER);
        table_cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_siteName.setColspan(7);
        table_cell_siteName.setBorder(Rectangle.NO_BORDER);
        table_cell_siteName.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_workers_advances_report.setColspan(7);
        table_cell_workers_advances_report.setBorder(Rectangle.NO_BORDER);
        table_cell_workers_advances_report.setHorizontalAlignment(Element.ALIGN_CENTER);

        table_cell_siteId.setColspan(6);
        table_cell_siteId.setBorder(Rectangle.NO_BORDER);
        table_cell_siteId.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_from.setColspan(3);
        table_cell_from.setBorder(Rectangle.NO_BORDER);
        table_cell_from.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell_to.setColspan(3);
        table_cell_to.setBorder(Rectangle.NO_BORDER);
        table_cell_to.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell_total_no_of_days.setColspan(14);
        table_cell_total_no_of_days.setBorder(Rectangle.NO_BORDER);
        table_cell_total_no_of_days.setHorizontalAlignment(Element.ALIGN_RIGHT);


        my_table_header.addCell(table_cell_industry_name);
        my_table_header.addCell(table_cell_generated_on);
        my_table_header.addCell(table_cell_company_name);
        my_table_header.addCell(table_cell_siteName);
        my_table_header.addCell(table_cell_workers_advances_report);
        my_table_header.addCell(table_cell_siteId);
        my_table_header.addCell(table_cell_from);
        my_table_header.addCell(table_cell_to);
        my_table_header.addCell(table_cell_total_no_of_days);
        PdfPTable my_table = new PdfPTable(9);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;
        try {
            iText_xls_2_pdf.add(my_table_header);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        createPdfHeaderRow(modelDateArrayList, shortDateList, siteId1, siteName1, iText_xls_2_pdf, my_table, alertDialog);
    }

    private void createPdfHeaderRow(ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDate> shortDateList,
                                    long siteId1, String siteName1, Document iText_xls_2_pdf, PdfPTable my_table, AlertDialog alertDialog) throws DocumentException {
        PdfPTable table_header = new PdfPTable(11);


        table_header.setWidthPercentage(95);
//        table_header.setWidths(new float[modelDateArrayList.size()+8]);

        com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font();
        fontBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontBold.setSize(12);
        fontBold.setColor(BaseColor.BLACK);
        fontBold.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font();
        fontNormal.setStyle(com.itextpdf.text.Font.NORMAL);
        fontNormal.setSize(12);
        fontNormal.setColor(BaseColor.BLACK);

        com.itextpdf.text.Font fontGreen = new com.itextpdf.text.Font();
        fontGreen.setStyle(com.itextpdf.text.Font.BOLD);
        fontGreen.setSize(12);
        fontGreen.setColor(BaseColor.BLACK);

        com.itextpdf.text.Font fontRed = new com.itextpdf.text.Font();
        fontRed.setStyle(com.itextpdf.text.Font.BOLD);
        fontRed.setSize(12);
        fontRed.setColor(BaseColor.RED);

        com.itextpdf.text.Font fontRedHeading = new com.itextpdf.text.Font();
        fontRedHeading.setStyle(com.itextpdf.text.Font.BOLD | com.itextpdf.text.Font.UNDERLINE);
        fontRedHeading.setSize(12);
        fontRedHeading.setColor(BaseColor.RED);

        com.itextpdf.text.Font fontBlue = new com.itextpdf.text.Font();
        fontBlue.setStyle(com.itextpdf.text.Font.NORMAL);
        fontBlue.setSize(12);
        fontBlue.setColor(BaseColor.BLUE);

        PdfPCell srNo, id, name, type, wages, total_att, total_adv, payable_amt, date;
        srNo = (new PdfPCell(new Phrase("Sr No", fontBold)));
        id = (new PdfPCell(new Phrase("Worker Id", fontBold)));
        id.setHorizontalAlignment(Element.ALIGN_CENTER);
        name = (new PdfPCell(new Phrase("Worker Name", fontBold)));
        name.setHorizontalAlignment(Element.ALIGN_CENTER);
        type = (new PdfPCell(new Phrase("Type", fontBold)));
        type.setHorizontalAlignment(Element.ALIGN_CENTER);
        wages = (new PdfPCell(new Phrase("Wages", fontBold)));
        wages.setHorizontalAlignment(Element.ALIGN_CENTER);
        total_att = (new PdfPCell(new Phrase("Total Att.", fontBold)));
        total_att.setHorizontalAlignment(Element.ALIGN_CENTER);
        total_adv = (new PdfPCell(new Phrase("Total Adv.", fontBold)));
        total_adv.setHorizontalAlignment(Element.ALIGN_CENTER);
        payable_amt = (new PdfPCell(new Phrase("Payable Amount", fontBold)));
        payable_amt.setHorizontalAlignment(Element.ALIGN_CENTER);
        date = (new PdfPCell(new Phrase(currentDate1, fontBold)));
        date.setHorizontalAlignment(Element.ALIGN_CENTER);

        id.setColspan(2);
        name.setColspan(2);


//        table_header.addCell(new PdfPCell(new Phrase("Sr No",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Worker Id",fontBold))).setColspan(2);
//        table_header.addCell(new PdfPCell(new Phrase("Worker Name",fontBold))).setColspan(2);
//        table_header.addCell(new PdfPCell(new Phrase("Type",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Wages",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Total Att.",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Total Adv.",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Payable Amount",fontBold)));
        table_header.addCell(srNo);
        table_header.addCell(id);
        table_header.addCell(name);
        table_header.addCell(type);
        table_header.addCell(wages);
        table_header.addCell(total_att);
        table_header.addCell(total_adv);
        table_header.addCell(payable_amt);
        table_header.addCell(date);


        for (int i = 0; i < labourList.size() * 2; i++) {
            if (i % 2 == 0) {
                PdfPCell tableCell_SrNo = new PdfPCell();
                tableCell_SrNo.setNoWrap(false);
                tableCell_SrNo.setPhrase(new Phrase(String.valueOf(i / 2 + 1), fontNormal));
                tableCell_SrNo.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_SrNo.setFixedHeight(20);
                table_header.addCell(tableCell_SrNo);

                PdfPCell tableCell_Id = new PdfPCell();
                tableCell_Id.setNoWrap(false);
                tableCell_Id.setPhrase(new Phrase(labourList.get(i / 2).getLabourId(), fontNormal));
                tableCell_Id.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_Id.setFixedHeight(20);
                tableCell_Id.setColspan(2);
                table_header.addCell(tableCell_Id);


                PdfPCell tableCell_Name = new PdfPCell();
                tableCell_Name.setNoWrap(false);
                tableCell_Name.setPhrase(new Phrase(labourList.get(i / 2).getName(), fontNormal));
                tableCell_Name.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_Name.setFixedHeight(20);
                tableCell_Name.setColspan(2);
                table_header.addCell(tableCell_Name);


                PdfPCell tableCell_Type = new PdfPCell();
                tableCell_Type.setNoWrap(false);
                tableCell_Type.setPhrase(new Phrase(labourList.get(i / 2).getType(), fontNormal));
                tableCell_Type.setFixedHeight(20);
                tableCell_Type.setHorizontalAlignment(Element.ALIGN_CENTER);

                table_header.addCell(tableCell_Type);

                PdfPCell tableCell_Wages = new PdfPCell();
                tableCell_Wages.setNoWrap(false);
                tableCell_Wages.setPhrase(new Phrase(String.valueOf(labourList.get(i / 2).getWages()), fontNormal));
                tableCell_Wages.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_Wages.setFixedHeight(20);
                table_header.addCell(tableCell_Wages);


                com.itextpdf.text.Font fontHeading = new com.itextpdf.text.Font();
                fontHeading.setStyle(com.itextpdf.text.Font.BOLD);
                fontHeading.setSize(12);
                fontHeading.setColor(BaseColor.BLACK);
                fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));


                int AmtSum = 0;
                float CountPresent = 0;
                float countHalf = 0;
                float countTotal = 0;
                for (int j = i / 2; j < ModelCompileStatusArrayList.size(); j = j + labourList.size()) {
                    if (ModelCompileStatusArrayList.get(j).getStatus().equals("P")) {
                        CountPresent++;
                    } else {
                        if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                            countHalf++;
                        }
                    }
                    if (!ModelCompileStatusArrayList.get(j).getAmount().equals("0")) {
                        AmtSum = AmtSum + Integer.parseInt(ModelCompileStatusArrayList.get(j).getAmount());
                    }

                }
                int PayableAmt = (int) ((labourList.get(i / 2).getWages() * CountPresent + ((labourList.get(i / 2).getWages() / 2) * countHalf)) - AmtSum);

                String status = String.valueOf(AmtSum);
                String payableAmt = String.valueOf(PayableAmt);
                countTotal = CountPresent + (countHalf / 2);
                PdfPCell table_cell_pT = new PdfPCell();
                table_cell_pT.setNoWrap(false);
                table_cell_pT.setPhrase(new Phrase((String.valueOf(countTotal)), fontGreen));
                table_cell_pT.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell_pT.setFixedHeight(20);
                ;
                table_header.addCell(table_cell_pT);

                PdfPCell table_cell_ts = new PdfPCell();
                table_cell_ts.setNoWrap(false);
                table_cell_ts.setPhrase(new Phrase((status), fontGreen));
                table_cell_ts.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell_ts.setFixedHeight(20);
                table_header.addCell(table_cell_ts);

                PdfPCell table_cell_pa = new PdfPCell();
                table_cell_pa.setNoWrap(false);
                if (PayableAmt < 0) {
                    table_cell_pa.setPhrase(new Phrase((payableAmt), fontRed));
                    table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_cell_pa.setFixedHeight(20);
                } else {
                    table_cell_pa.setPhrase(new Phrase((payableAmt), fontBlue));
                    table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_cell_pa.setFixedHeight(20);
                }

                table_header.addCell(table_cell_pa);


                for (int j = i / 2; j < ModelCompileStatusArrayList.size(); j = j + labourList.size()) {
                    if (ModelCompileStatusArrayList.get(j).getStatus().equals("P")) {
                        PdfPCell present = new PdfPCell();
                        present.setNoWrap(false);
                        present.setPhrase(new Phrase(("P"), fontBlue));
                        table_header.addCell(present);
                        present.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                        PdfPCell p2 = new PdfPCell();
                        p2.setNoWrap(false);
                        p2.setPhrase(new Phrase(("P/2"), fontBlue));
                        p2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_header.addCell(p2);
                    } else {
                        PdfPCell absent = new PdfPCell();
                        absent.setNoWrap(false);
                        absent.setPhrase(new Phrase(("A"), fontRed));
                        absent.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_header.addCell(absent);
                    }
                }


//                PdfPCell table_cell_att=new PdfPCell();
//                table_cell_att.setNoWrap(false);
//                table_cell_att.setPhrase(new Phrase(("A"),fontRed));


            } else {
                PdfPCell column = new PdfPCell();
                column.setNoWrap(false);
                column.setPhrase(new Phrase(("")));
                column.setColspan(10);
                column.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_header.addCell(column);

                for (int j = i / 2; j < ModelCompileStatusArrayList.size(); j = j + labourList.size()) {
                    if (ModelCompileStatusArrayList.get(j).getAmount().equals("0")) {
                        PdfPCell Amount_null = new PdfPCell();
                        Amount_null.setNoWrap(false);
                        Amount_null.setPhrase(new Phrase(("0"), fontNormal));
                        Amount_null.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_header.addCell(Amount_null);
                    } else {
                        PdfPCell Amount = new PdfPCell();
                        Amount.setNoWrap(false);
                        Amount.setPhrase(new Phrase((ModelCompileStatusArrayList.get(j).getAmount()), fontNormal));
                        Amount.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_header.addCell(Amount);
                    }
                }
            }


        }
        PdfPCell blank = new PdfPCell();
        blank.setNoWrap(false);
        blank.setPhrase(new Phrase(("")));
        blank.setColspan(8);
        table_header.addCell(blank);
        PdfPCell blank1 = new PdfPCell();
        blank1.setNoWrap(false);
        blank1.setPhrase(new Phrase(("Skilled/Unskilled"), fontBold));
        blank1.setHorizontalAlignment(Element.ALIGN_CENTER);
        blank1.setColspan(2);
        table_header.addCell(blank1);
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            int presentSkilled = 0;
            int presentUnskilled = 0;
            int sum = 0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
                    if (ModelCompileStatusArrayList.get(i + j).getType().equals("Skilled")) {
                        presentSkilled += 1;
                    } else {
                        presentUnskilled += 1;
                    }
                }

                if (j == labourList.size() - 1) {

                    PdfPCell att = new PdfPCell();
                    att.setNoWrap(false);
                    att.setPhrase(new Phrase(("" + presentSkilled + "/" + presentUnskilled), fontBold));
                    att.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_header.addCell(att);

                }


            }

        }
        table_header.addCell(blank);
        blank1.setPhrase(new Phrase(("Total Payment"), fontBold));
        blank1.setHorizontalAlignment(Element.ALIGN_CENTER);
        blank1.setColspan(2);
        table_header.addCell(blank1);
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            int presentSkilled = 0;
            int presentUnskilled = 0;
            int sum = 0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (!ModelCompileStatusArrayList.get(i + j).getAmount().equals("0")) {
                    sum = sum + Integer.parseInt(ModelCompileStatusArrayList.get(i + j).getAmount());
                }

                if (j == labourList.size() - 1) {

                    PdfPCell att = new PdfPCell();
                    att.setNoWrap(false);
                    att.setPhrase(new Phrase((String.valueOf(sum)), fontBold));
                    att.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_header.addCell(att);

                }


            }

        }


        try {
            iText_xls_2_pdf.add(table_header);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

//          String date_val1 =  "Generated by: "+ " " +getString(R.string.app_name)+" "+ "-आपके अपने मोबाइल पर";
        try {
            Drawable d = getResources().getDrawable(R.drawable.logo23);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_CENTER);
            image.setWidthPercentage(50);
            image.setScaleToFitHeight(true);
            iText_xls_2_pdf.add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

//          Paragraph p4 = new Paragraph(date_val1);
//          p4.setAlignment(Paragraph.ALIGN_CENTER);


        Paragraph p7 = new Paragraph("\n");
        p7.setAlignment(Paragraph.ALIGN_CENTER);


        try {

            iText_xls_2_pdf.add(p7);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        iText_xls_2_pdf.close();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("dailyDate", currentDate);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(timelineActivity.this, "File Downloaded and saved in Downloads Folder", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Log.e("Progress", "" + progressDialog.isShowing());
                alertDialog.dismiss();
            }
        });

    }


//        int amount=100;


    private void createPayment(Sheet sheet, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);

        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
        Font fontPresent = sheet.getWorkbook().createFont();
        fontPresent.setColor(HSSFColor.BLACK.index);
        fontPresent.setFontHeightInPoints((short) 12);
        cellStylePresent.setFont(fontPresent);
        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
        cellStylePresent.setBorderBottom((short) 1);
        cellStylePresent.setBorderTop((short) 1);
        cellStylePresent.setBorderLeft((short) 1);
        cellStylePresent.setBorderRight((short) 1);
        cellStylePresent.setFont(fontPresent);
        int countPresent = 0, cloneJ = 0, k = 0;

        for (int j = 0; j < labourList.size(); j++) {
            cloneJ = j;
            Row row = sheet.createRow(8 + (2 * j));
//                k = i / 10;
//                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
//                Cell cell = row.createCell(k + 5);
//                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
//                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("0")) {
//                    countPresent += 1;
//                    cell.setCellStyle(cellStyle);
//                } else {
//                    cell.setCellStyle(cellStylePresent);
//                }
//
//                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getAmount());


//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
//            int y=i/10;
//            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
        }
        createPaymentDatasheet(sheet, labourList, modelDateArrayList, modelCompileStatusArrayList);


    }

    private void createPaymentDatasheet(Sheet sheet, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList,
                                        ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        ;
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);

        CellStyle cellStyleUnderLine = sheet.getWorkbook().createCellStyle();
        Font font1 = sheet.getWorkbook().createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font1.setFontHeightInPoints((short) 12);
        font1.setUnderline(Font.U_SINGLE);
        cellStyleUnderLine.setFont(font1);
        cellStyleUnderLine.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleUnderLine.setBorderBottom((short) 1);
        cellStyleUnderLine.setBorderTop((short) 1);
        cellStyleUnderLine.setBorderLeft((short) 1);
        cellStyleUnderLine.setBorderRight((short) 1);
        cellStyleUnderLine.setWrapText(true);

        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
        Font fontPresent = sheet.getWorkbook().createFont();
        fontPresent.setColor(HSSFColor.BLACK.index);
        fontPresent.setFontHeightInPoints((short) 12);
        cellStylePresent.setFont(fontPresent);
        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
        cellStylePresent.setBorderBottom((short) 1);
        cellStylePresent.setBorderTop((short) 1);
        cellStylePresent.setBorderLeft((short) 1);
        cellStylePresent.setBorderRight((short) 1);
        cellStylePresent.setFont(fontPresent);

        CellStyle cellStyleNegative = sheet.getWorkbook().createCellStyle();
        Font font3 = sheet.getWorkbook().createFont();
        font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font3.setUnderline(Font.U_SINGLE);
        font3.setFontHeightInPoints((short) 12);
        font3.setColor(Font.COLOR_RED);
        cellStyleNegative.setFont(font3);

        int countPresent = 0, cloneJ = 0, k = 0;
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {

            for (int j = 0; j < labourList.size(); j++) {
                cloneJ = j;
                Row row = sheet.getRow(8 + (2 * j));
                row.setHeightInPoints(22);
                k = i / labourList.size();
                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
                Cell cell = row.createCell(k + 8);
                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("0")) {
                    countPresent += 1;
                    cell.setCellStyle(cellStyle);
                } else {
                    cell.setCellStyle(cellStylePresent);
                }

                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getAmount());


//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
//            int y=i/10;
//            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
            }
        }
        for (int j = 0; j < labourList.size(); j++) {
            for (int m = 0; m < 8; m++) {
                Row row = sheet.getRow(8 + (2 * j));
                row.setHeightInPoints(22);
                Cell cell = row.createCell(m);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");

            }
        }
        for (int s = 0; s < labourList.size(); s++) {
//            int CountPresent=0;
            int AmtSum = 0;
            float countHalf = 0;
            float CountPresent = 0;
            float countTotal = 0;
            for (int j = s; j < ModelCompileStatusArrayList.size(); j = j + labourList.size()) {
                if (!ModelCompileStatusArrayList.get(j).getStatus().equals("0")) {
                    AmtSum = AmtSum + Integer.parseInt(ModelCompileStatusArrayList.get(j).getAmount());
                }
                if (ModelCompileStatusArrayList.get(j).getStatus().equals("P")) {
                    CountPresent++;
                } else {
                    if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                        countHalf++;
                    }
                }


            }
            Log.e("LabourCheck", "S::::" + s + ":::" + labourList.get(s / labourList.size()).getLabourId());
            int PayableAmt = (int) ((labourList.get(s).getWages() * CountPresent + ((labourList.get(s / labourList.size()).getWages() / 2) * countHalf)) - AmtSum);
//            countTotal=countPresent+(countHalf/2);
//            Log.e("Stats123",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+AmtSum+"/"+modelDateArrayList.size());
            Row rowPresentStatus = sheet.getRow((7 + (2 * s)));
            Cell cellPresentStatus = rowPresentStatus.createCell(6);
            if (AmtSum > 0) {
                cellPresentStatus.setCellStyle(cellStyleUnderLine);
            } else {
                cellPresentStatus.setCellStyle(cellStyle);
            }
            String status = String.valueOf(AmtSum);

            cellPresentStatus.setCellValue(status);
            Cell cellPayableAmt = rowPresentStatus.createCell(7);
            String payableAmt = String.valueOf(PayableAmt);
            if (PayableAmt < 0) {
                cellPayableAmt.setCellStyle(cellStyleNegative);
            } else {
                cellPayableAmt.setCellStyle(cellStyleUnderLine);
            }

            cellPayableAmt.setCellValue(payableAmt);
        }
    }

    private void createOrderId(int amount, AlertDialog.Builder builder) throws RazorpayException, JSONException {


        // Handle Exception


    }

    private void createFooter(Sheet sheet, ArrayList<ModelLabour> labourList,
                              ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> ModelCompileStatusArrayList) {
        int totalSkilledCount = 0, totalUnskilledCount = 0, presentSkilled = 0, presentUnskilled = 0, sum = 0;
        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
        Font fontPresent = sheet.getWorkbook().createFont();
        fontPresent.setColor(HSSFColor.BLACK.index);
        fontPresent.setFontHeightInPoints((short) 12);
        cellStylePresent.setFont(fontPresent);
        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
        cellStylePresent.setBorderBottom((short) 1);
        cellStylePresent.setBorderTop((short) 1);
        cellStylePresent.setBorderLeft((short) 1);
        cellStylePresent.setBorderRight((short) 1);
        cellStylePresent.setFont(fontPresent);

        CellStyle cellStyle2 = sheet.getWorkbook().createCellStyle();
        Font font2 = sheet.getWorkbook().createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setBorderBottom((short) 0);
        cellStyle2.setBorderTop((short) 0);
        cellStyle2.setBorderLeft((short) 0);
        cellStyle2.setBorderRight((short) 0);
        cellStyle2.setWrapText(true);
        Date c = Calendar.getInstance().getTime();

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);


        sheet.createRow((labourList.size() * 2) + 8);
        sheet.createRow((labourList.size() * 2) + 9);


        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            presentSkilled = 0;
            presentUnskilled = 0;
            sum = 0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
                    if (ModelCompileStatusArrayList.get(i + j).getType().equals("Skilled")) {
                        presentSkilled += 1;
                    } else {
                        presentUnskilled += 1;
                    }
                }
                if (!ModelCompileStatusArrayList.get(i + j).getAmount().equals("0")) {
                    sum = sum + Integer.parseInt(ModelCompileStatusArrayList.get(i + j).getAmount());

                }

                if (j == labourList.size() - 1) {
                    int y = i / labourList.size();
                    Row rowPresentSkilled = sheet.getRow((labourList.size() * 2) + 8);
                    rowPresentSkilled.setHeightInPoints(22);
                    Cell Status = rowPresentSkilled.createCell(y + 8);
                    Status.setCellStyle(cellStyle);
                    Status.setCellValue("" + presentSkilled + "/" + presentUnskilled);
                    Row rowTotalPayment = sheet.getRow((labourList.size() * 2) + 9);
                    rowTotalPayment.setHeightInPoints(22);
                    Cell Payment = rowTotalPayment.createCell(y + 8);
                    Payment.setCellStyle(cellStyle);
                    Payment.setCellValue(sum);

                }


            }


//            float countPresent = 0;
//            float countHalf = 0;
//            float countTotal = 0;
//
//            for (int s = 0; s < ModelCompileStatusArrayList.size(); s++) {
//                if (ModelCompileStatusArrayList.get(s).getStatus().equals("P")) {
//                    countPresent++;
//                } else if (ModelCompileStatusArrayList.get(s).getStatus().equals("P/2")) {
//                    countHalf++;
//                }
//                countTotal = countPresent + (countHalf / 2);
//            }
//            Row rowPresentSkilled = sheet.getRow(labourList.size() + 8);
//            Cell Status = rowPresentSkilled.createCell(4);
//            Status.setCellStyle(cellStylePresent);
//            Status.setCellValue("" + countTotal + "/" + ModelCompileStatusArrayList.size());


            Row row = sheet.createRow(labourList.size() * 2 + 12);
            Cell cellFullName = row.createCell(0);


            cellFullName.setCellStyle(cellStyle2);
//        cellFullName.setCellValue("Attendance Report");
            String date_val = "Generated by: " + " " + "Hajiri Register";
            cellFullName.setCellValue(date_val);

            CellRangeAddress cellMerge = new CellRangeAddress(labourList.size() * 2 + 12, labourList.size() * 2 + 14, 0, 4);
            sheet.addMergedRegion(cellMerge);

            String date_val1 = "Powered by: " + " " + "Skill Zoomers";

            Row row1 = sheet.createRow(labourList.size() * 2 + 14);
            Cell cellHeading = row1.createCell(0);

            cellHeading.setCellStyle(cellStyle2);
            cellHeading.setCellValue(date_val1);

            CellRangeAddress cellMerge1 = new CellRangeAddress(labourList.size() * 2 + 16, labourList.size() * 2 + 18, 0, 4);
            sheet.addMergedRegion(cellMerge1);

            String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ " + " " + "Skill Zoomers";

            Row row2 = sheet.createRow(labourList.size() * 2 + 20);
            Cell cellHeading1 = row2.createCell(0);

            cellHeading1.setCellStyle(cellStyle2);
            cellHeading1.setCellValue(date_val2);

            CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size() * 2 + 20, labourList.size() * 2 + 22, 0, 4);
            sheet.addMergedRegion(cellMerge2);


        }
//        Row row5=sheet.createRow(labourList.size()*2+7);
//        for(int l=0;l<modelDateArrayList.size()+5;l++){
//            Cell Status = row5.createCell(0);
//            Status.setCellStyle(cellStylePresent);
//            Status.setCellValue("");
//        }
        for (int k = 8; k < 10; k++) {
            Row row = sheet.getRow((labourList.size() * 2) + k);
            for (int m = 0; m < 5; m++) {
                Cell Status = row.createCell(m);
                Status.setCellStyle(cellStyle2);
                Status.setCellValue("");
            }
            Cell Status = row.createCell(5);
            Status.setCellStyle(cellStyle);
            if (k == 8) {
                Status.setCellValue("Skilled/Unskilled");
            } else {
                Status.setCellValue("Total Payment");
            }
            row.createCell(6).setCellStyle(cellStyle);
            row.createCell(7).setCellStyle(cellStyle);
            CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size() * 2 + k, labourList.size() * 2 + k, 5, 7);
            sheet.addMergedRegion(cellMerge2);


        }

    }

    private void createAttendanceData(Sheet sheet, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> ModelCompileStatusArrayList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setColor(HSSFColor.RED.index);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        CellStyle cellStyle2 = sheet.getWorkbook().createCellStyle();
        Font font2 = sheet.getWorkbook().createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setBorderBottom((short) 0);
        cellStyle2.setBorderTop((short) 0);
        cellStyle2.setBorderLeft((short) 0);
        cellStyle2.setBorderRight((short) 0);
        cellStyle2.setWrapText(true);
        Date c = Calendar.getInstance().getTime();

        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
        Font fontPresent = sheet.getWorkbook().createFont();
        fontPresent.setColor(HSSFColor.BLUE.index);
        fontPresent.setFontHeightInPoints((short) 12);
        cellStylePresent.setFont(fontPresent);
        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
        cellStylePresent.setBorderBottom((short) 1);
        cellStylePresent.setBorderTop((short) 1);
        cellStylePresent.setBorderLeft((short) 1);
        cellStylePresent.setBorderRight((short) 1);
        cellStylePresent.setFont(fontPresent);
        int countPresent = 0, cloneJ = 0, k = 0;
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            countPresent = 0;
            for (int j = 0; j < labourList.size(); j++) {
                cloneJ = j;
                Row row = sheet.getRow(7 + (2 * j));
                row.setHeightInPoints(22);
                k = i / labourList.size();
                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
                Cell cell = row.createCell(k + 8);
                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
                    countPresent += 1;
                    cell.setCellStyle(cellStylePresent);
                } else {
                    cell.setCellStyle(cellStyle);
                }

                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getStatus());

            }
//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
//            int y=i/labourList.size();
//            Row rowPresentStatus = sheet.createRow(y+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
        }
        for (int s = 0; s < labourList.size(); s++) {
//            int CountPresent=0;
            float CountPresent = 0;
            float countHalf = 0;
            float countTotal = 0;
            for (int j = s; j < ModelCompileStatusArrayList.size(); j = j + labourList.size()) {
                if (ModelCompileStatusArrayList.get(j).getStatus().equals("P")) {
                    CountPresent++;
                } else {
                    if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                        countHalf++;
                    }
                }

            }
            countTotal = CountPresent + (countHalf / 2);
//            Log.e("Stats1256",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+CountPresent+"/"+modelDateArrayList.size());
//            Log.e("Stats1256",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+countTotal+"/"+modelDateArrayList.size());
            Row rowPresentStatus = sheet.getRow((7 + (2 * s)));
            Cell cellPresentStatus = rowPresentStatus.createCell(5);
            String status = "" + countTotal;
            cellPresentStatus.setCellStyle(cellStyle2);
            cellPresentStatus.setCellValue(status);
        }
//        for(int s=0;s<labourList.size();s++){
////            int CountPresent=0;
//            float CountPresent=0;
//            float countHalf=0;
//            float countTotal=0;
//            for(int j=s;j<ModelCompileStatusArrayList.size();j=j+labourList.size()){
//                if(ModelCompileStatusArrayList.get(k).getStatus().equals("P")){
//                    CountPresent++;
//                }else {
//                    if (ModelCompileStatusArrayList.get(k).getStatus().equals("P/2")) {
//                        countHalf++;
//                    }
//                }
//
//            }
//            countTotal=countPresent+(countHalf/2);
//            Log.e("Stats123",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+CountPresent+"/"+modelDateArrayList.size());
//            Row rowPresentStatus = sheet.getRow((s+7));
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countTotal+"/"+modelDateArrayList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
//        }
    }

    private void createLAbourData(Sheet sheet, ArrayList<ModelLabour> labourList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            font.setColor((short) getColor(R.color.black));
        }
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        for (int i = 0; i < labourList.size(); i++) {
            Row row = sheet.createRow((7 + (2 * i)));
            row.setHeightInPoints(22);
            Cell cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(String.valueOf(i + 1));
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(cellStyle);
            cell1.setCellValue(labourList.get(i).getLabourId());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(cellStyle);
            cell2.setCellValue(labourList.get(i).getName());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(cellStyle);
            cell3.setCellValue(labourList.get(i).getType());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(cellStyle);
            cell4.setCellValue(labourList.get(i).getWages());


        }
    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelDate> dateModelArrayList, ArrayList<ModelDate> shortDateList, long siteId1, String siteName1) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);

        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        Font font1 = sheet.getWorkbook().createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font1.setFontHeightInPoints((short) 12);
        cellStyle1.setFont(font);
        cellStyle1.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle1.setBorderBottom((short) 0);
        cellStyle1.setBorderTop((short) 0);
        cellStyle1.setBorderLeft((short) 0);
        cellStyle1.setBorderRight((short) 0);
        cellStyle1.setWrapText(true);

        CellStyle cellStyle2 = sheet.getWorkbook().createCellStyle();
        Font font2 = sheet.getWorkbook().createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setBorderBottom((short) 0);
        cellStyle2.setBorderTop((short) 0);
        cellStyle2.setBorderLeft((short) 0);
        cellStyle2.setBorderRight((short) 0);
        cellStyle2.setWrapText(true);
        Date c = Calendar.getInstance().getTime();

        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);


        Row row = sheet.createRow(0);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle1);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") +
                "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "");

        cellFullName.setCellValue(date_val);
//        int size=0;
//        if(dateModelArrayList.size()+5<8){
//            size=8;
//        }else{
//            size=dateModelArrayList.size()+5;
//        }

        CellRangeAddress cellMerge = new CellRangeAddress(0, 3, 0, 6);
        sheet.addMergedRegion(cellMerge);

        Cell cellFullName1 = row.createCell(7);
        cellFullName1.setCellStyle(cellStyle1);
        String date_val1 = "Generated On: " + currentDate + "\n" + "Site Id: " + siteId1 +
                "\t\t\t\t\t Site Name: " + siteName1;
        cellFullName1.setCellValue(date_val1);


        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 3, 7, 12);
        sheet.addMergedRegion(cellMerge1);

        Cell cellFullName3 = row.createCell(13);
        cellFullName3.setCellStyle(cellStyle1);
//        String date_val3="From: " +" "+dateModelArrayList.get(0).getDate()+"\n"+ "To: " + dateModelArrayList.get(dateModelArrayList.size()-1).getDate();
//        cellFullName3.setCellValue(date_val3);


//        CellRangeAddress cellMerge3 = new CellRangeAddress(0, 3, 13, 17);
//        sheet.addMergedRegion(cellMerge3);


        Row row1 = sheet.createRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("Workers Compiled Report" + "\t\t\t" + "Total No of Days:1");

        CellRangeAddress cellMerge2 = new CellRangeAddress(4, 5, 0, 8);
        sheet.addMergedRegion(cellMerge2);

        Row rowValues = sheet.createRow(6);
        rowValues.setHeightInPoints(30);
        Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        Cell cellSrNo1 = rowValues.createCell(1);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Worker Id");
        Cell cellSrNo2 = rowValues.createCell(2);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Worker Name");
        Cell cellSrNo3 = rowValues.createCell(3);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Type");
        Cell cellSrNo4 = rowValues.createCell(4);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Wages");
        Cell cellSrNo6 = rowValues.createCell(5);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Total Attendance");
        Cell cellSrNo7 = rowValues.createCell(6);
        cellSrNo7.setCellStyle(cellStyle);
        cellSrNo7.setCellValue("Total Advances");
        Cell cellSrNo5 = rowValues.createCell(7);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("PayableAmt");
        Cell cellSrNo8 = rowValues.createCell(6);
        cellSrNo8.setCellStyle(cellStyle);
        cellSrNo8.setCellValue("Date");


//        Cell cellDesignation= row.createCell(1);
//        cellDesignation.setCellStyle(cellStyle);
//        cellDesignation.setCellValue("WorkerName");
//        for(int i=0;i<dateModelArrayList.size();i++){
//            Cell cellDate= row.createCell(i+3);
//            cellDate.setCellStyle(cellStyle);
//            cellDate.setCellValue(dateModelArrayList.get(i).getDate());
//        }


    }

    private void getIndustryList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIndustryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUserIndustry modelCategory = ds.getValue(ModelUserIndustry.class);
                    userIndustryArrayList.add(modelCategory);
                }

                SpinnerCategoryAdapter spinnerCategoryAdapter = new SpinnerCategoryAdapter();
                binding.spinnerSelectIndustry.setAdapter(spinnerCategoryAdapter);

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

    private void getSliderData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Banner");
        reference.child("Timeline").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderDataTimelines.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SliderDataTimeline sliderDataTimeline = ds.getValue(SliderDataTimeline.class);
                    sliderDataTimelines.add(sliderDataTimeline);
                }
                Log.e("SizeSlider", "" + sliderDataTimelines.size());
                SliderAdapterTimeline adapter = new SliderAdapterTimeline(timelineActivity.this, sliderDataTimelines);
                adapter.notifyDataSetChanged();
                binding.sliderTimeline.setSliderAdapter(adapter, false);
                binding.sliderTimeline.setAutoCycle(true);
                binding.sliderTimeline.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                Log.e("LOggy", "" + binding.sliderTimeline.getCurrentPagePosition());
                binding.sliderTimeline.setScrollTimeInSec(5);

                binding.sliderTimeline.startAutoCycle();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showSlider() {


    }

    private void getSiteListPending() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteArrayList_opt3.clear();
                progressDialog.dismiss();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    siteArrayList_opt3.add(modelSite);


                }
                if (siteArrayList_opt3.size() > 0) {
                    binding.llHeading.setVisibility(View.GONE);
                    Log.e("ModelArrayList123", "" + siteArrayList_opt3.size());
                    Log.e("ModelArrayList", "" + siteArrayList_opt3.get(0).getSiteName());
                    Log.e("ModelArrayList", "" + siteArrayList_opt3.get(0).getSiteCreatedDate());
//                    AdapterSelfTimeLine adapterTimelineSelf = new AdapterSelfTimeLine(timelineActivity.this, siteArrayList_opt3);
//                    binding.rvTimelineSelf.setAdapter(adapterTimelineSelf);
//                    binding.rvTimelineSelf.setVisibility(View.VISIBLE);

                } else if (siteArrayList_opt3.size() == 0) {
                    binding.llHeading.setVisibility(View.VISIBLE);
                    binding.llTimelineHeading.setVisibility(View.VISIBLE);
//                    binding.llTimelineHeadingSelf.setVisibility(View.GONE);
//                    binding.rvTimelineSelf.setVisibility(View.GONE);
//                    binding.txtForgotPassword.setText("No site is added");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    progressDialog.dismiss();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        modelUser = child.getValue(ModelUser.class);
                        if (modelUser.getUserType().equals("HR Manager")) {
                            String str = modelUser.getName();
                            String[] strArray = str.split(" ");
                            StringBuilder builder = new StringBuilder();
                            for (String s : strArray) {
                                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                                builder.append(cap + " ");
                            }
                            if (getSharedPreferences("UserDetails", MODE_PRIVATE).getString("Language", "hi").equals("en")) {
                                binding.txtAdmin.setText(getString(R.string.hii) + " " + builder + " " + getString(R.string.hi_you_are_an_administrator1) + " " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("designation", ""));
                            } else {
                                binding.txtAdmin.setText(getString(R.string.hii) + " " + builder + " " + getString(R.string.hi_you_are_an_administrator1) + " " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("designationHindi", "") + " है");

                            }

                            Log.e("ModelUser", modelUser.getMobile());
                        } else {
                            binding.txtAdmin.setText(getString(R.string.hii) + modelUser.getName() + getString(R.string.you_are_an_associate));
                        }
                    }

                    companyName = modelUser.getCompanyName();
                    binding.txtComapnyName.setText(companyName);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getCompanyName() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                Log.e("curosr", "getCompanyName");
                Log.e("cursor", modelUser.getName());

                companyName = modelUser.getCompanyName();

                binding.txtComapnyName.setText(companyName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                Log.e("DataChange", "Yes");
                siteArrayList.clear();
                progressDialog.dismiss();
                Log.e("DateM", "Today12:::" + currentDate);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String date = ds.child("date").getValue(String.class);
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    Log.e("DateM", "Today:::" + (date == null));
                    if (date != null && !date.equals(currentDate)) {
                        Log.e("DateM", "ConTrue");
                        makeOffline(modelSite);
                    } else {
                        Log.e("DateM", "ConFalse");
                    }
                    if (selected_option == 1 && modelSite.getMemberStatus().equals("Registered")) {
                        siteArrayList.add(modelSite);
                    } else if (selected_option == 3) {
                        siteArrayList.add(modelSite);
                    }


                }
                if (siteArrayList.size() > 0) {
                    binding.btnSite.setVisibility(View.VISIBLE);
                    binding.btnAddAssociate.setVisibility(View.VISIBLE);
                    Log.e("Before","A::::"+siteArrayList.size());
                    Log.e("Before","B::::"+siteSpinnerList.size());
                   siteSpinnerList=new ArrayList<>(siteArrayList);
                    siteSpinnerList.add(0,new ModelSite(getString(R.string.select_site), 0, false));
                    Log.e("Before","2"+siteArrayList.size());
                    binding.llHeading.setVisibility(View.GONE);
                    binding.rvTimeline.setVisibility(View.VISIBLE);
                    Log.e("ModelArrayList", "" + siteArrayList.size());
                    Log.e("ModelArrayList", "" + siteArrayList.get(0).getSiteName());
                    Log.e("ModelArrayList", "" + siteArrayList.get(0).getSiteCreatedDate());
                    if (selected_option == 1) {
                        AdapterTimeline adapterTimeline = new AdapterTimeline(timelineActivity.this, siteArrayList);
                        binding.rvTimeline.setAdapter(adapterTimeline);
                    } else if (selected_option == 3) {
                        getSiteListSorted(siteArrayList);
                    }

                } else if (siteArrayList.size() == 0) {
                    binding.llHeading.setVisibility(View.VISIBLE);
                    binding.rvTimeline.setVisibility(View.GONE);
                    binding.llTimelineHeading.setVisibility(View.GONE);
                    binding.btnSite.setVisibility(View.GONE);
                    binding.btnAddAssociate.setVisibility(View.GONE);
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


    private void getSiteListSorted(ArrayList<ModelSite> siteArrayList) {
        siteSortedArrayList.clear();

        Log.e("SiteSorted",""+siteSortedArrayList.size());

        for (int i = 0; i < siteArrayList.size(); i++) {

            Log.e("Option3", "" + siteArrayList.get(i).getMemberStatus());
            Log.e("Option3", "" + siteArrayList.get(i).getSiteStatus());
            Log.e("Option3", "" + siteArrayList.size());
            if (siteArrayList.get(i).getMemberStatus().equals("Registered") && siteArrayList.get(i).getSiteStatus().equals("Active")) {
                siteSortedArrayList.add(siteArrayList.get(i));
            }
        }
        for (int i = 0; i < siteArrayList.size(); i++) {
            if (siteArrayList.get(i).getMemberStatus().equals("Pending") && siteArrayList.get(i).getSiteStatus().equals("Active")) {
                siteSortedArrayList.add(siteArrayList.get(i));
            }
        }
//        getSiteMemberCount(siteSortedArrayList);
        if (siteSortedArrayList.size() > 0) {
            AdapterTimeline adapterTimeline = new AdapterTimeline(timelineActivity.this, siteSortedArrayList);
            binding.rvTimeline.setAdapter(adapterTimeline);
        }

    }


    private void makeOffline(ModelSite modelSite) {
        Log.e("DateM", "MakeOffline");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", false);
        hashMap.put("skilled", 0);
        hashMap.put("unskilled", 0);
        hashMap.put("SkilledTime", "");
        hashMap.put("UnskilledTime", "");
        hashMap.put("memberOnline", 0);
        hashMap.put("picActivity", false);
        hashMap.put("picId", "");
        hashMap.put("picTime", "");
        hashMap.put("picDate", "");
        hashMap.put("picLink", "");
        hashMap.put("picRemark", "");
        hashMap.put("picLatitude", 0);
        hashMap.put("picLongitude", 0);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                getMemberList(modelSite);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failure", e.getMessage());
            }
        });
    }

    private void getMemberList(ModelSite modelSite) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(modelSite.getSiteId())).child("Members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelAssociate modelUser = ds.getValue(ModelAssociate.class);
                    makeOffLineUser(modelSite, modelUser.getMemberUid());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeOffLineUser(ModelSite modelSite, String memberUid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", false);
        hashMap.put("skilled", 0);
        hashMap.put("unskilled", 0);
        hashMap.put("SkilledTime", "");
        hashMap.put("UnskilledTime", "");
        hashMap.put("picActivity", false);
        hashMap.put("picId", "");
        hashMap.put("picTime", "");
        hashMap.put("picDate", "");
        hashMap.put("picLink", "");
        hashMap.put("picRemark", "");
        hashMap.put("picLatitude", 0);
        hashMap.put("picLongitude", 0);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        if(FirebaseAuth.getInstance().getUid()!=null&& memberUid!=null){
            reference.child(FirebaseAuth.getInstance().getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(modelSite.getSiteId())).child("Members")
                    .child(memberUid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.e("Done", "Member Offline");
                }
            });
        }

    }


//    // Create menu


    // Action menu


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile:

                startActivity(new Intent(timelineActivity.this, AboutActivity.class));
//                finish();
                return true;
            case R.id.language:
                startActivity(new Intent(timelineActivity.this, LanguageChange.class));
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
//                startActivity(new Intent(timelineActivity.this,AdminLoginOptions.class));
//                return true;
            case R.id.transaction:
                startActivity(new Intent(timelineActivity.this, TransactionListActivity.class));
                return true;
            case R.id.swapAdmin:
                startActivity(new Intent(timelineActivity.this, ActivitySowap.class));
                return true;
            case R.id.logout:
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(timelineActivity.this);
                builder.setTitle(R.string.log_out).setMessage(R.string.are_you_sure_you).setCancelable(false).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    firebaseAuth.signOut();
                    editor.clear();
                    editor.commit();
                    dialogInterface.dismiss();
//                                    startActivity(new Intent(timelineActivity.this,SplashActivity.class));
                    this.finishAffinity();
                }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(timelineActivity.this);
        builder.setTitle(getString(R.string.exit)).setMessage(R.string.are_you_sure_you_exit).setCancelable(true).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            this.finishAffinity();
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Activity", "On Resume Timeline");
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        if (!sharedpreferences.getString("Language", "hi").equals("en")) {
            context = LocaleHelper.setLocale(timelineActivity.this, "hi");
            my.updateLanguage(timelineActivity.this, sharedpreferences.getString("Language", "hi"));
        } else {
            context = LocaleHelper.setLocale(timelineActivity.this, "en");
            my.updateLanguage(timelineActivity.this, sharedpreferences.getString("Language", "en"));
        }
    }
    class SiteSpinnerAdapter
            extends BaseAdapter {
        SiteSpinnerAdapter() {
        }

        public int getCount() {
            return siteSpinnerList.size();
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
            textView.setText(siteSpinnerList.get(n).getSiteCity());
            return view2;
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
                number = number.replaceAll("\\s+","");
                Log.e("number",number);
                if(number.startsWith("+")) {
                    if (number.length() == 13) {
                        String str_getMOBILE = number.substring(3);
                        et_mobile_number.setText(str_getMOBILE);
                    } else if (number.length() == 14) {
                        String str_getMOBILE = number.substring(4);
                        et_mobile_number.setText(str_getMOBILE);
                    }
                }


//                }else if(number.contains(" ")){
//
//                    String[] strArray = number.split(" ");
//                    StringBuilder builder = new StringBuilder();
//                    for (String s : strArray) {
//                        String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
//                        builder.append(cap);
//                    }
//
//
//                }
                else
                {
                    et_mobile_number.setText(number);
                }


            }
        }
    }
}