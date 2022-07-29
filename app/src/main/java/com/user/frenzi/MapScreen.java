package com.user.frenzi;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.frenzi.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

public class MapScreen extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener{

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    private static final String TAG = "MapScreen";

    FloatingActionButton btn_current_location;
    LatLng latLng;
    LinearLayout btn_destination;
    LatLng a = null;
    private Circle circle;
    private GoogleMap mMap;
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    Activity activity;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    ImageView menu_bar;

    GoogleApiClient googleApiClient;
    String user_Name, user_Image;

//    @Override
//    protected void onPause() {
//        super.onPause();
//        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        setContentView(R.layout.activity_map_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        user_Name = spp.getString(Constant.USER_NAME, "");
        user_Image = spp.getString(Constant.USER_IMAGE, "");

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//        addCustomMarker();
        btn_current_location = findViewById(R.id.btn_current_location);
        btn_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
                moveMap();
            }
        });
        drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.setVisibility(View.INVISIBLE);

        final ViewTreeObserver viewTreeObserver = drawerLayout.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    circularRevealTransition(); //Function To start the animation
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        drawerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        drawerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(getResources().getColor(R.color.Black));
//        }
        menu_bar = findViewById(R.id.menu_bar);
        btn_destination = findViewById(R.id.btn_destination);
        btn_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent search = new Intent(MapScreen.this, Wheretogo.class);
                startActivity(search);
            }
        });
        menu_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        // GetLocation
//        getCurrentLocation();
//        moveMap();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case LOCATION_REQUEST_CODE: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //if permission granted.
//                    locationPermission = true;
//                    getCurrentLocation();
//                    moveMap();
//
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//        }
//    }

//    //to get user location
//    private void getMyLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(false);
//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
////                Findroutes(a,b,c);
//////                myLocation=location;
////                LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                        a, 16f);
//                mMap.animateCamera(cameraUpdate);
//            }
//        });
//
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
////        LatLng pos = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
////        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18.0f));
//        if (locationPermission) {
//            getMyLocation();
//        }
//
//
////        circle = mMap.addCircle(new CircleOptions()
////                .center(a)
////                .radius(40)
////                .strokeWidth(2)
////
////                .strokeColor(Color.YELLOW)
////                .fillColor(R.color.yellow_project_trans));
//
//        googleMap.addMarker(new MarkerOptions().position(a).title("Me").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_j)));
//
//// Uses a custom icon.
//
////        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
////
////            @Override
////            public void onCircleClick(Circle circle) {
////                // Flip the r, g and b components of the circle's
////                // stroke color.
////                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
////                circle.setStrokeColor(strokeColor);
////            }
////        });
//
//        //Set Focus
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                a, 18f);
//        mMap.animateCamera(cameraUpdate);
//        MapsInitializer.initialize(this);
////        addCustomMarker();
//
//    }
//
//    private void requestPermision() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_REQUEST_CODE);
//        } else {
//            locationPermission = true;
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

    private void addCustomMarker() {
        if (mMap == null) {
            return;
        }

        // adding a marker on map with image from  drawable
        mMap.addMarker(new MarkerOptions()
                .position(a)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.map_j))));
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(null, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.img_profile);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    @Override
//    public void onBackPressed() {
//        int orientation = this.getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
////            getSupportActionBar().show();
//            View windowDecorView = getWindow().getDecorView();
////            windowDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            windowDecorView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
//
//
//        } else {
//
//            if (getFragmentManager().getBackStackEntryCount() > 0) {
//                getFragmentManager().popBackStack();
//            }
////        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
////            drawerLayout.closeDrawer(GravityCompat.START);
////        }
//
//
//            else {
//                super.onBackPressed();
//            }
//        }
//
//        // If drawer is already close -- Do not override original functionality
//
//
//    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout book_your_ride = findViewById(R.id.book_your_ride);
        LinearLayout payments = findViewById(R.id.payments);

        LinearLayout your_ride_layout = findViewById(R.id.your_ride_layout);
        LinearLayout refer_layout = findViewById(R.id.refer_layout);
        CircularImageView img_profile = findViewById(R.id.img_profile);
        LinearLayout btn_logout = findViewById(R.id.btn_logout);
        LinearLayout btn_contact_us = findViewById(R.id.btn_contact_us);
        LinearLayout btn_about = findViewById(R.id.btn_about);
        TextView txt = findViewById(R.id.txt);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.menu);

        txt.setText(user_Name);
        RequestOptions options = new RequestOptions()
                .centerInside()
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile);
        Glide.with(MapScreen.this).load(user_Image).apply(options).into(img_profile);

        book_your_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent1 = new Intent(MapScreen.this, MapScreen.class);
                startActivity(intent1);
            }
        });
        payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent1 = new Intent(MapScreen.this, StripeAccountActivity.class);
                startActivity(intent1);
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent1 = new Intent(MapScreen.this, ProfileDetailActivity.class);
                startActivity(intent1);
            }
        });

        refer_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent1 = new Intent(MapScreen.this, InviteCodeActivity.class);
                startActivity(intent1);
            }
        });

        your_ride_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent1 = new Intent(MapScreen.this, RideHistoryActivity.class);
                startActivity(intent1);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                showDialog();
            }
        });
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                Intent intent222 = new Intent(MapScreen.this, AboutUsActivity.class);
                startActivity(intent222);

            }
        });
        btn_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                Intent intent1 = new Intent(MapScreen.this, ContactUsActivity.class);
                startActivity(intent1);
            }
        });
