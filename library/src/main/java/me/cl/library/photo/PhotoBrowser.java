package me.cl.library.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/04/26
 * desc   :
 * version: 1.0
 */
public class PhotoBrowser {

    public static final int REQUEST_CODE = 666;
    public static final String EXTRA_CURRENT_ITEM = "current_item";
    public static final String EXTRA_PHOTOS = "photos";
    public static final String EXTRA_DOWNLOAD_PATH = "download_path";

    public PhotoBrowser() {

    }

    public static PhotoBrowser.Builder builder() {
        return new PhotoBrowser.Builder();
    }

    public static class Builder {

        private Bundle mBrowserOptionsBundle = new Bundle();
        private Intent mBrowserIntent = new Intent();

        public Builder() {

        }

        public void start(@NonNull Activity activity) {
            activity.startActivity(this.getIntent(activity));
        }

        private Intent getIntent(@NonNull Context context) {
            this.mBrowserIntent.setClass(context, PhotoActivity.class);
            this.mBrowserIntent.putExtras(this.mBrowserOptionsBundle);
            return this.mBrowserIntent;
        }

        public PhotoBrowser.Builder setPhotos(ArrayList<String> photoPaths) {
            this.mBrowserOptionsBundle.putStringArrayList(EXTRA_PHOTOS, photoPaths);
            return this;
        }

        public PhotoBrowser.Builder setCurrentItem(int currentItem) {
            this.mBrowserOptionsBundle.putInt(EXTRA_CURRENT_ITEM, currentItem);
            return this;
        }

        public PhotoBrowser.Builder setDownloadPath(String downloadPath) {
            this.mBrowserOptionsBundle.putString(EXTRA_DOWNLOAD_PATH, downloadPath);
            return this;
        }


    }
}
