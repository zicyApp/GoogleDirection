package com.example.robertpolovitzer.multiplelocationmap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int LOCATION_PERMISSION = 1;
    private String TAG = this.getClass().getName();
    private WebView webView;
    private LocationManager mLocationManager;
    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (canLocationPermission(GetActivity())) {
            Log.e("Location Manager", "Location Granted");

            double my_long = -71.19140625;
            double my_lat = 46.837649560937464;
            myLocation = getLastKnownLocation();
            if (myLocation != null) {
                my_long = myLocation.getLongitude();
                my_lat = myLocation.getLatitude();
            }
            Log.e(TAG, "" + my_long);
            Log.e(TAG, "" + my_lat);

            double d1_lat = 46.8283627;
            double d1_lon = -71.2421034;

            double d2_lat = 46.790657811997384;
            double d2_lon = -71.30264282226562;

            double d3_lat = 46.763383782259346;
            double d3_lon = -71.36444091796875;

            // Add a marker in Sydney and move the camera
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.common_full_open_on_phone);

            LatLng mylocation = new LatLng(my_lat, my_long);
            mMap.addMarker(new MarkerOptions().position(mylocation).title("My Location").icon(icon));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 10.0f));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(mylocation), 14, null);

            LatLng loc1 = new LatLng(d1_lat, d1_lon);
            mMap.addMarker(new MarkerOptions().position(loc1).title("Location 1"));

            LatLng loc2 = new LatLng(d2_lat, d2_lon);
            mMap.addMarker(new MarkerOptions().position(loc2).title("Location 2"));

            LatLng loc3 = new LatLng(d3_lat, d3_lon);
            mMap.addMarker(new MarkerOptions().position(loc3).title("Location 3"));

        } else {
            try {
                openLocationPermissionDialog(GetActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean canLocationPermission(final Activity activity) {

        boolean isCheck;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isCheck = true;
        } else {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                isCheck = false;
            } else if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                isCheck = false;
            } else {
                isCheck = true;
            }
        }

        return isCheck;
    }

    private Activity GetActivity() {
        return this;
    }

    public void openLocationPermissionDialog(final Activity activity) {
        boolean firstTimeAccount = true;
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Log.e(TAG, "ACCESS_COARSE_LOCATION");

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION);

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.e(TAG, "ACCESS_FINE_LOCATION");

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        } else {
            Log.e(TAG, "ACCESS_COARSE_LOCATION & ACCESS_FINE_LOCATION" + ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION));

            Log.e(TAG, "ACCESS_COARSE_LOCATION & ACCESS_FINE_LOCATION" + ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION));
            if (firstTimeAccount || true) {
                // 1. first time, never asked
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION);
            } else {
                showLocationPermissionDialog(activity);
            }


        }

    }

    MaterialDialog materialDialog;

    public void dismissAlertDialog() {
        try {
            if (materialDialog != null) {
                materialDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLocationPermissionDialog(final Activity activity) {
        try {
            dismissAlertDialog();

            materialDialog = new MaterialDialog.Builder(GetDialogContext(activity))
                    .content(String.format("Allow Location", activity.getString(R.string.app_name)))
                    .positiveText("Allow")
                    .negativeText("No")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            try {
                                dialog.dismiss();

                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivityForResult(intent, LOCATION_PERMISSION);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            try {
                                dialog.dismiss();
                                activity.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).build();
            materialDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ContextThemeWrapper GetDialogContext(Activity act) {

        ContextThemeWrapper themedContext;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            themedContext = new ContextThemeWrapper(act,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        } else {
            themedContext = new ContextThemeWrapper(act,
                    android.R.style.Theme_Light_NoTitleBar);
        }
        return themedContext;
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return bestLocation;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        Log.e("BestLocation", "" + bestLocation);
        return bestLocation;
    }
}
