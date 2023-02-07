package takenoko.player.bot;

import java.util.*;
import takenoko.action.Action;
import takenoko.action.PossibleActionLister;
import takenoko.game.board.Board;
import takenoko.game.objective.Objective;
import takenoko.game.objective.TilePatternObjective;
import takenoko.game.tile.TileDeck;
import takenoko.player.PlayerBase;
import takenoko.utils.Utils;

public class PlotRushBot extends PlayerBase<PlotRushBot> implements PlayerBase.PlayerBaseInterface {
    private final Random randomSource;
    HashMap<Action, Map<Objective, Objective.Status>> outStatuses = new HashMap<>();

    public PlotRushBot(Random randomSource) {
        this.randomSource = randomSource;
    }

    public Action chooseActionImpl(Board board, PossibleActionLister actionLister) {
        var possibleActions = actionLister.getPossibleActions(TileDeck.DEFAULT_DRAW_PREDICATE);
        // If an objective is achieved, unveil it
        for (var action : possibleActions) {
            if (action instanceof Action.UnveilObjective) {
                return action;
            }
        }

        // if we do not have enough action credits, end the turn
        if (availableActionCredits() == 0) return Action.END_TURN;

        // Starting by drawing a lot of tile pattern objectives
        for (var action : possibleActions) {
            if (action instanceof Action.TakeTilePatternObjective) {
                return action;
            }
        }

        // And get the first coordinate where placing a tile would be beneficial
        List<Action> possiblePlaceTiles = new ArrayList<>();
        for (var action : possibleActions) {
            if (action instanceof Action.PlaceTile placeTile) {
                possiblePlaceTiles.add(placeTile);
            }
        }

        if (outStatuses.isEmpty() && !possiblePlaceTiles.isEmpty())
            return new Action.SimulateActions(possiblePlaceTiles, outStatuses);

        if (!outStatuses.isEmpty()) {
            Action.PlaceTile ptaction = null;

            for (var entry : outStatuses.entrySet()) {
                var action = entry.getKey();
                if (!(action instanceof Action.PlaceTile pt)) continue;
                for (var mapObjectiveStatus : entry.getValue().entrySet()) {
                    var newStatus = mapObjectiveStatus.getValue();
                    var newObjective = mapObjectiveStatus.getKey();

                    for (TilePatternObjective tpo :
                            this.getPrivateInventory().getObjectives().stream()
                                    .filter(TilePatternObjective.class::isInstance)
                                    .map(o -> (TilePatternObjective) o)
                                    .toList()) {
                        if (tpo == newObjective
                                && newStatus.progressFraction() > tpo.status().progressFraction()) {
                            ptaction = pt;
                            break;
                        }
                    }
                }
            }

            outStatuses.clear();
            if (ptaction != null) {
                return ptaction;
            }
        }

        // else, just place a tile
        for (var action : possibleActions) {
            if (action instanceof Action.PlaceTile) {
                return action;
            }
        }

        // else, play a random action
        return Utils.randomPick(possibleActions, randomSource).orElse(Action.END_TURN);
    }
}
