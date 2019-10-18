package me.cl.library.photo;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import me.cl.library.R;

/**
 * author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/04/30
 * desc   : 图片保存
 * version: 1.0
 */
public class PhotoDownloadService implements Runnable {

    private Context context;
    private String url;
    private PhotoListener.OnDownLoadListener onDownLoadListener;
    private File dirFile;
    private String dirName = "Touches";

    public PhotoDownloadService(Context context, String url, PhotoListener.OnDownLoadListener onDownLoadListener) {
        this.context = context;
        this.url = url;
        this.onDownLoadListener = onDownLoadListener;
        this.dirFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
    }

    public void setDirName(String dirName) {
        if (!TextUtils.isEmpty(dirName)) {
            this.dirName = dirName;
        }
    }

    @Override
    public void run() {
        try {
            File dir = new File(dirFile, dirName);
            if (!dir.exists()) {
                boolean mkdirs = dir.mkdirs();
            }
            if (fileExists(dir, url)) {
                onDownLoadListener.onFailed(R.string.photo_save_exists);
                return;
            }

            File inFile = Glide.with(context)
                    .downloadOnly()
                    .load(url)
                    .submit()
                    .get();

            File outFile = new File(dir, getFileName(url));
            saveImage(inFile, outFile);
        } catch (Exception e) {
            onDownLoadListener.onFailed(R.string.photo_save_error);
        }
    }

    private void saveImage(File inFile, File outFile) throws Exception {
        if (outFile.exists()) {
            boolean newFile = outFile.createNewFile();
        }
        FileInputStream fis = new FileInputStream(inFile);
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buffer = new byte[1024];
        while (fis.read(buffer) > 0) {
            fos.write(buffer);
        }
        onDownLoadListener.onSuccess(Uri.fromFile(outFile));
        fos.close();
        fis.close();
    }

    private boolean fileExists(File dir, String url) throws Exception {
        File file = new File(dir, getFileName(url));
        return file.exists();
    }

    private String getFileName(String url) throws Exception {
        String[] split = url.split("/");
        String fileName = split[split.length - 1];
        if (!fileName.contains(".")) {
            fileName += ".jpg";
        }
        return fileName;
    }

}
