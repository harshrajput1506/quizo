package in.hypernation.quizo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Size;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        konfettiView = findViewById(R.id.konfetti_view);
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