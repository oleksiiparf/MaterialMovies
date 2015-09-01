package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.roodie.materialmovies.util.TypefaceManager;
import com.roodie.materialmovies.views.MMoviesApplication;

import javax.inject.Inject;

/**
 * Created by Roodie on 01.09.2015.
 */
public class MMoviesButton extends AppCompatButton {

    @Inject
    TypefaceManager mTypefaceManager;

    public MMoviesButton(Context context) {
        this(context, null);
    }

    public MMoviesButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MMoviesButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MMoviesApplication.from(context).inject(this);
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
