package com.example.ekene.blogzone;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewListActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private Bitmap bitmap;
    private EditText textTitle, textDesc;
    private ImageButton imageBtn;
    private Button addBtn;
    private Switch hasLocation, hasTimestamp;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        addBtn = (Button) findViewById(R.id.addBtn);
        textDesc = (EditText) findViewById(R.id.textDesc);
        textTitle = (EditText) findViewById(R.id.textTitle);
        hasLocation = (Switch) findViewById(R.id.hasLocation);
        hasTimestamp = (Switch) findViewById(R.id.hasTimestamp);
        dbManager = new DBManager(this);
        dbManager.open();
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
                    final String fname = getUniqueImageFilename();
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
                Toast.makeText(NewListActivity.this, "Saving list...", Toast.LENGTH_SHORT).show();
                final String ListTitle = textTitle.getText().toString().trim();
                String ListDesc = textDesc.getText().toString().trim();
                if (!TextUtils.isEmpty(ListTitle)){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] byteArray = null;
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                        byteArray = stream.toByteArray();
                    }
                    dbManager.insertList(ListTitle,ListDesc,byteArray,hasLocation.isChecked(),hasTimestamp.isChecked());
                    setResult(2);
                    NewListActivity.super.onBackPressed();
                }
                else
                    Toast.makeText(NewListActivity.this,"Please Enter Title",Toast.LENGTH_SHORT).show();
            }
        });
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


    public static String getUniqueImageFilename() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "img_"+timeStamp+".jpg";
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
                bitmap = getResizedBitmap(bitmap,450);
                imageBtn.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
