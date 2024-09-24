package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.datacollection.CSVUtils;
import org.apache.commons.csv.CSVRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Manager {



    public   int[] extractNumbers(String input) {
        // Define the regex pattern to match numbers
        Pattern pattern = Pattern.compile("(\\d+) of (\\d+)");
        Matcher matcher = pattern.matcher(input);

        // Check if the pattern matches
        if (matcher.find()) {
            // Extract the numbers from the matched groups
            int firstNumber = Integer.parseInt(matcher.group(1));
            int secondNumber = Integer.parseInt(matcher.group(2));
            return new int[] { firstNumber, secondNumber };
        }

        // Return null if no match is found
        return null;
    }

    public String checkNull(Object value) {
        return value == null ? "NA" : value.toString();
    }


    public String convertSecondsToMinuteFormat(double totalSeconds) {
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;
        int milliseconds = (int) ((totalSeconds - (int) totalSeconds) * 1000);

        // Convert milliseconds to string and remove trailing zeros

        return String.format("%02d:%02d.%01d", minutes, seconds, milliseconds);
    }


    /**
     * Retrieves the athlete ID from the record.
     * @param record The CSV record containing play-by-play data.
     * @param id The athlete number.
     * @return The athlete ID.
     */
    public int getAthleteID(CSVRecord record, int id){
        return CSVUtils.rowHasValue(record, "athlete_id_" + id) ? CSVUtils.getInt(record, "athlete_id_" + id) : 0;
    }



    public Matchup findCorrelatingMatchupWithID(String date,String name, List<Matchup> matchups, int id){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() == 0){
                if (matchup.getDate().equals(date)) {
                    for (Player player : matchup.getTotalPlayers()){
                        if (player.getName().equals(name)){
                            matchup.setGameID(id);
                            return matchup;
                        }
                    }
                }
            }else{
                if (matchup.getGameID() == id){
                    return matchup;
                }
            }
        }
        return null;
    }

    public boolean isMatchup(Matchup matchup, String date, String name, int id) {
        if (matchup.getGameID() == 0){
            if (matchup.getDate().equals(date)) {
                for (Player player : matchup.getTotalPlayers()){
                    if (player.getName().equals(name)){
                        matchup.setGameID(id);
                        return true;
                    }
                }
            }
        }else{
            return matchup.getGameID() == id;
        }
        return false;
    }

    public  String convertDateFormat(String dateStr) {
        // Define the date format for MM/DD/YYYY
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        // Define the desired date format for YYYY-MM-DD
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;

        try {
            // Parse the input date string into a Date object
            date = inputFormat.parse(dateStr);
            // Convert it to the new format
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "Invalid date format";
        }
    }



}
