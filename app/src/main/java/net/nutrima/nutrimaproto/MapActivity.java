package net.nutrima.nutrimaproto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private String city;

    private final int MY_PERMISSIONS_REQUEST = 0;

    private ArrayList<Business> businessesToMap;

    private final String QUERY = "restaurant";

    public static final String TAG = MapActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
        System.out.println("[SISI DEBUG] FAILED");
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
        mMap.setOnMarkerClickListener(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            return;
        }

        // 1- Get my current location ////////////////////////////////////
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    1);
            if(addresses.size() > 0)
                city = addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getAddressLine(1);
            //addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, mLastLocation.toString());
        //////////////////////////////////////////////////////////////////

        // 2- Use location to get surrounding businesses /////////////////
        LatLng northEast = move(new LatLng(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), 709, 709);
        LatLng southWest = move(new LatLng(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), -709, -709);

        LatLngBounds tmpBounds = new LatLngBounds(southWest, northEast);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();

        PendingResult<AutocompletePredictionBuffer> result =
                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, QUERY,
                        tmpBounds, typeFilter);
        //////////////////////////////////////////////////////////////////

        // 3- Filter surroundings

        // 3-a- Filter for "FOOD" ////////////////////////////////////////
        businessesToMap = new ArrayList<Business>();

        Yelp.getYelp(this.getBaseContext());

        businessesToMap = Yelp.getYelp(this).search("food",
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude(),
                city);

        //////////////////////////////////////////////////////////////////

        // 3-b- Filter for presence in AWS ///////////////////////////////

        //////////////////////////////////////////////////////////////////

        // 3-c- Filter through Engine ////////////////////////////////////

        //////////////////////////////////////////////////////////////////

        // 4- Display surroundings

        // 4-a- Display my location //////////////////////////////////////
        // Add a marker in my location and animate camera
        mMap.setMyLocationEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), 13.0f));
        //////////////////////////////////////////////////////////////////

        // 4-b- Display surroundings /////////////////////////////////////

        for(Business b : businessesToMap) {
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_no_bckgrnd))
                .title(b.getName())
                //.snippet("The most populous city in Australia.")
                .position(b.getCoordinates()));
        }
        //////////////////////////////////////////////////////////////////
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private static final double EARTHRADIUS = 6366198;

    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }


    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        LinearLayout l = (LinearLayout)findViewById(R.id.bottom_bar_linearlayout);
        l.setVisibility(View.VISIBLE);

        final TextView businessName = (TextView)findViewById(R.id.business_name_textview);
        TextView businessPhone = (TextView)findViewById(R.id.business_phone_textview);
        TextView businessAddress = (TextView)findViewById(R.id.business_address_textview);

        businessName.setText(marker.getTitle());

        for(Business b : businessesToMap) {
            if(b.getName().equals(marker.getTitle())){
                businessPhone.setText(b.getPhone());
                businessAddress.setText(b.getAddress());
            }
        }

        ImageView businessPageGoImageView = (ImageView)findViewById(R.id.business_page_go_imageview);
        businessPageGoImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), BusinessDetailsActivity.class);
                String message = marker.getTitle();
                intent.putExtra("BUSINESS_NAME", message);
                startActivity(intent);
            }
        });

        return true;
    }
}
