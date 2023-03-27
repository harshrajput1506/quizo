package in.hypernation.quizo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.R;

public class AuthActivity extends AppCompatActivity {

    private CardView continueBtn, googleBtn;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private ProgressDialog progressDialog;
    private View contextView;
    private EditText numberEditTxt;
    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            Intent i = new Intent(AuthActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //ADD HOOKS
        continueBtn = findViewById(R.id.auth_continue_btn);
        googleBtn = findViewById(R.id.auth_google);
        numberEditTxt = findViewById(R.id.auth_edit_number);
        contextView = findViewById(android.R.id.content);

        //Init firebase auth
        auth = FirebaseAuth.getInstance();
        createGoogleRequest();

        //Init SP
        SPManager.init(getApplicationContext());


        //CLick Listener
        continueBtn.setOnClickListener(v -> {
            if(numberEditTxt.getText() !=null){
                String number = numberEditTxt.getText().toString().trim();
                if(number.length()==10){
                    Intent i = new Intent(AuthActivity.this, VerificationActivity.class);
                    i.putExtra("number", number);
                    startActivity(i);
                } else {
                    Snackbar.make(contextView, "Enter Valid Mobile No.", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(contextView, "Enter 10 Digit Mobile No.", Snackbar.LENGTH_SHORT).show();
            }
        });

        googleBtn.setOnClickListener(view -> {
            initProgressDialog();
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    // Create a google sign in request
    private void createGoogleRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // After Request Get Data from Firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        if(user!=null){
                            user.getIdToken(true).addOnCompleteListener(task1 -> {
                               if(task1.isSuccessful()){
                                   String token = task1.getResult().getToken();
                                   googleAuthRequest(user.getUid(), token, user.getEmail(), user.getDisplayName(), user.getPhotoUrl().toString());
                               } else {
                                   progressDialog.dismiss();
                               }
                            });
                        } else {
                            progressDialog.dismiss();
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(contextView, "Sigin Failed", Snackbar.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private  void googleAuthRequest(String uid, String token, String email, String name, String profilePicture){
        String url = Constant.USER_URL +"auth/2901";
        Map<String, String> body = new HashMap<>();
        body.put("uid", uid);
        body.put("name", name);
        body.put("email", email);
        body.put("profilePicture", profilePicture);
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, new JSONObject(body), token, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "onSuccess: Data from Google Signing Request - "+response);
                try {
                    if(response.getInt("success")==1){
                        JSONObject data = response.getJSONObject("data");
                        if(response.getString("message").equals("Register")){
                            SPManager.setStringValue("uid", uid);
                            SPManager.setStringValue("name", name);
                            SPManager.setStringValue("email", email);
                            SPManager.setStringValue("profilePicture", profilePicture);
                            SPManager.setStringValue("referCode", data.getString("referCode"));
                            SPManager.setStringValue("username", data.getString("username"));

                            Toast.makeText(AuthActivity.this, response.getString("message")+" Successful", Toast.LENGTH_SHORT).show();


                        } else if (response.getString("message").equals("Login")){
                            Iterator<String> keys = data.keys();
                            while (keys.hasNext()){
                                String key = keys.next();
                                SPManager.setStringValue(key, data.getString(key));
                            }

                            Toast.makeText(AuthActivity.this, response.getString("message")+" Successful", Toast.LENGTH_SHORT).show();

                        }

                        progressDialog.dismiss();
                        Intent i = new Intent(AuthActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();


                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(contextView, "Something Went Wrong", Snackbar.LENGTH_SHORT).show();
                        auth.signOut();

                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();

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

}