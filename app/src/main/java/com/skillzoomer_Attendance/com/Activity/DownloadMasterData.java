package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import com.skillzoomer_Attendance.com.Adapter.AdapterLabourCard;
import com.skillzoomer_Attendance.com.Adapter.AdapterPicActivity;
import com.skillzoomer_Attendance.com.Adapter.AdapterSiteSelect;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelPicActivity;
import com.skillzoomer_Attendance.com.Model.ModelPromo;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Utilities.HeaderFooterPageEvent;
import com.skillzoomer_Attendance.com.databinding.ActivityDownloadMasterDataBinding;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class DownloadMasterData extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityDownloadMasterDataBinding binding;
    long AmountRuleExcel=0,AmountRulePdf=0;
    String userType, siteId = "0", siteName = "";
    private ArrayList<ModelLabour> labourList;
    private ArrayList<ModelLabour> labourList1;
    private ArrayList<ModelLabour> skilledLabourList;
    private ArrayList<ModelLabour> unskilledLabourList;
    private ProgressDialog progressDialog;
    private String userDesignation;
    private ArrayList<ModelSite> siteAdminList;
    FirebaseAuth firebaseAuth;
    private int selected_option;
    FileOutputStream fos = null;
    File file = null;
    int promo_spinner_position = 0;
    String promo_title;
    Checkout checkout = null;
    private ArrayList<ModelPromo> promoArrayList;
    int amount = 0;
    int AmountTemp = 0;
    AlertDialog alertDialogPaymentConfirm=null;
    private String dnl_file_type="";
    File pdfFile;
    HSSFWorkbook workbook = null;
    RecyclerView rv_site_select;
    AdapterSiteSelect adapterSiteSelect;
    String currentDate;
    String timestamp ;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDownloadMasterDataBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        getSiteListAdministrator();
        labourList=new ArrayList<>();
        labourList1=new ArrayList<>();
        siteAdminList=new ArrayList<>();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        timestamp = "" + System.currentTimeMillis();
        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
                reference.child("MasterData").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
                        AmountRulePdf = snapshot.child("Pdf").getValue(long.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadMasterData.this);
                View mView = getLayoutInflater().inflate(R.layout.show_file_type, null);

                RadioButton rb_pdf,rb_xls;
                Button btn_pay;
                TextView txt_amount;
                LinearLayout ll_amount;
                ImageView iv_close;
                rb_pdf=mView.findViewById(R.id.rb_pdf);
                rb_xls=mView.findViewById(R.id.rb_xls);
                btn_pay=mView.findViewById(R.id.btn_pay);
                txt_amount=mView.findViewById(R.id.txt_amount);
                ll_amount=mView.findViewById(R.id.ll_amount);
                iv_close=mView.findViewById(R.id.iv_close);
                ll_amount.setVisibility(View.GONE);
                alert.setView(mView);
                AlertDialog alertDialog=alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                rb_pdf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            rb_xls.setChecked(false);
                            dnl_file_type="pdf";
                            amount =2*labourList1.size();
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
                        if(b){
                            rb_pdf.setChecked(false);
                            dnl_file_type="xls";
                            amount =5*labourList1.size();
                            if (amount < AmountRuleExcel) {
                                ll_amount.setVisibility(View.VISIBLE);
                                txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(amount));
                                AmountTemp = amount;
                            } else {
                                ll_amount.setVisibility(View.GONE);
                                AmountTemp = (int) AmountRulePdf;
                            }
                        }
                        Log.e("AMOUNT",""+AmountTemp);
                    }
                });

                btn_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dnl_file_type.equals("")){
                            Toast.makeText(DownloadMasterData.this, "Select file type", Toast.LENGTH_SHORT).show();
                        }else{

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

//        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//                reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            ModelSite modelSite = ds.getValue(ModelSite.class);
//                            Log.e("siteAdminList", "Size" + siteAdminList.size());
//                            for (int i = 0; i < siteAdminList.size(); i++) {
//                                if (siteAdminList.get(i).getSiteId() == modelSite.getSiteId()) {
//                                    Log.e("SiteFound", "" + siteAdminList.get(i).getSiteId());
//                                    if (siteAdminList.get(i).getSelected()) {
//                                        Log.e("SiteFound", "2::::" + siteAdminList.get(i).getSiteId());
//                                        long siteId1 = siteAdminList.get(i).getSiteId();
//                                        String siteName1 = siteAdminList.get(i).getSiteName();
////                                                    long countChild = snapshot.child(String.valueOf(siteId1)).child("Attendance").child("Labours").child(currentDate).getChildrenCount();
////                                                    if (countChild <= 0) {
////                                                        Toast.makeText(ShowAttendanceActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
////                                                    } else {
//
////                                                        labourList.clear();
////                                                        modelDateArrayList.clear();
////                                                        shortDateList.clear();
//
//                                        Date c = Calendar.getInstance().getTime();
//
//
//                                        Calendar c12 = Calendar.getInstance();
//                                        c12.add(Calendar.DATE, 1);
//                                        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//                                        String currentDate11 = df1.format(c);
//
//                                        // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//                                        Date temp = c12.getTime();
//                                        String currentDate1 = sdf1.format(temp);
//                                        Log.e("Labour1", currentDate11);
//                                        Log.e("Labour2", currentDate1);
//                                        labourList.clear();
//                                        for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()) {
//                                            ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
//                                            labourList.add(modelLabour);
//                                        }
//                                        DownloadExcel(labourList,siteId1,siteName1);
//
//
//
//
////                                                        modelDateArrayList.add(new ModelDate(currentDate11));
////                                                        shortDateList.add(new ModelDate(currentDate11));
//
//
//
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
//                                    }
//                                }
//                            }
//
//                        }
//                        getFirebaseToken();
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });
        binding.etDownloadSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadSiteDialog();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_position"));
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
    private void showDownloadSiteDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadMasterData.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_download_spinner_dialog, null);
        rv_site_select = (RecyclerView) mView.findViewById(R.id.rv_site_select);
        Button btn_ok = (Button) mView.findViewById(R.id.btn_ok);
        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        adapterSiteSelect = new AdapterSiteSelect(DownloadMasterData.this, siteAdminList);
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
                    getLabourList();

                } else {
                    Toast.makeText(DownloadMasterData.this, "Select At-least one site to download report", Toast.LENGTH_SHORT).show();
                }


            }
        });


        alertDialog.show();

    }

    private void getLabourList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labourList1.clear();
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
//                                                        Toast.makeText(ShowAttendanceActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
//                                                    } else {

