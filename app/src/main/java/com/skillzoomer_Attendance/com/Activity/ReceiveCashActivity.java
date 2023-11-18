package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelPaymentData;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityReceiveCashBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReceiveCashActivity extends AppCompatActivity {
    ActivityReceiveCashBinding binding;
    long siteId;
    String currentDate,currentTime;
    long receiveCashId=100;
    long cashInHand;
    private ProgressDialog progressDialog;
    private String userType;
    String timestamp,siteName,companyName;
    private Boolean Reconcile=false;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityReceiveCashBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);
        userType=sharedpreferences.getString("userDesignation","");
        if(userType.equals("Supervisor")){
            uid=sharedpreferences.getString("hrUid","");
        }else{
            uid=sharedpreferences.getString("uid","");
        }
        if(userType.equals("Supervisor")){
            siteId=sharedpreferences.getLong("siteId",0);
            userType=sharedpreferences.getString("userDesignation","");
        }else{
            siteId=intent.getLongExtra("siteId",0);
            siteName=intent.getStringExtra("siteName");
            Log.e("SiteID7860",""+siteName+"::::"+siteId);

        }
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Reconcile=intent.getBooleanExtra("Reconcile",false);
        if(Reconcile){
            binding.etReceivedFund.setText(intent.getStringExtra("Amount"));
            binding.etReceivedFrom.setText(getString(R.string.reconcile));
            binding.etReceivedFund.setEnabled(false);
        }

        binding.etReceivedFund.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().startsWith("-")){
                    binding.etReceivedFrom.setText(getString(R.string.reconcile));
                    binding.etReceivedFrom.setTextColor(getResources().getColor(R.color.red));
                }else {
                    binding.etReceivedFrom.getText().clear();
                }
                if(charSequence.length()>1&& !charSequence.toString().startsWith("-")){
                    if(charSequence.toString().contains("-")){
                        binding.etReceivedFund.setError(getString(R.string.invalid_amount));

                    }else if(charSequence.toString().startsWith("-")&& charSequence.length()>1&&charSequence.toString().substring(1).contains("-")){
                        binding.etReceivedFund.setError(getString(R.string.invalid_amount));

                    }else if(charSequence.toString().startsWith("-")&& charSequence.length()>1&&Integer.parseInt(charSequence.toString().substring(1))>cashInHand){
                        binding.etReceivedFund.setError(getString(R.string.not_accepted));
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        getCashReceiveId();
        getCashInHand();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submitting entry");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.btnSubmitReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.etReceivedFund.getText().toString())){
                    Toast.makeText(ReceiveCashActivity.this, "Enter Amount Received:", Toast.LENGTH_SHORT).show();
                }else  if(TextUtils.isEmpty(binding.etReceivedFund.getText().toString())){
                    Toast.makeText(ReceiveCashActivity.this, "Enter Received From", Toast.LENGTH_SHORT).show();
                }else if(binding.etReceivedFund.getText().toString().startsWith("-")){
                    Log.e("CashInHand",""+cashInHand);
                    if(Integer.parseInt(binding.etReceivedFund.getText().toString().substring(1))>cashInHand){
                        Toast.makeText(ReceiveCashActivity.this, "Not Accepted", Toast.LENGTH_SHORT).show();
                    }else if(!intent.getBooleanExtra("Reconcile",false)){
                        String message="";
                        if(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("Language","hi").equals("en")){
                            message=getString(R.string.reconcile_you_are_trying)+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrDesignation","")+".Do you wish to continue?";
                        }else{
                            message=getString(R.string.reconcile_you_are_trying)+getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrDesignation","")+" से परमिशन लेनी होगी। क्या आप जारी रखना चाहते हैं?";
                        }
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ReceiveCashActivity.this);
                        builder.setTitle(getString(R.string.not_allowed))
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ask_permission, (dialogInterface, i) -> {
                                    getHrUid(siteId,binding.etReceivedFund.getText().toString());

                                })
                                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                                        dialogInterface.dismiss());
                        builder.show();
                    }else if(intent.getBooleanExtra("Reconcile",false)){
                        progressDialog.show();

                        updateToDatabase();
                    }



                }else{
                    progressDialog.show();

                    updateToDatabase();
                }

            }
        });
        binding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReceiveCashActivity.this, AdvancesHomeActivity.class));
            }
        });
    }

    private void getHrUid(long siteID, String amount) {
        Log.e("SiteId",""+siteID);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hrUid = snapshot.child("hrUid").getValue(String.class);
                Log.e("HrUId",hrUid);
                getTokenAdmin(siteID, hrUid,amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTokenAdmin(long siteID, String hrUid, String amount) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(hrUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("token").getValue(String.class);
                sendNotification(siteID,token,amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(long siteID, String token, String amount) {
        Log.d("token : ", token);
        RequestQueue requestQueue = Volley.newRequestQueue(ReceiveCashActivity.this);
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();

            jsonobject_notification.put("title", "Request from Associate");
            jsonobject_notification.put("body", "Associate has Requested to Reconcile"+getString(R.string.rupee_symbol)+amount+" "+".Click to take Action");




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
                updateOnSiteDatabase(siteID,amount);




            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error :  ", String.valueOf(error));
                updateOnSiteDatabase(siteID,amount);


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

    private void updateOnSiteDatabase(long siteID, String amount) {
        Log.e("CHECK",""+Reconcile);
       
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("associateRequest",true);
            hashMap.put("associateRequestType","Reconcile");
            hashMap.put("toPayAmount",amount);
            hashMap.put("associateRequestStatus","Pending");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteID)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        
       
    }

    private void checkForReqPendency(int receiving) {
        final int[] fRec = {receiving};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer request_amt=0;
                if(snapshot.hasChild("reqAmt")){
//                    Log.e("Frec",""+fRec[0]);

                   request_amt=Integer.parseInt(snapshot.child("reqAmt").getValue(String.class));
                    Log.e("Frec","A"+request_amt);
                   if(fRec[0] >=request_amt){
                       Log.e("OnDataChange","Called");
                       updateToSiteDatabase(false,0);
                       fRec[0] =-1;
                   }else if(fRec[0]>0 && fRec[0]<request_amt){
                       updateToSiteDatabase(true,(request_amt-receiving));
                       fRec[0] =-1;
                   }else{
                       updateToSiteDatabase(false,request_amt);
                       fRec[0] =-1;
                   }
                }else{
                    updateToSiteDatabase(false,0);
                    fRec[0] =-1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCashInHand() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("cashInHand")){
                    cashInHand=snapshot.child("cashInHand").getValue(long.class);

                }else{
                   cashInHand=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCashReceiveId() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Receive").child(currentDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.getChildrenCount()>0){
                    receiveCashId= (snapshot.getChildrenCount()+1)*100;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToDatabase() {
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails" , Context.MODE_PRIVATE);
        String recId=""+siteId+receiveCashId;
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("recCashId",recId);
        hashMap.put("recType","CashReceive");
        hashMap.put("amount",binding.etReceivedFund.getText().toString());
        hashMap.put("reqTime",currentTime);
        hashMap.put("reqDate",currentDate);
        hashMap.put("recFrom",binding.etReceivedFrom.getText().toString());
        hashMap.put("entryByUid",sharedpreferences.getString("uid",""));
        hashMap.put("entryByName",sharedpreferences.getString("userName",""));
        hashMap.put("entryByType",userType);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Cash").child("Receive").child(currentDate).child(recId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                checkForReqPendency(Integer.parseInt(binding.etReceivedFund.getText().toString()));

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReceiveCashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void updateToSiteDatabase(boolean b, int i) {
        if(binding.etReceivedFund.getText().toString().startsWith("-")){
            cashInHand=cashInHand-Integer.parseInt(binding.etReceivedFund.getText().toString().substring(1));
        }else{
            cashInHand=cashInHand+Integer.parseInt(binding.etReceivedFund.getText().toString());
        }
        Log.e("cashInHand",""+cashInHand);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("cashInHand",cashInHand);
        if(b){
            hashMap.put("reqPendency",true);
            hashMap.put("reqAmt",String.valueOf(i));
        }else{
            hashMap.put("reqPendency",false);
            hashMap.put("reqAmt",String.valueOf(i));
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                binding.etReceivedFund.getText().clear();
                binding.etReceivedFrom.getText().clear();
                if(Reconcile){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequest").removeValue();
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequestType").removeValue();
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("toPayAmount").removeValue();
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("payableAmount").removeValue();
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("labourIdPaying").removeValue();
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequestStatus").removeValue();
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("associateRequestReason").removeValue();

                }
                if(!b){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("reqStatus").removeValue();
                    reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("reqAmt").removeValue();

                }
                Toast.makeText(ReceiveCashActivity.this, "Receiving of Amount:"+binding.etReceivedFund.getText().toString()+
                        " submitted successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ReceiveCashActivity.this,MemberTimelineActivity.class));
                
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ReceiveCashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}