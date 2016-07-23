package com.roodie.materialmovies.util;


import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import com.roodie.model.util.TextUtils;

/**
 * Created by Roodie on 22.05.2016.
 */

public class CustomTypefaceSpan extends MetricAffectingSpan {

    private final Typeface typeface;

    public CustomTypefaceSpan(Typeface paramTypeface) {
        this.typeface = paramTypeface;
    }

    public static SpannableString applySpan(CharSequence paramCharSequence, Typeface paramTypeface) {
        if (TextUtils.isEmpty(paramCharSequence))
            return null;
        SpannableString localSpannableString = new SpannableString(paramCharSequence);
        localSpannableString.setSpan(new CustomTypefaceSpan(paramTypeface), 0, localSpannableString.length(), 18);
        return localSpannableString;
    }

    public void updateDrawState(TextPaint paramTextPaint) {
        paramTextPaint.setTypeface(this.typeface);
    }

    public void updateMeasureState(TextPaint paramTextPaint) {
        paramTextPaint.setTypeface(this.typeface);
    }
}
