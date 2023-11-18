package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterTransferLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityTransferLabourBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TransferLabourActivity extends AppCompatActivity {
    ActivityTransferLabourBinding binding;
    FirebaseAuth firebaseAuth;
    String siteId,siteName;

    private String siteNameSelected,siteIdSelected;
    private ArrayList<ModelSite> siteAdminList;
    private ArrayList<ModelSite> fromSiteList;
    private ArrayList<ModelSite> toSiteList;

    private ArrayList<ModelLabour> labourArrayList;
    private ArrayList<ModelLabour> labourSearchArrayList;
    private ArrayList<ModelLabour> labourAtthisSite;
    ProgressDialog progressDialog;

    long fromsite,tosite;

    String currentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTransferLabourBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setCancelable(false);
        fromSiteList=new ArrayList<>();
        toSiteList=new ArrayList<>();

        firebaseAuth=FirebaseAuth.getInstance();
        siteAdminList=new ArrayList<>();
        labourArrayList=new ArrayList<>();
        labourSearchArrayList=new ArrayList<>();
        labourAtthisSite=new ArrayList<>();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            currentDate = df.format(c);
        }

        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent=getIntent();
        siteId=intent.getStringExtra("siteId");
        siteName=intent.getStringExtra("siteName");

        toSiteList=new ArrayList<>();

        Log.e("TRansfer",""+siteId);
        getSiteListAdministrator();



        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    toSiteList.clear();
                    siteId=String.valueOf(siteAdminList.get(i).getSiteId());
                    siteIdSelected=String.valueOf(siteAdminList.get(i).getSiteId());
                    fromsite=siteAdminList.get(i).getSiteId();

                    getLabourList(String.valueOf(siteAdminList.get(i).getSiteId()));

                    for(int j=0;j<siteAdminList.size();j++){
                        if(fromsite!=siteAdminList.get(j).getSiteId()){
                            toSiteList.add(siteAdminList.get(j));
                        }
                    }

                        Log.e("ToSiteList",""+toSiteList.size());
                        ToSpinnerAdapter toSpinnerAdapter=new ToSpinnerAdapter();
                        binding.spinnerTo.setAdapter(toSpinnerAdapter);



                }else{
                    binding.llLabour.setVisibility(View.GONE);
                    if(binding.spinnerTo.getSelectedItemPosition()==0){
                        SiteSpinnerAdapter siteSpinnerAdapter=new SiteSpinnerAdapter();
                        binding.spinnerTo.setAdapter(siteSpinnerAdapter);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    fromSiteList.clear();
                    siteIdSelected=String.valueOf(siteAdminList.get(i).getSiteId());
                    tosite=toSiteList.get(i).getSiteId();

//                    getLabourList(String.valueOf(siteAdminList.get(i).getSiteId()));

                    if(binding.spinnerSelectSite.getSelectedItemPosition()==0){
                        for(int j=0;j<siteAdminList.size();j++) {
                            if (tosite != siteAdminList.get(j).getSiteId()) {
                                fromSiteList.add(siteAdminList.get(j));
                            }
                        }

                        FromSpinnerAdapter fromSpinnerAdapter=new FromSpinnerAdapter();
                        binding.spinnerSelectSite.setAdapter(fromSpinnerAdapter);
                    }





                }else{

                    if(binding.spinnerSelectSite.getSelectedItemPosition()==0){
                        SiteSpinnerAdapter siteSpinnerAdapter=new SiteSpinnerAdapter();
                        binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              if(b){
                  for(int i=0;i<labourArrayList.size();i++){
                      labourArrayList.get(i).setPresent(true);

                  }
                  AdapterTransferLabour adapterTransferLabour=new AdapterTransferLabour(TransferLabourActivity.this,labourArrayList);
                  binding.rvLabour.setAdapter(adapterTransferLabour);
              }  else{
                  for(int i=0;i<labourArrayList.size();i++){
                      labourArrayList.get(i).setPresent(false);

                  }
                  AdapterTransferLabour adapterTransferLabour=new AdapterTransferLabour(TransferLabourActivity.this,labourArrayList);
                  binding.rvLabour.setAdapter(adapterTransferLabour);

              }
            }
        });
        binding.etSearchLabour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    labourSearchArrayList.clear();
                    for(int j=0;j<labourArrayList.size();j++){
                        String name=charSequence.toString();
                        if ((labourArrayList.get(j).getName().toLowerCase(Locale.ROOT)).contains((name.toLowerCase(Locale.ROOT))) ||
                                (labourArrayList.get(j).getLabourId().toLowerCase(Locale.ROOT)).contains(name.toLowerCase(Locale.ROOT))) {
                            ModelLabour labourSeach=labourArrayList.get(j);
                            labourSeach.setOriginalPosition(j);


                            labourSearchArrayList.add(labourArrayList.get(j));

                        }
                    }
                    Log.e("Transfer","Search"+labourSearchArrayList.size());
                    AdapterTransferLabour adapterTransferLabour=new AdapterTransferLabour(TransferLabourActivity.this,labourSearchArrayList);
                    binding.rvLabour.setAdapter(adapterTransferLabour);
                }else{

//                    getLabourList(String.valueOf(siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId()));
                    AdapterTransferLabour adapterTransferLabour=new AdapterTransferLabour(TransferLabourActivity.this,labourArrayList);
                    binding.rvLabour.setAdapter(adapterTransferLabour);
                    labourSearchArrayList.clear();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("site_position"));
        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int countPresent=0;
                for(int m=0;m<labourArrayList.size();m++){
                    if(labourArrayList.get(m).getPresent()){
                        countPresent++;
                    }
                }
                if(countPresent>0){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(TransferLabourActivity.this);
                    builder.setTitle(getString(R.string.data_transfer))
                            .setMessage(R.string.do_you_wish_to_transfer)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                progressDialog.show();
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                                        .child(firebaseAuth.getUid())
                                        .child("Industry").child("Construction").child("Site")
                                        .child(String.valueOf(tosite)).child("Labours");
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long countChildren=snapshot.getChildrenCount();
                                        if(countChildren>0) {
                                            labourAtthisSite.clear();
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                ModelLabour modelLabour = ds.getValue(ModelLabour.class);
                                                labourAtthisSite.add(modelLabour);

                                            }
                                        }
                                            int labourCount=labourAtthisSite.size();

                                            for(int j=0;j<labourArrayList.size();j++){
                                                Boolean allow=true;
                                                if(labourArrayList.get(j).getPresent()){
                                                    if(labourAtthisSite.size()>0){
                                                        for(int m=0;m<labourAtthisSite.size();m++){
                                                            if((labourArrayList.get(j).getUniqueId()!=null||labourArrayList.get(j).getUniqueId()!="")&&
                                                                    ((labourArrayList.get(j).getUniqueId().equals(labourAtthisSite.get(m).getUniqueId())
                                                                    &&labourArrayList.get(j).getName().toLowerCase(Locale.ROOT).equals(labourAtthisSite.get(m).getName().toLowerCase())))){
                                                                Toast.makeText(TransferLabourActivity.this, "Labour Already Present", Toast.LENGTH_SHORT).show();
                                                                allow=false;
                                                            }
                                                        }
                                                    }
                                                    if(allow){
                                                        labourCount++;
                                                        String labourId = ""+tosite + labourCount;
                                                        Log.e("LabourId",labourId);
                                                        String siteId1=""+siteId;

                                                        String timestamp=""+System.currentTimeMillis();
                                                        HashMap<String,Object> hashMap=new HashMap<>();
                                                        hashMap.put("labourId", labourId);
                                                        hashMap.put("name",labourArrayList.get(j).getName());
                                                        hashMap.put("fatherName",labourArrayList.get(j).getFatherName());
                                                        hashMap.put("siteName",siteName);
                                                        hashMap.put("siteCode",Integer.parseInt(siteId));
                                                        hashMap.put("wages",labourArrayList.get(j).getWages());
                                                        hashMap.put("uniqueId",labourArrayList.get(j).getUniqueId());
                                                        hashMap.put("timestamp",timestamp);
                                                        hashMap.put("type",labourArrayList.get(j).getType());
                                                        hashMap.put("profile",labourArrayList.get(j).getProfile());
                                                        hashMap.put("dateOfRegister",currentDate);
                                                        hashMap.put("status", "Registered");
                                                        hashMap.put("payableAmt", 0);


                                                        Log.e("LabourHVfg",""+tosite);
                                                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                                                        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site")
                                                                .child(String.valueOf(tosite)).child("Labours").child(labourId).setValue(hashMap)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {



                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(TransferLabourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });

                                                    }




                                                }
                                            }
                                            progressDialog.dismiss();
                                        }



                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                                Log.e("LabourArrayList",""+labourArrayList.size());


