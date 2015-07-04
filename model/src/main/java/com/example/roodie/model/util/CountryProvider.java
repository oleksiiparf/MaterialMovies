package com.example.roodie.model.util;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface CountryProvider {

    public static final String US_TWO_LETTER_CODE = "UA";

    /**
     * @return ISO 3166-1 country code
     */
    public String getTwoLetterCountryCode();

    /**
     * @return ISO 639-1 language code
     */
    public String getTwoLetterLanguageCode();

}