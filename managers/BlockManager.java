package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import me.antcode.TypesOfAction.Actions;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;

import java.util.List;

public class BlockManager extends Manager {

    /**
     * Handles the processing of a block play.
     * @param index The current index in the labeled plays list.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The first labeled play.
     * @return The updated index after processing the block play.
     */
    public int handleBlock(int index, Matchup matchup, LabeledPlay lPlayOne) {
        LabeledPlay lPlayTwo;
        try {
            lPlayTwo = matchup.getAllLabeledPlays().get(index + 1);
            Actions action = lPlayTwo.getAction();
            if (action != Actions.TEAM && action != Actions.REBOUND) {
                createSingleBlockPlay(matchup, lPlayOne);
            } else {
                createBlockPlays(matchup, lPlayOne, lPlayTwo);
                index++;
            }
        } catch (IndexOutOfBoundsException e) {
            createSingleBlockPlay(matchup, lPlayOne);
        }
        return index;
    }

    /**
     * Creates a block play with two labeled plays.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The first labeled play.
     * @param lPlayTwo The second labeled play.
     */
    private void createBlockPlays(Matchup matchup,  LabeledPlay lPlayOne, LabeledPlay lPlayTwo){
        Play play = new Play(matchup, PlayTypes.BLOCK_AND_NO_POSSESSION_CHANGE, List.of(lPlayOne, lPlayTwo), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player blockedPlayer = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        Player playerBlocking = matchup.findPlayerObject(lPlayOne.getBlockPlayer());

        if (playerBlocking != null && playerBlocking.getId() != 0){
            playerBlocking.addBlocks(1);
            play.setPlayerBlocking(playerBlocking);
        }
        if (blockedPlayer != null && blockedPlayer.getId() != 0){
            blockedPlayer.addFieldGoalsAttempted(1);
            if (lPlayOne.isThreePointer()){
                blockedPlayer.addThreePointFieldGoalsAttempted(1);
            }
            play.setPlayerShooting(blockedPlayer);
            play.setBlockedPlayer(blockedPlayer);
        }
        Actions action = lPlayTwo.getAction();
        if (action == Actions.TEAM || action== Actions.REBOUND){
            if (action == Actions.REBOUND) {
                Player rebounder = matchup.findPlayerObject(lPlayTwo.getMultUsePlayer());
                if (rebounder != null && rebounder.getId() != 0){
                    rebounder.addRebounds(1);
                    play.setRebounder(rebounder);
                    play.setWasTeam(false);
                }
            }
            if (action == Actions.TEAM){
                play.setWasTeam(true);
            }
            if (lPlayTwo.isDefensive()) {
                play.setPlayType(PlayTypes.BLOCK_AND_POSSESSION_CHANGE);
            }

        }
        matchup.getPlayByPlays().add(play);
    }


    /**
     * Creates a single block play.
     * @param matchup The matchup object containing labeled plays.
     * @param lPlayOne The labeled play.
     */
    private void createSingleBlockPlay(Matchup matchup,LabeledPlay lPlayOne){
        Play play = new Play(matchup, PlayTypes.BLOCK, List.of(lPlayOne), lPlayOne.getHomeScore(), lPlayOne.getAwayScore());
        Player blockedPlayer = matchup.findPlayerObject(lPlayOne.getMultUsePlayer());
        Player playerBlocking = matchup.findPlayerObject(lPlayOne.getBlockPlayer());
        if (blockedPlayer != null && blockedPlayer.getId() != 0){
            blockedPlayer.addFieldGoalsAttempted(1);
            if (lPlayOne.isThreePointer()){
                blockedPlayer.addThreePointFieldGoalsAttempted(1);
            }
            play.setPlayerShooting(blockedPlayer);
            play.setBlockedPlayer(blockedPlayer);
        }
        if (playerBlocking != null && playerBlocking.getId() != 0){
            play.setPlayerBlocking(playerBlocking);
            playerBlocking.addBlocks(1);
        }
        matchup.getPlayByPlays().add(play);
    }


}
