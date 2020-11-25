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
    /**
     * awards won by the actor
     */
    private Map<ActorsAwards, Integer> awards;

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

    public double getAverageScore(){
        return averageScore;
    }

    public void setAverageScore(HashMap<String,Double> movieRatings){
        double noRatings = 0.0;
        for (String showTitle : filmography){
            if (movieRatings.containsKey(showTitle)){
                if (movieRatings.get(showTitle) != 0){
                    noRatings++;
                    averageScore += movieRatings.get(showTitle);
                }
            }
        }
        System.out.println("   => " + name);
        System.out.println("   => " + averageScore + "   " + noRatings);
        System.out.println();
        if (noRatings != 0)
            averageScore /= noRatings;
    }

    public void setCareerDescription(final String careerDescription) {
        this.careerDescription = careerDescription;
    }

    @Override
    public String toString() {
        return "ActorInputData{"
                + "name='" + name + '\''
                + ", careerDescription='"
                + careerDescription + '\''
                + ", filmography=" + filmography + '}';
    }
}
