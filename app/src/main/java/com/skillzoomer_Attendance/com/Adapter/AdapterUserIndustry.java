package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelUserIndustry;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterUserIndustry extends RecyclerView.Adapter<AdapterUserIndustry.HolderUserIndustry>{

    private Context context;
    private ArrayList<ModelUserIndustry> userIndustryArrayList;

    public AdapterUserIndustry(Context context, ArrayList<ModelUserIndustry> userIndustryArrayList) {
        this.context = context;
        this.userIndustryArrayList = userIndustryArrayList;
    }

    @NonNull
    @Override
    public AdapterUserIndustry.HolderUserIndustry onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_user_industry,parent,false);
        return new AdapterUserIndustry.HolderUserIndustry(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserIndustry.HolderUserIndustry holder, int position) {
        ModelUserIndustry modelUserIndustry=userIndustryArrayList.get(position);
        holder.txt_companyName.setText(modelUserIndustry.getCompanyName());
        holder.txt_category_name.setText(modelUserIndustry.getIndustryName());
        holder.txt_designation.setText(modelUserIndustry.getDesignationName());

    }

    @Override
    public int getItemCount() {
        return userIndustryArrayList.size();
    }

    public class HolderUserIndustry extends RecyclerView.ViewHolder {
        TextView txt_companyName,txt_category_name,txt_designation;
        public HolderUserIndustry(@NonNull View itemView) {
            super(itemView);
            txt_companyName=itemView.findViewById(R.id.txt_companyName);
            txt_category_name=itemView.findViewById(R.id.txt_category_name);
            txt_designation=itemView.findViewById(R.id.txt_designation);
        }
    }
}
