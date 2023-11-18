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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.itextpdf.text.Header;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.skillzoomer_Attendance.com.Adapter.AdapterNestedChild1;
import com.skillzoomer_Attendance.com.Adapter.AdapterSiteSelect;
import com.skillzoomer_Attendance.com.Model.DateModel;
import com.skillzoomer_Attendance.com.Model.ModelAttendance;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourPayment;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.Model.ModelPromo;
import com.skillzoomer_Attendance.com.Model.ModelShowAttendance;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelSiteSpinner;
import com.skillzoomer_Attendance.com.Utilities.HeaderFooterPageEvent;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Adapter.TableViewAdapter;
import com.skillzoomer_Attendance.com.Adapter.TableViewListener;
import com.skillzoomer_Attendance.com.Adapter.TableViewModel;
import com.skillzoomer_Attendance.com.Utilities.PDFUtility;
import com.skillzoomer_Attendance.com.databinding.ActivityShowPaymentBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.Map;

public class ShowPaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityShowPaymentBinding binding;
    LayoutToolbarBinding toolbarBinding;
    private final Calendar myCalendar = Calendar.getInstance();
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
    Checkout checkout = null;
    private ArrayList<ModelPromo> promoArrayList;
    int amount = 0;
    int AmountTemp = 0;
    AlertDialog alertDialogPaymentConfirm = null;
    FileOutputStream fos = null;
    File file = null;
    int promo_spinner_position = 0;
    String promo_title;
    float present_count = 0;
    float half_count = 0;
    private String dnl_file_type = "";
    HSSFWorkbook workbook = null;
    RecyclerView rv_site_select;
    AdapterSiteSelect adapterSiteSelect;

    long AmountRuleExcel=0,AmountRulePdf=0;

    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityShowPaymentBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());


        toolbarBinding = binding.toolbarLayout;
        toolbarBinding.heading.setText("Show Attendance");
        toolbarBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        siteArrayList = new ArrayList<>();
        siteNameArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        siteAdminList = new ArrayList<>();
        Workbook workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Attendance");
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        modelLabourStatusArrayList = new ArrayList<>();
        promoArrayList = new ArrayList<>();
        labourByType = new ArrayList<>();
        binding.btnDownloadReport.setVisibility(View.GONE);




        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);

        userType=sharedpreferences.getString("userDesignation","");

        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        userType = sharedpreferences.getString("userDesignation", "");
        userName = sharedpreferences.getString("userName", "");
        MyApplication my = new MyApplication();
        my.updateLanguage(this, sharedpreferences.getString("Language", "hi"));


        Intent intent = getIntent();
        status = intent.getStringExtra("Activity");
        if (!status.equals("")) {
            if (status.equals("ShowAttendance")) {
                binding.btnDownloadReport.setVisibility(View.GONE);
                binding.btnShow.setVisibility(View.VISIBLE);
                binding.spinnerSelectSite.setVisibility(View.VISIBLE);
                binding.etDownloadSpinner.setVisibility(View.GONE);

            } else {
                binding.btnDownloadReport.setVisibility(View.VISIBLE);
                binding.btnShow.setVisibility(View.GONE);
                binding.spinnerSelectSite.setVisibility(View.GONE);
                binding.etDownloadSpinner.setVisibility(View.VISIBLE);
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


//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

        getSiteList();
        Checkout.preload(this);

        checkout = new Checkout();

        checkout.setKeyID(getString(R.string.razorpay_key_id));


//        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                selectedSiteId = siteNameArrayList.get(i).getSiteId();
//                selectedSiteName = siteNameArrayList.get(i).getSiteName();
//
//                Log.e("SelectedSiteName", "" + selectedSiteId + "\tName" + selectedSiteName);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


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
        modelAttendances = new ArrayList<>();


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
        ShowPaymentActivity.SpinnerAdapter spinnerAdapter = new ShowPaymentActivity.SpinnerAdapter();
        binding.spinnerSearchType.setAdapter(spinnerAdapter);
        ShowPaymentActivity.SpinnerAdapter1 spinnerAdapter1 = new ShowPaymentActivity.SpinnerAdapter1();
//        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.e("LOngL",""+l);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        binding.spinnerSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 1) {
                    binding.llDate.setVisibility(View.VISIBLE);

                } else if (position == 2) {
                    binding.llDate.setVisibility(View.VISIBLE);

                }
                searchSelected = search[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    siteId = siteAdminList.get(i).getSiteId();
                    siteName = siteAdminList.get(i).getSiteName();
                    Log.e("SiteId", "Spinner" + siteId);
                    binding.llSelectPeriod.setVisibility(View.VISIBLE);
                    binding.btnToday.setChecked(true);
                    checkForAttendance();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Log.e("UserType", userType);
        if (userType.equals("Supervisor")) {
            siteName = sharedpreferences.getString("siteName", "");
            siteId = sharedpreferences.getLong("siteId", 0);
            binding.llSelectSite.setVisibility(View.GONE);
            binding.llSelectPeriod.setVisibility(View.VISIBLE);
        } else {
            getSiteListAdministrator();


        }
//        binding.btnCustom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(status.equals("ShowAttendance")){
//
//                }
//                binding.btnShow.setVisibility(View.VISIBLE);
//                binding.llDate.setVisibility(View.VISIBLE);
//            }
//        });
//        binding.btnToday.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                progressDialog.setMessage("Data Loading");
//                progressDialog.show();
//
//                modelLabourStatusArrayList.clear();
//                checkForAttendance();
//
//            }
//        });
        binding.btnToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.btnCustom.setChecked(false);
                    binding.btnToday.setChecked(true);
                    binding.llDate.setVisibility(View.GONE);
                    if (status.equals("ShowAttendance")) {
                        binding.btnDownloadReport.setVisibility(View.GONE);
                        binding.tableview.setVisibility(View.VISIBLE);
                        checkForAttendance();
                    } else {
                        binding.btnDownloadReport.setVisibility(View.VISIBLE);
                        binding.tableview.setVisibility(View.GONE);
                    }
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
                    if (status.equals("ShowAttendance")) {
                        binding.btnShow.setVisibility(View.VISIBLE);
                        binding.btnDownloadReport.setVisibility(View.GONE);
                    } else {
                        binding.btnShow.setVisibility(View.GONE);
                        binding.btnDownloadReport.setVisibility(View.VISIBLE);
                    }
                }


            }
        });
        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.btnToday.isChecked()){
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
                        Toast.makeText(ShowPaymentActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(ShowPaymentActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(ShowPaymentActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                } else if (f) {
//                    progressDialog.setMessage("Downloading Your Reports");
//                    progressDialog.show();
                    String type = "Custom";
                    modelLabourStatusArrayList.clear();
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
                    reference.child("Advances").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                            AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    final AlertDialog.Builder alert = new AlertDialog.Builder(ShowPaymentActivity.this);
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
                                amount = Math.round((modelDateArrayList.size() * 10));
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
                                Toast.makeText(ShowPaymentActivity.this, "Select file type", Toast.LENGTH_SHORT).show();
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






//


        });
//        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(binding.btnToday.isChecked()){
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
//                        Toast.makeText(ShowPaymentActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
//                        f = false;
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                if (fromDate.equals("")) {
//                    Toast.makeText(ShowPaymentActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
//                } else if (toDate.equals("")) {
//                    Toast.makeText(ShowPaymentActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
//                } else if (f) {
////                    progressDialog.setMessage("Downloading Your Reports");
////                    progressDialog.show();
//                    String type = "Custom";
//                    modelLabourStatusArrayList.clear();
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
//                if (binding.btnToday.isChecked()) {
//                    progressDialog.show();
//                    checkForAttendance();
//
//                }
//                else {
//                    Log.e("SizeOfDateREQ", "" + modelDateArrayList.size());
//                    Log.e("SizeOfDateREQ", "La: " + labourList.size());
//                    countLoop = 0;
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//                    reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot ds : snapshot.getChildren()) {
//                                ModelSite modelSite = ds.getValue(ModelSite.class);
//                                Log.e("siteAdminList", "Size" + siteAdminList.size());
//                                for (int i = 0; i < siteAdminList.size(); i++) {
//                                    if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
//                                        Log.e("SiteFound", "" + siteAdminList.get(i).getSiteId());
//                                        if (siteAdminList.get(i).getSelected()) {
//                                            Log.e("SiteFound", "2::::" + siteAdminList.get(i).getSiteId());
//                                            long siteId1 = siteAdminList.get(i).getSiteId();
//                                            String siteName1 = siteAdminList.get(i).getSiteName();
////                                                    long countChild = snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).getChildrenCount();
////                                                    if (countChild <= 0) {
////                                                        Toast.makeText(ShowPaymentActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
////                                                    } else {
//                                            modelLabourStatusArrayList.clear();
////                                                        labourList.clear();
////                                                        modelDateArrayList.clear();
////                                                        shortDateList.clear();
//                                            binding.btnShow.setVisibility(View.GONE);
//                                            binding.llDate.setVisibility(View.GONE);
//                                            binding.tableview.setVisibility(View.VISIBLE);
//                                            binding.txtMessage.setVisibility(View.GONE);
//                                            Date c = Calendar.getInstance().getTime();
//
//
//                                            Calendar c12 = Calendar.getInstance();
//                                            c12.add(Calendar.DATE, 1);
//                                            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                                            String currentDate11 = df1.format(c);
//
//                                            // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//                                            Date temp = c12.getTime();
//                                            String currentDate1 = sdf1.format(temp);
//                                            Log.e("Labour1", currentDate11);
//                                            Log.e("Labour2", currentDate1);
//                                            Log.e("NullCheck", "A:" + snapshot.child(String.valueOf(siteId1)).child("Labours").getChildrenCount());
//                                            Log.e("ModelDatearrayList", "" + modelDateArrayList.size());
//                                            for (int m = 0; m < modelDateArrayList.size(); m++) {
//                                                labourList.clear();
//                                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()) {
//                                                    ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
//                                                    labourList.add(modelLabour);
//
//
//                                                    Log.e("NULLCHEXK", String.valueOf(siteId1));
//                                                    Log.e("NULLCHEXK", currentDate11);
//                                                    Log.e("NULLCHEXK", modelLabour.getLabourId());
//                                                    if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0) {
//                                                        int countLabour=0;
//                                                        int AmtSum=0;
//                                                        String amount;
//                                                        for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()){
//                                                            ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
//                                                            if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
//                                                                amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
//                                                                AmtSum=AmtSum+Integer.parseInt(amount);
//                                                            }
//
//                                                        }
//                                                        ModelLabourStatus modelLabourStatus=new ModelLabourStatus(currentDate11,modelLabour.getLabourId(),String.valueOf(AmtSum),modelLabour.getType());
//                                                        modelLabourStatusArrayList.add(modelLabourStatus);
//                                                    }else{
//                                                        ModelLabourStatus modelLabourStatus=new ModelLabourStatus(currentDate11,modelLabour.getLabourId(),"0",modelLabour.getType());
//                                                        modelLabourStatusArrayList.add(modelLabourStatus);
//                                                    }
//
//
//                                                }
//
//                                            }
////                                                        modelDateArrayList.add(new ModelDate(currentDate11));
////                                                        shortDateList.add(new ModelDate(currentDate11));
//
//
//                                            Log.e("SizeCheck", "SiteID:" + siteId1);
//                                            Log.e("SizeCheck", "Labour: " + labourList.size());
//                                            Log.e("SizeCheck", "Date: " + modelDateArrayList.size());
//                                            Log.e("SizeCheck", "Status: " + modelLabourStatusArrayList.size());
//
//
//                                            DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList, siteId1, siteName1);
//
////                                        getLabourList(currentDate11, currentDate1, "Today");
//
////                                                    Log.e("CHILDDNL", "" + countChild);
////                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Site");
////                                    reference1.child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate)
////                                            .addValueEventListener(new ValueEventListener() {
////                                                @Override
////                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                                    if (snapshot.exists()) {
////                                                        Log.e("CheckAtte",String.valueOf(siteId));
////                                                        binding.btnShow.setVisibility(View.GONE);
////                                                        binding.llDate.setVisibility(View.GONE);
////                                                        binding.tableview.setVisibility(View.VISIBLE);
////                                                        binding.txtMessage.setVisibility(View.GONE);
////                                                        Date c = Calendar.getInstance().getTime();
////
////
////                                                        Calendar c12 = Calendar.getInstance();
////                                                        c12.add(Calendar.DATE, 1);
////                                                        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
////                                                        String currentDate11 = df1.format(c);
////
////                                                        // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
////                                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
////                                                        Date temp = c12.getTime();
////                                                        String currentDate1 = sdf1.format(temp);
////                                                        Log.e("Labour1", currentDate11);
////                                                        Log.e("Labour2", currentDate1);
////                                                        getLabourList(currentDate11, currentDate1,"Today");
////
////                                                    } else {
////                                                        if(status.equals("ShowAttendance")) {
////                                                            progressDialog.dismiss();
////                                                            binding.tableview.setVisibility(View.GONE);
//////                            binding.btnShow.setVisibility(View.GONE);
//////                            binding.llDate.setVisibility(View.GONE);
//////                            binding.btnToday.setVisibility(View.GONE);
//////                            binding.btnCustom.setVisibility(View.GONE);
////                                                            binding.txtMessage.setText(R.string.you_have_not_uploaded_any_attendance);
////                                                            binding.txtMessage.setVisibility(View.VISIBLE);
////                                                        }
////                                                    }
////                                                }
////
////                                                @Override
////                                                public void onCancelled(@NonNull DatabaseError error) {
////
////                                                }
////                                            });
//                                        }
//                                    }
//                                }
//
//                            }
//                            getFirebaseToken();
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//
////                            for (int i = 0; i < modelDateArrayList.size(); i++) {
////                                Log.e("modelLabourStatus", "" + modelLabourStatusArrayList.size());
////                                for (int j = 0; j < labourList.size(); j++) {
////                                    Log.e("date", modelDateArrayList.get(i).getDate());
////                                    Log.e("date", "" + siteId);
////                                    Log.e("date", labourList.get(j).getLabourId());
////                                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getType());
////                                }
////                            }
////                            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
////                            Log.e("modelLabourStatus", "AfterLoop" + modelLabourStatusArrayList.size());
//                }
//
//
//
//
//
//
//
//
//            }
//
//
//
//
//
//
////
//
//
//        });
//        final RecyclerView.OnScrollListener[] scrollListeners = new RecyclerView.OnScrollListener[2];
//        scrollListeners[1] = new RecyclerView.OnScrollListener( )
//        {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
//            {
//                super.onScrolled(recyclerView, dx, dy);
//                binding.rvStatus.removeOnScrollListener(scrollListeners[1]);
//                binding.rvStatus.scrollBy(dx, dy);
//                binding.rvStatus.addOnScrollListener(scrollListeners[1]);
//            }
//        };
//        scrollListeners[0] = new RecyclerView.OnScrollListener( )
//        {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
//            {
//                super.onScrolled(recyclerView, dx, dy);
//                binding.rvShowAttendance.removeOnScrollListener(scrollListeners[0]);
//                binding.rvShowAttendance.scrollBy(dx, dy);
//                binding.rvShowAttendance.addOnScrollListener(scrollListeners[0]);
//            }
//        };
//        binding.rvShowAttendance.addOnScrollListener(scrollListeners[0]);
//        binding.rvStatus.addOnScrollListener(scrollListeners[1]);
//        SelfScrolListener firstOSL= new SelfScrolListener() {
//            @Override
//            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                Log.e("ViewIsScrolling","A"+viewIsScrolling);
//                Log.e("ViewIsScrolling","1"+firstIsTouched);
//
//                if (firstIsTouched) {
//                    if (viewIsScrolling == -1) {
//                        viewIsScrolling = 0;
//                    }
//                    if (viewIsScrolling == 0) {
//                        binding.rvStatus.scrollBy(dx, dy);
//                    }
//                }
//            }
//        };
//        SelfScrolListener secondOSL= new SelfScrolListener() {
//            @Override
//            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                Log.e("ViewIsScrolling","B"+viewIsScrolling);
//                Log.e("ViewIsScrolling","2"+secondIsTouched);
//                if(secondIsTouched){
//                    if (viewIsScrolling == -1) {
//                        viewIsScrolling = 1;
//                    }
//                    if (viewIsScrolling == 1) {
//                        Log.e("Condition","Aaegi????");
//                        binding.rvShowAttendance.scrollBy(dx, dy);
//                    }
//                }
//            }
//        };
//
//        binding.rvShowAttendance.addOnScrollListener(firstOSL);
//        binding.rvStatus.addOnScrollListener(secondOSL);


        showAttendanceArrayList = new ArrayList<>();
        labourList = new ArrayList<>();
//        getAttendanceMaster();

        binding.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!status.equals("")) {
                    if (status.equals("ShowAttendance")) {
                        binding.btnDownloadReport.setVisibility(View.GONE);
                    } else {
                        binding.btnDownloadReport.setVisibility(View.VISIBLE);
                    }
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Boolean f = true;
                try {
                    Date fDate = dateFormat.parse(fromDate);
                    Date tDate = dateFormat.parse(toDate);
                    if (tDate.before(fDate)) {
                        Toast.makeText(ShowPaymentActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(ShowPaymentActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(ShowPaymentActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                } else if (f) {
                    modelLabourStatusArrayList.clear();
                    getLabourList(fromDate, toDate, "Custom");
                }
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_position"));

    }
    private void getFirebaseToken() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("token").getValue(String.class)!=null){
                    String token=snapshot.child("token").getValue(String.class);
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    JSONObject js = new JSONObject();
                    try {
                        JSONObject jsonobject_notification = new JSONObject();

                        jsonobject_notification.put("title", "Report has been generated");
                        jsonobject_notification.put("body", "Your reports are generated. Check your downloads folder");




                        JSONObject jsonobject_data = new JSONObject();
                        jsonobject_data.put("imgurl", "https://firebasestorage.googleapis.com/v0/b/haajiri-register.appspot.com/o/FCMImages%2Fopt2.png?alt=media&token=f4e37ac3-a1ff-417f-a0c7-3e5445515505");

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
                            progressDialog.dismiss();


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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForAttendance() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);
        Log.e("CurrentDate", currentDate);
        Log.e("CurrentDate", "site1:" + String.valueOf(siteId));
        if (status.equals("ShowAttendance")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(currentDate)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.e("CheckAtte", String.valueOf(siteId));
                                binding.btnShow.setVisibility(View.GONE);
                                binding.llDate.setVisibility(View.GONE);
                                binding.tableview.setVisibility(View.VISIBLE);
                                binding.txtMessage.setVisibility(View.GONE);
                                Date c = Calendar.getInstance().getTime();


                                Calendar c12 = Calendar.getInstance();
                                c12.add(Calendar.DATE, 1);
                                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                String currentDate11 = df1.format(c);

                                // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                                Date temp = c12.getTime();
                                String currentDate1 = sdf1.format(temp);
                                Log.e("Labour1", currentDate11);
                                Log.e("Labour2", currentDate1);
                                getLabourList(currentDate11, currentDate1, "Today");

                            } else {
                                if (status.equals("ShowAttendance")) {
                                    progressDialog.dismiss();
                                    binding.tableview.setVisibility(View.GONE);
//                            binding.btnShow.setVisibility(View.GONE);
//                            binding.llDate.setVisibility(View.GONE);
//                            binding.btnToday.setVisibility(View.GONE);
//                            binding.btnCustom.setVisibility(View.GONE);
                                    binding.txtMessage.setText(R.string.you_have_not_payment);
                                    binding.txtMessage.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        } else {

            modelDateArrayList = new ArrayList<>();
            shortDateList = new ArrayList<>();
//            for(int i=1;i<siteAdminList.size();i++) {
//                if (siteAdminList.get(i).getSelected()) {
//                    siteId = siteAdminList.get(i).getSiteId();
//                    Log.e("SiteDNL", String.valueOf(siteId));
//        }}
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelSite modelSite = ds.getValue(ModelSite.class);
                        Log.e("siteAdminList", "Size" + siteAdminList.size());
                        for (int i = 0; i < siteAdminList.size(); i++) {
                            if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
                                Log.e("SiteFound", "" + siteAdminList.get(i).getSiteId());
                                if (siteAdminList.get(i).getSelected()) {
                                    Log.e("SiteFound", "2::::" + siteAdminList.get(i).getSiteId());
                                    long siteId1 = siteAdminList.get(i).getSiteId();
                                    String siteName1=siteAdminList.get(i).getSiteName();
                                    long countChild = snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount();
                                    if (countChild <= 0) {
                                        Toast.makeText(ShowPaymentActivity.this, "No Payments Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        modelLabourStatusArrayList.clear();
                                        labourList.clear();
                                        modelDateArrayList.clear();
                                        shortDateList.clear();
                                        binding.btnShow.setVisibility(View.GONE);
                                        binding.llDate.setVisibility(View.GONE);
                                        binding.tableview.setVisibility(View.VISIBLE);
                                        binding.txtMessage.setVisibility(View.GONE);
                                        Date c = Calendar.getInstance().getTime();


                                        Calendar c12 = Calendar.getInstance();
                                        c12.add(Calendar.DATE, 1);
                                        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                        String currentDate11 = df1.format(c);

                                        // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                                        Date temp = c12.getTime();
                                        String currentDate1 = sdf1.format(temp);
                                        Log.e("Labour1", currentDate11);
                                        Log.e("Labour2", currentDate1);
                                        Log.e("NullCheck","A:"+snapshot.child(String.valueOf(siteId1)).child("Labours").getChildrenCount());
                                        modelDateArrayList.add(new ModelDate(currentDate11));
                                        shortDateList.add(new ModelDate(currentDate11));
                                        for(DataSnapshot ds1:  snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()){
                                            ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
                                            labourList.add(modelLabour);


                                            Log.e("NULLCHEXK",String.valueOf(siteId1));
                                            Log.e("NULLCHEXK",currentDate11);
                                            Log.e("NULLCHEXK",modelLabour.getLabourId());
                                            Log.e("CurrentDate11",currentDate11);
                                            if(snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount()>0){
                                                int countLabour=0;
                                                int AmtSum=0;
                                                String amount,reason="";
                                                Log.e("CurrentDate11",currentDate11);
                                                for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildren()){
                                                    ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
                                                    Log.e("LPay",labourPayment.getLabourId());
                                                    Log.e("LPay","1:"+modelLabour.getLabourId());
                                                    if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
                                                        amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
                                                        AmtSum=AmtSum+Integer.parseInt(amount);
                                                        if(snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).child(labourPayment.getTimestamp()).child("Reason")!=null){
                                                            reason=snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).child(labourPayment.getTimestamp()).child("Reason").getValue(String.class);
                                                        }else{
                                                            reason="";
                                                        }

                                                    }

                                                }
                                                ModelLabourStatus modelLabourStatus=new ModelLabourStatus(currentDate11,modelLabour.getLabourId(),String.valueOf(AmtSum),modelLabour.getType(),reason);
                                                modelLabourStatusArrayList.add(modelLabourStatus);
                                            }else{
                                                ModelLabourStatus modelLabourStatus=new ModelLabourStatus(currentDate11,modelLabour.getLabourId(),"0",modelLabour.getType(),"");
                                                modelLabourStatusArrayList.add(modelLabourStatus);
                                            }


                                        }
                                        DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList,siteId1,siteName1);

//                                        getLabourList(currentDate11, currentDate1, "Today");
                                    }
                                    Log.e("CHILDDNL", "" + countChild);
//                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Site");
//                                    reference1.child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate)
//                                            .addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.exists()) {
//                                                        Log.e("CheckAtte",String.valueOf(siteId));
//                                                        binding.btnShow.setVisibility(View.GONE);
//                                                        binding.llDate.setVisibility(View.GONE);
//                                                        binding.tableview.setVisibility(View.VISIBLE);
//                                                        binding.txtMessage.setVisibility(View.GONE);
//                                                        Date c = Calendar.getInstance().getTime();
//
//
//                                                        Calendar c12 = Calendar.getInstance();
//                                                        c12.add(Calendar.DATE, 1);
//                                                        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                                                        String currentDate11 = df1.format(c);
//
//                                                        // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//                                                        Date temp = c12.getTime();
//                                                        String currentDate1 = sdf1.format(temp);
//                                                        Log.e("Labour1", currentDate11);
//                                                        Log.e("Labour2", currentDate1);
//                                                        getLabourList(currentDate11, currentDate1,"Today");
//
//                                                    } else {
//                                                        if(status.equals("ShowAttendance")) {
//                                                            progressDialog.dismiss();
//                                                            binding.tableview.setVisibility(View.GONE);
////                            binding.btnShow.setVisibility(View.GONE);
////                            binding.llDate.setVisibility(View.GONE);
////                            binding.btnToday.setVisibility(View.GONE);
////                            binding.btnCustom.setVisibility(View.GONE);
//                                                            binding.txtMessage.setText(R.string.you_have_not_uploaded_any_attendance);
//                                                            binding.txtMessage.setVisibility(View.VISIBLE);
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });

                                    getFirebaseToken();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


    }

    private void showDownloadSiteDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowPaymentActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_download_spinner_dialog, null);
        rv_site_select = (RecyclerView) mView.findViewById(R.id.rv_site_select);
        Button btn_ok = (Button) mView.findViewById(R.id.btn_ok);
        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
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
                    Toast.makeText(ShowPaymentActivity.this, "Select Atleast one site to download report", Toast.LENGTH_SHORT).show();
                }


            }
        });


        alertDialog.show();

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


    private void showPaymentDialog() {

        file = null;
        fos = null;
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowPaymentActivity.this);
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
        getPromoCode(spinner_promo);
        Log.e("Dnl_file", dnl_file_type);
