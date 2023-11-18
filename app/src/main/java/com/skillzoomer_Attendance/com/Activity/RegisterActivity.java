package com.skillzoomer_Attendance.com.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.skillzoomer_Attendance.com.Model.ModelCategory;
import com.skillzoomer_Attendance.com.Model.ModelDesignation;
import com.skillzoomer_Attendance.com.Utilities.LocaleHelper;
import com.skillzoomer_Attendance.com.Model.ModelMember;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityRegisterBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;
import com.squareup.picasso.Picasso;

import pl.droidsonroids.gif.GifImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;
import uk.co.deanwild.materialshowcaseview.shape.Shape;
import uk.co.deanwild.materialshowcaseview.target.Target;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    LayoutToolbarBinding toolbarBinding;
    private SharedPreferences.Editor editor;
    private String[] designation={"HR Manager"};
    private String selectedDesignation="HR Manager";
    private ProgressDialog progressDialog;
    String verificationId;
    FirebaseAuth firebaseAuth;
    PhoneAuthCredential credential1;
    String userId;
    String password;
    String c_password;
    ModelMember modelMember;
    String mobile;
    long siteId=0;
    String siteName;
    String userEmail;
    String status;
    String companyName;
    private SharedPreferences.Editor editorLogin;
    private String hrUid="";
    private String name;
    private Boolean forceLogoutOpt;
    private ArrayList<ModelUser> modelUserArrayList;
    String registerMessage;
    Context context;
    long memberCount=0;
    public int counter = 30;
    private ArrayList<ModelDesignation> designationArrayList;
    private ArrayList<ModelCategory> categoryArrayList;
    long industryPosition=0;
    String industryName;
    String hrDesignation,hrDesignationHindi;

    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
        @Override
        public void onTick(long l) {
            int minute = counter / 60;
            int sec = counter % 60;
            if (minute > 0) {
                binding.txtForcedLogoutMsg.setText("" + minute + "minutes and " + sec + " seconds");
            } else {
                binding.txtForcedLogoutMsg.setText("" + sec + " seconds");
            }
            counter--;

        }

        @Override
        public void onFinish() {
            binding.txtResendOtp.setVisibility(View.VISIBLE);
            binding.llForceLogoutStart.setVisibility(View.GONE);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        toolbarBinding.heading.setText("Register");
        //progress dialog initialisation
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editorLogin = spLogin.edit();
        modelUserArrayList = new ArrayList<>();
        designationArrayList=new ArrayList<>();
        categoryArrayList=new ArrayList<>();
        getCategoryList();
        getDesignationList();
        binding.llForceLogoutStart.setVisibility(View.GONE);
        binding.txtResendOtp.setVisibility(View.GONE);


        Intent intent = getIntent();
        Log.e("IntentR", "" + (intent.getExtras() == null));
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("TermsAndCondition");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(sharedpreferences.getString("Language","hi").equals("en")){
                    binding.cbTermsNcond.setText(snapshot.child("text").getValue(String.class));
                }else{
                    binding.cbTermsNcond.setText(snapshot.child("textHindi").getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.cbTermsNcond.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(!TextUtils.isEmpty(binding.etName.getText().toString()) && !TextUtils.isEmpty(binding.etMobileNumber.getText().toString())){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            new MaterialShowcaseView.Builder(RegisterActivity.this)
                                    .setTarget(binding.btnSendOtp)
                                    .setDismissOnTouch(true)
                                    .setGravity(2)
                                    .withOvalShape()
                                    .setTargetTouchable(true)
                                    .renderOverNavigationBar()
                                    .setContentText(getString(R.string.content_register1))// optional but starting animations immediately in onCreate can make them choppy
                                    .setContentTextColor(getColor(R.color.white))
                                    .show();
                        }
                    }
                }
            }
        });
        if (intent.getExtras() != null) {
            mobile = intent.getStringExtra("mob");
            hrUid = intent.getStringExtra("admin");
            selectedDesignation = intent.getStringExtra("role");
            companyName = intent.getStringExtra("company");
            name = intent.getStringExtra("name");
            Log.e("CompanyName11", companyName);
            Log.e("CompanyName11", hrUid);
            binding.etCompanyName.setText(companyName);
            binding.etCompanyName.setClickable(false);
            binding.etCompanyName.setEnabled(false);
            binding.etCompanyName.setFocusable(false);
            siteId = intent.getIntExtra("site_id", 0);
            Log.e("SiteId", "" + siteId);
            String phone = "+91" + mobile;
//            progressDialog.setMessage("Verifying your Number");
//            progressDialog.show();
            Log.e("Phone1234",mobile);
            Log.e("Phone1234",phone);
            binding.etName.setText(name);
            binding.etMobileNumber.setText(mobile);
            binding.etMobileNumber.setEnabled(false);
//            checkForInvitedMembers(mobile,phone);


//            getDynmaicLinkPlayStore();

        }
        binding.etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()==10){
                    if(TextUtils.isEmpty(binding.etName.getText().toString())){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            new MaterialShowcaseView.Builder(RegisterActivity.this)
                                    .setTarget(binding.etName)
                                    .setGravity(2)
                                    .withRectangleShape(true)
                                    .setDismissOnTouch(true)
                                    .setContentText(getString(R.string.content_register2))// optional but starting animations immediately in onCreate can make them choppy
                                    .setContentTextColor(getColor(R.color.white))
                                    .show();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        MyApplication my = new MyApplication( );

        editor = sharedpreferences.edit();
        modelMember = new ModelMember();
        binding.txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("Activity", "Register");
                startActivity(intent);
            }
        });
        if(sharedpreferences.getString("Language","hi").equals("en")){
            binding.llLanguage.setVisibility(View.GONE);
        }else{
            binding.llLanguage.setVisibility(View.VISIBLE);
        }
        binding.spinnerSelectIndustry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(categoryArrayList.get(i).getName().equals("Custom")){
                    binding.etCustomIndustry.setVisibility(View.VISIBLE);
                }else{
                    binding.etCustomIndustry.setVisibility(View.GONE);
                }
                if(i>0){
                    binding.llDesignation.setVisibility(View.VISIBLE);
                    binding.llUserId.setVisibility(View.VISIBLE);
                    binding.txtAvail.setVisibility(View.GONE);
                    getDesignationList();
                    binding.btnNext.setVisibility(View.GONE);

                } else{
                    binding.llDesignation.setVisibility(View.GONE);
                    binding.llUserId.setVisibility(View.GONE);
                    binding.txtAvail.setVisibility(View.GONE);
                    binding.btnNext.setVisibility(View.GONE);
                }

