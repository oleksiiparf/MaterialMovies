package com.roodie.model.util;

/**
 * Created by Roodie on 12.08.2015.
 */

public interface StringFetcher {

    public String getString(int id);

    public String getString(int id, Object... format);

}