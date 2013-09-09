package com.example.Animated;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class AnimatedBackgroundLinearLayout extends LinearLayout {

    private static final int DEFAULT_COLOR = 0xFF309AD8;
    private static final int DEFAULT_DURATION = 800;
    private static final int INVALID_INDEX = -1;

    private final OnClickListener mTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            final int newSelected = (Integer) view.getTag(R.id.tab_index);
            setCurrentItem(newSelected);
        }
    };

    private AnimatedTabAdapter mAdapter;
    private int mSelectedIndex = INVALID_INDEX;

    private OnTabReselectedListener mOnTabReselectedListener;
    private OnTabSelectedListener mOnTabSelectedListener;

    private final Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBackgroundColor = DEFAULT_COLOR;
    private int mBackgroundDuration = DEFAULT_DURATION;

    private final Scroller mScroller = new Scroller(getContext());
    private int mCurrentX;

    public AnimatedBackgroundLinearLayout(Context context) {
        super(context);
    }

    public AnimatedBackgroundLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimatedBackgroundLinearLayout);
        mBackgroundColor = typedArray.getColor(R.styleable.AnimatedBackgroundLinearLayout_animatedBackground, DEFAULT_COLOR);
        mBackgroundDuration = typedArray.getInt(R.styleable.AnimatedBackgroundLinearLayout_animatedDuration, DEFAULT_DURATION);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int count = getChildCount();
        if (count == 0 || mSelectedIndex <= INVALID_INDEX) {
            return;
        }

        mBackgroundPaint.setColor(mBackgroundColor);
        View child = getChildAt(mSelectedIndex);
        canvas.drawRect(mCurrentX, 0, mCurrentX + child.getWidth(), getHeight(), mBackgroundPaint);
    }

    public void setAdapter(AnimatedTabAdapter adapter) {
        if (adapter == mAdapter) {
            return;
        }
        mAdapter = adapter;
        setWillNotDraw(mAdapter == null);

        rebuildViews();
    }

    private void rebuildViews() {
        removeAllViews();
        addTabs();
    }

    private void addTabs() {
        if (mAdapter == null) {
            return;
        }

        final int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View tabView = mAdapter.getView(i);
            if (tabView == null) {
                throw new NullPointerException("getView() cannot be null.");
            }
            tabView.setTag(R.id.tab_index, i);
            tabView.setOnClickListener(mTabClickListener);
            addView(tabView, new LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 1));
        }

        if (mSelectedIndex > count) {
            mSelectedIndex = count - 1;
        }
        if (mSelectedIndex > INVALID_INDEX) {
            setCurrentItem(mSelectedIndex);
        }
    }

    public void setCurrentItem(final int index) {
        final int count = getChildCount();
        final int oldIndex = mSelectedIndex;
        mSelectedIndex = index >= count ? count - 1 : index < 0 ? 0 : index;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final boolean isSelected = i == mSelectedIndex;
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(oldIndex, index);
                performListeners(oldIndex, mSelectedIndex);
            }
        }
    }

    private void performListeners(int oldSelected, int newSelected) {
        if (oldSelected == newSelected && mOnTabReselectedListener != null) {
            mOnTabReselectedListener.onTabReselected(newSelected);
        } else if (oldSelected != newSelected && mOnTabSelectedListener != null) {
            mOnTabSelectedListener.onTabSelected(newSelected);
        }
    }

    private void animateToTab(final int oldPosition, final int position) {
        if (oldPosition > INVALID_INDEX && position > INVALID_INDEX) {
            final int startX = getChildAt(oldPosition).getLeft();
            int dx = getChildAt(position).getLeft() - startX;
            mScroller.startScroll(startX, 0, dx, 0, mBackgroundDuration);
            postAnimationInvalidate();
        }
    }

    private void postAnimationInvalidate() {
        if (mScroller.computeScrollOffset()) {
            final int currentX = mScroller.getCurrX();
            if (mCurrentX != currentX) {
                mCurrentX = currentX;
                invalidate();
            }
            if (mCurrentX != mScroller.getFinalX()) {
                postDelayed(mScrollerRunnable, 5);
                return;
            }
        }
        mScroller.abortAnimation();
    }

    private final Runnable mScrollerRunnable = new Runnable() {
        @Override
        public void run() {
            postAnimationInvalidate();
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mScrollerRunnable);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollerRunnable != null) {
            removeCallbacks(mScrollerRunnable);
        }
    }

    public int getCurrentItem() {
        return mSelectedIndex;
    }

    public AnimatedBackgroundLinearLayout setOnTabReselectedListener(OnTabReselectedListener listener) {
        mOnTabReselectedListener = listener;
        return this;
    }

    public AnimatedBackgroundLinearLayout setOnTabSelectedListener(OnTabSelectedListener listener) {
        mOnTabSelectedListener = listener;
        return this;
    }

    public interface AnimatedTabAdapter {

        int getCount();

        View getView(int position);

    }

    public interface OnTabReselectedListener {
        void onTabReselected(int index);
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int index);
    }

}