//        if(dnl_file_type.equals("Pdf")){
//            amount = Math.round((modelDateArrayList.size()*5));
//        }else{
//            amount = Math.round((modelDateArrayList.size()*10));
//        }


//        AmountTemp = amount;
        spinner_promo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    promo_spinner_position = i;
                    promo_title = promoArrayList.get(i).getTitle();

                    Float dis = Float.valueOf(promoArrayList.get(i).getDiscount());


                    Float d_amount = Float.valueOf(Math.round(modelDateArrayList.size() * 4.5));
                    dis = dis.floatValue() / 100 * amount;
                    AmountTemp = Math.round(amount - dis);

//                           amount= (int) (amount-((promoArrayList.get(i).getDiscount()/100)*amount));

                    Log.e("Amt", "AD" + amount);
                    Log.e("Amt", "D" + (promoArrayList.get(i).getDiscount() / 100));
                    Log.e("Amt", "DF" + dis.floatValue() / 100 * amount);
                    Log.e("Amt", "Af" + d_amount);

                    txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(AmountTemp));
                    txt_heading.setText(getString(R.string.your_file_one_click_away) + " " + String.valueOf(AmountTemp) + " " + getString(R.string.your_have_tom_make_payment));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        txt_heading.setText(getString(R.string.your_file_one_click_away) + " " + String.valueOf(AmountTemp) + " " + getString(R.string.your_have_tom_make_payment));
        txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(AmountTemp));
        btn_cancel = mView.findViewById(R.id.btn_cancel);
        btn_pay = mView.findViewById(R.id.btn_pay);
        txt_file_type.setText(getString(R.string.attendance_report));
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

                    Thread createOrderId=new Thread(new ShowPaymentActivity.createOrderIdThread(AmountTemp));
                    createOrderId.start();
                } else {
//                    DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList,siteId,siteName);
                    Thread createOrderId=new Thread(new ShowPaymentActivity.createOrderIdThread(AmountTemp));
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



    private void ExcelToPdf(File file, FileOutputStream fos, HSSFWorkbook workbook, long siteId1, String siteName1) throws DocumentException {
        ArrayList<ModelDate> modelDateTempArrayList=new ArrayList<>();
        modelDateTempArrayList=modelDateArrayList;

        int startPoint=0;
        int endpoint=15;
        FileInputStream input_document = null;
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "HajiriRegister_"+""+"PaymentReport_" + timestamp+ ".pdf";
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
        com.itextpdf.text.Document iText_xls_2_pdf ;
        if(modelDateArrayList.size()>15){
            iText_xls_2_pdf=new Document();
            iText_xls_2_pdf.setPageSize(new Rectangle(40*modelDateArrayList.size(),297));

        }else{
            iText_xls_2_pdf=new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 0f);
        }


//TODO BREAk By Sizr /15
//TODO BREAk By Sizr /15

        try {
            PdfWriter writer=PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(ShowPaymentActivity.this,siteId1,siteName1,modelDateArrayList,"Workers Advances Report");
            writer.setPageEvent(event);
        } catch (DocumentException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();
        }
        iText_xls_2_pdf.open();
        com.itextpdf.text.Font fontBold=new com.itextpdf.text.Font();
        fontBold.setStyle(com.itextpdf.text.Font.BOLD|com.itextpdf.text.Font.UNDERLINE);
        fontBold.setSize(12);
        fontBold.setColor(BaseColor.BLACK);
        fontBold.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontHeading=new com.itextpdf.text.Font();
        fontHeading.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeading.setSize(12);
        fontHeading.setColor(BaseColor.BLACK);
        fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontHeaderNormal=new com.itextpdf.text.Font();
        fontHeading.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeading.setSize(12);
        fontHeading.setColor(BaseColor.BLACK);
        fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontHeaderBold=new com.itextpdf.text.Font();
        fontHeaderBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderBold.setSize(14);
        fontHeaderBold.setColor(BaseColor.BLACK);
        fontHeaderBold.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontDateHeading=new com.itextpdf.text.Font();
        fontDateHeading.setStyle(com.itextpdf.text.Font.BOLD);
        fontDateHeading.setSize(11);
        fontDateHeading.setColor(BaseColor.BLACK);
        fontDateHeading.setFamily(String.valueOf(Paint.Align.CENTER));

        PdfPTable my_table_header = new PdfPTable( 20);
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
        table_cell_industry_name=new PdfPCell(new Phrase("Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""),fontHeaderBold));
        table_cell_company_name=new PdfPCell(new Phrase("Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName",""),fontHeaderBold));
        table_cell_siteId=new PdfPCell(new Phrase( "Site Id: " + siteId1,fontHeaderNormal));
        table_cell_siteName=new PdfPCell(new Phrase("Site Name: " + siteName1,fontHeaderNormal));
        table_cell_generated_on=new PdfPCell(new Phrase("Generated On: " +currentDate,fontHeaderNormal));
        table_cell_workers_advances_report=new PdfPCell(new Phrase("Workers Advances Report",fontHeaderBold));
        table_cell_from=new PdfPCell(new Phrase("From:"+modelDateArrayList.get(0).getDate(),fontHeaderNormal));
        table_cell_to=new PdfPCell(new Phrase("To:"+modelDateArrayList.get(modelDateArrayList.size()-1).getDate(),fontHeaderNormal));
        table_cell_total_no_of_days=new PdfPCell(new Phrase("Total No of days:"+modelDateArrayList.size(),fontHeaderNormal));

        table_cell_industry_name.setColspan(7);
        table_cell_industry_name.setBorder(Rectangle.NO_BORDER);
        table_cell_industry_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_generated_on.setColspan(7);
        table_cell_generated_on.setBorder(Rectangle.NO_BORDER);
        table_cell_generated_on.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_company_name.setColspan(6);
        table_cell_company_name.setBorder(Rectangle.NO_BORDER);
        table_cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_siteName.setColspan(7);
        table_cell_siteName.setBorder(Rectangle.NO_BORDER);
        table_cell_siteName.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_workers_advances_report.setColspan(7);
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
        table_cell_total_no_of_days.setColspan(14);
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









        PdfPTable my_table = new PdfPTable( modelDateArrayList.size()+10);
        my_table.setWidthPercentage(95);
//        float[] value=new float[modelDateArrayList.size()+5];
//        for(int i=0;i<modelDateArrayList.size()+5;i++){
//            if(i==0){
//              value[i]=5;
//            }else if(i==1){
//               value[i]=10;
//            }else if(i==2){
//                value[i]=15;
//            }else if(i==3){
//                value[i]=10;
//            }else if(i==4){
//                value[i]=10;
//            }else if(i>4){
//                value[i]=50/modelDateArrayList.size();
//            }
//
//        }
//        my_table.setWidths(new float[] {5,8,13,7,7,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4});

//        my_table.setHorizontalAlignment(Element.ALIGN_LEFT);



        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;

        //Loop through rows.


        Row row = rowIterator.next();
        Row row2 = my_worksheet.getRow(6);
        if(row2!=null){
            Iterator<Cell> cellIterator = row2.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); //Fetch CELL

                switch (cell.getCellType()) { //Identify CELL type
                    //you need to add more code here based on
                    //your requirement / transformations
                    case Cell.CELL_TYPE_STRING:
                        //Push the data from Excel to PDF Cell




                            if(cell.getColumnIndex()==1){
                                table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(),fontHeading));
                                table_cell.setColspan(2);
                            }else  if(cell.getColumnIndex()==2){
                                table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(),fontHeading));
                                table_cell.setColspan(3);
                            }else  if(cell.getColumnIndex()==3){
                                table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(),fontHeading));
                                table_cell.setColspan(2);
                            }else  if(cell.getColumnIndex()==4){
                                table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(),fontHeading));
                                table_cell.setColspan(2);
                            }else{
                                table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(),fontDateHeading));
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
        for (int i = 0; i < labourList.size() ; i++) {
            Log.e("ValueI123", "" + i);



            Row row1 = my_worksheet.getRow(i + 7);
            if(row1!=null){
                Iterator<Cell> cellIterator = row1.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next(); //Fetch CELL

                    switch (cell.getCellType()) { //Identify CELL type
                        //you need to add more code here based on
                        //your requirement / transformations
                        case Cell.CELL_TYPE_STRING:
                            //Push the data from Excel to PDF Cell



                                if (cell.getColumnIndex() == 4) {
                                    if (Integer.parseInt(cell.getStringCellValue()) > 0) {
                                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue(), fontBold));
                                        table_cell.setColspan(2);

                                        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table_cell.setFixedHeight(25);
                                        table_cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    } else {
                                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                                        table_cell.setColspan(2);

                                        table_cell.setFixedHeight(25);
                                        table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        table_cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    }
                                } else {
                                    table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                                    if(cell.getColumnIndex()==1){
                                        table_cell.setColspan(2);
                                    }else  if(cell.getColumnIndex()==2){
                                        table_cell.setColspan(3);
                                    }else  if(cell.getColumnIndex()==3){
                                        table_cell.setColspan(2);
                                    }
                                    table_cell.setFixedHeight(25);
                                    table_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table_cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                }

                                Log.e("cellValue", cell.getStringCellValue());
                                Log.e("cellValue", "" + cell.getColumnIndex());
                                //feel free to move the code below to suit to your needs
                                my_table.addCell(table_cell);
                                break;

                    }
                    //next line
                }
            }

