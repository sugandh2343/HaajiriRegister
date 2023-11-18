package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Activity.AddEventActivity;
import com.skillzoomer_Attendance.com.Activity.ShowAttendanceActivity;
import com.skillzoomer_Attendance.com.Model.ModelEvent;
import com.skillzoomer_Attendance.com.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.HolderEvent> {
    private Context context;
    private ArrayList<ModelEvent> eventArrayList;
    private String Activity;
    long siteId;
    String siteName;

    public AdapterEvent(Context context, ArrayList<ModelEvent> eventArrayList, String activity, long siteId, String siteName) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        Activity = activity;
        this.siteId = siteId;
        this.siteName = siteName;
    }

    @NonNull
    @Override
    public AdapterEvent.HolderEvent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_event_single_row,parent,false);
        return new AdapterEvent.HolderEvent(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderEvent holder, int position) {
        ModelEvent modelEvent=eventArrayList.get(position);
        holder.name_TV.setText(modelEvent.getEventName());
        holder.mobile_TV.setText(modelEvent.getEventDate());
        String eventDate=modelEvent.getEventDate();
        String todayDate;

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        todayDate = df.format(c);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Boolean f = true;
        try {
            Date fDate = dateFormat.parse(todayDate);
            Date tDate = dateFormat.parse(eventDate);
            if(tDate.before(fDate)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status_TV.setBackgroundColor(context.getColor(R.color.red));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status_TV.setTextColor(context.getColor(R.color.white));
                }
                holder.status_TV.setText("Past");
                if(Activity.equals("Update")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "You cannot update a Past/Completed Event", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else if (todayDate.equals(eventDate)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status_TV.setBackgroundColor(context.getColor(R.color.darkGreen));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status_TV.setTextColor(context.getColor(R.color.white));
                }
                holder.status_TV.setText("Today");
                if(Activity.equals("Update")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(context, AddEventActivity.class);
                            intent.putExtra("id",modelEvent.getEventId());
                            intent.putExtra("name",modelEvent.getEventName());
                            intent.putExtra("date",modelEvent.getEventDate());
                            intent.putExtra("start",modelEvent.getEventStart());
                            intent.putExtra("end",modelEvent.getEventEnd());
                            intent.putExtra("category",modelEvent.getEventCategory());
                            intent.putExtra("addedBy",modelEvent.getAddedBy());
                            intent.putExtra("siteId",siteId);
                            intent.putExtra("siteName",siteName);
                            intent.putExtra("Activity","Update");
                            context.startActivity(intent);
                        }
                    });
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status_TV.setBackgroundColor(context.getColor(R.color.darkBlue));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status_TV.setTextColor(context.getColor(R.color.white));
                }
                holder.status_TV.setText("Upcoming");
                if(Activity.equals("Update")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(context, AddEventActivity.class);
                            intent.putExtra("id",modelEvent.getEventId());
                            intent.putExtra("name",modelEvent.getEventName());
                            intent.putExtra("date",modelEvent.getEventDate());
                            intent.putExtra("start",modelEvent.getEventStart());
                            intent.putExtra("end",modelEvent.getEventEnd());
                            intent.putExtra("category",modelEvent.getEventCategory());
                            intent.putExtra("addedBy",modelEvent.getAddedBy());
                            intent.putExtra("siteId",siteId);
                            intent.putExtra("siteName",siteName);
                            intent.putExtra("Activity","Update");
                            context.startActivity(intent);
                        }
                    });
                }
            }
//            Log.e("ADAPETer",""+(Activity==null));
//            Log.e("ADAPETer",""+(modelEvent.getStatus()==null));

//            if(Activity.equals("Update") && (modelEvent.getStatus().equals("Today")||modelEvent.getStatus().equals("Upcoming"))){
//
//            }else if(Activity.equals("Update") && modelEvent.getStatus().equals("Past")){
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(context, "You cannot update a Past/Completed Event", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }


        } catch (ParseException e) {
            e.printStackTrace();
        }




    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    public class HolderEvent extends RecyclerView.ViewHolder {
        TextView name_TV,mobile_TV,status_TV;
        public HolderEvent(@NonNull View itemView) {
            super(itemView);
            status_TV=itemView.findViewById(R.id.status_TV);
            name_TV=itemView.findViewById(R.id.name_TV);
            mobile_TV=itemView.findViewById(R.id.mobile_TV);
        }
    }
}
