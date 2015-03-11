package com.pukza.plibrary.component.image;

import android.content.Context;

import com.facebook.cache.disk.DiskStorageCache;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineFactory;

import java.io.File;

/**
 * @author created by hwaseopchoi
 * @date on 2016. 1. 8.
 */

public class FrescoImageConfig {

    public static void clearImageCache(Context context) {
        if(Fresco.getImagePipeline() != null)
            Fresco.getImagePipeline().clearCaches();
    }

    public static long getCacheSize()
    {
        long mem = 0;
        ImagePipelineFactory pipeline = ImagePipelineFactory.getInstance();
        if(pipeline != null)
        {
            DiskStorageCache mainCache = pipeline.getMainDiskStorageCache();
            DiskStorageCache subCache = pipeline.getSmallImageDiskStorageCache();

            if(mainCache != null)
                mem += mainCache.getSize();

            if(subCache != null)
                mem += subCache.getSize();

        }

        return mem;
    }

    public static boolean deleteFile(File path) {
        if(!path.exists()) {
            return false;
        }

        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteFile(file);
            } else {
                file.delete();
            }
        }

        return path.delete();
    }
}
