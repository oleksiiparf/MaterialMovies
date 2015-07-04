package com.roodie.model.util;

import java.util.Collection;

/**
 * Created by Roodie on 25.06.2015.
 */
public class MoviesCollections {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static int size(Collection<?> collection) {
        return collection != null ? collection.size() : 0;
    }
}
