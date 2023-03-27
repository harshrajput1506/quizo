package in.hypernation.quizo.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.hypernation.quizo.Activities.TopicActivity;
import in.hypernation.quizo.Adapters.PDListAdapter;
import in.hypernation.quizo.Listeners.TopicFragmentRefresh;
import in.hypernation.quizo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PDFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PD = "prizeDistribution";

    // TODO: Rename and change types of parameters
    private String pd;
    private JSONArray ranks;
    private JSONArray prizes;
    private ArrayList<String> rankList;
    private ArrayList<String> prizeList;
    private PDListAdapter pdListAdapter;

    public PDFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PDFragment newInstance(String pd) {
        PDFragment fragment = new PDFragment();
        Bundle args = new Bundle();
        args.putString(PD, pd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pd = getArguments().getString(PD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_p_d, container, false);

        ListView listView = view.findViewById(R.id.pd_list);

        ((TopicActivity)getActivity()).setTopicFragmentRefresh(new TopicFragmentRefresh() {
            @Override
            public void onRefresh(JSONObject pd) {
                try{
                    ranks = pd.getJSONArray("ranks");
                    prizes = pd.getJSONArray("prize");
                    rankList.clear();
                    prizeList.clear();
                    for (int i = 0; i < ranks.length(); i++) {
                        rankList.add(ranks.getString(i));
                        prizeList.add(prizes.getString(i));
                    }

                    pdListAdapter.notifyDataSetChanged();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        try {
            JSONObject prizeDistribution = new JSONObject(pd);
            rankList = new ArrayList<>();
            prizeList = new ArrayList<>();
            ranks = prizeDistribution.getJSONArray("ranks");
            prizes = prizeDistribution.getJSONArray("prize");
            for (int i = 0; i < ranks.length(); i++) {
                rankList.add(ranks.getString(i));
                prizeList.add(prizes.getString(i));
            }
            pdListAdapter = new PDListAdapter(getContext(), rankList, prizeList);
            listView.setAdapter(pdListAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}