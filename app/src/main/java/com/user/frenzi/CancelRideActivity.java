package com.user.frenzi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.frenzi.R;

public class CancelRideActivity extends AppCompatActivity {

     Button btn_cancel_ride;
     ImageButton img_cross;




    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_ride);

        img_cross=findViewById(R.id.img_cross);
        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.MidnightBlue));
        }

        btn_cancel_ride=findViewById(R.id.btn_cancel_ride);
        btn_cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ride=new Intent(CancelRideActivity.this,ThankYouActivity.class);
                startActivity(ride);
            }
        });

    }
}