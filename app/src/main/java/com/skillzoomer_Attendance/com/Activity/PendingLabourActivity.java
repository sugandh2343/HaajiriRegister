package com.skillzoomer_Attendance.com.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skillzoomer_Attendance.com.Adapter.AdapterPendingLabour;
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.databinding.ActivityPendingLabourBinding;

import java.util.ArrayList;

public class PendingLabourActivity extends AppCompatActivity {
    ActivityPendingLabourBinding binding;
    long siteId;
    private ArrayList<ModelLabour> modelLabourArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPendingLabourBinding.inflate(getLayoutInflater());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        siteId=intent.getLongExtra("siteId",0);
        modelLabourArrayList=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(getSharedPreferences("UserDetails",MODE_PRIVATE).getString("hrUid","")).child("Industry").child("Construction")
                .child("Site").child(String.valueOf(siteId)).child("Labours").orderByChild("status").equalTo("Pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelLabourArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLabour modelLabour=ds.getValue(ModelLabour.class);
                    modelLabourArrayList.add(modelLabour);

                }
                AdapterPendingLabour adapterPendingLabour=new AdapterPendingLabour(PendingLabourActivity.this,modelLabourArrayList);
                binding.rvPendingLabour.setAdapter(adapterPendingLabour);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PendingLabourActivity.this, MemberTimelineActivity.class));
            }
        });
    }
}