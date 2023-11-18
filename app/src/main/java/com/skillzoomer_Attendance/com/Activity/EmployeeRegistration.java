package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.skillzoomer_Attendance.com.Adapter.AdapterLeavePolicy;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.Model.ModelLeavePolicy;
import com.skillzoomer_Attendance.com.Model.ModelWorkPlace;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEmployeeRegistrationBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class EmployeeRegistration extends AppCompatActivity implements OnMapReadyCallback {
    ActivityEmployeeRegistrationBinding binding;
    FirebaseAuth firebaseAuth;
    String gradeValue;
    private ArrayList<ModelEmployee> employeeArrayList;

    private String[] department = {"Select Department", "Administration", "Sales", "Finance", "Marketing", "Operation Management", "Human Resources(HR)", "Information Technology(IT)"};
    private String[] designation_admin = {"Select Designation", "Branch Manager", "Branch Head", "Administrator"};
    private String[] designation = {"Select Designation", "Project Lead", "Custom"};
    private String[] grade = {"Select Grade", "Grade 1", "Grade 2","Grade 3","Grade 4"};
    private String[] grade1 = {"Select Designation", "BOD", "Partner","Investor","Custom"};
    private String[] grade2 = {"Select Designation", "Branch Manager", "Branch Head", "Administrator","Custom"};
    private String[] grade3 = {"Select Designation", "Project Lead", "Custom"};
    private String[] grade4 = {"Select Designation", "Peon", "Cleaner","Cook","Custom"};

    private ArrayList<ModelLeavePolicy> leavePolicyArrayList;

    private String companyName;
    private String companyCode, siteName;
    long siteId;

    ProgressDialog progressDialog;

    private final Calendar myCalendar = Calendar.getInstance();

    int EmpCode;

    private String doj;

    private ArrayList<ModelWorkPlace> modelWorkPlaceArrayList;

    Marker marker;

    private String selectedPolicyName;
    private long selectedPolicyId;

    private Circle circle;
    int radius;

    GoogleMap googleMap;

    double latitude,longitude;
    String tlName,tlUid,tlUserId;
    String uid;
    String hrName,hrUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Scanner sc= new Scanner(System.in);
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        siteId = intent.getLongExtra("siteId", siteId);
        siteName = intent.getStringExtra("siteName");

        leavePolicyArrayList=new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        modelWorkPlaceArrayList = new ArrayList<>();
        employeeArrayList=new ArrayList<>();




        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("Associate")){
            uid=getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","");
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    hrName=snapshot.child("name").getValue(String.class);
                    hrUserId=snapshot.child("userId").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            uid=firebaseAuth.getUid();
        }
        getWorkPlaceDetails(siteId);
