package com.user.frenzi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.frenzi.R;

public class PaymenyMethodActivity extends AppCompatActivity {

    Button btn_payyyyyy;
    ImageView btn_back;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymeny_method);


        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }
        btn_payyyyyy=findViewById(R.id.btn_payyyyyy);
//Get Local Databse data
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
                    Intent in = new Intent(PaymenyMethodActivity.this, DriverDetailsAfterBookingActivity.class);
                    startActivity(in);
                    finish();
//                }

            }
        });
    }
}