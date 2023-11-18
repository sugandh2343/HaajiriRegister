package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

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
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEditLabourBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditLabourActivity extends AppCompatActivity {
    ActivityEditLabourBinding binding;
    private String labourId;
    private int siteId;
    private ArrayList<ModelLabour> modelLabourArrayList;
    private ProgressDialog progressDialog;
    String editTextSelectionButtonId = "";
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private final int REQUEST_IMAGE = 400;
    private final int REQUEST_GALLERY = 401;
    private Bitmap image_uri;
    private Uri image_uri1;
    ArrayList<String> result;
    private String selectedType;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditLabourBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        modelLabourArrayList=new ArrayList<>();
        Intent intent=getIntent();
        labourId=intent.getStringExtra("LabourId");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        siteId=intent.getIntExtra("siteId",0);
        if(labourId!=null && !labourId.equals("")&& siteId>0){
            progressDialog.show();
            getLabourDetails(labourId,siteId);
        }
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
            public void beforeTextChanged(CharSequence charSequence , int i , int i1 , int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence , int i , int i1 , int i2) {
                if(charSequence.length()>=2){
                    binding.rlProfile.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                if(TextUtils.isEmpty(binding.txtLabourName.getText().toString())){
                    Toast.makeText(EditLabourActivity.this, "Enter Labour Name", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(binding.txtLabourWages.getText().toString())){
                    Toast.makeText(EditLabourActivity.this, "Enter Labour per day Wages", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    registerLabour(siteId,labourId);
                }

            }
        });

    }

    private void registerLabour(int siteId, String labourId) {

        if(image_uri==null){
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("name",binding.txtLabourName.getText().toString());
            hashMap.put("fatherName",binding.txtLabourFathersName.getText().toString());
            hashMap.put("wages",Long.parseLong(binding.txtLabourWages.getText().toString()));
            hashMap.put("uniqueId",binding.txtLabourIdNumber.getText().toString());
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                        updateLabourId(timestamp);
                            String message;
                            if(selectedType.equals("Skilled")){
                                message=getString(R.string.skilled_worker_with_worker_id);
                            }else{
                                message=getString(R.string.unskilled_worker_with_worker_id);
                            }
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditLabourActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle(R.string.success)
                                    .setMessage(message+ labourId +getString(R.string.updated_successfully))
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                                        binding.txtLabourName.getText().clear();
                                        binding.txtLabourFathersName.getText().clear();
                                        binding.txtLabourIdNumber.getText().clear();
                                        binding.txtLabourWages.getText().clear();
                                        binding.editImageView.setImageResource(R.drawable.edit_profile_image);
                                        binding.profileImageView.setImageResource(R.drawable.my_profile);
                                        image_uri1=null;
                                        image_uri=null;
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(EditLabourActivity.this, timelineActivity.class));

                                    });

                            builder.show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditLabourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }else{
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image_uri.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] data = bytes.toByteArray();
            String filePathAndName="Labour/"+String.valueOf(siteId)+"/"+ labourId;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri=uriTask.getResult();
                            if(uriTask.isSuccessful()) {
                                String timestamp=""+System.currentTimeMillis();
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("name",binding.txtLabourName.getText().toString());
                                hashMap.put("fatherName",binding.txtLabourFathersName.getText().toString());
                                hashMap.put("wages",Long.parseLong(binding.txtLabourWages.getText().toString()));
                                hashMap.put("uniqueId",binding.txtLabourIdNumber.getText().toString());
                                hashMap.put("timestamp",timestamp);
                                hashMap.put("profile",""+downloadImageUri);

                                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
//                        updateLabourId(timestamp);
                                                String message;
                                                if(selectedType.equals("Skilled")){
                                                    message=getString(R.string.skilled_worker_with_worker_id);
                                                }else{
                                                    message=getString(R.string.unskilled_worker_with_worker_id);
                                                }
                                                progressDialog.dismiss();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(EditLabourActivity.this);
                                                builder.setCancelable(false);
                                                builder.setTitle(R.string.success)
                                                        .setMessage(message+ labourId +getString(R.string.updated_successfully))
                                                        .setCancelable(false)
                                                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                                                            binding.txtLabourName.getText().clear();
                                                            binding.txtLabourFathersName.getText().clear();
                                                            binding.txtLabourIdNumber.getText().clear();
                                                            binding.txtLabourWages.getText().clear();
                                                            binding.editImageView.setImageResource(R.drawable.edit_profile_image);
                                                            binding.profileImageView.setImageResource(R.drawable.my_profile);
                                                            image_uri1=null;
                                                            image_uri=null;
                                                            dialogInterface.dismiss();

                                                        });

                                                builder.show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditLabourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
        }

    }

    private void permissionCheck() {
        Dexter.withActivity(EditLabourActivity.this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(EditLabourActivity.this);
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
        Uri uri = Uri.fromParts("package", EditLabourActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void launchCameraIntentForTax() {
//        ContentValues contentValues=new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_ Image Description");
//        image_uri1=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,REQUEST_IMAGE);
    }
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
                    .makeText(EditLabourActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void getLabourDetails(String labourId, int siteId) {
        modelLabourArrayList.clear();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                binding.txtLabourName.setText(snapshot.child("name").getValue(String.class));
                binding.txtLabourFathersName.setText(snapshot.child("fatherName").getValue(String.class));
                binding.txtLabourWages.setText(String.valueOf(snapshot.child("wages").getValue(long.class)));
                binding.txtLabourIdNumber.setText(snapshot.child("uniqueId").getValue(String.class));
                selectedType=snapshot.child("type").getValue(String.class);
                if(snapshot.child("profile").getValue(String.class)!=null && !snapshot.child("profile").getValue(String.class).equals("")){
                    progressDialog.dismiss();
                    image_uri1=Uri.parse(snapshot.child("profile").getValue(String.class));
                    Picasso.get().load(snapshot.child("profile").getValue(String.class)).
                            resize(400, 400).centerCrop()
                            .placeholder(R.drawable.ic_download).into(binding.profileImageView);
                }else{
                    progressDialog.dismiss();
                    binding.profileImageView.setImageResource(R.drawable.ic_download);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
            binding.profileImageView.setImageBitmap(image_uri);


            //callProfileUpload(base64);
        }
    }
}