package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelEditLabour;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterEditLabour extends RecyclerView.Adapter<AdapterEditLabour.HolderEditLabour> {
    private Context context;
    private ArrayList<ModelEditLabour> editLabourArrayList;
    private String[] designation={"P","P/2","A"};

    public AdapterEditLabour(Context context, ArrayList<ModelEditLabour> editLabourArrayList) {
        this.context = context;
        this.editLabourArrayList = editLabourArrayList;

    }



    @NonNull
    @Override
    public AdapterEditLabour.HolderEditLabour onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_attendance_edit_single_row,parent,false);
        return new AdapterEditLabour.HolderEditLabour(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEditLabour.HolderEditLabour holder, int position) {
        holder.setIsRecyclable(false);
        ModelEditLabour modelEditLabour=editLabourArrayList.get(position);
        if(modelEditLabour.getStatus().equals("P")){
            holder.cb_p.setChecked(true);
            holder.cb_p2.setChecked(false);
            holder.cb_a.setChecked(false);
        }else if(modelEditLabour.getStatus().equals("A")){
            holder.cb_p.setChecked(false);
            holder.cb_p2.setChecked(false);
            holder.cb_a.setChecked(true);
        }else{
            holder.cb_p.setChecked(false);
            holder.cb_p2.setChecked(true);
            holder.cb_a.setChecked(false);
        }
        holder.cb_p.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    holder.cb_p.setChecked(true);
                    holder.cb_p2.setChecked(false);
                    holder.cb_a.setChecked(false);
                    Intent intent = new Intent("DatabaseChange");

                    intent.putExtra("Position", holder.getAdapterPosition());
                    intent.putExtra("status","P");

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    notifyDataSetChanged();
                }
            }
        });
        holder.cb_p2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    holder.cb_p.setChecked(false);
                    holder.cb_p2.setChecked(true);
                    holder.cb_a.setChecked(false);
                    Intent intent = new Intent("DatabaseChange");

                    intent.putExtra("Position", holder.getAdapterPosition());
                    intent.putExtra("status","P/2");

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    notifyDataSetChanged();
                }
            }
        });
        holder.cb_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    holder.cb_p.setChecked(false);
                    holder.cb_p2.setChecked(false);
                    holder.cb_a.setChecked(true);
                    Intent intent = new Intent("DatabaseChange");

                    intent.putExtra("Position", holder.getAdapterPosition());
                    intent.putExtra("status","A");

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    notifyDataSetChanged();
                }
            }
        });

        holder.txt_labourId.setText(modelEditLabour.getLabourId());
        holder.txt_labourName.setText(modelEditLabour.getLabourName());


    }

    @Override
    public int getItemCount() {
        return editLabourArrayList.size();
    }

    public class HolderEditLabour extends RecyclerView.ViewHolder {
        private TextView txt_labourId,txt_labourName;
        private CheckBox cb_p2,cb_p,cb_a;
        public HolderEditLabour(@NonNull View itemView) {
            super(itemView);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);
            cb_p2=itemView.findViewById(R.id.cb_p2);
            cb_p=itemView.findViewById(R.id.cb_p);
            cb_a=itemView.findViewById(R.id.cb_a);


        }
    }


}
