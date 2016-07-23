package com.roodie.materialmovies.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.TextView;

import com.google.common.base.Preconditions;

/**
 * Created by Roodie on 07.08.2015.
 */
public class FontManager {

    public static final int FONT_ROBOTO_CONDENSED = 1;
    public static final int FONT_ROBOTO_CONDENSED_LIGHT = 2;
    public static final int FONT_ROBOTO_CONDENSED_BOLD = 3;
    public static final int FONT_ROBOTO_SLAB = 4;

    public static final int FONT_DIN_REGULAR = 5;
    public static final int FONT_DIN_MEDIUM = 6;
    public static final int FONT_DIN_LIGHT = 7;

    private static final String ROBOTO_CONDENSED = "RobotoCondensed-Regular.ttf";
    private static final String ROBOTO_CONDENSED_BOLD = "RobotoCondensed-Bold.ttf";
    private static final String ROBOTO_CONDENSED_LIGHT = "RobotoCondensed-Light.ttf";
    private static final String DIN_REGULAR = "DINPro-Regular.otf";
    private static final String DIN_MEDIUM = "DINPro-Medium.otf";
    private static final String DIN_LIGHT = "DINPro-Light.otf";
    private static final String ROBOTO_SLAB = "RobotoSlab-Regular.ttf";

    private static final String ROBOTO_LIGHT_NATIVE_FONT_FAMILY = "sans-serif-light";
    private static final String ROBOTO_CONDENSED_NATIVE_FONT_FAMILY = "sans-serif-condensed";

    private final LruCache<String, Typeface> mCache;
    private final AssetManager mAssetManager;

    public FontManager(AssetManager assetManager) {
        mAssetManager = Preconditions.checkNotNull(assetManager, "assetManager cannot be null");
        mCache = new LruCache<>(3);
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

    public Typeface getRobotoSlab() {
        return getTypeface(ROBOTO_SLAB);
    }


    private Typeface getTypeface(final String filename) {
        Typeface typeface = mCache.get(filename);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(mAssetManager, "fonts/" + filename);
            mCache.put(filename, typeface);
        }
        return typeface;
    }

    public Typeface getFont(final int fontType) {
        Typeface typeface = null;

        switch (fontType) {
            case FONT_ROBOTO_CONDENSED:
                typeface = getRobotoCondensed();
                break;
            case FONT_ROBOTO_CONDENSED_LIGHT:
                typeface = getRobotoCondensedLight();
                break;
            case FONT_ROBOTO_CONDENSED_BOLD:
                typeface = getRobotoCondensedBold();
                break;
            case FONT_ROBOTO_SLAB:
                typeface = getRobotoSlab();
                break;
            case FONT_DIN_REGULAR:
                typeface = getDinRegular();
                break;
            case FONT_DIN_MEDIUM:
                typeface = getDinMedium();
                break;
            case FONT_DIN_LIGHT:
                typeface = getDinLight();
                break;
        }

        return typeface;
    }

    public void setFont(TextView textView, final Integer fontType) {
        if (textView == null)
            return;
        textView.setTypeface(getFont(fontType));
    }

    public void setFont(TextView textView) {
        setFont(textView, FONT_DIN_REGULAR);
    }
}