//
                            }).setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
                    builder.show();
                }else{
                    Toast.makeText(TransferLabourActivity.this, "You have not selected any labour to transfer data.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            ArrayList<ModelSite> siteArrayList1=new ArrayList<>();
//            siteAdminList=intent.getParcelableArrayListExtra("array");
            int position = intent.getIntExtra("position", 0);
            Boolean value = intent.getBooleanExtra("boolean", true);

            labourArrayList.get(position).setPresent(value);
            AdapterTransferLabour adapterTransferLabour=new AdapterTransferLabour(TransferLabourActivity.this,labourArrayList);
            binding.rvLabour.setAdapter(adapterTransferLabour);
            binding.etSearchLabour.getText().clear();




//            if (position == 0 && value) {
//                for (int i = 0; i < siteAdminList.size(); i++) {
//                    siteAdminList.get(i).setSelected(true);
//                }
//                Log.e("siteAL", "" + siteAdminList.size());
//
//
//            }else if(position ==0 && !value){
//                for(int i=0;i<siteAdminList.size();i++){
//                    siteAdminList.get(i).setSelected(false);
//                }
//            }else if(position>0 && value){
//               Log.e("PositionSite",""+position);
//               Log.e("PositionSite",""+value);
//                for(int i=0;i<siteAdminList.size();i++){
//                    if(i==position){
//                        siteAdminList.get(position).setSelected(true);
//                    }else if(!siteAdminList.get(i).getSelected()){
//                        siteAdminList.get(i).setSelected(false);
//                    }
//
//                }
//            }else if(position>0 && !value){
//
//                siteAdminList.get(1).setSelected(false);
//                siteAdminList.get(position).setSelected(false);
////
//            }


        }


    };

    private void getLabourList(String valueOf) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site")
                .child(String.valueOf(valueOf)).child("Labours");
        reference.orderByChild("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labourArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
                    if(modelLabour.getProfile()!=null && !modelLabour.getProfile().equals("")){
                        modelLabour.setPresent(false);
                        labourArrayList.add(modelLabour);
                    }


                }
                if(labourArrayList.size()>0){
                    binding.llLabour.setVisibility(View.VISIBLE);
                    AdapterTransferLabour adapterTransferLabour=new AdapterTransferLabour(TransferLabourActivity.this,labourArrayList);
                    binding.rvLabour.setAdapter(adapterTransferLabour);
                }else{
                    binding.llLabour.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSiteListAdministrator() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Industry").child("Construction").child("Site").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siteAdminList.clear();
                fromSiteList.clear();
                toSiteList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSite modelSite = ds.getValue(ModelSite.class);
//                    if (modelSite.getSiteStatus().equals("Active")&& !String.valueOf(modelSite.getSiteId()).equals(siteId)) {
//                        siteAdminList.add(modelSite);
//                    }
                    siteAdminList.add(modelSite);
                }
                fromSiteList=new ArrayList<>(siteAdminList);
                toSiteList=new ArrayList<>(siteAdminList);
                Log.e("siteAdminList", "" + siteAdminList.size());
                Log.e("siteAdminList", "Log::" + binding.spinnerTo.getSelectedItemPosition());

                siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0));
                    SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
                    binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
                    binding.spinnerTo.setAdapter(siteSpinnerAdapter);
                    siteIdSelected = String.valueOf(siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId());
                    siteNameSelected = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteCity();
                    binding.llSelectSite.setVisibility(View.VISIBLE);



