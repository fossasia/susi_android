package org.fossasia.susi.ai.helper;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by mejariamol on 10/17/2016.
 */

public class CustomBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {

    public CustomBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        params.bottomMargin = parent.getHeight() - (int) dependency.getY();
        child.setLayoutParams(params);
        return false;
    }
}
