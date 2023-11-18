package com.skillzoomer_Attendance.com.Activity;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.skillzoomer_Attendance.com.Adapter.AdapterDayBook;
import com.skillzoomer_Attendance.com.Adapter.AdapterSiteSelect;
import com.skillzoomer_Attendance.com.Model.DateModel;
import com.skillzoomer_Attendance.com.Model.ModelAttendance;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelDayBookClass;
import com.skillzoomer_Attendance.com.Model.ModelError;
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
import com.skillzoomer_Attendance.com.databinding.ActivityDayBookListBinding;

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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DayBookListActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    private final Calendar myCalendar = Calendar.getInstance();
    ActivityDayBookListBinding binding;
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
    XSSFWorkbook workbook1 = null;
    long AmountRuleExcel = 0, AmountRulePdf = 0;
    int amount = 0;
    int AmountTemp = 0;
    private String dnl_file_type = "";
    AlertDialog alertDialogPaymentConfirm = null;
    private ArrayList<ModelError> errorArrayList;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDayBookListBinding.inflate(getLayoutInflater());
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        siteAdminList = new ArrayList<>();
        modelDateArrayList = new ArrayList<>();
        receiveCashArrayList = new ArrayList<>();
        expenseArrayList = new ArrayList<>();
        modelDayBookClassArrayList = new ArrayList<>();
        binding.llDayBookHeading.setVisibility(View.GONE);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType = sharedpreferences.getString("userDesignation", "");

        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        userName = sharedpreferences.getString("userName", "");
        selected_option = sharedpreferences.getInt("workOption", 0);
        errorArrayList = new ArrayList<>();
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

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_position"));

        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                siteId = siteAdminList.get(i).getSiteId();
                siteName = siteAdminList.get(i).getSiteName();
                Log.e("SiteId", "Spinner" + siteId);
                if (binding.rbComplete.isChecked()) {
                    getSiteCreatedDate();
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
                            Toast.makeText(DayBookListActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                            f = false;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (fromDate.equals("")) {
                        Toast.makeText(DayBookListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                    } else if (toDate.equals("")) {
                        Toast.makeText(DayBookListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                    } else if (f) {
                        try {
                            getDateRange(fromDate, toDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                    getLabourList(fromDate, toDate,"Custom");
                    }
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
                    getSiteCreatedDate();


                }
            }
        });

        binding.rbToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (binding.rbToday.isChecked()) {
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
                    try {
                        getDateRange(currentDate11, currentDate11);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        binding.rbCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
                        Toast.makeText(DayBookListActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(DayBookListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(DayBookListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
                reference.child("DayBook").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                        AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final AlertDialog.Builder alert = new AlertDialog.Builder(DayBookListActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.show_file_type, null);
                if (binding.rbComplete.isChecked()) {
                    modelDateArrayList = new ArrayList<>();
                    shortDateList = new ArrayList<>();
//                    Log.e("siteCreatedDate", siteCreatedDate);
//                    Log.e("siteCreatedDate", currentDate);
                    Calendar c12 = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date siteCreated = null;
                    SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    Date date = null, date1 = null;
                    String str = null, str1 = null;

//

                }
                else if (binding.rbToday.isChecked()) {
                    modelDayBookClassArrayList.clear();
                    modelDateArrayList.clear();
                    modelDateArrayList.add(new ModelDate(currentDate));


                }
                else if (binding.rbCustom.isChecked()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Boolean f = true;
                    try {

                        Date fDate = dateFormat.parse(fromDate);
                        Date tDate = dateFormat.parse(toDate);
                        if (tDate.before(fDate)) {
                            Toast.makeText(DayBookListActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                            f = false;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (fromDate.equals("")) {
                        Toast.makeText(DayBookListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                    } else if (toDate.equals("")) {
                        Toast.makeText(DayBookListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
                                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yy", Locale.US);
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
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

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
                            if (binding.rbComplete.isChecked()) {
                                modelDateArrayList = new ArrayList<>();
                                shortDateList = new ArrayList<>();
//                                Log.e("siteCreatedDate", siteCreatedDate);
//                                Log.e("siteCreatedDate", currentDate);
                                Calendar c12 = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date siteCreated = null;
                                SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                Date date = null, date1 = null;
                                String str = null, str1 = null;
                                amount = (int) AmountRulePdf;

//

                            } else if (binding.rbToday.isChecked()) {
                                modelDayBookClassArrayList.clear();
                                modelDateArrayList.clear();
                                modelDateArrayList.add(new ModelDate(currentDate));
                                amount = 3 * 1;


                            } else if (binding.rbCustom.isChecked()) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Boolean f = true;
                                try {

                                    Date fDate = dateFormat.parse(fromDate);
                                    Date tDate = dateFormat.parse(toDate);
                                    if (tDate.before(fDate)) {
                                        Toast.makeText(DayBookListActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                                        f = false;
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (fromDate.equals("")) {
                                    Toast.makeText(DayBookListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                                } else if (toDate.equals("")) {
                                    Toast.makeText(DayBookListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
                                                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yy", Locale.US);
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

                                            amount = 3 * modelDateArrayList.size();


                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
//                            amount = Math.round((modelDateArrayList.size() * 5));
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
                            if (binding.rbComplete.isChecked()) {
                                modelDateArrayList = new ArrayList<>();
                                shortDateList = new ArrayList<>();
//                                Log.e("siteCreatedDate", siteCreatedDate);
//                                Log.e("siteCreatedDate", currentDate);
                                Calendar c12 = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date siteCreated = null;
                                SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                Date date = null, date1 = null;
                                String str = null, str1 = null;
                                amount = (int) AmountRuleExcel;

//

                            } else if (binding.rbToday.isChecked()) {
                                modelDayBookClassArrayList.clear();
                                modelDateArrayList.clear();
                                modelDateArrayList.add(new ModelDate(currentDate));
                                amount = 5;


                            } else if (binding.rbCustom.isChecked()) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Boolean f = true;
                                try {

                                    Date fDate = dateFormat.parse(fromDate);
                                    Date tDate = dateFormat.parse(toDate);
                                    if (tDate.before(fDate)) {
                                        Toast.makeText(DayBookListActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                                        f = false;
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (fromDate.equals("")) {
                                    Toast.makeText(DayBookListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                                } else if (toDate.equals("")) {
                                    Toast.makeText(DayBookListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
                                                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yy", Locale.US);
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

                                            amount = 5 * modelDateArrayList.size();


                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
//                            amount = Math.round((modelDateArrayList.size() * 10));
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
                            Toast.makeText(DayBookListActivity.this, "Select file type", Toast.LENGTH_SHORT).show();
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

//        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//
//                reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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
//                                        String siteName1 = siteAdminList.get(i).getSiteName();
//                                        if (binding.rbComplete.isChecked()) {
//                                            modelDateArrayList = new ArrayList<>();
//                                            shortDateList = new ArrayList<>();
//                                            siteCreatedDate = modelSite.getSiteCreatedDate();
//                                            Log.e("siteCreated", siteCreatedDate);
////                                    Log.e("siteCreatedDate", siteCreatedDate);
////                                    Log.e("siteCreatedDate", currentDate);
//                                            Calendar c12 = Calendar.getInstance();
//                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                                            Date siteCreated = null;
//                                            SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//                                            Date date = null, date1 = null;
//                                            String str = null, str1 = null;
////
//                                            try {
//                                                date = df1.parse(siteCreatedDate);// it's format should be same as inputPattern
//                                                str = sdf.format(date);
//                                                date1 = df1.parse(currentDate);// it's format should be same as inputPattern
//                                                str1 = sdf.format(date1);
//                                                Log.e("Log ", "str " + str + "str1" + str1);
//                                                Calendar c = Calendar.getInstance();
//
//
////                                                c12.setTime(sdf.parse(currentDate));
//                                                int count = 0;
//                                                Date temp = date;
//                                                if (siteCreatedDate.equals(currentDate)) {
//                                                    c.add(Calendar.DATE, 1);
//                                                    temp = c.getTime();
//                                                }
//                                                Log.e("callTemp", "" + temp);
//                                                while (temp.before(date1)) {
//                                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//                                                    SimpleDateFormat df12 = new SimpleDateFormat("dd/MM/yy", Locale.US);
//                                                    String date11 = df.format(date);
//                                                    String date12 = df12.format(date);
//                                                    Log.e("ShreyaMamKaDate", date11);
//                                                    modelDateArrayList.add(new ModelDate(date11));
//                                                    shortDateList.add(new ModelDate(date12));
//                                                    Log.e("dateeeee1", modelDateArrayList.get(count).getDate());
//                                                    count++;
//                                                    Log.e("dateeee", date11);
//                                                    c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                                                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy");
//                                                    temp = c.getTime();
//                                                    Log.e("Temp", "" + count + ":" + temp);
//
//                                                }
//                                                modelDayBookClassArrayList.clear();
//                                                errorArrayList.clear();
//                                                for (int m = 0; m < modelDateArrayList.size(); m++) {
//                                                    ModelDayBookClass modelDayBookClass = new ModelDayBookClass();
//                                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).exists()) {
//                                                        receiving_amt = 0;
//                                                        receiving_by = "";
//                                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildren()) {
//                                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
//                                                                ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
//                                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
//                                                                receiving_by = "Multiple";
//                                                            } else if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() == 1) {
//                                                                ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
//                                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
//                                                                receiving_by = modelReceiveCash.getRecFrom();
//                                                            }
//                                                        }
//                                                        modelDayBookClass.setDate(shortDateList.get(m).getDate());
//                                                        modelDayBookClass.setRecAmt(String.valueOf(receiving_amt));
//                                                        modelDayBookClass.setRec_from(receiving_by);
//
//                                                    } else {
//                                                        modelDayBookClass.setDate(shortDateList.get(m).getDate());
//                                                        modelDayBookClass.setRecAmt("0");
//                                                        modelDayBookClass.setRec_from("NR");
//                                                    }
//                                                    paymentArrayList.clear();
//                                                    if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).exists()) {
//
//                                                        for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()) {
//                                                            ModelPayment modelExpense = ds2.getValue(ModelPayment.class);
//                                                            expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
//                                                            paymentArrayList.add(modelExpense);
//
//
//                                                        }
//                                                        if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
//                                                            modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
//                                                            modelDayBookClass.setExpRemark("Multiple");
//                                                        } else {
//                                                            modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
//                                                            modelDayBookClass.setExpRemark(paymentArrayList.get(0).getLabourName());
//                                                        }
//
//
//                                                    } else {
//                                                        modelDayBookClass.setExpAmt("0");
//                                                        modelDayBookClass.setExpRemark("NR");
//                                                    }
//
//                                                    modelDayBookClassArrayList.add(modelDayBookClass);
//
//
//                                                }
//
////                                                DownloadExcel(modelDayBookClassArrayList,modelDateArrayList,siteId1,siteName1);
//
//
//                                            } catch (ParseException e) {
//                                                Log.e("Exc", e.getMessage());
//                                                e.printStackTrace();
//                                            }
//                                        } else if (binding.rbToday.isChecked()) {
//                                            modelDayBookClassArrayList.clear();
//                                            modelDateArrayList.clear();
//                                            modelDateArrayList.add(new ModelDate(currentDate));
//                                            ModelDayBookClass modelDayBookClass = new ModelDayBookClass();
//                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).exists()) {
//                                                receiving_amt = 0;
//                                                receiving_by = "";
//                                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).getChildren()) {
//                                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).getChildrenCount() > 1) {
//                                                        ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
//                                                        receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
//                                                        receiving_by = "Multiple";
//                                                    } else if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).getChildrenCount() == 1) {
//                                                        ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
//                                                        receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
//                                                        receiving_by = modelReceiveCash.getRecFrom();
//                                                    }
//                                                }
//                                                modelDayBookClass.setDate(currentDate);
//                                                modelDayBookClass.setRecAmt(String.valueOf(receiving_amt));
//                                                modelDayBookClass.setRec_from(receiving_by);
//
//                                            } else {
//                                                modelDayBookClass.setDate(currentDate);
//                                                modelDayBookClass.setRecAmt("0");
//                                                modelDayBookClass.setRec_from("NR");
//                                            }
//                                            paymentArrayList.clear();
//                                            if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).exists()) {
//
//                                                for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildren()) {
//                                                    ModelPayment modelExpense = ds2.getValue(ModelPayment.class);
//                                                    expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
//                                                    paymentArrayList.add(modelExpense);
//
//
//                                                }
//                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount() > 1) {
//                                                    modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
//                                                    modelDayBookClass.setExpRemark("Multiple");
//                                                } else {
//                                                    modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
//                                                    modelDayBookClass.setExpRemark(paymentArrayList.get(0).getLabourName());
//                                                }
//
//
//                                            } else {
//                                                modelDayBookClass.setExpAmt("0");
//                                                modelDayBookClass.setExpRemark("NR");
//                                            }
//
//                                            modelDayBookClassArrayList.add(modelDayBookClass);
//                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Error").child(currentDate).exists()) {
//                                                for (DataSnapshot ds1 : snapshot.getChildren()) {
//                                                    ModelError modelError = ds1.getValue(ModelError.class);
//                                                    errorArrayList.add(modelError);
//
//                                                }
//                                            }
////                                            DownloadExcel(modelDayBookClassArrayList,modelDateArrayList,siteId1,siteName1);
//
//                                        } else if (binding.rbCustom.isChecked()) {
//                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                                            Boolean f = true;
//                                            try {
//
//                                                Date fDate = dateFormat.parse(fromDate);
//                                                Date tDate = dateFormat.parse(toDate);
//                                                if (tDate.before(fDate)) {
//                                                    Toast.makeText(DayBookListActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
//                                                    f = false;
//                                                }
//
//                                            } catch (ParseException e) {
//                                                e.printStackTrace();
//                                            }
//                                            if (fromDate.equals("")) {
//                                                Toast.makeText(DayBookListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
//                                            } else if (toDate.equals("")) {
//                                                Toast.makeText(DayBookListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
//                                                            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yy", Locale.US);
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
//                                                        modelDayBookClassArrayList.clear();
//                                                        ArrayList<ModelReceiveCash> modelReceiveCashArrayList = new ArrayList<>();
//                                                        ArrayList<ModelExpense> expenseArrayList = new ArrayList<>();
//
//                                                        for (int m = 0; m < modelDateArrayList.size(); m++) {
//                                                            ModelDayBookClass modelDayBookClass = new ModelDayBookClass();
//                                                            modelReceiveCashArrayList.clear();
//                                                            expenseArrayList.clear();
//
//                                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).exists()) {
//                                                                receiving_amt = 0;
//                                                                receiving_by = "";
//                                                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildren()) {
//                                                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
//                                                                        ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
//                                                                        modelReceiveCashArrayList.add(modelReceiveCash);
////                                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
////                                                                receiving_by = "Multiple";
//
//                                                                    } else if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() == 1) {
//                                                                        ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
////                                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
////                                                                receiving_by = modelReceiveCash.getRecFrom();
//                                                                        modelReceiveCashArrayList.add(modelReceiveCash);
//                                                                    }
//                                                                }
////                                                        modelDayBookClass.setDate(shortDateList.get(m).getDate());
////                                                        modelDayBookClass.setRecAmt(String.valueOf(receiving_amt));
////                                                        modelDayBookClass.setRec_from(receiving_by);
//
//                                                            } else {
//                                                                modelDayBookClass.setDate(shortDateList.get(m).getDate());
//                                                                modelDayBookClass.setRecAmt("0");
//                                                                modelDayBookClass.setRec_from("NR");
//                                                            }
//                                                            paymentArrayList.clear();
//                                                            expense_amt = 0;
//                                                            if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).exists()) {
//
//                                                                for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()) {
//                                                                    ModelPayment modelExpense = ds2.getValue(ModelPayment.class);
//                                                                    expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
//                                                                    paymentArrayList.add(modelExpense);
//
//
//                                                                }
//                                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
//                                                                    modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
//                                                                    modelDayBookClass.setExpRemark("Multiple");
//                                                                } else {
//                                                                    modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
//                                                                    modelDayBookClass.setExpRemark(paymentArrayList.get(0).getLabourName());
//                                                                }
//
//
//                                                            } else {
//                                                                modelDayBookClass.setExpAmt("0");
//                                                                modelDayBookClass.setExpRemark("NR");
//                                                                modelDayBookClassArrayList.add(modelDayBookClass);
//                                                            }
//                                                            if (receiveCashArrayList.size() > paymentArrayList.size()) {
//                                                                int diff = receiveCashArrayList.size() - paymentArrayList.size();
//                                                                for (int k = 0; k < diff; k++) {
//                                                                    ModelPayment modelExpense = new ModelPayment();
//                                                                    modelExpense.setAmount("0");
//                                                                    modelExpense.setLabourName("NR");
//                                                                    modelExpense.setUploadedByName("NR");
//                                                                    paymentArrayList.add(modelExpense);
//                                                                }
//                                                            } else if (receiveCashArrayList.size() < paymentArrayList.size()) {
//                                                                int diff = paymentArrayList.size() - receiveCashArrayList.size();
//                                                                for (int k = 0; k < diff; k++) {
//                                                                    ModelReceiveCash modelReceiveCash = new ModelReceiveCash();
//                                                                    modelReceiveCash.setAmount("0");
//                                                                    modelReceiveCash.setRecFrom("NR");
//                                                                    receiveCashArrayList.add(modelReceiveCash);
//                                                                }
//                                                            }
//                                                            if (receiveCashArrayList.size() == paymentArrayList.size() && receiveCashArrayList.size() > 0) {
//                                                                for (int d = 0; d < receiveCashArrayList.size(); d++) {
//                                                                    modelDayBookClass.setDate(shortDateList.get(m).getDate());
//                                                                    modelDayBookClass.setRecAmt(String.valueOf(receiveCashArrayList.get(i).getAmount()));
//                                                                    modelDayBookClass.setRec_from(receiveCashArrayList.get(i).getRecFrom());
//                                                                    modelDayBookClass.setExpAmt(String.valueOf(paymentArrayList.get(i).getAmount()));
//                                                                    modelDayBookClass.setExpRemark(paymentArrayList.get(i).getLabourName());
//                                                                    modelDayBookClass.setExpDoneBy(paymentArrayList.get(i).getUploadedByName());
//                                                                    modelDayBookClassArrayList.add(modelDayBookClass);
//                                                                }
//                                                            }
//
//
//                                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Error").child(modelDateArrayList.get(m).getDate()).exists()) {
//                                                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Error").child(modelDateArrayList.get(m).getDate()).getChildren()) {
//                                                                    ModelError modelError = ds1.getValue(ModelError.class);
//                                                                    errorArrayList.add(modelError);
//
//                                                                }
//                                                            }
//                                                        }
//
////                                                       DownloadExcel(modelDayBookClassArrayList,modelDateArrayList,siteId1,siteName1);
//
//
//                                                    }
//                                                } catch (ParseException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//                                        DownloadExcel(modelDayBookClassArrayList, modelDateArrayList, siteId1, siteName1, errorArrayList);
//                                    }
//                                }
//                            }
//
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(DayBookListActivity.this);
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
        Log.e("Dnl_file", dnl_file_type);
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
        txt_file_type.setText("Day Book Report");
        if (binding.rbComplete.isChecked()) {
            txt_from.setText("Complete Record");
            txt_to.setText("Complete Record");
        } else if (binding.rbCustom.isChecked()) {
            txt_from.setText(modelDateArrayList.get(0).getDate());
            txt_to.setText(modelDateArrayList.get(modelDateArrayList.size() - 1).getDate());
        } else if (binding.rbToday.isChecked()) {
            txt_from.setText("Current Date");
            txt_to.setText("Current Date");
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

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                String siteName1 = siteAdminList.get(i).getSiteName();
                                if (binding.rbComplete.isChecked()) {
                                    modelDateArrayList = new ArrayList<>();
                                    shortDateList = new ArrayList<>();
                                    siteCreatedDate = modelSite.getSiteCreatedDate();
                                    Log.e("siteCreated", siteCreatedDate);
//                                    Log.e("siteCreatedDate", siteCreatedDate);
//                                    Log.e("siteCreatedDate", currentDate);
                                    Calendar c12 = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    Date siteCreated = null;
                                    SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                    Date date = null, date1 = null;
                                    String str = null, str1 = null;
//
                                    try {
                                        date = df1.parse(siteCreatedDate);// it's format should be same as inputPattern
                                        str = sdf.format(date);
                                        date1 = df1.parse(currentDate);// it's format should be same as inputPattern
                                        str1 = sdf.format(date1);
                                        Log.e("Log ", "str " + str + "str1" + str1);
                                        Calendar c = Calendar.getInstance();


//                                                c12.setTime(sdf.parse(currentDate));
                                        int count = 0;
                                        Date temp = date;
                                        if (siteCreatedDate.equals(currentDate)) {
                                            c.add(Calendar.DATE, 1);
                                            temp = c.getTime();
                                        }
                                        Log.e("callTemp", "" + temp);
                                        while (temp.before(date1)) {
                                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                            SimpleDateFormat df12 = new SimpleDateFormat("dd/MM/yy", Locale.US);
                                            String date11 = df.format(date);
                                            String date12 = df12.format(date);
                                            Log.e("ShreyaMamKaDate", date11);
                                            modelDateArrayList.add(new ModelDate(date11));
                                            shortDateList.add(new ModelDate(date12));
                                            Log.e("dateeeee1", modelDateArrayList.get(count).getDate());
                                            count++;
                                            Log.e("dateeee", date11);
                                            c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy");
                                            temp = c.getTime();
                                            Log.e("Temp", "" + count + ":" + temp);

                                        }
                                        modelDayBookClassArrayList.clear();
                                        errorArrayList.clear();
                                        for (int m = 0; m < modelDateArrayList.size(); m++) {
                                            ModelDayBookClass modelDayBookClass = new ModelDayBookClass();
                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).exists()) {
                                                receiving_amt = 0;
                                                receiving_by = "";
                                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildren()) {
                                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
                                                        ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
                                                        receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
                                                        receiving_by = "Multiple";
                                                    } else if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() == 1) {
                                                        ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
                                                        receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
                                                        receiving_by = modelReceiveCash.getRecFrom();
                                                    }
                                                }
                                                modelDayBookClass.setDate(shortDateList.get(m).getDate());
                                                modelDayBookClass.setRecAmt(String.valueOf(receiving_amt));
                                                modelDayBookClass.setRec_from(receiving_by);

                                            } else {
                                                modelDayBookClass.setDate(shortDateList.get(m).getDate());
                                                modelDayBookClass.setRecAmt("0");
                                                modelDayBookClass.setRec_from("NR");
                                            }
                                            paymentArrayList.clear();
                                            if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).exists()) {

                                                for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()) {
                                                    ModelPayment modelExpense = ds2.getValue(ModelPayment.class);
                                                    expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
                                                    paymentArrayList.add(modelExpense);


                                                }
                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
                                                    modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
                                                    modelDayBookClass.setExpRemark("Multiple");
                                                } else {
                                                    modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
                                                    modelDayBookClass.setExpRemark(paymentArrayList.get(0).getLabourName());
                                                }


                                            } else {
                                                modelDayBookClass.setExpAmt("0");
                                                modelDayBookClass.setExpRemark("NR");
                                            }

                                            modelDayBookClassArrayList.add(modelDayBookClass);


                                        }

//                                                DownloadExcel(modelDayBookClassArrayList,modelDateArrayList,siteId1,siteName1);


                                    } catch (ParseException e) {
                                        Log.e("Exc", e.getMessage());
                                        e.printStackTrace();
                                    }
                                } else if (binding.rbToday.isChecked()) {
                                    modelDayBookClassArrayList.clear();
                                    modelDateArrayList.clear();
                                    modelDateArrayList.add(new ModelDate(currentDate));
                                    ModelDayBookClass modelDayBookClass = new ModelDayBookClass();
                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).exists()) {
                                        receiving_amt = 0;
                                        receiving_by = "";
                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).getChildren()) {
                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).getChildrenCount() > 1) {
                                                ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
                                                receiving_by = "Multiple";
                                            } else if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(currentDate).getChildrenCount() == 1) {
                                                ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
                                                receiving_by = modelReceiveCash.getRecFrom();
                                            }
                                        }
                                        modelDayBookClass.setDate(currentDate);
                                        modelDayBookClass.setRecAmt(String.valueOf(receiving_amt));
                                        modelDayBookClass.setRec_from(receiving_by);

                                    } else {
                                        modelDayBookClass.setDate(currentDate);
                                        modelDayBookClass.setRecAmt("0");
                                        modelDayBookClass.setRec_from("NR");
                                    }
                                    paymentArrayList.clear();
                                    if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).exists()) {

                                        for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildren()) {
                                            ModelPayment modelExpense = ds2.getValue(ModelPayment.class);
                                            expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
                                            paymentArrayList.add(modelExpense);


                                        }
                                        if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount() > 1) {
                                            modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
                                            modelDayBookClass.setExpRemark("Multiple");
                                        } else {
                                            modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
                                            modelDayBookClass.setExpRemark(paymentArrayList.get(0).getLabourName());
                                        }


                                    } else {
                                        modelDayBookClass.setExpAmt("0");
                                        modelDayBookClass.setExpRemark("NR");
                                    }

                                    modelDayBookClassArrayList.add(modelDayBookClass);
                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Error").child(currentDate).exists()) {
                                        for (DataSnapshot ds1 : snapshot.getChildren()) {
                                            ModelError modelError = ds1.getValue(ModelError.class);
                                            errorArrayList.add(modelError);

                                        }
                                    }
//                                            DownloadExcel(modelDayBookClassArrayList,modelDateArrayList,siteId1,siteName1);

                                } else if (binding.rbCustom.isChecked()) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    Boolean f = true;
                                    try {

                                        Date fDate = dateFormat.parse(fromDate);
                                        Date tDate = dateFormat.parse(toDate);
                                        if (tDate.before(fDate)) {
                                            Toast.makeText(DayBookListActivity.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                                            f = false;
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (fromDate.equals("")) {
                                        Toast.makeText(DayBookListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                                    } else if (toDate.equals("")) {
                                        Toast.makeText(DayBookListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
                                                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yy", Locale.US);
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
                                                modelDayBookClassArrayList.clear();
                                                ArrayList<ModelReceiveCash> modelReceiveCashArrayList = new ArrayList<>();
                                                ArrayList<ModelExpense> expenseArrayList = new ArrayList<>();

                                                for (int m = 0; m < modelDateArrayList.size(); m++) {
                                                    ModelDayBookClass modelDayBookClass = new ModelDayBookClass();
                                                    modelReceiveCashArrayList.clear();
                                                    expenseArrayList.clear();

                                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).exists()) {
                                                        receiving_amt = 0;
                                                        receiving_by = "";
                                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildren()) {
                                                            if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
                                                                ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
                                                                modelReceiveCashArrayList.add(modelReceiveCash);
//                                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
//                                                                receiving_by = "Multiple";

                                                            } else if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Receive").child(modelDateArrayList.get(m).getDate()).getChildrenCount() == 1) {
                                                                ModelReceiveCash modelReceiveCash = ds1.getValue(ModelReceiveCash.class);
//                                                                receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
//                                                                receiving_by = modelReceiveCash.getRecFrom();
                                                                modelReceiveCashArrayList.add(modelReceiveCash);
                                                            }
                                                        }
//                                                        modelDayBookClass.setDate(shortDateList.get(m).getDate());
//                                                        modelDayBookClass.setRecAmt(String.valueOf(receiving_amt));
//                                                        modelDayBookClass.setRec_from(receiving_by);

                                                    } else {
                                                        modelDayBookClass.setDate(shortDateList.get(m).getDate());
                                                        modelDayBookClass.setRecAmt("0");
                                                        modelDayBookClass.setRec_from("NR");
                                                    }
                                                    paymentArrayList.clear();
                                                    expense_amt = 0;
                                                    if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).exists()) {

                                                        for (DataSnapshot ds2 : snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()) {
                                                            ModelPayment modelExpense = ds2.getValue(ModelPayment.class);
                                                            expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
                                                            paymentArrayList.add(modelExpense);


                                                        }
                                                        if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 1) {
                                                            modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
                                                            modelDayBookClass.setExpRemark("Multiple");
                                                        } else {
                                                            modelDayBookClass.setExpAmt(String.valueOf(expense_amt));
                                                            modelDayBookClass.setExpRemark(paymentArrayList.get(0).getLabourName());
                                                        }


                                                    } else {
                                                        modelDayBookClass.setExpAmt("0");
                                                        modelDayBookClass.setExpRemark("NR");
                                                        modelDayBookClassArrayList.add(modelDayBookClass);
                                                    }
                                                    if (receiveCashArrayList.size() > paymentArrayList.size()) {
                                                        int diff = receiveCashArrayList.size() - paymentArrayList.size();
                                                        for (int k = 0; k < diff; k++) {
                                                            ModelPayment modelExpense = new ModelPayment();
                                                            modelExpense.setAmount("0");
                                                            modelExpense.setLabourName("NR");
                                                            modelExpense.setUploadedByName("NR");
                                                            paymentArrayList.add(modelExpense);
                                                        }
                                                    } else if (receiveCashArrayList.size() < paymentArrayList.size()) {
                                                        int diff = paymentArrayList.size() - receiveCashArrayList.size();
                                                        for (int k = 0; k < diff; k++) {
                                                            ModelReceiveCash modelReceiveCash = new ModelReceiveCash();
                                                            modelReceiveCash.setAmount("0");
                                                            modelReceiveCash.setRecFrom("NR");
                                                            receiveCashArrayList.add(modelReceiveCash);
                                                        }
                                                    }
                                                    if (receiveCashArrayList.size() == paymentArrayList.size() && receiveCashArrayList.size() > 0) {
                                                        for (int d = 0; d < receiveCashArrayList.size(); d++) {
                                                            modelDayBookClass.setDate(shortDateList.get(m).getDate());
                                                            modelDayBookClass.setRecAmt(String.valueOf(receiveCashArrayList.get(i).getAmount()));
                                                            modelDayBookClass.setRec_from(receiveCashArrayList.get(i).getRecFrom());
                                                            modelDayBookClass.setExpAmt(String.valueOf(paymentArrayList.get(i).getAmount()));
                                                            modelDayBookClass.setExpRemark(paymentArrayList.get(i).getLabourName());
                                                            modelDayBookClass.setExpDoneBy(paymentArrayList.get(i).getUploadedByName());
                                                            modelDayBookClassArrayList.add(modelDayBookClass);
                                                        }
                                                    }


                                                    if (snapshot.child(String.valueOf(siteId1)).child("Cash").child("Error").child(modelDateArrayList.get(m).getDate()).exists()) {
                                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Cash").child("Error").child(modelDateArrayList.get(m).getDate()).getChildren()) {
                                                            ModelError modelError = ds1.getValue(ModelError.class);
                                                            errorArrayList.add(modelError);

                                                        }
                                                    }
                                                }

//                                                       DownloadExcel(modelDayBookClassArrayList,modelDateArrayList,siteId1,siteName1);


                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                DownloadExcel(modelDayBookClassArrayList, modelDateArrayList, siteId1, siteName1, errorArrayList);
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(DayBookListActivity.this);
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
            txt_message.setText("Your DayBook Report PDF file is generated and stored in DOWNLOADS folder. ");
        } else {
            txt_message.setText("Your DayBook Report EXCEL+PDF file is generated and stored in DOWNLOADS folder. ");
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
                hashMap.put("downloadFile", "DayBook Report");
                hashMap.put("siteId", siteId);
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", dnl_file_type);
                hashMap.put("totalAmount", String.valueOf(amount));
                if (!binding.rbToday.isChecked()) {
                    hashMap.put("fromDate", fromDate);
                    hashMap.put("toDate", toDate);
                }


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData", paymentData.getData().toString());
                                Log.e("paymentData", paymentData.getPaymentId().toString());
//

                                Log.e("fos", "flush");
                                Toast.makeText(DayBookListActivity.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                Uri dirUri = FileProvider.getUriForFile(ShowAttendanceActivity.this,getApplicationContext().getPackageName() + ".com.skillzoomer_Attendance.com",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(DayBookListActivity.this);
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
                hashMap.put("downloadFile", "DayBook Report");
                hashMap.put("fromDate", fromDate);
                hashMap.put("toDate", toDate);
                hashMap.put("siteId", siteId);
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", dnl_file_type);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                Log.e("paymentData",paymentData.getData().toString());
//                                Log.e("paymentData",paymentData.getPaymentId().toString());
                                Toast.makeText(DayBookListActivity.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();

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

    private void DownloadExcel(ArrayList<ModelDayBookClass> modelDayBookClassArrayList, ArrayList<ModelDate> modelDateArrayList, long siteId1, String siteName1, ArrayList<ModelError> errorArrayList) {
        Log.e("Size123", "" + modelDayBookClassArrayList.size());
        Log.e("Size123", "A" + modelDateArrayList.size());
        Log.e("Size123", "B" + errorArrayList.size());
        workbook = new HSSFWorkbook();



// Set text watermark appearance



        workbook1 = new XSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        sheet.createRow(0);
        sheet.createRow(1);
        sheet.createRow(2);
        sheet.createRow(3);
        sheet.createRow(4);
        sheet.createRow(5);



//        HSSFPatriarch dp = sheet.createDrawingPatriarch();
//        HSSFClientAnchor anchor = new HSSFClientAnchor
//                (0, 0, 1023, 255, (short) 2, modelDayBookClassArrayList.size()/2, (short) 13,(modelDayBookClassArrayList.size()/2)+4 );
//        HSSFTextbox txtbox = dp.createTextbox(anchor);
//        HSSFRichTextString rtxt = new HSSFRichTextString("Hajiri Register");
//        HSSFFont font = workbook.createFont();
//        font.setColor(HSSFColor.GREY_40_PERCENT.index);
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
//        font.setFontHeightInPoints((short) 56);
//        font.setFontName("Verdana");
//        rtxt.applyFont(font);
//        txtbox.setString(rtxt);
//        txtbox.setLineStyle(HSSFShape.LINESTYLE_NONE);
//        txtbox.setNoFill(false);
//        workbook.write(fileOut);
//        fileOut.close();

        String filename = "path/spreadsheet.xlsx";
//        Watermarker watermarker = new Watermarker(filename, new SpreadsheetLoadOptions());
//
//// Set text watermark appearance
//        TextWatermark watermark = new TextWatermark("DRAFT", new com.groupdocs.watermark.Font("Segoe UI", 19));
//        watermark.setHorizontalAlignment(HorizontalAlignment.Center);
//        watermark.setVerticalAlignment(VerticalAlignment.Center);
//        watermark.setRotateAngle(-45);
//        watermark.setSizingType(SizingType.ScaleToParentDimensions);
//        watermark.setScaleFactor(0.5);
//        watermark.setOpacity(0.5);
//
//// Add watermark and save the spreadsheet with watermark
//        watermarker.add(watermark);
//        watermarker.save("path/watermark-all-spreadsheet.xlsx");
//        watermarker.close();



        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());


        sheet.setColumnWidth(1, 15*256);
        sheet.setColumnWidth(2, 15*256);
        sheet.setColumnWidth(3, 15*256);
        sheet.setColumnWidth(4, 15*256);
        sheet.setColumnWidth(5, 15*256);
        sheet.setColumnWidth(6, 20 * 256);
        sheet.setColumnWidth(7, 20 * 256);
//  sheet.setFitToPage(true);
//  sheet.setAutobreaks(true);

        createHeaderRow(sheet, modelDateArrayList, modelDayBookClassArrayList, siteId1, siteName1);
        createDayBookData(sheet, modelDateArrayList, modelDayBookClassArrayList, siteId1, siteName1);

        if (errorArrayList.size() > 0) {
            createErrorData(sheet, modelDateArrayList, modelDayBookClassArrayList, siteId1, siteName1, errorArrayList);
            createErrorDataValue(sheet, modelDateArrayList, modelDayBookClassArrayList, siteId1, siteName1, errorArrayList);
        }

        createFooter(sheet, modelDateArrayList, modelDayBookClassArrayList, siteId1, siteName1, errorArrayList);
//        sheet.setRepeatingRows(CellRangeAddress.valueOf("1:1"));



        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String timestamp = "" + System.currentTimeMillis();
            Log.e("StrPath", str_path);
            file = new File(str_path, siteName1 + "" + "DayBook" + timestamp + ".xls");
            Log.e("SIIIII",""+modelDayBookClassArrayList.size());


                HSSFPatriarch dp = sheet.createDrawingPatriarch();

                HSSFClientAnchor anchor = new HSSFClientAnchor
                        (0, 0, 650, 255, (short) 2, modelDayBookClassArrayList.size()/2, (short) 13, modelDayBookClassArrayList.size()/2+3);


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




            if (dnl_file_type.equals("pdf")) {
//                workbook.write(fos);

                ExcelToPdf(file, fos, workbook, siteId1, siteName1, modelDayBookClassArrayList, errorArrayList);

            } else {
//                Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//                createOrderId.start();
                Log.e("FilePath", file.getAbsolutePath().toString());
                str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                Log.e("StrPath", str_path);
                file = new File(str_path, siteName1 + "" + "DayBook" + timestamp + ".xls");

                try {
                    fos = new FileOutputStream(file);
                    workbook.write(fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("FilePath", file.getAbsolutePath().toString());
                ExcelToPdf(file, fos, workbook, siteId1, siteName1, modelDayBookClassArrayList, errorArrayList);
            }

//            fos = new FileOutputStream(file);

//            Log.e
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Log.e("DayBook","SiteID:"+siteId1);
//        Log.e("DayBook","Date\tReceived\tRFrom\tExpense\tExpenseRemark");
//
//        for(int i=0;i<modelDayBookClassArrayList.size();i++){
//           Log.e("DayBook",modelDayBookClassArrayList.get(i).getDate()+"\t"+modelDayBookClassArrayList.get(i).getRecAmt()+"\t"
//           +modelDayBookClassArrayList.get(i).getRec_from()+"\t"+modelDayBookClassArrayList.get(i).getExpAmt()+"\t"+modelDayBookClassArrayList.get(i).getExpRemark());
//        }

    }


    private void createErrorDataValue(Sheet sheet, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDayBookClass> modelDayBookClassArrayList, long siteId1, String siteName1, ArrayList<ModelError> errorArrayList) {
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
        for (int i = 0; i < errorArrayList.size(); i++) {
            Row row = sheet.createRow(i + (modelDayBookClassArrayList.size() + 11));
            row.setHeightInPoints(25);
            Cell cellSrNo = row.createCell(0);
            cellSrNo.setCellStyle(cellStyle);
            cellSrNo.setCellValue(String.valueOf(i + 1));
            Cell cellDate = row.createCell(1);
            cellDate.setCellStyle(cellStyle);
            cellDate.setCellValue(errorArrayList.get(i).getDate());

            Cell cellSrNo1 = row.createCell(2);
            cellSrNo1.setCellStyle(cellStyle);
            cellSrNo1.setCellValue(errorArrayList.get(i).getTime());
            Cell cellSrNo2 = row.createCell(3);
            cellSrNo2.setCellStyle(cellStyle);
            cellSrNo2.setCellValue(errorArrayList.get(i).getAmount());
            Cell cellSrNo3 = row.createCell(4);
            cellSrNo3.setCellStyle(cellStyle);
            cellSrNo3.setCellValue(errorArrayList.get(i).getCashInHand());
            Cell cellSrNo4 = row.createCell(5);
            cellSrNo4.setCellStyle(cellStyle);
            cellSrNo4.setCellValue(errorArrayList.get(i).getRemark());
            Cell cellSrNo5 = row.createCell(6);
            cellSrNo5.setCellStyle(cellStyle);
            cellSrNo5.setCellValue(errorArrayList.get(i).getName());

        }
    }

    private void createErrorData(Sheet sheet, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDayBookClass> modelDayBookClassArrayList, long siteId1, String siteName1, ArrayList<ModelError> errorArrayList) {
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


        Row row1 = sheet.createRow(modelDayBookClassArrayList.size() + 8);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("Error Report");

        CellRangeAddress cellMerge2 = new CellRangeAddress(modelDayBookClassArrayList.size() + 8, modelDayBookClassArrayList.size() + 9, 0, 7);
        sheet.addMergedRegion(cellMerge2);

        Row rowValues = sheet.createRow(modelDayBookClassArrayList.size() + 10);
        rowValues.setHeightInPoints(30);
        Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        Cell cellDate = rowValues.createCell(1);
        cellDate.setCellStyle(cellStyle);
        cellDate.setCellValue("Date");

        Cell cellSrNo1 = rowValues.createCell(2);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Time");
        Cell cellSrNo2 = rowValues.createCell(3);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Amount");
        Cell cellSrNo3 = rowValues.createCell(4);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Cash In Hnd");
        Cell cellSrNo4 = rowValues.createCell(5);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Remark");
        Cell cellSrNo5 = rowValues.createCell(6);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("Name");

    }

    private void ExcelToPdf(File file,
                            FileOutputStream fos, HSSFWorkbook workbook, long siteId1, String siteName1, ArrayList<ModelDayBookClass> modelDayBookClassArrayList,
                            ArrayList<ModelError> errorArrayList) throws DocumentException {
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + siteName1 +   "DayBook" + timestamp + ".pdf";
        File pdfFile = new File(str_path);
        Document iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 80f);
//        iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));


        try {
            PdfWriter writer=PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(DayBookListActivity.this,siteId1,siteName1,modelDateArrayList,"Day Book Report");
            writer.setPageEvent(event);
        } catch (DocumentException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();
        }
        iText_xls_2_pdf.open();






        com.itextpdf.text.Font fontHeaderNormal=new com.itextpdf.text.Font();
        fontHeaderNormal.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderNormal.setSize(12);
        fontHeaderNormal.setColor(BaseColor.BLACK);
        fontHeaderNormal.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontHeaderBold=new com.itextpdf.text.Font();
        fontHeaderBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderBold.setSize(14);
        fontHeaderBold.setColor(BaseColor.BLACK);
        fontHeaderBold.setFamily(String.valueOf(Paint.Align.CENTER));

        PdfPTable my_table_header = new PdfPTable( 20);
        my_table_header.setWidthPercentage(95);

//        PdfPCell table_cell_industry_name;
//        PdfPCell table_cell_company_name;
//        PdfPCell table_cell_siteId;
//        PdfPCell table_cell_siteName;
//        PdfPCell table_cell_generated_on;
//        PdfPCell table_cell_workers_advances_report;
//        PdfPCell table_cell_to;
//        PdfPCell table_cell_from;
//        PdfPCell table_cell_total_no_of_days;
//        table_cell_industry_name=new PdfPCell(new Phrase("Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""),fontHeaderBold));
//        table_cell_company_name=new PdfPCell(new Phrase("Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName",""),fontHeaderBold));
//        table_cell_siteId=new PdfPCell(new Phrase( "Site Id: " + siteId1,fontHeaderNormal));
//        table_cell_siteName=new PdfPCell(new Phrase("Site Name: " + siteName1,fontHeaderNormal));
//        table_cell_generated_on=new PdfPCell(new Phrase("Generated On: " +currentDate,fontHeaderNormal));
//        table_cell_workers_advances_report=new PdfPCell(new Phrase("Day Book Report",fontHeaderBold));
//        table_cell_from=new PdfPCell(new Phrase("From:"+modelDateArrayList.get(0).getDate(),fontHeaderNormal));
//        table_cell_to=new PdfPCell(new Phrase("To:"+modelDateArrayList.get(modelDateArrayList.size()-1).getDate(),fontHeaderNormal));
//        table_cell_total_no_of_days=new PdfPCell(new Phrase("Total No of days:"+modelDateArrayList.size(),fontHeaderNormal));
//
//        table_cell_industry_name.setColspan(7);
//        table_cell_industry_name.setBorder(Rectangle.NO_BORDER);
//        table_cell_industry_name.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table_cell_generated_on.setColspan(7);
//        table_cell_generated_on.setBorder(Rectangle.NO_BORDER);
//        table_cell_generated_on.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table_cell_company_name.setColspan(6);
//        table_cell_company_name.setBorder(Rectangle.NO_BORDER);
//        table_cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table_cell_siteName.setColspan(7);
//        table_cell_siteName.setBorder(Rectangle.NO_BORDER);
//        table_cell_siteName.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table_cell_workers_advances_report.setColspan(7);
//        table_cell_workers_advances_report.setBorder(Rectangle.NO_BORDER);
//        table_cell_workers_advances_report.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//        table_cell_siteId.setColspan(6);
//        table_cell_siteId.setBorder(Rectangle.NO_BORDER);
//        table_cell_siteId.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table_cell_from.setColspan(3);
//        table_cell_from.setBorder(Rectangle.NO_BORDER);
//        table_cell_from.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table_cell_to.setColspan(3);
//        table_cell_to.setBorder(Rectangle.NO_BORDER);
//        table_cell_to.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table_cell_total_no_of_days.setColspan(14);
//        table_cell_total_no_of_days.setBorder(Rectangle.NO_BORDER);
//        table_cell_total_no_of_days.setHorizontalAlignment(Element.ALIGN_RIGHT);
//
//
//        my_table_header.addCell(table_cell_industry_name);
//        my_table_header.addCell(table_cell_generated_on);
//        my_table_header.addCell(table_cell_company_name);
//        my_table_header.addCell(table_cell_siteName);
//        my_table_header.addCell(table_cell_workers_advances_report);
//        my_table_header.addCell(table_cell_siteId);
//        my_table_header.addCell(table_cell_from);
//        my_table_header.addCell(table_cell_to);
//        my_table_header.addCell(table_cell_total_no_of_days);
//
//        iText_xls_2_pdf.add(my_table_header);
        PdfPTable my_table = new PdfPTable(modelDateArrayList.size() + 8);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;

        createPdfHeaderRow(modelDateArrayList, shortDateList,modelDayBookClassArrayList,siteId1,siteName1,iText_xls_2_pdf,my_table,pdfFile);
//        try {
//            Drawable d = getResources().getDrawable(R.drawable.logo23);
//            BitmapDrawable bitDw = ((BitmapDrawable) d);
//            Bitmap bmp = bitDw.getBitmap();
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            Image image = Image.getInstance(stream.toByteArray());
//            image.setAlignment(Element.ALIGN_CENTER);
//            image.setWidthPercentage(50);
//            image.setScaleToFitHeight(true);
//            iText_xls_2_pdf.add(image);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String date_val3 = "To know us more visit https://skillzoomers.com/hajiri-register/ ";
//
////          Paragraph p4 = new Paragraph(date_val1);
////          p4.setAlignment(Paragraph.ALIGN_CENTER);
//
//
//        Paragraph p7 = new Paragraph("\n");
//        p7.setAlignment(Paragraph.ALIGN_CENTER);
//
//        Paragraph p8 = new Paragraph(date_val3);
//        p8.setAlignment(Paragraph.ALIGN_CENTER);
//
//        try {
//
//            iText_xls_2_pdf.add(p7);
//            iText_xls_2_pdf.add(p8);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        iText_xls_2_pdf.close();


//
//
//
//


    }


    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        PdfContentByte under = stamper.getUnderContent(1);

        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 15);
        Phrase p = new Phrase("This watermark is added UNDER the existing content", f);
        ColumnText.showTextAligned(under, Element.ALIGN_CENTER, p, 297, 550, 0);
        PdfContentByte over = stamper.getOverContent(1);
        p = new Phrase("This watermark is added ON TOP OF the existing content", (com.itextpdf.text.Font) f);
        ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, 297, 500, 0);
        p = new Phrase("This TRANSPARENT watermark is added ON TOP OF the existing content", f);
        over.saveState();
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.5f);
        over.setGState(gs1);
        ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, 297, 450, 0);
        over.restoreState();
        stamper.close();
        reader.close();
    }



    private void createPdfHeaderRow(ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDate> shortDateList,
                                    ArrayList<ModelDayBookClass> modelDayBookClassArrayList, long siteId1, String siteName1,
                                    Document iText_xls_2_pdf, PdfPTable my_table, File pdfFile) {
        PdfPTable table_header=new PdfPTable(8);

        table_header.setWidthPercentage(100);
        table_header.setTotalWidth(iText_xls_2_pdf.getPageSize().getWidth());
//        table_header.setWidths(new float[modelDateArrayList.size()+8]);

        com.itextpdf.text.Font fontBold=new com.itextpdf.text.Font();
        fontBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontBold.setSize(12);
        fontBold.setColor(BaseColor.BLACK);
        fontBold.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontNormal=new com.itextpdf.text.Font();
        fontNormal.setStyle(com.itextpdf.text.Font.NORMAL);
        fontNormal.setSize(12);
        fontNormal.setColor(BaseColor.BLACK);

        com.itextpdf.text.Font fontGreen=new com.itextpdf.text.Font();
        fontGreen.setStyle(com.itextpdf.text.Font.BOLD);
        fontGreen.setSize(12);
        fontGreen.setColor(BaseColor.BLACK);

        com.itextpdf.text.Font fontRed=new com.itextpdf.text.Font();
        fontRed.setStyle(com.itextpdf.text.Font.BOLD);
        fontRed.setSize(12);
        fontRed.setColor(BaseColor.RED);

        com.itextpdf.text.Font fontBlue=new com.itextpdf.text.Font();
        fontBlue.setStyle(com.itextpdf.text.Font.NORMAL);
        fontBlue.setSize(12);
        fontBlue.setColor(BaseColor.BLUE);

        table_header.addCell(new PdfPCell(new Phrase("Sr No", fontBold)));
        table_header.addCell(new PdfPCell(new Phrase("Date", fontBold)));
        table_header.addCell(new PdfPCell(new Phrase("Receiving", fontBold)));
        table_header.addCell(new PdfPCell(new Phrase("Received From", fontBold)));
        table_header.addCell(new PdfPCell(new Phrase("Expense", fontBold)));
        table_header.addCell(new PdfPCell(new Phrase("Expense For", fontBold)));
        table_header.addCell(new PdfPCell(new Phrase("Expense Paid By", fontBold)));
        table_header.addCell(new PdfPCell(new Phrase("C.B.", fontBold)));
        Log.e("ModelDayBook",""+modelDayBookClassArrayList.size());

        for (int i = 0; i < modelDayBookClassArrayList.size(); i++) {
            PdfPCell tableCell_SrNo = new PdfPCell();
            tableCell_SrNo.setNoWrap(false);
            tableCell_SrNo.setPhrase(new Phrase(String.valueOf(i + 1), fontNormal));
            tableCell_SrNo.setHorizontalAlignment(Element.ALIGN_CENTER);
            Log.e("CellDatta",""+i+String.valueOf(i + 1));
            table_header.addCell(tableCell_SrNo);

            PdfPCell tableCell_Id = new PdfPCell();
            tableCell_Id.setNoWrap(false);
            tableCell_Id.setPhrase(new Phrase(modelDayBookClassArrayList.get(i).getDate(), fontNormal));
            tableCell_Id.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_header.addCell(tableCell_Id);
            Log.e("CellDatta",""+i+modelDayBookClassArrayList.get(i).getDate());


            PdfPCell tableCell_Name = new PdfPCell();
            tableCell_Name.setNoWrap(false);
            tableCell_Name.setPhrase(new Phrase(modelDayBookClassArrayList.get(i).getRecAmt(), fontNormal));
            tableCell_Name.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_header.addCell(tableCell_Name);
            Log.e("CellDatta",""+i+modelDayBookClassArrayList.get(i).getRecAmt());


            PdfPCell tableCell_Type = new PdfPCell();
            tableCell_Type.setNoWrap(false);
            if (Integer.parseInt(modelDayBookClassArrayList.get(i).getRecAmt()) < 0) {
                Log.e("Reconcile","Reconcile");
                Log.e("Reconcile",modelDayBookClassArrayList.get(i).getRec_from());
                tableCell_Type.setPhrase(new Phrase("Reconciled", fontRed));
                tableCell_Type.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_header.addCell(tableCell_Type);
            } else {
                tableCell_Type.setPhrase(new Phrase(modelDayBookClassArrayList.get(i).getRec_from(), fontNormal));
                tableCell_Type.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_header.addCell(tableCell_Type);
            }
//            tableCell_Type.setPhrase(new Phrase(modelDayBookClassArrayList.get(i).getRec_from(), fontNormal));
//            table_header.addCell(tableCell_Type);


            PdfPCell tableCell_Wages = new PdfPCell();
            tableCell_Wages.setNoWrap(false);
            tableCell_Wages.setPhrase(new Phrase(modelDayBookClassArrayList.get(i).getExpAmt(), fontNormal));
            tableCell_Wages.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_header.addCell(tableCell_Wages);

            PdfPCell table_exp_for = new PdfPCell();
            table_exp_for.setNoWrap(false);
            table_exp_for.setPhrase(new Phrase(modelDayBookClassArrayList.get(i).getExpRemark(), fontNormal));
            table_exp_for.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_header.addCell(table_exp_for);

            PdfPCell table_expense_paid = new PdfPCell();
            table_expense_paid.setNoWrap(false);
            table_expense_paid.setPhrase(new Phrase(modelDayBookClassArrayList.get(i).getExpDoneBy(), fontNormal));
            table_expense_paid.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_header.addCell(table_expense_paid);

            PdfPCell table_cel_cb = new PdfPCell();
            table_cel_cb.setNoWrap(false);
            table_cel_cb.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cel_cb.setPhrase(new Phrase(String.valueOf(Integer.parseInt(modelDayBookClassArrayList.get(i).getRecAmt()) - Integer.parseInt(modelDayBookClassArrayList.get(i).getExpAmt())), fontNormal));
            table_header.addCell(table_cel_cb);


        }
        int Receive_sum = 0;
        int ExpenseSum = 0;
        for (int j = 0; j < modelDayBookClassArrayList.size(); j++) {

            Receive_sum = Receive_sum + Integer.parseInt(modelDayBookClassArrayList.get(j).getRecAmt());
            ExpenseSum = ExpenseSum + Integer.parseInt(modelDayBookClassArrayList.get(j).getExpAmt());





        }
        PdfPCell table_cel_cb1 = new PdfPCell();
        table_cel_cb1.setNoWrap(false);
        table_cel_cb1.setColspan(2);
        table_cel_cb1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cel_cb1.setPhrase(new Phrase("Total",fontBold));

        table_header.addCell(table_cel_cb1);

        table_header.addCell(new PdfPCell(new Phrase(String.valueOf(Receive_sum), fontBold))).setHorizontalAlignment(Element.ALIGN_CENTER);
        table_header.addCell(new PdfPCell(new Phrase("", fontBold))).setHorizontalAlignment(Element.ALIGN_CENTER);
        table_header.addCell(new PdfPCell(new Phrase(String.valueOf(ExpenseSum), fontBold))).setHorizontalAlignment(Element.ALIGN_CENTER);
        table_header.addCell(new PdfPCell(new Phrase("", fontBold))).setHorizontalAlignment(Element.ALIGN_CENTER);
        table_header.addCell(new PdfPCell(new Phrase("", fontBold))).setHorizontalAlignment(Element.ALIGN_CENTER);

        table_header.addCell(new PdfPCell(new Phrase(String.valueOf(Receive_sum-ExpenseSum), fontBold))).setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph e1 = new Paragraph("\nError Report\n");
        Paragraph e2 = new Paragraph("\n\n");
        e1.setAlignment(Paragraph.ALIGN_CENTER);


        PdfPTable my_table1 = new PdfPTable(7);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell1;
        my_table1.addCell(new PdfPCell(new Phrase("Sr No", fontBold)));
        my_table1.addCell(new PdfPCell(new Phrase("Date", fontBold)));
        my_table1.addCell(new PdfPCell(new Phrase("Time", fontBold)));
        my_table1.addCell(new PdfPCell(new Phrase("Amount", fontBold)));
        my_table1.addCell(new PdfPCell(new Phrase("Cash In Hand", fontBold)));
        my_table1.addCell(new PdfPCell(new Phrase("Remark", fontBold)));
        my_table1.addCell(new PdfPCell(new Phrase("Name", fontBold)));

        for(int i=0;i<errorArrayList.size();i++){
            my_table1.addCell(new PdfPCell(new Phrase(String.valueOf(i+1), fontNormal))).setHorizontalAlignment(Element.ALIGN_CENTER);
            my_table1.addCell(new PdfPCell(new Phrase(errorArrayList.get(i).getDate(), fontNormal))).setHorizontalAlignment(Element.ALIGN_CENTER);
            my_table1.addCell(new PdfPCell(new Phrase(errorArrayList.get(i).getTime(), fontNormal))).setHorizontalAlignment(Element.ALIGN_CENTER);
            my_table1.addCell(new PdfPCell(new Phrase(errorArrayList.get(i).getAmount(), fontNormal))).setHorizontalAlignment(Element.ALIGN_CENTER);
            my_table1.addCell(new PdfPCell(new Phrase(errorArrayList.get(i).getCashInHand(), fontNormal))).setHorizontalAlignment(Element.ALIGN_CENTER);
            my_table1.addCell(new PdfPCell(new Phrase(errorArrayList.get(i).getRemark(), fontNormal))).setHorizontalAlignment(Element.ALIGN_CENTER);
            my_table1.addCell(new PdfPCell(new Phrase(errorArrayList.get(i).getName(), fontNormal))).setHorizontalAlignment(Element.ALIGN_CENTER);

        }
//
//
//
//
//
        try {
            iText_xls_2_pdf.add(table_header);
            iText_xls_2_pdf.add(e1);
            iText_xls_2_pdf.add(e2);
            iText_xls_2_pdf.add(my_table1);
        } catch (DocumentException e) {
            e.printStackTrace();
            Log.e("Exc124354463",e.getMessage());
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

//
//        try {
////            new DayBookListActivity().manipulatePdf(pdfFile.getAbsolutePath(),pdfFile.getAbsolutePath() );
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (DocumentException e) {
//            throw new RuntimeException(e);
//        }
        //we created our pdf file..
//          try {
//
//              input_document.close(); //close xls
//          } catch (IOException e) {
//              e.printStackTrace();
//          }
    }


    private void createFooter(Sheet sheet, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDayBookClass> modelDayBookClassArrayList, long siteId1, String siteName1, ArrayList<ModelError> errorArrayList) {
        Log.e("Footer", "Error" + errorArrayList.size());
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




        Row row = sheet.createRow(modelDayBookClassArrayList.size() + errorArrayList.size() + 12);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle2);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Generated by: " + " " + "Hajiri Register";
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(modelDayBookClassArrayList.size() + errorArrayList.size() + 12, modelDayBookClassArrayList.size() + 13 + errorArrayList.size(), 0, 10);
        sheet.addMergedRegion(cellMerge);

        String date_val1 = "Powered by: " + " " + "Skill Zoomers";

        Row row1 = sheet.createRow(modelDayBookClassArrayList.size() + errorArrayList.size() + 14);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue(date_val1);

        CellRangeAddress cellMerge1 = new CellRangeAddress(modelDayBookClassArrayList.size() + errorArrayList.size() + 14, modelDayBookClassArrayList.size() + 15 + errorArrayList.size(), 0, 10);
        sheet.addMergedRegion(cellMerge1);

        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ ";

        Row row2 = sheet.createRow(modelDayBookClassArrayList.size() + errorArrayList.size() + 16);
        Cell cellHeading1 = row2.createCell(0);

        cellHeading1.setCellStyle(cellStyle2);
        cellHeading1.setCellValue(date_val2);

        CellRangeAddress cellMerge2 = new CellRangeAddress(modelDayBookClassArrayList.size() + errorArrayList.size() + 16, modelDayBookClassArrayList.size() + 18 + errorArrayList.size(), 0, 10);
        sheet.addMergedRegion(cellMerge2);
    }

    private void createDayBookData(Sheet sheet, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDayBookClass> modelDayBookClassArrayList, long siteId1, String siteName1) {
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
        CellStyle cellStyle1 = sheet.getWorkbook().createCellStyle();
        Font font1 = sheet.getWorkbook().createFont();
        font1.setColor(Font.COLOR_RED);
        font1.setFontHeightInPoints((short) 12);
        cellStyle1.setFont(font1);
        cellStyle1.setBorderBottom((short) 1);
        cellStyle1.setBorderTop((short) 1);
        cellStyle1.setBorderLeft((short) 1);
        cellStyle1.setBorderRight((short) 1);
        cellStyle1.setWrapText(true);
        cellStyle1.setAlignment(CellStyle.ALIGN_CENTER);


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


        for (int i = 0; i < modelDayBookClassArrayList.size(); i++) {
            Row row = sheet.createRow(i + 7);
            row.setHeightInPoints(25);
            Cell cellSrNo = row.createCell(0);
            cellSrNo.setCellStyle(cellStyle);
            cellSrNo.setCellValue(String.valueOf(i + 1));
            Cell cellDate = row.createCell(1);
            cellDate.setCellStyle(cellStyle);
            cellDate.setCellValue(modelDayBookClassArrayList.get(i).getDate());

            Cell cellSrNo1 = row.createCell(2);
            cellSrNo1.setCellStyle(cellStyle);


            cellSrNo1.setCellValue(modelDayBookClassArrayList.get(i).getRecAmt());
            Cell cellSrNo2 = row.createCell(3);
            Log.e("CrashValue",""+i);
            if (modelDayBookClassArrayList.get(i).getRecAmt()!=null&&Integer.parseInt(modelDayBookClassArrayList.get(i).getRecAmt()) < 0) {
                cellSrNo2.setCellStyle(cellStyle1);
            } else {
                cellSrNo2.setCellStyle(cellStyle);
            }
            cellSrNo2.setCellValue(modelDayBookClassArrayList.get(i).getRec_from());
            Cell cellSrNo3 = row.createCell(4);
            cellSrNo3.setCellStyle(cellStyle);
            cellSrNo3.setCellValue(modelDayBookClassArrayList.get(i).getExpAmt());
            Cell cellSrNo4 = row.createCell(5);
            cellSrNo4.setCellStyle(cellStyle);
            cellSrNo4.setCellValue(modelDayBookClassArrayList.get(i).getExpRemark());
            Cell cellSrNo6 = row.createCell(6);
            cellSrNo6.setCellStyle(cellStyle);
            cellSrNo6.setCellValue(modelDayBookClassArrayList.get(i).getExpDoneBy());
            Cell cellSrNo5 = row.createCell(7);
            cellSrNo5.setCellStyle(cellStyle);
            if (modelDayBookClassArrayList.get(i).getRecAmt()!=null){
                cellSrNo5.setCellValue(String.valueOf(Integer.parseInt(modelDayBookClassArrayList.get(i).getRecAmt()) - Integer.parseInt(modelDayBookClassArrayList.get(i).getExpAmt())));
            }else{
                cellSrNo5.setCellValue("0");
            }



        }
        Row row = sheet.createRow(modelDayBookClassArrayList.size() + 7);

        int Receive_sum = 0;
        int ExpenseSum = 0;
        for (int j = 0; j < modelDayBookClassArrayList.size(); j++) {
            if(modelDayBookClassArrayList.get(j).getRecAmt()!=null){
                Receive_sum = Receive_sum + Integer.parseInt(modelDayBookClassArrayList.get(j).getRecAmt());
                ExpenseSum = ExpenseSum + Integer.parseInt(modelDayBookClassArrayList.get(j).getExpAmt());
            }



        }
        Cell cellDate1= row.createCell(0);
        cellDate1.setCellStyle(cellStyle);
        cellDate1.setCellValue("TOTAL");

        row.createCell(1).setCellStyle(cellStyle);

        CellRangeAddress cellMerge2 = new CellRangeAddress(modelDayBookClassArrayList.size() + 7, modelDayBookClassArrayList.size()+7 , 0, 1);
        sheet.addMergedRegion(cellMerge2);



        Cell cellDate = row.createCell(2);
        cellDate.setCellStyle(cellStyle);
        cellDate.setCellValue(String.valueOf(Receive_sum));
        Cell cellExpense = row.createCell(4);
        cellExpense.setCellStyle(cellStyle);
        cellExpense.setCellValue(String.valueOf(ExpenseSum));

        Cell cellCb = row.createCell(7);
        cellCb.setCellStyle(cellStyle);
        cellCb.setCellValue(String.valueOf(Receive_sum-ExpenseSum));


    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDayBookClass> modelDayBookClassArrayList, long siteId1, String siteName1) {
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


        Row row = sheet.getRow(0);
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

        CellRangeAddress cellMerge = new CellRangeAddress(0, 3, 0, 4);
        sheet.addMergedRegion(cellMerge);

        Cell cellFullName1 = row.createCell(4);
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

        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 4, 4, 8);
        sheet.addMergedRegion(cellMerge1);


        Row row1 = sheet.getRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("Day Book Report"+"\t\t\t"+"From: " +
                " "+modelDateArrayList.get(0).getDate()+"\t\t"+ "To: " + modelDateArrayList.get(modelDateArrayList.size()-1).getDate()+"\t\t\t"+"Total No of Days:"+modelDateArrayList.size());

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
        cellSrNo1.setCellValue("Receiving");
        Cell cellSrNo2 = rowValues.createCell(3);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Received From");
        Cell cellSrNo3 = rowValues.createCell(4);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Expense");
        Cell cellSrNo4 = rowValues.createCell(5);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Expense For");
        Cell cellSrNo6 = rowValues.createCell(6);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Expense Paid By");
        Cell cellSrNo5 = rowValues.createCell(7);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("C.B.");


    }

    private void getReceivingDataIndividual(String date) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Receive").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiving_amt = 0;
                receiving_by = "";
                expense_amt = 0;

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (snapshot.getChildrenCount() > 1) {
                            ModelReceiveCash modelReceiveCash = ds.getValue(ModelReceiveCash.class);
                            receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
                            receiving_by = "Multiple";
                        } else if (snapshot.getChildrenCount() == 1) {
                            ModelReceiveCash modelReceiveCash = ds.getValue(ModelReceiveCash.class);
                            receiving_amt = receiving_amt + Integer.parseInt(modelReceiveCash.getAmount());
                            receiving_by = modelReceiveCash.getRecFrom();
                        }

                    }
                    getExpenseDataIndividual(date, String.valueOf(receiving_amt), receiving_by);
                } else {
                    getExpenseDataIndividual(date, "0", "N/R");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getExpenseDataIndividual(String date, String s, String s1) {
        Log.e("Array", "Site;;;;;" + siteId);
        Log.e("Array", "Date;;;;;" + date);
        paymentArrayList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expense_amt = 0;
                String expense_to = "";
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        ModelPayment modelExpense = ds.getValue(ModelPayment.class);
                        expense_amt = expense_amt + Integer.parseInt(String.valueOf(modelExpense.getAmount()));
                        paymentArrayList.add(modelExpense);


                    }
                    Log.e("Array", "Snap::" + snapshot.getChildrenCount());
                    if (snapshot.getChildrenCount() > 1) {

                        ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, snapshot.getChildrenCount(), String.valueOf(expense_amt), "Multiple");
                        addToArray(modelDayBookClass);

                    } else {
                        ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, snapshot.getChildrenCount(), String.valueOf(expense_amt), paymentArrayList.get(0).getLabourName());
                        addToArray(modelDayBookClass);
                    }


                } else {
                    ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, 0, "0", "N/R");
                    addToArray(modelDayBookClass);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showDownloadSiteDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(DayBookListActivity.this);
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

                } else {
                    Toast.makeText(DayBookListActivity.this, "Select Atleast one site to download report", Toast.LENGTH_SHORT).show();
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

    private void getExpenseData(long siteId) {
        Log.e("SiteId", String.valueOf(siteId));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelExpense modelExpense = ds.getValue(ModelExpense.class);
                    expenseArrayList.add(modelExpense);

                }
                Log.e("ExpenseArrayList", "" + expenseArrayList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getSiteMemberStatus(long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
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
                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yy", Locale.US);
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
            modelDayBookClassArrayList.clear();

            for (int i = 0; i < modelDateArrayList.size(); i++) {
                getReceivingDataIndividual(modelDateArrayList.get(i).getDate());
            }


        }
    }


    private void getLabourPayment(String date, String s, String s1, long childrenCount, String valueOf) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String expense_to = snapshot.child("labourName").getValue(String.class);
                ModelDayBookClass modelDayBookClass = new ModelDayBookClass(date, s, s1, snapshot.getChildrenCount(), String.valueOf(expense_amt), expense_to);
                addToArray(modelDayBookClass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToArray(ModelDayBookClass modelDayBookClass) {
        modelDayBookClassArrayList.add(modelDayBookClass);


        if (modelDayBookClassArrayList.size() == modelDateArrayList.size()) {
            Log.e("Array", "" + selected_option);
            if (selected_option == 2) {
                binding.receivedAmountHeading.setVisibility(View.GONE);
                binding.receivedFromHeading.setVisibility(View.GONE);
                binding.closingBalanceHeading.setVisibility(View.GONE);
                binding.llDayBookHeading.setWeightSum(3);
                AdapterDayBook adapterDayBook = new AdapterDayBook(DayBookListActivity.this, modelDayBookClassArrayList, selected_option);
                binding.rvDayBook.setAdapter(adapterDayBook);
//                DownloadExcel(modelDayBookClassArrayList);
            } else if (selected_option == 1) {
                AdapterDayBook adapterDayBook = new AdapterDayBook(DayBookListActivity.this, modelDayBookClassArrayList, selected_option);
                binding.rvDayBook.setAdapter(adapterDayBook);
//                DownloadExcel(modelDayBookClassArrayList);
            } else if (selected_option == 3) {
                getSiteMemberStatusFinal(siteId, modelDayBookClassArrayList);
//                DownloadExcel(modelDayBookClassArrayList);
            } else if (userType.equals("Supervisor")) {
                getSiteMemberStatusFinal(siteId, modelDayBookClassArrayList);
            }

//            for(int i=0;i<modelDayBookClassArrayList.size();i++){
//                Log.e("Array",modelDayBookClassArrayList.get(i).getDate()+"\t"+
//                        modelDayBookClassArrayList.get(i).getRecAmt()+"\t"+modelDayBookClassArrayList.get(i).getRec_from()+
//                        "\t"+modelDayBookClassArrayList.get(i).getExpAmt()+"\t"+modelDayBookClassArrayList.get(i).getCount());
//            }
        }

    }


    private void getSiteMemberStatusFinal(long siteId, ArrayList<ModelDayBookClass> modelDayBookClassArrayList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Size1234", "" + modelDayBookClassArrayList.size());
                if (snapshot.child("memberStatus").getValue(String.class) != null) {
                    if (snapshot.child("memberStatus").getValue(String.class).equals("Registered")) {
                        binding.receivedAmountHeading.setVisibility(View.VISIBLE);
                        binding.receivedFromHeading.setVisibility(View.VISIBLE);
                        binding.closingBalanceHeading.setVisibility(View.VISIBLE);
                        binding.llDayBookHeading.setWeightSum(6);
                        AdapterDayBook adapterDayBook = new AdapterDayBook(DayBookListActivity.this, modelDayBookClassArrayList, selected_option, snapshot.child("memberStatus").getValue(String.class), siteId);
                        binding.rvDayBook.setAdapter(adapterDayBook);
                        getErrorList();
                    } else {
                        binding.receivedAmountHeading.setVisibility(View.GONE);
                        binding.receivedFromHeading.setVisibility(View.GONE);
                        binding.closingBalanceHeading.setVisibility(View.GONE);
                        binding.llDayBookHeading.setWeightSum(3);
                        AdapterDayBook adapterDayBook = new AdapterDayBook(DayBookListActivity.this, modelDayBookClassArrayList, selected_option, snapshot.child("memberStatus").getValue(String.class), siteId);
                        binding.rvDayBook.setAdapter(adapterDayBook);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getErrorList() {
        errorArrayList.clear();
        for (int i = 0; i < modelDateArrayList.size(); i++) {
            getErrorData(modelDateArrayList.get(i).getDate());
        }
    }

    private void getErrorData(String date) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Error").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelError modelError = ds.getValue(ModelError.class);
                    errorArrayList.add(modelError);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getReceivingData(ArrayList<ModelDate> modelDateArrayList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Receive").addValueEventListener(new ValueEventListener() {
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

    private void getSiteCreatedDate() {
        Log.e("siteId", "" + siteId);
        if (siteId > 0) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    siteCreatedDate = snapshot.child("siteCreatedDate").getValue(String.class);
//                    Log.e("siteCreatedDate", siteCreatedDate);
//                    Log.e("siteCreatedDate", currentDate);
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
        reference.child(uid).child("Industry").child("Construction").child("Site").orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteAdminList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    modelSite.setSelected(false);
                    siteAdminList.add(modelSite);

                }
                Log.e("siteAdminList", "" + siteAdminList.size());
                if (status.equals("ShowAttendance")) {
                    siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0));
                } else {
                    siteAdminList.add(0, new ModelSite("All Site", 0, false));
                }

                SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
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

                adapterSiteSelect = new AdapterSiteSelect(DayBookListActivity.this, siteAdminList);
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