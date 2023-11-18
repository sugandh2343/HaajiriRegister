package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLeavePolicy;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterLeavePolicy extends RecyclerView.Adapter<AdapterLeavePolicy.HolderLeavePolicy> {
    private Context context;
    private ArrayList<ModelLeavePolicy> leavePolicyArrayList;

    public AdapterLeavePolicy(Context context, ArrayList<ModelLeavePolicy> leavePolicyArrayList) {
        this.context = context;
        this.leavePolicyArrayList = leavePolicyArrayList;
    }

    @NonNull
    @Override
    public AdapterLeavePolicy.HolderLeavePolicy onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_leave_policy_single_row,parent,false);
        return new AdapterLeavePolicy.HolderLeavePolicy(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLeavePolicy.HolderLeavePolicy holder, int position) {
        ModelLeavePolicy modelLeavePolicy=leavePolicyArrayList.get(position);
        holder.cb_cl.setEnabled(false);
        holder.cb_late.setEnabled(false);
        holder.cb_short_leave.setEnabled(false);
        holder.cb_half_day.setEnabled(false);
        holder.cb_el.setEnabled(false);
        holder.cb_ml.setEnabled(false);
        holder.cb_pl.setEnabled(false);
        holder.cb_sl.setEnabled(false);
        if(modelLeavePolicy.getShortLeave()){
            holder.cb_short_leave.setChecked(true);
        }
        if(modelLeavePolicy.getLate()){
            holder.cb_late.setChecked(true);
        }
        if(modelLeavePolicy.getHalfday()){
            holder.cb_half_day.setChecked(true);
        }
        if(modelLeavePolicy.getCl()){
            holder.cb_cl.setChecked(true);
        }
        if(modelLeavePolicy.getEl()){
            holder.cb_el.setChecked(true);
        }
        if(modelLeavePolicy.getMl()){
            holder.cb_ml.setChecked(true);
        }
        if(modelLeavePolicy.getPl()){
            holder.cb_pl.setChecked(true);
        }
        if(modelLeavePolicy.getSl()){
            holder.cb_sl.setChecked(true);
        }
        holder.txt_name.setText(modelLeavePolicy.getPolicyName());

    }


    @Override
    public int getItemCount() {
        return leavePolicyArrayList.size();
    }

    public class HolderLeavePolicy extends RecyclerView.ViewHolder {
        TextView txt_name;
        CheckBox cb_late,cb_short_leave,cb_half_day,cb_cl,cb_el,cb_ml,cb_pl,cb_sl;
        public HolderLeavePolicy(@NonNull View itemView) {
            super(itemView);

            txt_name=itemView.findViewById(R.id.txt_name);
            cb_late=itemView.findViewById(R.id.cb_late);
            cb_short_leave=itemView.findViewById(R.id.cb_short_leave);
            cb_half_day=itemView.findViewById(R.id.cb_half_day);
            cb_cl=itemView.findViewById(R.id.cb_cl);
            cb_el=itemView.findViewById(R.id.cb_el);
            cb_ml=itemView.findViewById(R.id.cb_ml);
            cb_pl=itemView.findViewById(R.id.cb_pl);
            cb_sl=itemView.findViewById(R.id.cb_sl);
        }
    }
}
