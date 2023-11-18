package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelTransaction;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Activity.TransactionDetailsActivity;

import java.util.ArrayList;

public class AdapterTransaction extends RecyclerView.Adapter<AdapterTransaction.HolderTransaction> {
    private Context context;
    private ArrayList<ModelTransaction> transactionArrayList;

    public AdapterTransaction(Context context, ArrayList<ModelTransaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;
    }

    @NonNull
    @Override
    public AdapterTransaction.HolderTransaction onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_transaction_single_row,parent,false);
        return new AdapterTransaction.HolderTransaction(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderTransaction holder, int position) {
        holder.setIsRecyclable(false);
        ModelTransaction modelTransaction=transactionArrayList.get(position);
        holder.txt_download_file_type.setText(modelTransaction.getDownloadFile());
        holder.txt_download_period.setText(modelTransaction.getFromDate()+" "+"To"+" "+modelTransaction.getToDate());
        holder.txt_payment_date.setText(modelTransaction.getDateOfPayment());
        holder.txt_payment_time.setText(modelTransaction.getTimeOfPayment());
        holder.txt_amount.setText(context.getString(R.string.rupee_symbol)+modelTransaction.getPaidAmount());
        if(modelTransaction.getStatus().equals("Success")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.status.setBackgroundColor(context.getColor(R.color.darkGreen));
            }
            holder.status.setText("Successful");

        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.status.setBackgroundColor(context.getColor(R.color.red));
            }
            holder.status.setText("Failed");
        }

        holder.txt_siteName.setText(modelTransaction.getSiteName());
//        holder.ll_details.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(context,TransactionDetailsActivity.class);
//                intent.putExtra("timestamp",modelTransaction.getTimestamp());
//                context.startActivity(intent);
//            }
//        });
        holder.img_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, TransactionDetailsActivity.class);
                intent.putExtra("timestamp",modelTransaction.getTimestamp());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    public class HolderTransaction extends RecyclerView.ViewHolder {
        TextView txt_download_file_type,txt_download_period,status,txt_payment_date,txt_payment_time,txt_amount,txt_siteName;
        LinearLayout ll_details;
        ImageView img_details;
        public HolderTransaction(@NonNull View itemView) {
            super(itemView);
            txt_download_file_type=itemView.findViewById(R.id.txt_download_file_type);
            txt_download_period=itemView.findViewById(R.id.txt_download_period);
            status=itemView.findViewById(R.id.status);
            txt_payment_date=itemView.findViewById(R.id.txt_payment_date);
            txt_payment_time=itemView.findViewById(R.id.txt_payment_time);
            ll_details=itemView.findViewById(R.id.ll_details);
            txt_amount=itemView.findViewById(R.id.txt_amount);
            txt_siteName=itemView.findViewById(R.id.txt_siteName);
            img_details=itemView.findViewById(R.id.img_details);
        }
    }
}
