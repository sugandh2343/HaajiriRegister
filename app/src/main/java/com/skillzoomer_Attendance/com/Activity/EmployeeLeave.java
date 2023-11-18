package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.skillzoomer_Attendance.com.Adapter.ImagesAdapter;
import com.skillzoomer_Attendance.com.Model.CustomModel;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLeavePolicy;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEmployeeLeaveBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EmployeeLeave extends AppCompatActivity {
    ActivityEmployeeLeaveBinding binding;

    private String fromDate = "", toDate = "";
    private final Calendar myCalendar = Calendar.getInstance();
    String currentDate;
    String startTime;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    ArrayList<String> result;
    private ArrayList<ModelDate> modelDateArrayList;
    private ArrayList<ModelDate> shortDateList;
    ImagesAdapter adapter;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;
    private static final int READ_PERMISSION_CODE = 101;
    FirebaseAuth firebaseAuth;
    String policyId, hrUid,tlUid;
    long siteId;
    List<CustomModel> imagesList;
    private ArrayList<String> spinnerPolicyAdd;
    public static String[] storge_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.llSelectType.setVisibility(View.GONE);
        binding.llShortLeave.setVisibility(View.GONE);
        binding.llMultiLeave.setVisibility(View.GONE);
        binding.llMain.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        spinnerPolicyAdd = new ArrayList<>();
        imagesList = new ArrayList<>();
        adapter = new ImagesAdapter(this, imagesList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
        modelDateArrayList=new ArrayList<>();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() != 0) {
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        storge_permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storge_permissions_33 = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
        }

        long timestamp = System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new android.icu.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                policyId = snapshot.child("leavePolicyId").getValue(String.class);
                hrUid = snapshot.child("hrUid").getValue(String.class);
                tlUid=snapshot.child("tlUid").getValue(String.class);
                siteId = snapshot.child("siteId").getValue(long.class);
                name=snapshot.child("name").getValue(String.class);

                Log.e("HRUID", hrUid);
                Log.e("HRUID", policyId);
                Log.e("HRUID", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""));

                reference.child(hrUid).child("Industry")
                        .child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""))
                        .child("Site").child(String.valueOf(siteId)).child("Policy").child("Leave").child(policyId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ModelLeavePolicy modelLeavePolicy = snapshot.getValue(ModelLeavePolicy.class);
                                spinnerPolicyAdd.add("Select Leave Type");
                                if (modelLeavePolicy.getHalfday()) {
                                    spinnerPolicyAdd.add("Half Day");
                                }
                                if (modelLeavePolicy.getShortLeave()) {
                                    spinnerPolicyAdd.add("Short Leave");
                                }
                                if (modelLeavePolicy.getCl()) {
                                    spinnerPolicyAdd.add("Casual Leave(CL)");
                                }
                                if (modelLeavePolicy.getEl()) {
                                    spinnerPolicyAdd.add("Earned Leave (EL)");
                                }
                                if (modelLeavePolicy.getSl()) {
                                    spinnerPolicyAdd.add("Sick Leave(SL)");
                                }
                                if (modelLeavePolicy.getMl()) {
                                    spinnerPolicyAdd.add("Maternity Leave(ML)");
                                }
                                if (modelLeavePolicy.getPl()) {
                                    spinnerPolicyAdd.add("Paternity Leave(PL)");
                                }
                                spinnerPolicyAdd.add("Leave Without Pay(LWP)");

                                LeavePolicyAdapter leavePolicyAdapter = new LeavePolicyAdapter();
                                binding.spinnerLeaveType.setAdapter(leavePolicyAdapter);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.spinnerLeaveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    if (!spinnerPolicyAdd.get(position).equals("Half Day") && !spinnerPolicyAdd.get(position).equals("Short Leave")) {
                        binding.llShortLeave.setVisibility(View.GONE);
                        binding.llMultiLeave.setVisibility(View.VISIBLE);
                        binding.llMain.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.rbCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.llMain.setVisibility(View.VISIBLE);
                    binding.llMultiLeave.setVisibility(View.VISIBLE);
                    binding.llShortLeave.setVisibility(View.GONE);
                    binding.TodateEt.setVisibility(View.VISIBLE);
                    binding.FromdateEt.setHint("Leave From");

                    binding.rbHalf.setChecked(false);
                    binding.rbShort.setChecked(false);
                    binding.rbSingle.setChecked(false);
                } else {
                    if (!binding.rbHalf.isChecked() && !binding.rbShort.isChecked() && !binding.rbSingle.isChecked()) {
                        binding.llSelectType.setVisibility(View.VISIBLE);
                        binding.llShortLeave.setVisibility(View.GONE);
                        binding.llMultiLeave.setVisibility(View.GONE);
                        binding.llMain.setVisibility(View.GONE);
                    }
                }
            }
        });

        binding.rbShort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.llMain.setVisibility(View.VISIBLE);
                    binding.llMultiLeave.setVisibility(View.GONE);
                    binding.llShortLeave.setVisibility(View.VISIBLE);
                    binding.rbCustom.setChecked(false);
                    binding.rbHalf.setChecked(false);
                    binding.rbSingle.setChecked(false);
                } else {
                    if (!binding.rbHalf.isChecked() && !binding.rbCustom.isChecked() && !binding.rbSingle.isChecked()) {
                        binding.llSelectType.setVisibility(View.VISIBLE);
                        binding.llShortLeave.setVisibility(View.GONE);
                        binding.llMultiLeave.setVisibility(View.GONE);
                        binding.llMain.setVisibility(View.GONE);
                    }
                }
            }
        });
        binding.txtWorkingDays.setText("-");
        binding.rbSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.llMain.setVisibility(View.VISIBLE);
                    binding.llMultiLeave.setVisibility(View.VISIBLE);
                    binding.llShortLeave.setVisibility(View.GONE);
                    binding.TodateEt.setVisibility(View.GONE);
                    binding.rbCustom.setChecked(false);
                    binding.rbHalf.setChecked(false);
                    binding.rbShort.setChecked(false);
                    binding.FromdateEt.setHint("Select Leave Date");
                    toDate = "NA";

                } else {
                    if (!binding.rbHalf.isChecked() && !binding.rbShort.isChecked() && !binding.rbCustom.isChecked()) {
                        binding.llSelectType.setVisibility(View.VISIBLE);
                        binding.llShortLeave.setVisibility(View.GONE);
                        binding.llMultiLeave.setVisibility(View.GONE);
                        binding.llMain.setVisibility(View.GONE);
                    }
                }
            }
        });
        binding.rbHalf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.llMain.setVisibility(View.VISIBLE);
                    binding.llMultiLeave.setVisibility(View.GONE);
                    binding.llShortLeave.setVisibility(View.VISIBLE);
                    binding.rbCustom.setChecked(false);
                    binding.rbShort.setChecked(false);
                    binding.rbSingle.setChecked(false);

                } else {
                    if (!binding.rbCustom.isChecked() && !binding.rbShort.isChecked() && !binding.rbSingle.isChecked()) {
                        binding.llSelectType.setVisibility(View.VISIBLE);
                        binding.llShortLeave.setVisibility(View.GONE);
                        binding.llMultiLeave.setVisibility(View.GONE);
                        binding.llMain.setVisibility(View.GONE);
                    }
                }
            }
        });
        binding.rbArriveLate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.rbDepartEarly.setChecked(false);
                    binding.txtShortType.setText("Expected Time of Arrival");

                }
            }
        });
        binding.rbDepartEarly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.rbArriveLate.setChecked(false);
                    binding.txtShortType.setText("Expected Time of Departure");
                }
            }
        });
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "dd-MMM-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            fromDate = sdf.format(myCalendar.getTime());
            Log.e("Date", "From:" + fromDate);
            updateLabel(binding.FromdateEt);
            if(!TextUtils.isEmpty(binding.TodateEt.getText().toString())&& !binding.TodateEt.getText().toString().equals(binding.FromdateEt.getText().toString())){
                try {
                    getDateRange(binding.FromdateEt.getText().toString(), binding.TodateEt.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }else if(binding.TodateEt.getText().toString().equals(binding.FromdateEt.getText().toString())){
                binding.txtWorkingDays.setText("1");
                modelDateArrayList.add(new ModelDate(fromDate));

            }


        };
        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd-MMM-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            toDate = sdf.format(myCalendar.getTime());
            Log.e("Date", "From:" + toDate);
            updateLabel(binding.TodateEt);
            if(!TextUtils.isEmpty(binding.FromdateEt.getText().toString())&& !binding.TodateEt.getText().toString().equals(binding.FromdateEt.getText().toString())){
                try {
                    getDateRange(binding.FromdateEt.getText().toString(), binding.TodateEt.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }else if(binding.TodateEt.getText().toString().equals(binding.FromdateEt.getText().toString())){
                binding.txtWorkingDays.setText("1");
                modelDateArrayList.add(new ModelDate(toDate));
            }


        };
        binding.txtUploadDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "Clicked");
                verifyPermissionAndPickImage();
            }
        });



        binding.FromdateEt.setOnClickListener((View v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(timestamp);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

//            try {
//                Date fDate = dateFormat.parse(siteCreatedDate);
//                Log.e("Parse Success","Success");
////                datePickerDialog.getDatePicker().setMinDate(fDate.getTime());
//            } catch (ParseException e) {
//                Log.e("ParseException",e.getMessage());
//                e.printStackTrace();
//            }

            datePickerDialog.show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here

        });
        binding.TodateEt.setOnClickListener((View v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here


        });


        TimePickerDialog.OnTimeSetListener timeStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                String myFormat = "hh:mm"; //In which you need put here
                android.icu.text.SimpleDateFormat sdf = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sdf = new android.icu.text.SimpleDateFormat(myFormat, Locale.US);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startTime = sdf.format(myCalendar.getTime());
                }
                Log.e("Time", "Start:" + startTime);
                binding.etTime.setText(startTime);

            }
        };
        binding.etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EmployeeLeave.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        binding.etTime.setText(selectedHour + ":" + selectedMinute);
                        startTime = selectedHour + ":" + selectedMinute;
                        Log.e("StartTime", startTime);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        binding.ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.ENGLISH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_to_search));


                try {

                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(EmployeeLeave.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.FromdateEt.getText().toString())){
                    Toast.makeText(EmployeeLeave.this, "Enter the leave start date", Toast.LENGTH_SHORT).show();
                }else  if(TextUtils.isEmpty(binding.TodateEt.getText().toString())){

                        Toast.makeText(EmployeeLeave.this, "Enter the leave end date", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(binding.etSearchLabour.getText().toString())){
                    Toast.makeText(EmployeeLeave.this, "Specify the leave reason", Toast.LENGTH_SHORT).show();
                }else if(modelDateArrayList.size()==0){
                    Toast.makeText(EmployeeLeave.this, "Some internal error occurred", Toast.LENGTH_SHORT).show();
                }else{
                    updateLeaveToFirebase();
                }
            }
        });
    }

    private void updateLeaveToFirebase() {

        Toast.makeText(this, "Leave Request sent for approval", Toast.LENGTH_SHORT).show();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Leaves").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int leaveValue=0;
                long countLeav=snapshot.getChildrenCount()+1;
                String timestamp = "" + System.currentTimeMillis();
                String currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentDate = new android.icu.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                }
                String[] separataed=currentDate.split("-");
                String currentTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                }

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("leaveType",spinnerPolicyAdd.get(binding.spinnerLeaveType.getSelectedItemPosition()));
                    hashMap.put("leaveStartDate",modelDateArrayList.get(0).getDate());
                    hashMap.put("leaveEndDate",modelDateArrayList.get(modelDateArrayList.size()-1).getDate());
                    hashMap.put("leaveDays",binding.txtWorkingDays.getText().toString());
                    hashMap.put("leaveStatus","Requested");
                    hashMap.put("leaveId",timestamp);
                    hashMap.put("leaveApplyDate",currentDate);
                    hashMap.put("leaveApplyTime",currentTime);
                    hashMap.put("leaveCurrentDate",modelDateArrayList.get(modelDateArrayList.size()-1).getDate());
                    hashMap.put("leaveappliedByUid",firebaseAuth.getUid());
                    hashMap.put("leaveappliedByName",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
                    hashMap.put("status","Leave");
                    hashMap.put("reason",binding.etSearchLabour.getText().toString());
                    if(imagesList.size()>0){
                        hashMap.put("documentUpload",true);
                    }else{
                        hashMap.put("documentUpload",false);
                    }
                    reference.child(firebaseAuth.getUid()).child("Leaves").child(timestamp).updateChildren(hashMap);
                    if(imagesList.size()>0) {
                        for (int i = 0; i < imagesList.size(); i++) {


//        String path = MediaStore.Images.Media.insertImage(
//                LoginPic.this.getContentResolver(), image_uri, "IMG_" + System.currentTimeMillis(), null
//        );
//        image_uri1 = Uri.parse(path);

                            String filePathAndName = "LeaveDocuments/" + firebaseAuth.getUid() + "/" + currentDate + "/" + String.valueOf(i + 1);
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                            String finalCurrentDate = currentDate;
                            String finalCurrentTime = currentTime;
                            int finalI = i;
                            storageReference.putFile(imagesList.get(i).getImageURI()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!uriTask.isSuccessful()) ;

//                         refreshGallery(LoginPic.this,timestamp,image_uri);
                                            Uri downloadImageUri = uriTask.getResult();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("documentId", String.valueOf(finalI + 1));
                                            hashMap.put("documentType", imagesList.get(finalI).getExtension());
                                            hashMap.put("documentLink", "" + downloadImageUri);
                                            reference.child(firebaseAuth.getUid()).child("Leaves").child(timestamp).child("Document").child(String.valueOf(finalI)).updateChildren(hashMap);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EmployeeLeave.this, "Document Upload Failed due to :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                    for(int i=0;i<modelDateArrayList.size();i++){
                        Log.e("NGUDUKG",modelDateArrayList.get(i).getDate());
                        String[] separataed1=modelDateArrayList.get(i).getDate().split("-");
                        int finalI = i;
                        reference.child(firebaseAuth.getUid()).child("Attendance").child(separataed1[2]).child(separataed1[1])
                                .child(modelDateArrayList.get(i).getDate())
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        reference .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                                                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                                                .child("Site").child(String.valueOf(siteId))
                                                .child("Attendance").child(separataed[2]).child(separataed[1]).child(modelDateArrayList.get(finalI).getDate())
                                                .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userName","")).updateChildren(hashMap);
                                    }
                                });
                    }
                reference .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                        .child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                        .child("Site").child(String.valueOf(siteId)).child("LeaveRequest").child(timestamp).updateChildren(hashMap);
                        Toast.makeText(EmployeeLeave.this, "Leave Request Sent to the authority. You will be notified once they will approve", Toast.LENGTH_SHORT).show();
                    sendNotification(hashMap,separataed[2],separataed[1],timestamp,getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userName",""));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(HashMap<String, Object> hashMap , String year , String month , String timestamp , String userId) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(tlUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("token").getValue(String.class);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject js = new JSONObject();
                try {
                    JSONObject jsonobject_notification = new JSONObject();

                    jsonobject_notification.put("title", "Employee has requested leave");
                    jsonobject_notification.put("body", name + " " + "requested leave for" + " " +binding.txtWorkingDays.getText().toString()+" "+"days.");


                    JSONObject jsonobject_data = new JSONObject();
                    jsonobject_data.put("imgurl", "https://firebasestorage.googleapis.com/v0/b/haajiri-register.appspot.com/o/Notifications%2Fic_app_logo1.png?alt=media&token=d77857f1-b0e0-4b15-9934-3ec8a88903e9");

                    //JSONObject jsonobject = new JSONObject();

                    js.put("to", token);
                    js.put("notification", jsonobject_notification);
                    js.put("data", jsonobject_data);

                    //js.put("", jsonobject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "https://fcm.googleapis.com/fcm/send";
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.e("response", String.valueOf(response));





                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error :  ", String.valueOf(error));


                    }
                }) {

                    /**
                     * Passing some request headers
                     * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer AAAAOXCkmcc:APA91bFUByxO9XAex4Tdz9fLVUDpRhbAL1XbQ_pKBA0JNFEX_wHhSoMcWzRwbsBDOYV0AzS60c8beYsnHlA9zXj829SlBGScHFH675E5TIkzYHKQZEvDxnRgP9Rv4EUx1_8Nq2HbtK3a ");//TODO add Server key here
                        //headers.put("Content-Type", "application/json");
                        return headers;
                    }

                };
                requestQueue.add(jsonObjReq);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDateRange(String fromDate, String toDate) throws ParseException {
        modelDateArrayList = new ArrayList<>();
        shortDateList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Boolean f = true;
        Date fDate = null, tDate = null;
        Log.e("callFrom", fromDate);
        Log.e("callFrom", toDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date c1 = Calendar.getInstance().getTime();

        Calendar c = Calendar.getInstance();
        Calendar c12 = Calendar.getInstance();
        c12.setTime(sdf.parse(toDate));

        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String currentDate11 = df2.format(c1);
        c.setTime(sdf.parse(fromDate));
        try {
            fDate = dateFormat.parse(fromDate);
            tDate = dateFormat.parse(toDate);
            Log.e("Date1111", "Before" + tDate.toString());


            tDate = c12.getTime();
            Log.e("Date1111", "" + tDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (fDate == null || tDate == null) {
            Log.e("Exception", "Error");
        } else {
            Date temp = fDate;
            Log.e("callTemp", "" + temp);

            int count = 0;
            while (!temp.after(tDate)) {


                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM", Locale.US);
                String date = df.format(temp);
                String date1 = df1.format(temp);
                Log.e("ShreyaMamKaDate", date1);
                modelDateArrayList.add(new ModelDate(date));
                shortDateList.add(new ModelDate(date1));
                Log.e("dateeeee1", modelDateArrayList.get(count).getDate());
                count++;
                Log.e("dateeee", date);
                c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy");
                temp = c.getTime();
                Log.e("Temp", "" + count + ":" + temp);


            }
//
//            Log.e("SizeOfDate", "" + modelDateArrayList.size());
//            Log.e("SizeOfDate", "" + modelDateArrayList.get(0).getDate());
            binding.txtWorkingDays.setText(String.valueOf(modelDateArrayList.size()));






            Log.e("modelDateArrayList", "" + modelDateArrayList.size());

        }
    }

    private void verifyPermissionAndPickImage() {
        Log.e("Permission", "" + (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
        Log.e("Permission", "" + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(EmployeeLeave.this,
                    storge_permissions_33,
                    READ_PERMISSION_CODE);
            //If permission is granted


        } else {
            //no need to check permissions in android versions lower then marshmallow
            ActivityCompat.requestPermissions(EmployeeLeave.this,
                    storge_permissions,
                    READ_PERMISSION_CODE);
        }

    }

    private void pickImage() {
        Log.e("Clicked", "Pick Image");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    private void updateLabel(EditText fromdateEt) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fromdateEt.setText(sdf.format(myCalendar.getTime()));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(this, "" + grantResults[0], Toast.LENGTH_SHORT).show();
                }
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
                binding.etSearchLabour.setText(
                        Objects.requireNonNull(result).get(0));

            }
        } else if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uri = clipData.getItemAt(i).getUri();

                        adapter = new ImagesAdapter(this, imagesList);
                        binding.recyclerView.setAdapter(adapter);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        String extension;
                        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {

                            //If scheme is a content
                            final MimeTypeMap mime = MimeTypeMap.getSingleton();
                            extension = mime.getExtensionFromMimeType(EmployeeLeave.this.getContentResolver().getType(uri));
                            imagesList.add(new CustomModel(getFileNameFromUri(uri), uri,extension));
                            Log.e("Extension","1:::"+extension);
                        } else {
                            //If scheme is a File
                            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
                            imagesList.add(new CustomModel(getFileNameFromUri(uri), uri,extension));
                            Log.e("Extension","2:::"+extension);

                        }
                    }
                } else {
                    Uri uri = data.getData();
                    String extension;
                    if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {

                        //If scheme is a content
                        final MimeTypeMap mime = MimeTypeMap.getSingleton();
                        extension = mime.getExtensionFromMimeType(EmployeeLeave.this.getContentResolver().getType(uri));
                        imagesList.add(new CustomModel(getFileNameFromUri(uri), uri,extension));
                        Log.e("Extension","3:::"+extension);
                    } else {
                        //If scheme is a File
                        //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                        extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
                        imagesList.add(new CustomModel(getFileNameFromUri(uri), uri,extension));
                        Log.e("Extension","4:::"+extension);

                    }
                    adapter = new ImagesAdapter(this, imagesList);
                    binding.recyclerView.setAdapter(adapter);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = EmployeeLeave.this.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        String[] result1 = result.split(".", 1);
        return result1[0];
    }

    class LeavePolicyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return spinnerPolicyAdd.size();
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
            designationText.setText(spinnerPolicyAdd.get(position));


            return row;
        }
    }


}