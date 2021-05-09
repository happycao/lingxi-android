package me.cl.lingxi.module.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.databinding.PublishActivityBinding;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.module.main.MainActivity;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

public class PublishActivity extends BaseActivity implements View.OnClickListener {

    private PublishActivityBinding mActivityBinding;

    private AppCompatEditText mEtFeedInfo;
    private RecyclerView mRecyclerView;

    private PhotoSelAdapter mPhotoSelAdapter;
    private List<String> mPhotos = new ArrayList<>();

    private String mInfo = "";
    private final boolean showAdd = false;

    // 话题与艾特相关
    private final StringBuilder mFeedInfoSb = new StringBuilder();
    private final List<String> mActionList = new ArrayList<>();
    private final List<ForegroundColorSpan> mColorSpans = new ArrayList<>();
    private final boolean isInAfter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = PublishActivityBinding.inflate(getLayoutInflater());
        setContentView(mActivityBinding.getRoot());
        init();
    }

    private void init() {
        Toolbar toolbar = mActivityBinding.includeToolbar.toolbar;
        mEtFeedInfo = mActivityBinding.feedInfo;
        mRecyclerView = mActivityBinding.recyclerView;

        mActivityBinding.ivCamera.setOnClickListener(this);
        mActivityBinding.ivEit.setOnClickListener(this);
        mActivityBinding.ivTopic.setOnClickListener(this);
        mActivityBinding.ivLink.setOnClickListener(this);

        ToolbarUtil.init(toolbar, this)
                .setTitle("发布新动态")
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .setMenu(R.menu.send_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_send:
                                mInfo = mEtFeedInfo.getText().toString().trim();
                                if (TextUtils.isEmpty(mInfo)) {
                                    showToast("好歹写点什么吧！");
                                    break;
                                }
                                if (mPhotos.isEmpty()) {
                                    postSaveFeed(mPhotos);
                                } else {
                                    postUpload(mPhotos);
                                }
                                break;
                        }
                        return false;
                    }
                })
                .build();

        setLoading("发布中...");
        initTextChangeListener();
        initRecycleView();
    }

    private void initTextChangeListener() {
        mEtFeedInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                // 查找话题和@
                String content = s.toString();
                mActionList.clear();
                mActionList.addAll(Utils.findAction(content));
                // 首先移除之前设置的colorSpan
                Editable editable = mEtFeedInfo.getText();
                if (editable == null) {
                    return;
                }
                for (ForegroundColorSpan mColorSpan : mColorSpans) {
                    editable.removeSpan(mColorSpan);
                }
                mColorSpans.clear();
                // 设置前景色colorSpan
                int findPos = 0;
                for (String topic : mActionList) {
                    findPos = content.indexOf(topic, findPos);
                    if (findPos != -1) {
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.rgb(3, 169, 244));
                        editable.setSpan(colorSpan, findPos, findPos = findPos + topic.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mColorSpans.add(colorSpan);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtFeedInfo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && isInAfter) {
                    int selectionStart = mEtFeedInfo.getSelectionStart();
                    int selectionEnd = mEtFeedInfo.getSelectionEnd();
                    // 如果光标起始和结束在同一位置,说明是选中效果,直接返回 false 交给系统执行删除动作
                    if (selectionStart != selectionEnd) {
                        return false;
                    }
                    Editable editable = mEtFeedInfo.getText();
                    if (editable == null) {
                        return false;
                    }
                    String content = editable.toString();
                    int lastPos = 0;
                    // 遍历判断光标的位置
                    for (String action : mActionList) {
                        lastPos = content.indexOf(action, lastPos);
                        if (lastPos != -1) {
                            if (selectionStart != 0 && selectionStart >= lastPos && selectionStart <= (lastPos + action.length())) {
                                //选中话题
                                mEtFeedInfo.setSelection(lastPos, lastPos + action.length());
                                return true;
                            }
                        }
                        lastPos += action.length();
                    }
                }
                return false;
            }
        });
    }

    // 设置文字和光标
    private void setText() {
        mEtFeedInfo.setText(mInfo);
        mEtFeedInfo.setSelection(mInfo.length());
    }

    private void initRecycleView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(PublishActivity.this, 3));
        mPhotoSelAdapter = new PhotoSelAdapter(mPhotos, false);
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
                mPhotoSelAdapter.setPhotos(mPhotos, showAdd);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_camera:
                photoPicker();
                break;
            case R.id.iv_eit:
                gotoTopicEit(TopicEitActivity.Type.EIT);
                break;
            case R.id.iv_topic:
                gotoTopicEit(TopicEitActivity.Type.TOPIC);
                break;
            case R.id.iv_link:
                showToast("紧张开发中");
                break;
        }
    }

    private void gotoTopicEit(TopicEitActivity.Type type){
        Intent intent = new Intent(this, TopicEitActivity.class);
        intent.putExtra(TopicEitActivity.TYPE, type);
        startActivityForResult(intent, TopicEitActivity.REQUEST_CODE);
    }

    // 图片选择
    private void photoPicker() {
        PhotoPicker.builder()
                .setPhotoCount(6)
                .setShowCamera(true)
                .setShowGif(true)
                .setSelected((ArrayList<String>) mPhotos)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode " + requestCode + ",resultCode " + resultCode);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                switch (requestCode) {
                    case PhotoPicker.REQUEST_CODE:
                    case PhotoPreview.REQUEST_CODE:
                        mPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        break;
                    case TopicEitActivity.REQUEST_CODE:
                        Serializable type = data.getSerializableExtra(TopicEitActivity.TYPE);
                        ArrayList<String> msgList = data.getStringArrayListExtra(TopicEitActivity.MSG);
                        Log.i(TAG, "onActivityResult: type " + type + ",msgList " + msgList);
                        if (msgList == null || msgList.isEmpty()) {
                            return;
                        }
                        mInfo = Objects.requireNonNull(mEtFeedInfo.getText()).toString();
                        if (TopicEitActivity.Type.TOPIC == type) {
                            StringBuilder sb = new StringBuilder();
                            for (String eit : msgList) {
                                sb.append("#").append(eit).append("# ");
                            }
                            mInfo = mInfo + sb.toString();
                            setText();
                        }
                        if (TopicEitActivity.Type.EIT == type) {
                            StringBuilder sb = new StringBuilder();
                            for (String eit : msgList) {
                                sb.append("@").append(eit).append(" ");
                            }
                            mInfo = mInfo + sb.toString();
                            setText();
                        }
                        break;
                }
            }
        }
        mPhotoSelAdapter.setPhotos(mPhotos, showAdd);
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
                        mEtFeedInfo.setText(null);
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
    private void addPhotoAdd(List<String> photoList) {
        if (showAdd && !photoList.contains(PhotoSelAdapter.mPhotoAdd)) {
            photoList.add(PhotoSelAdapter.mPhotoAdd);
        }
    }

    // 去除添加图片按钮
    private void removePhotoAdd(List<String> photoList) {
        photoList.remove(PhotoSelAdapter.mPhotoAdd);
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
