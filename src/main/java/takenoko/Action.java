package takenoko;

import java.util.Objects;

public sealed interface Action permits Action.None, Action.PlaceTile, Action.TakeIrrigationStick {
    Action NONE = new Action.None();

    int cost();

    final class None implements Action {
        @Override
        public int cost() {
            return 1;
        }
    }

    record PlaceTile(Coord coord, Tile tile) implements Action {
        @Override
        public int cost() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof PlaceTile other) {
                return coord.equals(other.coord) && tile.equals(other.tile);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(coord, tile);
        }
    }

    // action "take irrigation stick" (cost 1) : take an irrigation stick from the stack and put it
    // in the player's inventory
    record TakeIrrigationStick() implements Action {
        @Override
        public int cost() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof TakeIrrigationStick;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}
