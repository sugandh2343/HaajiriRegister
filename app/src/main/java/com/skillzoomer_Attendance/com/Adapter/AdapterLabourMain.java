package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterLabourMain extends RecyclerView.Adapter<AdapterLabourMain.HolderLabourMain> {
    private Context context;
    private ArrayList<ModelLabour> labourArrayList;

    public AdapterLabourMain(Context context , ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
    }

    @NonNull
    @Override
    public HolderLabourMain onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_master_data_single_column,parent,false);
        return new HolderLabourMain(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLabourMain holder , int position) {
        AdapterLabour adapterLabour=new AdapterLabour(context,labourArrayList);
        holder.rv_recyclerViewHorizontal.setAdapter(adapterLabour);

    }

    @Override
    public int getItemCount() {
        return labourArrayList.size();
    }

    public class HolderLabourMain extends RecyclerView.ViewHolder {
        RecyclerView rv_recyclerViewHorizontal;
        public HolderLabourMain(@NonNull View itemView) {
            super(itemView);
            rv_recyclerViewHorizontal=itemView.findViewById(R.id.rv_recyclerViewHorizontal);
        }
    }
}
