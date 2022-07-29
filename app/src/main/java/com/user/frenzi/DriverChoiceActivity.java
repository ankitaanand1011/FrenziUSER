package com.user.frenzi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

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
import com.user.frenzi.Responce.ResponceFetchPreferedDriverList;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;
import com.user.frenzi.adapter.AdapterPrefferedDriverList;
import com.user.frenzi.adapter.AdapterRecentAddressList;

import java.util.ArrayList;
import java.util.List;
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
    RecyclerView recycler_preffered_drivers;
    private final List<ResponceFetchPreferedDriverList.Response> DriverList = new ArrayList<>();
    AdapterPrefferedDriverList adapterPrefferedDriverList;
    private static final String TAG = "DriverChoice";
    String User_ID, vehicle_id;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_choice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }



        btn_back=findViewById(R.id.btn_back);
        recycler_preffered_drivers=findViewById(R.id.recycler_preffered_drivers);
        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        User_ID = spp.getString(Constant.USER_ID, "");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
//        btn_select=findViewById(R.id.btn_select);
//        btn_select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent selectbtn=new Intent(DriverChoiceActivity.this,DriverDetailsAfterBpookingActivity.class);
//                startActivity(selectbtn);
//            }
//        });

        adapterPrefferedDriverList = new AdapterPrefferedDriverList(this,getApplicationContext(), DriverList, new AdapterPrefferedDriverList.OnItemClickListener() {
            @Override
            public void onItemClick(ResponceFetchPreferedDriverList.Response item) {

            }
        });


        RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(this);
        recycler_preffered_drivers.setLayoutManager(mmLayoutManager);
        recycler_preffered_drivers.setItemAnimator(new DefaultItemAnimator());
        recycler_preffered_drivers.setAdapter(adapterPrefferedDriverList);

        vehicle_id = getIntent().getStringExtra("vehicle_id");
        Log.e(TAG, "vehicle_id :: " + vehicle_id);

        FetchDriverList();
        getCurrentLocation();
        moveMap();
    }

    private void FetchDriverList() {
        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();



        RequestBody vehicleType = RequestBody.create(MediaType.parse("text/plain"),vehicle_id );


        RestClient.getClient().fetchDriverList(vehicleType).enqueue(new Callback<ResponceFetchPreferedDriverList>() {
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
}