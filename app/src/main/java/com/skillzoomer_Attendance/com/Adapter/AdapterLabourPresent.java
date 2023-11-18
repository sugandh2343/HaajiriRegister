package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLabourPresent;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterLabourPresent extends RecyclerView.Adapter<AdapterLabourPresent.HolderLabourPresent> {
    private Context context;
    private ArrayList<ModelLabourPresent> labourPresentArrayList;

    public AdapterLabourPresent(Context context, ArrayList<ModelLabourPresent> labourPresentArrayList) {
        this.context = context;
        this.labourPresentArrayList = labourPresentArrayList;
    }

    @NonNull
    @Override
    public AdapterLabourPresent.HolderLabourPresent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_present_labour_single_row,parent,false);
        return new AdapterLabourPresent.HolderLabourPresent(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLabourPresent.HolderLabourPresent holder, int position) {
        ModelLabourPresent modelLabourPresent=labourPresentArrayList.get(position);
        holder.txt_labourId.setText(modelLabourPresent.getLabourId());
        holder.txt_labourName.setText(modelLabourPresent.getLabourName());

    }

    @Override
    public int getItemCount() {
        return labourPresentArrayList.size();
    }

    public class HolderLabourPresent extends RecyclerView.ViewHolder {
        TextView txt_labourId,txt_labourName;
        public HolderLabourPresent(@NonNull View itemView) {
            super(itemView);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);
        }
    }
}
