package com.user.frenzi;

import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.frenzi.R;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;
import com.user.frenzi.adapter.AdapterRecentAddressList;


import java.util.ArrayList;
import java.util.List;
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
    EditText edt_wheretoGo, edt_current_location;
    private AdapterRecentAddressList adapterRecentAddressList;
    private List<ResponceFetchRecentAddressList.Response> RecentAddresses = new ArrayList<>();
    private static final String TAG = "WhereToGo";
    RecyclerView recent_address_recycler;
    RelativeLayout btn_add_home, btn_add_work, btn_saved_address;

    public void onStart() {
        if (SearchAddressActivity.mGetAddress != null && !SearchAddressActivity.mGetAddress.equals("")) {
            System.out.println("data ------>" + SearchAddressActivity.mGetAddress);
            edt_wheretoGo.setText(SearchAddressActivity.mGetAddress);
        } else {
            edt_wheretoGo.setText("");
            edt_wheretoGo.setHint("Enter Pickup Location");
        }
        if (SearchAddressActivity.mGetAddress2 != null && !SearchAddressActivity.mGetAddress2.equals("")) {
            System.out.println("data ------>" + SearchAddressActivity.mGetAddress2);
            edt_current_location.setText(SearchAddressActivity.mGetAddress2);
        } else {
            edt_current_location.setText("");
            edt_current_location.setHint("Enter Drop Location");


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

        btn_back = findViewById(R.id.btn_back);
        edt_wheretoGo = findViewById(R.id.edt_wheretoGo);
        edt_current_location = findViewById(R.id.edt_current_location);
        btn_next = findViewById(R.id.btn_next);
        btn_add_home = findViewById(R.id.btn_add_home);
        btn_add_work = findViewById(R.id.btn_add_work);
        btn_saved_address = findViewById(R.id.btn_saved_address);
        btn_add_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: clicked ");
                AddHomeBottomSheetDialog bottomSheetDialog = AddHomeBottomSheetDialog.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
        btn_add_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_saved_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSavedPlaces bottomSheetDialog = BottomSheetSavedPlaces.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edt_wheretoGo.setText("Test");
        edt_current_location.setText("Test");

        edt_wheretoGo.requestFocus();
        edt_wheretoGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(Wheretogo.this,MonicaWhereToGoActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(Wheretogo.this, SearchAddressActivity.class);
                intent.putExtra("data", "address_1");
                startActivity(intent);
            }
        });
        edt_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wheretogo.this, SearchAddressActivity.class);
                intent.putExtra("data", "address_2");
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }

//        homeadd=findViewById(R.id.homeadd);
        recent_address_recycler = findViewById(R.id.recent_address_recycler);

        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        User_ID = spp.getString(Constant.USER_ID, "");


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_current_location.getText().toString().trim()) || edt_current_location.getText().toString().trim().equals("Enter Pickup Location")) {
                    edt_current_location.setError("Please select Pickup location !");
                    return;
                } else if (TextUtils.isEmpty(edt_wheretoGo.getText().toString().trim()) || edt_current_location.getText().toString().trim().equals("Enter Drop Location")) {
                    edt_wheretoGo.setError("Please select drop location !");
                    return;
                } else {
                    Intent hoadd = new Intent(Wheretogo.this, ChooseRideActivity.class);
                    startActivity(hoadd);
                }


            }
        });

//        homeadd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent hoadd=new Intent(Wheretogo.this,ChooseRideActivity.class);
//                startActivity(hoadd);
//
//
//            }
//        });
//        edt_wheretoGo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent hoadd=new Intent(Wheretogo.this,MonicaWhereToGoActivity.class);
//                startActivity(hoadd);
//
//
//            }
//        });

        adapterRecentAddressList = new AdapterRecentAddressList(getApplicationContext(), RecentAddresses, new AdapterRecentAddressList.OnItemClickListener() {
            @Override
            public void onItemClick(ResponceFetchRecentAddressList.Response item) {

            }
        });
        RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(this);
        recent_address_recycler.setLayoutManager(mmLayoutManager);
        recent_address_recycler.setItemAnimator(new DefaultItemAnimator());
        recent_address_recycler.setAdapter(adapterRecentAddressList);

        FetchRecentAddress();
    }


    private void FetchRecentAddress() {

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

}