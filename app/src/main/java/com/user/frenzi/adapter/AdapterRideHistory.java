package com.user.frenzi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.user.frenzi.Responce.ResponceFetchRideHistory;
import com.user.frenzi.Responce.ResponceFetchRideHistory;

import java.util.ArrayList;
import java.util.List;

public class AdapterRideHistory extends RecyclerView.Adapter<AdapterRideHistory.MyViewHolder> {

    private static final String TAG = "AdapterResidentsFamilyL";
    private Context mContext;
    private final AdapterRideHistory.OnItemClickListener listener;
    private List<ResponceFetchRideHistory.Response> AssignmentList;
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


    private Fragment callingFragment;
    int index = -1;


    public interface OnItemClickListener {
        void onItemClick(ResponceFetchRideHistory.Response item);

    }

    public AdapterRideHistory(Activity activity, Context mContext, List<ResponceFetchRideHistory.Response> AssignmentList, AdapterRideHistory.OnItemClickListener listener) {
        this.activity = activity;
        this.AssignmentList = AssignmentList;
        this.listener = listener;
        this.mContext = mContext;


    }

    @NonNull
    @Override
    public AdapterRideHistory.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                      int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ride_history, parent, false);
        return new AdapterRideHistory.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterRideHistory.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_pickup_add.setText(AssignmentList.get(position).getPickupAddress());
        holder.txt_drop_add.setText(AssignmentList.get(position).getDropAddress());
        holder.txt_pickup_time.setText(String.valueOf(AssignmentList.get(position).getStartTime()));
        holder.txt_drop_time.setText(String.valueOf(AssignmentList.get(position).getEndTime()));
        holder.txt_amount.setText("£"+ String.valueOf(AssignmentList.get(position).getAmount()));

//
//        holder.btn_select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                popUppayment();
//
//            }
//        });

//        holder.full_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                index = position;
//                notifyDataSetChanged();
//            }
//        });

//        if (index == position) {
//            holder.full_layout.setBackgroundColor(Color.parseColor("#2CD3D3D3"));
////            holder.txt_property_name.setTextColor(Color.BLACK);
//        } else {
//            holder.full_layout.setBackgroundColor(Color.parseColor("#ffffff"));
////            holder.txt_property_name.setTextColor(Color.RED);
//        }

    }


    public void filterList(ArrayList<ResponceFetchRideHistory.Response> filterdNames) {
//        Log.e(TAG, "ADAPTER -- filterList: " + filterdNames );
        this.AssignmentList = filterdNames;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return AssignmentList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_pickup_add, txt_drop_add, txt_pickup_time, txt_drop_time,btn,txt_amount;

        public MyViewHolder(View view) {
            super(view);

            txt_pickup_add = view.findViewById(R.id.txt_pickup_add);
            txt_drop_add = view.findViewById(R.id.txt_drop_add);
            txt_pickup_time = view.findViewById(R.id.txt_pickup_time);
            txt_drop_time = view.findViewById(R.id.txt_drop_time);
            btn=view.findViewById(R.id.btn);
            txt_amount=view.findViewById(R.id.txt_amount);

        }


    }
}