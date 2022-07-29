package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.google.android.material.snackbar.Snackbar;
import com.user.frenzi.Responce.FPAssResponse;
import com.user.frenzi.Responce.ResponseRegistration;
import com.user.frenzi.Responce.ServerGeneralResponse;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final  String TAG = "RegisterActivity";
    Button button_register;
    ImageView btn_back;
    EditText edt_name,edt_address,edt_email,edt_phone,edt_password;
    SwitchCompat switch_accept;
    AlertDialog alertDialog;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initControls();


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }




    }

    private void initControls() {

        btn_back=findViewById(R.id.btn_back);
        edt_name=findViewById(R.id.edt_name);
        edt_address=findViewById(R.id.edt_address);
        edt_email=findViewById(R.id.edt_email);
        edt_phone=findViewById(R.id.edt_phone);
        edt_password=findViewById(R.id.edt_password);
        switch_accept=findViewById(R.id.switch_accept);
        button_register=findViewById(R.id.button_register);




        functions();
    }

    private void functions() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registration();
            }
        });


    }

    private void Registration() {
        if (TextUtils.isEmpty(edt_name.getText().toString().trim())) {
            edt_name.setError("Please Enter FullName");
            return;
        } else if (TextUtils.isEmpty(edt_email.getText().toString().trim())) {
            edt_email.setError("Please Enter Email");
            return;
        } else if (TextUtils.isEmpty(edt_password.getText().toString().trim())) {
            edt_password.setError("Please Enter PassWord");
            return;
        } else if (TextUtils.isEmpty(edt_phone.getText().toString().trim())) {
            edt_phone.setError("Please Enter Mobile Number");
            return;
        } else if (TextUtils.isEmpty(edt_address.getText().toString().trim())) {
            edt_address.setError("Please Enter Address");
            return;
        }  else {
            ACProgressFlower dialog = new ACProgressFlower.Builder(RegisterActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .fadeColor(Color.BLACK).build();
            dialog.show();


            RequestBody name = RequestBody.create(MediaType.parse("txt/plain"), edt_name.getText().toString().trim());
            RequestBody email = RequestBody.create(MediaType.parse("txt/plain"), edt_email.getText().toString().trim());
            RequestBody phone = RequestBody.create(MediaType.parse("txt/plain"), edt_phone.getText().toString().trim());
            RequestBody password = RequestBody.create(MediaType.parse("txt/plain"), edt_password.getText().toString().trim());
            RequestBody address = RequestBody.create(MediaType.parse("txt/plain"), edt_address.getText().toString().trim());
            RequestBody reg_type = RequestBody.create(MediaType.parse("txt/plain"), "1");

            Log.e(TAG, "Registration: name >>"+ edt_name.getText().toString() );
            RestClient.getClient().UserRegistration(name, email, phone, password, address,reg_type).enqueue(new Callback<ResponseRegistration>() {
                @Override
                public void onResponse(Call<ResponseRegistration> call, Response<ResponseRegistration> response) {
                    Log.e(TAG, "onResponse: Code :" + response.body());
                    Log.e(TAG, "onResponse: " + response.code());
                    Log.e(TAG, "onResponse: " + response.message());
                    Log.e(TAG, "onResponse: " + response.errorBody());
                    assert response.body() != null;
                    if (response.body().getStatus().equals(200)) {
                        dialog.dismiss();


                  /*      Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, response.body().getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();*/


//                      SAVE  DATA TO LOCAL STORAGE
                        SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                       // assert response.body() != null;

                        editor.putString(Constant.USER_NAME, response.body().getResponse().getName());
                        editor.putString(Constant.USER_ID, String.valueOf(response.body().getResponse().getUser_id()));
                        editor.putString(Constant.USER_ADDRESS, response.body().getResponse().getAddress());
                        editor.putString(Constant.USER_MAIL, response.body().getResponse().getEmail());
                        editor.putString(Constant.USER_PHONE, response.body().getResponse().getPhone());
                        editor.putString(Constant.USER_IMAGE, response.body().getResponse().getImage());
                        editor.apply();
                    //    showDialogRegistrationDone(RegisterActivity.this);

                        sendEmailforOtpDialog(response.body().getResponse().getEmail());





                    } else {
                        dialog.dismiss();
                     /*   Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, response.body().getMessage(), Snackbar.LENGTH_LONG);

                        snackbar.show();*/

                    }
                }

                @Override
                public void onFailure(Call<ResponseRegistration> call, Throwable t) {
                    Log.e(TAG, "onFailure: " );

                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG);

                    snackbar.show();*/
                    dialog.dismiss();
                }
            });
        }
    }

    private void sendEmailforOtpDialog(String email) {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.otp_sent_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        Button btn_cancel =promptsView.findViewById(R.id.btn_cancel);
        Button btn_done =promptsView.findViewById(R.id.btn_done);
        EditText edt_f_email =promptsView.findViewById(R.id.edt_f_email);





        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        edt_f_email.setText(email);

        btn_done.setOnClickListener(v -> {
            alertDialog.dismiss();
            send_otp_mail(edt_f_email);

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();



    }


    private void send_otp_mail( EditText  edt_f_email ) {
        if(TextUtils.isEmpty(edt_f_email.getText().toString().trim())) {
            edt_f_email.setError("Please Enter Email");
            return;
        }else {
            ACProgressFlower dialog = new ACProgressFlower.Builder(RegisterActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .fadeColor(Color.BLACK).build();
            dialog.show();


            RequestBody email_id = RequestBody.create(MediaType.parse("txt/plain"), edt_f_email.getText().toString().trim());

            RestClient.getClient().SendOtpToEmail(email_id).enqueue(new Callback<ServerGeneralResponse>() {
                @Override
                public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {
                    Log.e(TAG, "onResponse: Code :" + response.body());
                    Log.e(TAG, "onResponse: " + response.code());
                    Log.e(TAG, "onResponse: " + response.errorBody());
                    if (!String.valueOf(response.code()).equals("500")) {
                        if (response.body().getStatus().equals(200)) {
                            dialog.dismiss();
                          //  alertDialog.dismiss();

                        //    Log.e(TAG, "onResponse code: "+response.body().getResponse() );


                            Intent reg=new Intent(RegisterActivity.this,VerifyOtpActivity.class);
                            reg.putExtra("email",  edt_f_email.getText().toString());
                            startActivity(reg);




                        } else {
                            dialog.dismiss();
//

                        }
                    }else{
                        dialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"Something went wrong !!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerGeneralResponse> call, Throwable t) {

                    dialog.dismiss();
                }
            });
        }

    }
}