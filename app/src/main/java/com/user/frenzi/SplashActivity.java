
package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.frenzi.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class SplashActivity extends AppCompatActivity {
    final private static int SPLASH_TIME_OUT = 3000;
    //get access to location permission
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Button start_btn;
    private static final String TAG = "Splash Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }


        start_btn = findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                requestPermissions();



            }
        });

//        ImageView img_loader=findViewById(R.id.img_loader);
//        Glide.with(this)
//                .asGif()
//                .load(R.drawable.caranim) //or url
//                .into(new SimpleTarget<GifDrawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
//
//                        resource.start();
//                        //resource.setLoopCount(1);
//                        img_loader.setImageDrawable(resource);
//                    }
//                });
//        new Handler().postDelayed(new Runnable() {
//
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             * want to show case your app logo / company
//             */
//
//            @Override
//            public void run() {
//                // This method will be executed once the timer is over
//                // Start your app main activity
//
////                SharedPreferences prefs = getSharedPreferences(USER_PREF_DATA, MODE_PRIVATE);
////                Log.d(TAG, "onCreate: " + prefs.getBoolean(USER_PREF_PHONE_USER_IS_LOGGED_IN, false));
////                boolean isLoggedIn = prefs.getBoolean(USER_PREF_PHONE_USER_IS_LOGGED_IN, false);
////                if (isLoggedIn) {
//
//                Intent intent2 = new Intent(SplashActivity.this.getApplicationContext(), MainActivity.class);
////                    Log.d(TAG, "Launching Chat Fragment: " + prefs.getBoolean(USER_PREF_PHONE_USER_IS_LOGGED_IN, false));
//                startActivity(intent2);
//                finish();
////                    return;
////                } else {
////                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
////                    startActivity(i);
////                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
////                    // close this activity
////                    finish();
//
//
//
//
//            }
//        }, SPLASH_TIME_OUT);
    }
    private void requestPermissions() {
        // below line is use to request
        // permission in the current activity.
        Dexter.withActivity(this)
                // below line is use to request the number of
                // permissions which are required in our app.
                .withPermissions(
                        // below is the list of permissions
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                // after adding permissions we are
                // calling an with listener method.
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        // this method is called when all permissions are granted
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            // do you work now
                            SharedPreferences spp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                            String User_Id = spp.getString(Constant.USER_ID, "");


                            if (!User_Id.equalsIgnoreCase("")) {
                                Intent intent1 = new Intent(SplashActivity.this, MapScreen.class);
                                startActivity(intent1);
                                finish();

                            } else {
                                Intent intent2 = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent2);
                                finish();
                            }
//                            Toast.makeText(SplashActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently,
                            // we will show user a dialog message.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        // this method is called when user grants some
                        // permission and denies some of them.
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
            // this method is use to handle error
            // in runtime permissions
            @Override
            public void onError(DexterError error) {
                // we are displaying a toast message for error message.
                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }
        })
                // below line is use to run the permissions
                // on same thread and to check the permissions
                .onSameThread().check();
    }
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel();
                // below is the intent from which we
                // are redirecting our user.
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called when
                // user click on negative button.
                dialog.cancel();
            }
        });
        // below line is used
        // to display our dialog
        builder.show();
    }
}

