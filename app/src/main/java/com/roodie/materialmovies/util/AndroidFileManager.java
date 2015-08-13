package com.roodie.materialmovies.util;

import com.google.common.base.Preconditions;
import com.roodie.model.util.FileManager;

import java.io.File;

/**
 * Created by Roodie on 14.07.2015.
 */
public class AndroidFileManager implements FileManager {

    private final File mBaseDir;

    public AndroidFileManager(File baseDir) {
        mBaseDir = Preconditions.checkNotNull(baseDir, "baseDir cannot be null");
    }

    @Override
    public File getFile(String filename) {
        return new File(mBaseDir, filename);
    }

}
