package com.user.frenzi;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frenzi.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;
import com.user.frenzi.adapter.AdapterMyAddressList;
import com.user.frenzi.adapter.AdapterSavedAddreslist;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetSavedPlaces extends BottomSheetDialogFragment {

    TextView btn_close;
    private static final String TAG = "SavedPlaces";
    String User_ID;
    RecyclerView recycler_saved_address_list;
    private AdapterSavedAddreslist adapterSavedAddreslist;
    private List<ResponceFetchRecentAddressList.Response> RecentAddresses = new ArrayList<>();

    public static BottomSheetSavedPlaces newInstance() {
        BottomSheetSavedPlaces fragment = new BottomSheetSavedPlaces();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.bottom_sheet_saved_placews, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        recycler_saved_address_list = view.findViewById(R.id.recycler_saved_address_list);
        btn_close = view.findViewById(R.id.btn_close);
        adapterSavedAddreslist = new AdapterSavedAddreslist(requireContext(), RecentAddresses, new AdapterSavedAddreslist.OnItemClickListener() {
            @Override
            public void onItemClick(ResponceFetchRecentAddressList.Response item) {

            }
        });


        RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(requireActivity());
        recycler_saved_address_list.setLayoutManager(mmLayoutManager);
        recycler_saved_address_list.setItemAnimator(new DefaultItemAnimator());
        recycler_saved_address_list.setAdapter(adapterSavedAddreslist);
        FetchRecentAddress();

        btn_close.setOnClickListener(new View.OnClickListener() {
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
        SharedPreferences spp = requireActivity().getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
        User_ID = spp.getString(Constant.USER_ID, "");

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


                    adapterSavedAddreslist.notifyDataSetChanged();
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