package com.skillzoomer_Attendance.com.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skillzoomer_Attendance.com.Activity.TimelineOtherIndustry1;
import com.skillzoomer_Attendance.com.Model.ModelLeave;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterLeave extends RecyclerView.Adapter<AdapterLeave.HolderLeave> {
    private Context context;
    private ArrayList<ModelLeave> leaveArrayList;
    private long siteId;

    public AdapterLeave(Context context , ArrayList<ModelLeave> leaveArrayList , long siteId) {
        this.context = context;
        this.leaveArrayList = leaveArrayList;
        this.siteId = siteId;
    }

    @NonNull
    @Override
    public AdapterLeave.HolderLeave onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_show_leave_single_row,parent,false);
        return new AdapterLeave.HolderLeave(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLeave.HolderLeave holder , @SuppressLint("RecyclerView") int position) {
        ModelLeave modelLeave=leaveArrayList.get(position);
        holder.txt_name.setText(modelLeave.getLeaveappliedByName());
        holder.txt_days.setText(modelLeave.getLeaveDays());
        holder.txt_leave_type.setText(modelLeave.getLeaveType());
        holder.txt_status.setText(modelLeave.getLeaveStatus());
        holder.txt_leave_reason.setText(modelLeave.getReason());
        holder.txt_leave_applied_date.setText(modelLeave.getLeaveApplyDate());
        holder.txt_from.setText(modelLeave.getLeaveStartDate());
        holder.txt_to.setText(modelLeave.getLeaveEndDate());
        if(!modelLeave.getLeaveStatus().equals("Requested")){
            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_deny.setVisibility(View.GONE);
        }else{
            holder.btn_accept.setVisibility(View.VISIBLE);
            holder.btn_deny.setVisibility(View.VISIBLE);
        }
       holder.btn_accept.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
               builder.setTitle("Accept Leave").setMessage("Are you sure you want to accept?").setCancelable(false).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                   HashMap<String,Object> hashMap=new HashMap<>();
                   hashMap.put("leaveStatus","Approved");
                   hashMap.put("approvedBy", FirebaseAuth.getInstance().getUid());

                   DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                   reference .child(context.getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                           .child(context.getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                           .child("Site")
                           .child(String.valueOf(siteId)).child("LeaveRequest").child(modelLeave.getLeaveId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused) {
                                   Toast.makeText(context , "Leave Approved Successfully" , Toast.LENGTH_SHORT).show();
                                   reference.child(FirebaseAuth.getInstance().getUid()).child("Leaves").child(leaveArrayList.get(position).getLeaveId())
                                           .updateChildren(hashMap);
                               }
                           });

               }).setNegativeButton(R.string.no, (dialogInterface, i) ->{
                           dialogInterface.dismiss()  ;
               } );
               builder.show();

           }
       });
       holder.btn_deny.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
               builder.setTitle("Decline Leave").setMessage("Are you sure you want to Decline?").setCancelable(false).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                   HashMap<String,Object> hashMap=new HashMap<>();
                   hashMap.put("leaveStatus","Declined");
                   hashMap.put("approvedBy", FirebaseAuth.getInstance().getUid());

                   DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                   reference .child(context.getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry")
                           .child(context.getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""))
                           .child("Site")
                           .child(String.valueOf(siteId)).child("LeaveRequest").child(modelLeave.getLeaveId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused) {
                                   Toast.makeText(context , "Leave Declined Successfully" , Toast.LENGTH_SHORT).show();
                                   reference.child(FirebaseAuth.getInstance().getUid()).child("Leaves").child(leaveArrayList.get(position).getLeaveId())
                                           .updateChildren(hashMap);
                               }
                           });

               }).setNegativeButton(R.string.no, (dialogInterface, i) ->{
                   dialogInterface.dismiss()  ;
               } );
               builder.show();

           }
       });
    }

    @Override
    public int getItemCount() {
        return leaveArrayList.size();
    }

    public class HolderLeave extends RecyclerView.ViewHolder {
        TextView txt_name,
                txt_days,
                txt_status,
                txt_leave_type,
                txt_leave_reason,
                txt_from,
                txt_to,
                txt_leave_applied_date,
                txt_details;
        Button btn_accept,btn_deny;
        public HolderLeave(@NonNull View itemView) {
            super(itemView);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_days=itemView.findViewById(R.id.txt_days);
            txt_status=itemView.findViewById(R.id.txt_status);
            txt_leave_type=itemView.findViewById(R.id.txt_leave_type);
            txt_leave_reason=itemView.findViewById(R.id.txt_leave_reason);
            txt_from=itemView.findViewById(R.id.txt_from);
            txt_to=itemView.findViewById(R.id.txt_to);
            txt_leave_applied_date=itemView.findViewById(R.id.txt_leave_applied_date);
            txt_details=itemView.findViewById(R.id.txt_details);
            btn_accept=itemView.findViewById(R.id.btn_accept);
            btn_deny=itemView.findViewById(R.id.btn_deny);

        }
    }
}
