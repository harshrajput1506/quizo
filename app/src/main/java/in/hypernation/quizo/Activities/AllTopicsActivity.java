package in.hypernation.quizo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import in.hypernation.quizo.Adapters.AllTopicsAdapter;
import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.AllTopicListener;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.Models.Topic;
import in.hypernation.quizo.R;

public class AllTopicsActivity extends AppCompatActivity implements AllTopicListener {

    private final String TAG = "AllTopicsActivity";
    private RecyclerView allTopicsRV;
    private ImageView backBtn;
    private ConstraintLayout titleBar;
    private ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_topics);


        allTopicsRV = findViewById(R.id.all_topics_RV);
        backBtn = findViewById(R.id.all_topics_back_btn);
        titleBar = findViewById(R.id.all_topics_title_bar);
        shimmerFrameLayout = findViewById(R.id.all_topics_shimmer);

        //Start Shimmer
        allTopicsRV.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        getTopics();

        allTopicsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scrollY = recyclerView.computeVerticalScrollOffset();

                if(scrollY <= 0){
                    titleBar.setElevation(0);
                } else {
                    titleBar.setElevation(20);
                }
            }
        });

        backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

    }

    private void getTopics() {
        String url = Constant.QUIZ_URL+"all-topics";
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getApplicationContext(), url, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "onSuccess: Topic Data - "+response);
                ArrayList<Topic> list = new ArrayList<>();
                try {
                    JSONArray data = response.getJSONObject("data").getJSONArray("topics");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject topicData = data.getJSONObject(i);
                        Topic topic = new Topic(topicData.getInt("id"), topicData.getString("icon"),
                                topicData.getString("title"), topicData.getInt("popularity"),
                                topicData.getString("description"));
                        list.add(topic);
                    }
                    setAllTopicsRV(list);
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "onError: Topic Error - ", error);
            }
        });

        volleyCallRequest.callGetRequest();
    }

    private void setAllTopicsRV(ArrayList<Topic> list){
        allTopicsRV.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.VERTICAL, false));
        AllTopicsAdapter allTopicsAdapter = new AllTopicsAdapter(list, getApplicationContext(), this);
        allTopicsRV.setAdapter(allTopicsAdapter);
        shimmerFrameLayout.setVisibility(View.GONE);
        allTopicsRV.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmer();

    }

    @Override
    public void onClick(Topic topic) {
        Intent i = new Intent(AllTopicsActivity.this, TopicActivity.class);
        String[] topicData  = {topic.getTitle(), topic.getIcon(), topic.getDescription()};
        i.putExtra("topicData", topicData);
        startActivity(i);
    }
}