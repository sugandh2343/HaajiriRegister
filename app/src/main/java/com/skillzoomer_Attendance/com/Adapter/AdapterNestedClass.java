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

public class AdapterNestedClass extends RecyclerView.Adapter<AdapterNestedClass.HolderShowAttendance> {
    private Context context;
    private ArrayList<ModelLabour>labourArrayList;
    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;


    public AdapterNestedClass(Context context ,
                              ArrayList<ModelLabour> labourArrayList ,
                              ArrayList<ModelLabourStatus> modelLabourStatusArrayList ,
                              ArrayList<ModelDate> dateModelArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        this.dateModelArrayList = dateModelArrayList;
    }

    @NonNull
    @Override
    public HolderShowAttendance onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_parent_class,parent,false);
        return new HolderShowAttendance(view);    }

    @Override
    public void onBindViewHolder(@NonNull HolderShowAttendance holder , int position) {
        if(holder.getAdapterPosition()==0){
            holder.txt_labourId.setText("Labour Id");
            holder.txt_labourName.setText("Labour Name");
            Log.e("DateAdapter",""+dateModelArrayList.size());


        }else {

            holder.txt_labourName.setText(labourArrayList.get(position - 1).getName());
            holder.txt_labourId.setText(labourArrayList.get(position - 1).getLabourId());





        }




    }

    @Override
    public int getItemCount() {
        return labourArrayList.size()+1;
    }

    public class HolderShowAttendance extends RecyclerView.ViewHolder {
        TextView txt_labourId,txt_labourName;
        public HolderShowAttendance(@NonNull View itemView) {
            super(itemView);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);


        }
    }
}
