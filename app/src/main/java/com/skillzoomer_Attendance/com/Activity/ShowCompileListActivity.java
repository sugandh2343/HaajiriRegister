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
import com.skillzoomer_Attendance.com.Model.DateModel;
import com.skillzoomer_Attendance.com.Model.ModelAttendance;
import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourPayment;
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
import com.skillzoomer_Attendance.com.databinding.ActivityShowCompileListBinding;
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

public class ShowCompileListActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityShowCompileListBinding binding;
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
    private ArrayList<ModelLabour> labourList1;
    private ArrayList<ModelAttendance> modelAttendances;
   
    private ArrayList<ModelCompileStatus> ModelCompileStatusArrayList;
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
       binding=ActivityShowCompileListBinding.inflate(getLayoutInflater());
//       getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
       setContentView(binding.getRoot());
        toolbarBinding = binding.toolbarLayout;
        toolbarBinding.heading.setText("Show Attendance");
        toolbarBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
        ModelCompileStatusArrayList = new ArrayList<>();
        promoArrayList = new ArrayList<>();
        labourByType = new ArrayList<>();
        binding.btnDownloadReport.setVisibility(View.GONE);


        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userType = sharedpreferences.getString("userDesignation", "");
        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=firebaseAuth.getUid();
        }
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
        ShowCompileListActivity.SpinnerAdapter spinnerAdapter = new ShowCompileListActivity.SpinnerAdapter();
        binding.spinnerSearchType.setAdapter(spinnerAdapter);
        ShowCompileListActivity.SpinnerAdapter1 spinnerAdapter1 = new ShowCompileListActivity.SpinnerAdapter1();
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
//                ModelCompileStatusArrayList.clear();
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
                        Toast.makeText(ShowCompileListActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(ShowCompileListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(ShowCompileListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                } else if (f) {
//                    progressDialog.setMessage("Downloading Your Reports");
//                    progressDialog.show();
                    String type = "Custom";
                    ModelCompileStatusArrayList.clear();
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
                    reference.child("Compile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                            AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
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
                                Toast.makeText(ShowCompileListActivity.this, "Select file type", Toast.LENGTH_SHORT).show();
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
//                if (binding.btnToday.isChecked()) {
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
//                        Toast.makeText(ShowCompileListActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
//                        f = false;
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                if (fromDate.equals("")) {
//                    Toast.makeText(ShowCompileListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
//                } else if (toDate.equals("")) {
//                    Toast.makeText(ShowCompileListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
//                } else if (f) {
////                    progressDialog.setMessage("Downloading Your Reports");
////                    progressDialog.show();
//                    String type = "Custom";
//                    ModelCompileStatusArrayList.clear();
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
//                }
//
//                if (binding.btnToday.isChecked()) {
//                    progressDialog.show();
//                    checkForAttendance();
//
//                }
//                else {
////                    Log.e("SizeOfDateREQ", "" + modelDateArrayList.size());
////                    Log.e("SizeOfDateREQ", "La: " + labourList.size());
//                    countLoop = 0;
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                    reference.child(uid).child("Industry").child("Construction").child("Site").orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
////                                                        Toast.makeText(ShowCompileListActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
////                                                    } else {
//                                            ModelCompileStatusArrayList.clear();
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
////                                            Log.e("Labour1", currentDate11);
////                                            Log.e("Labour2", currentDate1);
////                                            Log.e("NullCheck", "A:" + snapshot.child(String.valueOf(siteId1)).child("Labours").getChildrenCount());
////                                            Log.e("ModelDatearrayList", "" + modelDateArrayList.size());
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
//                                                    if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(modelDateArrayList.get(m).getDate()).child(modelLabour.getLabourId())
//                                                            .getChildrenCount() > 0) {
//                                                        if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0){
//                                                            int countLabour=0;
//                                                            int AmtSum=0;
//                                                            String amount;
//                                                            for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()){
//                                                                ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
//                                                                if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
//                                                                    amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
//                                                                    AmtSum=AmtSum+Integer.parseInt(amount);
//                                                                }
//
//                                                            }
//                                                            ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"P",modelLabour.getType(),String.valueOf(AmtSum));
//                                                            ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                        }else{
//                                                            ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"P",modelLabour.getType(),"0");
//                                                            ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                        }
//                                                    } else {
//                                                        if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0){
//                                                            int countLabour=0;
//                                                            int AmtSum=0;
//                                                            String amount;
//                                                            for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()){
//                                                                ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
//                                                                if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
//                                                                    amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
//                                                                    AmtSum=AmtSum+Integer.parseInt(amount);
//                                                                }
//
//                                                            }
//                                                            ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"A",modelLabour.getType(),String.valueOf(AmtSum));
//                                                            ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                        }else{
//                                                            ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"A",modelLabour.getType(),"0");
//                                                            ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                        }
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
//                                            Log.e("SizeCheck", "Status: " + ModelCompileStatusArrayList.size());
//
//
//                                            DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList, siteId1, siteName1);
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
////                                Log.e("ModelCompileStatus", "" + ModelCompileStatusArrayList.size());
////                                for (int j = 0; j < labourList.size(); j++) {
////                                    Log.e("date", modelDateArrayList.get(i).getDate());
////                                    Log.e("date", "" + siteId);
////                                    Log.e("date", labourList.get(j).getLabourId());
////                                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getType());
////                                }
////                            }
////                            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
////                            Log.e("ModelCompileStatus", "AfterLoop" + ModelCompileStatusArrayList.size());
//                }
//            }
//        });

        showAttendanceArrayList = new ArrayList<>();
        labourList = new ArrayList<>();
        labourList1 = new ArrayList<>();
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
                        Toast.makeText(ShowCompileListActivity.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(ShowCompileListActivity.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(ShowCompileListActivity.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                } else if (f) {
                    ModelCompileStatusArrayList.clear();
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
            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate)
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
                                    binding.txtMessage.setText(R.string.you_have_not_uploaded_any_attendance);
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
                                    long countChild = snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).getChildrenCount();
                                    if (countChild <= 0) {
                                        Toast.makeText(ShowCompileListActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        ModelCompileStatusArrayList.clear();
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
                                        for(DataSnapshot ds1:  snapshot.child(uid)
                                                .child("Industry").child("Construction").child("Site")
                                                .child(String.valueOf(siteId1)).child("Labours").getChildren()){
                                            ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
                                            labourList.add(modelLabour);


                                            Log.e("NULLCHEXK",String.valueOf(siteId1));
                                            Log.e("NULLCHEXK",currentDate11);
                                            Log.e("NULLCHEXK",modelLabour.getLabourId());
                                            if(snapshot.child(uid)
                                                    .child("Industry").child("Construction").child("Site")
                                                    .child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).child(modelLabour.getLabourId()).getChildrenCount()>0){
                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount() > 0){
                                                    int countLabour=0;
                                                    int AmtSum=0;
                                                    String amount;
                                                    for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildren()){
                                                        ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
                                                        if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
                                                            amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
                                                            AmtSum=AmtSum+Integer.parseInt(amount);
                                                        }

                                                    }
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(currentDate,modelLabour.getLabourId(),"P",modelLabour.getType(),String.valueOf(AmtSum));
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }else{
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(currentDate,modelLabour.getLabourId(),"P",modelLabour.getType(),"0");
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }
                                                
                                            }else{
                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildrenCount() > 0){
                                                    int countLabour=0;
                                                    int AmtSum=0;
                                                    String amount;
                                                    for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).getChildren()){
                                                        ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
                                                        if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
                                                            amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(currentDate).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
                                                            AmtSum=AmtSum+Integer.parseInt(amount);
                                                        }

                                                    }
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(currentDate,modelLabour.getLabourId(),"A",modelLabour.getType(),String.valueOf(AmtSum));
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }else{
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(currentDate,modelLabour.getLabourId(),"A",modelLabour.getType(),"0");
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }
                                            }


                                        }
                                        DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList,siteId1,siteName1);

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
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
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
                    Toast.makeText(ShowCompileListActivity.this, "Select Atleast one site to download report", Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
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
//                    DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList,siteId,siteName);
//                    if (binding.btnToday.isChecked()) {
//                        progressDialog.show();
//                        checkForAttendance();
//
//                    }
//                    else {
//                        Log.e("SizeOfDateREQ", "" + modelDateArrayList.size());
//                        Log.e("SizeOfDateREQ", "La: " + labourList.size());
//                        countLoop = 0;
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//                        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot ds : snapshot.getChildren()) {
//                                    ModelSite modelSite = ds.getValue(ModelSite.class);
//                                    Log.e("siteAdminList", "Size" + siteAdminList.size());
//                                    for (int i = 0; i < siteAdminList.size(); i++) {
//                                        if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
//                                            Log.e("SiteFound", "" + siteAdminList.get(i).getSiteId());
//                                            if (siteAdminList.get(i).getSelected()) {
//                                                Log.e("SiteFound", "2::::" + siteAdminList.get(i).getSiteId());
//                                                long siteId1 = siteAdminList.get(i).getSiteId();
//                                                String siteName1 = siteAdminList.get(i).getSiteName();
////                                                    long countChild = snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).getChildrenCount();
////                                                    if (countChild <= 0) {
////                                                        Toast.makeText(ShowCompileListActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
////                                                    } else {
//                                                ModelCompileStatusArrayList.clear();
////                                                        labourList.clear();
////                                                        modelDateArrayList.clear();
////                                                        shortDateList.clear();
//                                                binding.btnShow.setVisibility(View.GONE);
//                                                binding.llDate.setVisibility(View.GONE);
//                                                binding.tableview.setVisibility(View.VISIBLE);
//                                                binding.txtMessage.setVisibility(View.GONE);
//                                                Date c = Calendar.getInstance().getTime();
//
//
//                                                Calendar c12 = Calendar.getInstance();
//                                                c12.add(Calendar.DATE, 1);
//                                                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                                                String currentDate11 = df1.format(c);
//
//                                                // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//                                                Date temp = c12.getTime();
//                                                String currentDate1 = sdf1.format(temp);
////                                                Log.e("Labour1", currentDate11);
////                                                Log.e("Labour2", currentDate1);
////                                                Log.e("NullCheck", "A:" + snapshot.child(String.valueOf(siteId1)).child("Labours").getChildrenCount());
////                                                Log.e("ModelDatearrayList", "" + modelDateArrayList.size());
//                                                for (int m = 0; m < modelDateArrayList.size(); m++) {
//                                                    labourList.clear();
//                                                    for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()) {
//                                                        ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
//                                                        labourList.add(modelLabour);
//
//
////                                                        Log.e("NULLCHEXK", String.valueOf(siteId1));
////                                                        Log.e("NULLCHEXK", currentDate11);
////                                                        Log.e("NULLCHEXK", modelLabour.getLabourId());
//                                                        if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(modelDateArrayList.get(m).getDate()).child(modelLabour.getLabourId())
//                                                                .getChildrenCount() > 0) {
//                                                            if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0){
//                                                                int countLabour=0;
//                                                                int AmtSum=0;
//                                                                String amount;
//                                                                for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()){
//                                                                    ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
//                                                                    if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
//                                                                        amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
//                                                                        AmtSum=AmtSum+Integer.parseInt(amount);
//                                                                    }
//
//                                                                }
//                                                                ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"P",modelLabour.getType(),String.valueOf(AmtSum));
//                                                                ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                            }else{
//                                                                ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"P",modelLabour.getType(),"0");
//                                                                ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                            }
//                                                        } else {
//                                                            if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0){
//                                                                int countLabour=0;
//                                                                int AmtSum=0;
//                                                                String amount;
//                                                                for(DataSnapshot ds2:  snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildren()){
//                                                                    ModelLabourPayment labourPayment=ds2.getValue(ModelLabourPayment.class);
//                                                                    if (labourPayment.getLabourId().equals(modelLabour.getLabourId())){
//                                                                        amount=snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).child(labourPayment.getTimestamp()).child("amount").getValue(String.class);
//                                                                        AmtSum=AmtSum+Integer.parseInt(amount);
//                                                                    }
//
//                                                                }
//                                                                ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"A",modelLabour.getType(),String.valueOf(AmtSum));
//                                                                ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                            }else{
//                                                                ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"A",modelLabour.getType(),"0");
//                                                                ModelCompileStatusArrayList.add(modelCompileStatus);
//                                                            }
//                                                        }
//
//
//                                                    }
//
//                                                }
////                                                        modelDateArrayList.add(new ModelDate(currentDate11));
////                                                        shortDateList.add(new ModelDate(currentDate11));
//
//
////                                                Log.e("SizeCheck", "SiteID:" + siteId1);
////                                                Log.e("SizeCheck", "Labour: " + labourList.size());
////                                                Log.e("SizeCheck", "Date: " + modelDateArrayList.size());
////                                                Log.e("SizeCheck", "Status: " + ModelCompileStatusArrayList.size());
//
//
//                                                DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList, siteId1, siteName1);
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
//                                            }
//                                        }
//                                    }
//
//                                }
//                                getFirebaseToken();
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//
////                            for (int i = 0; i < modelDateArrayList.size(); i++) {
////                                Log.e("ModelCompileStatus", "" + ModelCompileStatusArrayList.size());
////                                for (int j = 0; j < labourList.size(); j++) {
////                                    Log.e("date", modelDateArrayList.get(i).getDate());
////                                    Log.e("date", "" + siteId);
////                                    Log.e("date", labourList.get(j).getLabourId());
////                                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getType());
////                                }
////                            }
////                            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
////                            Log.e("ModelCompileStatus", "AfterLoop" + ModelCompileStatusArrayList.size());
//                    }
                    Thread createOrderId=new Thread(new ShowCompileListActivity.createOrderIdThread(AmountTemp));
                    createOrderId.start();
                } else {
//                    DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList,siteId,siteName);
                    Thread createOrderId=new Thread(new ShowCompileListActivity.createOrderIdThread(AmountTemp));
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

    private void DownloadPdf(File file, FileOutputStream fos, HSSFWorkbook workbook, long siteId1,
                             String siteName1, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList,
                             ArrayList<ModelCompileStatus> modelCompileStatusArrayList, ArrayList<ModelLabour> list, ArrayList<ModelDate> shortDateList, long id1, String name1) throws DocumentException {
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "HajiriRegister_"+""+"CompiledReoprt_" + timestamp+ ".pdf";
        File pdfFile = new File(str_path);
        com.itextpdf.text.Document iText_xls_2_pdf = new Document();
        if(modelDateArrayList.size()>15){
            iText_xls_2_pdf.setPageSize(new Rectangle(40*modelDateArrayList.size(), 1584));
        }else{
            iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 80f);
        }
        try {
            PdfWriter writer=PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(ShowCompileListActivity.this,siteId1,siteName1,modelDateArrayList,"Worker Compiled Report");
            writer.setPageEvent(event);
        } catch (DocumentException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            Log.e("exception123", e.getMessage());
            e.printStackTrace();
        }
        iText_xls_2_pdf.open();



        PdfPTable my_table = new PdfPTable(modelDateArrayList.size() + 8);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;

        createPdfHeaderRow(modelDateArrayList, shortDateList,siteId1,siteName1,iText_xls_2_pdf,my_table);
    }

    private void createPdfHeaderRow(ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDate> shortDateList, long siteId1, String siteName1, Document iText_xls_2_pdf, PdfPTable my_table) throws DocumentException {
        PdfPTable table_header=new PdfPTable(modelDateArrayList.size()+10);


        table_header.setWidthPercentage(95);
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

        com.itextpdf.text.Font fontRedHeading=new com.itextpdf.text.Font();
        fontRedHeading.setStyle(com.itextpdf.text.Font.BOLD|com.itextpdf.text.Font.UNDERLINE);
        fontRedHeading.setSize(12);
        fontRedHeading.setColor(BaseColor.RED);

        com.itextpdf.text.Font fontBlue=new com.itextpdf.text.Font();
        fontBlue.setStyle(com.itextpdf.text.Font.NORMAL);
        fontBlue.setSize(12);
        fontBlue.setColor(BaseColor.BLUE);

        PdfPCell srNo,id,name,type,wages,total_att,total_adv,payable_amt;
        srNo=(new PdfPCell(new Phrase("Sr No",fontBold)));
        id=(new PdfPCell(new Phrase("Worker Id",fontBold)));
        name=(new PdfPCell(new Phrase("Worker Name",fontBold)));
        type=(new PdfPCell(new Phrase("Type",fontBold)));
        wages=(new PdfPCell(new Phrase("Wages",fontBold)));
        total_att=(new PdfPCell(new Phrase("Total Att.",fontBold)));
        total_adv=(new PdfPCell(new Phrase("Total Adv.",fontBold)));
        payable_amt=(new PdfPCell(new Phrase("Payable Amount",fontBold)));

        id.setColspan(2);
        name.setColspan(2);


        Log.e("ModelDateArrayList",""+modelDateArrayList.size());
        int startPos=0;
        int endPos=15;

        if(modelDateArrayList.size()>15){
            for(int i= 0;i<modelDateArrayList.size();i=i+15){
                Log.e("JVALUE","IS::::"+i);
                for(int j=i;j<(i+15);j++){
                    Log.e("JVALUE","J::::"+j);
                }
                Log.e("JVALUE","EI::"+i);

            }

        }



//        table_header.addCell(new PdfPCell(new Phrase("Sr No",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Worker Id",fontBold))).setColspan(2);
//        table_header.addCell(new PdfPCell(new Phrase("Worker Name",fontBold))).setColspan(2);
//        table_header.addCell(new PdfPCell(new Phrase("Type",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Wages",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Total Att.",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Total Adv.",fontBold)));
//        table_header.addCell(new PdfPCell(new Phrase("Payable Amount",fontBold)));
        table_header.addCell(srNo);
        table_header.addCell(id);
        table_header.addCell(name);
        table_header.addCell(type);
        table_header.addCell(wages);
        table_header.addCell(total_att);
        table_header.addCell(total_adv);
        table_header.addCell(payable_amt);

        for (int i = 0; i < shortDateList.size(); i++) {
            table_header.addCell(new PdfPCell(new Phrase(shortDateList.get(i).getDate(),fontBold)));
//            table_header.addCell(tableCell);
        }

        for(int i=0;i<labourList.size()*2;i++){
            if(i%2==0){
                PdfPCell tableCell_SrNo=new PdfPCell();
                tableCell_SrNo.setNoWrap(false);
                tableCell_SrNo.setPhrase(new Phrase(String.valueOf(i/2+1),fontNormal));
                tableCell_SrNo.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_SrNo.setFixedHeight(20);
                table_header.addCell(tableCell_SrNo);

                PdfPCell tableCell_Id=new PdfPCell();
                tableCell_Id.setNoWrap(false);
                tableCell_Id.setPhrase(new Phrase(labourList.get(i/2).getLabourId(),fontNormal));
                tableCell_Id.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_Id.setFixedHeight(20);
                tableCell_Id.setColspan(2);
                table_header.addCell(tableCell_Id);


                PdfPCell tableCell_Name=new PdfPCell();
                tableCell_Name.setNoWrap(false);
                tableCell_Name.setPhrase(new Phrase(labourList.get(i/2).getName(),fontNormal));
                tableCell_Name.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_Name.setFixedHeight(20);
                tableCell_Name.setColspan(2);
                table_header.addCell(tableCell_Name);


                PdfPCell tableCell_Type=new PdfPCell();
                tableCell_Type.setNoWrap(false);
                tableCell_Type.setPhrase(new Phrase(labourList.get(i/2).getType(),fontNormal));
                tableCell_Type.setFixedHeight(20);
                tableCell_Type.setHorizontalAlignment(Element.ALIGN_CENTER);

                table_header.addCell(tableCell_Type);

                PdfPCell tableCell_Wages=new PdfPCell();
                tableCell_Wages.setNoWrap(false);
                tableCell_Wages.setPhrase(new Phrase(String.valueOf(labourList.get(i/2).getWages()),fontNormal));
                tableCell_Wages.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCell_Wages.setFixedHeight(20);
                table_header.addCell(tableCell_Wages);


                com.itextpdf.text.Font fontHeading=new com.itextpdf.text.Font();
                fontHeading.setStyle(com.itextpdf.text.Font.BOLD);
                fontHeading.setSize(12);
                fontHeading.setColor(BaseColor.BLACK);
                fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));


                int  AmtSum=0;
                float CountPresent=0;
                float countHalf=0;
                float countTotal=0;
                for(int j=i/2;j<ModelCompileStatusArrayList.size();j=j+labourList.size()){
                    if(ModelCompileStatusArrayList.get(j).getStatus().equals("P")){
                        CountPresent++;
                    }else {
                        if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                            countHalf++;
                        }
                    }
                    if(!ModelCompileStatusArrayList.get(j).getAmount().equals("0")){
                        AmtSum=AmtSum+Integer.parseInt(ModelCompileStatusArrayList.get(j).getAmount());
                    }

                }
                int PayableAmt= (int) ((labourList.get(i/2).getWages()*CountPresent+ ((labourList.get(i/2).getWages()/2)*countHalf))-AmtSum);

                String status=String.valueOf(AmtSum);
                String payableAmt=String.valueOf(PayableAmt);
                countTotal=CountPresent+(countHalf/2);
                PdfPCell table_cell_pT=new PdfPCell();
                table_cell_pT.setNoWrap(false);
                table_cell_pT.setPhrase(new Phrase((String.valueOf(countTotal)),fontGreen));
                table_cell_pT.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell_pT.setFixedHeight(20);;
                table_header.addCell(table_cell_pT);

                PdfPCell table_cell_ts=new PdfPCell();
                table_cell_ts.setNoWrap(false);
                table_cell_ts.setPhrase(new Phrase((status),fontGreen));
                table_cell_ts.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell_ts.setFixedHeight(20);
                table_header.addCell(table_cell_ts);

                PdfPCell table_cell_pa=new PdfPCell();
                table_cell_pa.setNoWrap(false);
                if(PayableAmt<0){
                    table_cell_pa.setPhrase(new Phrase((payableAmt),fontRed));
                    table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_cell_pa.setFixedHeight(20);
                }else{
                    table_cell_pa.setPhrase(new Phrase((payableAmt),fontBlue));
                    table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_cell_pa.setFixedHeight(20);
                }

                table_header.addCell(table_cell_pa);


                for(int j=i/2;j<ModelCompileStatusArrayList.size();j=j+labourList.size()){
                    if(ModelCompileStatusArrayList.get(j).getStatus().equals("P")){
                        PdfPCell present=new PdfPCell();
                        present.setNoWrap(false);
                        present.setPhrase(new Phrase(("P"),fontBlue));
                        table_header.addCell(present);
                        present.setHorizontalAlignment(Element.ALIGN_CENTER);
                    }else if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                            PdfPCell p2=new PdfPCell();
                            p2.setNoWrap(false);
                            p2.setPhrase(new Phrase(("P/2"),fontBlue));
                            p2.setHorizontalAlignment(Element.ALIGN_CENTER);
                            table_header.addCell(p2);
                        }else {
                        PdfPCell absent=new PdfPCell();
                        absent.setNoWrap(false);
                        absent.setPhrase(new Phrase(("A"),fontRed));
                        absent.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_header.addCell(absent);
                    }
                }


