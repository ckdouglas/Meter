package com.example.lime.meter.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.lime.meter.activities.CodeVerificationActivity;
import com.example.lime.meter.activities.LoginActivity;
import com.example.lime.meter.activities.ScanActivity;
import com.example.lime.meter.utils.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthHelper {

    private final String TAG = AuthHelper.class.getName();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Context mContext = null;
    Context mActivity = null;
    FirebaseUser mUser = null;
    static String phoneNumber;
    static String mRawCode;
    ProgressDialog progressDialog;
    DatabaseHelper databaseHelper;



    public AuthHelper(Context mContext) {
        Log.d(TAG,"AuthHelper:");
        progressDialog = new ProgressDialog(mContext);
        this.mContext = mContext;

    }

    public AuthHelper(){
        Log.d(TAG,"AuthHelper:");
        this.mUser = mAuth.getCurrentUser();
    }

    public AuthHelper(Activity mActivity, String phoneNumber, Context mContext){
        Log.d(TAG, "Auth Helper");
        this.mActivity = mActivity;
        this.phoneNumber = phoneNumber;
        this.mContext = mContext;
        progressDialog = new ProgressDialog(mContext);
    }


    public boolean signedIn() {
        if (this.mUser !=null)
            return true;
        return false;
    }


    public void startVerificationProcess(){
        Log.d(TAG, "startVerificationProcess");
        progressDialog.setMessage("Verifying "+ phoneNumber);
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60,
                TimeUnit.SECONDS,
                (Activity) mActivity,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneCredentials(phoneAuthCredential);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        mContext.startActivity(
                                new Intent(mContext, LoginActivity.class).
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        );
                        Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(String mVerificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(mVerificationId, forceResendingToken);
                        progressDialog.dismiss();
                        mRawCode = mVerificationId;
                        mActivity.startActivity(
                                new Intent(mContext, CodeVerificationActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        );
                    }
                });
    }


    public void verifyCodeSent(String code){
        Log.d(TAG, "verifyCodeSent");
        progressDialog.setMessage("Verifying Code");
        progressDialog.show();
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mRawCode,code);
        signInWithPhoneCredentials(phoneAuthCredential);

    }


    private void signInWithPhoneCredentials(PhoneAuthCredential phoneAuthCredential) {
        Log.d(TAG,"signInWithPhoneVerification");
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mUser = mAuth.getCurrentUser();
                    databaseHelper = new DatabaseHelper(mContext);
                    databaseHelper.saveData();
                    progressDialog.dismiss();
                    mContext.startActivity(new Intent(mContext,
                                ScanActivity.class).
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK));

                }
            }
        });
    }

}
