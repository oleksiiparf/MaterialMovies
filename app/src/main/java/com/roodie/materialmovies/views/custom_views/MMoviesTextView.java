package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.TypefaceManager;
import com.roodie.materialmovies.views.MMoviesApplication;

import javax.inject.Inject;

/**
 * Created by Roodie on 07.08.2015.
 */
public class MMoviesTextView extends TextView {

    @Inject TypefaceManager mTypefaceManager;

    public static final int FONT_LOBSTER = 1;
    public static final int FONT_ROBOTO_LIGHT = 2;
    public static final int FONT_ROBOTO_CONDENSED = 3;
    public static final int FONT_ROBOTO_CONDENSED_LIGHT = 4;
    public static final int FONT_ROBOTO_CONDENSED_BOLD = 5;
    public static final int FONT_ROBOTO_SLAB = 6;

    public static final int FONT_DIN_REGULAR = 7;
    public static final int FONT_DIN_MEDIUM = 8;
    public static final int FONT_DIN_LIGHT = 9;
    public static final int FONT_DIN_ULTRA_LIGHT = 10;


    public MMoviesTextView(Context context) {
        this(context, null);
    }

    public MMoviesTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MMoviesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MMoviesApplication.from(context).inject(this);

        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MMoviesTextView);
            setFont(a.getInt(R.styleable.MMoviesTextView_font, 0));
            a.recycle();
        }
    }


    public void setFont(final int font) {
        Typeface typeface = getFont(mTypefaceManager, font);
        if (typeface != null) {
            setPaintFlags(getPaintFlags() | TextPaint.SUBPIXEL_TEXT_FLAG);
            setTypeface(typeface);
        }
    }

    public static Typeface getFont(TypefaceManager typefaceManager, final int customFont) {
        Typeface typeface = null;

        switch (customFont) {
            case FONT_LOBSTER:
                typeface = typefaceManager.getLobster();
                break;
            case FONT_ROBOTO_LIGHT:
                typeface = typefaceManager.getRobotoLight();
                break;
            case FONT_ROBOTO_CONDENSED:
                typeface = typefaceManager.getRobotoCondensed();
                break;
            case FONT_ROBOTO_CONDENSED_LIGHT:
                typeface = typefaceManager.getRobotoCondensedLight();
                break;
            case FONT_ROBOTO_CONDENSED_BOLD:
                typeface = typefaceManager.getRobotoCondensedBold();
                break;
            case FONT_ROBOTO_SLAB:
                typeface = typefaceManager.getRobotoSlab();
                break;
            case FONT_DIN_REGULAR:
                typeface = typefaceManager.getDinRegular();
                break;
            case FONT_DIN_MEDIUM:
                typeface = typefaceManager.getDinMedium();
                break;
            case FONT_DIN_LIGHT:
                typeface = typefaceManager.getDinLight();
                break;
            case FONT_DIN_ULTRA_LIGHT:
                typeface = typefaceManager.getDinUltraLight();
                break;

        }

        return typeface;
    }


}
