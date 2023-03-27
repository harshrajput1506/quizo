package in.hypernation.quizo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.hypernation.quizo.Adapters.OnboardSliderAdapter;
import in.hypernation.quizo.Managers.ExpandAnimation;
import in.hypernation.quizo.Managers.SPManager;
import in.hypernation.quizo.R;

public class OnBoardActivity extends AppCompatActivity {

    private LinearLayout dotsLayout, textContainer;
    private ViewPager2 viewPager2;
    private TextView title, description, skipBtn, nextBtnTxt;
    private CardView nextBtn;
    private ImageView nextBtnImg;
    private OnboardSliderAdapter onboardSliderAdapter;
    private final String TAG = "OnBoard Screen";
    private Animation textAnimation;
    private List<Integer> titleList, descriptionList, sliderImages;
    private int sliderPosition = 0;
    private boolean isStart = true, isNextImg = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        //Add Hooks
        viewPager2 = findViewById(R.id.onboard_slider);
        title = findViewById(R.id.onboard_title);
        description = findViewById(R.id.onboard_description);
        dotsLayout = findViewById(R.id.onboard_dots);
        nextBtn = findViewById(R.id.onboard_next_btn);
        skipBtn = findViewById(R.id.onboard_skip_btn);
        textContainer = findViewById(R.id.onboard_text_container);
        nextBtnImg = findViewById(R.id.onboard_next_icon);
        nextBtnTxt = findViewById(R.id.onboard_next_txt);

        //title and description list
        titleList = new ArrayList<>();
        descriptionList = new ArrayList<>();
        titleList.add(R.string.onboard_title_1);
        titleList.add(R.string.onboard_title_2);
        titleList.add(R.string.onboard_title_3);
        descriptionList.add(R.string.onboard_description_1);
        descriptionList.add(R.string.onboard_description_2);
        descriptionList.add(R.string.onboard_description_3);

        sliderImages = new ArrayList<>();
        sliderImages.add(R.drawable.ic_slider1);
        sliderImages.add(R.drawable.ic_slider2);
        sliderImages.add(R.drawable.ic_slider3);


        //set Animation
        textAnimation = AnimationUtils.loadAnimation(this, R.anim.onboard_text_slide);

        //SP INIT
        SPManager.init(getApplicationContext());

        //Init slider
        setSlider();
        setSliderDots();

        //TODO: Slider change listener
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d(TAG, "onPageScrollStateChanged: "+state);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d(TAG, "onPageScrolled: "+position+" offset: "+positionOffset+" offsetPixels: "+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderPosition = position;
                setCurrentSliderDot(position);
                if(isNextImg){
                    if(position+1==sliderImages.size()){
                        Log.d(TAG, "onCreate: orgWidth"+nextBtn.getWidth());
                        Animation fadeOut = AnimationUtils.loadAnimation(OnBoardActivity.this, R.anim.fade_out);
                        ExpandAnimation expandAnimation = new ExpandAnimation(nextBtn, nextBtn.getWidth(), getResources().getDisplayMetrics().density, 70);
                        expandAnimation.setDuration(300);
                        expandAnimation.setInterpolator(OnBoardActivity.this, android.R.anim.linear_interpolator);
                        nextBtn.setClickable(false);
                        nextBtn.startAnimation(expandAnimation);
                        nextBtnImg.startAnimation(fadeOut);
                        expandAnimationListener(expandAnimation);
                    }
                }
            }
        });

        //TODO: On Click Methods

        skipBtn.setOnClickListener(view -> {
            SPManager.setBooleanValue("isOnBoard", false);
            Intent i = new Intent(OnBoardActivity.this, AuthActivity.class);
            startActivity(i);
            OnBoardActivity.this.finish();
        });

        nextBtn.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: orgWidth"+view.getWidth()+" "+getResources().getDisplayMetrics().density+" "+view.getLeft());
            if(viewPager2.getCurrentItem() + 1 < onboardSliderAdapter.getItemCount()){
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }

            if(!isNextImg){
                SPManager.setBooleanValue("isOnBoard", false);
                Intent i = new Intent(OnBoardActivity.this, AuthActivity.class);
                startActivity(i);
                OnBoardActivity.this.finish();
            }
        });

        //TODO: Animation Listener
        textAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                title.setText(titleList.get(sliderPosition));
                description.setText(descriptionList.get(sliderPosition));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void setSlider(){

        onboardSliderAdapter = new OnboardSliderAdapter(sliderImages);
        viewPager2.setAdapter(onboardSliderAdapter);

    }

    private void setSliderDots(){
        ImageView[] dots = new ImageView[onboardSliderAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = this.getResources().getInteger(R.integer.dots_margin);
        layoutParams.setMargins(margin, 0, margin, 0);

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getApplicationContext());
            dots[i].setImageResource(R.drawable.slider_dot_inactive);
            dots[i].setLayoutParams(layoutParams);
            dotsLayout.addView(dots[i]);
        }
    }

    private void setCurrentSliderDot(int position){
        int childCount = dotsLayout.getChildCount();
        if(!isStart){
            textContainer.startAnimation(textAnimation);  //Start Animation (Not start in first)
        } else {
            title.setText(titleList.get(sliderPosition));
            description.setText(descriptionList.get(sliderPosition));
            isStart = false;
        }
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) dotsLayout.getChildAt(i);
            if(i==position){
                imageView.setImageResource(R.drawable.slider_dot_active);
            } else {
                imageView.setImageResource(R.drawable.slider_dot_inactive);
            }
        }
    }

    private void expandAnimationListener(ExpandAnimation expandAnimation){
        //TODO: ExpandAnimation Listener
        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextBtnImg.setVisibility(View.INVISIBLE);
                nextBtnTxt.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.INVISIBLE);
                isNextImg = false;
                nextBtn.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}