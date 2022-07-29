package com.user.frenzi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.example.frenzi.R;
import com.user.frenzi.Responce.ProfileResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import dev.ronnie.github.imagepicker.ImagePicker;
import dev.ronnie.github.imagepicker.ImageResult;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDetailActivity extends AppCompatActivity {
    String TAG = "ProfileDetailActivity";
    ImagePicker imagePicker;
    ImageView iv_back,iv_profile,iv_add_image;
    EditText et_user_name,et_user_mobile,et_email,et_address;
    TextView tv_save;
    File profile_image;
    String user_Image,user_Name,userId;
    String profile_image_str, clicked_on_profile = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.gradient));
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileDetailActivity.this,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(ProfileDetailActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
            }
            else{
                if(checkForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 124)){

                }

            }
        }

        SharedPreferences spp = Objects.requireNonNull(getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE));
        user_Name = spp.getString(Constant.USER_NAME, "");
        user_Image = spp.getString(Constant.USER_IMAGE, "");
        userId = spp.getString(Constant.USER_ID, "");
        Log.e(TAG, "onCreate:userId "+userId );

        initControls();

    }

    private void initControls() {

        imagePicker = new ImagePicker(ProfileDetailActivity.this);
        iv_back = findViewById(R.id.iv_back);
        iv_profile = findViewById(R.id.iv_profile);
        iv_add_image = findViewById(R.id.iv_add_image);
        et_user_name = findViewById(R.id.et_user_name);
        et_user_mobile = findViewById(R.id.et_user_mobile);
        et_email = findViewById(R.id.et_email);
        et_address = findViewById(R.id.et_address);
        tv_save = findViewById(R.id.tv_save);

        functions();
        FetchProfile();
    }

    private void functions() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iv_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked_on_profile ="yes";
                selectImage();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick:clicked_on_profile >> "+clicked_on_profile );
                if (clicked_on_profile.equals("no")){
                    UpdateProfileWithoutImage();
                }else {
                    UpdateProfile();
                }
            }
        });
    }

    private void selectImage() {
        try {
            PackageManager pm = ProfileDetailActivity.this.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, ProfileDetailActivity.this.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo","Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();

                            imagePicker.takeFromCamera(imageCallBack());

                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();

                            imagePicker.pickFromStorage(imageCallBack());
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(ProfileDetailActivity.this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ProfileDetailActivity.this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Function1<ImageResult<? extends Uri>, Unit> imageCallBack() {
        return imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
                Log.e(TAG, "imageCallBack: uri>>> "+uri );
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                            uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                saveToInternalStorage(bitmap);

                iv_profile.setImageURI(uri);
                RequestOptions options = new RequestOptions()
                        .centerInside();
                //   .placeholder(R.mipmap.man)
                // .error(R.mipmap.man);
                Glide.with(ProfileDetailActivity.this).load(uri).apply(options).into(iv_profile);

                Log.e(TAG, "imageCallBack:profile_image_str >> "+profile_image_str);

            } else {
                String errorString = ((ImageResult.Failure) imageResult).getErrorString();
                Toast.makeText(ProfileDetailActivity.this, errorString, Toast.LENGTH_LONG).show();
            }
            return null;
        };
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        long tsLong = System.currentTimeMillis()/1000;
        String ts = Long.toString(tsLong);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("DriverFrenzi", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"image" + ts +".jpg");
        Log.e(TAG, "saveToInternalStorage: mypath>> "+mypath );

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profile_image = mypath;
        Log.e(TAG, "saveToInternalStorage: profile_image >> "+profile_image );

        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path) {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //   ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //  img.setImageBitmap(b);


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public void successAlert(String msg){


        Dialog dialog = new Dialog(ProfileDetailActivity.this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dlg_dismiss);
        dialog.setCanceledOnTouchOutside(true);


        TextView tv_alert_msg =  dialog.findViewById(R.id.tv_alert_msg);
        TextView tv_dismiss =  dialog.findViewById(R.id.tv_dismiss);

        tv_alert_msg.setText(msg);


        tv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay! Do the
            // camera-related task you need to do.
            Toast.makeText(ProfileDetailActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {

            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            Toast.makeText(ProfileDetailActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean checkForPermission(final String[] permissions, final int permRequestCode) {

        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            final String perm = permissions[i];
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(ProfileDetailActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {

                    Log.d("permisssion","not granted");


                    if (shouldShowRequestPermissionRationale(permissions[i])) {

                        Log.d("if","if");
                        permissionsNeeded.add(perm);

                    } else {
                        // add the request.
                        Log.d("else","else");
                        permissionsNeeded.add(perm);
                    }

                }
            }
        }

        if (permissionsNeeded.size() > 0) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // go ahead and request permissions
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), permRequestCode);
            }
            return false;
        } else {
            // no permission need to be asked so all good...we have them all.
            return true;
        }

    }





    private void FetchProfile() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();

      /*  String addresslatlng= et_full_address.getText().toString();
        getLocationFromAddress(ProfileDetailActivity.this,addresslatlng);
*/

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"),userId );

        Log.e(TAG, "UpdateProfile: driver_id>>> "+user_id.toString() );
        // Log.e(TAG, "UpdateProfile: profile_image>>> "+profile_image );


        RestClient.getClient().UserProfile(user_id).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    // dialog.dismiss();


                    SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    assert response.body() != null;

                    editor.putString(Constant.USER_NAME, response.body().getResponse().getUser_name());
                    editor.putString(Constant.USER_ID, String.valueOf(response.body().getResponse().getUser_id()));
                    editor.putString(Constant.USER_ADDRESS, response.body().getResponse().getAddress());
                    editor.putString(Constant.USER_MAIL, response.body().getResponse().getUser_email());
                    editor.putString(Constant.USER_IMAGE, response.body().getResponse().getImage());
                    editor.apply();

                    et_user_name.setText(response.body().getResponse().getUser_name());
                    et_user_mobile.setText(response.body().getResponse().getUser_phone());
                    et_email.setText(response.body().getResponse().getUser_email());
                    et_address.setText(response.body().getResponse().getAddress());

                    RequestOptions options = new RequestOptions()
                            .centerInside();
                            //   .placeholder(R.mipmap.man)
                           // .error(R.mipmap.man);
                    Glide.with(ProfileDetailActivity.this).load(response.body().getResponse().getImage()).apply(options).into(iv_profile);
                    dialog.dismiss();



                } else  {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }
    private void UpdateProfile() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();




        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"),userId );
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"),et_user_name.getText().toString().trim() );
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"),et_user_mobile.getText().toString().trim() );
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"),et_email.getText().toString().trim() );
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"),et_address.getText().toString().trim() );

        MultipartBody.Part pro_image = MultipartBody.Part.createFormData(
                "image_icon",
                profile_image.getName(),
                RequestBody.create(MediaType.parse("file"), profile_image));


        Log.e(TAG, "UpdateProfile: driver_id>>> "+userId );
        Log.e(TAG, "UpdateProfile: profile_image>>> "+profile_image );


        RestClient.getClient().UpdateUserProfile(user_id,name,phone,email,
                address,pro_image
        ).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();


                    SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    assert response.body() != null;


                    editor.putString(Constant.USER_NAME, response.body().getResponse().getUser_name());
                    editor.putString(Constant.USER_ID, String.valueOf(response.body().getResponse().getUser_id()));
                    editor.putString(Constant.USER_ADDRESS, response.body().getResponse().getAddress());
                    editor.putString(Constant.USER_MAIL, response.body().getResponse().getUser_email());
                    editor.putString(Constant.USER_IMAGE, response.body().getResponse().getImage());
                    editor.apply();


                    successAlert( "Profile Updated Successfully");
                    // Toast.makeText(ProfileDetailActivity.this, response.body().getMessage(),Toast.LENGTH_LONG).show();



                } else  {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }
    private void UpdateProfileWithoutImage() {

        ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(R.color.ForestGreen)
                .fadeColor(Color.WHITE).build();
        dialog.setCancelable(false);
        dialog.show();




        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"),userId );
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"),et_user_name.getText().toString().trim() );
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"),et_user_mobile.getText().toString().trim() );
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"),et_email.getText().toString().trim() );
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"),et_address.getText().toString().trim() );

        Log.e(TAG, "UpdateProfileWithoutImage:user_id> "+userId );


        RestClient.getClient().UpdateUserProfileWithoutImage(user_id,name,phone,email,
                address
        ).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                Log.e(TAG, "onResponse 2 : " + response.code());
                Log.e(TAG, "onResponse 2: " + response.isSuccessful());
                // ppDialog.dismiss();
                assert response.body() != null;
                if (response.body().getStatus().equals(200)) {
                    dialog.dismiss();


                    SharedPreferences sp = getSharedPreferences(Constant.USER_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    assert response.body() != null;


                    editor.putString(Constant.USER_NAME, response.body().getResponse().getUser_name());
                    editor.putString(Constant.USER_ID, String.valueOf(response.body().getResponse().getUser_id()));
                    editor.putString(Constant.USER_ADDRESS, response.body().getResponse().getAddress());
                    editor.putString(Constant.USER_MAIL, response.body().getResponse().getUser_email());
                    editor.apply();


                    successAlert( "Profile Updated Successfully");
                    // Toast.makeText(ProfileDetailActivity.this, response.body().getMessage(),Toast.LENGTH_LONG).show();



                } else  {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "onFailure 2: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

}