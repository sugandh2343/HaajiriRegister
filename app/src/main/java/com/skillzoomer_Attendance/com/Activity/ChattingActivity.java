package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.icu.text.SimpleDateFormat;
import android.location.LocationManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.iceteck.silicompressorr.SiliCompressor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.skillzoomer_Attendance.com.Adapter.AdapterPicActivity;
import com.skillzoomer_Attendance.com.Model.ModelPicActivity;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityChattingBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChattingActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityChattingBinding binding;
    String currentDate;
    public int counter = 600;
    FileOutputStream fos = null;
    File file = null;
    private String lastLoginTime;
    private Boolean attendance_activity, pic_activity;
    CountDownTimer countDownTimer = new CountDownTimer(counter * 1000, 1000) {
        @Override
        public void onTick(long l) {
            Log.e("counter", "" + counter);
            int minute = counter / 60;
            int sec = counter % 60;
            if (minute > 0) {
                binding.txtForcedLogoutMsg.setText("" + minute + "minutes and " + sec + " seconds");
            } else {
                binding.txtForcedLogoutMsg.setText("" + sec + " seconds");
            }
            counter--;

        }

        @Override
        public void onFinish() {
            forceLogout();

        }
    };
    ProgressDialog pd;
    private static final int Location_Request_code = 100;
    private String[] locationPermissions;
    private ProgressDialog progressDialog;
    private String userName;
    private String lastActivityTime;
    FirebaseAuth firebaseAuth;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorLogin;
    String userType, siteName;
    long siteId;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final int REQUEST_DIALOG_CODE_SPEECH_INPUT = 2;
    private static final int RECORD_AUDIO = 3;
    private int counterValue = 3600;
    ArrayList<String> result;
    private final int REQUEST_IMAGE = 400;
    private final int REQUEST_VIDEO = 500;
    private Uri image_uri1;
    private Bitmap image_uri;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private double latitude, longitude;
    private ArrayList<ModelSite> siteAdminList;
    private ImageView iv_image;
    private VideoView video_pic;
    EditText et_remark_dialog;
    private ArrayList<ModelPicActivity> picActivityArrayList;
    String siteStatus = "Registered";
    MediaRecorder mediaRecorder;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private AudioRecord audioRecord = null;
    private int currentFormat = 1;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};

    private String timestamp;
    long AmountRuleExcel = 0, AmountRulePdf = 0;
    int amount = 0;
    int AmountTemp = 0;

    AlertDialog alertDialogPaymentConfirm = null;



    String uid;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChattingBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editorLogin = spLogin.edit();
        Intent intent = getIntent();
        lastLoginTime = spLogin.getString("LastLoginTime", "NA");
        lastActivityTime = spLogin.getString("LastActivityTime", "NA");
        attendance_activity = spLogin.getBoolean("AttendanceActivity", false);
        pic_activity = spLogin.getBoolean("PicActivity", false);
        userType = sharedpreferences.getString("userDesignation", "");

        if(userType.equals("Supervisor")){
            uid=getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }
        siteAdminList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        timestamp = "" + System.currentTimeMillis();
        if (lastActivityTime.equals("NA")) {
            lastActivityTime = lastLoginTime;
        }
        Log.e("ForceLogout", "" + sharedpreferences.getBoolean("ForceLogout", false));
        if ((userType.equals("Supervisor") && sharedpreferences.getBoolean("ForceLogout", false))) {
            if (!lastLoginTime.equals("NA")) {
                binding.llForceLogout.setVisibility(View.VISIBLE);
                String currentTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                }
                DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = df.parse(currentTime);
                    date2 = df.parse(lastActivityTime);
                } catch (ParseException e) {
                    Log.e("DateException", e.getMessage());
                    e.printStackTrace();
                }

                long diff = date1.getTime() - date2.getTime();
                String currentTime1 = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(diff);
                }
                Log.e("CurrentTime1", currentTime1);
                Log.e("Date1", "" + date1.getTime());
                Log.e("Date2", "" + date2.getTime());
                int timeInSeconds = (int) (diff / 1000);
                Log.e("timeInSeconds", "" + timeInSeconds);