//                PdfPCell table_cell_att=new PdfPCell();
//                table_cell_att.setNoWrap(false);
//                table_cell_att.setPhrase(new Phrase(("A"),fontRed));


            }else{
                PdfPCell column=new PdfPCell();
                column.setNoWrap(false);
                column.setPhrase(new Phrase(("")));
                column.setColspan(10);
                column.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_header.addCell(column);

                for(int j=i/2;j<ModelCompileStatusArrayList.size();j=j+labourList.size()){
                    if(ModelCompileStatusArrayList.get(j).getAmount().equals("0")){
                        PdfPCell Amount_null=new PdfPCell();
                        Amount_null.setNoWrap(false);
                        Amount_null.setPhrase(new Phrase(("0"),fontNormal));
                        Amount_null.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_header.addCell(Amount_null);
                    }else{
                        PdfPCell Amount=new PdfPCell();
                        Amount.setNoWrap(false);
                        Amount.setPhrase(new Phrase((ModelCompileStatusArrayList.get(j).getAmount()),fontNormal));
                        Amount.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_header.addCell(Amount);
                    }
                }
            }



        }
        PdfPCell blank=new PdfPCell();
        blank.setNoWrap(false);
        blank.setPhrase(new Phrase(("")));
        blank.setColspan(10+modelDateArrayList.size());
        table_header.addCell(blank);
        PdfPCell blank1=new PdfPCell();
        blank1.setNoWrap(false);
        blank1.setPhrase(new Phrase(("Skilled/Unskilled"),fontBold));
        blank1.setHorizontalAlignment(Element.ALIGN_CENTER);
        blank1.setColspan(10);
        table_header.addCell(blank1);
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            int presentSkilled = 0;
            int presentUnskilled = 0;
            int sum=0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
                    if (ModelCompileStatusArrayList.get(i + j).getType().equals("Skilled")) {
                        presentSkilled += 1;
                    } else {
                        presentUnskilled += 1;
                    }
                }

                if (j == labourList.size() - 1) {

                    PdfPCell att=new PdfPCell();
                    att.setNoWrap(false);
                    att.setPhrase(new Phrase(("" + presentSkilled + "/" + presentUnskilled),fontBold));
                    att.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_header.addCell(att);

                }


            }

        }
        blank1.setPhrase(new Phrase(("Total Payment"),fontBold));
        blank1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_header.addCell(blank1);
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            int presentSkilled = 0;
            int presentUnskilled = 0;
            int sum=0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (!ModelCompileStatusArrayList.get(i + j).getAmount().equals("0")) {
                   sum=sum+Integer.parseInt(ModelCompileStatusArrayList.get(i + j).getAmount());
                }

                if (j == labourList.size() - 1) {

                    PdfPCell att=new PdfPCell();
                    att.setNoWrap(false);
                    att.setPhrase(new Phrase((String.valueOf(sum)),fontBold));
                    att.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_header.addCell(att);

                }


            }

        }








        try {
            iText_xls_2_pdf.add(table_header);
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

    }


    private void ExcelToPdf(File file, FileOutputStream fos, HSSFWorkbook workbook, long siteId1, String siteName1) {
        FileInputStream input_document = null;
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "HajiriRegister_"+""+"CompiledReport_" + timestamp+ ".pdf";
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
        com.itextpdf.text.Document iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 80f);


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

        String date_val = "Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName","")+
                "\n"+"Generated On: " +currentDate+
                "\n"+"Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName","")+"\n"+
                "Site Id: " + siteId1 +
                "\n Site Name: " + siteName1;;
        Paragraph p1 = new Paragraph(date_val);
        p1.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p2 = new Paragraph("Workers Compiled Report\n");
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
        //we have two columns in the Excel sheet, so we create a PDF table with two columns
        //Note: There are ways to make this dynamic in nature, if you want to.
        PdfPTable my_table = new PdfPTable(modelDateArrayList.size() + 8);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;

        //Loop through rows.


        Row row = rowIterator.next();
        Log.e("ValueI", "" + labourList.size());
