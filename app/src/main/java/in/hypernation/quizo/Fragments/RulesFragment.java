package in.hypernation.quizo.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.hypernation.quizo.Activities.TopicActivity;
import in.hypernation.quizo.Listeners.TopicRuleRefresh;
import in.hypernation.quizo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RulesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String QUESTIONS = "totalQuestions";

    private TextView rules;

    // TODO: Rename and change types of parameters
    private String questions;

    public RulesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RulesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RulesFragment newInstance(String totalQuestions) {
        RulesFragment fragment = new RulesFragment();
        Bundle args = new Bundle();
        args.putString(QUESTIONS, totalQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questions = getArguments().getString(QUESTIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rules, container, false);

        rules = view.findViewById(R.id.topic_rules_questions);

        ((TopicActivity)getActivity()).setTopicRuleRefresh(new TopicRuleRefresh() {
            @Override
            public void onRefresh(String totalQuestions) {
                String rule1 = "1. Answer a set of "+totalQuestions+" questions.";
                rules.setText(rule1);
            }
        });

        String rule1 = "1. Answer a set of "+questions+" questions.";
        rules.setText(rule1);



        return view;
    }
}