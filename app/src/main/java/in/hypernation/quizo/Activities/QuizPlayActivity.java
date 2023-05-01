package in.hypernation.quizo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.R;

public class QuizPlayActivity extends AppCompatActivity {

    private CircleImageView player1image, player2image;
    private CircularProgressIndicator waitingProgress;
    private LinearProgressIndicator questionProgress;
    private TextView player1name, player2name, questions, questionTxt, option1, option2, option3, option4, timer, player1score, player2score, waitingTimer, message, remarks;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String roomID;
    private int currentQuestionNo = 1;
    private int opponentQuestion = 1;
    private long totalQuestions = 5;
    private int prizePool;
    private LinearLayout questionLayout, optionLayout, waitingLayout;
    private JSONArray questionArray;
    private CardView questionImageLayout, option1layout, option2layout, option3layout, option4layout;
    private Space space1, space2;
    private ImageView questionImg;
    private String ans1, ans2, ans3, ans4, correctAns;
    private double livePoint, totalScores, opponentPoints, playerPoints;
    private CountDownTimer gameTimer;
    private boolean isGameEnd = false;

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
        option1layout = findViewById(R.id.option1layout);
        option2layout = findViewById(R.id.option2layout);
        option3layout = findViewById(R.id.option3layout);
        option4layout = findViewById(R.id.option4layout);
        waitingLayout = findViewById(R.id.waiting_layout);
        waitingTimer = findViewById(R.id.waiting_timer);
        message = findViewById(R.id.message);
        waitingProgress = findViewById(R.id.circular_progress);
        questionProgress = findViewById(R.id.question_progress);
        remarks = findViewById(R.id.remarks);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
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

        startWaiting("Game Starts in...", false, false);

        //Live Snapshot Listener
        final DocumentReference df = db.collection("gameRooms").document(roomID);
        df.addSnapshotListener(QuizPlayActivity.this,(value, error) -> {
            if(error!=null){
                Log.w("Realtime Firestore Error", "Listen failed.", error);
            }
            assert value != null;
            if(value.exists()){
                Map<String, Object> data = value.getData();
                totalQuestions = (long) data.get("total_questions");
                questionProgress.setMax((int) totalQuestions);

                long pool = (long) data.get("prizePool");
                prizePool = (int) pool;

                String playerTag = getIntent().getStringExtra("playerTag");
                String opponentTag = Objects.equals(playerTag, "player1") ? "player2":"player1";

                double liveOpponentPoints = value.getDouble(opponentTag+"points");
                double livePlayerPoints = value.getDouble(playerTag+"points");
                long liveOpponentQuestion = (long) data.get(opponentTag+"question");
                long livePlayerQuestion = (long) data.get(playerTag+"question");
                Log.d("TAG", "onCreate: Realtime Data"+liveOpponentPoints+" lop "+opponentPoints);

                if(liveOpponentQuestion>opponentQuestion && liveOpponentPoints>=opponentPoints){
                    updateOpponentPoints(liveOpponentPoints);
                    opponentQuestion = (int) liveOpponentQuestion;
                }

                if(livePlayerQuestion==totalQuestions && liveOpponentQuestion==totalQuestions && !isGameEnd){
                    isGameEnd=true;
                    if(livePlayerPoints>liveOpponentPoints){
                        String uid = SPManager.getStringValue("uid", "");
                        gameEnd(true, playerTag, uid, false, null, livePlayerPoints, liveOpponentPoints);
                    } else if (livePlayerPoints<liveOpponentPoints) {
                        String uid = value.getString(opponentTag+"id");
                        gameEnd(false, opponentTag, uid, false, null, livePlayerPoints, liveOpponentPoints);

                    } else {
                        String[] multipleWinners = {playerTag, opponentTag};
                        String uid = value.getString(opponentTag+"id");
                        gameEnd(true, "both", uid, true, multipleWinners, livePlayerPoints, liveOpponentPoints);
                    }

                }


                Log.d("QuizPlayActivity", "onCreate: Firestore Value"+value.getDouble(playerTag+"points"));
                Log.d("QuizPlayActivity", "onCreate: Firestore Data"+value.getData());

            }

        });

