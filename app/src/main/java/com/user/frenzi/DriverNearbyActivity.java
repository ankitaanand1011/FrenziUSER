package com.user.frenzi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.mckrpk.animatedprogressbar.AnimatedProgressBar;
import com.user.frenzi.Responce.ResponseCancelRide;
import com.user.frenzi.Responce.ResponseNewRideDetails;
import com.user.frenzi.Responce.ResponseReferCode;
import com.user.frenzi.Responce.ResponseRideAccepted;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverNearbyActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener{

    ProgressBar progress;
    ImageView btn_back;
    RelativeLayout txt_cancel_ride;

    private double longitude;
    private double latitude;
    LatLng latLng;
    private static final String TAG = "DriverNearby";
    LatLng a = null;
    private GoogleMap mMap;
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;
    GoogleApiClient googleApiClient;
    TextView tv_pickup_address;
    String pickup_lat, pickup_long, drop_lat, drop_long;
    String drop_add,pickup_add,ride_id;
    private Circle circle;
    AnimatedProgressBar animatedProgressBar, animatedProgressBar1;
    AlertDialog alertDialog;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nearby);
        
        initControls();


    }

    private void initControls() {


        
        
        btn_back=findViewById(R.id.btn_back);
        progress=findViewById(R.id.progress);
        tv_pickup_address=findViewById(R.id.tv_pickup_address);
        animatedProgressBar=findViewById(R.id.animatedProgressBar);
        animatedProgressBar1=findViewById(R.id.animatedProgressBar1);

        tv_pickup_address.setText(pickup_add);
        txt_cancel_ride=findViewById(R.id.txt_cancel_ride);
        
        function();


    }

    private void function() {

        drop_add = getIntent().getStringExtra("drop_add");
        pickup_add = getIntent().getStringExtra("pickup_add");
        pickup_lat = getIntent().getStringExtra("pickup_lat");
        pickup_long = getIntent().getStringExtra("pickup_long");
        drop_lat = getIntent().getStringExtra("drop_lat");
        drop_long = getIntent().getStringExtra("drop_long");
        ride_id = getIntent().getStringExtra("ride_id");

        Log.e(TAG, "onCreate:ride_id "+ride_id );


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        a = new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long));



        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(9000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = animatedProgressBar.getWidth();
                final float translationX = width * progress;
                animatedProgressBar.setTranslationX(translationX);
                animatedProgressBar1.setTranslationX(translationX - width);
            }
        });
        animator.start();

        int progressValue=progress.getProgress();
        progress.setMax(100);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txt_cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                popUp();
            }
        });

        acceptedRide();

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
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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
            Log.d(TAG, "getCurrentLocation: latitude >> "+latitude);
            Log.d(TAG, "getCurrentLocation: longitude >> "+longitude);

            //moving the map to location
            moveMap();
        }
    }

    private void moveMap() {
        //String to display current latitude and longitude
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
        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long)), 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String msg = latitude + ", "+longitude;
        Log.e(TAG, "moveMap: Location Point ::"+msg );

        //Creating a LatLng Object to store Coordinates
        latLng = new LatLng(latitude, longitude);
        //latLng = new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long));


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

        circle = mMap.addCircle(new CircleOptions()
                .center(a)
                .radius(60)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(R.color.yellow_project_trans));

        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(@NonNull Circle circle) {
                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);

            }
        });

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setIntValues(20, 100);
        valueAnimator.setDuration(3000);
        valueAnimator.setEvaluator(new IntEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circle.setRadius(animatedFraction * 100);
            }
        });

        valueAnimator.start();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long)), 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
//        googleMap.addMarker(new MarkerOptions().position(latLng).title("Me").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_j)));

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onBackPressed() {

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            View windowDecorView = getWindow().getDecorView();
            windowDecorView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);


        } else {

            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }

            else {
                super.onBackPressed();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                acceptedRide();

            }
        }, 2000);
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


      //  tv_cancel_msg.setText("Cancel your ride with "+driver_name);
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

    private void acceptedRide( ) {

   /*     ACProgressFlower dialog = new ACProgressFlower.Builder(DriverNearbyActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();*/


        RequestBody rideId = RequestBody.create(MediaType.parse("txt/plain"), ride_id);

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
                            Intent in = new Intent(DriverNearbyActivity.this, DriverDetailsAfterBookingActivity.class);
                            in.putExtra("ride_id", ride_id);
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
                    Toast.makeText(DriverNearbyActivity.this,"Something went wrong !!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseRideAccepted> call, Throwable t) {

               // dialog.dismiss();
            }
        });
    }

    private void cancel_ride() {


        ACProgressFlower dialog = new ACProgressFlower.Builder(DriverNearbyActivity.this)
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
                   /* SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    // editor.putString(Constant.DRIVER_PREF, "1");
                    editor.putString(Constant.BOOKING, "no");
                    editor.apply();*/

                    Intent cancel=new Intent(DriverNearbyActivity.this,MapScreen.class);
                    startActivity(cancel);
                    Toast.makeText(DriverNearbyActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(DriverNearbyActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

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

}