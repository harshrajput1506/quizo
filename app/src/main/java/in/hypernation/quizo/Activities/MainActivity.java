package in.hypernation.quizo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.R;

public class MainActivity extends AppCompatActivity {

    private Intent intent;
    private Boolean isOnBoard;
    private View contextView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextView = findViewById(android.R.id.content);

        //SP INIT
        SPManager.init(getApplicationContext());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user !=null){
            String uid = SPManager.getStringValue("uid", null);
            if(uid!=null){
                user.getIdToken(false).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String token = task.getResult().getToken();
                        getUserData(token, uid);
                    } else {
                        Snackbar.make(contextView, "Something Went Wrong", Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                auth.signOut();
                timer();
            }
        } else {
            timer();
        }

    }

    private void timer(){
        new Handler().postDelayed(() -> {
            isOnBoard = SPManager.getBooleanValue("isOnBoard", true);
            Log.d(TAG, "run: "+isOnBoard);
            if(isOnBoard){
                intent = new Intent(MainActivity.this, OnBoardActivity.class);
            } else {
                intent = new Intent(MainActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            finish();

        }, 2000);
    }

    private void getUserData(String token, String uid){
        String url = Constant.USER_URL+"auth/2900";
        Map<String, String> body = new HashMap<>();
        body.put("uid", uid.trim());

        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, new JSONObject(body), token, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(response.getString("success").equals("1")){
                        JSONObject data = response.getJSONObject("data");
                        Iterator<String> keys = data.keys();
                        while (keys.hasNext()){
                            String key = keys.next();
                            SPManager.setStringValue(key, data.getString(key));
                        }

                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Snackbar.make(contextView, "Something Went Wrong", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error);
                if(error instanceof NoConnectionError){
                    Snackbar.make(contextView, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(contextView, "Something Went Wrong", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        volleyCallRequest.callPostRequest();
    }
}