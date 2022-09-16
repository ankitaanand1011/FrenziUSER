package com.user.frenzi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenzi.R;
import com.user.frenzi.DriverDetailsAfterBookingActivity;
import com.user.frenzi.Responce.ResponceFetchPreferedDriverList;
import com.user.frenzi.SaveRideData;
import com.user.frenzi.StripeAccountActivity;

import java.util.ArrayList;
import java.util.List;

public class AdapterPrefferedDriverList extends RecyclerView.Adapter<AdapterPrefferedDriverList.MyViewHolder> {

    private static final String TAG = "AdapterResidentsFamilyL";
    private Context mContext;
    private final AdapterPrefferedDriverList.OnItemClickListener listener;
    private List<ResponceFetchPreferedDriverList.Response> AssignmentList;
    Activity activity;
    private static final String USER_PREF = "USER_PREF";
    private static final String SHOP_NAME = "SHOP_NAME";
    private static final String SHOP_ADDRESS = "SHOP_ADDRESS";
    private static final String ORDER_STATUS = "ORDER_STATUS";
    private static final String ORDER_TOTAL_PRIZE = "ORDER_TOTAL_PRIZE";
    private static final String ORDER_ID = "ORDER_ID";
    private static final String ORDER_PLACED_ID = "ORDER_PLACED_ID";
    //FAMILY MEMEBER DETAIL DB
    private static final String SHOP_BANNER = "SHOP_BANNER";
    AlertDialog alertDialog;
    String Status;

    SaveRideData saveRideData;
    private Fragment callingFragment;
    int index = -1;


    public interface OnItemClickListener {
        void onItemClick(ResponceFetchPreferedDriverList.Response item);

    }

    public AdapterPrefferedDriverList(Activity activity, Context mContext, List<ResponceFetchPreferedDriverList.Response> AssignmentList, AdapterPrefferedDriverList.OnItemClickListener listener) {
        this.activity = activity;
        this.AssignmentList = AssignmentList;
        this.listener = listener;
        this.mContext = mContext;


    }

    @NonNull
    @Override
    public AdapterPrefferedDriverList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                      int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_preffered_drivers, parent, false);
        return new AdapterPrefferedDriverList.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPrefferedDriverList.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_driver_name.setText(AssignmentList.get(position).getName());
//        holder.txt_driver_rating.setText(AssignmentList.get(position).getReviews());
        holder.full_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(AssignmentList.get(position));
                index = position;
               // saveRideData.setDriver_id(String.valueOf(AssignmentList.get(position).getId()));
                notifyDataSetChanged();
               // popUppayment();

            }
        });

//        holder.full_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        if (index == position) {
            holder.full_layout.setBackgroundColor(Color.parseColor("#2CD3D3D3"));
//            holder.txt_property_name.setTextColor(Color.BLACK);
        } else {
            holder.full_layout.setBackgroundColor(Color.parseColor("#ffffff"));
//            holder.txt_property_name.setTextColor(Color.RED);
        }

    }


    public void filterList(ArrayList<ResponceFetchPreferedDriverList.Response> filterdNames) {
//        Log.e(TAG, "ADAPTER -- filterList: " + filterdNames );
        this.AssignmentList = filterdNames;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return AssignmentList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_driver_name, txt_driver_rating;
        CardView full_layout;
        Button btn_select;

        public MyViewHolder(View view) {
            super(view);

            txt_driver_name = view.findViewById(R.id.txt_driver_name);
            txt_driver_rating = view.findViewById(R.id.txt_driver_rating);
            full_layout = view.findViewById(R.id.full_layout);
            btn_select = view.findViewById(R.id.btn_select);


        }


    }



}
