package com.skillzoomer_Attendance.com.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityLabourRegistrationBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;
import com.squareup.picasso.Picasso;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class LabourRegistration extends AppCompatActivity {
    ActivityLabourRegistrationBinding binding;
    LayoutToolbarBinding toolbarBinding;
    FirebaseAuth firebaseAuth;
    ModelUser modelUser;
    String uid, userName;
    private ProgressDialog progressDialog;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    String editTextSelectionButtonId = "";
    ArrayList<String> result;
    private String[] type = {"Select Type", "Skilled", "Unskilled"};
    String selectedType;
    private final int REQUEST_IMAGE = 400;
    private final int REQUEST_GALLERY = 401;
    private Bitmap image_uri;
    private Uri image_uri1;
    private ArrayList<ModelSite> siteArrayList;
    private ArrayList<String> siteName;
    private String selected_site_code = "";
    private String selected_site_name;
    private String userType;
    String labourId;
    String currentDate;
    String show;
    String memberStatus = "";
    String uid1;
    private ArrayList<ModelLabour> labourArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLabourRegistrationBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        toolbarBinding = binding.toolbarLayout;
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        toolbarBinding.heading.setText("Add Labour");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        labourArrayList = new ArrayList<>();
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        modelUser = new ModelUser();
        siteArrayList = new ArrayList<>();
        binding.llFormLabourRegister.setVisibility(View.GONE);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            currentDate = df.format(c);
        }

        siteName = new ArrayList();
        Intent intent = getIntent();
        if (intent != null) {
            show = intent.getStringExtra("Activity");
            Log.e("Show", show);

            if (show != null && show.equals("Main")) {
                binding.llSelectWorkerType.setVisibility(View.VISIBLE);
                binding.llFormLabourRegister.setVisibility(View.GONE);
                selected_site_name = intent.getStringExtra("siteName");
                selected_site_code = intent.getStringExtra("siteId");
            } else if (show != null && show.equals("MasterData")) {
                binding.llSelectWorkerType.setVisibility(View.GONE);
                binding.llFormLabourRegister.setVisibility(View.VISIBLE);
                selectedType = intent.getStringExtra("type");
                selected_site_code = intent.getStringExtra("siteId");
                selected_site_name = intent.getStringExtra("siteName");
            } else if (show != null && show.equals("Pending")) {
                binding.llSelectWorkerType.setVisibility(View.GONE);
                binding.llFormLabourRegister.setVisibility(View.VISIBLE);
                selectedType = intent.getStringExtra("type");
                selected_site_code = String.valueOf(intent.getLongExtra("siteId", 0));
                Log.e("SiteId", "Pending" + selected_site_code);
                selected_site_name = intent.getStringExtra("siteName");
                binding.txtLabourName.setText(intent.getStringExtra("name"));
                binding.txtLabourName.setEnabled(false);
                binding.ivNameMic.setVisibility(View.GONE);
                if (!intent.getStringExtra("fatherName").equals("")) {
                    binding.txtLabourFathersName.setText(intent.getStringExtra("fatherName"));
                    binding.txtLabourFathersName.setEnabled(false);
                    binding.ivFatherNameMic.setVisibility(View.GONE);
                }
                if (!intent.getStringExtra("uniqueId").equals("")) {
                    binding.txtLabourIdNumber.setText(intent.getStringExtra("uniqueId"));
                    binding.txtLabourIdNumber.setEnabled(false);
                    binding.ivVerificationIdMic.setVisibility(View.GONE);
                }

                binding.txtLabourWages.setText(String.valueOf(intent.getLongExtra("wages", 0)));
                binding.txtLabourWages.setEnabled(false);
                binding.rlProfile.setVisibility(View.VISIBLE);
                if (intent.getStringExtra("profile") != null && !intent.getStringExtra("profile").equals("")) {
                    Picasso.get().load(intent.getStringExtra("profile")).
                            resize(400, 400).centerCrop()
                            .placeholder(R.drawable.ic_download).into(binding.profileImageView);
                }


            } else if (show != null && show.equals("Attendance")) {
                binding.llSelectWorkerType.setVisibility(View.GONE);
                binding.llFormLabourRegister.setVisibility(View.VISIBLE);
                selectedType = intent.getStringExtra("type");
                selected_site_code = intent.getStringExtra("siteId");
                selected_site_name = intent.getStringExtra("siteName");
                binding.txtLabourName.setText(intent.getStringExtra("searchName"));

            }


//            binding.llFormLabourRegister.setVisibility(View.VISIBLE);
            if (selectedType != null) {
                if (selectedType.equals("Skilled") && (show.equals("Main") || (show.equals("MasterData")))) {
                    binding.addLabourBtn.setText(R.string.add_skilled_worker);
                } else if (selectedType.equals("Unskilled") && (show.equals("Main") || (show.equals("MasterData")))) {
                    binding.addLabourBtn.setText(R.string.add_unskilled_worker);
                } else {
                    binding.addLabourBtn.setText(R.string.confirm_details);
                }
            }
        } else {

            selectedType = "";
        }


        binding.UserCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedType = "Skilled";
                binding.addLabourBtn.setText(R.string.add_skilled_worker);
                binding.llFormLabourRegister.setVisibility(View.VISIBLE);
                binding.UserCard.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));
                binding.DriverCard.setCardBackgroundColor(getResources().getColor(R.color.white));

                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(200);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    config.setContentTextColor(getColor(R.color.white));
                    config.setMaskColor(getColor(R.color.black));

                }

                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(LabourRegistration.this, "Labour");

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
                sequence.singleUse("Labour");


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourName)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour5))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.ivNameMic)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour4))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourFathersName)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour3))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourIdNumber)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour2))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourIdNumber)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour_1))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());

                    sequence.start();
                }

            }
        });
        binding.DriverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedType = "Unskilled";
                binding.addLabourBtn.setText(R.string.add_unskilled_worker);
                binding.llFormLabourRegister.setVisibility(View.VISIBLE);
                binding.UserCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                binding.DriverCard.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));

                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(200);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    config.setContentTextColor(getColor(R.color.white));
                    config.setMaskColor(getColor(R.color.black));

                }

                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(LabourRegistration.this, "Labour");

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
                sequence.singleUse("Labour");


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourName)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour5))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.ivNameMic)
                            .setGravity(Gravity.BOTTOM)
                            .withOvalShape()
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour4))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourFathersName)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour3))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourIdNumber)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour2))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());
                    sequence.addSequenceItem(new MaterialShowcaseView.Builder(LabourRegistration.this)
                            .setTarget(binding.txtLabourIdNumber)
                            .setGravity(Gravity.BOTTOM)
                            .withRectangleShape(true)
                            .setShapePadding(10)
                            .setTargetTouchable(false)
                            .setContentText(getString(R.string.labour_1))// optional but starting animations immediately in onCreate can make them choppy
                            .setContentTextColor(getColor(R.color.white))
                            .setDismissOnTouch(true)
                            .setShapePadding(20)
                            .build());

                    sequence.start();
                }


            }
        });

        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType = sharedpreferences.getString("userDesignation", "");
        if (userType.equals("Supervisor")) {
            uid1 = sharedpreferences.getString("hrUid", "");
        } else {
            uid1 = sharedpreferences.getString("uid", "");
        }
        if (userType.equals("Supervisor")) {
            selected_site_name = sharedpreferences.getString("siteName", "");
            selected_site_code = String.valueOf(sharedpreferences.getLong("siteId", 0));
        }

        Log.e("Site1st", "A" + selected_site_code);
        labourId = selected_site_code + "1";
        if (selected_site_code == "0" || selected_site_name.equals("")) {
            selected_site_name = intent.getStringExtra("siteName");
            selected_site_code = intent.getStringExtra("siteId");
            Log.e("Site1st", "B" + selected_site_code);
        }


        Log.e("selected_site_name", selected_site_name);
        Log.e("selected_site_code", "" + selected_site_code);
        getSiteinfo();
        updateLabourId();
        uid = firebaseAuth.getCurrentUser().getUid();
