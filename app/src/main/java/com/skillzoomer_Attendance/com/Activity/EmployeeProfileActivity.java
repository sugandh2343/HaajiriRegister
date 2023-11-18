package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEmployeeProfileBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EmployeeProfileActivity extends AppCompatActivity {
    ActivityEmployeeProfileBinding binding;
    FirebaseAuth firebaseAuth;
    ModelEmployee modelEmployee;
    private final Calendar myCalendar = Calendar.getInstance();
    String dob;
    private static final int camera_request_code=200;
    private static final int storage_request_code=300;
    private static final int image_pick_gallery_code=400;
    private static final int image_pick_camera_code=500;
    private Uri image_uri;
    private String[] cameraPermissions;
    public static String[] storge_permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmployeeProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        modelEmployee=new ModelEmployee();
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
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelEmployee=snapshot.getValue(ModelEmployee.class);
                binding.etName.setText(modelEmployee.getName());
                binding.mobileNoTV.setText(modelEmployee.getMobile());
                binding.designation.setText(modelEmployee.getDesignation());
                if(modelEmployee.getAadhar()==null || modelEmployee.getAadhar().equals("")){
                    binding.txtAadharNo.getText().clear();
                    binding.txtAadharNo.setHint("Enter Aadhar No");

                }else{
                    binding.txtAadharNo.setText(modelEmployee.getAadhar());
                }
                binding.txtGender.setText(modelEmployee.getGender());
                if(modelEmployee.getDob()==null || modelEmployee.getDob().equals("")){
                    binding.txtDob.setFocusable(false);
                    binding.txtDob.getText().clear();
                    binding.txtDob.setHint("Date of Birth");
                    DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        dob = sdf.format(myCalendar.getTime());

                        updateLabel(binding.txtDob);
                    };
                    binding.txtDob.setOnClickListener((View v) -> {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(EmployeeProfileActivity.this, date1, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();


                    });
                }else {
                    binding.txtDob.setText(modelEmployee.getDob());
                }
                if(modelEmployee.getProfile()==null || modelEmployee.getProfile().equals("")){
                    binding.imgProfile.setImageResource(R.drawable.ic_upload);
                    binding.imgProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }else{
                    Picasso.get().load(modelEmployee.getProfile()).
                            resize(400,400).centerCrop()
                            .placeholder(R.drawable.ic_add).into(binding.imgProfile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickdialog();
            }
        });


    }
    private void updateLabel(EditText etDoj) {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDoj.setText(sdf.format(myCalendar.getTime()));
    }
    private void showImagePickdialog() {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        String[] options={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            if(check_camera_permission()){
                                pick_from_camera();
                            }
                            else{
                                requestCameraPermission();
                            }

                        }
                        else{
                            if(check_storage_permission()){
                                pick_from_gallery();

                            }
                            else{
                                requestStoragePermission();
                            }

                        }
                    }
                });
        builder.show();
    }
    private boolean check_storage_permission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        Log.d("Result",""+result);
        return result;
    }

    private void requestStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(EmployeeProfileActivity.this,
                    storge_permissions_33,
                    storage_request_code);
            //If permission is granted


        } else {
            //no need to check permissions in android versions lower then marshmallow
            ActivityCompat.requestPermissions(EmployeeProfileActivity.this,
                    storge_permissions,
                    storage_request_code);
        }

    }
    private void pick_from_camera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_ Image Description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,image_pick_camera_code);
    }

    private boolean check_camera_permission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void pick_from_gallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,image_pick_gallery_code);
    }

    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(EmployeeProfileActivity.this,
                    storge_permissions_33,
                    camera_request_code);
            //If permission is granted


        } else {
            //no need to check permissions in android versions lower then marshmallow
            ActivityCompat.requestPermissions(EmployeeProfileActivity.this,
                    storge_permissions,
                    camera_request_code);
        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case camera_request_code: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("storage","Storage Accepted -"+storageAccepted);
                    Log.d("Camera","Camera Accepted -"+cameraAccepted);
                    Log.d("length","Grant Results -"+grantResults[0]);
                    Log.d("temp",""+PackageManager.PERMISSION_GRANTED);
                    if (cameraAccepted && storageAccepted) {
                        pick_from_camera();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Camera Permissions Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case storage_request_code: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("temp",""+PackageManager.PERMISSION_GRANTED);
                    Log.d("length","Grant Results -"+grantResults[0]);
                    Log.d("temp",""+PackageManager.PERMISSION_GRANTED);
                    if (storageAccepted) {
                        //Permission Allowed
                        Log.d("compiler","is at current position");
                        pick_from_gallery();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Storage Permission Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==RESULT_OK){
            if(requestCode==image_pick_gallery_code){
                image_uri=data.getData();
                binding.imgProfile.setImageURI(image_uri);

            }
            else if(requestCode==image_pick_camera_code){
                image_uri=data.getData();
                binding.imgProfile.setImageURI(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}