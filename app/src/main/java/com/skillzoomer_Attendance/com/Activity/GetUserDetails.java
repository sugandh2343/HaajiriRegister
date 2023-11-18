package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterUserDetails;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.Model.ModelUserIndustry;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityGetUserDetailsBinding;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.internal.cache.DiskLruCache;

public class GetUserDetails extends AppCompatActivity {
    ActivityGetUserDetailsBinding binding;
    private ArrayList<ModelUser> userArrayList;
    private ArrayList<ModelUser> userArrayListFilter;
    File pdfFile;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userArrayList = new ArrayList<>();
        userArrayListFilter = new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("LOADING...");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        getUserData("All");
        binding.etSearch.setVisibility(View.GONE);
        binding.rbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.rbToday.setChecked(false);
                    binding.rbCustom.setChecked(false);
                    binding.llDate.setVisibility(View.GONE);
                    binding.rvUserDetails.setVisibility(View.VISIBLE);
                    getUserData("All");
                }
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userArrayListFilter.clear();
                if (userArrayList.size() > 0) {
                    if (charSequence.toString().length() > 0) {
                        for (int k = 0; k < userArrayList.size(); k++) {
                            if (userArrayList.get(k).getUserId().toLowerCase(Locale.ROOT).equals(charSequence.toString().toLowerCase(Locale.ROOT))
                                    || userArrayList.get(k).getUserId().toLowerCase(Locale.ROOT).contains(charSequence.toString().toLowerCase(Locale.ROOT))
                                    || userArrayList.get(k).getUserId().toLowerCase(Locale.ROOT).equals(charSequence.toString().toLowerCase(Locale.ROOT))
                                    || userArrayList.get(k).getUserId().toLowerCase(Locale.ROOT).equals(charSequence.toString().toLowerCase(Locale.ROOT))
                                    || userArrayList.get(k).getUserId().toLowerCase(Locale.ROOT).contains(charSequence.toString().toLowerCase(Locale.ROOT))
                                    || userArrayList.get(k).getUserId().toLowerCase(Locale.ROOT).equals(charSequence.toString().toLowerCase(Locale.ROOT))){
                                userArrayListFilter.add(userArrayList.get(k));

                            }
                        }
                        AdapterUserDetails adapterUserDetails = new AdapterUserDetails(GetUserDetails.this, userArrayListFilter);

                        binding.rvUserDetails.setAdapter(adapterUserDetails);


                    }
                }else{
                    getUserData("All");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.btnOperatiom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                            reference1.child(ds.child("hrUid").getValue(String.class)).child("Industry").child("Construction")
                                    .child("Site")
                                    .child(String.valueOf(ds.child("siteId").getValue(long.class))).setValue(ds.getValue())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(GetUserDetails.this, "Operation Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        binding.btnSyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {


                            if (ds.child("userType").getValue(String.class) != null && ds.child("userType").getValue(String.class).equals("HR Manager")) {
                                Log.e("UserId", "" + ds.getKey().toString());
                                if (ds.child("Industry").child("1").exists()) {
                                    for (DataSnapshot ds1 : ds.child("Industry").child("1").getChildren()) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("companyEmail", ds.child("companyEmail").getValue(String.class));
                                        hashMap.put("industryPosition", ds.child("industryPosition").getValue(long.class));
                                        hashMap.put("industryName", ds.child("industryName").getValue(String.class));
                                        hashMap.put("industryNameHindi", ds.child("industryNameHindi").getValue(String.class));
                                        hashMap.put("designationName", ds.child("designationName").getValue(String.class));
                                        hashMap.put("designationNameHindi", ds.child("designationNameHindi").getValue(String.class));
                                        hashMap.put("companyName", ds.child("companyName").getValue(String.class));
                                        hashMap.put("designationPosition", ds.child("designationPosition").getValue(long.class));
                                        reference.child(ds.child("uid").getValue(String.class)).child("Industry").child("Construction").updateChildren(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        reference.child(ds.child("uid").getValue(String.class)).child("Industry").child("1")
                                                                .removeValue();
                                                    }
                                                });


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
        });

        binding.rbToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.rbAll.setChecked(false);
                    binding.rbCustom.setChecked(false);
                    binding.llDate.setVisibility(View.GONE);
                    binding.rvUserDetails.setVisibility(View.VISIBLE);
                    getUserData("Today");
                }
            }
        });

        binding.rbCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.rbAll.setChecked(false);
                    binding.rbToday.setChecked(false);
                    binding.llDate.setVisibility(View.VISIBLE);
                    userArrayList.clear();
                    binding.rvUserDetails.setVisibility(View.GONE);
