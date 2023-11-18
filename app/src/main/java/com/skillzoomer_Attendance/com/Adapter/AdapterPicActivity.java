package com.skillzoomer_Attendance.com.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skillzoomer_Attendance.com.Model.ModelPicActivity;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Activity.ReplyActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class AdapterPicActivity extends RecyclerView.Adapter<AdapterPicActivity.HolderPicActivity> {
    private Context context;
    private ArrayList<ModelPicActivity> picActivityArrayList;
    private String userType;
    private String siteStatus;
    Boolean MediaPlayPermit = true;

    private ProgressDialog progressDialog;

    public AdapterPicActivity(Context context, ArrayList<ModelPicActivity> picActivityArrayList, String userType, String siteStatus) {
        this.context = context;
        this.picActivityArrayList = picActivityArrayList;
        this.userType = userType;
        this.siteStatus = siteStatus;
    }

    @NonNull
    @Override
    public AdapterPicActivity.HolderPicActivity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_single_row, parent, false);
        return new AdapterPicActivity.HolderPicActivity(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPicActivity.HolderPicActivity holder, int position) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getResources().getString(R.string.please_wait));

//        if(position==picActivityArrayList.size()&& !progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }else{
//            progressDialog.show();
//        }
        ModelPicActivity modelPicActivity = picActivityArrayList.get(position);

        holder.setIsRecyclable(false);

        if (userType.equals("Supervisor")) {
            holder.txt_associate_name.setVisibility(View.GONE);
        } else {
            holder.txt_associate_name.setVisibility(View.VISIBLE);
        }
        if (modelPicActivity.getReply() != null && modelPicActivity.getReply()) {
            if (modelPicActivity.getReplyType().equals("Text")) {
                Log.e("PicAcx", "" + modelPicActivity.getReplyMsg().equals(""));

                holder.txt_admin_reply_msg.setVisibility(View.VISIBLE);

                holder.iv_play_reply.setVisibility(View.GONE);
            } else {
                holder.txt_admin_reply_msg.setVisibility(View.GONE);
                holder.iv_play_reply.setVisibility(View.VISIBLE);
            }
            holder.ll_reply_admin.setVisibility(View.VISIBLE);
            if (modelPicActivity.getReplyMsg().equals("")) {
                holder.txt_admin_reply_msg.setText("Seen");
                holder.txt_admin_reply_msg.setBackgroundColor(context.getResources().getColor(R.color.bottom_layout));
            } else {
                holder.txt_admin_reply_msg.setText(modelPicActivity.getReplyMsg());
            }

            holder.txt_reply_date.setText(modelPicActivity.getReplyDate());
            holder.txt_reply_time.setText(modelPicActivity.getReplyTime());
            holder.btn_reply.setVisibility(View.GONE);

        } else {
            holder.ll_reply_admin.setVisibility(View.GONE);
            if (!userType.equals("Supervisor")) {
                holder.btn_reply.setVisibility(View.VISIBLE);
                holder.iv_play_reply.setVisibility(View.GONE);
                holder.txt_associate_name.setVisibility(View.VISIBLE);
                if (siteStatus.equals("Pending")) {
                    holder.ll_reply_admin.setVisibility(View.GONE);
                } else {
                    holder.ll_reply_admin.setVisibility(View.VISIBLE);
                }

                holder.txt_admin_reply_msg.setVisibility(View.GONE);
                holder.ll_reply_date.setVisibility(View.GONE);


            } else {
                holder.txt_associate_name.setVisibility(View.GONE);
            }

        }
        if (modelPicActivity.getPicType().equals("Text")) {
            holder.ll_video.setVisibility(View.GONE);
            holder.ll_msg_text.setVisibility(View.VISIBLE);
            holder.ll_msg_image.setVisibility(View.GONE);
            holder.txt_msg_associate.setText(modelPicActivity.getPicMsg());
            holder.ll_audio.setVisibility(View.GONE);

        } else if (modelPicActivity.getPicType().equals("Image")) {
            holder.ll_video.setVisibility(View.GONE);
            holder.ll_audio.setVisibility(View.GONE);
            holder.ll_msg_text.setVisibility(View.GONE);
            holder.ll_msg_image.setVisibility(View.VISIBLE);
            Picasso.get().load(modelPicActivity.getPicLink()).
                    resize(400, 400).centerCrop()
                    .placeholder(R.drawable.ic_download).into(holder.iv_image);
            if (modelPicActivity.getPicRemark() != null) {
                holder.txt_image_comment.setText(modelPicActivity.getPicRemark());
                holder.iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        showPicActivityDialog(picActivityArrayList.get(position), "Image");
                    }
                });
            }

        } else if (modelPicActivity.getPicType().equals("Video")) {
            holder.ll_video.setVisibility(View.VISIBLE);
            holder.ll_audio.setVisibility(View.GONE);
            holder.ll_msg_text.setVisibility(View.GONE);
            holder.ll_msg_image.setVisibility(View.GONE);
//            Uri uri = Uri.parse(modelPicActivity.getPicLink());
//            holder.vv_video.setVideoURI(uri);
            holder.txt_video_comment.setText(modelPicActivity.getPicRemark());
            holder.vv_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPicActivityDialog(picActivityArrayList.get(position), "Video");
//                    holder.vv_video.setMediaController(new MediaController(context));
//                    holder.vv_video.requestFocus();
//                    holder.vv_video.start();
                }
            });


        } else if (modelPicActivity.getPicType().equals("Audio")) {
            holder.ll_video.setVisibility(View.GONE);
            holder.ll_audio.setVisibility(View.VISIBLE);
            holder.ll_msg_text.setVisibility(View.GONE);
            holder.ll_msg_image.setVisibility(View.GONE);
            MediaPlayer mediaPlayer = new MediaPlayer();
            MediaController mediaController=new MediaController(context);

            try {

                mediaPlayer.setDataSource(picActivityArrayList.get(position).getPicLink());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("MPLa", "" + mediaPlayer.isPlaying());

                    if (mediaPlayer.isPlaying()) {
                        holder.iv_play.setImageResource(R.drawable.ic_play);
                        mediaPlayer.pause();
                        MediaPlayPermit = true;

                    } else {
                        if (MediaPlayPermit) {
                            holder.iv_play.setImageResource(R.drawable.ic_baseline_pause_24);
                            mediaPlayer.start();
                            MediaPlayPermit = false;
                        } else {
                            Toast.makeText(context, "Media in use. Please pause the other media", Toast.LENGTH_SHORT).show();
                        }

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
                    holder.iv_play.setImageResource(R.drawable.ic_play);

                    mediaPlayer.seekTo(0);
                    MediaPlayPermit = true;
//                    mediaPlayer.stop();
                }
            });

        }
        holder.txt_msg_date.setText(modelPicActivity.getDateOfUpload());
        holder.txt_associate_name.setText(modelPicActivity.getUploadedByName());
        holder.txt_msg_time.setText(modelPicActivity.getTimeofUpload());

            holder.btn_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ReplyActivity.class);
                    intent.putExtra("siteId", modelPicActivity.getSiteId());
                    intent.putExtra("picId", modelPicActivity.getPicId());
                    intent.putExtra("replyUid", modelPicActivity.getUploadedbyUid());
                    context.startActivity(intent);

                }
            });
        if(picActivityArrayList.get(position).getReply()!=null && picActivityArrayList.get(position).getReply()){
            MediaPlayer mediaPlayer1 = new MediaPlayer();
            try {

                mediaPlayer1.setDataSource(picActivityArrayList.get(position).getReplyLink());
                mediaPlayer1.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.iv_play_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mediaPlayer1.isPlaying()) {
                        Log.e("MPLa", "" + mediaPlayer1.isPlaying());
                        holder.iv_play_reply.setImageResource(R.drawable.ic_play);
                        mediaPlayer1.pause();
                    } else {
                        holder.iv_play_reply.setImageResource(R.drawable.ic_baseline_pause_24);
                        mediaPlayer1.start();
//                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mediaPlayer) {
//                            mediaPlayer.start();
//                        }
//                    });
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            holder.iv_play_reply.setImageResource(R.drawable.ic_play);
//                            mediaPlayer.seekTo(0);
//                        }
//                    });

                    }
                }
            });

        }


    }

    private void showPicActivityDialog(ModelPicActivity modelSite, String type) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.pic_activity_single_details, null);
        TextView txt_pic_date, txt_pic_time, txt_remark, txt_uploaded_by;
        ImageView iv_pic;
        VideoView iv_video;
        Button btn_ok;
        txt_pic_date = mView.findViewById(R.id.txt_pic_date);
        txt_pic_time = mView.findViewById(R.id.txt_pic_time);
        txt_remark = mView.findViewById(R.id.txt_remark);
        iv_pic = mView.findViewById(R.id.iv_pic);
        txt_uploaded_by = mView.findViewById(R.id.txt_uploaded_by);
        iv_video = mView.findViewById(R.id.iv_video);
        txt_uploaded_by.setText(modelSite.getUploadedByName());
        btn_ok = mView.findViewById(R.id.btn_ok);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        if (type.equals("Image")) {
            iv_pic.setVisibility(View.VISIBLE);
            iv_video.setVisibility(View.GONE);
            progressDialog.dismiss();
        } else {
            iv_pic.setVisibility(View.GONE);
            iv_video.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(modelSite.getPicLink());
            MediaController mediaController=new MediaController(context);
            iv_video.setVideoPath(modelSite.getPicLink());
            iv_video.setMediaController(mediaController);
            mediaController.setAnchorView(iv_video);
            iv_video.requestFocus();
            iv_video.start();
            progressDialog.dismiss();
        }
        Log.e("PicLink", modelSite.getPicLink());
        if (modelSite.getPicLink() != null && (!TextUtils.isEmpty(modelSite.getPicLink()))) {
            Picasso.get().load(modelSite.getPicLink()).
                    resize(400, 400).centerCrop()
                    .placeholder(R.drawable.ic_download).into(iv_pic);
        } else {
            iv_pic.setVisibility(View.GONE);
        }

        txt_pic_date.setText(modelSite.getDateOfUpload());
        txt_pic_time.setText(modelSite.getTimeofUpload());
        if (modelSite.getPicRemark() != null && (modelSite.getPicRemark() != "")) {
            txt_remark.setText(modelSite.getPicRemark());
        } else {
            txt_remark.setVisibility(View.GONE);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();


    }

    @Override
    public int getItemCount() {
        return picActivityArrayList.size();
    }

    public class HolderPicActivity extends RecyclerView.ViewHolder {
        TextView txt_msg_associate, txt_image_comment, txt_video_comment, txt_msg_date, txt_msg_time,
                txt_admin_reply_msg, txt_reply_date, txt_reply_time, txt_associate_name;
        LinearLayout ll_msg_text, ll_msg_image, ll_video, ll_date_associate, ll_reply_admin, ll_reply_date, ll_audio;
        ImageView iv_image, iv_play, iv_play_reply;
        VideoView vv_video;
        Button btn_reply;

        public HolderPicActivity(@NonNull View itemView) {
            super(itemView);
            txt_msg_associate = itemView.findViewById(R.id.txt_msg_associate);
            txt_image_comment = itemView.findViewById(R.id.txt_image_comment);
            txt_video_comment = itemView.findViewById(R.id.txt_video_comment);
            txt_msg_date = itemView.findViewById(R.id.txt_msg_date);
            txt_msg_time = itemView.findViewById(R.id.txt_msg_time);
            txt_admin_reply_msg = itemView.findViewById(R.id.txt_admin_reply_msg);
            txt_reply_time = itemView.findViewById(R.id.txt_reply_time);
            ll_msg_text = itemView.findViewById(R.id.ll_msg_text);
            ll_msg_image = itemView.findViewById(R.id.ll_msg_image);
            ll_video = itemView.findViewById(R.id.ll_video);
            ll_reply_admin = itemView.findViewById(R.id.ll_reply_admin);
            ll_date_associate = itemView.findViewById(R.id.ll_date_associate);
            ll_reply_date = itemView.findViewById(R.id.ll_reply_date);
            iv_image = itemView.findViewById(R.id.iv_image);
            vv_video = itemView.findViewById(R.id.vv_video);
            txt_reply_date = itemView.findViewById(R.id.txt_reply_date);
            btn_reply = itemView.findViewById(R.id.btn_reply);
            iv_play = itemView.findViewById(R.id.iv_play);
            ll_audio = itemView.findViewById(R.id.ll_audio);
            iv_play_reply = itemView.findViewById(R.id.iv_play_reply);
            txt_associate_name = itemView.findViewById(R.id.txt_associate_name);


        }
    }
}
