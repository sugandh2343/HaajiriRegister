package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelReceiveCash;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterReceiveCash extends RecyclerView.Adapter<AdapterReceiveCash.HolderReceiveCash> {
    private Context context;
    private ArrayList<ModelReceiveCash> receiveCashArrayList;

    public AdapterReceiveCash(Context context, ArrayList<ModelReceiveCash> receiveCashArrayList) {
        this.context = context;
        this.receiveCashArrayList = receiveCashArrayList;
    }

    @NonNull
    @Override
    public AdapterReceiveCash.HolderReceiveCash onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_receive_single_day,parent,false);
        return new AdapterReceiveCash.HolderReceiveCash(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReceiveCash.HolderReceiveCash holder, int position) {
        ModelReceiveCash modelReceiveCash=receiveCashArrayList.get(position);
        holder.rec_amount.setText(modelReceiveCash.getAmount());
        holder.rec_from.setText(modelReceiveCash.getRecFrom());
        holder.time.setText(modelReceiveCash.getReqTime());

    }

    @Override
    public int getItemCount() {
        return receiveCashArrayList.size();
    }

    public class HolderReceiveCash extends RecyclerView.ViewHolder {
        TextView rec_amount,time,rec_from;
        public HolderReceiveCash(@NonNull View itemView) {
            super(itemView);
            rec_amount=itemView.findViewById(R.id.rec_amount);
            time=itemView.findViewById(R.id.time);
            rec_from=itemView.findViewById(R.id.rec_from);
        }
    }
}
