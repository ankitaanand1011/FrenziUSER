package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.user.frenzi.Responce.ResponceFetchRidedetails;
import com.user.frenzi.Responce.ServerGeneralResponse;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingEndActivity extends AppCompatActivity {

    private static final String TAG = "RatingActivity";
    Button btn_done;
    ImageView btn_back;
    AlertDialog alertDialog;
    TextView tv_comments,tv_tip_1, tv_tip_2, tv_tip_3, tv_driver_name, tv_skip,txt_add;
    EditText edt_reviews, edt_custom_amt;
    String tip_amount = "0";
    RatingBar  rating;
    CircularImageView civ_driver;
    String ride_id,user_id,driver_id;

    String driver_name, vehicle_no, driver_phn;

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_end);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.MidnightBlue));
        }


        initControls();
    }

    private void initControls() {

        btn_done=findViewById(R.id.btn_done);
        tv_comments=findViewById(R.id.tv_comments);
        edt_reviews=findViewById(R.id.edt_reviews);
        btn_back=findViewById(R.id.btn_back);
        tv_tip_1=findViewById(R.id.tv_tip_1);
        tv_tip_2=findViewById(R.id.tv_tip_2);
        tv_tip_3=findViewById(R.id.tv_tip_3);
        txt_add=findViewById(R.id.txt_add);
        rating = findViewById(R.id.rating);
        civ_driver = findViewById(R.id.civ_driver);
        tv_driver_name = findViewById(R.id.tv_driver_name);
        edt_custom_amt = findViewById(R.id.edt_custom_amt);
        tv_skip = findViewById(R.id.tv_skip);

        functions();
    }

    private void functions() {

        ride_id = getIntent().getStringExtra("ride_id");
        user_id = getIntent().getStringExtra("user_id");
        driver_id = getIntent().getStringExtra("driver_id");

        Log.e(TAG, "functions: ride_id>"+ride_id );
        Log.e(TAG, "functions: user_id>"+user_id );
        Log.e(TAG, "functions: driver_id>"+driver_id );

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                if (tip_amount.equals("0")){
                    if(!edt_custom_amt.getText().toString().isEmpty()){
                        tip_amount = edt_custom_amt.getText().toString();
                    }else{
                        tip_amount = "0";
                    }
                }
                rateDriver();
            }
        });


      /*  tv_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_reviews.setVisibility(View.VISIBLE);
            }
        });*/

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                // editor.putString(Constant.DRIVER_PREF, "1");
                editor.putString(Constant.BOOKING, "no");
                editor.apply();*/
                Intent navi = new Intent(RatingEndActivity.this, MapScreen.class);
                startActivity(navi);
                finish();
            }
        });


        tv_tip_1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                tip_amount = "5";
                tv_tip_1.setBackground(getResources().getDrawable(R.drawable.circle_pink));
                tv_tip_2.setBackground(getResources().getDrawable(R.drawable.circle_white));
                tv_tip_3.setBackground(getResources().getDrawable(R.drawable.circle_white));
            }
        });

        tv_tip_2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                tip_amount = "10";
                tv_tip_1.setBackground(getResources().getDrawable(R.drawable.circle_white));
                tv_tip_2.setBackground(getResources().getDrawable(R.drawable.circle_pink));
                tv_tip_3.setBackground(getResources().getDrawable(R.drawable.circle_white));
            }
        });

        tv_tip_3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                tip_amount = "15";
                tv_tip_1.setBackground(getResources().getDrawable(R.drawable.circle_white));
                tv_tip_2.setBackground(getResources().getDrawable(R.drawable.circle_white));
                tv_tip_3.setBackground(getResources().getDrawable(R.drawable.circle_pink));
            }
        });

        FetchRideDetails();
    }

    private void rateDriver() {
       /* if(TextUtils.isEmpty(edt_email.getText().toString().trim())) {
            edt_email.setError("Please Enter Email");
            return;
        }else if(TextUtils.isEmpty(edt_otp_code.getText().toString().trim())) {
            edt_otp_code.setError("Please Enter OTP");
            return;
        }else if(TextUtils.isEmpty(edt_password.getText().toString().trim())) {
            edt_password.setError("Please Enter Password");
            return;
        }else {*/
        ACProgressFlower dialog = new ACProgressFlower.Builder(RatingEndActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();


        RequestBody userid = RequestBody.create(MediaType.parse("txt/plain"), user_id);
        RequestBody driverid = RequestBody.create(MediaType.parse("txt/plain"), driver_id);
        RequestBody rideid = RequestBody.create(MediaType.parse("txt/plain"), ride_id);
        RequestBody ratings = RequestBody.create(MediaType.parse("txt/plain"), String.valueOf(rating.getRating()));
        RequestBody review_comment = RequestBody.create(MediaType.parse("txt/plain"), edt_reviews.getText().toString());
        RequestBody tipamount = RequestBody.create(MediaType.parse("txt/plain"), tip_amount);

        RestClient.getClient().UserReviews(userid, driverid, rideid, ratings,
                review_comment, tipamount).enqueue(new Callback<ServerGeneralResponse>() {
            @Override
            public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {
                Log.e(TAG, "onResponse: Code :" + response.body());
                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.errorBody());
                if (!String.valueOf(response.code()).equals("500")) {
                    if (response.body().getStatus().equals(200)) {
                        dialog.dismiss();

                        //  Log.e(TAG, "onResponse code: "+response.body().getResponse() );

                    /*    SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        // editor.putString(Constant.DRIVER_PREF, "1");
                        editor.putString(Constant.BOOKING, "no");
                        editor.apply();*/
                        Toast.makeText(RatingEndActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent navi = new Intent(RatingEndActivity.this, MapScreen.class);
                        startActivity(navi);
                        finish();


                    } else {
                        dialog.dismiss();
//

                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(RatingEndActivity.this, "Something went wrong !!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerGeneralResponse> call, Throwable t) {

                dialog.dismiss();
            }
        });
    }

    private void FetchRideDetails() {
        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();




        RequestBody RideID = RequestBody.create(MediaType.parse("text/plain"), ride_id);


        RestClient.getClient().fetchRideDetails(RideID).enqueue(new Callback<ResponceFetchRidedetails>() {
            @Override
            public void onResponse(Call<ResponceFetchRidedetails> call, Response<ResponceFetchRidedetails> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();

                    ResponceFetchRidedetails listResponse = response.body();
                    driver_name = String.valueOf(response.body().getResponse().getDriverDetails().getName());
                    vehicle_no = String.valueOf(response.body().getResponse().getDriverDetails().getVehicleNo());
                    driver_phn = String.valueOf(response.body().getResponse().getDriverDetails().getPhone());

                    tv_driver_name.setText(driver_name);
                    txt_add.setText("Add a tip for "+driver_name);

                } else  {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponceFetchRidedetails> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }
}