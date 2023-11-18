package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.razorpay.PaymentResultListener;
import com.skillzoomer_Attendance.com.databinding.ActivityMakePaymentBinding;

public class MakePaymentActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityMakePaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMakePaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void onPaymentSuccess(String s) {

    }

    @Override
    public void onPaymentError(int i, String s) {

    }
}