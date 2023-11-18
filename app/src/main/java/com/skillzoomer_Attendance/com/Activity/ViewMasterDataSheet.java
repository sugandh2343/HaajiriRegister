package com.skillzoomer_Attendance.com.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.skillzoomer_Attendance.com.Adapter.AdapterLabour;
import com.skillzoomer_Attendance.com.Adapter.AdapterLabourAdmin;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelPromo;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityViewMasterDataSheetBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewMasterDataSheet extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityViewMasterDataSheetBinding binding;
    LayoutToolbarBinding toolbarBinding;
    String userType, siteId = "0", siteName = "";
    private ArrayList<ModelLabour> labourList;
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
    String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewMasterDataSheetBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
//        Log.e("binding",""+(binding.btnAddMember.getVisibility()==View.VISIBLE));
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        promoArrayList=new ArrayList<>();
        userDesignation = sharedpreferences.getString("userDesignation", "");
        selected_option = sharedpreferences.getInt("workOption", 0);
        MyApplication my = new MyApplication();
        my.updateLanguage(this, sharedpreferences.getString("Language", "hi"));
        Intent intent = getIntent();
        Log.e("Intent", "" + (intent == null));
        siteAdminList = new ArrayList<>();
        if(userDesignation.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        if (userDesignation.equals("Supervisor")) {
            binding.llSelectSite.setVisibility(View.GONE);
            binding.btnDownload.setVisibility(View.GONE);
            binding.btnAddWorker.setVisibility(View.VISIBLE);
            siteId = String.valueOf(sharedpreferences.getLong("siteId", 0));
            siteName = sharedpreferences.getString("siteName", "");
            Log.e("SiteID786", "Receive:" + siteName + "::::::" + siteId);
        } else {
            binding.btnDownload.setVisibility(View.VISIBLE);
            siteId = String.valueOf(intent.getStringExtra("siteId"));
            siteName = intent.getStringExtra("siteName");
            Log.e("SiteID786123", "Receive:" + siteName + "::::::" + siteId);
            if (intent.getStringExtra("siteId") == null) {
                Log.e("siteId215352", "null");
                binding.btnAddWorker.setVisibility(View.VISIBLE);
                binding.btnDownload.setVisibility(View.VISIBLE);
                binding.llSelectSite.setVisibility(View.VISIBLE);
                getSiteListAdministrator();
                binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i > 0) {
                            siteId = String.valueOf(siteAdminList.get(i).getSiteId());
                            siteName = siteAdminList.get(i).getSiteName();
//                        getSiteListAdministrator();
                            Log.e("SiteId", "Spinner" + siteId);
                            binding.btnAddWorker.setVisibility(View.VISIBLE);
                            if(siteAdminList.size()>2){
                                binding.btnTransfer.setVisibility(View.VISIBLE);
                            }

                            getLabourList(userType);
                        }else{
                            binding.btnAddWorker.setVisibility(View.VISIBLE);
                            binding.btnTransfer.setVisibility(View.VISIBLE);
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
        }

        binding.btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ViewMasterDataSheet.this,TransferLabourActivity.class);
//                intent1.putExtra("siteId",siteId);
//                intent1.putExtra("siteName",siteName);
//                Log.e("SiteIDSend",""+siteId);
                startActivity(intent1);
            }
        });


        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final AlertDialog.Builder alert = new AlertDialog.Builder(ViewMasterDataSheet.this);
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
//                            amount = Math.round((labourList.size()*5));
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
//                            amount = Math.round((labourList.size()*10));
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
//                            Toast.makeText(ViewMasterDataSheet.this, "Select file type", Toast.LENGTH_SHORT).show();
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


                startActivity(new Intent(ViewMasterDataSheet.this,DownloadMasterData.class));





//

            }
        });


//        if(userDesignation!=null&&userDesignation.equals("Supervisor")){
//
//        }


