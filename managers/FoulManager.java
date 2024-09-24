package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class FoulManager extends Manager {



    /**
     * Creates a play for a single ejection event and updates the matchup.
     * @param matchup The current matchup containing all labeled plays.
     * @param lPlayOne The labeled play that contains the ejection information.
     */
    public void createSingleEjectionPlay(Matchup matchup, LabeledPlay lPlayOne) {
        // Create a new play with the ejection event
        Play play = new Play(matchup, PlayTypes.EJECTION, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        // Find the ejected player
        Player ejectedPlayer = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());

        // If valid ejected player is found, add them to the ejection list
        if (ejectedPlayer != null && ejectedPlayer.getId() != 0) {
            play.getPlayersEjected().add(ejectedPlayer);
        }

        // Handle the case where the previous play was a first technical foul
        Play lastPlay = matchup.getPlayByPlays().getLast();
        Play oneBeforeLastPlay = matchup.getPlayByPlays().get(matchup.getPlayByPlays().indexOf(lastPlay) - 1);
        if (lastPlay.getPlayType().toString().contains("FIRST_TECHNICAL")) {
            lastPlay.setPlayType(PlayTypes.SECOND_TECHNICAL_FOUL);
            if (lastPlay.getFoulCommitter() != null) {
                lastPlay.getFoulCommitter().addFouls(1);
            }
        }

        // Add the ejection play to the play-by-play list
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Creates a foul play based on the labeled play and updates the matchup.
     * @param matchup The current matchup containing all labeled plays.
     * @param lPlayOne The labeled play that contains the foul information.
     */
    public void createFoulPlays(Matchup matchup, LabeledPlay lPlayOne) {
        // Create a new play with the foul event
        Play play = new Play(matchup, PlayTypes.FOUL, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player playerOne = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        String typeText = lPlayOne.getTypeText().toLowerCase();
        String description = lPlayOne.getText().toLowerCase();

        // If a valid player is found, set them as the foul committer
        if (playerOne != null && playerOne.getId() != 0) {
            play.setFoulCommitter(playerOne);
            // Add a foul to the player unless it's a technical or special case
            if (!typeText.contains("3 seconds") && !typeText.contains("technical") && !description.contains("taunting")) {
                playerOne.addFouls(1);
            } else if (typeText.contains("double technical")) {
                handleDoubleTechnicalFoul(matchup, lPlayOne, play);
            }
        }

        // Handle flagrant foul types
        if (lPlayOne.getAction() == Actions.FLAGRANT_FOUL) {
            play.setPlayType(typeText.contains("flagrant-1") ? PlayTypes.FLAGRANT_FOUL_TYPE_ONE : typeText.contains("flagrant-2") ? PlayTypes.FLAGRANT_FOUL_TYPE_TWO : play.getPlayType());
        } else {
            // Handle specific foul types like delay or taunting
            if (description.contains("delay technical")) {
                play.setPlayType(PlayTypes.DELAY_TECHNICAL);
            } else {
                play.setPlayType(determineFoulPlayType(typeText));
                // Check for duplicate or upgraded fouls
                handleDuplicateOrUpgradedFoul(matchup, play);
            }

            // Set the fouled player if it's a personal foul
            if (play.getPlayType() == PlayTypes.PERSONAL_FOUL) {
                Player playerFouled = matchup.findPlayerObject(lPlayOne.getOpponentColumn());
                if (playerFouled != null) {
                    play.setPlayerFouled(playerFouled);
                }
            }
        }

        if (description.contains("taunting")) {
            play.setPlayType(PlayTypes.TAUNTING_FOUL);
        }

        // Add the play to the play-by-play list
        matchup.getPlayByPlays().add(play);
    }

    /**
     * Handles a double technical foul by setting the technical players in the play.
     * @param matchup The current matchup.
     * @param lPlayOne The labeled play containing the double technical foul.
     * @param play The play being processed.
     */
    private void handleDoubleTechnicalFoul(Matchup matchup, LabeledPlay lPlayOne, Play play) {
        Player playerTwo = matchup.findPlayerObject(lPlayOne.getOpponentColumn());
        play.setTechOnePlayer(matchup.findPlayerObject(lPlayOne.getMultUsePlayer()));
        play.setTechTwoPlayer(playerTwo);
    }

    /**
     * Handles the logic for checking and updating plays if they are duplicated or upgraded.
     * @param matchup The current matchup.
     * @param play The current play being processed.
     */
    private void handleDuplicateOrUpgradedFoul(Matchup matchup, Play play) {
        if (!matchup.getPlayByPlays().isEmpty()) {
            Play lastPlay = matchup.getPlayByPlays().getLast();
            if (isDuplicated(play, lastPlay) || isUpgraded(play, lastPlay)) {
                if (lastPlay.getFoulCommitter() != null) {
                    if (isDuplicated(play, lastPlay) && lastPlay.getPlayType() == PlayTypes.FIRST_TECHNICAL_FOUL) {
                        matchup.getPlayByPlays().remove(lastPlay);
                        matchup.getPlayByPlays().add(play);
                        return;
                    }
                    lastPlay.getFoulCommitter().setFouls(lastPlay.getFoulCommitter().getFouls() - 1);
                    matchup.getPlayByPlays().remove(lastPlay);
                }
                matchup.getPlayByPlays().add(play);
            }
        }
    }

    /**
     * Checks if two plays are duplicates based on their content.
     * @param play The current play being processed.
     * @param previousPlay The previous play to compare against.
     * @return True if the two plays are duplicates, false otherwise.
     */
    private boolean isDuplicated(Play play, Play previousPlay) {
        if (!previousPlay.getPlayType().equals(play.getPlayType())) return false;
        if (!previousPlay.getMakeUpOfPlay().getLast().getText().equalsIgnoreCase(play.getMakeUpOfPlay().getLast().getText())) return false;
        if (previousPlay.getTimeLeftInQuarter() != play.getTimeLeftInQuarter()) return false;
        return play.getFoulCommitter() == previousPlay.getFoulCommitter() && previousPlay.getQuarter() == play.getQuarter();
    }

    /**
     * Checks if a foul play has been upgraded (e.g., from personal to technical).
     * @param play The current play being processed.
     * @param previousPlay The previous play to compare against.
     * @return True if the foul has been upgraded, false otherwise.
     */
    private boolean isUpgraded(Play play, Play previousPlay) {
        if (previousPlay.getPlayType() == PlayTypes.PERSONAL_FOUL && play.getPlayType() == PlayTypes.SECOND_TECHNICAL_FOUL) {
            return previousPlay.getTimeLeftInQuarter() == play.getTimeLeftInQuarter() &&
                    previousPlay.getFoulCommitter() == play.getFoulCommitter() &&
                    previousPlay.getQuarter() == play.getQuarter();
        }
        return false;
    }

    /**
     * Determines the foul play type based on the text description.
     * @param text The text description of the play.
     * @return The determined PlayType.
     */
    private PlayTypes determineFoulPlayType(String text) {
        return switch (text) {
            case String t when t.contains("personal") -> PlayTypes.PERSONAL_FOUL;
            case String t when t.contains("shooting") -> PlayTypes.SHOOTING_FOUL;
            case String t when t.contains("loose ball") -> PlayTypes.LOOSE_BALL_FOUL;
            case String t when t.contains("3-seconds") -> PlayTypes.DEFENSIVE_THREE_SECONDS_FOUL;
            case String t when t.contains("transition take") -> PlayTypes.TRANSITION_TAKE_FOUL;
            case String t when t.contains("personal take") -> PlayTypes.PERSONAL_TAKE_FOUL;
            case String t when t.contains("double technical") -> PlayTypes.DOUBLE_TECHNICAL_FOUL;
            case String t when t.contains("technical") -> PlayTypes.FIRST_TECHNICAL_FOUL;
            case String t when t.contains("delay technical") -> PlayTypes.DELAY_TECHNICAL_FOUL;
            case String t when t.contains("too many players") -> PlayTypes.TOO_MANY_PLAYERS_TECHNICAL;
            case String t when t.contains("away from play") -> PlayTypes.AWAY_FROM_PLAY_FOUL;
            case String t when t.contains("clear path") -> PlayTypes.CLEAR_PATH_FOUL;
            default -> PlayTypes.FOUL;
        };
    }



}
