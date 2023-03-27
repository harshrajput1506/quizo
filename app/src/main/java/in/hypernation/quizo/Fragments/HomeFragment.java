package in.hypernation.quizo.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Activities.AllTopicsActivity;
import in.hypernation.quizo.Activities.ProfileActivity;
import in.hypernation.quizo.Activities.TopicActivity;
import in.hypernation.quizo.Adapters.LivePoolAdapter;
import in.hypernation.quizo.Adapters.TopicsAdapter;
import in.hypernation.quizo.Listeners.HomeRecyclerViewListener;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.Models.Pool;
import in.hypernation.quizo.Models.Topic;
import in.hypernation.quizo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";


    // TODO: All Variable Here
    private RecyclerView popularTopicsRV, livePoolRV;
    private ShimmerFrameLayout popularTopicsShimmer, liveQuizShimmer;
    private CircleImageView profileImage;
    private TextView userName, allTopicsBtn;
    private HomeRecyclerViewListener homeRecyclerViewListener;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        popularTopicsShimmer.stopShimmer();
        liveQuizShimmer.stopShimmer();
    }

    @Override
    public void onResume() {
        super.onResume();
        popularTopicsShimmer.startShimmer();
        liveQuizShimmer.startShimmer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //SP init
        SPManager.init(getContext());

        //TODO: Binding
        popularTopicsRV = view.findViewById(R.id.popular_topics_RV);
        livePoolRV = view.findViewById(R.id.live_quiz_RV);
        popularTopicsShimmer = view.findViewById(R.id.popular_topics_shimmer);
        liveQuizShimmer = view.findViewById(R.id.live_quiz_shimmer);
        profileImage = view.findViewById(R.id.profile_picture);
        userName = view.findViewById(R.id.user_name);
        allTopicsBtn = view.findViewById(R.id.topics_more_btn);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));


        //Start Shimmer layout
        popularTopicsShimmer.startShimmer();
        liveQuizShimmer.startShimmer();

        //TODO: Buttons Listeners
        allTopicsBtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), AllTopicsActivity.class);
            startActivity(i);
        });

        profileImage.setOnClickListener( v-> {
            Intent i = new Intent(getActivity(), ProfileActivity.class);
            startActivity(i);
        });

        //RecyclerView Listeners
        homeRecyclerViewListener = new HomeRecyclerViewListener() {
            @Override
            public void onTopicClick(Topic topic) {
                Intent i = new Intent(getActivity(), TopicActivity.class);
                String[] topicData  = {topic.getTitle(), topic.getIcon(), topic.getDescription()};
                i.putExtra("topicData", topicData);
                startActivity(i);
            }
        };

        //Set User Credentials
        userName.setText(SPManager.getStringValue("name", "Master"));
        Glide.with(getContext()).load(SPManager.getStringValue("profilePicture", "")).placeholder(R.drawable.profileavatar).into(profileImage);

        //TODO: Rest of the code
        try {
            getTopics();
            getLivePools();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void setPopularRV(ArrayList<Topic> popularList){
        TopicsAdapter topicsAdapter = new TopicsAdapter(getContext(), popularList, homeRecyclerViewListener);
        popularTopicsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularTopicsRV.setAdapter(topicsAdapter);
        popularTopicsShimmer.setVisibility(View.GONE);
        popularTopicsShimmer.stopShimmer();
        popularTopicsRV.setVisibility(View.VISIBLE);


    }

    private void setLivePoolRV(ArrayList<Pool> list){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        LivePoolAdapter livePoolAdapter = new LivePoolAdapter(getContext(), list);
        livePoolRV.setLayoutManager(linearLayoutManager);
        livePoolRV.setAdapter(livePoolAdapter);
        liveQuizShimmer.setVisibility(View.GONE);
        liveQuizShimmer.stopShimmer();
        livePoolRV.setVisibility(View.VISIBLE);
    }

    private void getTopics() throws JSONException {
        String url = "https://api.hypernation.in/hyperquizo/api/v1/quiz/topics";
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getContext(), url, new VolleyRequestListener() {
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
                    setPopularRV(list);
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

    private void getLivePools(){
        String url = "https://api.hypernation.in/hyperquizo/api/v1/quiz/pool/live";
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getContext(), url, new VolleyRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "onSuccess: Live Quiz Data - "+response);
                try {
                    if(response.getInt("success")==1){
                        // Data Found 
                        JSONArray poolArrayData = response.getJSONArray("data");
                        ArrayList<Pool> poolList = new ArrayList<>();
                        for (int i = 0; i < poolArrayData.length(); i++) {
                            JSONObject poolData = poolArrayData.getJSONObject(i);
                            Pool pool = new Pool();
                            pool.setPoolID(poolData.getInt("id"));
                            pool.setTitle(poolData.getString("title"));
                            pool.setPrizePool(poolData.getInt("prizePool"));
                            pool.setEntryFee(poolData.getInt("entryFees"));
                            pool.setQuestions(poolData.getInt("questions"));
                            pool.setTotalPlayers(poolData.getInt("totalPlayers"));
                            pool.setPlayersJoined(poolData.getInt("playersJoined"));
                            pool.setBonus(poolData.getInt("bonus"));
                            pool.setCategory(poolData.getString("category"));
                            pool.setTotalWinners(poolData.getInt("totalWinners"));
                            pool.setCategoryIcon(poolData.getString("icon"));
                            JSONObject jsonObject = new JSONObject(poolData.getString("prizeDistribution"));
                            pool.setPrizeDistribution(jsonObject);
                            poolList.add(pool);
                        }

                        Log.d(TAG, "onSuccess: Get Pool Live Data " +poolList);

                        setLivePoolRV(poolList);

                    } else {
                        //No Live Pools Yet
                        //TODO: Hide Recycler View & Show No Data Message Container
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "onError: Live Quiz Error - ", error);

            }
        });

        volleyCallRequest.callGetRequest();
    }
}