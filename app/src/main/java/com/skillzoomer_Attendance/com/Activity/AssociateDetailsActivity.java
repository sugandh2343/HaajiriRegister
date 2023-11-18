package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import com.itextpdf.text.BadElementException;
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
import com.skillzoomer_Attendance.com.Adapter.AdapterAssociateDetails;
import com.skillzoomer_Attendance.com.Adapter.AdapterDayBook;
import com.skillzoomer_Attendance.com.Adapter.AdapterSiteSelect;
import com.skillzoomer_Attendance.com.Adapter.AdapterTeamMemberCard;
import com.skillzoomer_Attendance.com.Model.DateModel;
import com.skillzoomer_Attendance.com.Model.ModelAssociateDetails;
import com.skillzoomer_Attendance.com.Model.ModelAttendance;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelDayBookClass;
import com.skillzoomer_Attendance.com.Model.ModelExpense;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.Model.ModelPayment;
import com.skillzoomer_Attendance.com.Model.ModelReceiveCash;
import com.skillzoomer_Attendance.com.Model.ModelShowAttendance;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelSiteSpinner;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Utilities.HeaderFooterPageEvent;
import com.skillzoomer_Attendance.com.databinding.ActivityAssociateDetailsBinding;

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
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class AssociateDetailsActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    private final Calendar myCalendar = Calendar.getInstance();
    ActivityAssociateDetailsBinding binding;
    private String[] search = {"Select Search Criteria", "Search by Labour", "Search by Date"};
    private String[] workerType = {"Select Type", "Skilled", "Unskilled"};
    private String toDate = "", fromDate = "";
    String userType, siteName, userName;
    long siteId;
    private Sheet sheet;
    private ArrayList<ModelShowAttendance> showAttendanceArrayList;
    private ArrayList<ModelDate> modelDateArrayList;
    private ArrayList<ModelSite> siteArrayList;
    private ArrayList<DateModel> dateModelArrayList;
    private String siteCreatedDate;
    private Double siteLatitude = 0.0, siteLongitude = 0.0;
    FirebaseAuth firebaseAuth;
    String searchSelected, workerTypeSelected;
    private ArrayList<ModelLabour> labourList;
    private ArrayList<ModelAttendance> modelAttendances;
    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
    int countLoop = 0;
    private ProgressDialog progressDialog;
    private String status = "";
    String currentDate;
    private Cell cell = null;
    ArrayList<ModelLabour> labourByType;
    ArrayList<ModelDate> shortDateList;
    ArrayAdapter siteAdapter;
    private ArrayList<ModelSiteSpinner> siteNameArrayList;
    private long selectedSiteId;
    private String selectedSiteName;
    int viewIsScrolling = 1;
    boolean firstIsTouched = false;
    boolean secondIsTouched = false;
    private ArrayList<ModelSite> siteAdminList;
    int selected_option = 0;
    int selected_option_radio = 0;
    private ArrayList<ModelReceiveCash> receiveCashArrayList;
    private ArrayList<ModelExpense> expenseArrayList;
    private int spinnerPosition;
    private int receiving_amt = 0, expense_amt = 0;
    Date c;
    private String receiving_by = "";
    private ArrayList<ModelDayBookClass> modelDayBookClassArrayList;
    private ArrayList<ModelPayment> paymentArrayList;
    RecyclerView rv_site_select;
    AdapterSiteSelect adapterSiteSelect;
    FileOutputStream fos = null;
    File file = null;
    HSSFWorkbook workbook = null;
    private ArrayList<ModelAssociateDetails> associateDetailsArrayList;
    private ArrayList<ModelAssociateDetails> associateLoginDetails;
    private ArrayList<ModelAssociateDetails> associateLogOutdetails;
    private String timestamp;
    ProgressDialog pd;

    int amount = 0;
    int AmountTemp = 0;
    AlertDialog alertDialogPaymentConfirm = null;
    long AmountRuleExcel=0,AmountRulePdf=0;
    private String dnl_file_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssociateDetailsBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        status = intent.getStringExtra("Activity");
        siteAdminList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        modelDateArrayList = new ArrayList<>();
        receiveCashArrayList = new ArrayList<>();
        expenseArrayList = new ArrayList<>();
        associateDetailsArrayList = new ArrayList<>();
        associateLoginDetails = new ArrayList<>();
        associateLogOutdetails = new ArrayList<>();
        timestamp = "" + System.currentTimeMillis();
//        modelDayBookClassArrayList = new ArrayList<>();
        binding.llDayBookHeading.setVisibility(View.GONE);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType = sharedpreferences.getString("userDesignation", "");
        userName = sharedpreferences.getString("userName", "");
        selected_option = sharedpreferences.getInt("workOption", 0);


        if (!status.equals("")) {
            if (status.equals("ShowAttendance")) {
                binding.btnDownload.setVisibility(View.GONE);
                binding.btnShow.setVisibility(View.VISIBLE);
                binding.spinnerSelectSite.setVisibility(View.VISIBLE);
                binding.etDownloadSpinner.setVisibility(View.GONE);
                binding.rvDayBook.setVisibility(View.VISIBLE);


            } else {
                binding.btnDownload.setVisibility(View.VISIBLE);
                binding.btnShow.setVisibility(View.GONE);
                binding.spinnerSelectSite.setVisibility(View.GONE);
                binding.etDownloadSpinner.setVisibility(View.VISIBLE);
                binding.rvDayBook.setVisibility(View.GONE);
                binding.llDayBookHeading.setVisibility(View.GONE);
                if (!userType.equals("Supervisor")) {
                    progressDialog.show();
                    getSiteListAdministrator();
                }


            }
        }
        binding.etDownloadSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadSiteDialog();
            }
        });
        paymentArrayList = new ArrayList<>();
        if (userType.equals("Supervisor")) {
            siteName = sharedpreferences.getString("siteName", "");
            siteId = sharedpreferences.getLong("siteId", 0);
            binding.llSelectSite.setVisibility(View.GONE);
        } else {
            getSiteListAdministrator();


        }

        if (selected_option == 1) {
//            getReceivingData(modelDateArrayList);
        } else if (selected_option == 3) {
            if (siteId > 0) {
                getSiteMemberStatus(siteId);
            }
        } else if (selected_option == 2) {
//            getExpenseData(siteId);

        }
        binding.llSelectPeriod.setVisibility(View.GONE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_position"));

        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i>0){
                    binding.llSelectPeriod.setVisibility(View.VISIBLE);
                    siteId = siteAdminList.get(i).getSiteId();
                    siteName = siteAdminList.get(i).getSiteName();
                    if (i > 0) {
                        getSiteData(siteId);
                    }
                    Log.e("SiteId", "Spinner" + siteId);
                    if (binding.rbComplete.isChecked()) {
                        getSiteCreatedDate("Complete");
                    } else if (binding.rbToday.isChecked()) {
                        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        String currentDate11 = df1.format(c);
                        try {
                            getDateRange(currentDate11, currentDate11);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (binding.rbCustom.isChecked()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Boolean f = true;
                        if (status.equals("ShowAttendance")) {
                            binding.llDayBookHeading.setVisibility(View.VISIBLE);
                        }

                        try {
                            Date fDate = dateFormat.parse(fromDate);
                            Date tDate = dateFormat.parse(toDate);
                            if (tDate.before(fDate)) {
                                Toast.makeText(AssociateDetailsActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                                f = false;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (fromDate.equals("")) {
                            Toast.makeText(AssociateDetailsActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                        } else if (toDate.equals("")) {
                            Toast.makeText(AssociateDetailsActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                        } else if (f) {
                            try {
                                getDateRange(fromDate, toDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
//                    getLabourList(fromDate, toDate,"Custom");
                        }
                    }
                }else{
                    binding.llSelectPeriod.setVisibility(View.GONE);
                    binding.llDate.setVisibility(View.GONE);
                    binding.btnShow.setVisibility(View.GONE);
                    binding.llDayBookHeading.setVisibility(View.GONE);
                    binding.rvDayBook.setVisibility(View.GONE);
                }




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);
        binding.llDate.setVisibility(View.GONE);
        binding.btnShow.setVisibility(View.GONE);
//        binding.btnDownload.setVisibility(View.GONE);
        binding.rbComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (binding.rbComplete.isChecked()) {
                    if (status.equals("ShowAttendance")) {
                        binding.llDayBookHeading.setVisibility(View.VISIBLE);
                    }
                    binding.rbComplete.setChecked(true);
                    binding.rbCustom.setChecked(false);
                    binding.rbToday.setChecked(false);
                    selected_option_radio = 1;
                    binding.llDate.setVisibility(View.GONE);
                    binding.btnShow.setVisibility(View.GONE);
                    getSiteCreatedDate("Complete");


                }
            }
        });

        binding.rbToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("Statusjhgu",status);
                if(status.equals("ShowAttendance")){
                    if(binding.spinnerSelectSite.getSelectedItemPosition()>0){
                        if (b) {
                            if (status.equals("ShowAttendance")) {
                                binding.llDayBookHeading.setVisibility(View.VISIBLE);
                            }
                            binding.rbComplete.setChecked(false);
                            binding.rbCustom.setChecked(false);
                            binding.rbToday.setChecked(true);
                            selected_option_radio = 2;
                            binding.llDate.setVisibility(View.GONE);
                            binding.btnShow.setVisibility(View.GONE);
                            Calendar c12 = Calendar.getInstance();

                            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                            String currentDate11 = df1.format(c);
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                            Date temp = c12.getTime();
                            String currentDate1 = sdf1.format(temp);
                            associateDetailsArrayList.clear();
                            getReceivingDataIndividual(currentDate);


                        }
                    }
                }else{
                    if (b) {
                        if (status.equals("ShowAttendance")) {
                            binding.llDayBookHeading.setVisibility(View.VISIBLE);
                            binding.rvDayBook.setVisibility(View.VISIBLE);
                            Calendar c12 = Calendar.getInstance();
                            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                            String currentDate11 = df1.format(c);
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                            Date temp = c12.getTime();
                            String currentDate1 = sdf1.format(temp);
                            associateDetailsArrayList.clear();
                            getReceivingDataIndividual(currentDate);
                        }else{
                            binding.llDayBookHeading.setVisibility(View.GONE);
                            binding.rvDayBook.setVisibility(View.GONE);
                        }
                        binding.rbComplete.setChecked(false);
                        binding.rbCustom.setChecked(false);
                        binding.rbToday.setChecked(true);
                        selected_option_radio = 2;
                        binding.llDate.setVisibility(View.GONE);
                        binding.btnShow.setVisibility(View.GONE);



                    }
                }


            }
        });
        binding.rbCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(status.equals("ShowAttendance")){
                    if (binding.spinnerSelectSite.getSelectedItemPosition() > 0) {
                        if (binding.rbCustom.isChecked()) {

                            binding.rbComplete.setChecked(false);
                            binding.rbCustom.setChecked(true);
                            binding.rbToday.setChecked(false);
                            selected_option_radio = 3;
                            binding.llDate.setVisibility(View.VISIBLE);
                            if (status.equals("ShowAttendance")) {
                                binding.btnShow.setVisibility(View.VISIBLE);
                            } else {

                                binding.btnShow.setVisibility(View.GONE);

                            }

                        }
                    }
                }else{
                    if (binding.rbCustom.isChecked()) {

                        binding.rbComplete.setChecked(false);
                        binding.rbCustom.setChecked(true);
                        binding.rbToday.setChecked(false);
                        selected_option_radio = 3;
                        binding.llDate.setVisibility(View.VISIBLE);
                        if (status.equals("ShowAttendance")) {
                            binding.btnShow.setVisibility(View.VISIBLE);
                        } else {

                            binding.btnShow.setVisibility(View.GONE);

                        }

                    }
                }

            }

        });
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

