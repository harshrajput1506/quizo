package in.hypernation.quizo.Activities;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.Models.Pool;
import in.hypernation.quizo.R;

public class WaitingRoomActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private int endTime = 90;
    private ProgressBar progressBar;
    private TextView timerTxt, player1name, player2name;
    private CircleImageView player1picture, player2picture;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String RoomID;
    private LinearLayout player2container;
    private FrameLayout searchingContainer;
    private LottieAnimationView searchingPlayer;
    private View parentLayout;
    private ImageView versus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        progressBar = findViewById(R.id.timer_progress);
        timerTxt = findViewById(R.id.timer_txt);
        player1name = findViewById(R.id.player1name);
        player2name = findViewById(R.id.player2name);
        player1picture = findViewById(R.id.player1picture);
        player2picture = findViewById(R.id.player2picture);
        player2container = findViewById(R.id.player2_container);
        searchingContainer = findViewById(R.id.searching_container);
        searchingPlayer = findViewById(R.id.searching_player);
        parentLayout = findViewById(android.R.id.content);
        versus = findViewById(R.id.versus_img);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        SPManager.init(getApplicationContext());

        RoomID = getIntent().getStringExtra("roomID");
        Log.d("WaitingRoomActivity", "onCreate: RoomID Waiting Room"+RoomID);

        setProgress();
        setCountDownTimer();

        //Set Player1
        String player1_name = SPManager.getStringValue("name", "");
        player1name.setText(player1_name);
        String player1_picture = SPManager.getStringValue("profilePicture", "");
        Glide.with(this).load(player1_picture).placeholder(R.drawable.profileavatar).into(player1picture);

        final DocumentReference doc = db.collection("gameRooms").document(RoomID);
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.w("Realtime Firestore Data", "onEvent: ", error);
                    return;
                }
                if(value != null && value.exists()){
                    Log.d("Realtime Firestore Data", "Current data: " + value.getData());
                    Map<String, Object> data = value.getData();
                    long playersJoined = (long) data.get("playersJoined");
                    if(playersJoined==2){
                        // stop timer
                        stopCountdownTimer();

                        // set opponent data
                        String playerTag = getIntent().getStringExtra("opponentTag");
                        String player2_name = (String) data.get(playerTag+"name");
                        String player2_picture = (String) data.get(playerTag+"picture");

                        Glide.with(getApplicationContext()).load(player2_picture).placeholder(R.drawable.profileavatar).into(player2picture);
                        player2name.setText(player2_name);
                        player2container.setVisibility(View.VISIBLE);
                        searchingContainer.setVisibility(View.GONE);
                        searchingPlayer.cancelAnimation();

                        //Check question Set is available or not
                        if(data.containsKey("questionSet")) {
                            String questionSet = (String) data.get("questionSet");
                            gameStart(data, questionSet);
                        } else {
                            //set data on quizPool model
                            Pool quizPool = new Pool();
                            long questions = (long) data.get("total_questions");
                            quizPool.setQuestions((int)questions);
                            quizPool.setCategory((String) data.get("category"));
                            quizPool.setTitle((String) data.get("title"));

                            //get token questionUpdate request
                            user.getIdToken(true).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    String token = task.getResult().getToken();
                                    try {
                                        updateQuestionsSet(quizPool, RoomID, token);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                } else {
                    Log.d("Realtime Firestore Data", "Current data: null");
                }
            }
        });

    }

    private void setCountDownTimer(){
        try {
            if(countDownTimer!=null){
                countDownTimer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        countDownTimer = new CountDownTimer(endTime* 1000L, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000);
                progressBar.setProgress(seconds);
                timerTxt.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                timerTxt.setText("0");
                timerTxt.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                changeRoomStatus("Time_Out");
                showDialog();
            }
        };
        countDownTimer.start();
    }

    private void stopCountdownTimer(){
        countDownTimer.cancel();
    }

    private void setProgress() {
        progressBar.setMax(endTime);
        progressBar.setProgress(endTime);

    }

    private void changeRoomStatus(String status){
        db.collection("gameRooms").document(RoomID).update("status", status);
    }

    private void showDialog(){
        Dialog dialog = new Dialog(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.lyt_alert_dialog, viewGroup, false);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView msg = dialog.findViewById(R.id.dialog_msg);
        CardView tryBtn = dialog.findViewById(R.id.dialog_action_btn);
        CardView quitBtn = dialog.findViewById(R.id.dialog_cancel_btn);

        title.setText("Sorry!");
        msg.setText("No match found. We have not found your opponent yet.");

        tryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                changeRoomStatus("Open");
                dialog.dismiss();
                setCountDownTimer();
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final DocumentReference doc = db.collection("gameRooms").document(RoomID);
                doc.delete();
                getToken();
                //Update User Balance (Most Imp)
                WaitingRoomActivity.super.onBackPressed();
            }
        });

        dialog.show();
    }

    private void gameStart(Map<String, Object> data, String questionSet){
        progressBar.setVisibility(View.INVISIBLE);
        versus.setVisibility(View.VISIBLE);
        new Handler().postDelayed( () -> {
            String playerTag = getIntent().getStringExtra("opponentTag");
            Intent i = new Intent(WaitingRoomActivity.this, QuizPlayActivity.class);
            i.putExtra("roomID", RoomID);
            i.putExtra("opponentName", (String) data.get(playerTag+"name"));
            i.putExtra("opponentPicture", (String) data.get(playerTag+"picture"));
            i.putExtra("questionSet", questionSet);
            startActivity(i);
            WaitingRoomActivity.this.finish();
        }, 2000);
    }

    private void getToken(){
        if(user!=null){
            user.getIdToken(true).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    try {
                        patchRefund(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(parentLayout, "Something Went Wrong", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateQuestionsSet(Pool quizPool, String roomID, String token) throws JSONException{
        String url = Constant.QUIZ_URL+"1901/room/questions";
        int questions = quizPool.getQuestions();
        String category = quizPool.getCategory();
        if(category.equals("DQ")){
            category = quizPool.getTitle();
        }
        String uid = SPManager.getStringValue("uid", "");
        JSONObject body = new JSONObject();
        body.put("uid", uid);
        body.put("category", category);
        body.put("questions", questions);
        body.put("roomID", roomID);

        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, body, token, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(response.getInt("success")==1){
                        Log.d("Update Question Set", "onSuccess: "+response);

                    }
                } catch(JSONException e){
                    e.printStackTrace();
                    Log.e("Update Question Set", "onJsonError: ", e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("Update Question Set", "onVolleyError: ", error);
            }
        });

        volleyCallRequest.callPostRequest();

    }

    private void patchRefund(String token) throws JSONException{
        String url = Constant.USER_URL+"wallet/4902";
        String uid = SPManager.getStringValue("uid", "").trim();
        double[] entryData = getIntent().getDoubleArrayExtra("entryData");
        double depositBalance = entryData[0];
        double winningBalance = entryData[1];
        double bonusBalance = entryData[2];
        double totalBalance = entryData[3];
        String transMessage = "Game cancelled, amount of ₹"+totalBalance+" refunded";
        JSONObject body = new JSONObject();
        body.put("uid", uid);
        body.put("depositBalance", depositBalance);
        body.put("winningBalance", winningBalance);
        body.put("bonusBalance", bonusBalance);
        body.put("totalBalance", totalBalance);
        body.put("type", "Refund");
        body.put("title", "Refund of A Game");
        body.put("message", transMessage);
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, body, token, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d("Refund Payment", "onSuccess: "+response);
                    if(response.getInt("success")==1){
                        Log.d("Refund When Game Cancelled", "onSuccess: "+response);
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                    Log.e("Refund When Game Cancelled", "onJsonError: ", e);
                }

            }

            @Override
            public void onError(VolleyError error) {
                Log.e("Refund When Game Cancelled", "onVolleyError: ", error);
            }
        });

        volleyCallRequest.callPatchRequest();
    }

    @Override
    public void onBackPressed() {
        // Nothing Happens
        //super.onBackPressed();
    }
}