package com.roodie.model.util;

/**
 * Created by Roodie on 23.09.2015.
 */
public class Tmdb {

    /**
     * Show status used when exporting data. Compare with {@link com.roodie.model.entities.ShowWrapper}.
     */
    public interface ShowStatusExport {
        String CONTINUING = "Returning Series";
        String ENDED = "Ended";
        String UNKNOWN = "Unknown";
    }

}
