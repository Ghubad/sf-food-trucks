package com.technortium.tracker.sffoodtrucks.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.technortium.tracker.sffoodtrucks.AppController;
import com.technortium.tracker.sffoodtrucks.R;
import com.technortium.tracker.sffoodtrucks.model.FoodTruck;
import com.technortium.tracker.sffoodtrucks.model.FoodTruckStore;
import com.technortium.tracker.sffoodtrucks.network.OnRequestCallback;

import java.io.IOException;
import java.util.List;

public class GMapFragment extends Fragment implements OnRequestCallback, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int GPS_ERROR_REQUEST = 9001;
    private static final float ZOOM_LEVEL = 17;
    private static final String DEFAULT_LOCATION = "san francisco";
    private static final String API_ENDPOINT = "https://data.sfgov.org/resource/6a9r-agq8.json";
    private static final String X_APP_TOKEN = "hsHjdNgZ8xhvv2dyMyeHH0IjU";
    private static final String SFFT = "SSFT";
    private static final String TAG = GMapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private View view;
    private MapFragment mapFragment;
    private ProgressDialog pDialog;
    private AutoCompleteTextView searchBox;
    private Button button;
    private Marker markers;

    public GMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.loading));

        initDefaultButton();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (mMap != null) {
            initGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getActivity(), getString(R.string.map_unavailable), Toast.LENGTH_SHORT).show();
            log(getString(R.string.map_unavailable));
        }

    }

    public void initGoogleApiClient() {

        log("Initiating google api client");

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        log("Google api client connected");

        try {

            goToCurrentLocation();
            getFoodVehiclesData();

        } catch (Exception ex) {
            log(ex.getMessage());
            Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
            if (pDialog != null) {
                pDialog.dismiss();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        log("Connection suspended.");
    }

    @Override
    public void onLocationChanged(Location location) {
        log("Location changed.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log("Connection Failed.");
        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
    }

    public void goToCurrentLocation() {

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {

            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng ll = new LatLng(lat, lng);

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, ZOOM_LEVEL);
            mMap.animateCamera(update);

        } else {
            Toast.makeText(getActivity(), R.string.geolocate_error, Toast.LENGTH_SHORT).show();
            log("Unable to locate user");
        }

    }

    private void goToGoDefaultLocation() throws IOException {

        Geocoder geoCoder = new Geocoder(getActivity());
        if (geoCoder == null) {
            return;
        }

        List<Address> addresses = geoCoder.getFromLocationName(DEFAULT_LOCATION, 1);

        if (addresses != null && addresses.size() > 0) {

            Address address = addresses.get(0);
            if (address != null) {
                double lat = address.getLatitude();
                double lng = address.getLongitude();

                goToLocation(lat, lng, ZOOM_LEVEL);
            }
        }
    }

    private void goToLocation(double lat, double lng, float zoom) {

        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);

        log("Panned camera to default location");

    }

    private void getFoodVehiclesData() {

        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();

        log("Fetching data from api endpoint");
        FoodTruckStore.getInstance().getFoodTruckData(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

        AppController.getInstance().getRequestQueue().cancelAll(SFFT);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }


    private void drawMarkers() {

        List<FoodTruck> foodTruckList =
                FoodTruckStore.getInstance().getFoodTruckList();

        if (markers != null)
            markers.remove();

        for (FoodTruck foodTruck : foodTruckList) {

            double lat = foodTruck.getLatitude();
            double lng = foodTruck.getLongitude();
            LatLng ll = new LatLng(lat, lng);

            MarkerOptions marker = new MarkerOptions()
                    .title(foodTruck.getApplicant())
                    .position(ll)
                    .snippet(foodTruck.getFooditems());
            markers = mMap.addMarker(marker);

        }

        log("Added " + foodTruckList.size() + " markers !!");
    }

    @Override
    public void onResponseResult(Boolean result) {

        log("Response result: " + result);

        if (result) {
            drawMarkers();
        } else {
            //todo provide snack bar to allow user to retry/refresh
        }

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    private void initDefaultButton() {
        button = (Button) view.findViewById(R.id.default_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    goToGoDefaultLocation();
                } catch (IOException ex) {
                    log(ex.getMessage());
                    Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
