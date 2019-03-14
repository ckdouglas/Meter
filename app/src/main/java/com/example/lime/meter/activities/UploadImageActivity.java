package com.example.lime.meter.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.lime.meter.R;

public class UploadImageActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        this.imageView = (ImageView) findViewById(R.id.meterImage);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("BitmapImage");
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);

        }
    }
}