//            try {
//                Date fDate = dateFormat.parse(siteCreatedDate);
//                Log.e("Parse Success","Success");
//                datePickerDialog.getDatePicker().setMinDate(fDate.getTime());
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
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 86400000);
            datePickerDialog.show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here

        });
        binding.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Boolean f = true;
                if (status.equals("ShowAttendance")) {
                    binding.llDayBookHeading.setVisibility(View.VISIBLE);
                }
                try {
                    Date fDate = dateFormat.parse(fromDate);
                    Date tDate = dateFormat.parse(toDate);
                    if (tDate.before(fDate)) {
                        Toast.makeText(AssociateDetailsActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(AssociateDetailsActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(AssociateDetailsActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                } else if (f) {
                    try {
                        getDateRange(fromDate, toDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    getLabourList(fromDate, toDate,"Custom");
                }
            }
        });
        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbToday.isChecked()) {
                    Date c = Calendar.getInstance().getTime();


                    Calendar c12 = Calendar.getInstance();
                    c12.add(Calendar.DATE, 1);
                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    fromDate = df1.format(c);

                    // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date temp = c12.getTime();
                    toDate = sdf1.format(temp);

                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Boolean f = true;
                try {
                    Date fDate = dateFormat.parse(fromDate);
                    Date tDate = dateFormat.parse(toDate);
                    if (tDate.before(fDate)) {
                        Toast.makeText(AssociateDetailsActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(AssociateDetailsActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(AssociateDetailsActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                } else if (f) {
//                    progressDialog.setMessage("Downloading Your Reports");
//                    progressDialog.show();
                    String type = "Custom";
//                    modelLabourStatusArrayList.clear();
                    modelDateArrayList = new ArrayList<>();
                    shortDateList = new ArrayList<>();
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
                        if (type.equals("Custom")) {
                            c12.add(Calendar.DATE, 1);
                        }

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
                    }
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
                    reference.child("AssociateReport").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                            AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    final AlertDialog.Builder alert = new AlertDialog.Builder(AssociateDetailsActivity.this);
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
                                amount = Math.round((modelDateArrayList.size() * 5));
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
                                amount = Math.round((modelDateArrayList.size() * 3));
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
                                Toast.makeText(AssociateDetailsActivity.this, "Select file type", Toast.LENGTH_SHORT).show();
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
            }
        });
//        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (binding.rbToday.isChecked()) {
//                    Date c = Calendar.getInstance().getTime();
//
//
//                    Calendar c12 = Calendar.getInstance();
//                    c12.add(Calendar.DATE, 1);
//                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                    fromDate = df1.format(c);
//
//                    // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//                    Date temp = c12.getTime();
//                    toDate = sdf1.format(temp);
//
//                }
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                Boolean f = true;
//                try {
//                    Date fDate = dateFormat.parse(fromDate);
//                    Date tDate = dateFormat.parse(toDate);
//                    if (tDate.before(fDate)) {
//                        Toast.makeText(AssociateDetailsActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
//                        f = false;
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                if (fromDate.equals("")) {
//                    Toast.makeText(AssociateDetailsActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
//                } else if (toDate.equals("")) {
//                    Toast.makeText(AssociateDetailsActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
//                } else if (f) {
////                    progressDialog.setMessage("Downloading Your Reports");
////                    progressDialog.show();
//                    String type = "Custom";
////                    modelLabourStatusArrayList.clear();
//                    modelDateArrayList = new ArrayList<>();
//                    shortDateList = new ArrayList<>();
//                    dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//                    Date fDate = null, tDate = null;
//                    Log.e("callFrom", fromDate);
//                    Log.e("callFrom", toDate);
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    Date c1 = Calendar.getInstance().getTime();
//
//                    Calendar c = Calendar.getInstance();
//                    Calendar c12 = Calendar.getInstance();
//                    try {
//                        c12.setTime(sdf.parse(toDate));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                    String currentDate11 = df2.format(c1);
//                    try {
//                        c.setTime(sdf.parse(fromDate));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        fDate = dateFormat.parse(fromDate);
//                        tDate = dateFormat.parse(toDate);
//                        Log.e("Date1111", "Before" + tDate.toString());
//                        if (type.equals("Custom")) {
//                            c12.add(Calendar.DATE, 1);
//                        }
//
//                        tDate = c12.getTime();
//                        Log.e("Date1111", "" + tDate.toString());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    if (fDate == null || tDate == null) {
//                        Log.e("Exception", "Error");
//                    } else {
//                        Date temp = fDate;
//                        Log.e("callTemp", "" + temp);
//
//                        int count = 0;
//                        while (temp.before(tDate)) {
//
//
//                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//                            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM", Locale.US);
//                            String date = df.format(temp);
//                            String date1 = df1.format(temp);
//                            Log.e("ShreyaMamKaDate", date1);
//                            modelDateArrayList.add(new ModelDate(date));
//                            shortDateList.add(new ModelDate(date1));
//                            Log.e("dateeeee1", modelDateArrayList.get(count).getDate());
//                            count++;
//                            Log.e("dateeee", date);
//                            c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy");
//                            temp = c.getTime();
//                            Log.e("Temp", "" + count + ":" + temp);
//
//
//                        }
//                    }
//
//                }
//
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//                reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        binding.txtNoData.setVisibility(View.GONE);
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            ModelSite modelSite = ds.getValue(ModelSite.class);
//                            Log.e("siteAdminList", "Size" + siteAdminList.size());
//                            modelDateArrayList.clear();
////                            saxjb
//                            for (int i = 0; i < siteAdminList.size(); i++) {
//                                if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
//                                    Log.e("SiteFound", "" + siteAdminList.get(i).getSiteId());
//                                    if (siteAdminList.get(i).getSelected()) {
//                                        Log.e("SiteFound", "2::::" + siteAdminList.get(i).getSiteId());
//                                        long siteId1 = siteAdminList.get(i).getSiteId();
//                                        siteLatitude = snapshot.child(String.valueOf(siteId1)).child("siteLatitude").getValue(double.class);
//
//                                        siteLongitude = snapshot.child(String.valueOf(siteId1)).child("siteLongitude").getValue(double.class);
//                                        String siteName1 = siteAdminList.get(i).getSiteName();
//                                        modelDateArrayList.clear();
//                                        associateLoginDetails.clear();
//                                        associateLogOutdetails.clear();
////                                        associateDetailsArrayList.clear();
//                                        if (binding.rbToday.isChecked()) {
//
//                                            if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child(currentDate).getChildrenCount() > 0) {
//                                                Log.e("ModelAssociate123", "SnapshotExist");
//                                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Attendance").child(currentDate).getChildren()) {
//                                                    ModelAssociateDetails modelAssociateDetails = ds1.getValue(ModelAssociateDetails.class);
//                                                    Log.e("Model123", "" + (modelAssociateDetails.getStatus() == null));
//                                                    Log.e("Model123", "" + (modelAssociateDetails.getStatus()));
//                                                    modelAssociateDetails.setDate(currentDate);
//
//                                                    if (siteLatitude > 0 && siteLongitude > 0) {
//                                                        modelAssociateDetails.setSiteLatitude(siteLatitude);
//                                                        modelAssociateDetails.setSiteLongitude(siteLongitude);
//                                                    }
//                                                    if (modelAssociateDetails.getStatus().equals("NormalLogin")) {
//                                                        associateLoginDetails.add(modelAssociateDetails);
//                                                    } else {
//                                                        associateLogOutdetails.add(modelAssociateDetails);
//                                                    }
////                                                    associateDetailsArrayList.add(modelAssociateDetails);
//
//
//                                                }
//                                                DownloadExcel(associateLoginDetails, associateLogOutdetails, siteId1, siteName1);
//
//
//                                            }
//
//
//                                        } else if (binding.rbCustom.isChecked()) {
//                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                                            Boolean f = true;
//                                            try {
//
//                                                Date fDate = dateFormat.parse(fromDate);
//                                                Date tDate = dateFormat.parse(toDate);
//                                                if (tDate.before(fDate)) {
//                                                    Toast.makeText(AssociateDetailsActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
//                                                    f = false;
//                                                }
//
//                                            } catch (ParseException e) {
//                                                e.printStackTrace();
//                                            }
//                                            if (fromDate.equals("")) {
//                                                Toast.makeText(AssociateDetailsActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
//                                            } else if (toDate.equals("")) {
//                                                Toast.makeText(AssociateDetailsActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
//                                            } else if (f) {
//                                                try {
//                                                    modelDateArrayList = new ArrayList<>();
//                                                    shortDateList = new ArrayList<>();
//                                                    Date fDate = null, tDate = null;
//                                                    Log.e("callFrom", fromDate);
//                                                    Log.e("callFrom", toDate);
//                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                                                    Date c1 = Calendar.getInstance().getTime();
//
//                                                    Calendar c = Calendar.getInstance();
//                                                    Calendar c12 = Calendar.getInstance();
//                                                    c12.setTime(sdf.parse(toDate));
//
//                                                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                                                    String currentDate11 = df2.format(c1);
//                                                    c.setTime(sdf.parse(fromDate));
//                                                    try {
//                                                        fDate = dateFormat.parse(fromDate);
//                                                        tDate = dateFormat.parse(toDate);
//                                                        Log.e("Date1111", "Before" + tDate.toString());
//                                                        c12.add(Calendar.DATE, 1);
//
//
//                                                        tDate = c12.getTime();
//                                                        Log.e("Date1111", "" + tDate.toString());
//                                                    } catch (ParseException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                    if (fDate == null || tDate == null) {
//                                                        Log.e("Exception", "Error");
//                                                    } else {
//                                                        Date temp = fDate;
//                                                        Log.e("callTemp", "" + temp);
//
//                                                        int count = 0;
//                                                        while (temp.before(tDate)) {
//
//
//                                                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//                                                            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM", Locale.US);
//                                                            String date = df.format(temp);
//                                                            String date1 = df1.format(temp);
//                                                            Log.e("ShreyaMamKaDate", date1);
//                                                            modelDateArrayList.add(new ModelDate(date));
//                                                            shortDateList.add(new ModelDate(date1));
//                                                            Log.e("dateeeee1", modelDateArrayList.get(count).getDate());
//                                                            count++;
//                                                            Log.e("dateeee", date);
//                                                            c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                                                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy");
//                                                            temp = c.getTime();
//                                                            Log.e("Temp", "" + count + ":" + temp);
//
//
//                                                        }
////                                                        modelDayBookClassArrayList.clear();
//                                                        associateDetailsArrayList.clear();
//
//                                                        for (int m = 0; m < modelDateArrayList.size(); m++) {
//                                                            if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0) {
//                                                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Attendance").child(modelDateArrayList.get(m).getDate()).getChildren()) {
//
//                                                                    ModelAssociateDetails modelAssociateDetails = ds1.getValue(ModelAssociateDetails.class);
//                                                                    Log.e("Model123", "" + (modelAssociateDetails.getStatus() == null));
//                                                                    Log.e("Model123", "" + (modelAssociateDetails.getStatus()));
//                                                                    modelAssociateDetails.setDate(modelDateArrayList.get(m).getDate());
//
//                                                                    if (siteLatitude > 0 && siteLongitude > 0) {
//                                                                        modelAssociateDetails.setSiteLatitude(siteLatitude);
//                                                                        modelAssociateDetails.setSiteLongitude(siteLongitude);
//                                                                    }
//                                                                    Log.e("ModelAss123","DS1"+ds1.getKey());
//                                                                    Log.e("ModelAss123",modelAssociateDetails.getTimeStamp());
//                                                                    Log.e("ModelAss123",modelAssociateDetails.getStatus());
//
//                                                                    if (modelAssociateDetails.getStatus().equals("NormalLogin")) {
//                                                                        associateLoginDetails.add(modelAssociateDetails);
//                                                                    } else {
//                                                                        associateLogOutdetails.add(modelAssociateDetails);
//                                                                    }
//
//
//                                                                }
//                                                            }
//
//                                                        }
//
//                                                        DownloadExcel(associateLoginDetails, associateLogOutdetails, siteId1, siteName1);
//
//
//                                                    }
//                                                } catch (ParseException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//            }
//        });
    }
    private void showPaymentDialog() {

        file = null;
        fos = null;
        final AlertDialog.Builder alert = new AlertDialog.Builder(AssociateDetailsActivity.this);
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
//        getPromoCode(spinner_promo);
//        Log.e("Dnl_file", dnl_file_type);
//        if(dnl_file_type.equals("Pdf")){
//            amount = Math.round((modelDateArrayList.size()*5));
//        }else{
//            amount = Math.round((modelDateArrayList.size()*10));
//        }


//        AmountTemp = amount;
//        spinner_promo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i > 0) {
//                    promo_spinner_position = i;
//                    promo_title = promoArrayList.get(i).getTitle();
//
//                    Float dis = Float.valueOf(promoArrayList.get(i).getDiscount());
//
//
//                    Float d_amount = Float.valueOf(Math.round(modelDateArrayList.size() * 4.5));
//                    dis = dis.floatValue() / 100 * amount;
//                    AmountTemp = Math.round(amount - dis);
//
////                           amount= (int) (amount-((promoArrayList.get(i).getDiscount()/100)*amount));
//
//                    Log.e("Amt", "AD" + amount);
//                    Log.e("Amt", "D" + (promoArrayList.get(i).getDiscount() / 100));
//                    Log.e("Amt", "DF" + dis.floatValue() / 100 * amount);
//                    Log.e("Amt", "Af" + d_amount);
//
//                    txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(AmountTemp));
//                    txt_heading.setText(getString(R.string.your_file_one_click_away) + " " + String.valueOf(AmountTemp) + " " + getString(R.string.your_have_tom_make_payment));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        txt_heading.setText(getString(R.string.your_file_one_click_away) + " " + String.valueOf(AmountTemp) + " " + getString(R.string.your_have_tom_make_payment));
        txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(AmountTemp));
        btn_cancel = mView.findViewById(R.id.btn_cancel);
        btn_pay = mView.findViewById(R.id.btn_pay);
        txt_file_type.setText("Team Member Report");
        txt_from.setText(modelDateArrayList.get(0).getDate());
        txt_to.setText(modelDateArrayList.get(modelDateArrayList.size() - 1).getDate());

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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.txtNoData.setVisibility(View.GONE);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    Log.e("siteAdminList", "Size" + siteAdminList.size());
                    modelDateArrayList.clear();
//                            saxjb
                    for (int i = 0; i < siteAdminList.size(); i++) {
                        if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
                            Log.e("SiteFound", "" + siteAdminList.get(i).getSiteId());
                            if (siteAdminList.get(i).getSelected()) {
                                Log.e("SiteFound", "2::::" + siteAdminList.get(i).getSiteId());
                                long siteId1 = siteAdminList.get(i).getSiteId();
                                siteLatitude = snapshot.child(String.valueOf(siteId1)).child("siteLatitude").getValue(double.class);

                                siteLongitude = snapshot.child(String.valueOf(siteId1)).child("siteLongitude").getValue(double.class);
                                String siteName1 = siteAdminList.get(i).getSiteName();
                                modelDateArrayList.clear();
                                associateLoginDetails.clear();
                                associateLogOutdetails.clear();
//                                        associateDetailsArrayList.clear();
                                if (binding.rbToday.isChecked()) {

                                    if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child(currentDate).getChildrenCount() > 0) {
                                        Log.e("ModelAssociate123", "SnapshotExist");
                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Attendance").child(currentDate).getChildren()) {
                                            ModelAssociateDetails modelAssociateDetails = ds1.getValue(ModelAssociateDetails.class);
                                            Log.e("Model123", "" + (modelAssociateDetails.getStatus() == null));
                                            Log.e("Model123", "" + (modelAssociateDetails.getStatus()));
                                            modelAssociateDetails.setDate(currentDate);

                                            if (siteLatitude > 0 && siteLongitude > 0) {
                                                modelAssociateDetails.setSiteLatitude(siteLatitude);
                                                modelAssociateDetails.setSiteLongitude(siteLongitude);
                                            }
                                            if (modelAssociateDetails.getStatus().equals("NormalLogin")) {
                                                associateLoginDetails.add(modelAssociateDetails);
                                            } else {
                                                associateLogOutdetails.add(modelAssociateDetails);
                                            }
//                                                    associateDetailsArrayList.add(modelAssociateDetails);


                                        }
                                        DownloadExcel(associateLoginDetails, associateLogOutdetails, siteId1, siteName1);


                                    }


                                } else if (binding.rbCustom.isChecked()) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    Boolean f = true;
                                    try {

                                        Date fDate = dateFormat.parse(fromDate);
                                        Date tDate = dateFormat.parse(toDate);
                                        if (tDate.before(fDate)) {
                                            Toast.makeText(AssociateDetailsActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                                            f = false;
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (fromDate.equals("")) {
                                        Toast.makeText(AssociateDetailsActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                                    } else if (toDate.equals("")) {
                                        Toast.makeText(AssociateDetailsActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                                    } else if (f) {
                                        try {
                                            modelDateArrayList = new ArrayList<>();
                                            shortDateList = new ArrayList<>();
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
//                                                        modelDayBookClassArrayList.clear();
                                                associateDetailsArrayList.clear();

                                                for (int m = 0; m < modelDateArrayList.size(); m++) {
                                                    if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0) {
                                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Attendance").child(modelDateArrayList.get(m).getDate()).getChildren()) {

                                                            ModelAssociateDetails modelAssociateDetails = ds1.getValue(ModelAssociateDetails.class);
                                                            Log.e("Model123", "" + (modelAssociateDetails.getStatus() == null));
                                                            Log.e("Model123", "" + (modelAssociateDetails.getStatus()));
                                                            modelAssociateDetails.setDate(modelDateArrayList.get(m).getDate());

                                                            if (siteLatitude > 0 && siteLongitude > 0) {
                                                                modelAssociateDetails.setSiteLatitude(siteLatitude);
                                                                modelAssociateDetails.setSiteLongitude(siteLongitude);
                                                            }
                                                            if (modelAssociateDetails.getStatus().equals("NormalLogin")) {
                                                                associateLoginDetails.add(modelAssociateDetails);
                                                            } else {
                                                                associateLogOutdetails.add(modelAssociateDetails);
                                                            }


                                                        }
                                                    }

                                                }

                                                DownloadExcel(associateLoginDetails, associateLogOutdetails, siteId1, siteName1);


                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(AssociateDetailsActivity.this);
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
            txt_message.setText("Your Team Member Report PDF file is generated and stored in DOWNLOADS folder. ");
        } else {
            txt_message.setText("Your Team Member Report EXCEL+PDF file is generated and stored in DOWNLOADS folder. ");
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
                hashMap.put("siteId", siteId);
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", dnl_file_type);
                hashMap.put("totalAmount", String.valueOf(amount));

                hashMap.put("fromDate", fromDate);
                hashMap.put("toDate", toDate);
                hashMap.put("promoApplied", false);
                hashMap.put("promoTitle", "");
                
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData", paymentData.getData().toString());
                                Log.e("paymentData", paymentData.getPaymentId().toString());
//

                                Log.e("fos", "flush");
                                Toast.makeText(AssociateDetailsActivity.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                Uri dirUri = FileProvider.getUriForFile(AssociateDetailsActivity.this,getApplicationContext().getPackageName() + ".com.skillzoomer_Attendance.com",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
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

    private void getSiteData(long siteId) {
        Log.e("siteId", "" + siteId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("SLigi", String.valueOf(snapshot.child("siteLatitude").getValue(double.class)));
                siteLatitude = snapshot.child("siteLatitude").getValue(double.class);
                siteLongitude = snapshot.child("siteLongitude").getValue(double.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DownloadExcel(ArrayList<ModelAssociateDetails> associateLoginDetails, ArrayList<ModelAssociateDetails> associateLogOutdetails, long siteId1, String siteName1) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        Log.e("StrPath","InExcel");
//        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        createHeaderRow(sheet, associateDetailsArrayList, siteId1, siteName1);
        createDayBookData(sheet, associateLoginDetails, associateLogOutdetails, siteId1, siteName1);
        createFooter(sheet, associateLoginDetails, siteId1, siteName1);

        for(int i=0;i<associateLogOutdetails.size();i++){
            Log.e("ModelAss123","i:"+i+":"+associateLogOutdetails.get(i).getStatus());
            Log.e("ModelAss123","T:"+i+":"+associateLogOutdetails.get(i).getTimeStamp());
        }

        showChats asyncTask=new showChats(siteName1,siteId1,associateLoginDetails,associateLogOutdetails);
        asyncTask.execute();


        dnl_file_type="xls";
        if(dnl_file_type.equals("xls")){
            try {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());

                HSSFPatriarch dp = sheet.createDrawingPatriarch();

                HSSFClientAnchor anchor = new HSSFClientAnchor
                        (0, 0, 650, 255, (short) 2, associateLoginDetails.size()/2, (short) 13, associateLoginDetails.size()/2+3);


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
                String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                String timestamp = "" + System.currentTimeMillis();
                file = new File(str_path, siteName1 + "" + "TMRep" + timestamp + ".xls");
                fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.flush();
                fos.close();
                Toast.makeText(AssociateDetailsActivity.this, "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Escepkjdfgb",e.getMessage());
            }

        }
//        try {
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
////            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
////            Log.e("Directory",directory.getAbsolutePath());
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            String timestamp = "" + System.currentTimeMillis();
////            Log.e("StrPath", str_path);
////
//////            fos = new FileOutputStream(file);
////            Log.e("FilePath", file.getAbsolutePath().toString());
////            Log.e("StrPath", str_path);
//            file = new File(str_path, siteName1 + "" + "TMRep" + timestamp + ".xls");
//            fos = new FileOutputStream(file);
//            workbook.write(fos);
//            fos.flush();
//            fos.close();
//            Toast.makeText(AssociateDetailsActivity.this, "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("Exceptio434",e.getMessage());
//        }




//        Log.e("DayBook","SiteID:"+siteId1);
//        Log.e("DayBook","Date\tReceived\tRFrom\tExpense\tExpenseRemark");
//
//        for(int i=0;i<modelDayBookClassArrayList.size();i++){
//           Log.e("DayBook",modelDayBookClassArrayList.get(i).getDate()+"\t"+modelDayBookClassArrayList.get(i).getRecAmt()+"\t"
//           +modelDayBookClassArrayList.get(i).getRec_from()+"\t"+modelDayBookClassArrayList.get(i).getExpAmt()+"\t"+modelDayBookClassArrayList.get(i).getExpRemark());
//        }

    }
    public class showChats extends AsyncTask<String, String, String> {

        private String siteName1;
        private long siteId1;
       private ArrayList<ModelAssociateDetails> associateLoginDetails;
       private ArrayList<ModelAssociateDetails> associateLogoutDetails;

        public showChats(String siteName1, long siteId1, ArrayList<ModelAssociateDetails> associateLoginDetails, ArrayList<ModelAssociateDetails> associateLogoutDetails) {
            this.siteName1 = siteName1;
            this.siteId1 = siteId1;
            this.associateLoginDetails = associateLoginDetails;
            this.associateLogoutDetails = associateLogoutDetails;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(AssociateDetailsActivity.this);
            pd.setTitle(getString(R.string.please_wait));
//             progressDialog.setMessage("Laoding Chats.....");
//             progressDialog.show();
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.setMessage("Downloading Report");
            pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            FileInputStream input_document = null;
            String timestamp = "" + System.currentTimeMillis();
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "Tm_report"+""+siteName1 + timestamp+ ".pdf";
            File pdfFile = new File(str_path);

            com.itextpdf.text.Font fontHeading=new com.itextpdf.text.Font();
            fontHeading.setStyle(com.itextpdf.text.Font.NORMAL);
            fontHeading.setSize(22);
            fontHeading.setColor(BaseColor.BLACK);
            fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));

            com.itextpdf.text.Font fontHeading1=new com.itextpdf.text.Font();
            fontHeading1.setStyle(com.itextpdf.text.Font.BOLD);
            fontHeading1.setSize(16);
            fontHeading1.setColor(BaseColor.BLACK);
            fontHeading1.setFamily(String.valueOf(Paint.Align.CENTER));

            com.itextpdf.text.Font fontBold=new com.itextpdf.text.Font();
            fontBold.setStyle(com.itextpdf.text.Font.BOLD);
            fontBold.setSize(18);
            fontBold.setColor(BaseColor.BLACK);
            fontBold.setFamily(String.valueOf(Paint.Align.CENTER));
//
            // Read workbook into HSSFWorkbook
            HSSFWorkbook my_xls_workbook = null;
            my_xls_workbook = workbook;
            // Read worksheet into HSSFSheet
            HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
            // To iterate over the rows
            my_worksheet.getRow(6);
            Iterator<Row> rowIterator = my_worksheet.iterator();
            //We will create output PDF document objects at this point
            com.itextpdf.text.Document iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 80f);

//            iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
//            iText_xls_2_pdf.setPageCount(associateLoginDetails.size()+1);
            try {
                PdfWriter writer=PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
                HeaderFooterPageEvent event = new HeaderFooterPageEvent(AssociateDetailsActivity.this,siteId1,siteName1,modelDateArrayList,"Team Member Attendance Report");
                writer.setPageEvent(event);
            } catch (DocumentException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();

            } catch (FileNotFoundException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();
            }
            iText_xls_2_pdf.open();

            PdfPTable my_table1 = new PdfPTable( 8);
            my_table1.setTotalWidth(iText_xls_2_pdf.getPageSize().getWidth());

            PdfPCell table_cellhh1 = new PdfPCell();
            table_cellhh1.setColspan(2);
            table_cellhh1.setPhrase(new Phrase("Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""),fontHeading1));
            table_cellhh1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh1);

            PdfPCell table_cellhh2 = new PdfPCell();
            table_cellhh2.setColspan(2);
            table_cellhh2.setPhrase(new Phrase("Generated On: " +currentDate,fontHeading1));
            table_cellhh2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh2);

            PdfPCell table_cellhh3 = new PdfPCell();
            table_cellhh3.setColspan(2);
            table_cellhh3.setPhrase(new Phrase( "Site Id: " + siteId1,fontHeading1));
            table_cellhh3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh3);

            PdfPCell table_cellhh4 = new PdfPCell();
            table_cellhh4.setColspan(2);
            table_cellhh4.setPhrase(new Phrase(  "Site Name: " + siteName1,fontHeading1));
            table_cellhh4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh4);

            PdfPCell table_cellhh5 = new PdfPCell();
            table_cellhh5.setColspan(2);
            table_cellhh5.setPhrase(new Phrase(  "Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName",""),fontHeading1));
            table_cellhh5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh5);

            PdfPCell table_cellhh6 = new PdfPCell();
            table_cellhh6.setColspan(6);
            table_cellhh6.setPhrase(new Phrase(  "Team Member Attendance Report",fontHeading1));
            table_cellhh6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellhh6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table1.addCell(table_cellhh6);

//
//            String date_val = "Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName","")+
//                    "\n"+"Generated On: " +currentDate+
//                    "\n"+"Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName","")+"\n"+
//                    "Site Id: " + siteId1 +
//                    "\n Site Name: " + siteName1;;
            Paragraph p1 = new Paragraph("\n\n");
            p1.setAlignment(Paragraph.ALIGN_CENTER);
//
//            Paragraph p2 = new Paragraph("Workers Master Data Sheet\n");
//            p2.setAlignment(Paragraph.ALIGN_CENTER);
//
//            Paragraph p3 = new Paragraph("\n");
//            p3.setAlignment(Paragraph.ALIGN_CENTER);


            try {
                iText_xls_2_pdf.add(my_table1);
                iText_xls_2_pdf.add(p1);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            //we have two columns in the Excel sheet, so we create a PDF table with two columns
            //Note: There are ways to make this dynamic in nature, if you want to.
            PdfPTable my_table = new PdfPTable( 9+4);
            my_table.setWidthPercentage(95);
            //We will use the object below to dynamically add new data to the table
            PdfPCell table_cell;
            PdfPCell table_cellh1 = new PdfPCell();
            table_cellh1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh1.setPhrase(new Phrase("Sr No",fontBold));
            table_cellh1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh1);

            PdfPCell table_cellh2 = new PdfPCell();
            table_cellh2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh2.setPhrase(new Phrase("Date",fontBold));
            table_cellh2.setColspan(2);
            table_cellh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh2);

            PdfPCell table_cellh3 = new PdfPCell();
            table_cellh3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh3.setPhrase(new Phrase("Name",fontBold));
            table_cellh3.setColspan(3);
            table_cellh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh3);

            PdfPCell table_cellh4 = new PdfPCell();
            table_cellh4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh4.setPhrase(new Phrase("Login Time",fontBold));
            table_cellh4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh4);

            PdfPCell table_cellh5 = new PdfPCell();
            table_cellh5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh5.setPhrase(new Phrase("Distance",fontBold));
            table_cellh5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh5);

            PdfPCell table_cellh6 = new PdfPCell();
            table_cellh6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh6.setPhrase(new Phrase("Log Out Time",fontBold));
            table_cellh6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh6);

            PdfPCell table_cellh7 = new PdfPCell();
            table_cellh7.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh7.setPhrase(new Phrase("Distance",fontBold));
            table_cellh7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh7);

            PdfPCell table_cellh8 = new PdfPCell();
            table_cellh8.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh8.setPhrase(new Phrase("Status",fontBold));
            table_cellh8.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh8);

            PdfPCell table_cellh9 = new PdfPCell();
            table_cellh9.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh9.setPhrase(new Phrase("Selfie With Site",fontBold));
            table_cellh9.setColspan(2);
            table_cellh9.setFixedHeight(20);
            table_cellh9.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh9);




            for (int i = 0; i < associateLoginDetails.size(); i++) {
                PdfPCell table_cell1 = new PdfPCell();
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setPhrase(new Phrase(String.valueOf(i+1),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell1);

                PdfPCell table_cell2 = new PdfPCell();
                table_cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell2.setPhrase(new Phrase(associateLoginDetails.get(i).getDate(),fontHeading));
                table_cell2.setColspan(2);
                table_cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell2);

                PdfPCell table_cell3 = new PdfPCell();
                table_cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (associateLoginDetails.get(i).getName() != null && !associateLoginDetails.get(i).getName().equals("")) {
                    table_cell3.setPhrase(new Phrase(associateLoginDetails.get(i).getName(),fontHeading));
                    table_cell3.setColspan(3);
                    my_table.addCell(table_cell3);

                }else {
                    table_cell3.setPhrase(new Phrase("-",fontHeading));
                    table_cell3.setColspan(3);
                    my_table.addCell(table_cell3);
                }


                table_cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell4 = new PdfPCell();
                table_cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

                if (associateLoginDetails.get(i).getStatus().equals("NormalLogin")) {
                    table_cell4.setPhrase(new Phrase(associateLoginDetails.get(i).getTime(),fontHeading));
                    my_table.addCell(table_cell4);
                }

                table_cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell5 = new PdfPCell();
                table_cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (associateLoginDetails.get(i).getSiteLatitude() > 0 && associateLoginDetails.get(i).getSiteLongitude() > 0 &&
                        associateLoginDetails.get(i).getMemberLatitude() > 0 &&
                        associateLoginDetails.get(i).getMemberLatitude() > 0) {
                    float[] results = new float[1];
                    Location.distanceBetween(associateLoginDetails.get(i).getSiteLatitude(), associateLoginDetails.get(i).getSiteLongitude(), associateLoginDetails.get(i).getMemberLatitude()
                            , associateLoginDetails.get(i).getMemberLongitude(), results);
                    float distance = results[0];
                    int dis_display = (int) distance;
                    table_cell5.setPhrase(new Phrase(String.valueOf(dis_display) + ("m"),fontHeading));
                    my_table.addCell(table_cell5);


                } else {
                    table_cell5.setPhrase(new Phrase("NR",fontHeading));
                    my_table.addCell(table_cell5);
                }


                table_cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell6 = new PdfPCell();
                table_cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell7 = new PdfPCell();
                table_cell7.setHorizontalAlignment(Element.ALIGN_CENTER);

                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);


                PdfPCell table_cell8 = new PdfPCell();
                table_cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell8.setPhrase(new Phrase(associateLoginDetails.get(i).getDate(),fontHeading));
                table_cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);

                if (i < associateLogOutdetails.size()) {
                    if(associateLogOutdetails.get(i).getStatus()!=null && !associateLogOutdetails.get(i).getStatus().equals("normalLogout")){
                        table_cell8.setPhrase(new Phrase("FL",fontHeading));

                    }else{
                        table_cell8.setPhrase(new Phrase("",fontHeading));
                    }
                    if (i + 1 < associateLoginDetails.size()) {
//                        Log.e("Tieerror",associateLogOutdetails.get(i).getTimeStamp());
//                        Log.e("Tieerror",associateLoginDetails.get(i + 1).getTimeStamp());
                        if (associateLogOutdetails.get(i).getTimeStamp()!=null&&associateLoginDetails.get(i + 1).getTimeStamp()!=null&&
                                Long.parseLong(associateLogOutdetails.get(i).getTimeStamp()) > Long.parseLong(associateLoginDetails.get(i + 1).getTimeStamp())) {
                            associateLogOutdetails.add(i, new ModelAssociateDetails());
                            table_cell6.setPhrase(new Phrase("-",fontHeading));
                            table_cell7.setPhrase(new Phrase("-",fontHeading));


                        } else {
                            table_cell6.setPhrase(new Phrase(associateLogOutdetails.get(i).getTime(),fontHeading));


                            if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 &&
                                    associateLogOutdetails.get(i).getMemberLatitude() > 0 &&
                                    associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                                float[] results = new float[1];
                                Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude()
                                        , associateLogOutdetails.get(i).getMemberLongitude(), results);
                                float distance = results[0];
                                int dis_display = (int) distance;
                                table_cell7.setPhrase(new Phrase(String.valueOf(dis_display) + ("m"),fontHeading));



                            } else {
                                table_cell7.setPhrase(new Phrase("NR",fontHeading));

                            }
                        }


                    } else {

                        table_cell6.setPhrase(new Phrase(associateLogOutdetails.get(i).getTime(),fontHeading));


                        if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 &&
                                associateLogOutdetails.get(i).getMemberLatitude() > 0 &&
                                associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                            float[] results = new float[1];
                            Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude()
                                    , associateLogOutdetails.get(i).getMemberLongitude(), results);
                            float distance = results[0];
                            int dis_display = (int) distance;
                            table_cell7.setPhrase(new Phrase(String.valueOf(dis_display) + ("m"),fontHeading));



                        } else {
                            table_cell7.setPhrase(new Phrase("NR",fontHeading));

                        }

                    }
                } else {
                    table_cell6.setPhrase(new Phrase("-",fontHeading));
                    table_cell7.setPhrase(new Phrase("-",fontHeading));
                    table_cell8.setPhrase(new Phrase("-",fontHeading));

                }




                my_table.addCell(table_cell6);
                my_table.addCell(table_cell7);
                my_table.addCell(table_cell8);









                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here

                    String imageUrl = associateLoginDetails.get(i).getProfile();
                    Image imageFromWeb = null;
                    try {
                        imageFromWeb = Image.getInstance(new URL(imageUrl));
                    } catch (BadElementException e) {
                        Log.e("IEcxc","1"+e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("IEcxc","1"+e.getMessage());
                        e.printStackTrace();
                    }
                    PdfPCell cellImageInTable = new PdfPCell(imageFromWeb , true);
                    cellImageInTable.setColspan(2);
                    my_table.addCell(cellImageInTable);


                }

            }




            try {
                iText_xls_2_pdf.add(my_table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

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
            String date_val3 = "To know us more visit https://skillzoomers.com/hajiri-register/ ";

//          Paragraph p4 = new Paragraph(date_val1);
//          p4.setAlignment(Paragraph.ALIGN_CENTER);


            Paragraph p7 = new Paragraph("\n");
            p7.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph p8 = new Paragraph(date_val3);
            p8.setAlignment(Paragraph.ALIGN_CENTER);

            try {

                iText_xls_2_pdf.add(p7);
                iText_xls_2_pdf.add(p8);
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(AssociateDetailsActivity.this,"Downloaded",Toast.LENGTH_LONG);
            pd.dismiss();

        }
    }

    private void DownloadPdf(ArrayList<ModelAssociateDetails> associateDetailsArrayList, ArrayList<ModelAssociateDetails> associateLogOutdetails, long siteId1, String siteName1, RecyclerView rvTmCard) {
        Bitmap recycler_view_bm = getScreenshotFromRecyclerView(rvTmCard);
        try {

            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "TM_Rep_" + siteName1 + timestamp + ".pdf";
            File pdfFile = new File(str_path);

//            pdfFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(pdfFile);

            PdfDocument document = new PdfDocument();
            com.itextpdf.text.Document iText_xls_2_pdf = new Document();
            iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
            try {
                PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            } catch (DocumentException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();

            } catch (FileNotFoundException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();
            }
            iText_xls_2_pdf.open();
            String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") +
                    "\n" + "Generated On: " + currentDate +
                    "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "") + "\n" +
                    "Site Id: " + siteId1 +
                    "\n Site Name: " + siteName1;
            ;
            Paragraph p1 = new Paragraph(date_val);
            p1.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph p2 = new Paragraph("Team Member Report");
            p2.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph p3 = new Paragraph("\n");
            p3.setAlignment(Paragraph.ALIGN_CENTER);

            try {
                iText_xls_2_pdf.add(p1);
                iText_xls_2_pdf.add(p2);
                iText_xls_2_pdf.add(p3);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);


            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(1224, recycler_view_bm.getHeight(), 2).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            recycler_view_bm.prepareToDraw();
            Canvas c;
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);

            c = page.getCanvas();
            c.drawPaint(paint);

            c.drawText(date_val, 0, 0, paint);
//            c.drawText(date_val,0,0,null);
            c.drawBitmap(recycler_view_bm, 0, 25, null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
//            Toast.makeText(this, "Report Generated Successfully", Toast.LENGTH_SHORT).show();

//            Snackbar snackbar = Snackbar
//                    .make(equipmentsRecordActivityLayout, "PDF generated successfully.", Snackbar.LENGTH_LONG)
//                    .setAction("Open", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            openPDFRecord(pdfFile);
//                        }
//                    });
//
//            snackbar.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            Log.e("Size12323234213424", "" + adapter.getItemCount());
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {

                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, 1224, holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();

                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
                if (i % 2 == 0 && i + 2 < size) {
                    binding.rvTmCard.scrollToPosition(i + 2);
                }

            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);


            for (int i = 0; i < size; i++) {

                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
    }


    private void createFooter(Sheet sheet, ArrayList<ModelAssociateDetails> associateDetailsArrayList, long siteId1, String siteName1) {
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
        sheet.createRow(associateDetailsArrayList.size() + 8);


        Row row = sheet.createRow(associateDetailsArrayList.size() + 10);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle2);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Generated by: " + " " + "Hajiri Register";
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(associateDetailsArrayList.size() + 10, associateDetailsArrayList.size() + 12, 0, 6);
        sheet.addMergedRegion(cellMerge);

        String date_val1 = "Powered by: " + " " + "Skill Zoomers";

        Row row1 = sheet.createRow(associateDetailsArrayList.size() + 14);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue(date_val1);

        CellRangeAddress cellMerge1 = new CellRangeAddress(associateDetailsArrayList.size() + 14, associateDetailsArrayList.size() + 16, 0, 6);
        sheet.addMergedRegion(cellMerge1);

        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ " + " " + "Skill Zoomers";

        Row row2 = sheet.createRow(associateDetailsArrayList.size() + 18);
        Cell cellHeading1 = row2.createCell(0);

        cellHeading1.setCellStyle(cellStyle2);
        cellHeading1.setCellValue(date_val2);

        CellRangeAddress cellMerge2 = new CellRangeAddress(associateDetailsArrayList.size() + 18, associateDetailsArrayList.size() + 20, 0, 6);
        sheet.addMergedRegion(cellMerge2);
    }

    private void createDayBookData(Sheet sheet, ArrayList<ModelAssociateDetails> associateLoginDetails, ArrayList<ModelAssociateDetails> associateLogOutdetails, long siteId1, String siteName1) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            font.setColor((short) getColor(R.color.black));
        }
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        Log.e("AssLogDet", "" + associateLoginDetails.size());
        Log.e("AssLogDet", "" + associateLogOutdetails.size());
        for (int i = 0; i < associateLoginDetails.size(); i++) {

            Log.e("AssLogDate", associateLoginDetails.get(i).getDate());
            int j = i;
            Row row = sheet.createRow(i + 7);
            row.setHeightInPoints(25);
            Cell cellSrNo = row.createCell(0);
            cellSrNo.setCellStyle(cellStyle);
            cellSrNo.setCellValue(String.valueOf(i + 1));
            Cell cellDate = row.createCell(1);
            cellDate.setCellStyle(cellStyle);
            cellDate.setCellValue(associateLoginDetails.get(i).getDate());


            Cell cellSrNo1 = row.createCell(2);
            cellSrNo1.setCellStyle(cellStyle);
            if (associateLoginDetails.get(i).getName() != null && !associateLoginDetails.get(i).getName().equals("")) {
                cellSrNo1.setCellValue(associateLoginDetails.get(i).getName());
            } else {
                cellSrNo1.setCellValue("-");
            }

            Cell cellSrNo2 = row.createCell(3);
            cellSrNo2.setCellStyle(cellStyle);
//            cellSrNo2.setCellValue(modelDayBookClassArrayList.get(i).getRec_from());
            if (associateLoginDetails.get(i).getStatus().equals("NormalLogin")) {
                cellSrNo2.setCellValue(associateLoginDetails.get(i).getTime());
//                float[] results = new float[1];
//                Location.distanceBetween(associateDetailsArrayList.get(i).getSiteLatitude(), associateDetailsArrayList.get(i).getSiteLongitude(), associateDetailsArrayList.get(i).getMemberLatitude()
//                        , associateDetailsArrayList.get(i).getMemberLongitude(), results);
//                float distance = results[0];
//                int dis_display = (int) distance;
//
//                if (associateDetailsArrayList.get(holder.getAdapterPosition()).getSiteLongitude() > 0 && associateDetailsArrayList.get(holder.getAdapterPosition()).getMemberLongitude() > 0) {
//                    holder.txt_distance.setVisibility(View.VISIBLE);
//                    holder.txt_distance.setText(dis_display + ("m"));
//                } else {
//                    holder.txt_distance.setText("NA");
//                }

            }
            Cell cellSrNo3 = row.createCell(4);
            cellSrNo3.setCellStyle(cellStyle);
            if (associateLoginDetails.get(i).getSiteLatitude() > 0 && associateLoginDetails.get(i).getSiteLongitude() > 0 &&
                    associateLoginDetails.get(i).getMemberLatitude() > 0 &&
                    associateLoginDetails.get(i).getMemberLatitude() > 0) {
                float[] results = new float[1];
                Location.distanceBetween(associateLoginDetails.get(i).getSiteLatitude(), associateLoginDetails.get(i).getSiteLongitude(), associateLoginDetails.get(i).getMemberLatitude()
                        , associateLoginDetails.get(i).getMemberLongitude(), results);
                float distance = results[0];
                int dis_display = (int) distance;
                cellSrNo3.setCellValue(String.valueOf(dis_display) + ("m"));


            } else {
                cellSrNo3.setCellValue("NR");
            }
            Cell cellSrNo4 = row.createCell(5);
            cellSrNo4.setCellStyle(cellStyle);
//            cellSrNo4.setCellValue(modelDayBookClassArrayList.get(i).getExpRemark());
//            if(associateLoginDetails.get(i).getSiteLatitude()>0 &&associateLoginDetails.get(i).getSiteLongitude()>0 &&
//                    associateLoginDetails.get(i).getMemberLatitude()>0 &&
//                    associateLoginDetails.get(i).getMemberLatitude()>0 ){
//                float[] results = new float[1];
//                Location.distanceBetween(associateLoginDetails.get(i).getSiteLatitude(), associateLoginDetails.get(i).getSiteLongitude(), associateDetailsArrayList.get(i).getMemberLatitude()
//                        , associateLoginDetails.get(i).getMemberLongitude(), results);
//                float distance = results[0];
//                int dis_display = (int) distance;
//                cellSrNo4.setCellValue(String.valueOf(dis_display) + ("m"));
//
//
//
//
//            }else{
//                cellSrNo4.setCellValue("NA");
//            }
            Cell cellSrNo5 = row.createCell(6);
            cellSrNo5.setCellStyle(cellStyle);
            Cell cellSrNo6 = row.createCell(7);
            cellSrNo6.setCellStyle(cellStyle);
            if (i < associateLogOutdetails.size()) {
                if(associateLogOutdetails.get(i).getStatus()!=null && associateLogOutdetails.get(i).getStatus().equals("normalLogout")){
                    cellSrNo6.setCellValue("FL");

                }else{
                    cellSrNo6.setCellValue("");
                }
                if (i + 1 < associateLoginDetails.size()) {
                    if (Long.parseLong(associateLogOutdetails.get(i).getTimeStamp()) > Long.parseLong(associateLoginDetails.get(i + 1).getTimeStamp())) {
                        associateLogOutdetails.add(i, new ModelAssociateDetails());
                        cellSrNo4.setCellValue("-");
                        cellSrNo5.setCellValue("-");
                    } else {
                        cellSrNo4.setCellValue(associateLogOutdetails.get(i).getTime());

                        if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 &&
                                associateLogOutdetails.get(i).getMemberLatitude() > 0 &&
                                associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                            float[] results = new float[1];
                            Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude()
                                    , associateLogOutdetails.get(i).getMemberLongitude(), results);
                            float distance = results[0];
                            int dis_display = (int) distance;
                            cellSrNo5.setCellValue(String.valueOf(dis_display) + ("m"));


                        } else {
                            cellSrNo5.setCellValue("NR");
                        }
                    }


                } else {
                    cellSrNo4.setCellValue(associateLogOutdetails.get(i).getTime());

                    if (associateLogOutdetails.get(i).getSiteLatitude() > 0 && associateLogOutdetails.get(i).getSiteLongitude() > 0 &&
                            associateLogOutdetails.get(i).getMemberLatitude() > 0 &&
                            associateLogOutdetails.get(i).getMemberLatitude() > 0) {
                        float[] results = new float[1];
                        Location.distanceBetween(associateLogOutdetails.get(i).getSiteLatitude(), associateLogOutdetails.get(i).getSiteLongitude(), associateLogOutdetails.get(i).getMemberLatitude()
                                , associateLogOutdetails.get(i).getMemberLongitude(), results);
                        float distance = results[0];
                        int dis_display = (int) distance;
                        cellSrNo5.setCellValue(String.valueOf(dis_display) + ("m"));


                    } else {
                        cellSrNo5.setCellValue("NR");
                    }

                }
            } else {
                cellSrNo4.setCellValue("-");
                cellSrNo5.setCellValue("-");
            }


        }

    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelAssociateDetails> associateDetailsArrayList, long siteId1, String siteName1) {
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
        cellStyle1.setAlignment(CellStyle.ALIGN_LEFT);
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
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);


        Row row = sheet.createRow(0);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle1);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") +
                "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "");

        cellFullName.setCellValue(date_val);
        int size = 0;
        if (modelDateArrayList.size() + 5 < 8) {
            size = 8;
        } else {
            size = modelDateArrayList.size() + 5;
        }

        CellRangeAddress cellMerge = new CellRangeAddress(0, 3, 0, 6);
        sheet.addMergedRegion(cellMerge);

        Cell cellFullName1 = row.createCell(7);
        cellFullName1.setCellStyle(cellStyle1);
        String date_val1 = "Generated On: " + currentDate + "\n" + "Site Id: " + siteId1 +
                "\t\t\t\t\t Site Name: " + siteName1;
        cellFullName1.setCellValue(date_val1);
        int size1 = 0;
        if (modelDateArrayList.size() + 4 < 10) {
            size1 = 12;
        } else {
            size1 = modelDateArrayList.size() + 4;
        }

        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 3, 7, size1);
        sheet.addMergedRegion(cellMerge1);


        Row row1 = sheet.createRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("Team Member Attendance Report");

        CellRangeAddress cellMerge2 = new CellRangeAddress(4, 5, 0, 7);
        sheet.addMergedRegion(cellMerge2);

        Row rowValues = sheet.createRow(6);
        rowValues.setHeightInPoints(30);
        Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        Cell cellDate = rowValues.createCell(1);
        cellDate.setCellStyle(cellStyle);
        cellDate.setCellValue("Date");
        Cell cellSrNo1 = rowValues.createCell(2);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Name");
        Cell cellSrNo2 = rowValues.createCell(3);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Login");
        Cell cellSrNo3 = rowValues.createCell(4);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Distance");
        Cell cellSrNo4 = rowValues.createCell(5);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Logout");
        Cell cellSrNo5 = rowValues.createCell(6);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("Distance");
        Cell cellSrNo6 = rowValues.createCell(7);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Status");


    }

    private void getReceivingDataIndividual(String date) {
        Log.e("DateToday", date);
        Log.e("SiteId436",String.valueOf(siteId));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiving_amt = 0;
                receiving_by = "";
                expense_amt = 0;

                Log.e("ModelAssociate123", date);

                if (snapshot.exists()) {
                    Log.e("ModelAssociate123", "SnapshotExist");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelAssociateDetails modelAssociateDetails = ds.getValue(ModelAssociateDetails.class);
                        Log.e("Model123", "" + (modelAssociateDetails.getStatus() == null));
                        Log.e("Model123", "" + (modelAssociateDetails.getStatus()));
                        modelAssociateDetails.setDate(date);
//                        Log.e("AssDet",modelAssociateDetails.getTime());
//                        Log.e("AssDet",modelAssociateDetails.getDate());
                        if (siteLatitude!=null &&siteLatitude > 0 && siteLongitude!=null && siteLongitude > 0) {
                            modelAssociateDetails.setSiteLatitude(siteLatitude);
                            modelAssociateDetails.setSiteLongitude(siteLongitude);
                        }
                        associateDetailsArrayList.add(modelAssociateDetails);


//                        if(modelAssociateDetails.getUid()!=null){
//                            DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
//                            reference1.child(modelAssociateDetails.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    modelAssociateDetails.setName(snapshot.child("name").getValue(String.class));
//                                    associateDetailsArrayList.add(modelAssociateDetails);
//                                    Log.e("AssDet","1"+snapshot.child("name").getValue(String.class));
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        }else{
//                            associateDetailsArrayList.add(modelAssociateDetails);
//                            Log.e("AssDet","2"+associateDetailsArrayList.size());
//                        }

                    }

                }
