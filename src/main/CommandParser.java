package main;

import common.Constants;
import fileio.*;

import java.util.*;
import java.util.List;

class CommandParser
{
    // static variable single_instance of type Singleton
    private static CommandParser single_instance = null;

    ArrayList<User> users;
    ArrayList<Movie> movies;
    ArrayList<Serial> serials;

    ArrayList<Actor> actors;

    HashMap<String,Integer> movieViews;
    HashMap<String,Integer> movieFavs;

    HashMap<String,Double> movieSumRating;
    HashMap<String,Double> movieNoRatings;
    HashMap<String, Double> movieRatings;


    // private constructor restricted to this class itself
    private CommandParser()
    {
        users = new ArrayList<>();
        movies = new ArrayList<>();
        serials = new ArrayList<>();
        actors = new ArrayList<>();

        movieViews = new HashMap<>();
        movieFavs = new HashMap<>();

        movieSumRating = new HashMap<>();
        movieNoRatings = new HashMap<>();
        movieRatings = new HashMap<>();
    }

    // static method to create instance of Singleton class
    public static CommandParser getInstance()
    {
        if (single_instance == null)
            single_instance = new CommandParser();

        return single_instance;
    }

    public static void resetSingleton(){
        single_instance = new CommandParser();
    }

    public void initInstances(Input input){
        for (UserInputData userInputData : input.getUsers()){
            users.add(new User(userInputData.getUsername(), userInputData.getSubscriptionType(), userInputData.getHistory(), userInputData.getFavoriteMovies()));
        }
        for (MovieInputData movieInputData : input.getMovies()){
            movies.add(new Movie(movieInputData.getTitle(), movieInputData.getCast(), movieInputData.getGenres(), movieInputData.getYear(), movieInputData.getDuration()));
        }
        for (SerialInputData serialInputData : input.getSerials()){
            serials.add(new Serial(serialInputData.getTitle(), serialInputData.getCast(), serialInputData.getGenres(), serialInputData.getNumberSeason(), serialInputData.getSeasons(), serialInputData.getYear()));
        }
        for (ActorInputData actorInputData : input.getActors()){
            actors.add(new Actor(actorInputData.getName(), actorInputData.getCareerDescription(), actorInputData.getFilmography(), actorInputData.getAwards()));
        }
    }

