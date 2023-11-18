package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityForgotPasswordBinding;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    private String verificationId;
    String oldPassword;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        binding.llStart.setVisibility(View.VISIBLE);
        binding.llVerifyOtp.setVisibility(View.GONE);
        binding.llPassword.setVisibility(View.GONE);
        firebaseAuth=FirebaseAuth.getInstance();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        binding.btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                getUserIdDetails(binding.etUserId.getText().toString());
            }
        });
        binding.verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.MobileOtpPinview.getText().toString())){
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.otp_empty), Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    verifyCode(binding.MobileOtpPinview.getText().toString());

                }
            }
        });
        binding.txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
            }
        });
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etPassword.getText().toString())){
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.password_cannot_be_empty), Toast.LENGTH_SHORT).show();
                }else if(!binding.etPassword.getText().toString().equals(binding.etConfirmPassword.getText().toString())){
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show();
                }else if(binding.etPassword.getText().toString().length()<7){
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.password_min_6_characters), Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    resetPassword(binding.etUserId.getText().toString(),oldPassword,binding.etConfirmPassword.getText().toString());
                }
            }
        });

    }

    private void resetPassword(String toString, String oldPassword, String toString1) {
        firebaseAuth.signInWithEmailAndPassword(toString+"@yopmail.com",oldPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                changePassword(toString,oldPassword,toString1);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                     Log.e("Exception",e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, "Some error occurred", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void changePassword(String toString, String oldPassword, String toString1) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(toString+"@yopmail.com", oldPassword);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(toString1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("Tag", "Password updated");
                                       updateOnDatabase();
                                    } else {
                                        progressDialog.dismiss();
                                        Log.e("TAG", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d("TAG", "Error auth failed");
                        }
                    }
                });
    }

    private void updateOnDatabase() {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("password",binding.etConfirmPassword.getText().toString());
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        signOut();
                    }
                });
    }

    private void signOut() {
        progressDialog.dismiss();
        firebaseAuth.signOut();
        startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));

    }

    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        progressDialog.dismiss();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        binding.llStart.setVisibility(View.GONE);
        binding.llVerifyOtp.setVisibility(View.GONE);
        binding.llPassword.setVisibility(View.VISIBLE);




        // after getting credential we are
        // calling sign in method.

    }

    private void getUserIdDetails(String toString) {
        Log.e("Phone",toString);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("userId").equalTo(toString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Phone",""+snapshot.getChildrenCount());
                Log.e("Phone",""+ Objects.requireNonNull(snapshot.getValue()).toString());
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        ModelUser modelUser=ds.getValue(ModelUser.class);
                        String mobile= modelUser.getMobile();
                        oldPassword=modelUser.getPassword();
                        String phone="+91"+mobile;
                        sendVerificationCode(phone,mobile);

                    }


                }else{
                    Toast.makeText(ForgotPasswordActivity.this, "No user Id found", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendVerificationCode(String phone, String mobile) {
        Log.e("Phone",phone);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressDialog.dismiss();
                        binding.llStart.setVisibility(View.GONE);
                        binding.llVerifyOtp.setVisibility(View.VISIBLE);
                        binding.llPassword.setVisibility(View.GONE);
                        verificationId = s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


                        Log.e("Verification","Completed");
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}