//                    GetUserDetails.this.notifyAll();

                }
            }
        });

        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rbAll.isChecked()) {
                    DownloadExcel("All", userArrayList);
                } else if (binding.rbToday.isChecked()) {
                    DownloadExcel("Today", userArrayList);
                }
            }
        });

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

    private void DownloadExcel(String all, ArrayList<ModelUser> userArrayList) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String currentDate = df.format(c);
        FileOutputStream fos = null;
        File file = null;
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
        createHeaderRow(sheet, userArrayList);
        createUserData(sheet, userArrayList);
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            Log.e("StrPath", str_path);
            file = new File(str_path, "UserData" + currentDate + "_" + ".xls");
            fos = new FileOutputStream(file);
            Log.e("FilePath", file.getAbsolutePath().toString());
            workbook.write(fos);
            fos.flush();
            fos.close();
            Toast.makeText(GetUserDetails.this, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
//            Intent intentShare = new Intent(Intent.ACTION_SEND);
//            intentShare.setType("application/.xls");
//            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));
//            startActivity(Intent.createChooser(intentShare, "Share the file ..."));


        } catch (IOException e) {
            Log.e("IOEXCEPTION", e.getMessage());
            e.printStackTrace();
        } finally {
        }
    }

    private void createUserData(Sheet sheet, ArrayList<ModelUser> userArrayList) {
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
        for (int i = 0; i < userArrayList.size(); i++) {
            Row row = sheet.createRow(i + 6);
            Cell cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(String.valueOf(i + 1));
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(cellStyle);
            cell1.setCellValue(userArrayList.get(i).getName());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(cellStyle);
            cell2.setCellValue(userArrayList.get(i).getMobile());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(cellStyle);
            cell3.setCellValue(userArrayList.get(i).getUserId());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(cellStyle);
            cell4.setCellValue(userArrayList.get(i).getUserType());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(cellStyle);
            cell5.setCellValue(userArrayList.get(i).getCompanyAddress());
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(cellStyle);
            cell6.setCellValue(userArrayList.get(i).getCompanyEmail());
            Cell cel7 = row.createCell(7);
            cel7.setCellStyle(cellStyle);
            cel7.setCellValue(userArrayList.get(i).getCompanyName());
            Cell cel8 = row.createCell(8);
            cel8.setCellStyle(cellStyle);
            cel8.setCellValue(userArrayList.get(i).getCompanyWebsite());
            Cell cel9 = row.createCell(9);
            cel9.setCellStyle(cellStyle);
            cel9.setCellValue(userArrayList.get(i).getDateOfRegister());
            Cell cel10 = row.createCell(10);
            cel10.setCellStyle(cellStyle);
            cel10.setCellValue(userArrayList.get(i).getUid());


        }
    }

    private void createHeaderRow(Sheet sheet, ArrayList<ModelUser> userArrayList) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String currentDate = df.format(c);
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

        sheet.setColumnWidth(9, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(10, 15 * 256);


        Row row = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell cellFullName = row.createCell(0);

        cellFullName.setCellStyle(cellStyle);
        String date_val = "As on Dated" + currentDate;
        cellFullName.setCellValue(date_val);

        CellRangeAddress cellMerge = new CellRangeAddress(0, 2, 0, 7);
        sheet.addMergedRegion(cellMerge);

        Row row1 = sheet.createRow(3);
        org.apache.poi.ss.usermodel.Cell cellHeading = row1.createCell(0);

        cellHeading.setCellStyle(cellStyle);
        cellHeading.setCellValue("User Details Report");

        CellRangeAddress cellMerge1 = new CellRangeAddress(3, 4, 0, 7);
        sheet.addMergedRegion(cellMerge1);

        Row rowValues = sheet.createRow(5);
        org.apache.poi.ss.usermodel.Cell cellSrNo = rowValues.createCell(0);
        cellSrNo.setCellStyle(cellStyle);
        cellSrNo.setCellValue("Sr No");
        org.apache.poi.ss.usermodel.Cell cellSrNo1 = rowValues.createCell(1);
        cellSrNo1.setCellStyle(cellStyle);
        cellSrNo1.setCellValue("Name");
        org.apache.poi.ss.usermodel.Cell cellSrNo2 = rowValues.createCell(2);
        cellSrNo2.setCellStyle(cellStyle);
        cellSrNo2.setCellValue("Mobile");
        Cell cellSrNo3 = rowValues.createCell(3);
        cellSrNo3.setCellStyle(cellStyle);
        cellSrNo3.setCellValue("User Id");
        Cell cellSrNo4 = rowValues.createCell(4);
        cellSrNo4.setCellStyle(cellStyle);
        cellSrNo4.setCellValue("UserType");
        Cell cellSrNo5 = rowValues.createCell(5);
        cellSrNo5.setCellStyle(cellStyle);
        cellSrNo5.setCellValue("Company Address");
        Cell cellSrNo6 = rowValues.createCell(6);
        cellSrNo6.setCellStyle(cellStyle);
        cellSrNo6.setCellValue("Company Email");
        Cell cellSrNo7 = rowValues.createCell(7);
        cellSrNo7.setCellStyle(cellStyle);
        cellSrNo7.setCellValue("Company Name");
        Cell cellSrNo8 = rowValues.createCell(8);
        cellSrNo8.setCellStyle(cellStyle);
        cellSrNo8.setCellValue("Company Website");

        Cell cellSrNo9 = rowValues.createCell(9);
        cellSrNo9.setCellStyle(cellStyle);
        cellSrNo9.setCellValue("Date of Register");
        Cell cellSrNo10 = rowValues.createCell(10);
        cellSrNo10.setCellStyle(cellStyle);
        cellSrNo10.setCellValue("UID");
    }

    private void getUserData(String date) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        if (date.equals("All")) {
            reference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        if (ds.hasChild("industryPosition")) {
                            if ((ds.child("industryPosition").getValue(Long.class) < 2)) {
                                userArrayList.add(modelUser);
                            }
                        } else if (modelUser.getUserType() != null && !modelUser.getUserType().equals("Admin")) {
                            userArrayList.add(modelUser);
                        }
                    }
                    AdapterUserDetails adapterUserDetails = new AdapterUserDetails(GetUserDetails.this, userArrayList);
                    binding.rvUserDetails.setAdapter(adapterUserDetails);
                    progressDialog.dismiss();
                    binding.etSearch.setVisibility(View.VISIBLE);
                    Bitmap recycler_view_bm = getScreenshotFromRecyclerView(binding.rvUserDetails);