//        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
//            countPresent = 0;
//            for (int j = 0; j < labourList.size(); j++) {
//                cloneJ = j;
//                Row row = sheet.getRow( 7+(2*j));
//                row.setHeightInPoints(22);
//                k = i / labourList.size();
//                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
//                Cell cell = row.createCell(k + 6);
//                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
//                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
//                    countPresent += 1;
//                    cell.setCellStyle(cellStylePresent);
//                } else {
//                    cell.setCellStyle(cellStyle);
//                }
//
//                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getStatus());
//
//            }
        for (int i = 0; i < labourList.size()*2 + 1; i++) {
            Row row1 = my_worksheet.getRow(i+6);
            Log.e("ValueI123", "I:" + i+"Row:"+(row1==null));


            if(row1!=null){
                Iterator<Cell> cellIterator = row1.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next(); //Fetch CELL

                    switch (cell.getCellType()) { //Identify CELL type
                        //you need to add more code here based on
                        //your requirement / transformations

                        case Cell.CELL_TYPE_STRING:
                            //Push the data from Excel to PDF Cell
                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                            //feel free to move the code below to suit to your needs
                            my_table.addCell(table_cell);
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            table_cell=new PdfPCell(new Phrase(String.valueOf(cell.getNumericCellValue())));
                            my_table.addCell(table_cell);
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            Log.e("Blank","Found");
                    }
                    //next line
                }
            }


        }


        PdfPTable my_table1 = new PdfPTable(modelDateArrayList.size() + 8);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell1;

        Row row1 = my_worksheet.getRow(labourList.size()*2+8);


        if(row1!=null){
            Iterator<Cell> cellIterator = row1.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); //Fetch CELL

                switch (cell.getCellType()) { //Identify CELL type
                    //you need to add more code here based on
                    //your requirement / transformations

                    case Cell.CELL_TYPE_STRING:
                        //Push the data from Excel to PDF Cell
                        table_cell1 = new PdfPCell(new Phrase(String.valueOf(cell.getStringCellValue())));

                        //feel free to move the code below to suit to your needs
                        my_table1.addCell(table_cell1);
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        table_cell1=new PdfPCell(new Phrase(String.valueOf(cell.getNumericCellValue())));
                        my_table1.addCell(table_cell1);
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        Log.e("Blank","Found");
                }
                //next line
            }
        }

        PdfPTable my_table2 = new PdfPTable(modelDateArrayList.size() + 8);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell2;

        Row row2 = my_worksheet.getRow(labourList.size()*2+9);
        Log.e("Row2",""+(row2==null));


        if(row2!=null){
            Iterator<Cell> cellIterator = row2.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); //Fetch CELL

                switch (cell.getCellType()) { //Identify CELL type
                    //you need to add more code here based on
                    //your requirement / transformations

                    case Cell.CELL_TYPE_STRING:
                        //Push the data from Excel to PDF Cell
                        table_cell2 = new PdfPCell(new Phrase(cell.getStringCellValue()));
                        //feel free to move the code below to suit to your needs
                        my_table2.addCell(table_cell2);
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        table_cell2=new PdfPCell(new Phrase(String.valueOf(cell.getNumericCellValue())));
                        my_table2.addCell(table_cell2);
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        Log.e("Blank","Found");
                }
                //next line
            }
        }

        //Loop through rows.




        //Finally add the table to PDF document
        Paragraph p4 = new Paragraph("\n");
        p4.setAlignment(Paragraph.ALIGN_CENTER);
        try {
            iText_xls_2_pdf.add(my_table);
            iText_xls_2_pdf.add(p4);
            iText_xls_2_pdf.add(my_table1);
            iText_xls_2_pdf.add(my_table2);

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
                ShowCompileListActivity.PromoSpinnerAdapter promoSpinnerAdapter = new ShowCompileListActivity.PromoSpinnerAdapter();
                spinner_promo.setAdapter(promoSpinnerAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
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
                        ShowCompileListActivity.SiteSpinnerAdapter siteSpinnerAdapter = new ShowCompileListActivity.SiteSpinnerAdapter();
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
                    adapterSiteSelect = new AdapterSiteSelect(ShowCompileListActivity.this, siteAdminList);
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
                               ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> ModelCompileStatusArrayList,
                               ArrayList<ModelLabour> list, ArrayList<ModelDate> shortDateList, long siteId1, String siteName1) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet  = workbook.createSheet();
        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
        sheet.setColumnWidth(3,10*256);
        sheet.setColumnWidth(2,11*256);
        createHeaderRow(sheet, modelDateArrayList, shortDateList,siteId1,siteName1);
        createLAbourData(sheet, labourList);
        createAttendanceData(sheet, labourList, modelDateArrayList, ModelCompileStatusArrayList);
        createPayment(sheet, labourList, modelDateArrayList, ModelCompileStatusArrayList);
        createFooter(sheet, labourList, modelDateArrayList, ModelCompileStatusArrayList);

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String timestamp = "" + System.currentTimeMillis();
            Log.e("StrPath", str_path);
            file = new File(str_path, "HajiriRegister_"+""+"CompiledReoprt_" + timestamp+ ".xls");

//            fos = new FileOutputStream(file);
            Log.e("FilePath", file.getAbsolutePath().toString());

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

                DownloadPdf(file, fos, workbook,siteId1,siteName1,labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList, siteId1, siteName1);

            } else {
//                Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//                createOrderId.start();
                str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                Log.e("StrPath", str_path);
                file = new File(str_path, "HajiriRegister_"+""+"CompiledReoprt_" + timestamp+ ".xls");

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
                DownloadPdf(file, fos, workbook,siteId1,siteName1,labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList, siteId1, siteName1);
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

//            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ShowCompileListActivity.this);
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
////                                checkout.open(ShowCompileListActivity.this, object);
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

    private void createPayment(Sheet sheet, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
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
        int countPresent = 0, cloneJ = 0, k = 0;

        for (int j = 0; j < labourList.size(); j++) {
            cloneJ = j;
            Row row = sheet.createRow( 8+(2*j));
//                k = i / 10;
//                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
//                Cell cell = row.createCell(k + 5);
//                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
//                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("0")) {
//                    countPresent += 1;
//                    cell.setCellStyle(cellStyle);
//                } else {
//                    cell.setCellStyle(cellStylePresent);
//                }
//
//                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getAmount());


//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
//            int y=i/10;
//            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
        }
        createPaymentDatasheet(sheet, labourList,
                modelDateArrayList,modelCompileStatusArrayList);


    }

    private void createPaymentDatasheet(Sheet sheet, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList,
                                        ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);;
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);

        CellStyle cellStyleUnderLine = sheet.getWorkbook().createCellStyle();
        Font font1 = sheet.getWorkbook().createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font1.setFontHeightInPoints((short) 12);
        font1.setUnderline(Font.U_SINGLE);
        cellStyleUnderLine.setFont(font1);
        cellStyleUnderLine.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleUnderLine.setBorderBottom((short) 1);
        cellStyleUnderLine.setBorderTop((short) 1);
        cellStyleUnderLine.setBorderLeft((short) 1);
        cellStyleUnderLine.setBorderRight((short) 1);
        cellStyleUnderLine.setWrapText(true);

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

        CellStyle cellStyleNegative = sheet.getWorkbook().createCellStyle();
        Font font3 = sheet.getWorkbook().createFont();
        font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font3.setUnderline(Font.U_SINGLE);
        font3.setFontHeightInPoints((short) 12);
        font3.setColor(Font.COLOR_RED);
        cellStyleNegative.setFont(font3);

        int countPresent = 0, cloneJ = 0, k = 0;
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {

            for (int j = 0; j < labourList.size(); j++) {
                cloneJ = j;
                Row row = sheet.getRow(8 + (2 * j));
                row.setHeightInPoints(22);
                k = i / labourList.size();
                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
                Cell cell = row.createCell(k + 8);
                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("0")) {
                    countPresent += 1;
                    cell.setCellStyle(cellStyle);
                } else {
                    cell.setCellStyle(cellStylePresent);
                }

                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getAmount());



