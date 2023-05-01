package in.hypernation.quizo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Size;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.R;
import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class QuizResultActivity extends AppCompatActivity {

    private KonfettiView konfettiView;
    private TextView resultStatus, playerScore, opponentName, opponentStatus, opponentScore, resultTitle;
    private CircleImageView playerAvatar, opponentAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        konfettiView = findViewById(R.id.konfetti_view);
        resultStatus = findViewById(R.id.result_status);
        playerAvatar = findViewById(R.id.player_avatar);
        opponentAvatar = findViewById(R.id.opponent_avatar);
        playerScore = findViewById(R.id.player_score);
        opponentScore = findViewById(R.id.opponent_score);
        opponentName = findViewById(R.id.opponent_name);
        opponentStatus = findViewById(R.id.opponent_status);
        resultTitle = findViewById(R.id.result_title);

        SPManager.init(getApplicationContext());

        Bundle data = getIntent().getBundleExtra("data");
        boolean winner = data.getBoolean("winner");
        int prizePool = data.getInt("prizePool");
        double playerPoints = data.getDouble("playerPoints");

        String avatar = SPManager.getStringValue("profilePicture", Constant.AVATAR_URL);
        String opponentPicture = data.getString("opponentPicture");
        String opponent = data.getString("opponentName");
        opponentName.setText(opponent);
        Glide.with(this).load(avatar).placeholder(R.drawable.avatar2).into(playerAvatar);
        Glide.with(this).load(opponentPicture).placeholder(R.drawable.avatar2).into(opponentAvatar);
        String score = "Your Score - "+String.format("%.2f", playerPoints);
        String opponentPoints = String.format("%.2f", data.getDouble("opponentPoints"));
        playerScore.setText(score);
        opponentScore.setText(opponentPoints);

        String status, opponentStatusTxt;

        if(winner){
            resultTitle.setText("#Respect");
            status = "You Won ₹"+prizePool;
            opponentStatusTxt = "LOST";
            opponentStatus.setText(opponentStatusTxt);
            opponentStatus.setTextColor(getResources().getColor(R.color.red_70));
            resultStatus.setTextColor(getResources().getColor(R.color.green_70));
            resultStatus.setText(status);
            startConfetti();
        } else {
            resultTitle.setText("#TryAgain");
            status = "YOU LOST";
            opponentStatusTxt = "WON";
            opponentStatus.setText(opponentStatusTxt);
            opponentStatus.setTextColor(getResources().getColor(R.color.green_70));
            resultStatus.setTextColor(getResources().getColor(R.color.red_70));
            resultStatus.setText(status);

        }


    }

    private void startConfetti(){
        EmitterConfig emitterConfig = new Emitter(3L, TimeUnit.SECONDS).perSecond(30);
        Party rightParty = new PartyFactory(emitterConfig)
                .angle(Angle.LEFT + 45)
                .spread(Spread.WIDE)
                .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 60f)
                .position(new Position.Relative(1.0, 0.6))
                .fadeOutEnabled(true)
                .timeToLive(3L)
                .build();

        Party leftParty = new PartyFactory(emitterConfig)
                .angle(Angle.RIGHT - 45)
                .spread(Spread.WIDE)
                .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 60f)
                .position(new Position.Relative(0.0, 0.6))
                .fadeOutEnabled(true)
                .timeToLive(4L)
                .build();

        Party[] partyList = new Party[2];
        partyList[0]=leftParty;
        partyList[1]=rightParty;

        konfettiView.start(partyList);
    }
}