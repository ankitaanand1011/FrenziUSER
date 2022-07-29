package com.user.frenzi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
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

public class EmergencyTwoActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    final private static int SPLASH_TIME_OUT = 5000;


    RelativeLayout txt_call;
    ImageView btn_back;

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
        setContentView(R.layout.activity_emergency_two);


        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {


                Intent intent2 = new Intent(EmergencyTwoActivity.this, ThankYouActivity.class);
//                    Log.d(TAG, "Launching Chat Fragment: " + prefs.getBoolean(USER_PREF_PHONE_USER_IS_LOGGED_IN, false));
                startActivity(intent2);
                finish();
//

            }
        }, SPLASH_TIME_OUT);
//        txt_call=findViewById(R.id.txt_call);
//        txt_call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent call=new Intent(EmergencyTwoActivity.this,ThankYouActivity.class);
//                startActivity(call);
//            }
//        });

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