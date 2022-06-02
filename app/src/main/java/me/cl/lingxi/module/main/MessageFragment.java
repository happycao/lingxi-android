package me.cl.lingxi.module.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import me.cl.lingxi.adapter.ViewPagerAdapter;
import me.cl.lingxi.databinding.MessageFragmentBinding;

public class MessageFragment extends Fragment{

    private MessageFragmentBinding mBinding;

    private final String[] tabNamArray = {"未来日记", "飞鸽传书"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = MessageFragmentBinding.inflate(inflater, container, false);
        init();
        return mBinding.getRoot();
    }


    private void init() {
        final ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        addFragment(mPagerAdapter);
        mBinding.viewPager.post(() -> {
            mBinding.viewPager.setAdapter(mPagerAdapter);
            mBinding.includeTab.tabLayout.setupWithViewPager(mBinding.viewPager);
        });
    }

    private void addFragment(ViewPagerAdapter mPagerAdapter) {
        FutureFragment futureFragment = FutureFragment.newInstance("new");
        MessageChildFragment newsFragment = MessageChildFragment.newInstance("飞鸽传书功能准备当中\n" +
                "有好的建议可点击下方按钮↓");
        mPagerAdapter.addFragment(futureFragment, tabNamArray[0]);
        mPagerAdapter.addFragment(newsFragment, tabNamArray[1]);
    }

}