//                if(binding.spinnerTo.getSelectedItemPosition()==-1){
//                    siteAdminList.add(0, new ModelSite(getString(R.string.select_site), 0));
//                    SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
//                    binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
//                    siteIdSelected = String.valueOf(siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteId());
//                    siteNameSelected = siteAdminList.get(binding.spinnerSelectSite.getSelectedItemPosition()).getSiteCity();
//                    binding.llSelectSite.setVisibility(View.VISIBLE);
//                }else{
//                    for(int i=0;i<siteAdminList.size();i++){
//                        if(siteAdminList.get(i).getSiteId()!=tosite){
//                            fromSiteList.add(siteAdminList.get(i));
//                        }
//                        fromSiteList.add(0,new ModelSite("Select Site",0));
//                        FromSpinnerAdapter fromSpinnerAdapter=new FromSpinnerAdapter();
//                        binding.spinnerSelectSite.setAdapter(fromSpinnerAdapter);
//                    }
//                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


    class ToSpinnerAdapter
            extends BaseAdapter {
        ToSpinnerAdapter() {
        }

        public int getCount() {
            return toSiteList.size();
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
            textView.setText(toSiteList.get(n).getSiteCity());
            return view2;
        }
    }

    class FromSpinnerAdapter
            extends BaseAdapter {
        FromSpinnerAdapter() {
        }

        public int getCount() {
            return fromSiteList.size();
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
            textView.setText(fromSiteList.get(n).getSiteCity());
            return view2;
        }
    }
}