package in.hypernation.quizo.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.R;

public class QuizPlayActivity extends AppCompatActivity {

    private Dialog dialog;
    private CircleImageView player1image, player2image;
    private TextView player1name, player2name, questions, questionTxt, option1, option2, option3, option4, timer, player1score, player2score;
    private FirebaseFirestore db;
    private String roomID;
    private int currentQuestionNo = 1;
    private long totalQuestions = 5;
    private LinearLayout questionLayout, optionLayout;
    private JSONArray questionArray;
    private CardView questionImageLayout;
    private Space space1, space2;
    private ImageView questionImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_play);

        player1image = findViewById(R.id.player1image);
        player2image = findViewById(R.id.player2picture);
        player1name = findViewById(R.id.player1name);
        player2name = findViewById(R.id.player2name);
        questions = findViewById(R.id.questions);
        timer = findViewById(R.id.timer);
        questionLayout = findViewById(R.id.question_layout);
        optionLayout = findViewById(R.id.options_layout);
        space1 = findViewById(R.id.space1);
        space2 = findViewById(R.id.space2);
        questionImageLayout = findViewById(R.id.question_img_container);
        questionImg = findViewById(R.id.question_img);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        questionTxt = findViewById(R.id.question);
        player1score = findViewById(R.id.player1score);
        player2score = findViewById(R.id.player2score);

        db = FirebaseFirestore.getInstance();
        SPManager.init(getApplicationContext());

        setProfiles();

        String questionSet = getIntent().getStringExtra("questionSet");
        try {
            questionArray = new JSONArray(questionSet);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        roomID = getIntent().getStringExtra("roomID");
        Log.d("QuizPlay", "onCreate Quiz Play: "+roomID);
        final DocumentReference df = db.collection("gameRooms").document(roomID);
        df.addSnapshotListener((value, error) -> {
            if(error!=null){
                Log.w("Realtime Firestore Error", "Listen failed.", error);
            }
            assert value != null;
            if(value.exists()){
                Map<String, Object> data = value.getData();
                totalQuestions = (long) data.get("total_questions");
                questions.setText("Q. "+currentQuestionNo+"/"+totalQuestions);

            }

        });

        createWaitingDialog();

    }

    private void setProfiles(){
        String opponentName = getIntent().getStringExtra("opponentName");
        String opponentPicture = getIntent().getStringExtra("opponentPicture");


        String name = "You";
        String profilePicture = SPManager.getStringValue("profilePicture", "");
        Glide.with(this).load(profilePicture).placeholder(R.drawable.avatar2).into(player1image);
        Glide.with(this).load(opponentPicture).placeholder(R.drawable.avatar2).into(player2image);
        player1name.setText(name);
        player2name.setText(opponentName);
        player1score.setText("0");
        player2score.setText("0");
        Log.d("QuizPlay", "onCreate Quiz Play: "+opponentName+" "+opponentPicture);
    }

    private void gameStarts() throws JSONException {
        startGameTimer();
        nextQuestion();
    }

    private void nextQuestion() throws JSONException{
        int index = (int) currentQuestionNo-1;
        questions.setText("Q. "+currentQuestionNo+"/"+totalQuestions);
        JSONObject question = questionArray.getJSONObject(index);
        String questionLbl = question.getString("question");
        questionTxt.setText(questionLbl);
        if(!question.getBoolean("isImage")){
            space1.setVisibility(View.VISIBLE);
            space2.setVisibility(View.VISIBLE);
            questionImageLayout.setVisibility(View.GONE);
        } else {
            space1.setVisibility(View.GONE);
            space2.setVisibility(View.GONE);
            questionImageLayout.setVisibility(View.VISIBLE);
            String image = question.getString("image");
            if(image!="null"){
                Glide.with(this).load(image).placeholder(R.drawable.random).into(questionImg);
            }
        }
        questionLayout.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            try {
                setOptions(index);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            optionLayout.setVisibility(View.VISIBLE);
        }, 2000);

    }

    private void setOptions(int index) throws JSONException {
        JSONObject question = questionArray.getJSONObject(index);
        String option1Lbl = question.getString("option1");
        String option2Lbl = question.getString("option2");
        String option3Lbl = question.getString("option3");
        String option4Lbl = question.getString("option4");

        option1.setText("A. "+option1Lbl);
        option2.setText("B. "+option2Lbl);
        option3.setText("C. "+option3Lbl);
        option4.setText("D. "+option4Lbl);
    }

    private void startGameTimer(){
        int endTime = 13;
        CountDownTimer timer1 = new CountDownTimer(endTime*1000L, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) l/1000;
                timer.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                //updatePoints();
            }
        };

        timer1.start();

    }

    private void createWaitingDialog(){
        dialog = new Dialog(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.lyt_dialog_play_quiz, viewGroup, false);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView message = view.findViewById(R.id.message);
        ProgressBar progressBar = view.findViewById(R.id.circular_progress);
        int endTime = 5;
        progressBar.setMax(endTime);
        progressBar.setProgress(endTime);
        TextView timer = view.findViewById(R.id.timer);

        CountDownTimer countDownTimer = new CountDownTimer(endTime*1000L, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) l/1000;
                progressBar.setProgress(seconds);
                timer.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                dialog.cancel();
                try {
                    gameStarts();
                } catch (JSONException e) {
                }
            }
        };

        countDownTimer.start();

        dialog.show();
    }
}