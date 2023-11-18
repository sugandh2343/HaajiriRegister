package com.skillzoomer_Attendance.com.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Activity.MainActivity;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterSearchLabour extends RecyclerView.Adapter<AdapterSearchLabour.HolderSearchLabour> {
    private Context context;
    private ArrayList<ModelLabour> labourArrayList;

    public AdapterSearchLabour(Context context , ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
    }

    @NonNull
    @Override
    public HolderSearchLabour onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_search_labour_single_row,parent,false);
        return new HolderSearchLabour(view);    }

    @Override
    public void onBindViewHolder(@NonNull HolderSearchLabour holder , int position) {
        ModelLabour modelLabour=labourArrayList.get(position);
        String profile=modelLabour.getProfile();
        holder.txt_labourId.setText(modelLabour.getLabourId());
        holder.txt_labourName.setText(modelLabour.getName());
        holder.txt_labour_uniqueId.setText(modelLabour.getUniqueId());
        String type= modelLabour.getType();
        holder.txt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfileDialog(profile);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToLocalDatabase(modelLabour.getLabourId(), modelLabour.getName(),type,modelLabour.getSiteCode());

            }
        });
        holder.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToLocalDatabase(modelLabour.getLabourId(), modelLabour.getName(),type, modelLabour.getSiteCode());


            }
        });

    }
    private void uploadToLocalDatabase(String labourId, String laborName, String type, int siteCode){

        SimpleDateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }
        SimpleDateFormat time = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            time = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        }
        Date c = Calendar.getInstance().getTime();
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = df.format(c);
        }
        String currentTime= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = time.format(c);
        }
        EasyDB easyDB = EasyDB.init(context, "ATTENDANCE_Db")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        try {
            easyDB.addData("LabourId" , labourId)
                    .addData("LabourName" , laborName)
                    .addData("Date" , currentDate)
                    .addData("Time" , currentTime)
                    .doneDataAdding();
            Intent intent=new Intent(context, MainActivity.class);
            intent.putExtra("type",type);
            intent.putExtra("siteId",Long.parseLong(String.valueOf(siteCode)));
           Log.e("Back","S:"+siteCode);
            context.startActivity(intent);
        }catch (SQLiteConstraintException e){
            Log.e("Exception",e.getMessage());
        }//        holder.btn_delete.setVisibility(View.GONE);

    }


    private void showProfileDialog(String profile) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.show_image, null);

        ImageView iv_profile=(ImageView) mView.findViewById(R.id.iv_profile);
        Picasso.get().load(profile).
                resize(400,400).centerCrop()
                .placeholder(R.drawable.ic_add).into(iv_profile);




        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);



        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return labourArrayList.size();
    }

    public class HolderSearchLabour extends RecyclerView.ViewHolder {
        TextView txt_labourId,txt_labourName,txt_labour_uniqueId,txt_image;
        AppCompatButton btn_select;
        public HolderSearchLabour(@NonNull View itemView) {
            super(itemView);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);
            txt_labour_uniqueId=itemView.findViewById(R.id.txt_labour_uniqueId);
            txt_image=itemView.findViewById(R.id.txt_image);
            btn_select=itemView.findViewById(R.id.btn_select);
        }
    }
}
