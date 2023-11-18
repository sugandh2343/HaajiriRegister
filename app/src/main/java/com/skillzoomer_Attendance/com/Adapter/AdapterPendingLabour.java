package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Activity.LabourRegistration;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterPendingLabour extends RecyclerView.Adapter<AdapterPendingLabour.HolderPendingLabour>{
    private Context context;
    private ArrayList<ModelLabour> modelLabourArrayList;

    public AdapterPendingLabour(Context context, ArrayList<ModelLabour> modelLabourArrayList) {
        this.context = context;
        this.modelLabourArrayList = modelLabourArrayList;
    }

    @NonNull
    @Override
    public AdapterPendingLabour.HolderPendingLabour onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.pending_labour_single_row,parent,false);
        return new AdapterPendingLabour.HolderPendingLabour(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPendingLabour.HolderPendingLabour holder, int position) {
        holder.setIsRecyclable(false);
        ModelLabour modelLabour=modelLabourArrayList.get(position);
        holder.labour_id.setText(modelLabour.getLabourId());
        holder.labour_name.setText(modelLabour.getName());
        if(modelLabour.getType().equals("Skilled")){
            holder.labour_type.setText(context.getString(R.string.skilled));
        }else{
            holder.labour_type.setText(context.getString(R.string.unskilled));
        }
        holder.btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, LabourRegistration.class);
                intent.putExtra("Activity","Pending");
                intent.putExtra("type",modelLabour.getType());
                intent.putExtra("name",modelLabour.getName());
                intent.putExtra("fatherName",modelLabour.getFatherName());
                intent.putExtra("wages",modelLabour.getWages());
                intent.putExtra("uniqueId",modelLabour.getUniqueId());
                intent.putExtra("profile",modelLabour.getProfile());
                intent.putExtra("siteId",modelLabour.getSiteCode());
                intent.putExtra("siteName",modelLabour.getSiteName());
                intent.putExtra("labourId",modelLabour.getLabourId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelLabourArrayList.size();
    }

    public class HolderPendingLabour extends RecyclerView.ViewHolder {
        TextView labour_id,labour_name,labour_type;
        Button btn_show;
        public HolderPendingLabour(@NonNull View itemView) {
            super(itemView);
            labour_id=itemView.findViewById(R.id.labour_id);
            labour_name=itemView.findViewById(R.id.labour_name);
            labour_type=itemView.findViewById(R.id.labour_type);
            btn_show=itemView.findViewById(R.id.btn_show);
        }
    }
}
