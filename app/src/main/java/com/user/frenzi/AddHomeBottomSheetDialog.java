package com.user.frenzi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenzi.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;
import com.user.frenzi.Responce.ServerGeneralResponse;
import com.user.frenzi.adapter.AdapterMyAddressList;
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

public class AddHomeBottomSheetDialog extends BottomSheetDialogFragment {

    TextView btn_add;
    private static final String TAG = "AddHomeWork";
    String User_ID;
    RecyclerView recycler_address_list;
    private AdapterMyAddressList adapterMyAddressList;
    private List<ResponceFetchRecentAddressList.Response> RecentAddresses = new ArrayList<>();

    String address_id, status ="1";


    public static AddHomeBottomSheetDialog newInstance() {
        AddHomeBottomSheetDialog fragment = new AddHomeBottomSheetDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_add_home, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        recycler_address_list=contentView.findViewById(R.id.recycler_address_list);
        btn_add=contentView.findViewById(R.id.btn_add);
        adapterMyAddressList = new AdapterMyAddressList(requireContext(), RecentAddresses, new AdapterMyAddressList.OnItemClickListener() {
            @Override
            public void onItemClick(ResponceFetchRecentAddressList.Response item) {
                address_id = String.valueOf(item.getId());
                addHome();
            }
        });


        RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(requireActivity());
        recycler_address_list.setLayoutManager(mmLayoutManager);
        recycler_address_list.setItemAnimator(new DefaultItemAnimator());
        recycler_address_list.setAdapter(adapterMyAddressList);

        FetchRecentAddress();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void FetchRecentAddress() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(requireActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();
        SharedPreferences spp =requireActivity().getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
        User_ID = spp.getString(Constant.USER_ID, "");

//        Log.e(TAG, "User_id :: " + User_ID);

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


                    adapterMyAddressList.notifyDataSetChanged();
                    Log.e(TAG, "onResponse: Size Of Array ::" + RecentAddresses.size());

                } else  {

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

    private void addHome() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(requireActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();
        SharedPreferences spp =requireActivity().getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
        User_ID = spp.getString(Constant.USER_ID, "");

//        Log.e(TAG, "User_id :: " + User_ID);

        RequestBody Address_id = RequestBody.create(MediaType.parse("text/plain"), address_id);
        RequestBody Status = RequestBody.create(MediaType.parse("text/plain"), status);


        RestClient.getClient().AddHome(Address_id,Status).enqueue(new Callback<ServerGeneralResponse>() {
            @Override
            public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {

                Log.e(TAG, "onResponse: Code :" + response.body());
                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.errorBody());
                if (!String.valueOf(response.code()).equals("500")) {
                    if (response.body().getStatus().equals(200)) {
                        dialog.dismiss();

                        //  Log.e(TAG, "onResponse code: "+response.body().getResponse() );

                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();


                    } else {
                        dialog.dismiss();
//

                    }
                } else {
                    dialog.dismiss();
                  //  Toast.makeText(RatingEndActivity.this, "Something went wrong !!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ServerGeneralResponse> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

}