package com.roodie.materialmovies.mvp.views;

import com.roodie.model.entities.ListItem;

import java.util.List;

/**
 * Created by Roodie on 02.07.2015.
 */
public interface BaseMovieListView<E> extends MovieView {

    void setItems(List<ListItem<E>> items);

}
