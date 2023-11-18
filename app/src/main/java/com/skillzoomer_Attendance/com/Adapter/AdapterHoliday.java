package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelHoliday;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterHoliday extends RecyclerView.Adapter<AdapterHoliday.HolderAdapter>{
    private Context context;
    private ArrayList<ModelHoliday>holidayArrayList;

    public AdapterHoliday(Context context, ArrayList<ModelHoliday> holidayArrayList) {
        this.context = context;
        this.holidayArrayList = holidayArrayList;
    }

    @NonNull
    @Override
    public AdapterHoliday.HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_holiday_single_row,parent,false);
        return new AdapterHoliday.HolderAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHoliday.HolderAdapter holder, int position) {
        ModelHoliday modelHoliday=holidayArrayList.get(position);
        holder.et_holiday_date.setText(modelHoliday.getDate());
        holder.et_holiday_title.setText(modelHoliday.getTitle());

    }

    @Override
    public int getItemCount() {
        return holidayArrayList.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        EditText et_holiday_title,et_holiday_date;
        public HolderAdapter(@NonNull View itemView) {
            super(itemView);
            et_holiday_date=itemView.findViewById(R.id.et_holiday_date);
            et_holiday_title=itemView.findViewById(R.id.et_holiday_title);
        }
    }
}
