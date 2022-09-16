package com.user.frenzi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenzi.R;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;

import java.util.ArrayList;
import java.util.List;

public class AdapterSavedAddreslist extends RecyclerView.Adapter<AdapterSavedAddreslist.MyViewHolder>{
    private boolean isSelected = false;
    int index=-1;

    private static final String TAG = "AdapterResidentsFamilyL";
    private Context mContext;
    private final AdapterSavedAddreslist.OnItemClickListener listener;
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

    final private static int SPLASH_TIME_OUT = 1500;

    private Fragment callingFragment;



    public interface OnItemClickListener {
        void onItemClick(ResponceFetchRecentAddressList.Response item);

    }

    public AdapterSavedAddreslist(Context mContext, List<ResponceFetchRecentAddressList.Response> AssignmentList,  AdapterSavedAddreslist.OnItemClickListener listener) {
        this.AssignmentList = AssignmentList;
        this.listener = listener;
        this.mContext=mContext;



    }

    @NonNull
    @Override
    public AdapterSavedAddreslist.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saved_places, parent, false);
        return new AdapterSavedAddreslist.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterSavedAddreslist.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_address.setText(AssignmentList.get(position).getAddress());
//        holder.txt_date.setText(AssignmentList.get(position).getD());
        holder.full_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(AssignmentList.get(position));
//                index = position;
//                notifyDataSetChanged();
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
                holder.iv_address.setImageResource(R.drawable.star);
                break;
        }

     /*   if(AssignmentList.get(position).getHomeStatus().equals(1)){
            holder.selected_img.setVisibility(View.VISIBLE);
            holder.selected_img.setImageResource(R.drawable.home);
        }else{
            holder.selected_img.setVisibility(View.INVISIBLE);
        }
        if(AssignmentList.get(position).getWorkStatus().equals(1)){
            holder.selected_img.setVisibility(View.VISIBLE);
            holder.selected_img.setImageResource(R.drawable.bage);
        }else{
            holder.selected_img.setVisibility(View.INVISIBLE);
        }*/
//        if(AssignmentList.get(position).getHomeStatus().equals(1)){
//            holder.selected_img.setImageResource(R.drawable.home);
//        }
//        if(index==position){
//            holder.full_layout.setBackgroundColor(Color.parseColor("#191970"));
//            holder.txt_address.setTextColor(Color.WHITE);
//
//            //Shine Animation
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //Do something after 20 seconds
//                    handler.postDelayed(this, 1200);
//                    Animation animation = new TranslateAnimation(0, holder.full_layout.getWidth()+holder.shine.getWidth(),0, 0);
//                    animation.setDuration(600);
//                    animation.setFillAfter(false);
//                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
//                    holder.shine.startAnimation(animation);
//                }
//            }, 1200);
////                // This method will be executed once the timer is over
////                // Start your app main activity
//
//
//
//        }
//        else
//        {
//            holder.full_layout.setBackgroundColor(Color.parseColor("#ffffff"));
////            holder.txt_property_name.setTextColor(Color.RED);
//        }


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


        TextView txt_address,txt_address_2;
        RelativeLayout full_layout;
        ImageView selected_img;
        ImageView shine;
        ImageView iv_address;

        public MyViewHolder(View view) {
            super(view);

            txt_address = view.findViewById(R.id.txt_address);
            txt_address_2=view.findViewById(R.id.txt_address_2);
            full_layout=view.findViewById(R.id.full_layout);
            iv_address=view.findViewById(R.id.iv_address);
          //  selected_img=view.findViewById(R.id.selected_img);
         //   shine=view.findViewById(R.id.shine);


        }


    }
}



