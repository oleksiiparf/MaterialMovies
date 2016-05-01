package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.util.FontManager;

import javax.inject.Inject;

/**
 * Created by Roodie on 21.08.2015.
 */
public class MMoviesEditText extends AppCompatEditText {

    @Inject
    FontManager mTypefaceManager;

    public MMoviesEditText(Context context) {
        this(context, null);
    }

    public MMoviesEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MMoviesEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        MMoviesApp.from(context).inject(this);

        if (isInEditMode())
            return;
            setFont();
    }

    public void setFont() {
        Typeface typeface = mTypefaceManager.getDinRegular();
        if (typeface != null) {
            setPaintFlags(getPaintFlags() | TextPaint.SUBPIXEL_TEXT_FLAG);
            setTypeface(typeface);
        }
    }


}
