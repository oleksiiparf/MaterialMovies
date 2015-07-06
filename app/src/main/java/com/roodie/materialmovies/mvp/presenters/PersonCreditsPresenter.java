package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.uwetrottmann.tmdb.entities.Person;
import com.uwetrottmann.tmdb.entities.PersonCredits;

/**
 * Created by Roodie on 06.07.2015.
 */
public class PersonCreditsPresenter extends BasePresenter {

private PersonCreditListView mPersonCreditListView;

    public PersonCreditsPresenter() {
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

    public interface PersonCreditListView extends BaseMovieListView<PersonCredits> {

        void showPersonCastCredits(Person person);

        void showPersonCrewCredits(Person person);
    }

}
