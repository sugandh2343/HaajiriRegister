package com.skillzoomer_Attendance.com.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.skillzoomer_Attendance.com.Model.ModelAssociateDetails;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdapterTeamMemberCard extends RecyclerView.Adapter<AdapterTeamMemberCard.HolderTeamMemberCard> {
    private Context context;
    private ArrayList<ModelAssociateDetails> associateLoginList;
    private ArrayList<ModelAssociateDetails> associateLogoutList;
    private String siteName1;
    private long siteId1;

    public AdapterTeamMemberCard(Context context, ArrayList<ModelAssociateDetails> associateLoginList, ArrayList<ModelAssociateDetails> associateLogoutList, String siteName1, long siteId1) {
        this.context = context;
        this.associateLoginList = associateLoginList;
        this.associateLogoutList = associateLogoutList;
        this.siteName1 = siteName1;
        this.siteId1 = siteId1;
    }

    @NonNull
    @Override
    public AdapterTeamMemberCard.HolderTeamMemberCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_associate_details_single_row, parent, false);
        return new AdapterTeamMemberCard.HolderTeamMemberCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTeamMemberCard.HolderTeamMemberCard holder, int position) {
        holder.setIsRecyclable(false);


        Log.e("PALL123", "" + associateLoginList.size());
        Log.e("PALL123", "" + associateLogoutList.size());
        Date c;

        c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String currentDate = df.format(c);
        holder.txt_generated_on.setText(currentDate);
        holder.txt_site_name.setText(String.valueOf(siteId1)+siteName1);

        int j = position;

        holder.txt_ind_name.setText(context
                .getSharedPreferences("UserDetails",MODE_PRIVATE).getString("industryName",""));
        holder.txt_company_name.setText(context.getSharedPreferences("UserDetails",MODE_PRIVATE).getString("companyName",""));
        holder.txt_generated_on.setText(currentDate);
        holder.txt_site_name.setText(siteId1+"/ "+siteName1);



        if (associateLoginList.get(position).getName() != null && !associateLoginList.get(position).getName().equals("")) {
            holder.txt_name.setText(associateLoginList.get(position).getName());
        } else {
            holder.txt_name.setText("-");
        }

//            cellSrNo2.setCellValue(modelDayBookClassArrayList.get(i).getRec_from());
        if (associateLoginList.get(position).getStatus().equals("NormalLogin")) {
            holder.txt_login_time.setText(associateLoginList.get(position).getTime());
            holder.txt_type.setText(associateLoginList.get(position).getDate());
            Picasso.get().load(associateLoginList.get(position).getProfile()).
                    resize(400, 400).centerCrop()
                    .placeholder(R.drawable.profile).into(holder.img_profile);
//

        }
        if (associateLoginList.get(position).getSiteLatitude() > 0 && associateLoginList.get(position).getSiteLongitude() > 0 &&
                associateLoginList.get(position).getMemberLatitude() > 0 &&
                associateLoginList.get(position).getMemberLatitude() > 0) {
            float[] results = new float[1];
            Location.distanceBetween(associateLoginList.get(position).getSiteLatitude(), associateLoginList.get(position).getSiteLongitude(), associateLoginList.get(position).getMemberLatitude()
                    , associateLoginList.get(position).getMemberLongitude(), results);
            float distance = results[0];
            int dis_display = (int) distance;
            holder.txt_login_distance.setText(String.valueOf(dis_display) + ("m"));


        } else {
            holder.txt_login_distance.setText("NA");
        }


        if(position<associateLogoutList.size()){

            Log.e("Position",String.valueOf(position));
            if(associateLogoutList.get(position).getStatus()!=null&&associateLogoutList.get(position).getStatus().equals("normalLogout")){
                holder.txt_force_logout.setVisibility(View.GONE);
            }else{
                holder.txt_force_logout.setVisibility(View.VISIBLE);
            }
            if (associateLogoutList.get(position).getDate() == null || associateLogoutList.get(position).getDate().equals("")) {
                holder.txt_logout_time.setText("-");
                holder.txt_logout_distance.setText("-");
            }
            else {
                holder.txt_logout_time.setText(associateLogoutList.get(position).getTime());
                if (associateLogoutList.get(position).getSiteLatitude() > 0 && associateLogoutList.get(position).getSiteLongitude() > 0 &&
                        associateLogoutList.get(position).getMemberLatitude() > 0 &&
                        associateLogoutList.get(position).getMemberLatitude() > 0) {
                    float[] results = new float[1];
                    Location.distanceBetween(associateLogoutList.get(position).getSiteLatitude(), associateLogoutList.get(position).getSiteLongitude(), associateLogoutList.get(position).getMemberLatitude()
                            , associateLogoutList.get(position).getMemberLongitude(), results);
                    float distance = results[0];
                    int dis_display = (int) distance;
                    holder.txt_logout_distance.setText(String.valueOf(dis_display) + ("m"));


                } else {
                    holder.txt_logout_distance.setText("NA");
                }
            }
        }else{
            holder.txt_logout_time.setText("-");
            holder.txt_logout_distance.setText("-");
        }




    }

    @Override
    public int getItemCount() {
        return associateLoginList.size();
    }

    public class HolderTeamMemberCard extends RecyclerView.ViewHolder {
        TextView txt_type, txt_name, txt_status, txt_login_time, txt_login_distance, txt_logout_time, txt_ind_name, txt_generated_on, txt_company_name, txt_site_name, txt_logout_distance, txt_force_logout;
        ImageView img_profile;
        LinearLayout ll_heading;

        public HolderTeamMemberCard(@NonNull View itemView) {
            super(itemView);
            txt_type = itemView.findViewById(R.id.txt_type);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_status = itemView.findViewById(R.id.txt_status);
            img_profile = itemView.findViewById(R.id.img_profile);
            txt_ind_name = itemView.findViewById(R.id.txt_ind_name);
            txt_generated_on = itemView.findViewById(R.id.txt_generated_on);
            txt_company_name = itemView.findViewById(R.id.txt_company_name);
            txt_site_name = itemView.findViewById(R.id.txt_site_name);
            ll_heading = itemView.findViewById(R.id.ll_heading);
            txt_login_time = itemView.findViewById(R.id.txt_login_time);
            txt_login_distance = itemView.findViewById(R.id.txt_login_distance);
            txt_logout_time = itemView.findViewById(R.id.txt_logout_time);
            txt_logout_distance = itemView.findViewById(R.id.txt_logout_distance);
            txt_force_logout = itemView.findViewById(R.id.txt_force_logout);
        }
    }
}
