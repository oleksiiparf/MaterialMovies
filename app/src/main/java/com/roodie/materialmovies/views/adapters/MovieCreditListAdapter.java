/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roodie.materialmovies.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.model.entities.MovieCreditWrapper;

import java.util.List;

public class MovieCreditListAdapter extends RecyclerView.Adapter<MovieCreditListAdapter.ViewHolder> {

    List<MovieCreditWrapper> mItems;

    private static final String LOG_TAG = MovieCreditListAdapter.class.getSimpleName();

    public MovieCreditListAdapter(List<MovieCreditWrapper> items) {
       mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_2line, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        MovieCreditWrapper credit = mItems.get(i);
        viewHolder.nameTextView.setText(credit.getPerson().getName());
        viewHolder.characterTextView.setText(credit.getJob());
        //viewHolder.imageView. loadProfile()
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView characterTextView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.title);
            characterTextView = (TextView) itemView.findViewById(R.id.subtitle_1);
            imageView = (ImageView) itemView.findViewById(R.id.poster);

        }
    }
}
