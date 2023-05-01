package in.hypernation.quizo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView name, username;
    private CircleImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SPManager.init(getApplicationContext());

        name = findViewById(R.id.profile_name);
        username = findViewById(R.id.profile_username);
        avatar = findViewById(R.id.profile_avatar);

        String profileName = SPManager.getStringValue("name", "Master");
        String profileUsername = "@"+SPManager.getStringValue("username", "master");
        String profileAvatar = SPManager.getStringValue("profilePicture", Constant.AVATAR_URL);

        Glide.with(this).load(profileAvatar).placeholder(R.drawable.avatar2).into(avatar);
        name.setText(profileName);
        username.setText(profileUsername);
    }
}