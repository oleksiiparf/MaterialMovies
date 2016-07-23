package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.FontManager;

import javax.inject.Inject;

/**
 * Implementation of {@link TextView} with customizable font
 */
public class MMoviesTextView extends TextView {

    @Inject
    FontManager mFontManager;

    public MMoviesTextView(Context context) {
        this(context, null);
    }

    public MMoviesTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MMoviesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MMoviesApp.from(context).inject(this);

        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MMoviesTextView);
            setFont(a.getInt(R.styleable.MMoviesTextView_font, 7));
            a.recycle();
        }
    }

    public void setFont(final int font) {
        Typeface typeface = mFontManager.getFont(font);
        if (typeface != null) {
            setPaintFlags(getPaintFlags() | TextPaint.SUBPIXEL_TEXT_FLAG);
            setTypeface(typeface);
        }
    }


}
