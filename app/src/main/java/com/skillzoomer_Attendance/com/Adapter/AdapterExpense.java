package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelExpenseLabourByData;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterExpense extends RecyclerView.Adapter<AdapterExpense.HolderExpense> {
    private Context context;
    private ArrayList<ModelExpenseLabourByData> receiveCashArrayList;

    public AdapterExpense(Context context, ArrayList<ModelExpenseLabourByData> receiveCashArrayList) {
        this.context = context;
        this.receiveCashArrayList = receiveCashArrayList;
    }

    @NonNull
    @Override
    public AdapterExpense.HolderExpense onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_expense_single_day,parent,false);
        return new AdapterExpense.HolderExpense(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExpense.HolderExpense holder, int position) {
        ModelExpenseLabourByData modelReceiveCash=receiveCashArrayList.get(position);
        holder.rec_amount.setText(modelReceiveCash.getAmount());
        holder.rec_from.setText(modelReceiveCash.getLabourName());
        holder.time.setText(modelReceiveCash.getLabourId());
    }

    @Override
    public int getItemCount() {
        return receiveCashArrayList.size();
    }

    public class HolderExpense extends RecyclerView.ViewHolder {
        TextView rec_amount,time,rec_from;
        public HolderExpense(@NonNull View itemView) {
            super(itemView);
            rec_amount=itemView.findViewById(R.id.rec_amount);
            time=itemView.findViewById(R.id.time);
            rec_from=itemView.findViewById(R.id.rec_from);
        }
    }
}
