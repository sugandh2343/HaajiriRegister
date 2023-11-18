package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.skillzoomer_Attendance.com.Adapter.TableViewAdapter;
import com.skillzoomer_Attendance.com.Adapter.TableViewAdapterEmployee;
import com.skillzoomer_Attendance.com.Adapter.TableViewListener;
import com.skillzoomer_Attendance.com.Adapter.TableViewModel;
import com.skillzoomer_Attendance.com.Adapter.TableViewModelEmployee;
import com.skillzoomer_Attendance.com.Model.ModelAttendanceEmployee;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.Model.ModelMonth;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityAllEmployeeAttendanceReportBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AllEmployeeAttendanceReport extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityAllEmployeeAttendanceReportBinding binding;
    long siteId;
    String siteName;
    DatabaseReference reference;
    HashMap<String, Object> monthHashMap;
    HashMap<String, Object> yearHashMap;
    private ArrayList<ModelMonth> monthArrayList;
    private ArrayList<ModelMonth> yearArraylist;
    private ArrayList<ModelMonth> dateArrayList;
    String selectedMonth, selectedYear;
    int noOfdays;
    private ArrayList<ModelEmployee> employeeArrayList;
    private String dnl_file_type = "";
    private ArrayList<ModelAttendanceEmployee> attendanceEmployees;
    private ArrayList<ModelAttendanceEmployee> attendanceEmployeesSorted;
    private ArrayList<ModelAttendanceEmployee> attendanceEmployeesOff;
    private ArrayList<String> weekOffList;
    FirebaseAuth firebaseAuth;
    int countArray=0;
    long AmountRuleExcel=0,AmountRulePdf=0;
    int amount = 0;
    int AmountTemp = 0;
    FileOutputStream fos = null;
    AlertDialog alertDialogPaymentConfirm = null;
    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllEmployeeAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        siteId = intent.getLongExtra("siteId", 0);

        siteName = intent.getStringExtra("siteName");
        firebaseAuth = FirebaseAuth.getInstance();
        String timestamp = "" + System.currentTimeMillis();
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        String[] separataed = currentDate.split("-");
        selectedMonth = separataed[1];
        selectedYear = separataed[2];
        binding.txtMonth.setText(separataed[1]);
        binding.txtYear.setText(separataed[2]);
        monthArrayList = new ArrayList<>();
        yearArraylist = new ArrayList<>();
        dateArrayList = new ArrayList<>();
        employeeArrayList = new ArrayList<>();
        attendanceEmployees = new ArrayList<>();
        attendanceEmployeesOff = new ArrayList<>();
        attendanceEmployeesSorted = new ArrayList<>();
        weekOffList = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrUid", "")).child("Industry")
                .child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""))
                .child("Site").child(String.valueOf(siteId));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                weekOffList.clear();
                if (snapshot.child("Sunday").getValue(Boolean.class)) {
                    weekOffList.add(new String("Sunday"));
                } else if (snapshot.child("Monday").getValue(Boolean.class)) {
                    weekOffList.add(new String("Sunday"));
                } else if (snapshot.child("Tuesday").getValue(Boolean.class)) {
                    weekOffList.add(new String("Sunday"));
                } else if (snapshot.child("Wednesday").getValue(Boolean.class)) {
                    weekOffList.add(new String("Sunday"));
                } else if (snapshot.child("Thursday").getValue(Boolean.class)) {
                    weekOffList.add(new String("Sunday"));
                } else if (snapshot.child("Friday").getValue(Boolean.class)) {
                    weekOffList.add(new String("Sunday"));
                } else if (snapshot.child("Saturday").getValue(Boolean.class)) {
                    weekOffList.add(new String("Sunday"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getAttendanceInfo();
        reference.child("Attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthHashMap = new HashMap<>();
                yearHashMap = new HashMap<>();
                monthArrayList.clear();
                yearArraylist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    yearHashMap.put(ds.getKey(), ds.getKey());
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        Log.e("KEYVALUE", ds.getKey());
                        monthHashMap.put(ds1.getKey(), ds1.getKey());
                    }
                }
                for (HashMap.Entry<String, Object> entry : yearHashMap.entrySet()) {
                    ModelMonth modelMonth = new ModelMonth();
                    modelMonth.setMonth(entry.getKey());
                    yearArraylist.add(modelMonth);

                }
                for (HashMap.Entry<String, Object> entry : monthHashMap.entrySet()) {
                    ModelMonth modelMonth = new ModelMonth();
                    modelMonth.setMonth(entry.getKey());
                    monthArrayList.add(modelMonth);

                }
                monthArrayList.add(0, new ModelMonth("Change month"));
                yearArraylist.add(0, new ModelMonth("Change Year"));
                MonthSpinnerAdapter monthSpinnerAdapter = new MonthSpinnerAdapter();
                binding.spinnerMonth.setAdapter(monthSpinnerAdapter);
                YearSpinnerAdapter yearSpinnerAdapter = new YearSpinnerAdapter();
                binding.spinnerYear.setAdapter(yearSpinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedMonth = monthArrayList.get(i).getMonth();
                    getAttendanceInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedYear = yearArraylist.get(i).getMonth();
                    reference.child("Attendance").child(selectedYear).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (HashMap.Entry<String, Object> entry : monthHashMap.entrySet()) {
                                ModelMonth modelMonth = new ModelMonth();
                                modelMonth.setMonth(entry.getKey());
                                monthArrayList.add(modelMonth);

                            }

                            MonthSpinnerAdapter monthSpinnerAdapter = new MonthSpinnerAdapter();
                            binding.spinnerMonth.setAdapter(monthSpinnerAdapter);
                            selectedMonth = monthArrayList.get(0).getMonth();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
                reference.child("Attendance").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                        AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final AlertDialog.Builder alert = new AlertDialog.Builder(AllEmployeeAttendanceReport.this);
                View mView = getLayoutInflater().inflate(R.layout.show_file_type, null);

                RadioButton rb_pdf, rb_xls;
                Button btn_pay;
                TextView txt_amount;
                LinearLayout ll_amount;
                ImageView iv_close;
                rb_pdf = mView.findViewById(R.id.rb_pdf);
                rb_xls = mView.findViewById(R.id.rb_xls);
                btn_pay = mView.findViewById(R.id.btn_pay);
                txt_amount = mView.findViewById(R.id.txt_amount);
                ll_amount = mView.findViewById(R.id.ll_amount);
                iv_close = mView.findViewById(R.id.iv_close);
                ll_amount.setVisibility(View.GONE);
                alert.setView(mView);
                AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                rb_pdf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            rb_xls.setChecked(false);
                            dnl_file_type = "pdf";
                            amount = Math.round(30*5);
                            if (amount < AmountRulePdf) {
                                ll_amount.setVisibility(View.VISIBLE);
                                txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(amount));
                                AmountTemp = amount;
                            } else {
                                ll_amount.setVisibility(View.GONE);
                                AmountTemp = (int) AmountRulePdf;
                            }


                        }
                    }
                });
                rb_xls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            rb_pdf.setChecked(false);
                            dnl_file_type = "xls";
                            amount = Math.round(30*10);
                            if (amount < AmountRuleExcel) {
                                ll_amount.setVisibility(View.VISIBLE);
                                txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(amount));
                                AmountTemp = amount;
                            } else {
                                ll_amount.setVisibility(View.GONE);
                                AmountTemp = (int) AmountRuleExcel;
                            }


                        }
                    }
                });

                btn_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dnl_file_type.equals("")) {
                            Toast.makeText(AllEmployeeAttendanceReport.this, "Select file type", Toast.LENGTH_SHORT).show();
                        } else {

                            showPaymentDialog();
                            alertDialog.dismiss();
                        }
                    }
                });
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });


    }
    private void showPaymentDialog() {

        file = null;
        fos = null;
        final AlertDialog.Builder alert = new AlertDialog.Builder(AllEmployeeAttendanceReport.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_dialog, null);

        TextView txt_file_type, txt_from, txt_to, txt_amount, txt_mode_of_payment, txt_heading;
        Spinner spinner_promo;
        Button btn_cancel, btn_pay;
        txt_file_type = mView.findViewById(R.id.txt_file_type);
        txt_from = mView.findViewById(R.id.txt_from);
        txt_to = mView.findViewById(R.id.txt_to);
        txt_amount = mView.findViewById(R.id.txt_amount);
        txt_mode_of_payment = mView.findViewById(R.id.txt_mode_of_payment);
        txt_heading = mView.findViewById(R.id.txt_heading);
        spinner_promo = mView.findViewById(R.id.spinner_promo);
        spinner_promo.setVisibility(View.GONE);

        Log.e("Dnl_file", dnl_file_type);
//        if(dnl_file_type.equals("Pdf")){
//            amount = Math.round((modelDateArrayList.size()*5));
//        }else{
//            amount = Math.round((modelDateArrayList.size()*10));
//        }


//        AmountTemp = amount;



        txt_heading.setText(getString(R.string.your_file_one_click_away) + " " + String.valueOf(AmountTemp) + " " + getString(R.string.your_have_tom_make_payment));
        txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(AmountTemp));
        btn_cancel = mView.findViewById(R.id.btn_cancel);
        btn_pay = mView.findViewById(R.id.btn_pay);
        txt_file_type.setText(getString(R.string.attendance_report));
        txt_from.setText(dateArrayList.get(0).getMonth());
        txt_to.setText(dateArrayList.get(dateArrayList.size() - 1).getMonth());

        txt_mode_of_payment.setText("Razorpay");
        alert.setView(mView);
        alertDialogPaymentConfirm = alert.create();
        alertDialogPaymentConfirm.setCanceledOnTouchOutside(false);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                amount=AmountTemp;
                if (dnl_file_type.equals("xls")) {
//                    DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList,siteId,siteName);
                    Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
                    createOrderId.start();
                } else {
//                    DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList,siteId,siteName);
                    Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
                    createOrderId.start();
                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogPaymentConfirm.dismiss();
            }
        });