//                    int hours, minutes, seconds;
//                    hours = timeInSeconds / 3600;
//                    timeInSeconds = timeInSeconds - (hours * 3600);
//                    minutes = timeInSeconds / 60;
//                    timeInSeconds = timeInSeconds - (minutes * 60);
//                    seconds = timeInSeconds;
                counter = counterValue - timeInSeconds;
                if (counter <= 0) {
                    Log.e("ForceLogout", "ForceLogout");
                    forceLogout();
                }
                countDownTimer.start();

            } else {
                binding.llForceLogout.setVisibility(View.GONE);
                countDownTimer.cancel();

            }


        } else {
            binding.llForceLogout.setVisibility(View.GONE);
            countDownTimer.cancel();

        }
        if (userType.equals("Supervisor")) {
            siteName = sharedpreferences.getString("siteName", "");
            siteId = sharedpreferences.getLong("siteId", 0);
            binding.txtSiteId.setText(String.valueOf(getString(R.string.siteID) + ": " + siteId));
            binding.spinnerSelectSite.setVisibility(View.GONE);
            binding.txtSiteId.setVisibility(View.VISIBLE);
            binding.rvChatHistory.setVisibility(View.VISIBLE);
            binding.txtSelectSite.setVisibility(View.GONE);
            binding.btnDownload.setVisibility(View.GONE);

        } else {
            siteId = intent.getLongExtra("siteId", 0);
            binding.btnDownload.setVisibility(View.VISIBLE);

            if (intent.getStringExtra("message") == null || intent.getStringExtra("message").equals("")) {
                binding.msgCard.setVisibility(View.GONE);
                binding.spinnerSelectSite.setVisibility(View.VISIBLE);
                getSiteListAdministrator();
                binding.txtSiteId.setVisibility(View.GONE);
                binding.rvChatHistory.setVisibility(View.GONE);
            } else if (intent.getStringExtra("message").equals("Allow")) {
                siteStatus = "Pending";
                binding.msgCard.setVisibility(View.VISIBLE);
                binding.spinnerSelectSite.setVisibility(View.GONE);
                binding.txtSiteId.setVisibility(View.VISIBLE);
                binding.txtSiteId.setText(String.valueOf(getString(R.string.siteID) + ": " + siteId));
                binding.rvChatHistory.setVisibility(View.VISIBLE);
                binding.txtSelectSite.setVisibility(View.GONE);
                binding.btnDownload.setVisibility(View.GONE);
//                progressDialog.show();
                loadChats();

            }

        }


        userName = sharedpreferences.getString("fullName", "");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        picActivityArrayList = new ArrayList<>();
        SimpleDateFormat df1 = null;
//        progressDialog.show();
//        loadChats();


//        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ReportRules");
//                reference.child("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        AmountRuleExcel = snapshot.child("Excel").getValue(long.class);
//                        AmountRulePdf = snapshot.child("Pdf").getValue(long.class);
//                        file = null;
//                        fos = null;
//                        final AlertDialog.Builder alert = new AlertDialog.Builder(ChattingActivity.this);
//                        View mView = getLayoutInflater().inflate(R.layout.payment_dialog, null);
//
//                        TextView txt_file_type, txt_from, txt_to, txt_amount, txt_mode_of_payment, txt_heading;
//                        Spinner spinner_promo;
//                        Button btn_cancel, btn_pay;
//                        txt_file_type = mView.findViewById(R.id.txt_file_type);
//                        txt_from = mView.findViewById(R.id.txt_from);
//                        txt_to = mView.findViewById(R.id.txt_to);
//                        txt_amount = mView.findViewById(R.id.txt_amount);
//                        txt_mode_of_payment = mView.findViewById(R.id.txt_mode_of_payment);
//                        txt_heading = mView.findViewById(R.id.txt_heading);
//                        spinner_promo = mView.findViewById(R.id.spinner_promo);
//                        spinner_promo.setVisibility(View.GONE);
//                        txt_heading.setText(getString(R.string.your_file_one_click_away) + " " + String.valueOf(AmountRulePdf) + " " + getString(R.string.your_have_tom_make_payment));
//                        txt_amount.setText(getString(R.string.rupee_symbol) + " " + String.valueOf(AmountRulePdf));
//                        btn_cancel = mView.findViewById(R.id.btn_cancel);
//                        btn_pay = mView.findViewById(R.id.btn_pay);
//                        txt_file_type.setText(getString(R.string.attendance_report));
//                        txt_from.setText(picActivityArrayList.get(0).getDateOfUpload());
//                        txt_to.setText(picActivityArrayList.get(picActivityArrayList.size() - 1).getDateOfUpload());
//
//                        txt_mode_of_payment.setText("Razorpay");
//                        alert.setView(mView);
//                        alertDialogPaymentConfirm = alert.create();
//                        alertDialogPaymentConfirm.setCanceledOnTouchOutside(false);
//                        btn_pay.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Thread createOrderId = new Thread(new ChattingActivity.createOrderIdThread((int) AmountRulePdf));
//                                createOrderId.start();
//
//
//                            }
//                        });
//                        btn_cancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alertDialogPaymentConfirm.dismiss();
//                            }
//                        });
//
//
////
//                        alertDialogPaymentConfirm.show();
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });
        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChattingActivity.this,DownloadChatActivity.class));
            }
        });

        if (userType.equals("Supervisor")) {
            Log.e("Progress", "show");
//            showChats asyncTask = new showChats();
//            asyncTask.execute();
            loadChats();
        }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                df1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                currentDate = df1.format(c);
            }
