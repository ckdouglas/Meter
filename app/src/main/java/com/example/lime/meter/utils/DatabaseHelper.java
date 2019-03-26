package com.example.lime.meter.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.lime.meter.activities.ScanActivity;
import com.example.lime.meter.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {
    FirebaseFirestore firestoreInstance = null;
    FirebaseUser mUser = null;
    Map<String, Object> hashMap;
    private final String TAG = DatabaseHelper.class.getSimpleName();
    static  User user = null;
    Context mContext ;

    public DatabaseHelper(Context mContext){
        firestoreInstance = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        hashMap = new HashMap<>();
        this.mContext = mContext;
    }


    public void saveData() {
        hashMap.put("userID",this.mUser.getUid());
        hashMap.put("userPhone",mUser.getPhoneNumber());
        hashMap.put("created_at",new Timestamp(new Date()));

        firestoreInstance.collection("customers").document(this.mUser.getUid()).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "user successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing user", e);
            }
        });

    }

    public User getUser(){
        final DocumentReference documentReference = firestoreInstance.collection("customers").document(mUser.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot,FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot!=null && documentSnapshot.exists()){
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    user = documentSnapshot.toObject(User.class);
                } else {
                    Toast.makeText(mContext, "Current Data is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
       return user;
    }




}