package com.roodie.materialmovies.util;

/**
 * Created by Roodie on 09.05.2016.
 */

public class FlagUrlProvider {

    public String getCountryFlagUrl(String countryCode) {
        return "http://www.geonames.org/flags/x/" + countryCode.toLowerCase() + ".gif";
    }
}
