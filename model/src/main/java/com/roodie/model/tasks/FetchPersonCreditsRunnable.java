package com.roodie.model.tasks;

import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.PersonCastCredit;
import com.uwetrottmann.tmdb.entities.PersonCredits;
import com.uwetrottmann.tmdb.entities.PersonCrewCredit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchPersonCreditsRunnable extends BaseRunnable<PersonCredits> {

    private final int mId;

    public FetchPersonCreditsRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public PersonCredits doBackgroundCall() throws RetrofitError {
        return getTmdbClient().personService().movieCredits(mId);
    }

    @Override
    public void onSuccess(PersonCredits result) {
        PersonWrapper person = mState.getPerson(mId);

        // TODO
        if (person != null) {
            if (!MoviesCollections.isEmpty(result.cast)) {
                List<PersonCreditWrapper> credits = new ArrayList<>();
               for (PersonCastCredit credit : result.cast) {
                    credits.add(new PersonCreditWrapper(credit));
                }
                Collections.sort(credits, PersonCreditWrapper.COMPARATOR_SORT_DATE);
                person.setCastCredits(credits);
            }

            if (!MoviesCollections.isEmpty(result.crew)) {
                List<PersonCreditWrapper> credits = new ArrayList<>();
                for (PersonCrewCredit credit : result.crew) {
                    credits.add(new PersonCreditWrapper(credit));
                }
                Collections.sort(credits, PersonCreditWrapper.COMPARATOR_SORT_DATE);
                person.setCrewCredits(credits);
            }

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


}
