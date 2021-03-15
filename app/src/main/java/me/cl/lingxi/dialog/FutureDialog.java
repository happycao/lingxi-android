package me.cl.lingxi.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.util.ToastUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.Utils;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2018/10/12
 * desc   : 写给未来设置
 * version: 1.0
 */
public class FutureDialog extends DialogFragment {

    @BindView(R.id.future_type)
    RadioGroup mFutureType;
    @BindView(R.id.future_type_app)
    RadioButton mFutureTypeApp;
    @BindView(R.id.future_type_mail)
    RadioButton mFutureTypeMail;
    @BindView(R.id.future_mail)
    AppCompatEditText mFutureMail;
    @BindView(R.id.future_time_group)
    RadioGroup mFutureTimeGroup;
    @BindView(R.id.future_time_one)
    RadioButton mFutureTimeOne;
    @BindView(R.id.future_time_two)
    RadioButton mFutureTimeTwo;
    @BindView(R.id.future_time_three)
    RadioButton mFutureTimeThree;
    @BindView(R.id.future_time_other)
    RadioButton mFutureTimeOther;
    @BindView(R.id.future_time_edit)
    LinearLayout mFutureTimeEdit;
    @BindView(R.id.future_time_start)
    AppCompatEditText mFutureTimeStart;
    @BindView(R.id.future_time_end)
    AppCompatEditText mFutureTimeEnd;
    @BindView(R.id.future_cancel)
    Button mFutureCancel;
    @BindView(R.id.future_ok)
    Button mFutureOk;

    /**
     * 展示类型，0-APP\1-MAIL
     */
    private int type = 0;
    private String mail;
    /**
     * 时间区间编辑模式
     */
    private boolean isEditTime = false;
    private Integer startTime = 0;
    private Integer endTime = 6;

    public interface OnSendClickListener {
        void onSend(int type, String mail, Integer startTime, Integer endTime);
    }

    private OnSendClickListener mOnSendClickListener;

    public void setOnSendClickListener(OnSendClickListener listener) {
        mOnSendClickListener = listener;
    }

    public static FutureDialog newInstance() {
        Bundle args = new Bundle();
        FutureDialog fragment = new FutureDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.future_dialog, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        // 传达类型
        mFutureType.check(R.id.future_type_app);
        mFutureType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.future_type_app:
                        type = 0;
                        mFutureMail.setVisibility(View.GONE);
                        break;
                    case R.id.future_type_mail:
                        type = 1;
                        mFutureMail.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        // 传达时间
        mFutureTimeGroup.check(R.id.future_time_one);
        mFutureTimeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mFutureTimeEdit.setVisibility(View.GONE);
                isEditTime = false;
                switch (checkedId) {
                    case R.id.future_time_one:
                        startTime = 0;
                        endTime = 6;
                        break;
                    case R.id.future_time_two:
                        startTime = 6;
                        endTime = 12;
                        break;
                    case R.id.future_time_three:
                        startTime = 12;
                        endTime = 24;
                        break;
                    case R.id.future_time_other:
                        mFutureTimeEdit.setVisibility(View.VISIBLE);
                        isEditTime = true;
                        break;
                }
            }
        });
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.future_cancel, R.id.future_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.future_cancel:
                dismiss();
                break;
            case R.id.future_ok:
                String msg = verifyParam();
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil.showToast(getContext(), msg);
                    return;
                }
                if (mOnSendClickListener != null) {
                    mOnSendClickListener.onSend(type, mail, startTime, endTime);
                }
                dismiss();
                break;
        }
    }

    /**
     * 参数验证
     */
    private String verifyParam() {
        // 邮箱
        if (type == 1) {
            mail = mFutureMail.getText().toString().trim();
            if (TextUtils.isEmpty(mail) || !Utils.isMail(mail)) {
                return "邮箱地址错误";
            }
        }

        // 时间区
        if (isEditTime) {
            String startStr = mFutureTimeStart.getText().toString();
            String endStr = mFutureTimeEnd.getText().toString();
            if (TextUtils.isEmpty(startStr) || TextUtils.isEmpty(endStr)) {
                return "需要填写时间区间哟";
            } else {
                startTime = Integer.valueOf(startStr);
                endTime = Integer.valueOf(endStr);
            }

            if (TextUtils.isEmpty(startStr)) {
                startTime = Integer.valueOf(endStr);
                endTime = Integer.valueOf(endStr);
            }

            if (TextUtils.isEmpty(endStr)) {
                startTime = Integer.valueOf(startStr);
                endTime = Integer.valueOf(startStr);
            }

            int tmp;
            if (startTime > endTime) {
                tmp = startTime;
                startTime = endTime;
                endTime = tmp;
            }

            if (startTime.equals(endTime)) {
                endTime = null;
            }
        }

        return "";
    }
}