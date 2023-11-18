package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelPresentLabour;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterAttendance extends RecyclerView.Adapter<AdapterAttendance.HolderAttendance> {
    private Context context;
    public ArrayList<ModelPresentLabour> presentLabourArrayListlabourArrayList;
    public String workerType;

    public AdapterAttendance(Context context, ArrayList<ModelPresentLabour> presentLabourArrayListlabourArrayList, String workerType) {
        this.context = context;
        this.presentLabourArrayListlabourArrayList = presentLabourArrayListlabourArrayList;
        this.workerType = workerType;
    }

    @NonNull
    @Override
    public HolderAttendance onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_attendance_master_single_row,parent,false);
        return new HolderAttendance(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAttendance holder , int position) {
        ModelPresentLabour modelPresentLabour;
        holder.setIsRecyclable(false);
        modelPresentLabour = presentLabourArrayListlabourArrayList.get(position);
        holder.txt_company_id.setText(modelPresentLabour.getLabourId());
        holder.txt_name.setText(modelPresentLabour.getLabourName());
        holder.txt_date.setText(modelPresentLabour.getDate());
        holder.txt_time.setText(modelPresentLabour.getTime());
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("attendance_position");
                Log.e("ADP!","HOLDER:"+holder.getAdapterPosition());

                intent.putExtra("position1", holder.getAdapterPosition());
                intent.putExtra("workerDeletedType", workerType);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return presentLabourArrayListlabourArrayList.size();
    }

    public class HolderAttendance extends RecyclerView.ViewHolder {
        private TextView txt_company_id,txt_name,txt_date,txt_time;
        private ImageButton img_delete;
        public HolderAttendance(@NonNull View itemView) {
            super(itemView);
            txt_company_id=itemView.findViewById(R.id.txt_company_id);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_date=itemView.findViewById(R.id.txt_date);
            txt_time=itemView.findViewById(R.id.txt_time);
            img_delete=itemView.findViewById(R.id.img_delete);

        }
    }
}