//                                                        labourList.clear();
//                                                        modelDateArrayList.clear();
//                                                        shortDateList.clear();

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

                                for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()) {
                                    ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
                                    labourList1.add(modelLabour);
                                }



//
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

    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
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

                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    binding.btnDownloadReport.setVisibility(View.GONE);

                } else {
//                    siteAdminList.add(0,new ModelSite(getString(R.string.select_site),0,false));
                    siteAdminList.add(0, new ModelSite("All Sites", 0, false));
                    binding.llSelectSite.setVisibility(View.VISIBLE);
                    binding.btnDownloadReport.setVisibility(View.VISIBLE);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showPaymentDialog() {

        file=null;
        fos=null;
        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadMasterData.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_dialog, null);

        TextView txt_file_type,txt_from,txt_to,txt_amount,txt_mode_of_payment,txt_heading;
        Spinner spinner_promo;
        Button btn_cancel,btn_pay;
        txt_file_type=mView.findViewById(R.id.txt_file_type);
        txt_from=mView.findViewById(R.id.txt_from);
        txt_to=mView.findViewById(R.id.txt_to);
        txt_amount=mView.findViewById(R.id.txt_amount);
        txt_mode_of_payment=mView.findViewById(R.id.txt_mode_of_payment);
        txt_heading=mView.findViewById(R.id.txt_heading);
        spinner_promo=mView.findViewById(R.id.spinner_promo);
        spinner_promo.setVisibility(View.GONE);
//        getPromoCode(spinner_promo);
        Log.e("Dnl_file",dnl_file_type);
//        if(dnl_file_type.equals("Pdf")){
//            amount = Math.round((modelDateArrayList.size()*5));
//        }else{
//            amount = Math.round((modelDateArrayList.size()*10));
//        }

//        AmountTemp=amount;
        spinner_promo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    promo_spinner_position=i;
                    promo_title=promoArrayList.get(i).getTitle();

                    Float dis= Float.valueOf(promoArrayList.get(i).getDiscount());


                    Float d_amount= Float.valueOf(Math.round(labourList.size()*4.5));
                    dis=dis.floatValue()/100*amount;
                    AmountTemp =Math.round(amount-dis);

//                           amount= (int) (amount-((promoArrayList.get(i).getDiscount()/100)*amount));

                    Log.e("Amt","AD"+amount);
                    Log.e("Amt","D"+(promoArrayList.get(i).getDiscount()/100));
                    Log.e("Amt","DF"+dis.floatValue()/100*amount);
                    Log.e("Amt","Af"+d_amount);

                    txt_amount.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(AmountTemp));
                    txt_heading.setText(getString(R.string.your_file_one_click_away)+" "+String.valueOf(AmountTemp)+" "+getString(R.string.your_have_tom_make_payment));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txt_amount.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(AmountTemp));
        txt_heading.setText(getString(R.string.your_file_one_click_away)+" "+String.valueOf(amount)+" "+getString(R.string.your_have_tom_make_payment));
        btn_cancel=mView.findViewById(R.id.btn_cancel);
        btn_pay=mView.findViewById(R.id.btn_pay);
        txt_file_type.setText(getString(R.string.attendance_report));
