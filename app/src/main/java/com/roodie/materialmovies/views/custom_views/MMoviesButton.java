package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.util.FontManager;

import javax.inject.Inject;


/**
 * Implementation of {@link AppCompatButton} with customizable font
 */
public class MMoviesButton extends AppCompatButton {

    @Inject
    FontManager mTypefaceManager;

    public MMoviesButton(Context context) {
        this(context, null);
    }

    public MMoviesButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MMoviesButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MMoviesApp.from(context).inject(mTypefaceManager);
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
