package com.customer.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.customer.AdapterCartItems;
import com.customer.Application;
import com.customer.Constants;
import com.customer.Model;
import com.customer.R;
import com.customer.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.teliver.sdk.core.TLog;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.models.UserBuilder;

import java.util.ArrayList;

public class ActivityTracking extends AppCompatActivity {

    private String username = "user_1";

    private Application application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        TLog.setVisible(true);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        application = (Application) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Drawable drawable = toolbar.getNavigationIcon();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Model> listCartItems = new ArrayList<>();
        AdapterCartItems adapterCartItems = new AdapterCartItems();

        listCartItems.add(new Model("Veg hot garlic Sauce Momo", "1", "149.00", "149.00", R.drawable.ic_veg));
        listCartItems.add(new Model("Veg Spicy Mayo Tossed Momo", "2", "170.00", "340.00", R.drawable.ic_veg));
        listCartItems.add(new Model("Veg Tossed Momo", "2", "170.00", "340.00", R.drawable.ic_veg));

        adapterCartItems.setData(listCartItems);
        recyclerView.setAdapter(adapterCartItems);
        adapterCartItems.notifyDataSetChanged();

        findViewById(R.id.btnTracker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityTracking.this, ActivityHome.class));
            }
        });

        if (Utils.checkPermission(this)) {
            //checkGps();
        }
        Teliver.identifyUser(new UserBuilder(username).
                setUserType(UserBuilder.USER_TYPE.CONSUMER).registerPush().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        status.startResolutionForResult(ActivityTracking.this, Constants.SHOW_GPS_DIALOG);
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
            Toast.makeText(ActivityTracking.this, "Gps is turned on", Toast.LENGTH_SHORT).show();
        else if (requestCode == Constants.SHOW_GPS_DIALOG && resultCode == RESULT_CANCELED)
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.COARSE_LOCATION_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    finish();
                else checkGps();
        }
    }
}
