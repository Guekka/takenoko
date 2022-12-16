package takenoko;

import java.util.HashMap;
import java.util.Map;

public class BambooTile implements Tile {

    private Map<TileSide, Boolean> irrigatedSides;

    public BambooTile() {
        irrigatedSides = new HashMap<>();
        for (TileSide side : TileSide.values()) {
            irrigatedSides.put(side, false);
        }
    }

    @Override
    public boolean isCultivable() {
        return true;
    }

    public void irrigateSide(TileSide side) {
        irrigatedSides.put(side, true);
    }

    public boolean isIrrigated() {
        return irrigatedSides.values().stream().allMatch(Boolean::booleanValue);
    }

    public boolean isSideIrrigable(TileSide side) throws Exception {
        if (isSideIrrigated(side)) {
            throw new Exception("Error: this side is already irrigated.");
        }
        return irrigatedSides.get(side.leftSide()) || irrigatedSides.get(side.rightSide());
    }

    @Override
    public boolean isSideIrrigated(TileSide side) {
        return irrigatedSides.get(side);
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
