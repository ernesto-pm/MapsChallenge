package com.example.ernesto.stalkermaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location location;
    private Marker marker;
    private final int REQUEST_LOCATION_CODE = 99;
    DataManager dataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dataManager = new DataManager(this, getBaseContext());
        dataManager.clearDatabase();
    }

    public void execute(View v) {
        switch(v.getId()) {
            case R.id.zoom_in:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.zoom_out:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.style:
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;

            case R.id.challenge:
                //https://raw.githubusercontent.com/lutangar/cities.json/master/cities.json

                // This is how insert works
                dataManager.insert("Encamp", "AD","42.53474","1.58014");
                dataManager.insert("Ordino", "AD","42.55623","1.53319");
                dataManager.insert("Canillo", "AD","42.5676","1.59756");

                dataManager.insert("US101", "US", "37.4158675", "-122.0844253");

                // Uncomment to test:

                // This is how delete works
                dataManager.printContents();
                //dataManager.delete("Canillo");
                //dataManager.delete("Ordino");
                dataManager.delete("Encamp");
                dataManager.printContents();

                // This is how find works
                if(dataManager.find("Ordino") != null) {
                    //Log.i("Object", dataManager.find("Ordino").toString());
                    try {
                        JSONObject ordino = dataManager.find("Ordino");
                        LatLng latLng = new LatLng(Double.parseDouble(ordino.getString("lat")),
                                            Double.parseDouble(ordino.getString("lng")));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng).title(ordino.getString("name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mMap.addMarker(markerOptions);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if(dataManager.find("US101") != null) {
                    //Log.i("Object", dataManager.find("Ordino").toString());
                    try {
                        JSONObject ordino = dataManager.find("US101");
                        LatLng latLng = new LatLng(Double.parseDouble(ordino.getString("lat")),
                                Double.parseDouble(ordino.getString("lng")));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng).title(ordino.getString("name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mMap.addMarker(markerOptions);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if(dataManager.find("Encamp") != null) {
                    Log.i("Object", dataManager.find("Encamp").toString());
                }

                // This is how update works
                dataManager.update("Canillo", "MX", "0", "-150");
                dataManager.update("Ordino", "US", "100", "100");
                dataManager.printContents();




                break;


        }
    }

    public boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        } else {
            return true;
        }
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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        this.location = location;
        if(marker != null) {
            marker.remove();
        }

        LatLng latLng = new LatLng(lat, lon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("Aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        this.marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        if(googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
