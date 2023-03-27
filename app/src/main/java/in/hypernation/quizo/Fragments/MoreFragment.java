package in.hypernation.quizo.Fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import in.hypernation.quizo.Adapters.MoreAdapter;
import in.hypernation.quizo.BuildConfig;
import in.hypernation.quizo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   private RecyclerView moreRV;
   private TextView appVersion;

    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance(String param1, String param2) {
        MoreFragment fragment = new MoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        moreRV = view.findViewById(R.id.more_rv);
        appVersion = view.findViewById(R.id.more_version_txt);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.ghost_blue));

        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<Integer> iconList = new ArrayList<>();
        titleList.add("How to Play (Rules)");
        iconList.add(R.drawable.document);
        titleList.add("FAQ");
        iconList.add(R.drawable.question);
        titleList.add("About Us");
        iconList.add(R.drawable.about);
        titleList.add("Help Desk");
        iconList.add(R.drawable.helpdesk);
        titleList.add("Feedback");
        iconList.add(R.drawable.chat);
        titleList.add("Legality");
        iconList.add(R.drawable.legalhammer);
        titleList.add("Privacy Policy");
        iconList.add(R.drawable.privacy);
        titleList.add("Terms & Conditions");
        iconList.add(R.drawable.accept);

        String version_text = "Version - "+BuildConfig.VERSION_NAME;

        appVersion.setText(version_text);

        MoreAdapter moreAdapter = new MoreAdapter(titleList, iconList, getContext());
        moreRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        moreRV.setAdapter(moreAdapter);
        return view;
    }
}