package com.user.frenzi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.user.frenzi.Responce.ResponseExtraCharges;
import com.user.frenzi.Responce.ResponseRideStatus;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyTwoActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    final private static int SPLASH_TIME_OUT = 5000;

    String TAG = "EmergencyTwo";

    RelativeLayout txt_call;
    ImageView btn_back;

    LatLng a = null;
    private GoogleMap mMap;
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;
    String ride_id,amount,driver_id,user_id;
    AlertDialog alertDialog;
    ACProgressFlower dialog;
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_two);
        ride_id = getIntent().getStringExtra("ride_id");
        amount = getIntent().getStringExtra("amount");
        user_id = getIntent().getStringExtra("user_id");
        driver_id = getIntent().getStringExtra("driver_id");

        Log.e(TAG, "onCreate: user_id>"+user_id );
        Log.e(TAG, "onCreate: driver_id>"+driver_id );
        Log.e(TAG, "onCreate: amount>"+amount );
        Log.e(TAG, "onCreate: ride_id>"+ride_id );

        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        a = new LatLng(53.801277, -1.548567);

         dialog = new ACProgressFlower.Builder(EmergencyTwoActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();


      //  popUpAdditionalCharges();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
//                    getMyLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng pos = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18.0f));
        if(locationPermission) {
//            getMyLocation();
        }
//        googleMap.addMarker(new MarkerOptions().position(a).title("Me").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_i)));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                a, 18f);
        mMap.animateCamera(cameraUpdate);
        MapsInitializer.initialize(this);
//        addCustomMarker();

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void addCustomMarker() {
        if (mMap == null) {
            return;
        }

        // adding a marker on map with image from  drawable
//        mMap.addMarker(new MarkerOptions()
//                .position(a)
//                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.map_i))));
    }


    private void popUpAdditionalCharges(String drop_charge, String toll_charge, String luggage_charge) {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.extra_earning_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        // Button btn_cancel =promptsView.findViewById(R.id.btn_cancel);
        Button btn_done =promptsView.findViewById(R.id.btn_done);
        //Button btn_skip =promptsView.findViewById(R.id.btn_skip);
        TextView edt_drop_charges =promptsView.findViewById(R.id.edt_drop_charges);
        TextView edt_toll_charges =promptsView.findViewById(R.id.edt_toll_charges);
        TextView edt_luggage_charges =promptsView.findViewById(R.id.edt_luggage_charges);
        edt_drop_charges.setText("£"+drop_charge);
        edt_toll_charges.setText("£"+toll_charge);
        edt_luggage_charges.setText("£"+luggage_charge);


//        TextView txt_cancel = (TextView) promptsView.findViewById(R.id.txt_cancel);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //ride_arrived();



                popUpThankYou();

            }
        });



        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();

    }


    private void popUpThankYou() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(EmergencyTwoActivity.this);
        View promptsView = li.inflate(R.layout.popup_thankyou, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmergencyTwoActivity.this
        );

        TextView tv_next = promptsView.findViewById(R.id.tv_next);
        TextView tv_amount = promptsView.findViewById(R.id.tv_amount);

        tv_amount.setText("£ "+amount);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Intent intent2 = new Intent(EmergencyTwoActivity.this, RatingEndActivity.class);

                intent2.putExtra("ride_id",ride_id);
                intent2.putExtra("user_id", user_id);
                intent2.putExtra("driver_id", driver_id);
                startActivity(intent2);
                finish();

            }
        });




        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();



        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                CheckRideStatus();

            }
        }, 2000);

    }

    private void CheckRideStatus() {
       // dialog.show();
        RequestBody RideID = RequestBody.create(MediaType.parse("text/plain"), ride_id);


        RestClient.getClient().checkRideStatus(RideID).enqueue
                (new Callback<ResponseRideStatus>() {
                    @Override
                    public void onResponse(Call<ResponseRideStatus> call,
                                           Response<ResponseRideStatus> response) {
                        Log.e(TAG, "onResponse CheckRideStatus 2 : " + response.code());
                        Log.e(TAG, "onResponse CheckRideStatus  2: " + response.isSuccessful());
                        // ppDialog.dismiss();
                        assert response.body() != null;
                        if (response.body().getStatus().equals(200)) {
                            //    ll_hide_layout.setVisibility(View.VISIBLE);

                            if(response.body().getRide_status().equals("FINISHED")){

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                       // popUpThankYou();


                                        extra_pay();
                                    }
                                }, SPLASH_TIME_OUT);



                            }

                        } else
                        {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseRideStatus> call, Throwable t)
                    {
                        Log.e(TAG, "onFailure 2: " + t.getMessage());
                        // dialog.dismiss();
                    }
                });
    }


    private void extra_pay() {

        dialog.show();
        RequestBody rideId = RequestBody.create(MediaType.parse("txt/plain"), ride_id);


        RestClient.getClient().FetchExtraPay(rideId)
                .enqueue(new Callback<ResponseExtraCharges>() {
                    @Override
                    public void onResponse(Call<ResponseExtraCharges> call, Response<ResponseExtraCharges> response) {
                        if (response.body().getStatus().equals(200)) {
                            Log.e(TAG, "onResponse extra_pay : "+  response.code());
                           dialog.dismiss();


                            if (!response.body().getResponse().getDrop_off_charges().equals("0") ||
                                    !response.body().getResponse().getToll_charges().equals("0") ||
                                    !response.body().getResponse().getLuggage_charges().equals("0"))
                            {

                                        String drop_charge = response.body().getResponse().getDrop_off_charges();
                                        String toll_charge = response.body().getResponse().getToll_charges();
                                        String luggage_charge = response.body().getResponse().getLuggage_charges();

                                        popUpAdditionalCharges(drop_charge,toll_charge,luggage_charge);


                            }else{

                                popUpThankYou();
                            }

                            //  Toast.makeText(VerifyOtpActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();


                            //  startRide(dialog);




                           /* Toast.makeText(DropOffActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent in=new Intent(DropOffActivity.this,RatingActivity.class);
                            in.putExtra("ride_id",ride_id);
                            in.putExtra("user_id",userID);
                            in.putExtra("driver_id",driverID);

                            startActivity(in);
                            finish();*/

                        }else{
                            dialog.dismiss();

                            Toast.makeText(EmergencyTwoActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseExtraCharges> call, Throwable t) {
                        Toast.makeText(EmergencyTwoActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });




    }

}