//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
//            int y=i/10;
//            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
            }
        }
        for (int j = 0; j < labourList.size(); j++) {
            for (int m = 0; m < 8; m++) {
                Row row = sheet.getRow(8 + (2 * j));
                row.setHeightInPoints(22);
                Cell cell = row.createCell(m);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");

            }
        }
        for(int s=0;s<labourList.size();s++){
//            int CountPresent=0;
            int  AmtSum=0;
            float countHalf=0;
            float CountPresent=0;
            float countTotal=0;
            for(int j=s;j<ModelCompileStatusArrayList.size();j=j+labourList.size()){
                if(!ModelCompileStatusArrayList.get(j).getStatus().equals("0")){
                    AmtSum=AmtSum+Integer.parseInt(ModelCompileStatusArrayList.get(j).getAmount());
                }
                if(ModelCompileStatusArrayList.get(j).getStatus().equals("P")){
                    CountPresent++;
                }else {
                    if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                        countHalf++;
                    }
                }



            }
            Log.e("LabourCheck","S::::"+s+":::"+labourList.get(s/labourList.size()).getLabourId());
            int PayableAmt= (int) ((labourList.get(s).getWages()*CountPresent+ ((labourList.get(s/labourList.size()).getWages()/2)*countHalf))-AmtSum);
