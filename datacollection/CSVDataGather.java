package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.managers.*;
import me.antcode.plays.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to gather data from CSV file
 */
public class CSVDataGather {

  private final List<Matchup> matchups;
  private final AssistManager assistManager;
  private final FoulManager foulManager;
  private final FreeThrowManager freeThrowManager;
  private final ReboundManager reboundManager;
  private final ShotManager shotManager;
  private final SubstitutionManager substitutionManager;
  private final TimeoutManager timeoutManager;
  private final TurnoverManager turnoverManager;
  private final ViolationManager violationManager;
  private final BlockManager blockManager;
  MatchupManager matchupManager;
  PlayLabellingManager playLabellingManager;

  private final String playByPlayCSVPath;
  int count = 0;

  /**
   * Constructor to initialize CSVDataGather with paths to the CSV files.
   * Also creates the matchup objects.
   * @param matchupsCSVPath Path to the matchups CSV file.
   * @param playByPlayCSVPath Path to the play-by-play CSV file.
   */
  public CSVDataGather(String matchupsCSVPath, String playByPlayCSVPath) {
    this.playByPlayCSVPath = playByPlayCSVPath;
    assistManager = new AssistManager();
    foulManager = new FoulManager();
    freeThrowManager = new FreeThrowManager();
    reboundManager = new ReboundManager();
    shotManager = new ShotManager();
    substitutionManager = new SubstitutionManager();
    timeoutManager = new TimeoutManager();
    turnoverManager = new TurnoverManager();
    violationManager = new ViolationManager();
    blockManager = new BlockManager();
    matchupManager = new MatchupManager();
    playLabellingManager = new PlayLabellingManager();
    matchups = matchupManager.extractAllMatchups(matchupsCSVPath);
  }

    public Matchup curateSpecificPlay(String folderPath, int gameID, String desiredFile) {
//      Matchup matchup = generateSpecificMatchupPlays(playByPlayCSVPath, gameID, true, desiredFile, matchups);
      File[] files = new File(folderPath).listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
      Matchup matchup = null;

      // Loop through files and process either specific or all files
      for (File file : files) {
            if (file.getName().equalsIgnoreCase(desiredFile)) {
              matchup = processRecords(file, matchups, gameID);
            }
          }
      System.out.println("Finished Generated Report");
      return matchup;
    }


  /**
   * This method processes CSV files located in the specified folder, either processing all files or a specific one,
   * depending on the value of the `accessSpecificFiles` flag. Each file is parsed, and the play-by-play data is
   * extracted, labeled, and stored in a list of `Matchup` objects. After processing, the plays are finalized and cleared.
   *
   * @param folderPath            The path to the folder containing the CSV files.
   * @param accessSpecificFiles    A flag indicating whether to process all files or just one specific file.
   * @param fileNames              The names of the specific files to process if `accessSpecificFiles` is true.
   */
  public void generatePlayByPlays(String folderPath, boolean accessSpecificFiles, String[] fileNames) {
    File[] files = new File(folderPath).listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

    if (files == null) {
      System.out.println("No CSV files found in the folder.");
      return;
    }

    // Loop through files and process either specific or all files
    for (File file : files) {
      if (!accessSpecificFiles) {
        processRecords(file, matchups);
      } else {
        for (String fileName : fileNames) {
          if (file.getName().equalsIgnoreCase(fileName)) {
            processRecords(file, matchups);
          }
        }
      }
      }

    // Finalize plays for all matchups
//    matchups.forEach(this::finalizeMatchup);
  }



