package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Activity.WorkplaceActivity;
import com.skillzoomer_Attendance.com.Model.ModelAssociateTimeline;
import com.skillzoomer_Attendance.com.Model.ModelEmployee;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelWorkPlace;
import com.skillzoomer_Attendance.com.R;

import java.util.ArrayList;

public class AdapterTimeline1 extends RecyclerView.Adapter<AdapterTimeline1.HolderTimeline1>{
    private Context context;
    private ArrayList<ModelWorkPlace> siteArrayList;
    private ArrayList<ModelAssociateTimeline> userArrayList;
    ModelSite modelSite;
    String profile;
    Boolean open = null;
    MediaPlayer mediaPlayer;
    private long onlinecount=0,offlinecount=0;
    private ArrayList<ModelEmployee> employeeArrayList;

    public AdapterTimeline1(Context context, ArrayList<ModelWorkPlace> siteArrayList) {
        this.context = context;
        this.siteArrayList = siteArrayList;
    }

    @NonNull
    @Override
    public AdapterTimeline1.HolderTimeline1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_timeline_1_single_row, parent, false);
        return new AdapterTimeline1.HolderTimeline1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTimeline1.HolderTimeline1 holder, int position) {
        holder.setIsRecyclable(false);
        employeeArrayList=new ArrayList<>();
        ModelWorkPlace modelWorkPlace=siteArrayList.get(position);
        holder.txt_site_name.setText(modelWorkPlace.getSiteName());
        holder.txt_siteId.setText(""+modelWorkPlace.getSiteId());
        holder.txt_site_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, WorkplaceActivity.class);
                intent.putExtra("siteId",modelWorkPlace.getSiteId());
                intent.putExtra("siteName",modelWorkPlace.getSiteName());
                context.startActivity(intent);
            }
        });
        if(modelWorkPlace.getPunchIn()!=null &&modelWorkPlace.getPunchIn()){
            holder.iv_associate_login.setImageResource(R.drawable.green);
        }else{
            holder.iv_associate_login.setImageResource(R.drawable.images);
        }
        if(modelWorkPlace.getPunchOut()!=null&& modelWorkPlace.getPunchOut()){
            holder.associate_force_logout.setImageResource(R.drawable.green);
        }else{
            holder.associate_force_logout.setImageResource(R.drawable.images);
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Industry").child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName","")).child("Site").child(String.valueOf(siteArrayList.get(position).getSiteId()))
                .child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            holder.img_drop_down.setVisibility(View.VISIBLE);
                            for(DataSnapshot ds:snapshot.getChildren()){
                                ModelEmployee modelEmployee=ds.getValue(ModelEmployee.class);
                                employeeArrayList.add(modelEmployee);

                            }

                        }else{
                            holder.img_drop_down.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return siteArrayList.size();
    }

    public class HolderTimeline1 extends RecyclerView.ViewHolder {
        TextView txt_site_name, txt_siteId, txt_work_associate, txt_work_associate_time, txt_skilled, txt_unskilled, txt_skilled_time, txt_unskilled_time, txt_cashrequesttime, txt_requestPendencytime, txt_manPower, txt_cashRequest, txt_requestPendency, txt_work_associate_distance,
                txt_pic_activity_time, pic_activity_distance, txt_admin, txt_admin1, txt_admin2, txt_req_details,txt_associate_request;
        LinearLayout ll_work_associate_button, ll_work_associate_time, ll_manPower_status, ll_cashReqstdetails,
                ll_reqstPendency, ll_manPower_time, ll_main, ll_skUsk, siteDetails_verify, ll_site_registered, ll_cash_site_registered, ll_site_pic;
        ImageView iv_associate_login, associate_force_logout, iv_cashrequest, iv_reqstPendency, iv_pic_activity, img_next, img_drop_down;
        LinearLayout ll_site_navigation,ll_member_list;
        View view_workAssociate, View_manpower, view_cashreqst, view_rqstPendency, view_skilled;
        RecyclerView rv_member_list;
        Button btn_check_siteDetails, btn_associate_request;
        LinearLayout  ll_associate_request;
        public HolderTimeline1(@NonNull View itemView) {
            super(itemView);
            txt_site_name=itemView.findViewById(R.id.txt_site_name);
            txt_siteId=itemView.findViewById(R.id.txt_siteId);
            ll_work_associate_button=itemView.findViewById(R.id.ll_work_associate_button);
            iv_associate_login=itemView.findViewById(R.id.iv_associate_login);
            associate_force_logout=itemView.findViewById(R.id.associate_force_logout);
            txt_skilled=itemView.findViewById(R.id.txt_skilled);
            txt_unskilled=itemView.findViewById(R.id.txt_unskilled);
            txt_skilled_time=itemView.findViewById(R.id.txt_skilled_time);
            txt_unskilled_time=itemView.findViewById(R.id.txt_unskilled_time);
            iv_cashrequest=itemView.findViewById(R.id.iv_cashrequest);
            ll_cashReqstdetails=itemView.findViewById(R.id.ll_cashReqstdetails);
            iv_pic_activity=itemView.findViewById(R.id.iv_pic_activity);
            ll_member_list=itemView.findViewById(R.id.ll_member_list);
            rv_member_list=itemView.findViewById(R.id.rv_member_list);


        }
    }
}
