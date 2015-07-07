package com.roodie.model.entities;

/**
 * Created by Roodie on 07.07.2015.
 */
public abstract class BasicWrapper implements ListItem{

    public static final int TYPE_TMDB = 1;
    public static final int TYPE_IMDB = 2;

    @Override
    public int getListType() {
        return ListItem.TYPE_ITEM;
    }

    @Override
    public Object getListItem() {
        return  this;
    }

    @Override
    public int getListSectionTitle() {
        return 0;
    }
}
