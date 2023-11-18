package com.skillzoomer_Attendance.com.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterData extends RecyclerView.Adapter<AdapterData.HolderData> {
    private Context context;
    public ArrayList<ModelLabour> dataList;
    public  String workerType;

    public AdapterData(Context context, ArrayList<ModelLabour> dataList, String workerType) {
        this.context = context;
        this.dataList = dataList;
        this.workerType = workerType;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_labour_attendance,parent,false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, @SuppressLint("RecyclerView") int position) {
        holder.setIsRecyclable(false);
        ModelLabour modelLabour=dataList.get(position);
        if(!modelLabour.getStatus().equals("Pending")){
            Picasso.get().load(modelLabour.getProfile()).
                    resize(400,400).centerCrop()
                    .placeholder(R.drawable.ic_add).into(holder.img_profile);
            holder.txt_labourId.setText(modelLabour.getLabourId());
            holder.txt_labourName.setText(modelLabour.getName());
            holder.cb_present.setChecked(modelLabour.getPresent());
            holder.cb_present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("CheckedCHange","Position:"+position+" Checked:::::"+b);
                    if(b){
                        addToLocalDatabase(modelLabour);

//                    notifyDataSetChanged();

                    }else{
                        removeFromLocalDatabase(modelLabour);

//                    notifyDataSetChanged();
                    }
//                dataList.get(holder.getAdapterPosition()).setPresent(b);
//                notifyDataSetChanged();
                }
            });
            holder.img_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProfileDialog(modelLabour.getProfile());
                }
            });

        }

//


    }

    private void removeFromLocalDatabase(ModelLabour modelLabour) {
        EasyDB easyDB=EasyDB.init(context,"Attendance"+workerType)
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        try{
            easyDB.deleteRow(1,modelLabour.getLabourId());
            Log.e("RemoveLD",""+easyDB.getAllDataOrderedBy(1,true).getCount());
            Intent intent = new Intent("DatabaseChange");

            intent.putExtra("change", "removed");

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }catch(SQLiteConstraintException e){
            Log.e("Remove",e.getMessage());
        }



    }

    private void addToLocalDatabase(ModelLabour modelLabour) {
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
        Log.e("AdWT",workerType+modelLabour.getType());
        EasyDB easyDB = EasyDB.init(context, "Attendance"+workerType)
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "unique"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        try {
            easyDB.addData("LabourId" , modelLabour.getLabourId())
                    .addData("LabourName" , modelLabour.getName())
                    .addData("Date" , currentDate)
                    .addData("Time" , currentTime)
                    .doneDataAdding();
            Intent intent = new Intent("DatabaseChange");

            Log.e("RemoveLD","Avc"+easyDB.getAllDataOrderedBy(1,true).getCount());

            intent.putExtra("change", "added");

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }catch (SQLiteConstraintException e){
            Log.e("Exception",e.getMessage());
        }
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
        return dataList.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
       ImageView img_profile;
       TextView txt_labourId,txt_labourName;
       CheckBox cb_present;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            img_profile=itemView.findViewById(R.id.img_profile);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);
            cb_present=itemView.findViewById(R.id.cb_present);


        }
    }
}
