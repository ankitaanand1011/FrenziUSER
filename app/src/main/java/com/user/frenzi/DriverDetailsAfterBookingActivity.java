package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.user.frenzi.Responce.ResponceFetchRidedetails;
import com.user.frenzi.Responce.ServerGeneralResponse;

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
    LatLng latLng;
    private GoogleMap mMap;

    Button button_next;
//    ImageView btn_back;
    Button btn_cancellation;
    AlertDialog alertDialog;
    private static final String TAG = "RideDetails";
    LinearLayout btn_share;
    private static final int REQUEST_PHONE_CALL = 1;

    GoogleApiClient googleApiClient;
    TextView txt_Pickupaddress_name,txt_driver_name,txt_p_address,txt_reached_time,txt_d_address,txt_p_address_details,
             txt_d_address_details,txt_total_tips,txt_car_name,txt_car_number,txt_rating;
    ImageView img_driver;
    TextView txt_pin1,txt_pin2,txt_pin3,txt_pin4;
    LinearLayout btn_call;
    String  pickup_address,drop_address, pickup_lat, pickup_long, drop_lat, drop_long,
            distance, total_time, amount, start_date, start_time, end_date, end_time,
            driver_name, ride_id;
    int user_id, driver_id;

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



        FetchRideDetails();


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
        txt_reached_time=findViewById(R.id.txt_reached_time);
        btn_call=findViewById(R.id.btn_call);
        btn_share=findViewById(R.id.btn_share);

        btn_cancellation=findViewById(R.id.btn_cancellation);
        button_next=findViewById(R.id.button_next);
        functions();

    }

    private void functions() {

        ride_id = getIntent().getStringExtra("ride_id");

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Your body here";
                String sub = "Your Subject";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });

        btn_cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp();
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                post_ride();

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
                    String number="7076666007";
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(intent);
                }

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


        RestClient.getClient().fetchRideDetails(RideID).enqueue(new Callback<ResponceFetchRidedetails>() {
            @Override
            public void onResponse(Call<ResponceFetchRidedetails> call, Response<ResponceFetchRidedetails> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();

                    ResponceFetchRidedetails listResponse = response.body();
                    driver_name = String.valueOf(response.body().getResponse().getDriverDetails().getName());

                    txt_driver_name.setText(response.body().getResponse().getDriverDetails().getName());
                    txt_car_name.setText(response.body().getResponse().getDriverDetails().getVehicleType());
                    txt_car_number.setText(response.body().getResponse().getDriverDetails().getVehicleNo());

                    txt_p_address.setText(response.body().getResponse().getPickupAddress());
                    txt_d_address.setText(response.body().getResponse().getDropAddress());
                    txt_reached_time.setText(response.body().getResponse().getTotalTime());

                    user_id = response.body().getResponse().getUserId();
                    driver_id = response.body().getResponse().getDriverId();
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
                    amount = response.body().getResponse().getAmount();
                    start_date = response.body().getResponse().getStartDate();
                    start_time = response.body().getResponse().getStartTime();
                    end_date = response.body().getResponse().getEndDate();
                    end_time = response.body().getResponse().getEndTime();

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


                } else  {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponceFetchRidedetails> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
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
                Intent cancel=new Intent(DriverDetailsAfterBookingActivity.this,MapScreen.class);
                startActivity(cancel);            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               alertDialog.dismiss();
            }
        });

//        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                View radioButton = radio_group.findViewById(i);
//                int index = radio_group.indexOfChild(radioButton);
//
//                // Add logic here
//
//                switch (index) {
//                    case 0: // first button
//
////                        Toast.makeText(getApplicationContext(), "Selected button number " +index ,Toast.LENGTH_LONG).show();
//                        break;
//                    case 1: // secondbutton
//
////                        Toast.makeText(getApplicationContext(), "Selected button numbers "+index,Toast.LENGTH_LONG).show();
//                        break;
//                }
//            }
//        });





        btn_cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



//                Intent intent=new Intent(DestinationReachedActivity.this,RatingDriverRideActivity.class);
//                startActivity(intent);
//                finish();

            }
        });


        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
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
        latLng = new LatLng(latitude, longitude);

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
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //Displaying current coordinates in toast
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String msg = latitude + ", "+longitude;
        Log.e(TAG, "moveMap: Location Point ::"+msg );

        //Creating a LatLng Object to store Coordinates
        latLng = new LatLng(latitude, longitude);

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
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //Displaying current coordinates in toast
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.setOnMarkerDragListener(this);
//        mMap.setOnMapLongClickListener(this);
//        googleMap.addMarker(new MarkerOptions().position(latLng).title("Me").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_j)));

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


    private void post_ride() {
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
            ACProgressFlower dialog = new ACProgressFlower.Builder(DriverDetailsAfterBookingActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .fadeColor(Color.BLACK).build();
            dialog.show();


            RequestBody post_user_id = RequestBody.create(MediaType.parse("txt/plain"), String.valueOf(user_id));
            RequestBody post_driver_id = RequestBody.create(MediaType.parse("txt/plain"), String.valueOf(driver_id));
            RequestBody post_pickup_address = RequestBody.create(MediaType.parse("txt/plain"), pickup_address );
            RequestBody post_drop_address = RequestBody.create(MediaType.parse("txt/plain"), drop_address );
            RequestBody post_pickup_lat = RequestBody.create(MediaType.parse("txt/plain"), pickup_lat );
            RequestBody post_pickup_long = RequestBody.create(MediaType.parse("txt/plain"), pickup_long );
            RequestBody post_drop_lat = RequestBody.create(MediaType.parse("txt/plain"), drop_lat );
            RequestBody post_drop_long = RequestBody.create(MediaType.parse("txt/plain"), drop_long );
            RequestBody post_distance = RequestBody.create(MediaType.parse("txt/plain"), distance );
            RequestBody post_total_time = RequestBody.create(MediaType.parse("txt/plain"), total_time );
            RequestBody post_amount = RequestBody.create(MediaType.parse("txt/plain"), amount );
            RequestBody post_start_date = RequestBody.create(MediaType.parse("txt/plain"), start_date );
            RequestBody post_start_time = RequestBody.create(MediaType.parse("txt/plain"), start_time );
            RequestBody post_end_date = RequestBody.create(MediaType.parse("txt/plain"), end_date );
            RequestBody post_end_time = RequestBody.create(MediaType.parse("txt/plain"), end_time );


            RestClient.getClient().SubmitRide(post_user_id,post_driver_id,post_pickup_address,
                    post_drop_address, post_pickup_lat,post_pickup_long,post_drop_lat,post_drop_long,post_distance,
                    post_total_time,post_amount,post_start_date,post_start_time,post_end_date,
                    post_end_time).enqueue(new Callback<ServerGeneralResponse>() {
                @Override
                public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {
                    Log.e(TAG, "onResponse: Code :" + response.body());
                    Log.e(TAG, "onResponse: " + response.code());
                    Log.e(TAG, "onResponse: " + response.message());
                    Log.e(TAG, "onResponse: " + response.errorBody());

                    assert response.body() != null;
                    if (response.body().getStatus().equals(200)) {
                        dialog.dismiss();

                        Intent cancel=new Intent(DriverDetailsAfterBookingActivity.this,EmergencyTwoActivity.class);
                        startActivity(cancel);
                        Toast.makeText(DriverDetailsAfterBookingActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();


                    } else {

                        Toast.makeText(DriverDetailsAfterBookingActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

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

}


