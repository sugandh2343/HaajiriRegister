package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterLabour extends RecyclerView.Adapter<AdapterLabour.HolderLabour> {
    private Context context;
    public ArrayList<ModelLabour> labourArrayList;

    public AdapterLabour(Context context , ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
    }

    @NonNull
    @Override
    public HolderLabour onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_master_data_single_row,parent,false);
        return new HolderLabour(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLabour holder , int position) {
        if(position==0){
            holder.txt_sr_no.setText(R.string.sr_no);
            holder.et_name.setText(R.string.name);
            holder.et_father_name.setText(R.string.father_name);
            holder.et_company_id.setText(R.string.worker_if);
            holder.et_type.setText(R.string.labour_type);
            holder.et_wages.setText(R.string.wages);
            holder.uniqueId.setText(R.string.unique_id);
            holder.txt_sr_no.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_name.setTypeface(Typeface.DEFAULT_BOLD);
            holder.image.setTypeface(Typeface.DEFAULT_BOLD);

            holder.et_father_name.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_company_id.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_type.setTypeface(Typeface.DEFAULT_BOLD);
            holder.et_wages.setTypeface(Typeface.DEFAULT_BOLD);
            holder.uniqueId.setTypeface(Typeface.DEFAULT_BOLD);
            holder.ll_main.setBackgroundColor(context.getColor(R.color.white));
        }
        else if(position>0 && position%2!=0){
            holder.ll_main.setBackgroundColor(context.getColor(R.color.lightGreen));
        } else if(position>0 && position%2==0){
            holder.ll_main.setBackgroundColor(context.getColor(R.color.babyPink));
        }




        if(position>0) {
            ModelLabour modelLabour = labourArrayList.get(position-1);
            holder.txt_sr_no.setText(String.valueOf(holder.getAdapterPosition()));
            holder.et_name.setText(modelLabour.getName());
            holder.et_father_name.setText(modelLabour.getFatherName());
            holder.et_company_id.setText("Cons/"+modelLabour.getLabourId());
            holder.et_type.setText(modelLabour.getType());
            holder.et_wages.setText(String.valueOf(modelLabour.getWages()));
            holder.uniqueId.setText(modelLabour.getUniqueId());
            holder.uniqueId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            holder.et_wages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            holder.et_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            holder.et_company_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            holder.et_father_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            holder.et_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            holder.txt_sr_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(modelLabour.getProfile()==null){
                        Toast.makeText(context, "Labour Registration status is pending", Toast.LENGTH_SHORT).show();
                    }else{
                        showImage(modelLabour.getProfile());
                    }

                }
            });
        }


    }

    private void showImage(String profile) {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.show_image, null);

        ImageView iv_profile=(ImageView) mView.findViewById(R.id.iv_profile);
        Picasso.get().load(profile).
                resize(400,400).centerCrop()
                .placeholder(R.drawable.ic_add).into(iv_profile);




        alert.setView(mView);

        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);



        alertDialog.show();
    }

    private void showDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.error)
                .setMessage(R.string.read_only)
                .setCancelable(true);
        builder.show();

    }

    @Override
    public int getItemCount() {
        return labourArrayList.size()+1;
    }

    public class HolderLabour extends RecyclerView.ViewHolder {
        private TextView txt_sr_no;
        private EditText et_name,et_father_name,uniqueId,image,et_company_id,et_wages,et_type;
        private LinearLayout ll_main;
        public HolderLabour(@NonNull View itemView) {
            super(itemView);
            txt_sr_no=itemView.findViewById(R.id.txt_sr_no);
            et_name=itemView.findViewById(R.id.et_name);
            et_father_name=itemView.findViewById(R.id.et_father_name);
            uniqueId=itemView.findViewById(R.id.uniqueId);
            image=itemView.findViewById(R.id.image);
            et_company_id=itemView.findViewById(R.id.et_company_id);
            et_company_id=itemView.findViewById(R.id.et_company_id);
            et_wages=itemView.findViewById(R.id.et_wages);
            et_type=itemView.findViewById(R.id.et_type);
            ll_main=itemView.findViewById(R.id.ll_main);

        }
    }
}
