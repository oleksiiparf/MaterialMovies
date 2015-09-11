package com.roodie.materialmovies.views.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.PersonWrapper;

/**
 * Created by Roodie on 06.09.2015.
 */
public class SearchPeopleSectionedListAdapter extends BaseSectionedListAdapter<PersonWrapper> {

    private static final String LOG_TAG = SearchPeopleSectionedListAdapter.class.getSimpleName();

    public SearchPeopleSectionedListAdapter(Activity activity) {
        super(activity, R.layout.item_list_1line, R.layout.item_list_movie_section_header);
    }

    @Override
    protected void bindView(int position, View view, ListItem<PersonWrapper> item) {
        PersonWrapper person = item.getListItem();

        final TextView nameTextView = (TextView) view.findViewById(R.id.textview_title);
        nameTextView.setText(person.getName());

        final MMoviesImageView imageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);
        imageView.setAvatarMode(true);
        imageView.loadProfile(person);
    }
}
