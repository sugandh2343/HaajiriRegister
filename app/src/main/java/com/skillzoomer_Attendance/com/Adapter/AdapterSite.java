package com.skillzoomer_Attendance.com.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Activity.AddResourcesActivity;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Activity.SiteActivity;
import com.skillzoomer_Attendance.com.Activity.UpdateSiteActivity;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class AdapterSite extends RecyclerView.Adapter<AdapterSite.HolderSite> {
    private Context context;
    public ArrayList<ModelSite> modelSiteArayList;

    public AdapterSite(Context context , ArrayList<ModelSite> modelSiteArayList) {
        this.context = context;
        this.modelSiteArayList = modelSiteArayList;
    }

    @NonNull
    @Override
    public HolderSite onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_site_single_row,parent,false);
        return new HolderSite(view);
    }

    @Override

    public void onBindViewHolder(@NonNull HolderSite holder , int position) {
        ModelSite modelSite=modelSiteArayList.get(position);
        holder.txt_site_id.setText(String.valueOf(modelSite.getSiteId()));
        holder.txt_site_location.setText(modelSite.getSiteCity());
        holder.txt_site_name.setText(modelSite.getSiteName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new MaterialShowcaseView.Builder((Activity) context)
                    .setTarget(holder.txt_site_name_ll)
                    .setGravity(2)
                    .withOvalShape()
                    .setDismissOnTouch(true)
                    .setContentText(context.getString(R.string.add_team_member))// optional but starting animations immediately in onCreate can make them choppy
                    .setContentTextColor(context.getColor(R.color.white))
                    .singleUse("siteName")
                    .show();
        }
        holder.txt_site_name_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapter_position = holder.getAdapterPosition();

                Intent intent = new Intent(context, SiteActivity.class);
                intent.putExtra("position", adapter_position);
                Log.e("AdapterSite",""+modelSiteArayList.get(adapter_position).getSiteId());
                intent.putExtra("siteID", modelSiteArayList.get(adapter_position).getSiteId());
                intent.putExtra("siteName",modelSiteArayList.get(adapter_position).getSiteName());
                intent.putExtra("siteTimestamp",modelSiteArayList.get(adapter_position).getTimestamp());
                intent.putExtra("siteStatus",modelSiteArayList.get(adapter_position).getSiteStatus());
                context.startActivity(intent);

            }
        });
        holder.ic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ModelSite",""+modelSite.getEndTime());
                Intent intent=new Intent(context, UpdateSiteActivity.class);
                intent.putExtra("siteName",modelSite.getSiteName());
                intent.putExtra("siteId",modelSite.getSiteId());
                intent.putExtra("siteCity",modelSite.getSiteCity());
                intent.putExtra("siteStart",modelSite.getStartTime());
                intent.putExtra("siteEnd",modelSite.getEndTime());
                intent.putExtra("siteAddress",modelSite.getSiteAddress());
                context.startActivity(intent);
            }
        });
//        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(context);
//                builder.setTitle(R.string.coming_soon)
//                        .setMessage(R.string.site_deletion_coming_soon)
//                        .setCancelable(true)
//                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
//                            dialogInterface.dismiss();
//
//                        });
//
//                builder.show();
//            }
//
//        });


    }

    @Override
    public int getItemCount() {
        return modelSiteArayList.size();

    }

    public class HolderSite extends RecyclerView.ViewHolder {
        private TextView txt_site_id,txt_site_name,txt_site_location;
        ImageButton ic_delete,ic_edit;
        LinearLayout txt_site_name_ll;
        public HolderSite(@NonNull View itemView) {
            super(itemView);
            txt_site_id=itemView.findViewById(R.id.txt_site_id);
            txt_site_name=itemView.findViewById(R.id.txt_site_name);
            txt_site_location=itemView.findViewById(R.id.txt_site_location);
            ic_delete=itemView.findViewById(R.id.img_delete);
            ic_edit=itemView.findViewById(R.id.img_edit);
            txt_site_name_ll=itemView.findViewById(R.id.txt_site_name_ll);
        }
    }
}
