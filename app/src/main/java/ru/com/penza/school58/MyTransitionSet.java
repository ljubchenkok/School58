package ru.com.penza.school58;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by Константин on 27.03.2018.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyTransitionSet extends TransitionSet {

    public MyTransitionSet() {
        init();
    }


    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition (new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}