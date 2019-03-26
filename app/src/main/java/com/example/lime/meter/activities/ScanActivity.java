package com.example.lime.meter.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lime.meter.R;
import com.example.lime.meter.models.User;
import com.example.lime.meter.utils.DatabaseHelper;
import com.example.lime.meter.utils.AuthHelper;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {
    private static final String TAG = ScanActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 11 ;

    private BarcodeReader barcodeReader;
    private ImageView mImageView;
    private AuthHelper authHelper = null;
    DatabaseHelper databaseHelper = null;
    User user;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        authHelper = new AuthHelper();
        if (authHelper.signedIn()) {
            setContentView(R.layout.activity_main);
            mImageView = (ImageView) findViewById(R.id.meterImage);
            barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);
            databaseHelper = new DatabaseHelper(this);
            user = databaseHelper.getUser();
        } else {
            Intent intent = new Intent(ScanActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
    }

    @Override
    public void onScanned(final Barcode barcode) {
        Log.e(TAG, "onScanned: " + barcode.displayValue);
        barcodeReader.playBeep();
        final String code = barcode.displayValue;
        if (user != null){
            if ( user.getMeterNumber().equals(code)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Barcode: " + user.getMeterNumber(),
                                Toast.LENGTH_SHORT).show();
                        dispatchTakePictureIntent();
                    }
                });
            }
        }else {
            Toast.makeText(ScanActivity.this, "User is null", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
    }

    @Override
    public void onScanError(String errorMessage) {
    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
        finish();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
             File photoFile = null;
             try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                try{
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                     startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error"+ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, UploadImageActivity.class);
            intent.putExtra("imagePath",currentPhotoPath);
            startActivity(intent);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */".jpg",/* suffix */storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


}