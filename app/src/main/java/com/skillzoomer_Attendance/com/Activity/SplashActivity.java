package com.skillzoomer_Attendance.com.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Utilities.LocaleHelper;
import com.skillzoomer_Attendance.com.Utilities.MyApplication;
import com.skillzoomer_Attendance.com.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private ImageView txt_nirman_skills;
    String userType;
    long siteId;
    private SharedPreferences.Editor editor;

//    String[] language={getString(R.string.select_app_language),"Hindi","English"};

    private String selectedLanguage;

    private SharedPreferences.Editor editorL;
    Context context;

    LinearLayout ll_language;
    RadioButton rb_english,rb_hindi;
    Button btn_change_language;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
       txt_nirman_skills=findViewById(R.id.txt_nirman_skills);
        firebaseAuth = FirebaseAuth.getInstance();
        txt_nirman_skills=findViewById(R.id.txt_nirman_skills);
        rb_english=findViewById(R.id.rb_english);
        rb_hindi=findViewById(R.id.rb_hindi);
        ll_language=findViewById(R.id.ll_language);
        btn_change_language=findViewById(R.id.btn_change_language);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferencesLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        SharedPreferences sharedpreferences1 = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String paymentDate=sharedpreferences1.getString("PaymentLastUploadedDate","");
        Log.e("Paymentdate",paymentDate);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate1 = dateFormat.format(c);
        MyApplication my = new MyApplication( );

        ll_language.setVisibility(View.GONE);
        txt_nirman_skills.setVisibility(View.VISIBLE);

        context = LocaleHelper.setLocale(SplashActivity.this, "hi");
        my.updateLanguage(SplashActivity.this, sharedpreferences.getString("Language","hi"));

       rb_english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b){
                   rb_hindi.setChecked(false);
                   rb_english.setBackgroundColor(getResources().getColor(R.color.gray_more_light));
                   rb_hindi.setBackgroundColor(getResources().getColor(R.color.white));
                   btn_change_language.setText("CONTINUE");
                   btn_change_language.setVisibility(View.VISIBLE);
                   selectedLanguage="English";

               }
           }
       });

       rb_hindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b){
                   rb_english.setChecked(false);
                   rb_english.setBackgroundColor(getResources().getColor(R.color.white));
                   rb_hindi.setBackgroundColor(getResources().getColor(R.color.gray_more_light));
                   btn_change_language.setText("ज़ारी रखें");
                   btn_change_language.setVisibility(View.VISIBLE);

                   selectedLanguage="Hindi";
               }

           }
       });

//        SpinnerAdapter spinnerAdapter=new SpinnerAdapter();


        my.updateLanguage(this, sharedpreferences.getString("Language","hi"));
//        String currentDate1 = "02/11/2022";
        try {
            Date fDate = dateFormat.parse(paymentDate);
            Date cDate=dateFormat.parse(currentDate1);
            if(cDate.after(fDate)){
                Log.e("date11111","After");
                deleteCart();
            }else{
                Log.e("date11111","Before");
            }
        } catch (ParseException e) {
            Log.e("Exception",e.getMessage());
            e.printStackTrace();
        }

        editor=sharedpreferences.edit();


        userType=sharedpreferences.getString("userDesignation","");
        siteId=sharedpreferences.getLong("siteId",0);


        String currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        Log.e("currentDate",currentDate);
        Log.e("currentTime",currentTime);


        btn_change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedLanguage.equals("English")){
                    context = LocaleHelper.setLocale(SplashActivity.this, "en");
                    my.updateLanguage(SplashActivity.this, sharedpreferences.getString("Language","en"));
                    editor.putString("Language","en");
                    editor.apply();
                    editor.commit();
                }else{
                    context = LocaleHelper.setLocale(SplashActivity.this, "hi");
                    my.updateLanguage(SplashActivity.this, sharedpreferences.getString("Language","hi"));
                    editor.putString("Language","hi");
                    editor.apply();
                    editor.commit();
                }
                Intent intent = new Intent(SplashActivity.this, splashgif.class);
////                            mp.release();
                startActivity(intent);
                finish();

            }
        });



        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    Boolean resul = firebaseAuth.getCurrentUser() == null;

                    Log.d("CurrentUser", "" + resul);
//                    if (firebaseAuth.getCurrentUser() == null) {
//                        Intent intent = new Intent(SplashActivity.this, splashgif.class);
////                            mp.release();
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        if(userType.equals("Supervisor")){
//                            getAttendanceInfo();
//
//
//                        }else {
//                            Intent intent = new Intent(SplashActivity.this , splashgif.class);
////                            mp.release();
//                            startActivity(intent);
//                            finish();
//                        }
//                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(sharedpreferencesLogin.getBoolean("LoginFirstTime",true)){
                                txt_nirman_skills.setVisibility(View.GONE);
                                ll_language.setVisibility(View.VISIBLE);

                            }else{
                                Intent intent = new Intent(SplashActivity.this, splashgif.class);
////                            mp.release();
                                startActivity(intent);
                                finish();

                            }
                        }
                    });




                }
            }
        };

        timer.start();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this, R.color.white));

        }

    }

    private void getAttendanceInfo() {
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH).format(new Date());
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("UserDetails");
        reference.child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrUid","")).child("Industry")
                        .child("Construction")
                                .child("Site").child(String.valueOf(siteId)).child("Attendance").child(currentDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Intent intent = new Intent(SplashActivity.this, splashgif.class);
//                            mp.release();
                            startActivity(intent);
                            finish();
                        }else{
                            Log.e("Splash",""+getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrUid",""));
                            HashMap<String,Object> hashMap =new HashMap<>();

                            hashMap.put("online",false);
                            hashMap.put("profile","");
                            hashMap.put("latitude","");
                            hashMap.put("longitude","");
                            hashMap.put("timeStamp","");
                            hashMap.put("time","");
                            DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
                            reference1.child(getSharedPreferences("UserDetails", MODE_PRIVATE).getString("hrUid","")).child("Industry")
                                    .child("Construction")
                                    .child("Site").child(String.valueOf(siteId)).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            firebaseAuth.signOut();
                                            editor.clear();
                                            editor.commit();

                                            startActivity(new Intent(SplashActivity.this,splashgif.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





    }

    private void deleteCart() {
        EasyDB easyDB=EasyDB.init(this,"ATTENDANCE_Db")
                .setTableName("Attendance")
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .doneTableColumn();
        if(easyDB.getAllData().getCount()>0) {
            easyDB.deleteAllDataFromTable();
            deletePaymentCart();
        }
    }

    private void deletePaymentCart() {
        EasyDB easyDB=EasyDB.init(this,"Payment_Db")
                .setTableName("Payment")
              .addColumn(new Column("Timestamp", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourId", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourName", new String[]{"text", "not null"}))
                .addColumn(new Column("LabourType", new String[]{"text", "not null"}))
                .addColumn(new Column("Date", new String[]{"text", "not null"}))
                .addColumn(new Column("Time", new String[]{"text", "not null"}))
                .addColumn(new Column("Advance", new String[]{"text", "not null"}))
                .addColumn(new Column("Reason", new String[]{"text", "not null"}))
                .doneTableColumn();
        if(easyDB.getAllData().getCount()>0) {
            easyDB.deleteAllDataFromTable();
        }

    }

//    class SpinnerAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return language.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inf = getLayoutInflater();
//            View row = inf.inflate(R.layout.spinner_child, null);
//
//            TextView designationText = row.findViewById(R.id.txt_designation);
//            designationText.setText(language[position]);
//
//            return row;
//        }
//    }
}