//                if(i>0){
//                    binding.llDesignation.setVisibility(View.VISIBLE);
//                    binding.llUserId.setVisibility(View.VISIBLE);
//                    binding.txtAvail.setVisibility(View.GONE);
//                    getDesignationList();
//                    binding.btnNext.setVisibility(View.GONE);
//                }else{
//                    binding.txtAvail.setVisibility(View.GONE);
//                    getDesignationList();
//                    binding.btnNext.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.spinnerSelectIndustry.setSelection(1);
                binding.llDesignation.setVisibility(View.VISIBLE);
                binding.txtAvail.setVisibility(View.GONE);
                binding.btnNext.setVisibility(View.GONE);
            }
        });
        binding.btnShowTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String url="";
//                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("TermsAndCondition");
//                reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String url="";
//                        if(sharedpreferences.getString("Language","hi").equals("en")){
////                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(snapshot.child("link").getValue(String.class)));
//                            url=snapshot.child("link").getValue(String.class);
//                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
//
//                                intent.setDataAndType(Uri.parse(url), "application/pdf");
//                                startActivity(intent);
//
//
//                        }else{
////                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(snapshot.child("linkHindi").getValue(String.class)));
////                            intent.setDataAndType(Uri.parse(url), "application/pdf");
////                            startActivity(browserIntent);
//                            url=snapshot.child("linkHindi").getValue(String.class);
//                            WebView mWebView=new WebView(RegisterActivity.this);
//                            mWebView.getSettings().setJavaScriptEnabled(true);
//                            mWebView.loadUrl(url);
//                            setContentView(mWebView);
//
////                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
////                            intent.setDataAndType(Uri.parse(url), "application/pdf");
////                            startActivity(intent);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RegisterActivity.this);
                View mView = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_t_c, null);
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
        binding.spinnerSelectDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if(i==13){
                        binding.etCustomDesignation.setVisibility(View.VISIBLE);
                    }else{
                        binding.etCustomDesignation.setVisibility(View.GONE);
                    }
                    if(i>0){
                        ShowcaseConfig config = new ShowcaseConfig();
                        config.setDelay(300);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            config.setContentTextColor(getColor(R.color.white));
                        }
