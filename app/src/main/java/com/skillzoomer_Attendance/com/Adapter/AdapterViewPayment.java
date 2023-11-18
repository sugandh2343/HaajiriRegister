package com.skillzoomer_Attendance.com.Adapter;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
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
import com.skillzoomer_Attendance.com.Model.ModelViewPayment;
import com.skillzoomer_Attendance.com.R;

import org.json.JSONException;
import org.json.JSONObject;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterViewPayment extends RecyclerView.Adapter<AdapterViewPayment.HolderViewPayment> {
    private Context context;
    private ArrayList<ModelViewPayment> modelViewPayments;
    private long siteId;

    public AdapterViewPayment(Context context, ArrayList<ModelViewPayment> modelViewPayments, long siteId) {
        this.context = context;
        this.modelViewPayments = modelViewPayments;
        this.siteId = siteId;
    }

    @NonNull
    @Override
    public HolderViewPayment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_view_payment,parent,false);
        return new AdapterViewPayment.HolderViewPayment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewPayment holder, int position) {
        holder.setIsRecyclable(false);
        ModelViewPayment modelViewPayment=new ModelViewPayment();

            holder.et_payment.setEnabled(false);



            modelViewPayment=modelViewPayments.get(position);
            holder.txt_sr_no.setText(modelViewPayment.getSrNo());
            holder.txt_company_id.setText(modelViewPayment.getLabourId());
            holder.txt_name.setText(modelViewPayment.getLabourName());
            Log.e("Type",""+modelViewPayment.getLabourType());
            if(modelViewPayment.getLabourType().equals("Skilled")){
                holder.txt_type.setText("SK");
            }else{
                holder.txt_type.setText("USK");
            }

            holder.txt_time.setText(modelViewPayment.getTime());
            holder.et_payment.setText(modelViewPayment.getAmt());

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.et_payment.setFocusable(true);
                holder.et_payment.setEnabled(true);
                holder.btn_delete.setVisibility(View.GONE);
                holder.btn_add.setVisibility(View.VISIBLE);
                holder.btn_edit.setVisibility(View.GONE);

            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("HolderPosition",""+holder.getAdapterPosition());
                Log.e("HolderPosition","M"+modelViewPayments.size());
                if(modelViewPayments.size()>1) {
                    deleteLabourfromCart(modelViewPayments.get(holder.getAdapterPosition()).getId(), holder.getAdapterPosition());
                }else{
                    deletePaymentCart();
                }
            }
        });
        ModelViewPayment finalModelViewPayment = modelViewPayment;
        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getPayableAmountLabour(siteId, finalModelViewPayment.getLabourId(),holder.et_payment.getText().toString(),holder.getAdapterPosition()
                        ,modelViewPayments.get(holder.getAdapterPosition()),view,holder);



            }
        });

    }

    private void getPayableAmountLabour(long siteId, String labourId, String s, int adapterPosition, ModelViewPayment modelViewPayment, View view, HolderViewPayment holder) {
        String uid;
        if(context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteId)).child("Labours").child(labourId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long PayableAmount=snapshot.child("payableAmt").getValue(Long.class);
                if(!TextUtils.isEmpty(s)) {
                    if(Integer.parseInt(s)<=PayableAmount){
                        updateLabour(labourId,s,adapterPosition,modelViewPayments.get(adapterPosition),view);
                    }else if (context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","Supervisor").equals("Supervisor")){
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.not_allowed))
                                .setMessage(R.string.payable_amount_mesage)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ask_permission, (dialogInterface, i) -> {
                                    getHrUid(siteId,modelViewPayment,s, (int) PayableAmount);
                                    deleteLabourfromCart(modelViewPayment.getId(),adapterPosition);

                                })
                                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                                        dialogInterface.dismiss());
                        builder.show();
                        Toast.makeText(context,context.getString(R.string.you_cannot_pay_more_than_payable_amt) , Toast.LENGTH_SHORT).show();
                    }else{
                        showReasonDialog(s,PayableAmount,labourId,
                                modelViewPayment, adapterPosition,holder);
                    }

                }else{
                    Toast.makeText(context, R.string.enter_advance, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showReasonDialog(String s, long payableAmount, String labourId, ModelViewPayment modelViewPayment, int adapterPosition, HolderViewPayment holder) {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_admin_show_payment_request, null);
        alert.setView(mView);
        TextView txt_labourId,txt_to_pay_amount,txt_payable_amount;
        EditText et_your_remark;
        Button btn_allow,btn_cancel;
        txt_labourId=mView.findViewById(R.id.txt_labourId);
        txt_to_pay_amount=mView.findViewById(R.id.txt_to_pay_amount);
        txt_payable_amount=mView.findViewById(R.id.txt_payable_amount);
        btn_allow=mView.findViewById(R.id.btn_allow);
        btn_cancel=mView.findViewById(R.id.btn_cancel);
        et_your_remark=mView.findViewById(R.id.et_your_remark);
        txt_labourId.setText(labourId);
        txt_payable_amount.setText(String.valueOf(payableAmount));
        txt_to_pay_amount.setText(s);
        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_your_remark.getText().toString())){
                    Toast.makeText(context, "Remark cannot be empty", Toast.LENGTH_SHORT).show();
                }else {
                    updateLabour(labourId,s,adapterPosition,modelViewPayment,view);
                    alertDialog.dismiss();
                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog.show();
    }

    private void addToLocalDatabase(ModelViewPayment modelPaymentData, String s, int adapterPosition, HolderViewPayment holder, String toString) {
        SimpleDateFormat df = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }
        SimpleDateFormat time = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            time = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        }
        Date c = Calendar.getInstance().getTime();
        String currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentDate = df.format(c);
        }
        String currentTime= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentTime = time.format(c);
        }
        String timestam=""+System.currentTimeMillis();
        EasyDB easyDB = EasyDB.init(context.getApplicationContext(), "Payment_Db")
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
        try {
            easyDB.addData("Timestamp",timestam)
                    .addData("LabourId" , modelPaymentData.getLabourId())
                    .addData("LabourName" , modelPaymentData.getLabourName())
                    .addData("LabourType" , modelPaymentData.getLabourType())
                    .addData("Date" , currentDate)
                    .addData("Time" , currentTime)
                    .addData("Advance" , s)
                    .addData("Reason" , toString)
                    .doneDataAdding();
            Log.e("Deatails","Id"+modelPaymentData.getLabourId()+"--Name:"+
                    modelPaymentData.getLabourName()+"----Type:"+modelPaymentData.getLabourType()+"-----Advance"+s);
            Toast.makeText(context,"Data Added Successfully",Toast.LENGTH_LONG).show();
//            deleteFromList(adapterPosition,holder);
        }catch (SQLiteConstraintException e){
            Log.e("Exception",e.getMessage());
        }
    }



    private void getHrUid(long siteID, ModelViewPayment modelPaymentData, String amount, int payableAmount) {
        String uid;
        if(context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }
        Log.e("SiteId",""+siteID);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Industry").child("Construction").child("Site").child(String.valueOf(siteID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hrUid = snapshot.child("hrUid").getValue(String.class);
                Log.e("HrUId",hrUid);
                getTokenAdmin(siteID, hrUid,modelPaymentData,amount,payableAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTokenAdmin(long siteID, String hrUid, ModelViewPayment modelPaymentData, String amount, int payableAmount) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(hrUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("token").getValue(String.class);
                sendNotification(siteID,token,modelPaymentData,amount,payableAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(long siteID, String token, ModelViewPayment modelPaymentData, String amount, int payableAmount) {
        Log.d("token : ", token);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();

            jsonobject_notification.put("title", "Request from Associate");
            jsonobject_notification.put("body", "Associate has Requested to pay "+amount+" "+"to worker "+modelPaymentData.getLabourName()+".Click to take Action");




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
                updateOnSiteDatabase(siteID,modelPaymentData,amount,payableAmount);




            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error :  ", String.valueOf(error));
                updateOnSiteDatabase(siteID,modelPaymentData,amount,payableAmount);


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
    private void updateOnSiteDatabase(long siteID, ModelViewPayment modelPaymentData, String amount, int payableAmount) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("associateRequest",true);
        hashMap.put("associateRequestType","PayableAmount");
        hashMap.put("toPayAmount",amount);
        hashMap.put("payableAmount",payableAmount);
        hashMap.put("labourIdPaying",modelPaymentData.getLabourId());
        hashMap.put("associateRequestStatus","Pending");
        String uid;
        if(context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","").equals("Supervisor")){
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("hrUid","");
        }else{
            uid=context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE).getString("uid","");
        }

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
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


    private void deletePaymentCart() {
        EasyDB easyDB=EasyDB.init(context,"Payment_Db")
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
        easyDB.deleteAllDataFromTable();
//        deletePaymentCart();
        modelViewPayments.clear();
        notifyDataSetChanged();
    }

    private void updateLabour(String id, String advance, int adapterPosition, ModelViewPayment modelViewPayment, View view) {

        SimpleDateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        }
        SimpleDateFormat time = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            time = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        }
        Date c = Calendar.getInstance().getTime();
        String currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDate = df.format(c);
        }
        String currentTime= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentTime = time.format(c);
        }
        String timestam=""+System.currentTimeMillis();
        EasyDB db = EasyDB.init(context, "Payment_Db")
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
        db.deleteRow(2,id);
        try {
            db.addData("Timestamp",timestam)
                    .addData("LabourId" , modelViewPayment.getLabourId())
                    .addData("LabourName" , modelViewPayment.getLabourName())
                    .addData("LabourType" , modelViewPayment.getLabourId())
                    .addData("Date" , currentDate)
                    .addData("Time" , currentTime)
                    .addData("Advance" , advance)
                    .addData("Reason" , "")
                    .doneDataAdding();

            Toast.makeText(context,"Data Updated Successfully",Toast.LENGTH_LONG).show();

        }catch (SQLiteConstraintException e){
            Log.e("Exception",e.getMessage());
        }

        notifyDataSetChanged();
        Intent intent = new Intent("payment_updated");

        intent.putExtra("position", adapterPosition);
        intent.putExtra("timestamp",timestam);
        intent.putExtra("labourId", id);
        intent.putExtra("advanceUpdate", advance);
        LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
        notifyDataSetChanged();
    }

    private void deleteLabourfromCart(String labourId, int position) {
        EasyDB db = EasyDB.init(context, "Payment_Db")
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
        db.deleteRow(1,labourId);
        modelViewPayments.remove(position);
        notifyDataSetChanged();


    }

    @Override
    public int getItemCount() {
        return modelViewPayments.size();
    }

    public class HolderViewPayment extends RecyclerView.ViewHolder {
        TextView txt_sr_no,txt_company_id,txt_name,txt_type,txt_time,et_payment;
        ImageView btn_edit,btn_delete,btn_add;
        public HolderViewPayment(@NonNull View itemView) {
            super(itemView);
            txt_sr_no=itemView.findViewById(R.id.txt_sr_no);
            txt_company_id=itemView.findViewById(R.id.txt_company_id);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_type=itemView.findViewById(R.id.txt_type);
            txt_time=itemView.findViewById(R.id.txt_time);
            et_payment=itemView.findViewById(R.id.et_payment);
            btn_edit=itemView.findViewById(R.id.btn_edit);
            btn_delete=itemView.findViewById(R.id.btn_delete);
            btn_add=itemView.findViewById(R.id.btn_add);
        }
    }
}
