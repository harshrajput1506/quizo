package in.hypernation.quizo.Managers;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandAnimation extends Animation {

    protected final View view;
    protected float perValueW;
    protected final int originalWidth;

    public ExpandAnimation(View view, int originalWidth, float density, int expandWidth){
        this.view = view;
        this.originalWidth = originalWidth;
        this.perValueW = expandWidth*density;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int width = (int) (originalWidth +(perValueW*interpolatedTime));
        view.getLayoutParams().width = width;
        view.requestLayout();
        Log.d("TAG", "applyTransformation: perValueW = "+perValueW+" interpolatedTime = "+interpolatedTime+
                " originalWidth = "+originalWidth+" recentWidth = "+width);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