//                        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(RegisterActivity.this, "Register");
//
//                        sequence.setConfig(config);
//
//                        sequence.addSequenceItem(binding.etUserId,
//                                "Enter a unique userId. This would be required for login", "Dismiss");
//                        sequence.addSequenceItem(binding.etPassword,
//                                "A strong password mixture of numbers and alphabets is highly recommended", "Dismiss");
//
//                        sequence.start();
                    }






            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.cbChangeLabguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    binding.llLanguage.setVisibility(View.GONE);
                    if(sharedpreferences.getString("Language","hi").equals("en")){
                        editor.putString("Language","hi");
                        editor.apply();
                        editor.commit();
                        context = LocaleHelper.setLocale(RegisterActivity.this, "hi");
                        my.updateLanguage(RegisterActivity.this,"hi");
                        finish();
                        startActivity(getIntent());
                    }else{
                        editor.putString("Language","en");
                        editor.apply();
                        editor.commit();
                        context = LocaleHelper.setLocale(RegisterActivity.this, "en");
                        my.updateLanguage(RegisterActivity.this,"en");
                        finish();
                        startActivity(getIntent());
                    }

                }
            }
        });


        binding.llRegister.setVisibility(View.GONE);
        binding.llMobileNumber.setVisibility(View.VISIBLE);
        binding.llVerifyOtp.setVisibility(View.GONE);
        binding.llProfile.setVisibility(View.GONE);
        binding.txtMsgDisplay.setText(getString(R.string.register_your_self));
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
        binding.spinnerDesignation.setAdapter(spinnerAdapter);
        binding.spinnerDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedDesignation = designation[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                name = binding.etName.getText().toString().trim();
                mobile = binding.etMobileNumber.getText().toString().trim();
                String phone = "+91" + mobile;
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mobile) || mobile.length() < 10) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
                } else if(!binding.cbTermsNcond.isChecked()){
                    Toast.makeText(RegisterActivity.this, getString(R.string.accept_terms), Toast.LENGTH_SHORT).show();
                }else {
                    binding.llLanguage.setVisibility(View.GONE);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    checkForPhone(mobile,phone);



                }
            }

        });

        binding.txtResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Resend","+91"+binding.etMobileNumber.getText().toString().trim());
                binding.llForceLogoutStart.setVisibility(View.VISIBLE);
                binding.txtResendOtp.setVisibility(View.GONE);
                counter=30;
                countDownTimer.start();
                sendVerificationCode("+91"+binding.etMobileNumber.getText().toString().trim());
            }
        });
        binding.etUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(charSequence.toString().contains(" ")){
                   binding.etUserId.setError(getString(R.string.space_is_not_allowed));
               }
               if(charSequence.toString().contains("@")){
                   binding.etUserId.setError(getString(R.string.only_allowed));
               }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = binding.MobileOtpPinview.getText().toString().trim();
                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.otp_empty), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    verifyCode(binding.MobileOtpPinview.getText().toString());
                }
            }
        });
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userId = binding.etUserId.getText().toString();
                password = binding.etPassword.getText().toString();
                c_password = binding.etConfirmPassword.getText().toString();
                if (TextUtils.isEmpty(userId)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.enter_user_id_toast), Toast.LENGTH_SHORT).show();
                }else if(userId.contains(" ")|| userId.contains("@")){
                    Toast.makeText(RegisterActivity.this, getString(R.string.invalid_user_id), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.password_cannot_be_empty), Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.password_min_6_characters), Toast.LENGTH_SHORT).show();
                } else if (!password.equals(c_password)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show();
                }else if(selectedDesignation.equals("HR Manager")){
                    if(binding.spinnerSelectDesignation.getSelectedItemPosition()==0 ){
                        Toast.makeText(RegisterActivity.this, R.string.select_designation_your, Toast.LENGTH_SHORT).show();
                    } else if(binding.spinnerSelectDesignation.getSelectedItemPosition()==13 && TextUtils.isEmpty(binding.etCustomDesignation.getText().toString())) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.enter_custom_designation), Toast.LENGTH_SHORT).show();
                    }else{
                        checkUserId(credential1, userId, password);
                    }

                }else {

                    checkUserId(credential1, userId, password);

                }
            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.etCompanyName.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.enter_company_name), Toast.LENGTH_SHORT).show();
                } else if (selectedDesignation.equals("Select Designation")) {
                    Toast.makeText(RegisterActivity.this, "Select Designation", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(binding.etAddressName.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.enter_company_address), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(binding.etEmailName.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.enter_company_email), Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.show();

                    userEmail = binding.etUserId.getText().toString() + "@yopmail.com";
                    firebaseAuth.createUserWithEmailAndPassword(userEmail, c_password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    saveDataToFirebase(binding.etUserId.getText().toString(),
                                            binding.etPassword.getText().toString());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.e("Exception", "" + e.getMessage());
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                }

            }
        });
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

    }

    private void checkForPhone(String mobile, String phone) {
        Log.e("VerifyPhone----------",binding.etMobileNumber.getText().toString());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("Snapshot",""+snapshot.exists());
                        Boolean register=true;

                        Log.e("SelectedDesignation",selectedDesignation);
                        if(snapshot.exists()){

                            for(DataSnapshot ds:snapshot.getChildren()){
                                if(ds.child("mobile").getValue(String.class)!=null &&
                                        ds.child("mobile").getValue(String.class).equals(binding.etMobileNumber.getText().toString())&&
                                        selectedDesignation.equals("HR Manager")&& ds.child("userType").equals("HR Manager") ) {
                                    Log.e("Mobile1234",ds.child("mobile").getValue(String.class));
                                    Log.e("Mobile1234",binding.etMobileNumber.getText().toString());
                                    register=false;
                                }

                            }
                            if(!register){
                                progressDialog.dismiss();
                                Log.e("error","here");
                                Toast.makeText(RegisterActivity.this, getString(R.string.mobile_already_registered), Toast.LENGTH_SHORT).show();
                            } else{
                                checkForInvitedMembers(mobile,phone);

                            }


                        }else{
                            checkForInvitedMembers(mobile,phone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();

                    }
                });

    }

    private void checkForInvitedMembers(String mobile, String phone) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("InvitedMembers");
        reference.child(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Log.e("Snapshot","Exist");

                    hrUid = snapshot.child("Hruid").getValue(String.class);
                    name=snapshot.child("MemberName").getValue(String.class);
                    forceLogoutOpt=snapshot.child("forceOpt").getValue(Boolean.class);
                    selectedDesignation ="Supervisor";
                    companyName = snapshot.child("companyName").getValue(String.class);
                    industryPosition=snapshot.child("industryPosition").getValue(long.class);
                    industryName=snapshot.child("industryName").getValue(String.class);
                    hrDesignation=snapshot.child("hrDesignation").getValue(String.class);
                    hrDesignationHindi=snapshot.child("hrDesignationHindi").getValue(String.class);

                    name = binding.etName.getText().toString();
                    Log.e("CompanyName11", companyName);
                    binding.etCompanyName.setText(companyName);
                    binding.etCompanyName.setClickable(false);
                    binding.etCompanyName.setEnabled(false);
                    binding.etCompanyName.setFocusable(false);

                    siteId =snapshot.child("siteId").getValue(long.class);
                    Log.e("SiteId", "" + siteId);
                    String phone = "+91" + mobile;
                    progressDialog.setMessage("Verifying your Number");
//                    progressDialog.show();
                    getMemberCount(siteId,phone,hrUid);


                }else{
                    sendVerificationCode(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getCategoryList() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("CategoryMaster");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelCategory modelCategory=ds.getValue(ModelCategory.class);
                    categoryArrayList.add(modelCategory);
                }
                SpinnerCategoryAdapter spinnerCategoryAdapter=new SpinnerCategoryAdapter();
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
    private void getDesignationList() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("DesignationMaster");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                designationArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelDesignation modelCategory=ds.getValue(ModelDesignation.class);
                    designationArrayList.add(modelCategory);
                }
                SpinnerDesignationAdapter spinnerDesignationAdapter=new SpinnerDesignationAdapter();
                binding.spinnerSelectDesignation.setAdapter(spinnerDesignationAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    private void getMemberCount(long siteId, String mobile, String phone) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(phone).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.e("snapshot","Ada"+snapshot.getChildrenCount());
                    memberCount=snapshot.getChildrenCount();
                    getCompanyDetails(mobile,phone);
                }else{
                    memberCount=0;
                    getCompanyDetails(mobile,phone);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getCompanyDetails(String phone, String hrUid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
               reference.child(hrUid) .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        binding.etAddressName.setText( snapshot.child("companyAddress").getValue(String.class));
                        binding.etAddressName.setFocusable(false);
                        binding.etAddressName.setEnabled(false);
                        binding.etEmailName.setText(snapshot.child("companyEmail").getValue(String.class));
                        binding.etEmailName.setFocusable(false);
                        binding.etEmailName.setEnabled(false);
                        binding.etWebsiteName.setFocusable(false);
                        binding.etWebsiteName.setEnabled(false);
                        if(snapshot.child("companyWebsite").getValue(String.class)!=null && !snapshot.child("companyWebsite").getValue(String.class).equals("")){
                            binding.etWebsiteName.setText(snapshot.child("companyWebsite").getValue(String.class));
                        }else{
                            binding.etWebsiteName.setVisibility(View.GONE);
                        }
                        sendVerificationCode(phone);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void showProfilePage() {
        binding.llRegister.setVisibility(View.GONE);
        binding.llMobileNumber.setVisibility(View.GONE);
        binding.llVerifyOtp.setVisibility(View.GONE);
        binding.llProfile.setVisibility(View.VISIBLE);

    }

    private void saveDataToFirebase(String userId,String password) {
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",firebaseAuth.getUid());
        hashMap.put("userId",userId);
        hashMap.put("password",password);
        hashMap.put("name",name);
        hashMap.put("mobile",mobile);
        hashMap.put("userType",selectedDesignation);
        hashMap.put("timestamp",timestamp);
        hashMap.put("companyName",binding.etCompanyName.getText().toString());
        hashMap.put("companyAddress",binding.etAddressName.getText().toString());
        hashMap.put("companyEmail",binding.etEmailName.getText().toString());
        hashMap.put("companyWebsite",binding.etWebsiteName.getText().toString());
        hashMap.put("industryCount",1);
        hashMap.put("designationPosition",binding.spinnerSelectDesignation.getSelectedItemPosition());
        if (categoryArrayList.get(binding.spinnerSelectIndustry.getSelectedItemPosition()).getName().equals("Custom")){
            hashMap.put("industryPosition",binding.spinnerSelectIndustry.getSelectedItemPosition());
            hashMap.put("industryName",binding.etCustomIndustry.getText().toString());
            hashMap.put("industryNameHindi",binding.etCustomIndustry.getText().toString());
        }else if(binding.spinnerSelectIndustry.getSelectedItemPosition()>0){
            hashMap.put("industryPosition",binding.spinnerSelectIndustry.getSelectedItemPosition());
            hashMap.put("industryName",categoryArrayList.get(binding.spinnerSelectIndustry.getSelectedItemPosition()).getName());
            hashMap.put("industryNameHindi",categoryArrayList.get(binding.spinnerSelectIndustry.getSelectedItemPosition()).getHindiName());
        }
        if(designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getName().equals("Custom")){
            hashMap.put("designationName",binding.etCustomDesignation.getText().toString());
            hashMap.put("designationNameHindi",binding.etCustomDesignation.getText().toString());
        }else{
            hashMap.put("designationName",designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getName());
            hashMap.put("designationNameHindi",designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getNameHindi());
        }
        hashMap.put("dateOfRegister",currentDate);
        if(selectedDesignation.equals("Supervisor")){
            hashMap.put("siteId",siteId);
            hashMap.put("siteName",siteName);
            hashMap.put("memberBlock","Active");
            hashMap.put("forceOpt",forceLogoutOpt);
            hashMap.put("industryPosition",industryPosition);
            hashMap.put("industryName",industryName);
            hashMap.put("hrDesignation",hrDesignation);
            hashMap.put("hrDesignationHindi",hrDesignationHindi);

        }
        if(!hrUid.equals("")) {
            hashMap.put("hrUid", hrUid);
        }
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

//                        editor.putString("uid",firebaseAuth.getUid());
//                        editor.putString("userName",binding.etName.getText().toString());
//                        editor.putString("userDesignation",selectedDesignation);
//                        editor.putString("userMobile",binding.etMobileNumber.getText().toString());
//                        if(siteId>0){
//                            editor.putLong("siteId",siteId);
//                            editor.putString("siteName",siteName);
//                        }
//                        editor.apply();
                        String role="";
                        if(selectedDesignation.equals("Supervisor")){
                            role=getString(R.string.associate);
                            registerMessage=getString(R.string.hii)+" "+binding.etName.getText().toString()+", "+binding.etCompanyName.getText().toString()+" "+getString(R.string.successfully_registered1);
                        }else{
                            role="Administrator";
                            if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Language","hi").equals("en")){
                                if(binding.spinnerSelectDesignation.getSelectedItemPosition()==13){
                                    registerMessage=getString(R.string.hii)+" "+binding.etName.getText().toString()+", "+binding.etCompanyName.getText().toString()+" company assigns You"+binding.etCustomDesignation.getText().toString()+ "for Hajiri Register App.You have Successfully Registered. Start your day with your user Id and Password";

                                }else{
                                    registerMessage=getString(R.string.hii)+" "+binding.etName.getText().toString()+", "+binding.etCompanyName.getText().toString()+"company assigns You"+designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getName()+ "for Hajiri Register App.You have Successfully Registered. Start your day with your user Id and Password";

                                }
                            }else{
                                if(binding.spinnerSelectDesignation.getSelectedItemPosition()==13){
                                    registerMessage=getString(R.string.hii)+" "+binding.etName.getText().toString()+", "+binding.etCompanyName.getText().toString()+" कंपनी के लिए हाजिरी रजिस्टर में आप"+" "+ binding.etCustomDesignation.getText().toString() +" हैं | आप ने सफलतापूर्वक पंजीकरण कर लिया है | यूजर आईडी और पासवर्ड से अपने दिन की शुरुआत करें ";

                                }else{
                                    registerMessage=getString(R.string.hii)+" "+binding.etName.getText().toString()+", "+binding.etCompanyName.getText().toString()+" कंपनी के लिए हाजिरी रजिस्टर में आप"+" "+designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getNameHindi()+ " हैं | आप ने सफलतापूर्वक पंजीकरण कर लिया है | यूजर आईडी और पासवर्ड से अपने दिन की शुरुआत करें ";

                                }
                            }



                        }
                        AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle(getString(R.string.hii) +" "+name)

                                .setMessage(registerMessage)
                                .setPositiveButton(R.string.lets_work , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface , int i) {
                                        if(selectedDesignation.equals("HR Manager")) {
                                            HashMap<String,Object> hashMap1=new HashMap<>();
                                            hashMap1.put("industryPosition",binding.spinnerSelectIndustry.getSelectedItemPosition());
                                            hashMap1.put("industryName",categoryArrayList.get(binding.spinnerSelectIndustry.getSelectedItemPosition()).getName());
                                            hashMap1.put("industryNameHindi",categoryArrayList.get(binding.spinnerSelectIndustry.getSelectedItemPosition()).getHindiName());
                                            hashMap1.put("companyName",binding.etCompanyName.getText().toString());
                                            hashMap1.put("companyEmail",binding.etEmailName.getText().toString());
                                            hashMap1.put("designationPosition",binding.spinnerSelectDesignation.getSelectedItemPosition());
                                            if(designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getName().equals("Custom")){
                                                hashMap1.put("designationName",binding.etCustomDesignation.getText().toString());
                                                hashMap1.put("designationNameHindi",binding.etCustomDesignation.getText().toString());
                                            }else{
                                                hashMap1.put("designationName",designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getName());
                                                hashMap1.put("designationNameHindi",designationArrayList.get(binding.spinnerSelectDesignation.getSelectedItemPosition()).getNameHindi());
                                            }
                                            Log.e("DKDHGH",categoryArrayList.get(binding.spinnerSelectIndustry.getSelectedItemPosition()).getName());
                                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                                            reference.child(firebaseAuth.getUid()).child("Industry").child(categoryArrayList.get(binding.spinnerSelectIndustry.getSelectedItemPosition()).getName()).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    editorLogin.putString("LastLogin","Register");
                                                    editorLogin.putBoolean("LoginFirstTime",false);
                                                    editorLogin.commit();
                                                    firebaseAuth.signOut();
                                                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                                    intent.putExtra("Activity","Register");
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            });
                                        }else{
                                            getRoleFromInvitedMembers(mobile);

                                        }
                                    }
                                });
                        builder.show();



                        Log.e("Registration","Successful");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
