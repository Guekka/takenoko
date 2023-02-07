package takenoko.player.bot;

import java.util.*;
import takenoko.action.Action;
import takenoko.action.PossibleActionLister;
import takenoko.game.board.Board;
import takenoko.game.objective.Objective;
import takenoko.game.objective.TilePatternObjective;
import takenoko.game.tile.TileDeck;
import takenoko.player.PlayerBase;
import takenoko.player.bot.strategies.Strategies;
import takenoko.utils.Utils;

public class RuleBasedBot extends PlayerBase<RuleBasedBot>
        implements PlayerBase.PlayerBaseInterface {
    private final Random randomSource;
    private final Strategies strategy;

    public RuleBasedBot(Random randomSource) {
        this.randomSource = randomSource;
        Optional<Strategies> s = Utils.randomPick(List.of(Strategies.values()), randomSource);
        this.strategy = Strategies.GENERAL_TACTICS;
    }

    public RuleBasedBot(Random randomSource, Strategies strategy) {
        this.randomSource = randomSource;
        this.strategy = strategy;
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

        return switch (strategy) {
            case PLOT_RUSH -> plotRushStrategy(board, actionLister);
            case BAMBOO_PRUNING -> bambooPruningStrategy(board, actionLister);
            case GENERAL_TACTICS -> generalTacticsStrategy(board, actionLister);
            default -> throw new IllegalStateException("Unexpected value: " + strategy);
        };
    }

    private Action generalTacticsStrategy(Board board, PossibleActionLister actionLister) {
        List<Action> possibleActions =
                actionLister.getPossibleActions(TileDeck.DEFAULT_DRAW_PREDICATE);

        // TODO: implement the general tactics strategy

        // else, play a random action
        return Utils.randomPick(possibleActions, randomSource).orElse(Action.END_TURN);
    }

    private Action bambooPruningStrategy(Board board, PossibleActionLister actionLister) {
        List<Action> possibleActions =
                actionLister.getPossibleActions(TileDeck.DEFAULT_DRAW_PREDICATE);

        // TODO: implement the bamboo pruning strategy

        // else, play a random action
        return Utils.randomPick(possibleActions, randomSource).orElse(Action.END_TURN);
    }

    private Action plotRushStrategy(Board board, PossibleActionLister actionLister) {
        List<Action> possibleActions =
                actionLister.getPossibleActions(TileDeck.DEFAULT_DRAW_PREDICATE);

        // Starting by drawing a lot of tile pattern objectives
        for (var action : possibleActions) {
            if (action instanceof Action.TakeTilePatternObjective) {
                return action;
            }
        }

        // If we have enough pattern objectives, retrieve all objectives
        List<TilePatternObjective> unfinishedObjectives = new ArrayList<>();
        for (Objective objective : getPrivateInventory().getObjectives()) {
            if (objective instanceof TilePatternObjective tilePatternObjective) {
                unfinishedObjectives.add(tilePatternObjective);
            }
        }

        // And get the first coordinate where placing a tile would be beneficial

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
