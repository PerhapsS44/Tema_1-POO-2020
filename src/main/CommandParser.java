package main;

import common.Constants;
import fileio.*;

import java.util.ArrayList;

class CommandParser
{
    // static variable single_instance of type Singleton
    private static CommandParser single_instance = null;

    ArrayList<User> users;
    ArrayList<Movie> movies;
    ArrayList<Serial> serials;


    // private constructor restricted to this class itself
    private CommandParser()
    {
        users = new ArrayList<User>();
        movies = new ArrayList<Movie>();
        serials = new ArrayList<Serial>();
    }

    // static method to create instance of Singleton class
    public static CommandParser getInstance()
    {
        if (single_instance == null)
            single_instance = new CommandParser();

        return single_instance;
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
                        return Constants.SUCCESS + currentCommand.getTitle() + Constants.SUCCESS_VIEW + user.getHistory().get(currentCommand.getTitle());
                    }
                    else{
                        user.getHistory().put(currentCommand.getTitle(), 1);
                        return Constants.SUCCESS + currentCommand.getTitle() + Constants.SUCCESS_VIEW + user.getHistory().get(currentCommand.getTitle());
                    }
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


}
