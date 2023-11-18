package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Utilities.ItemClickListener;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterPowerDistribution extends RecyclerView.Adapter<AdapterPowerDistribution.HolderPowerDistributuion> {
    private Context context;
    private ArrayList<ModelUser> associateArrayList;
    ItemClickListener itemClickListener;
    int selectedPosition = -1;

    public AdapterPowerDistribution(Context context, ArrayList<ModelUser> associateArrayList, ItemClickListener itemClickListener) {
        this.context = context;
        this.associateArrayList = associateArrayList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public AdapterPowerDistribution.HolderPowerDistributuion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_distribute_power_single_row,parent,false);
        return new AdapterPowerDistribution.HolderPowerDistributuion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPowerDistribution.HolderPowerDistributuion holder, int position) {

        ModelUser modelAssociate=associateArrayList.get(holder.getAdapterPosition());
        holder.txt_name.setText(modelAssociate.getName());
        if(modelAssociate.getAttendanceManagement()){
            holder.rb_attendance.setChecked(true);
        }else{
            holder.rb_attendance.setChecked(false);
        }
        if(modelAssociate.getCashManagement()){
            holder.rb_cash.setChecked(true);
        }else{
            holder.rb_cash.setChecked(false);
        }
        if(modelAssociate.getFinanceManagement()){
            holder.rb_expense.setChecked(true);
        }else{
            holder.rb_expense.setChecked(false);
        }
        if(modelAssociate.getForceOpt()){
            holder.cb_force_logout.setChecked(true);
        }else{
            holder.cb_force_logout.setChecked(false);
        }

        holder.rb_attendance.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton compoundButton,
                            boolean b)
                    {
                        // check condition

                            // When checked
                            // update selected position
                            Intent intent = new Intent("AttendanceChange");

                            intent.putExtra("position", holder.getAdapterPosition());
                            intent.putExtra("value", b);

                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }
                });
        holder.rb_cash.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton compoundButton,
                            boolean b)
                    {
                        // check condition

                            // When checked
                            // update selected position
                            Intent intent = new Intent("CashChange");

                            intent.putExtra("position", holder.getAdapterPosition());
                        intent.putExtra("value", b);

                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }
                });
        holder.rb_expense.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton compoundButton,
                            boolean b)
                    {
                        // check condition

                            // When checked
                            // update selected position
                            Intent intent = new Intent("ExpenseChange");

                            intent.putExtra("position", holder.getAdapterPosition());
                        intent.putExtra("value", b);

                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }
                });
        holder.cb_force_logout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent = new Intent("ForceLogout");

                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("forceLogout", b);

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return associateArrayList.size();
    }

    public class HolderPowerDistributuion extends RecyclerView.ViewHolder {
        TextView txt_name;
        CheckBox rb_attendance,rb_cash,rb_expense;
        CheckBox cb_force_logout;
        public HolderPowerDistributuion(@NonNull View itemView) {
            super(itemView);
            txt_name=itemView.findViewById(R.id.txt_name);
            rb_attendance=itemView.findViewById(R.id.rb_attendance);
            rb_cash=itemView.findViewById(R.id.rb_cash);
            rb_expense=itemView.findViewById(R.id.rb_expense);
            cb_force_logout=itemView.findViewById(R.id.cb_force_logout);

        }
    }
}
