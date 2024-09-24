package me.antcode;

import me.antcode.datacollection.StatBreakdown;
import me.antcode.datacollection.DebugPlayerStat;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestPlayerStats {

  private final Map<String, CSVRecord> recordMap = new HashMap<>();

  private final List<DebugPlayerStat> debugStats = new ArrayList<>();

  StatBreakdown assistsAnalyze = new StatBreakdown("assists");
  StatBreakdown reboundAnalyze = new StatBreakdown("rebounds");
  StatBreakdown blocksAnalyze = new StatBreakdown("blocks");
  StatBreakdown stealsAnalyze = new StatBreakdown("steals");
  StatBreakdown turnoversAnalyze = new StatBreakdown("turnovers");
  StatBreakdown pointsAnalyze = new StatBreakdown("points");
  StatBreakdown foulsAnalyze = new StatBreakdown("fouls");
  StatBreakdown field_goals_madeAnalyze = new StatBreakdown("field_goals_made");
  StatBreakdown field_goals_attemptedAnalyze = new StatBreakdown("field_goals_attempted");
  StatBreakdown three_point_field_goals_madeAnalyze = new StatBreakdown("three_point_field_goals_made");
  StatBreakdown three_point_field_goals_attemptedAnalyze = new StatBreakdown("three_point_field_goals_attempted");
  StatBreakdown minutesAnalyze = new StatBreakdown("minutes");
 private int totalChecks;


  private final ArrayList<StatBreakdown> allStats = new ArrayList<>();
  public TestPlayerStats(){
    allStats.add(assistsAnalyze);
    allStats.add(reboundAnalyze);
    allStats.add(blocksAnalyze);
    allStats.add(stealsAnalyze);
    allStats.add(turnoversAnalyze);
    allStats.add(pointsAnalyze);
    allStats.add(foulsAnalyze);
    allStats.add(field_goals_madeAnalyze);
    allStats.add(field_goals_attemptedAnalyze);
    allStats.add(three_point_field_goals_madeAnalyze);
    allStats.add(minutesAnalyze);
    allStats.add(three_point_field_goals_attemptedAnalyze);
    totalChecks = 0;
  }

    /**
     * This method reads a CSV file, parses each record using Apache Commons CSV, and stores the records in a recordMap with a key generated by concatenating
     * the "game_id" and "athlete_id" fields.
     * It skips the header row during parsing and catches any IOException that may occur, printing an error message if the file cannot be read.
     * The parsed data is stored in memory for further processing, using the composite key for efficient lookup.
     */
  public void loadCSV() {
    try (Reader reader = new FileReader("src/main/java/me/antcode/teambox.csv");
         CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

      for (CSVRecord csvRecord : csvParser) {
        String key = csvRecord.get("game_id") + "-" + csvRecord.get("athlete_id");
        recordMap.put(key, csvRecord);
      }
    } catch (IOException e) {
      System.out.println("Failed to read file.");
      e.printStackTrace();
    }
  }
  public void checkPlayerStatsForMatchup(Matchup matchup) {
    double greatestTimeDiff = 0;
    for (Player player : matchup.getTotalPlayers()) { //It knows which matchup in terms of having different ids because same object
      String key = matchup.getStatSheetID() + "-" + player.getId();
      CSVRecord csvRecord = recordMap.get(key);
      if (csvRecord == null){
        if (player.getExtraID() != 0){
          csvRecord = recordMap.get(matchup.getStatSheetID() + "-" + player.getExtraID());
        }
      }
      if (csvRecord != null) {
        totalChecks++;
        assessPlayer(csvRecord, player, matchup);
      } else {
        System.out.println(
                "No data found for player: " + player.getName() + "| ID "
                        + player.getId()
                        + " in game ID "
                        + matchup.getStatSheetID());
      }
    }
  }

  private void assessPlayer(CSVRecord csvRecord, Player player, Matchup matchup){
    int rebounds = getInt(csvRecord, "rebounds");
    int assists = getInt(csvRecord, "assists");
    int blocks = getInt(csvRecord, "blocks");
    int steals = getInt(csvRecord, "steals");
    int turnovers = getInt(csvRecord, "turnovers");
    int points = getInt(csvRecord, "points");
    int fouls = getInt(csvRecord, "fouls");
    int minutes = getInt(csvRecord, "minutes");
    int field_goals_made = getInt(csvRecord, "field_goals_made");
    int field_goals_attempted = getInt(csvRecord, "field_goals_attempted");
    int three_point_field_goals_made = getInt(csvRecord, "three_point_field_goals_made");
    int three_point_field_goals_attempted =
            getInt(csvRecord, "three_point_field_goals_attempted");

    checkRebounds(player, rebounds, csvRecord, matchup);
    checkAssists(player, assists, csvRecord, matchup);
    checkBlocks(player, blocks, csvRecord, matchup);
    checkSteals(player, steals, csvRecord, matchup);
    checkTurnovers(player, turnovers, csvRecord, matchup);
    checkPoints(player, points, csvRecord, matchup);
    checkFouls(player, fouls, csvRecord, matchup);
    checkMinutes(player, minutes, csvRecord, matchup);
    checkFieldGoalsMade(player, field_goals_made, csvRecord, matchup);
    checkFieldGoalsAttempted(player, field_goals_attempted, csvRecord, matchup);
    checkThreePointFieldGoalsMade(player, three_point_field_goals_made, csvRecord, matchup);
    checkThreePointFieldGoalsAttempted(player, three_point_field_goals_attempted, csvRecord, matchup);
  }

  /**
   * Parses a string value to an integer value.
   * If the string represents a double, it will be converted to an integer by truncating the decimal part.
   * @param value The string to parse.
   * @return The integer value, or 0 if parsing fails.
   */
  public int parseInt(String value) {
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

  private int getInt(CSVRecord record, String value) {
    return parseInt(record.get(value));
  }

  private int convertSecondsToMinutes(double seconds) {
    return (int) (seconds / 60);
  }

  private double convertMinutesToSeconds(int minutes) {
    return (minutes * 60);
  }

  public StatBreakdown getAssistsAnalyze() {
    return assistsAnalyze;
  }

  public StatBreakdown getReboundAnalyze() {
    return reboundAnalyze;
  }

  public StatBreakdown getBlocksAnalyze() {
    return blocksAnalyze;
  }

  public StatBreakdown getStealsAnalyze() {
    return stealsAnalyze;
  }

  public StatBreakdown getTurnoversAnalyze() {
    return turnoversAnalyze;
  }

  public StatBreakdown getPointsAnalyze() {
    return pointsAnalyze;
  }

  public StatBreakdown getFoulsAnalyze() {
    return foulsAnalyze;
  }

  public StatBreakdown getField_goals_madeAnalyze() {
    return field_goals_madeAnalyze;
  }

  public StatBreakdown getField_goals_attemptedAnalyze() {
    return field_goals_attemptedAnalyze;
  }

  public StatBreakdown getThree_point_field_goals_madeAnalyze() {
    return three_point_field_goals_madeAnalyze;
  }

  public ArrayList<StatBreakdown> getAllStats() {
    return allStats;
  }

  public List<DebugPlayerStat> getDebugStats() {
    return debugStats;
  }

  public int getTotalPlayerChecks() {
    return totalChecks;
  }

  private void checkRebounds(Player player, int rebounds, CSVRecord csvRecord, Matchup matchup) {
    if (rebounds != player.getRebounds()) {
      reboundAnalyze.identifySplit(player.getRebounds(), rebounds, player);
      debugStats.add(new DebugPlayerStat(player, "REBOUNDS", player.getRebounds(), getInt(csvRecord, "rebounds"), matchup.getGameID(), matchup));
    }
  }

  private void checkAssists(Player player, int assists, CSVRecord csvRecord, Matchup matchup) {
    if (assists != player.getAssists()) {
      debugStats.add(new DebugPlayerStat(player, "ASSISTS", player.getAssists(), getInt(csvRecord, "assists"), matchup.getGameID(), matchup));
      assistsAnalyze.identifySplit(player.getAssists(), assists, player);
    }
  }

  private void checkBlocks(Player player, int blocks, CSVRecord csvRecord, Matchup matchup) {
    if (blocks != player.getBlocks()) {
      debugStats.add(new DebugPlayerStat(player, "BLOCKS", player.getBlocks(), getInt(csvRecord, "blocks"), matchup.getGameID(), matchup));
      blocksAnalyze.identifySplit(player.getBlocks(), blocks, player);
    }
  }

  private void checkSteals(Player player, int steals, CSVRecord csvRecord, Matchup matchup) {
    if (steals != player.getSteals()) {
      debugStats.add(new DebugPlayerStat(player, "STEALS", player.getSteals(), getInt(csvRecord, "steals"), matchup.getGameID(), matchup));
      stealsAnalyze.identifySplit(player.getSteals(), steals, player);
    }
  }

  private void checkTurnovers(Player player, int turnovers, CSVRecord csvRecord, Matchup matchup) {
    if (turnovers != player.getTurnovers()) {
      debugStats.add(new DebugPlayerStat(player, "TURNOVERS", player.getTurnovers(), getInt(csvRecord, "turnovers"), matchup.getGameID(), matchup));
      turnoversAnalyze.identifySplit(player.getTurnovers(), turnovers, player);
    }
  }

  private void checkPoints(Player player, int points, CSVRecord csvRecord, Matchup matchup) {
    if (points != player.getPoints()) {
      debugStats.add(new DebugPlayerStat(player, "POINTS", player.getPoints(), getInt(csvRecord, "points"), matchup.getGameID(), matchup));
      pointsAnalyze.identifySplit(player.getPoints(), points, player);
    }
  }

  private void checkFouls(Player player, int fouls, CSVRecord csvRecord, Matchup matchup) {
    if (fouls != player.getFouls()) {
      for (Play play : matchup.getPlayByPlays()) {
        if (play.getPlayType() == PlayTypes.SECOND_TECHNICAL_FOUL) {
          if (play.getFoulCommitter() != null && play.getFoulCommitter().getName().equals(player.getName())) {
            player.setFouls(player.getFouls() - 1);
            return;
          }
        }
        if (play.getPlayType() == PlayTypes.DOUBLE_TECHNICAL_FOUL) {
          if (play.getTechOnePlayer() != null && play.getTechOnePlayer().getName().equals(player.getName())) {
            player.addFouls(1);
            return;
          } else if (play.getTechTwoPlayer() != null && play.getTechTwoPlayer().getName().equals(player.getName())) {
            player.addFouls(1);
            return;
          }
        }
      }
      debugStats.add(new DebugPlayerStat(player, "FOULS", player.getFouls(), getInt(csvRecord, "fouls"), matchup.getGameID(), matchup));
      foulsAnalyze.identifySplit(player.getFouls(), fouls, player);
    }
  }

  private void checkMinutes(Player player, int minutes, CSVRecord csvRecord, Matchup matchup) {
      int minutesConverted = minutes * 60;
    if (minutesConverted != player.getMinutes()) {
      debugStats.add(new DebugPlayerStat(player, "MINUTES", convertSecondsToMinutes(player.getMinutes()), getInt(csvRecord, "minutes"), matchup.getGameID(), matchup));
      if (minutesConverted - player.getMinutes() >= 300) {
          minutesAnalyze.addFivePlus();
          return;
      }
      if (minutesConverted - player.getMinutes() >= 240) {
          minutesAnalyze.addByFour();
          return;
      }
      if (minutesConverted - player.getMinutes() >= 180) {
          minutesAnalyze.addByThree();
          return;
      }
      if (minutesConverted - player.getMinutes() >= 120) {
          minutesAnalyze.addByTwo();
          return;
      }

      if (minutesConverted - player.getMinutes() >= 60) {
          minutesAnalyze.addByOne();
      }
    }
  }

  private void checkFieldGoalsMade(Player player, int field_goals_made, CSVRecord csvRecord, Matchup matchup) {
    if (field_goals_made != player.getFieldGoalsMade()) {
      debugStats.add(new DebugPlayerStat(player, "FGM", player.getFieldGoalsMade(), getInt(csvRecord, "field_goals_made"), matchup.getGameID(), matchup));
      field_goals_madeAnalyze.identifySplit(player.getFieldGoalsMade(), field_goals_made, player);
    }
  }

  private void checkFieldGoalsAttempted(Player player, int field_goals_attempted, CSVRecord csvRecord, Matchup matchup) {
    if (field_goals_attempted != player.getFieldGoalsAttempted()) {
      debugStats.add(new DebugPlayerStat(player, "FGA", player.getFieldGoalsAttempted(), getInt(csvRecord, "field_goals_attempted"), matchup.getGameID(), matchup));
      field_goals_attemptedAnalyze.identifySplit(player.getFieldGoalsAttempted(), field_goals_attempted, player);
    }
  }

  private void checkThreePointFieldGoalsMade(Player player, int three_point_field_goals_made, CSVRecord csvRecord, Matchup matchup) {
    if (three_point_field_goals_made != player.getThreePointFieldGoalsMade()) {
      debugStats.add(new DebugPlayerStat(player, "3PM", player.getThreePointFieldGoalsMade(), getInt(csvRecord, "three_point_field_goals_made"), matchup.getGameID(), matchup));
      three_point_field_goals_madeAnalyze.identifySplit(player.getThreePointFieldGoalsMade(), three_point_field_goals_made, player);
    }
  }

  private void checkThreePointFieldGoalsAttempted(Player player, int three_point_field_goals_attempted, CSVRecord csvRecord, Matchup matchup) {
    if (three_point_field_goals_attempted != player.getThreePointFieldGoalsAttempted()) {
      debugStats.add(new DebugPlayerStat(player, "3PA", player.getThreePointFieldGoalsAttempted(), getInt(csvRecord, "three_point_field_goals_attempted"), matchup.getGameID(), matchup));
      three_point_field_goals_attemptedAnalyze.identifySplit(player.getThreePointFieldGoalsAttempted(), three_point_field_goals_attempted, player);
    }
  }












}
