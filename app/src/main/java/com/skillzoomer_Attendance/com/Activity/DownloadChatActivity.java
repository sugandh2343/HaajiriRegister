package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.skillzoomer_Attendance.com.Adapter.AdapterSiteSelect;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelPicActivity;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Utilities.HeaderFooterPageEvent;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.databinding.ActivityDownloadChatBinding;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFTextbox;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class DownloadChatActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityDownloadChatBinding binding;

    private ProgressDialog progressDialog;
    private final Calendar myCalendar = Calendar.getInstance();
    private String toDate = "", fromDate = "";
    String userType, siteName, userName;
    FirebaseAuth firebaseAuth;
    AdapterSiteSelect adapterSiteSelect;

    private ArrayList<ModelSite> siteAdminList;

    RecyclerView rv_site_select;
    FileOutputStream fos = null;

    private String timestamp;
    long AmountRuleExcel = 0, AmountRulePdf = 0;
    int amount = 0;
    int AmountTemp = 0;

    AlertDialog alertDialogPaymentConfirm = null;
    private ArrayList<ModelPicActivity> picActivityArrayList;

    private ArrayList<ModelDate> modelDateArrayList;
    HSSFWorkbook workbook = null;
    private ArrayList<ModelDate> shortDateList;

    String currentDate;

    private String dnl_file_type = "";
    File file = null;

    private ArrayList<ModelPicActivity> picActivitySizeCalculate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadChatBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        modelDateArrayList = new ArrayList<>();
        shortDateList = new ArrayList<>();
        picActivitySizeCalculate = new ArrayList<>();


        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType = sharedpreferences.getString("userDesignation", "");
        userName = sharedpreferences.getString("userName", "");
        MyApplication my = new MyApplication();
        my.updateLanguage(this, sharedpreferences.getString("Language", "hi"));
        siteAdminList = new ArrayList<>();
        picActivityArrayList = new ArrayList<>();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            fromDate = sdf.format(myCalendar.getTime());
            Log.e("Date", "From:" + fromDate);
            updateLabel(binding.FromdateEt);


        };
        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            toDate = sdf.format(myCalendar.getTime());
            Log.e("Date", "From:" + toDate);
            updateLabel(binding.TodateEt);

        };
        binding.FromdateEt.setOnClickListener((View v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");


            datePickerDialog.show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here
        });
        binding.TodateEt.setOnClickListener((View v) -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 86400000);
            datePickerDialog.show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here

        });

        getSiteListAdministrator();

        binding.etDownloadSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadSiteDialog();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_position"));
        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.btnToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.btnCustom.setChecked(false);
                    binding.btnToday.setChecked(true);
                    binding.llDate.setVisibility(View.GONE);
                    binding.btnDownloadReport.setVisibility(View.VISIBLE);


                }
            }
        });
        binding.btnCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.btnToday.setChecked(false);
                    binding.btnCustom.setChecked(true);
                    binding.llDate.setVisibility(View.VISIBLE);
                    binding.btnDownloadReport.setVisibility(View.VISIBLE);

                }


            }
        });
