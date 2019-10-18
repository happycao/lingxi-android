package me.cl.library.photo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.cl.library.R;
import me.cl.library.base.BaseActivity;
/**
* 手势操作：https://github.com/dinuscxj/MultiTouchGestureDetector/blob/master/README-ZH.md
*/
public class PhotoActivity extends BaseActivity {

    private static final String TAG = "PhotoActivity";

    private WeakReference<PhotoActivity> mWeakReference;
    private PhotoViewPager mPhotoViewPager;

    // 图片地址
    private ArrayList<String> mPhotoUrlList;
    // 当前位置
    private int currentPosition;
    // 图片保存位置
    private String downloadPath;

    private PhotoAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);
        mWeakReference = new WeakReference<>(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        mPhotoUrlList = new ArrayList<>(intent.getStringArrayListExtra(PhotoBrowser.EXTRA_PHOTOS));
        currentPosition = intent.getIntExtra(PhotoBrowser.EXTRA_CURRENT_ITEM, 0);
        downloadPath = intent.getStringExtra(PhotoBrowser.EXTRA_DOWNLOAD_PATH);
        if (mPhotoUrlList.isEmpty()) {
            onBackPressed();
        }
        if (currentPosition >= mPhotoUrlList.size()) {
            currentPosition = 0;
        }

        mPhotoViewPager = findViewById(R.id.photo_view_pager);
        mPhotoAdapter = new PhotoAdapter(Glide.with(this), mPhotoUrlList);
        mPhotoViewPager.setAdapter(mPhotoAdapter);
        mPhotoViewPager.setCurrentItem(currentPosition);
        mPhotoAdapter.setOnMoveOutListener(new PhotoListener.OnMoveOutListener() {
            @Override
            public void onMoveOut() {
                mPhotoViewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
//                        onBackPressed();
                    }
                }, 100);
            }
        });
        final AppCompatTextView photoIndex = findViewById(R.id.photo_index);
        final int count = mPhotoAdapter.getCount();
        if (count == 1) {
            photoIndex.setVisibility(View.GONE);
        }
        photoIndex.setText(getString(R.string.photo_index, currentPosition + 1, count));
        mPhotoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                currentPosition = i;
                photoIndex.setText(getString(R.string.photo_index, i + 1, count));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        AppCompatTextView photoSave = findViewById(R.id.photo_save);
        photoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mPhotoUrlList.get(currentPosition);
                if (url.contains("?")) {
                    url = url.split("\\?")[0];
                }
                PhotoDownloadService service = new PhotoDownloadService(getApplicationContext(), url, new PhotoListener.OnDownLoadListener() {
                    @Override
                    public void onSuccess(Uri uri) {
                        showToast(R.string.photo_save_success);
                        // 通知图库更新
                        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    }

                    @Override
                    public void onFailed(int message) {
                        showToast(message);
                    }
                });
                service.setDirName(downloadPath);
                new Thread(service).start();
            }
        });
    }
}