////        SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
////        binding.spinnerLabourType.setAdapter(spinnerAdapter);
////        binding.spinnerLabourType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
////                selectedType = type[position];
////
////            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        //onClickMic
        binding.ivNameMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSelectionButtonId = "Name";
                setEditText(binding.txtLabourName);

            }
        });
        binding.ivFatherNameMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSelectionButtonId = "Father's Name";
                setEditText(binding.txtLabourFathersName);
            }
        });
        binding.ivVerificationIdMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSelectionButtonId = "Verification Id";
                setEditText(binding.txtLabourIdNumber);
            }
        });
//        binding.ivSiteNameMic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editTextSelectionButtonId = "Site";
//                Intent intent
//                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//                        Locale.ENGLISH);
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
//
//
//                try {
//
//                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//                } catch (Exception e) {
//                    Toast
//                            .makeText(LabourRegistration.this, " " + e.getMessage(),
//                                    Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
        //TextChanged Listener
        binding.txtLabourName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    binding.ivNameDelete.setVisibility(View.VISIBLE);
                } else {
                    binding.ivNameDelete.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        binding.txtLabourFathersName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    binding.ivFatherNameDelete.setVisibility(View.VISIBLE);
                } else {
                    binding.ivFatherNameDelete.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        binding.txtLabourIdNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    binding.ivVerificationIdDelete.setVisibility(View.VISIBLE);
                } else {
                    binding.ivVerificationIdDelete.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        binding.txtLabourWages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    binding.rlProfile.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        new MaterialShowcaseView.Builder(LabourRegistration.this)
                                .setTarget(binding.editImageView)
                                .setGravity(2)
                                .withOvalShape()
                                .setTargetTouchable(true)
                                .setContentText(getString(R.string.click_picture))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(getColor(R.color.white))
                                .singleUse("addSite")
                                .setDismissOnTouch(true)
                                .show();
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        binding.txtLabourSiteName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.length() != 0) {
//                    binding.ivSiteNameDelete.setVisibility(View.VISIBLE);
//                } else {
//                    binding.ivSiteNameDelete.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                binding.txtLabourSiteName.showDropDown();
//
//
//            }
//        });
        //Delete Button functionality
        binding.ivNameDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtLabourName.getText().clear();
            }
        });
        binding.ivFatherNameDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtLabourFathersName.getText().clear();
            }
        });
        binding.ivVerificationIdDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtLabourIdNumber.getText().clear();
            }
        });
        binding.ivSiteNameDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtLabourSiteName.getText().clear();
            }
        });
        binding.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionCheck();
            }
        });

        binding.addLabourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("UserType", userType);
                if (show.equals("Pending")) {
                    if (intent.getStringExtra("profile") == null || intent.getStringExtra("profile").equals("")) {
                        if (image_uri == null) {
                            Toast.makeText(LabourRegistration.this, getString(R.string.take_a_photo_of_labour), Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(binding.txtLabourIdNumber.getText().toString())) {
                            Toast.makeText(LabourRegistration.this, "Enter the Unique Id/Mobile No. of worker", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.show();
                            getLabourList("Supervisor", intent.getStringExtra("labourId"));

                        }
                    } else {
                        if (TextUtils.isEmpty(binding.txtLabourIdNumber.getText().toString())) {
                            Toast.makeText(LabourRegistration.this, "Enter the Unique Id/Mobile No. of worker", Toast.LENGTH_SHORT).show();
                        } else {
                            image_uri1 = Uri.parse(intent.getStringExtra("profile"));
                            progressDialog.show();
                            getLabourList("Supervisor", intent.getStringExtra("labourId"));

                        }

                    }

                } else {
                    if (TextUtils.isEmpty(binding.txtLabourName.getText().toString())) {
                        Toast.makeText(LabourRegistration.this, "Enter Labour Name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.txtLabourWages.getText().toString())) {
                        Toast.makeText(LabourRegistration.this, "Enter Labour per day Wages", Toast.LENGTH_SHORT).show();
                    } else if (selectedType == "Select Type") {
                        Toast.makeText(LabourRegistration.this, "Select Labour Type: Skilled/Unskilled", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.txtLabourIdNumber.getText().toString())) {
                        Toast.makeText(LabourRegistration.this, "Enter the Unique Id/Mobile No. of worker", Toast.LENGTH_SHORT).show();
                    } else if (userType.equals("Supervisor")) {
                        if (image_uri == null) {
                            Toast.makeText(LabourRegistration.this, getString(R.string.take_a_photo_of_labour), Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.show();
                            getLabourList("Supervisor", labourId);
                        }
                    } else if (userType.equals("HR Manager")) {


                        if (image_uri == null) {
                            if (!memberStatus.equals("") && memberStatus.equals("Pending")) {
                                Toast.makeText(LabourRegistration.this, getString(R.string.take_a_photo_of_labour), Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.show();

                                getLabourList("Admin", "Pending");

                            }

                        } else {
                            progressDialog.show();
                            getLabourList("Admin", labourId);

                        }

                    } else {
                        progressDialog.show();
                        getLabourList("Supervisor", labourId);

                    }
                }


            }
        });

    }

    private void getLabourList(String supervisor, String labourId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid1)
                .child("Industry").child("Construction").child("Site")
                .child(String.valueOf(selected_site_code)).child("Labours");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labourArrayList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelLabour modelLabour = ds.getValue(ModelLabour.class);
                    labourArrayList.add(modelLabour);
                }
                Boolean allow = true;
                for (int i = 0; i < labourArrayList.size(); i++) {
                    if (binding.txtLabourIdNumber.getText().toString().equals(labourArrayList.get(i).getUniqueId()) &&
                            binding.txtLabourName.getText().toString().toLowerCase(Locale.ROOT).equals(labourArrayList.get(i).getName().toLowerCase())) {
                        allow = false;
                    }
                }

                if (allow) {
                    if (!labourId.equals("Pending")) {
                        registerLabour(supervisor, labourId);
                    } else {
                        registerLabourWithoutImage("Pending");
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LabourRegistration.this, "Labour with this id already present", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void registerLabourWithoutImage(String status) {
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("labourId", labourId);
        hashMap.put("name", binding.txtLabourName.getText().toString());
        hashMap.put("fatherName", binding.txtLabourFathersName.getText().toString());
        hashMap.put("siteName", selected_site_name);
        hashMap.put("siteCode", Integer.parseInt(selected_site_code));
        hashMap.put("wages", Long.parseLong(binding.txtLabourWages.getText().toString()));
        hashMap.put("uniqueId", binding.txtLabourIdNumber.getText().toString());
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", selectedType);
        hashMap.put("profile", "");
        hashMap.put("dateOfRegister", currentDate);
        hashMap.put("status", status);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid1).child("Industry").child("Construction").child("Site").child(String.valueOf(selected_site_code)).child("Labours").child(labourId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        updateLabourId(timestamp);
                        String message;
                        if (selectedType.equals("Skilled")) {
                            message = getString(R.string.skilled_worker_with_worker_id);
                        } else {
                            message = getString(R.string.unskilled_worker_with_worker_id);
                        }
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LabourRegistration.this);
                        builder.setCancelable(false);
                        builder.setTitle(R.string.success)
                                .setMessage(message + " " + labourId + " " + getString(R.string.has_been_initiated))
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                                    binding.txtLabourName.getText().clear();
                                    binding.txtLabourFathersName.getText().clear();
                                    binding.txtLabourIdNumber.getText().clear();
                                    binding.txtLabourWages.getText().clear();
                                    binding.editImageView.setImageResource(R.drawable.edit_profile_image);
                                    binding.profileImageView.setImageResource(R.drawable.my_profile);
                                    image_uri1 = null;
                                    image_uri = null;
                                    dialogInterface.dismiss();

                                });

                        builder.show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LabourRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


    private void registerLabour(String designation, String labourId) {
        Intent intent = getIntent();
        if (intent.getStringExtra("profile") == null || intent.getStringExtra("profile").equals("")) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image_uri.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] data = bytes.toByteArray();
            String filePathAndName = "Labour/" + firebaseAuth.getUid() + "Construction" + String.valueOf(selected_site_code) + "/" + labourId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()) {
                                String timestamp = "" + System.currentTimeMillis();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("labourId", labourId);
                                hashMap.put("name", binding.txtLabourName.getText().toString());
                                hashMap.put("fatherName", binding.txtLabourFathersName.getText().toString());
                                hashMap.put("siteName", selected_site_name);
                                hashMap.put("siteCode", Integer.parseInt(selected_site_code));
                                hashMap.put("wages", Long.parseLong(binding.txtLabourWages.getText().toString()));
                                hashMap.put("uniqueId", binding.txtLabourIdNumber.getText().toString());
                                hashMap.put("timestamp", timestamp);
                                hashMap.put("type", selectedType);
                                hashMap.put("profile", "" + downloadImageUri);
                                hashMap.put("dateOfRegister", currentDate);
                                hashMap.put("status", "Registered");
                                hashMap.put("payableAmt", 0);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(uid1).child("Industry").child("Construction").child("Site").child(String.valueOf(selected_site_code)).child("Labours").child(labourId).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
//                        updateLabourId(timestamp);
                                                String message;
                                                if (selectedType.equals("Skilled")) {
                                                    message = getString(R.string.skilled_worker_with_worker_id);
                                                } else {
                                                    message = getString(R.string.unskilled_worker_with_worker_id);
                                                }

                                                progressDialog.dismiss();
                                                binding.txtLabourName.getText().clear();
                                                binding.txtLabourFathersName.getText().clear();
                                                binding.txtLabourIdNumber.getText().clear();
                                                binding.txtLabourWages.getText().clear();
                                                binding.editImageView.setImageResource(R.drawable.edit_profile_image);
                                                binding.profileImageView.setImageResource(R.drawable.my_profile);
                                                image_uri1 = null;
                                                image_uri = null;
                                                binding.txtLabourName.getText().clear();
                                                binding.txtLabourFathersName.getText().clear();
                                                binding.txtLabourIdNumber.getText().clear();
                                                binding.txtLabourWages.getText().clear();
                                                binding.editImageView.setImageResource(R.drawable.edit_profile_image);
                                                binding.profileImageView.setImageResource(R.drawable.my_profile);
                                                image_uri1 = null;
                                                image_uri = null;
                                                Toast.makeText(LabourRegistration.this, message + " " + labourId + " " + getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LabourRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }
                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        } else {
            String timestamp = "" + System.currentTimeMillis();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("labourId", labourId);
            hashMap.put("name", binding.txtLabourName.getText().toString());
            hashMap.put("fatherName", binding.txtLabourFathersName.getText().toString());
            hashMap.put("siteName", selected_site_name);
            hashMap.put("siteCode", Integer.parseInt(selected_site_code));
            hashMap.put("wages", Long.parseLong(binding.txtLabourWages.getText().toString()));
            hashMap.put("uniqueId", binding.txtLabourIdNumber.getText().toString());
            hashMap.put("timestamp", timestamp);
            hashMap.put("type", selectedType);
            hashMap.put("profile", "" + intent.getStringExtra("profile"));
            hashMap.put("dateOfRegister", currentDate);
            hashMap.put("status", "Registered");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid1).child("Industry").child("Construction").child("Site").child(String.valueOf(selected_site_code)).child("Labours").child(labourId).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                        updateLabourId(timestamp);
                            String message;
                            if (selectedType.equals("Skilled")) {
                                message = getString(R.string.skilled_worker_with_worker_id);
                            } else {
                                message = getString(R.string.unskilled_worker_with_worker_id);
                            }

                            progressDialog.dismiss();
                            binding.txtLabourName.getText().clear();
                            binding.txtLabourFathersName.getText().clear();
                            binding.txtLabourIdNumber.getText().clear();
                            binding.txtLabourWages.getText().clear();
                            binding.editImageView.setImageResource(R.drawable.edit_profile_image);
                            binding.profileImageView.setImageResource(R.drawable.my_profile);
                            image_uri1 = null;
                            image_uri = null;
                            Toast.makeText(LabourRegistration.this, message + " " + labourId + " " + getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(LabourRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }


    }

    private void showSuccessDialog(String labourId, String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(LabourRegistration.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.success)
                .setMessage(message + " " + labourId + " " + getString(R.string.added_successfully))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    binding.txtLabourName.getText().clear();
                    binding.txtLabourFathersName.getText().clear();
                    binding.txtLabourIdNumber.getText().clear();
                    binding.txtLabourWages.getText().clear();
                    binding.editImageView.setImageResource(R.drawable.edit_profile_image);
                    binding.profileImageView.setImageResource(R.drawable.my_profile);
                    image_uri1 = null;
                    image_uri = null;
                    dialogInterface.dismiss();

                }).show();

    }

    private void updateLabourId() {
        String siteId = "" + selected_site_code;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid1)
                .child("Industry").child("Construction").child("Site")
                .child(siteId).child("Labours");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    Log.e("ChildrenCount", "" + snapshot.getChildrenCount() + 1);
                    String childrenCount = "" + (int) (snapshot.getChildrenCount() + 1);
                    labourId = siteId + childrenCount;
                    Log.e("LabourId", labourId);
                } else {
                    labourId = siteId + "1";
                    Log.e("LabourId", "Else" + labourId);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getSiteinfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid1).child("Industry").child("Construction").child("Site").child(selected_site_code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberStatus = snapshot.child("memberStatus").getValue(String.class);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void permissionCheck() {
        Dexter.withActivity(LabourRegistration.this)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        Log.e("Denied",""+report.getDeniedPermissionResponses().get(0).getPermissionName());
                        if (report.areAllPermissionsGranted()) {
                            launchCameraIntentForTax();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LabourRegistration.this);
        builder.setTitle(R.string.grant_permission);
        builder.setMessage(R.string.this_app_requires_permission);
        builder.setPositiveButton(R.string.goto_settings, (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", LabourRegistration.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showImagePickerOptions() {
        FilePickerActivity.showImagePickerOptions(LabourRegistration.this, new FilePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntentForTax();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntentForTax();
            }


        });
    }

    private void launchGalleryIntentForTax() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(pickPhoto, REQUEST_GALLERY);
    }

    private void launchCameraIntentForTax() {
        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_ Image Description");
//        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

//    class SpinnerAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return type.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inf = getLayoutInflater();
//            View row = inf.inflate(R.layout.spinner_child, null);
//
//            TextView designationText = row.findViewById(R.id.txt_designation);
//            designationText.setText(type[position]);
//
//            return row;
//        }
//    }

    private void setEditText(EditText editText) {
        Intent intent
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");


        try {

            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast
                    .makeText(LabourRegistration.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_SPEECH_INPUT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                if (editTextSelectionButtonId.equals("Name")) {
                    binding.txtLabourName.setText(Objects.requireNonNull(result).get(0));
                } else if (editTextSelectionButtonId.equals("Father's Name")) {
                    binding.txtLabourFathersName.setText(Objects.requireNonNull(result).get(0));
                } else if (editTextSelectionButtonId.equals("Verification Id")) {
                    binding.txtLabourIdNumber.setText(Objects.requireNonNull(result).get(0));
                } else if (editTextSelectionButtonId.equals("Site")) {
                    binding.txtLabourSiteName.setText(Objects.requireNonNull(result).get(0));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_uri = imageBitmap;
            binding.profileImageView.setImageBitmap(imageBitmap);
        }
//        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
//
//            image_uri = data.getData();
//            if (image_uri != null) {
////                    Log.e("Base64", "" + base64);
//                image_uri = (Uri) data.getData();
//                binding.profileImageView.setImageURI(image_uri);
//                //callProfileUpload(base64);
//
//
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        if (userType.equals("Supervisor")) {
            startActivity(new Intent(LabourRegistration.this, MemberTimelineActivity.class));
        } else {
            startActivity(new Intent(LabourRegistration.this, timelineActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("Called", "Destroy");
        super.onDestroy();
    }
}


