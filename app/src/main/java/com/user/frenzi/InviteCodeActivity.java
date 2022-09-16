package com.user.frenzi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;

public class InviteCodeActivity extends AppCompatActivity {

    Button btn_invite;
    ImageView btn_back;
    TextView tv_refer_code;


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        btn_back=findViewById(R.id.btn_back);
        tv_refer_code=findViewById(R.id.tv_refer_code);

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

        btn_invite=findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Intent invite=new Intent(InviteCodeActivity.this,InviteFriendsActivity.class);

                startActivity(invite);*/

                shareImageWithText();

            }
        });

    }

    void shareImageWithText(){
     //   Uri contentUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_launcher");

        StringBuilder msg = new StringBuilder();
        msg.append("Hey, Download this Frenzi app ");
        msg.append("\n");
        msg.append("https://play.google.com/store/apps/details?id=Your_Package_Name"); //example :com.package.name
        msg.append("\n");
        msg.append("with my referral code "+tv_refer_code.getText().toString());
       // if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        //    shareIntent.setType("*/*");
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.toString());
          //  shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            try {
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
            }
        }
}