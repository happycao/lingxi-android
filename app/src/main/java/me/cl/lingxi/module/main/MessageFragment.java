package me.cl.lingxi.module.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.ViewPagerAdapter;

public class MessageFragment extends Fragment{

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private String[] tabNamArray = {"写给未来", "飞鸽传书"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }


    private void init() {
        final ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        addFragment(mPagerAdapter);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setAdapter(mPagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });
    }

    private void addFragment(ViewPagerAdapter mPagerAdapter) {
        MessageChildFragment futureFragment = MessageChildFragment.newInstance("很久很久以前没有留下信件\n现在去写给未来");
        MessageChildFragment newsFragment = MessageChildFragment.newInstance("飞鸽传书功能准备当中\n" +
                "有好的建议可点击下方按钮↓");
        mPagerAdapter.addFragment(futureFragment, tabNamArray[0]);
        mPagerAdapter.addFragment(newsFragment, tabNamArray[1]);
    }

}
