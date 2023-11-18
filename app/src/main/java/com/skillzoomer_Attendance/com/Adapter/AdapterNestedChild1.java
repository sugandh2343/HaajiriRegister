package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabourStatus;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class
AdapterNestedChild1 extends RecyclerView.Adapter<AdapterNestedChild1.HolderStatus> {
    private Context context;
    private ArrayList<ModelLabourStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;

    private ArrayList<ModelLabour>labourArrayList;

    public AdapterNestedChild1(Context context , ArrayList<ModelLabourStatus> modelLabourStatusArrayList , ArrayList<ModelDate> dateModelArrayList  , ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        this.dateModelArrayList = dateModelArrayList;
        this.labourArrayList = labourArrayList;
    }

    public AdapterNestedChild1(Context context , ArrayList<ModelDate> dateModelArrayList ) {
        this.context = context;
        this.dateModelArrayList = dateModelArrayList;
    }

    @NonNull
    @Override
    public HolderStatus onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_child1,parent,false);
        return new HolderStatus(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderStatus holder , int position) {
        Log.e("ParentPosition","Original"+position);

        AdapterNestedChild2 adapterNestedChild2=new AdapterNestedChild2(context,modelLabourStatusArrayList,dateModelArrayList,position,labourArrayList);
        holder.rv_child1.setAdapter(adapterNestedChild2);
        holder.setIsRecyclable(false);


    }

    @Override
    public int getItemCount() {
        return dateModelArrayList.size();
    }

    public class HolderStatus extends RecyclerView.ViewHolder {
        private RecyclerView rv_child1;
        public HolderStatus(@NonNull View itemView) {
            super(itemView);
            rv_child1=itemView.findViewById(R.id.rv_child1);
        }
    }
}