//        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (binding.btnToday.isChecked()) {
//                    getCompleteChatSize();
//                } else {
//                    if (TextUtils.isEmpty(binding.FromdateEt.getText().toString())) {
//                        Toast.makeText(DownloadChatActivity.this, "Enter From Date", Toast.LENGTH_SHORT).show();
//                    } else if (TextUtils.isEmpty(binding.TodateEt.getText().toString())) {
//                        Toast.makeText(DownloadChatActivity.this, "Enter To Date", Toast.LENGTH_SHORT).show();
//                    } else {
//                        loadChats(binding.FromdateEt.getText().toString(), binding.TodateEt.getText().toString());
//                    }
//                }
//
//
//            }
//        });
        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnToday.isChecked()) {
                    loadChats(binding.FromdateEt.getText().toString(), binding.TodateEt.getText().toString());
                } else {
                    if (TextUtils.isEmpty(binding.FromdateEt.getText().toString())) {
                        Toast.makeText(DownloadChatActivity.this, "Enter From Date", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(binding.TodateEt.getText().toString())) {
                        Toast.makeText(DownloadChatActivity.this, "Enter To Date", Toast.LENGTH_SHORT).show();
                    } else {
                        loadChats(binding.FromdateEt.getText().toString(), binding.TodateEt.getText().toString());
                    }
                }
            }
        });
    }

    private void getCompleteChatSize() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                picActivitySizeCalculate.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);

                    for (int i = 0; i < siteAdminList.size(); i++) {
                        if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
                            if (siteAdminList.get(i).getSelected()) {
                                long siteId1 = siteAdminList.get(i).getSiteId();
                                String siteName1 = siteAdminList.get(i).getSiteName();
                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("PicActivity").getChildren()) {
                                    ModelPicActivity modelPicActivity = ds1.getValue(ModelPicActivity.class);
                                    picActivitySizeCalculate.add(modelPicActivity);


                                }

                            }
                        }
                    }
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
                reference.child("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                        AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadChatActivity.this);
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
                            amount = Math.round((picActivitySizeCalculate.size() * 3));
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
                            amount = Math.round((picActivitySizeCalculate.size() * 3));
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
                            Toast.makeText(DownloadChatActivity.this, "Select file type", Toast.LENGTH_SHORT).show();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showPaymentDialog() {

        file = null;
        fos = null;
        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadChatActivity.this);
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
        if(binding.btnCustom.isChecked()){
            txt_from.setText(modelDateArrayList.get(0).getDate());
            txt_to.setText(modelDateArrayList.get(modelDateArrayList.size() - 1).getDate());
        }else{
            txt_from.setText("Complete Report");
            txt_to.setText("Complete Report");
        }


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
                    Thread createOrderId = new Thread(new createOrderIdThread(AmountTemp));
                    createOrderId.start();
                } else {
//                    DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList,siteId,siteName);
                    Thread createOrderId = new Thread(new createOrderIdThread(AmountTemp));
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
            progressDialog.dismiss();

        } catch (Exception e) {
            Log.e("Sugandh", "Error in starting Razorpay Checkout", e);
        }
    }


    private void loadChats(String s, String toString) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                picActivitySizeCalculate.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    for (int i = 0; i < siteAdminList.size(); i++) {
                        if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
                            if (siteAdminList.get(i).getSelected()) {
                                long siteId1 = siteAdminList.get(i).getSiteId();
                                String siteName1 = siteAdminList.get(i).getSiteName();
                                picActivityArrayList.clear();
                                modelDateArrayList.clear();
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
                                try {
                                    c12.setTime(sdf.parse(toDate));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                String currentDate11 = df2.format(c1);
                                try {
                                    c.setTime(sdf.parse(fromDate));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    fDate = dateFormat.parse(fromDate);
                                    tDate = dateFormat.parse(toDate);
                                    Log.e("Date1111", "Before" + tDate.toString());
                                    c12.add(Calendar.DATE, 1);


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
                                    while (temp.before(tDate)) {


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
                                    Log.e("MDAL", "" + modelDateArrayList.size());
                                    for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("PicActivity").getChildren()) {
                                        ModelPicActivity modelPicActivity = ds1.getValue(ModelPicActivity.class);
                                        for (int k = 0; k < modelDateArrayList.size(); k++) {
                                            Log.e("MDAL1234", "Check" + modelPicActivity.getDateOfUpload());
                                            Log.e("MDAL1234", "Check" + modelDateArrayList.get(i).getDate());
                                            if (modelPicActivity.getDateOfUpload().equals(modelDateArrayList.get(k).getDate())) {
                                                picActivitySizeCalculate.add(modelPicActivity);
                                            }
                                        }

                                        Log.e("MDALSize", "Pic::" + picActivityArrayList.size());


                                    }


                                }


                            }
                        }
                    }
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
                reference.child("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                        AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadChatActivity.this);
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
                            amount = Math.round((picActivitySizeCalculate.size() * 3));
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
                            amount = Math.round((picActivitySizeCalculate.size() * 3));
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
                            Toast.makeText(DownloadChatActivity.this, "Select file type", Toast.LENGTH_SHORT).show();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void DownloadExcel(long siteId1, String siteName1, ArrayList<ModelPicActivity> picActivityArrayList) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 11 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);

        createHeaderRow(sheet, modelDateArrayList, shortDateList, siteId1, siteName1);

        createAttendanceData(sheet, picActivityArrayList, modelDateArrayList);
        createFooter(sheet, picActivityArrayList, modelDateArrayList);

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String timestamp = "" + System.currentTimeMillis();
            Log.e("StrPath", str_path);
            file = new File(str_path, "HajiriRegister_" + "" + "WorkActivityRep_" + timestamp + ".xls");