//            Cell cell1 = row1.getCell(2);


        }















        PdfPTable my_table1 = new PdfPTable(modelDateArrayList.size() +10);
        my_table1.setWidthPercentage(95);
//        my_table1.setWidths(new float[] {5,8,13,7,7,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4});
//        my_table1.setTotalWidth(new float[]{50f,75f});
        //We will use the object below to dynamically add new data to the table
//        PdfPCell table_cell1=new PdfPCell();
//
//        //Loop through rows.
//
//
//        Row row1 = my_worksheet.getRow(labourList.size() + 8);
//
//
//
//        if(row1!=null){
//            Iterator<Cell> cellIterator = row1.cellIterator();
//            while (cellIterator.hasNext()) {
//                Cell cell = cellIterator.next(); //Fetch CELL
//
//
//                switch (cell.getCellType()) { //Identify CELL type
//                    //you need to add more code here based on
//                    //your requirement / transformations
//                    case Cell.CELL_TYPE_STRING:
//                        if(cell.getColumnIndex()==3){
//                            table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            table_cell1 = new PdfPCell(new Phrase("",fontBold));
//                        }else if(cell.getColumnIndex()==4){
//                            table_cell1 = new PdfPCell(new Phrase(cell.getStringCellValue(),fontBold));
//                        }else{
//                            table_cell1 = new PdfPCell(new Phrase(cell.getStringCellValue()));
//                        }
//                        //Push the data from Excel to PDF Cell
//
//                        //feel free to move the code below to suit to your needs
//                        my_table1.addCell(table_cell1);
//                        break;
//                }
//                //next line
//            }
//
//        }

        PdfPCell table_cell1 = new PdfPCell();
        table_cell1.setColspan(8);
        table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell1.setPhrase(new Phrase("Total Advances",fontHeading));
        my_table1.addCell(table_cell1);
        int toatlAmt=0;
        for(int k=0;k<modelLabourStatusArrayList.size();k++){
            if (!modelLabourStatusArrayList.get(k).getStatus().equals("0")) {
                toatlAmt=toatlAmt+Integer.parseInt(modelLabourStatusArrayList.get(k).getStatus());
            }
        }
        PdfPCell table_cell3 = new PdfPCell();

        table_cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell3.setPhrase(new Phrase(String.valueOf(toatlAmt),fontHeading));
        table_cell3.setColspan(2);
        my_table1.addCell(table_cell3);

        for (int i = 0; i < modelLabourStatusArrayList.size(); i = i + labourList.size()) {

            int AmtSum=0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + modelLabourStatusArrayList.get(i + j).getStatus().equals("P"));
                if (!modelLabourStatusArrayList.get(i + j).getStatus().equals("0")) {
                    AmtSum=AmtSum+Integer.parseInt(modelLabourStatusArrayList.get(i+j).getStatus());
                }

                if (j == labourList.size() - 1) {
                    PdfPCell table_cell2 = new PdfPCell();
                    table_cell2.setPhrase(new Phrase(String.valueOf(AmtSum),fontHeading));
                    table_cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    my_table1.addCell(table_cell2);
                }


            }


