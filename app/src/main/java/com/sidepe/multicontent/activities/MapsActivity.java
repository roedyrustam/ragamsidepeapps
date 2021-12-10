package com.sidepe.multicontent.activities;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sidepe.multicontent.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String contentTitle;
    private String contentLatitude;
    private String contentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Get Intent Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("contentTitle")) {
                contentTitle = extras.getString("contentTitle");
                contentLatitude = extras.getString("contentLatitude");
                contentLongitude = extras.getString("contentLongitude");
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Convert String to Double
        double latitude = Double.valueOf(contentLatitude);
        double longitude = Double.valueOf(contentLongitude);

        // Add a marker in Sydney and move the camera
        LatLng contentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(contentLocation).title(contentTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(contentLocation));
        float zoomLevel = (float) 15.5;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(contentLocation, zoomLevel));
        //mMap.setMinZoomPreference(20);
        //mMap.setMaxZoomPreference(9);
    }
}
