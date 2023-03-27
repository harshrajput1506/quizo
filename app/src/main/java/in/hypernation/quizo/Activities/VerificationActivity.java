package in.hypernation.quizo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.OtpTextWatcher;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.R;

public class VerificationActivity extends AppCompatActivity {

    private ImageView backBtn;
    private CardView verificationBtn;
    private TextView verificationBtnTxt, numberTxt, timer, resendBtn;
    private ProgressBar progressBar;
    private String phoneNumber;
    private EditText editOtp1, editOtp2, editOtp3, editOtp4, editOtp5, editOtp6;
    private View contextView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private CountDownTimer countDownTimer;
    private String verificationID;
    private boolean isResend = false;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private static final String TAG = "VerificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        backBtn = findViewById(R.id.verification_back_btn);
        verificationBtn = findViewById(R.id.verification_btn);
        verificationBtnTxt = findViewById(R.id.auth_verification_btn_txt);
        progressBar = findViewById(R.id.auth_progress_bar);
        numberTxt = findViewById(R.id.verification_mobile_no);
        resendBtn = findViewById(R.id.verification_resend_btn);
        timer = findViewById(R.id.verification_timer);
        editOtp2 = findViewById(R.id.editOtp2);
        editOtp3 = findViewById(R.id.editOtp3);
        editOtp4 = findViewById(R.id.editOtp4);
        editOtp5 = findViewById(R.id.editOtp5);
        editOtp6 = findViewById(R.id.editOtp6);
        editOtp1 = findViewById(R.id.editOtp1);
        contextView = findViewById(android.R.id.content);

        dismissProgressBar();

        auth = FirebaseAuth.getInstance();
        phoneNumber = getResources().getString(R.string.country_code)+getIntent().getStringExtra("number");
        String mobile_no = getResources().getString(R.string.country_code)+" "+getIntent().getStringExtra("number");
        numberTxt.setText(mobile_no);

        EditText[] editOtp = { editOtp1, editOtp2, editOtp3, editOtp4,editOtp5, editOtp6 };
        editOtp1.addTextChangedListener(new OtpTextWatcher(editOtp, editOtp1));
        editOtp2.addTextChangedListener(new OtpTextWatcher(editOtp, editOtp2));
        editOtp3.addTextChangedListener(new OtpTextWatcher(editOtp, editOtp3));
        editOtp4.addTextChangedListener(new OtpTextWatcher(editOtp, editOtp4));
        editOtp5.addTextChangedListener(new OtpTextWatcher(editOtp, editOtp5));
        editOtp6.addTextChangedListener(new OtpTextWatcher(editOtp, editOtp6));

        //Phone Auth Verification Callback
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: "+phoneAuthCredential.getSmsCode());
                visibleProgressBar();
                String code = phoneAuthCredential.getSmsCode();
                assert code != null;
                editOtp1.setText(code.substring(0,1));
                editOtp2.setText(code.substring(1,2));
                editOtp3.setText(code.substring(2,3));
                editOtp4.setText(code.substring(3,4));
                editOtp5.setText(code.substring(4,5));
                editOtp6.setText(code.substring(5));
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(contextView, "Error in Mobile no.", Snackbar.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    dismissProgressBar();
                    Toast.makeText(VerificationActivity.this, "Failed Too Many Requests", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerificationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
                VerificationActivity.super.onBackPressed();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Snackbar.make(contextView, "OTP Sent", Snackbar.LENGTH_SHORT).show();
                dismissProgressBar();
                resendingToken = forceResendingToken;
                verificationID = s;
                if(isResend){
                    callCountDownTimer();
                }
            }
        };

        startNumberVerification(phoneNumber);
        callCountDownTimer();


