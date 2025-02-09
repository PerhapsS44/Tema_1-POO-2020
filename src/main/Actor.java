package main;

import actor.ActorsAwards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Information about an actor, retrieved from parsing the input test files
 * <p>
 * DO NOT MODIFY
 */
public final class Actor {
    /**
     * awards won by the actor
     */
    private final Map<ActorsAwards, Integer> awards;
    /**
     * actor name
     */
    private String name;
    /**
     * description of the actor's career
     */
    private String careerDescription;
    /**
     * videos starring actor
     */
    private ArrayList<String> filmography;
    private double averageScore = 0;

    public Actor(final String name, final String careerDescription,
                 final ArrayList<String> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public void setFilmography(final ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public void setCareerDescription(final String careerDescription) {
        this.careerDescription = careerDescription;
    }

    public double getAverageScore() {
        return averageScore;
    }

    /**
     * Calculeaza "scorul" unui actor, camp ce va fi folosit ca parametru la sortare
     * Acesta reprezinta media ratingurilor tuturor filmelor in care a jucat
     * @param movieRatings
     */
    public void setAverageScore(final HashMap<String, Double> movieRatings) {
        double noRatings = 0.0;
        averageScore = 0;
        for (String showTitle : filmography) {
            if (movieRatings.containsKey(showTitle)) {
                if (movieRatings.get(showTitle) != 0) {
                    noRatings++;
                    averageScore += movieRatings.get(showTitle);
                }
            }
        }
        if (noRatings != 0) {
            averageScore /= noRatings;
        }
    }

    /**
     * Calculeaza numarul de premii primite in total
     * @return numarul de premii primite in total
     */
    public int getNoAwards() {
        int noAwards = 0;
        for (Map.Entry<ActorsAwards, Integer> entry : awards.entrySet()) {
            noAwards += entry.getValue();
        }
        return noAwards;
    }
}