//        int a= sc.nextInt();
//        int b=sc.nextInt();
        binding.spinnerDesignation.setVisibility(View.GONE);
        binding.spinnerDepartment.setVisibility(View.GONE);
        binding.spinnerGrade.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        companyName = sharedPreferences.getString("companyName", "");
        String currentString = "Fruit: they taste good";
        String[] separated = siteName.split(" ");
        binding.llStep1.setVisibility(View.VISIBLE);
        binding.llStep2.setVisibility(View.GONE);
        binding.llStep3.setVisibility(View.GONE);
        binding.llStep4.setVisibility(View.GONE);
        binding.rbSameAsBranch.setChecked(true);
        binding.rbThisBranch.setChecked(true);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(EmployeeRegistration.this);
        companyCode = separated[0];
        if (companyCode.length() > 3) {
            companyCode = companyCode.substring(0, 3);
        }
        binding.rbProhabation.setChecked(true);
        GradeSpinnerAdapter gradeSpinnerAdapter=new GradeSpinnerAdapter();
        binding.spinnerGrade.setAdapter(gradeSpinnerAdapter);
        DepartmentSpinnerAdapter departmentSpinnerAdapter = new DepartmentSpinnerAdapter();
        AdminDesignationSpinnerAdapter adminDesignationSpinnerAdapter = new AdminDesignationSpinnerAdapter();
        DesignationSpinnerAdapter designationSpinnerAdapter = new DesignationSpinnerAdapter();

        binding.spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    binding.spinnerDesignation.setVisibility(View.VISIBLE);
                    binding.spinnerDesignation.setAdapter(designationSpinnerAdapter);
                    getReportingOfficerList();
                }else{
                    binding.spinnerDesignation.setVisibility(View.GONE);
                    binding.spinnerDepartment.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerDepartment.setAdapter(departmentSpinnerAdapter);

        binding.spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0 && charSequence.toString().length() < 10) {
                    binding.etMobile.setError("Invalid mobile number");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId));
        reference.child("Policy")  .child("Leave").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leavePolicyArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLeavePolicy modelLeavePolicy=ds.getValue(ModelLeavePolicy.class);
                    leavePolicyArrayList.add(modelLeavePolicy);

                }
                leavePolicyArrayList.add(0,new ModelLeavePolicy(0,"Select Policy"));
                LeavePolicyAdapter adapterLeavePolicy=new LeavePolicyAdapter();
                binding.spinnerLeavePolicy.setAdapter(adapterLeavePolicy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.etDoj.setFocusable(false);
        binding.etDob.setFocusable(false);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            doj = sdf.format(myCalendar.getTime());
            Log.e("Date", "From:" + doj);
            updateLabel(binding.etDoj);


        };
        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            doj = sdf.format(myCalendar.getTime());
            Log.e("Date", "From:" + doj);
            updateLabel(binding.etDob);


        };

        binding.etDoj.setOnClickListener((View v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here

        });
        binding.etDob.setOnClickListener((View v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here

        });
        binding.txtAutoGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid",""))
                        .child("Industry")
                        .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                        .child("Site").child(String.valueOf(siteId));
                reference.child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        EmpCode = (int) ((snapshot.getChildrenCount() + 1) * 100);

                        binding.etEmpId.setText(companyCode + EmpCode);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        binding.rbPermanent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbProhabation.isChecked()) {
                    binding.rbProhabation.setChecked(false);
                    binding.rbPermanent.setChecked(true);
                }
            }
        });

        binding.rbProhabation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbPermanent.isChecked()) {
                    binding.rbProhabation.setChecked(true);
                    binding.rbPermanent.setChecked(false);
                }
            }
        });
        binding.rbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rbMale.setChecked(true);
                binding.rbFemale.setChecked(false);
                binding.rbOthers.setChecked(false);
            }
        });
        binding.rbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rbMale.setChecked(false);
                binding.rbFemale.setChecked(true);
                binding.rbOthers.setChecked(false);
            }
        });
        binding.rbOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rbMale.setChecked(false);
                binding.rbFemale.setChecked(false);
                binding.rbOthers.setChecked(true);
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(binding.etSiteName.getText().toString())) {
                    Toast.makeText(EmployeeRegistration.this, "Enter employee name ", Toast.LENGTH_SHORT).show();
                } else if (binding.spinnerGrade.getSelectedItemPosition() <= 0) {
                    Toast.makeText(EmployeeRegistration.this, "Select Grade ", Toast.LENGTH_SHORT).show();
                } else if (binding.spinnerDesignation.getSelectedItemPosition() <= 0) {
                    Toast.makeText(EmployeeRegistration.this, "Select designation of Employee ", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(binding.etEmpId.getText().toString())) {
                    Toast.makeText(EmployeeRegistration.this, "Employee Id cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(binding.etDoj.getText().toString())) {
                    Toast.makeText(EmployeeRegistration.this, "Select date of joining of employee", Toast.LENGTH_SHORT).show();
                }else if(!binding.rbMale.isChecked() && !binding.rbFemale.isChecked() &&!binding.rbOthers.isChecked()){
                    Toast.makeText(EmployeeRegistration.this, "Select Gender", Toast.LENGTH_SHORT).show();
                } else {
                    binding.llStep1.setVisibility(View.GONE);
                    binding.llStep2.setVisibility(View.VISIBLE);
                    binding.llStep3.setVisibility(View.GONE);
                    binding.llStep4.setVisibility(View.GONE);
                }
            }
        });
        binding.btnNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.etSalary.getText().toString())) {
                    Toast.makeText(EmployeeRegistration.this, "Enter the employee salary. This would be confidential", Toast.LENGTH_SHORT).show();
                }else if(binding.spinnerGrade.getSelectedItemPosition()<3){
                    binding.llStep1.setVisibility(View.GONE);
                    binding.llStep2.setVisibility(View.GONE);
                    binding.llStep3.setVisibility(View.VISIBLE);
                    binding.llStep4.setVisibility(View.GONE);
                }else{
                    binding.llStep1.setVisibility(View.GONE);
                    binding.llStep2.setVisibility(View.GONE);
                    binding.llStep3.setVisibility(View.GONE);
                    binding.llStep4.setVisibility(View.VISIBLE);
                }

            }
        });
        binding.btnNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.llStep1.setVisibility(View.GONE);
                binding.llStep2.setVisibility(View.GONE);
                binding.llStep3.setVisibility(View.GONE);
                binding.llStep4.setVisibility(View.VISIBLE);
            }
        });

        binding.rbSameAsBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbCustom.isChecked()) {
                    binding.rbSameAsBranch.setChecked(true);
                    binding.rbCustom.setChecked(false);
                    binding.cbSunday.setEnabled(false);
                    binding.cbMonday.setEnabled(false);
                    binding.cbTuesday.setEnabled(false);
                    binding.cbWednesday.setEnabled(false);
                    binding.cbThursday.setEnabled(false);
                    binding.cbFriday.setEnabled(false);
                    binding.cbSaturday.setEnabled(false);
                }
            }
        });

        binding.rbCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbSameAsBranch.isChecked()) {
                    binding.rbSameAsBranch.setChecked(false);
                    binding.rbCustom.setChecked(true);
                    binding.cbSunday.setEnabled(true);
                    binding.cbMonday.setEnabled(true);
                    binding.cbTuesday.setEnabled(true);
                    binding.cbWednesday.setEnabled(true);
                    binding.cbThursday.setEnabled(true);
                    binding.cbFriday.setEnabled(true);
                    binding.cbSaturday.setEnabled(true);
                }

            }
        });

        binding.rbAnyBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbThisBranch.isChecked() || binding.rbAnywhere.isChecked()) {
                    binding.rbAnyBranch.setChecked(true);
                    binding.rbThisBranch.setChecked(false);
                    binding.rbAnywhere.setChecked(false);
                    for (int i = 0; i < modelWorkPlaceArrayList.size(); i++) {
                        LatLng latLng;
                        latLng = new LatLng(modelWorkPlaceArrayList.get(i).getSiteLatitude(), modelWorkPlaceArrayList.get(i).getSiteLongitude());
                        marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                                .title(modelWorkPlaceArrayList.get(i).getSiteName())
                                .draggable(true));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        circle = googleMap.addCircle(new CircleOptions()
                                .center(new LatLng(modelWorkPlaceArrayList.get(i).getCircleCenterLat(), modelWorkPlaceArrayList.get(i).getCircleCenterLon()))
                                .radius(modelWorkPlaceArrayList.get(i).getCircleRadius())
                                .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(1.0f));


                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));


                    }
                }
            }
        });

        binding.rbThisBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbAnyBranch.isChecked() || binding.rbAnywhere.isChecked()) {
                    binding.rbAnyBranch.setChecked(false);
                    binding.rbThisBranch.setChecked(true);
                    binding.rbAnywhere.setChecked(false);
                    for (int i = 0; i < modelWorkPlaceArrayList.size(); i++) {
                        if (modelWorkPlaceArrayList.get(i).getSiteId() == siteId) {
                            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            supportMapFragment.getMapAsync(EmployeeRegistration.this);
                            LatLng latLng;
                            latitude=modelWorkPlaceArrayList.get(i).getCircleCenterLat();
                            longitude=modelWorkPlaceArrayList.get(i).getCircleCenterLon();
                            radius=(int)modelWorkPlaceArrayList.get(i).getCircleRadius();

                            latLng = new LatLng(modelWorkPlaceArrayList.get(i).getSiteLatitude(), modelWorkPlaceArrayList.get(i).getSiteLongitude());
                            marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                                    .title(modelWorkPlaceArrayList.get(i).getSiteName())
                                    .draggable(true));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            circle = googleMap.addCircle(new CircleOptions()
                                    .center(new LatLng(modelWorkPlaceArrayList.get(i).getCircleCenterLat(), modelWorkPlaceArrayList.get(i).getCircleCenterLon()))
                                    .radius(modelWorkPlaceArrayList.get(i).getCircleRadius())
                                    .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(1.0f));


                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                        }
                    }

                }
            }
        });

        binding.rbAnywhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbAnyBranch.isChecked() || binding.rbThisBranch.isChecked()) {
                    binding.rbAnyBranch.setChecked(false);
                    binding.rbThisBranch.setChecked(false);
                    binding.rbAnywhere.setChecked(true);
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(EmployeeRegistration.this);
                    try {
                        googleMap.clear();
                    } catch (NullPointerException e) {
                        Log.e("GoogleMap", e.getMessage());
                    }


                }
            }
        });


    }

    private void getReportingOfficerList() {

        gradeValue="";
        employeeArrayList.clear();






        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                .child("Site").child(String.valueOf(siteId))
                .child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Snapshot1211",""+uid);
                Log.e("Snapshot1211",""+snapshot.getChildrenCount());
                if(snapshot.getChildrenCount()>0){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        ModelEmployee modelEmployee=ds.getValue(ModelEmployee.class);

                        gradeValue="Grade "+modelEmployee.getGrade();
                        Log.e("UIDGHDFGED",""+gradeValue);
                        boolean check;
                        if(grade[binding.spinnerGrade.getSelectedItemPosition()].equals("Grade 1")||grade[binding.spinnerGrade.getSelectedItemPosition()].equals("Grade 2")){
                            check=gradeValue.equals("Grade 1");
                        }else if(grade[binding.spinnerGrade.getSelectedItemPosition()].equals("Grade 3")){
                            check=gradeValue.equals("Grade 1")||gradeValue.equals("Grade 2");

                        }else {
                            check=gradeValue.equals("Grade 1")||gradeValue.equals("Grade 2")||gradeValue.equals("Grade 3");
                        }
                        Log.e("UIDGHDFGED",""+check);
                        if(check){
                            employeeArrayList.add(new ModelEmployee(modelEmployee.getName(),modelEmployee.getDesignation(),modelEmployee.getGrade(),modelEmployee.getUserId(),modelEmployee.getUid()));
                        }

                    }

                        ModelEmployee modelEmployee=new ModelEmployee(
                                hrName,"Admin",1,hrUserId,
                                getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid",""));
                        employeeArrayList.add(modelEmployee);

                }else{
                    ModelEmployee modelEmployee=new ModelEmployee(hrName,"Admin",1,hrUserId,
                            firebaseAuth.getUid());
                    employeeArrayList.add(modelEmployee);

                }
                employeeArrayList.add(0,new ModelEmployee("","Select Reporting Employee",""));
                ReportingOfficerAdapter reportingOfficerAdapter=new ReportingOfficerAdapter();
                binding.spinnerReportingOfficer.setAdapter(reportingOfficerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.spinnerReportingOfficer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view , int position , long id) {
                if(position>0){
                    tlName=employeeArrayList.get(position).getName();
                    tlUid=employeeArrayList.get(position).getUid();
                    tlUserId=employeeArrayList.get(position).getUserId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getWorkPlaceDetails(long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""))
                .child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelWorkPlaceArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelWorkPlace modelWorkPlace = ds.getValue(ModelWorkPlace.class);
                            modelWorkPlaceArrayList.add(modelWorkPlace);
                        }
                        if (modelWorkPlaceArrayList.size() > 0) {
                            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            supportMapFragment.getMapAsync(EmployeeRegistration.this);
                        }
                        for (int i = 0; i < modelWorkPlaceArrayList.size(); i++) {
                            if (modelWorkPlaceArrayList.get(i).getSiteId() == siteId) {
                                binding.cbSunday.setChecked(modelWorkPlaceArrayList.get(i).getSunday());
                                binding.cbMonday.setChecked(modelWorkPlaceArrayList.get(i).getMonday());
                                binding.cbTuesday.setChecked(modelWorkPlaceArrayList.get(i).getTuesday());
                                binding.cbWednesday.setChecked(modelWorkPlaceArrayList.get(i).getWednesday());
                                binding.cbThursday.setChecked(modelWorkPlaceArrayList.get(i).getThursday());
                                binding.cbFriday.setChecked(modelWorkPlaceArrayList.get(i).getFriday());
                                binding.cbSaturday.setChecked(modelWorkPlaceArrayList.get(i).getSaturday());
                                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                supportMapFragment.getMapAsync(EmployeeRegistration.this);
                                LatLng latLng;
                                latitude=modelWorkPlaceArrayList.get(i).getCircleCenterLat();
                                longitude=modelWorkPlaceArrayList.get(i).getCircleCenterLon();
                                radius=(int)modelWorkPlaceArrayList.get(i).getCircleRadius();

                                latLng = new LatLng(modelWorkPlaceArrayList.get(i).getSiteLatitude(), modelWorkPlaceArrayList.get(i).getSiteLongitude());
                                try{
                                    marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                                            .title(modelWorkPlaceArrayList.get(i).getSiteName())
                                            .draggable(true));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                    circle = googleMap.addCircle(new CircleOptions()
                                            .center(new LatLng(modelWorkPlaceArrayList.get(i).getCircleCenterLat(), modelWorkPlaceArrayList.get(i).getCircleCenterLon()))
                                            .radius(modelWorkPlaceArrayList.get(i).getCircleRadius())
                                            .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(1.0f));


                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                                }catch(NullPointerException e){

                                }



                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.spinnerReportingOfficer.getSelectedItemPosition()==0&& binding.spinnerGrade.getSelectedItemPosition()<3) {
                    Toast.makeText(EmployeeRegistration.this, "Select the reporting officer", Toast.LENGTH_SHORT).show();
                }else{
                    saveDataToFirebase();
                }
            }
        });
    }

    private void saveDataToFirebase() {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userId",binding.etEmpId.getText().toString());  //String
        hashMap.put("name",binding.etSiteName.getText().toString());//String
        hashMap.put("deptPosition",binding.spinnerDepartment.getSelectedItemPosition());//String
        hashMap.put("dept",department[binding.spinnerDepartment.getSelectedItemPosition()]);//String
        hashMap.put("designationPosition",binding.spinnerDesignation.getSelectedItemPosition());//Long
        hashMap.put("grade",binding.spinnerGrade.getSelectedItemPosition());//Long

        if(binding.spinnerGrade.getSelectedItemPosition()==1){

            hashMap.put("designation",designation[binding.spinnerDesignation.getSelectedItemPosition()]);//Long
            hashMap.put("userType","HR Manager1");//String

        }else if(binding.spinnerGrade.getSelectedItemPosition()==2){
            hashMap.put("designation",designation[binding.spinnerDesignation.getSelectedItemPosition()]);//Long
            hashMap.put("userType","Associate");//String
        }
        else{

            hashMap.put("designation",designation[binding.spinnerDesignation.getSelectedItemPosition()]);//Long
            hashMap.put("userType","Employee");//String
        }

        hashMap.put("mobile",binding.etMobile.getText().toString());//String
        hashMap.put("doj",binding.etDoj.getText().toString());//String
        hashMap.put("email",binding.etEmail.getText().toString());//String
        hashMap.put("workEmail",binding.etWorkEmail.getText().toString());//String
        hashMap.put("permanentEmployee",binding.rbPermanent.isChecked());//String
        hashMap.put("probationEmployee",binding.rbProhabation.isChecked());//String
        hashMap.put("singleAttendance",binding.rbThisBranch.isChecked());//Bool
        hashMap.put("multiAttendance",binding.rbAnyBranch.isChecked());//Bool
        hashMap.put("allAttendance",binding.rbAnywhere.isChecked());//Bool
        hashMap.put("Sunday",binding.cbSunday.isChecked());//Bool
        hashMap.put("Monday",binding.cbMonday.isChecked());//Bool
        hashMap.put("Tuesday",binding.cbTuesday.isChecked());//Bool
        hashMap.put("Wednesday",binding.cbWednesday.isChecked());//Bool
        hashMap.put("Thursday",binding.cbThursday.isChecked());//Bool
        hashMap.put("Friday",binding.cbFriday.isChecked());//Bool
        hashMap.put("Saturday",binding.cbSaturday.isChecked());//Bool
        hashMap.put("salary",binding.etSalary.getText().toString());//String
        hashMap.put("registrationStatus","Pending");//String
        hashMap.put("hrUid",firebaseAuth.getUid());//String
        hashMap.put("siteId",siteId);//Long
        hashMap.put("siteName",siteName);//String
        hashMap.put("companyName",companyName);//String
        hashMap.put("dob",binding.etDob.getText().toString());//String
        hashMap.put("industryPosition",getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("industryPosition",0));//Long
        hashMap.put("industryName",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""));//String
        if(binding.rbThisBranch.isChecked()){
            hashMap.put("latitude",latitude);//Long
            hashMap.put("longitude",longitude);//Long
            hashMap.put("radius",radius);//Long
        }
        hashMap.put("leavePolicyId",String.valueOf(selectedPolicyId));
        hashMap.put("leavePolicyName",selectedPolicyName);
        if(binding.rbMale.isChecked()){
            hashMap.put("gender","Male");
        }else if(binding.rbFemale.isChecked()){
            hashMap.put("gender","Female");
        }else if(binding.rbOthers.isChecked()){
            hashMap.put("gender","Others");
        }
        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("Associate")){
            hashMap.put("tlUid",tlUid);
            hashMap.put("hrUid",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid",""));
        }else{
            hashMap.put("hrUid",firebaseAuth.getUid());
            hashMap.put("tlUid",tlUid);  //todo pjkwjfckqhg
        }
        hashMap.put("addEmployee",binding.cbAddEmployee.isChecked());
        hashMap.put("deleteEmployee",binding.cbDeleteEmployee.isChecked());
        hashMap.put("leaveApproval",binding.cbLeaveApproval.isChecked());
        hashMap.put("leavePolicy",binding.cbLeavePolicy.isChecked());
        hashMap.put("salaryPolicy",binding.cbSalaryPolicy.isChecked());
        hashMap.put("holidayList",binding.cbHolidayList.isChecked());
        hashMap.put("eventAdd",binding.cbEvent.isChecked());
        hashMap.put("attendanceApproval",binding.cbAttendance.isChecked());

        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("InvitedMembers");
        reference1.child(binding.etMobile.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(EmployeeRegistration.this, "This mobile is already invited", Toast.LENGTH_SHORT).show();
                }else{
                    reference1.child(binding.etMobile.getText().toString()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                                            .child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""))
                                            .child("Site").child(String.valueOf(siteId)).child("Employee");
                                    reference.orderByChild("empId").equalTo(binding.etEmpId.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if(snapshot.getChildrenCount()>0){
                                                Toast.makeText(EmployeeRegistration.this, "Employee Id already registered with another employee", Toast.LENGTH_SHORT).show();
                                            }else{

                                                reference.child(binding.etEmpId.getText().toString()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        generateInvitelink(binding.etMobile.getText().toString(),binding.etSiteName.getText().toString());





                                                    }
                                                });
                                            }
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
    void generateInvitelink(String mobile, String name){
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
                            String inviteLink="https://haajiri1.page.link"+shortLink.getPath().toString();

                            // Toast.makeText(getApplicationContext(), inviteLink, Toast.LENGTH_LONG).show();
//                            System.out.println("+++++++++++++++++++++++++link==============");
//                            System.out.println(shortLink.getPath());
                            // Toast.makeText(InviteActivity.this,"site-"+site_id,Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(Intent.ACTION_SEND);
                            myIntent.setType("text/plain");
                            String body = "Hi"+"\b"+name+" "+"! I invite you on हाज़िरी Register as Team Member of "+companyName+" for my site "+siteName+
                                    "\nYour User id for Login is: "+binding.etEmpId.getText().toString()+"and Your password for Login is: "+binding.etMobile.getText().toString()+". Start your work.";
                            String sub = "Invite";
                            System.out.println("++++++++++++++++++++body"+body);
                            myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                            myIntent.putExtra(Intent.EXTRA_TEXT,body);
                            startActivity(Intent.createChooser(myIntent, "Share Using"));



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

    private void updateLabel(EditText etDoj) {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDoj.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.setMapType(1);
        if (binding.rbThisBranch.isChecked()&& modelWorkPlaceArrayList.size()>0) {
            for (int i = 0; i < modelWorkPlaceArrayList.size(); i++) {
                if (modelWorkPlaceArrayList.get(i).getSiteId() == siteId) {
                    LatLng latLng;
                    latLng = new LatLng(modelWorkPlaceArrayList.get(i).getSiteLatitude(), modelWorkPlaceArrayList.get(i).getSiteLongitude());
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng)


                            .title(modelWorkPlaceArrayList.get(i).getSiteName())
                            .draggable(true));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    circle = googleMap.addCircle(new CircleOptions()
                            .center(new LatLng(modelWorkPlaceArrayList.get(i).getCircleCenterLat(), modelWorkPlaceArrayList.get(i).getCircleCenterLon()))
                            .radius(modelWorkPlaceArrayList.get(i).getCircleRadius())
                            .fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(1.0f));


                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                }
            }
        }


            /*googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));*/


