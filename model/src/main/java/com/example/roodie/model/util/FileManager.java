package com.example.roodie.model.util;

import com.google.common.base.Preconditions;

import java.io.File;

/**
 * Created by Roodie on 02.07.2015.
 */
public class FileManager {

    private final File mBaseDir;

    public FileManager(File mBaseDir) {
        this.mBaseDir = Preconditions.checkNotNull(mBaseDir, "baseDir ca not be null");
    }

    public File getFile (String filename) {
        return new File(mBaseDir, filename);
    }
}