//            countTotal=countPresent+(countHalf/2);
            Log.e("Stats123",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+AmtSum+"/"+modelDateArrayList.size());
            Row rowPresentStatus = sheet.getRow((7+(2*s)));
            Cell cellPresentStatus=rowPresentStatus.createCell(6);
            if(AmtSum>0){
                cellPresentStatus.setCellStyle(cellStyleUnderLine);
            }else{
                cellPresentStatus.setCellStyle(cellStyle);
            }
            String status=String.valueOf(AmtSum);

            cellPresentStatus.setCellValue(status);
            Cell cellPayableAmt=rowPresentStatus.createCell(7);
            String payableAmt=String.valueOf(PayableAmt);
            if(PayableAmt<0){
                cellPayableAmt.setCellStyle(cellStyleNegative);
            }else{
                cellPayableAmt.setCellStyle(cellStyleUnderLine);
            }

            cellPayableAmt.setCellValue(payableAmt);
        }
    }

    private void createOrderId(int amount, AlertDialog.Builder builder) throws RazorpayException, JSONException {


        // Handle Exception


    }

    private void createFooter(Sheet sheet, ArrayList<ModelLabour> labourList,
                              ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> ModelCompileStatusArrayList) {
        int totalSkilledCount = 0, totalUnskilledCount = 0, presentSkilled = 0, presentUnskilled = 0, sum = 0;
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



       
        sheet.createRow((labourList.size() * 2) + 8);
        sheet.createRow((labourList.size() * 2) + 9);
        

        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            presentSkilled = 0;
            presentUnskilled = 0;
            sum = 0;
            for (int j = 0; j < labourList.size(); j++) {

                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
                    if (ModelCompileStatusArrayList.get(i + j).getType().equals("Skilled")) {
                        presentSkilled += 1;
                    } else {
                        presentUnskilled += 1;
                    }
                }
                if (!ModelCompileStatusArrayList.get(i + j).getAmount().equals("0")) {
                    sum = sum + Integer.parseInt(ModelCompileStatusArrayList.get(i + j).getAmount());

                }

                if (j == labourList.size() - 1) {
                    int y = i / labourList.size();
                    Row rowPresentSkilled = sheet.getRow((labourList.size() * 2) + 8);
                    rowPresentSkilled.setHeightInPoints(22);
                    Cell Status = rowPresentSkilled.createCell(y + 8);
                    Status.setCellStyle(cellStyle);
                    Status.setCellValue("" + presentSkilled + "/" + presentUnskilled);
                    Row rowTotalPayment = sheet.getRow((labourList.size() * 2) + 9);
                    rowTotalPayment.setHeightInPoints(22);
                    Cell Payment = rowTotalPayment.createCell(y + 8);
                    Payment.setCellStyle(cellStyle);
                    Payment.setCellValue(sum);

                }


            }


