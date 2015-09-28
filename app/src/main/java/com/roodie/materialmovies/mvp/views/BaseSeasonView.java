package com.roodie.materialmovies.mvp.views;

/**
 * Created by Roodie on 27.09.2015.
 */
public interface BaseSeasonView extends MovieView {

    public void markSeasonAsStared(String showId, String seasonId);

    public void markSeasonAsUnstared(String showId, String seasonId);

}
