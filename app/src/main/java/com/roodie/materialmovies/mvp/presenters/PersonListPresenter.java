package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.uwetrottmann.tmdb.entities.Person;

/**
 * Created by Roodie on 06.07.2015.
 */
public class PersonListPresenter extends BasePresenter {

    private PersonListView mPersonListView;

    public PersonListPresenter() {
    }

    @Override
    public void onResume() {

    }

    @Override
    protected void onInited() {
        super.onInited();
    }

    @Override
    protected void onPaused() {
        super.onPaused();
    }

    public interface PersonListView extends BaseMovieListView<Person> {

    }
}
