package com.user.frenzi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frenzi.R;
import com.google.android.libraries.places.widget.Autocomplete;
import com.user.frenzi.adapter.PlacesAutoCompleteAdapter;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAddressActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private static final String TAG = "SearchAddressActivity";
    public static String mGetAddress,mGetAddress2, mLat, mLng;
    RecyclerView placesRecyclerView;
    ImageView back_iv;
    LinearLayout right_Ll;
    EditText address_edt,edt_building;
    TextView txtCaption;
    String ComingFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.DeepPink));
        }

        Places.initialize(SearchAddressActivity.this, "AIzaSyAArqoAoVs9i0M_g2p6i87apwECkHdtI-M");


        initControls();
    }
    private void initControls() {


        placesRecyclerView=findViewById(R.id.places_recycler_view);
        back_iv=findViewById(R.id.back_iv);
        right_Ll=findViewById(R.id.right_Ll);
        address_edt=findViewById(R.id.address_edt);
        txtCaption=findViewById(R.id.txt_caption);
        edt_building=findViewById(R.id.edt_building);

        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            ComingFrom= intent.getStringExtra("data");
        }

        functions();


    }

    @SuppressLint("ClickableViewAccessibility")
    private void functions() {

        address_edt.requestFocus();

       back_iv.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               finish();
               return false;
           }
       });
        right_Ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!address_edt.getText().toString().isEmpty()) {
                    if (ComingFrom.equalsIgnoreCase("address_1")) {
                        if(edt_building.getText().toString().isEmpty()){
                            mGetAddress =  address_edt.getText().toString();
                        }else {
                            mGetAddress = edt_building.getText().toString() + ", " + address_edt.getText().toString();
                        }

                        hideSoftKeyboard(SearchAddressActivity.this);
                        System.out.println("get data------>" + mGetAddress);
                        Log.e(TAG, "onClick: Checked 1 ::" + mGetAddress);
                        finish();
                    } else {
                        if(edt_building.getText().toString().isEmpty()){
                            mGetAddress2 =  address_edt.getText().toString();
                        }else {
                            mGetAddress2 = edt_building.getText().toString() + ", " + address_edt.getText().toString();
                        }
                        hideSoftKeyboard(SearchAddressActivity.this);
                        System.out.println("get data------>" + mGetAddress2);
                        Log.e(TAG, "onClick: Checked 2 ::" + mGetAddress2);
                        finish();
                    }
                }else{
                    Toast.makeText(SearchAddressActivity.this, "Please enter the address", Toast.LENGTH_LONG).show();
                }

            }
        });


        address_edt.addTextChangedListener(filterTextWatcher);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(SearchAddressActivity.this);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(SearchAddressActivity.this));
        mAutoCompleteAdapter.setClickListener(this);
        placesRecyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

    }


    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (placesRecyclerView.getVisibility() == View.GONE) {
                    placesRecyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                if (placesRecyclerView.getVisibility() == View.VISIBLE) {
                    placesRecyclerView.setVisibility(View.GONE);
                }
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    };

    @Override
    public void click(Place place) {
       // Toast.makeText(this, place.getAddress()+", "+place.getLatLng().latitude+place.getLatLng().longitude+place.getAddress(), Toast.LENGTH_SHORT).show();



        Log.e(TAG, "click: place :"+ place.getAddress());
        Log.e(TAG, "click: postCode :"+place.getAddressComponents() );
        Log.e(TAG, "click: placeee :"+address_edt.getText().toString().length());
        String add = place.getAddress();

        String someJson ="([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";

        Pattern p = Pattern.compile(someJson);
        assert add != null;
        Matcher m = p.matcher(add);
        if (m.find()) {
            String post = m.group(0);
            Log.e(TAG, "match: post :"+post );

        }


        mLat = String.valueOf(Objects.requireNonNull(place.getLatLng()).latitude);
        mLng = String.valueOf(place.getLatLng().longitude);
        address_edt.setText( place.getAddress());
        hideSoftKeyboard(SearchAddressActivity.this);
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
      //  inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_PLACE_ADDRESS && resultCode == Activity.RESULT_OK) {
//            Place place = Autocomplete.getPlaceFromIntent(data);
////            Log.e("Data",Place_Data: Name: " + place.getName() + "\tLatLng: " + place.getLatLng() + "\tAddress: " + place.getAddress() + "\tAddress Component: " + place.getAddressComponents());
//
//            try {
//                List<Address> addresses;
//               Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//
//                try {
//                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                    String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                    String address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//
//                    Log.e("Address1: ", "" + address1);
//                    Log.e("Address2: ", "" + address2);
//                    Log.e("AddressCity: ", "" + city);
//                    Log.e("AddressState: ", "" + state);
//                    Log.e("AddressCountry: ", "" + country);
//                    Log.e("AddressPostal: ", "" + postalCode);
//                    Log.e("AddressLatitude: ", "" + place.getLatLng().latitude);
//                    Log.e("AddressLongitude: ", "" + place.getLatLng().longitude);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                //setMarker(latLng);
//            }
//        }
//    }
}