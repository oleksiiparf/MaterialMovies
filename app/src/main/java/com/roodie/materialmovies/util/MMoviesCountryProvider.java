package com.roodie.materialmovies.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.util.CountryProvider;

import java.util.Locale;

/**
 * Created by Roodie on 25.06.2015.
 */
public class MMoviesCountryProvider implements CountryProvider {

    private final Context mContext;

    private String mCountryCode;
    private String mLanguageCode;

    public MMoviesCountryProvider(Context mContext) {
        this.mContext = Preconditions.checkNotNull(mContext, "context can not be null");
    }

    @Override
    public String getTwoLetterCountryCode() {
        String code;
        if (mCountryCode == null) {
            code = getTwoLetterCountryCodeFromLocale();
            mCountryCode = code;
            }
        return  mCountryCode;
        }




    private String getTwoLetterCountryCodeFromLocale() {
        final Locale locale = Locale.getDefault();

        final String countryCode = locale.getCountry();
        if (!TextUtils.isEmpty(countryCode)) {
            return countryCode;
        }

        return null;
    }


    @Override
    public String getTwoLetterLanguageCode() {
       if (mLanguageCode == null) {
           Locale locale = Locale.getDefault();
           mLanguageCode = locale.getLanguage();
       }
        return  mLanguageCode;
    }
}
