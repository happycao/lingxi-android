package me.cl.lingxi.common.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import me.cl.lingxi.BuildConfig;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/26
 * desc   : 图片工具类，压缩使用https://github.com/zetbaitsu/Compressor
 * version: 1.0
 */
public class ImageUtil {

    private static final String TAG = "ImageUtil";

    /**
     * 调用系统图片裁剪
     */
    public static Intent callSystemCrop(Uri uri, String imagePath, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        intent.putExtra("scale", true);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);

        // 解决return data只能返回小图的问题
        File file = new File(imagePath);
        if (!file.getParentFile().exists()) {
            boolean mkdirs = file.getParentFile().mkdirs();
            Log.d(TAG, "callSystemCrop: file mkdirs " + mkdirs);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    /**
     * 路径转图片文件
     */
    public static List<File> pathToImageFile(List<String> filePaths) {
        List<File> files = new ArrayList<>();
        if (filePaths == null || filePaths.size() == 0) return files;

        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (!file.exists()) {
                continue;
            }
            files.add(file);
        }
        return files;
    }

    /**
     * 压缩图片，低分辨率
     */
    public static List<String> compressorImage(Context context, List<String> filePaths) {
        return compressorImage(context, filePaths, false);
    }

    /**
     * 压缩图片，可选高分辨率
     */
    public static List<String> compressorImage(Context context, List<String> filePaths, boolean isXxh) {
        List<String> newPaths = new ArrayList<>();
        if (filePaths == null || filePaths.size() == 0) return newPaths;

        // 缓存图片目录
        String imagePath = getImageCachePath(context);
        // 压缩分辨率阈值
        int thresholdXxh = 1080;
        int thresholdXh = 720;
        // 压缩设置
        Compressor compressor = new Compressor(context);
        compressor.setDestinationDirectoryPath(imagePath);
        compressor.setQuality(75);
        // 判断分辨率
        if (isXxh) {
            thresholdXh = thresholdXxh;
            compressor.setQuality(100);
        }
        // 设置宽高
        compressor.setMaxWidth(thresholdXh);
        compressor.setMaxHeight(thresholdXh);
        // 图片格式
        compressor.setCompressFormat(Bitmap.CompressFormat.JPEG);
        // 压缩文件
        for (String filePath : filePaths) {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }

                String fileType = getFileType(filePath);
                if (".gif".equals(fileType)) {
                    newPaths.add(filePath);
                    continue;
                }

                // 压缩后的文件
                File newFile = compressor.compressToFile(file);
                newPaths.add(newFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newPaths;
    }

    /**
     * 获取图片路径
     */
    public static String getImagePath(){
        return getImageFilePath() + getImageName();
    }

    /**
     * 获取图片缓存目录
     */
    private static String getImageCachePath(Context context){
        return context.getCacheDir().getPath() + "/image/";
    }

    /**
     * 获取图片目录
     */
    private static String getImageFilePath(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Touches/";
    }

    /**
     * 获取文件类型
     */
    private static String getFileType(String filePath) {
        return filePath.substring(filePath.lastIndexOf("."), filePath.length());
    }

    /**
     * 获取临时图片文件名
     */
    private static String getImageName() {
        return getUUID() + ".jpg";
    }

    /**
     * 获取uuid
     */
    private static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取文件uri，适配N+
     */
    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    // 预留，获取文件宽高
    private void getWH(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 阈值
        int thresholdXxh = 1920;
        int thresholdXh = 1080;
        // 关键项
        options.inJustDecodeBounds = true;
        // 此处返回的bitmap为null，但宽高可以从options获取
        BitmapFactory.decodeFile(filePath, options);
        // 宽高
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        // 比例
        float proportion = (float) outWidth / (float) outHeight;
        // 判断宽高
        if (outWidth > outHeight) {
            if (outWidth > thresholdXxh) {
                outWidth = thresholdXxh;
                outHeight = (int) (outWidth / proportion);
            }
            if (outWidth > thresholdXh && outWidth < thresholdXxh) {
                outWidth = thresholdXh;
                outHeight = (int) (outWidth / proportion);
            }
        } else {
            if (outHeight > thresholdXxh) {
                outHeight = thresholdXxh;
                outWidth = (int) (outHeight * proportion);
            }
            if (outHeight > thresholdXh && outHeight < thresholdXxh) {
                outHeight = thresholdXh;
                outWidth = (int) (outHeight * proportion);
            }
        }
        Log.d("xl", "outWidth = " + outWidth + ",outHeight = " + outHeight);
    }
}
