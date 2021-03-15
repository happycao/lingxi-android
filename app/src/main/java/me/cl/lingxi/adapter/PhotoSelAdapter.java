package me.cl.lingxi.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.ContentUtil;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/11/03
 * desc   : 图片选择
 * version: 1.0
 */
public class PhotoSelAdapter extends RecyclerView.Adapter<PhotoSelAdapter.PhotoViewHolder> {

    public static final String mPhotoAdd = "file:///android_asset/icon_photo_add.png";
    private List<String> mPhotos;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onPhotoClick(int position);
        void onDelete(int position);
    }

    public PhotoSelAdapter(List<String> photos, boolean showAdd) {
        this.mPhotos = photos;
        if (showAdd && mPhotos.size() < 6 && !mPhotos.contains(mPhotoAdd)) mPhotos.add(mPhotoAdd);
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.publish_photo_recycle_item, null);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder holder, final int position) {
        holder.bindItem(mPhotos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void setPhotos(List<String> photos, boolean showAdd) {
        this.mPhotos = photos;
        if (showAdd && mPhotos.size() < 6 && !mPhotos.contains(mPhotoAdd)) mPhotos.add(mPhotoAdd);
        notifyDataSetChanged();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView mIvPhoto;
        @BindView(R.id.iv_delete)
        ImageView mIvDelete;
        private int mPosition;

        PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(String photoUrl, final int position) {
            mPosition = position;
            if (mPhotos.get(position).equals(mPhotoAdd)) {
                mIvDelete.setVisibility(View.GONE);
            } else {
                mIvDelete.setVisibility(View.VISIBLE);
            }

            ContentUtil.loadImage(mIvPhoto , photoUrl);
        }

        @OnClick({R.id.iv_photo, R.id.iv_delete})
        public void onClick(View view){
            switch (view.getId()) {
                case R.id.iv_photo:
                    if (mOnItemClickListener != null) mOnItemClickListener.onPhotoClick(mPosition);
                    break;
                case R.id.iv_delete:
                    if (mOnItemClickListener != null) mOnItemClickListener.onDelete(mPosition);
                    break;
            }
        }
    }
}
