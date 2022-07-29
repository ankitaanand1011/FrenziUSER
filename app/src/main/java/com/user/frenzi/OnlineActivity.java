package com.user.frenzi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.frenzi.R;

public class OnlineActivity extends AppCompatActivity {

    Button button_accept;


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }
        button_accept=findViewById(R.id.button_accept);
        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent buacc=new Intent(OnlineActivity.this,Pikup.class);
                startActivity(buacc);
            }
        });


    }
}