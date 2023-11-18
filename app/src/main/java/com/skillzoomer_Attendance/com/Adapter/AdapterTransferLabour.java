package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterTransferLabour extends RecyclerView.Adapter<AdapterTransferLabour.HolderTransferLabour> {
    private Context context;
    private ArrayList<ModelLabour> labourArrayList;

    public AdapterTransferLabour(Context context, ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
    }

    @NonNull
    @Override
    public AdapterTransferLabour.HolderTransferLabour onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.transfer_labour_data_single_row,parent,false);
        return new AdapterTransferLabour.HolderTransferLabour(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTransferLabour.HolderTransferLabour holder, int position) {
        holder.setIsRecyclable(false);
        ModelLabour modelLabour=labourArrayList.get(position);
        if(modelLabour.getProfile()!=null && modelLabour.getProfile()!=""){
            Picasso.get().load(modelLabour.getProfile()).
                    resize(400,400).centerCrop()
                    .placeholder(R.drawable.userprofile).into(holder.img_profile);
        }

        holder.txt_name.setText(modelLabour.getName());
        holder.txt_wages.setText(String.valueOf(modelLabour.getWages()));
        holder.txt_labour_id.setText(modelLabour.getLabourId());
        holder.txt_type.setText(modelLabour.getType());

        holder.cb_select.setChecked(modelLabour.getPresent());


        holder.cb_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.cb_select.isChecked()){
                    Intent intent = new Intent("site_position");

                    if(modelLabour.getOriginalPosition()>0){
                        intent.putExtra("position",labourArrayList.get(holder.getAdapterPosition()).getOriginalPosition());
                    }else{
                        intent.putExtra("position",holder.getAdapterPosition());
                    }



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






    }

    @Override
    public int getItemCount() {
        return labourArrayList.size();
    }



    public class HolderTransferLabour extends RecyclerView.ViewHolder {
        CheckBox cb_select;
        ImageView img_profile;
        TextView txt_name,txt_labour_id,txt_wages,txt_type;
        public HolderTransferLabour(@NonNull View itemView) {

            super(itemView);
            cb_select=itemView.findViewById(R.id.cb_select);
            img_profile=itemView.findViewById(R.id.img_profile);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_labour_id=itemView.findViewById(R.id.txt_labour_id);
            txt_wages=itemView.findViewById(R.id.txt_wages);
            txt_type=itemView.findViewById(R.id.txt_type);
        }
    }
}