//                    try {
//                        String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//
//                        pdfFile = new File(str_path, "Users.pdf");
//
//                        pdfFile.createNewFile();
//                        FileOutputStream fOut = new FileOutputStream(pdfFile);
//
//                        PdfDocument document = new PdfDocument();
//                        PdfDocument.PageInfo pageInfo = new
//                                PdfDocument.PageInfo.Builder(recycler_view_bm.getWidth(), recycler_view_bm.getHeight(), 1).create();
//                        PdfDocument.Page page = document.startPage(pageInfo);
//                        recycler_view_bm.prepareToDraw();
//                        Canvas c;
//                        c = page.getCanvas();
//                        c.drawBitmap(recycler_view_bm, 0, 0, null);
//                        document.finishPage(page);
//                        document.writeTo(fOut);
//                        document.close();
////            Snackbar snackbar = Snackbar
////                    .make(equipmentsRecordActivityLayout, "PDF generated successfully.", Snackbar.LENGTH_LONG)
////                    .setAction("Open", new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////                            openPDFRecord(pdfFile);
////                        }
////                    });
////
////            snackbar.show();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (date.equals("Today")) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            String currentDate = df.format(c);
            reference.orderByChild("dateOfRegister").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        if (ds.hasChild("industryPosition")) {
                            if ((ds.child("industryPosition").getValue(Long.class) < 2)) {
                                userArrayList.add(modelUser);
                            }
                        } else if (!modelUser.getUserType().equals("Admin")) {
                            userArrayList.add(modelUser);
                        }
                    }
                    AdapterUserDetails adapterUserDetails = new AdapterUserDetails(GetUserDetails.this, userArrayList);
                    binding.rvUserDetails.setAdapter(adapterUserDetails);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}