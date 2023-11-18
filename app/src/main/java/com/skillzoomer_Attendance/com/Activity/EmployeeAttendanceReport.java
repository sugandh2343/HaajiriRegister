package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterEmployeeAttendance;
import com.skillzoomer_Attendance.com.Model.ModelAttendanceEmployee;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.Model.ModelMonth;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEmployeeAttendanceReportBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EmployeeAttendanceReport extends AppCompatActivity {
    ActivityEmployeeAttendanceReportBinding binding;
    DatabaseReference reference;
    HashMap<String, Object> monthHashMap;
    HashMap<String, Object> yearHashMap;
    private ArrayList<ModelMonth> monthArrayList;
    private ArrayList<ModelMonth> yearArraylist;
    private ArrayList<ModelMonth> dateArrayList;
    String selectedMonth, selectedYear;
    int noOfdays;
    private ArrayList<ModelEmployee> employeeArrayList;
    private String dnl_file_type = "";
    private ArrayList<ModelAttendanceEmployee> attendanceEmployees;
    private ArrayList<ModelAttendanceEmployee> attendanceEmployeesSorted;
    private ArrayList<ModelAttendanceEmployee> attendanceEmployeesOff;
    private ArrayList<String> weekOffList;
    FirebaseAuth firebaseAuth;
    int countArray=0;
    long AmountRuleExcel=0,AmountRulePdf=0;
    int amount = 0;
    int AmountTemp = 0;
    FileOutputStream fos = null;
    AlertDialog alertDialogPaymentConfirm = null;
    File file = null;
    long siteId;
    String siteName;
    String currentDate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmployeeAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        Log.e("Snapshot", String.valueOf(getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("siteId",0)));
        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("siteId",0)>0){
            siteId=getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("siteId",0);
            siteName=getSharedPreferences("UserDetails",MODE_PRIVATE).getString("siteName","");
        }else{
            siteId=intent.getLongExtra("siteId",0);
            siteName=intent.getStringExtra("siteName");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        String timestamp = "" + System.currentTimeMillis();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        String[] separataed = currentDate.split("-");
        selectedMonth = separataed[1];
        selectedYear = separataed[2];
        binding.txtMonth.setText(separataed[1]);
        binding.txtYear.setText(separataed[2]);
        monthArrayList = new ArrayList<>();
        yearArraylist = new ArrayList<>();
        dateArrayList = new ArrayList<>();
        employeeArrayList = new ArrayList<>();
        attendanceEmployees = new ArrayList<>();
        attendanceEmployeesOff = new ArrayList<>();
        attendanceEmployeesSorted = new ArrayList<>();
        weekOffList = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrUid", "")).child("Industry")
                .child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", ""))
                .child("Site").child(String.valueOf(siteId));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                weekOffList.clear();
                Log.e("Snapshot1216",""+snapshot.exists());
                Log.e("Snapshot1216",""+siteId);
                if(snapshot.exists()){
                    if (snapshot.child("Sunday").getValue(Boolean.class)) {
                        weekOffList.add(new String("Sunday"));
                    }  if (snapshot.child("Monday").getValue(Boolean.class)) {
                        weekOffList.add(new String("Monday"));
                    }  if (snapshot.child("Tuesday").getValue(Boolean.class)) {
                        weekOffList.add(new String("Tuesday"));
                    }  if (snapshot.child("Wednesday").getValue(Boolean.class)) {
                        weekOffList.add(new String("Wednesday"));
                    }  if (snapshot.child("Thursday").getValue(Boolean.class)) {
                        weekOffList.add(new String("Thursday"));
                    }  if (snapshot.child("Friday").getValue(Boolean.class)) {
                        weekOffList.add(new String("Friday"));
                    }  if (snapshot.child("Saturday").getValue(Boolean.class)) {
                        weekOffList.add(new String("Saturday"));
                    }
                }
                Log.e("WeekOff1216",""+weekOffList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("Attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthHashMap = new HashMap<>();
                yearHashMap = new HashMap<>();
                monthArrayList.clear();
                yearArraylist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    yearHashMap.put(ds.getKey(), ds.getKey());
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        Log.e("KEYVALUE", ds.getKey());
                        monthHashMap.put(ds1.getKey(), ds1.getKey());
                    }
                }
                for (HashMap.Entry<String, Object> entry : yearHashMap.entrySet()) {
                    ModelMonth modelMonth = new ModelMonth();
                    modelMonth.setMonth(entry.getKey());
                    yearArraylist.add(modelMonth);

                }
                for (HashMap.Entry<String, Object> entry : monthHashMap.entrySet()) {
                    ModelMonth modelMonth = new ModelMonth();
                    modelMonth.setMonth(entry.getKey());
                    monthArrayList.add(modelMonth);

                }
                monthArrayList.add(0, new ModelMonth("Change month"));
                yearArraylist.add(0, new ModelMonth("Change Year"));
                MonthSpinnerAdapter monthSpinnerAdapter = new MonthSpinnerAdapter();
                binding.spinnerMonth.setAdapter(monthSpinnerAdapter);
                YearSpinnerAdapter yearSpinnerAdapter = new YearSpinnerAdapter();
                binding.spinnerYear.setAdapter(yearSpinnerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getAttendanceInfo();

        binding.spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedMonth = monthArrayList.get(i).getMonth();
                    getAttendanceInfo();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedYear = yearArraylist.get(i).getMonth();
                    reference.child("Attendance").child(selectedYear).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (HashMap.Entry<String, Object> entry : monthHashMap.entrySet()) {
                                ModelMonth modelMonth = new ModelMonth();
                                modelMonth.setMonth(entry.getKey());
                                monthArrayList.add(modelMonth);

                            }

                            MonthSpinnerAdapter monthSpinnerAdapter = new MonthSpinnerAdapter();
                            binding.spinnerMonth.setAdapter(monthSpinnerAdapter);
                            selectedMonth = monthArrayList.get(0).getMonth();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    class MonthSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return monthArrayList.size();
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
            designationText.setText(monthArrayList.get(position).getMonth());


            return row;
        }
    }

    class YearSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return yearArraylist.size();
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
            designationText.setText(yearArraylist.get(position).getMonth());


            return row;
        }
    }
    private void getAttendanceInfo() {
        Log.e("Selected", selectedMonth);
        reference.child("Attendance").child(selectedYear).child(selectedMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateArrayList.clear();
                if (selectedMonth.equals("Jan") || selectedMonth.equals("Mar") || selectedMonth.equals("May") || selectedMonth.equals("Jul") ||
                        selectedMonth.equals("Aug") || selectedMonth.equals("Oct") || selectedMonth.equals("Dec")) {
                    noOfdays = 31;
                } else if (selectedMonth.equals("Apr") || selectedMonth.equals("Jun") || selectedMonth.equals("Sep") || selectedMonth.equals("Nov")) {
                    noOfdays = 30;
                } else if (selectedMonth.equals("Feb") && Integer.parseInt(selectedYear) % 4 == 0) {
                    noOfdays = 29;
                } else {
                    noOfdays = 28;
                }
                for (int i = 1; i <= noOfdays; i++) {
                    String dateselected = "";
                    if (i < 10) {
                        dateselected = "" + 0 + "" + i;
                    } else {
                        dateselected = "" + i;
                    }
                    String date = dateselected + "-" + selectedMonth + "-" + selectedYear;
                    Log.e("Selected", date);
                    dateArrayList.add(new ModelMonth(date));

                }
                Calendar c = Calendar.getInstance();
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy");

                String dayOfDate = "";
                Boolean dayOffresult = false;
                DateTimeFormatter dtfInput = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dtfInput = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                }
                DateTimeFormatter dtfOutput = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dtfOutput = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    dayOfDate = LocalDate.parse(dateArrayList.get(0).getMonth(), dtfInput).format(dtfOutput);

                }
                int blankCount=0;
                Log.e("DayOff",dayOfDate);
                if(dayOfDate.equals("Sunday")){
                    blankCount=0;
                }else if(dayOfDate.equals("Monday")){
                    blankCount=1;
                }else if(dayOfDate.equals("Tuesday")){
                    blankCount=2;
                }else if(dayOfDate.equals("Wednesday")){
                    blankCount=3;
                }else if(dayOfDate.equals("Thursday")){
                    blankCount=4;
                }else if(dayOfDate.equals("Friday")){
                    blankCount=5;
                }else if(dayOfDate.equals("Saturday")){
                    blankCount=6;
                }



                for(int i=0;i<blankCount;i++){
                    dateArrayList.add(i,new ModelMonth("-"));
                }


                if(dateArrayList.size()%7>0){
                    int CountEnd=7-(dateArrayList.size()%7);
                    for(int i=0;i<CountEnd;i++){
                        dateArrayList.add(new ModelMonth("-"));
                    }
                }
                Log.e("DATEBEFORE",""+dateArrayList.size());

                for(int i=0;i<dateArrayList.size();i++){
                    Log.e("DATETETFDF",dateArrayList.get(i).getMonth());
                }
                int count=0;
                countArray=0;
                attendanceEmployees.clear();
                for (int i = 0; i < dateArrayList.size(); i++) {
                    count++;
                    if(dateArrayList.get(i).getMonth().equals("-")){
                        Log.e("Datejhfghufj","----"+count);
                        addToArray("-","-","-","-",i);
                    }else{
                        getAttendanceStatus(dateArrayList.get(i).getMonth(),i);
                    }

                }