//            int y=i/10;
//            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
        }



//        for(int s=0;s<labourList.size();s++){
////            int CountPresent=0;
//            int  AmtSum=0;
//            float countHalf=0;
//            float countTotal=0;
//            for(int j=s;j<modelLabourStatusArrayList.size();j=j+labourList.size()){
//                if(!modelLabourStatusArrayList.get(j).getStatus().equals("0")){
//                    AmtSum=AmtSum+Integer.parseInt(modelLabourStatusArrayList.get(j).getStatus());
//
//                }
//
//            }
////            countTotal=countPresent+(countHalf/2);
//            Log.e("Stats123",modelLabourStatusArrayList.get(s).getLabourId()+":::"+AmtSum+"/"+modelDateArrayList.size());
//
//            PdfPCell table_cell2 = new PdfPCell();
//            table_cell2.setPhrase(new Phrase(String.valueOf(AmtSum),fontBold));
//            my_table1.addCell(table_cell2);
//
//        }


        //Finally add the table to PDF document
        Paragraph p4 = new Paragraph("\n");
        p4.setAlignment(Paragraph.ALIGN_CENTER);
        try {

//            iText_xls_2_pdf.add(my_table_header);
//            iText_xls_2_pdf.add(p4);
            iText_xls_2_pdf.add(my_table);
            iText_xls_2_pdf.add(p4);
            iText_xls_2_pdf.add(my_table1);
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

    public void onPDFDocumentClose(File file) {
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getPromoCode(Spinner spinner_promo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Promo");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promoArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPromo modelPromo = ds.getValue(ModelPromo.class);
                    promoArrayList.add(modelPromo);
                }
                Log.e("Promo", "" + promoArrayList.size());
                promoArrayList.add(0, new ModelPromo(null, null, null, "Select Promo", null, 0, 0));
                ShowPaymentActivity.PromoSpinnerAdapter promoSpinnerAdapter = new ShowPaymentActivity.PromoSpinnerAdapter();
                spinner_promo.setAdapter(promoSpinnerAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteAdminList.clear();
                Log.e("Snap", snapshot.getKey());

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    Log.e("Snap", "" + snapshot.child(String.valueOf(modelSite.getSiteId())).hasChild("Labours"));
                    modelSite.setSelected(false);
                    if (snapshot.child(String.valueOf(modelSite.getSiteId())).hasChild("Labours")) {
                        siteAdminList.add(modelSite);
                    }


                }

                Log.e("siteAdminList", "" + siteAdminList.size());
                if (siteAdminList.size() == 0) {
                    binding.spinnerSelectSite.setVisibility(View.GONE);
                    binding.llSelectPeriod.setVisibility(View.GONE);
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    binding.llDate.setVisibility(View.GONE);
                    binding.btnShow.setVisibility(View.GONE);
                    binding.btnDownloadReport.setVisibility(View.GONE);
                    binding.noDataToShow.setVisibility(View.VISIBLE);
                } else {
//                    siteAdminList.add(0,new ModelSite(getString(R.string.select_site),0,false));
                    if(status.equals("ShowAttendance")){
                        siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0, false));
                    }else{
                        siteAdminList.add(0, new ModelSite("All Sites", 0, false));
                    }
                    if (status.equals("ShowAttendance")) {
                        ShowPaymentActivity.SiteSpinnerAdapter siteSpinnerAdapter = new ShowPaymentActivity.SiteSpinnerAdapter();
                        binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                        siteId = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId();
                        siteName = "";
                    }

                    binding.spinnerSelectSite.setVisibility(View.VISIBLE);
                    binding.llSelectPeriod.setVisibility(View.VISIBLE);
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    binding.llDate.setVisibility(View.GONE);
                    binding.btnShow.setVisibility(View.GONE);
                    binding.btnDownloadReport.setVisibility(View.VISIBLE);
                    binding.noDataToShow.setVisibility(View.GONE);
                    adapterSiteSelect = new AdapterSiteSelect(ShowPaymentActivity.this, siteAdminList);
                    progressDialog.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void arrangeLabourByType(ArrayList<ModelLabour> labourList) {
        ArrayList<ModelLabour> skilledLabour = new ArrayList<>();
        ArrayList<ModelLabour> unSKilledLabour = new ArrayList<>();
        labourByType.clear();
        for (int i = 0; i < labourList.size(); i++) {
            if (labourList.get(i).getType().equals("Skilled")) {
                skilledLabour.add(labourList.get(i));
            } else {
                unSKilledLabour.add(labourList.get(i));
            }
        }
        for (int i = 0; i < skilledLabour.size(); i++) {
            labourByType.add(skilledLabour.get(i));
        }
        for (int i = 0; i < unSKilledLabour.size(); i++) {
            labourByType.add(unSKilledLabour.get(i));
        }
        for (int i = 0; i < labourByType.size(); i++) {
            Log.e("LabourByType", "" + labourByType.get(i).getLabourId());
        }

    }

    private void DownloadExcel(ArrayList<ModelLabour> labourList,
                               ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelLabourStatus> modelLabourStatusArrayList,
                               ArrayList<ModelLabour> list, ArrayList<ModelDate> shortDateList, long siteId1, String siteName1) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
        sheet.setColumnWidth(2,11*256);
        sheet.setColumnWidth(3,11*256);
        createHeaderRow(sheet, modelDateArrayList, shortDateList,siteId1,siteName1);
        createLAbourData(sheet, labourList);
        createAttendanceData(sheet, labourList, modelDateArrayList, modelLabourStatusArrayList);
        createFooter(sheet, labourList, modelDateArrayList, modelLabourStatusArrayList);

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String timestamp = "" + System.currentTimeMillis();
            Log.e("StrPath", str_path);
            file = new File(str_path, "HajiriRegister_"+""+"PaymentReport_" + timestamp+ ".xls");

//            fos = new FileOutputStream(file);
            Log.e("FilePath", file.getAbsolutePath().toString());
//            dnl_file_type="xls";

            HSSFPatriarch dp = sheet.createDrawingPatriarch();
            int size=0;
            if(labourList.size()>10){
                size=labourList.size();
            }else{
                size=8;
            }

            HSSFClientAnchor anchor = new HSSFClientAnchor
                    (0, 0, 650, 255, (short) 2, size, (short) 13, size+3);


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

            txtbox.setLineStyle(HSSFShape.LINESTYLE_NONE);
            txtbox.setNoFill(true);

            if (dnl_file_type.equals("pdf")) {
//                workbook.write(fos);

                ExcelToPdf(file, fos, workbook,siteId1,siteName1);

            } else {
//                Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//                createOrderId.start();
                str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                Log.e("StrPath", str_path);
                file = new File(str_path, "HajiriRegister_"+""+"PaymentReport_" + timestamp+ ".xls");

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
                ExcelToPdf(file, fos, workbook,siteId1,siteName1);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {

        }

//        int amount=100;
        Log.e("Amt", "Send" + amount);


//        try {
//
//        }  catch (RazorpayException e) {
//            if(e!=null){
//                Log.e("Exception","RazorPay+:::"+e.getMessage());
//            }
//            progressDialog.dismiss();
//            e.printStackTrace();
//        }catch(JSONException e){
//            progressDialog.dismiss();
//            if (e != null) {
//                Log.e("Exception","JSON"+e.getMessage());
//            }
//        }

//            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ShowPaymentActivity.this);
//                builder.setTitle("Success")
//                        .setMessage("Your Download is one click way. You need to make a payment of INR 30 to get your file in Excel Format. Click OK to " +
//                                "proceed to payment menu")
//                        .setCancelable(true)
//                        .setPositiveButton("OK", (dialogInterface, i) -> {
//
//
//
//                            // initialize Razorpay account.
////                            Checkout checkout = new Checkout();
////
////                            // set your id as below
////                            checkout.setKeyID("rzp_test1");
////
////
////                            // set image
////                            checkout.setImage(R.drawable.logo_toolbar);
////
////                            // initialize json object
////                            JSONObject object = new JSONObject();
////                            try {
////                                // to put name
////                                object.put("name", getString(R.string.app_name));
////
////                                // put description
////                                object.put("description", "Download Charge");
////
////                                // to set theme color
////                                object.put("theme.color", "");
////
////                                // put the currency
////                                object.put("currency", "INR");
////
////                                // put amount
////                                object.put("amount", amount);
////
////                                // put mobile number
////                                object.put("prefill.contact", "");
////
////                                // put email
////                                object.put("prefill.email", "");
////
////                                // open razorpay to checkout activity
////                                checkout.open(ShowPaymentActivity.this, object);
////                            } catch (JSONException e) {
////                                e.printStackTrace();
////                            }
//
//                        })
//                        .setNegativeButton("Cancel", ((dialogInterface, i) ->
//                                dialogInterface.dismiss()));
//
//                builder.show();
//


    }

    private void createOrderId(int amount, AlertDialog.Builder builder) throws RazorpayException, JSONException {


        // Handle Exception


    }

    private void createFooter(Sheet sheet, ArrayList<ModelLabour> labourList,
                              ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelLabourStatus> modelLabourStatusArrayList) {
        int totalSkilledCount = 0, totalUnskilledCount = 0, presentSkilled = 0, presentUnskilled = 0;
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
        sheet.createRow(labourList.size() + 8);

        for (int i = 0; i < modelLabourStatusArrayList.size(); i = i + labourList.size()) {
            presentSkilled = 0;
            presentUnskilled = 0;
            int AmtSum=0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + modelLabourStatusArrayList.get(i + j).getStatus().equals("P"));
                if (!modelLabourStatusArrayList.get(i + j).getStatus().equals("0")) {
                    AmtSum=AmtSum+Integer.parseInt(modelLabourStatusArrayList.get(i+j).getStatus());
                }

                if (j == labourList.size() - 1) {
                    int y = i / labourList.size();
                    Row rowPresentSkilled = sheet.getRow(labourList.size() + 8);
                    Cell Status = rowPresentSkilled.createCell(y + 5);
                    Status.setCellStyle(cellStyle);
                    Status.setCellValue(String.valueOf(AmtSum));
                }


            }


//            int y=i/10;
//            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
        }
        Row row21=sheet.getRow(labourList.size() + 8);
        float countPresent=0;
        float countHalf=0;
        float countTotal=0;
        int AmtSum=0;

        for(int s=0;s<modelLabourStatusArrayList.size();s++){
            if(!modelLabourStatusArrayList.get(s).getStatus().equals("0")){
                AmtSum=AmtSum+Integer.parseInt(modelLabourStatusArrayList.get(s).getStatus());
            }

        }
        Row rowPresentSkilled = sheet.getRow(labourList.size() + 8);
        Cell Status1 = rowPresentSkilled.createCell(0);
        rowPresentSkilled.createCell(1).setCellStyle(cellStyle);
        rowPresentSkilled.createCell(2).setCellStyle(cellStyle);
        rowPresentSkilled.createCell(3).setCellStyle(cellStyle);
        Status1.setCellStyle(cellStyle);
        Status1.setCellValue("Total Advances");

        CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size() + 8, labourList.size()+8 , 0, 3);
        sheet.addMergedRegion(cellMerge2);

        Cell Status = rowPresentSkilled.createCell(4);
        Status.setCellStyle(cellStyle);
        Status.setCellValue(String.valueOf(AmtSum));





        Row row = sheet.createRow(labourList.size() + 10);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle2);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Generated by: " + " " + "Hajiri Register";
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(labourList.size() + 10, labourList.size() + 12, 0, modelDateArrayList.size() + 4);
        sheet.addMergedRegion(cellMerge);

        String date_val1 = "Powered by: " + " " + "Skill Zoomers";

        Row row1 = sheet.createRow(labourList.size() + 14);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue(date_val1);

        CellRangeAddress cellMerge1 = new CellRangeAddress(labourList.size() + 14, labourList.size() + 16, 0, modelDateArrayList.size() + 4);
        sheet.addMergedRegion(cellMerge1);

        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ " + " " + "Skill Zoomers";

        Row row2 = sheet.createRow(labourList.size() + 18);
        Cell cellHeading1 = row2.createCell(0);

        cellHeading1.setCellStyle(cellStyle2);
        cellHeading1.setCellValue(date_val2);

        CellRangeAddress cellMerge3 = new CellRangeAddress(labourList.size() + 18, labourList.size() + 19, 0, modelDateArrayList.size() + 4);
        sheet.addMergedRegion(cellMerge3);




    }

    private void createAttendanceData(Sheet sheet, ArrayList<ModelLabour> labourList,
                                      ArrayList<ModelDate> modelDateArrayList,
                                      ArrayList<ModelLabourStatus> modelLabourStatusArrayList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setColor(HSSFColor.RED.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setUnderline(Font.U_SINGLE);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);

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

        CellStyle cellStyleBlackNormal = sheet.getWorkbook().createCellStyle();
        Font fontPresentBlackNormal = sheet.getWorkbook().createFont();
        fontPresentBlackNormal.setColor(HSSFColor.BLACK.index);
        fontPresentBlackNormal.setFontHeightInPoints((short) 12);
        cellStyleBlackNormal.setFont(fontPresent);
        cellStyleBlackNormal.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleBlackNormal.setBorderBottom((short) 1);
        cellStyleBlackNormal.setBorderTop((short) 1);
        cellStyleBlackNormal.setBorderLeft((short) 1);
        cellStyleBlackNormal.setBorderRight((short) 1);
        cellStyleBlackNormal.setFont(fontPresent);
        int countPresent = 0, cloneJ = 0, k = 0;
        for (int i = 0; i < modelLabourStatusArrayList.size(); i = i + labourList.size()) {
            countPresent = 0;
            for (int j = 0; j < labourList.size(); j++) {
                cloneJ = j;
                Row row = sheet.getRow(j + 7);
                row.setHeightInPoints(25);
                k = i / labourList.size();
                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
                Cell cell = row.createCell(k + 5);
                Log.e("CheckStatus", "" + modelLabourStatusArrayList.get(i + j).getStatus().equals("P"));
                if (!modelLabourStatusArrayList.get(i + j).getStatus().equals("0")) {
                    countPresent += 1;
                    cell.setCellStyle(cellStylePresent);
                } else {
                    cell.setCellStyle(cellStyleBlackNormal);
                }

                cell.setCellValue(modelLabourStatusArrayList.get(i + j).getStatus());

            }
            for(int s=0;s<modelLabourStatusArrayList.size();s++){
                Log.e("Stats123 ", "Date:"+modelLabourStatusArrayList.get(s).getDate()+" ID:"+modelLabourStatusArrayList.get(s).getLabourId()
                +"Status: "+modelLabourStatusArrayList.get(s).getStatus()+"Value: "+s);
            }

//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
            Log.e("ValueOfJ", "I" + i);
            Log.e("ValueOfJ", "C" + countPresent);
//            int y=i/10;



//
        }
        for(int s=0;s<labourList.size();s++){
//            int CountPresent=0;
            int  AmtSum=0;
            float countHalf=0;
            float countTotal=0;
            for(int j=s;j<modelLabourStatusArrayList.size();j=j+labourList.size()){
                if(!modelLabourStatusArrayList.get(j).getStatus().equals("0")){
                    AmtSum=AmtSum+Integer.parseInt(modelLabourStatusArrayList.get(j).getStatus());
                }

            }
//            countTotal=countPresent+(countHalf/2);
            Log.e("Stats123",modelLabourStatusArrayList.get(s).getLabourId()+":::"+AmtSum+"/"+modelDateArrayList.size());
            Row rowPresentStatus = sheet.getRow((s+7));
            Cell cellPresentStatus=rowPresentStatus.createCell(4);
            String status=String.valueOf(AmtSum);
            if(AmtSum>0){
                cellPresentStatus.setCellStyle(cellStyle);
            }else{
                cellPresentStatus.setCellStyle(cellStyleBlackNormal);
            }

            cellPresentStatus.setCellValue(status);
        }
    }

    private void createLAbourData(Sheet sheet, ArrayList<ModelLabour> labourList) {
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
        for (int i = 0; i < labourList.size(); i++) {
            Row row = sheet.createRow(i + 7);
            row.setHeightInPoints(25);
            Cell cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(String.valueOf(i + 1));
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(cellStyle);
            cell1.setCellValue(labourList.get(i).getLabourId());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(cellStyle);
            cell2.setCellValue(labourList.get(i).getName());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(cellStyle);
            cell3.setCellValue(labourList.get(i).getType());


        }
    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelDate> dateModelArrayList, ArrayList<ModelDate> shortDateList, long siteId1, String siteName1) {
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
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);



        Row row = sheet.createRow(0);
        Cell cellFullName = row.createCell(0);


        cellFullName.setCellStyle(cellStyle1);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName","")+
                "\n"+"Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName","");

        cellFullName.setCellValue(date_val);
        int size=0;
        if(dateModelArrayList.size()+5<8){
            size=8;
        }else{
            size=dateModelArrayList.size()+5;
        }

        CellRangeAddress cellMerge = new CellRangeAddress(0, 3, 0, 6);
        sheet.addMergedRegion(cellMerge);

        Cell cellFullName1=row.createCell(7);
        cellFullName1.setCellStyle(cellStyle1);
        String date_val1="Generated On: " +currentDate+"\n"+ "Site Id: " + siteId1 +
                "\t\t\t\t\t Site Name: " + siteName1;
        cellFullName1.setCellValue(date_val1);
        int size1=0;
        if(modelDateArrayList.size()+4<10){
            size1=12;
        }else{
            size1=modelDateArrayList.size()+4;
        }

        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 3, 7, 10);
        sheet.addMergedRegion(cellMerge1);




        Row row1 = sheet.createRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("Workers Advances Report"+"\t\t\t\t"+"From: " +
                " "+dateModelArrayList.get(0).getDate()+"\t\t"+ "To: " + dateModelArrayList.get(dateModelArrayList.size()-1).getDate()+"\t\t\t\t\t\t"+"Total No of Days:"+modelDateArrayList.size());

        CellRangeAddress cellMerge2 = new CellRangeAddress(4, 5, 0, 10);
        sheet.addMergedRegion(cellMerge2);

        Row rowValues = sheet.createRow(6);
        rowValues.setHeightInPoints(30);
        Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        Cell cellSrNo1 = rowValues.createCell(1);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Worker Id");
        Cell cellSrNo2 = rowValues.createCell(2);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Worker Name");
        Cell cellSrNo3 = rowValues.createCell(3);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Type");
        Cell cellSrNo4 = rowValues.createCell(4);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Total");

        for (int i = 0; i < shortDateList.size(); i++) {
            Cell dateNo = rowValues.createCell(i + 5);
            dateNo.setCellStyle(cellStyle);
            dateNo.setCellValue(shortDateList.get(i).getDate());
        }


