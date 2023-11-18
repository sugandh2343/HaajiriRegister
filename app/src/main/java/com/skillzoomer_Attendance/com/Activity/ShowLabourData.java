package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.skillzoomer_Attendance.com.Adapter.AdapterLabourSelect;
import com.skillzoomer_Attendance.com.Adapter.AdapterSiteSelect;
import com.skillzoomer_Attendance.com.Adapter.TableViewAdapter;
import com.skillzoomer_Attendance.com.Adapter.TableViewListener;
import com.skillzoomer_Attendance.com.Adapter.TableViewModel;
import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Utilities.HeaderFooterPageEvent;
import com.skillzoomer_Attendance.com.databinding.ActivityShowLabourDataBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ShowLabourData extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityShowLabourDataBinding binding;
    private ArrayList<ModelSite> siteAdminList;
    private ArrayList<ModelLabour> labourArrayList;
    private ArrayList<ModelLabour> labourSiteArrayList;
    private ArrayList<ModelLabour> searchLabourArrayList;

    private ArrayList<ModelLabour> labourListFinal;

    private ArrayList<ModelDate> modelDateArrayList;

    private ArrayList<ModelLabour> finalShowLabour;

    String currentDate;

    ArrayList<ModelDate> shortDateList;

    File file = null;

    FileOutputStream fos = null;


    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    long siteId;
    String siteName;

    FirebaseAuth firebaseAuth;

    String status;

    AdapterSiteSelect adapterSiteSelect;

    private ProgressDialog progressDialog;

    ArrayList<String> result;
    private String searchtype;

    private String toDate = "", fromDate = "";
    private final Calendar myCalendar = Calendar.getInstance();
    private ArrayList<ModelCompileStatus> ModelCompileStatusArrayList;

    int countLoop = 0;

    long AmountRuleExcel = 0, AmountRulePdf = 0;
    int amount = 0;
    int AmountTemp = 0;
    private String dnl_file_type = "";
    AlertDialog alertDialogPaymentConfirm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowLabourDataBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        siteAdminList = new ArrayList<>();
        labourArrayList = new ArrayList<>();
        labourSiteArrayList = new ArrayList<>();

        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        ModelCompileStatusArrayList = new ArrayList<>();
        finalShowLabour = new ArrayList<>();

        binding.btnDownloadReport.setVisibility(View.GONE);

        modelDateArrayList = new ArrayList<>();
        shortDateList = new ArrayList<>();

        labourListFinal = new ArrayList<>();

        searchLabourArrayList = new ArrayList<>();


        Intent intent = getIntent();

        status = intent.getStringExtra("Activity");

        Log.e("siteAdminList123", status);

        getSiteListAdministrator();


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

        if (intent.getStringExtra("Activity").equals("ShowAttendance")) {

            binding.btnDownloadReport.setVisibility(View.GONE);
        } else {

            binding.btnDownloadReport.setVisibility(View.GONE);
        }
        binding.llSearchType.setVisibility(View.VISIBLE);
        binding.llSearchLabour.setVisibility(View.GONE);
        binding.llList.setVisibility(View.GONE);
        binding.llSelectSite.setVisibility(View.VISIBLE);

        binding.btnBySite.setChecked(true);

        searchtype = "Site";


        binding.btnBySite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnByLabourDetails.isChecked()) {
                    binding.btnBySite.setChecked(true);
                    binding.btnByLabourDetails.setChecked(false);
                    binding.llSearchLabour.setVisibility(View.GONE);
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    searchtype = "Site";

                }

            }
        });

        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    siteId = siteAdminList.get(i).getSiteId();
                    siteName = siteAdminList.get(i).getSiteName();
                    Log.e("SiteId", "Spinner" + siteId);


                    binding.llSearchLabour.setVisibility(View.VISIBLE);
                    getLabourList(siteId, siteName);
                } else {

                    binding.llSearchLabour.setVisibility(View.GONE);
                    binding.llLabour.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnByLabourDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnBySite.isChecked()) {
                    binding.btnBySite.setChecked(false);
                    binding.btnByLabourDetails.setChecked(true);
                    binding.llSelectSite.setVisibility(View.GONE);
                    binding.llSearchLabour.setVisibility(View.VISIBLE);
                    searchtype = "Labour";
                    getSiteListAdministrator();
                }

            }
        });

        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llAfterLabourSelect.setVisibility(View.VISIBLE);
                binding.llBeforeLabourSelect.setVisibility(View.GONE);


            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_position"));

        binding.ivNameMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            .makeText(ShowLabourData.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }

        });

        binding.etSearchLabour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 2) {
                    String value = charSequence.toString();
                    searchLabourArrayList.clear();

                    if (binding.btnBySite.isChecked()) {
                        if (labourSiteArrayList.size() > 0) {
                            for (int m = 0; m < labourSiteArrayList.size(); m++) {
                                Log.e("Check", labourArrayList.get(m).getLabourId().toLowerCase(Locale.ROOT));
                                if (labourSiteArrayList.get(m).getLabourId().toLowerCase(Locale.ROOT).contains(value.toLowerCase())
                                        || labourSiteArrayList.get(m).getName().toLowerCase(Locale.ROOT).contains(value.toLowerCase())
                                        || labourSiteArrayList.get(m).getUniqueId().toLowerCase(Locale.ROOT).contains(value.toLowerCase())) {
                                    ModelLabour labourSearch = labourSiteArrayList.get(m);
                                    labourSearch.setOriginalPosition(m);
                                    searchLabourArrayList.add(labourSearch);
                                }
                            }
                            if (searchLabourArrayList.size() > 0) {
                                AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, searchLabourArrayList);
                                binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                            }
                        }
                    } else if (binding.btnByLabourDetails.isChecked()) {
                        if (labourArrayList.size() > 0) {
                            for (int m = 0; m < labourArrayList.size(); m++) {
                                Log.e("Check", labourArrayList.get(m).getLabourId().toLowerCase(Locale.ROOT));

                                if (labourArrayList.get(m).getLabourId().toLowerCase(Locale.ROOT).contains(value.toLowerCase())
                                        || labourArrayList.get(m).getName().toLowerCase(Locale.ROOT).contains(value.toLowerCase())
                                        || labourArrayList.get(m).getUniqueId().toLowerCase(Locale.ROOT).contains(value.toLowerCase())) {
                                    ModelLabour labourSearch = labourArrayList.get(m);
                                    labourSearch.setOriginalPosition(m);
                                    searchLabourArrayList.add(labourSearch);
                                }
                            }
                            if (searchLabourArrayList.size() > 0) {
                                AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, searchLabourArrayList);
                                binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                            }
                        }
                    }
                } else {
                    if (binding.btnBySite.isChecked()) {
                        AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, labourSiteArrayList);
                        binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                    } else {
                        AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, labourArrayList);
                        binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                finalShowLabour.clear();
                progressDialog.show();
                if (searchtype.equals("Site")) {
                    labourListFinal = labourSiteArrayList;
                } else {
                    labourListFinal = labourArrayList;
                }


                for (int i = 0; i < labourListFinal.size(); i++) {
                    if (labourListFinal.get(i).getPresent()) {
                        finalShowLabour.add(labourListFinal.get(i));
                    }
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Boolean f = true;
                try {
                    Date fDate = dateFormat.parse(fromDate);
                    Date tDate = dateFormat.parse(toDate);
                    if (tDate.before(fDate)) {
                        Toast.makeText(ShowLabourData.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(ShowLabourData.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(ShowLabourData.this, "Enter End Date", Toast.LENGTH_SHORT).show();
                } else if (f) {
                    ModelCompileStatusArrayList.clear();
                    try {
                        getDateRange(fromDate, toDate, finalShowLabour);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });

//        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    for (int i = 0; i < finalShowLabour.size(); i++) {
//                        DownloadPdf(modelDateArrayList, ModelCompileStatusArrayList, shortDateList, finalShowLabour.get(i), i);
//                    }
//
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
//                for (int i = 0; i < ModelCompileStatusArrayList.size(); i++) {
//                    Log.e("MCAL123", "i::::" + i + ModelCompileStatusArrayList.get(i).getLabourId() + "DATE" + ModelCompileStatusArrayList.get(i).getDate());
//                }
//            }
//        });

        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
                reference.child("LabourReport").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                        AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final AlertDialog.Builder alert = new AlertDialog.Builder(ShowLabourData.this);
                View mView = getLayoutInflater().inflate(R.layout.show_file_type, null);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Boolean f = true;
                try {

                    Date fDate = dateFormat.parse(fromDate);
                    Date tDate = dateFormat.parse(toDate);
                    if (tDate.before(fDate)) {
                        Toast.makeText(ShowLabourData.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                        f = false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (fromDate.equals("")) {
                    Toast.makeText(ShowLabourData.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                } else if (toDate.equals("")) {
                    Toast.makeText(ShowLabourData.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                RadioButton rb_pdf, rb_xls;
                Button btn_pay;
                TextView txt_amount;
                LinearLayout ll_amount,ll_image;
                ImageView iv_close;
                rb_pdf = mView.findViewById(R.id.rb_pdf);
                rb_xls = mView.findViewById(R.id.rb_xls);
                btn_pay = mView.findViewById(R.id.btn_pay);
                txt_amount = mView.findViewById(R.id.txt_amount);
                ll_amount = mView.findViewById(R.id.ll_amount);
                iv_close = mView.findViewById(R.id.iv_close);
                ll_image = mView.findViewById(R.id.ll_image);
                ll_amount.setVisibility(View.GONE);
                alert.setView(mView);
                AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                rb_xls.setVisibility(View.GONE);
                ll_image.setVisibility(View.GONE);

                rb_pdf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            rb_xls.setChecked(false);
                            dnl_file_type = "pdf";
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Boolean f = true;
                            try {

                                Date fDate = dateFormat.parse(fromDate);
                                Date tDate = dateFormat.parse(toDate);
                                if (tDate.before(fDate)) {
                                    Toast.makeText(ShowLabourData.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                                    f = false;
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (fromDate.equals("")) {
                                Toast.makeText(ShowLabourData.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                            } else if (toDate.equals("")) {
                                Toast.makeText(ShowLabourData.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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

                                        amount = 1* modelDateArrayList.size();


                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
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
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Boolean f = true;
                            try {

                                Date fDate = dateFormat.parse(fromDate);
                                Date tDate = dateFormat.parse(toDate);
                                if (tDate.before(fDate)) {
                                    Toast.makeText(ShowLabourData.this, "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show();
                                    f = false;
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (fromDate.equals("")) {
                                Toast.makeText(ShowLabourData.this, "Enter Start Date", Toast.LENGTH_LONG).show();
                            } else if (toDate.equals("")) {
                                Toast.makeText(ShowLabourData.this, "Enter End Date", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ShowLabourData.this, "Select file type", Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowLabourData.this);
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



        txt_heading.setText(getString(R.string.your_file_one_click_away) + " " + String.valueOf(AmountTemp) + " " + getString(R.string.your_have_tom_make_payment));
        txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(AmountTemp));
        btn_cancel = mView.findViewById(R.id.btn_cancel);
        btn_pay = mView.findViewById(R.id.btn_pay);
        txt_file_type.setText("Workers Compiled Report");
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

    private void DownloadPdf(ArrayList<ModelDate> modelDateArrayList,
                             ArrayList<ModelCompileStatus> modelCompileStatusArrayList, ArrayList<ModelDate> shortDateList,
                             ModelLabour modelLabour, int value) throws DocumentException {
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + modelLabour.getName() + "" + "CompiledReoprt_" + timestamp + ".pdf";
        File pdfFile = new File(str_path);
        com.itextpdf.text.Document iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 80f);

        try {
            PdfWriter writer = PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(ShowLabourData.this, 0, "", modelDateArrayList, "Worker Compiled Report");
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

        createPdfHeaderRow(modelDateArrayList, shortDateList, iText_xls_2_pdf, my_table, modelLabour, value);
    }

    private void createPdfHeaderRow(ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelDate> shortDateList,
                                    Document iText_xls_2_pdf, PdfPTable my_table, ModelLabour modelLabour, int value) throws DocumentException {
        PdfPTable table_header = new PdfPTable(6);
        Log.e("Labjghyfgu", "" + finalShowLabour.size());

//        table_header.setWidths(new float[modelDateArrayList.size()+8]);

        com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font();
        fontBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontBold.setSize(12);
        fontBold.setColor(BaseColor.BLACK);
        fontBold.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font();
        fontNormal.setStyle(com.itextpdf.text.Font.NORMAL);
        fontNormal.setSize(12);
        fontNormal.setColor(BaseColor.BLACK);

        com.itextpdf.text.Font fontGreen = new com.itextpdf.text.Font();
        fontGreen.setStyle(com.itextpdf.text.Font.BOLD);
        fontGreen.setSize(12);
        fontGreen.setColor(BaseColor.BLACK);

        com.itextpdf.text.Font fontRed = new com.itextpdf.text.Font();
        fontRed.setStyle(com.itextpdf.text.Font.BOLD);
        fontRed.setSize(12);
        fontRed.setColor(BaseColor.RED);

        com.itextpdf.text.Font fontRedHeading = new com.itextpdf.text.Font();
        fontRedHeading.setStyle(com.itextpdf.text.Font.BOLD | com.itextpdf.text.Font.UNDERLINE);
        fontRedHeading.setSize(12);
        fontRedHeading.setColor(BaseColor.RED);

        com.itextpdf.text.Font fontBlue = new com.itextpdf.text.Font();
        fontBlue.setStyle(com.itextpdf.text.Font.NORMAL);
        fontBlue.setSize(12);
        fontBlue.setColor(BaseColor.BLUE);


        PdfPCell id, name, type, wages;
        id = (new PdfPCell(new Phrase("Worker Id: " + "\t\t" + modelLabour.getLabourId(), fontBold)));
        name = (new PdfPCell(new Phrase("Worker Name: " + "\t\t" + modelLabour.getName(), fontBold)));
        type = (new PdfPCell(new Phrase("Type: " + "\t\t" + modelLabour.getType(), fontBold)));
        wages = (new PdfPCell(new Phrase("Wages: " + "\t\t" + modelLabour.getWages(), fontBold)));

        id.setColspan(2);
        name.setColspan(2);
        table_header.addCell(id);
        table_header.addCell(name);
        table_header.addCell(type);
        table_header.addCell(wages);

//        PdfPCell tableCell_Id = new PdfPCell();
//        tableCell_Id.setNoWrap(false);
//        Log.e("Modejkhg",modelLabour.getLabourId());
//        tableCell_Id.setPhrase(new Phrase(modelLabour.getLabourId(), fontNormal));
//        tableCell_Id.setHorizontalAlignment(Element.ALIGN_CENTER);
//        tableCell_Id.setFixedHeight(20);
//        tableCell_Id.setColspan(2);
//        table_header.addCell(tableCell_Id);
//
//
//        PdfPCell tableCell_Name = new PdfPCell();
//        tableCell_Name.setNoWrap(false);
//        tableCell_Name.setPhrase(new Phrase(modelLabour.getName(), fontNormal));
//        tableCell_Name.setHorizontalAlignment(Element.ALIGN_CENTER);
//        tableCell_Name.setFixedHeight(20);
//        tableCell_Name.setColspan(2);
//        table_header.addCell(tableCell_Name);
//
//        PdfPCell tableCell_Type = new PdfPCell();
//        tableCell_Type.setNoWrap(false);
//        tableCell_Type.setPhrase(new Phrase(modelLabour.getType(), fontNormal));
//        tableCell_Type.setFixedHeight(20);
//        tableCell_Type.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//        PdfPCell tableCell_Wages = new PdfPCell();
//        tableCell_Wages.setNoWrap(false);
//        tableCell_Wages.setPhrase(new Phrase(String.valueOf(modelLabour.getWages()), fontNormal));
//        tableCell_Wages.setHorizontalAlignment(Element.ALIGN_CENTER);
//        tableCell_Wages.setFixedHeight(20);
//        table_header.addCell(tableCell_Wages);

//        try {
//            iText_xls_2_pdf.add(table_header);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }


        PdfPTable table_data = new PdfPTable(15);

        for (int i = 0; i < modelDateArrayList.size(); i = i + 15) {

            Log.e("ValueofI", "I::" + i);

            if (i < modelDateArrayList.size()) {
                for (int m = 0; m < 3; m++) {
                    if (m == 0) {
                        for (int j = i; j < i + 15; j++) {
                            if (j < modelDateArrayList.size()) {
                                table_data.addCell(new PdfPCell(new Phrase(modelDateArrayList.get(j).getDate(), fontBold)));
                            } else {
                                table_data.addCell(new PdfPCell(new Phrase("", fontBold)));
                            }
                        }
                    } else if (m == 1) {
                        //Attendance of labour
                        for (int j = (value + (i * finalShowLabour.size())); j < ((i + 15) * finalShowLabour.size()); j = j + finalShowLabour.size()) {
                            if (j < ModelCompileStatusArrayList.size()) {
                                if (ModelCompileStatusArrayList.get(j).getStatus().equals("P")) {
                                    PdfPCell present = new PdfPCell();
                                    present.setNoWrap(false);
                                    present.setPhrase(new Phrase(("P"), fontBlue));
                                    table_data.addCell(present);
                                    present.setHorizontalAlignment(Element.ALIGN_CENTER);
                                } else if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
                                    PdfPCell p2 = new PdfPCell();
                                    p2.setNoWrap(false);
                                    p2.setPhrase(new Phrase(("P/2"), fontBlue));
                                    p2.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table_data.addCell(p2);
                                } else {
                                    PdfPCell absent = new PdfPCell();
                                    absent.setNoWrap(false);
                                    absent.setPhrase(new Phrase(("A"), fontRed));
                                    absent.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table_data.addCell(absent);
                                }
                            } else {

                                table_data.addCell(new PdfPCell(new Phrase("", fontBold)));

                            }

                        }
                    } else if (m == 2) {

                        for (int j = (value + (i * finalShowLabour.size())); j < ((i + 15) * finalShowLabour.size()); j = j + finalShowLabour.size()) {

                            if (j < ModelCompileStatusArrayList.size()) {
                                Log.e("ValueofJ", "I:::::" + i + "\tValue of J" + j + "Value::" + value + "\tStatus:" + ModelCompileStatusArrayList.get(j).getLabourId());
                                if (ModelCompileStatusArrayList.get(j).getAmount().equals("0")) {
                                    PdfPCell Amount_null = new PdfPCell();
                                    Amount_null.setNoWrap(false);
                                    Amount_null.setPhrase(new Phrase(("0"), fontNormal));
                                    Amount_null.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table_data.addCell(Amount_null);
                                } else {
                                    PdfPCell Amount = new PdfPCell();
                                    Amount.setNoWrap(false);
                                    Amount.setPhrase(new Phrase((ModelCompileStatusArrayList.get(j).getAmount()), fontNormal));
                                    Amount.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table_data.addCell(Amount);
                                }
                            }else {

                                table_data.addCell(new PdfPCell(new Phrase("", fontBold)));

                            }

                        }
                    }


                }
            }


        }

        PdfPTable table_summary = new PdfPTable(3);

        PdfPCell tot_att, tot_advances, payable;
        tot_att = (new PdfPCell(new Phrase("Total Presence", fontBold)));
        tot_advances = (new PdfPCell(new Phrase("Total Advances", fontBold)));
        payable = (new PdfPCell(new Phrase("Payable Amount", fontBold)));


        table_summary.addCell(tot_att);
        table_summary.addCell(tot_advances);
        table_summary.addCell(payable);

        int CountPresent = 0;
        int countHalf = -0;
        int AmtSum = 0;
        int countTotal = 0;

        for (int z = value; z < ModelCompileStatusArrayList.size(); z++) {
            if (ModelCompileStatusArrayList.get(z).getStatus().equals("P")) {
                CountPresent++;
            } else {
                if (ModelCompileStatusArrayList.get(z).getStatus().equals("P/2")) {
                    countHalf++;
                }
            }
            if (!ModelCompileStatusArrayList.get(z).getAmount().equals("0")) {
                AmtSum = AmtSum + Integer.parseInt(ModelCompileStatusArrayList.get(z).getAmount());
            }

        }

        int PayableAmt = (int) ((modelLabour.getWages() * CountPresent + ((modelLabour.getWages() / 2) * countHalf)) - AmtSum);
        String status = String.valueOf(AmtSum);
        String payableAmt = String.valueOf(PayableAmt);
        countTotal = CountPresent + (countHalf / 2);
        PdfPCell table_cell_pT = new PdfPCell();
        table_cell_pT.setNoWrap(false);
        table_cell_pT.setPhrase(new Phrase((String.valueOf(countTotal)), fontGreen));
        table_cell_pT.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_pT.setFixedHeight(20);
        ;
        table_summary.addCell(table_cell_pT);
        table_summary.addCell(status);
        table_summary.addCell(payableAmt);


        Paragraph p12 = new Paragraph("\n\n");
        p12.setAlignment(Paragraph.ALIGN_CENTER);


        try {
            iText_xls_2_pdf.add(table_header);
            iText_xls_2_pdf.add(table_data);
            iText_xls_2_pdf.add(p12);
            iText_xls_2_pdf.add(table_summary);
        } catch (DocumentException e) {
            e.printStackTrace();
        }


//            table_header.addCell(tableCell);


//                if(lsize==0){
//                    PdfPCell srNo,id,name,type,wages,total_att,total_adv,payable_amt;
//                    srNo=(new PdfPCell(new Phrase("Sr No",fontBold)));
//                    id=(new PdfPCell(new Phrase("Worker Id",fontBold)));
//                    name=(new PdfPCell(new Phrase("Worker Name",fontBold)));
//                    type=(new PdfPCell(new Phrase("Type",fontBold)));
//                    wages=(new PdfPCell(new Phrase("Wages",fontBold)));
//                    total_att=(new PdfPCell(new Phrase("Total Att.",fontBold)));
//                    total_adv=(new PdfPCell(new Phrase("Total Adv.",fontBold)));
//                    payable_amt=(new PdfPCell(new Phrase("Payable Amount",fontBold)));
//
//                    id.setColspan(2);
//                    name.setColspan(2);
//                    table_header.addCell(srNo);
//                    table_header.addCell(id);
//                    table_header.addCell(name);
//                    table_header.addCell(type);
//                    table_header.addCell(wages);
//                    table_header.addCell(total_att);
//                    table_header.addCell(total_adv);
//                    table_header.addCell(payable_amt);
//
//                    for (int i = size; i < size+15; i++) {
//                        if(i<modelDateArrayList.size()){
//                            table_header.addCell(new PdfPCell(new Phrase(shortDateList.get(i).getDate(),fontBold)));
//                        }
//
////            table_header.addCell(tableCell);
//                    }
//                }else

//        if (lsize >= 0) {
//            for (int size = 5; size < modelDateArrayList.size() + 5; size = size + 15) {
//                for (int i = size; i < size + 15; i++) {
//                    if ((lsize) % 2 == 0) {
//                        if (i - 5 == 0) {
//                            PdfPCell tableCell_SrNo = new PdfPCell();
//                            tableCell_SrNo.setNoWrap(false);
//                            tableCell_SrNo.setPhrase(new Phrase(String.valueOf(lsize / 2 + 1), fontNormal));
//                            tableCell_SrNo.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            tableCell_SrNo.setFixedHeight(20);
//                            table_header.addCell(tableCell_SrNo);
//                        } else if (i - 5 == 1) {
//                            PdfPCell tableCell_Id = new PdfPCell();
//                            tableCell_Id.setNoWrap(false);
//                            tableCell_Id.setPhrase(new Phrase(finalShowLabour.get((lsize) / 2).getLabourId(), fontNormal));
//                            tableCell_Id.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            tableCell_Id.setFixedHeight(20);
//                            tableCell_Id.setColspan(2);
//                            table_header.addCell(tableCell_Id);
//                        } else if (i - 5 == 2) {
//                            PdfPCell tableCell_Name = new PdfPCell();
//                            tableCell_Name.setNoWrap(false);
//                            tableCell_Name.setPhrase(new Phrase(finalShowLabour.get((lsize) / 2).getName(), fontNormal));
//                            tableCell_Name.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            tableCell_Name.setFixedHeight(20);
//                            tableCell_Name.setColspan(2);
//                            table_header.addCell(tableCell_Name);
//                        } else if (i - 5 == 3) {
//                            PdfPCell tableCell_Type = new PdfPCell();
//                            tableCell_Type.setNoWrap(false);
//                            tableCell_Type.setPhrase(new Phrase(finalShowLabour.get((lsize) / 2).getType(), fontNormal));
//                            tableCell_Type.setFixedHeight(20);
//                            tableCell_Type.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//                            table_header.addCell(tableCell_Type);
//                        } else if (i - 5 == 4) {
//                            PdfPCell tableCell_Wages = new PdfPCell();
//                            tableCell_Wages.setNoWrap(false);
//                            tableCell_Wages.setPhrase(new Phrase(String.valueOf(finalShowLabour.get((lsize) / 2).getWages()), fontNormal));
//                            tableCell_Wages.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            tableCell_Wages.setFixedHeight(20);
//                            table_header.addCell(tableCell_Wages);
//                        } else if (i - 5 > 4) {
//                            for (int j = (lsize) / 2; j < (size - 5 + 15) * finalShowLabour.size(); j = j + finalShowLabour.size()) {
//                                if (j < ModelCompileStatusArrayList.size()) {
//                                    if (ModelCompileStatusArrayList.get(j).getStatus().equals("P")) {
//                                        PdfPCell present = new PdfPCell();
//                                        present.setNoWrap(false);
//                                        present.setPhrase(new Phrase(("P"), fontBlue));
//                                        table_header.addCell(present);
//                                        present.setHorizontalAlignment(Element.ALIGN_CENTER);
//                                    } else if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
//                                        PdfPCell p2 = new PdfPCell();
//                                        p2.setNoWrap(false);
//                                        p2.setPhrase(new Phrase(("P/2"), fontBlue));
//                                        p2.setHorizontalAlignment(Element.ALIGN_CENTER);
//                                        table_header.addCell(p2);
//                                    } else {
//                                        PdfPCell absent = new PdfPCell();
//                                        absent.setNoWrap(false);
//                                        absent.setPhrase(new Phrase(("A"), fontRed));
//                                        absent.setHorizontalAlignment(Element.ALIGN_CENTER);
//                                        table_header.addCell(absent);
//                                    }
//                                }
//
//                            }
//                        }
//                    } else {
//                        PdfPCell column = new PdfPCell();
//                        column.setNoWrap(false);
//                        column.setPhrase(new Phrase(("")));
//                        column.setColspan(7);
//                        column.setHorizontalAlignment(Element.ALIGN_CENTER);
//                        table_header.addCell(column);
//
//                        for (int j = (lsize) / 2; j < (size - 5 + 15) * finalShowLabour.size(); j = j + finalShowLabour.size()) {
//                            if (ModelCompileStatusArrayList.get(j).getAmount().equals("0")) {
//                                PdfPCell Amount_null = new PdfPCell();
//                                Amount_null.setNoWrap(false);
//                                Amount_null.setPhrase(new Phrase(("0"), fontNormal));
//                                Amount_null.setHorizontalAlignment(Element.ALIGN_CENTER);
//                                table_header.addCell(Amount_null);
//                            } else {
//                                PdfPCell Amount = new PdfPCell();
//                                Amount.setNoWrap(false);
//                                Amount.setPhrase(new Phrase((ModelCompileStatusArrayList.get(j).getAmount()), fontNormal));
//                                Amount.setHorizontalAlignment(Element.ALIGN_CENTER);
//                                table_header.addCell(Amount);
//                            }
//                        }
//                    }
//
//
////
////                            com.itextpdf.text.Font fontHeading=new com.itextpdf.text.Font();
////                            fontHeading.setStyle(com.itextpdf.text.Font.BOLD);
////                            fontHeading.setSize(12);
////                            fontHeading.setColor(BaseColor.BLACK);
////                            fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));
////
////
////                            int  AmtSum=0;
////                            float CountPresent=0;
////                            float countHalf=0;
////                            float countTotal=0;
////                            for(int j=lsize;j<(size+15)*finalShowLabour.size();j=j+finalShowLabour.size()){
////                                if(j<ModelCompileStatusArrayList.size()){
////                                    if(ModelCompileStatusArrayList.get(j).getStatus().equals("P")){
////                                        CountPresent++;
////                                    }else {
////                                        if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
////                                            countHalf++;
////                                        }
////                                    }
////                                    if(!ModelCompileStatusArrayList.get(j).getAmount().equals("0")){
////                                        AmtSum=AmtSum+Integer.parseInt(ModelCompileStatusArrayList.get(j).getAmount());
////                                    }
////                                }
////
////
////                            }
////                            int PayableAmt= (int) ((finalShowLabour.get((lsize)/2).getWages()*CountPresent+ ((finalShowLabour.get((lsize)/2).getWages()/2)*countHalf))-AmtSum);
////
////                            String status=String.valueOf(AmtSum);
////                            String payableAmt=String.valueOf(PayableAmt);
////                            countTotal=CountPresent+(countHalf/2);
////                            PdfPCell table_cell_pT=new PdfPCell();
////                            table_cell_pT.setNoWrap(false);
////                            table_cell_pT.setPhrase(new Phrase((String.valueOf(countTotal)),fontGreen));
////                            table_cell_pT.setHorizontalAlignment(Element.ALIGN_CENTER);
////                            table_cell_pT.setFixedHeight(20);;
////                            table_header.addCell(table_cell_pT);
////
////                            PdfPCell table_cell_ts=new PdfPCell();
////                            table_cell_ts.setNoWrap(false);
////                            table_cell_ts.setPhrase(new Phrase((status),fontGreen));
////                            table_cell_ts.setHorizontalAlignment(Element.ALIGN_CENTER);
////                            table_cell_ts.setFixedHeight(20);
////                            table_header.addCell(table_cell_ts);
////
////                            PdfPCell table_cell_pa=new PdfPCell();
////                            table_cell_pa.setNoWrap(false);
////                            if(PayableAmt<0){
////                                table_cell_pa.setPhrase(new Phrase((payableAmt),fontRed));
////                                table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
////                                table_cell_pa.setFixedHeight(20);
////                            }else{
////                                table_cell_pa.setPhrase(new Phrase((payableAmt),fontBlue));
////                                table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
////                                table_cell_pa.setFixedHeight(20);
////                            }
////
////                            table_header.addCell(table_cell_pa);
//
//
//                }
//
//
//            }
//
//
//            Log.e("ModelDateArrayList", "" + modelDateArrayList.size());
//            int startPos = 0;
//            int endPos = 15;
//
//
////        table_header.addCell(new PdfPCell(new Phrase("Sr No",fontBold)));
////        table_header.addCell(new PdfPCell(new Phrase("Worker Id",fontBold))).setColspan(2);
////        table_header.addCell(new PdfPCell(new Phrase("Worker Name",fontBold))).setColspan(2);
////        table_header.addCell(new PdfPCell(new Phrase("Type",fontBold)));
////        table_header.addCell(new PdfPCell(new Phrase("Wages",fontBold)));
////        table_header.addCell(new PdfPCell(new Phrase("Total Att.",fontBold)));
////        table_header.addCell(new PdfPCell(new Phrase("Total Adv.",fontBold)));
////        table_header.addCell(new PdfPCell(new Phrase("Payable Amount",fontBold)));
//
//
////                for(int i=lsize;i<finalShowLabour.size()*2;i++){
////                    if(i%2==0){
////                        PdfPCell tableCell_SrNo=new PdfPCell();
////                        tableCell_SrNo.setNoWrap(false);
////                        tableCell_SrNo.setPhrase(new Phrase(String.valueOf(i/2+1),fontNormal));
////                        tableCell_SrNo.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        tableCell_SrNo.setFixedHeight(20);
////                        table_header.addCell(tableCell_SrNo);
////
////                        PdfPCell tableCell_Id=new PdfPCell();
////                        tableCell_Id.setNoWrap(false);
////                        tableCell_Id.setPhrase(new Phrase(finalShowLabour.get(i/2).getLabourId(),fontNormal));
////                        tableCell_Id.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        tableCell_Id.setFixedHeight(20);
////                        tableCell_Id.setColspan(2);
////                        table_header.addCell(tableCell_Id);
////
////
////                        PdfPCell tableCell_Name=new PdfPCell();
////                        tableCell_Name.setNoWrap(false);
////                        tableCell_Name.setPhrase(new Phrase(finalShowLabour.get(i/2).getName(),fontNormal));
////                        tableCell_Name.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        tableCell_Name.setFixedHeight(20);
////                        tableCell_Name.setColspan(2);
////                        table_header.addCell(tableCell_Name);
////
////
////                        PdfPCell tableCell_Type=new PdfPCell();
////                        tableCell_Type.setNoWrap(false);
////                        tableCell_Type.setPhrase(new Phrase(finalShowLabour.get(i/2).getType(),fontNormal));
////                        tableCell_Type.setFixedHeight(20);
////                        tableCell_Type.setHorizontalAlignment(Element.ALIGN_CENTER);
////
////                        table_header.addCell(tableCell_Type);
////
////                        PdfPCell tableCell_Wages=new PdfPCell();
////                        tableCell_Wages.setNoWrap(false);
////                        tableCell_Wages.setPhrase(new Phrase(String.valueOf(finalShowLabour.get(i/2).getWages()),fontNormal));
////                        tableCell_Wages.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        tableCell_Wages.setFixedHeight(20);
////                        table_header.addCell(tableCell_Wages);
////
////
////                        com.itextpdf.text.Font fontHeading=new com.itextpdf.text.Font();
////                        fontHeading.setStyle(com.itextpdf.text.Font.BOLD);
////                        fontHeading.setSize(12);
////                        fontHeading.setColor(BaseColor.BLACK);
////                        fontHeading.setFamily(String.valueOf(Paint.Align.CENTER));
////
////
////                        int  AmtSum=0;
////                        float CountPresent=0;
////                        float countHalf=0;
////                        float countTotal=0;
////                        for(int j=i/2;j<ModelCompileStatusArrayList.size();j=j+finalShowLabour.size()){
////                            if(ModelCompileStatusArrayList.get(j).getStatus().equals("P")){
////                                CountPresent++;
////                            }else {
////                                if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
////                                    countHalf++;
////                                }
////                            }
////                            if(!ModelCompileStatusArrayList.get(j).getAmount().equals("0")){
////                                AmtSum=AmtSum+Integer.parseInt(ModelCompileStatusArrayList.get(j).getAmount());
////                            }
////
////                        }
////                        int PayableAmt= (int) ((finalShowLabour.get(i/2).getWages()*CountPresent+ ((finalShowLabour.get(i/2).getWages()/2)*countHalf))-AmtSum);
////
////                        String status=String.valueOf(AmtSum);
////                        String payableAmt=String.valueOf(PayableAmt);
////                        countTotal=CountPresent+(countHalf/2);
////                        PdfPCell table_cell_pT=new PdfPCell();
////                        table_cell_pT.setNoWrap(false);
////                        table_cell_pT.setPhrase(new Phrase((String.valueOf(countTotal)),fontGreen));
////                        table_cell_pT.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        table_cell_pT.setFixedHeight(20);;
////                        table_header.addCell(table_cell_pT);
////
////                        PdfPCell table_cell_ts=new PdfPCell();
////                        table_cell_ts.setNoWrap(false);
////                        table_cell_ts.setPhrase(new Phrase((status),fontGreen));
////                        table_cell_ts.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        table_cell_ts.setFixedHeight(20);
////                        table_header.addCell(table_cell_ts);
////
////                        PdfPCell table_cell_pa=new PdfPCell();
////                        table_cell_pa.setNoWrap(false);
////                        if(PayableAmt<0){
////                            table_cell_pa.setPhrase(new Phrase((payableAmt),fontRed));
////                            table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
////                            table_cell_pa.setFixedHeight(20);
////                        }else{
////                            table_cell_pa.setPhrase(new Phrase((payableAmt),fontBlue));
////                            table_cell_pa.setHorizontalAlignment(Element.ALIGN_CENTER);
////                            table_cell_pa.setFixedHeight(20);
////                        }
////
////                        table_header.addCell(table_cell_pa);
////
////
////                        for(int j=i/2;j<ModelCompileStatusArrayList.size();j=j+finalShowLabour.size()){
////                            if(ModelCompileStatusArrayList.get(j).getStatus().equals("P")){
////                                PdfPCell present=new PdfPCell();
////                                present.setNoWrap(false);
////                                present.setPhrase(new Phrase(("P"),fontBlue));
////                                table_header.addCell(present);
////                                present.setHorizontalAlignment(Element.ALIGN_CENTER);
////                            }else if (ModelCompileStatusArrayList.get(j).getStatus().equals("P/2")) {
////                                PdfPCell p2=new PdfPCell();
////                                p2.setNoWrap(false);
////                                p2.setPhrase(new Phrase(("P/2"),fontBlue));
////                                p2.setHorizontalAlignment(Element.ALIGN_CENTER);
////                                table_header.addCell(p2);
////                            }else {
////                                PdfPCell absent=new PdfPCell();
////                                absent.setNoWrap(false);
////                                absent.setPhrase(new Phrase(("A"),fontRed));
////                                absent.setHorizontalAlignment(Element.ALIGN_CENTER);
////                                table_header.addCell(absent);
////                            }
////                        }
////
////
//////                PdfPCell table_cell_att=new PdfPCell();
//////                table_cell_att.setNoWrap(false);
//////                table_cell_att.setPhrase(new Phrase(("A"),fontRed));
////
////
////                    }
//////
////
////
////
////                }
////            PdfPCell blank=new PdfPCell();
////            blank.setNoWrap(false);
////            blank.setPhrase(new Phrase(("")));
////            blank.setColspan(10+modelDateArrayList.size());
////            table_header.addCell(blank);
////            PdfPCell blank1=new PdfPCell();
////            blank1.setNoWrap(false);
////            blank1.setPhrase(new Phrase(("Skilled/Unskilled"),fontBold));
////            blank1.setHorizontalAlignment(Element.ALIGN_CENTER);
////            blank1.setColspan(10);
////            table_header.addCell(blank1);
////            for (int i = size; i < ModelCompileStatusArrayList.size(); i = i + finalShowLabour.size()) {
////                int presentSkilled = 0;
////                int presentUnskilled = 0;
////                int sum=0;
////                for (int j = 0; j < finalShowLabour.size(); j++) {
////
////                    Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
////                    if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")) {
////                        if (ModelCompileStatusArrayList.get(i + j).getType().equals("Skilled")) {
////                            presentSkilled += 1;
////                        } else {
////                            presentUnskilled += 1;
////                        }
////                    }
////
////                    if (j == finalShowLabour.size() - 1) {
////
////                        PdfPCell att=new PdfPCell();
////                        att.setNoWrap(false);
////                        att.setPhrase(new Phrase(("" + presentSkilled + "/" + presentUnskilled),fontBold));
////                        att.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        table_header.addCell(att);
////
////                    }
////
////
////                }
////
////            }
////            blank1.setPhrase(new Phrase(("Total Payment"),fontBold));
////            blank1.setHorizontalAlignment(Element.ALIGN_CENTER);
////            table_header.addCell(blank1);
////            for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + finalShowLabour.size()) {
////                int presentSkilled = 0;
////                int presentUnskilled = 0;
////                int sum=0;
////                for (int j = 0; j < finalShowLabour.size(); j++) {
////
////                    Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
////                    if (!ModelCompileStatusArrayList.get(i + j).getAmount().equals("0")) {
////                        sum=sum+Integer.parseInt(ModelCompileStatusArrayList.get(i + j).getAmount());
////                    }
////
////                    if (j == finalShowLabour.size() - 1) {
////
////                        PdfPCell att=new PdfPCell();
////                        att.setNoWrap(false);
////                        att.setPhrase(new Phrase((String.valueOf(sum)),fontBold));
////                        att.setHorizontalAlignment(Element.ALIGN_CENTER);
////                        table_header.addCell(att);
////
////                    }
////
////
////                }
////
////            }
//
//
//            try {
//                iText_xls_2_pdf.add(table_header);
//            } catch (DocumentException e) {
//                e.printStackTrace();
//            }
//        }


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


    private void getDateRange(String fromDate, String toDate, ArrayList<ModelLabour> labourList) throws ParseException {
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

            Log.e("SizeOfDate", "" + modelDateArrayList.size());
            Log.e("SizeOfLabour", "" + labourList.size());

            countLoop = 0;


//            for (int k = 0; k < modelDateArrayList.size(); k = k + 15)


            for (int i = 0; i < modelDateArrayList.size(); i++) {
                if (i < modelDateArrayList.size()) {
                    Log.e("ModelCompileStatus", "" + labourList.size());
                    for (int j = 0; j < labourList.size(); j++) {
//                    Log.e("date", modelDateArrayList.get(i).getDate());
//                    Log.e("date", "" + siteId);
//                    Log.e("date", labourList.get(j).getLabourId());
                        Log.e("LabourList", "" + labourList.get(j).getPresent());

                        getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(), labourList.get(j).getSiteCode());

                    }
                }

            }
            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
            Log.e("ModelCompileStatus", "AfterLoop" + ModelCompileStatusArrayList.size());
        }


    }

    private void getAttendanceList(String date, String labourId, int siteCode) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry")
                .child("Construction").child("Site")
                .child(String.valueOf(siteCode)).child("Attendance")
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


                            getPaymentStatus(date, labourId, status1, siteCode);
//                            Log.e("model1234", "ID" + ModelCompileStatus.getLabourId() + status + ModelCompileStatus.getStatus());


//                            Log.e("Size11111",""+labourList.size());
//                            Log.e("Size22222",""+modelAttendances.size());
//                            Log.e("Size33333",""+modelDateArrayList.size());
//                            if(modelDateArrayList.size()>0) {
//                                AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowLabourData.this ,
//                                        labourList,
//                                        modelAttendances ,
//                                        modelDateArrayList);
//                                binding.rvShowAttendance.setAdapter(adapterShowAttendance);
//                            }

                        } else {
                            getPaymentStatus(date, labourId, "A", siteCode);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPaymentStatus(String date, String labourId, String status1, int siteCode) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction")
                .child("Site").child(String.valueOf(siteCode))
                .child("Payments")
                .child(date).orderByChild("labourId")
                .equalTo(labourId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountSum = 0;
                        if (snapshot.getChildrenCount() > 0) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String amt = ds.child("amount").getValue(String.class);
                                Log.e("Amt", amt);
                                amountSum = amountSum + Integer.parseInt(amt);
                            }
                            ModelCompileStatus modelCompileStatus = new ModelCompileStatus(date, labourId, status1, String.valueOf(amountSum), siteCode);
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
                            ModelCompileStatus modelCompileStatus = new ModelCompileStatus(date, labourId, status1, "0", siteCode);
                            addToarray(modelCompileStatus);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addToarray(ModelCompileStatus modelCompileStatus) {
        countLoop++;

//        Log.e("ModelCompileStatus", "Count:" + countLoop);
//        Log.e("ModelCompileStatus", "LabourSize:" + labourList.size());
//        Log.e("ModelCompileStatus", "DateSize:" + modelDateArrayList.size());
//
        ModelCompileStatusArrayList.add(modelCompileStatus);
//        Log.e("ModelCompileStatus","CountLoop:"+countLoop+"\t"+"Date:"+ModelCompileStatus.getDate()+"\t"+"LabourID"+ModelCompileStatus.getLabourId()
//        +"\t"+"Status:"+ModelCompileStatus.getStatus());
//        Log.e("ModelCompileStatus", ModelCompileStatus.getLabourId());
//        Log.e("ModelCompileStatus", "Status" + ModelCompileStatus.getStatus());
//        Log.e("ModelCompileStatus", "Size:" + ModelCompileStatusArrayList.size());
        if (countLoop == finalShowLabour.size() * modelDateArrayList.size()) {
            Log.e("CountLoop", "Here");
            progressDialog.dismiss();
            binding.tableview.setVisibility(View.VISIBLE);
            binding.txtMessage.setVisibility(View.GONE);
            if (status.equals("ShowAttendance")) {
                initialiseTableView(finalShowLabour, modelDateArrayList, ModelCompileStatusArrayList);
                binding.llList.setVisibility(View.VISIBLE);
                binding.btnDownloadReport.setVisibility(View.GONE);
            } else {
                binding.btnDownloadReport.setVisibility(View.VISIBLE);
//                DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList, shortDateList,siteId,siteName);
            }


//            AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowLabourData.this,
//                    labourList,
//                    ModelCompileStatusArrayList,
//                    modelDateArrayList);
//            binding.rvShowAttendance.setAdapter(adapterShowAttendance);
//
//            AdapterNestedChild1 adapterNestedChild1 = new AdapterNestedChild1(ShowLabourData.this, ModelCompileStatusArrayList, modelDateArrayList, labourList);
//            binding.rvStatus.setAdapter(adapterNestedChild1);
        } else {
            if (labourListFinal.size() < 1) {
                progressDialog.dismiss();
                binding.tableview.setVisibility(View.GONE);
//                binding.txtMessage.setText("No Labours Added");
//                binding.txtMessage.setVisibility(View.VISIBLE);

            }
        }
    }

    private void initialiseTableView(ArrayList<ModelLabour> labourListFinal, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
        TableViewModel tableViewModel = new TableViewModel(modelDateArrayList.size() + 5,
                labourListFinal.size() * 2, labourListFinal, modelDateArrayList, null, ModelCompileStatusArrayList, "Compile");

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

            if (binding.btnByLabourDetails.isChecked()) {
                labourArrayList.get(position).setPresent(value);
                AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, labourArrayList);
                binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                binding.rvLabourSelect.scrollToPosition(position);
                binding.etSearchLabour.getText().clear();
            } else {
                labourSiteArrayList.get(position).setPresent(value);
                AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, labourSiteArrayList);
                binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                binding.rvLabourSelect.scrollToPosition(position);
                binding.etSearchLabour.getText().clear();
            }


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

    private void getLabourList(long siteId, String siteName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labourSiteArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelLabour modelLabour = ds.getValue(ModelLabour.class);
                    if (modelLabour.getProfile() != null && !modelLabour.getProfile().equals("")) {
                        modelLabour.setPresent(false);
                        labourSiteArrayList.add(modelLabour);
                    }


                }
                if (labourArrayList.size() > 0) {
                    binding.llLabour.setVisibility(View.VISIBLE);
                    binding.llAfterLabourSelect.setVisibility(View.GONE);
                    AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, labourSiteArrayList);
                    binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                } else {
                    binding.llLabour.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteListAdministrator() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        siteAdminList.clear();
                        labourArrayList.clear();
                        Log.e("Snap", snapshot.getKey());

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelSite modelSite = ds.getValue(ModelSite.class);
                            Log.e("Snap", "" + snapshot.child(String.valueOf(modelSite.getSiteId())).hasChild("Labours"));
                            modelSite.setSelected(false);
                            if (snapshot.child(String.valueOf(modelSite.getSiteId())).hasChild("Labours")) {
                                siteAdminList.add(modelSite);
                                for (DataSnapshot ds1 : ds.child("Labours").getChildren()) {
                                    ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
                                    modelLabour.setPresent(false);
                                    if (modelLabour.getStatus() != null && !modelLabour.getStatus().equals("Pending")) {
                                        labourArrayList.add(modelLabour);
                                    }


                                }

                            }


                        }


                        Log.e("siteAdminList123", "" + siteAdminList.size());
                        Log.e("siteAdminList123", "" + labourArrayList.size());
                        if (siteAdminList.size() == 0) {
                            binding.spinnerSelectSite.setVisibility(View.GONE);

                            binding.llSelectSite.setVisibility(View.VISIBLE);


                            binding.noDataToShow.setVisibility(View.VISIBLE);
                        } else {
//                    siteAdminList.add(0,new ModelSite(getString(R.string.select_site),0,false));

                            siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0, false));


                            AdapterLabourSelect adapterTransferLabour = new AdapterLabourSelect(ShowLabourData.this, labourArrayList);
                            binding.rvLabourSelect.setAdapter(adapterTransferLabour);
                            binding.llLabour.setVisibility(View.VISIBLE);
                            Log.e("SiteAdminList1234", status);


                            SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
                            binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                            siteId = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId();
                            siteName = "";

                            if (binding.btnBySite.isChecked()) {
                                binding.spinnerSelectSite.setVisibility(View.VISIBLE);
                                binding.llSelectSite.setVisibility(View.VISIBLE);


                                binding.noDataToShow.setVisibility(View.GONE);
                                adapterSiteSelect = new AdapterSiteSelect(ShowLabourData.this, siteAdminList);
                                progressDialog.dismiss();
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            for (int i = 0; i < finalShowLabour.size(); i++) {
                DownloadPdf(modelDateArrayList, ModelCompileStatusArrayList, shortDateList, finalShowLabour.get(i), i);
            }

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowLabourData.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_sucess, null);
        TextView txt_payment_id, txt_payment_amt, txt_message;
        Button btn_ok;
        txt_payment_id = mView.findViewById(R.id.txt_payment_id);
        txt_payment_amt = mView.findViewById(R.id.txt_payment_amt);
        txt_message = mView.findViewById(R.id.txt_message);
        btn_ok = mView.findViewById(R.id.btn_ok);
        txt_payment_id.setText(paymentData.getPaymentId());
        txt_payment_amt.setText(String.valueOf(amount));
      
       
            txt_message.setText("Your WorkerReport Report PDF file is generated and stored in DOWNLOADS folder. ");
        
        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogPaymentConfirm.dismiss();
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
                hashMap.put("downloadFile", "Worker Report");
                hashMap.put("siteId", "NA");
                hashMap.put("siteName", "NA");
                hashMap.put("fileType", dnl_file_type);
                hashMap.put("totalAmount", String.valueOf(amount));


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData", paymentData.getData().toString());
                                Log.e("paymentData", paymentData.getPaymentId().toString());
//

                                Log.e("fos", "flush");
                                Toast.makeText(ShowLabourData.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowLabourData.this);
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
                                Toast.makeText(ShowLabourData.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();

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
            Log.e("TextView", textView.getText().toString());
            return view2;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);

                binding.etSearchLabour.setText(Objects.requireNonNull(result).get(0));

            }
        }
    }
}