//                reference.child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        employeeArrayList.clear();
//                        attendanceEmployeesOff.clear();
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            ModelEmployee modelEmployee = ds.getValue(ModelEmployee.class);
//                            employeeArrayList.add(modelEmployee);
//                        }
//                        int count=0;
//                        countArray=0;
//                        for (int i = 0; i < dateArrayList.size(); i++) {
//                                count++;
//                                if(dateArrayList.get(i).getMonth().equals("-")){
//                                    addToArray(dateArrayList.get(i).getMonth(),"-","-","-",i);
//                                }else{
//                                    getAttendanceStatus(dateArrayList.get(i).getMonth(),i);
//                                }
//
//                            }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getAttendanceStatus(String date, int count) {

        attendanceEmployeesSorted.clear();
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
        reference1.child(firebaseAuth.getUid()).child("Attendance").child(selectedYear).child(selectedMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Calendar c = Calendar.getInstance();
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy");

                    String dayOfDate = "";
                    Boolean dayOffresult = false;
                    DateTimeFormatter dtfInput = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dtfInput = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
                    }
                    DateTimeFormatter dtfOutput = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dtfOutput = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.e("YGDTHDYTD", "" + date + "-" + LocalDate.parse(date, dtfInput).format(dtfOutput) + "WOOOOO__--__" + weekOffList.size());
                        dayOfDate = LocalDate.parse(date, dtfInput).format(dtfOutput);

                    }

                    // yourdate is an object of type Date
