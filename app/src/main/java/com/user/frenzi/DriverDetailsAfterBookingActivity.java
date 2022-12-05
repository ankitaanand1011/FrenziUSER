package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.user.frenzi.Responce.ResponceFetchRidedetails;
import com.user.frenzi.Responce.ResponseCancelRide;
import com.user.frenzi.Responce.ResponseRideStatus;
import com.user.frenzi.Responce.ServerGeneralResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverDetailsAfterBookingActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    LatLng latLng,dlatLng;
    private GoogleMap mMap;

    Button button_next;
    //    ImageView btn_back;
    Button btn_cancellation;
    AlertDialog alertDialog;
    private static final String TAG = "RideDetails";
    LinearLayout btn_share;
    private static final int REQUEST_PHONE_CALL = 1;
    FloatingActionButton fab;

    GoogleApiClient googleApiClient;
    TextView txt_Pickupaddress_name,txt_driver_name,txt_p_address,txt_reached_time,txt_d_address,txt_p_address_details,
            txt_d_address_details,txt_total_tips,txt_car_name,txt_car_number,txt_rating,txt_msg;
    ImageView img_driver;
    TextView txt_pin1,txt_pin2,txt_pin3,txt_pin4;
    LinearLayout btn_call,ll_hide_layout;
    String  pickup_address,drop_address, pickup_lat, pickup_long, drop_lat, drop_long,
            distance, total_time, amount, start_date, start_time, end_date, end_time,
            driver_name, ride_id, vehicle_no, driver_phn,driverId;
    String user_id, driver_id,Ridestatus;
    private static RequestQueue mRequestQueue;

    public  <T> void addToRequestQueue(Context mContext, Request<T> request, String tag) {
        // set the default tag if tag is empty
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(TextUtils.isEmpty(tag));
        getRequestQueue(mContext).add(request);
    }

    private RequestQueue getRequestQueue(Context mContext) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }

        return mRequestQueue;
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details_after_booking);
        initControls();



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
        getCurrentLocation();
        moveMap();





    }

    private void initControls() {

        txt_Pickupaddress_name=findViewById(R.id.txt_Pickupaddress_name);
        txt_p_address=findViewById(R.id.txt_p_address);
        txt_d_address=findViewById(R.id.txt_d_address);
        txt_p_address_details=findViewById(R.id.txt_p_address_details);
        txt_d_address_details=findViewById(R.id.txt_d_address_details);
        txt_total_tips=findViewById(R.id.txt_total_tips);
        txt_car_name=findViewById(R.id.txt_car_name);
        txt_car_number=findViewById(R.id.txt_car_number);
        txt_rating=findViewById(R.id.txt_rating);
        img_driver=findViewById(R.id.img_driver);
        txt_driver_name=findViewById(R.id.txt_driver_name);
        txt_pin1=findViewById(R.id.txt_pin1);
        txt_pin2=findViewById(R.id.txt_pin2);
        txt_pin3=findViewById(R.id.txt_pin3);
        txt_pin4=findViewById(R.id.txt_pin4);
        txt_msg=findViewById(R.id.txt_msg);
        txt_reached_time=findViewById(R.id.txt_reached_time);
        btn_call=findViewById(R.id.btn_call);
        btn_share=findViewById(R.id.btn_share);
        ll_hide_layout=findViewById(R.id.ll_hide_layout);
        fab=findViewById(R.id.fab);

        btn_cancellation=findViewById(R.id.btn_cancellation);
        button_next=findViewById(R.id.button_next);
        functions();

    }

    private void functions() {

        // drop_add = getIntent().getStringExtra("drop_add");
        //  pickup_add = getIntent().getStringExtra("pickup_add");
        pickup_lat = getIntent().getStringExtra("pickup_lat");
        pickup_long = getIntent().getStringExtra("pickup_long");
        drop_lat = getIntent().getStringExtra("drop_lat");
        drop_long = getIntent().getStringExtra("drop_long");
        ride_id = getIntent().getStringExtra("ride_id");
        amount = getIntent().getStringExtra("amount");
      //  driverId = getIntent().getStringExtra("driver_id");

        Log.e(TAG, "functions: ride_id >>"+ride_id);

       // ll_hide_layout.setVisibility(View.GONE);


        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Your body here";
                String sub = "Your Subject";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));*/

                shareImageWithText();
            }
        });

        btn_cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCurrentLocation();
                        moveMap();
                    }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //   post_ride();