//        Cell cellDesignation= row.createCell(1);
//        cellDesignation.setCellStyle(cellStyle);
//        cellDesignation.setCellValue("WorkerName");
//        for(int i=0;i<dateModelArrayList.size();i++){
//            Cell cellDate= row.createCell(i+3);
//            cellDate.setCellStyle(cellStyle);
//            cellDate.setCellValue(dateModelArrayList.get(i).getDate());
//        }


    }



    private void getLabourList(String currentDate, String currentDate1, String type) {
//        Log.e("Workertypeselected",workerTypeSelected);
//        Log.e("Workertypeselected",""+modelDateArrayList.size());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours").orderByChild("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labourList.clear();
                Log.e("SiteId", "Labour" + siteId);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelLabour modelLabour = ds.getValue(ModelLabour.class);
                    labourList.add(modelLabour);

                }
                Log.e("LabourListSize", "" + labourList.size());
                try {
                    getDateRange(currentDate, currentDate1, labourList, type);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
//                Log.e("LabourListSize1",""+labourList.size());
//                for(int i=0;i<labourList.size();i++){
//                    dateModelArrayList=new ArrayList<>();
//                    for(int j=0;j<modelDateArrayList.size();j++){
//                        Log.e("LoopModelDateArrayList","j"+j);
//                        getDateStatus(labourList.get(i).getLabourId(),
//                                labourList.get(i).getName(),modelDateArrayList.get(j).getDate());
//
//                    }
//                    Log.e("LoopModelDateArrayList","i"+i);
//
//                    ModelShowAttendance modelShowAttendance=new ModelShowAttendance(labourList.get(i).getLabourId(),
//                            labourList.get(i).getName(),dateModelArrayList);
//                    showAttendanceArrayList.add(modelShowAttendance);
//
//
//
//
////                    Log.e("Date",""+i+" "+modelDateArrayList.get(i).getDate());
//                }
//                Log.e("ShowAttendanceArrayList",""+showAttendanceArrayList.size());
//                Log.e("showAttendanceDate",""+showAttendanceArrayList.get(0).getDateModelArrayList().size());


            }
