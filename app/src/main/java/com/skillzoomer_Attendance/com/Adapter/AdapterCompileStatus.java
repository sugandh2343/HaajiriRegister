package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterCompileStatus extends RecyclerView.Adapter<AdapterCompileStatus.HolderCompileStatus> {
    private Context context;
    private ArrayList<ModelCompileStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;
    private int Parentposition;
    private ArrayList<ModelLabour>labourArrayList;

    public AdapterCompileStatus(Context context, ArrayList<ModelDate> dateModelArrayList, int parentposition) {
        this.context = context;
        this.dateModelArrayList = dateModelArrayList;
        Parentposition = parentposition;
    }

    public AdapterCompileStatus(Context context,
                                ArrayList<ModelCompileStatus> modelLabourStatusArrayList, ArrayList<ModelDate> dateModelArrayList, int parentposition, ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        this.dateModelArrayList = dateModelArrayList;
        Parentposition = parentposition;
        this.labourArrayList = labourArrayList;
    }

    @NonNull
    @Override
    public AdapterCompileStatus.HolderCompileStatus onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_compile_date_single_row,parent,false);
        return new AdapterCompileStatus.HolderCompileStatus(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCompileStatus.HolderCompileStatus holder, int position) {
        holder.setIsRecyclable(false);
        Log.e("Position","Parent"+Parentposition);
            if(position==0){
                holder.txt_date.setText(modelLabourStatusArrayList.get((Parentposition* labourArrayList.size())+position).getDate());
                holder.txt_payment.setVisibility(View.GONE);
                holder.txt_date.setTypeface(Typeface.DEFAULT_BOLD);
                holder.txt_date.setTextSize(10.0F);
                holder.txt_date.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);


            }else{
                String status="";
                Log.e("MStatusSize",""+modelLabourStatusArrayList.size());
                holder.txt_date.setText(modelLabourStatusArrayList.get(((Parentposition* labourArrayList.size())+(position-1))).getStatus());
                holder.txt_payment.setText(modelLabourStatusArrayList.get(((Parentposition* labourArrayList.size())+(position-1))).getAmount());
                if(modelLabourStatusArrayList.get(((Parentposition* labourArrayList.size())+(position-1))).getStatus().equals("A")){
                    holder.txt_date.setTextColor(context.getColor(R.color.red));
                }else{
                    holder.txt_date.setTextColor(context.getColor(R.color.darkGreen));
                }
                if(modelLabourStatusArrayList.get(((Parentposition* labourArrayList.size())+(position-1))).getAmount().equals("0")){
                    holder.txt_payment.setTextColor(context.getColor(R.color.red));
                }else{
                    holder.txt_payment.setTextColor(context.getColor(R.color.darkGreen));
                }



//                for(int i=0;i<modelLabourStatusArrayList.size();i++){
//                    Log.e("StatusAdapter","Value of i"+i);
////                Log.e("StatusAdapter","DateModelArrayList:"+dateModelArrayList.get(position).getDate());
//                    Log.e("StatusAdapter","ModelLabourDate"+modelLabourStatusArrayList.get(i).getDate());
////                Log.e("StatusAdapter","ParentLabourList"+labourArrayList.get(Parentposition-1).getLabourId());
//                    Log.e("StatusAdapter","LabourStatusArrayList"+modelLabourStatusArrayList.get(i).getLabourId());
//                    Log.e("StatusAdapter","Status"+modelLabourStatusArrayList.get(i).getStatus());
//
//                        if(dateModelArrayList.get(position).getDate().equals(modelLabourStatusArrayList.get(i).getDate())
//                                &&labourArrayList.get(Parentposition-1).getLabourId().equals(modelLabourStatusArrayList.get(i).getLabourId())){
//                            Log.e("TrueAt",""+i);
//                            holder.txt_date.setText(modelLabourStatusArrayList.get(i).getStatus());
//                            holder.txt_payment.setText(modelLabourStatusArrayList.get(i).getAmount());
//                            if(modelLabourStatusArrayList.get(i).getStatus().equals("A")){
//                                holder.txt_date.setTextColor(context.getResources().getColor(R.color.red));
//                            }else{
//                                holder.txt_date.setTextColor(context.getResources().getColor(R.color.darkGreen));
//                            }
//
//                        }
//
//
//                }

            }





    }

    @Override
    public int getItemCount() {
        return labourArrayList.size()+1;
    }

    public class HolderCompileStatus extends RecyclerView.ViewHolder {
        private TextView txt_date,txt_payment;
        public HolderCompileStatus(@NonNull View itemView) {
            super(itemView);
            txt_date=itemView.findViewById(R.id.txt_date);
            txt_payment=itemView.findViewById(R.id.txt_payment);
        }
    }
}