//            float countPresent = 0;
//            float countHalf = 0;
//            float countTotal = 0;
//
//            for (int s = 0; s < ModelCompileStatusArrayList.size(); s++) {
//                if (ModelCompileStatusArrayList.get(s).getStatus().equals("P")) {
//                    countPresent++;
//                } else if (ModelCompileStatusArrayList.get(s).getStatus().equals("P/2")) {
//                    countHalf++;
//                }
//                countTotal = countPresent + (countHalf / 2);
//            }
//            Row rowPresentSkilled = sheet.getRow(labourList.size() + 8);
//            Cell Status = rowPresentSkilled.createCell(4);
//            Status.setCellStyle(cellStylePresent);
//            Status.setCellValue("" + countTotal + "/" + ModelCompileStatusArrayList.size());


            Row row = sheet.createRow(labourList.size()*2 + 12);
            Cell cellFullName = row.createCell(0);


            cellFullName.setCellStyle(cellStyle2);
//        cellFullName.setCellValue("Attendance Report");
            String date_val = "Generated by: " + " " + "Hajiri Register";
            cellFullName.setCellValue(date_val);

            CellRangeAddress cellMerge = new CellRangeAddress(labourList.size()*2 + 12, labourList.size()*2 + 14, 0, modelDateArrayList.size() + 4);
            sheet.addMergedRegion(cellMerge);

            String date_val1 = "Powered by: " + " " + "Skill Zoomers";

            Row row1 = sheet.createRow(labourList.size()*2 + 14);
            Cell cellHeading = row1.createCell(0);

            cellHeading.setCellStyle(cellStyle2);
            cellHeading.setCellValue(date_val1);

            CellRangeAddress cellMerge1 = new CellRangeAddress(labourList.size()*2 + 16, labourList.size()*2 + 18, 0, modelDateArrayList.size() + 4);
            sheet.addMergedRegion(cellMerge1);

            String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ " + " " + "Skill Zoomers";

            Row row2 = sheet.createRow(labourList.size()*2 + 20);
            Cell cellHeading1 = row2.createCell(0);

            cellHeading1.setCellStyle(cellStyle2);
            cellHeading1.setCellValue(date_val2);

            CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size()*2 + 20, labourList.size()*2 + 22, 0, modelDateArrayList.size() + 4);
            sheet.addMergedRegion(cellMerge2);


        }
