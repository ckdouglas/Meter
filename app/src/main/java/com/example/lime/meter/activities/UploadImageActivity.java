package com.example.lime.meter.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.lime.meter.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadImageActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageView;
    ImageView uploadImage;
    private ProgressDialog progressDialog;
    StorageReference storageReference;
    File imgFile;
    private String imageURl;
    FirebaseFirestore firestoreInstance = null;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        this.imageView = (ImageView) findViewById(R.id.meterImage);
        this.uploadImage = (ImageView) findViewById(R.id.uploadImage);
        this.uploadImage.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting Readings");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        firestoreInstance = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        /**
         * set image to view
         * */
        getSetImageToView();
    }

    private void getSetImageToView(){
        Bundle bundle = getIntent().getExtras();
        String imagePath = bundle.getString("imagePath");
        Toast.makeText(this, "imagePath"+imagePath, Toast.LENGTH_SHORT).show();
        imgFile = new File(imagePath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.uploadImage:
                uploadImage();
                break;
        }
    }


    private void uploadImage() {
        Uri imageUri = Uri.fromFile(imgFile);
        if (imageUri!=null){
            progressDialog.show();
            final StorageReference fileRef= storageReference.child(String.valueOf(System.currentTimeMillis()));
            final UploadTask uploadTask = fileRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downUri = task.getResult();
                                imageURl = downUri.toString();
                                saveImageUrl();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void saveImageUrl() {

        try{
            Map<String,Object> hashMap = new HashMap<>();
            hashMap.put("imageUrl",imageURl);
            DocumentReference  documentReference = firestoreInstance.collection("customers")
                    .document(mUser.getUid())
                    .collection("meterReadings")
                    .document(Timestamp.now().toDate().toGMTString());
            documentReference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to save image Url " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception ex){
            Toast.makeText(this, "Error"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