//            fos = new FileOutputStream(file);
            Log.e("FilePath", file.getAbsolutePath().toString());

            HSSFPatriarch dp = sheet.createDrawingPatriarch();

            HSSFClientAnchor anchor = new HSSFClientAnchor
                    (0, 0, 650, 255, (short) 2, picActivityArrayList.size()/2, (short) 13, picActivityArrayList.size()/2+3);


            HSSFTextbox txtbox = dp.createTextbox(anchor);
            HSSFRichTextString rtxt = new HSSFRichTextString("Generated by Hajiri Register");
            HSSFFont font = workbook.createFont();
            font.setColor((short) 27);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            font.setFontHeightInPoints((short) 32);
            font.setFontName(HSSFFont.FONT_ARIAL);
            font.setColor(HSSFColor.GREY_25_PERCENT.index);
            rtxt.applyFont(font);
            txtbox.setString(rtxt);
            txtbox.setRotationDegree((short) -45);
            txtbox.setLineStyle(HSSFShape.LINESTYLE_NONE);
            txtbox.setNoFill(true);

            dnl_file_type = "xls";

            if (dnl_file_type.equals("pdf")) {
//                workbook.write(fos);

                ExcelToPdf(file, fos, workbook, siteId1, siteName1);

            } else {
//                Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//                createOrderId.start();
                str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                Log.e("StrPath", str_path);
                file = new File(str_path, "HajiriRegister_" + "" + "WorkActivityRep_" + timestamp + ".xls");

                try {
                    Log.e("TryB", "Excel");
                    fos = new FileOutputStream(file);
                    workbook.write(fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.e("Exce1", e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("Exce2", e.getMessage());
                    e.printStackTrace();
                }
                Log.e("FilePath", file.getAbsolutePath().toString());
                ExcelToPdf(file, fos, workbook, siteId1, siteName1);
            }
        } finally {

        }

//        int amount=100;
        Log.e("Amt", "Send" + amount);
    }

    private void ExcelToPdf(File file, FileOutputStream fos, HSSFWorkbook workbook, long siteId1, String siteName1) {
        FileInputStream input_document = null;
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "HajiriRegister_" + "" + "WorkActivity_Rep" + timestamp + ".pdf";
        File pdfFile = new File(str_path);
//      try {
//          input_document = new FileInputStream(file);
//      } catch (FileNotFoundException e) {
//          e.printStackTrace();
//      }
        // Read workbook into HSSFWorkbook
        HSSFWorkbook my_xls_workbook = null;
        my_xls_workbook = workbook;
        // Read worksheet into HSSFSheet
        HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
        // To iterate over the rows
        my_worksheet.getRow(6);
        Iterator<Row> rowIterator = my_worksheet.iterator();
        //We will create output PDF document objects at this point
        com.itextpdf.text.Document iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 0f);
        com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font();
        fontBold.setStyle(com.itextpdf.text.Font.BOLD | com.itextpdf.text.Font.UNDERLINE);
        fontBold.setSize(12);
        fontBold.setColor(BaseColor.BLACK);

        com.itextpdf.text.Font fontHeading = new com.itextpdf.text.Font();
        fontHeading.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeading.setSize(12);
        fontHeading.setColor(BaseColor.BLACK);
        fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;


        try {
            PdfWriter writer=PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(DownloadChatActivity.this,siteId1,siteName1,modelDateArrayList,"Chat Report");
            writer.setPageEvent(event);
        } catch (DocumentException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();
        }
        iText_xls_2_pdf.open();


        com.itextpdf.text.Font fontHeaderNormal = new com.itextpdf.text.Font();
        fontHeaderNormal.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderNormal.setSize(12);
        fontHeaderNormal.setColor(BaseColor.BLACK);
        fontHeaderNormal.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontHeaderBold = new com.itextpdf.text.Font();
        fontHeaderBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderBold.setSize(14);
        fontHeaderBold.setColor(BaseColor.BLACK);
        fontHeaderBold.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontDateHeading = new com.itextpdf.text.Font();
        fontDateHeading.setStyle(com.itextpdf.text.Font.BOLD);
        fontDateHeading.setSize(11);
        fontDateHeading.setColor(BaseColor.BLACK);
        fontDateHeading.setFamily(String.valueOf(Paint.Align.CENTER));

        PdfPTable my_table_header = new PdfPTable(18);
        my_table_header.setWidthPercentage(95);

        PdfPCell table_cell_industry_name;
        PdfPCell table_cell_company_name;
        PdfPCell table_cell_siteId;
        PdfPCell table_cell_siteName;
        PdfPCell table_cell_generated_on;
        PdfPCell table_cell_workers_advances_report;
        PdfPCell table_cell_to;
        PdfPCell table_cell_from;
        PdfPCell table_cell_total_no_of_days;
        table_cell_industry_name = new PdfPCell(new Phrase("Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""), fontHeaderBold));
        table_cell_company_name = new PdfPCell(new Phrase("Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", ""), fontHeaderBold));
        table_cell_siteId = new PdfPCell(new Phrase("Site Id: " + siteId1, fontHeaderNormal));
        table_cell_siteName = new PdfPCell(new Phrase("Site Name: " + siteName1, fontHeaderNormal));
        table_cell_generated_on = new PdfPCell(new Phrase("Generated On: " + currentDate, fontHeaderNormal));
        table_cell_workers_advances_report = new PdfPCell(new Phrase("Workers Activity Report", fontHeaderBold));
        if (binding.btnCustom.isChecked()) {
            table_cell_from = new PdfPCell(new Phrase("From:" + modelDateArrayList.get(0).getDate(), fontHeaderNormal));
            table_cell_to = new PdfPCell(new Phrase("To:" + modelDateArrayList.get(modelDateArrayList.size() - 1).getDate(), fontHeaderNormal));
            table_cell_total_no_of_days = new PdfPCell(new Phrase("Total No of days:" + modelDateArrayList.size(), fontHeaderNormal));
        } else {
            table_cell_from = new PdfPCell(new Phrase("", fontHeaderNormal));
            table_cell_to = new PdfPCell(new Phrase("", fontHeaderNormal));
            table_cell_total_no_of_days = new PdfPCell(new Phrase("", fontHeaderNormal));
        }


        table_cell_industry_name.setColspan(6);
        table_cell_industry_name.setBorder(Rectangle.NO_BORDER);
        table_cell_industry_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_generated_on.setColspan(6);
        table_cell_generated_on.setBorder(Rectangle.NO_BORDER);
        table_cell_generated_on.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_company_name.setColspan(6);
        table_cell_company_name.setBorder(Rectangle.NO_BORDER);
        table_cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_siteName.setColspan(6);
        table_cell_siteName.setBorder(Rectangle.NO_BORDER);
        table_cell_siteName.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_workers_advances_report.setColspan(6);
        table_cell_workers_advances_report.setBorder(Rectangle.NO_BORDER);
        table_cell_workers_advances_report.setHorizontalAlignment(Element.ALIGN_CENTER);

        table_cell_siteId.setColspan(6);
        table_cell_siteId.setBorder(Rectangle.NO_BORDER);
        table_cell_siteId.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_from.setColspan(3);
        table_cell_from.setBorder(Rectangle.NO_BORDER);
        table_cell_from.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell_to.setColspan(3);
        table_cell_to.setBorder(Rectangle.NO_BORDER);
        table_cell_to.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell_total_no_of_days.setColspan(12);
        table_cell_total_no_of_days.setBorder(Rectangle.NO_BORDER);
        table_cell_total_no_of_days.setHorizontalAlignment(Element.ALIGN_RIGHT);


        my_table_header.addCell(table_cell_industry_name);
        my_table_header.addCell(table_cell_generated_on);
        my_table_header.addCell(table_cell_company_name);
        my_table_header.addCell(table_cell_siteName);
        my_table_header.addCell(table_cell_workers_advances_report);
        my_table_header.addCell(table_cell_siteId);
        my_table_header.addCell(table_cell_from);
        my_table_header.addCell(table_cell_to);
        my_table_header.addCell(table_cell_total_no_of_days);
        PdfPTable my_table = new PdfPTable(18);
        my_table.setWidthPercentage(95);
        //We will use the object below to dynamically add new data to the table

        //Loop through rows.


        Row row = rowIterator.next();

        Row row2 = my_worksheet.getRow(6);
        if (row2 != null) {
            Iterator<Cell> cellIterator = row2.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); //Fetch CELL

                switch (cell.getCellType()) { //Identify CELL type
                    //you need to add more code here based on
                    //your requirement / transformations
                    case Cell.CELL_TYPE_STRING:
                        //Push the data from Excel to PDF Cell
                        if (cell.getColumnIndex() == 1) {
                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), fontHeading));
                            table_cell.setColspan(3);
                        } else if (cell.getColumnIndex() == 4) {
                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), fontHeading));
                            table_cell.setColspan(4);
                        } else if (cell.getColumnIndex() == 5) {
                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), fontHeading));
                            table_cell.setColspan(3);
                        } else if (cell.getColumnIndex() == 6) {
                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), fontHeading));
                            table_cell.setColspan(3);
                        } else {
                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), fontDateHeading));
                            table_cell.setNoWrap(false);
                        }
                        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_cell.setFixedHeight(25);
                        //feel free to move the code below to suit to your needs
                        my_table.addCell(table_cell);
                        break;
                }
                //next line
            }
        }
        for (int i = 0; i < picActivityArrayList.size(); i++) {
            Log.e("ValueI123", "" + i);
            Row row1 = my_worksheet.getRow(i + 7);


            if (row1 != null) {
                Iterator<Cell> cellIterator = row1.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next(); //Fetch CELL

                    switch (cell.getCellType()) { //Identify CELL type
                        //you need to add more code here based on
                        //your requirement / transformations

                        case Cell.CELL_TYPE_STRING:


                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                            table_cell.setFixedHeight(25);
                            if (cell.getColumnIndex() == 1) {
                                table_cell.setColspan(3);
                            } else if (cell.getColumnIndex() == 4) {
                                table_cell.setColspan(4);
                            } else if (cell.getColumnIndex() == 5) {
                                table_cell.setColspan(3);
                            } else if (cell.getColumnIndex() == 6) {
                                table_cell.setColspan(3);
                            }
                            table_cell.setFixedHeight(25);
                            table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table_cell.setVerticalAlignment(Element.ALIGN_CENTER);

                            my_table.addCell(table_cell);
                            break;
                    }
                    //next line
                }
            }


        }


        Paragraph p4 = new Paragraph("\n");
        p4.setAlignment(Paragraph.ALIGN_CENTER);
        try {
//            iText_xls_2_pdf.add(my_table_header);
            iText_xls_2_pdf.add(p4);
            iText_xls_2_pdf.add(my_table);
            iText_xls_2_pdf.add(p4);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

//          String date_val1 =  "Generated by: "+ " " +getString(R.string.app_name)+" "+ "-आपके अपने मोबाइल पर";
        try {
            Drawable d = getResources().getDrawable(R.drawable.logo23);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_CENTER);
            image.setWidthPercentage(50);
            image.setScaleToFitHeight(true);
            iText_xls_2_pdf.add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }


//          Paragraph p4 = new Paragraph(date_val1);
//          p4.setAlignment(Paragraph.ALIGN_CENTER);


        Paragraph p7 = new Paragraph("\n");
        p7.setAlignment(Paragraph.ALIGN_CENTER);


        try {

            iText_xls_2_pdf.add(p7);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        iText_xls_2_pdf.close();
        //we created our pdf file..
//          try {
//
//              input_document.close(); //close xls
//          } catch (IOException e) {
//              e.printStackTrace();
//          }
    }

    private void createFooter(Sheet sheet, ArrayList<ModelPicActivity> picActivityArrayList, ArrayList<ModelDate> modelDateArrayList) {
        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
        Font fontPresent = sheet.getWorkbook().createFont();
        fontPresent.setColor(HSSFColor.BLACK.index);
        fontPresent.setFontHeightInPoints((short) 12);
        cellStylePresent.setFont(fontPresent);
        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
        cellStylePresent.setBorderBottom((short) 1);
        cellStylePresent.setBorderTop((short) 1);
        cellStylePresent.setBorderLeft((short) 1);
        cellStylePresent.setBorderRight((short) 1);
        cellStylePresent.setFont(fontPresent);

        CellStyle cellStyle2 = sheet.getWorkbook().createCellStyle();
        Font font2 = sheet.getWorkbook().createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setBorderBottom((short) 0);
        cellStyle2.setBorderTop((short) 0);
        cellStyle2.setBorderLeft((short) 0);
        cellStyle2.setBorderRight((short) 0);
        cellStyle2.setWrapText(true);
        Date c = Calendar.getInstance().getTime();

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);

        Row row = sheet.createRow(picActivityArrayList.size() + 10);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle2);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Generated by: " + " " + "Hajiri Register";
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(picActivityArrayList.size() + 10, picActivityArrayList.size() + 12, 0, modelDateArrayList.size() + 4);
        sheet.addMergedRegion(cellMerge);

        String date_val1 = "Powered by: " + " " + "Skill Zoomers";

        Row row1 = sheet.createRow(picActivityArrayList.size() + 14);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue(date_val1);

        CellRangeAddress cellMerge1 = new CellRangeAddress(picActivityArrayList.size() + 14, picActivityArrayList.size() + 16, 0, modelDateArrayList.size() + 4);
        sheet.addMergedRegion(cellMerge1);

        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ " + " " + "Skill Zoomers";

        Row row2 = sheet.createRow(picActivityArrayList.size() + 18);
        Cell cellHeading1 = row2.createCell(0);

        cellHeading1.setCellStyle(cellStyle2);
        cellHeading1.setCellValue(date_val2);

        CellRangeAddress cellMerge3 = new CellRangeAddress(picActivityArrayList.size() + 18, picActivityArrayList.size() + 20, 0, modelDateArrayList.size() + 4);
        sheet.addMergedRegion(cellMerge3);

    }

    private void createAttendanceData(Sheet sheet, ArrayList<ModelPicActivity> picActivityArrayList, ArrayList<ModelDate> modelDateArrayList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);


        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        Font font1 = sheet.getWorkbook().createFont();
        font1.setColor(HSSFColor.RED.index);
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font1.setFontHeightInPoints((short) 12);
        font1.setUnderline(Font.U_SINGLE);
        cellStyle1.setBorderBottom((short) 1);
        cellStyle1.setBorderTop((short) 1);
        cellStyle1.setBorderLeft((short) 1);
        cellStyle1.setBorderRight((short) 1);
        cellStyle1.setWrapText(true);
        cellStyle1.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle1.setFont(font1);

        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
        Font fontPresent = sheet.getWorkbook().createFont();
        fontPresent.setColor(HSSFColor.BLUE.index);
        fontPresent.setFontHeightInPoints((short) 12);
        cellStylePresent.setFont(fontPresent);
        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
        cellStylePresent.setBorderBottom((short) 1);
        cellStylePresent.setBorderTop((short) 1);
        cellStylePresent.setBorderLeft((short) 1);
        cellStylePresent.setBorderRight((short) 1);
        cellStylePresent.setFont(fontPresent);

        for (int i = 0; i < picActivityArrayList.size(); i++) {
            Row row = sheet.createRow(i + 7);
            row.setHeightInPoints(35);

            Cell cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(String.valueOf(i + 1));

            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(cellStyle);
            cell1.setCellValue(picActivityArrayList.get(i).getDateOfUpload());

            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(cellStyle);
            cell2.setCellValue(picActivityArrayList.get(i).getTimeofUpload());

            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(cellStyle);
            cell3.setCellValue(picActivityArrayList.get(i).getPicType());

            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(cellStyle);
            if (picActivityArrayList.get(i).getPicType().equals("Text")) {
                cell4.setCellValue(picActivityArrayList.get(i).getPicMsg());
            } else if (picActivityArrayList.get(i).getPicRemark() != null) {
                cell4.setCellValue(picActivityArrayList.get(i).getPicRemark());
            }

            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(cellStyle);
            cell5.setCellValue(picActivityArrayList.get(i).getPicId());

            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(cellStyle);

            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(cellStyle);

            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(cellStyle);


            if (picActivityArrayList.get(i).getReply()) {
                cell6.setCellValue(picActivityArrayList.get(i).getReplyDate());
                cell7.setCellValue(picActivityArrayList.get(i).getReplyTime());
                if (picActivityArrayList.get(i).getReplyType().equals("Text")) {
                    cell8.setCellValue(picActivityArrayList.get(i).getReplyMsg());
                } else {
                    cell8.setCellValue("Audio");
                }

            } else {
                cell6.setCellValue("-");
                cell7.setCellValue("-");
                cell8.setCellValue("-");

            }


        }
    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelDate> dateModelArrayList, ArrayList<ModelDate> shortDateList, long siteId1, String siteName1) {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);

        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        Font font1 = sheet.getWorkbook().createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font1.setFontHeightInPoints((short) 12);
        cellStyle1.setFont(font);
        cellStyle1.setAlignment(CellStyle.ALIGN_CENTER);

        cellStyle1.setBorderBottom((short) 0);
        cellStyle1.setBorderTop((short) 0);
        cellStyle1.setBorderLeft((short) 0);
        cellStyle1.setBorderRight((short) 0);
        cellStyle1.setWrapText(true);

        CellStyle cellStyle2 = sheet.getWorkbook().createCellStyle();
        Font font2 = sheet.getWorkbook().createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle2.setBorderBottom((short) 0);
        cellStyle2.setBorderTop((short) 0);
        cellStyle2.setBorderLeft((short) 0);
        cellStyle2.setBorderRight((short) 0);
        cellStyle2.setWrapText(true);

        Row row = sheet.createRow(0);
        Cell cellFullName = row.createCell(0);

        cellFullName.setCellStyle(cellStyle1);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") +
                "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "");

        cellFullName.setCellValue(date_val);
        int size = 0;
        if (dateModelArrayList.size() + 5 < 8) {
            size = 8;
        } else {
            size = dateModelArrayList.size() + 5;
        }

        CellRangeAddress cellMerge = new CellRangeAddress(0, 3, 0, 6);
        sheet.addMergedRegion(cellMerge);

        Cell cellFullName1 = row.createCell(7);
        cellFullName1.setCellStyle(cellStyle1);
        String date_val1 = "Generated On: " + currentDate + "\n" + "Site Id: " + siteId1 +
                "\t\t Site Name: " + siteName1;
        cellFullName1.setCellValue(date_val1);

        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 3, 7, 10);
        sheet.addMergedRegion(cellMerge1);


        Row row1 = sheet.createRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        if (binding.btnCustom.isChecked()) {
            cellHeading.setCellValue("Work Activity Report" + "\t\t\t" + "From: " +
                    " " + dateModelArrayList.get(0).getDate() + "\t\t" + "To: " + dateModelArrayList.get(dateModelArrayList.size() - 1).getDate() + "\t\t\t" + "Total No of Days:" + dateModelArrayList.size());
        } else {
            cellHeading.setCellValue("Work Activity Report");
        }


        CellRangeAddress cellMerge2 = new CellRangeAddress(4, 5, 0, 10);
        sheet.addMergedRegion(cellMerge2);

        Row rowValues = sheet.createRow(6);
        rowValues.setHeightInPoints(25);
        rowValues.setHeightInPoints(30);
        Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        Cell cellSrNo1 = rowValues.createCell(1);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Date");
        Cell cellSrNo2 = rowValues.createCell(2);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Time");
        Cell cellSrNo3 = rowValues.createCell(3);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Type");
        Cell cellSrNo4 = rowValues.createCell(4);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Message");
        Cell cellSrNo5 = rowValues.createCell(5);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("Work Id");
        Cell cellSrNo6 = rowValues.createCell(6);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Reply Date");
        Cell cellSrNo7 = rowValues.createCell(7);
        cellSrNo7.setCellStyle(cellStyle);
        cellSrNo7.setCellValue("Reply Time");
        Cell cellSrNo8 = rowValues.createCell(8);
        cellSrNo8.setCellStyle(cellStyle);
        cellSrNo8.setCellValue("Reply Type");


    }

    private void updateLabel(EditText fromdateEt) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fromdateEt.setText(sdf.format(myCalendar.getTime()));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            ArrayList<ModelSite> siteArrayList1=new ArrayList<>();