//                getLabourskilledList();


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteList() {
        Log.e("site", "called");
        Log.e("site", "called" + siteId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteArrayList.clear();
                siteNameArrayList.clear();
                if (snapshot.exists()) {
                    Log.e("Snapshot", "Exist");
                } else {
                    Log.e("Snapshot", "Not Exist");
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    Log.e("ModelArrayList", "" + modelSite.getSiteName());
                    siteArrayList.add(modelSite);
                    siteNameArrayList.add(new ModelSiteSpinner(modelSite.getSiteId(), modelSite.getSiteName()));
                }
                if (siteArrayList.size() > 0) {
                    Log.e("ModelArrayList", "" + siteArrayList.size());
                    Log.e("ModelArrayList", "" + siteArrayList.get(0).getSiteName());
                    Log.e("ModelArrayList", "" + siteArrayList.get(0).getSiteCreatedDate());
                    siteCreatedDate = siteArrayList.get(0).getSiteCreatedDate();
//                    ArrayAdapter<ModelSiteSpinner> adapter = new ArrayAdapter<ModelSiteSpinner>(ShowPaymentActivity.this, android.R.layout.simple_spinner_item, siteNameArrayList);
//                    binding.spinnerSelectSite.setAdapter(adapter);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDateRange(String fromDate, String toDate, ArrayList<ModelLabour> labourList, String type) throws ParseException {
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

            Log.e("SizeOfDate", "" + modelDateArrayList.size());
            Log.e("SizeOfLabour", "" + labourList.size());

            countLoop = 0;


            for (int i = 0; i < modelDateArrayList.size(); i++) {
                Log.e("modelLabourStatus", "" + modelLabourStatusArrayList.size());
                for (int j = 0; j < labourList.size(); j++) {
                    Log.e("date", modelDateArrayList.get(i).getDate());
                    Log.e("date", "" + siteId);
                    Log.e("date", labourList.get(j).getLabourId());
                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getType());
                }
            }
            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
            Log.e("modelLabourStatus", "AfterLoop" + modelLabourStatusArrayList.size());
        }


    }


    private void getAttendanceList(String date, String labourId, String type) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Payments").child(date).orderByChild("labourId").equalTo(labourId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountSum=0;
                        if (snapshot.getChildrenCount()>0) {
                            for(DataSnapshot ds:snapshot.getChildren()){
                                String amt=ds.child("amount").getValue(String.class);
                                Log.e("Amt",amt);
                                amountSum=amountSum+Integer.parseInt(amt);
                            }


                            ModelLabourStatus modelLabourStatus = new ModelLabourStatus(date, labourId, String.valueOf(amountSum), type);
                            addToarray(modelLabourStatus);
                            Log.e("model1234", "ID" + modelLabourStatus.getLabourId() + status + modelLabourStatus.getStatus());


//                            Log.e("Size11111",""+labourList.size());
//                            Log.e("Size22222",""+modelAttendances.size());
//                            Log.e("Size33333",""+modelDateArrayList.size());
//                            if(modelDateArrayList.size()>0) {
//                                AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowPaymentActivity.this ,
//                                        labourList,
//                                        modelAttendances ,
//                                        modelDateArrayList);
//                                binding.rvShowAttendance.setAdapter(adapterShowAttendance);
//                            }

                        } else {
                            ModelLabourStatus modelLabourStatus = new ModelLabourStatus(date, labourId, "0", type);
                            addToarray(modelLabourStatus);


                        }

