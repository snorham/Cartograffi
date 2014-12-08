package com.detroitlabs.cartograffi.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.activities.SaveActivity;
import com.detroitlabs.cartograffi.adapters.ColorsRecyclerAdapter;
import com.detroitlabs.cartograffi.interfaces.ColorClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFragment extends Fragment implements View.OnClickListener, LocationListener, ColorClickListener {
    public final static String MAP_IMAGE_KEY = "MAP_IMAGE_KEY";

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private String locationProvider;
    private Polyline polyline;
    private boolean drawOn;
    private int[] colors;
    private int currentColor;
    private Bitmap mapImage;
    private boolean hidden;
    private ToggleButton drawToggle;

    public CreateFragment() {
    }

    //FOR LATER
    public static CreateFragment newInstance() {
        Bundle args = new Bundle();
        CreateFragment createFrag = new CreateFragment();
        createFrag.setArguments(args);
        return createFrag;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_hide:
                if (hidden) {
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    drawToggle.setVisibility(View.VISIBLE);
                    hidden = false;

                } else {
                    googleMap.getUiSettings().setZoomControlsEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.getUiSettings().setCompassEnabled(false);
                    drawToggle.setVisibility(View.INVISIBLE);
                    hidden = true;
                }

                return true;
            case R.id.action_save_snapshot:

                //FIX LATER
                mapImage = BitmapFactory.decodeResource(getResources(),
                    R.drawable.die);
                goToSaveScreen();

                //captureMapImage();
                return true;

            case R.id.action_view_snapshots:
                //go to viewer
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create, container, false);

        currentColor = getResources().getColor(R.color.Black);
        final float defaultZoom = 15;
        setColors();

        setUpMapIfNeeded();
        initializeLocationManager();
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(defaultZoom);
        googleMap.animateCamera(zoom);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

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
        //when the userLocation changes, update the map by moving to the userLocation
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(userLatLng);
        googleMap.moveCamera(center);

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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }

        googleMap.setMyLocationEnabled(true);
    }

    private void initializeLocationManager() {
        final int minTime = 1000; //time between userLocation updates in milliseconds
        final int minDistance = 1; //distance required to move to update userLocation in meters;

        //get the userLocation manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //define the userLocation manager criteria
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        locationProvider = locationManager.getBestProvider(criteria, false);

        Location userLocation = locationManager.getLastKnownLocation(locationProvider);

        //initialize the userLocation
        if (userLocation != null) {
            onLocationChanged(userLocation);
        }

        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
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

    public void captureMapImage() {
        polyline = null;
        locationManager.removeUpdates(this);
        googleMap.setMyLocationEnabled(false);

        final GoogleMap.SnapshotReadyCallback snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                mapImage = bitmap;
                goToSaveScreen();
            }
        };
        googleMap.snapshot(snapshotReadyCallback);
    }

    public void goToSaveScreen() {
        Intent saveIntent = new Intent(getActivity(), SaveActivity.class);
        saveIntent.putExtra(MAP_IMAGE_KEY, mapImage);
        startActivity(saveIntent);
    }
}
