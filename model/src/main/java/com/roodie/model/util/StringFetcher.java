package com.roodie.model.util;

/**
 * Created by Roodie on 12.08.2015.
 */

public interface StringFetcher {

    String getString(int id);

    String getString(int id, Object... format);

}