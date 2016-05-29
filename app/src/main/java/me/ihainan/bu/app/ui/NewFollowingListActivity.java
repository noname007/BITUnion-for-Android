package me.ihainan.bu.app.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import me.ihainan.bu.app.R;
import me.ihainan.bu.app.ui.assist.SwipeActivity;
import me.ihainan.bu.app.ui.fragment.FollowingListFragment;

public class NewFollowingListActivity extends SwipeActivity {
    // Tags
    private final static String TAG = NewFollowingListActivity.class.getSimpleName();

    // UI references
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_following_list);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle(getString(R.string.action_manage_focus));

        // FrameLayout
        Fragment fragment = new FollowingListFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

        // Swipe
        setSwipeAnyWhere(false);
    }
}