/*
                Intent cancel=new Intent(DriverDetailsAfterBookingActivity.this,EmergencyTwoActivity.class);
                cancel.putExtra("ride_id", ride_id);
                cancel.putExtra("amount", amount);
                cancel.putExtra("user_id", user_id);
                cancel.putExtra("driver_id", driver_id);
                startActivity(cancel);*/

              //  if(Ridestatus.equals("STARTED")){

                final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            CheckRideStatus();

                        }
                    }, 2000);
                //}
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(DriverDetailsAfterBookingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DriverDetailsAfterBookingActivity.this, new String[]{Manifest.permission.CALL_PHONE}
                            ,REQUEST_PHONE_CALL);
                }
                else
                {
                    String number=driver_phn;
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(intent);
                }

            }
        });

        FetchRideDetails();
       /* if(driverId.equals("0")){
            FetchRideDetailsWithoutDriver();
        }else{
            FetchRideDetails();
        }*/
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

        RequestBody RideID = RequestBody.create(MediaType.parse("text/plain"), ride_id);


        RestClient.getClient().checkRideStatus(RideID).enqueue
                (new Callback<ResponseRideStatus>() {
                    @Override
                    public void onResponse(Call<ResponseRideStatus> call,
                                           Response<ResponseRideStatus> response) {
                        Log.e(TAG, "onResponse 2 : " + response.code());
                        Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                        // ppDialog.dismiss();
                        assert response.body() != null;
                        if (response.body().getStatus().equals(200)) {
                            //    ll_hide_layout.setVisibility(View.VISIBLE);

                           if(response.body().getRide_status().equals("STARTED")){

                               Intent cancel=new Intent(DriverDetailsAfterBookingActivity.this,EmergencyTwoActivity.class);
                               cancel.putExtra("ride_id", ride_id);
                               cancel.putExtra("amount", amount);
                               cancel.putExtra("user_id", user_id);
                               cancel.putExtra("driver_id", driver_id);
                               startActivity(cancel);


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



    private void FetchRideDetails() {
        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();




        RequestBody RideID = RequestBody.create(MediaType.parse("text/plain"), ride_id);


        RestClient.getClient().fetchRideDetails(RideID).enqueue
                (new Callback<ResponceFetchRidedetails>() {
            @Override
            public void onResponse(Call<ResponceFetchRidedetails> call,
                                   Response<ResponceFetchRidedetails> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();
                //    ll_hide_layout.setVisibility(View.VISIBLE);

                    ResponceFetchRidedetails listResponse = response.body();
                    driver_name = String.valueOf(response.body().getResponse().getDriverDetails().getName());

                    txt_car_number.setText(String.valueOf(response.body().getResponse().getDriverDetails().getVehicleNo()));
                    txt_driver_name.setText(String.valueOf(response.body().getResponse().getDriverDetails().getName()));
                    txt_rating.setText(String.valueOf(response.body().getResponse().getDriverDetails().getReviews()));

                    txt_p_address.setText(response.body().getResponse().getPickupAddress());
                    txt_d_address.setText(response.body().getResponse().getDropAddress());
                    txt_reached_time.setText(response.body().getResponse().getTotalTime());

                    String userId = String.valueOf(response.body().getResponse().getUserId());
                    user_id = userId ;
                    String driverId= String.valueOf(response.body().getResponse().getDriverId());
                    driver_id = driverId;
                    pickup_address = response.body().getResponse().getPickupAddress();
                    drop_address = response.body().getResponse().getDropAddress();

                    String  pickup_latlng = response.body().getResponse().getPickupLocation();
                    String[] pickup_lat_lng = pickup_latlng.split(",");
                    pickup_lat = pickup_lat_lng [0];
                    pickup_long = pickup_lat_lng [1];

                    String  drop_latlng = response.body().getResponse().getDropLocation();
                    String[] drop_lat_lng = drop_latlng.split(",");
                    drop_lat = drop_lat_lng [0];
                    drop_long = drop_lat_lng [1];

                    distance = response.body().getResponse().getDistance();
                    total_time = response.body().getResponse().getTotalTime();
                    String Amount = response.body().getResponse().getAmount();
                    amount = Amount;
                    start_date = response.body().getResponse().getStartDate();
                    start_time = response.body().getResponse().getStartTime();
                    end_date = response.body().getResponse().getEndDate();
                    end_time = response.body().getResponse().getEndTime();
                    Ridestatus = response.body().getResponse().getStatus();

                    Log.d(TAG, "onResponse:user_id  - "+user_id );
                    Log.d(TAG, "onResponse:driver_id  - "+driver_id );
                    Log.d(TAG, "onResponse:pickup_address - "+pickup_address );
                    Log.d(TAG, "onResponse:pickup_lat - "+pickup_lat );
                    Log.d(TAG, "onResponse:pickup_long - "+pickup_long );
                    Log.d(TAG, "onResponse:drop_lat - "+drop_lat );
                    Log.d(TAG, "onResponse:drop_long - "+drop_long );





                    String otp = String.valueOf(response.body().getResponse().getRideOtp());
                    Log.d(TAG, "onResponse: otp"+otp);
                    char optChar[] = otp.toCharArray();
                    for (int i = 0;i < optChar.length; i++){
                        System.out.println(optChar[i]);

                    }
                    txt_pin1.setText(String.valueOf(optChar[0]));
                    txt_pin2.setText(String.valueOf(optChar[1]));
                    txt_pin3.setText(String.valueOf(optChar[2]));
                    txt_pin4.setText(String.valueOf(optChar[3]));

                    Log.d(TAG, "onResponse: "+otp);


                } else
                {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponceFetchRidedetails> call, Throwable t)
            {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void FetchRideDetailsWithoutDriver() {
        // Tag used to cancel the request
        final String tag_string_req = "FetchRideDetails";

        String url = "https://mobileappsgamesstudio.com/works/frenzi_new/api/ride_details";
        Log.d(TAG, "url>>>  "+url);
        try{
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new com.android.volley.Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "FetchRideDetails Response: " + response);
                    //   dialog.dismiss();

                    Gson gson = new Gson();

                    try {

                        JsonObject jobj = gson.fromJson(response, JsonObject.class);
                        String status = jobj.get("status").getAsString().replaceAll("\"", "");
                        String message = jobj.get("message").getAsString().replaceAll("\"", "");


                        Log.e(TAG, "onResponse:status "+status );
                        Log.e(TAG, "onResponse:message "+message );

                        if (status.equals("200")) {

                            JsonObject response1 = jobj.getAsJsonObject("response");
                            Log.e(TAG, "onResponse:response1 " + response1);

                            String user_id = response1.get("user_id").getAsString().replaceAll("\"", "");
                            String driver_id = response1.get("driver_id").getAsString().replaceAll("\"", "");
                            String pickup_address = response1.get("pickup_address").getAsString().replaceAll("\"", "");
                            String drop_address = response1.get("drop_address").getAsString().replaceAll("\"", "");
                            String pickup_location = response1.get("pickup_location").getAsString().replaceAll("\"", "");
                            String drop_location = response1.get("drop_location").getAsString().replaceAll("\"", "");
                            distance = response1.get("distance").getAsString().replaceAll("\"", "");
                            String p_status = response1.get("status").getAsString().replaceAll("\"", "");
                            amount = response1.get("amount").getAsString().replaceAll("\"", "");
                            start_date = response1.get("start_date").getAsString().replaceAll("\"", "");
                            start_time = response1.get("start_time").getAsString().replaceAll("\"", "");
                            String ride_otp = response1.get("ride_otp").getAsString().replaceAll("\"", "");
                            String fare_cost = response1.get("fare_cost").getAsString().replaceAll("\"", "");
                            String discount = response1.get("discount").getAsString().replaceAll("\"", "");

                            Log.e(TAG, "onResponse:fare_cost " + fare_cost);
                            Log.e(TAG, "onResponse:discount " + discount);
                            Log.e(TAG, "onResponse:user_id " + user_id);
                            Log.e(TAG, "onResponse:p_status " + p_status);
                            Log.e(TAG, "onResponse:fare_cost " + fare_cost);
                            Log.e(TAG, "onResponse:fare_cost " + fare_cost);

                            JsonObject user_details = response1.getAsJsonObject("user_details");
                            Log.e(TAG, "onResponse:user_details " + user_details);

                            String customer_name = user_details.get("name").getAsString().replaceAll("\"", "");
                            // String user_id = user_details.get("user_id").getAsString().replaceAll("\"", "");
                            String customer_email = user_details.get("email").getAsString().replaceAll("\"", "");
                            String customer_phone = user_details.get("phone").getAsString().replaceAll("\"", "");
                            String customer_image = user_details.get("image").getAsString().replaceAll("\"", "");

                            Log.e(TAG, "onResponse: customer_name" + customer_name);
                            Log.e(TAG, "onResponse: customer_email" + customer_email);
                            Log.e(TAG, "onResponse: customer_phone" + customer_phone);
                            Log.e(TAG, "onResponse: customer_image" + customer_image);

                            txt_p_address.setText(pickup_address);
                            txt_Pickupaddress_name.setText(pickup_address);
                            txt_d_address.setText(drop_address);
                            //  txt_reached_time.setText();


                            String[] pickup_lat_lng = pickup_location.split(",");
                            pickup_lat = pickup_lat_lng[0];
                            pickup_long = pickup_lat_lng[1];

                            String[] drop_lat_lng = drop_location.split(",");
                            drop_lat = drop_lat_lng[0];
                            drop_long = drop_lat_lng[1];

                            Log.d(TAG, "onResponse: otp" + ride_otp);
                            char optChar[] = ride_otp.toCharArray();
                            for (int i = 0; i < optChar.length; i++) {
                                System.out.println(optChar[i]);

                            }
                            if (ride_otp.length() == 3) {

                                txt_pin1.setText("0");
                                txt_pin2.setText(String.valueOf(optChar[0]));
                                txt_pin3.setText(String.valueOf(optChar[1]));
                                txt_pin4.setText(String.valueOf(optChar[2]));
                            } else {
                                txt_pin1.setText(String.valueOf(optChar[0]));
                                txt_pin2.setText(String.valueOf(optChar[1]));
                                txt_pin3.setText(String.valueOf(optChar[2]));
                                txt_pin4.setText(String.valueOf(optChar[3]));
                            }

                            Log.d(TAG, "onResponse: " + ride_otp);

                            if (!driver_id.equals("0")) {
                                JsonObject driver_details = response1.getAsJsonObject("driver_details");
                                Log.e(TAG, "onResponse: driver_details" + driver_details);

                                Log.e(TAG, "onResponse:driver_details.isJsonNull()> " + driver_details.size());
                                if (driver_details.size() > 0) {
                                    String driver_name = driver_details.get("name").getAsString().replaceAll("\"", "");
                                    String driver_id1 = driver_details.get("driver_id").getAsString().replaceAll("\"", "");
                                    String driver_email = driver_details.get("email").getAsString().replaceAll("\"", "");
                                    String driver_phone = driver_details.get("phone").getAsString().replaceAll("\"", "");
                                    String driver_reviews = driver_details.get("reviews").getAsString().replaceAll("\"", "");
                                    String driver_vehicle_no = driver_details.get("vehicle_no").getAsString().replaceAll("\"", "");
                                    String driver_vehicle_type = driver_details.get("vehicle_type").getAsString().replaceAll("\"", "");
                                    String driver_image = driver_details.get("vehicle_image").getAsString().replaceAll("\"", "");

                                    txt_msg.setText("Message " + driver_name);
                                    switch (driver_vehicle_type) {
                                        case "5":
                                            txt_car_name.setText("Mini");
                                            break;
                                        case "6":
                                            txt_car_name.setText("Medium");
                                            break;
                                        case "7":
                                            txt_car_name.setText("Heavy");
                                            break;
                                        case "8":
                                            txt_car_name.setText("Hatchback");
                                            break;
                                    }

                                    txt_car_number.setText(driver_vehicle_no);
                                    txt_driver_name.setText(driver_name);
                                    txt_rating.setText(driver_reviews);


                                    //String otp = String.valueOf(response.body().getResponse().getRideOtp());

                                }

                            }



                        }else {

                            //  Toast.makeText(ChooseRideActivity.this,message,Toast.LENGTH_LONG).show();

                            //   dialog.dismiss();
//

                        }


                    }catch (Exception e){
                        e.printStackTrace();

                    }

                }
            }, new com.android.volley.Response.ErrorListener(){

                @Override

                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "FetchRideDetails Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Connection Error", Toast.LENGTH_LONG).show();
                    // dialog.dismiss();
                    //  mView.hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    params.put("ride_id", ride_id);


                    return params;
                }
            };

            this.addToRequestQueue(DriverDetailsAfterBookingActivity.this, strReq, tag_string_req);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void popUp() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_cancel, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        Button yes =promptsView.findViewById(R.id.yes);
        Button no =promptsView.findViewById(R.id.no);
        TextView tv_cancel_msg =promptsView.findViewById(R.id.tv_cancel_msg);


        tv_cancel_msg.setText("Cancel your ride with "+driver_name);
        alertDialogBuilder.setView(promptsView);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_ride();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });




        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    private void cancel_ride() {


        ACProgressFlower dialog = new ACProgressFlower.Builder(DriverDetailsAfterBookingActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();


        RequestBody rideId = RequestBody.create(MediaType.parse("txt/plain"), ride_id);

        RestClient.getClient().cancelRide(rideId).enqueue(new Callback<ResponseCancelRide>() {
            @Override
            public void onResponse(Call<ResponseCancelRide> call, Response<ResponseCancelRide> response) {
                Log.e(TAG, "onResponse: Code :" + response.body());
                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.message());
                Log.e(TAG, "onResponse: " + response.errorBody());

                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();

                  /*  SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    // editor.putString(Constant.DRIVER_PREF, "1");
                    editor.putString(Constant.BOOKING, "no");
                    editor.apply();*/

                    Intent cancel=new Intent(DriverDetailsAfterBookingActivity.this,MapScreen.class);
                    startActivity(cancel);
                    Toast.makeText(DriverDetailsAfterBookingActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(DriverDetailsAfterBookingActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

                    dialog.dismiss();
//

                }
            }

            @Override
            public void onFailure(Call<ResponseCancelRide> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }


    void shareImageWithText(){
        // Uri contentUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_launcher");

        StringBuilder msg = new StringBuilder();
        msg.append("I am travelling from ");
        msg.append(pickup_address);
        msg.append(" to ");
        msg.append(drop_address);
        msg.append(" with ");
        msg.append(driver_name);
        msg.append(" contact no. - ");
        msg.append(driver_phn);
        msg.append(" vehicle no. - ");
        msg.append(vehicle_no);

        // if (contentUri != null) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.toString());
        //  shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        try {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
        }
        //}
    }


    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }



    /*--------Map Implementation-----------*/
    //Getting current location
    private void getCurrentLocation() {
//        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
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
        // String msg = latitude + ", "+longitude;
        String msg = Double.parseDouble(pickup_lat) + ", "+Double.parseDouble(pickup_long);

        Log.e(TAG, "moveMap: Location Point ::"+msg );

        //Creating a LatLng Object to store Coordinates
        //latLng = new LatLng(latitude, longitude);
        latLng = new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long));

//        //Adding marker to map
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng) //setting position
//                .draggable(true) //Making the marker draggable
//                .title("Me")); //Adding a title
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


        //Displaying current coordinates in toast
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //  String msg = latitude + ", "+longitude;
        String msg = Double.parseDouble(pickup_lat) + ", "+Double.parseDouble(pickup_long);
        Log.e(TAG, "moveMap: Location Point ::"+msg );

        showLineBetween(Double.parseDouble(pickup_lat),Double.parseDouble(pickup_long),
                Double.parseDouble(drop_lat),Double.parseDouble(drop_long));

        //Creating a LatLng Object to store Coordinates
        // latLng = new LatLng(latitude, longitude);
        latLng = new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long));
        dlatLng = new LatLng(Double.parseDouble(drop_lat), Double.parseDouble(drop_long));

//        //Adding marker to map
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng) //setting position
//                .draggable(true) //Making the marker draggable
//                .title("Me")); //Adding a title
        if (mMap == null) {
            return;
        }

      /*  BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_j);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 84, 84, false);
        // adding a marker on map with image from  drawable
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));*/

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long)), 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);


        BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.navigation);
        Bitmap b1=bitmapdraw1.getBitmap();
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 84, 84, false);
        // adding a marker on map with image from  drawable
        mMap.addMarker(new MarkerOptions()
                .position(dlatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker1)));

     //   mMap.moveCamera(CameraUpdateFactory.newLatLng(dlatLng));
     //   mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //   mMap.moveCamera(CameraUpdateFactory.newLatLng(dlatLng));
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
    public void showLineBetween(double from_latitude, double from_longitude ,
                                double to_latitude, double to_longitude){

        ArrayList<LatLng> points = new ArrayList<LatLng>();
        PolylineOptions polyLineOptions = new PolylineOptions();
        points.add(new LatLng(from_latitude,from_longitude));
        points.add(new LatLng(to_latitude,to_longitude));
        polyLineOptions.width(7 * 1);
        polyLineOptions.geodesic(true);
        polyLineOptions.color(getResources().getColor(R.color.black));
        polyLineOptions.addAll(points);
        Polyline polyline = mMap.addPolyline(polyLineOptions);
        polyline.setGeodesic(true);

    }


}
