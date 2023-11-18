package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.Model.ModelUser;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivitySowapBinding;
import com.skillzoomer_Attendance.com.databinding.LayoutToolbarBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivitySowap extends AppCompatActivity {

    ActivitySowapBinding binding;
    LayoutToolbarBinding toolbarBinding;
    String country = "", city = "", sitename = "";
    private long siteId = 0;
    private ProgressDialog progressDialog;
    String userType, userMobile;
    FirebaseAuth firebaseAuth;
    Button btn;
    private final Calendar myCalendar = Calendar.getInstance();
    String startTime = "", endTime = "";
    String currentDate;
    int count = 0;

    String currentAdminUid, selectedAssociateUid, selectedSiteName, selectedAssociateName;
    long selectedSiteId;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editorLogin;
    private SharedPreferences.Editor editorPL;

    private ArrayList<ModelSite> siteArrayList;

    private ArrayList<ModelUser> modelUserArrayList;
    private String name, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySowapBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        modelUserArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        SharedPreferences sharedpreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferencesPL = getSharedPreferences("PermanentLogin", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editorPL = sharedpreferencesPL.edit();
        SharedPreferences spLogin = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        editorLogin = spLogin.edit();
        siteArrayList = new ArrayList<>();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getUserList();


        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    binding.submit.setVisibility(View.VISIBLE);
                    currentAdminUid = firebaseAuth.getUid();
                    selectedAssociateUid = modelUserArrayList.get(i).getUid();
                    selectedSiteId = modelUserArrayList.get(i).getSiteId();
                    selectedSiteName = modelUserArrayList.get(i).getSiteName();
                    selectedAssociateName = modelUserArrayList.get(i).getName();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "";
                if (sharedpreferences.getString("Language", "hi").equals("en")) {
                    message = "Are you sure you want to swap your Role with" + " " + selectedAssociateName + ". " + "After the swap you will work as associate for site "
                            + " " + selectedSiteName + " " + " and all your Admin Rights will be abolished";
                } else {
                    message = getString(R.string.swap_admin1) + " " + selectedAssociateName + " " + getString(R.string.swap_admin2) +
                            " " + selectedSiteName + " " + getString(R.string.swap_admin3);
                }
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ActivitySowap.this);
                builder.setTitle(R.string.warning)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialogInterface, j) -> {
                            progressDialog.show();
                            getUserDetails(selectedAssociateUid);

                        })
                        .setNegativeButton(R.string.no, (dialogInterface, j) ->
                                dialogInterface.dismiss());
                builder.show();
            }
        });


    }

    private void getUserDetails(String selectedAssociateUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(selectedAssociateUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean attendanceManagement = snapshot.child("attendanceManagement").getValue(Boolean.class);
                    Boolean cashManagement = snapshot.child("cashManagement").getValue(Boolean.class);
                    Boolean financeManagement = snapshot.child("financeManagement").getValue(Boolean.class);
                    Boolean forceLogout = snapshot.child("forceLogout").getValue(Boolean.class);
                    Boolean workActivity = snapshot.child("workActivity").getValue(Boolean.class);
                    String memberStatus = snapshot.child("memberStatus").getValue(String.class);
                    String token = snapshot.child("token").getValue(String.class);
                    name = snapshot.child("name").getValue(String.class);
                    mobile = snapshot.child("mobile").getValue(String.class);
                    getSiteList(attendanceManagement, cashManagement, financeManagement, forceLogout, workActivity, memberStatus, token);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ActivitySowap.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getSiteList(Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement, Boolean forceLogout, Boolean workActivity, String memberStatus, String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteArrayList.clear();
                Log.e("Snapshot", "" + snapshot.exists());
                if (snapshot.exists()) {
                    Log.e("snapshot", "" + snapshot.getChildrenCount());
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelSite modelSite = ds.getValue(ModelSite.class);
                        changeHrUidonSite(modelSite.getSiteId());
                    }
                    progressDialog.dismiss();
                    swapAdminToAssociate(attendanceManagement, cashManagement, financeManagement, forceLogout, workActivity, memberStatus, token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void swapAdminToAssociate(Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement, Boolean forceLogout, Boolean workActivity, String memberStatus, String token) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("attendanceManagement", attendanceManagement);
        hashMap.put("cashManagement", cashManagement);
        hashMap.put("financeManagement", financeManagement);
        hashMap.put("forceLogout", forceLogout);
        hashMap.put("workActivity", workActivity);
        hashMap.put("memberStatus", memberStatus);
        hashMap.put("hrUid", selectedAssociateUid);
        hashMap.put("siteId", selectedSiteId);
        hashMap.put("siteName", selectedSiteName);
        hashMap.put("online", false);
        hashMap.put("forceOpt", false);
        hashMap.put("userType", "Supervisor");
        hashMap.put("memberBlock", "Active");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                swapAssociateToAdmin(token);

            }
        });

    }

    private void swapAssociateToAdmin(String token) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("workOpt", 3);
        hashMap.put("userType", "HR Manager");
        hashMap.put("designationPosition",getSharedPreferences("UserDetails",MODE_PRIVATE).getLong("designationPosition",0) );

        hashMap.put("designationName", getSharedPreferences("UserDetails",MODE_PRIVATE).getString("designation",""));
        hashMap.put("designationNameHindi", getSharedPreferences("UserDetails",MODE_PRIVATE).getString("designationHindi",""));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(selectedAssociateUid);
        reference.child("attendanceManagement").removeValue();
        reference.child("cashManagement").removeValue();
        reference.child("financeManagement").removeValue();
        reference.child("forceLogout").removeValue();
        reference.child("workActivity").removeValue();
        reference.child("hrUid").removeValue();
        reference.child("memberStatus").removeValue();
        reference.child("siteId").removeValue();
        reference.child("siteName").removeValue();
        reference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("Updated", "upd");
                addIndustryListToAssociate(token);

            }
        });

    }

    private void addIndustryListToAssociate(String token) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference.child(selectedAssociateUid).child("Industry").setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        reference.child(firebaseAuth.getUid()).child("Industry").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sendNotificationToAssociate(token);
                            }
                        });

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void changeHrUidonSite(long siteId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("hrUid", selectedAssociateUid);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("Success", "" + siteId);
            }
        });
    }

    private void sendNotificationToAssociate(String token) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();

            jsonobject_notification.put("title", "Your Power is Changed");
            jsonobject_notification.put("body", "Your are now an Admin. Login to see your Sites and other details");


            JSONObject jsonobject_data = new JSONObject();
            jsonobject_data.put("imgurl", "https://firebasestorage.googleapis.com/v0/b/haajiri-register.appspot.com/o/FCMImages%2FpowerChange.jpg?alt=media&token=3f07ee64-281f-4fa0-8706-2b30497db23c");

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
                progressDialog.dismiss();
                firebaseAuth.signOut();
                editorLogin.clear();

                editor.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivitySowap.this.finishAffinity();
                }


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

    private void getUserList() {
        count = 0;
        Log.e("UserModel", "" + firebaseAuth.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelUserArrayList.clear();


                Log.e("UserModel", "" + snapshot.exists());

                if (snapshot.exists()) {
                    String uid = "", name = "", siteName = "";

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        uid = ds.child("uid").getValue(String.class);
                        name = ds.child("name").getValue(String.class);
                        Log.e("SiteId", "" + uid);
                        Log.e("SiteId", "" + name);
                        siteId = ds.child("siteId").getValue(long.class);
                        Log.e("SiteId", "" + siteId);
                        siteName = "";
                        if (ds.child("siteName").getValue(String.class) != null) {
                            siteName = ds.child("siteName").getValue(String.class);
                        }
                        ModelUser modelUser = new ModelUser(uid, name, siteName, siteId);
                        modelUserArrayList.add(modelUser);

                    }
                    modelUserArrayList.add(0, new ModelUser(null, "Select Associate", null, 0));

                    Log.e("UserModel", "" + modelUserArrayList.size());


                    MemberSpinnerAdapter siteSpinnerAdapter = new MemberSpinnerAdapter();
                    binding.spinner.setAdapter(siteSpinnerAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    class MemberSpinnerAdapter
            extends BaseAdapter {
        MemberSpinnerAdapter() {
        }

        public int getCount() {
            return modelUserArrayList.size();
        }

        public Object getItem(int n) {
            return null;
        }

        public long getItemId(int n) {
            return 0L;
        }

        public View getView(int n, View view, ViewGroup viewGroup) {
            @SuppressLint("ViewHolder") View view2 = getLayoutInflater().inflate(R.layout.layout_swap_admin, null);
            TextView textView = (TextView) view2.findViewById(R.id.txt_member_name);
            TextView textView1 = (TextView) view2.findViewById(R.id.txt_site_id);
            TextView textView2 = (TextView) view2.findViewById(R.id.txt_siteName);
            View view_1 = (View) view2.findViewById(R.id.view1);
            View view_2 = (View) view2.findViewById(R.id.view2);
            View view_3 = (View) view2.findViewById(R.id.view3);
            View view_4 = (View) view2.findViewById(R.id.view4);

            if (n == 0) {
                textView.setText(R.string.select_associate);
                textView1.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                view_1.setVisibility(View.GONE);
                view_2.setVisibility(View.GONE);
                view_3.setVisibility(View.GONE);
                view_4.setVisibility(View.GONE);
            } else {
                textView.setText(modelUserArrayList.get(n).getName());
                textView1.setText(String.valueOf(modelUserArrayList.get(n).getSiteId()));
                textView2.setText(modelUserArrayList.get(n).getSiteName());
                textView1.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                view_1.setVisibility(View.VISIBLE);
                view_2.setVisibility(View.VISIBLE);
                view_3.setVisibility(View.VISIBLE);
                view_4.setVisibility(View.VISIBLE);
            }


            return view2;
        }
    }
}