//        userType=intent.getStringExtra("userType");
//        siteId=intent.getStringExtra("siteId");
//        Log.e("SiteId","admin"+siteId);
//        siteName=intent.getStringExtra("siteName");
        binding.llMasterDataSheet.setVisibility(View.VISIBLE);
        binding.btnAddWorker.setVisibility(View.VISIBLE);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        binding.UserCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading....");
                userType = "Skilled";
                binding.btnAddWorker.setText(getString(R.string.add_skilled_worker));
                binding.txtHeading.setText(getString(R.string.master_data_sheet_for) + " " + getString(R.string.skilled));
                binding.llMasterDataSheet.setVisibility(View.VISIBLE);
                binding.UserCard.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));
                binding.DriverCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                getLabourList(userType);
            }
        });
        binding.DriverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage(getString(R.string.loding));
                userType = "Unskilled";
                binding.txtHeading.setText(getString(R.string.master_data_sheet_for) + " " + getString(R.string.unskilled));
                binding.btnAddWorker.setText(getString(R.string.add_unskilled_worker));
                binding.llMasterDataSheet.setVisibility(View.VISIBLE);
                binding.UserCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                binding.DriverCard.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));
                getLabourList(userType);
            }
        });
        binding.txtHeading.setText(getString(R.string.master_data_sheet_for) + " " + getString(R.string.skilled));
        labourList = new ArrayList<>();
        binding.btnAddWorker.setText(getString(R.string.add_skilled_worker));
        skilledLabourList = new ArrayList<>();
        unskilledLabourList = new ArrayList<>();

        binding.btnAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getString("userDesignation","").equals("HR Manager") && binding.spinnerSelectSite.getSelectedItemPosition()==0){
                    Toast.makeText(ViewMasterDataSheet.this, "Select Site At which you want to add labour", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(ViewMasterDataSheet.this, LabourRegistration.class);
                    intent.putExtra("Activity", "MasterData");
                    intent.putExtra("type", userType);
                    intent.putExtra("siteName", siteName);
                    intent.putExtra("siteId", siteId);
                    startActivity(intent);
                }

            }
        });

    }
    private void showPaymentDialog() {

        file=null;
        fos=null;
        final AlertDialog.Builder alert = new AlertDialog.Builder(ViewMasterDataSheet.this);
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
        getPromoCode(spinner_promo);
        Log.e("Dnl_file",dnl_file_type);
//        if(dnl_file_type.equals("Pdf")){
//            amount = Math.round((modelDateArrayList.size()*5));
//        }else{
//            amount = Math.round((modelDateArrayList.size()*10));
//        }

        AmountTemp=amount;
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

        txt_amount.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(amount));
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
                    DownloadExcel(labourList);
                }else{
                    DownloadExcel(labourList);
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



    private void DownloadExcel(ArrayList<ModelLabour> labourList) {
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
        createHeaderRow(sheet,labourList);
        createLAbourData(sheet, labourList);
        createFooter(labourList,sheet);
        

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());

        } finally {}

