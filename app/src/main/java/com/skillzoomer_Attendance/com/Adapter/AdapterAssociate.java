package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelAssociateTimeline;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterAssociate extends RecyclerView.Adapter<AdapterAssociate.HolderAssociate>{
    private Context context;
    private ArrayList<ModelSite> siteArrayList;
    private ArrayList<ModelAssociateTimeline> userArrayList;
    ModelSite modelSite;
    String profile;
    private long siteId;

    public AdapterAssociate(Context context, ArrayList<ModelAssociateTimeline> userArrayList, long siteId) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.siteId = siteId;
    }

    @NonNull
    @Override
    public AdapterAssociate.HolderAssociate onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_member_timeline_single_row,parent,false);
        return new AdapterAssociate.HolderAssociate(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAssociate.HolderAssociate holder, int position) {
        holder.setIsRecyclable(false);
        ModelAssociateTimeline modelAssociateTimeline = userArrayList.get(position);
        holder.txt_member_name.setText(modelAssociateTimeline.getName());
        if(userArrayList.get(holder.getAdapterPosition()).getUid()!=null){
            getBlockedStatus(userArrayList.get(holder.getAdapterPosition()).getUid(),holder);
        }else{
            holder.ll_site_registered_member.setVisibility(View.VISIBLE);
            holder.txt_blocked.setVisibility(View.GONE);
        }

        if (modelAssociateTimeline.getOnline() != null && modelAssociateTimeline.getOnline()) {
            holder.associate_force_logout_member.setImageResource(R.drawable.images);
            holder.iv_associate_login_member.setImageResource(R.drawable.green);
            holder.txt_work_associate_time_member.setText(modelAssociateTimeline.getTime());
        }else{
            holder.associate_force_logout_member.setImageResource(R.drawable.images);
            holder.iv_associate_login_member.setImageResource(R.drawable.images);
            holder.txt_work_associate_time_member.setText(modelAssociateTimeline.getTime());
        }

        if (modelAssociateTimeline.getPicActivity() != null && modelAssociateTimeline.getPicActivity()) {
            float[] results = new float[1];
            holder.iv_pic_activity_member.setImageResource(R.drawable.green);
            holder.txt_pic_activity_time.setText(modelAssociateTimeline.getPicTime());



        } else {
            holder.pic_activity_distance_member.setVisibility(View.GONE);
        }


    }

    private void getBlockedStatus(String uid, HolderAssociate holder) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("memberBlock").getValue(String.class).equals("Active")){
                    holder.ll_site_registered_member.setVisibility(View.VISIBLE);
                    holder.txt_blocked.setVisibility(View.GONE);
                }else{
                    holder.ll_site_registered_member.setVisibility(View.GONE);
                    holder.txt_blocked.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class HolderAssociate extends RecyclerView.ViewHolder {
        TextView txt_member_name,txt_work_associate_time_member,txt_work_associate_distance_member,txt_skilled_member,txt_unskilled_member,
                txt_skilled_time_member,txt_unskilled_time_member,txt_blocked,
                txt_cashrequesttime,txt_cashRequest_member,txt_pic_activity_time,pic_activity_distance_member;
        ImageView img_chat,iv_associate_login_member,associate_force_logout_member,iv_pic_activity_member,iv_cashrequest_member,iv_reqstPendency_member;
        LinearLayout ll_site_registered_member;
        public HolderAssociate(@NonNull View itemView) {
            super(itemView);
            txt_member_name=itemView.findViewById(R.id.txt_member_name);
            txt_work_associate_time_member=itemView.findViewById(R.id.txt_work_associate_time_member);
            txt_work_associate_distance_member=itemView.findViewById(R.id.txt_work_associate_distance_member);
            txt_skilled_member=itemView.findViewById(R.id.txt_skilled_member);
            txt_unskilled_member=itemView.findViewById(R.id.txt_unskilled_member);
            txt_skilled_time_member=itemView.findViewById(R.id.txt_skilled_time_member);
            txt_unskilled_time_member=itemView.findViewById(R.id.txt_unskilled_time_member);
            iv_cashrequest_member=itemView.findViewById(R.id.iv_cashrequest_member);
            iv_reqstPendency_member=itemView.findViewById(R.id.iv_reqstPendency_member);
            txt_cashrequesttime=itemView.findViewById(R.id.txt_cashrequesttime);
            txt_cashRequest_member=itemView.findViewById(R.id.txt_cashRequest_member);
            txt_pic_activity_time=itemView.findViewById(R.id.txt_pic_activity_time);
            pic_activity_distance_member=itemView.findViewById(R.id.pic_activity_distance_member);
            img_chat=itemView.findViewById(R.id.img_chat);
            iv_associate_login_member=itemView.findViewById(R.id.iv_associate_login_member);
            associate_force_logout_member=itemView.findViewById(R.id.associate_force_logout_member);
            iv_pic_activity_member=itemView.findViewById(R.id.iv_pic_activity_member);
            txt_blocked=itemView.findViewById(R.id.txt_blocked);
            ll_site_registered_member=itemView.findViewById(R.id.ll_site_registered_member);
        }
    }
}
