package com.user.frenzi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenzi.R;
import com.user.frenzi.EditAddressActivity;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;
import com.user.frenzi.Responce.ServerGeneralResponse;
import com.user.frenzi.RestClient;
import com.user.frenzi.Wheretogo;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRecentAddressList extends RecyclerView.Adapter<AdapterRecentAddressList.MyViewHolder>{

    private static final String TAG = "AdapterResidentsFamilyL";
    private Context mContext;
    private Activity activity;
    private final AdapterRecentAddressList.OnItemClickListener listener;
    private List<ResponceFetchRecentAddressList.Response> AssignmentList;
    private static final String USER_PREF = "USER_PREF";
    private static final String SHOP_NAME = "SHOP_NAME";
    private static final String SHOP_ADDRESS= "SHOP_ADDRESS";
    private static final String ORDER_STATUS= "ORDER_STATUS";
    private static final String ORDER_TOTAL_PRIZE= "ORDER_TOTAL_PRIZE";
    private static final String ORDER_ID= "ORDER_ID";
    private static final String ORDER_PLACED_ID= "ORDER_PLACED_ID";

    //FAMILY MEMEBER DETAIL DB
    private static final String SHOP_BANNER = "SHOP_BANNER";


    private Fragment callingFragment;
    String User_ID;

    AlertDialog alertDialog;


    public interface OnItemClickListener {
        void onItemClick(ResponceFetchRecentAddressList.Response item);

    }

    public AdapterRecentAddressList( Context mContext, List<ResponceFetchRecentAddressList.Response> AssignmentList, String User_ID,  AdapterRecentAddressList.OnItemClickListener listener) {
        this.AssignmentList = AssignmentList;
        this.listener = listener;
        this.User_ID = User_ID;
        this.mContext=mContext;



    }

    @NonNull
    @Override
    public AdapterRecentAddressList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recent_address, parent, false);
        return new AdapterRecentAddressList.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterRecentAddressList.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_address.setText(AssignmentList.get(position).getAddress());
        holder.txt_title.setText(AssignmentList.get(position).getTitle());

        holder.full_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(AssignmentList.get(position));
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  listener.onItemClick(AssignmentList.get(position));
                String add_id = String.valueOf(AssignmentList.get(position).getId());

                deleteAlert(add_id);
            }
        });

        holder.txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  listener.onItemClick(AssignmentList.get(position));
                String add_id = String.valueOf(AssignmentList.get(position).getId());
                String address = String.valueOf(AssignmentList.get(position).getAddress());
                String address_status = String.valueOf(AssignmentList.get(position).getAddressStatus());
                String address_title = String.valueOf(AssignmentList.get(position).getTitle());
                String address_lat = String.valueOf(AssignmentList.get(position).getAddLat());
                String address_long = String.valueOf(AssignmentList.get(position).getAddLong());

                Intent intent = new Intent(mContext, EditAddressActivity.class);
                intent.putExtra("add_id",add_id);
                intent.putExtra("address",address);
                intent.putExtra("address_status",address_status);
                intent.putExtra("address_title",address_title);
                intent.putExtra("address_lat",address_lat);
                intent.putExtra("address_long",address_long);
                intent.putExtra("address_long",address_long);

                Log.e(TAG, "onClick: edit add_id >> "+add_id );
                Log.e(TAG, "onClick: edit address >> "+address );
                mContext.startActivity(intent);

            }
        });

        switch (AssignmentList.get(position).getAddressStatus()) {
            case "Home":
                holder.iv_address.setImageResource(R.drawable.home_address);
                break;
            case "Work":
                holder.iv_address.setImageResource(R.drawable.work_address);
                break;
            case "Other":
                holder.iv_address.setImageResource(R.drawable.other_address);
                break;
            default:
                holder.iv_address.setImageResource(R.drawable.history_gray);
                break;
        }


    }



    public void filterList(ArrayList<ResponceFetchRecentAddressList.Response> filterdNames) {
//        Log.e(TAG, "ADAPTER -- filterList: " + filterdNames );
        this.AssignmentList = filterdNames;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return AssignmentList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_address,txt_address_2,txt_title,txt_edit;
        LinearLayout full_layout;
        ImageView iv_address,iv_delete;


        public MyViewHolder(View view) {
            super(view);

            txt_title = view.findViewById(R.id.txt_title);
            txt_address = view.findViewById(R.id.txt_address);
            txt_address_2=view.findViewById(R.id.txt_address_2);
            full_layout=view.findViewById(R.id.full_layout);
            iv_address=view.findViewById(R.id.iv_address);
            iv_delete=view.findViewById(R.id.iv_delete);
            txt_edit=view.findViewById(R.id.txt_edit);




        }


    }

    public void deleteAlert(String add_id){

        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.dialog_delete_add, null);

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);


        TextView txt_cancel =promptsView.findViewById(R.id.txt_cancel);
        TextView txt_delete =promptsView.findViewById(R.id.txt_delete);


        alertDialogBuilder.setView(promptsView);



        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DeleteAddress(add_id);
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();



    }

    private void DeleteAddress( String address_id ) {

       /* ACProgressFlower dialog = new ACProgressFlower.Builder(mContext)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();*/


        Log.e(TAG, "User_id :: " + User_ID);

        RequestBody UserId = RequestBody.create(MediaType.parse("text/plain"), User_ID);
        RequestBody AddressId = RequestBody.create(MediaType.parse("text/plain"), address_id);


        RestClient.getClient().deleteAddresses(UserId,AddressId).enqueue(new Callback<ServerGeneralResponse>() {
            @Override
            public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    //dialog.dismiss();
                    alertDialog.dismiss();
                    Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    if(mContext instanceof Wheretogo){
                        ((Wheretogo)mContext).FetchRecentAddress();
                    }



                } else {

                   // dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ServerGeneralResponse> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                //dialog.dismiss();
            }
        });
    }
}


