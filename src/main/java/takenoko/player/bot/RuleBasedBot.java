package takenoko.player.bot;

import java.util.*;
import takenoko.action.Action;
import takenoko.action.PossibleActionLister;
import takenoko.game.board.Board;
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
        // If an objective is achieved, unveil it
        for (var obj : getInventory().getObjectives())
            if (obj.wasAchievedAfterLastCheck()) return new Action.UnveilObjective(obj);

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
        return null;
    }

    private Action bambooPruningStrategy(Board board, PossibleActionLister actionLister) {
        return null;
    }

    private Action plotRushStrategy(Board board, PossibleActionLister actionLister) {
        return null;
    }
}
