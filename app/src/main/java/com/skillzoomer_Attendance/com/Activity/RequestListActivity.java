package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterFundRequest;
import com.skillzoomer_Attendance.com.Model.ModelFundRequest;
import com.skillzoomer_Attendance.com.Model.ModelSite;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityRequestListBinding;

import java.util.ArrayList;

public class RequestListActivity extends AppCompatActivity {
   ActivityRequestListBinding binding;
   private ArrayList<ModelFundRequest> modelFundRequestArrayList;
    String siteName;
    long siteId=0;
    private ArrayList<ModelSite> siteAdminList;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private long cashInHand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRequestListBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();

        siteAdminList=new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        modelFundRequestArrayList=new ArrayList<>();
//        getSiteListAdministrator();
        binding.llManpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestListActivity.this, ReportHome.class));
            }
        });
        binding.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add Activity work here
                startActivity(new Intent(RequestListActivity.this,timelineActivity.class));
            }
        });
        binding.llWorkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestListActivity.this,PicActivityAdmin.class));
            }
        });
        binding.spinnerSelectSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                siteId = siteAdminList.get(i).getSiteId();
                siteName = siteAdminList.get(i).getSiteName();
                Log.e("SiteId", "Spinner" + siteId);
                progressDialog.show();
//                getCashInHand(siteId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(siteId==0){
                    Toast.makeText(RequestListActivity.this, getString(R.string.select_site), Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
//                    getRequestList(siteId);
                }
            }
        });
    }
//    private void getCashInHand(long siteId) {
//        Log.e("SiteId1294",""+ siteId);
//        cashInHand=0;
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf (siteId)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                progressDialog.dismiss();
//                if(snapshot.hasChild("cashInHand")){
//                    cashInHand=snapshot.child("cashInHand").getValue(long.class);
//                    binding.txtCashInHand.setText(String.valueOf(cashInHand));
//
//                }else{
//                    cashInHand=0;
//                    binding.txtCashInHand.setText(String.valueOf(cashInHand));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                progressDialog.dismiss();
//
//            }
//        });
//    }

//    private void getRequestList(long siteId) {
//        modelFundRequestArrayList.clear();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Request").child("FundRequest").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                progressDialog.dismiss();
//                for(DataSnapshot ds:snapshot.getChildren()){
//                    ModelFundRequest modelFundRequest=ds.getValue(ModelFundRequest.class);
//                    modelFundRequestArrayList.add(modelFundRequest);
//                    Log.e("Size",""+modelFundRequest.getReqId());
//                }
//
//
//                if(modelFundRequestArrayList.size()==0){
//                    binding.rvReqList.setVisibility(View.GONE);
//                    binding.noReqToShow.setVisibility(View.VISIBLE);
//                }else{
//                    AdapterFundRequest adapterFundRequest=new AdapterFundRequest(RequestListActivity.this,modelFundRequestArrayList);
//                    binding.rvReqList.setAdapter(adapterFundRequest);
//                    binding.rvReqList.setVisibility(View.VISIBLE);
//                    binding.noReqToShow.setVisibility(View.GONE);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    private void getSiteListAdministrator() {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Site");
//        reference.orderByChild("hrUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                siteAdminList.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    ModelSite modelSite = ds.getValue(ModelSite.class);
//                    siteAdminList.add(modelSite);
//                }
//                Log.e("siteAdminList", "" + siteAdminList.size());
//                SiteSpinnerAdapter siteSpinnerAdapter = new SiteSpinnerAdapter();
//                binding.spinnerSelectSite.setAdapter(siteSpinnerAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
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
}