//                        for(int i=0;i<modelLabourStatusArrayList.size();i++) {
//                            Log.e("modelLabourStatusArrayList" , "" + modelLabourStatusArrayList.get(i).getLabourId()
//                            +"Status"+modelLabourStatusArrayList.get(i).getStatus());
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addToarray(ModelLabourStatus modelLabourStatus) {
        countLoop++;

//        Log.e("modelLabourStatus", "Count:" + countLoop);
//        Log.e("modelLabourStatus", "LabourSize:" + labourList.size());
//        Log.e("modelLabourStatus", "DateSize:" + modelDateArrayList.size());
//
        modelLabourStatusArrayList.add(modelLabourStatus);
//        Log.e("modelLabourStatus","CountLoop:"+countLoop+"\t"+"Date:"+modelLabourStatus.getDate()+"\t"+"LabourID"+modelLabourStatus.getLabourId()
//        +"\t"+"Status:"+modelLabourStatus.getStatus());
//        Log.e("modelLabourStatus", modelLabourStatus.getLabourId());
//        Log.e("modelLabourStatus", "Status" + modelLabourStatus.getStatus());
//        Log.e("modelLabourStatus", "Size:" + modelLabourStatusArrayList.size());
        if (countLoop == labourList.size() * modelDateArrayList.size()) {
            Log.e("CountLoop", "Here");
            progressDialog.dismiss();
            binding.tableview.setVisibility(View.VISIBLE);
            binding.txtMessage.setVisibility(View.GONE);
            if (status.equals("ShowAttendance")) {
                initialiseTableView(labourList, modelDateArrayList, modelLabourStatusArrayList);
            } else {
//                DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList,siteId,siteName);
            }

//            AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowPaymentActivity.this,
//                    labourList,
//                    modelLabourStatusArrayList,
//                    modelDateArrayList);
//            binding.rvShowAttendance.setAdapter(adapterShowAttendance);
//
//            AdapterNestedChild1 adapterNestedChild1 = new AdapterNestedChild1(ShowPaymentActivity.this, modelLabourStatusArrayList, modelDateArrayList, labourList);
//            binding.rvStatus.setAdapter(adapterNestedChild1);
        } else {
            if (labourList.size() < 1) {
                progressDialog.dismiss();
                binding.tableview.setVisibility(View.GONE);
//                binding.txtMessage.setText("No Labours Added");
//                binding.txtMessage.setVisibility(View.VISIBLE);

            }
        }
    }

    private void initialiseTableView(ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList,
                                     ArrayList<ModelLabourStatus> modelLabourStatusArrayList) {
        TableViewModel tableViewModel = new TableViewModel(modelDateArrayList.size() + 3,
                labourList.size(), labourList, modelDateArrayList, modelLabourStatusArrayList, null, "Payment");

        // Create TableView Adapter
        TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);

        binding.tableview.setAdapter(tableViewAdapter);
        binding.tableview.setTableViewListener(new TableViewListener(binding.tableview));

        // Create an instance of a Filter and pass the TableView.
        //mTableFilter = new Filter(mTableView);

        // Load the dummy data to the TableView
        tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
                .getRowHeaderList(), tableViewModel.getCellList());

    }

    private void getDateStatus(String labourId, String name, String date) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours")
                .child(date).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.e("LoopModelDateArrayList", "NotExist GetDateStatus");
                            DateModel dateModel = new DateModel(date, "N");
                            dateModelArrayList.add(dateModel);
                        } else {
                            Log.e("LoopModelDateArrayList", "Exist GetDateStatus");

                            getLabourStatus(labourId, name, date, reference);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getLabourStatus(String labourId, String name, String date, DatabaseReference reference) {
        reference.child(workerTypeSelected).child(labourId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("LoopModelDateArrayList", "NotExist GetLabourStatus");

                    DateModel dateModel = new DateModel(labourId, "A");
                    dateModelArrayList.add(dateModel);
                } else {
                    Log.e("LoopModelDateArrayList", "Exist GetLabourStatus");
                    DateModel dateModel = new DateModel(labourId, "P");
                    dateModelArrayList.add(dateModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getAttendanceMaster() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child("03-Nov-2022")
                .child("Skilled")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelShowAttendance modelShowAttendance = ds.getValue(ModelShowAttendance.class);
                            showAttendanceArrayList.add(modelShowAttendance);


                        }
                        Log.e("showAttendanceSize", "" + showAttendanceArrayList.size());

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

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        if (binding.btnToday.isChecked()) {
            progressDialog.show();
            checkForAttendance();

        }
        else {
            Log.e("SizeOfDateREQ", "" + modelDateArrayList.size());
            Log.e("SizeOfDateREQ", "La: " + labourList.size());
            countLoop = 0;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).child("Industry").child("Construction").child("Site").orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelSite modelSite = ds.getValue(ModelSite.class);
                        Log.e("siteAdminList", "Size" + siteAdminList.size());
                        for (int i = 0; i < siteAdminList.size(); i++) {
                            if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
                                Log.e("SiteFound", "" + siteAdminList.get(i).getSiteId());
                                if (siteAdminList.get(i).getSelected()) {
                                    Log.e("SiteFound", "2::::" + siteAdminList.get(i).getSiteId());
                                    long siteId1 = siteAdminList.get(i).getSiteId();
                                    String siteName1 = siteAdminList.get(i).getSiteName();
//                                                    long countChild = snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).getChildrenCount();
//                                                    if (countChild <= 0) {
//                                                        Toast.makeText(ShowPaymentActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
//                                                    } else {
                                    modelLabourStatusArrayList.clear();
//                                                        labourList.clear();
//                                                        modelDateArrayList.clear();
//                                                        shortDateList.clear();
                                    binding.btnShow.setVisibility(View.GONE);
                                    binding.llDate.setVisibility(View.GONE);
                                    binding.tableview.setVisibility(View.VISIBLE);
                                    binding.txtMessage.setVisibility(View.GONE);
                                    Date c = Calendar.getInstance().getTime();


                                    Calendar c12 = Calendar.getInstance();
                                    c12.add(Calendar.DATE, 1);
                                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                    String currentDate11 = df1.format(c);

                                    // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                                    Date temp = c12.getTime();
                                    String currentDate1 = sdf1.format(temp);
                                    Log.e("Labour1", currentDate11);
                                    Log.e("Labour2", currentDate1);
                                    Log.e("NullCheck", "A:" + snapshot.child(String.valueOf(siteId1)).child("Labours").getChildrenCount());
                                    Log.e("ModelDatearrayList", "" + modelDateArrayList.size());
                                    for (int m = 0; m < modelDateArrayList.size(); m++) {
                                        labourList.clear();
                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()) {
                                            ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
                                            labourList.add(modelLabour);


                                            Log.e("NULLCHEXK", String.valueOf(siteId1));
                                            Log.e("NULLCHEXK", currentDate11);
                                            Log.e("NULLCHEXK", modelLabour.getLabourId());
                                            if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0) {
                                                int countLabour=0;
                                                int AmtSum=0;
                                                String amount;
                                                for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()){
                                                    ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
                                                    if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
                                                        amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
                                                        AmtSum=AmtSum+Integer.parseInt(amount);
                                                    }

                                                }
                                                ModelLabourStatus modelLabourStatus=new ModelLabourStatus(currentDate11,modelLabour.getLabourId(),String.valueOf(AmtSum),modelLabour.getType());
                                                modelLabourStatusArrayList.add(modelLabourStatus);
                                            }else{
                                                ModelLabourStatus modelLabourStatus=new ModelLabourStatus(currentDate11,modelLabour.getLabourId(),"0",modelLabour.getType());
                                                modelLabourStatusArrayList.add(modelLabourStatus);
                                            }


                                        }

                                    }
//                                                        modelDateArrayList.add(new ModelDate(currentDate11));
//                                                        shortDateList.add(new ModelDate(currentDate11));


                                    Log.e("SizeCheck", "SiteID:" + siteId1);
                                    Log.e("SizeCheck", "Labour: " + labourList.size());
                                    Log.e("SizeCheck", "Date: " + modelDateArrayList.size());
                                    Log.e("SizeCheck", "Status: " + modelLabourStatusArrayList.size());


                                    DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList, shortDateList, siteId1, siteName1);

