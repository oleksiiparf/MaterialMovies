package com.roodie.model.entities;

/**
 * Created by Roodie on 29.06.2015.
 */
public interface ListItem<P> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SECTION = 1;

    public int getListType();

    public P getListItem();

    public int getListSectionTitle();
}
