package com.roodie.model.tasks;

import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.Person;
import com.uwetrottmann.tmdb.entities.PersonResultsPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 20.08.2015.
 */
abstract class BasePaginatedPersonRunnable extends BasePaginatedRunnable<MoviesState.PersonPaginatedResult, PersonWrapper, PersonResultsPage> {

    BasePaginatedPersonRunnable(int callingId, int page) {
        super(callingId, page);
    }

    @Override
    protected void updatePaginatedResult(
            ApplicationState.PersonPaginatedResult result,
            PersonResultsPage tmdbResult) {

        List<PersonWrapper> people = new ArrayList<>(tmdbResult.results.size());
        for (Person mPerson: tmdbResult.results) {
            people.add(getEntityMapper().map(mPerson));
        }

        result.items.addAll(people);

        result.page = tmdbResult.page;
        if (tmdbResult.total_pages != null) {
            result.totalPages = tmdbResult.total_pages;
        }
    }

    @Override
    protected MoviesState.PersonPaginatedResult createPaginatedResult() {
        return new MoviesState.PersonPaginatedResult();
    }
}
