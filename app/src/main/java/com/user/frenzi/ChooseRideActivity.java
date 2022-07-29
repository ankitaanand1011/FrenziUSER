package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

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
import com.user.frenzi.Responce.ResponceFetchCarList;
import com.user.frenzi.Responce.ResponceFetchRecentAddressList;
import com.user.frenzi.adapter.AdapterCarList;
import com.user.frenzi.adapter.AdapterRecentAddressList;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseRideActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
//To store longitude and latitude from map
private double longitude;
private double latitude;
        LatLng latLng;
private GoogleMap mMap;
    GoogleApiClient googleApiClient;

    String Status = null;
    ImageView btn_back;
    Button btn_book_now;
    AlertDialog alertDialog, alertDialog1;
    RadioGroup radio_group;
    RecyclerView recycler_view_car_list;
    AdapterCarList adapterCarList;
    private List<ResponceFetchCarList.Response> CarList = new ArrayList<>();
    String vehicle_id;
    private static final String TAG = "Choosecar";

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ride);
        btn_back = findViewById(R.id.btn_back);
        btn_book_now = findViewById(R.id.btn_book_now);
        recycler_view_car_list=findViewById(R.id.recycler_view_car_list);

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

//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //           window.setStatusBarColor(getResources().getColor(R.color.MidnightBlue));
        //       }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        adapterCarList = new AdapterCarList(getApplicationContext(), CarList, new AdapterCarList.OnItemClickListener() {
            @Override
            public void onItemClick(ResponceFetchCarList.Response item) {

                String strName = String.valueOf(item.getVehicleType());
                vehicle_id = String.valueOf(item.getVehicleId());
                Log.e("upcoming_dates", "id: " + vehicle_id);


            }
        });


        RecyclerView.LayoutManager mmLayoutManager = new LinearLayoutManager(this);
        recycler_view_car_list.setLayoutManager(mmLayoutManager);
        recycler_view_car_list.setItemAnimator(new DefaultItemAnimator());
        recycler_view_car_list.setAdapter(adapterCarList);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_book_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpPrefarence();
            }
        });

        FetchCarList();


    }
    private void FetchCarList() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();



        RequestBody pickupLat = RequestBody.create(MediaType.parse("text/plain"),"22.5016" );
        RequestBody pickupLong = RequestBody.create(MediaType.parse("text/plain"), "88.3209");
        RequestBody DropLat = RequestBody.create(MediaType.parse("text/plain"),"22.5839" );
        RequestBody DropLong = RequestBody.create(MediaType.parse("text/plain"),"88.3434" );


        RestClient.getClient().fetchCarlist(pickupLat,pickupLong,DropLat,DropLong).enqueue(new Callback<ResponceFetchCarList>() {
            @Override
            public void onResponse(Call<ResponceFetchCarList> call, Response<ResponceFetchCarList> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();

                    ResponceFetchCarList listResponse = response.body();


                    assert listResponse != null;

                    for (ResponceFetchCarList.Response list : listResponse.getResponse()) {
                        Log.e(TAG, "onResponse: block LIst " + list);
                        CarList.add(list);
                        Log.e(TAG, "onResponse: Blick List :" + list);


                    }


                    adapterCarList.notifyDataSetChanged();
                    Log.e(TAG, "onResponse: Size Of Array ::" + CarList.size());

                } else  {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponceFetchCarList> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void popUppayment() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_payment_choose, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        Button btn_pay_now = promptsView.findViewById(R.id.btn_pay_now);
        Button btn_pay_later = promptsView.findViewById(R.id.btn_pay_later);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent in=new Intent(ChooseRideActivity.this,DriverNearbyActivity.class);
//                startActivity(in);
//                finish();
  //              Intent in = new Intent(ChooseRideActivity.this, StripeAccountActivity.class);
//                startActivity(in);
//                finish();

                if (Status.equalsIgnoreCase("1")) {
                    Intent in = new Intent(ChooseRideActivity.this, PaymenyMethodActivity.class);
                    startActivity(in);
                    finish();

                } else if (Status.equalsIgnoreCase("0")) {
                    Intent in = new Intent(ChooseRideActivity.this, StripeAccountActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        });


        btn_pay_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Status.equalsIgnoreCase("1")) {
                    Intent in = new Intent(ChooseRideActivity.this, DriverChoiceActivity.class);
                    in.putExtra("vehicle_id",vehicle_id);
                    startActivity(in);
                    finish();

                } else if (Status.equalsIgnoreCase("0")) {
                    Intent in = new Intent(ChooseRideActivity.this, DriverNearbyActivity.class);
                    in.putExtra("vehicle_id",vehicle_id);
                    startActivity(in);
                    finish();
                }


            }
        });


        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    private void popUpPrefarence() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_driver_choice, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        Button btn_preferr = promptsView.findViewById(R.id.btn_preferr);
        Button btn_next = promptsView.findViewById(R.id.btn_next);


//        TextView txt_cancel = (TextView) promptsView.findViewById(R.id.txt_cancel);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);



        btn_preferr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent in=new Intent(ChooseRideActivity.this,DriverNearbyActivity.class);
//                startActivity(in);
//                finish();
                alertDialog1.dismiss();
//                popUppayment();
                Status = "1";

              //Add data to local database

                SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.DRIVER_PREF, "1");
                editor.apply();

                Intent in = new Intent(ChooseRideActivity.this, DriverChoiceActivity.class);
                in.putExtra("vehicle_id",vehicle_id);
                startActivity(in);
                finish();

            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog1.dismiss();
                Status = "0";
                popUppayment();
                //Add data to local database

                SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.DRIVER_PREF, "0");
                editor.apply();
            }
        });


        // create alert dialog
        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.setCancelable(false);
        // show it
        alertDialog1.show();
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