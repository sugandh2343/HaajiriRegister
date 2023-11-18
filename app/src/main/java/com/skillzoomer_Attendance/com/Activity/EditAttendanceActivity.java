package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterEditLabour;
import com.skillzoomer_Attendance.com.Model.ModelEditLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityEditAttendanceBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditAttendanceActivity extends AppCompatActivity {
    ActivityEditAttendanceBinding binding;
    private String[] designation={"P","P/2","A"};
    private long siteId;
    private ArrayList<ModelEditLabour> editLabourArrayList;
    private String type;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditAttendanceBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        editLabourArrayList=new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.please_wait));
        siteId=intent.getLongExtra("siteId",0);
        type=intent.getStringExtra("type");
        Date c = Calendar.getInstance().getTime();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        SimpleDateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = df.format(c);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("DatabaseChange"));
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate).orderByChild("labourType").equalTo(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editLabourArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    String labourId=ds.child("labourId").getValue(String.class);
                    String labourName=ds.child("labourName").getValue(String.class);
                    ModelEditLabour editLabour=new ModelEditLabour(labourId,labourName,"P");
                    editLabourArrayList.add(editLabour);
                }
//                Log.e("Edit",""+editLabourArrayList.size());
                checkForAbsentLabour(editLabourArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String finalCurrentDate = currentDate;
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setProgress(0);
                progressDialog.show();
//                Log.e("Size",""+editLabourArrayList.size());
                int count=0;
                for (int i=0;i<editLabourArrayList.size();i++){
                    count++;
                    checkForPresent(editLabourArrayList.get(i), finalCurrentDate,count);
                }

                updateOnSiteMaster(finalCurrentDate);

            }
        });

    }

    private void updateOnSiteMaster(String finalCurrentDate) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(finalCurrentDate).orderByChild("labourType").equalTo(type)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        updateonSiteDatabase(snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//         long count=0;
//        for (int i=0;i<editLabourArrayList.size();i++){
//            if(editLabourArrayList.get(i).getStatus().equals("P")||editLabourArrayList.get(i).getStatus().equals("P/2")){
//                count++;
//            }
//        }
//        HashMap<String,Object> hashMap=new HashMap<>();
//        if (type.equals("Skilled")){
//            hashMap.put("skilledSeen",true);
//            hashMap.put("skilled",count);
//        }else{
//            hashMap.put("unSkilledSeen",true);
//            hashMap.put("unSkilled",count);
//        }
//        DatabaseReference refrence=FirebaseDatabase.getInstance().getReference("Site");
//        refrence.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        progressDialog.dismiss();
//                        startActivity(new Intent(EditAttendanceActivity.this,timelineActivity.class));
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
    }

    private void updateonSiteDatabase(long childrenCount) {
        String currentTime1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        HashMap<String,Object> hashMap=new HashMap<>();
        if (type.equals("Skilled")){
            hashMap.put("skilledSeen",true);
            hashMap.put("skilled",childrenCount);
            hashMap.put("SkilledTime", currentTime1);
        }else{
            hashMap.put("unSkilledSeen",true);
            hashMap.put("unskilled",childrenCount);
            hashMap.put("UnskilledTime", currentTime1);
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        startActivity(new Intent(EditAttendanceActivity.this, timelineActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void checkForPresent(ModelEditLabour modelEditLabour, String finalCurrentDate, int count) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(finalCurrentDate).orderByChild("labourId").equalTo(modelEditLabour.getLabourId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){

                            if(modelEditLabour.getStatus().equals("A")){
//                                Log.e("RemoveValue",modelEditLabour.getLabourId());
//                                Log.e("Present","A");
                                removeLabourFromAttendance(modelEditLabour.getLabourId(),finalCurrentDate,count);
                            }else if(modelEditLabour.getStatus().equals("P/2")){
                                Log.e("Present","P/2");
                                updateOnLabour(modelEditLabour.getLabourId(),finalCurrentDate,"-P/2",count,modelEditLabour.getLabourName());
                            }

                        }else{
                            if(modelEditLabour.getStatus().equals("P")){
//                                Log.e("Present","A::::"+"P");
                                addLabourAttendance(modelEditLabour.getLabourId(),finalCurrentDate,count,modelEditLabour.getLabourName());
//                                removeLabourFromAttendance(modelEditLabour.getLabourId(),finalCurrentDate, count);
                            }else if(modelEditLabour.getStatus().equals("P/2")){
//                                Log.e("Present","A::::"+"P");
                                updateOnLabour(modelEditLabour.getLabourId(),finalCurrentDate,"+P/2", count,modelEditLabour.getLabourName());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void addLabourAttendance(String labourId, String finalCurrentDate, int count, String labourName) {
        HashMap<String, Object> hashMap = new HashMap<>();

            String currentTime1 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
            }
            hashMap.put("status", "P");
            hashMap.put("editTime", currentTime1);
            hashMap.put("editDate", finalCurrentDate);
            hashMap.put("labourType", type);
            hashMap.put("labourId", labourId);
            hashMap.put("labourName", labourName);
            hashMap.put("editedByUid", firebaseAuth.getUid());
            hashMap.put("editedByName",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
            hashMap.put("editedByType","HR Manager");


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(finalCurrentDate).child(labourId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getLabourWages(labourId,"P",finalCurrentDate, count);
                    }
                });


    }

    private void updateOnLabour(String labourId, String finalCurrentDate, String s, int count, String labourName) {
        Log.e("Present","UpdateLAbour");
        String currentTime1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
        }
        HashMap<String,Object> hashMap=new HashMap<>();
        if(!s.equals("P")) {
            hashMap.put("status", "P/2");
            hashMap.put("editTime", currentTime1);
            hashMap.put("editDate", finalCurrentDate);
            hashMap.put("labourType", type);
            hashMap.put("labourId", labourId);
            hashMap.put("labourName", labourName);
            hashMap.put("editedByUid", firebaseAuth.getUid());
            hashMap.put("editedByName",getSharedPreferences("UserDetails",MODE_PRIVATE).getString("fullName",""));
            hashMap.put("editedByType","HR Manager");
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(finalCurrentDate).child(labourId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("Present","UpdateLAbour");
                        getLabourWages(labourId,s,finalCurrentDate,count);
                    }
                });

    }

    private void removeLabourFromAttendance(String labourId, String finalCurrentDate, int count) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Attendance").child("Labours").child(finalCurrentDate).child(labourId)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      getLabourWages(labourId,"Remove",finalCurrentDate, count);
                    }
                });

    }

    private void getLabourWages(String labourId, String remove, String finalCurrentDate, int count) {
//        Log.e("LabourId123",labourId);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> hashMap=new HashMap<>();
                ModelLabour modelLabour=snapshot.getValue(ModelLabour.class);
                long wages =modelLabour.getWages();
                long payableAmount=snapshot.child("payableAmt").getValue(long.class);
                Log.e("Wages","Wages"+wages+"PA"+payableAmount);
                Log.e("Wages","Rem"+remove);
                if(remove.equals("Remove")){
                    payableAmount=payableAmount-wages;
                    hashMap.put("payableAmt",payableAmount);


                }else if(remove.equals("-P/2")){

                    payableAmount=payableAmount-(wages/2);
//                    Log.e("Wages","UPA"+payableAmount);
                    hashMap.put("payableAmt",payableAmount);
                }else if(remove.equals("+P/2")){
                    payableAmount=payableAmount+(wages/2);
                    hashMap.put("payableAmt",payableAmount);
                }else if(remove.equals("P")){
                    payableAmount=payableAmount+(wages);
                    hashMap.put("payableAmt",payableAmount);
                }
                reference.child(String.valueOf(siteId)).child("Labours").child(labourId).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child("Labours").child(labourId).child("Attendance").child(finalCurrentDate).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if(remove.equals("remove")){
                                           updateLabourAttendance(labourId,finalCurrentDate,"Remove",count);


                                        }else if(remove.equals("-P/2")){
                                            updateLabourAttendance(labourId,finalCurrentDate,"P/2",count);

                                        }else if(remove.equals("+P/2")){
                                            updateLabourAttendance(labourId,finalCurrentDate,"P/2",count);

                                        }else if(remove.equals("P")){
                                            updateLabourAttendance(labourId,finalCurrentDate,"P",count);

                                        }

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

    private void updateLabourAttendance(String labourId, String finalCurrentDate, String remove, int count) {
        Log.e("RemoveValue111111",remove);
        if(!remove.equals("Remove")){
            String currentTime1 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                currentTime1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date());
            }
            HashMap<String,Object> hashMap=new HashMap<>();

            hashMap.put("status", remove);
            hashMap.put("editTime", currentTime1);
            hashMap.put("editDate", finalCurrentDate);

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).child("Attendance").child(finalCurrentDate).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("Count",""+count);

                        }
                    });

        }else{
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).child("Attendance").child(finalCurrentDate).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Log.e("Removed",labourId);
                    updateOnSiteMaster(finalCurrentDate);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Failed","Removed"+labourId);
                }
            });
        }



    }

    private void reduceSkUskCount(long siteId, int i, String status) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               long update=0;
               if(type.equals("Skilled")){
                   update=snapshot.child("skilled").getValue(long.class);
               }else{
                   update=snapshot.child("unskilled").getValue(long.class);
               }
               if(status.equals("Reduce")){
                   updateToDatabase(update-1);
               }else{
                   updateToDatabase(update+1);
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private void updateToDatabase(long l) {
        HashMap<String,Object> hashMap=new HashMap<>();
        if(type.equals("skilled")){
            hashMap.put("skilled",l);
        }else{
            hashMap.put("unskilled",l);
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).updateChildren(hashMap);
    }

    private void checkForAbsentLabour(ArrayList<ModelEditLabour> editLabourArrayList) {
//        Log.e("Edit","1::::"+editLabourArrayList.size());
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").orderByChild("type").equalTo(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    int present=1;
                    String labourID=ds.child("labourId").getValue(String.class);
                    String labourName=ds.child("name").getValue(String.class);
                    for(int i=0;i<editLabourArrayList.size();i++){
                        if(labourID.equals(editLabourArrayList.get(i).getLabourId())){
                            present=0;
//                            Log.e("LabourId","P"+labourID);

                        }
                    }
                    if(present==1){
//                        Log.e("LabourId","A"+labourID);
                        ModelEditLabour editLabour=new ModelEditLabour(labourID,labourName,"A");
                        editLabourArrayList.add(editLabour);

                    }
                }
//                Log.e("Edit",""+editLabourArrayList.size());
                AdapterEditLabour adapterEditLabour=new AdapterEditLabour(EditAttendanceActivity.this,editLabourArrayList);
                binding.rvEditAttendance.setAdapter(adapterEditLabour);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return designation.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inf=getLayoutInflater();
            View row=inf.inflate(R.layout.spinner_child,null);

            TextView designationText = row.findViewById(R.id.txt_designation);
            designationText.setText(designation[position]);

            return row;
        }
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int adapter_position=intent.getIntExtra("Position",0);
            String status=intent.getStringExtra("status");
            editLabourArrayList.get(adapter_position).setStatus(status);
//            Log.e("AfterChange",editLabourArrayList.get(adapter_position).getStatus());
//            Log.e("AfterChange",""+adapter_position);




        }
    };
}