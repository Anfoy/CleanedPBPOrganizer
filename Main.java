package me.antcode;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.datacollection.CSVUtils;
import me.antcode.datacollection.DebugPlayerStat;
import me.antcode.datacollection.StatBreakdown;
import me.antcode.managers.PlayLabellingManager;
import java.util.List;

public class Main {
 static String[] fileNames = new String[]{"[03-05-2016]-[06-19-2016]-combined-stats.csv"};

  public static void main(String[] args) {
    // TIME DISCREPANCY IS BECAUSE FROM SECOND LAST TO LAST PLAY IS NOT REGISTERED MEANING YOU LOSE
    // TIME.

    //STAT COMPARING PURPOSES this stops games from showing up that are actually correct but stat sheet is incorrect.
    CSVUtils.loadBadIds();

    //Loads corrected Names since not all matchup names are correct with PBP names.
    CSVUtils.loadCorrectNamesMap();

    String matchupPath = "src/main/java/me/antcode/MATCHUPS_converted.csv";
    String playByPlayPath = "src/main/java/me/antcode/processedCSVs";

    List<Matchup> allMatchups;
    CSVDataGather csvDataGather = new CSVDataGather(matchupPath, playByPlayPath);
    TestPlayerStats testPlayerStats = new TestPlayerStats();
    PlayLabellingManager playLabellingManager = new PlayLabellingManager();


    testPlayerStats.loadCSV();


    allMatchups = csvDataGather.gatherAndOrganizeMatchups();
    generatePlaysForMatchups(csvDataGather, playByPlayPath,
            fileNames, false);

//   printSpecificMatchup( playByPlayPath,testPlayerStats, csvDataGather, 22301071, "[10-24-2023]-[06-17-2024]-combined-stats.csv", playLabellingManager, allMatchups);
    comparePlayerStats(testPlayerStats,allMatchups); //comment this out when checking a specific game

    printNonMatchingPlayerStats(testPlayerStats);

    generateStatistics(testPlayerStats, csvDataGather);
  }

  private static void generatePlaysForMatchups(CSVDataGather csvDataGather, String playByPlayPath, String[] fileName, boolean accessSpecificFile){
          csvDataGather.generatePlayByPlays(playByPlayPath, accessSpecificFile, fileName);
  }

  private static void printSpecificMatchup(String folderPath, TestPlayerStats testPlayerStats, CSVDataGather csvDataGather, int gameID, String fileName, PlayLabellingManager playLabellingManager, List<Matchup> allMatchups){
       playLabellingManager.printSpecificMatchup(folderPath, testPlayerStats, csvDataGather, gameID, fileName, allMatchups);
  }


  /**
   * Checks the teambox.csv to see if player stats are not matching up.
   * @param testPlayerStats TestPlayerStats class object. Dependency Injection
   * @param allMatchups all Matchups to check.
   */
  private static void comparePlayerStats(TestPlayerStats testPlayerStats, List<Matchup> allMatchups){
    for (Matchup matchup : allMatchups){
      if (matchup.getPlayByPlays().isEmpty()) continue;
      if (CSVUtils.badIds.contains(matchup.getGameID())) continue;
      testPlayerStats.checkPlayerStatsForMatchup(matchup);
    }
  }

  /**
   * Creates a CSV that will show all player stats and then another csv to show Play by play data.
   * @param allMatchups All matchups to look through.
   */
  private static void deployDataCSV(List<Matchup> allMatchups){
    CSVUtils.deployPlayerStatsCSV(allMatchups);
    CSVUtils.deployMatchupAndPlayByPlayCSV(allMatchups);
  }

  /**
   * Generates stats about how many were player objects were incorrect, how many total player objects, total plays, and a breakdown for each main stat category
   * @param testPlayerStats TestPlayerStats class object. Dependency injection
   * @param csvDataGather CSVDataGather class object. Dependency Injection
   */
  private static void generateStatistics(TestPlayerStats testPlayerStats, CSVDataGather csvDataGather){
    int incorrects = 0;
    for (StatBreakdown statBreakdown : testPlayerStats.getAllStats()){
      if (!statBreakdown.getName().equals("minutes")){
        incorrects += statBreakdown.getTotalAmount();
      }
      System.out.println(statBreakdown);
    }
    if (incorrects == 0){
      System.out.println("100%");
    } else {
      double percentage = ((double) incorrects / csvDataGather.getCount()) * 100;
        System.out.printf("%.10f %%\n", percentage);  // prints with 6 decimal places
        System.out.println("Incorrects: " + incorrects);
        System.out.println("Total checks: " + testPlayerStats.getTotalPlayerChecks());
        System.out.println("Total plays: " + csvDataGather.getCount());
    }
  }

  /**
   * Prints every DebugPlayerStat object, which holds the information for when a player stat the program got is not correlating with the teambox.csv stats.
   * *NOTE*: Make sure to run comparePlayerStats method to have these DebugPlayerStat objects.
   * @param testPlayerStats TestPlayerStats class object.
   */
  private static void printNonMatchingPlayerStats(TestPlayerStats testPlayerStats){
    for (DebugPlayerStat debugPlayerStat : testPlayerStats.getDebugStats()) {
      if (!debugPlayerStat.getType().equalsIgnoreCase("minutes")) {
        System.out.println(debugPlayerStat);
      }
    }
  }
}


