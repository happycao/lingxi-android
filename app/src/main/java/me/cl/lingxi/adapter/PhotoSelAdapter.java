package me.cl.lingxi.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.R;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.databinding.PublishPhotoRecycleItemBinding;

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
        PublishPhotoRecycleItemBinding binding = PublishPhotoRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PhotoViewHolder(binding);
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

    class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mIvPhoto;
        private final ImageView mIvDelete;
        private int mPosition;

        PhotoViewHolder(PublishPhotoRecycleItemBinding binding) {
            super(binding.getRoot());

            mIvPhoto = binding.ivPhoto;
            mIvDelete = binding.ivDelete;

            mIvPhoto.setOnClickListener(this);
            mIvDelete.setOnClickListener(this);
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

        @SuppressLint("NonConstantResourceId")
        @Override
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
