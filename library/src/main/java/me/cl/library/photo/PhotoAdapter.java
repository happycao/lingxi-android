package me.cl.library.photo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.PhotoView;
import com.bumptech.glide.RequestManager;

import java.util.List;

import me.cl.library.R;

/**
 * author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/04/26
 * desc   : 图片浏览
 * version: 1.0
 */
public class PhotoAdapter extends PagerAdapter {

    private View mCurrentView;
    private RequestManager mGlide;
    private List<String> photoUrlList;

    private PhotoListener.OnClickListener mOnClickListener;
    private PhotoListener.OnLongClickListener mOnLongClickListener;
    private PhotoListener.OnMoveOutListener mOnMoveOutListener;

    public PhotoAdapter(RequestManager glide, List<String> photoUrlList) {
        mGlide = glide;
        this.photoUrlList = photoUrlList;
    }

    public void setOnClickListener(PhotoListener.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(PhotoListener.OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnMoveOutListener(PhotoListener.OnMoveOutListener onMoveOutListener) {
        mOnMoveOutListener = onMoveOutListener;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
    }

    public View getCurrentView() {
        return mCurrentView;
    }

    @Override
    public int getCount() {
        return photoUrlList == null ? 0 : photoUrlList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        this.mGlide.clear((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final Context context = container.getContext();
        final PhotoView photoView = (PhotoView) LayoutInflater.from(context).inflate(R.layout.photo_adapter_item, container, false);
        final String url = photoUrlList.get(position);
        photoView.enable();
        if (url.endsWith(".gif")) {
            mGlide.asGif().load(url).into(photoView);
        } else {
            mGlide.load(url).into(photoView);
        }

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMoveOutListener != null) {
                    mOnMoveOutListener.onMoveOut();
                }
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongClickListener != null) {
                    mOnLongClickListener.onLongClick(v, position, url);
                }
                return false;
            }
        });
        photoView.setMoveOutListener(new PhotoView.OnMoveOutListener() {
            @Override
            public void onMoving(float scale) {

            }

            @Override
            public void onMoveOut() {
                if (mOnMoveOutListener != null) {
                    mOnMoveOutListener.onMoveOut();
                }
            }
        });

        container.addView(photoView);
        return photoView;
    }
}
