package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.user.frenzi.Responce.ResponceFetchCarList;
import com.user.frenzi.adapter.AdapterCarList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        LatLng latLng,dlatLng;
private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    String date_to_send;
    String Status = null;
    ImageView btn_back;
    Button btn_book_now;
    AlertDialog alertDialog, alertDialog1;
    RadioGroup radio_group;
    RecyclerView recycler_view_car_list;
    AdapterCarList adapterCarList;
    private List<ResponceFetchCarList.Response> CarList = new ArrayList<>();
    String vehicle_id = "0",User_ID;
    private static final String TAG = "Choosecar";
    String drop_add,pickup_add;
    String pickup_lat, pickup_long, drop_lat, drop_long;
    FloatingActionButton btn_current_location;
    String driver_prefer;
    String postcode_drop, postcode_pick;
    String pattern ="([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";


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
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ride);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        User_ID = spp.getString(Constant.USER_ID, "");
        drop_add = getIntent().getStringExtra("drop_add");
        pickup_add = getIntent().getStringExtra("pickup_add");
        pickup_lat = getIntent().getStringExtra("pickup_lat");
        pickup_long = getIntent().getStringExtra("pickup_long");
        drop_lat = getIntent().getStringExtra("drop_lat");
        drop_long = getIntent().getStringExtra("drop_long");

        Log.d(TAG, "onCreate: pickup_lat > "+pickup_lat);
        Log.d(TAG, "onCreate: pickup_long > "+pickup_long);
        Log.d(TAG, "onCreate: drop_lat > "+drop_lat);
        Log.d(TAG, "onCreate: drop_long > "+drop_long);
        Log.e(TAG, "onCreate: pickup_add > "+pickup_add);
        Log.e(TAG, "onCreate: drop_add > "+drop_add);
      //  String add = place.getAddress();


        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(pickup_add);
        if (m.find()) {
            postcode_pick = m.group(0);
            Log.e(TAG, "match: postcode_pick :"+postcode_pick );

        }

      //  String pattern ="([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";

       // Pattern p_d = Pattern.compile(pattern);
        Matcher m_d = p.matcher(drop_add);
        if (m_d.find()) {
            postcode_drop = m_d.group(0);
            Log.e(TAG, "match: postcode_drop :"+postcode_drop );

        }


        btn_back = findViewById(R.id.btn_back);
        btn_book_now = findViewById(R.id.btn_book_now);
        recycler_view_car_list=findViewById(R.id.recycler_view_car_list);
        btn_current_location=findViewById(R.id.btn_current_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //getCurrentLocation();
       // moveMap();


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
                if (!vehicle_id.equals("0")){
                    popUpPreference();
                }else{
                    Toast.makeText(ChooseRideActivity.this,"Please select any vehicle",Toast.LENGTH_LONG).show();

                }



            }
        });

        btn_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
                moveMap();
            }
        });

        FetchCarList();


    }
    public void datePicker(TextView textView) {
        final Calendar c = Calendar.getInstance();
        int  mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(ChooseRideActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in text-box
                        String date_val = dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year;
                        //  String formattedDate = df1.format(date);

                        textView.setText(date_val);
                         date_to_send = year + "-" +(monthOfYear + 1) + "-" + dayOfMonth;
                      //  FetchRideHistory(from_date, to_date);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }

    public void showTime(final TextView textView) {

        final Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String am_pm = "";
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                if (myCalendar.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "AM";
                else if (myCalendar.get(Calendar.AM_PM) == Calendar.PM)
                    am_pm = "PM";
                String strHrsToShow = (myCalendar.get(Calendar.HOUR) == 0) ? "12"
                        : myCalendar.get(Calendar.HOUR) + "";
                //UIHelper.showLongToastInCenter(context, strHrsToShow + ":" + myCalendar.get(Calendar.MINUTE) + " " + am_pm);
                textView.setText(strHrsToShow + ":" + myCalendar.get(Calendar.MINUTE) + " " + am_pm);
            }
        };
        new TimePickerDialog(ChooseRideActivity.this, mTimeSetListener, myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE), false).show();
    }

    private void popUpPayment() {

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
//                finish()
//                Intent in = new Intent(ChooseRideActivity.this, StripeAccountActivity.class);
//                startActivity(in);
//                finish();

                if (Status.equalsIgnoreCase("preferred")) {
                    alertDialog.dismiss();
                    Intent in = new Intent(ChooseRideActivity.this, PaymentMethodActivity.class);
                    startActivity(in);
                    finish();

                } else if (Status.equalsIgnoreCase("next_available")) {
                    alertDialog.dismiss();
                    Intent in = new Intent(ChooseRideActivity.this, StripeAccountActivity.class);
                    startActivity(in);
                    finish();
                }


            }
        });


        btn_pay_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Status.equalsIgnoreCase("preferred")) {
                    alertDialog.dismiss();

                    Intent in = new Intent(ChooseRideActivity.this, DriverChoiceActivity.class);
                    in.putExtra("vehicle_id",vehicle_id);
                    startActivity(in);
                    finish();

                } else if (Status.equalsIgnoreCase("next_available")) {
                    alertDialog.dismiss();
                    popUpConfirmBooking();
                }



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
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                popUpLater();

           /*     Intent intent = new Intent(ChooseRideActivity.this, MapScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
            }
        });




        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    private void popUpLater() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_ride_later, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        TextView tv_date =promptsView.findViewById(R.id.tv_date);
        TextView tv_time =promptsView.findViewById(R.id.tv_time);
        Button btn_book =promptsView.findViewById(R.id.btn_book);
        Button btn_cancel =promptsView.findViewById(R.id.btn_cancel);


        alertDialogBuilder.setView(promptsView);
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alertDialog.dismiss();
                showTime(tv_time);

            }
        });

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alertDialog.dismiss();
                datePicker(tv_date);

            }
        });
        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                //String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String currentTime = tv_time.getText().toString();
                String currentDate = date_to_send;

                Log.e(TAG, "onClick:currentDate "+currentDate );
                Log.e(TAG, "onClick:currentTime "+currentTime );

                ride_later(currentDate,currentTime);



            }
        });
         btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();

                      //  popUpLater();

                        Intent intent = new Intent(ChooseRideActivity.this, MapScreen.class);
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

    private void popUpPreference() {

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
                Status = "preferred";

              //Add data to local database


                popUpChoosePreference();
             /*   SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.DRIVER_PREF, "1");
                editor.apply();

                Intent in = new Intent(ChooseRideActivity.this, DriverChoiceActivity.class);
                in.putExtra("vehicle_id",vehicle_id);
                in.putExtra("drop_add",drop_add);
                in.putExtra("pickup_add",pickup_add);
                in.putExtra("pickup_lat",pickup_lat);
                in.putExtra("pickup_long",pickup_long);
                in.putExtra("drop_lat",drop_lat);
                in.putExtra("drop_long",drop_long);

                startActivity(in);
                finish();*/

            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog1.dismiss();
                Status = "next_available";
            //    popUpPayment();
                popUpChoosePreferenceNext();

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

    private void popUpChoosePreference() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_choose_preference, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        TextView tv_next = promptsView.findViewById(R.id.tv_next);
        RadioButton rb_male_only = promptsView.findViewById(R.id.rb_male_only);
        RadioButton rb_female_only = promptsView.findViewById(R.id.rb_female_only);
        RadioButton rb_male_female = promptsView.findViewById(R.id.rb_male_female);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);



        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog1.dismiss();
                Status = "preferred";

                if (rb_male_female.isChecked()) {
                    driver_prefer = "any";
                } else if (rb_female_only.isChecked()) {
                    driver_prefer = "female";
                } else if (rb_male_only.isChecked()) {
                    driver_prefer = "male";
                }

                //Add data to local database

                SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.DRIVER_PREF, "1");
               // editor.putString(Constant.BOOKING, "yes");
                editor.apply();

                Intent in = new Intent(ChooseRideActivity.this, DriverChoiceActivity.class);
                in.putExtra("vehicle_id",vehicle_id);
                in.putExtra("drop_add",drop_add);
                in.putExtra("pickup_add",pickup_add);
                in.putExtra("pickup_lat",pickup_lat);
                in.putExtra("pickup_long",pickup_long);
                in.putExtra("drop_lat",drop_lat);
                in.putExtra("drop_long",drop_long);
                in.putExtra("driver_prefer",driver_prefer);
                in.putExtra("pickup_postcode",postcode_pick);
                in.putExtra("drop_postcode",postcode_drop);

                startActivity(in);
                finish();

            }
        });


      /*  btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog1.dismiss();
                Status = "next_available";
                popUpPayment();
                //Add data to local database

                SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.DRIVER_PREF, "0");
                editor.apply();
            }
        });*/


        // create alert dialog
        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.setCancelable(false);
        // show it
        alertDialog1.show();
    }

    private void popUpChoosePreferenceNext() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_choose_preference, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this
        );

        TextView tv_next = promptsView.findViewById(R.id.tv_next);
        RadioButton rb_male_only = promptsView.findViewById(R.id.rb_male_only);
        RadioButton rb_female_only = promptsView.findViewById(R.id.rb_female_only);
        RadioButton rb_male_female = promptsView.findViewById(R.id.rb_male_female);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);



        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog1.dismiss();
                Status = "preferred";

                if (rb_male_female.isChecked()) {
                    driver_prefer = "any";
                } else if (rb_female_only.isChecked()) {
                    driver_prefer = "female";
                } else if (rb_male_only.isChecked()) {
                    driver_prefer = "male";
                }

                //Add data to local database
               // popUpPayment();
                popUpConfirmBooking();
               /* SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.DRIVER_PREF, "1");
                // editor.putString(Constant.BOOKING, "yes");
                editor.apply();

                Intent in = new Intent(ChooseRideActivity.this, DriverChoiceActivity.class);
                in.putExtra("vehicle_id",vehicle_id);
                in.putExtra("drop_add",drop_add);
                in.putExtra("pickup_add",pickup_add);
                in.putExtra("pickup_lat",pickup_lat);
                in.putExtra("pickup_long",pickup_long);
                in.putExtra("drop_lat",drop_lat);
                in.putExtra("drop_long",drop_long);
                in.putExtra("driver_prefer",driver_prefer);
                startActivity(in);
                finish();*/

            }
        });


      /*  btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog1.dismiss();
                Status = "next_available";
                popUpPayment();
                //Add data to local database

                SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.DRIVER_PREF, "0");
                editor.apply();
            }
        });*/


        // create alert dialog
        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.setCancelable(false);
        // show it
        alertDialog1.show();
    }

    private void FetchCarList() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();

        Log.d(TAG, "FetchCarList: pickup_lat > "+pickup_lat);
        Log.d(TAG, "FetchCarList: pickup_long > "+pickup_long);
        Log.d(TAG, "FetchCarList: drop_lat > "+drop_lat);
        Log.d(TAG, "FetchCarList: drop_long > "+drop_long);
        Log.d(TAG, "FetchCarList: postcode_pick > "+postcode_pick);

        RequestBody pickupLat = RequestBody.create(MediaType.parse("text/plain"),pickup_lat );
        RequestBody pickupLong = RequestBody.create(MediaType.parse("text/plain"), pickup_long);
        RequestBody DropLat = RequestBody.create(MediaType.parse("text/plain"),drop_lat );
        RequestBody DropLong = RequestBody.create(MediaType.parse("text/plain"),drop_long );
        RequestBody pickup_postcode = RequestBody.create(MediaType.parse("text/plain"),postcode_pick );


        RestClient.getClient().fetchCarlist(pickupLat,pickupLong,
                        DropLat,DropLong,pickup_postcode)
                .enqueue(new Callback<ResponceFetchCarList>() {
            @Override
            public void onResponse(Call<ResponceFetchCarList> call,
                                   Response<ResponceFetchCarList> response)
            {
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

    private void post_ride(String currentDate, String currentTime) {
        // Tag used to cancel the request
        final String tag_string_req = "post_ride";
        ACProgressFlower dialog = new ACProgressFlower.Builder(ChooseRideActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();

        String url = "https://mobileappsgamesstudio.com/works/frenzi_new/api/post_ride";
        Log.d(TAG, "url>>>  "+url);
        try{
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new com.android.volley.Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "change_password Response: " + response);
                    dialog.dismiss();

                    Gson gson = new Gson();

                    try {

                        JsonObject jobj = gson.fromJson(response, JsonObject.class);
                        String status = jobj.get("status").getAsString().replaceAll("\"", "");
                        String message = jobj.get("message").getAsString().replaceAll("\"", "");



                        if (status.equals("200")){
                           /* SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                           // editor.putString(Constant.DRIVER_PREF, "1");
                            editor.putString(Constant.BOOKING, "yes");
                            editor.apply();*/
                            JsonObject response1 = jobj.getAsJsonObject("response");
                            String ride_id  = response1.get("ride_id ").getAsString().replaceAll("\"", "");
                            Log.e(TAG, "onResponse:ride_id "+ride_id);
                            Intent in = new Intent(ChooseRideActivity.this, DriverNearbyActivity.class);
                            in.putExtra("ride_id",ride_id);
                            in.putExtra("vehicle_id",vehicle_id);
                            in.putExtra("pickup_add",pickup_add);
                            in.putExtra("drop_add",drop_add);
                            in.putExtra("pickup_lat",pickup_lat);
                            in.putExtra("pickup_long",pickup_long);
                            in.putExtra("drop_lat",drop_lat);
                            in.putExtra("drop_long",drop_long);
                            in.putExtra("pickup_postcode",postcode_pick);
                            in.putExtra("drop_postcode",postcode_drop);
                            startActivity(in);
                            finish();
                        }else {

                            Toast.makeText(ChooseRideActivity.this,message,Toast.LENGTH_LONG).show();

                            dialog.dismiss();
//

                        }


                    }catch (Exception e){
                        e.printStackTrace();

                    }

                }
            }, new com.android.volley.Response.ErrorListener(){

                @Override

                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "change_password Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Connection Error", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    //  mView.hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", User_ID);

                    params.put("driver_id", "0");
                    params.put("pickup_address", pickup_add);
                    params.put("drop_address", drop_add);
                    params.put("pickup_lat", pickup_lat);
                    params.put("pickup_long", pickup_long);
                    params.put("drop_lat", drop_lat);
                    params.put("drop_long", drop_long);
                    params.put("start_date", currentDate);
                    params.put("start_time", currentTime);
                    params.put("pickup_postcode", postcode_pick);
                    params.put("drop_postcode", postcode_drop);




                    return params;
                }
            };

            this.addToRequestQueue(ChooseRideActivity.this, strReq, tag_string_req);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ride_later(String currentDate, String currentTime) {
        // Tag used to cancel the request
        final String tag_string_req = "ride_later";
        ACProgressFlower dialog = new ACProgressFlower.Builder(ChooseRideActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();

        String url = "https://mobileappsgamesstudio.com/works/frenzi_new/api/ride_later";
        Log.d(TAG, "url>>>  "+url);
        try{
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new com.android.volley.Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "ride_later Response: " + response);
                    dialog.dismiss();

                    Gson gson = new Gson();

                    try {

                        JsonObject jobj = gson.fromJson(response, JsonObject.class);
                        String status = jobj.get("status").getAsString().replaceAll("\"", "");
                        String message = jobj.get("message").getAsString().replaceAll("\"", "");



                        if (status.equals("200")){

                            JsonObject response1 = jobj.getAsJsonObject("response");
                            String ride_id  = response1.get("ride_id ").getAsString().replaceAll("\"", "");
                            Log.e(TAG, "onResponse:ride_id "+ride_id);
                            Intent in = new Intent(ChooseRideActivity.this, DriverNearbyActivity.class);
                            in.putExtra("ride_id",ride_id);
                            in.putExtra("vehicle_id",vehicle_id);
                            in.putExtra("pickup_add",pickup_add);
                            in.putExtra("drop_add",drop_add);
                            in.putExtra("pickup_lat",pickup_lat);
                            in.putExtra("pickup_long",pickup_long);
                            in.putExtra("drop_lat",drop_lat);
                            in.putExtra("drop_long",drop_long);
                            in.putExtra("pickup_postcode",postcode_pick);
                            in.putExtra("drop_postcode",postcode_drop);
                            startActivity(in);
                            finish();
                        }else {

                            Toast.makeText(ChooseRideActivity.this,message,Toast.LENGTH_LONG).show();

                            dialog.dismiss();
//

                        }


                    }catch (Exception e){
                        e.printStackTrace();

                    }

                }
            }, new com.android.volley.Response.ErrorListener(){

                @Override

                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "change_password Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Connection Error", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    //  mView.hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", User_ID);

                    params.put("driver_id", "0");
                    params.put("pickup_address", pickup_add);
                    params.put("drop_address", drop_add);
                    params.put("pickup_lat", pickup_lat);
                    params.put("pickup_long", pickup_long);
                    params.put("drop_lat", drop_lat);
                    params.put("drop_long", drop_long);
                    params.put("start_date", currentDate);
                    params.put("start_time", currentTime);
                    params.put("pickup_postcode", postcode_pick);
                    params.put("drop_postcode", postcode_drop);




                    return params;
                }
            };

            this.addToRequestQueue(ChooseRideActivity.this, strReq, tag_string_req);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*---------Map Implementation-------------*/
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

     //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      //  mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pickup_lat), Double.parseDouble(pickup_long)), 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);


        BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.navigation);
        Bitmap b1=bitmapdraw1.getBitmap();
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 84, 84, false);
        // adding a marker on map with image from  drawable
        mMap.addMarker(new MarkerOptions()
                .position(dlatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker1)));


       /* mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(drop_lat), Double.parseDouble(drop_long)), 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
*/
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

            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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


    /*private void post_ride(String currentDate, String currentTime) {
     *//* if(TextUtils.isEmpty(edt_full_name.getText().toString().trim())) {
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
*//*
        ACProgressFlower dialog = new ACProgressFlower.Builder(ChooseRideActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.show();

        int dr_id =0;


        RequestBody post_user_id = RequestBody.create(MediaType.parse("txt/plain"), User_ID);
        RequestBody post_driver_id = RequestBody.create(MediaType.parse("txt/plain"), "00");
        RequestBody post_pickup_address = RequestBody.create(MediaType.parse("txt/plain"), pickup_add );
        RequestBody post_drop_address = RequestBody.create(MediaType.parse("txt/plain"), drop_add );
        RequestBody post_pickup_lat = RequestBody.create(MediaType.parse("txt/plain"), pickup_lat );
        RequestBody post_pickup_long = RequestBody.create(MediaType.parse("txt/plain"), pickup_long );
        RequestBody post_drop_lat = RequestBody.create(MediaType.parse("txt/plain"), drop_lat );
        RequestBody post_drop_long = RequestBody.create(MediaType.parse("txt/plain"), drop_long );
        RequestBody post_start_date = RequestBody.create(MediaType.parse("txt/plain"), currentDate );
        RequestBody post_start_time = RequestBody.create(MediaType.parse("txt/plain"), currentTime );
        Log.e(TAG, "post_ride:post_user_id> "+User_ID );
       // Log.e(TAG, "post_ride:post_driver_id> "+//driver_id );
       *//* Log.e(TAG, "post_ride:post_user_id> "+User_ID );
       // Log.e(TAG, "post_ride:post_driver_id> "+driver_id );
        Log.e(TAG, "post_ride:post_pickup_address> "+pickup_add );
        Log.e(TAG, "post_ride:post_drop_address> "+drop_add );
        Log.e(TAG, "post_ride:post_pickup_lat> "+pickup_lat );
        Log.e(TAG, "post_ride:post_pickup_long> "+pickup_long );
        Log.e(TAG, "post_ride:post_drop_lat> "+drop_lat );
        Log.e(TAG, "post_ride:post_drop_long> "+drop_long );
        Log.e(TAG, "post_ride:post_start_date> "+currentDate );
        Log.e(TAG, "post_ride:post_start_time> "+currentTime );*//*
       // Gson gson = new Gson();
        RestClient.getClient().SubmitRide(post_user_id,post_driver_id,post_pickup_address,
                        post_drop_address, post_pickup_lat,post_pickup_long,
                        post_drop_lat,post_drop_long,
                        post_start_date,post_start_time)
                .enqueue(new Callback<ResponseNewRideDetails>() {
                    @Override
                    public void onResponse(Call<ResponseNewRideDetails> call,
                                           Response<ResponseNewRideDetails> response) {
                        Log.e(TAG, "onResponse: Url :" + response);
                        Log.e(TAG, "onResponse: " + response.code());
                        Log.e(TAG, "onResponse: " + response.message());

                        assert response.body() != null;
                        if (response.body().getStatus().equals(200)) {
                            dialog.dismiss();
                            Log.e(TAG, "onResponse:driver_id "+response.body().getResponse().getDriverId() );
                            Log.e(TAG, "onResponse:user_id "+response.body().getResponse().getUserId() );

                            Intent in = new Intent(ChooseRideActivity.this, DriverNearbyActivity.class);
                            in.putExtra("ride_id",response.body().getResponse().getRide_id());
                            in.putExtra("vehicle_id",vehicle_id);
                            in.putExtra("pickup_add",pickup_add);
                            in.putExtra("drop_add",drop_add);
                            in.putExtra("pickup_lat",pickup_lat);
                            in.putExtra("pickup_long",pickup_long);
                            in.putExtra("drop_lat",drop_lat);
                            in.putExtra("drop_long",drop_long);
                            startActivity(in);
                            finish();


*//*

                    Intent cancel=new Intent(DriverChoiceActivity.this,EmergencyTwoActivity.class);
                    startActivity(cancel);
                    Toast.makeText(DriverChoiceActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
*//*


                        } else {

                            Toast.makeText(ChooseRideActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

                            dialog.dismiss();
//

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseNewRideDetails> call,
                                          Throwable t) {


                        Log.e(TAG, "onFailure: " );
                        dialog.dismiss();
                    }
                });
    }*/


}