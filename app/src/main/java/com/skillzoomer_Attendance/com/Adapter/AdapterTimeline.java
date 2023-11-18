package com.skillzoomer_Attendance.com.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Activity.ChattingActivity;
import com.skillzoomer_Attendance.com.Activity.EditAttendanceActivity;
import com.skillzoomer_Attendance.com.Activity.MapsActivity;
import com.skillzoomer_Attendance.com.Activity.MemberTimelineActivity;
import com.skillzoomer_Attendance.com.Activity.timelineActivity;
import com.skillzoomer_Attendance.com.Model.ModelAssociateTimeline;
import com.skillzoomer_Attendance.com.Model.ModelLabourPresent;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Activity.ReplyActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class AdapterTimeline extends RecyclerView.Adapter<AdapterTimeline.HolderTimeline> {
    private Context context;
    private ArrayList<ModelSite> siteArrayList;
    private ArrayList<ModelAssociateTimeline> userArrayList;
    ModelSite modelSite;
    String profile;
    Boolean open = null;
    MediaPlayer mediaPlayer;

    FirebaseAuth firebaseAuth;

    SharedPreferences.Editor editorTutorial;


    public AdapterTimeline(Context context, ArrayList<ModelSite> siteArrayList) {
        this.context = context;
        this.siteArrayList = siteArrayList;
    }

    @NonNull
    @Override
    public HolderTimeline onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_timeline_single_row, parent, false);
        return new HolderTimeline(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull HolderTimeline holder, int position) {
        editorTutorial=context.getSharedPreferences("Tutorial", MODE_PRIVATE).edit();
        open = false;
        holder.setIsRecyclable(false);
        userArrayList = new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();

        holder.view_skilled.setVisibility(View.GONE);

        holder.txt_manPower.setVisibility(View.GONE);
        holder.txt_cashRequest.setVisibility(View.GONE);

        holder.ll_skUsk.setVisibility(View.GONE);
        modelSite = siteArrayList.get(holder.getAdapterPosition());
        if(position==0){
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(200);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                config.setContentTextColor(context.getColor(R.color.white));
                config.setMaskColor(context.getColor(R.color.black));

            }
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence((Activity) context, "Register");
            sequence.setConfig(config);
            if(siteArrayList.get(0).getMemberCount()>1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sequence.singleUse("timelineDetails");

                    if (context.getSharedPreferences("Tutorial", MODE_PRIVATE).getBoolean("timelineHr", true)){
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.img_drop_down)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapetr2A))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)

                                .build());


                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_associate_login)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getResources().getString(R.string.adapter2b))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)

                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.associate_force_logout)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getResources().getString(R.string.adapter2c))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.txt_skilled)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getResources().getString(R.string.adapter2d))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());

                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.txt_unskilled)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapter2e))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_cashrequest)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapetr2f))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_reqstPendency)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapter2g))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_pic_activity)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapter2h))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .setListener(new IShowcaseListener() {
                                    @Override
                                    public void onShowcaseDisplayed(MaterialShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseDismissed(MaterialShowcaseView showcaseView) {
                                        editorTutorial.putBoolean("timelineHr",false);
                                        editorTutorial.apply();
                                        editorTutorial.commit();
                                    }
                                })
                                .build());

                        sequence.start();
                    }



                }
            }else if(siteArrayList.get(0).getMemberCount()==1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sequence.singleUse("timelineDetails");
                    if (context.getSharedPreferences("Tutorial", MODE_PRIVATE).getBoolean("timelineHr", true)){
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_associate_login)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getResources().getString(R.string.adapter2b))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)

                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.associate_force_logout)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getResources().getString(R.string.adapter2c))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.txt_skilled)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getResources().getString(R.string.adapter2d))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());

                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.txt_unskilled)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapter2e))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_cashrequest)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapetr2f))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_reqstPendency)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapter2g))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.iv_pic_activity)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapter2h))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .setListener(new IShowcaseListener() {
                                    @Override
                                    public void onShowcaseDisplayed(MaterialShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseDismissed(MaterialShowcaseView showcaseView) {
                                        editorTutorial.putBoolean("timelineHr",false);
                                        editorTutorial.apply();
                                        editorTutorial.commit();
                                    }
                                })
                                .build());


                        sequence.start();
                    }

                }
            }else if(siteArrayList.get(0).getMemberCount()==0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sequence.singleUse("timelineDetails");

                    if (context.getSharedPreferences("Tutorial", MODE_PRIVATE).getBoolean("timelineHr", true)){
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.img_next)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.adapter0a))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)

                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.txt_skilled)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getString(R.string.total_skilled))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
                                .setTarget(holder.txt_unskilled)
                                .setGravity(Gravity.BOTTOM)
                                .withOvalShape()
                                .setShapePadding(10)
                                .setTargetTouchable(false)
                                .setContentText(context.getResources().getString(R.string.total_unskilled))// optional but starting animations immediately in onCreate can make them choppy
                                .setContentTextColor(context.getColor(R.color.white))
                                .setDismissOnTouch(true)
                                .build());
                        sequence.start();
                    }




//                sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
//                        .setTarget(holder.iv_associate_login)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setShapePadding(10)
//                        .setTargetTouchable(false)
//                        .setContentText("Whenever Team member comes online this light will turn green and time of login and distance from site will be displayed. " +
//                                "Whenever Team Member will logout this light will turn red and logout time and distance from site will be displayed")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissText(context.getString(R.string.content_dismiss))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//
//                        .build());
//                sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
//                        .setTarget(holder.associate_force_logout)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setShapePadding(10)
//                        .setTargetTouchable(false)
//                        .setContentText("Whenever Associate is forced logout this light will turn red. Click on this when active to allow the login request of team member.")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissText(context.getString(R.string.content_dismiss))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .build());
//                sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
//                        .setTarget(holder.txt_skilled)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setShapePadding(10)
//                        .setTargetTouchable(false)
//                        .setContentText("Total skilled workers attendance uploaded by team member. Click to see details")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissText(context.getString(R.string.content_dismiss))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .build());

//                sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
//                        .setTarget(holder.iv_cashrequest)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setShapePadding(10)
//                        .setTargetTouchable(false)
//                        .setContentText("Fund Request status made by team member. In case associate make a request this will turn green.")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissText(context.getString(R.string.content_dismiss))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .build());
//                sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
//                        .setTarget(holder.iv_reqstPendency)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setShapePadding(10)
//                        .setTargetTouchable(false)
//                        .setContentText("In case of any cash pendency , this would turn red.")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissText(context.getString(R.string.content_dismiss))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .build());
//                sequence.addSequenceItem(new MaterialShowcaseView.Builder((Activity) context )
//                        .setTarget(holder.iv_pic_activity)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setShapePadding(10)
//                        .setTargetTouchable(false)
//                        .setContentText("Work Activity Status. When team member sends you any pic, video , audio recording, text this would turn green.")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissText(context.getString(R.string.content_dismiss))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .build());


                }
            }
        }
