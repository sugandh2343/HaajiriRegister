package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class
AdapterNestedChild2 extends RecyclerView.Adapter<AdapterNestedChild2.HolderStatus> {
    private Context context;
    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;
    private int Parentposition;
    private ArrayList<ModelLabour>labourArrayList;

    public AdapterNestedChild2(Context context , ArrayList<ModelLabourStatus> modelLabourStatusArrayList , ArrayList<ModelDate> dateModelArrayList , int parentposition , ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        this.dateModelArrayList = dateModelArrayList;
        Parentposition = parentposition;
        this.labourArrayList = labourArrayList;
    }

    public AdapterNestedChild2(Context context , ArrayList<ModelDate> dateModelArrayList , int parentposition) {
        this.context = context;
        this.dateModelArrayList = dateModelArrayList;
        Parentposition = parentposition;
    }

    @NonNull
    @Override
    public HolderStatus onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_date_single_column,parent,false);
        return new HolderStatus(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderStatus holder , int position) {
        holder.setIsRecyclable(false);
        Log.e("ParentPosition","Parent"+Parentposition);
        Log.e("ParentPosition","Child"+position);
        Log.e("ParentPosition","MLAL"+modelLabourStatusArrayList.size());
//        Log.e("Position","Date"+modelLabourStatusArrayList.get(position).getDate());
//        Log.e("Position","Value"+position);
//        Log.e("Position","Value1"+holder.getAdapterPosition());
//        Log.e("Position","Experiment"+(Parentposition* labourArrayList.size())+position);
        holder.setIsRecyclable(false);


                Log.e("ChilDpOS",""+position);
                Log.e("ChilDpOS",""+((Parentposition* labourArrayList.size())+(position)));
                holder.txt_date.setText(modelLabourStatusArrayList.get(((Parentposition* labourArrayList.size())+(position))).getStatus());
                if(modelLabourStatusArrayList.get(((Parentposition* labourArrayList.size())+(position))).getStatus().equals("A")||
                        (modelLabourStatusArrayList.get(((Parentposition* labourArrayList.size())+(position))).getStatus().equals("0"))){
                    holder.txt_date.setTextColor(context.getColor(R.color.red));
                }else{
                    holder.txt_date.setTextColor(context.getColor(R.color.darkGreen));
                }








    }

    @Override
    public int getItemCount() {
        return labourArrayList.size();
    }

    public class HolderStatus extends RecyclerView.ViewHolder {
        private TextView txt_date;
        public HolderStatus(@NonNull View itemView) {
            super(itemView);
            txt_date=itemView.findViewById(R.id.txt_date);
        }
    }
}
