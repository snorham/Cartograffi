package com.detroitlabs.cartograffi.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.adapters.ColorsRecyclerAdapter;
import com.detroitlabs.cartograffi.interfaces.ColorClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFragment extends Fragment implements View.OnClickListener, LocationListener, ColorClickListener, OnMapReadyCallback {
    public final static String MAP_IMAGE_KEY = "MAP_IMAGE_KEY";
    public final static String CAMERA_POSITION_KEY = "cameraPosition";
    private float defaultZoom;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private String locationProvider;
    private Polyline polyline;
    private boolean drawOn;
    private int[] colors;
    private int currentColor;
    private boolean hidden;
    private ToggleButton drawToggle;
    private Bundle savedInstanceState;
    private Menu menu;
    MapView mapView;

    public CreateFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create, container, false);

        mapView = (MapView)root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        Log.d(CreateFragment.class.getName(), "OnCreateView");
        currentColor = getResources().getColor(R.color.Black);
        setColors();

        drawToggle = (ToggleButton) root.findViewById(R.id.create_draw_toggle);
        drawToggle.setOnClickListener(this);

        RecyclerView colorsRecycler = (RecyclerView) root.findViewById(R.id.create_colors_recycler);
        colorsRecycler.setHasFixedSize(true);

        LinearLayoutManager linLayoutMan = new LinearLayoutManager(getActivity());
        linLayoutMan.setOrientation(LinearLayoutManager.HORIZONTAL);
        colorsRecycler.setLayoutManager(linLayoutMan);

        RecyclerView.Adapter recyclerAdapter = new ColorsRecyclerAdapter(this, colors);
        colorsRecycler.setAdapter(recyclerAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(CreateFragment.class.getName(), "OnResume");

        if (menu != null){
            menu.setGroupEnabled(0, true);
        }

        if (savedInstanceState != null){
            defaultZoom = savedInstanceState.getFloat(CAMERA_POSITION_KEY, defaultZoom);
        } else {
            defaultZoom = 15;
        }

        ActionBar ab = getActivity().getActionBar();
        if (ab != null){
            ab.setTitle(getResources().getString(R.string.title_activity_create));
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(savedInstanceState == null){
            savedInstanceState = new Bundle();
        }

        savedInstanceState.putFloat(CAMERA_POSITION_KEY,googleMap.getCameraPosition().zoom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_draw_toggle:
                drawOn = ((ToggleButton) v).isChecked();

                if (drawOn) {
                    startDrawing(currentColor);
                } else {
                    polyline = null;
                }
        }
    }

    @Override
    public void onColorClick(int color) {

        currentColor = color;

        if (drawOn) {
            startDrawing(currentColor);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (!hidden && googleMap != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(userLatLng);
            googleMap.moveCamera(center);
        }

        if (drawOn) {
            List<LatLng> polylineLatLngs = polyline.getPoints();
            polylineLatLngs.add(userLatLng);
            polyline.setPoints(polylineLatLngs);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //required by location listener
    }

    @Override
    public void onProviderEnabled(String provider) {
        //required by location listener
    }

    @Override
    public void onProviderDisabled(String provider) {
        //required by location listener
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(CreateFragment.class.getName(),"onMapReady");
        this.googleMap = googleMap;
        setUpGoogleMap();
    }

//    private void setUpMapFragment() {
//
//        if (mapView == null) {
//            Log.d(CreateFragment.class.getName(),"setUpMapFragment mapFragment == null");
//            mapFragment = MapFragment.newInstance();
//            mapFragment.setRetainInstance(true);
//            getChildFragmentManager().beginTransaction().add(R.id.create_map_container, mapFragment).commit();
//            mapFragment.getMapAsync(this);
//        } else {
//            Log.d(CreateFragment.class.getName(),"setUpMapFragment mapFragment != null");
//            mapFragment.getMapAsync(this);
//        }
//    }

    private void initializeLocationManager() {
        final int minTime = 1000; //time between userLocation updates in milliseconds
        final int minDistance = 1; //distance required to move to update userLocation in meters;
        //get the userLocation manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //define the userLocation manager criteria
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        locationProvider = locationManager.getBestProvider(criteria, false);
        //moved from here
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        //moved to here
        Location userLocation = locationManager.getLastKnownLocation(locationProvider);
        //initialize the userLocation
        if (userLocation != null) {
            onLocationChanged(userLocation);
        }
    }

    public void setColors() {
        colors = new int[]{
                getResources().getColor(R.color.Black),
                getResources().getColor(R.color.White),
                getResources().getColor(R.color.Gray),
                getResources().getColor(R.color.Red),
                getResources().getColor(R.color.Orange),
                getResources().getColor(R.color.Yellow),
                getResources().getColor(R.color.Green),
                getResources().getColor(R.color.Blue),
                getResources().getColor(R.color.Indigo),
                getResources().getColor(R.color.Violet)};
    }

    public void startDrawing(int color) {
        Location userLocation = locationManager.getLastKnownLocation(locationProvider);
        LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions rectOptions = new PolylineOptions()
                .color(color)
                .add(userLatLng);

        // Get back the mutable Polyline
        polyline = googleMap.addPolyline(rectOptions);
    }

    public void captureMapImage(final GoogleMap.SnapshotReadyCallback snapshotReadyCallback) {
        setMapUiEnabled(false);
        locationManager.removeUpdates(this);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.snapshot(snapshotReadyCallback);
            }
        });
    }

    public void setMapUiEnabled(boolean enabled){
        googleMap.setMyLocationEnabled(enabled);
        googleMap.getUiSettings().setZoomControlsEnabled(enabled);
        googleMap.getUiSettings().setMyLocationButtonEnabled(enabled);
        googleMap.getUiSettings().setCompassEnabled(enabled);

        if (enabled){
            drawToggle.setVisibility(View.VISIBLE);
        } else {
            drawToggle.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpGoogleMap() {
        initializeLocationManager();

        setMapUiEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(defaultZoom);
        googleMap.animateCamera(zoom);
    }

    public void toggleMapUi() {
        if (hidden) {
            setMapUiEnabled(true);
            hidden = false;

        } else {
            setMapUiEnabled(false);
            hidden = true;
        }
    }
}