//        if (drawerToggle == null) {
//            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
//                public void onDrawerClosed(View view) {
//
//                }
//
//                public void onDrawerOpened(View drawerView) {
//
//                }
//
//                public void onDrawerSlide (View drawerView, float slideOffset) {
////                    CategoryListVertical();
//                }
//
//                public void onDrawerStateChanged(int newState) {
//
//                }
//
//            };
//            drawerLayout.setDrawerListener(drawerToggle);
//        }
//
//        drawerToggle.syncState();

//        CategoryListVertical();
//        String [] numbers = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
//         itemArrayAdapter = new ItemArrayAdapter(this, R.layout.list_item, numbers);
//        recyclerview_vertical.setAdapter(adapterVerticalCatagoryList);
    }


    void showDialog() {
        //if(activity != null) {
            final Dialog dialog = new Dialog(MapScreen.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_logout);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            final TextView mTxt_Cancel = dialog.findViewById(R.id.txt_cancel);
            final TextView mTxt_Delete = dialog.findViewById(R.id.txt_logout);

            mTxt_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            mTxt_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                mAppManager.logoutUser();
                    SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                    sp.edit().remove(Constant.USER_ID)
                            .remove(Constant.USER_NAME)
                            .remove(Constant.USER_ADDRESS)
                            .remove(Constant.USER_MAIL)
                            .remove(Constant.USER_IMAGE)
                            .apply();
                    dialog.dismiss();

                    Intent intent=new Intent(MapScreen.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            dialog.show();
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
//        mMap.clear();
//
//        //Adding a new marker to the current pressed position
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }
    private void circularRevealTransition() {
        int X = 9 * drawerLayout.getWidth()/10; //The X coordinate for the initial position
        int Y = 9 * drawerLayout.getHeight()/10; //The Y coordinate for the initial position
        int Duration = 500;  //Duration for the animation

        float finalRadius = Math.max(drawerLayout.getWidth(), drawerLayout.getHeight()); //The final radius must be the end points of the current activity

        // create the animator for this view, with the start radius as zero
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(drawerLayout, X, Y, 0, finalRadius);
        circularReveal.setDuration(Duration);

        // set the view visible and start the animation
        drawerLayout.setVisibility(View.VISIBLE);
        // start the animation
        circularReveal.start();
    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
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

//    @Override
//    public void onClick(View v) {
//        if(v == buttonCurrent){
//            getCurrentLocation();
//            moveMap();
//        }
//    }
}
