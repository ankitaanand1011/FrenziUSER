package com.user.frenzi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

public class DriverNearbyActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    ProgressBar progress;
    ImageView btn_back;
    RelativeLayout txt_cancel_ride;


    LatLng a = null;
    private GoogleMap mMap;
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nearby);

        btn_back=findViewById(R.id.btn_back);
        progress=findViewById(R.id.progress);


        int progressValue=progress.getProgress();
        progress.setMax(100);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        txt_cancel_ride=findViewById(R.id.txt_cancel_ride);
        txt_cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ridec=new Intent(DriverNearbyActivity.this, DriverDetailsAfterBookingActivity.class);
                startActivity(ridec);
            }
        });

        //set
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        a = new LatLng(53.801277, -1.548567);

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
}