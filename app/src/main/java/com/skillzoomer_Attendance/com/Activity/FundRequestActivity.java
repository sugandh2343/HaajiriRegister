package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityFundRequestBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FundRequestActivity extends AppCompatActivity {
    ActivityFundRequestBinding binding;
    long siteId;
    String currentDate,currentTime;
    long requestId=100;
    FirebaseAuth firebaseAuth;
    String userName;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFundRequestBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);
        siteId=sharedpreferences.getLong("siteId",0);
        userName=sharedpreferences.getString("userName","");
        uid=sharedpreferences.getString("hrUid","");
        firebaseAuth=FirebaseAuth.getInstance();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        getRequestId();
        binding.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etRequiredFund.getText().toString())){
                    Toast.makeText(FundRequestActivity.this, "Request Amount cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(FundRequestActivity.this);
                    builder.setTitle(R.string.exit)
                            .setMessage(getString(R.string.you_have_entered_rs)+binding.etRequiredFund.getText().toString()+getString(R.string.proceed_with_request))
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                updateToFirebase();
                                Toast.makeText(FundRequestActivity.this, R.string.request_made_successfully, Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });
        binding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FundRequestActivity.this, AdvancesHomeActivity.class));
            }
        });
    }

    private void getRequestId() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Request").child("FundRequest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.getChildrenCount()>0){
                    requestId= (snapshot.getChildrenCount()+1)*100;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToFirebase() {
        String reqId=""+siteId+requestId;
        Log.e("reqId",reqId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("cashReq",true);
        hashMap.put("cashAmt",binding.etRequiredFund.getText().toString());
        hashMap.put("cashReqID",reqId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateTositeMasterDatabase(hashMap);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FundRequestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateTositeMasterDatabase(HashMap<String, Object> hashMap1) {
        String reqId=""+siteId+requestId;
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("reqId",""+reqId);
        hashMap.put("reqType","FundRequest");
        hashMap.put("amount",binding.etRequiredFund.getText().toString());
       hashMap.put("reqTime",currentTime);
       hashMap.put("reqDate",currentDate);
       hashMap.put("reqStatus","Pending");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Request").child("FundRequest").child(reqId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateToMember(hashMap1,reqId);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void updateToMember(HashMap<String, Object> hashMap, String reqId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Members").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FundRequestActivity.this);
                builder.setTitle(R.string.success)
                        .setMessage(getString(R.string.your_request_is_successfully_made)+reqId)
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialogInterface, i) -> {

                            dialogInterface.dismiss();

                            startActivity(new Intent(FundRequestActivity.this,MemberTimelineActivity.class));
                        });

                builder.show();
            }
        });

    }
    private void getAdminUid() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid=snapshot.child("hrUid").getValue(String.class);
                getFirebaseToken(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFirebaseToken(String uid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("token").getValue(String.class);
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

            jsonobject_notification.put("title", "Associate has uploaded Attendance");
            jsonobject_notification.put("body",  userName +" "+ "has made a Fund Request");




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


                Log.e("response", String.valueOf(response));
                Intent intent=new Intent(FundRequestActivity.this,MemberTimelineActivity.class);

                startActivity(intent);



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
}