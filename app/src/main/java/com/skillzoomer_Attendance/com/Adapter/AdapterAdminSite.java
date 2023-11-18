package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Activity.MemberTimelineActivity;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterAdminSite extends RecyclerView.Adapter<AdapterAdminSite.HolderAdminSite> {
    private Context context;
    private ArrayList<ModelSite> siteArrayList;

    public AdapterAdminSite(Context context, ArrayList<ModelSite> siteArrayList) {
        this.context = context;
        this.siteArrayList = siteArrayList;
    }

    @NonNull
    @Override
    public AdapterAdminSite.HolderAdminSite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_site_admin_single_row,parent,false);
        return new AdapterAdminSite.HolderAdminSite(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAdminSite.HolderAdminSite holder, int position) {
        ModelSite modelSite=siteArrayList.get(position);
        holder.txt_site_id.setText(String.valueOf(modelSite.getSiteId()));
        holder.txt_site_name.setText(modelSite.getSiteCity());
        holder.txt_associate_status.setText(modelSite.getMemberStatus());
        if(modelSite.getSkilled()>0){
            holder.iv_sk_upload_status.setImageResource(R.drawable.ic_baseline_verified_24);
        }else{
            holder.iv_sk_upload_status.setImageResource(R.drawable.ic_cross);
        }
        if(modelSite.getUnskilled()>0){
            holder.iv_usk_upload_status.setImageResource(R.drawable.ic_baseline_verified_24);
        }else{
            holder.iv_usk_upload_status.setImageResource(R.drawable.ic_cross);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MemberTimelineActivity.class);
                intent.putExtra("siteId",siteArrayList.get(holder.getAdapterPosition()).getSiteId());
                intent.putExtra("siteName",siteArrayList.get(holder.getAdapterPosition()).getSiteCity());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return siteArrayList.size();
    }

    public class HolderAdminSite extends RecyclerView.ViewHolder {
        TextView txt_site_id,txt_site_name,txt_associate_status;
        ImageView iv_sk_upload_status,iv_usk_upload_status;
        public HolderAdminSite(@NonNull View itemView) {
            super(itemView);
            txt_site_id=itemView.findViewById(R.id.txt_site_id);
            txt_site_name=itemView.findViewById(R.id.txt_site_name);
            txt_associate_status=itemView.findViewById(R.id.txt_associate_status);
            iv_sk_upload_status=itemView.findViewById(R.id.iv_sk_upload_status);
            iv_usk_upload_status=itemView.findViewById(R.id.iv_usk_upload_status);
        }
    }
}