    public void initDB(){

        movieViews = new HashMap<>();
        movieFavs = new HashMap<>();

        movieSumRating = new HashMap<>();
        movieNoRatings = new HashMap<>();

        for (Movie movie : movies){
            movieViews.put(movie.getTitle(), 0);
            movieFavs.put(movie.getTitle(), 0);
            movieNoRatings.put(movie.getTitle(), 0.0);
            movieSumRating.put(movie.getTitle(), 0.0);
        }
        for (Serial serial : serials){
            movieViews.put(serial.getTitle(), 0);
            movieFavs.put(serial.getTitle(), 0);
            movieNoRatings.put(serial.getTitle(), 0.0);
            movieSumRating.put(serial.getTitle(), 0.0);
            for (int i=1;i<=serial.getNumberSeason();i++){
                movieNoRatings.put(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i, 0.0);
                movieSumRating.put(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i, 0.0);
            }
        }
        // create the hashmap with <movie, totalNoViews> pairs
        for (User user : users){
            for (Map.Entry<String,Integer> entry : user.getHistory().entrySet()){
                if (movieViews.containsKey(entry.getKey()))
                    movieViews.put(entry.getKey(), movieViews.get(entry.getKey()) + entry.getValue());
                else
                    movieViews.put(entry.getKey(), entry.getValue());
            }
            for (String movie : user.getFavoriteMovies()){
                if (movieFavs.containsKey(movie))
                    movieFavs.put(movie, movieViews.get(movie) + 1);
                else
                    movieFavs.put(movie, 1);
            }
            for (HashMap.Entry<String,Double> entry : user.getShowRatings().entrySet()){
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
        for (HashMap.Entry<String,Double> entry : movieNoRatings.entrySet()){
            if (entry.getValue() != 0){
                movieRatings.put(entry.getKey(), movieSumRating.get(entry.getKey()) / entry.getValue());
            }
        }
        for (Serial serial : serials){
            boolean flag = false;
            double sumRating = 0.0;
            double noRatings = 0.0;
            if (movieSumRating.containsKey(serial.getTitle())){
                for (int i=1;i<= serial.getNumberSeason(); i++){
                    if (movieSumRating.containsKey(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i)){
                        if (movieSumRating.get(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i) != 0){
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag){
                    for (int i=1;i<= serial.getNumberSeason(); i++){
                        if (movieSumRating.containsKey(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i)){
                            if (movieSumRating.get(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i) != 0){
                                sumRating += movieSumRating.get(serial.getTitle() + Constants.SERIAL_IDENTIFIER + i);
                            }
                        }
                    }
                    movieSumRating.put(serial.getTitle(), sumRating);
                    movieNoRatings.put(serial.getTitle(), noRatings);
                    movieRatings.put(serial.getTitle(), sumRating / serial.getNumberSeason());
                }
            }
        }
    }

    public String parseCommand(ActionInputData currentCommand){
        String serialNewName;
        if (currentCommand.getType().equals(Constants.FAVORITE)) {
            // check if it exists in history
            // check if it exists in favMovies
            for(User user : users){
                if (user.getUsername().equals(currentCommand.getUsername())){
                    if (user.getHistory().containsKey(currentCommand.getTitle())){
                        if (!user.getFavoriteMovies().contains(currentCommand.getTitle())){
                            user.getFavoriteMovies().add(currentCommand.getTitle());
//                            arrayResult.add(fileWriter.writeFile(currentCommand.getActionId(),null, Constants.SUCCESS + currentCommand.getTitle()+Constants.SUCCESS_FAV));
                            return Constants.SUCCESS + currentCommand.getTitle()+Constants.SUCCESS_FAV;
                        }
                        else{
                            // it already exists in favMovies
                            return Constants.ERR + currentCommand.getTitle() + Constants.ERR_FAV_DUPLICATE;
                        }
                    }
                    else {
                        // it doesn't exist in history
                        return Constants.ERR + currentCommand.getTitle() + Constants.ERR_FAV_NOT_SEEN;
                    }
                }
            }
        }
        if (currentCommand.getType().equals(Constants.VIEW)){
            // check if it exists
            for(User user : users) {
                if (user.getUsername().equals(currentCommand.getUsername())){
                    if (user.getHistory().containsKey(currentCommand.getTitle())){
                        user.getHistory().put(currentCommand.getTitle(), user.getHistory().get(currentCommand.getTitle()) + 1);
                    }
                    else{
                        user.getHistory().put(currentCommand.getTitle(), 1);
                    }
                    return Constants.SUCCESS + currentCommand.getTitle() + Constants.SUCCESS_VIEW + user.getHistory().get(currentCommand.getTitle());
                }
            }
        }
        if (currentCommand.getType().equals(Constants.RATING)){
            for(User user : users) {
                if (user.getUsername().equals(currentCommand.getUsername())){
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
                    }
                    else{
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
    // TODO: daca imi pica teste, sa modific ordinea alfabetica cresc sau desc
    public static Comparator sortUsersAsc()
    {
        return (Comparator<User>) (s1, s2) -> {
            int crit1 = s1.getUsername().compareTo(s2.getUsername());
            int crit2 = s1.getShowRatings().size() - s2.getShowRatings().size();
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortMostViewedAsc()
    {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2 = CommandParser.getInstance().movieViews.get(m1.getTitle()) - CommandParser.getInstance().movieViews.get(m2.getTitle());
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortLongestAsc()
    {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2 = m1.getTotalDuration() - m2.getTotalDuration();
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortFavoriteAsc()
    {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2 = CommandParser.getInstance().movieFavs.get(m1.getTitle()) - CommandParser.getInstance().movieFavs.get(m2.getTitle());
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Show> sortRatingAsc()
    {
        return (m1, m2) -> {
            int crit1 = m1.getTitle().compareTo(m2.getTitle());
            int crit2;
            double rating1 = CommandParser.getInstance().movieNoRatings.get(m1.getTitle())/CommandParser.getInstance().movieSumRating.get(m1.getTitle());
            double rating2 = CommandParser.getInstance().movieNoRatings.get(m1.getTitle())/CommandParser.getInstance().movieSumRating.get(m1.getTitle());
            crit2 = (int)(rating1 - rating2);
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Actor> sortAverageAsc()
    {
        return (a1, a2) -> {
            int crit1 = a1.getName().compareTo(a2.getName());
            int crit2 = (int) (a1.getAverageScore() - a2.getAverageScore());
            if (crit2 != 0) return crit2;
            return crit1;
        };
    }

    public static Comparator<Actor> sortAwardsAsc(List<String> list)
    {
        return (a1, a2) -> {
            int critName = a1.getName().compareTo(a2.getName());
            ArrayList<Integer> crits = new ArrayList<>();
            for (String award : list){
                crits.add(a1.getAwards().get(award) - a2.getAwards().get(award));
            }
            for (Integer crit : crits){
                if (crit != 0) return crit;
            }
            return critName;
        };
    }

    public static Comparator<Actor> sortFilterDescAsc()
    {
        return Comparator.comparing(Actor::getName);
    }

    public String parseQuery(ActionInputData currentCommand) {
        String output;
        if (currentCommand.getObjectType().equals(Constants.USERS)){
            output = users_query(currentCommand.getNumber(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        if (currentCommand.getObjectType().equals(Constants.MOVIES)){

            output = movies_query(currentCommand.getNumber(), currentCommand.getFilters(), currentCommand.getCriteria(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        if (currentCommand.getObjectType().equals(Constants.SHOWS)){

            output = shows_query(currentCommand.getNumber(), currentCommand.getFilters(), currentCommand.getCriteria(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        if (currentCommand.getObjectType().equals(Constants.ACTORS)){

            output = actors_query(currentCommand.getNumber(), currentCommand.getFilters(), currentCommand.getCriteria(), currentCommand.getSortType()).toString();
            return Constants.QUERY_RES + output;
        }
        return "";
    }

    public ArrayList<String> users_query(int N, String order){
        ArrayList<User> sortedUsers = new ArrayList<>(users);
        ArrayList<String> usernames = new ArrayList<>();
        sortedUsers.sort(sortUsersAsc());
        if (order.equals(Constants.DESC))
            Collections.reverse(sortedUsers);
        for (int j=0, i=0;i < sortedUsers.size() && j<N; i++){
            if (sortedUsers.get(i).getShowRatings().size() != 0){
                usernames.add(sortedUsers.get(i).getUsername());
                j++;
            }
        }
//        System.out.println(usernames);
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

        if (criteria.equals(Constants.MOST_VIEWED)){
            sortedMovies.sort(CommandParser.sortMostViewedAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i=0, j=0; i < sortedMovies.size() && j < N; i++){
                if (movieViews.get(sortedMovies.get(i).getTitle()) != 0) {
                    movieTitles.add(sortedMovies.get(i).getTitle());
                    j++;
                }
            }

        }

        if (criteria.equals(Constants.LONGEST)){
            sortedMovies.sort(CommandParser.sortLongestAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i=0, j=0; i < sortedMovies.size() && j < N; i++){
                movieTitles.add(sortedMovies.get(i).getTitle());
                j++;
            }
        }

        if (criteria.equals(Constants.FAVORITE)){
            sortedMovies.sort(CommandParser.sortFavoriteAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i=0, j=0; i < sortedMovies.size() && j < N; i++){
                movieTitles.add(sortedMovies.get(i).getTitle());
                j++;
            }
        }

        if (criteria.equals(Constants.RATING)){
            sortedMovies.sort(CommandParser.sortRatingAsc());
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i=0, j=0; i < sortedMovies.size() && j < N; i++){
                if (movieNoRatings.get(sortedMovies.get(i).getTitle()) != 0) {
                    movieTitles.add(sortedMovies.get(i).getTitle());
                    j++;
                }
            }
        }

//        System.out.println(movieTitles);
        return movieTitles;
    }

    private ArrayList<String> shows_query(int number, List<List<String>> filters, String criteria, String sortType){
        ArrayList<String> showTitles = new ArrayList<>();
        ArrayList<Show> sortedShows = new ArrayList<>(movies);
        sortedShows.addAll(serials);

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

        if (criteria.equals(Constants.MOST_VIEWED)){
            sortedShows.sort(CommandParser.sortMostViewedAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i=0, j=0; i < sortedShows.size() && j < number; i++){
                if (movieViews.get(sortedShows.get(i).getTitle()) != 0) {
                    showTitles.add(sortedShows.get(i).getTitle());
                    j++;
                }
            }

        }

        if (criteria.equals(Constants.LONGEST)){
            sortedShows.sort(CommandParser.sortLongestAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i=0, j=0; i < sortedShows.size() && j < number; i++){
                showTitles.add(sortedShows.get(i).getTitle());
                j++;
            }
        }

        if (criteria.equals(Constants.FAVORITE)){ // TODO: aici am eroare
            System.out.println(sortedShows);
            System.out.println();
            System.out.println(movieFavs);
            sortedShows.sort(CommandParser.sortFavoriteAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i=0, j=0; i < sortedShows.size() && j < number; i++){
                if (movieFavs.get(sortedShows.get(i).getTitle()) != 0) {
                    showTitles.add(sortedShows.get(i).getTitle());
                    j++;
                }
            }
        }

        if (criteria.equals(Constants.RATING)){
            sortedShows.sort(CommandParser.sortRatingAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedShows);

            for (int i=0, j=0; i < sortedShows.size() && j < number; i++){
                if (movieNoRatings.get(sortedShows.get(i).getTitle()) != 0) {
                    showTitles.add(sortedShows.get(i).getTitle());
                    j++;
                }
            }
        }
//        System.out.println(showTitles);

        return showTitles;
    }

    private ArrayList<String> actors_query(int number, List<List<String>> filters, String criteria, String sortType){
        initDB();
        ArrayList<Actor> sortedActors = new ArrayList<>(actors);
        ArrayList<String> actorNames = new ArrayList<>();
        if (criteria.equals(Constants.AVERAGE)){
            for (Actor actor : sortedActors){
                actor.setAverageScore(movieRatings);
            }
            sortedActors.sort(sortAverageAsc());
            if (sortType.equals(Constants.DESC)){
                Collections.reverse(sortedActors);
            }

//            System.out.println(movieNoRatings);
//            System.out.println(movieSumRating);
//            System.out.println(movieRatings);
//            System.out.println(sortedActors);
            for (int i=0, j=0;i<sortedActors.size() && j < number;i++){
                if (sortedActors.get(i).getAverageScore() != 0) {
//                    System.out.println(sortedActors.get(i).getName());
//                    System.out.println(sortedActors.get(i).getAverageScore());
                    actorNames.add(sortedActors.get(i).getName());
                    j++;
                }
            }
        }
        if (criteria.equals(Constants.AWARDS)){
            sortedActors.removeIf((a)->{
                for (String award : filters.get(3)){
                    if (!a.getAwards().containsKey(award)){
                        return true;
                    }
                }
                return false;
            });

            sortedActors.sort(sortAwardsAsc(filters.get(3)));
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedActors);
            for (Actor actor : sortedActors){
                actorNames.add(actor.getName());
            }
        }
        // TODO: aici o sa am probleme la testele mari
        if (criteria.equals(Constants.FILTER_DESCRIPTIONS)){
            sortedActors.removeIf((a)->{
                for (String award : filters.get(2)){
                    if (!a.getCareerDescription().toLowerCase().contains(award)){
                        return true;
                    }
                }
                return false;
            });
            sortedActors.sort(sortFilterDescAsc());
            if (sortType.equals(Constants.DESC))
                Collections.reverse(sortedActors);
            for (Actor actor : sortedActors){
                actorNames.add(actor.getName());
            }
        }
        return actorNames;
    }

}
