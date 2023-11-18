package com.skillzoomer_Attendance.com.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdapterLabourCard extends RecyclerView.Adapter<AdapterLabourCard.HolderLabourCard> {
    private Context context;
    private ArrayList<ModelLabour> labourList;

    public AdapterLabourCard(Context context, ArrayList<ModelLabour> labourList) {
        this.context = context;
        this.labourList = labourList;
    }

    @NonNull
    @Override
    public AdapterLabourCard.HolderLabourCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_master_data_single_row_pdf,parent,false);
        return new AdapterLabourCard.HolderLabourCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLabourCard.HolderLabourCard holder, int position) {
        Log.e("LAdapter",""+labourList.size());
        ModelLabour modelLabour=labourList.get(position);
//        holder.setIsRecyclable(false);
//        if(holder.getAdapterPosition()==0){
//            holder.ll_heading.setVisibility(View.VISIBLE);
//        }else{
//            holder.ll_heading.setVisibility(View.GONE);
//        }
        String currentDate;
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = df.format(c);

        holder.txt_ind_name.setText(context
                .getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""));
        holder.txt_company_name.setText(context.getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName",""));
        holder.txt_generated_on.setText(currentDate);
        holder.txt_site_name.setText(modelLabour.getSiteCode()+"/ "+modelLabour.getSiteName());
        holder.txt_type.setText(modelLabour.getType());
        holder.txt_labourId.setText(modelLabour.getLabourId());
        holder.txt_name.setText(modelLabour.getName());
        holder.txt_labour_fathers_name.setText(modelLabour.getFatherName());
        holder.txt_labour_uniqueId.setText(modelLabour.getUniqueId());
        holder.txt_labour_wages.setText(context.getString(R.string.rupee_symbol)+" "+modelLabour.getWages());
        holder.txt_payable_amount.setText(context.getString(R.string.rupee_symbol)+" "+modelLabour.getPayableAmt());



        if(!(modelLabour.getProfile()==null) || !modelLabour.getProfile().equals("")){
            Picasso.get().load(modelLabour.getProfile()).
                    resize(400,400).centerCrop()
                    .placeholder(R.drawable.profile).into(holder.img_profile);
        }




    }

    @Override
    public int getItemCount() {
        return labourList.size();
    }

    public class HolderLabourCard extends RecyclerView.ViewHolder {
        TextView txt_type,txt_labourId,txt_name,txt_labour_fathers_name,txt_labour_uniqueId,txt_labour_wages,txt_payable_amount,txt_ind_name,txt_generated_on,txt_company_name,txt_site_name;
        ImageView img_profile;
        LinearLayout ll_heading;
        public HolderLabourCard(@NonNull View itemView) {
            super(itemView);
            txt_type=itemView.findViewById(R.id.txt_type);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_labour_fathers_name=itemView.findViewById(R.id.txt_labour_fathers_name);
            txt_labour_uniqueId=itemView.findViewById(R.id.txt_labour_uniqueId);
            txt_labour_wages=itemView.findViewById(R.id.txt_labour_wages);
            txt_payable_amount=itemView.findViewById(R.id.txt_payable_amount);
            img_profile=itemView.findViewById(R.id.img_profile);
            txt_ind_name=itemView.findViewById(R.id.txt_ind_name);
            txt_generated_on=itemView.findViewById(R.id.txt_generated_on);
            txt_company_name=itemView.findViewById(R.id.txt_company_name);
            txt_site_name=itemView.findViewById(R.id.txt_site_name);
            ll_heading=itemView.findViewById(R.id.ll_heading);

        }
    }
}