//        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//            @Override
//            public void onMarkerDrag(@NonNull Marker marker) {
//                marker.remove();
//            }
//
//            @Override
//            public void onMarkerDragEnd(@NonNull Marker marker) {
//                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
//                marker = googleMap.addMarker(new MarkerOptions().position(latLng)
//
//
//                        .title("Site Location")
//                        .draggable(true));
//            }
//
//            @Override
//            public void onMarkerDragStart(@NonNull Marker marker) {
//
//            }
//        });
//            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
//                    .title("Your Current Location");

        Log.e("HEllo", "going to call");
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(@NonNull CameraPosition cameraPosition) {
                Log.e("Latitude", "CL" + cameraPosition.target.latitude);
                Log.e("Latitude", "CLO" + cameraPosition.target.longitude);
            }
        });

    }

    class DepartmentSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return department.length;
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
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(department[position]);

            return row;
        }
    }

    class AdminDesignationSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return designation_admin.length;
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
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(designation_admin[position]);

            return row;
        }
    }

    class DesignationSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            if(binding.spinnerGrade.getSelectedItemPosition()==1) {

                return grade1.length;
            }else if(binding.spinnerGrade.getSelectedItemPosition()==2) {

                return grade2.length;
            } else if(binding.spinnerGrade.getSelectedItemPosition()==3) {

                return grade3.length;
            }else{
                return grade4.length;
            }
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
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            if(binding.spinnerGrade.getSelectedItemPosition()==1) {

                designationText.setText(grade1[position]);
            }else if(binding.spinnerGrade.getSelectedItemPosition()==2) {

                designationText.setText(grade2[position]);
            } else if(binding.spinnerGrade.getSelectedItemPosition()==3) {

                designationText.setText(grade3[position]);
            }else{
                designationText.setText(grade4[position]);
            }

            return row;
        }
    }

    class GradeSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return grade.length;
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
            View row = inf.inflate(R.layout.spinner_child, null);



            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(grade[position]);



            return row;
        }
    }


    class LeavePolicyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return leavePolicyArrayList.size();
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
            View row = inf.inflate(R.layout.spinner_child, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(leavePolicyArrayList.get(position).getPolicyName());
            selectedPolicyId=leavePolicyArrayList.get(position).getPolicyId();
            selectedPolicyName=leavePolicyArrayList.get(position).getPolicyName();


            return row;
        }
    }
    class ReportingOfficerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return employeeArrayList.size();
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
            View row = inf.inflate(R.layout.layout_reporting_officer_single_row, null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            TextView name = row.findViewById(R.id.txt_name);
            TextView grade = row.findViewById(R.id.txt_grade);
            if(position==0){
                grade.setVisibility(View.GONE);
                designationText.setVisibility(View.GONE);
            }else{
                grade.setVisibility(View.VISIBLE);
                designationText.setVisibility(View.VISIBLE);
            }
            designationText.setText(employeeArrayList.get(position).getDesignation());
            name.setText(employeeArrayList.get(position).getName());
            grade.setText("Grade "+employeeArrayList.get(position).getGrade());



            return row;
        }
    }


}