package com.skillzoomer_Attendance.com.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityViewPaymentReportBinding;

public class View_paymentReport extends AppCompatActivity {
    ActivityViewPaymentReportBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payment_report);
    }
}