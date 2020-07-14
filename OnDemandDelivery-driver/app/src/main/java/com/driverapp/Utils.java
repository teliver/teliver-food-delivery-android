package com.driverapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class Utils {

    @SuppressLint("InlinedApi")
    private static final String FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION,
            BG_LOC = Manifest.permission.ACCESS_BACKGROUND_LOCATION;

    private static final int PERMISSION_REQ_CODE = 115, GPS_REQ = 124,
            TAKE_PHOTO = 111, FROM_GALLERY = 116;

    public void init(TextView txtView, Context context, AttributeSet attrs) {
        try {
            Typeface typeface = getCustomFont(context, attrs);
            if (typeface != null)
                txtView.setTypeface(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Typeface getCustomFont(Context context, AttributeSet attrs) {
        Typeface typeface = null;
        try {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.CustomWidget);
            for (int i = 0, count = typedArray.getIndexCount(); i < count; i++) {
                int attribute = typedArray.getIndex(i);
                if (attribute == R.styleable.CustomWidget_font_name) {
                    typeface = Typeface.createFromAsset(context.getResources()
                            .getAssets(), typedArray.getString(attribute));
                }
            }
            typedArray.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return typeface;
    }


    public static boolean isAndQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static boolean checkLPermission(Activity context) {
        try {
            if (isPermissionGranted(context))
                return true;
            String[] permissions = isAndQ() ? new String[]{FINE_LOC, BG_LOC}
                    : new String[]{FINE_LOC};
            if (isShouldShow(context, FINE_LOC))
                showPermission(context, permissions);
            else
                showPermission(context, permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPermissionOk(int... results) {
        boolean isAllGranted = true;
        for (int result : results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    private static void showPermission(Activity context, String... permissions) {
        ActivityCompat.requestPermissions(context, permissions, PERMISSION_REQ_CODE);
    }


    private static boolean isShouldShow(Activity context, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(context, permission);
    }

    public static boolean isPermissionGranted(Context context) {
        if (isPermissionOk(context, FINE_LOC))
            return !isAndQ() || isPermissionOk(context, BG_LOC);
        else
            return false;
    }

    private static boolean isPermissionOk(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void enableGPS(final Activity context, final
    OnSuccessListener<Location> listener) {
        try {
            final FusedLocationProviderClient client = LocationServices
                    .getFusedLocationProviderClient(context);
            final LocationRequest locationRequest = getLocationReq();
            LocationSettingsRequest request = new LocationSettingsRequest
                    .Builder().addLocationRequest(locationRequest)
                    .setAlwaysShow(true).build();
            Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(
                    context).checkLocationSettings(request);
            task.addOnSuccessListener(locationSettingsResponse ->
                    getMyLocation(client, locationRequest, listener));
            task.addOnFailureListener(e -> {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(context, GPS_REQ);
                    } catch (IntentSender.SendIntentException sendEx) {
                        listener.onSuccess(null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private static void getMyLocation(final FusedLocationProviderClient client
            , LocationRequest locationRequest, final OnSuccessListener<Location> listener) {
        try {
            client.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null)
                        Log.d("OnDemandDelivery::","result null");
                    else {
                        Location location = locationResult.getLastLocation();
                        client.removeLocationUpdates(this);
                        listener.onSuccess(location);
                    }
                }
            }, Looper.myLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static LocationRequest getLocationReq() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(2000);
        return locationRequest;
    }

    public static boolean checkPermission(Activity context) {
        if (Build.VERSION.SDK_INT == 29) {
            boolean foreground = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (foreground) {
                boolean background = ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

                if (background) {
                    return true;
                } else {
                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 5);
                }
            } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 5);
            }

        } else if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, android.Manifest.permission.ACCESS_COARSE_LOCATION))
                    ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.COARSE_LOCATION_PERMISSION);
                else {
                    ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.COARSE_LOCATION_PERMISSION);
                }
            } else return true;
        } else return true;
        return false;
    }

}
