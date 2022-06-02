package me.cl.lingxi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.library.model.TipMessage;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.entity.Future;
import me.cl.lingxi.entity.PageInfo;
import okhttp3.Call;

/**
 * @author : happyc
 * time    : 2021/05/10
 * desc    :
 * version : 1.0
 */
public class FutureViewModel extends ViewModel {

    public final MutableLiveData<TipMessage> mTipMessage;
    public final MutableLiveData<PageInfo<Future>> mFutures;
    public final MutableLiveData<Boolean> mSuccess;

    public FutureViewModel() {
        mTipMessage = new MutableLiveData<>();
        mFutures = new MutableLiveData<>();
        mSuccess = new MutableLiveData<>();
    }

    public LiveData<TipMessage> getTipMessage() {
        return mTipMessage;
    }

    public LiveData<PageInfo<Future>> getFutures() {
        return mFutures;
    }

    public LiveData<Boolean> getSuccess() {
        return mSuccess;
    }

    /**
     * 查询话题
     * @param pageNum 页码
     * @param pageSize 页容量
     */
    public void doPageFuture(int pageNum, int pageSize) {
        OkUtil.post()
                .url(Api.pageFuture)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<Future>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Future>> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mFutures.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.resId(R.string.toast_get_future_error));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_get_future_error));
                    }
                });
    }

    /**
     * 提交保存信息
     */
    public void saveFuture(int type, String mail, String futureInfo, Integer startNum, Integer endNum) {
        OkUtil.post()
                .url(Api.saveFuture)
                .addParam("type", type)
                .addParam("mail", mail)
                .addParam("futureInfo", futureInfo)
                .addParam("startNum", startNum)
                .addParam("endNum", endNum)
                .execute(new ResultCallback<Result<Object>>() {
                    @Override
                    public void onSuccess(Result<Object> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mSuccess.postValue(true);
                        } else {
                            mTipMessage.postValue(TipMessage.str("信件偏离预定轨道，请调整重试"));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.str("信件偏离预定轨道，请调整重试"));
                    }
                });
    }
}
