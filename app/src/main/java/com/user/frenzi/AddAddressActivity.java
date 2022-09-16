package com.user.frenzi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frenzi.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.GeoPoint;
import com.user.frenzi.Responce.ServerGeneralResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener
        /* , GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener */
{

    private static final String TAG = "AddAddressActivity";
    ImageView iv_back;
    TextView tv_add_address;
    Spinner spinner;
    String[] spinner_title = { "Home", "Work", "Other"};
    GoogleApiClient googleApiClient;
    double longitude;
    double latitude;
    LatLng latLng;
    LatLng a = null;
    private Circle circle;
    private GoogleMap mMap;
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;
    Activity activity;
    EditText edt_address, edt_pincode, edt_city, edt_state, edt_country;
    String user_id,title;
    Marker mCurrLocationMarker = null;
    String add_stat;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }

     /*   SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/

        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        user_id = spp.getString(Constant.USER_ID, "");
     //   user_Image = spp.getString(Constant.USER_IMAGE, "");

        spinner=findViewById(R.id.spinner);
        iv_back=findViewById(R.id.iv_back);
        edt_country=findViewById(R.id.edt_country);
        edt_state=findViewById(R.id.edt_state);
        edt_city=findViewById(R.id.edt_city);
        edt_pincode=findViewById(R.id.edt_pincode);
        edt_address=findViewById(R.id.edt_address);
        tv_add_address=findViewById(R.id.tv_add_address);


        spinner.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,spinner_title);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(edt_country.getText().toString().trim())) {
                    edt_country.setError("Please Enter Country");
                    return;

                }/* else if (TextUtils.isEmpty(edt_state.getText().toString().trim())) {
                    edt_state.setError("Please Enter State");
                    return;
                }*/ else if (TextUtils.isEmpty(edt_city.getText().toString().trim())) {
                    edt_city.setError("Please Enter City");
                    return;
                } else if (TextUtils.isEmpty(edt_pincode.getText().toString().trim())) {
                    edt_pincode.setError("Please Enter PinCode");
                    return;
                } else if (TextUtils.isEmpty(edt_address.getText().toString().trim())) {
                    edt_address.setError("Please Enter Address");
                    return;
                }  else {
                    String full_address = edt_address.getText().toString().trim() + ", " +
                            edt_pincode.getText().toString().trim() + ", " +
                            edt_city.getText().toString().trim() + ", " +
                         //   edt_state.getText().toString().trim() + ", " +
                            edt_country.getText().toString().trim();


                    getLocationFromAddress(full_address);
                }

            }
        });

     /*   getCurrentLocation();
        moveMap();*/
    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Log.e(TAG, "onItemSelected: "+spinner_title[position] );
        title = spinner_title[position];

        switch (title) {
            case "Home":
                add_stat = "0";
                break;
            case "Work":
                add_stat = "1";
                break;
            case "Other":
                add_stat = "2";
                break;
        }

       // Toast.makeText(getApplicationContext(),spinner_title[position] , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            String address_lat =  String.valueOf(location.getLatitude());
            String address_long =  String.valueOf(location.getLongitude());

            Log.e(TAG, "onClick:address_lat>   "+address_lat );
            Log.e(TAG, "onClick:address_long>   "+address_long );

            // p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
            //    (double) (location.getLongitude() * 1E6));

                AddAddress(address_lat, address_long, strAddress);



        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void AddAddress(String address_lat, String address_long, String strAddress) {

        ACProgressFlower dialog = new ACProgressFlower.Builder(AddAddressActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();


        RequestBody userId = RequestBody.create(MediaType.parse("txt/plain"), user_id);
        RequestBody post_title = RequestBody.create(MediaType.parse("txt/plain"),title );
        RequestBody post_address = RequestBody.create(MediaType.parse("txt/plain"), strAddress);
        RequestBody post_latitude = RequestBody.create(MediaType.parse("txt/plain"), address_lat);
        RequestBody post_longitude = RequestBody.create(MediaType.parse("txt/plain"), address_long);
        RequestBody address_status = RequestBody.create(MediaType.parse("txt/plain"), add_stat);


        RestClient.getClient().AddAddress(userId,post_title,post_address,post_latitude,post_longitude,address_status).
                enqueue(new Callback<ServerGeneralResponse>() {
                    @Override
                    public void onResponse(Call<ServerGeneralResponse> call, Response<ServerGeneralResponse> response) {
                        Log.e(TAG, "onResponse: Code :" + response.body());
                        Log.e(TAG, "onResponse: " + response.message());
                        Log.e(TAG, "onResponse: " + response.errorBody());

                        assert response.body() != null;
                        if (response.body().getStatus().equals(200)) {
                            dialog.dismiss();

                            Toast.makeText(AddAddressActivity.this,response.body().getMessage(),Toast.LENGTH_SHORT ).show();
                            finish();

                        }else{
                            dialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(Call<ServerGeneralResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });

    }








   /*  @Override
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
            Log.d(TAG, "getCurrentLocation: latitude >> "+latitude);
            Log.d(TAG, "getCurrentLocation: longitude >> "+longitude);

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
        getAddressFromLatLong(latLng);
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
//        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
//        googleMap.addMarker(new MarkerOptions().position(latLng).title("Me").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_j)));

    }
   *//* @Override
    public void onLocationChanged(Location location) {

        double lattitude = location.getLatitude();
        double longitude = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(lattitude, longitude);


        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.setPosition(latLng);
        }else{
            mCurrLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("I am here");
        }

        tv_loc.append("Lattitude: " + lattitude + "  Longitude: " + longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }
*//*
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

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();


        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_j);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 84, 84, false);


        //Adding a new marker to the current pressed position
     *//*   mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
*//*
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        Log.e(TAG, "onMapLongClick: latlong >> "+String.valueOf(latLng ));
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        getAddressFromLatLong(latLng);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }


    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onBackPressed() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int cx = drawerLayout.getWidth() - getDips(44);
//            int cy = drawerLayout.getBottom() - getDips(44);
//
//            float finalRadius = Math.max(drawerLayout.getWidth(), drawerLayout.getHeight());
//            Animator circularReveal = ViewAnimationUtils.createCircularReveal(drawerLayout, cx, cy, finalRadius, 0);
//
//            circularReveal.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animator) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animator) {
//                    drawerLayout.setVisibility(View.INVISIBLE);
//
//                    finish();
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animator) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animator) {
//
//                }
//            });
//            circularReveal.setDuration(3000);
//            circularReveal.start();
//        }
//        else {
//            super.onBackPressed();
//        }
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            getSupportActionBar().show();
            View windowDecorView = getWindow().getDecorView();
//            windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            windowDecorView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);


        } else {

            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }


            else {
                super.onBackPressed();
            }
        }

    }

    public void getAddressFromLatLong(LatLng latLng){

        double lat= latLng.latitude;
        double longi= latLng.longitude;

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

           // String full_add = address+", "+city+", "+state+", "+country;
            edt_address.setText(address);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    */




}
