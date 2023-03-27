package in.hypernation.quizo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.hypernation.quizo.Adapters.LoadingDialogAdapter;
import in.hypernation.quizo.Adapters.TopicEntryFeeAdapter;
import in.hypernation.quizo.Adapters.TopicTabAdapter;
import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Fragments.PDFragment;
import in.hypernation.quizo.Fragments.RulesFragment;
import in.hypernation.quizo.Listeners.EntryFeeListener;
import in.hypernation.quizo.Listeners.TopicFragmentRefresh;
import in.hypernation.quizo.Listeners.TopicRuleRefresh;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.Models.Pool;
import in.hypernation.quizo.Models.Topic;
import in.hypernation.quizo.R;

public class TopicActivity extends AppCompatActivity {

    private final String RUPEE_SYMBOL = "₹";
    private boolean isPayClickable = true;
    private ArrayList<Pool> poolList;
    private RecyclerView topicRV;

    private BottomSheetDialog bottomSheetDialog;
    private ImageView icon, backBtn;
    private TextView title, descriptionTxt, prizePool, playersJoined, bonus, entryBtnTxt;
    private LinearLayout entryBtn;
    private ViewPager viewPager;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TabLayout tabLayout;
    private TopicFragmentRefresh topicFragmentRefresh;
    private TopicRuleRefresh topicRuleRefresh;
    private int currentPoolPosition = 0;
    private View parentLayout;
    private LoadingDialogAdapter loadingDialogAdapter;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        topicRV = findViewById(R.id.topic_entry_fee_RV);
        icon = findViewById(R.id.topic_icon);
        title = findViewById(R.id.topic_title);
        descriptionTxt = findViewById(R.id.topic_description);
        backBtn = findViewById(R.id.topic_back_btn);
        bonus = findViewById(R.id.topic_bonus);
        playersJoined = findViewById(R.id.topic_players_joined);
        prizePool = findViewById(R.id.topic_prize_pool);
        tabLayout = findViewById(R.id.topic_tab);
        viewPager = findViewById(R.id.topic_viewpager);
        entryBtn = findViewById(R.id.topic_entry_btn);
        entryBtnTxt = findViewById(R.id.topic_entry_btn_txt);
        shimmerFrameLayout = findViewById(R.id.topic_shimmer);
        parentLayout = findViewById(android.R.id.content);

        SPManager.init(getApplicationContext());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        loadingDialogAdapter = LoadingDialogAdapter.newInstance();

        String category = getIntent().getStringArrayExtra("topicData")[0];
        String iconUrl = getIntent().getStringArrayExtra("topicData")[1];
        String description = getIntent().getStringArrayExtra("topicData")[2];

        startShimmer();

        getQuizPool(category);

        //Set Data
        title.setText(category);
        descriptionTxt.setText(description);
        Glide.with(getApplicationContext()).load(iconUrl).placeholder(R.drawable.random).into(icon);



        //TODO: Listeners
        backBtn.setOnClickListener(view ->
        {
            super.onBackPressed();
        });

