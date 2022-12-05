package com.user.frenzi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frenzi.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.GeoPoint;
import com.user.frenzi.Responce.ServerGeneralResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    private static final String TAG = "AddAddressActivity";
    ImageView iv_back, iv_home, iv_work ,iv_other ;
    TextView tv_add_address,tv_header;
    EditText edt_address, edt_address2, edt_pincode, edt_city, edt_title, edt_country;
    String user_id,title;
    String add_stat ="0";
    RelativeLayout rl_home,rl_work, rl_other;
    TextView tv_home,tv_work, tv_other;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }

        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        user_id = spp.getString(Constant.USER_ID, "");

        iv_back=findViewById(R.id.iv_back);
        edt_country=findViewById(R.id.edt_country);
        edt_city=findViewById(R.id.edt_city);
        edt_pincode=findViewById(R.id.edt_pincode);
        edt_address=findViewById(R.id.edt_address);
        edt_address2=findViewById(R.id.edt_address2);
        edt_title=findViewById(R.id.edt_title);
        tv_add_address=findViewById(R.id.tv_add_address);
        rl_home=findViewById(R.id.rl_home);
        rl_work=findViewById(R.id.rl_work);
        rl_other=findViewById(R.id.rl_other);
        tv_home=findViewById(R.id.tv_home);
        tv_work=findViewById(R.id.tv_work);
        tv_other=findViewById(R.id.tv_other);

        iv_home=findViewById(R.id.iv_home);
        iv_work=findViewById(R.id.iv_work);
        iv_other=findViewById(R.id.iv_other);

        tv_header=findViewById(R.id.tv_header);

        tv_header.setText("Add Address");

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rl_home.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {

                add_stat = "0";
                rl_home.setBackground(getResources().getDrawable(R.drawable.et_bg_pink));
                rl_work.setBackground(getResources().getDrawable(R.drawable.et_bg_white_grey_stroke));
                rl_other.setBackground(getResources().getDrawable(R.drawable.et_bg_white_grey_stroke));

                tv_home.setTextColor(getResources().getColor(R.color.dark_pink));
                tv_work.setTextColor(getResources().getColor(R.color.black));
                tv_other.setTextColor(getResources().getColor(R.color.black));

                iv_home.setImageDrawable(getResources().getDrawable(R.drawable.home_address_white));
                iv_work.setImageDrawable(getResources().getDrawable(R.drawable.work_address));
                iv_other.setImageDrawable(getResources().getDrawable(R.drawable.other_address));

            }
        });
        rl_work.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {

                add_stat = "1";
                rl_home.setBackground(getResources().getDrawable(R.drawable.et_bg_white_grey_stroke));
                rl_work.setBackground(getResources().getDrawable(R.drawable.et_bg_pink));
                rl_other.setBackground(getResources().getDrawable(R.drawable.et_bg_white_grey_stroke));

                tv_home.setTextColor(getResources().getColor(R.color.black));
                tv_work.setTextColor(getResources().getColor(R.color.dark_pink));
                tv_other.setTextColor(getResources().getColor(R.color.black));

                iv_home.setImageDrawable(getResources().getDrawable(R.drawable.home_address));
                iv_work.setImageDrawable(getResources().getDrawable(R.drawable.work_address_white));
                iv_other.setImageDrawable(getResources().getDrawable(R.drawable.other_address));


            }
        });

        rl_other.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {

                add_stat = "2";
                rl_home.setBackground(getResources().getDrawable(R.drawable.et_bg_white_grey_stroke));
                rl_work.setBackground(getResources().getDrawable(R.drawable.et_bg_white_grey_stroke));
                rl_other.setBackground(getResources().getDrawable(R.drawable.et_bg_pink));

                tv_home.setTextColor(getResources().getColor(R.color.black));
                tv_work.setTextColor(getResources().getColor(R.color.black));
                tv_other.setTextColor(getResources().getColor(R.color.dark_pink));

                iv_home.setImageDrawable(getResources().getDrawable(R.drawable.home_address));
                iv_work.setImageDrawable(getResources().getDrawable(R.drawable.work_address));
                iv_other.setImageDrawable(getResources().getDrawable(R.drawable.other_address_white));


            }
        });






        tv_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(edt_title.getText().toString().trim())) {
                    edt_country.setError("Please Enter Building Number");
                    return;

                } else if (TextUtils.isEmpty(edt_address.getText().toString().trim())) {
                    edt_address.setError("Please Enter Address");
                    return;

                } else if (TextUtils.isEmpty(edt_city.getText().toString().trim())) {
                    edt_city.setError("Please Enter City");
                    return;

                }else if (TextUtils.isEmpty(edt_pincode.getText().toString().trim())) {
                    edt_pincode.setError("Please Enter PinCode");
                    return;

                }else  if (TextUtils.isEmpty(edt_country.getText().toString().trim())) {
                    edt_country.setError("Please Enter Country");
                    return;
                }else {
                    String full_address =
                            edt_address.getText().toString().trim() + ", " +
                            edt_address2.getText().toString().trim() + ", " +
                            edt_city.getText().toString().trim() + ", " +
                            edt_pincode.getText().toString().trim() + ", " +
                            edt_country.getText().toString().trim();


                    getLocationFromAddress(full_address);
                }

            }
        });


    }



    public GeoPoint getLocationFromAddress(String strAddress) {

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
            String address_lat =  String.valueOf(location.getLatitude());
            String address_long =  String.valueOf(location.getLongitude());

            Log.e(TAG, "onClick:address_lat>   "+address_lat );
            Log.e(TAG, "onClick:address_long>   "+address_long );

            // p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
            //    (double) (location.getLongitude() * 1E6));

                AddAddress(address_lat, address_long, strAddress);



        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void AddAddress(String address_lat, String address_long, String strAddress) {

        ACProgressFlower dialog = new ACProgressFlower.Builder(AddAddressActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();


        RequestBody userId = RequestBody.create(MediaType.parse("txt/plain"), user_id);
        RequestBody post_title = RequestBody.create(MediaType.parse("txt/plain"),edt_title.getText().toString() );
        RequestBody post_address = RequestBody.create(MediaType.parse("txt/plain"), strAddress);
        RequestBody post_latitude = RequestBody.create(MediaType.parse("txt/plain"), address_lat);
        RequestBody post_longitude = RequestBody.create(MediaType.parse("txt/plain"), address_long);
        RequestBody address_status = RequestBody.create(MediaType.parse("txt/plain"), add_stat);


        RestClient.getClient().AddAddress(userId,post_title,post_address,post_latitude,post_longitude,address_status).
                enqueue(new Callback<ServerGeneralResponse>() {
                    @Override
                    public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {
                        Log.e(TAG, "onResponse: Code :" + response.body());
                        Log.e(TAG, "onResponse: " + response.message());
                        Log.e(TAG, "onResponse: " + response.errorBody());

                        assert response.body() != null;
                        if (response.body().getStatus().equals(200)) {
                            dialog.dismiss();

                            Toast.makeText(AddAddressActivity.this,response.body().getMessage(),Toast.LENGTH_SHORT ).show();
                            finish();

                        }else{
                            dialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(Call<ServerGeneralResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });

    }


}