//                                        getLabourList(currentDate11, currentDate1, "Today");

//                                                    Log.e("CHILDDNL", "" + countChild);
//                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Site");
//                                    reference1.child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate)
//                                            .addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.exists()) {
//                                                        Log.e("CheckAtte",String.valueOf(siteId));
//                                                        binding.btnShow.setVisibility(View.GONE);
//                                                        binding.llDate.setVisibility(View.GONE);
//                                                        binding.tableview.setVisibility(View.VISIBLE);
//                                                        binding.txtMessage.setVisibility(View.GONE);
//                                                        Date c = Calendar.getInstance().getTime();
//
//
//                                                        Calendar c12 = Calendar.getInstance();
//                                                        c12.add(Calendar.DATE, 1);
//                                                        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                                                        String currentDate11 = df1.format(c);
//
//                                                        // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//                                                        Date temp = c12.getTime();
//                                                        String currentDate1 = sdf1.format(temp);
//                                                        Log.e("Labour1", currentDate11);
//                                                        Log.e("Labour2", currentDate1);
//                                                        getLabourList(currentDate11, currentDate1,"Today");
//
//                                                    } else {
//                                                        if(status.equals("ShowAttendance")) {
//                                                            progressDialog.dismiss();
//                                                            binding.tableview.setVisibility(View.GONE);
////                            binding.btnShow.setVisibility(View.GONE);
////                            binding.llDate.setVisibility(View.GONE);
////                            binding.btnToday.setVisibility(View.GONE);
////                            binding.btnCustom.setVisibility(View.GONE);
//                                                            binding.txtMessage.setText(R.string.you_have_not_uploaded_any_attendance);
//                                                            binding.txtMessage.setVisibility(View.VISIBLE);
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
                                }
                            }
                        }

                    }
                    getFirebaseToken();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


//                            for (int i = 0; i < modelDateArrayList.size(); i++) {
//                                Log.e("modelLabourStatus", "" + modelLabourStatusArrayList.size());
//                                for (int j = 0; j < labourList.size(); j++) {
//                                    Log.e("date", modelDateArrayList.get(i).getDate());
//                                    Log.e("date", "" + siteId);
//                                    Log.e("date", labourList.get(j).getLabourId());
//                                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getType());
//                                }
//                            }
//                            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
//                            Log.e("modelLabourStatus", "AfterLoop" + modelLabourStatusArrayList.size());
        }

//        if (dnl_file_type.equals("pdf")) {
//            ExcelToPdf(file, fos, workbook,siteId,siteName);
//        } else {
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            Log.e("StrPath", str_path);
//            file = new File(str_path, "PaymentReport_" + siteName + "_" + currentDate + ".xls");
//
//            try {
//                fos = new FileOutputStream(file);
//                workbook.write(fos);
//                fos.flush();
//                fos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.e("FilePath", file.getAbsolutePath().toString());
//            ExcelToPdf(file, fos, workbook,siteId,siteName);
//        }
//        Toast.makeText(this, "Payment Success and Payment Id is " + s, Toast.LENGTH_LONG);


        Log.e("Success", s);
        alertDialogPaymentConfirm.dismiss();
        Log.e("from", "" + fromDate);
        Log.e("from", "to::::" + toDate);
        Log.e("from", "SpinnerPos:::" + promo_spinner_position);
        Log.e("from", "PromoTitle:::" + promo_title);
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowPaymentActivity.this);
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
            txt_message.setText("Your Attendance Report PDF file is generated and stored in DOWNLOADS folder. ");
        } else {
            txt_message.setText("Your Attendance Report EXCEL+PDF file is generated and stored in DOWNLOADS folder. ");
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
                hashMap.put("downloadFile", "Advances Report");
                hashMap.put("siteId", siteId);
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", dnl_file_type);
                hashMap.put("totalAmount", String.valueOf(amount));

                hashMap.put("fromDate", fromDate);
                hashMap.put("toDate", toDate);
                if (promo_spinner_position > 0) {
                    hashMap.put("promoApplied", true);
                    hashMap.put("promoTitle", promo_title);
                } else {
                    hashMap.put("promoApplied", false);
                    hashMap.put("promoTitle", "");
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData", paymentData.getData().toString());
                                Log.e("paymentData", paymentData.getPaymentId().toString());
//

                                Log.e("fos", "flush");
                                Toast.makeText(ShowPaymentActivity.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                Uri dirUri = FileProvider.getUriForFile(ShowPaymentActivity.this,getApplicationContext().getPackageName() + ".com.skillzoomer_Attendance.com",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
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
        Log.e("from", "SpinnerPos:::" + promo_spinner_position);
        Log.e("from", "PromoTitle:::" + promo_title);
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowPaymentActivity.this);
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
                hashMap.put("downloadFile", "Attendance Report");
                hashMap.put("fromDate", fromDate);
                hashMap.put("toDate", toDate);
                hashMap.put("siteId", siteId);
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", dnl_file_type);
                if (promo_spinner_position > 0) {
                    hashMap.put("promoApplied", true);
                    hashMap.put("promoTitle", promo_title);
                } else {
                    hashMap.put("promoApplied", false);
                    hashMap.put("promoTitle", "");
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                Log.e("paymentData",paymentData.getData().toString());
//                                Log.e("paymentData",paymentData.getPaymentId().toString());
                                Toast.makeText(ShowPaymentActivity.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();

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


    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return search.length;
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
            designationText.setText(search[position]);

            return row;
        }
    }

    class SpinnerAdapter1 extends BaseAdapter {

        @Override
        public int getCount() {
            return workerType.length;
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
            designationText.setText(workerType[position]);

            return row;
        }
    }

    public class SelfScrolListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.e("ViewIsScrolling", "P" + viewIsScrolling);
            Log.e("ViewIsScrolling", "3" + firstIsTouched);
            Log.e("ViewIsScrolling", "N" + (newState == RecyclerView.SCROLL_STATE_IDLE));
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                viewIsScrolling = -1;
            }
        }
    }

    //    class SiteSpinnerAdapter1
//            extends BaseAdapter {
//        SiteSpinnerAdapter1() {
//        }
//
//        public int getCount() {
//            return siteAdminList.size()+1;
//        }
//
//        public Object getItem(int n) {
//            return null;
//        }
//
//        public long getItemId(int n) {
//            return 0L;
//        }
//
//        public View getView(int n, View view, ViewGroup viewGroup) {
//            View view2 = getLayoutInflater().inflate(R.layout.layout_select_site, null);
//            TextView textView = (TextView) view2.findViewById(R.id.txt_select_site);
//            CheckBox cb_spinner=(CheckBox)view2.findViewById(R.id.cb_spinner);
//            Button btn_ok=(Button) view2.findViewById(R.id.btn_ok);
//            if(n<1){
//                textView.setVisibility(View.VISIBLE);
//                cb_spinner.setVisibility(View.GONE);
//                btn_ok.setVisibility(View.GONE);
//            }else if(n>=0 && n<=siteAdminList.size()-1){
//                textView.setVisibility(View.GONE);
//                cb_spinner.setVisibility(View.VISIBLE);
//                cb_spinner.setText(siteAdminList.get(n).getSiteCity());
//                btn_ok.setVisibility(View.GONE);
//                cb_spinner.setChecked(siteAdminList.get(n).getSelected());
//                cb_spinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        if(n==1 && b){
//                            for(int i=1;i<siteAdminList.size();i++){
//                                siteAdminList.get(i).setSelected(true);
//                            }
//                            notifyDataSetChanged();
//
//                        }else if(n>1 && b){
//                            for(int i=1;i<siteAdminList.size();i++){
//                                if(i==n){
//                                    siteAdminList.get(n).setSelected(true);
//                                }else if(!siteAdminList.get(n).getSelected()){
//                                    siteAdminList.get(n).setSelected(false);
//                                }
//
//                            }
//                        }else if(n==1 && !b){
//                            for(int i=1;i<siteAdminList.size();i++){
//                                siteAdminList.get(i).setSelected(false);
//                            }
//                            notifyDataSetChanged();
//                        }else if(n>1 && !b){
//                            siteAdminList.get(1).setSelected(false);
//                            siteAdminList.get(n).setSelected(false);
//                            notifyDataSetChanged();
//                        }
//                    }
//                });
//            }else if(n==siteAdminList.size()){
//                btn_ok.setVisibility(View.VISIBLE);
//                textView.setVisibility(View.GONE);
//                cb_spinner.setVisibility(View.GONE);
//                btn_ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                       binding.spinnerSelectSite.setSelection(0);
//                       textView.setText("Site Selected");
////                       binding.spinnerSelectSite.setDropDownWidth(0);
//                    }
//                });
//            }
////            textView.setText(siteAdminList.get(n).getSiteCity());
//            return view2;
//        }
//    }
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

    class PromoSpinnerAdapter
            extends BaseAdapter {
        PromoSpinnerAdapter() {
        }

        public int getCount() {
            return promoArrayList.size();
        }

        public Object getItem(int n) {
            return null;
        }

        public long getItemId(int n) {
            return 0L;
        }

        public View getView(int n, View view, ViewGroup viewGroup) {
            View view2 = getLayoutInflater().inflate(R.layout.promo_layout_single_row, null);
            LinearLayout ll_main;
            ImageView img_promo;
            TextView txt_promo_title, txt_promo_details;
            ll_main = view2.findViewById(R.id.ll_main);
            img_promo = view2.findViewById(R.id.img_promo);
            txt_promo_title = view2.findViewById(R.id.txt_promo_title);
            txt_promo_details = view2.findViewById(R.id.txt_promo_details);
            if (n == 0) {
                ll_main.setWeightSum(1);
                img_promo.setVisibility(View.GONE);
                txt_promo_details.setVisibility(View.GONE);
                txt_promo_title.setText(promoArrayList.get(n).getTitle());
            } else {
                ll_main.setWeightSum(2);
                Picasso.get().load(promoArrayList.get(n).getUrl())

                        .placeholder(R.drawable.logo_razor).into(img_promo);
                txt_promo_title.setText(promoArrayList.get(n).getTitle());
                txt_promo_details.setText(promoArrayList.get(n).getDetails());
            }


            return view2;
        }
    }


}