package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.TestPlayerStats;
import me.antcode.TypesOfAction.Actions;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.datacollection.CSVUtils;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVRecord;
import java.util.*;

public class PlayLabellingManager extends Manager {

    /**
     * Determines the shooting action type based on the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @return The determined shooting action type.
     */
    private Actions determineShootingAction(CSVRecord csvRecord) {
        if (csvRecord.get("assists").equals("true")) {
            return Actions.ASSIST;
        }
        return Actions.SHOT;
    }

    /**
     * Creates a labeled play object from the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @param action The action type of the play.
     * @return A LabeledPlay object.
     */
    public LabeledPlay createLabeledPlay(CSVRecord csvRecord, Actions action) {
        LabeledPlay labeledPlay = new LabeledPlay(
                action,
                CSVUtils.getInt(csvRecord, "game_id"),
                CSVUtils.getInt(csvRecord, "period"),
                CSVUtils.convertToSeconds(csvRecord.get("remaining_time")),
                CSVUtils.getInt(csvRecord, "away_score"),
                CSVUtils.getInt(csvRecord, "home_score"),
                csvRecord.get("event_type"),
                csvRecord.get("type"),
                csvRecord.get("description"),
                csvRecord.get("result").equals("made"),
                csvRecord.get("steal"),
                csvRecord.get("assist"),
                csvRecord.get("block"),
                csvRecord.get("player"),
                csvRecord.get("opponent"),
                CSVUtils.getInt(csvRecord, "shot_distance"),
                csvRecord.get("offensive").equals("true"),
                csvRecord.get("defensive").equals("true"),
                CSVUtils.getInt(csvRecord, "play_id"),
                csvRecord.get("flagrant").equals("true"),
                csvRecord.get("3pt").equals("true"),
                csvRecord.get("date"),
                CSVUtils.convertToSeconds(csvRecord.get("play_length")),
                CSVUtils.getInt(csvRecord, "num"),
                CSVUtils.getInt(csvRecord, "outof"),
                csvRecord.get("away"),
                csvRecord.get("home"),
                csvRecord.get("possession")
        );
        labeledPlay.setCourtPlayers(csvRecord);
        return labeledPlay;
    }

    /**
     * Determines the action type based on the given CSV record.
     * @param csvRecord The CSV record containing play-by-play data.
     * @return The determined action type.
     */
    public Actions determineAction(CSVRecord csvRecord) {
        String eventType = csvRecord.get("event_type").toLowerCase();
        if (eventType.equals("jump ball")){return Actions.JUMPBALL;
        }else if (eventType.equals("substitution")){return Actions.SUBSTITUTION;
        }else  if (isTrue(csvRecord, "timeout")){return Actions.TIMEOUT;
        }else if (isTrue(csvRecord, "ignore") && !csvRecord.get("type").equalsIgnoreCase("double technical")){
            return Actions.IGNORE;
        }
        else if (isTrue(csvRecord, "rebound") && csvRecord.get("wasTeam").equals("false")){return Actions.REBOUND;
        } else if (isTrue(csvRecord, "wasTeam")){return Actions.TEAM;
        }else if (isTrue(csvRecord, "violation")){return Actions.VIOLATION;
        }else if (eventType.equals("ejection")){return Actions.EJECTION;
        }else if (csvRecord.get("free_throw").equals("true") || eventType.equals("free_throw")){return Actions.FREE_THROW;
        }else if (isTrue(csvRecord, "flagrant") && isTrue(csvRecord, "foul")){return Actions.FLAGRANT_FOUL;
        }else if (isTrue(csvRecord, "foul")){return Actions.FOUL;
        }else if (!csvRecord.get("block").isEmpty()){return Actions.BLOCK;
        }else if (isTrue(csvRecord, "turnover")){return Actions.TURNOVER;
        }else if (isTrue(csvRecord, "assists")){return Actions.ASSIST;
        } else if (eventType.equals("shot")){return determineShootingAction(csvRecord);
        }else if (isTrue(csvRecord, "end_period")){return Actions.END_OF_PERIOD;}
        else {return Actions.UNKNOWN;}
    }

    private boolean isTrue(CSVRecord csvRecord, String field) {
        return "true".equals(csvRecord.get(field));
    }


    public void printPlaysForMatchup(List<Matchup> matchups, int gameID){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (Play play : matchup.getPlayByPlays()){
                System.out.println(play.getPlayType());
                System.out.println("------------------------");
                for (LabeledPlay labeledPlay1 : play.getMakeUpOfPlay()){
                    System.out.println(labeledPlay1);
                }
                System.out.println("------------------------");
            }
        }
    }

    private void printAllLabeledPlaysForMatchup(List<Matchup> matchups, int gameID){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() != gameID) continue;
            for (LabeledPlay labeledPlay : matchup.getAllLabeledPlays()){
        System.out.println("------------------");
        System.out.println(labeledPlay);
                System.out.println("------------------");
            }
        }
    }


    public void printSpecificMatchup(String folderPath, TestPlayerStats testPlayerStats, CSVDataGather csvDataGather, int gameID, String fileName,  List<Matchup> allMatchups){
        testPlayerStats.checkPlayerStatsForMatchup(csvDataGather.curateSpecificPlay(folderPath, gameID, fileName));
        printPlaysForMatchup(allMatchups, gameID);
        printAllLabeledPlaysForMatchup(allMatchups, gameID);
    }



}
