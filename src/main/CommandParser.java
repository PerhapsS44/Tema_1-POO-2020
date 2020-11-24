package main;

import common.Constants;
import fileio.*;

import java.util.*;

class CommandParser
{
    // static variable single_instance of type Singleton
    private static CommandParser single_instance = null;

    ArrayList<User> users;
    ArrayList<Movie> movies;
    ArrayList<Serial> serials;

    HashMap<String,Integer> movieViews;
    HashMap<String,Integer> movieFavs;

    HashMap<String,Double> movieSumRating;
    HashMap<String,Double> movieNoRatings;


    // private constructor restricted to this class itself
    private CommandParser()
    {
        users = new ArrayList<User>();
        movies = new ArrayList<Movie>();
        serials = new ArrayList<Serial>();

        movieViews = new HashMap<String, Integer>();
        movieFavs = new HashMap<String, Integer>();

        movieSumRating = new HashMap<String,Double>();
        movieNoRatings = new HashMap<String,Double>();
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
        for (
                UserInputData userInputData : input.getUsers()){
            users.add(new User(userInputData.getUsername(), userInputData.getSubscriptionType(), userInputData.getHistory(), userInputData.getFavoriteMovies()));
        }
        for (
                MovieInputData movieInputData : input.getMovies()){
            movies.add(new Movie(movieInputData.getTitle(), movieInputData.getCast(), movieInputData.getGenres(), movieInputData.getYear(), movieInputData.getDuration()));
        }
        for (
                SerialInputData serialInputData : input.getSerials()){
            serials.add(new Serial(serialInputData.getTitle(), serialInputData.getCast(), serialInputData.getGenres(), serialInputData.getNumberSeason(), serialInputData.getSeasons(), serialInputData.getYear()));
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
    public static Comparator<User> sortUsersAsc()
    {
        Comparator comp = new Comparator<User>(){
            @Override
            public int compare(User s1, User s2)
            {
                int crit1 = s1.getUsername().compareTo(s2.getUsername());
                int crit2 = s1.getShowRatings().size() - s2.getShowRatings().size();
                if (crit2 != 0) return crit2;
                return crit1;
            }
        };
        return comp;
    }

    public static Comparator<Movie> sortMostViewedAsc()
    {
        Comparator comp = new Comparator<Movie>(){
            @Override
            public int compare(Movie m1, Movie m2)
            {
                int crit1 = m1.getTitle().compareTo(m2.getTitle());
                int crit2 = CommandParser.getInstance().movieViews.get(m1.getTitle()) - CommandParser.getInstance().movieViews.get(m2.getTitle());
                if (crit2 != 0) return crit2;
                return crit1;
            }
        };
        return comp;
    }

    public static Comparator<Movie> sortLongestAsc()
    {
        Comparator comp = new Comparator<Movie>(){
            @Override
            public int compare(Movie m1, Movie m2)
            {
                int crit1 = m1.getTitle().compareTo(m2.getTitle());
                int crit2 = m1.getDuration() - m2.getDuration();
                if (crit2 != 0) return crit2;
                return crit1;
            }
        };
        return comp;
    }

    public static Comparator<Movie> sortFavoriteAsc()
    {
        Comparator comp = new Comparator<Movie>(){
            @Override
            public int compare(Movie m1, Movie m2)
            {
                int crit1 = m1.getTitle().compareTo(m2.getTitle());
                int crit2 = CommandParser.getInstance().movieFavs.get(m1.getTitle()) - CommandParser.getInstance().movieFavs.get(m2.getTitle());
                if (crit2 != 0) return crit2;
                return crit1;
            }
        };
        return comp;
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
        return "";
    }

    public ArrayList<String> users_query(int N, String order){
        ArrayList<User> sortedUsers = new ArrayList<User>(users);
        ArrayList<String> usernames = new ArrayList<String>();
        sortedUsers.sort(CommandParser.sortUsersAsc());
        if (order.equals(Constants.DESC))
            Collections.reverse(sortedUsers);
        for (int j=0, i=0;i < sortedUsers.size() && j<N; i++){
            if (sortedUsers.get(i).getShowRatings().size() != 0){
                usernames.add(sortedUsers.get(i).getUsername());
                j++;
            }
        }
        System.out.println(usernames);
        return usernames;
    }

    private ArrayList<String> movies_query(int N, List<List<String>> filters, String criteria, String type) {
        ArrayList<Movie> sortedMovies = new ArrayList<Movie>(movies);
        ArrayList<String> movieTitles = new ArrayList<String>();
        for (Movie movie : movies){
            movieViews.put(movie.getTitle(), 0);
            movieFavs.put(movie.getTitle(), 0);
        }
        // filter the movies
        sortedMovies.removeIf((v) -> {
            if (!Integer.toString(v.getYear()).equals(filters.get(0).get(0)))
                return true;
            if ( !v.getGenres().contains(filters.get(1).get(0)) )
                return true;
            return false;
        });
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
        }

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
            System.out.println(sortedMovies);
            System.out.println();
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i=0, j=0; i < sortedMovies.size() && j < N; i++){
                movieTitles.add(sortedMovies.get(i).getTitle());
                j++;
            }
        }

        if (criteria.equals(Constants.FAVORITE)){
            sortedMovies.sort(CommandParser.sortFavoriteAsc());
            System.out.println(sortedMovies);
            System.out.println();
            if (type.equals(Constants.DESC))
                Collections.reverse(sortedMovies);

            for (int i=0, j=0; i < sortedMovies.size() && j < N; i++){
                movieTitles.add(sortedMovies.get(i).getTitle());
                j++;
            }
        }

        System.out.println(movieTitles);
        return movieTitles;
    }


}
