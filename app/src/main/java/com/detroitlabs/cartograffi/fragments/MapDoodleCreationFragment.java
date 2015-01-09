package com.detroitlabs.cartograffi.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.detroitlabs.cartograffi.adapters.ColorsRecyclerAdapter;
import com.detroitlabs.cartograffi.fragments.DeleteConfirmationDialogFragment.DeleteConfirmationInterface;
import com.detroitlabs.cartograffi.interfaces.OnColorClickListener;
import com.detroitlabs.cartograffi.utils.CartograffiUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapDoodleCreationFragment extends Fragment implements View.OnClickListener, LocationListener, OnColorClickListener, OnMapReadyCallback {
    public static final String MAP_IMAGE_KEY = "MAP_IMAGE_KEY";
    public static final String CAMERA_ZOOM_KEY = "cameraZoom";
    public static final String CAMERA_POSITION_KEY = "cameraPosition";
    public static final String POLYLINES_KEY = "polylines";
    public static final String SELECTED_COLOR_INDEX_KEY = "selectedColorIndex";
    public static final String DRAW_ON_KEY = "drawOn";

    private float defaultZoom;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private String locationProvider;
    private Polyline polyline;
    private boolean drawOn;
    private boolean hidden;
    private ToggleButton drawToggle;
    private Bundle savedInstanceState;
    private Menu menu;
    private MapView mapView;
    private ArrayList<Polyline> polylines = new ArrayList<Polyline>();
    private int[] colors;
    private boolean[] selectedStates;
    private int selectedColorIndex;
    private ProgressDialog progressDialog;


    public MapDoodleCreationFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GoogleMap.SnapshotReadyCallback snapshotReadyCallback;

        switch (item.getItemId()){
            case R.id.action_hide:
                toggleMapUi();
                return true;

            case R.id.action_share:
                menu.setGroupEnabled(0, false);
                SaveMapSnapshotFragment.directory.mkdirs();
                snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        CartograffiUtils.shareBitmap(getActivity(), bitmap);
                    }
                };
                captureMapImage(snapshotReadyCallback);
                return true;

            case R.id.action_save_snapshot:
                menu.setGroupEnabled(0, false);
                displayLoadingWheelProgressDialog();
                SaveMapSnapshotFragment.directory.mkdirs();
                snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        goToSaveScreen(bitmap);
                    }
                };
                captureMapImage(snapshotReadyCallback);
                return true;

            case R.id.action_erase_map:
                displayDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setHasOptionsMenu(true);

        colors = CartograffiUtils.getAllColorResources(getActivity());
        selectedStates = new boolean[colors.length];

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create, container, false);
        loadDrawSettings();

        mapView = (MapView)root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        drawToggle = (ToggleButton) root.findViewById(R.id.create_draw_toggle);
        drawToggle.setChecked(drawOn);
        drawToggle.setOnClickListener(this);

        RecyclerView colorsRecycler = (RecyclerView) root.findViewById(R.id.create_colors_recycler);
        colorsRecycler.setHasFixedSize(true);
        colorsRecycler.setSaveEnabled(false);

        LinearLayoutManager linLayoutMan = new LinearLayoutManager(getActivity());
        linLayoutMan.setOrientation(LinearLayoutManager.HORIZONTAL);
        colorsRecycler.setLayoutManager(linLayoutMan);

        RecyclerView.Adapter recyclerAdapter = new ColorsRecyclerAdapter(this, colors, selectedStates);
        colorsRecycler.setAdapter(recyclerAdapter);
        colorsRecycler.scrollToPosition(selectedColorIndex);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (CartograffiUtils.tempSharedFile.exists()){
            CartograffiUtils.tempSharedFile.delete();
        }

        ActionBar ab = getActivity().getActionBar();
        if (ab != null){
            ab.setTitle(getResources().getString(R.string.title_activity_create));
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(false);
        }
        displayLoadingWheelProgressDialog();
        mapView.getMapAsync(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        saveSettingsToBundle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.create_draw_toggle:
                drawOn = ((ToggleButton) v).isChecked();
                if (drawOn) {
                    startDrawing(colors[selectedColorIndex]);
                } else {
                    polyline = null;
                }
        }
    }

    @Override
    public void onColorClick(int selectedColorIndex) {
        this.selectedColorIndex = selectedColorIndex;

        if (drawOn) {
            startDrawing(colors[this.selectedColorIndex]);
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        loadMapSettings();
        setUpGoogleMap();

        if (menu != null){
            menu.setGroupEnabled(0, true);
        }
    }

    public void saveSettingsToBundle() {
        if(savedInstanceState == null){
            savedInstanceState = new Bundle();
        }

        if(googleMap != null) savedInstanceState.putFloat(CAMERA_ZOOM_KEY,googleMap.getCameraPosition().zoom);
        savedInstanceState.putParcelable(CAMERA_POSITION_KEY, googleMap.getCameraPosition());
        savedInstanceState.putSerializable(POLYLINES_KEY, polylines);
        savedInstanceState.putInt(SELECTED_COLOR_INDEX_KEY, selectedColorIndex);
        savedInstanceState.putBoolean(DRAW_ON_KEY, drawOn);

    }

    public void loadMapSettings() {
        if (savedInstanceState != null){

            polylines = (ArrayList<Polyline>)savedInstanceState.getSerializable(POLYLINES_KEY);

            if (polylines != null){

                for (Polyline line: polylines){
                    PolylineOptions polylineOptions = new PolylineOptions();

                    for (LatLng point: line.getPoints()){
                        polylineOptions.add(point);
                    }

                    polylineOptions.color(line.getColor());
                    googleMap.addPolyline(polylineOptions);
                }

            } else {
                polylines = new ArrayList<Polyline>();
            }

            defaultZoom = savedInstanceState.getFloat(CAMERA_ZOOM_KEY, defaultZoom);
            if (defaultZoom == 0) defaultZoom = 15;

            CameraPosition cameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION_KEY);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.animateCamera(cameraUpdate);

        } else {
            defaultZoom = 15;
        }
    }

    public void loadDrawSettings(){
        if (savedInstanceState != null){
            selectedColorIndex = savedInstanceState.getInt(SELECTED_COLOR_INDEX_KEY, 0);
            drawOn = savedInstanceState.getBoolean(DRAW_ON_KEY, false);

        } else {
            selectedColorIndex = 0;
            drawOn = false;
        }

        selectedStates[selectedColorIndex] = true;
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
        //moved from here
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        //moved to here
        Location userLocation = locationManager.getLastKnownLocation(locationProvider);
        //initialize the userLocation
        if (userLocation != null) {
            onLocationChanged(userLocation);
        }
    }

    public void startDrawing(int color) {
        Location userLocation = locationManager.getLastKnownLocation(locationProvider);
        LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions rectOptions = new PolylineOptions()
                .color(color)
                .add(userLatLng);

        // Get back the mutable Polyline and add to arraylist
        polyline = googleMap.addPolyline(rectOptions);
        polylines.add(polyline);

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

    public void goToSaveScreen(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);
        dismissLoadingWheelProgressDialog();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.container_frame, SaveMapSnapshotFragment.newInstance(bitmap));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

        hidden = !enabled;
    }

    private void setUpGoogleMap() {
        setMapUiEnabled(true);
        initializeLocationManager();
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(defaultZoom);
        googleMap.animateCamera(zoom);

        googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                dismissLoadingWheelProgressDialog();
            }
        });
    }

    public void toggleMapUi() {
        if (hidden) {
            setMapUiEnabled(true);
        } else {
            setMapUiEnabled(false);
        }
    }

    public void displayDeleteConfirmationDialog(){
        final DeleteConfirmationDialogFragment deleteDialog = DeleteConfirmationDialogFragment.newInstance(new DeleteConfirmationInterface() {
            @Override
            public void onDialogClick(boolean confirmationClick) {
                if (confirmationClick) {
                    if (drawOn) {
                        drawToggle.callOnClick();
                        drawToggle.setChecked(false);
                    }
                    polylines = new ArrayList<Polyline>();
                    googleMap.clear();
                }
            }
        });
        deleteDialog.show(getFragmentManager(), "DeleteDialog");
    }

    public void displayLoadingWheelProgressDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.progress_dialog));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissLoadingWheelProgressDialog(){
        progressDialog.dismiss();
    }




    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //required by location listener... but not being used
    }

    @Override
    public void onProviderEnabled(String provider) {
        //required by location listener... but not being used
    }

    @Override
    public void onProviderDisabled(String provider) {
        //required by location listener... but not being used
    }
}