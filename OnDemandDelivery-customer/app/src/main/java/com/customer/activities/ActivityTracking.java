package com.customer.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.customer.AdapterCartItems;
import com.customer.Application;
import com.customer.Constants;
import com.customer.Model;
import com.customer.R;
import com.customer.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.teliver.sdk.core.TLog;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.models.UserBuilder;

import java.util.ArrayList;

public class ActivityTracking extends AppCompatActivity implements OnSuccessListener<Location> {

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

        if (Utils.checkLPermission(this))
            Utils.enableGPS(this, this);

        Teliver.identifyUser(new UserBuilder(username).
                setUserType(UserBuilder.USER_TYPE.CONSUMER).registerPush().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (requestCode==115){
            if (!Utils.isPermissionOk(grantResults)){
                Toast.makeText(this,"Location permission denied",Toast.LENGTH_SHORT).show();
                finish();
            }
            else Utils.enableGPS(this,this);
        }
    }

    @Override
    public void onSuccess(Location location) {
        Log.e("onSuccess::",location.getLatitude()+"");
    }
}