//        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (picActivityArrayList.size() > 0) {
//                    binding.rvChatHistory.setLayoutManager(new LinearLayoutManager(ChattingActivity.this, LinearLayoutManager.VERTICAL, false) {
//                        @Override
//                        public void onLayoutCompleted(RecyclerView.State state) {
//                            super.onLayoutCompleted(state);
//                            // TODO
//                            Log.e("DownloadingPdf", "PDF");
//                            getScreenshotFromRecyclerView(binding.rvChatHistory);
//                        }
//                    });
//
//                }
//
//            }
//        });
            binding.etMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        binding.ivMic.setVisibility(View.GONE);
                        binding.ivSend.setVisibility(View.VISIBLE);
                    } else {
                        binding.ivMic.setVisibility(View.VISIBLE);
                        binding.ivSend.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            binding.ivCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    permissionCheck("Camera");

                }
            });
            binding.ivRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChattingActivity.this, "Hold this icon to send voice recording", Toast.LENGTH_LONG).show();
                }
            });
            binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i > 0) {
                        siteId = siteAdminList.get(i).getSiteId();
                        siteName = siteAdminList.get(i).getSiteName();
                        Log.e("SiteId", "Spinner" + siteId);
                        binding.rvChatHistory.setVisibility(View.VISIBLE);
                        binding.txtSelectSite.setVisibility(View.GONE);
//                        progressDialog.show();
//                        loadChats();
                        showChats asyncTask = new showChats();
                        asyncTask.execute();
                    } else {

                        binding.rvChatHistory.setVisibility(View.GONE);
                        binding.txtSelectSite.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            binding.ivSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    String timestamp = "" + System.currentTimeMillis();

                    String currentTime = null;
                    String filePathAndName = "PicActivity/" + String.valueOf(siteId) + "/" + currentDate + "/" + timestamp;
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("picId", timestamp);
                    hashMap.put("picMsg", binding.etMessage.getText().toString());
                    hashMap.put("siteId", siteId);
                    hashMap.put("uploadedbyUid", firebaseAuth.getUid());
                    hashMap.put("picLink", "");
                    hashMap.put("picRemark", "");
                    hashMap.put("dateOfUpload", currentDate);
                    hashMap.put("timeofUpload", currentTime);
                    hashMap.put("picType", "Text");
                    hashMap.put("uploadedBytype", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userDesignation", ""));
                    hashMap.put("uploadedByName", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                    if (latitude > 0 && longitude > 0) {
                        hashMap.put("picLatitude", latitude);
                        hashMap.put("picLongitude", longitude);
                    }
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    String finalCurrentTime = currentTime;
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Log.e("Task", "storage");
                                    Log.e("siteIddWA", "" + siteId);
                                    if (userType.equals("Supervisor")) {
                                        updateToSite(siteId, finalCurrentTime, currentDate, null, timestamp, "Text");
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });
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
                                .makeText(ChattingActivity.this, " " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });
//        binding.ivMic.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                checkAudioPermission();
//                return false;
//            }
//        });
            binding.ivVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    permissionCheck("Video");
                }
            });
//        binding.ivRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {

//
//
//            }
//        });
            binding.ivRecord.setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Boolean microphone = checkformicrophonePresence();
                        Log.e("Microphone", "" + microphone);
                        if (microphone) {
                            checkAudioPermission();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
//                        AppLog.logString("stop Recording");
//                        stopRecording();
                        Log.e("Recording", "Stopped");
                        stopRecording();
                        break;
                }
                return false;
            });


        }


    public class createOrderIdThread implements Runnable {
        int amount;

        public createOrderIdThread(int amount) {
            this.amount = amount;
        }

        @Override
        public void run() {
            RazorpayClient razorpay = null;
            try {
                razorpay = new RazorpayClient(getString(R.string.razorpay_key_id), getString(R.string.razorpay_key_secret));
            } catch (RazorpayException e) {
                e.printStackTrace();
            }
            JSONObject orderRequest = new JSONObject();
            try {
                Log.e("Amt", "Request" + amount);
                orderRequest.put("amount", amount * 100); // amount in the smallest currency unit
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_11");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Exception", "COID");
            Order order = null;
            try {
                order = razorpay.orders.create(orderRequest);
            } catch (RazorpayException e) {
                Log.e("Exception", e.getMessage());
                e.printStackTrace();
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(String.valueOf(order));
                String id = jsonObject.getString("id");
                Log.e("Response", "" + id);
                if (id != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startPayment(id, amount);
                        }
                    });
                }

            } catch (JSONException e) {
                Log.e("Exception", e.getMessage());
                e.printStackTrace();
            }


        }
    }

    private void startPayment(String id, int amount) {
        Checkout.preload(this);
        Checkout checkout = new Checkout();
        checkout.setKeyID(getString(R.string.razorpay_key_id));
        /**
         * Instantiate Checkout
         */


        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo_razor);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", getString(R.string.app_name));
            options.put("description", "Haj123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("order_id", id);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(amount));//pass amount in currency subunits
            options.put("prefill.email", getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("userName", "") + "@yopmail.com");
            options.put("prefill.contact", getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("userMobile", ""));
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(this, options);
            progressDialog.dismiss();

        } catch (Exception e) {
            Log.e("Sugandh", "Error in starting Razorpay Checkout", e);
        }
    }


    private void DownloadPdfChat(Bitmap bigBitmap) {
//        Bitmap recycler_view_bm = getScreenshotFromRecyclerView(binding.rvChatHistory);
        Bitmap recycler_view_bm=bigBitmap;
        try {

            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "Chat_Rep_" + siteName + timestamp + ".pdf";
            File pdfFile = new File(str_path);

//            pdfFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(pdfFile);

            PdfDocument document = new PdfDocument();
            com.itextpdf.text.Document iText_xls_2_pdf = new Document();
            iText_xls_2_pdf.setPageSize(new Rectangle(1224, 1584));
            try {
                PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(pdfFile));
            } catch (DocumentException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();

            } catch (FileNotFoundException e) {
                Log.e("exception123", e.getMessage());
                e.printStackTrace();
            }
            iText_xls_2_pdf.open();
            String date_val = "Industry Name: " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("industryName", "") + "\n" + "Generated On: " + currentDate + "\n" + "Company Name:" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("companyName", "") + "\n" + "Site Id: " + siteId + "\n Site Name: " + siteName;
            ;
            Paragraph p1 = new Paragraph(date_val);
            p1.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph p2 = new Paragraph("Work Activity Report");
            p2.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph p3 = new Paragraph("\n");
            p3.setAlignment(Paragraph.ALIGN_CENTER);


            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            recycler_view_bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAlignment(Element.ALIGN_CENTER);
            image.setWidthPercentage(100);
            image.setScaleToFitHeight(true);

            try {
                iText_xls_2_pdf.add(p1);
                iText_xls_2_pdf.add(p2);
                iText_xls_2_pdf.add(p3);
                iText_xls_2_pdf.add(image);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            iText_xls_2_pdf.close();


//            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(recycler_view_bm.getWidth(), recycler_view_bm.getHeight(), 2).create();
//            PdfDocument.Page page = document.startPage(pageInfo);
//            recycler_view_bm.prepareToDraw();
//            Canvas c;
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(20);
//
//            c = page.getCanvas();
//            c.drawPaint(paint);
//
////            c.drawText(date_val, 10, 25, paint);
////            c.drawText(date_val,0,0,null);
//            c.drawBitmap(recycler_view_bm, 0, 0, null);
//            document.finishPage(page);
//            document.writeTo(fOut);
//            document.close();
//            Toast.makeText(this, "Report Generated Successfully", Toast.LENGTH_SHORT).show();

//            Snackbar snackbar = Snackbar
//                    .make(equipmentsRecordActivityLayout, "PDF generated successfully.", Snackbar.LENGTH_LONG)
//                    .setAction("Open", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            openPDFRecord(pdfFile);
//                        }
//                    });
//
//            snackbar.show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            Log.e("Size12323234213424", "" + adapter.getItemCount());
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {

                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
//                Picasso.get().load(labourList.get(i).getProfile()).
//                        resize(400,400).centerCrop()
//                        .placeholder(R.drawable.profile).into((ImageView) holder.itemView.findViewById(R.id.img_profile));
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();

                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
                if (i % 2 == 0 && i + 2 < size) {
                    binding.rvChatHistory.scrollToPosition(i + 2);
                }

            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);


            for (int i = 0; i < size; i++) {

                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
       DownloadPdfChat(bigBitmap);
    }

    private String getFilename() {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), Context.MODE_PRIVATE);
//            Log.e("Directory",directory.getAbsolutePath());
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            Log.e("StrPath", str_path);
            file = new File(str_path, "Record" + System.currentTimeMillis());

            FileOutputStream fos = new FileOutputStream(file);
            Log.e("FilePath", file.getAbsolutePath().toString());

        } catch (IOException e) {
            Log.e("IOEXCEPTION", e.getMessage());
            e.printStackTrace();
        } finally {
        }
        return (file.getAbsolutePath());
    }

    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteAdminList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
                    siteAdminList.add(modelSite);
                }

                Log.e("siteAdminList", "" + siteAdminList.size());
                siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0));
                SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                siteId = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId();
                siteName = "";
                binding.llSelectSite.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        if (picActivityArrayList.size() > 0) {
            binding.rvChatHistory.setLayoutManager(new LinearLayoutManager(ChattingActivity.this, LinearLayoutManager.VERTICAL, false) {
                @Override
                public void onLayoutCompleted(RecyclerView.State state) {
                    super.onLayoutCompleted(state);
                    // TODO
                    Log.e("DownloadingPdf", "PDF");
                  getScreenshotFromRecyclerView(binding.rvChatHistory);
                }
            });

        }
        alertDialogPaymentConfirm.dismiss();
        final AlertDialog.Builder alert = new AlertDialog.Builder(ChattingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.payment_sucess, null);
        TextView txt_payment_id, txt_payment_amt, txt_message;
        Button btn_ok;
        txt_payment_id = mView.findViewById(R.id.txt_payment_id);
        txt_payment_amt = mView.findViewById(R.id.txt_payment_amt);
        txt_message = mView.findViewById(R.id.txt_message);
        btn_ok = mView.findViewById(R.id.btn_ok);
        txt_payment_id.setText(paymentData.getPaymentId());
        txt_payment_amt.setText(String.valueOf(amount));
        txt_message.setText("Your Attendance Report PDF file is generated and stored in DOWNLOADS folder. ");

        alert.setView(mView);
        AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date c = Calendar.getInstance().getTime();

                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                currentDate = df.format(c);
                String currentTime = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new android.icu.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                }
                String timestamp = "" + System.currentTimeMillis();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("dateOfPayment", currentDate);
                hashMap.put("timeOfPayment", currentTime);
                hashMap.put("timestamp", timestamp);
                hashMap.put("paymentId", paymentData.getPaymentId());
                hashMap.put("orderId", paymentData.getOrderId());
                hashMap.put("signature", paymentData.getSignature());
                hashMap.put("paidAmount", String.valueOf(AmountTemp));
                hashMap.put("status", "Success");
                hashMap.put("downloadFile", "Chat Report");
                hashMap.put("siteId", siteId);
                hashMap.put("siteName", siteName);
                hashMap.put("fileType", "Pdf");
                hashMap.put("totalAmount", String.valueOf(AmountTemp));

                hashMap.put("fromDate", picActivityArrayList.get(0).getDateOfUpload());
                hashMap.put("toDate", picActivityArrayList.get(picActivityArrayList.size() - 1).getDateOfUpload());

                hashMap.put("promoApplied", false);
                hashMap.put("promoTitle", "");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(firebaseAuth.getUid()).child("Payments").child(timestamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e("paymentData", paymentData.getData().toString());
                                Log.e("paymentData", paymentData.getPaymentId().toString());
//

                                Log.e("fos", "flush");
                                Toast.makeText(ChattingActivity.this, "Excel Sheet Generated and stored in downloads folder", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                Uri dirUri = FileProvider.getUriForFile(ChattingActivity.this,getApplicationContext().getPackageName() + ".com.skillzoomer_Attendance.com",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
//                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                alertDialog.dismiss();
                            }


//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
//                        startActivity(intent);


                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
        alertDialog.show();


    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

    }

    class SiteSpinnerAdapter
            extends BaseAdapter {
        SiteSpinnerAdapter() {
        }

        public int getCount() {
            return siteAdminList.size();
        }

        public Object getItem(int n) {
            return null;
        }

        public long getItemId(int n) {
            return 0L;
        }

        public View getView(int n, View view, ViewGroup viewGroup) {
            View view2 = getLayoutInflater().inflate(R.layout.layout_of_country_row, null);
            TextView textView = (TextView) view2.findViewById(R.id.spinner_text);
            textView.setText(siteAdminList.get(n).getSiteCity());
            return view2;
        }
    }

    private void startRecording() {
        binding.etMessage.setVisibility(View.GONE);
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
            Log.e("Exception", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception1", e.getMessage());
        }

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

    private void stopRecording() {
        binding.etMessage.setVisibility(View.VISIBLE);
        binding.txtRecording.setVisibility(View.GONE);
        if (null != recorder) {
            try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                showPlayerDialog();
//                uploadToStorage();

                recorder = null;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }


        }
    }

    private void showPlayerDialog() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(ChattingActivity.this);
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
        View mView = LayoutInflater.from(ChattingActivity.this).inflate(R.layout.diakog_player, null);
        ImageView iv_play;
        TextView iv_pause;
        iv_play = mView.findViewById(R.id.iv_play);
        iv_pause = mView.findViewById(R.id.iv_send);
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
                if (mediaPlayer.isPlaying()) {
                    iv_play.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    iv_play.setImageResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();


                }


            }
        });
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.start();
//            }
//        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                iv_play.setImageResource(R.drawable.ic_play);
                mediaPlayer.seekTo(0);
            }
        });
        iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                progressDialog.show();
                uploadToStorage();
            }
        });
        alertDialog.show();


    }

    private void uploadToStorage() {


        String filePathAndName = "PicActivity/" + String.valueOf(siteId) + "/" + currentDate + "/" + System.currentTimeMillis();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                Log.e("Task", "Successfull");
                String timestamp = "" + System.currentTimeMillis();
                while (!uriTask.isSuccessful()) ;
                Uri downloadImageUri = uriTask.getResult();

                String currentTime = null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                }
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("picId", timestamp);
                hashMap.put("picMsg", binding.etMessage.getText().toString());
                hashMap.put("siteId", siteId);
                hashMap.put("uploadedbyUid", firebaseAuth.getUid());
                hashMap.put("picLink", "" + downloadImageUri.toString());
                hashMap.put("picRemark", "");
                hashMap.put("dateOfUpload", currentDate);
                hashMap.put("timeofUpload", currentTime);
                hashMap.put("picType", "Audio");
                hashMap.put("uploadedBytype", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userDesignation", ""));
                hashMap.put("uploadedByName", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                if (latitude > 0 && longitude > 0) {
                    hashMap.put("picLatitude", latitude);
                    hashMap.put("picLongitude", longitude);
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                String finalCurrentTime = currentTime;
                reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.etMessage.getText().clear();
                                Log.e("Task", "storage");
                                Log.e("siteIddWA", "" + siteId);
                                if (userType.equals("Supervisor")) {
                                    updateToSite(siteId, finalCurrentTime, currentDate, downloadImageUri, timestamp, "Audio");
                                } else {
//                                    progressDialog.dismiss();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(ChattingActivity.this, timelineActivity.class));
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.dismiss();
                                Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        //sending broadcast message to scan the media file so that it can be available
    }

    private Boolean checkformicrophonePresence() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        } else {
            return false;
        }

    }

    private void checkAudioPermission() {
        Log.e("Microphone", "Permission" + (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        } else {
            Log.e("Microphone", "else");
            requestRecordAudioPermission();
        }

    }

    private void loadChats() {
        Log.e("chatUid",uid);
        Log.e("chatUid",String.valueOf(siteId));
        if (userType.equals("Supervisor")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").orderByChild("uploadedbyUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    picActivityArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelPicActivity modelPicActivity = ds.getValue(ModelPicActivity.class);
                        picActivityArrayList.add(modelPicActivity);
                    }
                    if (picActivityArrayList.size() > 0) {
                        progressDialog.dismiss();
                        AdapterPicActivity adapterPicActivity = new AdapterPicActivity(ChattingActivity.this, picActivityArrayList, userType, siteStatus);
                        binding.rvChatHistory.setAdapter(adapterPicActivity);
                        binding.txtNochat.setVisibility(View.GONE);
                        binding.rvChatHistory.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        binding.txtNochat.setVisibility(View.VISIBLE);
                        binding.rvChatHistory.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    picActivityArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelPicActivity modelPicActivity = ds.getValue(ModelPicActivity.class);
                        picActivityArrayList.add(modelPicActivity);
                    }
                    if (picActivityArrayList.size() > 0) {
                        progressDialog.dismiss();
                        AdapterPicActivity adapterPicActivity = new AdapterPicActivity(ChattingActivity.this, picActivityArrayList, userType, siteStatus);
                        binding.rvChatHistory.setAdapter(adapterPicActivity);
                        binding.txtNochat.setVisibility(View.GONE);
                        binding.rvChatHistory.setVisibility(View.VISIBLE);
                    } else {
                        progressDialog.dismiss();
                        binding.txtNochat.setVisibility(View.VISIBLE);
                        binding.rvChatHistory.setVisibility(View.GONE);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    public class showChats extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("PreExecute", "Pre");
            pd = new ProgressDialog(ChattingActivity.this);
            pd.setTitle(getString(R.string.please_wait));
            binding.txtNochat.setVisibility(View.GONE);
            binding.txtSelectSite.setVisibility(View.GONE);
//             progressDialog.setMessage("Laoding Chats.....");
//             progressDialog.show();
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.setMessage("Loading Chats");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (userType.equals("Supervisor")) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").orderByChild("uploadedbyUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        picActivityArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelPicActivity modelPicActivity = ds.getValue(ModelPicActivity.class);
                            picActivityArrayList.add(modelPicActivity);
                        }
                        if (picActivityArrayList.size() > 0) {
//                        progressDialog.dismiss();
                            AdapterPicActivity adapterPicActivity = new AdapterPicActivity(ChattingActivity.this, picActivityArrayList, userType, siteStatus);
                            binding.rvChatHistory.setAdapter(adapterPicActivity);
                            binding.txtNochat.setVisibility(View.GONE);
                            binding.rvChatHistory.setVisibility(View.VISIBLE);

                        } else {
//                        progressDialog.dismiss();
                            binding.txtNochat.setVisibility(View.VISIBLE);
                            binding.rvChatHistory.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        picActivityArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelPicActivity modelPicActivity = ds.getValue(ModelPicActivity.class);
                            picActivityArrayList.add(modelPicActivity);
                        }
                        if (picActivityArrayList.size() > 0) {
                            progressDialog.dismiss();
                            AdapterPicActivity adapterPicActivity = new AdapterPicActivity(ChattingActivity.this, picActivityArrayList, userType, siteStatus);
                            binding.rvChatHistory.setAdapter(adapterPicActivity);
                            binding.txtNochat.setVisibility(View.GONE);
                            binding.rvChatHistory.setVisibility(View.VISIBLE);
                        } else {
                            progressDialog.dismiss();
                            binding.txtNochat.setVisibility(View.VISIBLE);
                            binding.rvChatHistory.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("PostExecute","Done");
            pd.dismiss();



        }
    }

    private void permissionCheck(String type) {
        Dexter.withActivity(ChattingActivity.this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        Log.e("Denied",""+report.getDeniedPermissionResponses().get(0).getPermissionName());
                        if (report.areAllPermissionsGranted()) {
                            if (type.equals("Camera")) {
                                launchCameraIntentForTax();
                            } else {
                                launchVideoIntentforTax();
                            }

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void launchVideoIntentforTax() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 5491520L);//5*1048*1048=5MB
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, REQUEST_VIDEO);

    }

    private void launchCameraIntentForTax() {
        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image title");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_ Image Description");
//        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);
        builder.setTitle(R.string.grant_permission);
        builder.setMessage(R.string.this_app_requires_permission);
        builder.setPositiveButton(R.string.goto_settings, (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", ChattingActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                binding.etMessage.setText(
                        Objects.requireNonNull(result).get(0));

            }

        } else if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_uri = imageBitmap;
            openImageDialog(imageBitmap, "Camera", null);


        } else if (requestCode == REQUEST_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Uri selectedVideoUri = data.getData();
            openImageDialog(null, "Video", selectedVideoUri);
        } else if (requestCode == REQUEST_DIALOG_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            et_remark_dialog.setText(
                    Objects.requireNonNull(result).get(0));
        } else if (requestCode == RECORD_AUDIO && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Uri soundRecordUri = data.getData();
        }
    }

    private void openImageDialog(Bitmap imageBitmap, String activity, Uri selectedVideoUri) {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(ChattingActivity.this);
        View mView = LayoutInflater.from(ChattingActivity.this).inflate(R.layout.dialog_image_pic_activity, null);

        ImageView iv_mic, iv_image;
        Button btn_send;
        VideoView iv_video;


        iv_mic = mView.findViewById(R.id.iv_mic);
        iv_image = mView.findViewById(R.id.iv_image);
        btn_send = mView.findViewById(R.id.btn_send);
        iv_video = mView.findViewById(R.id.iv_video);
        et_remark_dialog = mView.findViewById(R.id.et_remark);
        alert.setView(mView);
        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.ENGLISH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                try {

                    startActivityForResult(intent, REQUEST_DIALOG_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(ChattingActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        if (activity.equals("Camera")) {
            iv_image.setVisibility(View.VISIBLE);
            iv_video.setVisibility(View.GONE);
            iv_image.setImageBitmap(imageBitmap);
        } else {
            iv_image.setVisibility(View.GONE);
            iv_video.setVisibility(View.VISIBLE);

            iv_video.setMediaController(new MediaController(this));
            iv_video.setVideoURI(selectedVideoUri);
            iv_video.requestFocus();
            iv_video.start();

        }


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog=new ProgressDialog(ChattingActivity.this);
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();


                String timestamp = "" + System.currentTimeMillis();
                String currentTime = null;
                String filePathAndName = "PicActivity/" + String.valueOf(siteId) + "/" + currentDate + "/" + timestamp;
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
                }
                String finalCurrentTime = currentTime;
                String finalCurrentTime1 = currentTime;
                if (activity.equals("Camera")) {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image_uri.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    byte[] data = bytes.toByteArray();
                    filePathAndName = "PicActivity/" + String.valueOf(siteId) + "/" + currentDate + "/" + timestamp;
                    storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                    finalCurrentTime = currentTime;
                    finalCurrentTime1 = currentTime;
                    String finalCurrentTime2 = finalCurrentTime1;
                    String finalCurrentTime3 = finalCurrentTime;
                    storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    Uri downloadImageUri = uriTask.getResult();
                                    if (uriTask.isSuccessful()) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("picId", timestamp);
                                        hashMap.put("picRemark", et_remark_dialog.getText().toString());
                                        hashMap.put("siteId", siteId);
                                        hashMap.put("uploadedbyUid", firebaseAuth.getUid());
                                        hashMap.put("picLink", "" + downloadImageUri.toString());
                                        hashMap.put("dateOfUpload", currentDate);
                                        hashMap.put("timeofUpload", finalCurrentTime2);
                                        hashMap.put("picType", "Image");
                                        hashMap.put("uploadedBytype", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userDesignation", ""));
                                        hashMap.put("uploadedByName", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                                        if (latitude > 0 && longitude > 0) {
                                            hashMap.put("picLatitude", latitude);
                                            hashMap.put("picLongitude", longitude);
                                        }
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(timestamp).setValue(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.e("Task", "storage");
                                                        Log.e("siteIddWA", "" + siteId);
                                                        if (userType.equals("Supervisor")) {
                                                            alertDialog.dismiss();
                                                            updateToSite(siteId, finalCurrentTime3, currentDate, downloadImageUri, timestamp, "Image");
                                                        } else {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(ChattingActivity.this, timelineActivity.class));
                                                        }

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                                        progressDialog.dismiss();
                                                        Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    progressDialog.dismiss();
                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                else {
                    String finalCurrentTime4 = finalCurrentTime1;
//                    try {
//                        progressDialog.show();
//                        String compressFilePath = "";
//                        Log.e("Compressing","Video");
////                        compressFilePath = SiliCompressor.with(ChattingActivity.this).compressVideo(selectedVideoUri.toString(),);
////                        new VideoCompressAsyncTask(ChattingActivity.this).execute(selectedVideoUri., file.getParent());
////
////                        new VideoCompressAsyncTask(getActivity()).execute(file.getAbsolutePath(), file.getParent());
//
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                        Log.e("Comprssing",e.getMessage());
//                        Log.v("Error", e.getMessage());
//                    }
                    storageReference.putFile(selectedVideoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("picId", timestamp);
                                hashMap.put("picRemark", et_remark_dialog.getText().toString());
                                hashMap.put("siteId", siteId);
                                hashMap.put("uploadedbyUid", firebaseAuth.getUid());
                                hashMap.put("picLink", "" + downloadImageUri.toString());
                                hashMap.put("dateOfUpload", currentDate);
                                hashMap.put("timeofUpload", finalCurrentTime4);
                                hashMap.put("picType", "Video");
                                hashMap.put("uploadedBytype", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("userDesignation", ""));
                                hashMap.put("uploadedByName", getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                                if (latitude > 0 && longitude > 0) {
                                    hashMap.put("picLatitude", latitude);
                                    hashMap.put("picLongitude", longitude);
                                }
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("PicActivity").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.e("Task", "storage");
                                                Log.e("siteIddWA", "" + siteId);
                                                if (userType.equals("Supervisor")) {
                                                    alertDialog.dismiss();
                                                    updateToSite(siteId, finalCurrentTime4, currentDate, downloadImageUri, timestamp, "Video");
                                                } else {
//                                                    progressDialog.dismiss();
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(ChattingActivity.this, timelineActivity.class));
                                                }

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Failure", e.getMessage());
                        }
                    });
                }

            }
        });


        alertDialog.show();

    }

    public class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {
                String path = paths[0];
                String directoryPath = paths[1];
                filePath = SiliCompressor.with(mContext).compressVideo(path, directoryPath);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;
        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            ByteArrayOutputStream byteBuffer;

            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";

            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", "Video Compressed", imageFile.getName(), value);
//            Log.e(TAG, "text: " + text);
//            Log.e(TAG, "imageFile.getName() : " + imageFile.getName());
//            Log.e(TAG, "Path 0 : " + compressedFilePath);

            try {
                File file = new File(compressedFilePath);
                InputStream inputStream = null;//You can get an inputStream using any IO API
                inputStream = new FileInputStream(file.getAbsolutePath());
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
                Uri VideoUri = Uri.fromFile(file);
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output64.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                output64.close();
                String ba1 = output.toString();
                Log.e("BA1", ba1);
                // Here video size is reduce and call your method to upload file on server
//                uploadVideoMethod();
                progressDialog.dismiss();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateToSite(long siteId1, String finalCurrentTime, String currentDate, Uri downloadImageUri, String timestamp, String type) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("picUid", firebaseAuth.getUid());
        if (downloadImageUri != null) {
            hashMap.put("picActivity", true);
            hashMap.put("picId", timestamp);
            hashMap.put("picTime", finalCurrentTime);
            hashMap.put("picDate", currentDate);
            hashMap.put("picType", type);
            if (downloadImageUri != null) {
                hashMap.put("picLink", downloadImageUri.toString());
                if (type.equals("Audio")) {
                    hashMap.put("picRemark", "");
                } else {
                    hashMap.put("picRemark", et_remark_dialog.getText().toString());
                }

            }


            if (latitude > 0 && longitude > 0) {
                hashMap.put("picLatitude", latitude);
                hashMap.put("picLongitude", longitude);
            }


        } else {
            hashMap.put("picActivity", true);
            hashMap.put("picId", timestamp);
            hashMap.put("picTime", finalCurrentTime);
            hashMap.put("picDate", currentDate);
            hashMap.put("picLink", "");
            hashMap.put("picMsg", binding.etMessage.getText().toString());
            hashMap.put("picType", type);


        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId1)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        image_uri = null;
                        image_uri1 = null;
                        String currentTime = null;

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
                        }
                        editorLogin.putBoolean("PicActivity", true);
                        editorLogin.putString("LastActivityTime", currentTime);
                        editorLogin.apply();
                        editorLogin.commit();
                        countDownTimer.cancel();
                        if (userType.equals("Supervisor")) {
                            updateToMember(siteId, hashMap);
                        } else {
//                            progressDialog.dismiss();
                            progressDialog.dismiss();
                            Toast.makeText(ChattingActivity.this, getString(R.string.activity_updated), Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
                        Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateToMember(long siteId, HashMap<String, Object> hashMap) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                progressDialog.dismiss();
                binding.etMessage.getText().clear();
                progressDialog.dismiss();

                getAdminUid();

                Toast.makeText(ChattingActivity.this, getString(R.string.activity_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAdminUid() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = snapshot.child("hrUid").getValue(String.class);
                getFirebaseToken(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFirebaseToken(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue(String.class);
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
            jsonobject_notification.put("body", userName + "has sent you a message" + " " + " " + " at" + " " + siteName);


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

//                progressDialog.dismiss();
             progressDialog.show();
                loadChats();


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

    private void requestRecordAudioPermission() {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (this.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {

        } else {

            Toast.makeText(this, "This app needs to record audio through the microphone....", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    requestPermissions(new String[]{requiredPermission, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_AUDIO);
                } catch (RuntimeException e) {
                    e.getMessage();
                    Log.e("Exception", e.getMessage());
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
        } else {
            Log.e("Microphone", "Permission Denied");
        }

    }

    private void forceLogout() {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChattingActivity.this);
        builder.setCancelable(false);
        String finalCurrentTime = currentTime;
        builder.setTitle(R.string.forcelogout)
                .setMessage(R.string.forced_logout_attendance)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    progressDialog.show();
                    firebaseAuth.signOut();
                    editor.clear();
                    editor.commit();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("online", false);
                    hashMap.put("forceLogout", true);
                    hashMap.put("forceLogoutReason", "Work Inactivity");
                    hashMap.put("time", finalCurrentTime);
                    hashMap.put("uid",firebaseAuth.getUid());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    reference.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            String currentDate = "";
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                                currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
                                            }
                                            String timestamp = "" + System.currentTimeMillis();
                                            HashMap<String, Object> hashMap1 = new HashMap<>();
                                            hashMap1.put("uid", firebaseAuth.getUid());
                                            hashMap1.put("online", false);
                                            hashMap1.put("status", "Forced Logout");
                                            hashMap1.put("profile", "");
                                            hashMap1.put("timeStamp", "" + timestamp);
                                            hashMap1.put("time", "" + finalCurrentTime);
                                            hashMap1.put("name", "" + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("fullName", ""));
                                            if (latitude > 0 && longitude > 0) {
                                                hashMap1.put("memberLatitude", 0.0);
                                                hashMap1.put("memberLongitude", 0.0);
                                            }
                                            reference.child(String.valueOf(siteId)).child("Attendance").child(currentDate).child(timestamp).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    HashMap<String, Object> hashMap2 = new HashMap<>();
                                                    hashMap2.put("forceLogout", true);
                                                    reference.child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap2)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    firebaseAuth.signOut();
                                                                    editor.clear();
                                                                    editor.commit();
                                                                    finishAffinity();
                                                                }
                                                            });
                                                }
                                            });

                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (userType.equals("Supervisor")) {
            startActivity(new Intent(ChattingActivity.this, MemberTimelineActivity.class));
            finish();
        } else {
            startActivity(new Intent(ChattingActivity.this, timelineActivity.class));
            finish();
        }
    }
}