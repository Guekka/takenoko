package takenoko.action;

import java.util.LinkedHashMap;
import java.util.List;
import takenoko.game.Deck;
import takenoko.game.board.MovablePiece;
import takenoko.game.objective.Objective;
import takenoko.game.tile.PowerUp;
import takenoko.game.tile.Tile;
import takenoko.game.tile.TileSide;
import takenoko.utils.Coord;

/**
 *
 * This interface define the concept of action in Takenoko. Some have a cost, a player can perform 2 to 3 actions with a cost per turn.
 * Actions that do not have a cost can be game events such as MovePiece, UnveilObjective or EndTurn, but also actions that a player
 * can do whenever he wants, like PlaceIrrigationStick. The interface is sealed to make sure that we can't use an unexisting action.
 *
 */
public sealed interface Action
        permits Action.BeginSimulation,
                Action.EndSimulation,
                Action.EndTurn,
                Action.GrowOneTile,
                Action.MovePandaAnywhere,
                Action.MovePiece,
                Action.None,
                Action.PickPowerUp,
                Action.PlaceIrrigationStick,
                Action.PlacePowerUp,
                Action.PlaceTile,
                Action.SimulateActions,
                Action.TakeIrrigationStick,
                Action.TakeObjective,
                Action.UnveilObjective {
    Action NONE = new Action.None();
    Action END_TURN = new Action.EndTurn();
    Action.BeginSimulation BEGIN_SIMULATION = new Action.BeginSimulation();
    Action.EndSimulation END_SIMULATION = new Action.EndSimulation();

    default boolean hasCost() {
        return true;
    }

    default boolean isSameTypeAs(Action other) {
        return this.getClass() == other.getClass();
    }

    /**
     * We have created record here to override some methodes
     * whose "basic" result did not suit us
     */
    record None() implements Action {
        @Override
        public String toString() {
            return "Nothing happened.";
        }
    }

    record EndTurn() implements Action {
        @Override
        public String toString() {
            return "End of the current turn.";
        }

        @Override
        public boolean hasCost() {
            return false;
        }
    }

    record PlaceTile(Coord coord, Deck.DrawPredicate<Tile> drawPredicate) implements Action {}

    record TakeObjective(Objective.Type type) implements Action {}

    record UnveilObjective(Objective objective) implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }
    }

    record TakeIrrigationStick() implements Action {}

    record PlaceIrrigationStick(Coord coord, TileSide side) implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }
    }

    record MovePiece(MovablePiece piece, Coord to) implements Action {
        @Override
        public boolean isSameTypeAs(Action other) {
            if (other instanceof MovePiece otherMovePiece) {
                // moving different pieces is not the same action
                return piece.equals(otherMovePiece.piece);
            }
            return false;
        }
    }

    record BeginSimulation() implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }
    }

    record EndSimulation() implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }
    }

    /// Simulates a list of actions. They will be tried alternatively, not sequentially
    record SimulateActions(
            List<Action> alternativeActions,
            LinkedHashMap<Action, LinkedHashMap<Objective, Objective.Status>> outObjectiveStatus)
            implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }
    }

    record PickPowerUp(PowerUp powerUp) implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }
    }

    record PlacePowerUp(Coord coord, PowerUp powerUp) implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }
    }

    record GrowOneTile(Coord at) implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof GrowOneTile other) {
                return at.equals(other.at);
            }
            return false;
        }
    }

    record MovePandaAnywhere(Coord to) implements Action {
        @Override
        public boolean hasCost() {
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MovePandaAnywhere other) {
                return to.equals(other.to);
            }
            return false;
        }
    }
}