//            siteAdminList=intent.getParcelableArrayListExtra("array");
            int position = intent.getIntExtra("position", 0);
            Boolean value = intent.getBooleanExtra("boolean", true);

            if (value) {
                if (position < 1) {
                    for (int i = 0; i < siteAdminList.size(); i++) {
                        siteAdminList.get(i).setSelected(true);
                    }
                } else {
                    siteAdminList.get(0).setSelected(false);
                    siteAdminList.get(position).setSelected(true);
                }
            } else {
                if (position < 1) {
                    for (int i = 0; i < siteAdminList.size(); i++) {
                        siteAdminList.get(i).setSelected(false);
                    }
                } else {
                    siteAdminList.get(0).setSelected(false);
                    siteAdminList.get(position).setSelected(false);
                }
            }

            adapterSiteSelect.notifyDataSetChanged();
//            if (position == 0 && value) {
//                for (int i = 0; i < siteAdminList.size(); i++) {
//                    siteAdminList.get(i).setSelected(true);
//                }
//                Log.e("siteAL", "" + siteAdminList.size());
//
//
//            }else if(position ==0 && !value){
//                for(int i=0;i<siteAdminList.size();i++){
//                    siteAdminList.get(i).setSelected(false);
//                }
//            }else if(position>0 && value){
//               Log.e("PositionSite",""+position);
//               Log.e("PositionSite",""+value);
//                for(int i=0;i<siteAdminList.size();i++){
//                    if(i==position){
//                        siteAdminList.get(position).setSelected(true);
//                    }else if(!siteAdminList.get(i).getSelected()){
//                        siteAdminList.get(i).setSelected(false);
//                    }
//
//                }
//            }else if(position>0 && !value){
//
//                siteAdminList.get(1).setSelected(false);
//                siteAdminList.get(position).setSelected(false);
////
//            }


        }


    };


    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e("Snap", snapshot.getKey());

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);

                    modelSite.setSelected(false);
                    if (snapshot.child(String.valueOf(modelSite.getSiteId())).hasChild("PicActivity")) {
                        siteAdminList.add(modelSite);
                    }


                }

                if (siteAdminList.size() == 0) {

                    binding.llSelectPeriod.setVisibility(View.GONE);
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    binding.llDate.setVisibility(View.GONE);

                    binding.btnDownloadReport.setVisibility(View.GONE);

                } else {
//                    siteAdminList.add(0,new ModelSite(getString(R.string.select_site),0,false));
                    siteAdminList.add(0, new ModelSite("All Sites", 0, false));


                    binding.llSelectPeriod.setVisibility(View.VISIBLE);
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    binding.llDate.setVisibility(View.GONE);

                    binding.btnDownloadReport.setVisibility(View.VISIBLE);

                    adapterSiteSelect = new AdapterSiteSelect(DownloadChatActivity.this, siteAdminList);
//                    progressDialog.dismiss();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDownloadSiteDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadChatActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_download_spinner_dialog, null);
        rv_site_select = (RecyclerView) mView.findViewById(R.id.rv_site_select);
        rv_site_select.setAdapter(adapterSiteSelect);
        Button btn_ok = (Button) mView.findViewById(R.id.btn_ok);
        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
                for (int i = 1; i < siteAdminList.size(); i++) {
                    if (siteAdminList.get(i).getSelected()) {
                        count++;
                    }
                }
                if (count > 0) {
                    alertDialog.dismiss();
                    binding.etDownloadSpinner.setText("" + count + " " + "Sites Selected");
                    binding.etDownloadSpinner.setTextColor(getResources().getColor(R.color.darkGreen));

                } else {
                    Toast.makeText(DownloadChatActivity.this, "Select Atleast one site to download report", Toast.LENGTH_SHORT).show();
                }


            }
        });


        alertDialog.show();

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    for (int i = 0; i < siteAdminList.size(); i++) {
                        if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
                            if (siteAdminList.get(i).getSelected()) {
                                long siteId1 = siteAdminList.get(i).getSiteId();
                                String siteName1 = siteAdminList.get(i).getSiteName();
                                picActivityArrayList.clear();
                                modelDateArrayList.clear();
                                if (binding.btnToday.isChecked()) {
                                    for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("PicActivity").getChildren()) {
                                        ModelPicActivity modelPicActivity = ds1.getValue(ModelPicActivity.class);
                                        picActivityArrayList.add(modelPicActivity);


                                    }
                                    DownloadExcel(siteId1, siteName1, picActivityArrayList);
                                } else {
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
                                    try {
                                        c12.setTime(sdf.parse(toDate));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                    String currentDate11 = df2.format(c1);
                                    try {
                                        c.setTime(sdf.parse(fromDate));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        fDate = dateFormat.parse(fromDate);
                                        tDate = dateFormat.parse(toDate);
                                        Log.e("Date1111", "Before" + tDate.toString());
                                        c12.add(Calendar.DATE, 1);


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
                                        while (temp.before(tDate)) {


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
                                        Log.e("MDAL", "" + modelDateArrayList.size());
                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("PicActivity").getChildren()) {
                                            ModelPicActivity modelPicActivity = ds1.getValue(ModelPicActivity.class);
                                            for (int k = 0; k < modelDateArrayList.size(); k++) {
                                                Log.e("MDAL1234", "Check" + modelPicActivity.getDateOfUpload());
                                                Log.e("MDAL1234", "Check" + modelDateArrayList.get(i).getDate());
                                                if (modelPicActivity.getDateOfUpload().equals(modelDateArrayList.get(k).getDate())) {
                                                    picActivityArrayList.add(modelPicActivity);
                                                }
                                            }

                                            Log.e("MDALSize", "Pic::" + picActivityArrayList.size());


                                        }

                                        DownloadExcel(siteId1, siteName1, picActivityArrayList);
                                    }


                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.e("Success", s);
        alertDialogPaymentConfirm.dismiss();
        Log.e("from", "" + fromDate);
        Log.e("from", "to::::" + toDate);

        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadChatActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_sucess, null);
        TextView txt_payment_id, txt_payment_amt, txt_message;
        Button btn_ok;
        txt_payment_id = mView.findViewById(R.id.txt_payment_id);
        txt_payment_amt = mView.findViewById(R.id.txt_payment_amt);
        txt_message = mView.findViewById(R.id.txt_message);
        btn_ok = mView.findViewById(R.id.btn_ok);
        txt_payment_id.setText(paymentData.getPaymentId());
        txt_payment_amt.setText(String.valueOf(amount));
        if (dnl_file_type.equals("pdf")) {
            txt_message.setText("Your Work Activity Report PDF file is generated and stored in DOWNLOADS folder. ");
        } else {
            txt_message.setText("Your Work Activity Report EXCEL+PDF file is generated and stored in DOWNLOADS folder. ");
        }
        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                currentDate = df.format(c);
                String currentTime = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                }
                String timestamp = "" + System.currentTimeMillis();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("dateOfPayment", currentDate);
                hashMap.put("timeOfPayment", currentTime);
                hashMap.put("timestamp", timestamp);
                hashMap.put("paymentId", paymentData.getPaymentId());
                hashMap.put("orderId", paymentData.getOrderId());
                hashMap.put("signature", paymentData.getSignature());
                hashMap.put("paidAmount", String.valueOf(AmountTemp));
                hashMap.put("status", "Success");
                hashMap.put("downloadFile", "Attendance Report");
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", dnl_file_type);
                hashMap.put("totalAmount", String.valueOf(amount));

                hashMap.put("fromDate", fromDate);
                hashMap.put("toDate", toDate);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData", paymentData.getData().toString());
                                Log.e("paymentData", paymentData.getPaymentId().toString());
//

                                Log.e("fos", "flush");
                                Toast.makeText(DownloadChatActivity.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                Uri dirUri = FileProvider.getUriForFile(DownloadChatActivity.this,getApplicationContext().getPackageName() + ".com.skillzoomer_Attendance.com",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
//                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                alertDialog.dismiss();
                            }


//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
//                        startActivity(intent);


                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
        alertDialog.show();


    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment Failse due to " + s, Toast.LENGTH_LONG);
        Log.e("Success", s);
        alertDialogPaymentConfirm.dismiss();
        Log.e("from", "" + fromDate);
        Log.e("from", "to::::" + toDate);

        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadChatActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_failure, null);
        TextView txt_failed_reason;
        Button btn_ok;
        txt_failed_reason = mView.findViewById(R.id.txt_failed_reason);
        btn_ok = mView.findViewById(R.id.btn_ok);
        txt_failed_reason.setText(s);
        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                currentDate = df.format(c);
                String currentTime = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                }
                String timestamp = "" + System.currentTimeMillis();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("dateOfPayment", currentDate);
                hashMap.put("timeOfPayment", currentTime);
                hashMap.put("timestamp", timestamp);
                hashMap.put("paymentId", "");
                hashMap.put("orderId", paymentData.getOrderId());
                hashMap.put("signature", "");
                hashMap.put("paidAmount", String.valueOf(amount));
                hashMap.put("status", "Failed");
                hashMap.put("downloadFile", "Work Activity Report");
                hashMap.put("fromDate", fromDate);
                hashMap.put("toDate", toDate);
                hashMap.put("siteId", "");
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", dnl_file_type);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                Log.e("paymentData",paymentData.getData().toString());
//                                Log.e("paymentData",paymentData.getPaymentId().toString());
                                Toast.makeText(DownloadChatActivity.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();

                                alertDialog.dismiss();


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
        alertDialog.show();

    }
}