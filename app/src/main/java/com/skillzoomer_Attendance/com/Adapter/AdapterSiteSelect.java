package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterSiteSelect extends RecyclerView.Adapter<AdapterSiteSelect.HolderSiteSelect> {
    private Context context;
    private ArrayList<ModelSite>  siteArrayList;

    public AdapterSiteSelect(Context context, ArrayList<ModelSite> siteArrayList) {
        this.context = context;
        this.siteArrayList = siteArrayList;
    }

    @NonNull
    @Override
    public AdapterSiteSelect.HolderSiteSelect onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_select_site,parent,false);
        return new AdapterSiteSelect.HolderSiteSelect(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSiteSelect.HolderSiteSelect holder, int position) {
        ModelSite modelSite=siteArrayList.get(position);
        holder.cb_spinner.setText(modelSite.getSiteCity());
        holder.cb_spinner.setChecked(modelSite.getSelected());
        holder.cb_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.cb_spinner.isChecked()){
                    Intent intent = new Intent("site_position");

                    intent.putExtra("position",holder.getAdapterPosition());
                    intent.putExtra("boolean",true);
                    Log.e("PositionSend",""+holder.getAdapterPosition()+" V:"+true);

//                    intent.putExtra("position1", holder.getAdapterPosition());
//                    intent.putExtra("workerDeletedType", workerType);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                    notifyDataSetChanged();
//                     notifyDataSetChanged();
                }else{
                    Intent intent = new Intent("site_position");

                    intent.putExtra("position",holder.getAdapterPosition());
                    intent.putExtra("boolean",false);
                    Log.e("PositionSend",""+holder.getAdapterPosition()+" V:"+false);

//                    intent.putExtra("position1", holder.getAdapterPosition());
//                    intent.putExtra("workerDeletedType", workerType);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        });
//        holder.cb_spinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b){
//                    Intent intent = new Intent("site_position");
//
//                     intent.putExtra("position",holder.getAdapterPosition());
//                     intent.putExtra("boolean",true);
//                     Log.e("PositionSend",""+holder.getAdapterPosition()+" V:"+true);
//
////                    intent.putExtra("position1", holder.getAdapterPosition());
////                    intent.putExtra("workerDeletedType", workerType);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
////                    notifyDataSetChanged();
////                     notifyDataSetChanged();
//
//                 }else{
//                    Intent intent = new Intent("site_position");
//
//                    intent.putExtra("position",holder.getAdapterPosition());
//                    intent.putExtra("boolean",false);
//                    Log.e("PositionSend",""+holder.getAdapterPosition()+" V:"+false);
//
////                    intent.putExtra("position1", holder.getAdapterPosition());
////                    intent.putExtra("workerDeletedType", workerType);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                }
//
//
////                else if(holder.getAdapterPosition()>0 && b){
////                     for(int i=1;i<siteArrayList.size();i++){
////                         if(i==holder.getAdapterPosition()){
////                             siteArrayList.get(holder.getAdapterPosition()).setSelected(true);
////                         }else if(!siteArrayList.get(holder.getAdapterPosition()).getSelected()){
////                             siteArrayList.get(holder.getAdapterPosition()).setSelected(false);
////                         }
////
////                     }
////
////                    Intent intent = new Intent("site_position");
////
////                    intent.putExtra("position",holder.getAdapterPosition());
////                    intent.putExtra("boolean",true);
////                    Log.e("Sending","1");
////
//////                    intent.putExtra("position1", holder.getAdapterPosition());
//////                    intent.putExtra("workerDeletedType", workerType);
////                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
////                 }else if(holder.getAdapterPosition()==0 && !b){
////                     for(int i=1;i<siteArrayList.size();i++){
////                         siteArrayList.get(i).setSelected(false);
////                     }
////                    Intent intent = new Intent("site_position");
////
////                    intent.putExtra("position",0);
////                    intent.putExtra("boolean",false);
////
//////                    intent.putExtra("position1", holder.getAdapterPosition());
//////                    intent.putExtra("workerDeletedType", workerType);
////                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//////                     notifyDataSetChanged();
////                 }else if(holder.getAdapterPosition()>0 && !b){
////                     siteArrayList.get(1).setSelected(false);
////                     siteArrayList.get(holder.getAdapterPosition()).setSelected(false);
////                    Intent intent = new Intent("site_position");
////
////                    intent.putExtra("position",holder.getAdapterPosition());
////                    intent.putExtra("boolean",false);
////
//////                    intent.putExtra("position1", holder.getAdapterPosition());
//////                    intent.putExtra("workerDeletedType", workerType);
////                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
////
////                    Log.e("Sending","2");
//////                     notifyDataSetChanged();
////                 }
//
//            }
//
//        });

    }

    @Override
    public int getItemCount() {
        return siteArrayList.size();
    }

    public class HolderSiteSelect extends RecyclerView.ViewHolder {
        CheckBox cb_spinner;
        public HolderSiteSelect(@NonNull View itemView) {
            super(itemView);
            cb_spinner=itemView.findViewById(R.id.cb_spinner);
        }
    }
}
