package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
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

import com.skillzoomer_Attendance.com.Activity.EditLabourActivity;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterLabourAdmin extends RecyclerView.Adapter<AdapterLabourAdmin.HolderLabourAdmin> {
    private Context context;
    public ArrayList<ModelLabour> labourArrayList;

    public AdapterLabourAdmin(Context context, ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
    }

    @NonNull
    @Override
    public AdapterLabourAdmin.HolderLabourAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_master_data_admin_single_row,parent,false);
        return new AdapterLabourAdmin.HolderLabourAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLabourAdmin holder, int position) {
        holder.setIsRecyclable(false);
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
            holder.iv_edit.setVisibility(View.GONE);
            holder.txt_sr_no.setEnabled(false);
            holder.et_name.setEnabled(false);
            holder.et_father_name.setEnabled(false);
            holder.et_type.setEnabled(false);
            holder.et_wages.setEnabled(false);
            holder.uniqueId.setEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.ll_main.setBackgroundColor(context.getColor(R.color.white));
            }
        }else if(position>0 && position%2!=0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.ll_main.setBackgroundColor(context.getColor(R.color.lightGreen));
            }
        } else if(position>0 && position%2==0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.ll_main.setBackgroundColor(context.getColor(R.color.babyPink));
            }
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked","photo");
                if(holder.getAdapterPosition()>0) {
                    if(labourArrayList.get(holder.getAdapterPosition() - 1).getProfile()==null|| TextUtils.isEmpty(labourArrayList.get(holder.getAdapterPosition() - 1).getProfile())){
                        Toast.makeText(context, "Labour Registration status is pending", Toast.LENGTH_SHORT).show();
                    }else{
                        showImage(labourArrayList.get(holder.getAdapterPosition() - 1).getProfile());
                    }

                }
            }
        });




        if(position>0) {
            ModelLabour modelLabour = labourArrayList.get(position-1);
            holder.txt_sr_no.setText(String.valueOf(holder.getAdapterPosition()));
            holder.et_name.setText(modelLabour.getName());
            holder.et_father_name.setText(modelLabour.getFatherName());
            holder.et_company_id.setText(modelLabour.getLabourId());
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

            holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(modelLabour.getStatus().equals("Pending")){

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setTitle(R.string.success)
                                .setMessage(R.string.pending_status)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {

                                    dialogInterface.dismiss();

                                });

                        builder.show();
                    }else {
                        Intent intent = new Intent(context, EditLabourActivity.class);
                        intent.putExtra("LabourId", modelLabour.getLabourId());
                        intent.putExtra("siteId", modelLabour.getSiteCode());
                        context.startActivity(intent);
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
                .setMessage(R.string.edit_click_to_edit)
                .setCancelable(true);
        builder.show();

    }

    @Override
    public int getItemCount() {
        return labourArrayList.size()+1;
    }

    public class HolderLabourAdmin extends RecyclerView.ViewHolder {
        private TextView txt_sr_no;
        private EditText et_name,et_father_name,uniqueId,image,et_company_id,et_wages,et_type;
        private LinearLayout ll_main;
        private ImageView iv_edit;
        public HolderLabourAdmin(@NonNull View itemView) {
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
            iv_edit=itemView.findViewById(R.id.iv_edit);
        }
    }
}
