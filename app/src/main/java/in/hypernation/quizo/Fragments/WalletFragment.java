package in.hypernation.quizo.Fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import in.hypernation.quizo.Constant;
import in.hypernation.quizo.Listeners.VolleyRequestListener;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.Managers.VolleyCallRequest;
import in.hypernation.quizo.R;
import io.grpc.HandlerRegistry;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletFragment extends Fragment {

    // TODO: All Variables Here
    private TextView totalBalance, depositBalance, winningsBalance, bonusBalance;
    private CardView addBtn, withdrawBtn, recentTransactionBtn, kycBtn;
    private ImageView walletInfoBtn;
    private ShimmerFrameLayout totalBalanceShimmer, depositShimmer, winningsShimmer, bonusShimmer;

    public WalletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WalletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalletFragment newInstance(String param1, String param2) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        SPManager.init(getContext());

        totalBalance = view.findViewById(R.id.wallet_total_balance);
        depositBalance = view.findViewById(R.id.deposit_balance_txt);
        winningsBalance = view.findViewById(R.id.winnings_balance_txt);
        bonusBalance = view.findViewById(R.id.bonus_balance_txt);
        addBtn = view.findViewById(R.id.wallet_add);
        withdrawBtn = view.findViewById(R.id.wallet_withdraw);
        recentTransactionBtn = view.findViewById(R.id.recent_transactions_btn);
        kycBtn = view.findViewById(R.id.kyc_verification_btn);
        walletInfoBtn = view.findViewById(R.id.wallet_info_btn);
        totalBalanceShimmer = view.findViewById(R.id.total_balance_shimmer);
        depositShimmer = view.findViewById(R.id.deposit_balance_shimmer);
        bonusShimmer = view.findViewById(R.id.bonus_balance_shimmer);
        winningsShimmer = view.findViewById(R.id.winnings_balance_shimmer);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        totalBalance.setVisibility(View.INVISIBLE);
        totalBalanceShimmer.setVisibility(View.VISIBLE);
        depositBalance.setVisibility(View.GONE);
        bonusBalance.setVisibility(View.GONE);
        winningsBalance.setVisibility(View.GONE);
        depositShimmer.setVisibility(View.VISIBLE);
        bonusShimmer.setVisibility(View.VISIBLE);
        winningsShimmer.setVisibility(View.VISIBLE);
        totalBalanceShimmer.startShimmer();
        depositShimmer.startShimmer();
        winningsShimmer.startShimmer();
        bonusShimmer.startShimmer();



        String uid = SPManager.getStringValue("uid", "");
        getWalletData(uid);

        addBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add", Toast.LENGTH_SHORT).show();
        });
        withdrawBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Withdraw", Toast.LENGTH_SHORT).show();
        });

        recentTransactionBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Recent Transactions", Toast.LENGTH_SHORT).show();
        });

        kycBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "KYC Verification", Toast.LENGTH_SHORT).show();
        });

        walletInfoBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Wallet Info", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    public void getWalletData(String uid){
        String url = Constant.USER_URL+"wallet/3901/id/"+uid;
        VolleyCallRequest volleyCallRequest = new VolleyCallRequest(getContext(), url, new VolleyRequestListener() {
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

                       totalBalance.setText("₹ "+ totalAmount);
                       depositBalance.setText("₹"+ depositAmount);
                       winningsBalance.setText("₹"+ winningsAmount);
                       bonusBalance.setText("₹"+ bonusAmount);
                       SPManager.setStringValue("bonusBalance", String.valueOf(bonusAmount));
                       SPManager.setStringValue("depositBalance", String.valueOf(depositAmount));
                       SPManager.setStringValue("winningBalance", String.valueOf(winningsAmount));
                       totalBalanceShimmer.setVisibility(View.GONE);
                       depositShimmer.setVisibility(View.GONE);
                       winningsShimmer.setVisibility(View.GONE);
                       bonusShimmer.setVisibility(View.GONE);
                       bonusBalance.setVisibility(View.VISIBLE);
                       winningsBalance.setVisibility(View.VISIBLE);
                       depositBalance.setVisibility(View.VISIBLE);
                       totalBalance.setVisibility(View.VISIBLE);
                        totalBalanceShimmer.stopShimmer();
                        depositShimmer.stopShimmer();
                        bonusShimmer.stopShimmer();
                        winningsShimmer.stopShimmer();

                    } else {
                        Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        volleyCallRequest.callGetRequest();
    }
}