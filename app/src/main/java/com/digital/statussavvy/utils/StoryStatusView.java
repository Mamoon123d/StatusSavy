package com.digital.statussavvy.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.digital.statussavvy.R;
import com.digital.statussavvy.insta.StatusList;

import java.util.ArrayList;
import java.util.List;

public class StoryStatusView extends LinearLayout {
    private static final int MAX_PROGRESS = 100;
    private static final int SPACE_BETWEEN_PROGRESS_BARS = 5;
    public static int current;
    private final List<ObjectAnimator> animators = new ArrayList();
    private final List<ProgressBar> progressBars = new ArrayList();
    private int storiesCount = -1;
    /* access modifiers changed from: private */
    public UserInteractionListener userInteractionListener;

    public interface UserInteractionListener {
        void onImageStatusComplete();
    }

    public StoryStatusView(Context context) {
        super(context);
    }

    public StoryStatusView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StoryStatusView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void bindViews() {
        removeAllViews();
        int i = 0;
        while (i < this.storiesCount) {
            ProgressBar createProgressBar = createProgressBar();
            createProgressBar.setMax(100);
            this.progressBars.add(createProgressBar);
            addView(createProgressBar);
            i++;
            if (i < this.storiesCount) {
                addView(createSpace());
            }
        }
    }

    private ProgressBar createProgressBar() {
        ProgressBar progressBar = new ProgressBar(getContext(), (AttributeSet) null, 16842872);
        progressBar.setLayoutParams(new LayoutParams(0, -2, 1.0f));
        progressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress_bg));
        return progressBar;
    }

    private View createSpace() {
        View view = new View(getContext());
        view.setLayoutParams(new LayoutParams(5, -2));
        return view;
    }

    public ProgressBar getProgressBar() {
        try {
            return this.progressBars.get(current);
        } catch (IndexOutOfBoundsException unused) {
            return this.progressBars.get(0);
        }
    }

    public void setStoriesCount(int i) {
        current = 0;
        this.storiesCount = i;
        bindViews();
    }

    public void setStoryDuration(long j, ArrayList<StatusList> arrayList) {
        for (int i = 0; i < this.progressBars.size(); i++) {
            arrayList.get(i).setObjectAnimator(createAnimator(i, j));
        }
    }

    public void setUserInteractionListener(UserInteractionListener userInteractionListener2) {
        this.userInteractionListener = userInteractionListener2;
    }

    public void destroy() {
        for (ObjectAnimator next : this.animators) {
            next.removeAllListeners();
            next.cancel();
        }
    }

    private ObjectAnimator createAnimator(final int i, final long j) {
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this.progressBars.get(i), NotificationCompat.CATEGORY_PROGRESS, new int[]{100});
        ofInt.setInterpolator(new LinearInterpolator());
        ofInt.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                StoryStatusView.current = i;
                animator.setDuration(j);
            }

            public void onAnimationEnd(Animator animator) {
                if (StoryStatusView.this.userInteractionListener != null) {
                    StoryStatusView.this.userInteractionListener.onImageStatusComplete();
                }
            }
        });
        return ofInt;
    }
}
