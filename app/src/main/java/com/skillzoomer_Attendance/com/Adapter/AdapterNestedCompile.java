package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelCompileStatus;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterNestedCompile extends RecyclerView.Adapter<AdapterNestedCompile.HolderShowCompile> {
    private Context context;
    private ArrayList<ModelLabour>labourArrayList;
    private ArrayList<ModelCompileStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;

    public AdapterNestedCompile(Context context, ArrayList<ModelLabour> labourArrayList,
                                ArrayList<ModelCompileStatus> modelLabourStatusArrayList, ArrayList<ModelDate> dateModelArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        this.dateModelArrayList = dateModelArrayList;
    }

    @NonNull
    @Override
    public AdapterNestedCompile.HolderShowCompile onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_nested_compile_child,parent,false);
        return new AdapterNestedCompile.HolderShowCompile(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNestedCompile.HolderShowCompile holder, int position) {
        AdapterCompileStatus adapterCompileStatus=new AdapterCompileStatus(context,modelLabourStatusArrayList,dateModelArrayList,position,labourArrayList);
        holder.rv_compile_status.setAdapter(adapterCompileStatus);
        holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return dateModelArrayList.size();
    }

    public class HolderShowCompile extends RecyclerView.ViewHolder {

        RecyclerView rv_compile_status;
        public HolderShowCompile(@NonNull View itemView) {
            super(itemView);
            rv_compile_status=itemView.findViewById(R.id.rv_compile_status);
        }
    }
}
