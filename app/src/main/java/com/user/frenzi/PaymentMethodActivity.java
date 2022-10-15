package com.user.frenzi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.google.gson.Gson;
import com.user.frenzi.Responce.ResponseNewRideDetails;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodActivity extends AppCompatActivity {

    String TAG = "payment";
    Button btn_payyyyyy;
    ImageView btn_back;
    String  ride_id,amount;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymeny_method);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }

        ride_id = getIntent().getStringExtra("ride_id");
        amount = getIntent().getStringExtra("amount");


        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        btn_payyyyyy=findViewById(R.id.btn_payyyyyy);

        SharedPreferences spp =getSharedPreferences(Constant.USER_PREF, MODE_PRIVATE);
        String PrefarenseStatus = spp.getString(Constant.DRIVER_PREF, "");



        btn_payyyyyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(PrefarenseStatus.equalsIgnoreCase("1")){
//                    Intent in = new Intent(PaymenyMethodActivity.this,DriverChoiceActivity .class);
//                    startActivity(in);
//                    finish();
//                }
//                else if(PrefarenseStatus.equalsIgnoreCase("0")){
                    Intent in = new Intent(PaymentMethodActivity.this, DriverDetailsAfterBookingActivity.class);
                    startActivity(in);
                    finish();
//                }

            }
        });
    }
    private void makePayment() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(PaymentMethodActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();


        RequestBody rideId = RequestBody.create(MediaType.parse("txt/plain"), ride_id);
        RequestBody Amount = RequestBody.create(MediaType.parse("txt/plain"), amount);
        RequestBody tranId = RequestBody.create(MediaType.parse("txt/plain"), "tran_id" );



        Gson gson = new Gson();
        RestClient.getClient().MakePayment(rideId,Amount,tranId)
                .enqueue(new Callback<ResponseNewRideDetails>() {
                    @Override
                    public void onResponse(Call<ResponseNewRideDetails> call, Response<ResponseNewRideDetails> response) {
                        Log.e(TAG, "onResponse: Url :" + response);
                        Log.e(TAG, "onResponse: " + response.code());
                        Log.e(TAG, "onResponse: " + response.message());
                        //   Log.e(TAG, "onResponse: " + response.body().getResponse().getRide_id());

                        assert response.body() != null;
                        if (response.body().getStatus().equals(200)) {
                            dialog.dismiss();


                            //Call API notify_preferred_driver
                       /*     popUpMessage();


                            Log.e(TAG, "onResponse:driver_id "+response.body().getResponse().getDriverId() );
                            Log.e(TAG, "onResponse:user_id "+response.body().getResponse().getUserId() );
                            Log.e(TAG,  gson.toJson(response.body()));
                            Log.e(TAG, "onResponse:ride_id "+response.body().getResponse().getRide_id());
                            Log.e(TAG, "onResponse:ride_id "+response.body().getResponse().getRideID());
                            String driver_id = String.valueOf(response.body().getResponse().getDriverId());
                            RideID = String.valueOf(response.body().getResponse().getRide_id());
*/                            Toast.makeText(PaymentMethodActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();




                        } else {

                            Toast.makeText(PaymentMethodActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

                            dialog.dismiss();
//

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseNewRideDetails> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }

}