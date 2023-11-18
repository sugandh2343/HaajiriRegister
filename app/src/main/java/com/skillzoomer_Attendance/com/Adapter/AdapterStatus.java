package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class
AdapterStatus extends RecyclerView.Adapter<AdapterStatus.HolderStatus> {
    private Context context;
    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;
    private int Parentposition;
    private ArrayList<ModelLabour>labourArrayList;

    public AdapterStatus(Context context , ArrayList<ModelLabourStatus> modelLabourStatusArrayList , ArrayList<ModelDate> dateModelArrayList , int parentposition , ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        this.dateModelArrayList = dateModelArrayList;
        Parentposition = parentposition;
        this.labourArrayList = labourArrayList;
    }

    public AdapterStatus(Context context , ArrayList<ModelDate> dateModelArrayList , int parentposition) {
        this.context = context;
        this.dateModelArrayList = dateModelArrayList;
        Parentposition = parentposition;
    }

    @NonNull
    @Override
    public HolderStatus onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_date_single_row,parent,false);
        return new HolderStatus(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderStatus holder , int position) {
        Log.e("Position","Parent"+Parentposition);
        if(Parentposition==0){
            holder.txt_date.setText(dateModelArrayList.get(position).getDate());


        }else{
            String status="";
            Log.e("MStatusSize",""+modelLabourStatusArrayList.size());
            for(int i=0;i<modelLabourStatusArrayList.size();i++){
                Log.e("StatusAdapter","Value of i"+i);
//                Log.e("StatusAdapter","DateModelArrayList:"+dateModelArrayList.get(position).getDate());
                Log.e("StatusAdapter","ModelLabourDate"+modelLabourStatusArrayList.get(i).getDate());
//                Log.e("StatusAdapter","ParentLabourList"+labourArrayList.get(Parentposition-1).getLabourId());
                Log.e("StatusAdapter","LabourStatusArrayList"+modelLabourStatusArrayList.get(i).getLabourId());
                Log.e("StatusAdapter","Status"+modelLabourStatusArrayList.get(i).getStatus());
                if(dateModelArrayList.get(position).getDate().equals(modelLabourStatusArrayList.get(i).getDate())
                &&labourArrayList.get(Parentposition-1).getLabourId().equals(modelLabourStatusArrayList.get(i).getLabourId())){
                    Log.e("TrueAt",""+i);
                    holder.txt_date.setText(modelLabourStatusArrayList.get(i).getStatus());
                    if(modelLabourStatusArrayList.get(i).getStatus().equals("0")){
                        holder.txt_date.setTextColor(context.getResources().getColor(R.color.red));
                    }else{
                        holder.txt_date.setTextColor(context.getResources().getColor(R.color.darkGreen));
                    }
                }
            }

        }



    }

    @Override
    public int getItemCount() {
        return dateModelArrayList.size();
    }

    public class HolderStatus extends RecyclerView.ViewHolder {
        private TextView txt_date;
        public HolderStatus(@NonNull View itemView) {
            super(itemView);
            txt_date=itemView.findViewById(R.id.txt_date);
        }
    }
}
