package com.abraham.humapdemo;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    double latit=9.414327,longit=42.036699;
    private GoogleMap mgoogleMap;
    private GoogleApiClient mgoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Intent intent = getIntent();
      //  String message = intent.getStringExtra(EXTRA_MESSAGE);
        if (googleServiceAvailable()) {
            //if(message !=null){
               // latit=message[0];longit=message[1];
             //   Toast.makeText(getApplicationContext(),
             //           message, Toast.LENGTH_SHORT).show();
         //   }
            initMap();
        } else {
            //No Layout  Available
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final Button button = (Button) findViewById(R.id.btnFind);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    geoLocate(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean googleServiceAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can'tconnect to play service", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
       // goToLocation(latit, longit,15);
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mgoogleApiClient.connect();

    }

    private void goToLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mgoogleMap.addMarker(new MarkerOptions().position(latLng).title("Haramaya University"));
    }

    public void geoLocate(View view) throws IOException {
        EditText editText = (EditText) findViewById(R.id.place_find_feild);
        String location = editText.getText().toString();
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = geocoder.getFromLocationName(location, 1);
        Address address = list.get(0);
        String locality = address.getLocality();
        Toast.makeText(this, "Loading " + locality + " ...", Toast.LENGTH_LONG).show();
        goToLocation(address.getLatitude(), address.getLongitude(), 15);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_type_none:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                Toast.makeText(this, "None Mode", Toast.LENGTH_LONG).show();
                break;
            case R.id.map_type_normal:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(this, "Normal Mode", Toast.LENGTH_LONG).show();
                break;
            case R.id.map_type_satellite:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this, "SATELLITE Mode", Toast.LENGTH_LONG).show();
                break;
            case R.id.map_type_terrial:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Toast.makeText(this, "TERRAIN Mode", Toast.LENGTH_LONG).show();
                break;
            case R.id.map_type_hybrid:
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Toast.makeText(this, "HYBRID Mode", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
       mLocationRequest =LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null){

        }
        else{
            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng,15);
            mgoogleMap.animateCamera(cameraUpdate);
        }


    }
}