//        txt_from.setText(modelDateArrayList.get(0).getDate());
//        txt_to.setText(modelDateArrayList.get(modelDateArrayList.size()-1).getDate());
        txt_from.setVisibility(View.GONE);
        txt_to.setVisibility(View.GONE);

        txt_mode_of_payment.setText("Razorpay");
        alert.setView(mView);
        alertDialogPaymentConfirm = alert.create();
        alertDialogPaymentConfirm.setCanceledOnTouchOutside(false);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=AmountTemp;
                if(dnl_file_type.equals("xls")){
                    Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
                    createOrderId.start();
                }else{
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
//                                                        Toast.makeText(ShowAttendanceActivity.this, "No Attendance Found For Site:" + " " + modelSite.getSiteCity(), Toast.LENGTH_SHORT).show();
//                                                    } else {

//                                                        labourList.clear();
//                                                        modelDateArrayList.clear();
//                                                        shortDateList.clear();

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
                                    labourList.clear();
                                    for (DataSnapshot ds1 : snapshot.child(String.valueOf(siteId1)).child("Labours").getChildren()) {
                                        ModelLabour modelLabour = ds1.getValue(ModelLabour.class);
                                        labourList.add(modelLabour);
                                    }
                                DownloadExcel(labourList,siteId1,siteName1);




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
        Log.e("Success", s);
        alertDialogPaymentConfirm.dismiss();
       
        Log.e("from", "SpinnerPos:::" + promo_spinner_position);
        Log.e("from", "PromoTitle:::" + promo_title);
        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadMasterData.this);
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
                                Toast.makeText(DownloadMasterData.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
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

    private void DownloadPdf(ArrayList<ModelLabour> labourList, long siteId1, String siteName1, RecyclerView rvLabourCard) {
//        for(int i=0;i<labourList.size();i++){
//            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
//
//            StorageReference islandRef = storageReference.child("Labours").child(String.valueOf(siteId1)).child(labourList.get(i).getLabourId());
//
//            try {
//                File localFile = File.createTempFile("images", "jpg");
//                islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        Log.e("taskSnapshot",""+taskSnapshot.getBytesTransferred());
//                        Log.e("taskSnapshot",""+localFile.getAbsolutePath());
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        Bitmap recycler_view_bm = getScreenshotFromRecyclerView(rvLabourCard);
        try {

            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "MasterData_"+siteName1+ timestamp+ ".pdf";
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
            String date_val = "Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName","")+
                    "\n"+"Generated On: " +currentDate+
                    "\n"+"Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName","")+"\n"+
                    "Site Id: " + siteId1 +
                    "\n Site Name: " + siteName1;;
            Paragraph p1 = new Paragraph(date_val);
            p1.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph p2 = new Paragraph("Workers Attendance Sheet\n");
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
                    PdfDocument.PageInfo.Builder(recycler_view_bm.getWidth(), recycler_view_bm.getHeight(), 2).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            recycler_view_bm.prepareToDraw();
            Canvas c;
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);

            c = page.getCanvas();
            c.drawPaint(paint);

