package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.icu.text.SimpleDateFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skillzoomer_Attendance.com.Model.ModelPicActivity;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityReplyBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ReplyActivity extends AppCompatActivity {
    ActivityReplyBinding binding;
    long siteId;
    String picId;
    String picUid;
    File file = null;
    private static final int RECORD_AUDIO = 3;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private AudioRecord audioRecord=null;
    private int currentFormat = 1;
    private String replyUid;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,             MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityReplyBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog.setCanceledOnTouchOutside(false);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        siteId=intent.getLongExtra("siteId",0);
        picId=intent.getStringExtra("picId");
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        if(siteId>0){
            getDetails(siteId,picId);

        }
        binding.ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.EXTRA_LANGUAGE);
                if (sharedpreferences.getString("Language", "hi").equals("hi")) {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi");
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi");
                    intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "hi");
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                } else if (sharedpreferences.getString("Language", "hi").equals("en")) {
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
                    intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en");
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                }


                try {

                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(ReplyActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        binding.ivRecord.setOnTouchListener((view, motionEvent) -> {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Boolean microphone =checkformicrophonePresence();
                    Log.e("Microphone",""+microphone);
                    if(microphone){
                        checkAudioPermission();
                    }
                    return true;
                case MotionEvent.ACTION_UP:
//                        AppLog.logString("stop Recording");
//                        stopRecording();
                    Log.e("Recording","Stopped");
                    stopRecording();
                    break;
            }
            return false;
        });

        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();
                uploadToStorage("Text");

            }
        });

    }

    private void uploadToStorage(String type) {


        Log.e("StorageRecording",type);
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
        String finalCurrentTime = currentTime;
        String finalCurrentDate = currentDate;
        if (type.equals("Text")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("replyMsg",binding.etYourRemark.getText().toString());
            hashMap.put("reply", true);
            hashMap.put("replyTime", finalCurrentTime);
            hashMap.put("replyDate", finalCurrentDate);
            hashMap.put("replyType", "Text");
            hashMap.put("replyLink", "");
            Log.e("FReply",firebaseAuth.getUid());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(picUid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(picId).updateChildren(hashMap);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("picActivity", false);
                    hashMap.put("picId", "");
                    hashMap.put("picTime", "");
                    hashMap.put("picDate", "");
                    hashMap.put("picLink", "");
                    hashMap.put("picRemark", "");
                    hashMap.put("picLatitude", 0.0);
                    hashMap.put("picLongitude", 0.0);
                    hashMap.put("picUid", "");
                    hashMap.put("reply", true);
                    hashMap.put("replyUid", picUid);

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                    reference1.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    makePicActivityMemberOffline(hashMap,picUid , siteId);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReplyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else if(type.equals("Audio")) {
            String filePathAndName = "PicActivity/" + String.valueOf(siteId) + "/" + currentDate + "/" + System.currentTimeMillis();
            Log.e("StorageRecording",filePathAndName);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    Log.e("StorageRecording","Task Successfull");
                    String timestamp = "" + System.currentTimeMillis();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadImageUri = uriTask.getResult();

                    String currentTime = null;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("replyMsg",binding.etYourRemark.getText().toString());
                    hashMap.put("reply", true);
                    hashMap.put("replyTime", finalCurrentTime);
                    hashMap.put("replyDate", finalCurrentDate);
                    hashMap.put("replyType", "Audio");
                    hashMap.put("replyLink", ""+downloadImageUri.toString());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    String finalCurrentTime = currentTime;
                    reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(picUid).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    binding.etYourRemark.getText().clear();
                                    reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(picId).updateChildren(hashMap);
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("picActivity", false);
                                    hashMap.put("picId", "");
                                    hashMap.put("picTime", "");
                                    hashMap.put("picDate", "");
                                    hashMap.put("picLink", "");
                                    hashMap.put("picRemark", "");
                                    hashMap.put("picLatitude", 0.0);
                                    hashMap.put("picLongitude", 0.0);
                                    hashMap.put("picUid", "");
                                    hashMap.put("reply",true);
                                    hashMap.put("replyUid",picUid);

                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");

                                    reference1.child(firebaseAuth.getUid()).child("Industry").child("Construction")
                                            .child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    makePicActivityMemberOffline(hashMap,picUid , siteId);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ReplyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReplyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.e("StorageRecording",e.getMessage());
                }
            });

        }

    }

    private Boolean checkformicrophonePresence() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        }else{
            return false;
        }

    }

    private void checkAudioPermission() {
        Log.e("Microphone","Permission"+(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED));
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
            startRecording();
        }else{
            Log.e("Microphone","else");
            requestRecordAudioPermission();
        }

    }
    private void startRecording(){
        binding.etYourRemark.setVisibility(View.GONE);
        binding.txtRecording.setVisibility(View.VISIBLE);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioSamplingRate(16000);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception1",e.getMessage());
        }

    }
    private String getFilename(){
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            Log.e("StrPath", str_path);
            file = new File(str_path, "Record"+System.currentTimeMillis());

            FileOutputStream fos = new FileOutputStream(file);
            Log.e("FilePath", file.getAbsolutePath().toString());

        } catch (IOException e) {
            Log.e("IOEXCEPTION", e.getMessage());
            e.printStackTrace();
        } finally {}
        return (file.getAbsolutePath());
    }
    private void makePicActivityMemberOffline(HashMap<String, Object> hashMap, String picUid, long siteId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(picUid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getToken(picUid);
                Intent intent=new Intent(ReplyActivity.this, ChattingActivity.class);
                intent.putExtra("siteId",siteId);
                startActivity(intent);
                finish();

            }
        });
    }

    private void getToken(String picUid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(picUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token =snapshot.child("token").getValue(String.class);
                sendNotification(token);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String token) {
        Log.d("token : ", token);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();

            jsonobject_notification.put("title", "You have a new message");
            jsonobject_notification.put("body",   getSharedPreferences("UserDetails", MODE_PRIVATE).getString("designation","Site Admin")+" "+"has replied to your message");




            JSONObject jsonobject_data = new JSONObject();
            jsonobject_data.put("imgurl", "https://firebasestorage.googleapis.com/v0/b/haajiri-register.appspot.com/o/FCMImages%2Fopt2.png?alt=media&token=f4e37ac3-a1ff-417f-a0c7-3e5445515505");

            //JSONObject jsonobject = new JSONObject();

            js.put("to", token);
            js.put("notification", jsonobject_notification);
            js.put("data", jsonobject_data);

            //js.put("", jsonobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {






            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error :  ", String.valueOf(error));


            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer AAAAOXCkmcc:APA91bFUByxO9XAex4Tdz9fLVUDpRhbAL1XbQ_pKBA0JNFEX_wHhSoMcWzRwbsBDOYV0AzS60c8beYsnHlA9zXj829SlBGScHFH675E5TIkzYHKQZEvDxnRgP9Rv4EUx1_8Nq2HbtK3a ");//TODO add Server key here
                //headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        requestQueue.add(jsonObjReq);
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {

        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {

        }
    };

    private void stopRecording(){
        binding.etYourRemark.setVisibility(View.VISIBLE);
        binding.txtRecording.setVisibility(View.GONE);
        if(null != recorder){
            try{
                recorder.stop();
                recorder.reset();
                recorder.release();
                showPlayerDialog();
//                uploadToStorage();

                recorder = null;
            }catch (RuntimeException e){
                e.printStackTrace();
            }


        }
    }


    private void getDetails(long siteId, String picId) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(picId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPicActivity modelPicActivity=snapshot.getValue(ModelPicActivity.class);
                picUid=modelPicActivity.getUploadedbyUid();
                Log.e("PicType",modelPicActivity.getUploadedbyUid());
                if(modelPicActivity.getPicType().equals("Image")){
                    binding.ivImage.setVisibility(View.VISIBLE);
                    binding.ivVideo.setVisibility(View.GONE);
                    if (modelPicActivity.getPicLink() != null && (!TextUtils.isEmpty(modelPicActivity.getPicLink()))) {
                        Picasso.get().load(modelPicActivity.getPicLink()).
                                resize(400, 400).centerCrop()
                                .placeholder(R.drawable.ic_download).into(binding.ivImage);
                    }
                    binding.txtRemark.setText(modelPicActivity.getPicRemark());
                    binding.llImage.setVisibility(View.VISIBLE);

                }else  if(modelPicActivity.getPicType().equals("Video")){
                    binding.ivImage.setVisibility(View.GONE);
                    binding.ivVideo.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(modelPicActivity.getPicLink());
                    binding.ivVideo.setVideoURI(uri);
                    binding.ivVideo.setMediaController(new MediaController(ReplyActivity.this));
                    binding.ivVideo.requestFocus();
                    binding.ivVideo.start();
                    binding.txtRemark.setText(modelPicActivity.getPicRemark());
                    binding.llImage.setVisibility(View.VISIBLE);
                }else{
                    binding.ivImage.setVisibility(View.GONE);
                    binding.ivVideo.setVisibility(View.GONE);
                    binding.llImage.setVisibility(View.GONE);
                    binding.txtRemark.setText(modelPicActivity.getPicMsg());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                binding.etYourRemark.setText(
                        Objects.requireNonNull(result).get(0));

            }

        }
    }
    private void showPlayerDialog() {
        MediaPlayer mediaPlayer=new MediaPlayer();
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(ReplyActivity.this);
        String currentDate = null;
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = df1.format(c);
        }
        String currentTime = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        View mView = LayoutInflater.from(ReplyActivity.this).inflate(R.layout.diakog_player, null);
        ImageView iv_play;
        TextView iv_pause;
        iv_play=mView.findViewById(R.id.iv_play);
        iv_pause=mView.findViewById(R.id.iv_send);
        alert.setView(mView);
        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                if(mediaPlayer.isPlaying()){
                    iv_play.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else{
                    iv_play.setImageResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();


                }







            }
        });
        iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();
                uploadToStorage("Audio");
            }
        });
        alertDialog.show();


    }


    private void requestRecordAudioPermission() {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (this.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {

        } else {

            Toast.makeText(this, "This app needs to record audio through the microphone....", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try{
                    requestPermissions(new String[]{requiredPermission,Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_AUDIO);
                }catch(RuntimeException e){
                    e.getMessage();
                    Log.e("Exception",e.getMessage());
                }

            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // This method is called when the  permissions are given
        }else{
            Log.e("Microphone","Permission Denied");
        }

    }
}