//                else {
//                    ModelAssociateDetails modelAssociateDetails = new ModelAssociateDetails(0.0, 0.0, "", "", "", "NA", "", "", false);
//                    modelAssociateDetails.setDate(date);
//                    associateDetailsArrayList.add(modelAssociateDetails);
//
//                }
                Log.e("TopDtae", date);
                Log.e("TopDtae", "Size:"+associateDetailsArrayList.size());
                if (binding.rbCustom.isChecked()) {
                    AdapterAssociateDetails adapterAssociateDetails = new AdapterAssociateDetails(AssociateDetailsActivity.this, associateDetailsArrayList);
                    binding.rvDayBook.setAdapter(adapterAssociateDetails);
                    binding.rvDayBook.setVisibility(View.VISIBLE);
//                    Log.e("AssDet","3"+associateDetailsArrayList.size());
//                    Log.e("ModelAssociate1234","ModelAssociateDetails");
//                    Log.e("ModelAssociate1234","Size:"+associateDetailsArrayList.size());

//                    for (int i = 0; i < associateDetailsArrayList.size(); i++) {
////                Log.e("ModelAssociateDetails",associateDetailsArrayList.get(i).getName());
//                        Log.e("ModelAssociate12345", associateDetailsArrayList.get(i).getDate());
//                        Log.e("ModelAssociate1234", associateDetailsArrayList.get(i).getStatus());
//                        Log.e("ModelAssociate1234", associateDetailsArrayList.get(i).getTimeStamp());
//                        Log.e("ModelAssociate1234", "SL" + associateDetailsArrayList.get(i).getSiteLatitude());
//                        Log.e("ModelAssociate1234", "SO" + associateDetailsArrayList.get(i).getSiteLongitude());
//                    }

                } else if (binding.rbToday.isChecked()) {
                    AdapterAssociateDetails adapterAssociateDetails = new AdapterAssociateDetails(AssociateDetailsActivity.this, associateDetailsArrayList);
                    binding.rvDayBook.setAdapter(adapterAssociateDetails);
                    binding.rvDayBook.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void getExpenseDataIndividual(String date, String s, String s1) {
//        Log.e("Array", "Site;;;;;" + siteId);
//        Log.e("Array", "Date;;;;;" + date);
//        paymentArrayList.clear();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Payments").child(date).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                expense_amt = 0;
//                String expense_to = "";
//                if (snapshot.exists()) {
//
//                    for (DataSnapshot ds : snapshot.getChildren()) {
//
//                        ModelPayment modelExpense = ds.getValue(ModelPayment.class);
//                        expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
//                        paymentArrayList.add(modelExpense);
//
//
//                    }
//                    Log.e("Array", "Snap::" + snapshot.getChildrenCount());
//                    if (snapshot.getChildrenCount() > 1) {
//
//                        ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, snapshot.getChildrenCount(), String.valueOf(expense_amt), "Multiple");
//                        addToArray(modelDayBookClass);
//
//                    } else {
//                        ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, snapshot.getChildrenCount(), String.valueOf(expense_amt), paymentArrayList.get(0).getLabourName());
//                        addToArray(modelDayBookClass);
//                    }
//
//
//                } else {
//                    ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, 0, "0", "N/A");
//                    addToArray(modelDayBookClass);
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void showDownloadSiteDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(AssociateDetailsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_download_spinner_dialog, null);
        rv_site_select = (RecyclerView) mView.findViewById(R.id.rv_site_select);
        Button btn_ok = (Button) mView.findViewById(R.id.btn_ok);
        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        Log.e("GetSiteList", "" + siteAdminList.size());
        rv_site_select.setAdapter(adapterSiteSelect);
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
                    binding.llSelectPeriod.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(AssociateDetailsActivity.this, "Select Atleast one site to download report", Toast.LENGTH_SHORT).show();
                }


            }
        });


        alertDialog.show();

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

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


        }


    };

