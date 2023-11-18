package com.skillzoomer_Attendance.com.Adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.skillzoomer_Attendance.com.Model.Cell;
import com.skillzoomer_Attendance.com.Model.ColumnHeader;
import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.Model.RowHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableViewModel {
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

    private String Activity;
    // Drawables
    public TableViewModel() {
        // initialize drawables

    }


    public TableViewModel(Integer COLUMN_SIZE, Integer ROW_SIZE, ArrayList<ModelLabour> labourList, ArrayList<ModelDate> modelDateArrayList,
                          ArrayList<ModelLabourStatus> modelLabourStatusArrayList, ArrayList<ModelCompileStatus> modelCompileStatusArrayList, String activity) {
        this.COLUMN_SIZE = COLUMN_SIZE;
        this.ROW_SIZE = ROW_SIZE;
        this.labourList = labourList;
        this.modelDateArrayList = modelDateArrayList;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        ModelCompileStatusArrayList = modelCompileStatusArrayList;
        Activity = activity;
    }

    @NonNull
    private List<RowHeader> getSimpleRowHeaderList() {
        List<RowHeader> list = new ArrayList<>();
        if(Activity.equals("Attendance")||Activity.equals("Payment")) {
            for (int i = 0; i < ROW_SIZE; i++) {
                RowHeader header = new RowHeader(String.valueOf(i), labourList.get(i).getName().toUpperCase(Locale.ROOT));
                list.add(header);
            }
        }else{
            for(int i=0;i<ROW_SIZE;i++){
                if(i%2==0){
                    RowHeader header = new RowHeader(String.valueOf(i), labourList.get(i/2).getName().toUpperCase(Locale.ROOT));
                    list.add(header);
                }else{
                    RowHeader header = new RowHeader(String.valueOf(i), "Payment");
                    list.add(header);
                }
            }
        }

        return list;
    }


    @NonNull
    private List<ColumnHeader> getRandomColumnHeaderList() {
        List<ColumnHeader> list = new ArrayList<>();
        if(Activity.equals("Attendance")||Activity.equals("Payment")) {

            for (int i = 0; i < COLUMN_SIZE; i++) {
                String title = "";
                if (i == 0) {
                    title = "LabourId";
                } else if (i == 1) {
                    title = "Wages";
                } else if (i == 2 && Activity.equals("Attendance")) {
                    title = "Total Attendance";
                } else if (i == 2 && Activity.equals("Payment")) {
                    title = "Up to Date Payment";
                } else {
                    title = modelDateArrayList.get(i - 3).getDate();
                }


                ColumnHeader header = new ColumnHeader(String.valueOf(i), title);
                list.add(header);
            }
        }else {
            for (int i = 0; i < COLUMN_SIZE; i++) {
                String title = "";
                if (i == 0) {
                    title = "LabourId";
                } else if (i == 1) {
                    title = "Wages";
                } else if (i == 2) {
                    title = "Total Attendance";
                } else if (i == 3) {
                    title = "Up to Date Payment";
                }else if(i==4){
                    title = "Payable Amount";
                } else {
                    title = modelDateArrayList.get(i - 5).getDate();
                }
                ColumnHeader header = new ColumnHeader(String.valueOf(i), title);
                list.add(header);

            }
        }

        return list;
    }


    @NonNull
    private List<List<Cell>> getCellListForSortingTest() {
        List<List<Cell>> list = new ArrayList<>();
        if(Activity.equals("Attendance")||Activity.equals("Payment")) {
            Log.e("MLSTV", "" + modelLabourStatusArrayList.size());

            for (int i = 0; i < ROW_SIZE; i++) {
                List<Cell> cellList = new ArrayList<>();
                for (int j = 0; j < COLUMN_SIZE; j++) {
                    String text1 = "";
                    if (j == 0) {
                        text1 = labourList.get(i).getLabourId();
                    } else if (j == 1) {
                        text1 = String.valueOf(labourList.get(i).getWages());
                    } else if(j==2 && Activity.equals("Attendance")){
                        float countPresent=0;
                        float countHalf=0;
                        float countTotal=0;
                        for(int k=i;k<modelLabourStatusArrayList.size();k=k+labourList.size()){
                            if(modelLabourStatusArrayList.get(k).getStatus().equals("P")){
                                countPresent++;
                            }else{
                                if(modelLabourStatusArrayList.get(k).getStatus().equals("P/2")){
                                    countHalf++;
                                }

                            }

                        }
                        countTotal=countPresent+(countHalf/2);
                        text1=String.valueOf(countTotal)+"/"+String.valueOf(modelDateArrayList.size());
                    }else if(j==2 && Activity.equals("Payment")){
                        int sumAmt=0;
                        for(int k=i;k<modelLabourStatusArrayList.size();k=k+labourList.size()){
                            if(!modelLabourStatusArrayList.get(k).getStatus().equals("0")){
                               sumAmt=sumAmt+Integer.parseInt(modelLabourStatusArrayList.get(k).getStatus());
                            }
                        }
                        text1=String.valueOf(sumAmt);
                    }
                    else {
                        Log.e("Value","i::"+i+"J-2::"+(j-3)+"Size:"+labourList.size()+
                                " Status:"+modelLabourStatusArrayList.get((((j-3) * labourList.size()) + (i))).getStatus());
                        text1 = modelLabourStatusArrayList.get((((j-3) * labourList.size()) + (i))).getStatus();
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
        }
        else{
            for (int i = 0; i < ROW_SIZE; i++) {
                List<Cell> cellList = new ArrayList<>();


                for (int j = 0; j < COLUMN_SIZE; j++) {
                    String text1 = "";
                    if(j==0&& i%2==0){
                        text1 = labourList.get(i/2).getLabourId();
                    }else if (j==1 && i%2==0) {
                        text1 = String.valueOf(labourList.get(i/2).getWages());
                    }else if(j==2 && i%2==0){
                        float countTotal=0;
                        float countPresent=0;
                        float countHalf=0;

                        for(int k=i/2;k<ModelCompileStatusArrayList.size();k=k+labourList.size()){
                            if(ModelCompileStatusArrayList.get(k).getStatus().equals("P")){
                                countPresent++;
                            }else{
                                if(ModelCompileStatusArrayList.get(k).getStatus().equals("P/2")){
                                    countHalf++;
                                }

                            }
                        }
                        countTotal=countPresent+(countHalf/2);
                        text1=String.valueOf(countTotal)+"/"+String.valueOf(modelDateArrayList.size());
                    }else if(j==3 && (i%2)==0){
                        int sumAmt=0;
                        for(int k=i/2;k<ModelCompileStatusArrayList.size();k=k+labourList.size()){
                            if(!ModelCompileStatusArrayList.get(k).getStatus().equals("0")){
                                sumAmt=sumAmt+Integer.parseInt(ModelCompileStatusArrayList.get(k).getAmount());
                            }
                        }
                        text1=String.valueOf(sumAmt);
                    }else if(j==4 && i%2==0){
                        float countTotal=0;
                        float countPresent=0;
                        float countHalf=0;
                        int sumAmt=0;

                        for(int k=i/2;k<ModelCompileStatusArrayList.size();k=k+labourList.size()){
                            if(ModelCompileStatusArrayList.get(k).getStatus().equals("P")){
                                countPresent++;
                            }else{
                                if(ModelCompileStatusArrayList.get(k).getStatus().equals("P/2")){
                                    countHalf++;
                                }


                            }
                            if(!ModelCompileStatusArrayList.get(k).getStatus().equals("0")){
                                sumAmt=sumAmt+Integer.parseInt(ModelCompileStatusArrayList.get(k).getAmount());
                            }
                        }
                        Log.e("CCCPPP",""+countPresent);
                        Log.e("CCCPPP",""+countHalf);
                        Log.e("CCCPPP",""+labourList.get(i/2).getWages());
                        Log.e("CCCPPP",""+sumAmt);
                        int payableAmt=(int)(countPresent*labourList.get(i/2).getWages() + (int)countHalf*(labourList.get(i/2).getWages()/2)-sumAmt);


                        text1 = String.valueOf(payableAmt);
                    } else if(j>=5 && i%2==0){
                        text1 = ModelCompileStatusArrayList.get((((j-5) * labourList.size()) + (i/2))).getStatus();
                    }else if(j>=5 && i%2!=0){
                        text1 = ModelCompileStatusArrayList.get((((j-5) * labourList.size()) + (i/2))).getAmount();
                    }
                    Object text = text1;


                    // Create dummy id.
                    String id = j + "-" + i;

                    Cell cell;

                    cell = new Cell(id, text);

                    cellList.add(cell);

                }
                list.add(cellList);
            }

        }

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
