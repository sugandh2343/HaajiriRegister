package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLabourPayment;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterLabourPayment extends RecyclerView.Adapter<AdapterLabourPayment.HolderLabourPayment> {
    private Context context;
    private ArrayList<ModelLabourPayment> labourPaymentArrayList;

    public AdapterLabourPayment(Context context, ArrayList<ModelLabourPayment> labourPaymentArrayList) {
        this.context = context;
        this.labourPaymentArrayList = labourPaymentArrayList;
    }

    @NonNull
    @Override
    public AdapterLabourPayment.HolderLabourPayment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_labour_payment_single_row,parent,false);
        return new AdapterLabourPayment.HolderLabourPayment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLabourPayment.HolderLabourPayment holder, int position) {
        ModelLabourPayment modelLabourPayment=labourPaymentArrayList.get(position);
        holder.txt_labourId.setText(modelLabourPayment.getLabourId());
        holder.txt_payment.setText(modelLabourPayment.getAmount());
        holder.txt_labourName.setText(modelLabourPayment.getAmount());
        holder.txt_time.setText(modelLabourPayment.getTime());

    }

    @Override
    public int getItemCount() {
        return labourPaymentArrayList.size();
    }

    public class HolderLabourPayment extends RecyclerView.ViewHolder {
        TextView txt_labourId,txt_labourName,txt_payment,txt_time;
        public HolderLabourPayment(@NonNull View itemView) {
            super(itemView);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);
            txt_payment=itemView.findViewById(R.id.txt_payment);
            txt_time=itemView.findViewById(R.id.txt_time);
        }
    }
}
