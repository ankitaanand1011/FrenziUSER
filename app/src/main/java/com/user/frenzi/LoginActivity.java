package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.user.frenzi.Responce.FPAssResponse;
import com.user.frenzi.Responce.LoginResponce;
import com.google.android.material.snackbar.Snackbar;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button btn_login;
    TextView account,tv_forgot_password;
    EditText edt_user_name,edt_password;
    private static final String TAG = "MainActivity";
    CoordinatorLayout coordinatorLayout;
    AlertDialog alertDialog;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }

        edt_password=findViewById(R.id.edt_password);
        edt_user_name=findViewById(R.id.edt_user_name);
        btn_login=findViewById(R.id.btn_login);
        account=findViewById(R.id.account);
        tv_forgot_password=findViewById(R.id.tv_forgot_password);
        coordinatorLayout=findViewById(R.id.coordinatorLayout);
        //Clear all checksheet data On Home Page
        SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
        sp.edit().remove(Constant.USER_ID)
                .remove(Constant.USER_NAME)
                .remove(Constant.USER_ADDRESS)
                .remove(Constant.USER_MAIL)
                .apply();

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Type="1";
                Login(Type);
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp();
            }
        });
    }

    private void popUp() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.f_pass_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        Button btn_cancel =promptsView.findViewById(R.id.btn_cancel);
        Button btn_done =promptsView.findViewById(R.id.btn_done);
        EditText edt_f_email =promptsView.findViewById(R.id.edt_f_email);





        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });



        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forgotPassword(edt_f_email, alertDialog);

            }
        });


        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    private void forgotPassword( EditText  edt_f_email , AlertDialog  alertDialog) {
        if(TextUtils.isEmpty(edt_f_email.getText().toString().trim())) {
            edt_f_email.setError("Please Enter Email");
            return;
        }else {
            ACProgressFlower dialog = new ACProgressFlower.Builder(LoginActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .fadeColor(Color.BLACK).build();
            dialog.show();


            RequestBody email_id = RequestBody.create(MediaType.parse("txt/plain"), edt_f_email.getText().toString().trim());

            RestClient.getClient().ForgetPassword(email_id).enqueue(new Callback<FPAssResponse>() {
                @Override
                public void onResponse(Call<FPAssResponse> call, Response<FPAssResponse> response) {
                    Log.e(TAG, "onResponse: Code :" + response.body());
                    Log.e(TAG, "onResponse: " + response.code());
                    Log.e(TAG, "onResponse: " + response.errorBody());
                    if (!String.valueOf(response.code()).equals("500")) {
                        if (response.body().getStatus().equals(200)) {
                            dialog.dismiss();
                            alertDialog.dismiss();

                            Log.e(TAG, "onResponse code: "+response.body().getResponse() );


                            Intent in=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                            in.putExtra("email_id",edt_f_email.getText().toString());
                            startActivity(in);
                            // finish();



                        } else {
                            dialog.dismiss();
//

                        }
                    }else{
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Something went wrong !!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<FPAssResponse> call, Throwable t) {

                    dialog.dismiss();
                }
            });
        }

    }

    private void Login(String type) {
        if(TextUtils.isEmpty(edt_user_name.getText().toString().trim())) {
            edt_user_name.setError("Please Enter Username");
            return;
        }else if(TextUtils.isEmpty(edt_password.getText().toString().trim())) {
            edt_password.setError("Please Enter PassWord");
            return;
        }else {
            ACProgressFlower dialog = new ACProgressFlower.Builder(LoginActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .fadeColor(Color.BLACK).build();
            dialog.show();


            RequestBody email_id = RequestBody.create(MediaType.parse("txt/plain"), edt_user_name.getText().toString().trim());
            RequestBody password = RequestBody.create(MediaType.parse("txt/plain"), edt_password.getText().toString().trim());
            RequestBody Type = RequestBody.create(MediaType.parse("txt/plain"), type);

            RestClient.getClient().Login(email_id, password,Type).enqueue(new Callback<LoginResponce>() {
                @Override
                public void onResponse(Call<LoginResponce> call, Response<LoginResponce> response) {
                    Log.e(TAG, "onResponse: Code :" + response.body());
                    Log.e(TAG, "onResponse: " + response.code());
                    Log.e(TAG, "onResponse: " + response.message());
                    Log.e(TAG, "onResponse: " + response.errorBody());
                    if (response.body().getStatus().equals(200)) {
                        dialog.dismiss();



                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, response.body().getMessage(), Snackbar.LENGTH_LONG);

                    snackbar.show();



////                         SAVE  DATA TO LOCAL STORAGE
                        SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        assert response.body() != null;

                        editor.putString(Constant.USER_NAME, response.body().getResponse().getUserName());
                        editor.putString(Constant.USER_ID, String.valueOf(response.body().getResponse().getUserId()));
                        editor.putString(Constant.USER_ADDRESS, response.body().getResponse().getAddress());
                        editor.putString(Constant.USER_MAIL, response.body().getResponse().getEmail());
                        editor.putString(Constant.USER_IMAGE, response.body().getResponse().getUserImage());
                        editor.apply();
//                        Intent intent=new Intent(MainActivity.this,VerifyOtpActivity.class);
//                        startActivity(intent);
//                        finish();

                        Intent intent=new Intent(LoginActivity.this,MapScreen.class);
                        startActivity(intent);
                        finish();

//                    Toast.makeText(LoginPage.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        ProfileFragment profileFragment = new ProfileFragment();
//
//
//                        assert getFragmentManager() != null;
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.container_main, profileFragment)
//                                .addToBackStack(null)
//                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                .commit();

                    }else{
                        dialog.dismiss();
//                        Toast.makeText(MainActivity.this,"Wrong username or password !!", Toast.LENGTH_SHORT).show();
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, response.body().getMessage(), Snackbar.LENGTH_LONG);

                        snackbar.show();

                    }
                }

                @Override
                public void onFailure(Call<LoginResponce> call, Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, t.getMessage(), Snackbar.LENGTH_LONG);

                    snackbar.show();                    dialog.dismiss();
                }
            });
        }
    }

}