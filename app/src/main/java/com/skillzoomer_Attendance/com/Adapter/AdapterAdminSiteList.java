package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelAdminSite;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterAdminSiteList extends RecyclerView.Adapter<AdapterAdminSiteList.HolderAdminSite> {
    private Context context;
    private ArrayList<ModelAdminSite> modelAdminSiteArrayList;

    public AdapterAdminSiteList(Context context, ArrayList<ModelAdminSite> modelAdminSiteArrayList) {
        this.context = context;
        this.modelAdminSiteArrayList = modelAdminSiteArrayList;
    }

    @NonNull
    @Override
    public AdapterAdminSiteList.HolderAdminSite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_site_display_single,parent,false);
        return new AdapterAdminSiteList.HolderAdminSite(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAdminSiteList.HolderAdminSite holder, int position) {
        ModelAdminSite modelAdminSite=modelAdminSiteArrayList.get(position);
        holder.txt_site_id.setText(""+modelAdminSite.getSiteId());
        holder.txt_site_att.setText(""+modelAdminSite.getAttendanceCount());
        holder.txt_site_tm.setText(""+modelAdminSite.getTm());
        holder.txt_site_labours.setText(""+modelAdminSite.getLabourCount());
        holder.txt_site_name.setText(modelAdminSite.getSiteName());



    }

    @Override
    public int getItemCount() {
        return modelAdminSiteArrayList.size();
    }

    public class HolderAdminSite extends RecyclerView.ViewHolder {
        TextView txt_site_id,txt_site_name,txt_site_tm,txt_site_labours,txt_site_att;
        public HolderAdminSite(@NonNull View itemView) {
            super(itemView);
            txt_site_id=itemView.findViewById(R.id.txt_site_id);
            txt_site_name=itemView.findViewById(R.id.txt_site_name);
            txt_site_tm=itemView.findViewById(R.id.txt_site_tm);
            txt_site_labours=itemView.findViewById(R.id.txt_site_labours);
            txt_site_att=itemView.findViewById(R.id.txt_site_att);
        }
    }
}
