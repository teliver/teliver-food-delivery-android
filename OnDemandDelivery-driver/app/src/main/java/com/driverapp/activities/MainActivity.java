package com.driverapp.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driverapp.AdapterCartItems;
import com.driverapp.Constants;
import com.driverapp.R;
import com.driverapp.Utils;
import com.driverapp.views.CartItems;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.teliver.sdk.core.TLog;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.core.TripListener;
import com.teliver.sdk.models.PushData;
import com.teliver.sdk.models.Trip;
import com.teliver.sdk.models.TripBuilder;
import com.teliver.sdk.models.UserBuilder;

import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<Location> {

    private String trackingId = "TELIVERTRK_100", username = "driver_1";

    private com.driverapp.Application application;

    private TextView txtOrderStatus, txtProcessing, txtPickedUp, txtDelivered;

    private ImageView imgProcessing, imgPickedUp, imgDelivered;

    private ImageView imgViewProcessing, imgViewPickedUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (com.driverapp.Application) getApplicationContext();
        TLog.setVisible(true);
        /*trackingId = trackingId + new Random().nextInt(1000);
        if (!application.getBooleanInPef("created"))
            application.storeStringInPref(Constants.TRACKING_ID, trackingId);
        application.storeBooleanInPref("created", true);*/
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable drawable = toolbar.getNavigationIcon();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtOrderStatus = findViewById(R.id.txtOrderStatus);
        imgViewProcessing = findViewById(R.id.imgTwo);
        imgViewPickedUp = findViewById(R.id.imgThree);
        txtProcessing = findViewById(R.id.txtProcessing);
        txtPickedUp = findViewById(R.id.txtPickedUp);
        txtDelivered = findViewById(R.id.txtDelivered);
        imgProcessing = findViewById(R.id.imgProcessing);
        imgPickedUp = findViewById(R.id.imgPickedUp);
        imgDelivered = findViewById(R.id.imgDelivered);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<CartItems> listCartItems = new ArrayList<>();
        AdapterCartItems adapterCartItems = new AdapterCartItems();

        listCartItems.add(new CartItems("Veg hot garlic Sauce Momo", "1", "149.00", "149.00", R.drawable.ic_veg));
        listCartItems.add(new CartItems("Veg Spicy Mayo Tossed Momo", "2", "170.00", "340.00", R.drawable.ic_veg));
        listCartItems.add(new CartItems("Veg Tossed Momo", "2", "170.00", "340.00", R.drawable.ic_veg));
        adapterCartItems.setData(listCartItems);
        recyclerView.setAdapter(adapterCartItems);
        adapterCartItems.notifyDataSetChanged();
        setTexts(txtProcessing, txtPickedUp, txtDelivered);
        setImages(imgProcessing, imgPickedUp, imgDelivered);
        Teliver.identifyUser(new UserBuilder(username).setUserType(UserBuilder.USER_TYPE.OPERATOR).registerPush().build());
        if (Utils.checkLPermission(this))
            Utils.enableGPS(this, this);
        txtProcessing.setEnabled(true);
        imgProcessing.setEnabled(true);
    }

    private void setImages(ImageView... img) {
        for (ImageView imgView : img) {
            imgView.setEnabled(false);
            imgView.setOnClickListener(this);
        }
    }

    private void setTexts(TextView... txt) {
        for (TextView txtView : txt) {
            txtView.setEnabled(false);
            txtView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgProcessing:
            case R.id.txtProcessing:
                startTrip();
                changeColors(txtProcessing, imgViewProcessing, imgProcessing);
                txtOrderStatus.setText(getString(R.string.txtOrderProcessing));
                application.storeBooleanInPref(Constants.STEP_TWO, true);
                txtPickedUp.setEnabled(true);
                imgPickedUp.setEnabled(true);
                txtProcessing.setEnabled(false);
                imgProcessing.setEnabled(false);
                break;
            case R.id.imgPickedUp:
            case R.id.txtPickedUp:
                sendEventPush("2", "order out to delivery,track your order");
                changeColors(txtPickedUp, imgViewPickedUp, imgPickedUp);
                txtOrderStatus.setText(getString(R.string.txtPickedUp));
                application.storeBooleanInPref(Constants.STEP_THREE, true);
                txtDelivered.setEnabled(true);
                imgDelivered.setEnabled(true);
                txtPickedUp.setEnabled(false);
                imgPickedUp.setEnabled(false);
                break;
            case R.id.imgDelivered:
            case R.id.txtDelivered:
                sendEventPush("3", "your order is delivered");
                txtOrderStatus.setText(getString(R.string.txtOrderDelivered));
                changeColors(txtDelivered, imgViewPickedUp, imgDelivered);
                application.storeBooleanInPref(Constants.STEP_FOUR, true);
                txtDelivered.setEnabled(false);
                imgDelivered.setEnabled(false);
                Teliver.stopTrip(trackingId);
                application.deletePreference();
                break;
        }
    }

    private void sendEventPush(final String pushMessage, String tag) {
        String[] users = new String[]{"user_1", "user_2"};
        PushData pushData = new PushData(users);
        pushData.setMessage(tag);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", pushMessage);
            jsonObject.put("operator", "driver_1");
            pushData.setPayload(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Teliver.sendEventPush(trackingId, pushData, tag);
    }

    private void startTrip() {
        TripBuilder tripBuilder = new TripBuilder(trackingId);
        tripBuilder.withInterval(1000);
        tripBuilder.withDistance(5);
        Teliver.startTrip(tripBuilder.build());
        Teliver.setTripListener(new TripListener() {
            @Override
            public void onTripStarted(Trip tripDetails) {
                sendEventPush("1", "order confirmed");
            }

            @Override
            public void onLocationUpdate(Location location) {

            }

            @Override
            public void onTripEnded(String trackingID) {

            }

            @Override
            public void onTripError(String reason) {

            }
        });
    }

    @Override
    protected void onResume() {
        if (application.getBooleanInPef(Constants.STEP_FOUR))
            application.deletePreference();
        maintainState();
        super.onResume();
    }


    private void maintainState() {
        if (application.getBooleanInPef(Constants.STEP_TWO)) {
            changeColors(txtProcessing, imgViewProcessing, imgProcessing);
            txtOrderStatus.setText(getString(R.string.txtOrderProcessing));
            txtPickedUp.setEnabled(true);
            imgPickedUp.setEnabled(true);
            txtProcessing.setEnabled(false);
            imgProcessing.setEnabled(false);
        }
        if (application.getBooleanInPef(Constants.STEP_THREE)) {
            changeColors(txtPickedUp, imgViewPickedUp, imgPickedUp);
            txtOrderStatus.setText(getString(R.string.txtOrderPicked));
            txtDelivered.setEnabled(true);
            imgDelivered.setEnabled(true);
            txtPickedUp.setEnabled(false);
            imgPickedUp.setEnabled(false);
        }
        if (application.getBooleanInPef(Constants.STEP_FOUR)) {
            changeColors(txtDelivered, imgViewPickedUp, imgDelivered);
            txtOrderStatus.setText(getString(R.string.txtTelivered));
            txtDelivered.setEnabled(false);
            imgDelivered.setEnabled(false);
        }
    }

    private void checkGps() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(MainActivity.this, Constants.SHOW_GPS_DIALOG);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SHOW_GPS_DIALOG && resultCode == RESULT_OK)
            Toast.makeText(MainActivity.this, "Gps is turned on", Toast.LENGTH_SHORT).show();
        else if (requestCode == Constants.SHOW_GPS_DIALOG && resultCode == RESULT_CANCELED)
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 115) {
            if (!Utils.isPermissionOk(grantResults)) {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                finish();
            } else Utils.enableGPS(this, this);
        }
    }


    @Override
    public void onSuccess(Location location) {
        Log.e("onSuccess::", location.getLatitude() + "");
    }

    private void changeColors(TextView txtView, ImageView imgLine, ImageView img) {
        txtView.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        imgLine.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
        img.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
    }
}