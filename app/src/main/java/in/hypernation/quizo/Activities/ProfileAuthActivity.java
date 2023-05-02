package in.hypernation.quizo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Adapters.AuthAvatarsAdapter;
import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.AuthProfileListener;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.R;

public class ProfileAuthActivity extends AppCompatActivity implements AuthProfileListener {

    private RecyclerView avatarRV;
    private CircleImageView selectedAvatar;
    private EditText nameEditTxt, referCodeEditTxt;
    private ImageView backBtn;
    private TextView user_name, completeBtnTxt;
    private ProgressBar progressBar;
    private CardView completeBtn;
    private ArrayList<String> avatars;
    private String selectedAvatarURL;
    private FirebaseAuth auth;
    private static final String TAG = "ProfileAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_auth);

        avatarRV = findViewById(R.id.profile_auth_avatars_RV);
        selectedAvatar = findViewById(R.id.profile_auth_selected_avatar);
        nameEditTxt = findViewById(R.id.auth_profile_name_edit_txt);
        user_name = findViewById(R.id.profile_auth_user_name);
        completeBtn = findViewById(R.id.auth_complete_btn);
        referCodeEditTxt = findViewById(R.id.auth_profile_refer_edit_txt);
        completeBtnTxt = findViewById(R.id.profile_auth_complete_txt);
        progressBar = findViewById(R.id.profile_auth_progress_bar);
        backBtn = findViewById(R.id.profile_auth_back);

        SPManager.init(getApplicationContext());
        auth = FirebaseAuth.getInstance();

        dismissProgressBar();

        avatars = new ArrayList<>();
        avatars.add("https://api.quizo.fun/res/uploads/avatar.png");
        avatars.add("https://api.quizo.fun/res/uploads/avatar1.png");
        avatars.add("https://api.quizo.fun/res/uploads/avatar9.png");
        avatars.add("https://api.quizo.fun/res/uploads/avatar11.png");
        avatars.add("https://api.quizo.fun/res/uploads/avatar2.png");
        avatars.add("https://api.quizo.fun/res/uploads/avatar3.png");
        avatars.add("https://api.quizo.fun/res/uploads/avatar5.png");

        selectedAvatarURL = avatars.get(0);


        nameEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user_name.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        avatarRV.setAdapter(new AuthAvatarsAdapter(this, getResources().getDisplayMetrics().density));
        avatarRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        completeBtn.setOnClickListener(view -> {
            if(nameEditTxt.getText()!=null){
                FirebaseUser user = auth.getCurrentUser();
                if(user!=null){
                    user.getIdToken(true).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            visibleProgressBar();
                            String token = task.getResult().getToken();
                            String referCode = referCodeEditTxt.getText().toString().trim();
                            profileAuthRequest(token, referCode, nameEditTxt.getText().toString(), selectedAvatarURL);
                        }
                    });
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Enter Your Name", Snackbar.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(view -> {
            Intent i = new Intent(ProfileAuthActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        });

    }

    @Override
    public void onAuthAvatarClick(int avatarResource, int position) {
        selectedAvatar.setImageResource(avatarResource);
        selectedAvatarURL = avatars.get(position);
    }

    private void dismissProgressBar() {
        completeBtn.setClickable(true);
        progressBar.setVisibility(View.INVISIBLE);
        completeBtnTxt.setVisibility(View.VISIBLE);
    }

    private void visibleProgressBar(){
        completeBtn.setClickable(false);
        completeBtnTxt.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void profileAuthRequest(String token, String referCode, String name, String profilePicture){
        String url = Constant.USER_URL+"auth/2902";
        String uid = SPManager.getStringValue("uid", "");
        Map<String, String> body = new HashMap<>();
        body.put("uid", uid);
        body.put("friendCode", referCode);
        body.put("name", name);
        body.put("profilePicture", profilePicture);

        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, new JSONObject(body), token, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "onSuccess: "+response);
                try {
                    String message = response.getString("message");
                    if(response.getInt("success")==1){
                        SPManager.setStringValue("name", name);
                        SPManager.setStringValue("profilePicture", profilePicture);
                        if(message.equals("Completed With Referral")){
                            SPManager.setStringValue("friendCode", referCode);
                        }
                        dismissProgressBar();
                        Toast.makeText(getApplicationContext(), "Profile Completed", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    dismissProgressBar();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                if(error instanceof NoConnectionError){
                    Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Something Went Wrong", Snackbar.LENGTH_SHORT).show();
                }
                Log.d("TAG", "Volley Error "+ error.networkResponse+ " "+error.getMessage() + " "+error.getLocalizedMessage());
                dismissProgressBar();
            }
        });
        Intent i = new Intent(ProfileAuthActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        volleyCallRequest.callPutRequest();
    }

}