//                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
//                        finish();
                        Log.e("Registration","Failure:"+e.getMessage());

                    }
                });



    }

    private void getRoleFromInvitedMembers(String mobile) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("InvitedMembers");
        reference.child(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String hrUid = snapshot.child("Hruid").getValue(String.class);
                    Boolean attendanceManagement = snapshot.child("attendanceManagement").getValue(Boolean.class);
                    Boolean cashManagement = snapshot.child("cashManagement").getValue(Boolean.class);
                    Boolean financeManagement = snapshot.child("financeManagement").getValue(Boolean.class);
                    Boolean workActivity = snapshot.child("workActivity").getValue(Boolean.class);
                    Boolean forceLogout = snapshot.child("forceLogout").getValue(Boolean.class);
                    String name = snapshot.child("MemberName").getValue(String.class);
                    Log.e("MANAGEMENT", "" + attendanceManagement);
                    Log.e("MANAGEMENT", "" + cashManagement);
                    Log.e("MANAGEMENT", "" + financeManagement);
                    Log.e("MANAGEMENT", "" + workActivity);
                    updateInSiteDatabase(hrUid, attendanceManagement, cashManagement, financeManagement, workActivity,forceLogout,name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeFromInvitedMembers(String mobile, String hrUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("InvitedMembers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(mobile)){
                    reference.child(mobile).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("Member Removed","yes");
                            updateToSiteMasterDatabase(hrUid);


                        }
                    });
                }else{
                    firebaseAuth.signOut();
                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                    intent.putExtra("Activity","Register");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        updateInSiteDatabase();
    }

    private void updateToSiteMasterDatabase(String hrUid) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("memberStatus","Registered");
        hashMap.put("memberCount",memberCount+1);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(hrUid).child("Industry").child("Construction").child("Site").child(String.valueOf((siteId))).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseAuth.signOut();
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                intent.putExtra("Activity","Register");
                startActivity(intent);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateInSiteDatabase(String hrUid, Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement, Boolean workActivity, Boolean forceLogout, String name) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("memberStatus","Registered");
        hashMap.put("memberUid",firebaseAuth.getUid());
        hashMap.put("hruid",hrUid);
        hashMap.put("attendanceManagement",attendanceManagement);
        hashMap.put("cashManagement",cashManagement);
        hashMap.put("financeManagement",financeManagement);
        hashMap.put("workActivity",workActivity);
        hashMap.put("mobile",mobile);
        hashMap.put("forceLogout",forceLogout);
        hashMap.put("name",name);
        hashMap.put("online",false);
        hashMap.put("forceLogout",false);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");

        reference.child(hrUid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                editorLogin.putString("LastLogin","Register");
                editorLogin.commit();
                saveToMemberDatabase(hrUid,attendanceManagement,cashManagement,financeManagement,workActivity,forceLogout);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Failure",e.getMessage());
            }
        });
    }

    private void saveToMemberDatabase(String hrUid, Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement, Boolean workActivity, Boolean forceLogout) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("memberStatus","Registered");
        hashMap.put("hruid",hrUid);
        hashMap.put("attendanceManagement",attendanceManagement);
        hashMap.put("cashManagement",cashManagement);
        hashMap.put("financeManagement",financeManagement);
        hashMap.put("workActivity",workActivity);
        hashMap.put("forceLogout",forceLogout);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                removeFromInvitedMembers(mobile,hrUid);
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
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        Log.e("SelectedDesignation",selectedDesignation);
        checkExistingUser(credential);
    }

    private void checkExistingUser(PhoneAuthCredential credential) {
        Log.e("VerifyPhone----------",binding.etMobileNumber.getText().toString());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("Snapshot",""+snapshot.exists());
                        Boolean register=true;
                        progressDialog.dismiss();
                        Log.e("SelectedDesignation",selectedDesignation);
                        if(snapshot.exists()){

                            for(DataSnapshot ds:snapshot.getChildren()){
                                if(ds.child("mobile").getValue(String.class)!=null && ds.child("mobile").getValue(String.class).equals(binding.etMobileNumber.getText().toString())&&selectedDesignation.equals("HR Manager") ) {
                                    Log.e("Mobile1234",ds.child("mobile").getValue(String.class));
                                    Log.e("Mobile1234",binding.etMobileNumber.getText().toString());
                                    register=false;
                                }

                            }
                            if(!register){
                                Log.e("error","here");
                                Toast.makeText(RegisterActivity.this, getString(R.string.mobile_already_registered), Toast.LENGTH_SHORT).show();
                            } else{
                               // Log.d("hiii","checkExistingUser: if(snapshot.hasChild(binding.etMobileNumber.getText().toString())):else");
                                Log.d("hiii","checkExistingUser: else");
                                final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RegisterActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
                                TextView txt_title = mView.findViewById(R.id.txt_title);
                                TextView txt_msg = mView.findViewById(R.id.txt_msg);
                                GifImageView iv_gif = mView.findViewById(R.id.iv_gif);
                                Button btn_start = mView.findViewById(R.id.btn_start);


                                txt_title.setText(getString(R.string.welcome));
                                if(selectedDesignation.equals("Supervisor")){
                                    binding.llDesignation.setVisibility(View.GONE);
                                    binding.llIndustry.setVisibility(View.GONE);
                                    binding.llUserId.setVisibility(View.VISIBLE);

                                    txt_msg.setText(getString(R.string.hii)+" "+name+" "+getString(R.string.turn_to_be_associate));
                                }else{
                                    binding.llDesignation.setVisibility(View.VISIBLE);
                                    binding.llIndustry.setVisibility(View.VISIBLE);
                                    binding.llUserId.setVisibility(View.GONE);

                                    txt_msg.setText(getString(R.string.hii)+" "+binding.etName.getText().toString()+" "+getString(R.string.turn_to_be_administrator) );
                                }

                                alert.setView(mView);
                                final android.app.AlertDialog alertDialog = alert.create();
                                alertDialog.setCanceledOnTouchOutside(true);
                                btn_start.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showRegisterPage(credential);
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                            }


                        }else{
                            Log.d("hiii","checkExistingUser: else");
                                final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RegisterActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
                                TextView txt_title = mView.findViewById(R.id.txt_title);
                                TextView txt_msg = mView.findViewById(R.id.txt_msg);
                                GifImageView iv_gif = mView.findViewById(R.id.iv_gif);
                                Button btn_start = mView.findViewById(R.id.btn_start);

                                txt_title.setText("Welcome");
                            if(selectedDesignation.equals("HR Manager")){
                                txt_msg.setText(getString(R.string.hii)+" "+binding.etName.getText().toString()+" "+getString(R.string.turn_to_be_administrator));
                            }else{

                                txt_msg.setText(getString(R.string.hii)+" "+name+getString(R.string.turn_to_be_associate));
                            }
                                 alert.setView(mView);
                                 final android.app.AlertDialog alertDialog = alert.create();
                                    alertDialog.setCanceledOnTouchOutside(true);
                                btn_start.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showRegisterPage(credential);
                                       alertDialog.dismiss();
                                    }
                                });

                                alertDialog.show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();

                    }
                });
    }


    private void showRegisterPage(PhoneAuthCredential credential) {
        progressDialog.dismiss();
        binding.txtMsgDisplay.setText(getString(R.string.register_your_self));


        binding.llRegister.setVisibility(View.VISIBLE);


        binding.llMobileNumber.setVisibility(View.GONE);
        binding.llVerifyOtp.setVisibility(View.GONE);
        binding.llProfile.setVisibility(View.GONE);
        credential1=credential;

    }

    private void checkUserId(PhoneAuthCredential credential,String userId,String password) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("userId").equalTo(binding.etUserId.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            progressDialog.dismiss();
                            Log.e("snapshotChildren",""+snapshot.getChildrenCount());
                            Log.e("User Id","Taken");
                            Toast.makeText(RegisterActivity.this, R.string.userIId_already_taken, Toast.LENGTH_LONG).show();
                        }
                        else{
                            showProfilePage();

//                            signInWithCredential(credential);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressDialog.dismiss();
                        binding.txtMsgDisplay.setText(getString(R.string.register_your_self));
                        binding.llRegister.setVisibility(View.GONE);
                        binding.llMobileNumber.setVisibility(View.GONE);
                        binding.llVerifyOtp.setVisibility(View.VISIBLE);
                        binding.llProfile.setVisibility(View.GONE);
                        binding.llForceLogoutStart.setVisibility(View.VISIBLE);
                        binding.txtResendOtp.setVisibility(View.GONE);
                        countDownTimer.start();
                        verificationId = s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                        Log.e("Verification","Completed");
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressDialog.dismiss();
                        Log.e("Resend",e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

//    private void signInWithCredential(PhoneAuthCredential credential) {
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful())
//                        {
//
//                            Log.e("Task","successful");
//
//
//                        } else {
//                            progressDialog.dismiss();
//                            Log.e("Task",""+task.getException().getMessage().toString());
//                            Toast.makeText(getApplicationContext(),"Some Error Occurred",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//
//    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if(binding.llRegister.getVisibility()==View.VISIBLE){
//            binding.llRegister.setVisibility(View.GONE);
//            binding.llMobileNumber.setVisibility(View.GONE);
//            binding.llVerifyOtp.setVisibility(View.VISIBLE);
//        }else if(binding.llVerifyOtp.getVisibility()==View.VISIBLE){
//            binding.llRegister.setVisibility(View.GONE);
//            binding.llMobileNumber.setVisibility(View.VISIBLE);
//            binding.llVerifyOtp.setVisibility(View.GONE);
//        }else if(binding.llMobileNumber.getVisibility()==View.VISIBLE){
//            onBackPressed();
//        }
//    }
}