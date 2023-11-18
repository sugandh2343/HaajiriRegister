package com.skillzoomer_Attendance.com.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterMember extends RecyclerView.Adapter<AdapterMember.HolderMember>{
    private Context context;
    private ArrayList<ModelUser> memberArrayList;

    public AdapterMember(Context context , ArrayList<ModelUser> memberArrayList) {
        this.context = context;
        this.memberArrayList = memberArrayList;
    }

    @NonNull
    @Override
    public HolderMember onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_member_single_row,parent,false);
        return new HolderMember(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HolderMember holder , int position) {
        ModelUser modelMember=memberArrayList.get(position);
        holder.txt_force_logout_heading.setVisibility(View.GONE);
        holder.txt_pic_activity.setVisibility(View.GONE);
        holder.img_force_logout.setVisibility(View.VISIBLE);
        holder.img_pic_activity.setVisibility(View.VISIBLE);
        holder.img_pic_activity.setImageResource(R.drawable.ic_checked_true);
        holder.txt_name.setText(modelMember.getName());
        holder.img_attendance.setVisibility(View.VISIBLE);
        holder.img_cash.setVisibility(View.VISIBLE);
        holder.img_expense.setVisibility(View.VISIBLE);
       holder.txt_attendance.setVisibility(View.GONE);
       holder.txt_cash.setVisibility(View.GONE);
       holder.txt_expense.setVisibility(View.GONE);
//       Log.e("MemberStatus",modelMember.getMemberBlock());
       if(modelMember.getMemberBlock().equals("Active")){
           holder.txt_block_unblock.setText(context.getString(R.string.block));
           holder.txt_block_unblock.setBackgroundColor(context.getResources().getColor(R.color.red));
           holder.txt_block_unblock.setVisibility(View.VISIBLE);
       }else{
           holder.txt_block_unblock.setText(context.getString(R.string.unblock));
           holder.txt_block_unblock.setBackgroundColor(context.getResources().getColor(R.color.darkGreen));
           holder.txt_block_unblock.setVisibility(View.VISIBLE);
       }
        if(modelMember.getMemberBlock().equals("Blocked")&& memberArrayList.size()==1){
            holder.img_attendance.setVisibility(View.GONE);
            holder.img_pic_activity.setVisibility(View.GONE);
            holder.img_cash.setVisibility(View.GONE);
            holder.img_expense.setVisibility(View.GONE);
            holder.img_force_logout.setVisibility(View.GONE);
            holder.ll_main.setWeightSum(7);

        }else{
            holder.img_attendance.setVisibility(View.VISIBLE);
            holder.img_pic_activity.setVisibility(View.VISIBLE);
            holder.img_cash.setVisibility(View.VISIBLE);
            holder.img_expense.setVisibility(View.VISIBLE);
            holder.img_force_logout.setVisibility(View.VISIBLE);
            holder.ll_main.setWeightSum(7);
        }

       holder.txt_block_unblock.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String message;
               if(modelMember.getMemberBlock().equals("Active")){
                 message=context.getResources().getString(R.string.block_associate);
                 if(memberArrayList.size()==1){
                     String msg=context.getString(R.string.on_blocking);
                     androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                     builder.setTitle(R.string.sure)
                             .setMessage(msg)
                             .setCancelable(false)
                             .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                 holder.txt_block_unblock.setText(context.getString(R.string.unblock));
                                 holder.txt_block_unblock.setBackgroundColor(context.getResources().getColor(R.color.darkGreen));
                                removeFromMembers(modelMember.getSiteId(),memberArrayList.get(holder.getAdapterPosition()),modelMember.getMemberBlock().equals("Active"));

                             })
                             .setNegativeButton(R.string.no, (dialogInterface, i) ->
                                     dialogInterface.dismiss());
                     builder.show();

                 }else if(memberArrayList.size()>1){
                     if(memberArrayList.get(holder.getAdapterPosition()).getAttendanceManagement()||memberArrayList.get(holder.getAdapterPosition()).getCashManagement()|| memberArrayList.get(holder.getAdapterPosition()).getFinanceManagement()){
                         Toast.makeText(context, "You need to Re-distribute the power of associate you are blocking to other asscociates", Toast.LENGTH_LONG).show();
                     }else{
                         androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                         builder.setTitle(R.string.sure)
                                 .setMessage(message)
                                 .setCancelable(false)
                                 .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                     holder.txt_block_unblock.setText(context.getString(R.string.unblock));
                                     holder.txt_block_unblock.setBackgroundColor(context.getResources().getColor(R.color.darkGreen));
                                     updateStatusToMember(memberArrayList.get(holder.getAdapterPosition()),modelMember.getMemberBlock().equals("Active"));
                                 })
                                 .setNegativeButton(R.string.no, (dialogInterface, i) ->
                                         dialogInterface.dismiss());
                         builder.show();

                     }

                 }


               }else{
                   message=context.getResources().getString(R.string.unblock_associate);
                   if(memberArrayList.size()==1){
                       holder.txt_block_unblock.setText(context.getString(R.string.block));
                       holder.txt_block_unblock.setBackgroundColor(context.getResources().getColor(R.color.red));

                       addToMember(modelMember.getUid(),modelMember,modelMember.getMemberBlock().equals("Active"));
                   }else if(memberArrayList.size()>1){
                       androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                       builder.setTitle(R.string.sure)
                               .setMessage(message)
                               .setCancelable(false)
                               .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                   holder.txt_block_unblock.setText(context.getString(R.string.block));
                                   holder.txt_block_unblock.setBackgroundColor(context.getResources().getColor(R.color.red));
                                   updateStatusToMember(modelMember,modelMember.getMemberBlock().equals("Active"));
                               })
                               .setNegativeButton(R.string.no, (dialogInterface, i) ->
                                       dialogInterface.dismiss());
                       builder.show();
                   }

               }


           }
       });



        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(String.valueOf(modelMember.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("forceOpt")){
                    if(snapshot.child("forceOpt").getValue(Boolean.class)){
                        holder.img_force_logout.setImageResource(R.drawable.ic_checked_true);
                    }else{
                        holder.img_force_logout.setImageResource(R.drawable.ic_checked_false);
                    }

                }else{
                    holder.img_force_logout.setImageResource(R.drawable.ic_checked_false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(modelMember.getAttendanceManagement()){
            holder.img_attendance.setImageResource(R.drawable.ic_checked_true);
        }else{
            holder.img_attendance.setImageResource(R.drawable.ic_checked_false);
        }
        if(modelMember.getCashManagement()){
            holder.img_expense.setImageResource(R.drawable.ic_checked_true);
        }else{
            holder.img_expense.setImageResource(R.drawable.ic_checked_false);
        }
        Log.e("ADAPTER",modelMember.getUid());
        if(modelMember.getFinanceManagement()){
            holder.img_cash.setImageResource(R.drawable.ic_checked_true);
        }else{
            holder.img_cash.setImageResource(R.drawable.ic_checked_false);
        }

    }

    private void addToMember(String uid, ModelUser modelMember, boolean active) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("memberStatus","Registered");
        hashMap.put("memberUid",uid);
        hashMap.put("hruid", FirebaseAuth.getInstance().getUid());
        hashMap.put("attendanceManagement",modelMember.getAttendanceManagement());
        hashMap.put("cashManagement",modelMember.getCashManagement());
        hashMap.put("financeManagement",modelMember.getFinanceManagement());
        hashMap.put("workActivity",modelMember.getWorkActivity());
        hashMap.put("mobile",modelMember.getMobile());
        hashMap.put("forceLogout",modelMember.getForceOpt());
        hashMap.put("name",modelMember.getName());

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getUid()).child("Industry").child("Construction").child("Site").
                child(String.valueOf(modelMember.getSiteId())).child("Members").child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               updateToMemberStatusToSite(modelMember,active);
            }
        });

    }

    private void updateToMemberStatusToSite(ModelUser modelMember, boolean active) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("memberStatus","Registered");
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
        reference1.child(FirebaseAuth.getInstance().getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(modelMember.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateStatusToMember(modelMember, active);
            }
        });
    }

    private void removeFromMembers(long siteId, ModelUser modelUser, boolean active) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               HashMap<String,Object> hashMap=new HashMap<>();
               hashMap.put("memberStatus","Pending");
               DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
               reference1.child(FirebaseAuth.getInstance().getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       updateStatusToMember(modelUser,active);
                   }
               });
            }
        });
    }

    private void updateStatusToMember(ModelUser modelUser, boolean active) {
        HashMap<String,Object> hashMap=new HashMap<>();
        if(active){
            hashMap.put("memberBlock","Blocked");
        }else{
            hashMap.put("memberBlock","Active");
        }

       DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
       reference.child(modelUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {

           @Override
           public void onSuccess(Void unused) {
               if(active){
                   Toast.makeText(context, "Associate Blocked.", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent("Refresh");

                   intent.putExtra("position", "change");

                   LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
               }else{
                   Toast.makeText(context, "Associate Unblocked.", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent("Refresh");

                   intent.putExtra("position", "change");

                   LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
               }

           }
       });
    }

    @Override
    public int getItemCount() {
        return memberArrayList.size();
    }

    public class HolderMember extends RecyclerView.ViewHolder {
        TextView txt_name,txt_force_logout_heading,txt_pic_activity,txt_attendance,txt_cash,txt_expense,txt_block_unblock;
        ImageView img_force_logout,img_pic_activity,img_attendance,img_cash,img_expense;
        LinearLayout ll_main;
        public HolderMember(@NonNull View itemView) {
            super(itemView);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_force_logout_heading=itemView.findViewById(R.id.txt_force_logout_heading);
            txt_pic_activity=itemView.findViewById(R.id.txt_pic_activity);
            img_force_logout=itemView.findViewById(R.id.img_force_logout);
            img_pic_activity=itemView.findViewById(R.id.img_pic_activity);
            txt_attendance=itemView.findViewById(R.id.txt_attendance);
            txt_cash=itemView.findViewById(R.id.txt_cash);
            txt_expense=itemView.findViewById(R.id.txt_expense);
            img_attendance=itemView.findViewById(R.id.img_attendance);
            img_cash=itemView.findViewById(R.id.img_cash);
            img_expense=itemView.findViewById(R.id.img_expense);

            txt_block_unblock=itemView.findViewById(R.id.txt_block_unblock);
            ll_main=itemView.findViewById(R.id.ll_main);




        }
    }
}