//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(200);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            config.setContentTextColor(context.getColor(R.color.white));
//            config.setMaskColor(context.getColor(R.color.black));
//
//        }
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence((Activity) context, "Register");
//        sequence.setConfig(config);





        if (modelSite.getMemberCount() > 1) {
            holder.img_drop_down.setVisibility(View.VISIBLE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            new MaterialShowcaseView.Builder((Activity)context )
//                    .setTarget(holder.img_drop_down)
//                    .setGravity(Gravity.BOTTOM)
//                    .withOvalShape()
//                    .setTargetTouchable(true)
//                    .setContentText("Click to see all associates")// optional but starting animations immediately in onCreate can make them choppy
//                    .setContentTextColor(context.getColor(R.color.white))
//                    .setDismissTextColor(context.getColor(R.color.red))
//                    .setDismissStyle(Typeface.DEFAULT_BOLD)
//                    .singleUse("seeMember")
//                    .show();
//            }
            holder.ll_site_navigation.setWeightSum(4);

        } else if (modelSite.getMemberCount() == 1) {
            Log.e("Cursor", "Here");
            holder.img_drop_down.setVisibility(View.GONE);

            holder.ll_site_navigation.setWeightSum(4);

        }else if(modelSite.getMemberCount()==0){

        }
        Log.e("Unskilled", "" + modelSite.getUnskilled());
        holder.ll_member_list.setVisibility(View.GONE);
        holder.txt_skilled.setText(String.valueOf(modelSite.getSkilled()));
        holder.txt_unskilled.setText(String.valueOf(modelSite.getUnskilled()));

        Log.e("SiteID", "@ " + "" + (holder.getAdapterPosition()) + " " + modelSite.getSiteId());
        String str = modelSite.getSiteCity();
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        if (siteArrayList.get(holder.getAdapterPosition()).getMemberStatus().equals("Pending")) {
            holder.img_next.setVisibility(View.VISIBLE);
            holder.ll_site_navigation.setWeightSum(4);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                new MaterialShowcaseView.Builder((Activity)context )
//                        .setTarget(holder.img_next)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setTargetTouchable(true)
//                        .setContentText("Click to perform site Activity like take attendance , make payment to workers.")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .singleUse("siteActivity")
//                        .show();
//            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ll_site_navigation.setElevation(32);
            }
            holder.ll_site_navigation.setBackgroundColor(context.getResources().getColor(R.color.gray_more_light));


        }
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        holder.txt_site_name.setText(builder);
        holder.txt_siteId.setText(String.valueOf(modelSite.getSiteId()));
        Log.e("Siteid", "Position:" + holder.getAdapterPosition() + "SiteId:" + modelSite.getSiteId());
        if (modelSite.getOnline() != null && modelSite.getForceLogout() != null) {
            Log.e("Siteid", "Online" + (modelSite.getOnline()));



            if (modelSite.getOnline()) {
                holder.associate_force_logout.setImageResource(R.drawable.images);
                long siteIdSend = modelSite.getSiteId();
                Log.e("Online", "" + modelSite.getSiteId());
                holder.txt_work_associate_time.setText(modelSite.getTime());
                Log.e("modelSite.getTime()", modelSite.getTime());
                holder.iv_associate_login.setImageResource(R.drawable.green);
                holder.txt_skilled.setText(String.valueOf(modelSite.getSkilled()));
                holder.txt_unskilled.setText(String.valueOf(modelSite.getUnskilled()));
                if (siteArrayList.get(holder.getAdapterPosition()).getMemberStatus().equals("Pending")) {
                    holder.ll_site_registered.setVisibility(View.GONE);
                    holder.txt_admin.setVisibility(View.VISIBLE);
                    holder.ll_cash_site_registered.setVisibility(View.GONE);
                    holder.txt_admin1.setVisibility(View.VISIBLE);
                    holder.ll_site_pic.setVisibility(View.GONE);
                    holder.txt_admin2.setVisibility(View.VISIBLE);
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    new MaterialShowcaseView.Builder((Activity)context )
//                            .setTarget(holder.iv_associate_login)
//                            .setGravity(Gravity.BOTTOM)
//                            .withOvalShape()
//                            .setTargetTouchable(true)
//                            .setContentText("Click to see the selfie of the team member who logged in ")// optional but starting animations immediately in onCreate can make them choppy
//                            .setContentTextColor(context.getColor(R.color.white))
//                            .setDismissTextColor(context.getColor(R.color.red))
//                            .setDismissStyle(Typeface.DEFAULT_BOLD)
//                            .singleUse("selfie")
//                            .show();
//                }
                holder.iv_associate_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("Sending", "" + siteIdSend);
                        if (siteArrayList.get(holder.getAdapterPosition()).getMemberStatus().equals("Registered")) {
                            getMemberImage(siteIdSend, siteArrayList.get(holder.getAdapterPosition()).getOnlineTimestamp());
                        }

                    }
                });
                float[] results = new float[1];
                Location.distanceBetween(modelSite.getSiteLatitude(), modelSite.getSiteLongitude(), modelSite.getMemberLatitude()
                        , modelSite.getMemberLongitude(), results);
                float distance = results[0];
                int dis_display = (int) distance;
                if (siteArrayList.get(holder.getAdapterPosition()).getMemberStatus().equals("Registered")) {
                    if (siteArrayList.get(holder.getAdapterPosition()).getSiteLongitude() > 0 && siteArrayList.get(holder.getAdapterPosition()).getMemberLongitude() > 0) {
                        holder.txt_work_associate_distance.setVisibility(View.VISIBLE);
                        holder.txt_work_associate_distance.setText(dis_display + ("m"));
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            new MaterialShowcaseView.Builder((Activity)context )
//                                    .setTarget(holder.txt_work_associate_distance)
//                                    .setGravity(Gravity.BOTTOM)
//                                    .withOvalShape()
//                                    .setTargetTouchable(true)
//                                    .setContentText("This is the distance between team member log in point from site.")// optional but starting animations immediately in onCreate can make them choppy
//                                    .setContentTextColor(context.getColor(R.color.white))
//                                    .setDismissTextColor(context.getColor(R.color.red))
//                                    .setDismissStyle(Typeface.DEFAULT_BOLD)
//                                    .singleUse("dist")
//                                    .show();
//                        }
                    } else {
                        holder.txt_work_associate_distance.setVisibility(View.GONE);
                    }
                }


            } else {
                holder.iv_associate_login.setImageResource(R.drawable.images);
                if (modelSite.getTime().equals("")) {
                    holder.txt_work_associate_time.setText("");
                } else {
                    holder.txt_work_associate_time.setText(modelSite.getTime());
                    holder.txt_work_associate_time.setTextColor(context.getResources().getColor(R.color.red));
                }

                Log.e("Siteid", "Online" + (siteArrayList.get(holder.getAdapterPosition()).getMemberStatus().equals("Pending")));
                if (siteArrayList.get(holder.getAdapterPosition()).getMemberStatus().equals("Pending")) {
                    holder.ll_site_registered.setVisibility(View.GONE);
                    holder.txt_admin.setVisibility(View.VISIBLE);
                    holder.txt_admin.setText(context.getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userName", ""));
                    holder.ll_cash_site_registered.setVisibility(View.GONE);
                    holder.txt_admin1.setVisibility(View.VISIBLE);
                    holder.txt_admin1.setText(context.getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userName", ""));
                    holder.ll_site_pic.setVisibility(View.GONE);
                    holder.txt_admin2.setVisibility(View.VISIBLE);
                    holder.txt_admin2.setText(context.getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userName", ""));

                }
            }
            Log.e("SkilledSeen", "" + modelSite.getSkilledSeen());
            Log.e("SkilledCond", "" + ((modelSite.getSkilled() > 0 && modelSite.getSkilledSeen() != null && !modelSite.getSkilledSeen())));
            if (modelSite.getSkilled() > 0 && modelSite.getSkilledSeen() != null && !modelSite.getSkilledSeen()) {
                holder.txt_skilled.setText(String.valueOf(modelSite.getSkilled()));
                holder.txt_skilled.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    new MaterialShowcaseView.Builder((Activity)context )
//                            .setTarget(holder.txt_skilled)
//                            .setGravity(Gravity.BOTTOM)
//                            .withOvalShape()
//                            .setTargetTouchable(true)
//                            .setContentText("Click here to see detailed attendance")// optional but starting animations immediately in onCreate can make them choppy
//                            .setContentTextColor(context.getColor(R.color.white))
//                            .setDismissTextColor(context.getColor(R.color.red))
//                            .setDismissStyle(Typeface.DEFAULT_BOLD)
//                            .singleUse("skAtt")
//                            .show();
//                }
                holder.txt_skilled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("SkilledSeen123", "" + (modelSite.getAttendanceEditUnSkilled() != null));

                        try {
                            openLabourDialog("Skilled", siteArrayList.get(holder.getAdapterPosition()).getSiteId(), siteArrayList.get(holder.getAdapterPosition()).getEndTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                });

            } else if (modelSite.getSkilled() <= 0) {
                holder.txt_skilled.setText("-");
                holder.txt_skilled.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.txt_skilled.setText(String.valueOf(modelSite.getSkilled()));
                holder.txt_skilled.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            if (modelSite.getUnskilled() > 0 && modelSite.getUnSkilledSeen() != null && !modelSite.getUnSkilledSeen()) {
                holder.txt_unskilled.setText(String.valueOf(modelSite.getUnskilled()));
                holder.txt_unskilled.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    new MaterialShowcaseView.Builder((Activity)context )
//                            .setTarget(holder.txt_unskilled)
//                            .setGravity(Gravity.BOTTOM)
//                            .withOvalShape()
//                            .setTargetTouchable(true)
//                            .setContentText("Click here to see detailed attendance")// optional but starting animations immediately in onCreate can make them choppy
//                            .setContentTextColor(context.getColor(R.color.white))
//                            .setDismissTextColor(context.getColor(R.color.red))
//                            .setDismissStyle(Typeface.DEFAULT_BOLD)
//                            .singleUse("uskAtt")
//                            .show();
//                }
                holder.txt_unskilled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            openLabourDialog("Unskilled", siteArrayList.get(holder.getAdapterPosition()).getSiteId(), siteArrayList.get(holder.getAdapterPosition()).getEndTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (modelSite.getUnskilled() <= 0) {
                holder.txt_unskilled.setText("-");
                holder.txt_unskilled.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.txt_unskilled.setText(String.valueOf(modelSite.getUnskilled()));
                holder.txt_unskilled.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

            if (modelSite.getSkilledSeen() != null && modelSite.getSkilledSeen()) {
                holder.txt_skilled.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.txt_skilled.setClickable(false);
            }
            if (modelSite.getUnSkilledSeen() != null && modelSite.getUnSkilledSeen()) {
                holder.txt_unskilled.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.txt_unskilled.setClickable(false);

            }

            if (modelSite.getForceLogout()) {
                Log.e("ModelSiteFL", "2" + modelSite.getSiteId());
                holder.associate_force_logout.setImageResource(R.drawable.ic_red);
                holder.iv_associate_login.setImageResource(R.drawable.images);
                holder.txt_work_associate_time.setText(modelSite.getTime());
                holder.associate_force_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showForceLogoutDialog(siteArrayList.get(holder.getAdapterPosition()));
                    }
                });

            }
            if (modelSite.getCashReq() != null&&modelSite.getCashReq()) {
                holder.iv_cashrequest.setImageResource(R.drawable.green);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    new MaterialShowcaseView.Builder((Activity)context )
//                            .setTarget(holder.iv_cashrequest)
//                            .setGravity(Gravity.BOTTOM)
//                            .withOvalShape()
//                            .setTargetTouchable(true)
//                            .setContentText("Click here to see the Cash Request details")// optional but starting animations immediately in onCreate can make them choppy
//                            .setContentTextColor(context.getColor(R.color.white))
//                            .setDismissTextColor(context.getColor(R.color.red))
//                            .setDismissStyle(Typeface.DEFAULT_BOLD)
//                            .singleUse("cashShow")
//                            .show();
//                }
                holder.iv_cashrequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("SiteID1234", String.valueOf(siteArrayList.get(holder.getAdapterPosition()).getSiteId()));
                        Log.e("SiteID1234", "" + (modelSite.getCashReqID() == null));
                        showCashReqDialog(siteArrayList.get(holder.getAdapterPosition()));
                    }
                });
            } else {
                holder.iv_cashrequest.setImageResource(R.drawable.images);
            }
            if (modelSite.getReqPendency() != null && modelSite.getReqPendency()) {
                holder.iv_reqstPendency.setImageResource(R.drawable.green);
                holder.iv_reqstPendency.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showReqPendencyDialog(siteArrayList.get(holder.getAdapterPosition()));
                    }
                });
            } else {
                holder.iv_reqstPendency.setImageResource(R.drawable.images);
            }
            if (modelSite.getLocationVerifyConfirm() != null) {
                if (modelSite.getLocationVerifyConfirm() == modelSite.getLocationVerify()) {
                    holder.siteDetails_verify.setVisibility(View.GONE);
                } else {
                    holder.siteDetails_verify.setVisibility(View.VISIBLE);

                }
            } else {
                holder.siteDetails_verify.setVisibility(View.GONE);
            }
            holder.btn_check_siteDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSiteDetailsDialog(siteArrayList.get(holder.getAdapterPosition()).getSiteLatitude(), siteArrayList.get(holder.getAdapterPosition()).getSiteLongitude(), siteArrayList.get(holder.getAdapterPosition()).getSiteId());
                }
            });
        }
        if (modelSite.getSkilledTime() != null) {
            holder.txt_skilled_time.setVisibility(View.VISIBLE);
            holder.txt_skilled_time.setText(modelSite.getSkilledTime());
        } else {
            holder.txt_skilled_time.setVisibility(View.GONE);
        }
        if (modelSite.getUnskilledTime() != null) {
            holder.txt_unskilled_time.setVisibility(View.VISIBLE);
            holder.txt_unskilled_time.setText(modelSite.getUnskilledTime());
        } else {
            holder.txt_unskilled_time.setVisibility(View.GONE);
        }
        Log.e("PicActivity", "" + modelSite.getPicActivity());
        if (siteArrayList.get(holder.getAdapterPosition()).getPicActivity() != null && siteArrayList.get(holder.getAdapterPosition()).getPicActivity()) {
            float[] results = new float[1];
            if (modelSite.getPicLatitude() > 0 && modelSite.getPicLongitude() > 0) {
                Location.distanceBetween(modelSite.getSiteLatitude(), modelSite.getSiteLongitude(), modelSite.getPicLatitude()
                        , modelSite.getPicLongitude(), results);
                float distance = results[0];
                int dis_display = (int) distance;
                holder.pic_activity_distance.setText(dis_display + ("m"));
            } else {
                holder.pic_activity_distance.setVisibility(View.GONE);
            }
            holder.iv_pic_activity.setImageResource(R.drawable.green);
            holder.txt_pic_activity_time.setText(modelSite.getPicTime());
            holder.iv_pic_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPicActivityDialog(siteArrayList.get(holder.getAdapterPosition()), holder);
                }
            });


        } else {
            holder.pic_activity_distance.setVisibility(View.GONE);
        }

        holder.ll_site_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelSite.getMemberCount() == 0) {
                    if (siteArrayList.get(holder.getAdapterPosition()).getMemberStatus().equals("Pending")) {
                        Intent intent = new Intent(context, MemberTimelineActivity.class);
                        intent.putExtra("siteId", siteArrayList.get(holder.getAdapterPosition()).getSiteId());
                        intent.putExtra("siteName", siteArrayList.get(holder.getAdapterPosition()).getSiteName());
                        Log.e("siteID786010", "Ad:" + siteArrayList.get(holder.getAdapterPosition()).getSiteId());
                        context.startActivity(intent);
                    }
                } else {
                    if (!open) {
                        open = true;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(firebaseAuth.getUid()).child("Industry")
                                .child("Construction")
                                .child("Site")
                                .child(String.valueOf(siteArrayList.get(holder.getAdapterPosition()).getSiteId())).child("Members").orderByChild("online").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    ModelAssociateTimeline modelUser = ds.getValue(ModelAssociateTimeline.class);
                                    userArrayList.add(modelUser);
                                }
                                if (userArrayList.size() > 0) {

                                    AdapterAssociate adapterAssociate = new AdapterAssociate(context, userArrayList, modelSite.getSiteId());
                                    holder.rv_member_list.setAdapter(adapterAssociate);
                                    holder.ll_member_list.setVisibility(View.VISIBLE);

                                }

                                Log.e("ModelUser", "" + userArrayList.size());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        open = false;
                        holder.ll_member_list.setVisibility(View.GONE);
                    }
                }


            }
        });
        holder.img_drop_down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!open) {
                    open = true;
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(firebaseAuth.getUid()).child("Industry")
                            .child("Construction")
                            .child("Site").child(String.valueOf(siteArrayList.get(holder.getAdapterPosition()).getSiteId())).child("Members").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelAssociateTimeline modelUser = ds.getValue(ModelAssociateTimeline.class);
                                userArrayList.add(modelUser);
                            }
                            if (userArrayList.size() > 0) {

                                AdapterAssociate adapterAssociate = new AdapterAssociate(context, userArrayList, modelSite.getSiteId());
                                holder.rv_member_list.setAdapter(adapterAssociate);
                                holder.ll_member_list.setVisibility(View.VISIBLE);

                            }

                            Log.e("ModelUser", "" + userArrayList.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    open = false;
                    holder.ll_member_list.setVisibility(View.GONE);
                }

            }
        });

        if (modelSite.getAssociateRequest() != null && modelSite.getAssociateRequest()) {
            holder.ll_associate_request.setVisibility(View.VISIBLE);
            if (modelSite.getAssociateRequestType().equals("PayableAmount")) {
                holder.txt_associate_request.setText(context.getString(R.string.associate_request));

            } else {
                holder.txt_associate_request.setText(context.getString(R.string.associate_request));
            }
            holder.btn_associate_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (siteArrayList.get(holder.getAdapterPosition()).getAssociateRequestType().equals("PayableAmount")) {
//                        Log.e("ReasonShow1",siteArrayList.get(holder.getAdapterPosition()).getAssociateRequestReason());
                        if (siteArrayList.get(holder.getAdapterPosition()).getAssociateRequestStatus().equals("Pending")) {
                            showPayableAmountRequestDialog(siteArrayList.get(holder.getAdapterPosition()), false);
                        } else {
                            showPayableAmountRequestDialog(siteArrayList.get(holder.getAdapterPosition()), true);
                        }

                    } else {
                        if (siteArrayList.get(holder.getAdapterPosition()).getAssociateRequestStatus().equals("Pending")) {
                            showReconcileDialog(siteArrayList.get(holder.getAdapterPosition()), false);
                        }
                        else {
                            showReconcileDialog(siteArrayList.get(holder.getAdapterPosition()), true);
                        }
                    }
                }
            });
        }


