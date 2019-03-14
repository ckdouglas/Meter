package com.example.lime.meter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lime.meter.R;
import com.example.lime.meter.utils.DatabaseHelper;
import com.example.lime.meter.utils.LoginHelper;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {
    private static final String TAG = ScanActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 11 ;

    private BarcodeReader barcodeReader;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check if user is logged in
        if (LoginHelper.userIsLoggedIn()) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mImageView = (ImageView) findViewById(R.id.meterImage);
            // getting barcode instance
            barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);


            /***
             * Providing beep sound. The sound file has to be placed in
             * `assets` folder
             */
            //barcodeReader.setBeepSoundFile("shutter.mp3");

            /**
             * Pausing / resuming barcode reader. This will be useful when you want to
             * do some foreground user interaction while leaving the barcode
             * reader in background
             * */

            //barcodeReader.pauseScanning();
            // barcodeReader.resumeScanning();
        } else {
            Intent intent = new Intent(ScanActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onScanned(final Barcode barcode) {

        Log.e(TAG, "onScanned: " + barcode.displayValue);
        barcodeReader.playBeep();
        final String code = barcode.displayValue;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //toast the barcode
                Toast.makeText(getApplicationContext(), "Barcode: " + code, Toast.LENGTH_SHORT).show();

                if (DatabaseHelper.barCodeIsInDb(code) && DatabaseHelper.userIsAvailable(code)) {
                    if (DatabaseHelper.codeIsForCurrentUser(code)) {
                       dispatchTakePictureIntent(code);
                    }
                }
            }
        });
    }


    //redirects to the camera intent
    private void dispatchTakePictureIntent(String code) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Toast.makeText(getApplicationContext(), "inCamera "+code,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            if (imageBitmap!=null){
                Intent intent = new Intent(ScanActivity.this, UploadImageActivity.class);
                intent.putExtra("BitmapImage", imageBitmap);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        Log.e(TAG, "onScannedMultiple: " + barcodes.size());
        String codes = "";
        for (Barcode barcode : barcodes) {
            codes += barcode.displayValue + ", ";
        }

        final String finalCodes = codes;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Barcodes: " + finalCodes, Toast.LENGTH_SHORT).show();
            }
        });


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

}