package com.customer.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.customer.Application;
import com.customer.Constants;
import com.customer.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.core.TrackingListener;
import com.teliver.sdk.models.MarkerOption;
import com.teliver.sdk.models.TLocation;
import com.teliver.sdk.models.TrackingBuilder;


public class ActivityHome extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {


    private Application application;

    private TextView txtPayment, txtInKitchen, txtOnRoute, txtDeliverHint, txtDelivered;

    private ImageView imgOne, imgTwo, imgThree, imgFour, imgViewOne, imgViewtwo, imgViewThree;

    private GoogleMap googleMap;

    private GoogleApiClient googleApiClient;

    private LinearLayout layoutDelivered;

    private RelativeLayout layoutTracking;

    private AlertDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("message"));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        supportMapFragment.getMapAsync(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_tracking_steps, null);
        view.setPadding(8, 0, 8, 0);

        txtPayment = (TextView) view.findViewById(R.id.txtpayment);
        txtInKitchen = (TextView) view.findViewById(R.id.txtInKitchen);
        txtOnRoute = (TextView) view.findViewById(R.id.initTeliver);
        txtOnRoute.setOnClickListener(this);
        txtDeliverHint = (TextView) view.findViewById(R.id.txtDeliveryHint);
        txtDelivered = (TextView) view.findViewById(R.id.txtDelivered);
        imgOne = (ImageView) view.findViewById(R.id.imgOne);
        imgTwo = (ImageView) view.findViewById(R.id.imgTwo);
        imgThree = (ImageView) view.findViewById(R.id.imgThree);
        imgFour = (ImageView) view.findViewById(R.id.imgFour);
        imgViewOne = (ImageView) view.findViewById(R.id.viewOne);
        imgViewtwo = (ImageView) view.findViewById(R.id.viewTwo);
        imgViewThree = (ImageView) view.findViewById(R.id.viewThree);
        layoutDelivered = (LinearLayout) view.findViewById(R.id.layoutdelivered);
        layoutTracking = (RelativeLayout) view.findViewById(R.id.layoutTracker);

        alert.setView(view);

        application = (Application) getApplicationContext();
        alert.setCancelable(false);
        dialog = alert.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    finish();
                return false;
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.BOTTOM;
        params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(params);


        dialog.show();

        Intent intent = getIntent();

        if (intent != null) {
            String msg = getIntent().getStringExtra("msg");
            setStates(msg);
        }
        txtOnRoute.setEnabled(false);
    }

    private void maintainState() {
        if (application.getBooleanInPef(Constants.STEP_ONE)) {
            txtPayment.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
            setCompletedIcon(imgOne, imgViewOne);
        }
        if (application.getBooleanInPef(Constants.STEP_TWO)) {
            setCompletedText(txtPayment, txtInKitchen, imgTwo, imgViewtwo);
            setCompletedText(txtPayment, txtDeliverHint, imgTwo, imgViewtwo);
            setCompletedIcon(imgTwo, imgViewtwo);
            txtOnRoute.setEnabled(true);
        }
        if (application.getBooleanInPef(Constants.STEP_THREE)) {
            setCompletedText(txtInKitchen, txtOnRoute, imgThree, imgViewThree);
            setCompletedText(txtDeliverHint, txtOnRoute, imgThree, imgViewThree);
            setCompletedIcon(imgThree, imgViewThree);
        }
        if (application.getBooleanInPef(Constants.STEP_FOUR)) {
            storeStepCompletion(Constants.STEP_FOUR);
            layoutTracking.setVisibility(View.GONE);
            layoutDelivered.setVisibility(View.VISIBLE);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }
        if (application.getBooleanInPef(Constants.STEP_ONE_RUNNING)) {
            txtPayment.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
            changeTintMode(imgOne, imgViewOne);
        }
        if (application.getBooleanInPef(Constants.STEP_TWO_RUNNING)) {
            txtInKitchen.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
            txtDeliverHint.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
            changeTintMode(imgTwo, imgViewtwo);
        }
        if (application.getBooleanInPef(Constants.STEP_THREE_RUNNING)) {
            changeTintMode(imgThree, imgViewThree);
            txtOnRoute.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
        }
    }

    private void setCompletedText(TextView txtNow, TextView txtNext, ImageView img, ImageView imgViewLine) {
        txtNow.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        txtNext.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        img.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
        imgViewLine.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
    }

    private void setCompletedIcon(ImageView img, ImageView imgViewLine) {
        img.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
        imgViewLine.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
    }

    private void changeTintMode(ImageView imgStatus, ImageView imgView) {
        imgStatus.setColorFilter(ContextCompat.getColor(this, R.color.colorOrange));
        imgView.setColorFilter(ContextCompat.getColor(this, R.color.colorOrange));
    }

    public void setStates(String message) {
        if (message != null) {
            switch (message) {
                case "1":
                    txtPayment.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                    setCompletedIcon(imgOne, imgViewOne);
                    application.storeBooleanInPref(Constants.STEP_ONE_RUNNING, false);
                    changeColors(txtPayment, txtInKitchen, imgTwo, imgViewtwo);
                    changeColors(txtPayment, txtDeliverHint, imgTwo, imgViewtwo);
                    changeTintMode(imgTwo, imgViewtwo);
                    storeStepCompletion(Constants.STEP_ONE);
                    storeStepCompletion(Constants.STEP_TWO_RUNNING);
                    setCompletedIcon(imgOne, imgViewOne);
                    break;
                case "2":
                    txtOnRoute.setEnabled(true);
                    changeColors(txtInKitchen, txtOnRoute, imgThree, imgViewThree);
                    changeColors(txtDeliverHint, txtOnRoute, imgThree, imgViewThree);
                    changeTintMode(imgThree, imgViewThree);
                    storeStepCompletion(Constants.STEP_TWO);
                    storeStepCompletion(Constants.STEP_THREE_RUNNING);
                    application.storeBooleanInPref(Constants.STEP_TWO_RUNNING, false);
                    setCompletedIcon(imgTwo, imgViewtwo);
                    break;
                case "3":
                    setCompletedIcon(imgThree, imgViewThree);
                    storeStepCompletion(Constants.STEP_THREE);
                    application.storeBooleanInPref(Constants.STEP_THREE_RUNNING, false);
                    storeStepCompletion(Constants.STEP_FOUR);
                    layoutTracking.setVisibility(View.GONE);
                    layoutDelivered.setVisibility(View.VISIBLE);
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.CENTER;
                    window.setAttributes(params);
                    break;
                default:
                    break;
            }
        }
    }


    private void changeColors(TextView txtView, TextView txtNext, ImageView img, ImageView imgView) {
        txtView.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        txtNext.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
        img.setColorFilter(ContextCompat.getColor(this, R.color.colorOrange));
        imgView.setColorFilter(ContextCompat.getColor(this, R.color.colorOrange));

    }

    private void storeStepCompletion(String step) {
        application.storeBooleanInPref(step, true);
    }

    public void startTracking(String trackingID) {

        TrackingBuilder builder = new TrackingBuilder(new MarkerOption(trackingID)).withListener(new TrackingListener() {
            @Override
            public void onTrackingStarted(String trackingId) {
                Log.d("TELIVER::", "onTrackingStarted: " + trackingId);
            }

            @Override
            public void onLocationUpdate(String trackingId, TLocation location) {
                Log.d("TELIVER::", "onLocationUpdate: " + location.getLatitude() + location.getLongitude());
            }

            @Override
            public void onTrackingEnded(String trackingId) {
                Log.d("TELIVER::", "onTrackingEnded: " + trackingId);
            }

            @Override
            public void onTrackingError(String reason) {
                Log.d("TELIVER::", "onTrackingError: " + reason);
            }
        });
        Teliver.startTracking(builder.build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.initTeliver:
                String trackingId = application.getStringInPref(Constants.TRACKING_ID);
                startTracking(trackingId);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        maintainState();
        if (application.getBooleanInPef(Constants.STEP_FOUR))
            application.deletePreference();
        if (dialog != null)
            dialog.show();
        super.onResume();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setStates(intent.getStringExtra("msg"));
        }
    };

    @Override
    protected void onPause() {
        if (dialog != null)
            dialog.dismiss();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (dialog != null)
            dialog.dismiss();
        super.onDestroy();
    }
}

















