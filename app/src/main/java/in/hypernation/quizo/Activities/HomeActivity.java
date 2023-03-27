package in.hypernation.quizo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import in.hypernation.quizo.Fragments.ContestFragment;
import in.hypernation.quizo.Fragments.HomeFragment;
import in.hypernation.quizo.Fragments.MoreFragment;
import in.hypernation.quizo.Fragments.WalletFragment;
import in.hypernation.quizo.R;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class HomeActivity extends AppCompatActivity {

    AnimatedBottomBar bottomBar;
    Fragment selectedFragment;
    private HomeFragment homeFragment;
    private WalletFragment walletFragment;
    private ContestFragment contestFragment;
    private MoreFragment moreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeFragment = new HomeFragment();
        walletFragment = new WalletFragment();
        moreFragment = new MoreFragment();
        contestFragment = new ContestFragment();

        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.frame_layout, homeFragment).commit();

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1) {
                    case 0:
                        selectedFragment = homeFragment;
                        break;

                    case 1:
                        selectedFragment = contestFragment;
                        break;

                    case 2:
                        selectedFragment = walletFragment;
                        break;

                    case 3:
                        selectedFragment = moreFragment;
                        break;
                }

                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();

            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

    }
}