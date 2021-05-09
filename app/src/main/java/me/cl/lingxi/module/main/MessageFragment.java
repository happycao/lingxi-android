package me.cl.lingxi.module.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import me.cl.lingxi.adapter.ViewPagerAdapter;
import me.cl.lingxi.databinding.MessageFragmentBinding;

public class MessageFragment extends Fragment{

    private MessageFragmentBinding mFragmentBinding;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private final String[] tabNamArray = {"未来日记", "飞鸽传书"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentBinding = MessageFragmentBinding.inflate(inflater, container, false);
        init();
        return mFragmentBinding.getRoot();
    }


    private void init() {
        mTabLayout = mFragmentBinding.includeTabLayout.tabLayout;
        mViewPager = mFragmentBinding.viewPager;

        final ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        addFragment(mPagerAdapter);
        mViewPager.post(() -> {
            mViewPager.setAdapter(mPagerAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
        });
    }

    private void addFragment(ViewPagerAdapter mPagerAdapter) {
        FutureFragment futureFragment = FutureFragment.newInstance("new");
        MessageChildFragment futureFragmentOld = MessageChildFragment.newInstance("很久很久以前没有留下信件\n现在去写给未来");
        MessageChildFragment newsFragment = MessageChildFragment.newInstance("飞鸽传书功能准备当中\n" +
                "有好的建议可点击下方按钮↓");
        mPagerAdapter.addFragment(futureFragment, tabNamArray[0]);
        mPagerAdapter.addFragment(newsFragment, tabNamArray[1]);
    }

}