        changeQuestionNo();

        //Listeners

        option1layout.setOnClickListener(v-> {
            disableClicks();
            if(Objects.equals(correctAns, ans1)){
                rightAns(option1layout, option1);
            } else {
                wrongAns(option1layout, option1);
            }
        });

        option2layout.setOnClickListener(v->{
            disableClicks();
            if(Objects.equals(correctAns, ans2)){
                rightAns(option2layout, option2);
            } else {
                wrongAns(option2layout, option2);
            }

        });

        option3layout.setOnClickListener(v-> {
            disableClicks();
            if(Objects.equals(correctAns, ans3)){
                rightAns(option3layout, option3);
            } else {
                wrongAns(option3layout, option3);
            }

        });

        option4layout.setOnClickListener(v->{
            disableClicks();
            if(Objects.equals(correctAns, ans4)){
                rightAns(option4layout, option4);
            } else {
                wrongAns(option4layout, option4);
            }

        });

    }

    private void rightAns(CardView card, TextView text){
        gameTimer.cancel();
        card.setCardBackgroundColor(getResources().getColor(R.color.green_70));
        text.setTextColor(getResources().getColor(R.color.white));
        text.setBackgroundColor(Color.TRANSPARENT);
        updatePoints(livePoint);
        playerPoints=livePoint;
    }

    private void wrongAns(CardView card, TextView text){
        gameTimer.cancel();
        card.setCardBackgroundColor(getResources().getColor(R.color.red_70));
        text.setTextColor(getResources().getColor(R.color.white));
        text.setBackgroundColor(Color.TRANSPARENT);
        updatePoints(0);
        playerPoints=0;
    }

    private void changeQuestionNo() {
        questions.setText("Q. "+currentQuestionNo+"/"+totalQuestions);
        questionProgress.setProgress(currentQuestionNo);
    }

    private void updatePoints(double point) {
        totalScores = totalScores+point;
        String playerTag = getIntent().getStringExtra("playerTag");
        Map<String, Object> updates = new HashMap<>();
        updates.put(playerTag+"points", totalScores);
        updates.put(playerTag+"question", currentQuestionNo);
        db.collection("gameRooms").document(roomID).update(updates).addOnSuccessListener(unused -> {
            updatePlayerPoints(point);
            ++currentQuestionNo;
            checkQuestionNo();

        }).addOnFailureListener(e -> Log.e("TAG", "onFailure: ", e));
    }

    private void checkQuestionNo(){
        new Handler().postDelayed( () -> {
            if(totalQuestions>=currentQuestionNo){
                //next question

                startWaiting("Next Question in...", true, false);

            }else {
                //waiting result
                waitingResult();
            }
        }, 1500);
    }

    private void waitingResult() {
        startWaiting("Waiting for Result...", false, true);
        //TODO: Waiting logic for 30 seconds if opponent got exit or disconnect or opponent status is idle then user wins the game
    }

    private void gameEnd(boolean winner, String winnerStatus, String winnerId, boolean isMultipleWinners, String[] multipleWinners, double playerPoints, double opponentPoints){
        if(winner){
            if(!isMultipleWinners){
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Completed");
                updates.put("winnerStatus", winnerStatus);
                updates.put("winnerId", winnerId);
                db.collection("gameRooms").document(roomID).update(updates).addOnSuccessListener(unused -> {
                    //TODO: You are the winner - Open new screen result activity with some intent data -> winner(true), picture, pool, scores, names
                    if(prizePool!=0){
                        user.getIdToken(true).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                String token = task.getResult().getToken();
                                try {
                                    patchUpdateBalance(token);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        });
                    }
                }).addOnFailureListener(e -> Log.e("TAG", "gameEnd: ", e));
            } else {
                //TODO: Multiple winners
                Log.d("TAG", "gameEnd: Multiple Winners"+multipleWinners.toString());
            }
        } else {
            //patchUpdateLevel();
            //TODO: You are not the winner - Open new screen result activity with some intent data -> winner(false), picture, pool, scores, names
            Log.d("TAG", "gameEnd: Patch Update Level");
        }

        Intent i = new Intent(QuizPlayActivity.this, QuizResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("prizePool", prizePool);
        String opponentName = getIntent().getStringExtra("opponentName");
        String opponentPicture = getIntent().getStringExtra("opponentPicture");
        bundle.putString("opponentName", opponentName);
        bundle.putString("opponentPicture", opponentPicture);
        bundle.putDouble("playerPoints", playerPoints);
        bundle.putDouble("opponentPoints", opponentPoints);
        bundle.putBoolean("winner", winner);
        i.putExtra("data", bundle);
        startActivity(i);
        QuizPlayActivity.this.finish();

    }

    private void patchUpdateBalance(String token) throws JSONException{
        String query = Constant.USER_URL+"wallet/game/1101";
        String uid = SPManager.getStringValue("uid", "");
        JSONObject body = new JSONObject();
        body.put("uid", uid);
        body.put("bonusAmount", 0);
        body.put("depositAmount", 0);
        body.put("gameID", roomID);
        body.put("gameType", "room");
        body.put("amountType", "real");
        body.put("winningAmount", prizePool);

        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), query, body, token, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(response.getInt("success")==1) Log.d("TAG", "onSuccess: Completed");
                } catch(JSONException e){
                    Log.e("TAG", "onSuccess: ", e);
                }

            }

            @Override
            public void onError(VolleyError error) {
                Log.e("TAG", "onError: Volley Patch Update Balance ", error);
            }
        });
        volleyCallRequest.callPatchRequest();
    }

    private void resetOptions() {
        option1layout.setCardBackgroundColor(getResources().getColor(R.color.white));
        option2layout.setCardBackgroundColor(getResources().getColor(R.color.white));
        option3layout.setCardBackgroundColor(getResources().getColor(R.color.white));
        option4layout.setCardBackgroundColor(getResources().getColor(R.color.white));
        option1.setTextColor(getResources().getColor(R.color.grey));
        option2.setTextColor(getResources().getColor(R.color.grey));
        option3.setTextColor(getResources().getColor(R.color.grey));
        option4.setTextColor(getResources().getColor(R.color.grey));

    }

    private void updateOpponentPoints(double points){
        if(points==opponentPoints){
            player2score.setText("0");
            opponentPoints = points;
            player2score.setTextColor(getResources().getColor(R.color.red_70));
        } else {
            double updatePoints = points-opponentPoints;
            opponentPoints = points;
            player2score.setText("+"+String.format("%.2f",updatePoints));
            player2score.setTextColor(getResources().getColor(R.color.green_70));
        }

        new Handler().postDelayed( () -> {
            if(points==0){
                player2score.setText("0");
                player2score.setTextColor(getResources().getColor(R.color.white));
            } else {
                player2score.setText(String.format("%.2f", points));
                player2score.setTextColor(getResources().getColor(R.color.white));
            }
        }, 2000);
    }

    private void updatePlayerPoints(double points){
        if(points==0){
            player1score.setText("0");
            player1score.setTextColor(getResources().getColor(R.color.red_70));
        } else {
            player1score.setText("+"+String.format("%.2f", points));
            player1score.setTextColor(getResources().getColor(R.color.green_70));
        }

        new Handler().postDelayed( () -> {
            if(totalScores==0){
                player1score.setText("0");
                player1score.setTextColor(getResources().getColor(R.color.white));
            } else {
                player1score.setText(String.format("%.2f", totalScores));
                player1score.setTextColor(getResources().getColor(R.color.white));
            }
        }, 2000);
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
        totalScores = 0;
        Log.d("QuizPlay", "onCreate Quiz Play: "+opponentName+" "+opponentPicture);
    }

    private void gameStarts() throws JSONException {
        startGameTimer();
        nextQuestion();
    }

    private void nextQuestion() throws JSONException{
        int index = (int) currentQuestionNo-1;
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
                Glide.with(QuizPlayActivity.this).load(image).thumbnail(0.05f).into(questionImg);
            }
        }
        questionLayout.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            try {
                setOptions(index);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, 2000);

    }

    private void setOptions(int index) throws JSONException {
        JSONObject question = questionArray.getJSONObject(index);
        String option1Lbl = question.getString("option1");
        String option2Lbl = question.getString("option2");
        String option3Lbl = question.getString("option3");
        String option4Lbl = question.getString("option4");
        ans1 = option1Lbl;
        ans2 = option2Lbl;
        ans3 = option3Lbl;
        ans4 = option4Lbl;

        option1.setText("A. "+option1Lbl);
        option2.setText("B. "+option2Lbl);
        option3.setText("C. "+option3Lbl);
        option4.setText("D. "+option4Lbl);
        correctAns = question.getString("correctOption");

        resetOptions();
        optionLayout.setVisibility(View.VISIBLE);
        enableClicks();
        pointTimer();
    }

    private void disableClicks(){
        option1layout.setClickable(false);
        option2layout.setClickable(false);
        option3layout.setClickable(false);
        option4layout.setClickable(false);
    }

    private void enableClicks(){
        option1layout.setClickable(true);
        option2layout.setClickable(true);
        option3layout.setClickable(true);
        option4layout.setClickable(true);
    }

    private void startGameTimer(){
        int endTime = 12;
        try {
            if(gameTimer!=null){
                gameTimer.cancel();
            }
        }catch (Exception e){
            Log.e("TAG", "startGameTimer: ",e );
        }

        gameTimer = new CountDownTimer(endTime*1000L, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) l/1000;
                timer.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                //updatePoints();
                livePoint = 0;
                disableClicks();
                updatePoints(livePoint);
            }
        };

        gameTimer.start();

    }

    private void pointTimer() {
        int endTime = 10;
        CountDownTimer countDownTimer = new CountDownTimer(endTime*1000, 10) {
            @Override
            public void onTick(long l) {
                livePoint  = (double) l/1000;
            }

            @Override
            public void onFinish() {
                livePoint = 0;
            }
        };
        countDownTimer.start();
    }

    private void startWaiting(String msg, boolean remarkStatus, boolean isEnd){
        questions.setText("Q. "+currentQuestionNo+"/"+totalQuestions);
        questionProgress.setProgress(currentQuestionNo);
        timer.setText("12");
        questionLayout.setVisibility(View.GONE);
        optionLayout.setVisibility(View.GONE);
        waitingLayout.setVisibility(View.VISIBLE);

        message.setText(msg);
        int endTime = 5;
        waitingProgress.setMax(endTime);
        waitingProgress.setProgress(endTime);

        if(remarkStatus){
            remarks.setVisibility(View.VISIBLE);
            if(playerPoints>=8 && playerPoints<=10){
                remarks.setText("EXCELLENT");
                remarks.setTextColor(getResources().getColor(R.color.green_70));
            } else if (playerPoints>=5 && playerPoints<8) {
                remarks.setText("GOOD");
                remarks.setTextColor(getResources().getColor(R.color.green_70));
            } else if (playerPoints>=3 && playerPoints<5) {
                remarks.setText("SLOW");
                remarks.setTextColor(getResources().getColor(R.color.green_70));
            } else if (playerPoints==0) {
                remarks.setText("WRONG ANSWER");
                remarks.setTextColor(getResources().getColor(R.color.red_70));
            } else{
                remarks.setText("TOO SLOW");
                remarks.setTextColor(getResources().getColor(R.color.green_70));
            }
        } else {
            remarks.setVisibility(View.GONE);
        }

        if(isEnd){
            waitingTimer.setVisibility(View.GONE);
            waitingProgress.setIndeterminate(true);
        } else {
            new CountDownTimer(endTime * 1000L, 1000) {
                @Override
                public void onTick(long l) {
                    int second = (int) l / 1000;
                    waitingTimer.setText(String.valueOf(second));
                    waitingProgress.setProgress(second);
                }

                @Override
                public void onFinish() {
                    waitingLayout.setVisibility(View.GONE);
                    try {
                        gameStarts();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.start();
        }


    }
}