package main;

import java.util.ArrayList;

public final class Movie extends Show {
    /**
     * Duration in minutes of a season
     */
    private final int duration;

    public Movie(final String title, final ArrayList<String> cast,
                 final ArrayList<String> genres, final int year,
                 final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int getTotalDuration() {
        return duration;
    }

}
