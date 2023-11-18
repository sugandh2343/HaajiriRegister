package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Activity.GetSingleUserDetail;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterUserDetails extends RecyclerView.Adapter<AdapterUserDetails.HolderUserDetails>{
    private Context context;
    private ArrayList<ModelUser> userArrayList;

    public AdapterUserDetails(Context context, ArrayList<ModelUser> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public AdapterUserDetails.HolderUserDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_user_details,parent,false);
        return new AdapterUserDetails.HolderUserDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserDetails.HolderUserDetails holder, int position) {
        holder.setIsRecyclable(false);
        if(position>0){
            ModelUser modelUser=userArrayList.get(position);
            if(modelUser.getName()!=null){
                holder.txt_name.setText(modelUser.getName());

            }else{
                holder.txt_name.setText("Test");

            }
            if(modelUser.getCompanyName()!=null){
                holder.txt_company.setText(modelUser.getCompanyName());
            }else{
                holder.txt_company.setText("Test");
            }


            if(modelUser.getDateOfRegister()!=null){
                holder.txt_dor.setText(modelUser.getDateOfRegister());

            }else{
                holder.txt_dor.setText("Test");

            }


            if(modelUser.getMobile()!=null){
                holder.txt_mobile.setText(modelUser.getMobile());
            }else{
                holder.txt_mobile.setText("test");
            }
            holder.txt_userId.setText(modelUser.getUserId());
            holder.txt_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    String p = "tel:" + modelUser.getMobile();
                    i.setData(Uri.parse(p));
                    context.startActivity(i);
                }
            });

            holder.txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, GetSingleUserDetail.class);
                    intent.putExtra("uid",modelUser.getUid());
                    intent.putExtra("userId",modelUser.getUserId());
                    context.startActivity(intent);
                }
            });

        }



    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public class HolderUserDetails extends RecyclerView.ViewHolder {
        TextView txt_name,txt_mobile,txt_userId,txt_dor,txt_company;
        public HolderUserDetails(@NonNull View itemView) {
            super(itemView);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_mobile=itemView.findViewById(R.id.txt_mobile);
            txt_userId=itemView.findViewById(R.id.txt_userId);
            txt_dor=itemView.findViewById(R.id.txt_dor);
            txt_company=itemView.findViewById(R.id.txt_company);
        }
    }
}
