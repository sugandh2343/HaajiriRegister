package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
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

public class AdapterShowCompile extends RecyclerView.Adapter<AdapterShowCompile.HolderShowCompile> {
    private Context context;
    private ArrayList<ModelLabour>labourArrayList;
    private ArrayList<ModelCompileStatus> modelLabourStatusArrayList;
    private ArrayList<ModelDate> dateModelArrayList;

    public AdapterShowCompile(Context context, ArrayList<ModelLabour> labourArrayList,
                              ArrayList<ModelCompileStatus> modelLabourStatusArrayList, ArrayList<ModelDate> dateModelArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
        this.modelLabourStatusArrayList = modelLabourStatusArrayList;
        this.dateModelArrayList = dateModelArrayList;
    }

    @NonNull
    @Override
    public AdapterShowCompile.HolderShowCompile onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_compile_single_row,parent,false);
        return new AdapterShowCompile.HolderShowCompile(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShowCompile.HolderShowCompile holder, int position) {
        if(holder.getAdapterPosition()==0){
            holder.txt_labourId.setText("Labour Id");
            holder.txt_labourName.setText("Labour Name");
            holder.txt_laborType.setText("Type");
            holder.txt_labour_wages.setText("Wages");
            Log.e("DateAdapter",""+dateModelArrayList.size());


        }else {
            Log.e("Position",""+position);
            holder.txt_labourName.setText(labourArrayList.get(position - 1).getName());
            holder.txt_labourId.setText(labourArrayList.get(position - 1).getLabourId());
            holder.txt_laborType.setText(labourArrayList.get(position - 1).getType());
            holder.txt_labour_wages.setText(String.valueOf(labourArrayList.get(position - 1).getWages()));






        }

    }

    @Override
    public int getItemCount() {
        return labourArrayList.size()+1;
    }

    public class HolderShowCompile extends RecyclerView.ViewHolder {
        TextView txt_labourId,txt_labourName,txt_laborType,txt_labour_wages;
        RecyclerView rv_status;
        public HolderShowCompile(@NonNull View itemView) {
            super(itemView);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);
            txt_laborType=itemView.findViewById(R.id.txt_laborType);
            txt_labour_wages=itemView.findViewById(R.id.txt_labour_wages);
            rv_status=itemView.findViewById(R.id.rv_status);
        }
    }
}
