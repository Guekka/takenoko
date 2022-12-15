package takenoko;

import java.util.Arrays;

public class BambooTile implements Tile {

    private final Boolean[] irrigatedSides;

    public BambooTile() {
        irrigatedSides = new Boolean[6];
    }

    @Override
    public boolean isCultivable() {
        return true;
    }

    public void irrigateSide(int side) {
        irrigatedSides[side] = true;
    }

    public boolean isIrrigated() {
        return Arrays.stream(irrigatedSides).anyMatch(b -> b);
    }

    public boolean isSideIrrigated(int side) {
        return irrigatedSides[side];
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BambooTile;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
