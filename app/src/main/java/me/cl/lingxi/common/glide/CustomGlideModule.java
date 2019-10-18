package me.cl.lingxi.common.glide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/31
 * desc   : 实现AppGlideModule，GlideV4
 * version: 1.0
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {

    // 无需实现任何方法即可使用
    // 官方文档 https://muyangmin.github.io/glide-docs-cn/doc/configuration.html#applications

    /**
     * 相关配置
     */
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // 内存缓存
        int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        // Bitmap 池
        int bitmapPoolSizeBytes = 1024 * 1024 * 30; // 30mb
        builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));
        // 磁盘缓存
        int diskCacheSizeBytes = 1024 * 1024 * 100;  // 100 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
        // 请求选项
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .disallowHardwareConfig());
        // 日志级别
        builder.setLogLevel(Log.ERROR);
    }
}
