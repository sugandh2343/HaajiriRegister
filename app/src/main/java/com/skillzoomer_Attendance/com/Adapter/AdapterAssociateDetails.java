package com.skillzoomer_Attendance.com.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelAssociateDetails;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAssociateDetails extends RecyclerView.Adapter<AdapterAssociateDetails.HolderAssociateDetails> {
    private Context context;
    private ArrayList<ModelAssociateDetails> associateDetailsArrayList;

    public AdapterAssociateDetails(Context context, ArrayList<ModelAssociateDetails> associateDetailsArrayList) {
        this.context = context;
        this.associateDetailsArrayList = associateDetailsArrayList;
    }

    @NonNull
    @Override
    public AdapterAssociateDetails.HolderAssociateDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_associate_details,parent,false);
        return new AdapterAssociateDetails.HolderAssociateDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAssociateDetails.HolderAssociateDetails holder, int position) {
        ModelAssociateDetails modelAssociateDetails=associateDetailsArrayList.get(holder.getAdapterPosition());

        holder.txt_date.setText(modelAssociateDetails.getDate());
        if(modelAssociateDetails.getStatus().equals("NormalLogin")){
            holder.txt_status.setText("LOGIN");
            holder.txt_status.setTextColor(context.getResources().getColor(R.color.darkGreen));
            float[] results = new float[1];
            Location.distanceBetween(modelAssociateDetails.getSiteLatitude(), modelAssociateDetails.getSiteLongitude(), modelAssociateDetails.getMemberLatitude()
                    , modelAssociateDetails.getMemberLongitude(), results);
            float distance = results[0];
            int dis_display = (int) distance;

                if (associateDetailsArrayList.get(holder.getAdapterPosition()).getSiteLongitude() > 0 && associateDetailsArrayList.get(holder.getAdapterPosition()).getMemberLongitude() > 0) {
                    holder.txt_distance.setVisibility(View.VISIBLE);
                    holder.txt_distance.setText(dis_display + ("m"));
                } else {
                    holder.txt_distance.setText("NA");
                }



        }
        else if(modelAssociateDetails.getStatus().equals("normalLogout")){
            holder.txt_status.setText("LOG-OUT");
            holder.txt_status.setTextColor(context.getResources().getColor(R.color.red));
            float[] results = new float[1];
            Location.distanceBetween(modelAssociateDetails.getSiteLatitude(), modelAssociateDetails.getSiteLongitude(), modelAssociateDetails.getMemberLatitude()
                    , modelAssociateDetails.getMemberLongitude(), results);
            float distance = results[0];
            int dis_display = (int) distance;

            if (associateDetailsArrayList.get(holder.getAdapterPosition()).getSiteLongitude() > 0 && associateDetailsArrayList.get(holder.getAdapterPosition()).getMemberLongitude() > 0) {
                holder.txt_distance.setVisibility(View.VISIBLE);
                holder.txt_distance.setText(dis_display + ("m"));
            } else {
                holder.txt_distance.setText("NA");
            }
            holder.txt_selfie.setText("-");
        }
        else if(modelAssociateDetails.getStatus().equals("NA")){
            holder.txt_status.setText("-");
            holder.txt_time.setText("-");
            holder.txt_selfie.setText("-");
            holder.txt_distance.setText("-");
        }else{
            holder.txt_status.setText(modelAssociateDetails.getStatus());
        }
        Log.e("Holder",modelAssociateDetails.getTime());
        holder.txt_time.setText(modelAssociateDetails.getTime());
        if(modelAssociateDetails.getStatus().equals("NormalLogin")){
            holder.txt_selfie.setText("VIEW");
            holder.txt_selfie.setTextColor(context.getResources().getColor(R.color.darkBlue));
            holder.txt_selfie.setTypeface(Typeface.DEFAULT_BOLD);
            holder.txt_selfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProfileDialog(modelAssociateDetails.getProfile());
                }
            });

        }
        if(modelAssociateDetails.getName()!=null){
            holder.txt_name.setText(modelAssociateDetails.getName());

        }else{
            holder.txt_name.setText("-");
        }

    }

    private void showProfileDialog(String profile) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.show_image, null);

        ImageView iv_profile = (ImageView) mView.findViewById(R.id.iv_profile);
        Picasso.get().load(profile).
                resize(400, 400).centerCrop()
                .placeholder(R.drawable.ic_add).into(iv_profile);


        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);


        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return associateDetailsArrayList.size();
    }

    public class HolderAssociateDetails extends RecyclerView.ViewHolder {
        TextView txt_name,txt_status,txt_time,txt_distance,txt_selfie,txt_date;


        public HolderAssociateDetails(@NonNull View itemView) {
            super(itemView);

            txt_name=itemView.findViewById(R.id.txt_name);
            txt_status=itemView.findViewById(R.id.txt_status);
            txt_time=itemView.findViewById(R.id.txt_time);
            txt_distance=itemView.findViewById(R.id.txt_distance);
            txt_selfie=itemView.findViewById(R.id.txt_selfie);
            txt_date=itemView.findViewById(R.id.txt_date);
        }
    }
}
