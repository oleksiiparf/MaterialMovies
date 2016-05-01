package com.roodie.model.util;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface CountryProvider {

    String US_TWO_LETTER_CODE = "US";

    /**
     * @return ISO 3166-1 country code
     */
    String getTwoLetterCountryCode();

    /**
     * @return ISO 639-1 language code
     */
    String getTwoLetterLanguageCode();

}