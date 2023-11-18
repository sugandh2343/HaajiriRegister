package com.skillzoomer_Attendance.com.Adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.skillzoomer_Attendance.com.Activity.EmployeeDashboard;
import com.skillzoomer_Attendance.com.Model.Cell;
import com.skillzoomer_Attendance.com.Model.ColumnHeader;
import com.skillzoomer_Attendance.com.Model.ModelAttendanceEmployee;
import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.Model.ModelMonth;
import com.skillzoomer_Attendance.com.Model.RowHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableViewModelEmployee {
    public static final int MOOD_COLUMN_INDEX = 3;
    public static final int GENDER_COLUMN_INDEX = 4;

    // Constant values for icons
    public static final int SAD = 1;
    public static final int HAPPY = 2;
    public static final int BOY = 1;
    public static final int GIRL = 2;

    // Constant size for dummy data sets
    private Integer COLUMN_SIZE;
    private Integer ROW_SIZE ;
    private ArrayList<ModelLabour> labourList;
    private ArrayList<ModelDate> modelDateArrayList;
    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
    private ArrayList<ModelCompileStatus> ModelCompileStatusArrayList;

    private ArrayList<ModelEmployee> employeeArrayList;
    private ArrayList<ModelAttendanceEmployee> modelAttendanceEmployees;
    private ArrayList<ModelMonth> dateArrayList;

    private String Activity;

    public TableViewModelEmployee() {

    }

    public TableViewModelEmployee(Integer COLUMN_SIZE,
                                  Integer ROW_SIZE,
                                  ArrayList<ModelLabour> labourList,
                                  ArrayList<ModelDate> modelDateArrayList,
                                  ArrayList<ModelLabourStatus> modelLabourStatusArrayList,
                                  ArrayList<ModelCompileStatus> modelCompileStatusArrayList,
                                  String activity) {
        this.COLUMN_SIZE = COLUMN_SIZE;
        this.ROW_SIZE = ROW_SIZE;
        this.labourList = labourList;
        this.modelDateArrayList = modelDateArrayList;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        ModelCompileStatusArrayList = modelCompileStatusArrayList;
        Activity = activity;
    }

    public TableViewModelEmployee(ArrayList<ModelEmployee> employeeArrayList, ArrayList<ModelAttendanceEmployee> modelAttendanceEmployees, ArrayList<ModelMonth> dateArrayList) {
        this.employeeArrayList = employeeArrayList;
        this.modelAttendanceEmployees = modelAttendanceEmployees;
        this.dateArrayList = dateArrayList;
    }

    @NonNull
    private List<RowHeader> getSimpleRowHeaderList() {
        List<RowHeader> list = new ArrayList<>();
        for (int i = 0; i < employeeArrayList.size(); i++) {
            RowHeader header = new RowHeader(String.valueOf(i), employeeArrayList.get(i).getName());
            list.add(header);
        }
//        if(Activity.equals("Attendance")||Activity.equals("Payment")) {
//            for (int i = 0; i < ROW_SIZE; i++) {
//                RowHeader header = new RowHeader(String.valueOf(i), labourList.get(i).getName().toUpperCase(Locale.ROOT));
//                list.add(header);
//            }
//        }else{
//            for(int i=0;i<ROW_SIZE;i++){
//                if(i%2==0){
//                    RowHeader header = new RowHeader(String.valueOf(i), labourList.get(i/2).getName().toUpperCase(Locale.ROOT));
//                    list.add(header);
//                }else{
//                    RowHeader header = new RowHeader(String.valueOf(i), "Payment");
//                    list.add(header);
//                }
//            }
//        }

        return list;
    }


    @NonNull
    private List<ColumnHeader> getRandomColumnHeaderList() {
        List<ColumnHeader> list = new ArrayList<>();
        for(int i=0; i<dateArrayList.size()+1;i++){
            String title = "";
             if (i == 0) {
                title = "Emp Id";
            }  else {
                if(i-1<10){
                    title = ""+0+""+(i);
                }else{
                    title=""+(i);
                }

            }


            ColumnHeader header = new ColumnHeader(String.valueOf(i), title);
            list.add(header);
        }


        return list;
    }


    @NonNull
    private List<List<Cell>> getCellListForSortingTest() {
        List<List<Cell>> list = new ArrayList<>();
        for(int i=0;i< employeeArrayList.size();i++){
            List<Cell> cellList = new ArrayList<>();
            for (int j=0;j<dateArrayList.size()+1;j++){
                String text1 = "";
                if (j%32 == 0) {
                    text1 = employeeArrayList.get(i).getUserId();
                } else{

                        text1=modelAttendanceEmployees.get((i*31)+(j-1)).getStatus();


                }
                Object text = text1;


                // Create dummy id.
                String id = j+ "-" + i;

                Cell cell;

                cell = new Cell(id, text);



                cellList.add(cell);

            }
            list.add(cellList);
        }
//

        return list;
    }



    @NonNull
    public List<List<Cell>> getCellList() {
        return getCellListForSortingTest();
    }

    @NonNull
    public List<RowHeader> getRowHeaderList() {
        return getSimpleRowHeaderList();
    }

    @NonNull
    public List<ColumnHeader> getColumnHeaderList() {
        return getRandomColumnHeaderList();
    }
}
