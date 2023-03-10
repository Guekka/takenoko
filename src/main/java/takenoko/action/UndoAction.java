package takenoko.action;

import takenoko.game.board.Board;
import takenoko.game.board.VisibleInventory;
import takenoko.game.objective.Objective;
import takenoko.game.tile.PowerUp;
import takenoko.utils.Coord;

/**
 * This interface is used to simulate actions. It allows to rollback one or several previous
 * actions. It is not meant to be used explicitly by the player.
 */
public sealed interface UndoAction
        permits UndoAction.BeginSimulation,
                UndoAction.EndTurn,
                UndoAction.GrowOneTile,
                UndoAction.MovePiece,
                UndoAction.None,
                UndoAction.PickPowerUp,
                UndoAction.PlaceIrrigationStick,
                UndoAction.PlacePowerUp,
                UndoAction.PlaceTile,
                UndoAction.TakeIrrigationStick,
                UndoAction.TakeObjective,
                UndoAction.UnveilObjective {
    None NONE = new None();
    EndTurn END_TURN = new EndTurn();
    BeginSimulation BEGIN_SIMULATION = new BeginSimulation();

    record None() implements UndoAction {}

    record EndTurn() implements UndoAction {}

    record PlaceTile(Coord coord) implements UndoAction {}

    record UnveilObjective(Objective objective) implements UndoAction {}

    record TakeIrrigationStick() implements UndoAction {}

    record PlaceIrrigationStick(Board previousBoard) implements UndoAction {}

    record MovePiece(Board previousBoard, VisibleInventory previousInventory)
            implements UndoAction {}

    record BeginSimulation() implements UndoAction {}

    record TakeObjective(Objective.Type type, Objective objective) implements UndoAction {}

    record PlacePowerUp(Coord coord, PowerUp powerUp) implements UndoAction {}

    record PickPowerUp(PowerUp powerUp) implements UndoAction {}

    record GrowOneTile(Coord at) implements UndoAction {}
}
