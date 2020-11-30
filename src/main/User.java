package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    /**
     * User's username
     */
    private final String username;
    /**
     * Subscription Type
     */
    private final String subscriptionType;
    /**
     * The history of the movies seen
     */
    private final Map<String, Integer> history;
    /**
     * Movies added to favorites
     */
    private final ArrayList<String> favoriteMovies;
    private final HashMap<String, Double> showRatings;

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history,
                final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.favoriteMovies = favoriteMovies;
        this.history = history;
        this.showRatings = new HashMap<>();
    }

    public final String getUsername() {
        return username;
    }

    public final Map<String, Integer> getHistory() {
        return history;
    }

    public final String getSubscriptionType() {
        return subscriptionType;
    }

    public final ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public final HashMap<String, Double> getShowRatings() {
        return showRatings;
    }

}