//        int amount=100;
        Log.e("Amt","Send"+amount);
        Thread createOrderId=new Thread(new createOrderIdThread(amount));
        createOrderId.start();
        Bitmap recycler_view_bm = getScreenshotFromRecyclerView(binding.rvLabourList);
        try {
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            pdfFile = new File(str_path, "MasterData" + siteName +"_" +".xls");

            pdfFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(pdfFile);

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(recycler_view_bm.getWidth(), recycler_view_bm.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            recycler_view_bm.prepareToDraw();
            Canvas c;
            c = page.getCanvas();
            c.drawBitmap(recycler_view_bm, 0, 0, null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
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

    private void createFooter(ArrayList<ModelLabour> labourList, Sheet sheet) {
        Row row = sheet.createRow(labourList.size()+10);
        Cell cellFullName = row.createCell(0);

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 12);
        cellStyle.setBorderTop((short) 12);
        cellStyle.setBorderLeft((short) 12);
        cellStyle.setBorderRight((short) 12);


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

        String date_val2 = "To know us more visit https://skillzoomers.com/hajiri-register/ "+ " " +"Skill Zoomers";

        Row row2 = sheet.createRow(labourList.size()+18);
        Cell cellHeading1 = row2.createCell(0);

        cellHeading1.setCellStyle(cellStyle);
        cellHeading1.setCellValue(date_val2);

        CellRangeAddress cellMerge2 = new CellRangeAddress(labourList.size()+18, labourList.size()+20, 0, 5);
        sheet.addMergedRegion(cellMerge2);

    }

    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
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
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
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
    public class createOrderIdThread implements Runnable{
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
                Log.e("Amt","Request"+amount);
                orderRequest.put("amount", amount*100); // amount in the smallest currency unit
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_11");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Exception","COID");
            Order order = null;
            try {
                order = razorpay.orders.create(orderRequest);
            } catch (RazorpayException e) {
                Log.e("Exception",e.getMessage());
                e.printStackTrace();
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(String.valueOf(order));
                String id = jsonObject.getString("id");
                Log.e("Response",""+id);
                if(id!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startPayment(id,amount);
                        }
                    });
                }

            } catch (JSONException e) {
                Log.e("Exception",e.getMessage());
                e.printStackTrace();
            }


        }
    }

    private void startPayment(String id, int amount) {
        Checkout.preload(this);
        Checkout checkout=new Checkout();
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

            options.put("name", getString(R.string.app_name1));
            options.put("description", "Haj123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("order_id", id);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(amount));//pass amount in currency subunits
            options.put("prefill.email", getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userName","")+"@yopmail.com");
            options.put("prefill.contact",getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userMobile",""));
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(this, options);
            progressDialog.dismiss();

        } catch(Exception e) {
            Log.e("Sugandh", "Error in starting Razorpay Checkout", e);
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
        cellStyle.setBorderBottom((short) 12);
        cellStyle.setBorderTop((short) 12);
        cellStyle.setBorderLeft((short) 12);
        cellStyle.setBorderRight((short) 12);
        for (int i = 0; i < labourList.size(); i++) {
            Row row = sheet.createRow(i + 6);
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
            Cell cell6 = row.createCell(5);
            cell6.setCellStyle(cellStyle);
            cell6.setCellValue(String.valueOf(labourList.get(i).getWages()));
            Cell cel7 = row.createCell(6);
            cel7.setCellStyle(cellStyle);
            cel7.setCellValue(String.valueOf(labourList.get(i).getPayableAmt()));
            



        }
        

        
    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelLabour> labourList) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setBorderBottom((short) 12);
        cellStyle.setBorderTop((short) 12);
        cellStyle.setBorderLeft((short) 12);
        cellStyle.setBorderRight((short) 12);


        Row row = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell cellFullName = row.createCell(0);

        cellFullName.setCellStyle(cellStyle);
//        cellFullName.setCellValue("Attendance Report");
        String date_val =  "Site Id: " + siteId +
                "\t SiteName" + siteName;
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(0, 2, 0, 7);
        sheet.addMergedRegion(cellMerge);

        Row row1 = sheet.createRow(3);
        org.apache.poi.ss.usermodel.Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle);
        cellHeading.setCellValue("Master Data Report");

        CellRangeAddress cellMerge1 = new CellRangeAddress(3, 4, 0, 7);
        sheet.addMergedRegion(cellMerge1);

        Row rowValues = sheet.createRow(5);
        org.apache.poi.ss.usermodel.Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        org.apache.poi.ss.usermodel.Cell cellSrNo1 = rowValues.createCell(1);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Worker Id");
        org.apache.poi.ss.usermodel.Cell cellSrNo2 = rowValues.createCell(2);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Worker Type");
        Cell cellSrNo3 = rowValues.createCell(3);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("Father's Name");
        Cell cellSrNo4 = rowValues.createCell(4);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("Unique ID No");
        Cell cellSrNo5 = rowValues.createCell(5);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("Per Day Wages");
        Cell cellSrNo6 = rowValues.createCell(6);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Payable Amount");
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
                PromoSpinnerAdapter promoSpinnerAdapter = new PromoSpinnerAdapter();
                spinner_promo.setAdapter(promoSpinnerAdapter);


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
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    if (modelSite.getSiteStatus().equals("Active")) {
                        siteAdminList.add(modelSite);
                    }

                }
                Log.e("siteAdminList", "" + siteAdminList.size());
                siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0));
                SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                siteId = String.valueOf(siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId());
                siteName = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteCity();
                binding.llSelectSite.setVisibility(View.VISIBLE);
                if(siteAdminList.size()>2){
                    binding.btnTransfer.setVisibility(View.VISIBLE);
                }else{
                    binding.btnTransfer.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLabourList(String userType) {
        labourList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(siteId)).child("Labours");
        reference.orderByChild("type").equalTo(userType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelLabour modelLabour = ds.getValue(ModelLabour.class);
                    labourList.add(modelLabour);

                }
                Log.e("LabourListSize1", "" + labourList.size());
                if (labourList.size() == 0) {
                    binding.rvLabourList.setVisibility(View.GONE);
                    binding.txtNoLabours.setVisibility(View.VISIBLE);
                } else {
                    binding.rvLabourList.setVisibility(View.VISIBLE);
                    binding.txtNoLabours.setVisibility(View.GONE);
                    AdapterLabour adapterLabourSkilled = new AdapterLabour(ViewMasterDataSheet.this, labourList);
                    AdapterLabourAdmin adapterLabourAdmin = new AdapterLabourAdmin(ViewMasterDataSheet.this, labourList);
                    Log.e("IIIII", userDesignation);
                    if (userDesignation.equals("Supervisor")) {
                        binding.rvLabourList.setAdapter(adapterLabourSkilled);
                    } else {
                        binding.rvLabourList.setAdapter(adapterLabourAdmin);
                    }

                }
//                getLabourskilledList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Toast.makeText(this,"Payment Success and Payment Id is "+s,Toast.LENGTH_LONG);
        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        Log.e("StrPath", str_path);
        file = new File(str_path, "MasterData" + siteName + "_" + ".xls");

        try {
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("FilePath", file.getAbsolutePath().toString());

        Log.e("Success",s);
        alertDialogPaymentConfirm.dismiss();

        Log.e("from","SpinnerPos:::"+promo_spinner_position);
        Log.e("from","PromoTitle:::"+promo_title);
        final AlertDialog.Builder alert = new AlertDialog.Builder(ViewMasterDataSheet.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_sucess, null);
        TextView txt_payment_id,txt_payment_amt;
        Button btn_ok;
        txt_payment_id=mView.findViewById(R.id.txt_payment_id);
        txt_payment_amt=mView.findViewById(R.id.txt_payment_amt);
        btn_ok=mView.findViewById(R.id.btn_ok);
        txt_payment_id.setText(paymentData.getPaymentId());
        txt_payment_amt.setText(String.valueOf(amount));
        alert.setView(mView);
        AlertDialog alertDialog= alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentDate;
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                currentDate = df.format(c);
                String currentTime="";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                }
                String timestamp = "" + System.currentTimeMillis();

                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("dateOfPayment",currentDate);
                hashMap.put("timeOfPayment",currentTime);
                hashMap.put("timestamp",timestamp);
                hashMap.put("paymentId",paymentData.getPaymentId());
                hashMap.put("orderId",paymentData.getOrderId());
                hashMap.put("signature",paymentData.getSignature());
                hashMap.put("paidAmount",String.valueOf(amount));
                hashMap.put("status","Success");
                hashMap.put("downloadFile","Master Data");

                hashMap.put("fromDate","");
                hashMap.put("toDate","");
                if(promo_spinner_position>0){
                    hashMap.put("promoApplied",true);
                    hashMap.put("promoTitle",promo_title);
                }else{
                    hashMap.put("promoApplied",false);
                    hashMap.put("promoTitle","");
                }
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData",paymentData.getData().toString());
                                Log.e("paymentData",paymentData.getPaymentId().toString());
                                if (fos != null) {
                                    try {
                                        Log.e("fos", "flush");
                                        fos.flush();
                                        fos.close();
                                        Toast.makeText(ViewMasterDataSheet.this, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    } catch (IOException e) {
                                        Log.e("IOEXCEPTION1", e.getMessage());
                                        e.printStackTrace();
                                    }
                                }


//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
//                        startActivity(intent);

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

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this,"Payment failed due to: "+s,Toast.LENGTH_LONG);
        Log.e("Success",s);
        alertDialogPaymentConfirm.dismiss();
        Log.e("from","SpinnerPos:::"+promo_spinner_position);
        Log.e("from","PromoTitle:::"+promo_title);
        final AlertDialog.Builder alert = new AlertDialog.Builder(ViewMasterDataSheet.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_failure, null);
        TextView txt_failed_reason;
        Button btn_ok;
        txt_failed_reason=mView.findViewById(R.id.txt_failed_reason);
        btn_ok=mView.findViewById(R.id.btn_ok);
        txt_failed_reason.setText(s);
        alert.setView(mView);
        AlertDialog alertDialog= alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentDate;
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                currentDate = df.format(c);
                String currentTime="";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                }
                String timestamp = "" + System.currentTimeMillis();

                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("dateOfPayment",currentDate);
                hashMap.put("timeOfPayment",currentTime);
                hashMap.put("timestamp",timestamp);
                hashMap.put("paymentId","");
                hashMap.put("orderId",paymentData.getOrderId());
                hashMap.put("signature","");
                hashMap.put("paidAmount",String.valueOf(amount));
                hashMap.put("status","Failed");
                hashMap.put("downloadFile","Master Data");
                hashMap.put("fromDate","");
                hashMap.put("toDate","");
                hashMap.put("siteId",siteId);
                hashMap.put("siteName",siteName);
                if(promo_spinner_position>0){
                    hashMap.put("promoApplied",true);
                    hashMap.put("promoTitle",promo_title);
                }else{
                    hashMap.put("promoApplied",false);
                    hashMap.put("promoTitle","");
                }
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData",paymentData.getData().toString());
                                Log.e("paymentData",paymentData.getPaymentId().toString());
                                Toast.makeText(ViewMasterDataSheet.this, getString(R.string.payment_failed_and_file_could_not_be_generated), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();



//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
//                        startActivity(intent);

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
            return view2;
        }
    }

    private void getLabourskilledList() {
        Log.e("LabourListSize", "" + labourList.size());
        if (labourList.size() > 0) {
            for (int i = 0; i < labourList.size(); i++) {
                Log.e("Check", "" + labourList.get(i).getType());
                if (labourList.get(i).getType().equals("Skilled")) {
                    ModelLabour modelLabour = labourList.get(i);
                    Log.e("wages", "" + modelLabour.getWages());
                    skilledLabourList.add(modelLabour);
                } else {
                    ModelLabour modelLabour = labourList.get(i);
                    unskilledLabourList.add(modelLabour);
                }
                Log.e("skilledLabour1", "" + skilledLabourList.size());
                if (userType.equals("Skilled")) {
                    AdapterLabour adapterLabourSkilled = new AdapterLabour(this, skilledLabourList);
                    binding.rvLabourList.setAdapter(adapterLabourSkilled);
                }

                AdapterLabour adapterLabourunskilled = new AdapterLabour(this, unskilledLabourList);
                binding.rvLabourList.setAdapter(adapterLabourunskilled);

            }
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

    @Override
    protected void onResume() {
        Log.e("UserTye", "Resume" + userType);
        getLabourList(userType);
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            startActivity(new Intent(ViewMasterDataSheet.this,MemberTimelineActivity.class));
            finish();
        }else{
            startActivity(new Intent(ViewMasterDataSheet.this,timelineActivity.class));
            finish();
        }

    }
}