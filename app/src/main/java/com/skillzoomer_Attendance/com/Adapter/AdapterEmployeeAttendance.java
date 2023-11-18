package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelAttendanceEmployee;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterEmployeeAttendance extends RecyclerView.Adapter<AdapterEmployeeAttendance.HolderEmployeeAttendance> {
    private Context context;
    private ArrayList<ModelAttendanceEmployee> attendanceEmployeeArrayList;

    public AdapterEmployeeAttendance(Context context, ArrayList<ModelAttendanceEmployee> attendanceEmployeeArrayList) {
        this.context = context;
        this.attendanceEmployeeArrayList = attendanceEmployeeArrayList;
    }

    @NonNull
    @Override
    public AdapterEmployeeAttendance.HolderEmployeeAttendance onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_employee_calendar,parent,false);
        return new AdapterEmployeeAttendance.HolderEmployeeAttendance(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEmployeeAttendance.HolderEmployeeAttendance holder, int position) {
        if(position<7){
            holder.txt_status.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
            holder.txt_date.setTypeface(Typeface.DEFAULT_BOLD);
            if(position==0){
                holder.txt_date.setText("Su");
            }else if(position==1){
                holder.txt_date.setText("Mo");
            }else if(position==2){
                holder.txt_date.setText("Tu");
            }else if(position==3){
                holder.txt_date.setText("We");
            }else if(position==4){
                holder.txt_date.setText("Th");
            }else if(position==5){
                holder.txt_date.setText("Fr");
            }else if(position==6){
                holder.txt_date.setText("Sa");
            }
        }else{
            holder.txt_status.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.VISIBLE);
            holder.txt_date.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            if(attendanceEmployeeArrayList.get(position-7).getDate().equals("-")){
                holder.txt_status.setVisibility(View.GONE);
                holder.txt_date.setText("-");
                holder.view.setVisibility(View.GONE);
            }else{
                String[] separataed = attendanceEmployeeArrayList.get(position-7).getDate().split("-");
                holder.txt_date.setText(separataed[0]);
                holder.txt_status.setVisibility(View.VISIBLE);
                holder.txt_status.setText(attendanceEmployeeArrayList.get(position-7).getStatus());
            }

        }

    }

    @Override
    public int getItemCount() {
        return attendanceEmployeeArrayList.size()+7;
    }

    public class HolderEmployeeAttendance extends RecyclerView.ViewHolder {
        TextView txt_date,txt_status;
        View view;
        LinearLayout ll_main;

        public HolderEmployeeAttendance(@NonNull View itemView) {
            super(itemView);
            txt_date=itemView.findViewById(R.id.txt_date);
            txt_status=itemView.findViewById(R.id.txt_status);
            view=itemView.findViewById(R.id.view);
            ll_main=itemView.findViewById(R.id.ll_main);
        }
    }
}