//    private void getExpenseData(long siteId) {
//        Log.e("SiteId", String.valueOf(siteId));
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Cash").child("Expense").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                expenseArrayList.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    ModelExpense modelExpense = ds.getValue(ModelExpense.class);
//                    expenseArrayList.add(modelExpense);
//
//                }
//                Log.e("ExpenseArrayList", "" + expenseArrayList.size());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


    private void getSiteMemberStatus(long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("memberStatus").getValue(String.class) != null) {
                    if (snapshot.child("memberStatus").getValue(String.class).equals("Registered")) {
                        getReceivingData(modelDateArrayList);
                    }
                }
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
            associateDetailsArrayList.clear();
            for (int i = 0; i < modelDateArrayList.size(); i++) {
                Log.e("Date456",modelDateArrayList.get(i).getDate());

                getReceivingDataIndividual(modelDateArrayList.get(i).getDate());
            }


        }
    }


//    private void getLabourPayment(String date, String s, String s1, long childrenCount, String valueOf) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Payments").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String expense_to = snapshot.child("labourName").getValue(String.class);
//                ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, snapshot.getChildrenCount(), String.valueOf(expense_amt), expense_to);
//                addToArray(modelDayBookClass);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void addToArray(ModelDayBookClass modelDayBookClass) {
//        modelDayBookClassArrayList.add(modelDayBookClass);
//        if (modelDayBookClassArrayList.size() == modelDateArrayList.size()) {
//            Log.e("Array", "" + selected_option);
//            if (selected_option == 2) {
////                binding.receivedAmountHeading.setVisibility(View.GONE);
////                binding.receivedFromHeading.setVisibility(View.GONE);
////                binding.closingBalanceHeading.setVisibility(View.GONE);
//                binding.llDayBookHeading.setWeightSum(3);
////                AdapterDayBook adapterDayBook = new AdapterDayBook(AssociateDetailsActivity.this, modelDayBookClassArrayList, selected_option);
////                binding.rvDayBook.setAdapter(adapterDayBook);
////                DownloadExcel(modelDayBookClassArrayList);
//            } else if (selected_option == 1) {
////                AdapterDayBook adapterDayBook = new AdapterDayBook(AssociateDetailsActivity.this, modelDayBookClassArrayList, selected_option);
////                binding.rvDayBook.setAdapter(adapterDayBook);
////                DownloadExcel(modelDayBookClassArrayList);
//            } else if (selected_option == 3) {
//                getSiteMemberStatusFinal(siteId, modelDayBookClassArrayList);
////                DownloadExcel(modelDayBookClassArrayList);
//            } else if (userType.equals("Supervisor")) {
//                getSiteMemberStatusFinal(siteId, modelDayBookClassArrayList);
//            }

