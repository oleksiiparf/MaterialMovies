package com.roodie.model.util;

/**
 * Created by Roodie on 13.03.2016.
 */

import android.content.Context;
import android.support.annotation.ColorRes;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.URLSpan;

public class TextUtils {

    public static boolean isEmpty(CharSequence text) {
        return null == text || text.length() == 0;
    }


    public static Spanned toSpanned(final Context context, final String toastString, @ColorRes final int toastColor) {
        final String string = "" + context.getResources().getColor(toastColor);
        final Spannable spannable = (Spannable) Html.fromHtml(colorize_a(colorize_em(toastString, string, true), string));
        for (final URLSpan urlSpan : (URLSpan[])spannable.getSpans(0, spannable.length(), (Class)URLSpan.class)) {
            spannable.setSpan(urlSpan, spannable.getSpanStart(urlSpan), spannable.getSpanEnd(urlSpan), 0);
        }
        return spannable;
    }

    private static String colorize_a(final String s, final String s2) {
        return nullToEmpty(s).replaceAll("<a (.+?)>(.+?)</a>", String.format("<b><font color='%s'><a $1>$2</a></font></b>", s2));
    }

    private static String colorize_em(final String s, final String s2, final boolean b) {
        final String nullToEmpty = nullToEmpty(s);
        String string = "";
        if (b) {
            string += "<b>";
        }
        String s3 = string + "<font color='%s'>$1</font>";
        if (b) {
            s3 += "</b>";
        }
        return nullToEmpty.replaceAll("<em>(.+?)</em>", String.format(s3, s2));
    }

    public static String nullToEmpty(String s) {
        if (s == null) {
            s = "";
        }
        return s;
    }


}