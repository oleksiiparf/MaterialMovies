package com.roodie.model.entities;

/**
 * Created by Roodie on 13.08.2015.
 */
import com.uwetrottmann.tmdb.entities.Genre;
import com.uwetrottmann.tmdb.entities.TvSeason;

import java.util.Date;
import java.util.List;

public class TvShow {

    public Integer id;
    public String original_name;
    public String name;
    public List<String> origin_country;
    public Date first_air_date;
    public Date last_air_date;
    public List<Genre> genres;
    public String overview;
    public Integer number_of_episodes;
    public Integer number_of_seasons;
    public String backdrop_path;
    public String poster_path;
    public Double popularity;
    public Double vote_average;
    public Integer vote_count;
    public List<TvSeason> seasons;

}