        verificationBtn.setOnClickListener(view -> {
            String code  = editOtp1.getText().toString()+editOtp2.getText().toString()+
                    editOtp3.getText().toString()+editOtp4.getText().toString()+editOtp5.getText().toString()
                    +editOtp6.getText().toString();
            if(code.trim().length()==6){
                if(verificationID!=null) {
                    verifyWithCode(verificationID, code.trim());
                } else {
                    Snackbar.make(contextView, "Invalid OTP", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(contextView, "Enter 6 Digits OTP", Snackbar.LENGTH_SHORT).show();
            }
        });

        resendBtn.setOnClickListener(view -> {
            resendBtn.setClickable(false);
            visibleProgressBar();
            isResend = true;
            resendNumberVerification(phoneNumber, resendingToken);
        });

        backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

    }

    private void visibleProgressBar(){
        verificationBtn.setClickable(false);
        verificationBtnTxt.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void dismissProgressBar(){
        verificationBtn.setClickable(true);
        verificationBtnTxt.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void startNumberVerification(String number){
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void resendNumberVerification(String number, PhoneAuthProvider.ForceResendingToken forceResendingToken){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .setForceResendingToken(forceResendingToken)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyWithCode(String verificationID, String code){
        visibleProgressBar();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            user = task.getResult().getUser();
                            if(user!=null){
                                user.getIdToken(true).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        String token = task1.getResult().getToken();
                                        String profileUrl = Constant.RES_URL+"avatar.png";
                                        phoneAuthRequest(user.getUid(), token, profileUrl, user.getPhoneNumber());
                                    }
                                });
                            }
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            dismissProgressBar();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Snackbar.make(contextView, "Invalid OTP", Snackbar.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            } else {
                                Snackbar.make(contextView, "Failed", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    private  void phoneAuthRequest(String uid, String token, String profilePicture, String number){
        String url = Constant.USER_URL +"auth/2901";
        Map<String, String> body = new HashMap<>();
        body.put("uid", uid);
        body.put("name", "Master");
        body.put("email", "");
        body.put("number", number);
        body.put("profilePicture", profilePicture);
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, new JSONObject(body), token, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "onSuccess: Data from Phone Signing Request - "+response);
                try {
                    if(response.getInt("success")==1){
                        JSONObject data = response.getJSONObject("data");
                        if(response.getString("message").equals("Register")){
                            SPManager.setStringValue("uid", uid);
                            SPManager.setStringValue("profilePicture", profilePicture);
                            SPManager.setStringValue("number", number);
                            SPManager.setStringValue("referCode", data.getString("referCode"));
                            SPManager.setStringValue("username", data.getString("username"));

                            Toast.makeText(getApplicationContext(), response.getString("message")+" Successful", Toast.LENGTH_SHORT).show();
                            dismissProgressBar();
                            verificationBtn.setClickable(false);
                            Intent i = new Intent(VerificationActivity.this, ProfileAuthActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        } else if (response.getString("message").equals("Login")){
                            Iterator<String> keys = data.keys();
                            while (keys.hasNext()){
                                String key = keys.next();
                                SPManager.setStringValue(key, data.getString(key));
                            }

                            Toast.makeText(getApplicationContext(), response.getString("message")+" Successful", Toast.LENGTH_SHORT).show();
                            dismissProgressBar();
                            verificationBtn.setClickable(false);
                            Intent i = new Intent(VerificationActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }



                    } else {
                        dismissProgressBar();
                        Snackbar.make(contextView, "Something Went Wrong", Snackbar.LENGTH_SHORT).show();
                        auth.signOut();

                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                dismissProgressBar();

                if(error instanceof NoConnectionError){
                    Snackbar.make(contextView, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(contextView, "Something Went Wrong", Snackbar.LENGTH_SHORT).show();
                }
                Log.d("TAG", "Volley Error "+ error);

                auth.signOut();

                // Handling Errors
            }
        });

        volleyCallRequest.callPostRequest();
    }

    private void callCountDownTimer(){
        isResend = false;
        resendBtn.setClickable(true);
        resendBtn.setVisibility(View.GONE);
        timer.setVisibility(View.VISIBLE);
        countDownTimer =  new CountDownTimer(59000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long lastDigits = (long) millisUntilFinished / 1000;
                String s = Long.toString(lastDigits);
                String text = null;
                if(s.length()==1){
                    text = "0:0" + lastDigits;
                } else {
                    text = "0:" + lastDigits;
                }
                timer.setText(text);
            }
            @Override
            public void onFinish() {
                timer.setVisibility(View.INVISIBLE);
                resendBtn.setVisibility(View.VISIBLE);
                resendBtn.setClickable(true);
            }
        };
        countDownTimer.start();
    }
}