//        Row row5=sheet.createRow(labourList.size()*2+7);
//        for(int l=0;l<modelDateArrayList.size()+5;l++){
//            Cell Status = row5.createCell(0);
//            Status.setCellStyle(cellStylePresent);
//            Status.setCellValue("");
//        }
        for(int k=8;k<10;k++){
            Row row=sheet.getRow((labourList.size()*2)+k);
            for(int m=0;m<5;m++){
                Cell Status = row.createCell(m);
                Status.setCellStyle(cellStyle2);
                Status.setCellValue("");
            }
            Cell Status = row.createCell(5);
            Status.setCellStyle(cellStyle);
            if(k==8){
                Status.setCellValue("Skilled/Unskilled");
            }else{
                Status.setCellValue("Total Payment");
            }
            row.createCell(6).setCellStyle(cellStyle);
            row.createCell(7).setCellStyle(cellStyle);
            CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size()*2 + k, labourList.size()*2 + k, 5, 7);
            sheet.addMergedRegion(cellMerge2);



        }

    }

    private void createAttendanceData(Sheet sheet, ArrayList<ModelLabour> labourList,
                                      ArrayList<ModelDate> modelDateArrayList,
                                      ArrayList<ModelCompileStatus> ModelCompileStatusArrayList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setColor(HSSFColor.RED.index);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
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
        int countPresent = 0, cloneJ = 0, k = 0;
        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
            countPresent = 0;
            for (int j = 0; j < labourList.size(); j++) {
                cloneJ = j;
                Row row = sheet.getRow( 7+(2*j));
                row.setHeightInPoints(22);
                k = i / labourList.size();
                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
                Cell cell = row.createCell(k + 8);
                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
                    countPresent += 1;
                    cell.setCellStyle(cellStylePresent);
                } else {
                    cell.setCellStyle(cellStyle);
                }

                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getStatus());

            }
//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
//            int y=i/labourList.size();
//            Row rowPresentStatus = sheet.createRow(y+6);
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countPresent+"/"+labourList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
        }
        for(int s=0;s<labourList.size();s++){
//            int CountPresent=0;
            float CountPresent=0;
            float countHalf=0;
            float countTotal=0;
            for(int j=s;j<ModelCompileStatusArrayList.size();j=j+labourList.size()){
                if(ModelCompileStatusArrayList.get(j).getStatus().equals("P")){
                    CountPresent++;
                }else {
                    if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                        countHalf++;
                    }
                }

            }
            countTotal=CountPresent+(countHalf/2);
            Log.e("Stats1256",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+CountPresent+"/"+modelDateArrayList.size());
            Log.e("Stats1256",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+countTotal+"/"+modelDateArrayList.size());
            Row rowPresentStatus = sheet.getRow((7+(2*s)));
            Cell cellPresentStatus=rowPresentStatus.createCell(5);
            String status=""+countTotal;
            cellPresentStatus.setCellStyle(cellStyle2);
            cellPresentStatus.setCellValue(status);
        }
