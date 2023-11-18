package com.skillzoomer_Attendance.com.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.icu.text.SimpleDateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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
import com.skillzoomer_Attendance.com.Model.ModelLabour;
import com.skillzoomer_Attendance.com.R;
import com.squareup.picasso.Picasso;

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

public class AdapterSearchLabourPayment extends RecyclerView.Adapter<AdapterSearchLabourPayment.HolderSearchLabourPayment> {
    private Context context;
    private ArrayList<ModelLabour> labourArrayList;

    public AdapterSearchLabourPayment(Context context, ArrayList<ModelLabour> labourArrayList) {
        this.context = context;
        this.labourArrayList = labourArrayList;
    }

    @NonNull
    @Override
    public AdapterSearchLabourPayment.HolderSearchLabourPayment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_search_labour_payment_single_row,parent,false);
        return new AdapterSearchLabourPayment.HolderSearchLabourPayment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSearchLabourPayment.HolderSearchLabourPayment holder, int position) {
        ModelLabour modelLabour=labourArrayList.get(position);
        String profile=modelLabour.getProfile();
        holder.txt_labourId.setText(modelLabour.getLabourId());
        holder.txt_labourName.setText(modelLabour.getName());
        String type= modelLabour.getType();
        holder.txt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfileDialog(profile);
            }
        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if()
//                uploadToLocalDatabase(holder.txt_advance.getText().toString(),modelLabour.getLabourId(), modelLabour.getName(),type,holder.getAdapterPosition(),holder);
//
//            }
//        });
        holder.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(holder.txt_advance.getText().toString())) {
                    if(Integer.parseInt(holder.txt_advance.getText().toString())<=labourArrayList.get(holder.getAdapterPosition()).getPayableAmt()){
                        uploadToLocalDatabase(holder.txt_advance.getText().toString(), modelLabour.getLabourId(), modelLabour.getName(), type, holder.getAdapterPosition(), holder,"");
                    }else if (context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("userDesignation","Supervisor").equals("Supervisor")){
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.not_allowed))
                                .setMessage(R.string.payable_amount_mesage)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ask_permission, (dialogInterface, i) -> {
                                    getHrUid(labourArrayList.get(holder.getAdapterPosition()).getSiteCode(),modelLabour,holder.txt_advance.getText().toString());

                                })
                                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                                        dialogInterface.dismiss());
                        builder.show();
                        Toast.makeText(context,context.getString(R.string.you_cannot_pay_more_than_payable_amt) , Toast.LENGTH_SHORT).show();
                        Toast.makeText(context,context.getString(R.string.you_cannot_pay_more_than_payable_amt) , Toast.LENGTH_SHORT).show();
                    }else{
                        showReasonDialog(holder.txt_advance.getText().toString(),labourArrayList.get(holder.getAdapterPosition()).getPayableAmt(),labourArrayList.get(holder.getAdapterPosition()).getLabourId(),
                                modelLabour.getName(), type, holder.getAdapterPosition(), holder);
                    }


                }else{
                    Toast.makeText(context, R.string.enter_advance, Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
    private void showReasonDialog(String toString, long payableAmt, String labourId, String name, String type, int adapterPosition, HolderSearchLabourPayment holder) {
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
        alertDialog.show();
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_your_remark.getText().toString())){
                    Toast.makeText(context, "Remark cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    uploadToLocalDatabase(holder.txt_advance.getText().toString(), labourId, name, type, holder.getAdapterPosition(), holder,et_your_remark.getText().toString());
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
    }
    private void getHrUid(long siteID, ModelLabour modelPaymentData, String amount) {
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

    private void getTokenAdmin(long siteID, String hrUid, ModelLabour modelPaymentData, String amount) {
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

    private void sendNotification(long siteID, String token, ModelLabour modelPaymentData, String amount) {
        Log.d("token : ", token);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();

            jsonobject_notification.put("title", "Request from Associate");
            jsonobject_notification.put("body", "Associate has Requested to pay "+amount+" "+"to worker "+modelPaymentData.getName()+".Click to take Action");




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

    private void updateOnSiteDatabase(long siteID, ModelLabour modelPaymentData, String amount) {
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

    private void uploadToLocalDatabase(String advance, String labourId, String name, String type, int position, HolderSearchLabourPayment holder, String s) {
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
        EasyDB easyDB = EasyDB.init(context, "Payment_Db")
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
                    .addData("LabourId" ,labourId)
                    .addData("LabourName" , name)
                    .addData("LabourType" , type)
                    .addData("Date" , currentDate)
                    .addData("Time" , currentTime)
                    .addData("Advance" , advance)
                    .addData("Reason" , s)
                    .doneDataAdding();
            Log.e("Deatails","Id"+labourId+"--Name:"+
                   name+"----Type:"+type+"-----Advance"+advance);
            Toast.makeText(context,"Payment Updated Successfully ",Toast.LENGTH_LONG).show();
            holder.btn_select.setVisibility(View.GONE);
        }catch (SQLiteConstraintException e){
            Log.e("Exception",e.getMessage());
        }
    }

    private void showProfileDialog(String profile) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.show_image, null);

        ImageView iv_profile=(ImageView) mView.findViewById(R.id.iv_profile);
        Picasso.get().load(profile).
                resize(400,400).centerCrop()
                .placeholder(R.drawable.ic_add).into(iv_profile);




        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);



        alertDialog.show();
    }



    @Override
    public int getItemCount() {
        return labourArrayList.size();
    }

    public class HolderSearchLabourPayment extends RecyclerView.ViewHolder {
        TextView txt_labourId,txt_labourName,txt_image;
        EditText txt_advance;
        AppCompatButton btn_select;
        public HolderSearchLabourPayment(@NonNull View itemView) {
            super(itemView);
            txt_labourId=itemView.findViewById(R.id.txt_labourId);
            txt_labourName=itemView.findViewById(R.id.txt_labourName);
            txt_advance=itemView.findViewById(R.id.txt_advance);
            txt_image=itemView.findViewById(R.id.txt_image);
            btn_select=itemView.findViewById(R.id.btn_select);

        }
    }
}
