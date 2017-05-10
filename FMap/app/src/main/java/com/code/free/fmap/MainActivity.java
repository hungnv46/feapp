package com.code.free.fmap;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback {

    private static GoogleApiClient googleApiClient;
    private static Location lastLocation;
    private static GoogleMap fMap;

    private static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static String TAG = "MainActivityLog";
    private static LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    public void getLocation(View view) {

        currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        fMap.addMarker(new MarkerOptions().position(currentLocation)
                .title("Marker in myLocaltion"));
        fMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        fMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );

        Context context = getApplicationContext();

        Geocoder geocoder;
        List<android.location.Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String district = addresses.get(0).getSubAdminArea();

            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, "Thanh Pho: " + city + "_" + "Dia chi: " + address + "_" + "Quan: " + district, duration);
            toast.show();

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);

            TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
            textView.setText("Thành Phố: " + city + "____" + "Quận: " + district + "____" + "Địa chỉ nhà: " + address);

            dialog.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        fMap = googleMap;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v(String.valueOf('a'), String.valueOf('b'));
            return;
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (lastLocation != null) {
            Log.v(String.valueOf("abc123"), String.valueOf(lastLocation.getLatitude()));
            Button btnLocation = (Button)findViewById(R.id.btnLocation);
            btnLocation.setText(lastLocation.getLatitude() + ":" + lastLocation.getLongitude());

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