        entryBtn.setOnClickListener(v-> {
            showBottomSheet(currentPoolPosition);
        });

    }

    private void showBottomSheet(int currentPoolPosition) {
        bottomSheetDialog = new BottomSheetDialog(TopicActivity.this);
        bottomSheetDialog.setContentView(R.layout.lyt_bottomsheet_entry_confirmation);

        TextView dwBalance, bonusBalance, bonusEntry, dwEntry, totalBalance, entryFeeTxt;
        LinearLayout cancelBtn, payBtn;
        dwBalance = bottomSheetDialog.findViewById(R.id.DW_balance);
        bonusBalance = bottomSheetDialog.findViewById(R.id.bonus_balance);
        bonusEntry = bottomSheetDialog.findViewById(R.id.bonus_entry_fee);
        dwEntry = bottomSheetDialog.findViewById(R.id.DW_entry_fee);
        totalBalance = bottomSheetDialog.findViewById(R.id.total_balance);
        entryFeeTxt = bottomSheetDialog.findViewById(R.id.entry_fee);
        cancelBtn = bottomSheetDialog.findViewById(R.id.cancel_btn);
        payBtn = bottomSheetDialog.findViewById(R.id.pay_btn);


        double depositBalance =  Double.parseDouble(SPManager.getStringValue("depositBalance", "0.00"));
        double winningBalance = Double.parseDouble(SPManager.getStringValue("winningBalance", "0.00"));
        double avDwBalance = depositBalance+winningBalance;
        double avBonusBalance = Double.parseDouble(SPManager.getStringValue("bonusBalance", "0.00"));
        double avTotalBalance = depositBalance+winningBalance+avBonusBalance;


        dwBalance.setText(RUPEE_SYMBOL+avDwBalance);
        bonusBalance.setText(RUPEE_SYMBOL+avBonusBalance);
        totalBalance.setText(RUPEE_SYMBOL+avTotalBalance);

        if(poolList!=null){
            Pool pool = poolList.get(currentPoolPosition);
            double entryFee = (double) pool.getEntryFee();
            double bonus = pool.getBonus();
            double bonusEntryFee = (entryFee*bonus/100);
            double dwEntryFee = entryFee-bonusEntryFee;

            bonusEntry.setText(RUPEE_SYMBOL+bonusEntryFee);
            dwEntry.setText(RUPEE_SYMBOL+dwEntryFee);
            entryFeeTxt.setText(RUPEE_SYMBOL+entryFee);

        }
        assert payBtn != null;
        payBtn.setClickable(isPayClickable);

        cancelBtn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        payBtn.setOnClickListener( v -> {
            // if entry fee is zero it means it's a  free  game. Direct we get rooms and checking them and then go to waiting screen...
            payBtn.setClickable(false);
            if(poolList!=null){
                getWalletData(poolList.get(currentPoolPosition));
            }
        });

        bottomSheetDialog.show();
    }

    public void setTopicFragmentRefresh(TopicFragmentRefresh topicFragmentRefresh){
        this.topicFragmentRefresh = topicFragmentRefresh;
    }

    public void setTopicRuleRefresh(TopicRuleRefresh topicRuleRefresh){
        this.topicRuleRefresh = topicRuleRefresh;
    }

    private void startShimmer(){
        tabLayout.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.INVISIBLE);
        topicRV.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
    }

    private void stopShimmer(){
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        topicRV.setVisibility(View.VISIBLE);
    }

    private void changePool(int position){
        Pool selectedPool = poolList.get(position);
        currentPoolPosition = position;
        String prizePoolLbl = "₹"+selectedPool.getPrizePool();
        String playersJoinedLbl = selectedPool.getPlayersJoined()+"/2";
        playersJoined.setText(playersJoinedLbl);
        prizePool.setText(prizePoolLbl);
        if(selectedPool.getEntryFee()==0){
            entryBtnTxt.setText("Play Now Free");
        } else {
            String entryTxt = "Play Now "+Constant.RUPEE_SYMBOL+selectedPool.getEntryFee();
            entryBtnTxt.setText(entryTxt);
        }
        if(selectedPool.getBonus() == 0){
            bonus.setText("");
        } else {
            String bonusLbl = "Bonus "+(int) (selectedPool.getBonus())+"%";
            bonus.setText(bonusLbl);
        }

        //Change prize distribution
        if(topicFragmentRefresh!=null){
            topicFragmentRefresh.onRefresh(selectedPool.getPrizeDistribution());
        }

        if(topicRuleRefresh!=null){
            topicRuleRefresh.onRefresh(String.valueOf(selectedPool.getQuestions()));
        }
    }

    private void setTabs(int position){
        TopicTabAdapter topicTabAdapter = new TopicTabAdapter(getSupportFragmentManager());
        Pool pool = poolList.get(position);
        topicTabAdapter.addFragment(RulesFragment.newInstance(String.valueOf(pool.getQuestions())), "Rules");
        topicTabAdapter.addFragment(PDFragment.newInstance(pool.getPrizeDistribution().toString()), "Prize Distribution");

        viewPager.setAdapter(topicTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setTopicRV(){
        topicRV.setLayoutManager(new LinearLayoutManager(TopicActivity.this, LinearLayoutManager.HORIZONTAL, false));
        TopicEntryFeeAdapter topicEntryFeeAdapter = new TopicEntryFeeAdapter(TopicActivity.this, poolList, getResources().getDisplayMetrics().density , this::changePool);

        topicRV.setAdapter(topicEntryFeeAdapter);
        changePool(0);
    }

    private void getQuizPool(String category){
        String url = Constant.QUIZ_URL+"quizPool/category/"+category;
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(response.getString("success").equals("1")){

                        poolList = new ArrayList<>();
                        JSONArray poolArray = response.getJSONArray("data");

                        for (int i = 0; i < poolArray.length(); i++) {
                            Pool poolModel = new Pool();
                            JSONObject pool = poolArray.getJSONObject(i);
                            poolModel.setPoolID(pool.getInt("id"));
                            poolModel.setTitle(pool.getString("title"));
                            poolModel.setPrizePool(pool.getInt("prizePool"));
                            poolModel.setEntryFee(pool.getInt("entryFees"));
                            poolModel.setQuestions(pool.getInt("questions"));
                            poolModel.setTotalPlayers(pool.getInt("totalPlayers"));
                            poolModel.setPlayersJoined(pool.getInt("playersJoined"));
                            poolModel.setBonus(pool.getInt("bonus"));
                            poolModel.setCategory(pool.getString("category"));
                            poolModel.setCategoryIcon(pool.getString("icon"));
                            poolModel.setTotalWinners(pool.getInt("totalWinners"));
                            JSONObject jsonObject = new JSONObject(pool.getString("prizeDistribution"));
                            poolModel.setPrizeDistribution(jsonObject);
                            poolList.add(poolModel);
                        }

                        setTopicRV();
                        stopShimmer();
                        setTabs(0);



                    } else {
                        Toast.makeText(getApplicationContext(), "No Quiz Yet", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(TopicActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        volleyCallRequest.callGetRequest();
    }

    public void getWalletData(Pool pool){
        String uid = SPManager.getStringValue("uid", "").trim();
        String url = Constant.USER_URL+"wallet/3901/id/"+uid;
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(response.getInt("success")==1){
                        // Get Data
                        JSONObject data = response.getJSONObject("data");
                        double totalAmount =  (double) data.getInt("totalBalance");
                        double depositAmount =  (double) data.getInt("depositBalance");
                        double winningsAmount =  (double) data.getInt("winningBalance");
                        double bonusAmount =  (double) data.getInt("bonusBalance");


                        SPManager.setStringValue("bonusBalance", String.valueOf(bonusAmount));
                        SPManager.setStringValue("depositBalance", String.valueOf(depositAmount));
                        SPManager.setStringValue("winningBalance", String.valueOf(winningsAmount));
                        SPManager.setStringValue("totalBalance", String.valueOf(totalAmount));

                        checkBalance(pool);



                    } else {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        volleyCallRequest.callGetRequest();
    }

    private void checkBalance(Pool quizPool){
        double depositBalance = Double.parseDouble(SPManager.getStringValue("depositBalance", "0"));
        double bonusBalance = Double.parseDouble(SPManager.getStringValue("bonusBalance", "0"));
        double winningBalance = Double.parseDouble(SPManager.getStringValue("winningBalance", "0"));
        double entryFees = quizPool.getEntryFee();
        double bonus = quizPool.getBonus();
        double winningEntry = 0;
        double depositEntry = 0;
        double bonusEntryFee;
        if(bonus == 0){
            bonusEntryFee = 0;
        } else {
            bonusEntryFee = entryFees*bonus/100;
            if(bonusBalance<bonusEntryFee){
                bonusEntryFee = bonusBalance;
            }
        }
        if(depositBalance<(entryFees-bonusEntryFee)){
            if(winningBalance<(entryFees-bonusEntryFee-depositBalance)){
                insufficientBalance();
            } else {
                depositEntry = depositBalance;
                winningEntry = entryFees-bonusEntryFee-depositEntry;
                double[] entryData  = {depositEntry, winningEntry, bonusEntryFee, entryFees};
                startPaymentProcess(quizPool, entryData);
            }
        } else {
            depositEntry = entryFees-bonusEntryFee;
            double[] entryData  = {depositEntry, winningEntry, bonusEntryFee, entryFees};
            startPaymentProcess(quizPool, entryData);
        }

    }

    private void startPaymentProcess(Pool pool, double[] entryData){
        bottomSheetDialog.dismiss();
        loadingDialogAdapter.show(getSupportFragmentManager(), "loader");
        getToken(pool, entryData);
    }

    private void getToken(Pool pool, double[] entryData){
        if(user!=null){
            user.getIdToken(true).addOnCompleteListener( task -> {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    try {
                        patchPoolPayment(pool, entryData, token);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    private void insufficientBalance() {
        // InsufficientBalance
        bottomSheetDialog.dismiss();
        Snackbar snackbar = Snackbar.make(parentLayout, "Insufficient Balance", Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(R.color.black));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    private void patchPoolPayment(Pool pool,  double[] entryData ,String token) throws JSONException{
        String url = Constant.USER_URL+"pool/4901";
        String uid = SPManager.getStringValue("uid", "").trim();
        double winningEntry =  entryData[1];
        double bonusEntry = entryData[2];
        double depositEntry = entryData[0];
        double totalEntry = winningEntry+bonusEntry+depositEntry;
        JSONObject body = new JSONObject();
        body.put("uid", uid);
        body.put("totalEntry", totalEntry);
        body.put("depositEntry", depositEntry);
        body.put("winningEntry", winningEntry);
        body.put("bonusEntry", bonusEntry);


        VolleyCallRequest patchRequest = new VolleyCallRequest(getApplicationContext(), url, body, token,new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if(response.getInt("success")==1){
                        getRoomByPoolID(pool, entryData);
                    }
                } catch (JSONException e){
                    loadingDialogAdapter.dismissAllowingStateLoss();
                    e.printStackTrace();
                    Log.d("Pool Payment", "onError: "+e);
                }
                Log.d("Pool Payment", "onSuccess: "+response);
            }

            @Override
            public void onError(VolleyError error) {
                loadingDialogAdapter.dismissAllowingStateLoss();
                Log.d("Pool Payment", "onSuccess: "+error);
                Snackbar.make(parentLayout, "Something Went Wrong", Snackbar.LENGTH_LONG).show();
            }
        });

        patchRequest.callPatchRequest();
    }

    private void getRoomByPoolID(Pool pool, double[] entryData){
        db.collection("gameRooms").whereEqualTo("poolID", pool.getPoolID()).
                whereEqualTo("status", "Open").limit(1).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(!task.getResult().getDocuments().isEmpty()){
                            Log.d("Pool Payment", task.getResult().getDocuments().get(0).getId() + " => " + task.getResult().getDocuments().get(0).getData());
                            // Room created
                            String roomID = task.getResult().getDocuments().get(0).getId();
                            // Update Room - Player 2 data update
                            updateExistingRoom(roomID, entryData);
                        } else {
                            // Room not created - Create a new room after that send question update request
                            createNewRoom(pool, entryData);
                        }

                    } else {
                        Log.d("Pool Payment", "Error getting documents: ", task.getException());
                        //Refund Request
                    }
                });
    }

    private void createNewRoom(Pool pool, double[] entryData) {
        String name = SPManager.getStringValue("name", "master");
        String profilePicture = SPManager.getStringValue("profilePicture", "https://api.hypernation.in/res/uploads/avatar.png");
        String uid = SPManager.getStringValue("uid", "").trim();
        Map<String, Object> addData = new HashMap<>();
        addData.put("player1id", uid);
        addData.put("player1name", name);
        addData.put("player1picture", profilePicture);
        addData.put("playersJoined", 1);
        addData.put("status", "Open");
        addData.put("winnerStatus", "Pending");
        addData.put("player1points", 0);
        addData.put("entryFee", pool.getEntryFee());
        addData.put("prizePool", pool.getPrizePool());
        addData.put("poolID", pool.getPoolID());
        addData.put("category", pool.getCategory());
        addData.put("title", pool.getTitle());
        addData.put("total_questions", pool.getQuestions());
        addData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("gameRooms").add(addData).addOnSuccessListener(documentReference -> {
            String roomID = documentReference.getId();

            Log.d("Created New Room", "onSuccess: id - "+roomID);

            // get token & call update question method
            user.getIdToken(false).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    try {
                        updateQuestionsSet(pool, roomID, token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            loadingDialogAdapter.dismissAllowingStateLoss();
            Intent i = new Intent(TopicActivity.this, WaitingRoomActivity.class);
            i.putExtra("roomID", roomID);
            i.putExtra("opponentTag", "player2");
            i.putExtra("entryData", entryData);
            startActivity(i);
        }).addOnFailureListener(e -> {
            loadingDialogAdapter.dismissAllowingStateLoss();
            isPayClickable = true;

            user.getIdToken(true).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    try {
                        refundBalance(entryData, token);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

    }

    private void updateQuestionsSet(Pool pool, String roomID, String token) throws JSONException {
        String url = Constant.QUIZ_URL+"1901/room/questions";
        int questions = pool.getQuestions();
        String category = pool.getCategory();
        if(category.equals("DQ")){
            category = pool.getTitle();
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

    private void updateExistingRoom(String roomID, double[] entryData) {
        Map<String, Object> updates = new HashMap<>();
        String name = SPManager.getStringValue("name", "master");
        String profilePicture = SPManager.getStringValue("profilePicture", "https://api.hypernation.in/res/uploads/avatar.png");
        String uid = SPManager.getStringValue("uid", "").trim();
        updates.put("player2name", name);
        updates.put("player2picture", profilePicture);
        updates.put("player2id", uid);
        updates.put("status", "Live");
        updates.put("playersJoined", 2);
        updates.put("player2status", "Online");
        updates.put("player2points", 0);

        DocumentReference doc = db.collection("gameRooms").document(roomID);

        doc.update(updates).addOnCompleteListener(task -> {

            Log.d("GameRoom Update", "onComplete: "+task.isSuccessful());
            loadingDialogAdapter.dismissAllowingStateLoss();
            Intent i = new Intent(TopicActivity.this, WaitingRoomActivity.class);
            i.putExtra("roomID", roomID);
            i.putExtra("opponentTag", "player1");
            i.putExtra("entryData", entryData);
            startActivity(i);

        }).addOnFailureListener(e -> {

            loadingDialogAdapter.dismissAllowingStateLoss();
            isPayClickable = true;
            // Update Failed

            user.getIdToken(true).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String token = task.getResult().getToken();
                    try {
                        refundBalance(entryData, token);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        });
    }

    private void refundBalance(double[] entryData, String token) throws JSONException {
        String url = Constant.USER_URL+"wallet/4902";
        String uid = SPManager.getStringValue("uid", "").trim();
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
                        Log.d("Refund When Game Cancelled", "onSuccess: "+response.getString("data"));
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

}