//
        alertDialogPaymentConfirm.show();
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

    }

    public class createOrderIdThread implements Runnable {
        int amount;

        public createOrderIdThread(int amount) {
            this.amount = amount;
        }

        @Override
        public void run() {
            RazorpayClient razorpay = null;
            try {
                razorpay = new RazorpayClient(getString(R.string.razorpay_key_id), getString(R.string.razorpay_key_secret));
            } catch (RazorpayException e) {
                e.printStackTrace();
            }
            JSONObject orderRequest = new JSONObject();
            try {
                Log.e("Amt", "Request" + amount);
                orderRequest.put("amount", amount * 100); // amount in the smallest currency unit
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_11");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Exception", "COID");
            Order order = null;
            try {
                order = razorpay.orders.create(orderRequest);
            } catch (RazorpayException e) {
                Log.e("Exception", e.getMessage());
                e.printStackTrace();
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(String.valueOf(order));
                String id = jsonObject.getString("id");
                Log.e("Response", "" + id);
                if (id != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startPayment(id, amount);
                        }
                    });
                }

            } catch (JSONException e) {
                Log.e("Exception", e.getMessage());
                e.printStackTrace();
            }


        }
    }

    private void startPayment(String id, int amount) {
        Checkout.preload(this);
        Checkout checkout = new Checkout();
        checkout.setKeyID(getString(R.string.razorpay_key_id));
        /**
         * Instantiate Checkout
         */


        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo_razor);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", getString(R.string.app_name));
            options.put("description", "Haj123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("order_id", id);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(amount));//pass amount in currency subunits
            options.put("prefill.email", getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("userName", "") + "@yopmail.com");
            options.put("prefill.contact", getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("userMobile", ""));
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(this, options);
//            progressDialog.dismiss();

        } catch (Exception e) {
            Log.e("Sugandh", "Error in starting Razorpay Checkout", e);
        }
    }

    private void getAttendanceInfo() {
        Log.e("Selected", selectedMonth);
        reference.child("Attendance").child(selectedYear).child(selectedMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateArrayList.clear();
                if (selectedMonth.equals("Jan") || selectedMonth.equals("Mar") || selectedMonth.equals("May") || selectedMonth.equals("Jul") ||
                        selectedMonth.equals("Aug") || selectedMonth.equals("Oct") || selectedMonth.equals("Dec")) {
                    noOfdays = 31;
                } else if (selectedMonth.equals("Apr") || selectedMonth.equals("Jun") || selectedMonth.equals("Sep") || selectedMonth.equals("Nov")) {
                    noOfdays = 30;
                } else if (selectedMonth.equals("Feb") && Integer.parseInt(selectedYear) % 4 == 0) {
                    noOfdays = 29;
                } else {
                    noOfdays = 28;
                }
                for (int i = 1; i <= noOfdays; i++) {
                    String dateselected = "";
                    if (i < 10) {
                        dateselected = "" + 0 + "" + i;
                    } else {
                        dateselected = "" + i;
                    }
                    String date = dateselected + "-" + selectedMonth + "-" + selectedYear;
                    Log.e("Selected", date);
                    dateArrayList.add(new ModelMonth(date));

                }
                reference.child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        employeeArrayList.clear();
                        attendanceEmployeesOff.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelEmployee modelEmployee = ds.getValue(ModelEmployee.class);
                            employeeArrayList.add(modelEmployee);
                        }
                        int count=0;
                        countArray=0;
                        attendanceEmployees.clear();
                        attendanceEmployeesSorted.clear();
                        getAttendanceStatus();
//                        for (int j = 0; j < employeeArrayList.size(); j++) {
//                        for (int i = 0; i < dateArrayList.size(); i++) {
//                            count++;
//                            Log.e("COUNHGCGHC",""+count);
//                            Log.e("DATESEND",dateArrayList.get(i).getMonth());
//
//                            }
//                        }
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
    }
    private void getAttendanceStatus() {

        reference.child("Attendance").child(selectedYear).child(selectedMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                countArray=0;
                for (int i = 0; i < dateArrayList.size(); i++) {
                    for (int j = 0; j < employeeArrayList.size(); j++) {

                        count++;
                        Log.e("COUNHGCGHC",""+count);
                        Log.e("DATESEND",dateArrayList.get(i).getMonth());
                        Calendar c = Calendar.getInstance();
                        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy");

                        String dayOfDate = "";
                        Boolean dayOffresult = false;
                        DateTimeFormatter dtfInput = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dtfInput = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                        }
                        DateTimeFormatter dtfOutput = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dtfOutput = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            dayOfDate = LocalDate.parse(dateArrayList.get(i).getMonth(), dtfInput).format(dtfOutput);

                        }

                        // yourdate is an object of type Date
//                if(weekOffList.contains(dayOfDate)){
//
//                }

                        for (int k = 0; k < weekOffList.size(); k++) {
                            if (dayOfDate.equals(weekOffList.get(k))) {
                                dayOffresult = true;

                            }
                        }
                        Log.e("WeekOff",""+dayOffresult);
                        if(dayOffresult) {
                            if (snapshot.child(dateArrayList.get(i).getMonth()).exists()) {
                                if(snapshot.child(dateArrayList.get(i).getMonth()).child(employeeArrayList.get(j).getUserId()).exists()){
                                    String status=snapshot.child(dateArrayList.get(i).getMonth()).child(employeeArrayList.get(j).getUserId()).child("status").getValue(String.class);
                                    if(status.equals("P")){
                                        addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"P",count);
                                    }else if(status.equals("Pending")){
                                        addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"IC",count);
                                    }else if(status.equals("Leave")){
                                        if(snapshot.child(dateArrayList.get(i).getMonth()).child(employeeArrayList.get(j).getUserId()).child("leaveStatus").getValue(String.class).equals("Approved")){
//                                        String leaveType=snapshot.child(date).child("leaveType").getValue(String.class);
                                            addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"Le",count);
                                        }else{
                                            addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"LR",count);
                                        }
                                    }else{
                                        addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"A",count);
                                    }
                                }else{
                                    addToArray(dateArrayList.get(i).getMonth(), employeeArrayList.get(j).getUserId(),
                                            employeeArrayList.get(j).getName(), "WO", count);
                                }

                            } else {

                                addToArray(dateArrayList.get(i).getMonth(),  employeeArrayList.get(j).getUserId(),
                                        employeeArrayList.get(j).getName(), "WO", count);
                            }
                        }else if(snapshot.child(dateArrayList.get(i).getMonth()).exists()){
                            if(snapshot.child(dateArrayList.get(i).getMonth()).child(employeeArrayList.get(j).getUserId()).exists()){
                                String status=snapshot.child(dateArrayList.get(i).getMonth()).child(employeeArrayList.get(j).getUserId()).child("status").getValue(String.class);
                                if(status.equals("P")){
                                    addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"P",count);
                                }else if(status.equals("Pending")){
                                    addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"IC",count);
                                }else if(status.equals("Leave")){
                                    if(snapshot.child(dateArrayList.get(i).getMonth()).child(employeeArrayList.get(j).getUserId()).child("leaveStatus").getValue(String.class).equals("Approved")){
//                                        String leaveType=snapshot.child(date).child("leaveType").getValue(String.class);
                                        addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"Le",count);
                                    }else{
                                        addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"LR",count);
                                    }
                                }else{
                                    addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"A",count);
                                }
                            }else{
                                addToArray(dateArrayList.get(i).getMonth(),employeeArrayList.get(j).getUserId(),employeeArrayList.get(j).getName(),"A",count);
                            }


                        }else{
                            int finalI = i;
                            int finalJ = j;
                            int finalCount = count;
                            reference.child("Holiday").child(dateArrayList.get(i).getMonth()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(snapshot.exists()){
                                        addToArray(dateArrayList.get(finalI).getMonth(),employeeArrayList.get(finalJ).getUserId(),employeeArrayList.get(finalJ).getName(),"Ho", finalCount);
                                    }else{
                                        addToArray(dateArrayList.get(finalI).getMonth(),employeeArrayList.get(finalJ).getUserId(),employeeArrayList.get(finalJ).getName(),"A", finalCount);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

//    private void getAttendanceStatus(String date, String userId, String name, DatabaseReference reference, int count) {
//        attendanceEmployees.clear();
//        attendanceEmployeesSorted.clear();
//
//
//        reference.child("Attendance").child(selectedYear).child(selectedMonth).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.e("DATEINCOMING", date);
//
//                Calendar c = Calendar.getInstance();
//                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy");
//
//                String dayOfDate = "";
//                Boolean dayOffresult = false;
//                DateTimeFormatter dtfInput = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    dtfInput = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
//                }
//                DateTimeFormatter dtfOutput = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    dtfOutput = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
//                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    Log.e("YGDTHDYTD", "" + date + "-" + LocalDate.parse(date, dtfInput).format(dtfOutput) + "WOOOOO__--__" + weekOffList.size());
//                    dayOfDate = LocalDate.parse(date, dtfInput).format(dtfOutput);
//
//                }
//
//                // yourdate is an object of type Date
////                if(weekOffList.contains(dayOfDate)){
////
////                }
//
//                for (int i = 0; i < weekOffList.size(); i++) {
//                    if (dayOfDate.equals(weekOffList.get(i))) {
//                        dayOffresult = true;
//
//                    }
//                }
//
//                Log.e("SNALE",""+snapshot.exists()+"-----"+date);
//
//
//                if (snapshot.exists()) {
////                    Log.e("Snpshkjg",""+snapshot.child(userId).exists());
//                    if (snapshot.child(userId).exists()) {
//                        if (!dayOffresult) {
//                            if (snapshot.child(userId).child("status").getValue(String.class) != null) {
//                                if (snapshot.child(userId).child("status").getValue(String.class).equals("Pending")) {
//                                    addToArray(date, userId, name, "NC", count);
//                                    Log.e("ADDEDFIRST", date);
////                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"NC"));
//
//                                } else if (snapshot.child(userId).child("status").getValue(String.class).equals("P")) {
//                                    addToArray(date, userId, name, "P", count);
//                                    Log.e("ADDEDFIRST", date);
////                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"P"));
//                                }
//                            } else {
//                                addToArray(date, userId, name, "P", count);
//                                Log.e("ADDEDFIRST", date);
////                                attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"P"));
//                            }
//                        } else {
//                            if (snapshot.child(userId).child("status").getValue(String.class) != null) {
//                                if (snapshot.child(userId).child("status").getValue(String.class).equals("Pending")) {
//                                    addToArray(date, userId, name, "NC", count);
//                                    Log.e("ADDEDFIRST", date);
////                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"NC"));
//
//                                } else if (snapshot.child(userId).child("status").getValue(String.class).equals("P")) {
//                                    addToArray(date, userId, name, "CO", count);
//                                    Log.e("ADDEDFIRST", date);
////                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"CO"));
//                                }
//                            } else {
//                                addToArray(date, userId, name, "WO", count);
//                                Log.e("ADDEDFIRST", date);
////                                attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"WO"));
//                            }
//                        }
//
//
//                    } else {
//                        //TODO LEAVE CHECK AT THIS LEVEL
//                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
//                        reference1.child(firebaseAuth.getUid()).child("Leaves").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists()) {
//                                    if (snapshot.child("leaveType").getValue(String.class).equals("LWP")) {
//                                        addToArray(date, userId, name, "LWP", count);
//                                        Log.e("ADDEDFIRST", date);
////                                        attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"LWP"));
//                                    } else {
//                                        addToArray(date, userId, name, snapshot.child("leaveType").getValue(String.class), count);
//                                        Log.e("ADDEDFIRST", date);
////                                        attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,snapshot.child("leaveType").getValue(String.class)));
//                                    }
//                                } else {
//                                    addToArray(date, userId, name, "A", count);
//                                    Log.e("ADDEDFIRST", date);
////                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"A"));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                }
//                else {
//
//
//
//                    if (dayOffresult) {
////                        Log.e("Snpshkjg","100"+date);
//                        addToArray(date, userId, name, "WO", count);
////                        attendanceEmployees.add(new ModelAttendanceEmployee(date, userId, name, "WO"));
//                    } else {
//                        reference.child("Holiday").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                if (snapshot.exists()) {
//                                    addToArray(date, userId, name, "Ho", count);
////                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"Ho"));
//                                } else {
//                                    reference.child("Events").child("Announcement").addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            for (DataSnapshot ds : snapshot.getChildren()) {
//                                                String inputPattern = "dd-MMM-yyyy";
//                                                String outputPattern = "dd/MM/yyyy";
//                                                SimpleDateFormat inputFormat = null;
//                                                SimpleDateFormat outputFormat = null;
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                                    inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
//                                                }
//
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                                    outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
//                                                }
//
//                                                Date date1 = null;
//                                                String str = null;
//
//                                                try {
//                                                    String strCurrentDate = date;
//                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                                        date1 = inputFormat.parse(strCurrentDate);// it's format should be same as inputPattern
//                                                    }
//                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                                        str = outputFormat.format(date1);
//                                                    }
//                                                    Log.e("Log ", "str " + str);
//                                                } catch (ParseException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                Log.e("Snpshkjg",""+(ds.child("eventDate").getValue(String.class).equals(str) && ds.child("eventCategory").getValue(String.class).equals("Office Closed"))+"ufghf"+date);
//
//                                                if (ds.child("eventDate").getValue(String.class).equals(str) && ds.child("eventCategory").getValue(String.class).equals("Office Closed")) {
////                                                    Log.e("DATENE", "TRUE" + date);
//                                                    addToArray(date, userId, name, "OA", count);
//                                                    Log.e("ADDEDFIRST", date);
////                                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"OA"));
//
//                                                } else {
//                                                    Log.e("DATENE", "FALSE" + date);
//                                                    addToArray(date, userId, name, "NA", count);
//                                                    Log.e("ADDEDFIRST", date);
////                                                    attendanceEmployees.add(new ModelAttendanceEmployee(date,userId,name,"NA"));
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//
//                }
////                Log.e("TestAtt", "Sno\tEmpId\tEmpName\tDate\tStatus" + attendanceEmployees.size());
////                for (int i = 0; i < attendanceEmployees.size(); i++) {
////                    Log.e("TestAtt1", "" + i + 1 + "\t" + attendanceEmployees.get(i).getEmpId() + "\t" + attendanceEmployees.get(i).getEmpNmae()
////                            + "\t" + attendanceEmployees.get(i).getDate() + "\t" + attendanceEmployees.get(i).getStatus());
////
////                }
////                if(count==dateArrayList.size()-1){
////                    initializeTableView();
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void addToArray(String date, String userId, String name, String nc, int count) {
        Log.e("CHECK",date+"--"+userId+"-"+name+"--"+nc+"----"+count);
//        if(nc.equals("WO")){
//            attendanceEmployeesOff.add(new ModelAttendanceEmployee(date, userId, name, "WO"));
//        }else{
//            attendanceEmployees.add(new ModelAttendanceEmployee(date, userId, name, nc));

//        }

        attendanceEmployees.add(new ModelAttendanceEmployee(date, userId, name, nc,count));
        Log.e("ATTSIXE",""+attendanceEmployees.size()+"Date::::"+dateArrayList.size()+"Emp::::"+employeeArrayList.size());




//

        if (attendanceEmployees.size() == (dateArrayList.size()*employeeArrayList.size())) {


//            Log.e("DAJDGHF",""+attendanceEmployees.size()+"---"+date+"----"+count);
//            attendanceEmployeesSorted.clear();
//            for(int i=0;i< attendanceEmployees.size();i++){
//                ModelAttendanceEmployee modelAttendanceEmployeetemp=attendanceEmployees.get(i);
//                for(int j=i;j<attendanceEmployees.size();j++){
//
//                    if(attendanceEmployees.get(i).getCount()>attendanceEmployees.get(j).getCount()){
//                        modelAttendanceEmployeetemp=attendanceEmployees.get(j);
//                    }
//
//                }
//
//                attendanceEmployeesSorted.add(modelAttendanceEmployeetemp);
//
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                attendanceEmployees.sort((modelAttendanceEmployee, t1) -> {
//
//                    return 0;
//                });
//            }
            Collections.sort(attendanceEmployees);
            for (ModelAttendanceEmployee str : attendanceEmployees) {
                Log.e("MDOGUIYF",""+str.toString());
            }
//            Log.e("TestAtt", "Sno\tEmpId\tEmpName\tDate\tStatus" + attendanceEmployees.size()+"OFF---"+attendanceEmployeesSorted.size());
            for (int i = 0; i < attendanceEmployees.size(); i++) {
//                Log.e("TestAtt1", "" +attendanceEmployeesSorted.get(i).getCount() + "\t" + attendanceEmployeesSorted.get(i).getEmpId() + "\t" + attendanceEmployeesSorted.get(i).getEmpNmae()
//                        + "\t" + attendanceEmployeesSorted.get(i).getDate() + "\t" + attendanceEmployeesSorted.get(i).getStatus());
                Log.e("TestAtt1", "" +attendanceEmployees.get(i).getCount() + "\t" + attendanceEmployees.get(i).getEmpId() + "\t" + attendanceEmployees.get(i).getEmpNmae()
                        + "\t" + attendanceEmployees.get(i).getDate() + "\t" + attendanceEmployees.get(i).getStatus());

            }
//            initializeTableView();
        }

    }

    private void initializeTableView() {
        TableViewModelEmployee tableViewModel = new TableViewModelEmployee(employeeArrayList, attendanceEmployees, dateArrayList);
        TableViewAdapterEmployee tableViewAdapter = new TableViewAdapterEmployee(tableViewModel);
        binding.tableview.setVisibility(View.VISIBLE);
        binding.noDataToShow.setVisibility(View.GONE);

        binding.tableview.setAdapter(tableViewAdapter);
        binding.tableview.setTableViewListener(new TableViewListener(binding.tableview));

        // Create an instance of a Filter and pass the TableView.
        //mTableFilter = new Filter(mTableView);

        // Load the dummy data to the TableView
        tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
                .getRowHeaderList(), tableViewModel.getCellList());

    }

    class MonthSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return monthArrayList.size();
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
            designationText.setText(monthArrayList.get(position).getMonth());


            return row;
        }
    }

    class YearSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return yearArraylist.size();
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
            designationText.setText(yearArraylist.get(position).getMonth());


            return row;
        }
    }
}