//        holder.txt_siteName.setText(modelSite.getSiteName());


    }

    private void showReqPendencyDialog(ModelSite siteId) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_req_pendency_dialog, null);
        alert.setView(mView);
        TextView txt_requested_amount,txt_request_status;
        txt_requested_amount = mView.findViewById(R.id.txt_requested_amount);
        txt_request_status = mView.findViewById(R.id.txt_request_status);
        txt_request_status.setText(siteId.getReqStatus());
        txt_requested_amount.setText(siteId.getReqAmt());
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void showPayableAmountRequestDialog(ModelSite modelSite, Boolean reasonShow) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_payable_amount_dialog, null);
        alert.setView(mView);
        TextView txt_labourId, txt_to_pay_amount, txt_payable_amount, txt_reason;
        Button btn_allow, btn_ask_why, btn_deny;
        txt_labourId = mView.findViewById(R.id.txt_labourId);
        txt_to_pay_amount = mView.findViewById(R.id.txt_to_pay_amount);
        txt_payable_amount = mView.findViewById(R.id.txt_payable_amount);
        btn_allow = mView.findViewById(R.id.btn_allow);
        btn_ask_why = mView.findViewById(R.id.btn_ask_why);
        btn_deny = mView.findViewById(R.id.btn_deny);
        txt_reason = mView.findViewById(R.id.txt_reason);
        Log.e("ReasonShow",""+reasonShow);
        if (reasonShow) {
            txt_reason.setText(modelSite.getAssociateRequestReason());
            btn_ask_why.setVisibility(View.GONE);
            btn_deny.setVisibility(View.VISIBLE);
        } else {
            txt_reason.setVisibility(View.GONE);
            btn_ask_why.setVisibility(View.VISIBLE);
            btn_deny.setVisibility(View.GONE);
        }
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        txt_labourId.setText(modelSite.getLabourIdPaying());
        txt_to_pay_amount.setText(modelSite.getToPayAmount());
        txt_payable_amount.setText(String.valueOf(modelSite.getPayableAmount()));
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("associateRequestStatus", "Allowed");
                hashMap.put("associateRequest", false);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        btn_ask_why.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("associateRequestStatus", "AskWhy");
                hashMap.put("associateRequest", false);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequest").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequestType").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("toPayAmount").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("payableAmount").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("labourIdPaying").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequestStatus").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequestReason").removeValue();
                Toast.makeText(context, "Request Denied", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            }
        });

        alertDialog.show();
    }

    private void showReconcileDialog(ModelSite modelSite, Boolean reasonShow) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_reconcile_amount_request_dialog, null);
        alert.setView(mView);
        TextView txt_reconcile,txt_reason;
        Button btn_allow, btn_deny,btn_ask_why;
        txt_reconcile = mView.findViewById(R.id.txt_reconcile);
        btn_ask_why = mView.findViewById(R.id.btn_ask_why);
        txt_reason = mView.findViewById(R.id.txt_reason);
        btn_allow = mView.findViewById(R.id.btn_allow);
        btn_deny = mView.findViewById(R.id.btn_deny);
        Log.e("Reconcile", modelSite.getToPayAmount());
        txt_reconcile.setText(modelSite.getToPayAmount());
        Log.e("ReasonShow",""+reasonShow);
        if (reasonShow) {
            txt_reason.setText(modelSite.getAssociateRequestReason());
            btn_ask_why.setVisibility(View.GONE);
            btn_deny.setVisibility(View.VISIBLE);
        } else {
            txt_reason.setVisibility(View.GONE);
            btn_ask_why.setVisibility(View.VISIBLE);
            btn_deny.setVisibility(View.GONE);
        }

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("associateRequestStatus", "Allowed");
                hashMap.put("associateRequest", false);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        btn_ask_why.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("associateRequestStatus", "AskWhy");
                hashMap.put("associateRequest", false);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequest").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequestType").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("toPayAmount").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("payableAmount").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("labourIdPaying").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequestStatus").removeValue();
                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(modelSite.getSiteId())).child("associateRequestReason").removeValue();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();


    }

    private void showPicActivityDialog(ModelSite modelSite, HolderTimeline holder) {
        Log.e("PicAcRe",modelSite.getPicType());
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        String currentDate = null;
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = df1.format(c);
        }
        String currentTime = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_pic_activity_dialog, null);
        TextView txt_site_name, txt_site_id, txt_pic_date, txt_pic_time, txt_remark;
        VideoView iv_video;
        LinearLayout ll_audio, ll_image;
        ImageView iv_play;

        EditText et_your_remark;
        ImageView iv_mic;
        et_your_remark = mView.findViewById(R.id.et_your_remark);
        iv_mic = mView.findViewById(R.id.iv_mic);
        iv_video = mView.findViewById(R.id.iv_video);
        ll_audio = mView.findViewById(R.id.ll_audio);
        ll_image = mView.findViewById(R.id.ll_image);
        iv_play = mView.findViewById(R.id.iv_play);
        txt_remark = mView.findViewById(R.id.txt_remark);
        alert.setView(mView);


        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReplyActivity.class);
                intent.putExtra("siteId", modelSite.getSiteId());
                intent.putExtra("picId", modelSite.getPicId());
                intent.putExtra("replyUid", modelSite.getPicUid());
                context.startActivity(intent);
                alertDialog.dismiss();

            }
        });
        Log.e("PicType", modelSite.getPicType());

        ImageView iv_pic;
        Button btn_ok,btn_reply;
        txt_site_name = mView.findViewById(R.id.txt_site_name);
        txt_site_id = mView.findViewById(R.id.txt_site_id);
        txt_pic_date = mView.findViewById(R.id.txt_pic_date);
        txt_pic_time = mView.findViewById(R.id.txt_pic_time);
        btn_reply = mView.findViewById(R.id.btn_reply);

        iv_pic = mView.findViewById(R.id.iv_pic);
        btn_ok = mView.findViewById(R.id.btn_ok);
        if (modelSite.getPicType().equals("Text")) {
            txt_remark.setText(modelSite.getPicMsg());
            ll_image.setVisibility(View.GONE);
            ll_audio.setVisibility(View.GONE);

        } else if (modelSite.getPicType().equals("Image")) {
            Log.e("PicAcRe",modelSite.getPicRemark());
            txt_remark.setText(modelSite.getPicRemark());
            ll_image.setVisibility(View.VISIBLE);
            ll_audio.setVisibility(View.GONE);
            iv_video.setVisibility(View.GONE);
            iv_pic.setVisibility(View.VISIBLE);

        } else if (modelSite.getPicType().equals("Video")) {
            txt_remark.setText(modelSite.getPicRemark());
            ll_image.setVisibility(View.VISIBLE);
            ll_audio.setVisibility(View.GONE);
            iv_pic.setVisibility(View.GONE);
            iv_video.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(modelSite.getPicLink());
            iv_video.setVideoURI(uri);
            iv_video.setMediaController(new MediaController(context));
            iv_video.requestFocus();
            iv_video.start();

        } else if (modelSite.getPicType().equals("Audio")) {
            txt_remark.setVisibility(View.GONE);
            ll_image.setVisibility(View.GONE);
            ll_audio.setVisibility(View.VISIBLE);

            String status = "play";
           mediaPlayer = new MediaPlayer();
           Log.e("Sitehgfgfghf",modelSite.getPicLink());
            try {
                mediaPlayer.setDataSource(modelSite.getPicLink());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mediaPlayer.isPlaying()) {
                        iv_play.setImageResource(R.drawable.ic_play);
                        mediaPlayer.pause();
                    } else {
                        iv_play.setImageResource(R.drawable.ic_baseline_pause_24);
                        mediaPlayer.start();



                    }
                }
            });
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                }
//            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    iv_play.setImageResource(R.drawable.ic_play);
                    mediaPlayer.seekTo(0);
                }
            });



        }


        if (modelSite.getPicLink() != null && (!TextUtils.isEmpty(modelSite.getPicLink()))) {
            Picasso.get().load(modelSite.getPicLink()).
                    resize(400, 400).centerCrop()
                    .placeholder(R.drawable.ic_download).into(iv_pic);
        } else {
            iv_pic.setVisibility(View.GONE);
        }
        txt_site_name.setText(modelSite.getSiteName());
        txt_site_id.setText(String.valueOf(modelSite.getSiteId()));
        txt_pic_date.setText(modelSite.getPicDate());
        txt_pic_time.setText(modelSite.getPicTime());

        if (modelSite.getPicRemark() != null && (!modelSite.getPicRemark().equals(""))) {
            Log.e("CUrsorgiykg","Here");
            txt_remark.setText(modelSite.getPicRemark());
        } else if(modelSite.getPicMsg()==null) {
            txt_remark.setVisibility(View.GONE);
        }
        String finalCurrentTime = currentTime;
        String finalCurrentDate = currentDate;
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("replyMsg", et_your_remark.getText().toString());
                    hashMap.put("reply", true);
                    hashMap.put("replyTime", finalCurrentTime);
                    hashMap.put("replyDate", finalCurrentDate);
                    hashMap.put("replyType", "Text");
                    hashMap.put("replyLink", "");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site");

                reference.child(String.valueOf(modelSite.getSiteId())).child("Members").child(modelSite.getPicUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child(String.valueOf(modelSite.getSiteId())).child("PicActivity").child(modelSite.getPicId()).updateChildren(hashMap);
                            HashMap<String, Object> hashMap1 = new HashMap<>();
                            hashMap1.put("picActivity", false);
                            hashMap1.put("picId", "");
                            hashMap1.put("picTime", "");
                            hashMap1.put("picDate", "");
                            hashMap1.put("picLink", "");
                            hashMap1.put("picRemark", "");
                            hashMap1.put("picLatitude", 0.0);
                            hashMap1.put("picLongitude", 0.0);
                            hashMap1.put("picUid", "");
                            hashMap1.put("reply", true);
                            hashMap1.put("replyUid",modelSite.getPicUid());

                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");

                            reference1.child(firebaseAuth.getUid()).child("Industry")
                                    .child("Construction")
                                    .child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            alertDialog.dismiss();
                                            notifyDataSetChanged();
                                            makePicActivityMemberOffline(hashMap1, modelSite.getPicUid(), modelSite.getSiteId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

        });
        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReplyActivity.class);
                intent.putExtra("siteId", modelSite.getSiteId());
                intent.putExtra("picId", modelSite.getPicId());
                intent.putExtra("replyUid", modelSite.getPicUid());
                context.startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();


    }

    private void makePicActivityMemberOffline(HashMap<String, Object> hashMap, String picUid, long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry")
                .child("Construction")
                .child("Site").child(String.valueOf(siteId)).child("Members").child(picUid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("siteId", siteId);
                context.startActivity(intent);
            }
        });
    }

    private void openLabourDialog(String type, long siteId, String endTime) throws ParseException {
        ArrayList<ModelLabourPresent> modelPresentLabourArrayList = new ArrayList<>();
        String currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_show_present_labour_list, null);
        RecyclerView rv_present_labour = mView.findViewById(R.id.rv_present_labour);
        Button btn_ok = mView.findViewById(R.id.btn_ok);
        TextView txt_edit = mView.findViewById(R.id.txt_edit);
        TextView txt_heading = mView.findViewById(R.id.txt_heading);
        if (type.equals("Skilled")) {
            txt_heading.setText(context.getString(R.string.present_worker) + " " + context.getString(R.string.skilled));
        } else {
            txt_heading.setText(context.getString(R.string.present_worker) + " " + context.getString(R.string.unskilled));
        }
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        Log.e("CurrentDate", currentDate);
        Log.e("CurrentDate", "Ada" + siteId);
        Log.e("CurrentDate", "typ" + type);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry")
                .child("Construction")
                .child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate).orderByChild("labourType").equalTo(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Snapshot1234", "" + snapshot.exists());
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelLabourPresent modelPresentLabour = ds.getValue(ModelLabourPresent.class);
                        modelPresentLabourArrayList.add(modelPresentLabour);
                    }
                    Log.e("ModelLabourPresent", "" + modelPresentLabourArrayList.size());
                    AdapterLabourPresent adapterLabourPresent = new AdapterLabourPresent(context, modelPresentLabourArrayList);
                    rv_present_labour.setAdapter(adapterLabourPresent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        if(attendanceEdit!=null && attendanceEdit ){
//            txt_edit.setVisibility(View.GONE);
//        }else{
//            txt_edit.setVisibility(View.VISIBLE);
//        }
        SimpleDateFormat simpleDateFormat = null;
        Date date1 = null;
        Date date2 = null;
        Date c = Calendar.getInstance().getTime();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");

        }
        String currentTime = null;
        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar.setTime(simpleDateFormat.parse(endTime));
        }
        calendar.add(Calendar.MINUTE, 90);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentTime = simpleDateFormat.format(c);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                date1 = simpleDateFormat.parse(endTime);
                date2 = simpleDateFormat.parse(currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        Log.e("Difference", "" + min);
        Log.e("Difference", "" + endTime);
        Log.e("Difference", "" + currentTime);
        Log.e("Difference", "" + ((hours * 60) + min));
        int calc_time_diff = (hours * 60) + min;
//        if(calc_time_diff>0){
//            if()
//        }
        if (calc_time_diff > -90 && calc_time_diff < 90) {
            txt_edit.setVisibility(View.VISIBLE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                new MaterialShowcaseView.Builder((Activity)context )
//                        .setTarget(txt_edit)
//                        .setGravity(Gravity.BOTTOM)
//                        .withOvalShape()
//                        .setTargetTouchable(true)
//                        .setContentText("Click here to edit the attendance")// optional but starting animations immediately in onCreate can make them choppy
//                        .setContentTextColor(context.getColor(R.color.white))
//                        .setDismissTextColor(context.getColor(R.color.red))
//                        .setDismissStyle(Typeface.DEFAULT_BOLD)
//                        .singleUse("editAtt")
//                        .show();
//            }
            btn_ok.setVisibility(View.VISIBLE);
        } else {
            txt_edit.setVisibility(View.GONE);
            btn_ok.setVisibility(View.GONE);
        }
        hours = (hours < 0 ? -hours : hours);
        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ;
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.edit_attendance))
                        .setMessage(R.string.edit_attendance_message)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            Intent intent = new Intent(context, EditAttendanceActivity.class);
                            intent.putExtra("siteId", siteId);
                            intent.putExtra("type", type);
                            context.startActivity(intent);


                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentTime1 = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                }
                HashMap<String, Object> hashMap = new HashMap<>();
                if (type.equals("Skilled")) {
                    hashMap.put("skilledSeen", true);
                    hashMap.put("SkilledTime", currentTime1);
                } else {
                    hashMap.put("unSkilledSeen", true);
                    hashMap.put("UnskilledTime", currentTime1);
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                reference.child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                alertDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog.dismiss();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.show();
    }

    private void showForceLogoutDialog(ModelSite modelSite1) {
        Log.e("ModelSiteFL", "1" + modelSite1.getSiteId());
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.force_logout_dialog, null);
        TextView txt_forceLogout_time;
        Button btn_allow, btn_deny;
        txt_forceLogout_time = mView.findViewById(R.id.txt_forceLogout_time);
        btn_allow = mView.findViewById(R.id.btn_allow);
        btn_deny = mView.findViewById(R.id.btn_deny);
        txt_forceLogout_time.setText(modelSite.getTime());
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            new MaterialShowcaseView.Builder((Activity)context )
//                    .setTarget(btn_allow)
//                    .setGravity(Gravity.BOTTOM)
//                    .withOvalShape()
//                    .setTargetTouchable(true)
//                    .setContentText("Click here to allow the associate to relogin")// optional but starting animations immediately in onCreate can make them choppy
//                    .setContentTextColor(context.getColor(R.color.white))
//                    .setDismissTextColor(context.getColor(R.color.red))
//                    .setDismissStyle(Typeface.DEFAULT_BOLD)
//                    .singleUse("forceAllow")
//                    .show();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            new MaterialShowcaseView.Builder((Activity)context )
//                    .setTarget(btn_deny)
//                    .setGravity(Gravity.BOTTOM)
//                    .withOvalShape()
//                    .setTargetTouchable(true)
//                    .setContentText("Click here to deny the associate from re login")// optional but starting animations immediately in onCreate can make them choppy
//                    .setContentTextColor(context.getColor(R.color.white))
//                    .setDismissTextColor(context.getColor(R.color.red))
//                    .setDismissStyle(Typeface.DEFAULT_BOLD)
//                    .singleUse("forceAllow")
//                    .show();
//        }
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("online", false);
                hashMap.put("forceLogout", false);
                hashMap.put("time", "");
                hashMap.put("Denytime", "");
                Log.e("ModelSiteFL", String.valueOf(modelSite1.getSiteId()));
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site");

                reference.child(String.valueOf(modelSite1.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                reference.child(String.valueOf(modelSite1.getSiteId())).child("Members").child(modelSite1.getUid())
                                        .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Associate allowed to re-login", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
            }
        });
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("online", false);
                hashMap.put("forceLogout", true);
                hashMap.put("time", "");
                hashMap.put("Denytime", "");
                Log.e("ModelSiteFL", String.valueOf(modelSite1.getSiteId()));
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Industry")
                        .child("Construction")
                        .child("Site");

                reference.child(String.valueOf(modelSite1.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child(String.valueOf(modelSite1.getSiteId())).child("Members").child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Associate denied to re-login", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });

            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            btn_deny.setOnClickListener(new View.OnClickListener() {
//                String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
//                @Override
//                public void onClick(View view) {
//                    HashMap<String,Object> hashMap=new HashMap<>();
//                    hashMap.put("online",false);
//                    hashMap.put("forceLogout",true);
//                    hashMap.put("Denytime",currentTime);
//                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Site");
//                    reference.child(String.valueOf(modelSite1.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Toast.makeText(context, "Associate denied from re-login", Toast.LENGTH_SHORT).show();
//                                    alertDialog.dismiss();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    alertDialog.dismiss();
//                                }
//                            });
//                }
        }
        alertDialog.show();
    }


    private void showCashReqDialog(ModelSite modelSite) {
        //TODO Design the layout of request
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_admin_latest_fund_req_deatils, null);
        TextView txt_requested_amount, txt_full_amount;
        Button btn_submit,btn_hold;
        EditText et_partitial_amount;
        RadioButton rb_allow_full, rb_allow_partial, rb_deny;
        txt_requested_amount = mView.findViewById(R.id.txt_requested_amount);
        btn_submit = mView.findViewById(R.id.btn_submit);
        et_partitial_amount = mView.findViewById(R.id.et_partitial_amount);
        rb_allow_full = mView.findViewById(R.id.rb_allow_full);
        rb_allow_partial = mView.findViewById(R.id.rb_allow_partial);
        rb_deny = mView.findViewById(R.id.rb_deny);
        txt_full_amount = mView.findViewById(R.id.txt_full_amount);
        btn_hold = mView.findViewById(R.id.btn_hold);
        txt_requested_amount.setText(modelSite.getCashAmt());
        rb_allow_full.setChecked(true);
        et_partitial_amount.setVisibility(View.GONE);
        txt_full_amount.setVisibility(View.VISIBLE);
        txt_full_amount.setText(txt_requested_amount.getText().toString());
        alert.setView(mView);
        Log.e("SiteID1234", "1:" + modelSite.getSiteId());
        Log.e("SiteID1234", "1:" + (modelSite.getCashReqID() == null));

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        btn_hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadTofirebase(modelSite.getCashAmt(), false, modelSite, alertDialog, "Hold");
            }
        });

        rb_allow_partial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb_allow_full.setChecked(false);
                    rb_deny.setChecked(false);
                    et_partitial_amount.setVisibility(View.VISIBLE);
                    txt_full_amount.setVisibility(View.GONE);
                    et_partitial_amount.setText("");
                    btn_hold.setVisibility(View.GONE);


                }
            }
        });
        rb_allow_full.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb_allow_partial.setChecked(false);
                    rb_deny.setChecked(false);
                    et_partitial_amount.setVisibility(View.GONE);
                    txt_full_amount.setVisibility(View.VISIBLE);
                    txt_full_amount.setText(modelSite.getCashAmt());
                    btn_hold.setVisibility(View.VISIBLE);

                }
            }
        });
        rb_deny.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                rb_allow_partial.setChecked(false);
                rb_allow_full.setChecked(false);
                et_partitial_amount.setVisibility(View.GONE);
                txt_full_amount.setVisibility(View.GONE);
                btn_hold.setVisibility(View.VISIBLE);

            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String submit_amt = "";
                if (rb_allow_full.isChecked()) {
                    submit_amt = modelSite.getCashAmt();
                    uploadTofirebase(submit_amt, true, modelSite, alertDialog, "Full");
                } else if (rb_allow_partial.isChecked()) {
                    if (rb_allow_partial.isChecked() && TextUtils.isEmpty(et_partitial_amount.getText().toString())) {
                        Toast.makeText(context, "In case of Partial Request amount cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        submit_amt = et_partitial_amount.getText().toString();
                        uploadTofirebase(submit_amt, false, modelSite, alertDialog, "Partial");

                    }
                } else if (rb_deny.isChecked()) {
                    submit_amt = modelSite.getCashAmt();
                    uploadTofirebase(submit_amt, false, modelSite, alertDialog, "Deny");
                }
            }
        });