//        for(int s=0;s<labourList.size();s++){
////            int CountPresent=0;
//            float CountPresent=0;
//            float countHalf=0;
//            float countTotal=0;
//            for(int j=s;j<ModelCompileStatusArrayList.size();j=j+labourList.size()){
//                if(ModelCompileStatusArrayList.get(k).getStatus().equals("P")){
//                    CountPresent++;
//                }else {
//                    if (ModelCompileStatusArrayList.get(k).getStatus().equals("P/2")) {
//                        countHalf++;
//                    }
//                }
//
//            }
//            countTotal=countPresent+(countHalf/2);
//            Log.e("Stats123",ModelCompileStatusArrayList.get(s).getLabourId()+":::"+CountPresent+"/"+modelDateArrayList.size());
//            Row rowPresentStatus = sheet.getRow((s+7));
//            Cell cellPresentStatus=rowPresentStatus.createCell(4);
//            String status=countTotal+"/"+modelDateArrayList.size();
//            cellPresentStatus.setCellStyle(cellStylePresent);
//            cellPresentStatus.setCellValue(status);
//        }
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
            Row row = sheet.createRow((7+(2*i)));
            row.setHeightInPoints(22);
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
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(cellStyle);
            cell4.setCellValue(labourList.get(i).getWages());



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

        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 3, 7, 12);
        sheet.addMergedRegion(cellMerge1);

        Cell cellFullName3=row.createCell(13);
        cellFullName3.setCellStyle(cellStyle1);
        String date_val3="From: " +" "+dateModelArrayList.get(0).getDate()+"\n"+ "To: " + dateModelArrayList.get(dateModelArrayList.size()-1).getDate();
        cellFullName3.setCellValue(date_val3);


        CellRangeAddress cellMerge3 = new CellRangeAddress(0, 3, 13, 17);
        sheet.addMergedRegion(cellMerge3);




        Row row1 = sheet.createRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("Workers Compiled Report"+"\t\t\t"+"Total No of Days:"+modelDateArrayList.size());

        CellRangeAddress cellMerge2 = new CellRangeAddress(4, 5, 0, size);
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
        cellSrNo4.setCellValue("Wages");
        Cell cellSrNo6 = rowValues.createCell(5);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Total Attendance");
        Cell cellSrNo7 = rowValues.createCell(6);
        cellSrNo7.setCellStyle(cellStyle);
        cellSrNo7.setCellValue("Total Advances");
        Cell cellSrNo5 = rowValues.createCell(7);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("PayableAmt");

        for (int i = 0; i < shortDateList.size(); i++) {
            Cell dateNo = rowValues.createCell(i + 8);
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

        Log.e("Compile10123",""+(uid==null));
        Log.e("Compile10123",""+(uid));
        Log.e("Compile10123",""+(siteId));



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours").orderByChild("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e("Snapshopt",""+snapshot.exists());
                labourList.clear();
                Log.e("Compile10123",""+(snapshot.getChildrenCount()));
                Log.e("SiteId", "Labour" + siteId);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.e("DASGF",ds.getKey());
                    ModelLabour modelLabour = ds.getValue(ModelLabour.class);
//                    Log.e("Compile101234",modelLabour.getLabourId());
                    labourList.add(modelLabour);

                }
                Log.e("LabourListSize123", "" + labourList.size());
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
        reference.child(uid).child("Industry").child("Construction").child("Site").orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
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
//                    ArrayAdapter<ModelSiteSpinner> adapter = new ArrayAdapter<ModelSiteSpinner>(ShowCompileListActivity.this, android.R.layout.simple_spinner_item, siteNameArrayList);
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
                Log.e("ModelCompileStatus", "" + labourList.size());
                for (int j = 0; j < labourList.size(); j++) {
//                    Log.e("date", modelDateArrayList.get(i).getDate());
//                    Log.e("date", "" + siteId);
//                    Log.e("date", labourList.get(j).getLabourId());
                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getType());
                }
            }
            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
            Log.e("ModelCompileStatus", "AfterLoop" + ModelCompileStatusArrayList.size());
        }


    }


    private void getAttendanceList(String date, String labourId, String type) {

        Log.e("Compile",""+uid);
        Log.e("Compile",""+siteId);
        Log.e("Compile",""+date);
        Log.e("Compile",""+labourId);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance")
                .child("Labours").child(date).child(labourId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String status1 = "";
                            if (snapshot.child("status").getValue(String.class) != null) {
                                status1 = snapshot.child("status").getValue(String.class);
                            } else {
                                status1 = "P";
                            }


                            getPaymentStatus(date,labourId,status1,type);
//                            Log.e("model1234", "ID" + ModelCompileStatus.getLabourId() + status + ModelCompileStatus.getStatus());


//                            Log.e("Size11111",""+labourList.size());
//                            Log.e("Size22222",""+modelAttendances.size());
//                            Log.e("Size33333",""+modelDateArrayList.size());
//                            if(modelDateArrayList.size()>0) {
//                                AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowCompileListActivity.this ,
//                                        labourList,
//                                        modelAttendances ,
//                                        modelDateArrayList);
//                                binding.rvShowAttendance.setAdapter(adapterShowAttendance);
//                            }

                        } else {
                            getPaymentStatus(date,labourId,"A",type);

                        }

//                        for(int i=0;i<ModelCompileStatusArrayList.size();i++) {
//                            Log.e("ModelCompileStatusArrayList" , "" + ModelCompileStatusArrayList.get(i).getLabourId()
//                            +"Status"+ModelCompileStatusArrayList.get(i).getStatus());
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPaymentStatus(String date, String labourId, String status1, String type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction")
                .child("Site").child(String.valueOf(siteId))
                .child("Payments")
                .child(date).orderByChild("labourId")
                .equalTo(labourId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountSum=0;
                        if (snapshot.getChildrenCount()>0) {
                            for(DataSnapshot ds:snapshot.getChildren()){
                                String amt=ds.child("amount").getValue(String.class);
                                Log.e("Amt",amt);
                                amountSum=amountSum+Integer.parseInt(amt);
                            }
                            ModelCompileStatus modelCompileStatus = new ModelCompileStatus(date, labourId, status1,type,String.valueOf(amountSum));
                            addToarray(modelCompileStatus);



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
                            ModelCompileStatus modelCompileStatus = new ModelCompileStatus(date, labourId, status1,type,"0");
                            addToarray(modelCompileStatus);


                        }

//                        for(int i=0;i<ModelCompileStatusArrayList.size();i++) {
//                            Log.e("ModelCompileStatusArrayList" , "" + ModelCompileStatusArrayList.get(i).getLabourId()
//                            +"Status"+ModelCompileStatusArrayList.get(i).getStatus());
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addToarray(ModelCompileStatus ModelCompileStatus) {
        countLoop++;

//        Log.e("ModelCompileStatus", "Count:" + countLoop);
//        Log.e("ModelCompileStatus", "LabourSize:" + labourList.size());
//        Log.e("ModelCompileStatus", "DateSize:" + modelDateArrayList.size());
//
        ModelCompileStatusArrayList.add(ModelCompileStatus);
//        Log.e("ModelCompileStatus","CountLoop:"+countLoop+"\t"+"Date:"+ModelCompileStatus.getDate()+"\t"+"LabourID"+ModelCompileStatus.getLabourId()
//        +"\t"+"Status:"+ModelCompileStatus.getStatus());
//        Log.e("ModelCompileStatus", ModelCompileStatus.getLabourId());
//        Log.e("ModelCompileStatus", "Status" + ModelCompileStatus.getStatus());
//        Log.e("ModelCompileStatus", "Size:" + ModelCompileStatusArrayList.size());
        if (countLoop == labourList.size() * modelDateArrayList.size()) {
            Log.e("CountLoop", "Here");
            progressDialog.dismiss();
            binding.tableview.setVisibility(View.VISIBLE);
            binding.txtMessage.setVisibility(View.GONE);
            if (status.equals("ShowAttendance")) {
                initialiseTableView(labourList, modelDateArrayList, ModelCompileStatusArrayList);
            } else {
//                DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList,siteId,siteName);
            }

//            AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowCompileListActivity.this,
//                    labourList,
//                    ModelCompileStatusArrayList,
//                    modelDateArrayList);
//            binding.rvShowAttendance.setAdapter(adapterShowAttendance);
//
//            AdapterNestedChild1 adapterNestedChild1 = new AdapterNestedChild1(ShowCompileListActivity.this, ModelCompileStatusArrayList, modelDateArrayList, labourList);
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
                                     ArrayList<ModelCompileStatus> ModelCompileStatusArrayList) {
        TableViewModel tableViewModel = new TableViewModel(modelDateArrayList.size() + 5,
                labourList.size()*2, labourList, modelDateArrayList,null, ModelCompileStatusArrayList, "Compile");

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
//                                                        Toast.makeText(ShowCompileListActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
//                                                    } else {
                                    ModelCompileStatusArrayList.clear();
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
                                            if (snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(modelDateArrayList.get(m).getDate()).child(modelLabour.getLabourId())
                                                    .getChildrenCount() > 0) {
                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0){
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
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"P",modelLabour.getType(),String.valueOf(AmtSum));
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }else{
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"P",modelLabour.getType(),"0");
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }
                                            } else {
                                                if (snapshot.child(String.valueOf(siteId1)).child("Payments").child(modelDateArrayList.get(m).getDate()).getChildrenCount() > 0){
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
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"A",modelLabour.getType(),String.valueOf(AmtSum));
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }else{
                                                    ModelCompileStatus modelCompileStatus=new ModelCompileStatus(modelDateArrayList.get(m).getDate(),modelLabour.getLabourId(),"A",modelLabour.getType(),"0");
                                                    ModelCompileStatusArrayList.add(modelCompileStatus);
                                                }
                                            }


                                        }

                                    }
//                                                        modelDateArrayList.add(new ModelDate(currentDate11));
//                                                        shortDateList.add(new ModelDate(currentDate11));


                                    Log.e("SizeCheck", "SiteID:" + siteId1);
                                    Log.e("SizeCheck", "Labour: " + labourList.size());
                                    Log.e("SizeCheck", "Date: " + modelDateArrayList.size());
                                    Log.e("SizeCheck", "Status: " + ModelCompileStatusArrayList.size());


                                    DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList, siteId1, siteName1);

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
//                                Log.e("ModelCompileStatus", "" + ModelCompileStatusArrayList.size());
//                                for (int j = 0; j < labourList.size(); j++) {
//                                    Log.e("date", modelDateArrayList.get(i).getDate());
//                                    Log.e("date", "" + siteId);
//                                    Log.e("date", labourList.get(j).getLabourId());
//                                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getType());
//                                }
//                            }
//                            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
//                            Log.e("ModelCompileStatus", "AfterLoop" + ModelCompileStatusArrayList.size());
        }

//        if (dnl_file_type.equals("pdf")) {
//            ExcelToPdf(file, fos, workbook,siteId,siteName);
//        } else {
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            Log.e("StrPath", str_path);
//            file = new File(str_path, "CompiledReoprt_" + siteName + "_" + currentDate + ".xls");
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
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
                hashMap.put("downloadFile", "Attendance Report");
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
                                Toast.makeText(ShowCompileListActivity.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                Uri dirUri = FileProvider.getUriForFile(ShowCompileListActivity.this,getApplicationContext().getPackageName() + ".com.skillzoomer_Attendance.com",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
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
                                Toast.makeText(ShowCompileListActivity.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();

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