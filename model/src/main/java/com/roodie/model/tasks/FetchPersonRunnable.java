package com.roodie.model.tasks;

import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.AppendToResponse;
import com.uwetrottmann.tmdb.entities.Person;
import com.uwetrottmann.tmdb.entities.PersonCastCredit;
import com.uwetrottmann.tmdb.entities.PersonCrewCredit;
import com.uwetrottmann.tmdb.enumerations.AppendToResponseItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchPersonRunnable extends BaseRunnable<Person> {

    private final int mId;

    public FetchPersonRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Person doBackgroundCall() throws RetrofitError {
        return getTmdbClient().personService().summary(mId,
                new AppendToResponse(AppendToResponseItem.MOVIE_CREDITS));
    }

    @Override
    public void onSuccess(Person result) {
        PersonWrapper person = getEntityMapper().map(result);

        if (person != null && result.movie_credits != null) {

            if (!MoviesCollections.isEmpty(result.movie_credits.cast)) {
                List<PersonCreditWrapper> credits = new ArrayList<>();
                for (PersonCastCredit credit : result.movie_credits.cast) {
                    credits.add(new PersonCreditWrapper(credit));
                }
                Collections.sort(credits, PersonCreditWrapper.COMPARATOR_SORT_DATE);
                person.setCastCredits(credits);
            }

            if (!MoviesCollections.isEmpty(result.movie_credits.crew)) {
                List<PersonCreditWrapper> credits = new ArrayList<>();
                for (PersonCrewCredit credit : result.movie_credits.crew) {
                    credits.add(new PersonCreditWrapper(credit));
                }
                Collections.sort(credits, PersonCreditWrapper.COMPARATOR_SORT_DATE);
                person.setCrewCredits(credits);
            }

            person.setfetchedCredits(true);

            getEventBus().post(new MoviesState.PersonChangedEvent(getCallingId(), person));
        }
    }

    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        PersonWrapper person = mState.getPerson(mId);
        if (person != null) {
            getEventBus().post(new MoviesState.PersonChangedEvent(getCallingId(), person));
        }
    }

    /* TODO
    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return super.createLoadingProgressEvent(show);
    }
    */
}
