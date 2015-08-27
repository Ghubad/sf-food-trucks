package com.technortium.tracker.sffoodtrucks.fragment;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.technortium.tracker.sffoodtrucks.AppController;
import com.technortium.tracker.sffoodtrucks.R;
import com.technortium.tracker.sffoodtrucks.model.Destination;
import com.technortium.tracker.sffoodtrucks.model.FoodTruck;
import com.technortium.tracker.sffoodtrucks.model.FoodTruckStore;
import com.technortium.tracker.sffoodtrucks.model.GpsLocation;
import com.technortium.tracker.sffoodtrucks.model.Order;
import com.technortium.tracker.sffoodtrucks.model.ResponseObject;
import com.technortium.tracker.sffoodtrucks.model.UpdateLocation;
import com.technortium.tracker.sffoodtrucks.network.CustomJsonRequest;
import com.technortium.tracker.sffoodtrucks.network.CustomRequest;
import com.technortium.tracker.sffoodtrucks.network.OnRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class GMapFragment extends Fragment implements OnRequestCallback, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int GPS_ERROR_REQUEST = 9001;
    private static final float ZOOM_LEVEL = 17;
    private static final String DEFAULT_LOCATION = "san francisco";
    private static final String API_ENDPOINT = "https://hypertrack-api-staging.herokuapp.com/api/v1/gps/";
    private static final String SFFT = "SSFT";
    private static final String TAG = GMapFragment.class.getSimpleName();

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private View view;
    private MapFragment mapFragment;
    private ProgressDialog pDialog;
    private AutoCompleteTextView searchBox;
    private Button button;
    private Marker mMarkers;

    private Firebase ref;
    //private Marker selectedMarker = null;
    private LocationRequest mLocationRequest;
    private String tripId;
    private Firebase trips;
    private List<LatLng> locations;
    volatile long speed;

    //Animation POC
    private List<Marker> markers = new ArrayList<Marker>();
    private final Handler mHandler = new Handler();

    private Marker selectedMarker;

    Handler handler = new Handler();
    private List<GpsLocation> gpsLocationList = new ArrayList<GpsLocation>();
    private Marker trackingMarker;
    private int index;
    private Order order;
    private Marker homeMarker;
    private EditText courierEdit;
    private Button trackButton;
    private LinearLayout searchLayout;
    private LatLng destiationPos;
    private LatLng lastKnownLocation;

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

        initSearchLayout();
        //setUpDatabase();

        //initDefaultButton();
        return view;
    }

    public void initSearchLayout() {

        searchLayout = (LinearLayout)view.findViewById(R.id.editTextLayout);
        courierEdit = (EditText)searchLayout.findViewById(R.id.orderEdit);
        trackButton = (Button) searchLayout.findViewById(R.id.button);
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "";
                if(courierEdit.getText().toString().equals("")) {
                    message = "Please enter order id :)";
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
                    //start tracking
                    getOrderDetails(courierEdit.getText().toString());
                }

            }
        });
    }

    private void setUpDatabase() {

        ref = new Firebase("https://incandescent-inferno-3069.firebaseio.com");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        trips = ref.child("trips");

        Map<String, String> trip = new HashMap<String, String>();
        trip.put("timestamp", formattedDate);

        Firebase newTrip = trips.push();
        newTrip.setValue(trip);

        tripId = newTrip.getKey();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        locations = new ArrayList<LatLng>();

        if (mMap != null) {
            initGoogleApiClient();
            mMap.setMyLocationEnabled(true);

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                    //drawHomeMarker(latLng);

                    updateCustomLocation(latLng);

                    homeMarker = mMap.addMarker(new MarkerOptions().position(latLng));

                    if (order.getEstimated_delivery_time() != null) {
                        int minutes = getTheEstimatedTime(getCurrentTime(),order.getEstimated_delivery_time());
                        String titleMsg = minutes + " mins to go !";
                        homeMarker.setTitle(titleMsg);
                        homeMarker.showInfoWindow();
                    }

                    //call his API request



                   /* if (markers.size() == 7) {

                        Toast.makeText(getActivity(), "Thank you for adding 7 mMarkers. Let there be Animation !", Toast.LENGTH_SHORT).show();
                        animator.startAnimation(true);

                    } else {

                        Toast.makeText(getActivity(), (7- markers.size()) +" more mMarkers to go !", Toast.LENGTH_SHORT).show();

                    }*/


                    //First attempt for animating marker

                    /*mMarkers = mMap.addMarker(marker);

                    locations.add(latLng);

                    if (locations.size() == 7) {
                        Toast.makeText(getActivity(), "Thank you for adding 7 mMarkers. Let there be Animation !", Toast.LENGTH_SHORT).show();
                        LatLngInterpolator interpolator = new LatLngInterpolator();
                        for (LatLng ll : locations) {
                            log("animating to point " + ll.latitude + "," + ll.longitude);
                            animateMarkerToHC(selectedMarker, latLng, interpolator);
                        }
                    } else {
                        Toast.makeText(getActivity(), (7- locations.size()) +" more mMarkers to go !", Toast.LENGTH_SHORT).show();
                    }*/

                    //Code for simplest animation
                    //LatLngInterpolator interpolator = new LatLngInterpolator();
                    //animateMarkerToICS(vMarker, latLng, interpolator);
                }

            });

        } else {
            Toast.makeText(getActivity(), getString(R.string.map_unavailable), Toast.LENGTH_SHORT).show();
            log(getString(R.string.map_unavailable));
        }

    }

    public void updateCustomLocation(LatLng latLng) {

        String url = "https://hypertrack-api-staging.herokuapp.com/api/v1/destinations/" + order.getDestination().getId() + "/update_location/?order_id=" + order.getId();

        com.technortium.tracker.sffoodtrucks.model.Location loc = new com.technortium.tracker.sffoodtrucks.model.Location();

        loc.setType("Point");
        double lng = latLng.longitude;
        double lat = latLng.latitude;
        double[] cords = {lng, lat};
        loc.setCoordinates(cords);

        UpdateLocation updateLocation = new UpdateLocation();
        updateLocation.setLocation(loc);

        Gson gson = new Gson();
        String jsonString = gson.toJson(updateLocation);

        CustomJsonRequest<Destination> jsonObjReq = new CustomJsonRequest<Destination>(
                url, jsonString, Destination.class,
                new Response.Listener<Destination>() {

                    @Override
                    public void onResponse(Destination response) {
                        Log.d(TAG, response.toString() + "is accurate - " + response.getId());
                        getOrderDetails(String.valueOf(order.getId()));
                        //getGpsLocationData(String.valueOf(order.getTrip().getId()));
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(jsonObjReq, SFFT);

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
            //Get Gps logs
            //getGpsLocationData();

            //Uncomment this to activate continous location updates
            //createLocationRequest();

            //Uncomment this to fetch food truck data
            //getFoodVehiclesData();

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

        //updateDatabase(location);
        GpsLocation gpsLocation = new GpsLocation();

        com.technortium.tracker.sffoodtrucks.model.Location loc = new com.technortium.tracker.sffoodtrucks.model.Location();
        loc.setType("Point");
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        double[] cords = {lng, lat};
        loc.setCoordinates(cords);

        gpsLocation.setLocation(loc);
        gpsLocation.setLocation_accuracy(location.getAccuracy());
        gpsLocation.setAltitude(location.getAltitude());
        gpsLocation.setBearing(location.getBearing());
        gpsLocation.setSpeed(location.getSpeed());
        gpsLocation.setCourier("2");

        try {
            postGpsLocationData(gpsLocation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions marker = new MarkerOptions()
                .position(ll);
        mMap.addMarker(marker);

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

            //drawMarker(ll);

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, ZOOM_LEVEL);
            mMap.animateCamera(update);

            //updateDatabase(location);

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

    private void postGpsLocationData(final GpsLocation gpsLocation) throws JSONException {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        //pDialog.show();

        Gson gson = new Gson();
        String jsonString = gson.toJson(gpsLocation);
        JSONObject jsonObject = new JSONObject(jsonString);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                API_ENDPOINT, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        log(response.toString());
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                        //pDialog.hide();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        //pDialog.hide();
                    }
                }) {

          /*  @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                Gson gson = new Gson();
                String location = gson.toJson(gpsLocation.getLocation());

                params.put("location", location);
                params.put("location_accuracy", gpsLocation.getLocation_accuracy());
                params.put("speed", gpsLocation.getSpeed());
                params.put("bearing", gpsLocation.getBearing());
                params.put("altitude", gpsLocation.getAltitude());
                params.put("courier", gpsLocation.getCourier());

                return params;
            }*/

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, SFFT);

    }

    private void getOrderDetails(String order_id) {

        String url = "https://hypertrack-api-staging.herokuapp.com/api/v1/orders/"+order_id;
        CustomRequest<Order> jsonObjReq =
                new CustomRequest<Order>(url, Order.class,
                        new Response.Listener<Order>() {
                            @Override
                            public void onResponse(Order response) {
                                log(response.toString());
                                setOrderData(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //todo - handle contextual error responses
                        Toast.makeText(getActivity(), "Get Request Failed", Toast.LENGTH_SHORT).show();
                    }
                });

        AppController.getInstance().addToRequestQueue(jsonObjReq, SFFT);

    }

    private void setOrderData(Order response) {

        this.order = response;

        if(!response.getStatus().equalsIgnoreCase("out_for_delivery")) {

            Toast.makeText(getActivity(), "Delivery not started. Please try again !", Toast.LENGTH_LONG).show();

        } else {

            if(order != null && order.getDestination() != null) {

                if(order.getDestination().is_location_accurate()) {

                    //marker add
                    //Estimated time of arrival


                    if(order.getDestination().getLocation() != null) {
                        com.technortium.tracker.sffoodtrucks.model.Location loc = order.getDestination().getLocation();

                        double[] coords = loc.getCoordinates();

                        destiationPos = new LatLng(coords[1],coords[0]);


                        homeMarker = mMap.addMarker(new MarkerOptions().position(destiationPos));

                        if (order.getEstimated_delivery_time() != null) {
                            int minutes = getTheEstimatedTime(getCurrentTime(),order.getEstimated_delivery_time());
                            String titleMsg = minutes + " mins to go !";
                            homeMarker.setTitle(titleMsg);
                            homeMarker.showInfoWindow();
                        }

                    }


                    LinearLayout ll = (LinearLayout) view.findViewById(R.id.infoLayout);

                    TextView nameTextView = (TextView) ll.findViewById(R.id.nameInfo);
                    TextView descTextView = (TextView) ll.findViewById(R.id.descriptionInfo);
                    TextView licenseView = (TextView) ll.findViewById(R.id.licenseInfo);

                    if (order != null && order.getEstimated_delivery_time() != null) {

                        if (order.getTrip() != null && order.getTrip().getCourier() != null) {

                            if(order.getTrip().getCourier().getName() != null)
                                nameTextView.setText(order.getTrip().getCourier().getName());

                            if(order.getTrip().getCourier().getVehicle() != null) {

                                if(order.getTrip().getCourier().getVehicle().getDescription() != null)
                                    descTextView.setText(order.getTrip().getCourier().getVehicle().getDescription());

                                if(order.getTrip().getCourier().getVehicle().getLicense_plate()!= null)
                                    licenseView.setText(order.getTrip().getCourier().getVehicle().getLicense_plate());
                            }

                        }

                    }

                    String trip_id = String.valueOf(response.getTrip().getId());
                    getGpsLocationData(trip_id);

                } else {
                    //logic for long press
                    Toast.makeText(getActivity(), "Please select delivery location on map", Toast.LENGTH_LONG).show();

                }
            }



        }
    }

    private void getGpsLocationData(String trip_id) {

        Log.d("network", "Location points for trip_id - " + trip_id);
        String url = "https://hypertrack-api-staging.herokuapp.com/api/v1/gps/filtered/?start=1&trip_id="+trip_id; //+cDateTime;
        //String url = "https://hypertrack-api-staging.herokuapp.com/api/v1/gps/?trip_id=153"; //+String.valueOf(trip_id)+"&min_time="+min_time;

        Log.d("animate", "Fetching first set of points from: " + url);
        //show dialog and close when you get response

        CustomRequest<ResponseObject> jsonObjReq =
                new CustomRequest<ResponseObject>(url, ResponseObject.class,
                        new Response.Listener<ResponseObject>() {
                            @Override
                            public void onResponse(ResponseObject response) {
                                log(response.toString());
                                //Toast.makeText(getActivity(), response.toString().substring(0,30), Toast.LENGTH_SHORT).show();
                                storeMarkerData(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //todo - handle contextual error responses
                        Toast.makeText(getActivity(), "Post Request Failed", Toast.LENGTH_SHORT).show();
                    }
                });


        AppController.getInstance().addToRequestQueue(jsonObjReq, SFFT);

    }

    public boolean nextSetIsBeginFetched = false;

    private void getNextGpsLocationData(int trip_id, String min_time) {
        //if (nextSetIsBeginFetched)
        //    return;
        //nextSetIsBeginFetched = true;
        log("fetchign for trip_id " + trip_id + " min_time: " + min_time);
        String url = "https://hypertrack-api-staging.herokuapp.com/api/v1/gps/filtered/?trip_id="+String.valueOf(trip_id)+"&min_time="+min_time;


        //show dialog and close when you get response

        CustomRequest<ResponseObject> jsonObjReq =
                new CustomRequest<ResponseObject>(url, ResponseObject.class,
                        new Response.Listener<ResponseObject>() {
                            @Override
                            public void onResponse(ResponseObject response) {
                                log(response.toString());
                                //Toast.makeText(getActivity(), response.toString().substring(0,30), Toast.LENGTH_SHORT).show();
                                storeMarkerData(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //todo - handle contextual error responses
                        Toast.makeText(getActivity(), "Post Request Failed", Toast.LENGTH_SHORT).show();
                    }
                });


        AppController.getInstance().addToRequestQueue(jsonObjReq, SFFT);

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
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

    }


    private void drawMarkers() {

        List<FoodTruck> foodTruckList =
                FoodTruckStore.getInstance().getFoodTruckList();

        if (mMarkers != null)
            mMarkers.remove();

        for (FoodTruck foodTruck : foodTruckList) {

            double lat = foodTruck.getLatitude();
            double lng = foodTruck.getLongitude();
            LatLng ll = new LatLng(lat, lng);

            MarkerOptions marker = new MarkerOptions()
                    .title(foodTruck.getApplicant())
                    .position(ll)
                    .snippet(foodTruck.getFooditems());
            mMarkers = mMap.addMarker(marker);

        }

        log("Added " + foodTruckList.size() + " mMarkers !!");
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


    private void animateMarkerToICS(Marker marker, LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {

        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };

        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(5000);
        animator.start();
    }

    public static void animateMarkerToHC(final Marker marker, final LatLng finalPosition,
                                         final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, finalPosition);
                marker.setPosition(newPosition);
            }
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }

    public class LatLngInterpolator {

        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lng = (b.longitude - a.longitude) * fraction + a.longitude;
            return new LatLng(lat, lng);
        }
    }

    private void drawHomeMarker(LatLng ll) {
         homeMarker = mMap.addMarker(new MarkerOptions().position(ll));
    }

    private void updateDatabase(Location location) {

        log("Trip ID:" + tripId);
        if (tripId != null && trips != null) {
            Firebase trip = trips.child(tripId);
            trip.child("locations").push().setValue(location);
        }
        //Firebase locationsRef = ref.child("locations");
        //locationsRef.push().setValue(location);
    }

    protected void createLocationRequest() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);

        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    //Animation POC code down below

    /*private Animator animator = new Animator();*/

    int currentPt;

  /*  GoogleMap.CancelableCallback MyCancelableCallback =
            new GoogleMap.CancelableCallback(){

                @Override
                public void onCancel() {
                    System.out.println("onCancelled called");
                }

                @Override
                public void onFinish() {


                    if(++currentPt < markers.size()){

//						//Get the current location
//						Location startingLocation = new Location("starting point");
//						startingLocation.setLatitude(googleMap.getCameraPosition().target.latitude);
//						startingLocation.setLongitude(googleMap.getCameraPosition().target.longitude);
//
//						//Get the target location
//						Location endingLocation = new Location("ending point");
//						endingLocation.setLatitude(markers.get(currentPt).getPosition().latitude);
//						endingLocation.setLongitude(markers.get(currentPt).getPosition().longitude);
//
//						//Find the Bearing from current location to next location
//						float targetBearing = startingLocation.bearingTo(endingLocation);

                        float targetBearing = bearingBetweenLatLngs( mMap.getCameraPosition().target, markers.get(currentPt).getPosition());

                        LatLng targetLatLng = markers.get(currentPt).getPosition();
                        //float targetZoom = zoomBar.getProgress();


                        System.out.println("currentPt  = " + currentPt  );
                        System.out.println("size  = " + markers.size());
                        //Create a new CameraPosition
                        CameraPosition cameraPosition =
                                new CameraPosition.Builder()
                                        .target(targetLatLng)
                                        .tilt(currentPt<markers.size()-1 ? 90 : 0)
                                        .bearing(targetBearing)
                                        .zoom(mMap.getCameraPosition().zoom)
                                        .build();


                        mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(cameraPosition),
                                2000,
                                MyCancelableCallback);
                        System.out.println("Animate to: " + markers.get(currentPt).getPosition() + "\n" +
                                "Bearing: " + targetBearing);

                        markers.get(currentPt).showInfoWindow();

                    }else{
                        //info.setText("onFinish()");
                    }

                }

            };*/

    /*public class Animator implements Runnable {

        private static final long ANIMATE_SPEEED = 2000; //controls speeds
        private static final int ANIMATE_SPEEED_TURN = 500; // controls speed turn
        private int BEARING_OFFSET = 20; // bearing offset.. what is it ?
        private final Interpolator interpolator = new LinearInterpolator(); //creates a new interpolar

        int currentIndex = 0; // sets first marker as current

        float tilt = 90; // sets tilts as 90
        float zoom = 12f; // sets default zoom level
        boolean upward=true; // don't know what this is

        long start = SystemClock.uptimeMillis();

        LatLng endLatLng = null;
        LatLng beginLatLng = null;

        boolean showPolyline = false;

        private Marker trackingMarker;

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();
            GMapFragment.this.speed = getTimeDifference(0,1);
        }

        public void stop() {
            //trackingMarker.remove();
            mHandler.removeCallbacks(animator);

        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;

            highLightMarker(0);

            if (showPolyLine) {
                polyLine = initializePolyLine();
            }

            // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
            LatLng markerPos = markers.get(0).getPosition();
            LatLng secondPos = markers.get(1).getPosition();

            setupCameraPositionForMovement(markerPos, secondPos);

        }

        private void setupCameraPositionForMovement(LatLng markerPos,
                                                    LatLng secondPos) {

            float bearing = bearingBetweenLatLngs(markerPos, secondPos);

            trackingMarker = mMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_marker2)));

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(markerPos)
                            //.bearing(bearing + BEARING_OFFSET)
                            //.tilt(90)
                            .zoom(mMap.getCameraPosition().zoom >=12 ? mMap.getCameraPosition().zoom : 12)
                            .build();


            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            animator.reset();
                            Handler handler = new Handler();
                            handler.post(animator);
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }
            );
        }

        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();


        private Polyline initializePolyLine() {
            //polyLinePoints = new ArrayList<LatLng>();
            rectOptions.add(markers.get(0).getPosition());
            return mMap.addPolyline(rectOptions);
        }

        *//**
         * Add the marker to the polyline.
         *//*
        private void updatePolyLine(LatLng latLng) {
            List<LatLng> points = polyLine.getPoints();
            points.add(latLng);
            polyLine.setPoints(points);
        }


        public void stopAnimation() {
            animator.stop();
        }

        public void startAnimation(boolean showPolyLine) {
            if (markers.size()>2) {
                animator.initialize(showPolyLine);
            }
        }


        @Override
        public void run() {

            float bearingL = 0.0f;
            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);

//			LatLng endLatLng = getEndLatLng();
//			LatLng beginLatLng = getBeginLatLng();

            double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
            double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
            LatLng newPosition = new LatLng(lat, lng);

            trackingMarker.setPosition(newPosition);

            if (showPolyline) {
                updatePolyLine(newPosition);
            }

            // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
            //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
            //navigateToPoint(newPosition,false);

            if (t< 1) {
                mHandler.postDelayed(this, 16);
            } else {

               Log.d("animate","Move to next marker.... current = " + currentIndex + " to = " + (currentIndex+1));

                if (currentIndex<markers.size()-2) {

                    currentIndex++;

                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();

                    start = SystemClock.uptimeMillis();

                    GMapFragment.this.speed = getTimeDifference(currentIndex);
                    Log.d("animate","Time difference: " + speed);

                    LatLng begin = getBeginLatLng();
                    LatLng end = getEndLatLng();

                    bearingL = bearingBetweenLatLngs(begin, end);
                    trackingMarker.setRotation(bearingL+ BEARING_OFFSET);

                    highLightMarker(currentIndex);

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(end) // changed this...
//                                    .bearing(bearingL  + BEARING_OFFSET)
//                                    .tilt(tilt)
                                    .zoom(mMap.getCameraPosition().zoom)
                                    .build();

                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );

                    if (currentIndex == markers.size()-6) {
                        GpsLocation gpsLocation = gpsLocationList.get(markers.size()-1);
                        //getNextGpsLocationData(Integer.valueOf(gpsLocation.getTrip_id()), 6, gpsLocation.getId());
                    }

                    start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(animator, 16);

                } else {
                    currentIndex++;
                    highLightMarker(currentIndex);
                    stopAnimation();
                }

            }
        }




        private LatLng getEndLatLng() {
            return markers.get(currentIndex+1).getPosition();
        }

        private LatLng getBeginLatLng() {
            return markers.get(currentIndex).getPosition();
        }

        private void adjustCameraPosition() {
            //System.out.println("tilt = " + tilt);
            //System.out.println("upward = " + upward);
            //System.out.println("zoom = " + zoom);
            if (upward) {

                if (tilt<90) {
                    tilt ++;
                    zoom-=0.01f;
                } else {
                    upward=false;
                }

            } else {
                if (tilt>0) {
                    tilt --;
                    zoom+=0.01f;
                } else {
                    upward=true;
                }
            }
        }
    };*/

    /**
     * Allows us to navigate to a certain point.
     */
    public void navigateToPoint(LatLng latLng,float tilt, float bearing, float zoom,boolean animate) {
        CameraPosition position =
                new CameraPosition.Builder().target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build();

        changeCameraPosition(position, animate);

    }

    public void navigateToPoint(LatLng latLng, boolean animate) {
        CameraPosition position = new CameraPosition.Builder().target(latLng).build();
        changeCameraPosition(position, animate);
    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        Location beginL= convertLatLngToLocation(begin);
        Location endL= convertLatLngToLocation(end);

        return beginL.bearingTo(endL);
    }

    public void toggleStyle() {
        if (GoogleMap.MAP_TYPE_NORMAL == mMap.getMapType()) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


    /**
     * Adds a marker to the map.
     */
    public void addMarkerToMap(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("title")
                .snippet("snippet"));
        markers.add(marker);

    }

    /**
     * Clears all markers from the map.
     */
    public void clearMarkers() {
        mMap.clear();
        markers.clear();
    }

    /**
     * Remove the currently selected marker.
     */
    public void removeSelectedMarker() {
        this.markers.remove(this.selectedMarker);
        this.selectedMarker.remove();
    }

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {
        highLightMarker(markers.get(index));
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {

		/*
		for (Marker foundMarker : this.markers) {
			if (!foundMarker.equals(marker)) {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			} else {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				foundMarker.showInfoWindow();
			}
		}
		*/
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        //Utils.bounceMarker(googleMap, marker);

        this.selectedMarker=marker;
    }

    private void resetMarkers() {
        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    public boolean animate = false;

    protected void storeMarkerData(ResponseObject responseObject) {

        GpsLocation[] gpsLocations = responseObject.getGpsLocations();

        Log.d("animate", "Fetched " + gpsLocations.length + " points");

        for (int i=0; i < gpsLocations.length-1; i++) {
            com.technortium.tracker.sffoodtrucks.model.Location location = gpsLocations[i].getLocation();

            log(location.toString());
            double[] ll = location.getCoordinates();
            LatLng latLng = new LatLng(ll[1], ll[0]);

            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
            markers.add(marker);
            gpsLocationList.add(gpsLocations[i]);
            marker.setVisible(false);
        }

        Log.d("animate", "Size after adding " + markers.size() + "with animate flag still " + animate);

        if ((markers.size() > 2 && !animate) || nextSetIsBeginFetched) {
            letTheAnimationBegin();
            animate = true;
            nextSetIsBeginFetched = false;
        }
            //animator.startAnimation(true);

    }

    private int getTheEstimatedTime(String currentTime, String estimatedTime) {

        long seconds = 0;
        int minutes = 0;

        Log.d("ETA", "ETA " + currentTime + " " + estimatedTime);
        String currentTimeString = currentTime.substring(0,currentTime.length()-5);
        String estimatedTimeString = estimatedTime.substring(0,estimatedTime.length()-1);
        Log.d("ETA", "ETA " + currentTimeString + " " + estimatedTimeString);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {

            Date startDate = df.parse(currentTimeString);
            Date endDate = df.parse(estimatedTimeString);
            seconds = (endDate.getTime() - startDate.getTime())/1000;
            minutes = (int)seconds/60;

            //Log.d("Animate", startTimeStamp.toString() + " " + endTimeStamp.toString() + " Seconds: " + seconds);
        } catch(ParseException ex) {
            ex.printStackTrace();
        }

        if(minutes < 0) minutes=0;

        Log.d("ETA", "ETA" + minutes);
        return minutes;
    }

    private long getTimeDifference(int firstIndex) {

        long seconds = 0;


        if (firstIndex < gpsLocationList.size()-2) {
            GpsLocation startGpsLocation = gpsLocationList.get(firstIndex);
            GpsLocation endGpsLocation = gpsLocationList.get(firstIndex + 1);

            String startTimeStamp = formatDate(startGpsLocation.getCreated_at());
            String endTimeStamp = formatDate(endGpsLocation.getCreated_at());

            Log.d("Animate", startTimeStamp + " " + endTimeStamp);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            try {

                Date startDate = df.parse(startTimeStamp);
                Date endDate = df.parse(endTimeStamp);
                seconds = (endDate.getTime() - startDate.getTime())/1000;
                //Log.d("Animate", startTimeStamp.toString() + " " + endTimeStamp.toString() + " Seconds: " + seconds);
            } catch(ParseException ex) {
                ex.printStackTrace();
            }

        }

        return seconds;
    }

    private long getTimeDifferenceFromLastPoint(int currentIndex) {

        //todo this things might get changed
        long seconds = 0;

        if (currentIndex < gpsLocationList.size()-2) {
            GpsLocation startGpsLocation = gpsLocationList.get(currentIndex);
            GpsLocation endGpsLocation = gpsLocationList.get(gpsLocationList.size()-1);

            String startTimeStamp = formatDate(startGpsLocation.getCreated_at());
            String endTimeStamp = formatDate(endGpsLocation.getCreated_at());

            //Log.d("Animate", startTimeStamp + " " + endTimeStamp);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            try {

                Date startDate = df.parse(startTimeStamp);
                Date endDate = df.parse(endTimeStamp);
                seconds = (endDate.getTime() - startDate.getTime())/1000;

                //Log.d("Animate", startTimeStamp.toString() + " " + endTimeStamp.toString() + " Seconds: " + seconds);
            } catch(ParseException ex) {
                ex.printStackTrace();
            }

        }
        return seconds;
    }

    private String formatDate(String dateString) {
        return dateString.substring(0,dateString.length()-4);
    }

    public void letTheAnimationBegin() {
        
        searchLayout.setVisibility(View.INVISIBLE);

        index =  0;
        LatLng markerPos = markers.get(0).getPosition();
        LatLng secondPos = markers.get(1).getPosition();
        float bearing = bearingBetweenLatLngs(markerPos, secondPos);

        LatLngBounds.Builder b = new LatLngBounds.Builder();

        b.include(markerPos);
        b.include(destiationPos);

        trackingMarker = mMap.addMarker(new MarkerOptions().position(markerPos)
                .title("title")
                .snippet("snippet")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_marker2)));

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(markerPos)
                                //.bearing(bearing + BEARING_OFFSET)
                                //.tilt(90)
                        .zoom(mMap.getCameraPosition().zoom >=12 ? mMap.getCameraPosition().zoom : 12)
                        .build();

        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);

        //CameraUpdateFactory.newCameraPosition(cameraPosition)
        mMap.animateCamera(cu
                ,
                2000,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        animateToNextPoint();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("cancelling camera");
                    }
                }
        );

    }

    public double calculateDistance(double fromLatitude,double fromLongitude,double toLatitude,double toLongitude)
    {

        float results[] = new float[1];

        try {
            Location.distanceBetween(fromLatitude,fromLongitude, toLatitude, toLongitude, results);
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
        }

        int dist = (int) results[0];
        if(dist<=0)
            return 0D;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        results[0]/=1000D;
        String distance = decimalFormat.format(results[0]);
        double d = Double.parseDouble(distance);
        return d;
    }

    public float lastBearing = -1000;
    public int lastIndex = -1;

    public void animateToNextPoint() {
        if (lastIndex == -1)
            lastIndex = 0;
        if (index < markers.size() - 1) {
            index++;
            LatLng _startLatLng = markers.get(lastIndex).getPosition();
            LatLng _toPosition = markers.get(index).getPosition();

            double _distance = calculateDistance(_startLatLng.latitude, _startLatLng.longitude, _toPosition.latitude, _toPosition.longitude);

            boolean doAnim = true;

            if (_distance < 0.05)
                doAnim = false;


            //long speedDuration = getTimeDifference(index) * 1000;
            //LatLng startLatLng = markers.get(index).getPosition();
            long speedDuration = getTimeDifference(index-1) * 1000;

            LatLng startLatLng = markers.get(lastIndex).getPosition();
            LatLng toPosition = markers.get(index).getPosition();

            double distance = calculateDistance(startLatLng.latitude, startLatLng.longitude, toPosition.latitude, toPosition.longitude);

            if (order.getEstimated_delivery_time() != null) {
                int minutes = getTheEstimatedTime(getCurrentTime(),order.getEstimated_delivery_time());
                String titleMsg = minutes + " mins to go !";
                homeMarker.setTitle(titleMsg);
                homeMarker.showInfoWindow();
            }

            Log.d("animate", "Points: " + startLatLng + " " + toPosition + "Distance: " + distance);

            boolean coordsChanged = true;
            if (startLatLng.longitude == toPosition.longitude && startLatLng.latitude == toPosition.latitude)
                coordsChanged = false;
            float bearing = 0;
            if (lastBearing == -1000 || coordsChanged)
                bearing = bearingBetweenLatLngs(startLatLng, toPosition);
            else
                bearing = lastBearing;
            lastBearing = bearing;

            trackingMarker.setRotation(bearing);

            if (doAnim)
                lastIndex = index;

            int bufferSize = markers.size() - index;

            if (gpsLocationList.get(index-1).isFetch_next_points() ) {
                getNextGpsLocationData(Integer.valueOf(gpsLocationList.get(lastIndex).getTrip_id()),getLastMinTime());
                Log.d("animate","Fetching next set of points because I was asked to");
            }

            animateMarker(trackingMarker, startLatLng, toPosition, speedDuration, bearing, doAnim);
            Log.d("animate", " Indexes: " + lastIndex + " " + index + " " + speedDuration + "secs" + " Bearing: " + bearing + " Fetch " + gpsLocationList.get(index-1).isFetch_next_points());
            Log.d("animate", " Gps cordinates ids: " + gpsLocationList.get(lastIndex).getId() + " " +  gpsLocationList.get(index).getId());
            //Log.d("animate", " Cordinates: " + startLatLng.latitude + "," + startLatLng.longitude + "," + " "+ toPosition.latitude + " " + toPosition.longitude + " Bearing: " + bearing);
        } else {

            getNextGpsLocationData(Integer.valueOf(gpsLocationList.get(lastIndex).getTrip_id()),getLastMinTime());
            Log.d("animate", "Ran out of points so I'm asking for more");
            nextSetIsBeginFetched = true;
            animateMarker(trackingMarker, markers.get(index).getPosition(),markers.get(index).getPosition(), 2000, lastBearing, false);

        }
    }

    public String getCurrentTime() {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String cDateTime=dateFormat.format(new Date());
        return  cDateTime;
    }

    public String getLastMinTime() {
        return  gpsLocationList.get(gpsLocationList.size()-1).getCreated_at();
    }

    //Try replacing marker with tracing marker
    public void animateMarker(final Marker marker, final LatLng startLatLng, final LatLng toPosition, final long speedDuration,
                              final float bearing, final boolean doAnim) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final int duration = (speedDuration <= 0) ? 1: (int) speedDuration;

        final Interpolator interpolator = new LinearInterpolator();

        if(doAnim) {
            handler.post(new Runnable() {
                             @Override
                             public void run() {
                                 {
                                     long elapsed = SystemClock.uptimeMillis() - start;
                                     float t = interpolator.getInterpolation((float) elapsed
                                             / speedDuration);
                                     double lng = t * toPosition.longitude + (1 - t)
                                             * startLatLng.longitude;
                                     double lat = t * toPosition.latitude + (1 - t)
                                             * startLatLng.latitude;
                                     LatLng ll = new LatLng(lat, lng);
                                     marker.setPosition(ll);

                                     lastKnownLocation = ll;

                                     if (t < 1.0) {
                                         // Post again 16ms later.
                                         handler.postDelayed(this, 16);
                                     } else {
                                         if (index < markers.size() - 2)
                                             animateToNextPoint();
                                         else {
                                             Log.d("animate", "Inside animateMarker" + trackingMarker.getPosition());
                                         }
                                     }
                                 }
                             }
                         }
            );
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / speedDuration);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        if (index < markers.size() - 2)
                            animateToNextPoint();
                        else {
                            Log.d("animate", "Inside animateMarker" + trackingMarker.getPosition());
                        }
                    }
                }
            });
        }
    }
}
