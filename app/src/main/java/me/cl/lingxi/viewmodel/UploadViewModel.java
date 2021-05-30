package me.cl.lingxi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.model.TipMessage;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.result.ResultConstant;
import me.cl.lingxi.common.util.ImageUtil;
import okhttp3.Call;

/**
 * @author : happyc
 * time    : 2021/05/10
 * desc    :
 * version : 1.0
 */
public class UploadViewModel extends ViewModel {

    private final MutableLiveData<TipMessage> mTipMessage;
    private final MutableLiveData<List<String>> mPhotos;
    private final MutableLiveData<String> mPhoto;

    public UploadViewModel() {
        mTipMessage = new MutableLiveData<>();
        mPhotos = new MutableLiveData<>();
        mPhoto = new MutableLiveData<>();
    }

    public LiveData<TipMessage> getTipMessage() {
        return mTipMessage;
    }

    public LiveData<List<String>> getPhotos() {
        return mPhotos;
    }

    public LiveData<String> getPhoto() {
        return mPhoto;
    }

    /**
     * 上传用户头像
     */
    public void uploadUserImage(File file) {
        OkUtil.post()
                .url(Api.uploadUserImage)
                .addFile("file", file)
                .execute(new ResultCallback<Result<List<String>>>() {
                    @Override
                    public void onSuccess(Result<List<String>> response) {
                        String code = response.getCode();
                        List<String> data = response.getData();
                        if (ResultConstant.CODE_SUCCESS.equals(code) && data != null && !data.isEmpty()) {
                            mPhoto.postValue(data.get(0));
                        } else {
                            mPhoto.postValue(null);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mPhoto.postValue(null);
                    }
                });
    }

    /**
     * 上传动态图片
     * @param photos 图片地址
     */
    public void uploadFeedImage(List<String> photos) {
        OkUtil.post()
                .url(Api.uploadFeedImage)
                .addFiles("file", ImageUtil.pathToImageFile(photos))
                .execute(new ResultCallback<Result<List<String>>>() {
                    @Override
                    public void onSuccess(Result<List<String>> response) {
                        String code = response.getCode();
                        if (ResultConstant.CODE_SUCCESS.equals(code)) {
                            mPhotos.postValue(response.getData());
                        } else {
                            mTipMessage.postValue(TipMessage.str("00100".equals(code) ? response.getMsg() : "图片上传失败"));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mTipMessage.postValue(TipMessage.str("图片上传失败"));
                    }
                });
    }
}
