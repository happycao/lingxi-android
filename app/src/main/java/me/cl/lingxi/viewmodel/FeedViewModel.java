package me.cl.lingxi.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.cl.library.model.TipMessage;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.Relevant;
import okhttp3.Call;

/**
 * @author : happyc
 * time    : 2021/05/10
 * desc    :
 * version : 1.0
 */
public class FeedViewModel extends ViewModel {

    public final MutableLiveData<TipMessage> mTipMessage;
    public final MutableLiveData<PageInfo<Feed>> mFeedPage;
    public final MutableLiveData<Feed> mFeed;
    public final MutableLiveData<Integer> mFeedComment;
    public final MutableLiveData<PageInfo<Comment>> mCommentPage;
    public final MutableLiveData<PageInfo<Relevant>> mRelevantPage;

    public FeedViewModel() {
        mTipMessage = new MutableLiveData<>();
        mFeedPage = new MutableLiveData<>();
        mFeed = new MutableLiveData<>();
        mFeedComment = new MutableLiveData<>();
        mCommentPage = new MutableLiveData<>();
        mRelevantPage = new MutableLiveData<>();
    }

    /**
     * 获取动态
     * @param pageNum 页码
     * @param pageSize 页容量
     */
    public void doPageFeed(int pageNum, int pageSize) {
        this.doPageFeed(pageNum, pageSize, null);
    }

    /**
     * 获取动态
     * @param pageNum 页码
     * @param pageSize 页容量
     * @param searchUserId 用户id
     */
    public void doPageFeed(int pageNum, int pageSize, String searchUserId) {
        OkUtil.post()
                .url(Api.pageFeed)
                .addParam("searchUserId", searchUserId)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<Feed>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Feed>> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mFeedPage.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.resId(R.string.toast_get_feed_error));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_get_feed_error));
                    }
                });
    }

    /**
     * 保存动态
     * @param feedInfo 动态信息
     * @param photos 动态图片
     */
    public void saveFeed(String feedInfo, List<String> photos) {
        OkUtil.post()
                .url(Api.saveFeed)
                .addParam("feedInfo", feedInfo)
                .addUrlParams("photoList", photos)
                .execute(new ResultCallback<Result<Feed>>() {
                    @Override
                    public void onSuccess(Result<Feed> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mFeed.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.str("发布失败"));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.str("发布失败"));
                    }
                });
    }

    /**
     * 点赞
     * @param feed 动态
     */
    public void doLike(Feed feed) {
        OkUtil.post()
                .url(Api.saveAction)
                .addParam("feedId", feed.getId())
                .addParam("type", "0")
                .execute(new ResultCallback<Result<Object>>() {
                    @Override
                    public void onSuccess(Result<Object> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mFeed.postValue(feed);
                        } else {
                            mTipMessage.postValue(TipMessage.str("点赞失败"));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.str("点赞失败"));
                    }
                });
    }

    public void viewFeed(String feedId) {
        OkUtil.post()
                .url(Api.viewFeed)
                .addParam("id", feedId)
                .execute();
    }

    /**
     * 添加评论
     */
    public void addEvaluate(String feedId, String toUid, String comment) {
        OkUtil.post()
                .url(Api.saveComment)
                .addParam("feedId", feedId)
                .addParam("toUserId", toUid)
                .addParam("commentInfo", comment)
                .addParam("type", "0")
                .execute(new ResultCallback<Result<Object>>() {
                    @Override
                    public void onSuccess(Result<Object> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mFeedComment.postValue(0);
                        } else {
                            mTipMessage.postValue(TipMessage.str("评论失败"));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.str("评论失败"));
                    }
                });
    }

    /**
     * 添加回复
     */
    public void addReply(String feedId, String commentId, String toUid, String reply) {
        OkUtil.post()
                .url(Api.saveComment)
                .addParam("feedId", feedId)
                .addParam("commentId", commentId)
                .addParam("toUserId", toUid)
                .addParam("commentInfo", reply)
                .addParam("type", "1")
                .execute(new ResultCallback<Result<Object>>() {
                    @Override
                    public void onSuccess(Result<Object> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mFeedComment.postValue(1);
                        } else {
                            mTipMessage.postValue(TipMessage.str("回复失败"));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.str("回复失败"));
                    }
                });
    }

    /**
     * 获取评论数据
     */
    public void doPageComment(int pageNum, int pageSize, String feedId) {
        OkUtil.post()
                .url(Api.pageComment)
                .addParam("feedId", feedId)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<Comment>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Comment>> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mCommentPage.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.resId(R.string.toast_get_feed_error));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.resId(R.string.toast_get_feed_error));
                    }
                });
    }

    /**
     * 我的回复
     * @param pageNum 页码
     * @param pageSize 页容量
     * @param loadingDialog 加载动画
     */
    public void pageMineReply(int pageNum, int pageSize, LoadingDialog loadingDialog) {
        this.doRelevant(Api.mineReply, pageNum, pageSize, loadingDialog);
    }

    /**
     * 与我相关
     * @param pageNum 页码
     * @param pageSize 页容量
     * @param loadingDialog 加载动画
     */
    public void pageRelevant(int pageNum, int pageSize, LoadingDialog loadingDialog) {
        this.doRelevant(Api.relevant, pageNum, pageSize, loadingDialog);
    }

    private void doRelevant(String url, int pageNum, int pageSize, LoadingDialog loadingDialog) {
        OkUtil.post()
                .url(url)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .setLoadDelay()
                .setProgressDialog(loadingDialog)
                .execute(new ResultCallback<Result<PageInfo<Relevant>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Relevant>> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mRelevantPage.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.str("加载失败，请重试"));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.str("加载失败，请重试"));
                    }
                });
    }

    /**
     * 更新未读条数
     */
    public void updateUnread() {
        OkUtil.post()
                .url(Api.updateUnread)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        Constants.isRead = true;
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }
}
