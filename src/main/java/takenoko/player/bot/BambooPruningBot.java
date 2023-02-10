package takenoko.player.bot;

import java.util.*;
import takenoko.action.Action;
import takenoko.action.PossibleActionLister;
import takenoko.game.WeatherDice;
import takenoko.game.board.Board;
import takenoko.game.objective.Objective;
import takenoko.player.PlayerBase;
import takenoko.utils.Utils;

public class BambooPruningBot extends PlayerBase<BambooPruningBot>
        implements PlayerBase.PlayerBaseInterface {
    private final Random randomSource;
    LinkedHashMap<Action, LinkedHashMap<Objective, Objective.Status>> outStatuses =
            new LinkedHashMap<>();

    public BambooPruningBot(Random randomSource, String name) {
        super(name);
        this.randomSource = randomSource;
    }

    public Action chooseActionImpl(Board board, PossibleActionLister actionLister) {
        var possibleActions = actionLister.getPossibleActions();
        // If an objective is achieved, unveil it
        for (var action : possibleActions) {
            if (action instanceof Action.UnveilObjective) {
                return action;
            }
        }

        // if we do not have enough action credits, end the turn
        if (availableActionCredits() == 0) return Action.END_TURN;

        // Starting by drawing a lot of panda and gardener objectives
        for (var action : possibleActions) {
            if (action instanceof Action.TakeObjective objective
                            && objective.type() == Objective.Type.HARVESTING
                    || action instanceof Action.TakeObjective objective2
                            && objective2.type() == Objective.Type.BAMBOO_SIZE) {
                return action;
            }
        }

        // And get all the possible panda and gardener actions
        List<Action> possiblePandaAndGardenerActions = new ArrayList<>();
        for (var action : possibleActions) {
            if (action instanceof Action.MovePiece) {
                possiblePandaAndGardenerActions.add(action);
            }
        }

        // Checking best action to take
        Optional<Action> simulation =
                this.simulateBestBambooPruningMove(possiblePandaAndGardenerActions);
        if (simulation.isPresent()) return simulation.get();

        // if we have no advantageous move piece action, take an irrigation stick (or place one)
        List<Action> irrigationAction = new ArrayList<>();
        for (var action : possibleActions) {
            if (action instanceof Action.PlaceIrrigationStick) {
                irrigationAction.add(action);
            }
        }

        // Checking best action to take
        Optional<Action> simulation2 = this.simulateBestBambooPruningMove(irrigationAction);
        if (simulation2.isPresent()) return simulation2.get();

        // if we can't place irrigation, then we take one
        for (var action : possibleActions) {
            if (action instanceof Action.TakeIrrigationStick) {
                return action;
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

    public Optional<Action> simulateBestBambooPruningMove(
            List<Action> possiblePandaAndGardenerActions) {
        if (outStatuses.isEmpty() && !possiblePandaAndGardenerActions.isEmpty())
            return Optional.of(
                    new Action.SimulateActions(possiblePandaAndGardenerActions, outStatuses));

        if (!outStatuses.isEmpty()) {
            var actionToObjectiveProgress = getObjectiveProgressFromSimulation(outStatuses);

            Action bestAction = null;
            double bestProgress = 0;
            for (var entry : actionToObjectiveProgress.entrySet()) {
                var action = entry.getKey();
                for (var mapObjectiveStatus : entry.getValue().entrySet()) {
                    var progress = mapObjectiveStatus.getValue();
                    if (progress > bestProgress) {
                        bestProgress = progress;
                        bestAction = action;
                    }
                }
            }

            outStatuses.clear();
            return Optional.ofNullable(bestAction);
        }
        return Optional.empty();
    }

    @Override
    public WeatherDice.Face chooseWeatherImpl(List<WeatherDice.Face> allowedWeathers) {
        // Tries to take SUN. If he can't, take CLOUD else WIND and finally RANDOM
        if (allowedWeathers.contains(WeatherDice.Face.SUN)) return WeatherDice.Face.SUN;
        else if (allowedWeathers.contains(WeatherDice.Face.RAIN)) return WeatherDice.Face.RAIN;
        else if (allowedWeathers.contains(WeatherDice.Face.WIND)) return WeatherDice.Face.WIND;
        else if (allowedWeathers.contains(WeatherDice.Face.CLOUDY)) return WeatherDice.Face.CLOUDY;
        return Utils.randomPick(allowedWeathers, randomSource).orElse(null);
    }
}
