package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterNestedChild1;
import com.skillzoomer_Attendance.com.Adapter.AdapterNestedClass;
import com.skillzoomer_Attendance.com.Model.DateModel;
import com.skillzoomer_Attendance.com.Model.ModelAttendance;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.Model.ModelShowAttendance;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityNestedRecyclerViewBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NestedRecyclerView extends AppCompatActivity {
//    ActivityNestedRecyclerViewBinding binding;
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
//    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
//    int countLoop = 0;
//    private ProgressDialog progressDialog;
//    private String status = "";
//    String currentDate;
//    private Cell cell = null;
//    ArrayList<ModelLabour> labourByType ;
//    ArrayList<ModelDate> shortDateList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding=ActivityNestedRecyclerViewBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
////        toolbarBinding = binding.toolbarLayout;
////        toolbarBinding.heading.setText("Show Attendance");
////        toolbarBinding.back.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                onBackPressed();
////            }
////        });
//        firebaseAuth = FirebaseAuth.getInstance();
//        siteArrayList = new ArrayList<>();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle(getResources().getString(R.string.please_wait));
//        progressDialog.setCanceledOnTouchOutside(false);
//        Workbook workbook = new HSSFWorkbook();
//        sheet = workbook.createSheet("Attendance");
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
//        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        modelLabourStatusArrayList = new ArrayList<>();
//        labourByType = new ArrayList<>();
//
//
//        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
//        userType = sharedpreferences.getString("userDesignation", "");
//        siteName = sharedpreferences.getString("siteName", "");
//        siteId = sharedpreferences.getLong("siteId", 0);
//        userName = sharedpreferences.getString("userName", "");
//        Intent intent = getIntent();
//        status = intent.getStringExtra("Activity");
//        if (!status.equals("")) {
//            if (status.equals("ShowAttendance")) {
//                binding.btnDownloadReport.setVisibility(View.GONE);
//            } else {
//                binding.btnDownloadReport.setVisibility(View.VISIBLE);
//            }
//        }
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
////            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 86400000);
//            datePickerDialog.show();
//            String myFormat = "dd/MM/yyyy"; //In which you need put here
//
//        });
//        NestedRecyclerView.SpinnerAdapter spinnerAdapter = new NestedRecyclerView.SpinnerAdapter();
//        binding.spinnerSearchType.setAdapter(spinnerAdapter);
//        NestedRecyclerView.SpinnerAdapter1 spinnerAdapter1 = new NestedRecyclerView.SpinnerAdapter1();
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
//                progressDialog.setMessage("Data Loading");
//                progressDialog.show();
//
//                modelLabourStatusArrayList.clear();
//                checkForAttendance();
//
//            }
//        });
//        binding.btnDownloadReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final AlertDialog.Builder alert = new AlertDialog.Builder(NestedRecyclerView.this);
//                View mView = getLayoutInflater().inflate(R.layout.layout_select_format, null);
//                Button btn_excel = (Button) mView.findViewById(R.id.btn_excel);
//                Button btn_pdf = (Button) mView.findViewById(R.id.btn_pdf);
//                btn_excel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (modelDateArrayList.size() == 0) {
//                            Toast.makeText(NestedRecyclerView.this, "Select Date Range", Toast.LENGTH_SHORT).show();
//                        } else {
//
//                            DownloadExcel(labourList, modelDateArrayList, modelLabourStatusArrayList, labourList,shortDateList);
//                        }
//
//                    }
//                });
//
//                alert.setView(mView);
//                final AlertDialog alertDialog = alert.create();
//                alertDialog.setCanceledOnTouchOutside(true);
//                btn_pdf.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (siteId > 0) {
//                            progressDialog.show();
//
//                        }
//                    }
//                });
//
//
//                alertDialog.show();
//            }
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
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                Boolean f = true;
//                try {
//                    Date fDate = dateFormat.parse(fromDate);
//                    Date tDate = dateFormat.parse(toDate);
//                    if (tDate.before(fDate)) {
//                        Toast.makeText(NestedRecyclerView.this, "StartDate cannot be after End Date", Toast.LENGTH_SHORT).show();
//                        f = false;
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                if (fromDate.equals("")) {
//                    Toast.makeText(NestedRecyclerView.this, "Enter Start Date", Toast.LENGTH_LONG).show();
//                } else if (toDate.equals("")) {
//                    Toast.makeText(NestedRecyclerView.this, "Enter End Date", Toast.LENGTH_SHORT).show();
//                } else if (f) {
//                    modelLabourStatusArrayList.clear();
//                    getLabourList(fromDate, toDate);
//                }
//            }
//        });


    }

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
//    private void DownloadExcel(ArrayList<ModelLabour> labourList, ArrayList<ModelDate> dateModelArrayList, ArrayList<ModelLabourStatus> modelLabourStatusArrayList, ArrayList<ModelLabour> list, ArrayList<ModelDate> shortDateList) {
//        Workbook workbook = new HSSFWorkbook();
//        Sheet sheet = workbook.createSheet();
//        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
//        createHeaderRow(sheet, modelDateArrayList,shortDateList);
//        createLAbourData(sheet, labourList);
//        createAttendanceData(sheet, labourList, modelDateArrayList, modelLabourStatusArrayList);
//        createFooter(sheet, labourList, modelDateArrayList, modelLabourStatusArrayList);
//        FileOutputStream fos = null;
//        File file = null;
//        try {
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
////            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
////            Log.e("Directory",directory.getAbsolutePath());
//            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            Log.e("StrPath", str_path);
//            file = new File(str_path, "AttendanceReport_"+siteName +"_"+currentDate+ ".xls");
//
//            fos = new FileOutputStream(file);
//            Log.e("FilePath", file.getAbsolutePath().toString());
//            workbook.write(fos);
//        } catch (IOException e) {
//            Log.e("IOEXCEPTION", e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    Log.e("fos", "flush");
//                    fos.flush();
//                    fos.close();
//                } catch (IOException e) {
//                    Log.e("IOEXCEPTION1", e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//            Toast.makeText(NestedRecyclerView.this, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
//        }
////        Intent intent = new Intent(Intent.ACTION_VIEW);
////        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
////        startActivity(intent);
//
//    }
//
//    private void createFooter(Sheet sheet, ArrayList<ModelLabour> labourList,
//                              ArrayList<ModelDate> modelDateArrayList, ArrayList<ModelLabourStatus> modelLabourStatusArrayList) {
//        int totalSkilledCount=0,totalUnskilledCount=0,presentSkilled=0,presentUnskilled=0;
//        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
//        Font fontPresent = sheet.getWorkbook().createFont();
//        fontPresent.setColor(HSSFColor.BLACK.index);
//        fontPresent.setFontHeightInPoints((short) 12);
//        cellStylePresent.setBorderBottom((short) 12);
//        cellStylePresent.setBorderTop((short) 12);
//        cellStylePresent.setBorderLeft((short) 12);
//        cellStylePresent.setBorderRight((short) 12);
//        cellStylePresent.setFont(fontPresent);
//        sheet.createRow(labourList.size()+6);
//        for (int i = 0; i < modelLabourStatusArrayList.size(); i = i + labourList.size()) {
//            presentSkilled=0;
//            presentUnskilled=0;
//            for (int j = 0; j < labourList.size(); j++) {
//
//                Log.e("CheckStatus", "" + modelLabourStatusArrayList.get(i + j).getStatus().equals("P"));
//                if(modelLabourStatusArrayList.get(i + j).getStatus().equals("P")){
//                    if(modelLabourStatusArrayList.get(i+j).getType().equals("Skilled")){
//                        presentSkilled+=1;
//                    }else{
//                        presentUnskilled+=1;
//                    }
//                }
//
//                if(j==labourList.size()-1){
//                    int y= i/labourList.size();
//                    Row rowPresentSkilled=sheet.getRow(labourList.size()+6);
//                    Cell Status=rowPresentSkilled.createCell(y+4);
//                    Status.setCellStyle(cellStylePresent);
//                    Status.setCellValue(""+presentSkilled+"/"+presentUnskilled);
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
//
//    }
//
//    private void createAttendanceData(Sheet sheet, ArrayList<ModelLabour> labourList,
//                                      ArrayList<ModelDate> modelDateArrayList,
//                                      ArrayList<ModelLabourStatus> modelLabourStatusArrayList) {
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        Font font = sheet.getWorkbook().createFont();
//        font.setColor(HSSFColor.RED.index);
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setBorderBottom((short) 12);
//        cellStyle.setBorderTop((short) 12);
//        cellStyle.setBorderLeft((short) 12);
//        cellStyle.setBorderRight((short) 12);
//        cellStyle.setFont(font);
//
//        CellStyle cellStylePresent = sheet.getWorkbook().createCellStyle();
//        Font fontPresent = sheet.getWorkbook().createFont();
//        fontPresent.setColor(HSSFColor.BLUE.index);
//        fontPresent.setFontHeightInPoints((short) 12);
//        cellStylePresent.setBorderBottom((short) 12);
//        cellStylePresent.setBorderTop((short) 12);
//        cellStylePresent.setBorderLeft((short) 12);
//        cellStylePresent.setBorderRight((short) 12);
//        cellStylePresent.setFont(fontPresent);
//        int countPresent = 0, cloneJ = 0, k = 0;
//        for (int i = 0; i < modelLabourStatusArrayList.size(); i = i + labourList.size()) {
//            countPresent = 0;
//            for (int j = 0; j < labourList.size(); j++) {
//                cloneJ = j;
//                Row row = sheet.getRow(j + 6);
//                k = i / labourList.size();
//                Log.e("ValueOFI", "i=:" + i + "Value:" + (k + 4));
//                Cell cell = row.createCell(k + 4);
//                Log.e("CheckStatus", "" + modelLabourStatusArrayList.get(i + j).getStatus().equals("P"));
//                if (modelLabourStatusArrayList.get(i + j).getStatus().equals("P")) {
//                    countPresent += 1;
//                    cell.setCellStyle(cellStylePresent);
//                } else {
//                    cell.setCellStyle(cellStyle);
//                }
//
//                cell.setCellValue(modelLabourStatusArrayList.get(i + j).getStatus());
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
//        font.setColor((short) getColor(R.color.black));
//        font.setFontHeightInPoints((short) 12);
//        cellStyle.setFont(font);
//        cellStyle.setBorderBottom((short) 12);
//        cellStyle.setBorderTop((short) 12);
//        cellStyle.setBorderLeft((short) 12);
//        cellStyle.setBorderRight((short) 12);
//        for (int i = 0; i < labourList.size(); i++) {
//            Row row = sheet.createRow(i + 6);
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
//        cellStyle.setBorderBottom((short) 12);
//        cellStyle.setBorderTop((short) 12);
//        cellStyle.setBorderLeft((short) 12);
//        cellStyle.setBorderRight((short) 12);
//
//
//        Row row = sheet.createRow(0);
//        Cell cellFullName = row.createCell(0);
//
//        cellFullName.setCellStyle(cellStyle);
////        cellFullName.setCellValue("Attendance Report");
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
//        cellHeading.setCellValue("Attendance Report");
//
//        CellRangeAddress cellMerge1 = new CellRangeAddress(3, 4, 0, dateModelArrayList.size() + 4);
//        sheet.addMergedRegion(cellMerge1);
//
//        Row rowValues = sheet.createRow(5);
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
//
//        for (int i = 0; i < shortDateList.size(); i++) {
//            Cell dateNo = rowValues.createCell(i + 4);
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
//                            getLabourList(currentDate11, currentDate1);
//
//                        } else {
//                            progressDialog.dismiss();
//                            binding.btnShow.setVisibility(View.GONE);
//                            binding.llDate.setVisibility(View.GONE);
//                            binding.btnToday.setVisibility(View.GONE);
//                            binding.btnCustom.setVisibility(View.GONE);
//                            binding.txtMessage.setText("You have not uploaded any attendance for today");
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
//    private void getLabourList(String currentDate, String currentDate1) {
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
//                    getDateRange(currentDate, currentDate1, labourList);
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
//    private void getDateRange(String fromDate, String toDate, ArrayList<ModelLabour> labourList) throws ParseException {
//        modelDateArrayList = new ArrayList<>();
//        shortDateList=new ArrayList<>();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        Boolean f = true;
//        Date fDate = null, tDate = null;
//        Log.e("callFrom", fromDate);
//        Log.e("callFrom", toDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        Calendar c = Calendar.getInstance();
//        c.setTime(sdf.parse(fromDate));
//        try {
//            fDate = dateFormat.parse(fromDate);
//            tDate = dateFormat.parse(toDate);
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
//                Log.e("modelLabourStatus", "" + modelLabourStatusArrayList.size());
//                for (int j = 0; j < labourList.size(); j++) {
//                    Log.e("date", modelDateArrayList.get(i).getDate());
//                    Log.e("date", labourList.get(j).getLabourId());
//                    getAttendanceList(modelDateArrayList.get(i).getDate(), labourList.get(j).getLabourId(),labourList.get(j).getType());
//                }
//            }
//            Log.e("modelDateArrayList", "" + modelDateArrayList.size());
//            Log.e("modelLabourStatus", "AfterLoop" + modelLabourStatusArrayList.size());
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
//                            ModelLabourStatus modelLabourStatus = new ModelLabourStatus(date, labourId, "P",type);
//                            addToarray(modelLabourStatus);
////                            Log.e("model1234", "ID" + modelLabourStatus.getLabourId() + "\t status" + modelLabourStatus.getStatus());
//
//
////                            Log.e("Size11111",""+labourList.size());
////                            Log.e("Size22222",""+modelAttendances.size());
////                            Log.e("Size33333",""+modelDateArrayList.size());
////                            if(modelDateArrayList.size()>0) {
////                                AdapterShowAttendance adapterShowAttendance = new AdapterShowAttendance(NestedRecyclerView.this ,
////                                        labourList,
////                                        modelAttendances ,
////                                        modelDateArrayList);
////                                binding.rvShowAttendance.setAdapter(adapterShowAttendance);
////                            }
//
//                        } else {
//                            ModelLabourStatus modelLabourStatus = new ModelLabourStatus(date, labourId, "A",type);
//                            addToarray(modelLabourStatus);
//
//
//                        }
//
////                        for(int i=0;i<modelLabourStatusArrayList.size();i++) {
////                            Log.e("modelLabourStatusArrayList" , "" + modelLabourStatusArrayList.get(i).getLabourId()
////                            +"Status"+modelLabourStatusArrayList.get(i).getStatus());
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
//    private void addToarray(ModelLabourStatus modelLabourStatus) {
//        countLoop++;
//
//
//
//        modelLabourStatusArrayList.add(modelLabourStatus);
//        Log.e("modelLabourStatus", "Count:" + countLoop+"\t Date:"+ modelLabourStatus.getDate()+"\t Id:"+modelLabourStatus.getLabourId()+"\t Status:"+modelLabourStatus.getStatus());
////        Log.e("modelLabourStatus", "Size:" + modelLabourStatusArrayList.size());
//        if (countLoop == labourList.size() * modelDateArrayList.size()) {
//            progressDialog.dismiss();
//            AdapterNestedClass adapterShowAttendance = new AdapterNestedClass(NestedRecyclerView.this,
//                    labourList,
//                    modelLabourStatusArrayList,
//                    modelDateArrayList);
//            binding.rvShowAttendance.setAdapter(adapterShowAttendance);
//            AdapterNestedChild1 adapterNestedChild1 = new AdapterNestedChild1(NestedRecyclerView.this,modelLabourStatusArrayList,modelDateArrayList,labourList);
//            binding.rvStatus.setAdapter(adapterNestedChild1);
//        } else {
//            if (labourList.size() < 1) {
//                progressDialog.dismiss();
//                binding.rvShowAttendance.setVisibility(View.GONE);
//                binding.txtMessage.setText("No Labours Added");
//                binding.txtMessage.setVisibility(View.VISIBLE);
//
//            }
//        }
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
}