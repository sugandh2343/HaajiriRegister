//package com.skillzoomer_Attendance.com.Activity;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.ContextWrapper;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPRow;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.razorpay.Checkout;
//import com.razorpay.Order;
//import com.razorpay.PaymentData;
//import com.razorpay.PaymentResultWithDataListener;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.skillzoomer_Attendance.com.Model.DateModel;
//import com.skillzoomer_Attendance.com.Model.ModelAttendance;
//import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
//import com.skillzoomer_Attendance.com.Model.ModelDate;
//import com.skillzoomer_Attendance.com.Model.ModelLabour;
//import com.skillzoomer_Attendance.com.Model.ModelPromo;
//import com.skillzoomer_Attendance.com.Model.ModelShowAttendance;
//import com.skillzoomer_Attendance.com.Model.ModelSite;
//import com.skillzoomer_Attendance.com.Utilities.MyApplication;
//import com.skillzoomer_Attendance.com.R;
//import com.skillzoomer_Attendance.com.Adapter.TableViewAdapter;
//import com.skillzoomer_Attendance.com.Adapter.TableViewListener;
//import com.skillzoomer_Attendance.com.Adapter.TableViewModel;
//import com.skillzoomer_Attendance.com.databinding.ActivityShowCompileListBinding;
//import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;
//import com.squareup.picasso.Picasso;
//
//import org.apache.poi.hssf.usermodel.HSSFCellStyle;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.hssf.util.HSSFColor;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.Font;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Locale;
//
//public class ShowCompileListActivity1 extends AppCompatActivity implements PaymentResultWithDataListener {
//    ActivityShowCompileListBinding binding;
//    LayoutToolbarBinding toolbarBinding;
//    private final Calendar myCalendar = Calendar.getInstance();
//    private String[] search = {"Select Search Criteria", "Search by Labour", "Search by Date"};
//    private String[] workerType = {"Select Type", "Skilled", "Unskilled"};
//    private String toDate = "", fromDate = "";
//    String userType, siteName, userName;
//    long siteId;
//    private Sheet sheet;
//    private ArrayList<ModelShowAttendance> showAttendanceArrayList;
//    private ArrayList<ModelDate> modelDateArrayList;
//    private ArrayList<ModelSite> siteArrayList;
//    private ArrayList<DateModel> dateModelArrayList;
//    private String siteCreatedDate;
//    FirebaseAuth firebaseAuth;
//    String searchSelected, workerTypeSelected;
//    private ArrayList<ModelLabour> labourList;
//    private ArrayList<ModelAttendance> modelAttendances;
//    private ArrayList<ModelCompileStatus> ModelCompileStatusArrayList;
//    int countLoop = 0;
//    private ProgressDialog progressDialog;
//    private String status = "";
//    String currentDate;
//    private Cell cell = null;
//    ArrayList<ModelLabour> labourByType ;
//    ArrayList<ModelDate> shortDateList;
//    private ArrayList<ModelSite> siteAdminList;
//    private ArrayList<ModelPromo> promoArrayList;
//    int amount=0;
//    int AmountTemp=0;
//    androidx.appcompat.app.AlertDialog alertDialogPaymentConfirm=null;
//    FileOutputStream fos = null;
//    File file = null;
//    Checkout checkout=null;
//    int promo_spinner_position=0;
//    String promo_title;
//    private String dnl_file_type="";
//    HSSFWorkbook workbook = null;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding=ActivityShowCompileListBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        setContentView(binding.getRoot());
//        toolbarBinding = binding.toolbarLayout;
//        toolbarBinding.heading.setText("Show Attendance");
//        toolbarBinding.back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//        firebaseAuth = FirebaseAuth.getInstance();
//        siteArrayList = new ArrayList<>();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle(getResources().getString(R.string.please_wait));
//        progressDialog.setCanceledOnTouchOutside(false);
//        siteAdminList=new ArrayList<>();
//        promoArrayList=new ArrayList<>();
//
//        Workbook workbook = new HSSFWorkbook();
//        sheet = workbook.createSheet("Attendance");
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
//        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        ModelCompileStatusArrayList = new ArrayList<>();
//        labourByType = new ArrayList<>();
//
//
//        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
//        userType = sharedpreferences.getString("userDesignation", "");
//        userName = sharedpreferences.getString("userName", "");
//        if(userType.equals("Supervisor")) {
//            siteName = sharedpreferences.getString("siteName", "");
//            siteId = sharedpreferences.getLong("siteId", 0);
//            binding.llSelectSite.setVisibility(View.GONE);
//            binding.llSelectPeriod.setVisibility(View.VISIBLE);
//        }else{
//            siteId=0;
//            siteName="";
//            binding.llSelectSite.setVisibility(View.VISIBLE);
//            binding.llSelectPeriod.setVisibility(View.GONE);
//            getSiteListAdministrator();
//        }
//        MyApplication my = new MyApplication( );
//        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));
//        Checkout.preload(this);
//
//        checkout=new Checkout();
//
//        checkout.setKeyID(getString(R.string.razorpay_key_id));
//
//        Intent intent = getIntent();
//        status = intent.getStringExtra("Activity");
//
////        progressDialog.setMessage("Loading...");
////        progressDialog.show();
//
//        getSiteList();
//
//
//        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
//            // TODO Auto-generated method stub
//            myCalendar.set(Calendar.YEAR, year);
//            myCalendar.set(Calendar.MONTH, monthOfYear);
//            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            String myFormat = "dd/MM/yyyy"; //In which you need put here
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//            fromDate = sdf.format(myCalendar.getTime());
//            Log.e("Date", "From:" + fromDate);
//            updateLabel(binding.FromdateEt);
//
//
//        };
//        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
//            // TODO Auto-generated method stub
//            myCalendar.set(Calendar.YEAR, year);
//            myCalendar.set(Calendar.MONTH, monthOfYear);
//            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            String myFormat = "dd/MM/yyyy"; //In which you need put here
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//            toDate = sdf.format(myCalendar.getTime());
//            Log.e("Date", "From:" + toDate);
//            updateLabel(binding.TodateEt);
//
//        };
//        modelAttendances = new ArrayList<>();
//
//
//        binding.FromdateEt.setOnClickListener((View v) -> {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH));
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
////            Log.e("siteCreatedDate", siteCreatedDate);
////            try {
////                Date fDate = dateFormat.parse(siteCreatedDate);
////                Log.e("Parse Success","Success");
////                datePickerDialog.getDatePicker().setMinDate(fDate.getTime());
////            } catch (ParseException e) {
////                Log.e("ParseException",e.getMessage());
////                e.printStackTrace();
////            }
//
//            datePickerDialog.show();
//            String myFormat = "dd/MM/yyyy"; //In which you need put here
//        });
//        binding.TodateEt.setOnClickListener((View v) -> {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this, date1, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH));
//
//            datePickerDialog.show();
//            String myFormat = "dd/MM/yyyy"; //In which you need put here
//
//        });
//        ShowCompileListActivity.SpinnerAdapter spinnerAdapter = new ShowCompileListActivity.SpinnerAdapter();
//        binding.spinnerSearchType.setAdapter(spinnerAdapter);
//        ShowCompileListActivity.SpinnerAdapter1 spinnerAdapter1 = new ShowCompileListActivity.SpinnerAdapter1();
//
//        binding.spinnerSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                if (position == 1) {
//                    binding.llDate.setVisibility(View.VISIBLE);
//
//                } else if (position == 2) {
//                    binding.llDate.setVisibility(View.VISIBLE);
//
//                }
//                searchSelected = search[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        binding.btnCustom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.btnShow.setVisibility(View.VISIBLE);
//                binding.llDate.setVisibility(View.VISIBLE);
//            }
//        });
//        binding.btnToday.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!status.equals("")) {
//                    Log.e("Status1122",status);
//                    if (status.equals("ShowAttendance")) {
//
//                        binding.btnDownloadReport.setVisibility(View.GONE);
//                    } else {
//                        binding.btnDownloadReport.setVisibility(View.VISIBLE);
//                    }
//                }
//                progressDialog.setMessage("Data Loading");
//                progressDialog.show();
//
//                ModelCompileStatusArrayList.clear();
//                checkForAttendance();
//
//            }
//        });
//        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i>0) {
//                    siteId = siteAdminList.get(i).getSiteId();
//                    siteName = siteAdminList.get(i).getSiteName();
//                    Log.e("SiteId", "Spinner" + siteId);
//                    binding.llSelectPeriod.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
//                View mView = getLayoutInflater().inflate(R.layout.show_file_type, null);
//
//                RadioButton rb_pdf,rb_xls;
//                Button btn_pay;
//                TextView txt_amount;
//                LinearLayout ll_amount;
//                ImageView iv_close;
//                rb_pdf=mView.findViewById(R.id.rb_pdf);
//                rb_xls=mView.findViewById(R.id.rb_xls);
//                btn_pay=mView.findViewById(R.id.btn_pay);
//                txt_amount=mView.findViewById(R.id.txt_amount);
//                ll_amount=mView.findViewById(R.id.ll_amount);
//                iv_close=mView.findViewById(R.id.iv_close);
//                ll_amount.setVisibility(View.GONE);
//                alert.setView(mView);
//                AlertDialog alertDialog=alert.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                rb_pdf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        if(b){
//                            rb_xls.setChecked(false);
//                            dnl_file_type="pdf";
//                            amount = Math.round((modelDateArrayList.size()*7));
//                            ll_amount.setVisibility(View.VISIBLE);
//                            txt_amount.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(amount));
//                        }
//                    }
//                });
//                rb_xls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        if(b){
//                            rb_pdf.setChecked(false);
//                            dnl_file_type="xls";
//                            amount = Math.round((modelDateArrayList.size()*14));
//                            ll_amount.setVisibility(View.VISIBLE);
//                            txt_amount.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(amount));
//                        }
//                    }
//                });
//
//                btn_pay.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(dnl_file_type.equals("")){
//                            Toast.makeText(ShowCompileListActivity.this, "Select file type", Toast.LENGTH_SHORT).show();
//                        }else{
//
//                            showPaymentDialog();
//                            alertDialog.dismiss();
//                        }
//                    }
//                });
//                iv_close.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        alertDialog.dismiss();
//                    }
//                });
//
//                alertDialog.show();
//
//
//
//
//
////
//            }
//
//
//        });
//
//
//        showAttendanceArrayList = new ArrayList<>();
//        labourList = new ArrayList<>();
////        getAttendanceMaster();
//
//        binding.btnShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("Status1122",""+status.equals(""));
//                if (!status.equals("")) {
//                    Log.e("Status1122",status);
//
//                    if (status.equals("ShowAttendance")) {
//                        binding.btnDownloadReport.setVisibility(View.GONE);
//                    } else {
//                        binding.btnDownloadReport.setVisibility(View.VISIBLE);
//                    }
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
//                    ModelCompileStatusArrayList.clear();
//                    getLabourList(fromDate, toDate,"Custom");
//                }
//            }
//        });
//
//
//    }
//
//    private void showPaymentDialog() {
//
//        file=null;
//        fos=null;
//        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
//        View mView = getLayoutInflater().inflate(R.layout.payment_dialog, null);
//
//        TextView txt_file_type,txt_from,txt_to,txt_amount,txt_mode_of_payment,txt_heading;
//        Spinner spinner_promo;
//        Button btn_cancel,btn_pay;
//        txt_file_type=mView.findViewById(R.id.txt_file_type);
//        txt_from=mView.findViewById(R.id.txt_from);
//        txt_to=mView.findViewById(R.id.txt_to);
//        txt_amount=mView.findViewById(R.id.txt_amount);
//        txt_mode_of_payment=mView.findViewById(R.id.txt_mode_of_payment);
//        txt_heading=mView.findViewById(R.id.txt_heading);
//        spinner_promo=mView.findViewById(R.id.spinner_promo);
//        getPromoCode(spinner_promo);
//        Log.e("Dnl_file",dnl_file_type);
////        if(dnl_file_type.equals("Pdf")){
////            amount = Math.round((modelDateArrayList.size()*5));
////        }else{
////            amount = Math.round((modelDateArrayList.size()*10));
////        }
//
//        AmountTemp=amount;
//        spinner_promo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i>0){
//                    promo_spinner_position=i;
//                    promo_title=promoArrayList.get(i).getTitle();
//
//                    Float dis= Float.valueOf(promoArrayList.get(i).getDiscount());
//
//
//                    Float d_amount= Float.valueOf(Math.round(modelDateArrayList.size()*4.5));
//                    dis=dis.floatValue()/100*amount;
//                    AmountTemp =Math.round(amount-dis);
//
////                           amount= (int) (amount-((promoArrayList.get(i).getDiscount()/100)*amount));
//
//                    Log.e("Amt","AD"+amount);
//                    Log.e("Amt","D"+(promoArrayList.get(i).getDiscount()/100));
//                    Log.e("Amt","DF"+dis.floatValue()/100*amount);
//                    Log.e("Amt","Af"+d_amount);
//
//                    txt_amount.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(AmountTemp));
//                    txt_heading.setText(getString(R.string.your_file_one_click_away)+" "+String.valueOf(AmountTemp)+" "+getString(R.string.your_have_tom_make_payment));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        txt_amount.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(amount));
//        txt_heading.setText(getString(R.string.your_file_one_click_away)+" "+String.valueOf(amount)+" "+getString(R.string.your_have_tom_make_payment));
//        btn_cancel=mView.findViewById(R.id.btn_cancel);
//        btn_pay=mView.findViewById(R.id.btn_pay);
//        txt_file_type.setText(getString(R.string.attendance_report));
//        txt_from.setText(modelDateArrayList.get(0).getDate());
//        txt_to.setText(modelDateArrayList.get(modelDateArrayList.size()-1).getDate());
//
//        txt_mode_of_payment.setText("Razorpay");
//        alert.setView(mView);
//        alertDialogPaymentConfirm = alert.create();
//        alertDialogPaymentConfirm.setCanceledOnTouchOutside(false);
//        btn_pay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                amount=AmountTemp;
//                if(dnl_file_type.equals("xls")){
//                    DownloadExcel(labourList, modelDateArrayList, ModelCompileStatusArrayList, labourList,shortDateList);
//                }else{
//                    DownloadExcel(labourList,modelDateArrayList,ModelCompileStatusArrayList,labourList,shortDateList);
//                }
//
//            }
//        });
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialogPaymentConfirm.dismiss();
//            }
//        });
//
//
//
////
//        alertDialogPaymentConfirm.show();
//    }
//
//    private void getPromoCode(Spinner spinner_promo) {
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Promo");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                promoArrayList.clear();
//                for(DataSnapshot ds:snapshot.getChildren()){
//                    ModelPromo modelPromo=ds.getValue(ModelPromo.class);
//                    promoArrayList.add(modelPromo);
//                }
//                Log.e("Promo",""+promoArrayList.size());
//                promoArrayList.add(0,new ModelPromo(null,null,null,"Select Promo",null,0,0));
//                PromoSpinnerAdapter promoSpinnerAdapter=new PromoSpinnerAdapter();
//                spinner_promo.setAdapter(promoSpinnerAdapter);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    class PromoSpinnerAdapter
//            extends BaseAdapter {
//        PromoSpinnerAdapter() {
//        }
//
//        public int getCount() {
//            return promoArrayList.size();
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
//            View view2 = getLayoutInflater().inflate(R.layout.promo_layout_single_row, null);
//            LinearLayout ll_main;
//            ImageView img_promo;
//            TextView txt_promo_title,txt_promo_details;
//            ll_main=view2.findViewById(R.id.ll_main);
//            img_promo=view2.findViewById(R.id.img_promo);
//            txt_promo_title=view2.findViewById(R.id.txt_promo_title);
//            txt_promo_details=view2.findViewById(R.id.txt_promo_details);
//            if(n==0){
//                ll_main.setWeightSum(1);
//                img_promo.setVisibility(View.GONE);
//                txt_promo_details.setVisibility(View.GONE);
//                txt_promo_title.setText(promoArrayList.get(n).getTitle());
//            }else{
//                ll_main.setWeightSum(2);
//                Picasso.get().load(promoArrayList.get(n).getUrl())
//
//                        .placeholder(R.drawable.logo_razor).into(img_promo);
//                txt_promo_title.setText(promoArrayList.get(n).getTitle());
//                txt_promo_details.setText(promoArrayList.get(n).getDetails());
//            }
//
//
//            return view2;
//        }
//    }
//    private void getSiteListAdministrator() {
//
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Site");
//        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                siteAdminList.clear();
//                for(DataSnapshot ds: snapshot.getChildren()){
//                    ModelSite modelSite=ds.getValue(ModelSite.class);
//                    modelSite.setSelected(false);
//                    siteAdminList.add(modelSite);
//                }
//                Log.e("siteAdminList",""+siteAdminList.size());
//                siteAdminList.add(0,new ModelSite(getString(R.string.select_site),0,false));
//                SiteSpinnerAdapter siteSpinnerAdapter=new SiteSpinnerAdapter();
//                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
//                binding.llSelectSite.setVisibility(View.VISIBLE);
//                binding.llSelectPeriod.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @Override
//    public void onPaymentSuccess(String s, PaymentData paymentData) {
//        Toast.makeText(this,"Payment Success and Payment Id is "+s,Toast.LENGTH_LONG);
//        if(dnl_file_type.equals("pdf")){
//            ExcelToPdf(file,fos,workbook);
//        }else{
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            Log.e("StrPath", str_path);
//            file = new File(str_path, "CompileReport_" + siteName + "_" + currentDate + ".xls");
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
//            ExcelToPdf(file,fos,workbook);
//        }
//
//        Log.e("Success",s);
//        alertDialogPaymentConfirm.dismiss();
//        Log.e("from",""+fromDate);
//        Log.e("from","to::::"+toDate);
//        Log.e("from","SpinnerPos:::"+promo_spinner_position);
//        Log.e("from","PromoTitle:::"+promo_title);
//        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(ShowCompileListActivity.this);
//        View mView = getLayoutInflater().inflate(R.layout.payment_sucess, null);
//        TextView txt_payment_id,txt_payment_amt,txt_message;
//        Button btn_ok;
//        txt_payment_id=mView.findViewById(R.id.txt_payment_id);
//        txt_payment_amt=mView.findViewById(R.id.txt_payment_amt);
//        txt_message=mView.findViewById(R.id.txt_message);
//        btn_ok=mView.findViewById(R.id.btn_ok);
//        txt_payment_id.setText(paymentData.getPaymentId());
//        txt_payment_amt.setText(String.valueOf(amount));
//        if(dnl_file_type.equals("pdf")){
//            txt_message.setText("Your Compile Report PDF file is generated and stored in DOWNLOADS folder. ");
//        }else{
//            txt_message.setText("Your Compile Report EXCEL+PDF file is generated and stored in DOWNLOADS folder. ");
//        }
//        alert.setView(mView);
//        AlertDialog alertDialog= alert.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Date c = Calendar.getInstance().getTime();
//
//                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//                currentDate = df.format(c);
//                String currentTime="";
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
//                }
//                String timestamp = "" + System.currentTimeMillis();
//
//                HashMap<String,Object> hashMap=new HashMap<>();
//                hashMap.put("dateOfPayment",currentDate);
//                hashMap.put("timeOfPayment",currentTime);
//                hashMap.put("timestamp",timestamp);
//                hashMap.put("paymentId",paymentData.getPaymentId());
//                hashMap.put("orderId",paymentData.getOrderId());
//                hashMap.put("signature",paymentData.getSignature());
//                hashMap.put("paidAmount",String.valueOf(amount));
//                hashMap.put("status","Success");
//                hashMap.put("downloadFile","Compile Report");
//
//                hashMap.put("fromDate",fromDate);
//                hashMap.put("toDate",toDate);
//                hashMap.put("totalAmount",String.valueOf(amount));
//                if(promo_spinner_position>0){
//                    hashMap.put("promoApplied",true);
//                    hashMap.put("promoTitle",promo_title);
//                }else{
//                    hashMap.put("promoApplied",false);
//                    hashMap.put("promoTitle","");
//                }
//                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
//                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Log.e("paymentData",paymentData.getData().toString());
//                                Log.e("paymentData",paymentData.getPaymentId().toString());
//                                if (fos != null) {
//                                    try {
//                                        Log.e("fos", "flush");
//                                        fos.flush();
//                                        fos.close();
//                                        Toast.makeText(ShowCompileListActivity.this, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
//                                        alertDialog.dismiss();
//                                    } catch (IOException e) {
//                                        Log.e("IOEXCEPTION1", e.getMessage());
//                                        e.printStackTrace();
//                                        alertDialog.dismiss();
//                                    }
//                                }else{
//                                    alertDialog.dismiss();
//                                }
//
//
////                        Intent intent = new Intent(Intent.ACTION_VIEW);
////                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
////                        startActivity(intent);
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//            }
//        });
//        alertDialog.show();
//
//
//    }
//
//    @Override
//    public void onPaymentError(int i, String s, PaymentData paymentData) {
//        Toast.makeText(this,"Payment Success and Payment Id is "+s,Toast.LENGTH_LONG);
//        Log.e("Success",s);
//        alertDialogPaymentConfirm.dismiss();
//        Log.e("from",""+fromDate);
//        Log.e("from","to::::"+toDate);
//        Log.e("from","SpinnerPos:::"+promo_spinner_position);
//        Log.e("from","PromoTitle:::"+promo_title);
//        final AlertDialog.Builder alert = new AlertDialog.Builder(ShowCompileListActivity.this);
//        View mView = getLayoutInflater().inflate(R.layout.payment_failure, null);
//        TextView txt_failed_reason;
//        Button btn_ok;
//        txt_failed_reason=mView.findViewById(R.id.txt_failed_reason);
//        btn_ok=mView.findViewById(R.id.btn_ok);
//        txt_failed_reason.setText(s);
//        alert.setView(mView);
//        AlertDialog alertDialog= alert.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Date c = Calendar.getInstance().getTime();
//
//                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//                currentDate = df.format(c);
//                String currentTime="";
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
//                }
//                String timestamp = "" + System.currentTimeMillis();
//
//                HashMap<String,Object> hashMap=new HashMap<>();
//                hashMap.put("dateOfPayment",currentDate);
//                hashMap.put("timeOfPayment",currentTime);
//                hashMap.put("timestamp",timestamp);
//                hashMap.put("paymentId","");
//                hashMap.put("orderId",paymentData.getOrderId());
//                hashMap.put("signature","");
//                hashMap.put("paidAmount",String.valueOf(amount));
//                hashMap.put("status","Failed");
//                hashMap.put("downloadFile","Compile Report");
//                hashMap.put("fromDate",fromDate);
//                hashMap.put("toDate",toDate);
//                hashMap.put("siteId",siteId);
//                hashMap.put("siteName",siteName);
//                hashMap.put("fileType",dnl_file_type);
//                if(promo_spinner_position>0){
//                    hashMap.put("promoApplied",true);
//                    hashMap.put("promoTitle",promo_title);
//                }else{
//                    hashMap.put("promoApplied",false);
//                    hashMap.put("promoTitle","");
//                }
//                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
//                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
////                                Log.e("paymentData",paymentData.getData().toString());
////                                Log.e("paymentData",paymentData.getPaymentId().toString());
//                                Toast.makeText(ShowCompileListActivity.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();
//                                alertDialog.dismiss();
//
//
//
////                        Intent intent = new Intent(Intent.ACTION_VIEW);
////                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
////                        startActivity(intent);
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//            }
//        });
//        alertDialog.show();
//
//    }
//
//    class SiteSpinnerAdapter
//            extends BaseAdapter {
//        SiteSpinnerAdapter() {
//        }
//
//        public int getCount() {
//            return siteAdminList.size();
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
//            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
//            TextView textView = (TextView)view2.findViewById(R.id.spinner_text);
//            textView.setText(siteAdminList.get(n).getSiteCity());
//            return view2;
//        }
//    }
//
//    private void arrangeLabourByType(ArrayList<ModelLabour> labourList) {
//        ArrayList<ModelLabour> skilledLabour = new ArrayList<>();
//        ArrayList<ModelLabour> unSKilledLabour = new ArrayList<>();
//        labourByType.clear();
//        for(int i=0;i<labourList.size();i++){
//            if(labourList.get(i).getType().equals("Skilled")){
//                skilledLabour.add(labourList.get(i));
//            }else{
//                unSKilledLabour.add(labourList.get(i));
//            }
//        }
//        for(int i=0;i<skilledLabour.size();i++){
//            labourByType.add(skilledLabour.get(i));
//        }
//        for(int i=0;i<unSKilledLabour.size();i++){
//            labourByType.add(unSKilledLabour.get(i));
//        }
//        for(int i=0;i<labourByType.size();i++){
//            Log.e("LabourByType",""+labourByType.get(i).getLabourId());
//        }
//
//    }
//
//    private void DownloadExcel(ArrayList<ModelLabour> labourList, ArrayList<ModelDate> dateModelArrayList, ArrayList<ModelCompileStatus> ModelCompileStatusArrayList, ArrayList<ModelLabour> list, ArrayList<ModelDate> shortDateList) {
//        workbook = new HSSFWorkbook();
//        Sheet sheet = workbook.createSheet();
//        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
//        createHeaderRow(sheet, modelDateArrayList,shortDateList);
//        createLAbourData(sheet, labourList);
//        createAttendanceData(sheet, labourList, modelDateArrayList, ModelCompileStatusArrayList);
//        createFooter(sheet, labourList, modelDateArrayList, ModelCompileStatusArrayList);
//        createPayment(sheet, labourList, modelDateArrayList, ModelCompileStatusArrayList);
//        FileOutputStream fos = null;
//        File file = null;
//
//
//
//        try {
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
////            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
////            Log.e("Directory",directory.getAbsolutePath());
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            Log.e("StrPath", str_path);
//            file = new File(str_path, "PaymentReport" + siteName + "_" + currentDate + ".xls");
//
////            fos = new FileOutputStream(file);
//            Log.e("FilePath", file.getAbsolutePath().toString());
////            workbook.write(fos);
//        } finally {}
//        if(dnl_file_type.equals("pdf")){
////                workbook.write(fos);
//            Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//            createOrderId.start();
////            ExcelToPdf(file,fos,workbook);
//        }else{
//            Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//            createOrderId.start();
//        }
//
////        try {
////            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//////            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//////            Log.e("Directory",directory.getAbsolutePath());
////            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
////            Log.e("StrPath", str_path);
////            file = new File(str_path, "CompileReport"+siteName +"_"+currentDate+ ".xls");
////
////            fos = new FileOutputStream(file);
////            Log.e("FilePath", file.getAbsolutePath().toString());
////            workbook.write(fos);
////        } catch (IOException e) {
////            Log.e("IOEXCEPTION", e.getMessage());
////            e.printStackTrace();
////        } finally {
////            if (fos != null) {
////                try {
////                    Log.e("fos", "flush");
////                    fos.flush();
////                    fos.close();
////                } catch (IOException e) {
////                    Log.e("IOEXCEPTION1", e.getMessage());
////                    e.printStackTrace();
////                }
////            }
////            Toast.makeText(ShowCompileListActivity.this, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
////        }
////        Intent intent = new Intent(Intent.ACTION_VIEW);
////        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
////        startActivity(intent);
//
//    }
//    private void ExcelToPdf(File file, FileOutputStream fos, HSSFWorkbook workbook){
//        FileInputStream input_document = null;
//        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+"CompileReport_" + siteName + "_" + currentDate + ".pdf";
//        File pdfFile=new File(str_path);
////      try {
////          input_document = new FileInputStream(file);
////      } catch (FileNotFoundException e) {
////          e.printStackTrace();
////      }
//        // Read workbook into HSSFWorkbook
//        HSSFWorkbook my_xls_workbook = null;
//        my_xls_workbook =workbook;
//        // Read worksheet into HSSFSheet
//        HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
//        // To iterate over the rows
//        Iterator<Row> rowIterator = my_worksheet.iterator();
//        //We will create output PDF document objects at this point
//        com.itextpdf.text.Document iText_xls_2_pdf = new Document();
//
//        iText_xls_2_pdf.setPageSize(new Rectangle(1224,1584));
//        try {
//            PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
//        } catch (DocumentException e) {
//            Log.e("exception123",e.getMessage());
//            e.printStackTrace();
//
//        } catch (FileNotFoundException e) {
//            Log.e("exception123",e.getMessage());
//            e.printStackTrace();
//        }
//        iText_xls_2_pdf.open();
//        String date_val = "From: " + modelDateArrayList.get(0).getDate() + "\t To: " + modelDateArrayList.get(modelDateArrayList.size() - 1).getDate() + "\n Site Id: " + siteId +
//                "\t SiteName" +  " "+siteName;
//        Paragraph p1 = new Paragraph(date_val);
//        p1.setAlignment(Paragraph.ALIGN_CENTER);
//
//        Paragraph p2 = new Paragraph("Compile Report\n");
//        p2.setAlignment(Paragraph.ALIGN_CENTER);
//
//        Paragraph p3 = new Paragraph("\n");
//        p3.setAlignment(Paragraph.ALIGN_CENTER);
//
//        try {
//            iText_xls_2_pdf.add(p1);
//            iText_xls_2_pdf.add(p2);
//            iText_xls_2_pdf.add(p3);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        //we have two columns in the Excel sheet, so we create a PDF table with two columns
//        //Note: There are ways to make this dynamic in nature, if you want to.
//        PdfPTable my_table = new PdfPTable(modelDateArrayList.size()+6);
//        //We will use the object below to dynamically add new data to the table
//        PdfPCell table_cell;
//
//        PdfPRow table_row;
//        //Loop through rows.
//
//
//
//        Row row = rowIterator.next();
//        for (int i = 0; i < labourList.size()*2 + 1; i++) {
//            Row row1 = my_worksheet.getRow(i+5);
//            Cell cell1=row1.getCell(2);
//
//            Iterator<Cell> cellIterator = row1.cellIterator();
//
//            if(i%2==0 && i>1){
//                for (int j=0;j<modelDateArrayList.size()+6;j++){
////                        Cell cell = row1.getCell(5); //Fetch CELL
//                    if(j<=5){
//                        table_cell = new PdfPCell(new Phrase(""));
//                        //feel free to move the code below to suit to your needs
//                        my_table.addCell(table_cell);
//                    }else{
//                        if(row1.getCell(j).getStringCellValue()!=null){
//                            table_cell=new PdfPCell(new Phrase((row1.getCell(j).getStringCellValue())));
//                            my_table.addCell(table_cell);
//                        }else{
//                            table_cell = new PdfPCell(new Phrase(""));
//                            //feel free to move the code below to suit to your needs
//                            my_table.addCell(table_cell);
//                        }
//
//                    }
//
//                }
//            }else {
//                while (cellIterator.hasNext()) {
//
//
//                    Cell cell = cellIterator.next(); //Fetch CELL
//
//
//                    switch (cell.getCellType()) { //Identify CELL type
//                        //you need to add more code here based on
//                        //your requirement / transformations
//                        case Cell.CELL_TYPE_STRING:
//                            //Push the data from Excel to PDF Cell
//                            table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
//                            //feel free to move the code below to suit to your needs
//                            my_table.addCell(table_cell);
//                            break;
//                        case Cell.CELL_TYPE_NUMERIC:
//                            table_cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cell.getNumericCellValue()))));
//                            my_table.addCell(table_cell);
//                            break;
//                        case Cell.CELL_TYPE_BLANK:
//                            table_cell = new PdfPCell(new Phrase(" "));
//                            my_table.addCell(table_cell);
//                            break;
//                    }
//
//                    //next line
//                }
//            }
//
//        }
//        //Finally add the table to PDF document
//        try {
//            iText_xls_2_pdf.add(my_table);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//
//        String date_val3 = "To know us more visit https://skillzoomers.com/hajiri-register/ ";
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
//
//
//        Paragraph p5 = new Paragraph("\n");
//        p5.setAlignment(Paragraph.ALIGN_CENTER);
//
//
//
//        Paragraph p7 = new Paragraph("\n");
//        p7.setAlignment(Paragraph.ALIGN_CENTER);
//
//        Paragraph p8= new Paragraph(date_val3);
//        p8.setAlignment(Paragraph.ALIGN_CENTER);
//
//        try {
//            iText_xls_2_pdf.add(p7);
//            iText_xls_2_pdf.add(p8);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        iText_xls_2_pdf.close();
//        //we created our pdf file..
////          try {
////
////              input_document.close(); //close xls
////          } catch (IOException e) {
////              e.printStackTrace();
////          }
//
//    }
//
//    public class createOrderIdThread implements Runnable{
//        int amount;
//
//        public createOrderIdThread(int amount) {
//            this.amount = amount;
//        }
//
//        @Override
//        public void run() {
//            RazorpayClient razorpay = null;
//            try {
//                razorpay = new RazorpayClient(getString(R.string.razorpay_key_id), getString(R.string.razorpay_key_secret));
//            } catch (RazorpayException e) {
//                e.printStackTrace();
//            }
//            JSONObject orderRequest = new JSONObject();
//            try {
//                orderRequest.put("amount", amount*100); // amount in the smallest currency unit
//                orderRequest.put("currency", "INR");
//                orderRequest.put("receipt", "order_rcptid_11");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            Log.e("Exception","COID");
//            Order order = null;
//            try {
//                order = razorpay.orders.create(orderRequest);
//            } catch (RazorpayException e) {
//                progressDialog.dismiss();
//                Toast.makeText(ShowCompileListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(String.valueOf(order));
//                String id = jsonObject.getString("id");
//                Log.e("Response",""+id);
//                if(id!=null){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressDialog.dismiss();
//                            startPayment(id,amount);
//                        }
//                    });
//                }
//
//            } catch (JSONException e) {
//                progressDialog.dismiss();
//                Toast.makeText(ShowCompileListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//
//
//        }
//    }
//
//    private void startPayment(String id, int amount) {
//        Checkout.preload(this);
//        Checkout checkout=new Checkout();
//        checkout.setKeyID(getString(R.string.razorpay_key_id));
//
//
//
//
//        checkout.setImage(R.drawable.logo_razor);
//
//
//        final Activity activity = this;
//
//
//        try {
//            JSONObject options = new JSONObject();
//
//            options.put("name", getString(R.string.app_name1));
//            options.put("description", "Haj123456");
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
//            options.put("order_id", id);//from response of step 3.
//            options.put("theme.color", "#3399cc");
//            options.put("currency", "INR");
//            options.put("amount", String.valueOf(amount));//pass amount in currency subunits
//            options.put("prefill.email", getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userName","")+"@yopmail.com");
//            options.put("prefill.contact",getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userMobile",""));
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 4);
//            options.put("retry", retryObj);
//
//            checkout.open(this, options);
//            progressDialog.dismiss();
//
//        } catch(Exception e) {
//            Log.e("Sugandh", "Error in starting Razorpay Checkout", e);
//        }
//    }
//
//
//    private void createPayment(Sheet sheet, ArrayList<ModelLabour> labourList,
//                               ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        Font font = sheet.getWorkbook().createFont();
//        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStyle.setBorderBottom((short) 1);
//        cellStyle.setBorderTop((short) 1);
//        cellStyle.setBorderLeft((short) 1);
//        cellStyle.setBorderRight((short) 1);
//        cellStyle.setWrapText(true);
//
//        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
//        Font fontPresent = sheet.getWorkbook().createFont();
//        fontPresent.setColor(HSSFColor.BLACK.index);
//        fontPresent.setFontHeightInPoints((short) 12);
//        cellStylePresent.setFont(fontPresent);
//        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStylePresent.setBorderBottom((short) 1);
//        cellStylePresent.setBorderTop((short) 1);
//        cellStylePresent.setBorderLeft((short) 1);
//        cellStylePresent.setBorderRight((short) 1);
//        cellStylePresent.setFont(fontPresent);
//        int countPresent = 0, cloneJ = 0, k = 0;
//
//        for (int j = 0; j < labourList.size(); j++) {
//            cloneJ = j;
//            Row row = sheet.createRow( 7+(2*j));
////                k = i / 10;
////                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
////                Cell cell = row.createCell(k + 5);
////                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
////                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("0")) {
////                    countPresent += 1;
////                    cell.setCellStyle(cellStyle);
////                } else {
////                    cell.setCellStyle(cellStylePresent);
////                }
////
////                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getAmount());
//
//
////            Log.e("ValueOfJ", "" + cloneJ);
////            Log.e("ValueOfJ", "K" + k);
////            Log.e("ValueOfJ", "C" + countPresent);
////            int y=i/10;
////            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
////            Cell cellPresentStatus=rowPresentStatus.createCell(4);
////            String status=countPresent+"/"+labourList.size();
////            cellPresentStatus.setCellStyle(cellStylePresent);
////            cellPresentStatus.setCellValue(status);
//        }
//        createPaymentDatasheet(sheet, labourList,
//                modelDateArrayList,modelCompileStatusArrayList);
//
//    }
//
//    private void createPaymentDatasheet(Sheet sheet, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList,
//                                        ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        Font font = sheet.getWorkbook().createFont();
//        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStyle.setBorderBottom((short) 1);
//        cellStyle.setBorderTop((short) 1);
//        cellStyle.setBorderLeft((short) 1);
//        cellStyle.setBorderRight((short) 1);
//        cellStyle.setWrapText(true);
//
//        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
//        Font fontPresent = sheet.getWorkbook().createFont();
//        fontPresent.setColor(HSSFColor.BLACK.index);
//        fontPresent.setFontHeightInPoints((short) 12);
//        cellStylePresent.setFont(fontPresent);
//        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStylePresent.setBorderBottom((short) 1);
//        cellStylePresent.setBorderTop((short) 1);
//        cellStylePresent.setBorderLeft((short) 1);
//        cellStylePresent.setBorderRight((short) 1);
//        cellStylePresent.setFont(fontPresent);
//        int countPresent = 0, cloneJ = 0, k = 0;
//        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
//
//            for (int j = 0; j < labourList.size(); j++) {
//                cloneJ = j;
//                Row row = sheet.getRow(7 + (2 * j));
//                row.setHeightInPoints(22);
//                k = i / labourList.size();
//                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
//                Cell cell = row.createCell(k + 6);
//                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
//                if (ModelCompileStatusArrayList.get(i + j).getStatus().equals("0")) {
//                    countPresent += 1;
//                    cell.setCellStyle(cellStyle);
//                } else {
//                    cell.setCellStyle(cellStylePresent);
//                }
//
//                cell.setCellValue(ModelCompileStatusArrayList.get(i + j).getAmount());
//
//
////            Log.e("ValueOfJ", "" + cloneJ);
////            Log.e("ValueOfJ", "K" + k);
////            Log.e("ValueOfJ", "C" + countPresent);
////            int y=i/10;
////            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
////            Cell cellPresentStatus=rowPresentStatus.createCell(4);
////            String status=countPresent+"/"+labourList.size();
////            cellPresentStatus.setCellStyle(cellStylePresent);
////            cellPresentStatus.setCellValue(status);
//            }
//        }
//    }
//
//    private void createFooter(Sheet sheet, ArrayList<ModelLabour> labourList,
//                              ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> ModelCompileStatusArrayList) {
//        int totalSkilledCount=0,totalUnskilledCount=0,presentSkilled=0,presentUnskilled=0,sum=0;
//        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
//        Font fontPresent = sheet.getWorkbook().createFont();
//        fontPresent.setColor(HSSFColor.BLACK.index);
//        fontPresent.setFontHeightInPoints((short) 12);
//        cellStylePresent.setFont(fontPresent);
//        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStylePresent.setBorderBottom((short) 1);
//        cellStylePresent.setBorderTop((short) 1);
//        cellStylePresent.setBorderLeft((short) 1);
//        cellStylePresent.setBorderRight((short) 1);
//        cellStylePresent.setFont(fontPresent);
//
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        Font font = sheet.getWorkbook().createFont();
//        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStyle.setBorderBottom((short) 1);
//        cellStyle.setBorderTop((short) 1);
//        cellStyle.setBorderLeft((short) 1);
//        cellStyle.setBorderRight((short) 1);
//        cellStyle.setWrapText(true);
//
//        sheet.createRow((labourList.size()*2)+6);
//        sheet.createRow((labourList.size()*2)+7);
//        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
//            presentSkilled=0;
//            presentUnskilled=0;
//            sum=0;
//            for (int j = 0; j < labourList.size(); j++) {
//
//                Log.e("CheckStatus", "" + ModelCompileStatusArrayList.get(i + j).getStatus().equals("P"));
//                if(ModelCompileStatusArrayList.get(i + j).getStatus().equals("P")){
//                    if(ModelCompileStatusArrayList.get(i+j).getType().equals("Skilled")){
//                        presentSkilled+=1;
//                    }else{
//                        presentUnskilled+=1;
//                    }
//                }
//                if(!ModelCompileStatusArrayList.get(i + j).getAmount().equals("0")){
//                    sum=sum+Integer.parseInt(ModelCompileStatusArrayList.get(i+j).getAmount());
//
//                }
//
//                if(j==labourList.size()-1){
//                    int y= i/labourList.size();
//                    Row rowPresentSkilled=sheet.getRow((labourList.size()*2)+6);
//                    rowPresentSkilled.setHeightInPoints(22);
//                    Cell Status=rowPresentSkilled.createCell(y+5);
//                    Status.setCellStyle(cellStylePresent);
//                    Status.setCellValue(""+presentSkilled+"/"+presentUnskilled);
//                    Row rowTotalPayment=sheet.getRow((labourList.size()*2)+7);
//                    rowTotalPayment.setHeightInPoints(22);
//                    Cell Payment=rowTotalPayment.createCell(y+5);
//                    Payment.setCellStyle(cellStylePresent);
//                    Payment.setCellValue(sum);
//
//                }
//
//
//
//
//            }
//
//
////            int y=i/10;
////            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
////            Cell cellPresentStatus=rowPresentStatus.createCell(4);
////            String status=countPresent+"/"+labourList.size();
////            cellPresentStatus.setCellStyle(cellStylePresent);
////            cellPresentStatus.setCellValue(status);
//        }
//        Row row = sheet.createRow(labourList.size()+10);
//        Cell cellFullName = row.createCell(0);
//
//
//        cellFullName.setCellStyle(cellStyle);
////        cellFullName.setCellValue("Attendance Report");
//        String date_val = "Generated by: "+ " " +"Hajiri Register";
//        cellFullName.setCellValue(date_val);
//
//        CellRangeAddress cellMerge = new CellRangeAddress(labourList.size()+10, labourList.size()+12, 0, 5);
//        sheet.addMergedRegion(cellMerge);
//
//        String date_val1 = "Powered by: "+ " " +"Skill Zoomers";
//
//        Row row1 = sheet.createRow(labourList.size()+14);
//        Cell cellHeading = row1.createCell(0);
//
//        cellHeading.setCellStyle(cellStyle);
//        cellHeading.setCellValue(date_val1);
//
//        CellRangeAddress cellMerge1 = new CellRangeAddress(labourList.size()+14, labourList.size()+16, 0, 5);
//        sheet.addMergedRegion(cellMerge1);
//
//        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ "+ " " +"Skill Zoomers";
//
//        Row row2 = sheet.createRow(labourList.size()+18);
//        Cell cellHeading1 = row2.createCell(0);
//
//        cellHeading1.setCellStyle(cellStyle);
//        cellHeading1.setCellValue(date_val2);
//
//        CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size()+18, labourList.size()+20, 0, 5);
//        sheet.addMergedRegion(cellMerge2);
//
//
//    }
//
//    private void createAttendanceData(Sheet sheet, ArrayList<ModelLabour> labourList,
//                                      ArrayList<ModelDate> modelDateArrayList,
//                                      ArrayList<ModelCompileStatus> ModelCompileStatusArrayList) {
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        Font font = sheet.getWorkbook().createFont();
//        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStyle.setBorderBottom((short) 1);
//        cellStyle.setBorderTop((short) 1);
//        cellStyle.setBorderLeft((short) 1);
//        cellStyle.setBorderRight((short) 1);
//        cellStyle.setWrapText(true);
//
//        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
//        Font fontPresent = sheet.getWorkbook().createFont();
//        fontPresent.setColor(HSSFColor.BLACK.index);
//        fontPresent.setFontHeightInPoints((short) 12);
//        cellStylePresent.setFont(fontPresent);
//        cellStylePresent.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStylePresent.setBorderBottom((short) 1);
//        cellStylePresent.setBorderTop((short) 1);
//        cellStylePresent.setBorderLeft((short) 1);
//        cellStylePresent.setBorderRight((short) 1);
//        cellStylePresent.setFont(fontPresent);
//        int countPresent = 0, cloneJ = 0, k = 0;
//        for (int i = 0; i < ModelCompileStatusArrayList.size(); i = i + labourList.size()) {
//            countPresent = 0;
//            for (int j = 0; j < labourList.size(); j++) {
//                cloneJ = j;
//                Row row = sheet.getRow( 6+(2*j));
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
//            Log.e("ValueOfJ", "" + cloneJ);
//            Log.e("ValueOfJ", "K" + k);
//            Log.e("ValueOfJ", "C" + countPresent);
////            int y=i/10;
////            Row rowPresentStatus = sheet.createRow((labourList.size())+6);
////            Cell cellPresentStatus=rowPresentStatus.createCell(4);
////            String status=countPresent+"/"+labourList.size();
////            cellPresentStatus.setCellStyle(cellStylePresent);
////            cellPresentStatus.setCellValue(status);
//        }
//    }
//
//    private void createLAbourData(Sheet sheet, ArrayList<ModelLabour> labourList) {
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        Font font = sheet.getWorkbook().createFont();
//        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStyle.setBorderBottom((short) 1);
//        cellStyle.setBorderTop((short) 1);
//        cellStyle.setBorderLeft((short) 1);
//        cellStyle.setBorderRight((short) 1);
//        cellStyle.setWrapText(true);
//        for (int i = 0; i < labourList.size(); i++) {
//            Row row = sheet.createRow((6+(2*i)));
//            row.setHeightInPoints(22);
//            Cell cell = row.createCell(0);
//            cell.setCellStyle(cellStyle);
//            cell.setCellValue(String.valueOf(i + 1));
//            Cell cell1 = row.createCell(1);
//            cell1.setCellStyle(cellStyle);
//            cell1.setCellValue(labourList.get(i).getLabourId());
//            Cell cell2 = row.createCell(2);
//            cell2.setCellStyle(cellStyle);
//            cell2.setCellValue(labourList.get(i).getName());
//            Cell cell3 = row.createCell(3);
//            cell3.setCellStyle(cellStyle);
//            cell3.setCellValue(labourList.get(i).getType());
//            Cell cell4 = row.createCell(4);
//            cell4.setCellStyle(cellStyle);
//            cell4.setCellValue(labourList.get(i).getWages());
//            Cell cell5 = row.createCell(5);
//            cell5.setCellStyle(cellStyle);
//            cell5.setCellValue(labourList.get(i).getPayableAmt());
//
//
//        }
//    }
//
//    private void createHeaderRow(Sheet sheet, ArrayList<ModelDate> dateModelArrayList, ArrayList<ModelDate> shortDateList) {
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        Font font = sheet.getWorkbook().createFont();
//        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        cellStyle.setBorderBottom((short) 1);
//        cellStyle.setBorderTop((short) 1);
//        cellStyle.setBorderLeft((short) 1);
//        cellStyle.setBorderRight((short) 1);
//        cellStyle.setWrapText(true);
//
//
//        Row row = sheet.createRow(0);
//        Cell cellFullName = row.createCell(0);
//
//        cellFullName.setCellStyle(cellStyle);
////        cellFullName.setCellValue("Compile Report");
//        String date_val = "From: " + dateModelArrayList.get(0).getDate() + "\t To: " + dateModelArrayList.get(dateModelArrayList.size() - 1).getDate() + "\n Site Id: " + siteId +
//                "\t SiteName" + siteName;
//        cellFullName.setCellValue(date_val);
//
//        CellRangeAddress cellMerge = new CellRangeAddress(0, 2, 0, dateModelArrayList.size() + 4);
//        sheet.addMergedRegion(cellMerge);
//
//        Row row1 = sheet.createRow(3);
//        Cell cellHeading = row1.createCell(0);
//
//        cellHeading.setCellStyle(cellStyle);
//        cellHeading.setCellValue("Compile Report");
//
//        CellRangeAddress cellMerge1 = new CellRangeAddress(3, 4, 0, dateModelArrayList.size() + 4);
//        sheet.addMergedRegion(cellMerge1);
//
//        Row rowValues = sheet.createRow(5);
//        rowValues.setHeightInPoints(30);
//        Cell cellSrNo = rowValues.createCell(0);
//        cellSrNo.setCellStyle(cellStyle);
//        cellSrNo.setCellValue("Sr No");
//        Cell cellSrNo1 = rowValues.createCell(1);
//        cellSrNo1.setCellStyle(cellStyle);
//        cellSrNo1.setCellValue("Worker Id");
//        Cell cellSrNo2 = rowValues.createCell(2);
//        cellSrNo2.setCellStyle(cellStyle);
//        cellSrNo2.setCellValue("Worker Name");
//        Cell cellSrNo3 = rowValues.createCell(3);
//        cellSrNo3.setCellStyle(cellStyle);
//        cellSrNo3.setCellValue("Worker Type");
//        Cell cellSrNo4 = rowValues.createCell(4);
//        cellSrNo4.setCellStyle(cellStyle);
//        cellSrNo4.setCellValue("Wages");
//        Cell cellSrNo5 = rowValues.createCell(5);
//        cellSrNo5.setCellStyle(cellStyle);
//        cellSrNo5.setCellValue("PayableAmt");
//
//        for (int i = 0; i < shortDateList.size(); i++) {
//            Cell dateNo = rowValues.createCell(i + 6);
//            dateNo.setCellStyle(cellStyle);
//            dateNo.setCellValue(shortDateList.get(i).getDate());
//        }
//
//
////        Cell cellDesignation= row.createCell(1);
////        cellDesignation.setCellStyle(cellStyle);
////        cellDesignation.setCellValue("WorkerName");
////        for(int i=0;i<dateModelArrayList.size();i++){
////            Cell cellDate= row.createCell(i+3);
////            cellDate.setCellStyle(cellStyle);
////            cellDate.setCellValue(dateModelArrayList.get(i).getDate());
////        }
//
//
//    }
//
//    private void checkForAttendance() {
//        Date c = Calendar.getInstance().getTime();
//
//
//        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//        currentDate = df.format(c);
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            binding.btnShow.setVisibility(View.GONE);
//                            binding.llDate.setVisibility(View.GONE);
//                            Date c = Calendar.getInstance().getTime();
//
//
//                            Calendar c12 = Calendar.getInstance();
//                            c12.add(Calendar.DATE, 1);
//                            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                            String currentDate11 = df1.format(c);
//
//                            // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//                            Date temp = c12.getTime();
//                            String currentDate1 = sdf1.format(temp);
//                            Log.e("Labour1", currentDate11);
//                            Log.e("Labour2", currentDate1);
//                            getLabourList(currentDate11, currentDate1,"Today");
//
//                        } else {
//                            progressDialog.dismiss();
//                            binding.btnShow.setVisibility(View.GONE);
//                            binding.llDate.setVisibility(View.GONE);
//                            binding.btnToday.setVisibility(View.GONE);
//                            binding.btnCustom.setVisibility(View.GONE);
//                            binding.txtMessage.setText(R.string.you_have_not_uploaded_any_attendance);
//                            binding.txtMessage.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private void getLabourList(String currentDate, String currentDate1, String type) {
////        Log.e("Workertypeselected",workerTypeSelected);
////        Log.e("Workertypeselected",""+modelDateArrayList.size());
//        labourList.clear();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site")
//                .child(String.valueOf(siteId)).child("Labours");
//        reference.orderByChild("type").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    ModelLabour modelLabour = ds.getValue(ModelLabour.class);
//                    labourList.add(modelLabour);
//
//                }
//                try {
//                    getDateRange(currentDate, currentDate1, labourList,type);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
////                Log.e("LabourListSize1",""+labourList.size());
////                for(int i=0;i<labourList.size();i++){
////                    dateModelArrayList=new ArrayList<>();
////                    for(int j=0;j<modelDateArrayList.size();j++){
////                        Log.e("LoopModelDateArrayList","j"+j);
////                        getDateStatus(labourList.get(i).getLabourId(),
////                                labourList.get(i).getName(),modelDateArrayList.get(j).getDate());
////
////                    }
////                    Log.e("LoopModelDateArrayList","i"+i);
////
////                    ModelShowAttendance modelShowAttendance=new ModelShowAttendance(labourList.get(i).getLabourId(),
////                            labourList.get(i).getName(),dateModelArrayList);
////                    showAttendanceArrayList.add(modelShowAttendance);
////
////
////
////
//////                    Log.e("Date",""+i+" "+modelDateArrayList.get(i).getDate());
////                }
////                Log.e("ShowAttendanceArrayList",""+showAttendanceArrayList.size());
////                Log.e("showAttendanceDate",""+showAttendanceArrayList.get(0).getDateModelArrayList().size());
//
//
//            }
////                getLabourskilledList();
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void getSiteList() {
//        Log.e("site", "called");
//        Log.e("site", "called" + siteId);
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                siteArrayList.clear();
//                if (snapshot.exists()) {
//                    Log.e("Snapshot", "Exist");
//                } else {
//                    Log.e("Snapshot", "Exist");
//                }
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    ModelSite modelSite = ds.getValue(ModelSite.class);
//                    Log.e("ModelArrayList", "" + modelSite.getSiteName());
//                    siteArrayList.add(modelSite);
//                }
//                if (siteArrayList.size() > 0) {
//                    Log.e("ModelArrayList", "" + siteArrayList.size());
//                    Log.e("ModelArrayList", "" + siteArrayList.get(0).getSiteName());
//                    Log.e("ModelArrayList", "" + siteArrayList.get(0).getSiteCreatedDate());
//                    siteCreatedDate = siteArrayList.get(0).getSiteCreatedDate();
//
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
//    }
//
//    private void getDateRange(String fromDate, String toDate, ArrayList<ModelLabour> labourList, String type) throws ParseException {
//        modelDateArrayList = new ArrayList<>();
//        shortDateList=new ArrayList<>();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        Boolean f = true;
//        Date fDate = null, tDate = null;
//        Log.e("callFrom", fromDate);
//        Log.e("callFrom", toDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        Calendar c = Calendar.getInstance();
//        Calendar c12 = Calendar.getInstance();
//        c12.setTime(sdf.parse(toDate));
//        c.setTime(sdf.parse(fromDate));
//        try {
//            fDate = dateFormat.parse(fromDate);
//
//            tDate = dateFormat.parse(toDate);
//            if(type.equals("Custom")){
//                c12.add(Calendar.DATE, 1);
//            }
//            tDate=c12.getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (fDate == null || tDate == null) {
//            Log.e("Exception", "Error");
//        } else {
//            Date temp = fDate;
//            Log.e("callTemp", "" + temp);
//
//            int count = 0;
//            while (temp.before(tDate)) {
//
//
//                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM", Locale.US);
//                String date = df.format(temp);
//                String date1=df1.format(temp);
//                Log.e("ShreyaMamKaDate",date1);
//                modelDateArrayList.add(new ModelDate(date));
//                shortDateList.add(new ModelDate(date1));
//                Log.e("dateeeee1", modelDateArrayList.get(count).getDate());
//                count++;
//                Log.e("dateeee", date);
//                c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy");
//                temp = c.getTime();
//                Log.e("Temp", "" + count + ":" + temp);
//
//
//            }
//
//            Log.e("SizeOfDate", "" + modelDateArrayList.size());
//            Log.e("SizeOfLabour", "" + labourList.size());
//
//            countLoop = 0;
//
//            for (int i = 0; i < modelDateArrayList.size(); i++) {
//                Log.e("ModelCompileStatus", "" + ModelCompileStatusArrayList.size());
//                for (int j = 0; j < labourList.size(); j++) {
//                    Log.e("date", modelDateArrayList.get(i).getDate());
//                    Log.e("date", labourList.get(j).getLabourId());
//
//                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(),labourList.get(j).getType());
//                }
//            }
//            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
//            Log.e("ModelCompileStatus", "AfterLoop" + ModelCompileStatusArrayList.size());
//        }
//
//
//    }
//
//    private void getAttendanceList(String date, String labourId, String type) {
//
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Attendance").child("Labours").child(date).child(labourId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            String status1="";
//                            if(snapshot.child("status").getValue(String.class)!=null){
//                                status1= snapshot.child("status").getValue(String.class);
//                            }else{
//                                status1="P";
//                            }
//
//                            getPaymentStatus(date,labourId,status1,type);
////                            ModelCompileStatus ModelCompileStatus = new ModelCompileStatus(date, labourId, "P",type);
////                            addToarray(ModelCompileStatus);
////                            Log.e("model1234", "ID" + ModelCompileStatus.getLabourId() + "\t status" + ModelCompileStatus.getStatus()); //TODO correct
//
//
////                            Log.e("Size11111",""+labourList.size());
////                            Log.e("Size22222",""+modelAttendances.size());
////                            Log.e("Size33333",""+modelDateArrayList.size());
////                            if(modelDateArrayList.size()>0) {
////                                AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowCompileListActivity.this ,
////                                        labourList,
////                                        modelAttendances ,
////                                        modelDateArrayList);
////                                binding.rvShowAttendance.setAdapter(adapterShowAttendance);
////                            }
//
//                        } else {
//                            getPaymentStatus(date,labourId,"A",type);
////                            ModelCompileStatus ModelCompileStatus = new ModelCompileStatus(date, labourId, "A",type);
////                            addToarray(ModelCompileStatus);//TODO correct
//
//
//                        }
//
////                        for(int i=0;i<ModelCompileStatusArrayList.size();i++) {
////                            Log.e("ModelCompileStatusArrayList" , "" + ModelCompileStatusArrayList.get(i).getLabourId()
////                            +"Status"+ModelCompileStatusArrayList.get(i).getStatus());
////                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private void getPaymentStatus(String date, String labourId, String p, String type) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Payments").child(date).orderByChild("labourId").equalTo(labourId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        int amountSum=0;
//                        if (snapshot.getChildrenCount()>0) {
//                            for(DataSnapshot ds:snapshot.getChildren()){
//                                String amt=ds.child("amount").getValue(String.class);
//                                Log.e("Amt",amt);
//                                amountSum=amountSum+Integer.parseInt(amt);
//                            }
//                            ModelCompileStatus modelCompileStatus = new ModelCompileStatus(date, labourId, p,type,String.valueOf(amountSum));
//                            addToarray(modelCompileStatus);
//
//
//
////                            Log.e("Size11111",""+labourList.size());
////                            Log.e("Size22222",""+modelAttendances.size());
////                            Log.e("Size33333",""+modelDateArrayList.size());
////                            if(modelDateArrayList.size()>0) {
////                                AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(ShowPaymentActivity.this ,
////                                        labourList,
////                                        modelAttendances ,
////                                        modelDateArrayList);
////                                binding.rvShowAttendance.setAdapter(adapterShowAttendance);
////                            }
//
//                        } else {
//                            ModelCompileStatus modelCompileStatus = new ModelCompileStatus(date, labourId, p,type,"0");
//                            addToarray(modelCompileStatus);
//
//
//                        }
//
////                        for(int i=0;i<ModelCompileStatusArrayList.size();i++) {
////                            Log.e("ModelCompileStatusArrayList" , "" + ModelCompileStatusArrayList.get(i).getLabourId()
////                            +"Status"+ModelCompileStatusArrayList.get(i).getStatus());
////                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private void addToarray(ModelCompileStatus ModelCompileStatus) {
//        countLoop++;
//
////        Log.e("ModelCompileStatus", "Count:" + countLoop);
////        Log.e("ModelCompileStatus", "LabourSize:" + labourList.size());
////        Log.e("ModelCompileStatus", "DateSize:" + modelDateArrayList.size());
////
//        ModelCompileStatusArrayList.add(ModelCompileStatus);
////        Log.e("ModelCompileStatus", ModelCompileStatus.getDate());
////        Log.e("ModelCompileStatus", ModelCompileStatus.getLabourId());
////        Log.e("ModelCompileStatus", "Status" + ModelCompileStatus.getStatus());
////        Log.e("ModelCompileStatus", "Size:" + ModelCompileStatusArrayList.size());
//        if (countLoop == labourList.size() * modelDateArrayList.size()) {
//            progressDialog.dismiss();
////            AdapterShowCompile adapterShowAttendance = new AdapterShowCompile(ShowCompileListActivity.this,
////                    labourList,
////                    ModelCompileStatusArrayList,
////                    modelDateArrayList);
////            binding.rvShowAttendance.setAdapter(adapterShowAttendance);
////            AdapterNestedCompile adapterNestedCompile = new AdapterNestedCompile(ShowCompileListActivity.this,
////                    labourList,
////                    ModelCompileStatusArrayList,
////                    modelDateArrayList);
////            binding.rvStatus.setAdapter(adapterNestedCompile);
//            initialiseTableView(labourList,modelDateArrayList,ModelCompileStatusArrayList);
//            binding.tableview.setVisibility(View.VISIBLE);
//            binding.txtMessage.setVisibility(View.GONE);
//        } else {
//            if (labourList.size() < 1) {
//                progressDialog.dismiss();
//
////                binding.txtMessage.setText("No Labours Added");
////                binding.txtMessage.setVisibility(View.VISIBLE);
//
//            }
//        }
//    }
//
//    private void initialiseTableView(ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelCompileStatus> modelCompileStatusArrayList) {
//        TableViewModel tableViewModel = new TableViewModel(modelDateArrayList.size()+5,
//                labourList.size()*2,labourList,modelDateArrayList,null,ModelCompileStatusArrayList,"Compile");
//
//        // Create TableView Adapter
//        TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);
//
//        binding.tableview.setAdapter(tableViewAdapter);
//        binding.tableview.setTableViewListener(new TableViewListener(binding.tableview));
//
//        // Create an instance of a Filter and pass the TableView.
//        //mTableFilter = new Filter(mTableView);
//
//        // Load the dummy data to the TableView
//        tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
//                .getRowHeaderList(), tableViewModel.getCellList());
//    }
//
//    private void getDateStatus(String labourId, String name, String date) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Attendance").child("Labours")
//                .child(date).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (!snapshot.exists()) {
//                            Log.e("LoopModelDateArrayList", "NotExist GetDateStatus");
//                            DateModel dateModel = new DateModel(date, "N");
//                            dateModelArrayList.add(dateModel);
//                        } else {
//                            Log.e("LoopModelDateArrayList", "Exist GetDateStatus");
//
//                            getLabourStatus(labourId, name, date, reference);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private void getLabourStatus(String labourId, String name, String date, DatabaseReference reference) {
//        reference.child(workerTypeSelected).child(labourId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists()) {
//                    Log.e("LoopModelDateArrayList", "NotExist GetLabourStatus");
//
//                    DateModel dateModel = new DateModel(labourId, "A");
//                    dateModelArrayList.add(dateModel);
//                } else {
//                    Log.e("LoopModelDateArrayList", "Exist GetLabourStatus");
//                    DateModel dateModel = new DateModel(labourId, "P");
//                    dateModelArrayList.add(dateModel);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//    }
//
//    private void getAttendanceMaster() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Attendance").child("Labours").child("03-Nov-2022")
//                .child("Skilled")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            ModelShowAttendance modelShowAttendance = ds.getValue(ModelShowAttendance.class);
//                            showAttendanceArrayList.add(modelShowAttendance);
//
//
//                        }
//                        Log.e("showAttendanceSize", "" + showAttendanceArrayList.size());
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private void updateLabel(EditText fromdateEt) {
//        String myFormat = "dd/MM/yyyy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        fromdateEt.setText(sdf.format(myCalendar.getTime()));
//    }
//
//    class SpinnerAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return search.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inf = getLayoutInflater();
//            View row = inf.inflate(R.layout.spinner_child, null);
//
//            TextView designationText = row.findViewById(R.id.txt_designation);
//            designationText.setText(search[position]);
//
//            return row;
//        }
//    }
//
//    class SpinnerAdapter1 extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return workerType.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inf = getLayoutInflater();
//            View row = inf.inflate(R.layout.spinner_child, null);
//
//            TextView designationText = row.findViewById(R.id.txt_designation);
//            designationText.setText(workerType[position]);
//
//            return row;
//        }
//    }
//
//}