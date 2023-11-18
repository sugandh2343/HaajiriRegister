//package com.skillzoomer_Attendance.com.Adapter;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.icu.text.SimpleDateFormat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.skillzoomer_Attendance.com.Activity.MemberTimelineActivity;
//import com.skillzoomer_Attendance.com.Model.ModelLabourPayment;
//import com.skillzoomer_Attendance.com.Model.ModelLabourPresent;
//import com.skillzoomer_Attendance.com.Model.ModelSite;
//import com.skillzoomer_Attendance.com.R;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//
//public class AdapterSelfTimeLine extends RecyclerView.Adapter<AdapterSelfTimeLine.HolderSelfTimeline> {
//    private Context context;
//    private ArrayList<ModelSite> siteArrayList;
//
//    public AdapterSelfTimeLine(Context context, ArrayList<ModelSite> siteArrayList) {
//        this.context = context;
//        this.siteArrayList = siteArrayList;
//    }
//
//    @NonNull
//    @Override
//    public AdapterSelfTimeLine.HolderSelfTimeline onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view= LayoutInflater.from(context).inflate(R.layout.layout_self_timeline,parent,false);
//        return new AdapterSelfTimeLine.HolderSelfTimeline(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AdapterSelfTimeLine.HolderSelfTimeline holder, int position) {
//        Log.e("Adapter",""+siteArrayList.size());
//        holder.setIsRecyclable(false);
//        ModelSite modelSite =siteArrayList.get(holder.getAdapterPosition());
//        holder.txt_siteId.setText(String.valueOf(modelSite.getSiteId()));
//        holder.txt_site_name.setText(modelSite.getSiteName());
//        if (modelSite.getSkilled() > 0 && modelSite.getSkilledSeen()!=null && !modelSite.getSkilledSeen()) {
//            holder.txt_skilled.setText(String.valueOf(modelSite.getSkilled()));
//            holder.txt_skilled.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
//            holder.txt_skilled_time.setVisibility(View.VISIBLE);
//            holder.txt_skilled_time.setText(modelSite.getSkilledTime());
//            holder.txt_skilled.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    openLabourDialog("Skilled",modelSite.getSiteId());
//                }
//            });
//
//        } else {
//            holder.txt_skilled.setText(String.valueOf(modelSite.getSkilled()));
//            holder.txt_skilled.setBackgroundColor(context.getResources().getColor(R.color.white));
//            holder.txt_skilled_time.setVisibility(View.GONE);
//        }
//        if (modelSite.getUnskilled() > 0&& modelSite.getUnSkilledSeen()!=null && !modelSite.getUnSkilledSeen()) {
//            holder.txt_unskilled.setText(String.valueOf(modelSite.getUnskilled()));
//            holder.txt_unskilled.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
//            holder.txt_unskilled_time.setText(modelSite.getUnskilledTime());
//            holder.txt_unskilled_time.setVisibility(View.VISIBLE);
//            holder.txt_unskilled.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    openLabourDialog("Unskilled",modelSite.getSiteId());
//                }
//            });
//        } else {
//            holder.txt_unskilled.setText(String.valueOf(modelSite.getUnskilled()));
//            holder.txt_unskilled.setBackgroundColor(context.getResources().getColor(R.color.white));
//            holder.txt_unskilled_time.setVisibility(View.GONE);
//        }
//
//        if(modelSite.getSkilledSeen()!=null && modelSite.getSkilledSeen()){
//            holder.txt_skilled.setBackgroundColor(context.getResources().getColor(R.color.white));
//            holder.txt_skilled.setClickable(false);
//        }
//        if(modelSite.getUnSkilledSeen()!=null && modelSite.getUnSkilledSeen()){
//            holder.txt_unskilled.setBackgroundColor(context.getResources().getColor(R.color.white));
//            holder.txt_unskilled.setClickable(false);
//
//        }
//        if(modelSite.getPaymentSum()>0){
//            holder.txt_payment_amount.setText(String.valueOf(modelSite.getPaymentSum()));
//            holder.txt_payment_amount.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showLabourPaymentDialog(siteArrayList.get(holder.getAdapterPosition()));
//                }
//            });
//        }else{
//            holder.txt_payment_amount.setText("0");
//        }
//        if(modelSite.getUpToDatePAyment()>0){
//            holder.txt_upTo_date_payment.setText(String.valueOf(modelSite.getUpToDatePAyment()));
//        }else{
//            holder.txt_upTo_date_payment.setText("0");
//        }
//        holder.txt_site_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(context, MemberTimelineActivity.class);
//                intent.putExtra("siteId",siteArrayList.get(holder.getAdapterPosition()).getSiteId());
//                intent.putExtra("siteName",siteArrayList.get(holder.getAdapterPosition()).getSiteName());
//                Log.e("siteId786010","Ad:"+siteArrayList.get(holder.getAdapterPosition()).getSiteId());
//                context.startActivity(intent);
//            }
//        });
//
//    }
//
//    private void showLabourPaymentDialog(ModelSite modelSite) {
//        ArrayList<ModelLabourPayment> labourPaymentArrayList=new ArrayList<>();
//        String currentDate = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
//        }
//        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
//        View mView = LayoutInflater.from(context).inflate(R.layout.layout_show_labour_payment, null);
//        RecyclerView rv_labour_payment=mView.findViewById(R.id.rv_labour_payment);
//        Button btn_ok=mView.findViewById(R.id.btn_ok);
//        alert.setView(mView);
//        final AlertDialog alertDialog = alert.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        Log.e("CurrentDate",currentDate);
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(modelSite.getSiteId())).child("Payments").child(currentDate).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.e("Snapshot1234",""+snapshot.exists());
//                if(snapshot.exists()){
//                    for(DataSnapshot ds: snapshot.getChildren()){
//                        ModelLabourPayment modelLabourPayment=ds.getValue(ModelLabourPayment.class);
//                        labourPaymentArrayList.add(modelLabourPayment);
//                    }
//                    Log.e("ModelLabourPresent",""+labourPaymentArrayList.size());
//                    AdapterLabourPayment adapterLabourPayment=new AdapterLabourPayment(context,labourPaymentArrayList);
//                    rv_labour_payment.setAdapter(adapterLabourPayment);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
//        alertDialog.show();
//    }
//
//    private void openLabourDialog(String type, long siteId) {
//        ArrayList<ModelLabourPresent> modelPresentLabourArrayList=new ArrayList<>();
//        String currentDate = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(new Date());
//        }
//        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
//        View mView = LayoutInflater.from(context).inflate(R.layout.layout_show_present_labour_list, null);
//        RecyclerView rv_present_labour=mView.findViewById(R.id.rv_present_labour);
//        Button btn_ok=mView.findViewById(R.id.btn_ok);
//        alert.setView(mView);
//        final AlertDialog alertDialog = alert.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        Log.e("CurrentDate",currentDate);
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Site");
//        reference.child(String.valueOf(siteId)).child("Attendance").child("Labours").child(currentDate).orderByChild("labourType").equalTo(type).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.e("Snapshot1234",""+snapshot.exists());
//                if(snapshot.exists()){
//                    for(DataSnapshot ds: snapshot.getChildren()){
//                        ModelLabourPresent modelPresentLabour=ds.getValue(ModelLabourPresent.class);
//                        modelPresentLabourArrayList.add(modelPresentLabour);
//                    }
//                    Log.e("ModelLabourPresent",""+modelPresentLabourArrayList.size());
//                    AdapterLabourPresent adapterLabourPresent=new AdapterLabourPresent(context,modelPresentLabourArrayList);
//                    rv_present_labour.setAdapter(adapterLabourPresent);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                HashMap<String,Object> hashMap=new HashMap<>();
//                if (type.equals("Skilled")){
//                    hashMap.put("skilledSeen",true);
//                }else{
//                    hashMap.put("unSkilledSeen",true);
//                }
//                DatabaseReference refrence=FirebaseDatabase.getInstance().getReference("Site");
//                refrence.child(String.valueOf(siteId)).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                alertDialog.dismiss();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                alertDialog.dismiss();
//                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
//        alertDialog.show();
//    }
//
//    @Override
//    public int getItemCount() {
//        return siteArrayList.size();
//    }
//
//    public class HolderSelfTimeline extends RecyclerView.ViewHolder {
//        TextView txt_site_name,txt_siteId,txt_skilled,txt_unskilled,txt_skilled_time,txt_unskilled_time,
//                txt_upTo_date_payment,txt_payment_amount;
//
//        public HolderSelfTimeline(@NonNull View itemView) {
//            super(itemView);
//            txt_site_name=itemView.findViewById(R.id.txt_site_name);
//            txt_siteId=itemView.findViewById(R.id.txt_siteId);
//            txt_skilled=itemView.findViewById(R.id.txt_skilled);
//            txt_unskilled=itemView.findViewById(R.id.txt_unskilled);
//            txt_skilled_time=itemView.findViewById(R.id.txt_skilled_time);
//            txt_unskilled_time=itemView.findViewById(R.id.txt_unskilled_time);
//            txt_payment_amount=itemView.findViewById(R.id.txt_payment_amount);
//            txt_upTo_date_payment=itemView.findViewById(R.id.txt_upTo_date_payment);
//        }
//    }
//}
