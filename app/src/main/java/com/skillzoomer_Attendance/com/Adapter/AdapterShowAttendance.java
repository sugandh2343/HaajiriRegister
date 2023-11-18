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



public class AdapterShowAttendance extends RecyclerView.Adapter<AdapterShowAttendance.HolderShowAttendance> {
    private Context context;
    private ArrayList<ModelLabour>labourArrayList;
    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;


    public AdapterShowAttendance(Context context ,
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
        View view= LayoutInflater.from(context).inflate(R.layout.layout_attendance_single_row,parent,false);
        return new HolderShowAttendance(view);    }

    @Override
    public void onBindViewHolder(@NonNull HolderShowAttendance holder , int position) {

            Log.e("Position",""+position);
            holder.txt_labourName.setText(labourArrayList.get(position ).getName());
            holder.txt_labourId.setText(labourArrayList.get(position).getLabourId());











    }

    @Override
    public int getItemCount() {
        return labourArrayList.size();
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
