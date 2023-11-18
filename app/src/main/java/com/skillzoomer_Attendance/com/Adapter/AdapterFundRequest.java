package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelFundRequest;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterFundRequest extends RecyclerView.Adapter<AdapterFundRequest.HolderFundRequest> {
    private Context context;
    private ArrayList<ModelFundRequest> modelFundRequestArrayList;

    public AdapterFundRequest(Context context, ArrayList<ModelFundRequest> modelFundRequestArrayList) {
        this.context = context;
        this.modelFundRequestArrayList = modelFundRequestArrayList;
    }

    @NonNull
    @Override
    public AdapterFundRequest.HolderFundRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_request_single_row,parent,false);
        return new AdapterFundRequest.HolderFundRequest(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFundRequest.HolderFundRequest holder, int position) {
        ModelFundRequest modelFundRequest=modelFundRequestArrayList.get(position);
        holder.req_id.setText(modelFundRequest.getReqId());
        holder.req_amt.setText(modelFundRequest.getAmount());
        holder.req_date.setText(modelFundRequest.getReqDate());
        holder.req_time.setText(modelFundRequest.getReqTime());
        if(modelFundRequest.getReqStatus().equals("ApprovedFull")){
            holder.req_status.setText(context.getString(R.string.allow_full_amount));
            holder.req_status.setTextColor(context.getResources().getColor(R.color.darkGreen));
        }else{
            holder.req_status.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return modelFundRequestArrayList.size();
    }

    public class HolderFundRequest extends RecyclerView.ViewHolder {
        TextView req_id,req_date,req_time,req_amt,req_status;
        public HolderFundRequest(@NonNull View itemView) {
            super(itemView);
            req_id=itemView.findViewById(R.id.req_id);
            req_date=itemView.findViewById(R.id.req_date);
            req_time=itemView.findViewById(R.id.req_time);
            req_amt=itemView.findViewById(R.id.req_amt);
            req_status=itemView.findViewById(R.id.req_status);
        }
    }
}
