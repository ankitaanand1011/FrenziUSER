package com.user.frenzi;

import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;
import com.user.frenzi.adapter.AdapterRecentAddressList;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Wheretogo extends AppCompatActivity {

    Button btn_next;
    String User_ID;
    //    RelativeLayout homeadd;
    ImageView btn_back;
    EditText edt_pickup, edt_drop;
    private AdapterRecentAddressList adapterRecentAddressList;
    private List<ResponceFetchRecentAddressList.Response> RecentAddresses = new ArrayList<>();
    private static final String TAG = "WhereToGo";
    RecyclerView recent_address_recycler;
    RelativeLayout btn_add_home, btn_add_work, btn_saved_address, rl_add_address;
    SaveRideData saveRideData;
    String pickup_lat, pickup_long, drop_lat, drop_long;
     double current_longitude;
     double current_latitude;
     int size;
     AlertDialog alertDialog;


    public void onStart() {
        if (SearchAddressActivity.mGetAddress != null && !SearchAddressActivity.mGetAddress.equals("")) {
            System.out.println("data ------>" + SearchAddressActivity.mGetAddress);
            edt_pickup.setText(SearchAddressActivity.mGetAddress);
        } else {
            //edt_pickup.setText("Stratton St, London W1J, UK");
            //  edt_pickup.setText("");
            edt_pickup.setHint("Enter Pickup Location");
        }
        if (SearchAddressActivity.mGetAddress2 != null && !SearchAddressActivity.mGetAddress2.equals("")) {
            System.out.println("data ------>" + SearchAddressActivity.mGetAddress2);
            edt_drop.setText(SearchAddressActivity.mGetAddress2);
        } else {
          // edt_drop.setText("Soho Square, London, UK");
             //   edt_drop.setText("");
           edt_drop.setHint("Enter Drop Location");


        }

        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheretogo);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }

        initControls();

    }

    private void initControls() {

        btn_back = findViewById(R.id.btn_back);
        edt_pickup = findViewById(R.id.edt_pickup);
        edt_drop = findViewById(R.id.edt_drop);
        btn_next = findViewById(R.id.btn_next);
        btn_add_home = findViewById(R.id.btn_add_home);
        btn_add_work = findViewById(R.id.btn_add_work);
        btn_saved_address = findViewById(R.id.btn_saved_address);
        rl_add_address = findViewById(R.id.rl_add_address);
        recent_address_recycler = findViewById(R.id.recent_address_recycler);

        functions();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void functions() {
        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        User_ID = spp.getString(Constant.USER_ID, "");

        current_latitude =getIntent().getDoubleExtra("current_latitude",0);
        current_longitude =getIntent().getDoubleExtra("current_longitude",0);



        edt_pickup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG, "onClick:edt_pickup " );
                Intent intent = new Intent(Wheretogo.this, SearchAddressActivity.class);
                intent.putExtra("data", "address_1");
                startActivity(intent);
                return false;
            }
        });
        edt_drop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG, "onClick:edt_drop " );
                Intent intent = new Intent(Wheretogo.this, SearchAddressActivity.class);
                intent.putExtra("data", "address_2");
                startActivity(intent);
                return false;
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_pickup.getText().toString().trim())
                        || edt_drop.getText().toString().trim().equals("Enter Pickup Location")) {
                    edt_pickup.setError("Please select Pickup location !");
                    return;
                } else if (TextUtils.isEmpty(edt_drop.getText().toString().trim())
                        || edt_pickup.getText().toString().trim().equals("Enter Drop Location")) {
                    edt_drop.setError("Please select Drop location !");
                    return;
                } else {

                    getLocationFromAddress1(edt_pickup.getText().toString().trim());
                    getLocationFromAddress2(edt_drop.getText().toString().trim());


                    Intent hoadd = new Intent(Wheretogo.this, ChooseRideActivity.class);
                    hoadd.putExtra("drop_add",edt_drop.getText().toString());
                    hoadd.putExtra("pickup_add",edt_pickup.getText().toString());
                    hoadd.putExtra("pickup_lat",pickup_lat);
                    hoadd.putExtra("pickup_long",pickup_long);
                    hoadd.putExtra("drop_lat",drop_lat);
                    hoadd.putExtra("drop_long",drop_long);
                    hoadd.putExtra("current_latitude",current_latitude);
                    hoadd.putExtra("current_longitude",current_longitude);


                    startActivity(hoadd);
                }


            }
        });

        btn_add_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: clicked ");

                if (size>0) {
                    AddHomeBottomSheetDialog bottomSheetDialog = AddHomeBottomSheetDialog.newInstance();
                    bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
                }else{
                    Toast.makeText(Wheretogo.this,"Please Add Address",Toast.LENGTH_LONG).show();


                }
            }
        });
        btn_add_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Wheretogo.this,"Work in Progress",Toast.LENGTH_LONG).show();
            }
        });
    /*    btn_saved_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   if (RecentAddresses.size()>=0) {
                BottomSheetSavedPlaces bottomSheetDialog = BottomSheetSavedPlaces.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

             *//*   }else{
                    Toast.makeText(Wheretogo.this,"Please Add Address",Toast.LENGTH_LONG).show();

                }*//*
            }
        });*/
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        rl_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hoadd=new Intent(Wheretogo.this,AddAddressActivity.class);
                startActivity(hoadd);


            }
        });

        adapterRecentAddressList = new AdapterRecentAddressList(Wheretogo.this, RecentAddresses, User_ID, new AdapterRecentAddressList.OnItemClickListener() {
            @Override
            public void onItemClick(ResponceFetchRecentAddressList.Response item) {

                Log.e(TAG, "onItemClick: "+item.address );
                String selected_address = item.address;

                popUpSetAddress(selected_address);
            }
        });
        RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(this);
        recent_address_recycler.setLayoutManager(mmLayoutManager);
        recent_address_recycler.setItemAnimator(new DefaultItemAnimator());
        recent_address_recycler.setAdapter(adapterRecentAddressList);

        FetchRecentAddress();

        /*RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(requireActivity());
        recycler_saved_address_list.setLayoutManager(mmLayoutManager);
        recycler_saved_address_list.setItemAnimator(new DefaultItemAnimator());
        recycler_saved_address_list.setAdapter(adapterSavedAddreslist);
        FetchRecentAddress();*/

    }

    private void popUpSetAddress(String selected_address) {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_set_address, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


        TextView txt_pick =promptsView.findViewById(R.id.txt_pick);
        TextView txt_drop =promptsView.findViewById(R.id.txt_drop);
        ImageView iv_close =promptsView.findViewById(R.id.iv_close);
        // TextView tv_cancel_msg =promptsView.findViewById(R.id.tv_cancel_msg);


        // tv_cancel_msg.setText("Cancel your ride with "+driver_name);
        alertDialogBuilder.setView(promptsView);
        txt_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                edt_pickup.setText(selected_address);

            }
        });
        txt_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                edt_drop.setText(selected_address);
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        FetchRecentAddress();
    }

    public void FetchRecentAddress() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();


        Log.e(TAG, "User_id :: " + User_ID);

        RequestBody UserId = RequestBody.create(MediaType.parse("text/plain"), User_ID);


        RestClient.getClient().fetchRecentaAddresses(UserId).enqueue(new Callback<ResponceFetchRecentAddressList>() {
            @Override
            public void onResponse(Call<ResponceFetchRecentAddressList> call, Response<ResponceFetchRecentAddressList> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                RecentAddresses.clear();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();

                    ResponceFetchRecentAddressList listResponse = response.body();

                    assert listResponse != null;

                    for (ResponceFetchRecentAddressList.Response list : listResponse.getResponse()) {
                        Log.e(TAG, "onResponse: block LIst " + list);
                        RecentAddresses.add(list);
                        Log.e(TAG, "onResponse: Blick List :" + list);


                    }


                    adapterRecentAddressList.notifyDataSetChanged();
                    Log.e(TAG, "onResponse: Size Of Array ::" + RecentAddresses.size());
                    size = RecentAddresses.size();

                } else {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponceFetchRecentAddressList> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    public GeoPoint getLocationFromAddress1(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
             pickup_lat =  String.valueOf(location.getLatitude());
             pickup_long =  String.valueOf(location.getLongitude());

            Log.e(TAG, "onClick:pickup_lat>   "+pickup_lat );
            Log.e(TAG, "onClick:pickup_long>   "+pickup_long );

           // p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                //    (double) (location.getLongitude() * 1E6));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public GeoPoint getLocationFromAddress2(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            drop_lat =  String.valueOf(location.getLatitude());
            drop_long =  String.valueOf(location.getLongitude());
            Log.e(TAG, "onClick:drop_lat>   "+drop_lat );
            Log.e(TAG, "onClick:drop_long>   "+drop_long );

           // p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                //    (double) (location.getLongitude() * 1E6));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}