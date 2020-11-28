package main;

import actor.ActorsAwards;
import common.Constants;
import entertainment.Genre;
import fileio.*;

import java.util.*;

class CommandParser {
    // static variable single_instance of type Singleton
    private static CommandParser single_instance = null;

    ArrayList<User> users;
    ArrayList<Movie> movies;
    ArrayList<Serial> serials;

    ArrayList<Actor> actors;

    HashMap<String, Integer> movieViews;
    HashMap<String, Integer> movieFavs;

    HashMap<String, Double> movieSumRating;
    HashMap<String, Double> movieNoRatings;
    HashMap<String, Double> movieRatings;

    HashMap<String, Integer> genreRating;
    HashMap<String, ArrayList<Show>> genreMovies;


    // private constructor restricted to this class itself
    private CommandParser() {
        users = new ArrayList<>();
        movies = new ArrayList<>();
        serials = new ArrayList<>();
        actors = new ArrayList<>();

        movieViews = new HashMap<>();
        movieFavs = new HashMap<>();

        movieSumRating = new HashMap<>();
        movieNoRatings = new HashMap<>();
        movieRatings = new HashMap<>();

        genreRating = new HashMap<>();
        genreMovies = new HashMap<>();
    }

    // static method to create instance of Singleton class
    public static CommandParser getInstance() {
        if (single_instance == null)
            single_instance = new CommandParser();

        return single_instance;
    }

    public static void resetSingleton() {
        single_instance = new CommandParser();
    }

