package main;

import entertainment.Season;

import java.util.ArrayList;

public class Serial extends Show {
    /**
     * Number of seasons
     */
    private final int numberOfSeasons;
    /**
     * Season list
     */
    private final ArrayList<Season> seasons;

    public Serial(final String title, final ArrayList<String> cast,
                  final ArrayList<String> genres,
                  final int numberOfSeasons, final ArrayList<Season> seasons,
                  final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public final int getNumberSeason() {
        return numberOfSeasons;
    }

    public final ArrayList<Season> getSeasons() {
        return seasons;
    }

    /**
     * Calculeaza durata totala a unui serial
     * @return durata
     */
    public int getTotalDuration() {
        int duration = 0;
        for (Season s : seasons) {
            duration += s.getDuration();
        }
        return duration;
    }
}
