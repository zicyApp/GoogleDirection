package com.example.robertpolovitzer.googlelocationapp;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int LOCATION_PERMISSION = 1;
    private String TAG = this.getClass().getName();
    private WebView webView;
    private LocationManager mLocationManager;
    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (canLocationPermission(GetActivity())) {
            Log.e("Location Manager", "Location Granted");

            double longitude = -71.19140625;
            double latitude = 46.837649560937464;
            myLocation = getLastKnownLocation();
            if (myLocation != null) {
                longitude = myLocation.getLongitude();
                latitude = myLocation.getLatitude();
            }
            Log.e(TAG, "" + longitude);
            Log.e(TAG, "" + latitude);

            double direction_latitude = 46.8283627;
            double direction_longitude = -71.2421034;

            String url = "https://www.google.ca/maps/dir/" + latitude + "," + longitude + "/" + direction_latitude + "," + direction_longitude + "/@" + latitude + "," + longitude + ",14z";
            webView.loadUrl(url);

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
