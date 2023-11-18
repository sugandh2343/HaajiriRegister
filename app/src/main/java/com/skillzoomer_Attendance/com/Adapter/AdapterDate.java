package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterDate extends RecyclerView.Adapter<AdapterDate.HolderDate> {
    private Context context;
    private ArrayList<ModelDate> modelDateArrayList;

    public AdapterDate(Context context, ArrayList<ModelDate> modelDateArrayList) {
        this.context = context;
        this.modelDateArrayList = modelDateArrayList;
    }

    @NonNull
    @Override
    public AdapterDate.HolderDate onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_date_single_column,parent,false);
        return new AdapterDate.HolderDate(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDate.HolderDate holder, int position) {
        holder.setIsRecyclable(false);
        holder.txt_date.setText(modelDateArrayList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return modelDateArrayList.size();
    }

    public class HolderDate extends RecyclerView.ViewHolder {
        TextView txt_date;
        public HolderDate(@NonNull View itemView) {
            super(itemView);
            txt_date= itemView.findViewById(R.id.txt_date);
        }
    }
}
