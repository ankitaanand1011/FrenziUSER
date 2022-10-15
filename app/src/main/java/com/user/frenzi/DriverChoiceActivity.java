package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.user.frenzi.Responce.ResponceFetchPreferedDriverList;
import com.user.frenzi.Responce.ResponseNewRideDetails;
import com.user.frenzi.Responce.ResponseRideAccepted;
import com.user.frenzi.Responce.ResponseRideDetails;
import com.user.frenzi.Responce.ServerGeneralResponse;
import com.user.frenzi.adapter.AdapterPrefferedDriverList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverChoiceActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    LatLng latLng;
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;

    ImageView btn_back;
    Button btn_select;
    RecyclerView recycler_preferred_drivers;
    private final List<ResponceFetchPreferedDriverList.Response> DriverList = new ArrayList<>();
    AdapterPrefferedDriverList adapterPrefferedDriverList;
    private static final String TAG = "DriverChoice";
    String User_ID, vehicle_id;
    String drop_add,pickup_add,driver_id;
    String pickup_lat, pickup_long, drop_lat, drop_long,driver_prefer;
    AlertDialog alertDialog;
    String RideID,amount;
    String postRide ="no";

    @Override
    protected void onPause() {

        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        Log.e(TAG, "OnPause : postRide>> "+postRide );
        super.onPause();
    /*    if(postRide.equals("yes")) {

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    acceptedRide();

                }
            }, 2000);
        }

      */


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_choice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



        drop_add = getIntent().getStringExtra("drop_add");
        pickup_add = getIntent().getStringExtra("pickup_add");
        pickup_lat = getIntent().getStringExtra("pickup_lat");
        pickup_long = getIntent().getStringExtra("pickup_long");
        drop_lat = getIntent().getStringExtra("drop_lat");
        drop_long = getIntent().getStringExtra("drop_long");
        driver_prefer = getIntent().getStringExtra("driver_prefer");

        btn_back=findViewById(R.id.btn_back);
        recycler_preferred_drivers=findViewById(R.id.recycler_preffered_drivers);
        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        User_ID = spp.getString(Constant.USER_ID, "");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        adapterPrefferedDriverList = new AdapterPrefferedDriverList(this,getApplicationContext(), DriverList, new AdapterPrefferedDriverList.OnItemClickListener() {
            @Override
            public void onItemClick(ResponceFetchPreferedDriverList.Response item) {
                Log.e(TAG, "onItemClick: "+item.getName() );
                Log.e(TAG, "onItemClick: "+item.getId() );

                driver_id = String.valueOf(item.getId());

                //Call API notify_preferred_driver
                popUpConfirmBooking();


            }
        });


        RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(this);
        recycler_preferred_drivers.setLayoutManager(mmLayoutManager);
        recycler_preferred_drivers.setItemAnimator(new DefaultItemAnimator());
        recycler_preferred_drivers.setAdapter(adapterPrefferedDriverList);

        vehicle_id = getIntent().getStringExtra("vehicle_id");
        Log.e(TAG, "vehicle_id :: " + vehicle_id);

        FetchDriverList();

    }

    private void FetchDriverList() {
        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();



        RequestBody vehicleType = RequestBody.create(MediaType.parse("text/plain"),vehicle_id );
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"),User_ID );
        RequestBody driverPreference = RequestBody.create(MediaType.parse("text/plain"),driver_prefer);
        RequestBody pickupLat = RequestBody.create(MediaType.parse("text/plain"),pickup_lat);
        RequestBody pickupLong = RequestBody.create(MediaType.parse("text/plain"),pickup_long);
        RequestBody dropLat = RequestBody.create(MediaType.parse("text/plain"),drop_lat);
        RequestBody dropLong = RequestBody.create(MediaType.parse("text/plain"),drop_long);


        RestClient.getClient().fetchDriverList(vehicleType,userId,
                driverPreference,pickupLat,pickupLong,dropLat,dropLong
            ).enqueue(new Callback<ResponceFetchPreferedDriverList>() {
            @Override
            public void onResponse(Call<ResponceFetchPreferedDriverList> call, Response<ResponceFetchPreferedDriverList> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();

                    ResponceFetchPreferedDriverList listResponse = response.body();


                    assert listResponse != null;

                    for (ResponceFetchPreferedDriverList.Response list : listResponse.getResponse()) {
                        Log.e(TAG, "onResponse: block LIst " + list);
                        DriverList.add(list);
                        Log.e(TAG, "onResponse: Blick List :" + list);


                    }


                    adapterPrefferedDriverList.notifyDataSetChanged();
                    Log.e(TAG, "onResponse: Size Of Array ::" + DriverList.size());

                } else  {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponceFetchPreferedDriverList> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void popUpMessage() {

        // get prompts.xml view

        final Dialog dialog = new Dialog(DriverChoiceActivity.this);
        dialog.setContentView(R.layout.popup_preference_message);
       // dialog.setTitle("Title...");

      /*  LayoutInflater li = LayoutInflater.from(DriverChoiceActivity.this);
        View promptsView = li.inflate(R.layout.popup_preference_message, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DriverChoiceActivity.this
        );
*/
        TextView tv_next = dialog.findViewById(R.id.tv_next);
        RadioButton rb_male_only = dialog.findViewById(R.id.rb_male_only);
        RadioButton rb_female_only = dialog.findViewById(R.id.rb_female_only);
        RadioButton rb_male_female = dialog.findViewById(R.id.rb_male_female);


        // set prompts.xml to alertdialog builder
       // alertDialogBuilder.setView(promptsView);

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


                String driverPrefer = "any";
                if (rb_male_female.isChecked()) {
                    driverPrefer = "any";
                } else if (rb_female_only.isChecked()) {
                    driverPrefer = "female";
                } else if (rb_male_only.isChecked()) {
                    driverPrefer = "male";
                }

                //Call API send_another_driver

                send_another(driverPrefer);
               //
            }
        });
        dialog.show();


       /* btn_pay_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CAll API post_ride
                alertDialog.dismiss();
                popUpConfirmBooking();

//                if (Status.equalsIgnoreCase("1")) {
//                    Intent in = new Intent(mContext.getApplicationContext(), DriverChoiceActivity.class);
//                    mContext.getApplicationContext().startActivity(in);
//
//                } else if (Status.equalsIgnoreCase("0")) {
//                    Intent in = new Intent(mContext.getApplicationContext(), DriverNearbyActivity.class);
//                    mContext.getApplicationContext().startActivity(in);
//                }


            }
        });*/


        // create alert dialog
       /* alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();*/
    }

    private void popUppayment() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(DriverChoiceActivity.this);
        View promptsView = li.inflate(R.layout.popup_payment_choose, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DriverChoiceActivity.this
        );

        Button btn_pay_now = promptsView.findViewById(R.id.btn_pay_now);
        Button btn_pay_later = promptsView.findViewById(R.id.btn_pay_later);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent in = new Intent(DriverChoiceActivity.this, StripeAccountActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("ride_id", RideID);
                in.putExtra("amount", amount);
                startActivity(in);

            }
        });


        btn_pay_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CAll API make_payment
                alertDialog.dismiss();
                Intent in = new Intent(DriverChoiceActivity.this, DriverNearbyActivity.class);
                in.putExtra("ride_id", RideID );
                in.putExtra("pickup_lat",pickup_lat);
                in.putExtra("pickup_long",pickup_long);
                in.putExtra("drop_lat",drop_lat);
                in.putExtra("drop_long",drop_long);
                in.putExtra("amount",amount);
                in.putExtra("pickup_add",pickup_add);

                // in.putExtra("driver_id",driver_id);
                startActivity(in);

            }
        });


        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    private void popUpConfirmBooking() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_confirm_ride, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        Button yes =promptsView.findViewById(R.id.yes);
        Button no =promptsView.findViewById(R.id.no);
       // TextView tv_cancel_msg =promptsView.findViewById(R.id.tv_cancel_msg);


       // tv_cancel_msg.setText("Cancel your ride with "+driver_name);
        alertDialogBuilder.setView(promptsView);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());

                Log.e(TAG, "onClick:currentDate "+currentDate );
                Log.e(TAG, "onClick:currentTime "+currentTime );

                post_ride(currentDate,currentTime);
              //  popUppayment();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(DriverChoiceActivity.this, MapScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });




        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    private void post_ride(String currentDate, String currentTime) {
       /* if(TextUtils.isEmpty(edt_full_name.getText().toString().trim())) {
            edt_full_name.setError("Please Enter Full name");
            return;
        }else if(TextUtils.isEmpty(edt_email.getText().toString().trim())) {
            edt_email.setError("Please Enter Email");
            return;
        }else if(TextUtils.isEmpty(edt_contact.getText().toString().trim())) {
            edt_contact.setError("Please Enter Contact Number");
            return;
        }else if(TextUtils.isEmpty(edt_message.getText().toString().trim())) {
            edt_message.setError("Please Enter Message");
            return;
        }else {
*/
        ACProgressFlower dialog = new ACProgressFlower.Builder(DriverChoiceActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();


        RequestBody post_user_id = RequestBody.create(MediaType.parse("txt/plain"), User_ID);
        RequestBody post_driver_id = RequestBody.create(MediaType.parse("txt/plain"), String.valueOf(driver_id));
        RequestBody post_pickup_address = RequestBody.create(MediaType.parse("txt/plain"), pickup_add );
        RequestBody post_drop_address = RequestBody.create(MediaType.parse("txt/plain"), drop_add );
        RequestBody post_pickup_lat = RequestBody.create(MediaType.parse("txt/plain"), pickup_lat );
        RequestBody post_pickup_long = RequestBody.create(MediaType.parse("txt/plain"), pickup_long );
        RequestBody post_drop_lat = RequestBody.create(MediaType.parse("txt/plain"), drop_lat );
        RequestBody post_drop_long = RequestBody.create(MediaType.parse("txt/plain"), drop_long );
        RequestBody post_start_date = RequestBody.create(MediaType.parse("txt/plain"), currentDate );
        RequestBody post_start_time = RequestBody.create(MediaType.parse("txt/plain"), currentTime );


        Log.e(TAG, "post_ride:post_user_id> "+User_ID );
        Log.e(TAG, "post_ride:post_driver_id> "+driver_id );
        Log.e(TAG, "post_ride:post_pickup_address> "+pickup_add );
        Log.e(TAG, "post_ride:post_drop_address> "+drop_add );
        Log.e(TAG, "post_ride:post_pickup_lat> "+pickup_lat );
        Log.e(TAG, "post_ride:post_pickup_long> "+pickup_long );
        Log.e(TAG, "post_ride:post_drop_lat> "+drop_lat );
        Log.e(TAG, "post_ride:post_drop_long> "+drop_long );
        Log.e(TAG, "post_ride:post_start_date> "+currentDate );
        Log.e(TAG, "post_ride:post_start_time> "+currentTime );
        Gson gson = new Gson();
        RestClient.getClient().SubmitRide(post_user_id,post_driver_id,post_pickup_address,
                post_drop_address, post_pickup_lat,post_pickup_long,post_drop_lat,post_drop_long,
                post_start_date,post_start_time)
                .enqueue(new Callback<ResponseNewRideDetails>() {
            @Override
            public void onResponse(Call<ResponseNewRideDetails> call, Response<ResponseNewRideDetails> response) {
                Log.e(TAG, "onResponse: Url :" + response);
//                Log.e(TAG, "onResponse: Code :" + response.body().getResponse().ride_id);
                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.message());
             //   Log.e(TAG, "onResponse: " + response.body().getResponse().getRide_id());

                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();


                    //Call API notify_preferred_driver

                    postRide = "yes";

                  /*  SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    // editor.putString(Constant.DRIVER_PREF, "1");
                    editor.putString(Constant.BOOKING, "yes");
                    editor.putString(Constant.PREFERRED_BOOKING, "yes");
                    editor.apply();*/

                    Log.e(TAG, "onResponse:driver_id "+response.body().getResponse().getDriverId() );
                    Log.e(TAG, "onResponse:user_id "+response.body().getResponse().getUserId() );
                    Log.e(TAG,  gson.toJson(response.body()));
                    Log.e(TAG, "onResponse:ride_id "+response.body().getResponse().getRide_id());
                    Log.e(TAG, "onResponse:ride_id "+response.body().getResponse().getRideID());
                    String driver_id = String.valueOf(response.body().getResponse().getDriverId());
                     amount = response.body().getResponse().getAmount();
                     RideID = String.valueOf(response.body().getResponse().getRide_id());


                    popUpMessage();
                 /*   Intent in = new Intent(DriverChoiceActivity.this, DriverDetailsAfterBookingActivity.class);
                    in.putExtra("ride_id",response.body().getResponse().getRide_id());
                    in.putExtra("pickup_lat",pickup_lat);
                    in.putExtra("pickup_long",pickup_long);
                    in.putExtra("drop_lat",drop_lat);
                    in.putExtra("drop_long",drop_long);
                   // in.putExtra("driver_id",driver_id);
                    startActivity(in);*/
/*

                    Intent cancel=new Intent(DriverChoiceActivity.this,EmergencyTwoActivity.class);
                    startActivity(cancel);
                    Toast.makeText(DriverChoiceActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
*/


                } else {

                    Toast.makeText(DriverChoiceActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

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

    private void send_another(String driver_prefer) {

        ACProgressFlower dialog = new ACProgressFlower.Builder(DriverChoiceActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();


        RequestBody post_user_id = RequestBody.create(MediaType.parse("txt/plain"), User_ID);
        RequestBody post_ride_id = RequestBody.create(MediaType.parse("txt/plain"), RideID);
        RequestBody post_driver_preference = RequestBody.create(MediaType.parse("txt/plain"), driver_prefer );

        Log.e(TAG, "post_ride:post_user_id> "+User_ID );
        Log.e(TAG, "post_ride:post_driver_id> "+driver_id );

        Gson gson = new Gson();
        RestClient.getClient().SendAnother(post_user_id,post_ride_id,post_driver_preference
                        )
                .enqueue(new Callback<ServerGeneralResponse>() {
                    @Override
                    public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {
                        Log.e(TAG, "onResponse: Url :" + response);
//                Log.e(TAG, "onResponse: Code :" + response.body().getResponse().ride_id);
                        Log.e(TAG, "onResponse: " + response.code());
                        Log.e(TAG, "onResponse: " + response.message());
                        //   Log.e(TAG, "onResponse: " + response.body().getResponse().getRide_id());

                        assert response.body() != null;
                        if (response.body().getStatus().equals(200)) {
                            dialog.dismiss();

                           /* SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            // editor.putString(Constant.DRIVER_PREF, "1");
                            editor.putString(Constant.BOOKING, "yes");
                            editor.putString(Constant.PREFERRED_BOOKING, "yes");
                            editor.apply();*/
                            //Call API notify_preferred_driver

                            popUppayment();



                        } else {

                            Toast.makeText(DriverChoiceActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

                            dialog.dismiss();
//

                        }
                    }

                    @Override
                    public void onFailure(Call<ServerGeneralResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume:postRide>> "+postRide );

        if(postRide.equals("yes")) {

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    acceptedRide();

                }
            }, 2000);
        }
    }



    private void acceptedRide( ) {


        RequestBody rideId = RequestBody.create(MediaType.parse("txt/plain"), RideID);

        RestClient.getClient().RideAccepted(rideId).enqueue(new Callback<ResponseRideAccepted>() {
            @Override
            public void onResponse(Call<ResponseRideAccepted> call, Response<ResponseRideAccepted> response) {
                Log.e(TAG, "onResponse: Code :" + response.body());
                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.errorBody());
                if (!String.valueOf(response.code()).equals("500")) {
                    if (response.body().getStatus().equals(200)) {
                        // dialog.dismiss();

                        //  Log.e(TAG, "onResponse code: "+response.body().getResponse() );
                        String message = response.body().getMessage();

                        if(message.equals("Ride Accepted")) {
                            Intent in = new Intent(DriverChoiceActivity.this, DriverDetailsAfterBookingActivity.class);
                            in.putExtra("ride_id", RideID);
                            in.putExtra("pickup_lat", pickup_lat);
                            in.putExtra("pickup_long", pickup_long);
                            in.putExtra("drop_lat", drop_lat);
                            in.putExtra("drop_long", drop_long);
                            in.putExtra("pickup_add", pickup_add);
                            in.putExtra("drop_add", drop_add);
                            //    in.putExtra("driver_id",response.body().g);
                            startActivity(in);
                            finish();
                        }

                    } else {
                        // dialog.dismiss();
//

                    }
                }else{
                    // dialog.dismiss();
                    Toast.makeText(DriverChoiceActivity.this,"Something went wrong !!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseRideAccepted> call, Throwable t) {

                // dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();

        Log.e(TAG, "onStop: "+postRide );
        super.onStop();
    }

    //Getting current location
    private void getCurrentLocation() {
//        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }

    //Function to move the map
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", "+longitude;
        Log.e(TAG, "moveMap: Location Point ::"+msg );

        //Creating a LatLng Object to store Coordinates
        //latLng = new LatLng(latitude, longitude);
        latLng = new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long));

        if (mMap == null) {
            return;
        }
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_j);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 84, 84, false);
        // adding a marker on map with image from  drawable
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long)), 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);


        //Displaying current coordinates in toast
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String msg = latitude + ", "+longitude;
        Log.e(TAG, "moveMap: Location Point ::"+msg );

        //Creating a LatLng Object to store Coordinates
       // latLng = new LatLng(latitude, longitude);
        latLng = new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long));

        if (mMap == null) {
            return;
        }

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_j);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 84, 84, false);
        // adding a marker on map with image from  drawable
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long)), 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);


    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}