  /**
   * This method processes a single CSV file, reading each record, correcting player names if necessary,
   * and associating each play with the correct `Matchup`. Valid plays are labeled and added to the corresponding matchup.
   *
   * @param file      The CSV file to process.
   * @param matchups  The list of `Matchup` objects to associate plays with.
   */
  private void processRecords(File file, List<Matchup> matchups) {
    System.out.println("Processing file: " + file.getName());
    try (CSVParser csvParser = new CSVParser(new FileReader(file),
            CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      Matchup matchup = null;
      Matchup currentMatchup = null;

      // Loop through each record in the CSV file
      for (CSVRecord csvRecord : csvParser) {
        String date = CSVUtils.getDate(csvRecord);
        String awayPlayer = getCorrectedAwayPlayer(csvRecord);
        int gameID = CSVUtils.getGameID(csvRecord);

        // Find corresponding matchup for the record
        matchup = findCorrelatingMatchupWithID(date, awayPlayer, matchups, gameID);
        if (currentMatchup == null && matchup != null){
          currentMatchup = matchup;
        }else if (matchup != null && matchup.getGameID() != currentMatchup.getGameID()){
          finalizeMatchup(currentMatchup);
          currentMatchup = matchup;
        }
        Actions action = playLabellingManager.determineAction(csvRecord);

        // Add valid plays to the matchup
        if (isValidAction(action) && matchup != null) {
          matchup.getAllLabeledPlays().add(playLabellingManager.createLabeledPlay(csvRecord, action));
          count++;
        }
      }

      // Finalize the plays for the matchup
//      if (matchup != null) {
//        System.out.println("Found matchup");
//        finalizeMatchup(matchup);
//      }

    } catch (IOException e) {
      System.out.println("Failed to read file: " + file.getName());
      e.printStackTrace();
    }
  }

  /**
   * This method processes a single CSV file, reading each record, correcting player names if necessary,
   * and associating each play with the correct `Matchup`. Valid plays are labeled and added to the corresponding matchup.
   *
   * @param file      The CSV file to process.
   * @param matchups  The list of `Matchup` objects to associate plays with.
   */
  private Matchup processRecords(File file, List<Matchup> matchups, int desiredGameID) {
    System.out.println("Processing file: " + file.getName());
    try (CSVParser csvParser = new CSVParser(new FileReader(file),
            CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      Matchup matchup = null;

      // Loop through each record in the CSV file
      for (CSVRecord csvRecord : csvParser) {
        String date = CSVUtils.getDate(csvRecord);
        String awayPlayer = getCorrectedAwayPlayer(csvRecord);
        int gameID = CSVUtils.getGameID(csvRecord);

        // Find corresponding matchup for the record
        if (desiredGameID == gameID) {
          matchup = findCorrelatingMatchupWithID(date, awayPlayer, matchups, gameID);
          Actions action = playLabellingManager.determineAction(csvRecord);

          // Add valid plays to the matchup
          if (isValidAction(action) && matchup != null) {
            matchup
                .getAllLabeledPlays()
                .add(playLabellingManager.createLabeledPlay(csvRecord, action));
            count++;
          }
        }
      }

      // Finalize the plays for the matchup
      if (matchup != null){
        finalizeMatchup(matchup);
        return matchup;
      }

    } catch (IOException e) {
      System.out.println("Failed to read file: " + file.getName());
      e.printStackTrace();
    }
   return null;
  }

  /**
   * This method corrects the away player name based on a predefined map of corrections.
   * In specific cases, such as when the gameID is `22000586` and the player is "Reggie Bullock Jr.",
   * the method corrects the name to "Reggie Bullock".
   *
   * @param csvRecord  The CSV record from which the player name is extracted.
   * @return The corrected player name.
   */
  private String getCorrectedAwayPlayer(CSVRecord csvRecord) {
    String awayPlayer = CSVUtils.getAwayPlayerOne(csvRecord);
    if (CSVUtils.correctNames.containsKey(awayPlayer)) {
      awayPlayer = CSVUtils.correctNames.get(awayPlayer);
      if (CSVUtils.getGameID(csvRecord) == 22000586 && awayPlayer.equalsIgnoreCase("Reggie Bullock Jr.")) {
        return "Reggie Bullock";
      }
    }
    return awayPlayer;
  }

  /**
   * Checks if the action from the CSV record is valid. Valid actions are not ignored, unknown,
   * or end-of-period actions.
   *
   * @param action  The action determined from the CSV record.
   * @return True if the action is valid; false otherwise.
   */
  private boolean isValidAction(Actions action) {
    return action != Actions.IGNORE && action != Actions.UNKNOWN && action != Actions.END_OF_PERIOD;
  }

  /**
   * Finalizes the plays for a matchup by sorting them based on play number and then processing the plays.
   * Once all plays have been processed, the list of plays for the matchup is cleared.
   *
   * @param matchup  The matchup to finalize.
   */
  private void finalizeMatchup(Matchup matchup) {
    matchup.getAllLabeledPlays().sort(Comparator.comparingInt(LabeledPlay::getGamePlayNumber));
    getPlays(matchup);
    matchup.getAllLabeledPlays().clear();
  }

  /**
   * Iterates through the plays in a matchup, handling each action and delegating processing to the
   * appropriate manager (e.g., shotManager, foulManager).
   *
   * @param matchup  The matchup containing the list of labeled plays.
   */
  private void getPlays(Matchup matchup) {
    for (int index = 0; index < matchup.getAllLabeledPlays().size(); index++) {
      LabeledPlay play = matchup.getAllLabeledPlays().get(index);
      index = handlePlayAction(play, matchup, index);
    }
  }

  /**
   * Handles the processing of a specific play action, delegating to the appropriate manager
   * (e.g., shotManager for shots, foulManager for fouls).
   * The index may be adjusted based on certain actions (e.g., free throws, blocks, shots).
   *
   * @param play     The labeled play to handle.
   * @param matchup  The matchup associated with the play.
   * @param index    The current index of the play in the list.
   * @return The updated index after processing the play.
   */
  private int handlePlayAction(LabeledPlay play, Matchup matchup, int index) {
    switch (play.getAction()) {
      case JUMPBALL -> shotManager.createJumpBallPlay(matchup, play);
      case TIMEOUT -> timeoutManager.createTimeoutPlays(matchup, play);
      case SUBSTITUTION -> substitutionManager.createSubstitutionPlays(matchup, play);
      case FREE_THROW -> index = freeThrowManager.handleFreeThrow(index, matchup, play);
      case VIOLATION -> violationManager.createViolationPlays(matchup, play);
      case TURNOVER -> turnoverManager.createTurnoverPlays(matchup, play);
      case SHOT -> index = shotManager.handleShot(index, matchup, play);
      case ASSIST -> assistManager.createAssistPlays(matchup, play);
      case FOUL, FLAGRANT_FOUL -> foulManager.createFoulPlays(matchup, play);
      case BLOCK -> index = blockManager.handleBlock(index, matchup, play);
      case REBOUND, TEAM -> reboundManager.createReboundPlay(matchup, play);
      case EJECTION -> foulManager.createSingleEjectionPlay(matchup, play);
      default -> createUnidentifiedPlay(matchup, play);
    }
    return index;
  }



  /**
   * Creates an unidentified play.
   * @param matchup The matchup object containing labeled plays.
   * @param lPlayOne The labeled play.
   */
  private void createUnidentifiedPlay(Matchup matchup, LabeledPlay lPlayOne) {
    Play play =
        new Play(
            matchup,
            PlayTypes.UNIDENTIFIEDPLAYTYPE,
            List.of(lPlayOne),
            lPlayOne.getHomeScore(),
            lPlayOne.getAwayScore());
    matchup.getPlayByPlays().add(play);
  }


  private Matchup findCorrelatingMatchupWithID(
      String date, String name, List<Matchup> matchups, int id) {
    for (Matchup matchup : matchups) {
      if (matchup.getGameID() == 0) {
        if (matchup.getDate().equals(date)) {
          for (Player player : matchup.getTotalPlayers()) {
            if (player.getName().equals(name)) {
              matchup.setGameID(id);
              return matchup;
            }
          }
        }
      } else {
        if (matchup.getGameID() == id) {
          return matchup;
        }
      }
    }
    return null;
  }

  public Matchup generateSpecificMatchupPlays(
      String playByPlayCSVPath,
      int gameID,
      boolean accessSpecificFiles,
      String fileName,
      List<Matchup> matchups) {
    List<LabeledPlay> allPlaysLabeled = new ArrayList<>();
    File folder = new File(playByPlayCSVPath);
    File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
    if (files != null) {
      for (File file : files) {
        if (accessSpecificFiles) {
          if (file.getName().equalsIgnoreCase(fileName)) {
            try (Reader reader = new FileReader(file);
                CSVParser csvParser =
                    new CSVParser(
                        reader,
                        CSVFormat.DEFAULT
                            .builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .build())) {
              for (CSVRecord csvRecord : csvParser) {
                if (CSVUtils.getInt(csvRecord, "game_id") != gameID) continue;
                Actions action = playLabellingManager.determineAction(csvRecord);
                LabeledPlay labeledPlay = playLabellingManager.createLabeledPlay(csvRecord, action);
                if (labeledPlay.getAction() != Actions.IGNORE
                    && labeledPlay.getAction() != Actions.UNKNOWN
                    && labeledPlay.getAction() != Actions.END_OF_PERIOD) {
                  allPlaysLabeled.add(labeledPlay);
                }
              }
            } catch (IOException e) {
              System.out.println("Failed to read file.");
              e.printStackTrace();
            }
          }
        }
      }
      allPlaysLabeled.sort(
          Comparator.comparingInt(LabeledPlay::getGameID)
              .thenComparingInt(LabeledPlay::getGamePlayNumber));
      if (allPlaysLabeled.getFirst().getAwayOnCourt().getFirst().equalsIgnoreCase("Reggie Bullock Jr.")){
        allPlaysLabeled.getFirst().getAwayOnCourt().set(allPlaysLabeled.getFirst().getAwayOnCourt().indexOf(allPlaysLabeled.getFirst().getAwayOnCourt().getFirst()), "Reggie Bullock");
      }
      Matchup desiredMatchup =
          findCorrelatingMatchupWithID(
              allPlaysLabeled.getFirst().getDate(),
              allPlaysLabeled.getFirst().getAwayOnCourt().getFirst(),
              matchups,
              allPlaysLabeled.getFirst().getGameID());
      for (LabeledPlay labeledPlay : allPlaysLabeled) {
        labeledPlay.setMatchup(desiredMatchup);
        desiredMatchup.getAllLabeledPlays().add(labeledPlay);
      }
      return desiredMatchup;
    }
    return null;
  }

  public int getCount(){
    return count;
  }

  public List<Matchup> gatherAndOrganizeMatchups(){
      matchups.sort((m1, m2) -> {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      try {
        Date date1 = dateFormat.parse(m1.getDate());
        Date date2 = dateFormat.parse(m2.getDate());
        return date1.compareTo(date2);
      } catch (ParseException e) {
        throw new IllegalArgumentException(e);
      }
    });
      return matchups;
  }


    }
