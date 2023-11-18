package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterEmployee extends RecyclerView.Adapter<AdapterEmployee.HolderEmployee>{
    private Context context;
    private ArrayList<ModelEmployee> employeeArrayList;

    public AdapterEmployee(Context context, ArrayList<ModelEmployee> employeeArrayList) {
        this.context = context;
        this.employeeArrayList = employeeArrayList;
    }

    @NonNull
    @Override
    public AdapterEmployee.HolderEmployee onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_emp_single_row,parent,false);
        return new AdapterEmployee.HolderEmployee(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderEmployee holder, int position) {

        if(position==0){
            holder.txt_sr_no.setText(R.string.sr_no);
            holder.et_name.setText(R.string.name);
            holder.et_father_name.setText("Gender");
            holder.et_company_id.setText(R.string.worker_if);
            holder.et_type.setText(R.string.labour_type);
            holder.et_wages.setText("Salary");
            holder.uniqueId.setText("Mobile");
            holder.txt_sr_no.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_name.setTypeface(Typeface.DEFAULT_BOLD);

            holder.et_father_name.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_company_id.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_type.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_wages.setTypeface(Typeface.DEFAULT_BOLD);
            holder.uniqueId.setTypeface(Typeface.DEFAULT_BOLD);
            holder.txt_image.setTypeface(Typeface.DEFAULT_BOLD);
            holder.txt_image.setVisibility(View.VISIBLE);
            holder.view2.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.ll_main.setBackgroundColor(context.getColor(R.color.white));
            }
        }else{
            holder.txt_image.setVisibility(View.GONE);
            holder.view2.setVisibility(View.VISIBLE);
            holder.txt_sr_no.setText(""+(position));
            holder.et_name.setText(employeeArrayList.get(holder.getAdapterPosition()-1).getName());
            holder.et_father_name.setText(employeeArrayList.get(holder.getAdapterPosition()-1).getGender());
            holder.et_company_id.setText(employeeArrayList.get(holder.getAdapterPosition()-1).getUserId());
            holder.et_wages.setText(employeeArrayList.get(holder.getAdapterPosition()-1).getSalary());
            holder.uniqueId.setText(employeeArrayList.get(holder.getAdapterPosition()-1).getMobile());
            if(employeeArrayList.get(holder.getAdapterPosition()-1).getPermanentEmployee()){
                holder.et_type.setText("Permanent");
            }else{
                holder.et_type.setText("Probation");
            }

            if(employeeArrayList.get(holder.getAdapterPosition()-1).getProfile()!=null &&
                    !employeeArrayList.get(holder.getAdapterPosition()-1).getProfile().equals("")){
                Picasso.get().load(employeeArrayList.get(holder.getAdapterPosition()-1).getProfile()).
                        resize(400,400).centerCrop()
                        .placeholder(R.drawable.profile).into(holder.img_profile);
            }

        }

    }

    @Override
    public int getItemCount() {
        return employeeArrayList.size()+1;
    }

    public class HolderEmployee extends RecyclerView.ViewHolder {
        private TextView txt_sr_no,txt_image;
        private EditText et_name,et_father_name,uniqueId,image,et_company_id,et_wages,et_type;
        private LinearLayout ll_main;
        private ImageView img_profile;
        private CardView view2;

        public HolderEmployee(@NonNull View itemView) {
            super(itemView);
            txt_sr_no=itemView.findViewById(R.id.txt_sr_no);
            et_name=itemView.findViewById(R.id.et_name);
            et_father_name=itemView.findViewById(R.id.et_father_name);
            uniqueId=itemView.findViewById(R.id.uniqueId);
            image=itemView.findViewById(R.id.image);
            et_company_id=itemView.findViewById(R.id.et_company_id);
            et_wages=itemView.findViewById(R.id.et_wages);
            et_type=itemView.findViewById(R.id.et_type);
            ll_main=itemView.findViewById(R.id.ll_main);
            txt_image=itemView.findViewById(R.id.txt_image);
            img_profile=itemView.findViewById(R.id.img_profile);
            view2=itemView.findViewById(R.id.view2);
        }
    }
}

