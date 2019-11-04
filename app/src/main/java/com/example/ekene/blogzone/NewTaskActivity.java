package com.example.ekene.blogzone;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity implements OnMapReadyCallback {
    // imports
    Context mContext;
    private ImageButton imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private Bitmap bitmap;
    private EditText textTitle;
    private EditText textDesc;
    private MapView mapView;
    private GoogleMap gmap;
    private LocationManager locationManager;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyD3PnRRrmeaG-LiWgM9YoHVNyWdvl4hxvY";
    private Button addBtn;
    private Marker loc;
    private DBManager dbManager;
    private int list_id;
    private boolean hasTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Bundle b = getIntent().getExtras();
        list_id = b.getInt("list_id");
        hasTimestamp = b.getBoolean("hasTimestamp");
        addBtn = (Button) findViewById(R.id.postBtn);
        textDesc = (EditText) findViewById(R.id.textDesc);
        textTitle = (EditText) findViewById(R.id.textTitle);
        dbManager = new DBManager(this);
        dbManager.open();
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        imageBtn = (ImageButton) findViewById(R.id.imageBtn);
        //picking image from gallery
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flag = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(permissions)) {
                        requestPermissions(permissions, 1);
                    } else {
                        flag = 1;
                    }
                } else {
                    flag = 1;

                }
                if (flag == 1) {
                    final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Lists" + File.separator);
                    boolean isDirectoryCreated = root.exists();
                    if (!isDirectoryCreated) {
                        isDirectoryCreated = root.mkdirs();
                    }
                    final String fname = NewListActivity.getUniqueImageFilename();
                    final File sdImageMainDirectory = new File(root, fname);
                    uri = Uri.fromFile(sdImageMainDirectory);

                    final List<Intent> cameraIntents = new ArrayList<Intent>();
                    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    final PackageManager packageManager = getPackageManager();
                    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                    for (ResolveInfo res : listCam) {
                        final String packageName = res.activityInfo.packageName;
                        final Intent intent = new Intent(captureIntent);
                        intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                        intent.setPackage(packageName);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        cameraIntents.add(intent);
                    }

                    final Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    Intent chooser = Intent.createChooser(galleryIntent, "Complete action using...");
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
                    startActivityForResult(chooser, GALLERY_REQUEST_CODE);
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NewTaskActivity.this, "Saving task...", Toast.LENGTH_SHORT).show();
                final String TaskTitle = textTitle.getText().toString().trim();
                final String TaskDesc = textDesc.getText().toString().trim();
                double lat,lng;
                String timestamp = "";
                if (!TextUtils.isEmpty(TaskTitle)){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] byteArray = null;
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                        byteArray = stream.toByteArray();
                    }
                    lat = loc.getPosition().latitude;
                    lng = loc.getPosition().longitude;
                    if (hasTimestamp) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        timestamp = formatter.format(ts);
                    }

                    dbManager.insertTask(TaskTitle,TaskDesc,byteArray,lat,lng,list_id,timestamp);
                    setResult(4);
                    NewTaskActivity.super.onBackPressed();
                }
                else
                    Toast.makeText(NewTaskActivity.this,"Please Enter Title",Toast.LENGTH_SHORT).show();
            }
        });

        mContext = this;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},2);
            isLocationEnabled();
            return;

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100,
                10, locationListenerGPS);
        isLocationEnabled();
    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(latitude, longitude);
                loc = gmap.addMarker(new MarkerOptions().position(latLng).draggable(true).visible(true));
                moveToCurrentLocation(latLng);
            }
            else
                Toast.makeText(mContext, "Location Unavailable", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        gmap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        gmap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }

    protected void onResume(){
        super.onResume();
    }

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
        else{
            Toast.makeText(this, "Location is Enabled.. Hurray!!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                return;
            }
            gmap = googleMap;
            loc = gmap.addMarker(new MarkerOptions().position(new LatLng(19.02,72.86)).visible(false));
            gmap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public boolean hasPermissions( String... permissions) {
        if (permissions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
//            uri = data.getData();
//            imageBtn.setImageURI(uri);
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageBtn.setImageBitmap(photo);
            final boolean isCamera;
            if (data == null || data.getData() == null) {
                isCamera = true;
            }
            else {
                isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                uri = data.getData();
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmap = NewListActivity.getResizedBitmap(bitmap,450);
                imageBtn.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
