package com.skillzoomer_Attendance.com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.icu.text.SimpleDateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.skillzoomer_Attendance.com.Model.ModelPaymentData;
import com.skillzoomer_Attendance.com.R;
import com.skillzoomer_Attendance.com.Activity.SearchForLabourPayment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterPaymentData extends RecyclerView.Adapter<AdapterPaymentData.HolderPaymentData> {
    private Context context;
    public ArrayList<ModelPaymentData> dataList;
    long siteID;
    String siteName;

    public AdapterPaymentData(Context context, ArrayList<ModelPaymentData> dataList, long siteID, String siteName) {
        this.context = context;
        this.dataList = dataList;
        this.siteID = siteID;
        this.siteName = siteName;
    }

    @NonNull
    @Override
    public HolderPaymentData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_payment_single_row,parent,false);
        return new AdapterPaymentData.HolderPaymentData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPaymentData holder, int position) {
        ModelPaymentData modelPaymentData=dataList.get(position);
        if(modelPaymentData.getCount()==1){
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.txt_type.setVisibility(View.VISIBLE);
            holder.et_payment.setVisibility(View.VISIBLE);
            holder.txt_name.setText(modelPaymentData.getLabourName());
            holder.txt_company_id.setText(modelPaymentData.getLabourId());
            holder.txt_type.setText(modelPaymentData.getType());
        }else if(modelPaymentData.getCount()==0){
            holder.txt_type.setVisibility(View.GONE);
            holder.et_payment.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_search.setVisibility(View.VISIBLE);
            holder.txt_name.setText(modelPaymentData.getLabourName());
            holder.txt_company_id.setText(modelPaymentData.getLabourId());
            holder.btn_delete.setVisibility(View.VISIBLE);
            holder.btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, SearchForLabourPayment.class);
                    intent.putExtra("name","");
                    intent.putExtra("siteId",siteID);
                    intent.putExtra("siteName",siteName);
                    intent.putExtra("WorkerType",modelPaymentData.getType());
                    context.startActivity(intent);
                }
            });
        }else if(modelPaymentData.getCount()>1){
            holder.txt_type.setVisibility(View.GONE);
            holder.et_payment.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.GONE);
            holder.txt_name.setText(modelPaymentData.getLabourName());
            holder.txt_company_id.setText(modelPaymentData.getLabourId());
            holder.btn_search.setVisibility(View.VISIBLE);
            Intent intent=new Intent(context,SearchForLabourPayment.class);
            intent.putExtra("name",modelPaymentData.getLabourName());
            intent.putExtra("WorkerType",modelPaymentData.getType());
            intent.putExtra("siteId",siteID);
            intent.putExtra("siteName",siteName);
            context.startActivity(intent);

        }
        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("PayableAmt",""+dataList.get(holder.getAdapterPosition()).getPayableAmt());
                if(!TextUtils.isEmpty(holder.et_payment.getText().toString())) {
                    if(Integer.parseInt(holder.et_payment.getText().toString())<=dataList.get(holder.getAdapterPosition()).getPayableAmt()){
                        addToLocalDatabase(modelPaymentData, holder.et_payment.getText().toString(), holder.getAdapterPosition(),holder,"");
                    }else if (context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","Supervisor").equals("Supervisor")){
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.not_allowed))
                                .setMessage(R.string.payable_amount_mesage)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ask_permission, (dialogInterface, i) -> {
                                    getHrUid(siteID,modelPaymentData,holder.et_payment.getText().toString());

                                })
                                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                                        dialogInterface.dismiss());
                        builder.show();
                        Toast.makeText(context,context.getString(R.string.you_cannot_pay_more_than_payable_amt) , Toast.LENGTH_SHORT).show();
                    }else{
                        showReasonDialog(holder.et_payment.getText().toString(),dataList.get(holder.getAdapterPosition()).getPayableAmt(),dataList.get(holder.getAdapterPosition()).getLabourId(),
                                modelPaymentData, holder.getAdapterPosition(),holder);
                    }

                }else{
                    Toast.makeText(context, R.string.enter_advance, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapter_position = holder.getAdapterPosition();
                Intent intent = new Intent("adapter_position");

                intent.putExtra("position", adapter_position);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                notifyDataSetChanged();
            }
        });


    }

    private void showReasonDialog(String toString, long payableAmt, String labourId, ModelPaymentData modelPaymentData, int adapterPosition, HolderPaymentData holder) {
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
        txt_payable_amount.setText(String.valueOf(payableAmt));
        txt_to_pay_amount.setText(String.valueOf(toString));
        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_your_remark.getText().toString())){
                    Toast.makeText(context, "Remark cannot be empty", Toast.LENGTH_SHORT).show();
                }else {
                    addToLocalDatabase(modelPaymentData, toString, holder.getAdapterPosition(), holder, et_your_remark.getText().toString());
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

    private void getHrUid(long siteID, ModelPaymentData modelPaymentData, String amount) {
        Log.e("SiteId",""+siteID);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hrUid = snapshot.child("hrUid").getValue(String.class);
                Log.e("HrUId",hrUid);
                getTokenAdmin(siteID, hrUid,modelPaymentData,amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTokenAdmin(long siteID, String hrUid, ModelPaymentData modelPaymentData, String amount) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(hrUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token=snapshot.child("token").getValue(String.class);
                sendNotification(siteID,token,modelPaymentData,amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(long siteID, String token, ModelPaymentData modelPaymentData, String amount) {
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
                updateOnSiteDatabase(siteID,modelPaymentData,amount);




            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error :  ", String.valueOf(error));
                updateOnSiteDatabase(siteID,modelPaymentData,amount);


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

    private void updateOnSiteDatabase(long siteID, ModelPaymentData modelPaymentData, String amount) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("associateRequest",true);
        hashMap.put("associateRequestType","PayableAmount");
        hashMap.put("toPayAmount",amount);
        hashMap.put("payableAmount",modelPaymentData.getPayableAmt());
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

    private void addToLocalDatabase(ModelPaymentData modelPaymentData, String advance, int position, HolderPaymentData holder, String s) {
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
                    .addData("LabourType" , modelPaymentData.getType())
                    .addData("Date" , currentDate)
                    .addData("Time" , currentTime)
                    .addData("Advance" , advance)
                    .addData("Reason" , "")
                    .doneDataAdding();
            Log.e("Deatails","Id"+modelPaymentData.getLabourId()+"--Name:"+
                    modelPaymentData.getLabourName()+"----Type:"+modelPaymentData.getType()+"-----Advance"+advance);
            Toast.makeText(context,"Data Added Successfully",Toast.LENGTH_LONG).show();
            deleteFromList(position,holder);
        }catch (SQLiteConstraintException e){
            Log.e("Exception",e.getMessage());
        }
    }

    private void deleteFromList(int position, HolderPaymentData holder) {
        holder.et_payment.getText().clear();
        dataList.remove(position);
        notifyDataSetChanged();
        notifyParent(position);
    }

    private void notifyParent(int position) {
        Intent intent = new Intent("payment_done");

        intent.putExtra("positionChange", String.valueOf(position));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class HolderPaymentData extends RecyclerView.ViewHolder {
        private TextView txt_company_id,txt_name,txt_type;
        private EditText et_payment;
       Button btn_add;
       ImageButton btn_search,btn_delete;
        public HolderPaymentData(@NonNull View itemView) {
            super(itemView);
            txt_company_id=itemView.findViewById(R.id.txt_company_id);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_type=itemView.findViewById(R.id.txt_type);
            et_payment=itemView.findViewById(R.id.et_payment);
            btn_add=itemView.findViewById(R.id.btn_add);
            btn_search=itemView.findViewById(R.id.btn_search);
            btn_delete=itemView.findViewById(R.id.btn_delete);
        }
    }
}
