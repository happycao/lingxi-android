package me.cl.lingxi.adapter;

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
 * desc   : 动态图片
 * version: 1.0
 */
public class FeedPhotoAdapter extends RecyclerView.Adapter<FeedPhotoAdapter.PhotoViewHolder> {

    private int mType = 0;
    private List<String> mPhotos;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onPhotoClick(int position);
    }

    public FeedPhotoAdapter(List<String> photos) {
        this.mPhotos = photos;
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

    public void setPhotos(List<String> photos) {
        this.mPhotos = photos;
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
        }

        public void bindItem(String photoUrl, final int position) {
            mPosition = position;
            mIvDelete.setVisibility(View.GONE);
            // 加载图片
            ContentUtil.loadFeedImage(mIvPhoto , photoUrl);
        }

        @Override
        public void onClick(View view){
            if (view.getId() == R.id.iv_photo) {
                if (mOnItemClickListener != null) mOnItemClickListener.onPhotoClick(mPosition);
            }
        }
    }
}
