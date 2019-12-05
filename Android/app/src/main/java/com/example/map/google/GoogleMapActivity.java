package com.example.map.google;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata_01.R;
import com.example.dialog.CreatDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;

public class GoogleMapActivity extends AppCompatActivity implements LocationListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap googleMap;
    private GoogleApiClient apiClient;
    private Location mLastLocation;
    private MapView mapView;
    private View view;
    private Dialog dialog;
    private String gardenId;
    private TextView huayuan;
    private TextView louceng;
    private TextView lu;
    private TextView qita;
    private TextView lat;
    private TextView lng;
    private int choose;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_view);
        mapFragment.getMapAsync(this);
        gardenId = getIntent().getStringExtra("gardenId");
        init();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    /**
     * 初始化地图
     */
    private void init() {
        view = getLayoutInflater().inflate(R.layout.send_map_data, null);
        dialog = CreatDialog.createSendMapDataDialog(this, view);
        initChoose();
    }

    private GoogleApiClient mGoogleApiClient;
    /**
     * 选择哪一个
     */
    private void initChoose() {
        TextView baiduText = findViewById(R.id.baidu_map);
        TextView tecentText = findViewById(R.id.tecent_map);
        tecentText.setBackgroundColor(0x99EEE6E6);
        baiduText.setBackgroundColor(0x99EEE6E6);

        huayuan = view.findViewById(R.id.huayuan);
        louceng = view.findViewById(R.id.louceng);
        lu = view.findViewById(R.id.lu);
        qita = view.findViewById(R.id.qita);

        lat = view.findViewById(R.id.lat);
        lng = view.findViewById(R.id.lng);

        huayuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                huayuan.setBackgroundColor(Color.RED);
                louceng.setBackgroundColor(0x99EEE6E6);
                lu.setBackgroundColor(0x99EEE6E6);
                qita.setBackgroundColor(0x99EEE6E6);
                choose = 0;
            }
        });

        louceng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                louceng.setBackgroundColor(Color.RED);
                huayuan.setBackgroundColor(0x99EEE6E6);
                lu.setBackgroundColor(0x99EEE6E6);
                qita.setBackgroundColor(0x99EEE6E6);
                choose = 1;
            }
        });
        lu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lu.setBackgroundColor(Color.RED);
                louceng.setBackgroundColor(0x99EEE6E6);
                huayuan.setBackgroundColor(0x99EEE6E6);
                qita.setBackgroundColor(0x99EEE6E6);
                choose = 2;
            }
        });
        qita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qita.setBackgroundColor(Color.RED);
                louceng.setBackgroundColor(0x99EEE6E6);
                lu.setBackgroundColor(0x99EEE6E6);
                huayuan.setBackgroundColor(0x99EEE6E6);
                choose = 3;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMyLocationEnabled(true);
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(">>>", "onSuccess: "+location);
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    // Logic to handle location object
                }
            }
        });
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();

    }



    @Override
    protected void onStart() {
        super.onStart();
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect();
        }
    }
    private LocationRequest mLocationRequest;
    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mGoogleApiClient) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(">>>>", "onConnectionFailed: "+connectionResult);

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(">>>", "onLocationChanged: "+location);
    }
}
