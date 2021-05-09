package me.cl.lingxi.module.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import me.cl.library.base.BaseFragment;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.MessageChildFragmentBinding;
import me.cl.lingxi.module.future.FutureActivity;

public class MessageChildFragment extends BaseFragment implements View.OnClickListener {

    private MessageChildFragmentBinding mFragmentBinding;

    private Toolbar mToolbar;
    private TextView mMsg;
    private Button mSend;

    private static final String NEWS_TYPE = "news_type";
    private boolean flag = false;
    private int tag = 0;
    private static final String mF = "飞鸽传书功能准备当中\n" +
            "可以想想，在这个即时通讯的年代\n" +
            "你的消息需要时间才能传递\n" +
            "更有被劫的可能\n" +
            "那被打劫了该怎么办呢？";

    private String mNewsType;

    public MessageChildFragment() {

    }

    public static MessageChildFragment newInstance(String newsType) {
        MessageChildFragment fragment = new MessageChildFragment();
        Bundle args = new Bundle();
        args.putString(NEWS_TYPE, newsType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsType = getArguments().getString(NEWS_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentBinding = MessageChildFragmentBinding.inflate(inflater, container, false);
        init();
        return mFragmentBinding.getRoot();
    }

    private void init() {
        mToolbar = mFragmentBinding.includeToolbar.toolbar;
        mMsg = mFragmentBinding.msg;
        mSend = mFragmentBinding.send;

        mToolbar.setVisibility(View.GONE);
        if (mNewsType.contains("飞鸽传书")) {
            flag = true;
        } else {
            mSend.setText("编写");
        }
        mSend.setVisibility(View.VISIBLE);
        mMsg.setText(mNewsType);

        mMsg.setOnClickListener(this);
        mSend.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg:
                if (flag) {
                    tag++;
                    if (tag == 7) {
                        tag = 0;
                        mMsg.setText(mF);
                    } else {
                        mMsg.setText(mNewsType);
                    }
                }
                break;
            case R.id.send:
                if (flag) {
                    boolean isWpa = Utils.wpaQQ(requireActivity(), "986417980");
                    if (!isWpa) {
                        showToast("未安装手Q或安装的版本不支持");
                    }
                } else {
                    gotoFuture();
                }
                break;
        }
    }

    private void gotoFuture() {
        Intent intent = new Intent(getActivity(), FutureActivity.class);
        startActivity(intent);
    }
}
