package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVUtils {


    public static HashMap<String, String> correctNames = new HashMap<>();
    public static List<Integer> badIds = new ArrayList<>();


    public static void loadCorrectNamesMap(){
        //PUT NAME IN PBP AS KEY, and NAME IN MATCHUP DATA AS VALUE
        correctNames.put("Enes Kanter", "Enes Freedom");
        correctNames.put("PJ Hairston", "P.J. Hairston");
        correctNames.put("C.J. Wilcox", "CJ Wilcox");
        correctNames.put("Marcelo Huertas", "Marcelinho Huertas");
        correctNames.put("JJ O'Brien", "J.J. O'Brien");
        correctNames.put("OG Anunoby", "O.G. Anunoby");
        correctNames.put("Frank Mason", "Frank Mason III");
        correctNames.put("Nicolas Claxton", "Nic Claxton");
        correctNames.put("Billy Garrett", "Billy Garrett Jr.");
        correctNames.put("Patrick Baldwin Jr.", "Patrick Baldwin");
        correctNames.put("Kenyon Martin Jr.", "KJ Martin");
        correctNames.put("Jeenathan Williams", "Nate Williams");
        correctNames.put("Reggie Bullock", "Reggie Bullock Jr.");
        correctNames.put("Vincent Hunter", "Vince Shamar Hunter");
        correctNames.put("Brandon Boston", "Brandon Boston Jr.");
        correctNames.put("Jeff Dowtin", "Jeff Dowtin Jr.");
        correctNames.put("P.J. Dozier", "PJ Dozier");
        correctNames.put("Xavier Tillman Sr.", "Xavier Tillman");
        correctNames.put("Craig Porter Jr.", "Craig Porter");
        correctNames.put("Pierre Jackson", "Pierre Deshawn Jackson");
        correctNames.put("Gigi Datome", "Luigi Datome");
        correctNames.put("Robbie Hummel", "Robbie John Hummel");
        correctNames.put("Jerome Jordan", "Jerome Adolphus Jordan");
        correctNames.put("Perry Jones III", "Perry Jones");
        correctNames.put("AJ Price", "A.J. Price");
        correctNames.put("Glen Rice", "Glen Rice Jr.");
        correctNames.put("Marcos Louzada Silva", "Didi Louzada");
    }

    public static void loadBadIds(){
        // TIME DISCREPANCY IS BECAUSE FROM SECOND LAST TO LAST PLAY IS NOT REGISTERED MEANING YOU LOSE
        // TIME.

        // 21900254 does not have sufficient PBP data to match rebounds for Batum and Williams
        // 21900047 Block counted torwards Redick instead of Brandon Ingram
        //21900540 Iman Isn't playing but apparently has stats????? Weird PBP
        badIds.add(21501193); //Wrong data everywhere STAT SHEET CSV
        badIds.add(21501221); //Wrong data everywhere STAT SHEET CSV
        badIds.add(21501192); //Wrong data everywhere STAT SHEET CSV
        badIds.add(21500971); //Wrong data rebounds? STAT SHEET CSV
        badIds.add(21501125); //Wrong data for 3pa STAT SHEET CSV
        badIds.add(21501126); //Wrong Data for 3pa STAT SHEET CSV
        badIds.add(21501127);  //Wrong Data for 3pa STAT SHEET CSV
        badIds.add(41500311); //Russ actually has 28, nba messed up
        badIds.add(41900303); //Rebounds messed up  STAT SHEET CSV
        badIds.add(41900112); //Rebounds messed up  STAT SHEET CSV
        badIds.add(41900142); //Rebounds messed up  STAT SHEET CSV
        badIds.add(41900224); //Stat Sheet wrong for steals
        badIds.add(41900103); //Kyle Korver Incorrect stats
        badIds.add(41900232); //Incorrect stats
        badIds.add(41900177); //Stat sheet incorrect for assists
        badIds.add(41900123); //Stat sheet incorrect for Turnovers
        badIds.add(41900122); //FGA, REBOUNDS, and Blocks incorrect on stat sheet
        badIds.add(21901281); // Rebounds incorrect on stat sheet
        badIds.add(21901247); //Rebounds incorrect on stat sheet
        badIds.add(21901248); //Incorrect steals on stat sheet
        badIds.add(21900075); //Blocks incorrect on stat sheet
        badIds.add(21900930); //Assists incorrect on stat sheet
        badIds.add(21500690); //3PA incorrect on Stat Sheet
        badIds.add(22200374); //Rebounds on Jackson and Covington wrong
//    badIds.add(21900540); //Iman Shumpert never played??
        badIds.add(21701120); //Dewayne Dedmon Turnovers Incorrect on stat sheet
        badIds.add(21701227); //Incorrect Rebound data on stat sheet
        badIds.add(21701222); //Incorrect Steal Data on stat sheet
        badIds.add(21701220); //Incorrect Steal Data on stat sheet
        badIds.add(21701186);//Incorrect turnovers on stat sheet
        badIds.add(21700272); //Multiple incorrect for JAMAL
        badIds.add(21700517); //Incorrect Turnovers on Stat sheet
        badIds.add(21700025); //Stat sheet incorrect for multiple stats
        badIds.add(21700101); //Stat sheet Incorrect for REBOUNDS
        badIds.add(21700207); //Stat sheet incorrect for Rebounds
        badIds.add(21700103); //Stat sheet incorrect for multiple stats of Felton
        badIds.add(21700060); //Incorrect TO on stat sheet
        badIds.add(21700239); //Incorrect TO on stat sheet
        badIds.add(22201222); //I am technically Incorrect, however PBP doesn't provide data for me to make a correction
        badIds.add(22000049); //Incorrect stat sheet
        badIds.add(22000557); //Incorrect stat sheet
        badIds.add(21601226); //Incorrect Stat sgheet
        badIds.add(21601220); //Incorrect stat sheet
        badIds.add(21601203); //Incorrect stat sheet
        badIds.add(21601205); //Incorrect stat sheet
        badIds.add(21601217); //Incorrect stat sheet
        badIds.add(21601212); //Incorrect stat sheet
        badIds.add(21601215); //Incorrect stat sheet
        badIds.add(21601207); //Incorrect stat sheet
        badIds.add(21601209);
        badIds.add(21601187);
        badIds.add(21600421);
        badIds.add(21600230);
        badIds.add(21600233);
    }
    public static  int getGameID(CSVRecord record){
        return getInt(record, "game_id");
    }

    public static String getDate(CSVRecord record){
        return record.get("date");
    }

    public static String getAwayPlayerOne(CSVRecord record){
        return record.get("a1");
    }

    public static String getAwayPlayerTwo(CSVRecord record){
        return record.get("a2");
    }

    public static String getAwayPlayerThree(CSVRecord record){
        return record.get("a3");
    }

    public static String getAwayPlayerFour(CSVRecord record){
        return record.get("a4");
    }

    public static String getAwayPlayerFive(CSVRecord record){
        return record.get("a5");
    }

    public static String getHomePlayerOne(CSVRecord record){
        return record.get("h1");
    }

    public static String getHomePlayerTwo(CSVRecord record){
        return record.get("h2");
    }

    public static String getHomePlayerThree(CSVRecord record){
        return record.get("h3");
    }

    public static String getHomePlayerFour(CSVRecord record){
        return record.get("h4");
    }

    public static String getHomePlayerFive(CSVRecord record){
        return record.get("h5");
    }

    public static int getQuarter(CSVRecord record){
        return getInt(record, "period");
    }

    public static int getAwayScore(CSVRecord record){
        return getInt(record, "away_score");
    }

    public static int getHomeScore(CSVRecord record){
        return getInt(record, "home_score");
    }

    public static double getRemainingTimeInSeconds(CSVRecord record){
        return convertToSeconds(record.get("remaining_time"));
    }

    public static double getElapsedTimeInSeconds(CSVRecord record){
        return convertToSeconds(record.get("elapsed"));
    }

    public static double getPlayLength(CSVRecord record){
        return convertToSeconds(record.get("play_length"));
    }

    public static int getGamePlayNumber(CSVRecord record){
        return getInt(record, "play_id");
    }

    public static String getPossessionTeam(CSVRecord record){
        return record.get("team");
    }

    public static String getEventColumn(CSVRecord record){
        return record.get("event_type");
    }

    public static String getAssistPlayerColumn(CSVRecord record){
        return record.get("assist");
    }

    public static String getAwayPlayerColumn(CSVRecord record){
        return record.get("away");
    }

    public static String getHomePlayerColumn(CSVRecord record){
        return record.get("home");
    }

    public static String getBlockPlayerColumn(CSVRecord record){
        return record.get("block");
    }

    public static String getEnteredPlayerColumn(CSVRecord record){
        return record.get("entered");
    }
    public static String getLeftPlayerColumn(CSVRecord record){
        return record.get("left");
    }

    public static int getFTNumberColumn(CSVRecord record){
        return getInt(record, "num");
    }

    public static String getOpponentPlayerColumn(CSVRecord record){
        return record.get("opponent");
    }

    public static int getFTOutOfColumn(CSVRecord record){
        return getInt(record, "outof");
    }

    public static String getMultiUsePlayerColumn(CSVRecord record){
        return record.get("player");
    }

    public static String getPossessionPlayerColumn(CSVRecord record){
        return record.get("possession");
    }
    public static String getReasonColumn(CSVRecord record){
        return record.get("reason");
    }

    public static String getResultColumn(CSVRecord record){
        return record.get("result");
    }

    public static String getStealPlayerColumn(CSVRecord record){
        return record.get("steal");
    }

    public static String getTypeColumn(CSVRecord record){
        return record.get("type");
    }

    public static int getDistanceColumn(CSVRecord record){
        return getInt(record, "shot_distance");
    }

    public static String getDescription(CSVRecord record){
        return record.get("description");
    }

    public static boolean getIsThreePointer(CSVRecord record){
        return record.get("3pt").equals("true");
    }

    public static boolean isDefensive(CSVRecord record){
       return record.get("defensive").equals("true");
    }
    public static boolean isOffensive(CSVRecord record){
      return record.get("offensive").equals("true");
    }

    public static boolean isFlagrant(CSVRecord record){
        return record.get("flagrant").equals("true");
    }


    /**
     * Parses a string value to an integer value.
     * If the string represents a double, it will be converted to an integer by truncating the decimal part.
     * @param value The string to parse.
     * @return The integer value, or 0 if parsing fails.
     */
    private static int parseInt(String value) {
        try {
            // Try to parse as an integer
            return Integer.parseInt(value);
        } catch (NumberFormatException e1) {
            try {
                // If parsing as an integer fails, try to parse as a double
                double doubleValue = Double.parseDouble(value);
                // Convert double to int (truncating the decimal part)
                return (int) doubleValue;
            } catch (NumberFormatException e2) {
                // Return 0 (or some default value) if both parsing attempts fail
                return 0;
            }
        }
    }
    /**
     * Retrieves an integer value from the record.
     * @param record The CSV record containing play-by-play data.
     * @param value The column name.
     * @return The integer value.
     */
    public static int getInt(CSVRecord record, String value){
        return parseInt(record.get(value));
    }

    /**
     * Converts a time string to seconds.
     * @param time The time string in the format "HH:MM:SS", "MM:SS" or "SS".
     * @return The time in seconds.
     */
    public static double convertToSeconds(String time) {
        String[] parts = time.split(":");
        double hours = 0;
        double minutes = 0;
        double seconds;

        if (parts.length == 3) {
            hours = Double.parseDouble(parts[0]);
            minutes = Double.parseDouble(parts[1]);
            seconds = Double.parseDouble(parts[2]);
        } else if (parts.length == 2) {
            minutes = Double.parseDouble(parts[0]);
            seconds = Double.parseDouble(parts[1]);
        } else if (parts.length == 1) {
            seconds = Double.parseDouble(parts[0]);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        return hours * 3600 + minutes * 60 + seconds;
    }

    /**
     * Checks if a CSV record has a valid value for the given key.
     * @param record The CSV record to check.
     * @param key The column name to check.
     * @return True if the value is valid, false otherwise.
     */
    public static boolean rowHasValue(CSVRecord record, String key) {
        return record.isMapped(key) && !record.get(key).equals("NA") && !record.get(key).isEmpty();
    }

    public static void deployMatchupAndPlayByPlayCSV(List<Matchup> matchups){
        try (FileWriter writer = new FileWriter("playspermatchup.csv");
             CSVPrinter printer =
                     new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("game_id", "play_type", "duration_of_play", "time_after_play", "qtr",
                                     "shooter_name", "shooter_id", "rebounder_name", "rebounder_id", "assister_name",
                                     "assister_id", "blocker_name", "blocker_id", "fouler_name", "fouler_id", "turnover_player_name",
                                     "turnover_player_id", "violater_name", "violater_id", "stealer_name", "stealer_id", "distance", "jumper_one_name",
                                     "jumper_one_id", "jumper_two_name", "jumper_two_id", "jumper_three_name", "jumper_three_id", "tech_one_name", "tech_one_id",
                                     "tech_two_name", "tech_two_id", "free_throw_number", "free_throw_total", "made_free_throw", "was_team")
                             .build())) {

            for (Matchup matchup : matchups) {
                for (Play play : matchup.getPlayByPlays()) {
                    printer.printRecord(
                            checkNull(play.getGameID()),
                            checkNull(play.getPlayType()),
                            checkNull(play.getPlayDuration()),
                            checkNull(convertSecondsToMinuteFormat(play.getTimeLeftInQuarter())),
                            checkNull(play.getQuarter()),
                            checkNull(play.getPlayerShooting() != null ? play.getPlayerShooting().getName() : null),
                            checkNull(play.getPlayerShooting() != null ? play.getPlayerShooting().getId() : null),
                            checkNull(play.getRebounder() != null ? play.getRebounder().getName() : null),
                            checkNull(play.getRebounder() != null ? play.getRebounder().getId() : null),
                            checkNull(play.getPlayerAssisting() != null ? play.getPlayerAssisting().getName() : null),
                            checkNull(play.getPlayerAssisting() != null ? play.getPlayerAssisting() .getId() : null),
                            checkNull(play.getPlayerBlocking() != null ? play.getPlayerBlocking().getName() : null),
                            checkNull(play.getPlayerBlocking() != null ? play.getPlayerBlocking().getId() : null),
                            checkNull(play.getFoulCommitter() != null ? play.getFoulCommitter().getName() : null),
                            checkNull(play.getFoulCommitter() != null ? play.getFoulCommitter().getId() : null),
                            checkNull(play.getTurnoverCommitter() != null ? play.getTurnoverCommitter() .getName() : null),
                            checkNull(play.getTurnoverCommitter()  != null ? play.getTurnoverCommitter() .getId() : null),
                            checkNull(play.getWhoViolated() != null ?play.getWhoViolated().getName() : null),
                            checkNull(play.getWhoViolated() != null ? play.getWhoViolated().getId() : null),
                            checkNull(play.getStealer() != null ? play.getStealer().getName() : null),
                            checkNull(play.getStealer() != null ? play.getStealer().getId() : null),
                            play.getDistance(),
                            checkNull(play.getJumperOne() != null ? play.getJumperOne().getName() : null),
                            checkNull(play.getJumperOne() != null ? play.getJumperOne().getId() : null),
                            checkNull(play.getJumperTwo() != null ? play.getJumperTwo().getName() : null),
                            checkNull(play.getJumperTwo() != null ? play.getJumperTwo().getId() : null),
                            checkNull(play.getJumperReceiver() != null ? play.getJumperReceiver().getName() : null),
                            checkNull(play.getJumperReceiver() != null ? play.getJumperReceiver().getId() : null),
                            checkNull(play.getTechOnePlayer() != null ? play.getTechOnePlayer().getName() : null),
                            checkNull(play.getTechOnePlayer() != null ? play.getTechOnePlayer().getId() : null),
                            checkNull(play.getTechTwoPlayer() != null ? play.getTechTwoPlayer().getName() : null),
                            checkNull(play.getTechTwoPlayer() != null ? play.getTechTwoPlayer().getId() : null),
                            play.getFreeThrowNumber(),
                            play.getFreeThrowTotal(),
                            play.isMadeFreeThrow(),
                            play.isWasTeam()
                    );
                }
            }

            printer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deployPlayerStatsCSV(List<Matchup> matchups){
        try (FileWriter writer = new FileWriter("stats.csv");
             CSVPrinter printer =
                     new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("game_id", "athlete_id", "athlete_name", "rebounds", "assists", "blocks",
                                     "steals", "turnovers", "points", "fouls", "minutes", "f_g_made", "f_g_att", "t_f_made", "t_f_att")
                             .build())) {
            for (Matchup matchup : matchups) {
                if (matchup.getGameID() != 0) {
                    for (Player player : matchup.getTotalPlayers()) {
                        printer.printRecord(
                                matchup.getGameID(),
                                player.getId(),
                                player.getName(),
                                player.getRebounds(),
                                player.getAssists(),
                                player.getBlocks(),
                                player.getSteals(),
                                player.getTurnovers(),
                                player.getPoints(),
                                player.getFouls(),
                                (int) player.getMinutes() / 60,
                                player.getFieldGoalsMade(),
                                player.getFieldGoalsAttempted(),
                                player.getThreePointFieldGoalsMade(),
                                player.getThreePointFieldGoalsAttempted());
                    }
                }
            }
            printer.flush();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String checkNull(Object value) {
        return value == null ? "NA" : value.toString();
    }


    private  static String convertSecondsToMinuteFormat(double totalSeconds) {
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;
        int milliseconds = (int) ((totalSeconds - (int) totalSeconds) * 1000);

        // Convert milliseconds to string and remove trailing zeros

        return String.format("%02d:%02d.%01d", minutes, seconds, milliseconds);
    }

    public static void developPlayTypesCSV(List<Matchup> allMatchups){
        try (FileWriter writer = new FileWriter("playTypes.csv");
             CSVPrinter printer =
                     new CSVPrinter(
                             writer,
                             CSVFormat.DEFAULT
                                     .builder()
                                     .setHeader("game_id", "play_type", "duration_of_play", "time_after_play", "qtr")
                                     .build())) {

            for (Matchup matchup : allMatchups) {
                for (Play play : matchup.getPlayByPlays()) {
                    printer.printRecord(
                            play.getGameID(),
                            play.getPlayType(),
                            play.getPlayDuration(),
                            convertSecondsToMinuteFormat(play.getTimeLeftInQuarter()),
                            play.getQuarter());
                }
            }

            printer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