//            c.drawText(date_val, 10, 25, paint);
//            c.drawText(date_val,0,0,null);
            c.drawBitmap(recycler_view_bm, 0, 0, null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
            Toast.makeText(this, "Report Generated Successfully", Toast.LENGTH_SHORT).show();
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
            Log.e("Size12323234213424",""+adapter.getItemCount());
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
                Picasso.get().load(labourList.get(i).getProfile()).
                        resize(400,400).centerCrop()
                        .placeholder(R.drawable.profile).into((ImageView) holder.itemView.findViewById(R.id.img_profile));
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();

                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
                if(i%2==0 &&i+2<size){
                    binding.rvLabourCard.scrollToPosition(i+2);
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

    private void DownloadExcel(ArrayList<ModelLabour> labourList, long siteId1, String siteName1) {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        sheet.setColumnWidth(0,7*256);
        sheet.setColumnWidth(1,11*256);
        sheet.setColumnWidth(2,11*256);
        sheet.setColumnWidth(3,11*256);
        sheet.setColumnWidth(4,11*256);
        sheet.setColumnWidth(5,11*256);
        sheet.setColumnWidth(6,11*256);
        createHeaderRow(sheet,labourList,siteId1,siteName1);
        createLAbourData(sheet, labourList,siteId1,siteName1);
        createFooter(labourList,sheet,siteId1,siteName1);
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            HSSFPatriarch dp = sheet.createDrawingPatriarch();

            HSSFClientAnchor anchor = new HSSFClientAnchor
                    (0, 0, 650, 255, (short) 2, labourList.size()*2/2, (short) 13, labourList.size()*2/2+3);


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

            String timestamp = "" + System.currentTimeMillis();
            Log.e("StrPath", str_path);
            file = new File(str_path, "MasterDataSheet"+"_"+siteName1 + timestamp+ ".xls");
//            File  localfile = new File(str_path, labourList.get(0).getLabourId()+".jpg");

//            fos = new FileOutputStream(file);

//            File localfile = new File(this.getCacheDir(), labourList.get(0).getLabourId());
//            localfile.createNewFile();
            Log.e("FilePath", file.getAbsolutePath().toString());
            Log.e("SiteId1", ""+siteId1);
            Log.e("SiteId1", labourList.get(0).getLabourId());
//            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
//            storageReference.child("Labour").child(String.valueOf(siteId1)).child(labourList.get(0).getLabourId())
//                    .getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            Log.e("Fjuhfuyf",localfile.getAbsolutePath());
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                         Log.e("Fjuhfuyf",e.getMessage());
//                        }
//                    });


            showChats asyncTask=new showChats(siteName1,siteId1);
            asyncTask.execute();
//





            if (dnl_file_type.equals("pdf")) {
//                workbook.write(fos);
//                AdapterLabourCard adapterLabourCard=new AdapterLabourCard(DownloadMasterData.this,labourList);
//                binding.rvLabourCard.setAdapter(adapterLabourCard);
//                binding.rvLabourCard.setLayoutManager(new LinearLayoutManager(DownloadMasterData.this, LinearLayoutManager.VERTICAL, false) {
//                    @Override
//                    public void onLayoutCompleted(RecyclerView.State state) {
//                        super.onLayoutCompleted(state);
//                        // TODO
//                        Log.e("DownloadingPdf","PDF");
//                        DownloadPdf(labourList,siteId1,siteName1,binding.rvLabourCard);
//                    }
//                });






            } else {
//                Thread createOrderId=new Thread(new createOrderIdThread(AmountTemp));
//                createOrderId.start();
                str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                Log.e("StrPath", str_path);
                file = new File(str_path, "MasterDataSheet"+"_"+siteName1 + timestamp+ ".xls");

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
//                AdapterLabourCard adapterLabourCard=new AdapterLabourCard(DownloadMasterData.this,labourList);
//                binding.rvLabourCard.setAdapter(adapterLabourCard);
//                binding.rvLabourCard.setLayoutManager(new LinearLayoutManager(DownloadMasterData.this, LinearLayoutManager.VERTICAL, false) {
//                    @Override
//                    public void onLayoutCompleted(RecyclerView.State state) {
//                        super.onLayoutCompleted(state);
//                        // TODO
//                        Log.e("DownloadingPdf","PDF");
//                        DownloadPdf(labourList,siteId1,siteName1,binding.rvLabourCard);
//                    }
//                });
            }
        } finally {

        }
    }

    private void ExcelToPdf(File file, FileOutputStream fos, HSSFWorkbook workbook, long siteId1, String siteName1) {
        FileInputStream input_document = null;
        String timestamp = "" + System.currentTimeMillis();
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "MasterData"+""+siteName1 + timestamp+ ".pdf";
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
        com.itextpdf.text.Document iText_xls_2_pdf = new Document();

        iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
        iText_xls_2_pdf.setPageCount(labourList.size()+1);
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

        Paragraph p2 = new Paragraph("Workers Master Data Sheet\n");
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
        PdfPTable my_table = new PdfPTable( 8);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;

        for(int i=0;i<labourList.size()*4;i++){
            PdfPCell table_cell1 = new PdfPCell();
            table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell1.setPhrase(new Phrase(String.valueOf(i)));
            my_table.addCell(table_cell1);

            PdfPCell table_cell2 = new PdfPCell();
            table_cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell2.setPhrase(new Phrase(labourList.get(i/4).getLabourId()));
            my_table.addCell(table_cell2);

            PdfPCell table_cell3 = new PdfPCell();
            table_cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell3.setPhrase(new Phrase(labourList.get(i/4).getName()));
            my_table.addCell(table_cell3);

            PdfPCell table_cell4 = new PdfPCell();
            table_cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell4.setPhrase(new Phrase(labourList.get(i/4).getType()));
            my_table.addCell(table_cell4);

            PdfPCell table_cell5 = new PdfPCell();
            table_cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell5.setPhrase(new Phrase(labourList.get(i/4).getFatherName()));
            my_table.addCell(table_cell5);

            PdfPCell table_cell6 = new PdfPCell();
            table_cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell6.setPhrase(new Phrase(labourList.get(i/4).getUniqueId()));
            my_table.addCell(table_cell6);

            PdfPCell table_cell7 = new PdfPCell();
            table_cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cell7.setPhrase(new Phrase(labourList.get(i/4).getWages()));
            my_table.addCell(table_cell7);




            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here

                String imageUrl = labourList.get(i/4).getProfile();
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
                my_table.addCell(cellImageInTable);


            }

        }



        //Loop through rows.


//        Row row = rowIterator.next();
//        Log.e("ValueI", "" + labourList.size());
//        for (int i = 0; i < labourList.size() + 1; i++) {
//            Log.e("ValueI123", "" + i);
//            Row row1 = my_worksheet.getRow(i + 6);
//
//            Cell cell1 = row1.getCell(2);
//
//            Iterator<Cell> cellIterator = row1.cellIterator();
//            while (cellIterator.hasNext()) {
//                Cell cell = cellIterator.next(); //Fetch CELL
//
//                switch (cell.getCellType()) { //Identify CELL type
//                    //you need to add more code here based on
//                    //your requirement / transformations
//                    case Cell.CELL_TYPE_STRING:
//                        //Push the data from Excel to PDF Cell
//                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
//                        //feel free to move the code below to suit to your needs
//                        my_table.addCell(table_cell);
//                        break;
//                }
//                //next line
//            }
//        }
//        Row row2=my_worksheet.getRow(labourList.size() + 8);
//        Iterator<Cell> cellIterator = row2.cellIterator();
//        while (cellIterator.hasNext()) {
//            Cell cell = cellIterator.next(); //Fetch CELL
//
//            switch (cell.getCellType()) { //Identify CELL type
//                //you need to add more code here based on
//                //your requirement / transformations
//                case Cell.CELL_TYPE_STRING:
//                    //Push the data from Excel to PDF Cell
//                    table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
//                    //feel free to move the code below to suit to your needs
//                    my_table.addCell(table_cell);
//                    break;
//                case Cell.CELL_TYPE_BLANK:
//                    table_cell=new PdfPCell(new Phrase(""));
//                    my_table.addCell(table_cell);
//            }
//            //next line
//        }


        //Finally add the table to PDF document
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

    }

    public class showChats extends AsyncTask<String, String, String> {

        private String siteName1;
        private long siteId1;

        public showChats(String siteName1, long siteId1) {
            this.siteName1 = siteName1;
            this.siteId1 = siteId1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(DownloadMasterData.this);
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
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "MasterData"+""+siteName1 + timestamp+ ".pdf";
            File pdfFile = new File(str_path);

            com.itextpdf.text.Font fontHeading=new com.itextpdf.text.Font();
            fontHeading.setStyle(com.itextpdf.text.Font.NORMAL);
            fontHeading.setSize(25);
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
            com.itextpdf.text.Document iText_xls_2_pdf = new Document(PageSize.A4.rotate(), 1f, 1f, 10f, 0f);


            try {
                PdfWriter writer=PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
                HeaderFooterPageEvent event = new HeaderFooterPageEvent(DownloadMasterData.this,siteId1,siteName1,null,"Master Data Sheet");
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
            table_cellhh6.setPhrase(new Phrase(  "Site Workers Master Data Sheet",fontHeading1));
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


//            try {
//               iText_xls_2_pdf.add(my_table1);
//               iText_xls_2_pdf.add(p1);
//            } catch (DocumentException e) {
//                e.printStackTrace();
//            }
            //we have two columns in the Excel sheet, so we create a PDF table with two columns
            //Note: There are ways to make this dynamic in nature, if you want to.
            PdfPTable my_table = new PdfPTable( 8);
            my_table.setTotalWidth(iText_xls_2_pdf.getPageSize().getWidth());
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
            table_cellh2.setPhrase(new Phrase("Worker Id",fontBold));
            table_cellh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh2);

            PdfPCell table_cellh3 = new PdfPCell();
            table_cellh3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh3.setPhrase(new Phrase("Name",fontBold));
            table_cellh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh3);

            PdfPCell table_cellh4 = new PdfPCell();
            table_cellh4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh4.setPhrase(new Phrase("Type",fontBold));
            table_cellh4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh4);

            PdfPCell table_cellh5 = new PdfPCell();
            table_cellh5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh5.setPhrase(new Phrase("Father's Name",fontBold));
            table_cellh5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh5);

            PdfPCell table_cellh6 = new PdfPCell();
            table_cellh6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh6.setPhrase(new Phrase("Unique Id /Mob No",fontBold));
            table_cellh6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh6);

            PdfPCell table_cellh7 = new PdfPCell();
            table_cellh7.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh7.setPhrase(new Phrase("Wages",fontBold));
            table_cellh7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh7);

            PdfPCell table_cellh8 = new PdfPCell();
            table_cellh8.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_cellh8.setPhrase(new Phrase("Image",fontBold));
            table_cellh8.setVerticalAlignment(Element.ALIGN_MIDDLE);
            my_table.addCell(table_cellh8);


            for(int i=0;i<labourList.size();i++){
                PdfPCell table_cell1 = new PdfPCell();
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setPhrase(new Phrase(String.valueOf(i+1),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell1);

                PdfPCell table_cell2 = new PdfPCell();
                table_cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell2.setPhrase(new Phrase(labourList.get(i).getLabourId(),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell2);

                PdfPCell table_cell3 = new PdfPCell();
                table_cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell3.setPhrase(new Phrase(labourList.get(i).getName(),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell3);

                PdfPCell table_cell4 = new PdfPCell();
                table_cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell4.setPhrase(new Phrase(labourList.get(i).getType(),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell4);

                PdfPCell table_cell5 = new PdfPCell();
                table_cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell5.setPhrase(new Phrase(labourList.get(i).getFatherName(),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell5);

                PdfPCell table_cell6 = new PdfPCell();
                table_cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell6.setPhrase(new Phrase(labourList.get(i).getUniqueId(),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell6);

                PdfPCell table_cell7 = new PdfPCell();
                table_cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell7.setPhrase(new Phrase(String.valueOf(labourList.get(i).getWages()),fontHeading));
                table_cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
                my_table.addCell(table_cell7);




                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here

                    String imageUrl = labourList.get(i/4).getProfile();
                    Image imageFromWeb = null;
                    Image croppedImage=null;
                    try {
                        imageFromWeb = Image.getInstance(new URL(imageUrl));
                        imageFromWeb.setScaleToFitHeight(true);
                        imageFromWeb.setWidthPercentage(50);

                    } catch (BadElementException e) {
                        Log.e("IEcxc","1"+e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("IEcxc","1"+e.getMessage());
                        e.printStackTrace();
                    }
                    PdfPCell cellImageInTable = new PdfPCell(imageFromWeb);
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(DownloadMasterData.this,"Downloaded",Toast.LENGTH_LONG);
            pd.dismiss();

        }
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
//                            progressDialog.dismiss();


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
    private void createFooter(ArrayList<ModelLabour> labourList, Sheet sheet, long siteId1, String siteName1) {
        Row row = sheet.createRow(labourList.size()+10);
        Cell cellFullName = row.createCell(0);

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 0);
        cellStyle.setBorderTop((short) 0);
        cellStyle.setBorderLeft((short) 0);
        cellStyle.setBorderRight((short) 0);


        cellFullName.setCellStyle(cellStyle);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Generated by: "+ " " +"Hajiri Register";
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(labourList.size()+10, labourList.size()+12, 0, 5);
        sheet.addMergedRegion(cellMerge);

        String date_val1 = "Powered by: "+ " " +"Skill Zoomers";

        Row row1 = sheet.createRow(labourList.size()+14);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle);
        cellHeading.setCellValue(date_val1);

        CellRangeAddress cellMerge1 = new CellRangeAddress(labourList.size()+14, labourList.size()+16, 0, 5);
        sheet.addMergedRegion(cellMerge1);

        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ ";

        Row row2 = sheet.createRow(labourList.size()+18);
        Cell cellHeading1 = row2.createCell(0);

        cellHeading1.setCellStyle(cellStyle);
        cellHeading1.setCellValue(date_val2);

        CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size()+18, labourList.size()+20, 0, 5);
        sheet.addMergedRegion(cellMerge2);


    }

    private void createLAbourData(Sheet sheet, ArrayList<ModelLabour> labourList, long siteId1, String siteName1) {
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
        for (int i = 0; i < labourList.size(); i++) {
            Row row = sheet.createRow(i + 7);
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
            cell4.setCellValue(labourList.get(i).getFatherName());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(cellStyle);
            cell5.setCellValue(labourList.get(i).getUniqueId());
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(cellStyle);
            cell6.setCellValue(String.valueOf(labourList.get(i).getWages()));
            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(cellStyle);
            cell7.setCellValue(String.valueOf("IMAGE"));



        }

    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelLabour> labourList, long siteId1, String siteName1) {
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
        org.apache.poi.ss.usermodel.Cell cellFullName = row.createCell(0);

        cellFullName.setCellStyle(cellStyle);
//        cellFullName.setCellValue("Attendance Report");
        String date_val = "Industry Name: " + getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName","")+
                "\n"+"Company Name:"+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName","");

        cellFullName.setCellValue(date_val);


        CellRangeAddress cellMerge = new CellRangeAddress(0, 3, 0, 6);
        sheet.addMergedRegion(cellMerge);

        Cell cellFullName1=row.createCell(7);
        cellFullName1.setCellStyle(cellStyle1);
        String date_val1="Generated On: " +currentDate+"\n"+ "Site Id: " + siteId1 +
                "\t\t\t\t\t Site Name: " + siteName1;
        cellFullName1.setCellValue(date_val1);


        CellRangeAddress cellMerge1 = new CellRangeAddress(0, 3, 7, 12);
        sheet.addMergedRegion(cellMerge1);




        Row row1 = sheet.createRow(4);
        Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle2);
        cellHeading.setCellValue("WORKERS MASTER DATA SHEET");

        CellRangeAddress cellMerge2 = new CellRangeAddress(4, 5, 0, 12);
        sheet.addMergedRegion(cellMerge2);

        Row rowValues = sheet.createRow(6);
        org.apache.poi.ss.usermodel.Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        org.apache.poi.ss.usermodel.Cell cellSrNo1 = rowValues.createCell(1);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Worker Id");
        org.apache.poi.ss.usermodel.Cell cellSrNo2 = rowValues.createCell(2);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Worker Name");
        Cell cellSrNo3 = rowValues.createCell(3);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Type");
        Cell cellSrNo4 = rowValues.createCell(4);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Father's Name");
        Cell cellSrNo5 = rowValues.createCell(5);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("Unique ID/Mob No");
        Cell cellSrNo6 = rowValues.createCell(6);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Wages");
        Cell cellSrNo7 = rowValues.createCell(7);
        cellSrNo7.setCellStyle(cellStyle);
        cellSrNo7.setCellValue("Worker Image");
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment Failse due to " + s, Toast.LENGTH_LONG);
        Log.e("Success", s);
        alertDialogPaymentConfirm.dismiss();
        Log.e("from", "SpinnerPos:::" + promo_spinner_position);
        Log.e("from", "PromoTitle:::" + promo_title);
        final AlertDialog.Builder alert = new AlertDialog.Builder(DownloadMasterData.this);
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
                hashMap.put("fromDate", "");
                hashMap.put("toDate", "");
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
                                Toast.makeText(DownloadMasterData.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();

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
}