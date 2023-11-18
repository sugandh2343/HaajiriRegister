package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelError;
import com.skillzoomer_Attendance.com.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterError extends RecyclerView.Adapter<AdapterError.HolderEror> {
    private Context context;
    private ArrayList<ModelError> errorArrayList;

    public AdapterError(Context context, ArrayList<ModelError> errorArrayList) {
        this.context = context;
        this.errorArrayList = errorArrayList;
    }

    @NonNull
    @Override
    public AdapterError.HolderEror onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.error_list_single_row,parent,false);
        return new AdapterError.HolderEror(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterError.HolderEror holder, int position) {
        ModelError modelError=errorArrayList.get(position);
        holder.txt_amount.setText(context.getString(R.string.rupee_symbol)+modelError.getAmount());
        holder.txt_date.setText(modelError.getDate());
        holder.txt_time.setText(modelError.getTime());
        holder.txt_name.setText(modelError.getName());
        holder.txt_remark.setText(modelError.getRemark());
        holder.txt_cash_in_hand.setText(modelError.getCashInHand());

    }

    @Override
    public int getItemCount() {
        return errorArrayList.size();
    }

    public class HolderEror extends RecyclerView.ViewHolder {
        private TextView txt_date,txt_time,txt_amount,txt_cash_in_hand,txt_remark,txt_name;
        public HolderEror(@NonNull View itemView) {
            super(itemView);
            txt_amount=itemView.findViewById(R.id.txt_amount);
            txt_date=itemView.findViewById(R.id.txt_date);
            txt_time=itemView.findViewById(R.id.txt_time);
            txt_cash_in_hand=itemView.findViewById(R.id.txt_cash_in_hand);
            txt_remark=itemView.findViewById(R.id.txt_remark);
            txt_name=itemView.findViewById(R.id.txt_name);
        }
    }
}
