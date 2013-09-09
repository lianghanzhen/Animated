package com.example.Animated;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity {

    private AnimatedBackgroundLinearLayout mContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContainer = (AnimatedBackgroundLinearLayout) findViewById(R.id.container);
        mContainer.setAdapter(new TabAdapter(this));
        mContainer.setCurrentItem(0);
        mContainer.setOnTabSelectedListener(new AnimatedBackgroundLinearLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                Toast.makeText(MyActivity.this, "Tab Selected: " + index, Toast.LENGTH_SHORT).show();
            }
        }).setOnTabReselectedListener(new AnimatedBackgroundLinearLayout.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int index) {
                Toast.makeText(MyActivity.this, "Tab Reselected: " + index, Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class TabAdapter implements AnimatedBackgroundLinearLayout.AnimatedTabAdapter {

        private static final CharSequence[] TITLES = {"首页", "游戏", "应用", "搜索", "宝箱"};
        private static final int[] ICONS = {R.drawable.menu_homepage_selector, R.drawable.menu_game_app_selector, R.drawable.menu_general_app_selector, R.drawable.menu_search_selector, R.drawable.menu_more_selector};

        private final Context mContext;

        TabAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public View getView(int position) {
            TextView view = new TextView(mContext);
            view.setGravity(Gravity.CENTER_HORIZONTAL);
            view.setPadding(5, 5, 5, 5);
            view.setTextColor(mContext.getResources().getColorStateList(R.drawable.tab_text_color_selector));
            view.setCompoundDrawablesWithIntrinsicBounds(0, ICONS[position], 0, 0);
            view.setText(TITLES[position]);
            return view;
        }
    }

}