//            for(int i=0;i<modelDayBookClassArrayList.size();i++){
//                Log.e("Array",modelDayBookClassArrayList.get(i).getDate()+"\t"+
//                        modelDayBookClassArrayList.get(i).getRecAmt()+"\t"+modelDayBookClassArrayList.get(i).getRec_from()+
//                        "\t"+modelDayBookClassArrayList.get(i).getExpAmt()+"\t"+modelDayBookClassArrayList.get(i).getCount());
//            }
//        }
    }


//    private void getSiteMemberStatusFinal(long siteId, ArrayList<ModelDayBookClass> modelDayBookClassArrayList) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.e("Size1234", "" + modelDayBookClassArrayList.size());
//                if (snapshot.child("memberStatus").getValue(String.class) != null) {
//                    if (snapshot.child("memberStatus").getValue(String.class).equals("Registered")) {
////                        binding.receivedAmountHeading.setVisibility(View.VISIBLE);
////                        binding.receivedFromHeading.setVisibility(View.VISIBLE);
////                        binding.closingBalanceHeading.setVisibility(View.VISIBLE);
//                        binding.llDayBookHeading.setWeightSum(6);
//                        AdapterDayBook adapterDayBook = new AdapterDayBook(AssociateDetailsActivity.this, modelDayBookClassArrayList, selected_option, snapshot.child("memberStatus").getValue(String.class), siteId);
//                        binding.rvDayBook.setAdapter(adapterDayBook);
//                    } else {
////                        binding.receivedAmountHeading.setVisibility(View.GONE);
////                        binding.receivedFromHeading.setVisibility(View.GONE);
////                        binding.closingBalanceHeading.setVisibility(View.GONE);
//                        binding.llDayBookHeading.setWeightSum(3);
//
//                        AdapterDayBook adapterDayBook = new AdapterDayBook(AssociateDetailsActivity.this, modelDayBookClassArrayList, selected_option, snapshot.child("memberStatus").getValue(String.class), siteId);
//                        binding.rvDayBook.setAdapter(adapterDayBook);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void getReceivingData(ArrayList<ModelDate> modelDateArrayList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Receive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelReceiveCash modelReceiveCash = ds.getValue(ModelReceiveCash.class);
                    receiveCashArrayList.add(modelReceiveCash);
                }
                Log.e("ReceiveCashArrayList", "" + receiveCashArrayList.size());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLabel(EditText fromdateEt) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fromdateEt.setText(sdf.format(myCalendar.getTime()));
    }

    private void getSiteCreatedDate(String complete) {
        Log.e("siteId", "" + siteId);
        if (siteId > 0) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    siteCreatedDate = snapshot.child("siteCreatedDate").getValue(String.class);
                    siteLatitude = snapshot.child("siteLatitude").getValue(double.class);
                    siteLongitude = snapshot.child("siteLongitude").getValue(double.class);
                    Log.e("siteCreatedDate", siteCreatedDate);
                    Log.e("siteCreatedDate", currentDate);
                    Calendar c12 = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date siteCreated = null;
                    SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    Date date = null, date1 = null;
                    String str = null, str1 = null;
                    try {
                        date = df1.parse(siteCreatedDate);// it's format should be same as inputPattern
                        str = sdf.format(date);
                        date1 = df1.parse(currentDate);// it's format should be same as inputPattern
                        str1 = sdf.format(date1);
                        Log.e("Log ", "str " + str + "str1" + str1);
//                        c12.setTime(sdf.parse(siteCreatedDate));
                    } catch (ParseException e) {
                        Log.e("Exc", e.getMessage());
                        e.printStackTrace();
                    }


                    String currentDate_ddmmyyyy = currentDate;
                    currentDate_ddmmyyyy = df1.format(c);
                    try {
                        if (siteCreatedDate != null) {
                            Log.e("SiteCreateSend", "Site: " + str + "Current: " + str1);
                            getDateRange(str, str1);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteAdminList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    if (modelSite.getMemberStatus().equals("Registered")) {
                        modelSite.setSelected(false);
                        siteAdminList.add(modelSite);
                    }


                }
                Log.e("siteAdminList", "" + siteAdminList.size());
                if (status.equals("ShowAttendance")) {
                    siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0));
                } else {
                    siteAdminList.add(0, new ModelSite("All Site", 0, false));
                }

                AssociateDetailsActivity.SiteSpinnerAdapter siteSpinnerAdapter = new AssociateDetailsActivity.SiteSpinnerAdapter();
                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                Intent intent = getIntent();
                spinnerPosition = intent.getIntExtra("position", -1);
                Log.e("SpinnerPosition", "" + spinnerPosition);
                if (spinnerPosition > -1) {
                    binding.spinnerSelectSite.setSelection(spinnerPosition);
                }
                siteId = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId();
                siteName = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteCity();
                ;
                binding.llSelectSite.setVisibility(View.VISIBLE);
                adapterSiteSelect = new AdapterSiteSelect(AssociateDetailsActivity.this, siteAdminList);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class SiteSpinnerAdapter
            extends BaseAdapter {
        SiteSpinnerAdapter() {
        }

        public int getCount() {
            return siteAdminList.size();
        }

        public Object getItem(int n) {
            return null;
        }

        public long getItemId(int n) {
            return 0L;
        }

        public View getView(int n, View view, ViewGroup viewGroup) {
            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
            TextView textView = (TextView) view2.findViewById(R.id.spinner_text);
            textView.setText(siteAdminList.get(n).getSiteCity());
            return view2;
        }
    }
}