package me.cl.lingxi.module.feed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.PhotoSelAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.common.util.ImageUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.module.main.MainActivity;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

public class PublishActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.feed_info)
    AppCompatEditText mMoodInfo;
    @BindView(R.id.iv_submit)
    ImageView mIvSubmit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PhotoSelAdapter mPhotoSelAdapter;
    private List<String> mPhotos = new ArrayList<>();

    private String mUid;
    private String mInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle("发布新动态")
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        mUid = SPUtil.build().getString(Constants.SP_USER_ID);
        setLoading("发布中...");
        initRecycleView();
    }

    private void initRecycleView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(PublishActivity.this, 3));
        mPhotoSelAdapter = new PhotoSelAdapter(mPhotos);
        mRecyclerView.setAdapter(mPhotoSelAdapter);
        mPhotoSelAdapter.setOnItemClickListener(new PhotoSelAdapter.OnItemClickListener() {
            @Override
            public void onPhotoClick(int position) {
                if (mPhotos.get(position).equals(PhotoSelAdapter.mPhotoAdd)) {
                    mPhotos.remove(position);
                    PhotoPicker.builder()
                            .setPhotoCount(6)
                            .setShowCamera(true)
                            .setShowGif(true)
                            .setSelected((ArrayList<String>) mPhotos)
                            .setPreviewEnabled(false)
                            .start(PublishActivity.this, PhotoPicker.REQUEST_CODE);
                } else {
                    mPhotos.remove(PhotoSelAdapter.mPhotoAdd);
                    PhotoPreview.builder()
                            .setPhotos((ArrayList<String>) mPhotos)
                            .setCurrentItem(position)
                            .setShowDeleteButton(true)
                            .start(PublishActivity.this);
                }
            }

            @Override
            public void onDelete(int position) {
                mPhotos.remove(position);
                mPhotoSelAdapter.setPhotos(mPhotos);
            }
        });
    }

    @OnClick(R.id.iv_submit)
    public void onClick() {
        mInfo = mMoodInfo.getText().toString().trim();
        if (TextUtils.isEmpty(mInfo)) {
            showToast("好歹写点什么吧！");
            return;
        }
        if (mPhotos.size() <= 1) {
            postSaveFeed(mPhotos);
        } else {
            postUpload(mPhotos);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                switch (requestCode) {
                    case PhotoPicker.REQUEST_CODE:
                    case PhotoPreview.REQUEST_CODE:
                        mPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        break;
                }
            }
        }
        mPhotoSelAdapter.setPhotos(mPhotos);
    }

    // 上传图片
    private void postUpload(List<String> photos) {
        removePhotoAdd(photos);

        // 压缩图片
        photos = ImageUtil.compressorImage(this, photos);

        OkUtil.post()
                .url(Api.uploadFeedImage)
                .addFiles("file", ImageUtil.pathToImageFile(photos))
                .execute(new ResultCallback<Result<List<String>>>() {
                    @Override
                    public void onSuccess(Result<List<String>> response) {
                        String code = response.getCode();
                        if ("00100".equals(code)) {
                            showToast(response.getMsg());
                            addPhotoAdd(mPhotos);
                            return;
                        }
                        if (!"00000".equals(code)) {
                            showToast("图片上传失败");
                            addPhotoAdd(mPhotos);
                            return;
                        }
                        // 发送动态
                        postSaveFeed(response.getData());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("图片上传失败");
                        addPhotoAdd(mPhotos);
                    }
                });
    }

    // 发布动态
    private void postSaveFeed(List<String> uploadImg) {
        removePhotoAdd(uploadImg);
        OkUtil.post()
                .url(Api.saveFeed)
                .addParam("userId", mUid)
                .addParam("feedInfo", mInfo)
                .addUrlParams("photoList", uploadImg)
                .execute(new ResultCallback<Result<Feed>>() {
                    @Override
                    public void onSuccess(Result<Feed> response) {
                        dismissLoading();
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast("发布失败");
                            addPhotoAdd(mPhotos);
                            return;
                        }
                        mMoodInfo.setText(null);
                        showToast("发布成功");
                        onBackPressed();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        dismissLoading();
                        showToast("发布失败");
                        addPhotoAdd(mPhotos);
                    }
                });
    }

    // 添加添加图片按钮
    private void addPhotoAdd(List<String> photList) {
        if (!photList.contains(PhotoSelAdapter.mPhotoAdd)) {
            photList.add(PhotoSelAdapter.mPhotoAdd);
        }
    }

    // 去除添加图片按钮
    private void removePhotoAdd(List<String> photList) {
        photList.remove(PhotoSelAdapter.mPhotoAdd);
    }

    @Override
    public void onBackPressed() {
        // 此处监听回退，通知首页刷新
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, R.id.navigation_camera);
        intent.putExtras(bundle);
        setResult(Constants.ACTIVITY_PUBLISH, intent);
        finish();
    }
}
