package com.roodie.materialmovies.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.google.common.base.Preconditions;

/**
 * Created by Roodie on 07.08.2015.
 */
public class TypefaceManager {

    private static final String LOBSTER = "Lobster-Regular.ttf";
    private static final String ROBOTO_LIGHT = "Roboto-Light.ttf";
    private static final String ROBOTO_CONDENSED = "RobotoCondensed-Regular.ttf";
    private static final String ROBOTO_CONDENSED_BOLD = "RobotoCondensed-Bold.ttf";
    private static final String ROBOTO_CONDENSED_LIGHT = "RobotoCondensed-Light.ttf";
    private static final String DIN_REGULAR = "DINPro-Regular.ttf";
    private static final String DIN_MEDIUM = "DINPro-Medium.ttf";
    private static final String DIN_LIGHT = "DINPro-Light.ttf";
    private static final String DIN_ULTRA_LIGHT = "DINNextLTPro-UltraLight.ttf";
    private static final String ROBOTO_SLAB = "RobotoSlab-Regular.ttf";

    private static final String ROBOTO_LIGHT_NATIVE_FONT_FAMILY = "sans-serif-light";
    private static final String ROBOTO_CONDENSED_NATIVE_FONT_FAMILY = "sans-serif-condensed";

    private final LruCache<String, Typeface> mCache;
    private final AssetManager mAssetManager;

    public TypefaceManager(AssetManager assetManager) {
        mAssetManager = Preconditions.checkNotNull(assetManager, "assetManager cannot be null");
        mCache = new LruCache<>(3);
    }

    public Typeface getRobotoLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_LIGHT_NATIVE_FONT_FAMILY, Typeface.NORMAL);
        }
        return getTypeface(ROBOTO_LIGHT);
    }

    public Typeface getRobotoCondensed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.NORMAL);
        }
        return getTypeface(ROBOTO_CONDENSED);
    }

    public Typeface getRobotoCondensedBold() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.BOLD);
        }
        return getTypeface(ROBOTO_CONDENSED_BOLD);
    }

    public Typeface getRobotoCondensedLight() {
        return getTypeface(ROBOTO_CONDENSED_LIGHT);
    }

    public Typeface getDinLight() {
        return getTypeface(DIN_LIGHT);
    }

    public Typeface getDinRegular() {
        return getTypeface(DIN_REGULAR);
    }
    public Typeface getDinMedium() {
        return getTypeface(DIN_MEDIUM);
    }

    public Typeface getDinUltraLight() {
        return getTypeface(DIN_ULTRA_LIGHT);
    }

    public Typeface getRobotoSlab() {
        return getTypeface(ROBOTO_SLAB);
    }

    public Typeface getLobster() {
        return  getTypeface(LOBSTER);
    }

    private Typeface getTypeface(final String filename) {
        Typeface typeface = mCache.get(filename);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(mAssetManager, "fonts/" + filename);
            mCache.put(filename, typeface);
        }
        return typeface;
    }
}