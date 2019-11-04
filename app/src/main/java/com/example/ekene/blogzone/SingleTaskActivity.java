package com.example.ekene.blogzone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SingleTaskActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView singleImage;
    private MapView mapView;
    private double lat;
    private double lng;
    private boolean hasPosition = false;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyD3PnRRrmeaG-LiWgM9YoHVNyWdvl4hxvY";
    private TextView singleTitle, singleDesc, timestamp;
    private Button deleteBtn;
    private DBManager dbManager;
    private int task_id;
    private Tasks task = new Tasks();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_task);

        task_id = getIntent().getExtras().getInt("task_id");
        dbManager = new DBManager(this);
        dbManager.open();
        task = dbManager.fetchSingleTask(task_id);
        singleImage = (ImageView)findViewById(R.id.singleImageview);

        byte[] image = task.getImage();
        Bitmap bitmap;
        if (image != null) {
            bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            singleImage.setImageBitmap(bitmap);
        }
        else
            singleImage.setVisibility(View.GONE);

        singleTitle = (TextView)findViewById(R.id.singleTitle);
        singleTitle.setText(task.getTitle());

        singleDesc = (TextView)findViewById(R.id.singleDesc);
        final String desc = task.getDesc();
        if (!TextUtils.isEmpty(desc))
            singleDesc.setText(desc);
        else
            singleDesc.setVisibility(View.GONE);

        timestamp = (TextView) findViewById(R.id.timestamp);
        final String ts = task.getTimestamp();
        if (!TextUtils.isEmpty(ts))
            timestamp.setText(ts);
        else
            timestamp.setVisibility(View.GONE);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        deleteBtn = (Button)findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager.deleteTask(task_id);
                setResult(4);
                Toast.makeText(SingleTaskActivity.this, "Task Deleted..", Toast.LENGTH_SHORT).show();
                SingleTaskActivity.super.onBackPressed();
            }
        });
        lat = task.getLat();
        lng = task.getLng();

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        if (lat != 0 && lng!= 0) {
            gmap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));
            moveToCurrentLocation(new LatLng(lat,lng));
        }
        else
            mapView.setVisibility(View.GONE);
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        gmap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        gmap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
}