//                if(weekOffList.contains(dayOfDate)){
//
//                }

                    for (int j = 0; j < weekOffList.size(); j++) {
                        if (dayOfDate.equals(weekOffList.get(j))) {
                            dayOffresult = true;

                        }
                    }
                Log.e("WeekOff",""+dayOffresult);
                if(dayOffresult){
                        if(snapshot.child(date).exists()){
                            addToArray(date,"","","CO",count);
                        }else{

                            addToArray(date,"","","WO",count);
                        }

                    }else if(snapshot.child(date).exists()){
                        String status=snapshot.child(date).child("status").getValue(String.class);
                            if(status.equals("P")){
                                addToArray(date,"","","P",count);
                            }else if(status.equals("Pending")){
                                addToArray(date,"","","IC",count);
                            }else if(status.equals("Leave")){
                                if(snapshot.child(date).child("leaveStatus").getValue(String.class).equals("Approved")){
                                    String leaveType=snapshot.child(date).child("leaveType").getValue(String.class);
                                    addToArray(date,"","","Le",count);
                                }else{
                                    addToArray(date,"","","LR",count);
                                }
                            }else{
                                addToArray(date,"","","A",count);
                            }

                    }else{
                    java.text.SimpleDateFormat dateFormat1 = new java.text.SimpleDateFormat("dd-MMM-yyyy");
                    Boolean f = true;
                    try {
                        Date fDate = dateFormat1.parse(date);
                        Date tDate = dateFormat1.parse(currentDate);
                        if (tDate.before(fDate)) {
                            addToArray(date,"","","-",count);
                            f = false;
                        }else{
                            addToArray(date,"","","A",count);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//
    }
    private void addToArray(String date, String userId, String name, String nc, int count) {
        Log.e("CHECK",date+"--"+userId+"-"+name+"--"+nc+"----"+count);
//
        attendanceEmployees.add(new ModelAttendanceEmployee(date, userId, name, nc,count));
        Log.e("ATTTSGHCGHDF",""+attendanceEmployees.size()+"Date::::"+dateArrayList.size());






//

        if (attendanceEmployees.size() ==dateArrayList.size()) {
            for(int i=0;i<attendanceEmployees.size();i++){
                Log.e("ghfgjhb","count::::"+attendanceEmployees.get(i).getCount()+"Date:"+attendanceEmployees.get(i).getDate());
            }


//
            Collections.sort(attendanceEmployees);
            for (ModelAttendanceEmployee str : attendanceEmployees) {
                Log.e("MDOGUIYF",""+str.toString());
            }
//            Log.e("TestAtt", "Sno\tEmpId\tEmpName\tDate\tStatus" + attendanceEmployees.size()+"OFF---"+attendanceEmployeesSorted.size());
            for (int i = 0; i < attendanceEmployees.size(); i++) {
//                Log.e("TestAtt1", "" +attendanceEmployeesSorted.get(i).getCount() + "\t" + attendanceEmployeesSorted.get(i).getEmpId() + "\t" + attendanceEmployeesSorted.get(i).getEmpNmae()
//                        + "\t" + attendanceEmployeesSorted.get(i).getDate() + "\t" + attendanceEmployeesSorted.get(i).getStatus());
                Log.e("TestAtt1", "" +attendanceEmployees.get(i).getCount() + "\t" + attendanceEmployees.get(i).getEmpId() + "\t" + attendanceEmployees.get(i).getEmpNmae()
                        + "\t" + attendanceEmployees.get(i).getDate() + "\t" + attendanceEmployees.get(i).getStatus());

            }
            AdapterEmployeeAttendance adapterEmployeeAttendance=new AdapterEmployeeAttendance(EmployeeAttendanceReport.this,attendanceEmployees);
            binding.rvCalender.setAdapter(adapterEmployeeAttendance);

        }

    }
}