//
//
//

//
//
//
        alertDialog.show();


    }

    private void uploadTofirebase(String submit_amt, boolean b, ModelSite modelSite, AlertDialog alertDialog, String deny) {
        HashMap<String, Object> hashMap = new HashMap<>();
        String approvedStatus = "";
        if (b) {
            approvedStatus = "ApprovedFull";
            hashMap.put("reqStatus", approvedStatus);
            hashMap.put("approvedAmount", submit_amt);
        } else if (deny.equals("Partial")) {
            approvedStatus = "ApprovedPartial";
            hashMap.put("reqStatus", approvedStatus);
            hashMap.put("approvedAmount", submit_amt);
        } else if (deny.equals("Deny")) {
            approvedStatus = "Deny";
            hashMap.put("reqStatus", approvedStatus);
            hashMap.put("approvedAmount", submit_amt);
        }else if(deny.equals("Hold")){
            approvedStatus = "Hold";
            hashMap.put("reqStatus", approvedStatus);
            hashMap.put("approvedAmount", submit_amt);
        }


        String finalApprovedStatus = approvedStatus;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry")
                .child("Construction")
                .child("Site").child(String.valueOf(modelSite.getSiteId())).child("Request").child("FundRequest").child(modelSite.getCashReqID()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateToSiteMaster(modelSite, alertDialog, finalApprovedStatus, submit_amt);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void updateToSiteMaster(ModelSite modelSite, AlertDialog alertDialog, String approvedStatus, String submit_amt) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (approvedStatus.equals("ApprovedFull")) {

            hashMap.put("cashReq", false);
            hashMap.put("cashAmt", "");
            hashMap.put("reqAmt", submit_amt);
            hashMap.put("reqPendency", true);
            hashMap.put("cashReqID", "");
            hashMap.put("reqStatus", "Approved");
        } else if (approvedStatus.equals("ApprovedPartial")) {
            hashMap.put("cashReq", false);
            hashMap.put("cashAmt", "");
            hashMap.put("cashReqID", "");
            hashMap.put("reqAmt", submit_amt);
            hashMap.put("reqPendency", true);
            hashMap.put("reqStatus", "ApprovedPartial");

        } else if (approvedStatus.equals("Deny")) {
            hashMap.put("cashReq", false);
            hashMap.put("cashAmt", "");
            hashMap.put("cashReqID", "");
            hashMap.put("reqAmt", submit_amt);
            hashMap.put("reqPendency", false);
            hashMap.put("reqStatus", "Denied");
        }else if (approvedStatus.equals("Hold")) {
            hashMap.put("cashReq", false);
            hashMap.put("cashAmt", "");
            hashMap.put("cashReqID", "");
            hashMap.put("reqAmt", submit_amt);
            hashMap.put("reqPendency", true);
            hashMap.put("reqStatus", "Hold");
        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry")
                .child("Construction")
                .child("Site").child(String.valueOf(modelSite.getSiteId())).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                alertDialog.dismiss();

            }
        });

    }

    private void openSiteDetailsDialog(double siteLatitude, double siteLongitude, long siteId) {
        Log.e("lati", "" + siteLatitude);
        Log.e("lati", "" + siteLongitude);
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_site_details_dialog, null);
        TextView txt_city, txt_pinCode, txt_address;
        Button btn_confirm, btn_deny;
        txt_city = mView.findViewById(R.id.txt_city);
        txt_pinCode = mView.findViewById(R.id.txt_pinCode);
        txt_address = mView.findViewById(R.id.txt_address);
        btn_confirm = mView.findViewById(R.id.btn_confirm);
        btn_deny = mView.findViewById(R.id.btn_deny);
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            if (siteLatitude > 0) {
                addresses = geocoder.getFromLocation(siteLatitude, siteLongitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String pincode = addresses.get(0).getPostalCode();
                Log.e("City", city);
                txt_city.setText(city);
                txt_address.setText(address);
                txt_pinCode.setText(pincode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                builder.setTitle(R.string.confirm)
                        .setMessage(R.string.are_you_sure_site_details)
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            updateToSite(true, siteId);
                            dialogInterface.dismiss();
                            alertDialog.dismiss();


                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        });
                builder.show();


            }

        });
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MapsActivity.class);
                intent.putExtra("latitude",siteLatitude);
                intent.putExtra("longitude",siteLongitude);
                intent.putExtra("siteId",siteId);
                context.startActivity(intent);
            }
        });


        alertDialog.show();


    }

    private void updateToSite(boolean b, long siteId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (b) {
            hashMap.put("locationVerifyConfirm", true);
            hashMap.put("locationVerify", true);

        } else {
            hashMap.put("locationVerifyConfirm", false);
            hashMap.put("locationVerify", false);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry")
                .child("Construction")
                .child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, context.getString(R.string.SiteLocationUpdated), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void getMemberImage(long siteId, String onlineTimestamp) {
        Log.e("Online", onlineTimestamp);
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        Log.e("SiteDet", "" + siteId + " " + currentDate);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry")
                .child("Construction")
                .child("Site").child(String.valueOf(siteId)).child("Attendance").child(currentDate).child(onlineTimestamp)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        profile = snapshot.child("profile").getValue(String.class);
                        showProfileDialog(profile);
                        Log.e("Profile", "" + (snapshot.child("profile").getValue(String.class) == null));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProfileDialog(String profile) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.show_image, null);

        ImageView iv_profile = (ImageView) mView.findViewById(R.id.iv_profile);
        Picasso.get().load(profile).
                resize(400, 400).centerCrop()
                .placeholder(R.drawable.ic_add).into(iv_profile);


        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);


        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return siteArrayList.size();
    }

    public class HolderTimeline extends RecyclerView.ViewHolder {
        TextView txt_site_name, txt_siteId, txt_work_associate, txt_work_associate_time, txt_skilled, txt_unskilled, txt_skilled_time, txt_unskilled_time, txt_cashrequesttime, txt_requestPendencytime, txt_manPower, txt_cashRequest, txt_requestPendency, txt_work_associate_distance,
                txt_pic_activity_time, pic_activity_distance, txt_admin, txt_admin1, txt_admin2, txt_req_details,txt_associate_request;
        LinearLayout ll_work_associate_button, ll_work_associate_time, ll_manPower_status, ll_cashReqstdetails,
                ll_reqstPendency, ll_manPower_time, ll_main, ll_skUsk, siteDetails_verify, ll_site_registered, ll_cash_site_registered, ll_site_pic;
        ImageView iv_associate_login, associate_force_logout, iv_cashrequest, iv_reqstPendency, iv_pic_activity, img_next, img_drop_down;
        LinearLayout ll_site_navigation;
        View view_workAssociate, View_manpower, view_cashreqst, view_rqstPendency, view_skilled;
        RecyclerView rv_member_list;
        Button btn_check_siteDetails, btn_associate_request;
        LinearLayout ll_member_list, ll_associate_request;

        public HolderTimeline(@NonNull View itemView) {
            super(itemView);
            txt_site_name = itemView.findViewById(R.id.txt_site_name);
            txt_siteId = itemView.findViewById(R.id.txt_siteId);
            txt_work_associate = itemView.findViewById(R.id.txt_work_associate);
            txt_work_associate_time = itemView.findViewById(R.id.txt_work_associate_time);
            txt_skilled = itemView.findViewById(R.id.txt_skilled);
            txt_unskilled = itemView.findViewById(R.id.txt_unskilled);
            txt_cashrequesttime = itemView.findViewById(R.id.txt_cashrequesttime);
//            txt_requestPendencytime=itemView.findViewById(R.id.txt_requestPendencytime);
            ll_work_associate_button = itemView.findViewById(R.id.ll_work_associate_button);
            ll_work_associate_time = itemView.findViewById(R.id.ll_work_associate_time);
            ll_manPower_status = itemView.findViewById(R.id.ll_manPower_status);
            ll_cashReqstdetails = itemView.findViewById(R.id.ll_cashReqstdetails);
//            ll_reqstPendency=itemView.findViewById(R.id.ll_reqstPendency);
            iv_associate_login = itemView.findViewById(R.id.iv_associate_login);
            associate_force_logout = itemView.findViewById(R.id.associate_force_logout);
            iv_cashrequest = itemView.findViewById(R.id.iv_cashrequest);
            iv_reqstPendency = itemView.findViewById(R.id.iv_reqstPendency);
            view_workAssociate = itemView.findViewById(R.id.view_workAssociate);
            View_manpower = itemView.findViewById(R.id.View_manpower);
            view_cashreqst = itemView.findViewById(R.id.view_cashreqst);
//            view_rqstPendency=itemView.findViewById(R.id.view_rqstPendency);
            ll_manPower_time = itemView.findViewById(R.id.ll_manPower_time);
            txt_manPower = itemView.findViewById(R.id.txt_manPower);
            txt_cashRequest = itemView.findViewById(R.id.txt_cashRequest);
            txt_requestPendency = itemView.findViewById(R.id.txt_requestPendency);
            ll_main = itemView.findViewById(R.id.ll_main);
            ll_skUsk = itemView.findViewById(R.id.ll_skUsk);
            txt_work_associate_distance = itemView.findViewById(R.id.txt_work_associate_distance);
            siteDetails_verify = itemView.findViewById(R.id.siteDetails_verify);
            btn_check_siteDetails = itemView.findViewById(R.id.btn_check_siteDetails);
            txt_skilled_time = itemView.findViewById(R.id.txt_skilled_time);
            txt_unskilled_time = itemView.findViewById(R.id.txt_unskilled_time);
            view_skilled = itemView.findViewById(R.id.view_skilled);
            txt_pic_activity_time = itemView.findViewById(R.id.txt_pic_activity_time);
            pic_activity_distance = itemView.findViewById(R.id.pic_activity_distance);
            iv_pic_activity = itemView.findViewById(R.id.iv_pic_activity);
            ll_site_registered = itemView.findViewById(R.id.ll_site_registered);
            txt_admin = itemView.findViewById(R.id.txt_admin);
            txt_admin1 = itemView.findViewById(R.id.txt_admin1);
            ll_cash_site_registered = itemView.findViewById(R.id.ll_cash_site_registered);
            txt_admin2 = itemView.findViewById(R.id.txt_admin2);
            ll_site_pic = itemView.findViewById(R.id.ll_site_pic);
            img_next = itemView.findViewById(R.id.img_next);
            ll_site_navigation = itemView.findViewById(R.id.ll_site_navigation);
            img_drop_down = itemView.findViewById(R.id.img_drop_down);
            rv_member_list = itemView.findViewById(R.id.rv_member_list);
            ll_member_list = itemView.findViewById(R.id.ll_member_list);
            btn_associate_request = itemView.findViewById(R.id.btn_associate_request);
            ll_associate_request = itemView.findViewById(R.id.ll_associate_request);
            txt_req_details = itemView.findViewById(R.id.txt_req_details);
            txt_associate_request = itemView.findViewById(R.id.txt_associate_request);

        }
    }
}
