package me.cl.lingxi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.cl.lingxi.databinding.PublicLicenseRecycleItemBinding;
import me.cl.lingxi.entity.PublicLicense;

/**
 * author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2017/09/07
 * desc   :
 * version: 1.0
 */
public class PublicLicenseAdapter extends RecyclerView.Adapter<PublicLicenseAdapter.ViewHolder> {

    private List<PublicLicense> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, PublicLicense license);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public PublicLicenseAdapter(List<PublicLicense> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PublicLicenseRecycleItemBinding binding = PublicLicenseRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final TextView mAuthor;
        private final TextView mDesc;

        private PublicLicense mLicense;

        public ViewHolder(PublicLicenseRecycleItemBinding binding) {
            super(binding.getRoot());

            mName = binding.name;
            mAuthor = binding.author;
            mDesc = binding.desc;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) mOnItemListener.onItemClick(v, mLicense);
                }
            });
        }

        public void bindItem(PublicLicense license) {
            mLicense = license;
            mName.setText(license.getName());
            mAuthor.setText(license.getAuthor());
            mDesc.setText(license.getDesc());
        }
    }
}
