package com.leeson.image_pickers.utils;

import android.content.Context;

import com.leeson.image_pickers.AppPath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * Author：chumengwei on 2020/12/14 14:38
 * Description：
 * Motto：We can do anything we want to do if we stick to it long enough.
 */
public class ImageCompressUtils {

    public static int compressCount = 0;

    public static void compressImages(Context context,List<String> paths,int size,CompressCallBack callback){
        compressCount = 0;
        final List<Map<String, String>> lubanCompressPaths = new ArrayList<>();
        Luban.with(context)
                .load(paths)
                .ignoreBy(size)
                .setTargetDir(getPath(context))
                .filter(path -> !path.endsWith(".gif"))
                .setRenameListener(filePath -> filePath.substring(filePath.lastIndexOf("/")))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        Map<String, String> map = new HashMap<>();
                        map.put("thumbPath", file.getAbsolutePath());
                        map.put("path", file.getAbsolutePath());
                        lubanCompressPaths.add(map);
                        compressCount++;
                        if(compressCount == paths.size()){
                            callback.onCompressComplete(lubanCompressPaths);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Map<String, String> map = new HashMap<>();
                        map.put("thumbPath", paths.get(compressCount));
                        map.put("path", paths.get(compressCount));
                        // 当压缩过程出现问题时调用
                        compressCount++;
                        if(compressCount == paths.size()){
                            callback.onCompressComplete(lubanCompressPaths);
                        }
                    }
                }).launch();

    }

    public static String getPath(Context context) {
        String path = new AppPath(context).getAppImgDirPath(false);
        File file = new File(path);
        if (file.mkdirs()) {
            createNomedia(path);
            return path;
        }
        createNomedia(path);
        return path;
    }

    public static void createNomedia(String path) {
        File nomedia = new File(path, ".nomedia");
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
