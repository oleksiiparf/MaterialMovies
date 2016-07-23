package com.roodie.materialmovies.views.fragments;

import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.mvp.presenters.ListPeoplePresenter;
import com.roodie.materialmovies.mvp.views.ListPeopleView;
import com.roodie.materialmovies.views.adapters.PeopleListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseListFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.PersonWrapper;

import java.util.List;

/**
 * Created by Roodie on 07.09.2015.
 */

public class SearchPeopleListFragment extends BaseListFragment<PeopleListAdapter.PeopleListViewHolder, PersonWrapper> implements ListPeopleView {

    @InjectPresenter
    ListPeoplePresenter mPresenter;

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.SEARCH_PEOPLE;
    }

    @Override
    protected easyRegularAdapter<PersonWrapper, PeopleListAdapter.PeopleListViewHolder> createAdapter(List<PersonWrapper> data) {
        return new PeopleListAdapter(data, this);
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void onClick(View view, int position) {
        PersonWrapper item = mAdapter.getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void showItemDetail(PersonWrapper person, View view) {
        Preconditions.checkNotNull(person, "person cannot be null");
        Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), null);
        }

    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }
}
