package me.cl.lingxi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.model.TipMessage;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.Topic;
import okhttp3.Call;

/**
 * @author : happyc
 * time    : 2021/05/10
 * desc    :
 * version : 1.0
 */
public class TopicViewModel extends ViewModel {

    private final MutableLiveData<TipMessage> mTipMessage;
    private final MutableLiveData<PageInfo<Topic>> mTopics;

    public TopicViewModel() {
        mTipMessage = new MutableLiveData<>();
        mTopics = new MutableLiveData<>();
    }

    public LiveData<TipMessage> getTipMessage() {
        return mTipMessage;
    }

    public LiveData<PageInfo<Topic>> getTopics() {
        return mTopics;
    }

    /**
     * 查询话题
     * @param queryName 搜索话题
     * @param pageNum 页码
     * @param pageSize 页容量
     */
    public void queryTopic(String queryName, int pageNum, int pageSize) {
        OkUtil.post()
                .url(Api.queryTopic)
                .addParam("name", queryName)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<Topic>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Topic>> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mTopics.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.resId(R.string.toast_get_topic_error));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_get_topic_error));
                    }
                });
    }
}