    public static Comparator<User> sortUsersAsc() {
        return (Comparator<User>) (s1, s2) -> {
            int crit1 = s1.getUsername().compareTo(s2.getUsername());
            int crit2 = s1.getShowRatings().size() - s2.getShowRatings().size();
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortMostViewedAsc() {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2 = CommandParser.getInstance().movieViews.get(m1.getTitle()) - CommandParser.getInstance().movieViews.get(m2.getTitle());
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortLongestAsc() {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2 = m1.getTotalDuration() - m2.getTotalDuration();
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortFavoriteAsc() {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2 = CommandParser.getInstance().movieFavs.get(m1.getTitle()) - CommandParser.getInstance().movieFavs.get(m2.getTitle());
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortFavorite(ArrayList<Show> movieDB) {
        return (m1, m2) -> {
            int crit1 = movieDB.indexOf(m1) - movieDB.indexOf(m2);
            int crit2 = CommandParser.getInstance().movieFavs.get(m2.getTitle()) - CommandParser.getInstance().movieFavs.get(m1.getTitle());
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortRatingAsc() {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2;
            double rating1 = CommandParser.getInstance().movieNoRatings.get(m1.getTitle()) / CommandParser.getInstance().movieSumRating.get(m1.getTitle());
            double rating2 = CommandParser.getInstance().movieNoRatings.get(m1.getTitle()) / CommandParser.getInstance().movieSumRating.get(m1.getTitle());
            crit2 = (int) (rating1 - rating2);
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Actor> sortAverageAsc() {
        return (a1, a2) -> {
            int crit1 = a1.getName().compareTo(a2.getName());
            int crit2 = (int) ((a1.getAverageScore() - a2.getAverageScore()) * 100.0);
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Actor> sortAwardsAsc(List<String> list) {
        return (a1, a2) -> {
            int critName = a1.getName().compareTo(a2.getName());
            ArrayList<Integer> crits = new ArrayList<>();
            crits.add(a1.getNoAwards() - a2.getNoAwards());
            for (Integer crit : crits) {
                if (crit != 0) return crit;
            }
            return critName;
        };
    }

    public static Comparator<Actor> sortFilterDescAsc() {
        return Comparator.comparing(Actor::getName);
    }

    public static Comparator<Show> sortBestUnseen(ArrayList<Show> movieDB) {
        return (m1, m2) -> {
            int crit1 = movieDB.indexOf(m1) - movieDB.indexOf(m2);
            int crit2;
            double rating1 = CommandParser.getInstance().movieRatings.get(m1.getTitle());
            double rating2 = CommandParser.getInstance().movieRatings.get(m2.getTitle());
            crit2 = (int) ((rating1 - rating2) * 100);
            if (crit2 != 0) return -crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortPopular(ArrayList<Show> movieDB, HashMap<String, Integer> hashMap) {
        return (m1, m2) -> {
            int crit1 = movieDB.indexOf(m1) - movieDB.indexOf(m2);
            int crit2;
            double rating1 = CommandParser.getInstance().movieNoRatings.get(m1.getTitle()) / CommandParser.getInstance().movieSumRating.get(m1.getTitle());
            double rating2 = CommandParser.getInstance().movieNoRatings.get(m1.getTitle()) / CommandParser.getInstance().movieSumRating.get(m1.getTitle());
            crit2 = (int) (rating1 - rating2);
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<String> sortGenres(HashMap<String, Integer> hashMap) {
        return Comparator.comparingInt(hashMap::get);
    }

    public void initInstances(Input input) {
        for (UserInputData userInputData : input.getUsers()) {
            users.add(new User(userInputData.getUsername(), userInputData.getSubscriptionType(), userInputData.getHistory(), userInputData.getFavoriteMovies()));
        }
        for (MovieInputData movieInputData : input.getMovies()) {
            movies.add(new Movie(movieInputData.getTitle(), movieInputData.getCast(), movieInputData.getGenres(), movieInputData.getYear(), movieInputData.getDuration()));
        }
        for (SerialInputData serialInputData : input.getSerials()) {
            serials.add(new Serial(serialInputData.getTitle(), serialInputData.getCast(), serialInputData.getGenres(), serialInputData.getNumberSeason(), serialInputData.getSeasons(), serialInputData.getYear()));
        }
        for (ActorInputData actorInputData : input.getActors()) {
            actors.add(new Actor(actorInputData.getName(), actorInputData.getCareerDescription(), actorInputData.getFilmography(), actorInputData.getAwards()));
        }
    }

    public void initDB() {

        movieViews = new HashMap<>();
        movieFavs = new HashMap<>();

        movieSumRating = new HashMap<>();
        movieNoRatings = new HashMap<>();

        movieRatings = new HashMap<>();

        genreRating = new HashMap<>();
        genreMovies = new HashMap<>();

        for (Movie movie : movies) {
            movieViews.put(movie.getTitle(), 0);
            movieFavs.put(movie.getTitle(), 0);
            movieNoRatings.put(movie.getTitle(), 0.0);
            movieSumRating.put(movie.getTitle(), 0.0);
            movieRatings.put(movie.getTitle(), 0.0);
        }
        for (Serial serial : serials) {
            movieViews.put(serial.getTitle(), 0);
            movieFavs.put(serial.getTitle(), 0);
            movieNoRatings.put(serial.getTitle(), 0.0);
            movieSumRating.put(serial.getTitle(), 0.0);
            movieRatings.put(serial.getTitle(), 0.0);
            for (int i = 1; i <= serial.getNumberSeason(); i++) {
                movieNoRatings.put(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i, 0.0);
                movieSumRating.put(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i, 0.0);
            }
        }
        // create the hashmap with <movie, totalNoViews> pairs
        for (User user : users) {
            for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
                if (movieViews.containsKey(entry.getKey()))
                    movieViews.put(entry.getKey(), movieViews.get(entry.getKey()) + entry.getValue());
                else
                    movieViews.put(entry.getKey(), entry.getValue());
            }
            for (String movie : user.getFavoriteMovies()) {
                if (movieFavs.containsKey(movie))
                    movieFavs.put(movie, movieFavs.get(movie) + 1);
                else
                    movieFavs.put(movie, 1);
            }
            for (HashMap.Entry<String, Double> entry : user.getShowRatings().entrySet()) {
                if (entry.getKey() != null) {
                    if (movieSumRating.containsKey(entry.getKey())) {
                        movieSumRating.put(entry.getKey(), movieSumRating.get(entry.getKey()) + entry.getValue());
                        movieNoRatings.put(entry.getKey(), movieNoRatings.get(entry.getKey()) + 1);
                    } else {
                        movieSumRating.put(entry.getKey(), entry.getValue());
                        movieNoRatings.put(entry.getKey(), 1.0);
                    }
                }
            }
        }
        for (HashMap.Entry<String, Double> entry : movieNoRatings.entrySet()) {
            if (entry.getValue() != 0) {
                movieRatings.put(entry.getKey(), movieSumRating.get(entry.getKey()) / entry.getValue());
            }
        }
        for (Serial serial : serials) {
            boolean flag = false;
            double sumRating = 0.0;
            double noRatings = 0.0;
            if (movieSumRating.containsKey(serial.getTitle())) {
                for (int i = 1; i <= serial.getNumberSeason(); i++) {
                    if (movieSumRating.containsKey(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i)) {
                        if (movieSumRating.get(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i) != 0) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    for (int i = 1; i <= serial.getNumberSeason(); i++) {
                        if (movieSumRating.containsKey(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i)) {
                            if (movieSumRating.get(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i) != 0) {
                                sumRating += movieSumRating.get(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i);
                                noRatings++;
                            }
                        }
                    }
                    movieSumRating.put(serial.getTitle(), sumRating);
                    movieNoRatings.put(serial.getTitle(), noRatings);
                    movieRatings.put(serial.getTitle(), sumRating / serial.getNumberSeason());
                }
            }
        }

        for (Genre genre : Genre.values()) {
            genreRating.put(genre.toString().replace('_', ' ').replace('-', ' ').replace("& ", ""), 0);
        }
        for (Genre genre : Genre.values()) {
            genreMovies.put(genre.toString().replace('_', ' ').replace('-', ' ').replace("& ", ""), new ArrayList<>());
        }
        for (Movie movie : movies) {
            for (String genre : movie.getGenres()) {
                genreRating.put(genre.toUpperCase().replace('-', ' ').replace("& ", ""), genreRating.get(genre.toUpperCase().replace('-', ' ').replace("& ", "")) + movieViews.get(movie.getTitle()));
            }
        }
        for (Serial serial : serials) {
            for (String genre : serial.getGenres()) {
                genreRating.put(genre.toUpperCase().replace('-', ' ').replace("& ", ""), genreRating.get(genre.toUpperCase().replace('-', ' ').replace("& ", "")) + movieViews.get(serial.getTitle()));
            }
        }

        for (Movie movie : movies) {
            for (String genre : movie.getGenres()) {
                genreMovies.get(genre.toUpperCase().replace('-', ' ').replace("& ", "")).add(movie);
            }
        }

        for (Serial serial : serials) {
            for (String genre : serial.getGenres()) {
                genreMovies.get(genre.toUpperCase().replace('-', ' ').replace("& ", "")).add(serial);
            }
        }
    }

    public String parseCommand(ActionInputData currentCommand) {
        String serialNewName;
        if (currentCommand.getType().equals(Constants.FAVORITE)) {
            // check if it exists in history
            // check if it exists in favMovies
            for (User user : users) {
                if (user.getUsername().equals(currentCommand.getUsername())) {
                    if (user.getHistory().containsKey(currentCommand.getTitle())) {
                        if (!user.getFavoriteMovies().contains(currentCommand.getTitle())) {
                            user.getFavoriteMovies().add(currentCommand.getTitle());
//                            arrayResult.add(fileWriter.writeFile(currentCommand.getActionId(),null, Constants.SUCCESS + currentCommand.getTitle()+Constants.SUCCESS_FAV));
                            return Constants.SUCCESS + currentCommand.getTitle() + Constants.SUCCESS_FAV;
                        } else {
                            // it already exists in favMovies
                            return Constants.ERR + currentCommand.getTitle() + Constants.ERR_FAV_DUPLICATE;
                        }
                    } else {
                        // it doesn't exist in history
                        return Constants.ERR + currentCommand.getTitle() + Constants.ERR_FAV_NOT_SEEN;
                    }
                }
            }
        }
        if (currentCommand.getType().equals(Constants.VIEW)) {
            // check if it exists
            for (User user : users) {
                if (user.getUsername().equals(currentCommand.getUsername())) {
                    if (user.getHistory().containsKey(currentCommand.getTitle())) {
                        user.getHistory().put(currentCommand.getTitle(), user.getHistory().get(currentCommand.getTitle()) + 1);
                    } else {
                        user.getHistory().put(currentCommand.getTitle(), 1);
                    }
                    return Constants.SUCCESS + currentCommand.getTitle() + Constants.SUCCESS_VIEW + user.getHistory().get(currentCommand.getTitle());
                }
            }
        }
        if (currentCommand.getType().equals(Constants.RATING)) {
            for (User user : users) {
                if (user.getUsername().equals(currentCommand.getUsername())) {
                    if (currentCommand.getSeasonNumber() == 0) {
                        if (user.getShowRatings().containsKey(currentCommand.getTitle())) {
                            // already rated
                            return Constants.ERR + currentCommand.getTitle() + Constants.ERR_RATING_DUPLICATE;
                        } else {
                            if (user.getHistory().containsKey(currentCommand.getTitle())) {
                                // good
                                user.getShowRatings().put(currentCommand.getTitle(), currentCommand.getGrade());
                                return Constants.SUCCESS + currentCommand.getTitle() + Constants.SUCCESS_RATING_1 + currentCommand.getGrade() + Constants.BY + currentCommand.getUsername();
                                // " was rated with 6.0 by insecureEagle8";
                            } else {
                                // not viewed
                                return Constants.ERR + currentCommand.getTitle() + Constants.ERR_RATING_NOT_SEEN;
                            }
                        }
                    } else {
                        // serial
                        serialNewName = currentCommand.getTitle() + Constants.SERIAL_IDENTIFIER + currentCommand.getSeasonNumber();
                        if (user.getShowRatings().containsKey(serialNewName)) {
                            // already rated
                            return Constants.ERR + currentCommand.getTitle() + Constants.ERR_RATING_DUPLICATE;
                        } else {
                            if (user.getHistory().containsKey(currentCommand.getTitle())) {
                                // good
                                user.getShowRatings().put(serialNewName, currentCommand.getGrade());
                                return Constants.SUCCESS + currentCommand.getTitle() + Constants.SUCCESS_RATING_1 + currentCommand.getGrade() + Constants.BY + currentCommand.getUsername();
                                // " was rated with 6.0 by insecureEagle8";
                            } else {
                                // not viewed
                                return Constants.ERR + currentCommand.getTitle() + Constants.ERR_RATING_NOT_SEEN;
                            }
                        }
                    }
                }
            }
        }
        return "0";
    }

    public String parseQuery(ActionInputData currentCommand) {
        String output;
        if (currentCommand.getObjectType().equals(Constants.USERS)) {
            output = users_query(currentCommand.getNumber(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        if (currentCommand.getObjectType().equals(Constants.MOVIES)) {

            output = movies_query(currentCommand.getNumber(), currentCommand.getFilters(), currentCommand.getCriteria(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        if (currentCommand.getObjectType().equals(Constants.SHOWS)) {

            output = shows_query(currentCommand.getNumber(), currentCommand.getFilters(), currentCommand.getCriteria(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        if (currentCommand.getObjectType().equals(Constants.ACTORS)) {

            output = actors_query(currentCommand.getNumber(), currentCommand.getFilters(), currentCommand.getCriteria(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        return "";
    }

    public ArrayList<String> users_query(int N, String order) {
        ArrayList<User> sortedUsers = new ArrayList<>(users);
        ArrayList<String> usernames = new ArrayList<>();
        sortedUsers.sort(sortUsersAsc());
        if (order.equals(Constants.DESC))
            Collections.reverse(sortedUsers);
        for (int j = 0, i = 0; i < sortedUsers.size() && j < N; i++) {
            if (sortedUsers.get(i).getShowRatings().size() != 0) {
                usernames.add(sortedUsers.get(i).getUsername());
                j++;
            }
        }
        return usernames;
    }

    private ArrayList<String> movies_query(int N, List<List<String>> filters, String criteria, String type) {
        ArrayList<Movie> sortedMovies = new ArrayList<>(movies);
        ArrayList<String> movieTitles = new ArrayList<>();

        initDB();
        // filter sortedMovies
        sortedMovies.removeIf((v) -> {
            if (filters.get(0).get(0) != null)
                if (!Integer.toString(v.getYear()).equals(filters.get(0).get(0)))
                    return true;
            if (filters.get(1).get(0) != null)
                return !v.getGenres().contains(filters.get(1).get(0));
            return false;
        });

        if (criteria.equals(Constants.MOST_VIEWED)) {
            sortedMovies.sort(CommandParser.sortMostViewedAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i = 0, j = 0; i < sortedMovies.size() && j < N; i++) {
                if (movieViews.get(sortedMovies.get(i).getTitle()) != 0) {
                    movieTitles.add(sortedMovies.get(i).getTitle());
                    j++;
                }
            }

        }

        if (criteria.equals(Constants.LONGEST)) {
            sortedMovies.sort(CommandParser.sortLongestAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i = 0, j = 0; i < sortedMovies.size() && j < N; i++) {
                movieTitles.add(sortedMovies.get(i).getTitle());
                j++;
            }
        }

        if (criteria.equals(Constants.FAVORITE)) {
            sortedMovies.sort(CommandParser.sortFavoriteAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i = 0, j = 0; i < sortedMovies.size() && j < N; i++) {
                movieTitles.add(sortedMovies.get(i).getTitle());
                j++;
            }
        }

        if (criteria.equals(Constants.RATINGS)) {
            sortedMovies.sort(CommandParser.sortRatingAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i = 0, j = 0; i < sortedMovies.size() && j < N; i++) {
                if (movieNoRatings.get(sortedMovies.get(i).getTitle()) != 0) {
                    movieTitles.add(sortedMovies.get(i).getTitle());
                    j++;
                }
            }
        }
        return movieTitles;
    }

    private ArrayList<String> shows_query(int number, List<List<String>> filters, String criteria, String sortType) { // show == seriale
        ArrayList<String> showTitles = new ArrayList<>();
        ArrayList<Show> sortedShows = new ArrayList<>(serials);

        initDB();

        // filter the movies
        sortedShows.removeIf((v) -> {
            if (filters.get(0).get(0) != null)
                if (!Integer.toString(v.getYear()).equals(filters.get(0).get(0)))
                    return true;
            if (filters.get(1).get(0) != null)
                return !v.getGenres().contains(filters.get(1).get(0));
            return false;
        });

        if (criteria.equals(Constants.MOST_VIEWED)) {
            sortedShows.sort(CommandParser.sortMostViewedAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i = 0, j = 0; i < sortedShows.size() && j < number; i++) {
                if (movieViews.get(sortedShows.get(i).getTitle()) != 0) {
                    showTitles.add(sortedShows.get(i).getTitle());
                    j++;
                }
            }

        }

        if (criteria.equals(Constants.LONGEST)) {
            sortedShows.sort(CommandParser.sortLongestAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i = 0, j = 0; i < sortedShows.size() && j < number; i++) {
                showTitles.add(sortedShows.get(i).getTitle());
                j++;
            }
        }

        if (criteria.equals(Constants.FAVORITE)) {
            sortedShows.sort(CommandParser.sortFavoriteAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i = 0, j = 0; i < sortedShows.size() && j < number; i++) {
                if (movieFavs.get(sortedShows.get(i).getTitle()) != 0) {
                    showTitles.add(sortedShows.get(i).getTitle());
                    j++;
                }
            }
        }

        if (criteria.equals(Constants.RATINGS)) {
            sortedShows.sort(CommandParser.sortRatingAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i = 0, j = 0; i < sortedShows.size() && j < number; i++) {
                if (movieNoRatings.get(sortedShows.get(i).getTitle()) != 0) {
                    showTitles.add(sortedShows.get(i).getTitle());
                    j++;
                }
            }
        }

        return showTitles;
    }

    private ArrayList<String> actors_query(int number, List<List<String>> filters, String criteria, String sortType) {
        initDB();
        ArrayList<Actor> sortedActors = new ArrayList<>(actors);
        ArrayList<String> actorNames = new ArrayList<>();
        if (criteria.equals(Constants.AVERAGE)) {
            for (Actor actor : sortedActors) {
                actor.setAverageScore(movieRatings);
            }
            sortedActors.sort(sortAverageAsc());
            if (sortType.equals(Constants.DESC)) {
                Collections.reverse(sortedActors);
            }
            for (int i = 0, j = 0; i < sortedActors.size() && j < number; i++) {
                if (sortedActors.get(i).getAverageScore() != 0) {
                    actorNames.add(sortedActors.get(i).getName());
                    j++;
                }
            }
        }
        if (criteria.equals(Constants.AWARDS)) {
            sortedActors.removeIf((a) -> {
                for (String award : filters.get(3)) {
                    if (!a.getAwards().containsKey(ActorsAwards.valueOf(award))) {
                        return true;
                    }
                }
                return false;
            });

            sortedActors.sort(sortAwardsAsc(filters.get(3)));
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedActors);
            for (Actor actor : sortedActors) {
                actorNames.add(actor.getName());
            }
        }
        // TODO: aici o sa am probleme la testele mari
        if (criteria.equals(Constants.FILTER_DESCRIPTIONS)) {
            sortedActors.removeIf((a) -> {
                for (String word : filters.get(2)) {

                    if (!a.getCareerDescription().replace('\n', ' ').toLowerCase().matches(new StringBuilder().append(".*?\\b").append(word).append("\\b.*?").toString())) {
                        return true;
                    }
                }
                return false;
            });
            sortedActors.sort(sortFilterDescAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedActors);
            for (Actor actor : sortedActors) {
                actorNames.add(actor.getName());
            }
        }
        return actorNames;
    }

    public String parseRecomandation(ActionInputData currentCommand) {
        // parseRecomandation
        String output;
        if (currentCommand.getType().equals(Constants.STANDARD)) {
            output = standardRecomandation(currentCommand.getUsername());
            if (output != null)
                return Constants.OUT_STANDARD + Constants.OUT_RECOMANDATION + output;
            else
                return Constants.OUT_STANDARD + Constants.ERR_RECOMMENDATION;
        }
        if (currentCommand.getType().equals(Constants.BEST_UNSEEN)) {
            output = bestUnseenRecomandation(currentCommand.getUsername());
            if (output != null)
                return Constants.OUT_BEST_UNSEEN + Constants.OUT_RECOMANDATION + output;
            else
                return Constants.OUT_BEST_UNSEEN + Constants.ERR_RECOMMENDATION;
        }
        if (currentCommand.getType().equals(Constants.POPULAR)) {
            output = popularRecomandation(currentCommand.getUsername());
            if (output != null)
                return Constants.OUT_POPULAR + Constants.OUT_RECOMANDATION + output;
            else
                return Constants.OUT_POPULAR + Constants.ERR_RECOMMENDATION;
        }
        if (currentCommand.getType().equals(Constants.FAVORITE)) {
            output = favoriteRecomandation(currentCommand.getUsername());
            if (output != null)
                return Constants.OUT_FAVORITE + Constants.OUT_RECOMANDATION + output;
            else
                return Constants.OUT_FAVORITE + Constants.ERR_RECOMMENDATION;
        }
        if (currentCommand.getType().equals(Constants.SEARCH)) {
            output = searchRecomandation(currentCommand.getUsername(), currentCommand.getGenre());
            if (output != null)
                return Constants.OUT_SEARCH + Constants.OUT_RECOMANDATION + output;
            else
                return Constants.OUT_SEARCH + Constants.ERR_RECOMMENDATION;
        }
        return "";
    }

    public String standardRecomandation(String username) {
        ArrayList<Show> showDB = new ArrayList<>(movies);
        showDB.addAll(serials);
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                showDB.removeIf((movie -> {
                    return user.getHistory().containsKey(movie.getTitle());
                }));
                if (showDB.size() > 0)
                    return showDB.get(0).getTitle();
            }
        }
        return null;
    }

    public String bestUnseenRecomandation(String username) {
        ArrayList<Show> showDB = new ArrayList<>(movies);
        showDB.addAll(serials);

        ArrayList<Show> listOfShows = new ArrayList<>(movies);
        listOfShows.addAll(serials);
        initDB();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                showDB.removeIf((movie -> {
                    return user.getHistory().containsKey(movie.getTitle());
                }));
                showDB.sort(sortBestUnseen(listOfShows));
                if (showDB.size() > 0)
                    return showDB.get(0).getTitle();
            }
        }
        return null;
    }

    public String popularRecomandation(String username) {
        ArrayList<Show> showDB = new ArrayList<>(movies);
        showDB.addAll(serials);
        ArrayList<String> genres = new ArrayList<>();
        HashMap<String, ArrayList<Show>> genreShows = new HashMap<>(genreMovies);
        initDB();
        for (Genre genre : Genre.values()) {
            genres.add(genre.toString().replace('_', ' '));
        }
        for (String genre : genres) {
            genreShows.put(genre, new ArrayList<>(genreMovies.get(genre)));
        }
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (!user.getSubscriptionType().equals(Constants.PREMIUM)) {
                    return null;
                }
                for (String genre : genres) {
                    genreShows.get(genre.toUpperCase().replace('-', ' ')
                            .replace("& ", ""))
                            .removeIf((s) -> user.getHistory().containsKey(s.getTitle()));
                }
                genres.sort(sortGenres(genreRating));
                Collections.reverse(genres);


                for (String genre : genres) {
                    if (genreShows.get(genre).size() != 0) {
                        return genreShows.get(genre).get(0).getTitle();
                    }
                }
            }
        }
        return null;
    }

    public String favoriteRecomandation(String username) {
        ArrayList<Show> showDB = new ArrayList<>(movies);
        showDB.addAll(serials);
        ArrayList<Show> shows = new ArrayList<>(movies);
        shows.addAll(serials);
        initDB();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (!user.getSubscriptionType().equals(Constants.PREMIUM)) {
                    return null;
                }
                showDB.removeIf((m) -> {
                    return user.getHistory().containsKey(m.getTitle());
                });
                showDB.removeIf((m) -> {
                    return movieFavs.get(m.getTitle()) == 0;
                });
                showDB.sort(sortFavorite(shows));
                if (showDB.size() > 0)
                    return showDB.get(0).getTitle();
            }
        }
        return null;
    }

    public String searchRecomandation(String username, String genre) {
        ArrayList<Show> showDB = new ArrayList<>(movies);
        ArrayList<String> showsTitles = new ArrayList<>();
        showDB.addAll(serials);
        initDB();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (!user.getSubscriptionType().equals(Constants.PREMIUM)) {
                    return null;
                }
                showDB.removeIf((s) -> {
                    return user.getHistory().containsKey(s.getTitle());
                });
                showDB.removeIf((s) -> {
                    return !s.getGenres().contains(genre);
                });
                showDB.sort(sortRatingAsc());
                if (showDB.size() == 0)
                    return null;
                for (Show show : showDB) {
                    showsTitles.add(show.getTitle());
                }
                return showsTitles.toString();
            }
        }
        return null;
    }

}
